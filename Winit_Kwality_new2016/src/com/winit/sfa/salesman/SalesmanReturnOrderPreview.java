package com.winit.sfa.salesman;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.OrderPreviewAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomListView;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.kwalitysfa.salesman.R;


public class SalesmanReturnOrderPreview extends BaseActivity implements ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llOrderPreview , llMiddleLayout, llEsignature, llCustomerSign, llPresellerSign, llHeaderLayout
						,llPresellerSignHeader,llCustomerSignHeader, llOrderPreviewBottom;
	private TextView tvHeaderPreview, tvItemName, tvOrderPreviewunits , tvTotalAmount, tvTotalAmountValue, tvOrderPreviewCases,
					 tvCustomerPasscode, tvPresellerSign, tvCustomerSign, tvOrderPreviewPriceValue , tvOrderPreviewFooterCases, tvOrderPreviewFooterunits
					 ,tvDeliveryDate, tvDeliveryDateValue, tvDeliveryToTimeValue,tvDeliveryFromTimeValue, tvDeliveryToTime, tvDeliveryFromTime
					 ,tvOrderActualAmount/*,tvlanguage*/;
	private Button btnFinalize, btnPrintSalesOrder , btnPrintMerchant , btnCustomerSignClear , btnPresellerSignClear,btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
	private EditText etCustomerPasscode;
	private CustomListView lvPreviewOrder;
	private OrderPreviewAdapter orderPreviewAdapter;
	public static Vector<ProductDO> vecMainProducts;
	private float totalPrice = 0, totalAmount = 0, totalCases = 0, totalUnits = 0, totalPerItemAmount = 0, discount = 0.0f,
				  totalSalesPrice = 0, totalInvoicedPrice =0.0f, orderInvoice = 0;;
	private OrderDO orderDO;
	private JourneyPlanDO mallsDetails;
	private boolean isSalesOrderGerenaterd = false, isPosted = false;
	private String  orderId;
	private MyView customerSignature, presellerSignature;
	private static Paint mPaint;
	private boolean isCustomerSigned = false, isPresellerSigned = false;
	private String from; 
	private boolean isMenu = false, isTask;
	
	@Override
	public void initialize()
	{
		//inflate the preview_order_list layout 
		llOrderPreview 	    = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_order_list, null);
		llBody.addView(llOrderPreview,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		if(getIntent().getExtras() != null)
		{
			totalPrice 		= 	StringUtils.getFloat(getIntent().getExtras().getString("TotalPrice"));
			discount   		= 	StringUtils.getFloat(getIntent().getExtras().getString("Discount"));
			mallsDetails 	= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			orderId			= getIntent().getExtras().getString("invoicenum");
			orderInvoice	= getIntent().getExtras().getFloat("invoiceamt");
			from			= getIntent().getExtras().getString("from");
			isMenu			= getIntent().getExtras().getBoolean("isMenu");
			isTask			= getIntent().getExtras().getBoolean("isTask");
		}
		
		InitializeControls();
		setTypeFaceRobotoNormal(llOrderPreview);
		
		if(isMenu)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		vecMainProducts = new Vector<ProductDO>();
		
		preference.saveBooleanInPreference("salesOrderPrited", false);
		preference.commitPreference();
		
		lvPreviewOrder = new CustomListView(SalesmanReturnOrderPreview.this);
		llMiddleLayout.addView(lvPreviewOrder,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		lvPreviewOrder.setCacheColorHint(0);
		lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
		lvPreviewOrder.setVerticalScrollBarEnabled(false);
		lvPreviewOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvPreviewOrder.addFooterView(llEsignature);
		lvPreviewOrder.addHeaderView(llHeaderLayout,null,false);
		
		lvPreviewOrder.setAdapter(orderPreviewAdapter = new OrderPreviewAdapter(SalesmanReturnOrderPreview.this, null,false,mallsDetails,TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()));
		
//		tvlanguage.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v)
//			{
//				ShowLanguagePopup();
//			}
//		});
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//initializing all Main Vector which contain all the product list
				Vector<String> vecCategoryIds = new Vector<String>();
				if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
				{
					hideLoader();
					return;
				}
				totalPerItemAmount = 0;
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				for(int i=0; i<vecCategoryIds.size(); i++)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(vecCategoryIds.get(i));
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(objProductDO.totalUnits > 0 )
						{
							totalCases 						= 	totalCases + StringUtils.getFloat(objProductDO.preCases);
							totalUnits 						= 	totalUnits + StringUtils.getFloat(objProductDO.preUnits);
							
							totalPerItemAmount 				= 	StringUtils.getFloat((totalPerItemAmount + objProductDO.itemPrice)+"");
							totalSalesPrice 				= 	StringUtils.getFloat((totalSalesPrice + objProductDO.totalPrice)+"");
							
							//Calculating total price per item 
							if(objProductDO.reason.equalsIgnoreCase(getResources().getString(R.string.return_)))
							{
								objProductDO.invoiceAmount 		= 	StringUtils.getFloat(objProductDO.unitSellingPrice*objProductDO.totalCases+"");
								objProductDO.discountAmount 	= 	StringUtils.getFloat((objProductDO.itemPrice - objProductDO.unitSellingPrice)*objProductDO.totalCases+"");
								
//								totalPerItemAmount 				= 	StringUtils.getFloat((totalPerItemAmount + objProductDO.itemPrice)+"");
//								totalSalesPrice 				= 	StringUtils.getFloat((totalSalesPrice + objProductDO.totalPrice)+"");
								totalInvoicedPrice 				= 	totalInvoicedPrice+objProductDO.invoiceAmount;
							}
							else
							{
								objProductDO.unitSellingPrice 	= 	0;
								objProductDO.invoiceAmount 		= 	0;
								objProductDO.discountAmount 	= 	0;
							}
							if(objProductDO.secondaryUOM == null || objProductDO.secondaryUOM.equalsIgnoreCase(""))
									objProductDO.secondaryUOM = "BOT";
							
							vecMainProducts.add(objProductDO);
						}
					}
				}
				
				//getting customer's total price by adding the total price of current order + pending balance
				totalAmount = totalInvoicedPrice;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						//Need to pass TrxHeaderDO
						orderPreviewAdapter.refresh(null);
						//setting text for the total-Layout
						tvOrderPreviewFooterCases.setText(""+(int)totalCases);
						tvOrderPreviewFooterunits.setText(""+(int)totalUnits);
						tvOrderPreviewPriceValue.setText(curencyCode+" "+decimalFormat.format(totalPerItemAmount));
						tvTotalAmountValue.setText(curencyCode+" "+decimalFormat.format(totalPrice));
						tvTotalAmountValue.setText(curencyCode+" "+decimalFormat.format(totalSalesPrice));
						tvDeliveryDateValue.setText(""+CalendarUtils.getOrderPostDate());
						tvOrderActualAmount.setText(curencyCode+" "+decimalFormat.format(totalInvoicedPrice));
						
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
						{
							tvCustomerSign.setText("Received By");
							tvPresellerSign.setText("Delivered By");
						}
						else
						{
							tvCustomerSign.setText(getString(R.string.Customer_Signature));
							tvPresellerSign.setText(getString(R.string.Preseller_Signature));
						}
						
						hideLoader();
					}
				});
			}
		}).start();
        
		btnFinalize.setTag("Finalize");
		btnFinalize.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("Finalize"))
				{
					if(!isCustomerSigned && !isPresellerSigned)
					{
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
							showCustomDialog(SalesmanReturnOrderPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReturnOrderPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
					}
					else if(!isCustomerSigned)
					{
						if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
							showCustomDialog(SalesmanReturnOrderPreview.this, "Alert !", "Please take Receiver's signature.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReturnOrderPreview.this, "Alert !", "Please take Customer's signature.", getString(R.string.OK), null, "scroll");
					}
					else if(!isPresellerSigned)
						if(from != null && from.equalsIgnoreCase("replacement"))
							showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.warning), "Please sign before submitting the replacement order.", getString(R.string.OK), null, "scroll");
						else
							showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.warning), "Please sign before submitting the return order.", getString(R.string.OK), null, "scroll");
					else
						postOrder(AppConstants.HHOrder);
				}
				else
					showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(isPosted || isSalesOrderGerenaterd)
					btnOrderPreviewContinue.performClick();
				else 
					finish();
			}
		});
		
		btnOrderPreviewContinue.setText(" Continue ");
		btnOrderPreviewContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showOrderCompletePopup();
			}
		});
		
		btnPrintSalesOrder.setText(" Print Order ");
		btnPrintSalesOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(SalesmanReturnOrderPreview.this, WoosimPrinterActivity.class);
//    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_RETURN);
//    			intent.putExtra("mallsDetails", mallsDetails);
//    			intent.putExtra("totalCases", totalCases);
//    			intent.putExtra("totalUnits", totalUnits);
//    			intent.putExtra("OrderId", orderDO.OrderId);
//    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
//    			intent.putExtra("totalPrice", totalPrice);
//				startActivityForResult(intent, 1000);
			}
		});
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        customerSignature = new MyView(SalesmanReturnOrderPreview.this, "csign");
		customerSignature.setDrawingCacheEnabled(true);
		customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		presellerSignature = new MyView(SalesmanReturnOrderPreview.this, "psign");
		presellerSignature.setDrawingCacheEnabled(true);
		presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
		
		if(customerSignature != null)
			llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		if(presellerSignature != null)
			llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
	
		btnCustomerSignClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				isCustomerSigned = false;
				llCustomerSign.removeAllViews();
				customerSignature = new MyView(SalesmanReturnOrderPreview.this, "csign");
				customerSignature.setDrawingCacheEnabled(true);
				customerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llCustomerSign.addView(customerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		});
		btnPresellerSignClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				isPresellerSigned = false;
				llPresellerSign.removeAllViews();
				presellerSignature = new MyView(SalesmanReturnOrderPreview.this,"psign");
				presellerSignature.setDrawingCacheEnabled(true);
				presellerSignature.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llPresellerSign.addView(presellerSignature, new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		});
	}
	public void postOrder(final String orderType)
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails.currencyCode == null || mallsDetails.currencyCode.length() <= 0)
					mallsDetails.currencyCode = curencyCode;
				
				//getting all the values for Order Table
				orderDO 				= 	new OrderDO();
				orderDO.BalanceAmount 	= 	totalAmount;
				orderDO.CustomerSiteId 	= 	mallsDetails.site;
				orderDO.strCustomerName	= 	mallsDetails.siteName;
				orderDO.DeliveryAgentId = 	"1";
				orderDO.DeliveryStatus 	= 	"E";
				orderDO.Discount 		= 	discount;
				orderDO.InvoiceDate 	= 	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				orderDO.PresellerId 	= 	preference.getStringFromPreference(Preference.SALESMANCODE, "");
				orderDO.empNo 			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				orderDO.TotalAmount 	= 	totalInvoicedPrice;
			
				if(from != null && from.equalsIgnoreCase("replacement"))
					orderDO.orderType		= 	AppConstants.REPLACEMETORDER;
				else
					orderDO.orderType		= 	AppConstants.RETURNORDER;
				
				orderDO.DeliveryDate 	= 	orderDO.InvoiceDate;
				orderDO.strUUID			= 	StringUtils.getUniqueUUID();
				orderDO.orderSubType	=	AppConstants.APPORDER;
				orderDO.strCustomerPriceKey	=  mallsDetails.priceList;
				orderDO.pushStatus		=	0;
				orderDO.message			=	"";
				
				orderDO.JourneyCode		=	mallsDetails.JourneyCode;
				orderDO.VisitCode		=	mallsDetails.VisitCode;
				orderDO.CurrencyCode	=	mallsDetails.currencyCode+"";
				orderDO.PaymentType		=	""+mallsDetails.customerPaymentMode;
//				orderDO.PaymentCode		=	""+mallsDetails.paymentCode;
				orderDO.TrxReasonCode	=	"";
				orderDO.LPOCode			=	"0";
				orderDO.StampDate		=	orderDO.InvoiceDate;
				orderDO.StampImage		=	"0000";
				orderDO.TRXStatus		=	"D";
				orderDO.salesmanCode	=	""+mallsDetails.salesmanCode;
				orderDO.vehicleNo   	=   preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				
				//pre-seller signature
				Bitmap bitmap = getBitmap(presellerSignature);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				orderDO.strPresellerSign = Base64.encodeBytes(stream.toByteArray());
				storeImage(bitmap, AppConstants.SALESMAN_SIGN);
				
				//customer signature
				Bitmap image = getBitmap(customerSignature);
				ByteArrayOutputStream streams = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
				orderDO.strCustomerSign = Base64.encodeBytes(streams.toByteArray());
				storeImage(image, AppConstants.CUSTOMER_SIGN);
				
				showLoader(getResources().getString(R.string.please_wait_order_pushing));
				
				orderDO.OrderId = ""+new OrderDA().insertOrderDetails_PromoNoOffer(orderDO, vecMainProducts, preference);
				
				if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
				{
					
					new OrderDA().updateInventoryStatusReturn(vecMainProducts, CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.EMP_NO, ""));
//					new OrderDA().updateInventoryStatus(vecMainProducts, CalendarUtils.getOrderPostDate());
					new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+(-totalInvoicedPrice), orderDO.OrderId,0);
//					new CustomerDA().updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
					uploadData();
					isSalesOrderGerenaterd = true;
				}
				else
					isSalesOrderGerenaterd = false;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isSalesOrderGerenaterd)
						{
//							showOrderCompletePopup();
//						
								if(!isMenu && mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && orderId != null && orderId.length() > 0)
								{
									if(! new PaymentDetailDA().isPaymentDone(orderId))
										onButtonYesClick("payment");
									else
										onButtonYesClick("servedtemp");
								}
								else
									onButtonYesClick("servedtemp");
//							
							btnPrintSalesOrder.setVisibility(View.GONE);
							btnOrderPreviewContinue.setVisibility(View.VISIBLE);
							btnFinalize.setVisibility(View.GONE);
						}
						else
							showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.warning), "Error occurred while taking order.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
	}
		
	private void showVisibleButton(final boolean isVisible)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isVisible)
				{
					btnOrderPreviewContinue.setVisibility(View.VISIBLE);
					btnFinalize.setVisibility(View.GONE);
				}
				else
				{
					btnOrderPreviewContinue.setVisibility(View.GONE);
					btnFinalize.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private void InitializeControls() 
	{
		llMiddleLayout 	    		= 	(LinearLayout)llOrderPreview.findViewById(R.id.llOrderPreviewMidle);
		llOrderPreviewBottom		= 	(LinearLayout)llOrderPreview.findViewById(R.id.llOrderPreviewBottom);
		tvHeaderPreview 			= 	(TextView)llOrderPreview.findViewById(R.id.tvOrderPreviewHeader);
		btnFinalize	 				= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewFinalize);
		btnPrintSalesOrder			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrder);
		btnPrintMerchant			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
		btnOrderPreviewContinue		= 	(Button)llOrderPreview.findViewById(R.id.btnOrderPreviewContinue);
		btnPrintSalesOrderMerchant	= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
//		tvlanguage					= 	(TextView)llOrderPreview.findViewById(R.id.tvlanguage);
		TextView tvLu				= 	(TextView)llOrderPreview.findViewById(R.id.tvLu);
		
		//Masafi logo Layout as header of list view
		llHeaderLayout      		= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header, null);
		tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
		tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
		tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
		TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
		TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
		TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
		TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
		
		//Signature Layout as footer of list view
		llEsignature 				= (LinearLayout)getLayoutInflater().inflate(R.layout.esignature_order_preview, null);
		llPresellerSign 			= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSignature_e);
		llCustomerSign 				= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSignature_e);
		llPresellerSignHeader		= (LinearLayout)llEsignature.findViewById(R.id.llPresellerSign);
		llCustomerSignHeader		= (LinearLayout)llEsignature.findViewById(R.id.llCustomerSign);
		tvPresellerSign 			= (TextView)llEsignature.findViewById(R.id.tvPresellerSignature_e);
		tvCustomerSign    			= (TextView)llEsignature.findViewById(R.id.tvCustomerSignature_e);
		tvCustomerPasscode			= (TextView)llEsignature.findViewById(R.id.tvCustomerPasscode_e);
		etCustomerPasscode			= (EditText)llEsignature.findViewById(R.id.etCustomerPasscode_e);
		tvDeliveryDate				= (TextView)llEsignature.findViewById(R.id.tvDeliveryDate);
		tvDeliveryDateValue			= (TextView)llEsignature.findViewById(R.id.tvDeliveryDateValue);
		tvDeliveryToTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTimeValue);
		tvDeliveryFromTimeValue		= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTimeValue);
		tvDeliveryToTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryToTime);
		tvDeliveryFromTime			= (TextView)llEsignature.findViewById(R.id.tvDeliveryFromTime);
		tvOrderActualAmount			= (TextView)llEsignature.findViewById(R.id.tvOrderActualAmount);
		tvTotalAmount				= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmount);
		tvTotalAmountValue 			= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewTotalAmountValue);
		tvOrderPreviewPriceValue	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewPriceValue);
		tvOrderPreviewFooterCases	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterCases);
		tvOrderPreviewFooterunits	= (TextView)llEsignature.findViewById(R.id.tvOrderPreviewFooterunits);
		btnCustomerSignClear		= (Button)llEsignature.findViewById(R.id.btnCustomerSignClear);
		btnPresellerSignClear		= (Button)llEsignature.findViewById(R.id.btnPresellerSignClear);
		LinearLayout llCustomer_Passcode	= (LinearLayout)llEsignature.findViewById(R.id.llCustomer_Passcode);
		LinearLayout llDeliveryDate			= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDate);
		LinearLayout llDeliveryDeliveryTime	= (LinearLayout)llEsignature.findViewById(R.id.llDeliveryDeliveryTime);
		TextView tvBottomDist			= (TextView)llEsignature.findViewById(R.id.tvBottomDist);
		
		tvDiscount.setVisibility(View.VISIBLE);
		tvInvoiceAmount.setVisibility(View.VISIBLE);
		tvTotalPrice.setVisibility(View.VISIBLE);
		tvPrice.setVisibility(View.VISIBLE);
		
		tvTotalAmountValue.setVisibility(View.VISIBLE);
		tvOrderPreviewPriceValue.setVisibility(View.VISIBLE);
		tvOrderActualAmount.setVisibility(View.VISIBLE);
		 
		llDeliveryDate.setVisibility(View.GONE);
		llCustomer_Passcode.setVisibility(View.GONE);
		llDeliveryDeliveryTime.setVisibility(View.GONE);
		
		if(mallsDetails.channelCode != null && mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			tvDiscount.setVisibility(View.GONE);
			tvInvoiceAmount.setVisibility(View.GONE);
			tvPrice.setVisibility(View.GONE);
			tvTotalPrice.setVisibility(View.GONE);
			
			tvOrderPreviewPriceValue.setVisibility(View.GONE);
			tvTotalAmountValue.setVisibility(View.GONE);
			tvOrderActualAmount.setVisibility(View.GONE);
			
//			llTotalPrice.setVisibility(View.GONE);
			
			tvBottomDist.setVisibility(View.GONE);
			
			tvOrderPreviewunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
			tvOrderPreviewFooterunits.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.width_for_pd), LayoutParams.WRAP_CONTENT));
		}
		
		
		//setting TypeFaces here
		tvDiscount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvInvoiceAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOrderPreviewContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderActualAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryToTimeValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryFromTimeValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryToTime.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryFromTime.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryDate.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDeliveryDateValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCustomerSignClear.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPresellerSignClear.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnFinalize.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrintMerchant.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrintSalesOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOrderPreviewContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderPreviewCases.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeaderPreview.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderPreviewunits.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalAmountValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderPreviewPriceValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderPreviewFooterCases.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderPreviewFooterunits.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvPresellerSign.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerSign.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerPasscode.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etCustomerPasscode.setTypeface(AppConstants.Roboto_Condensed_Bold);
	
		tvTotalPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		btnPrintSalesOrderMerchant.setVisibility(View.GONE);
		btnPrintSalesOrder.setVisibility(View.GONE);
		if(from != null && from.equalsIgnoreCase("replacement"))
			tvHeaderPreview.setText("Preview Replacement Order");
		else
			tvHeaderPreview.setText("Preview Return Order");
		showVisibleButton(false);
		
		tvLu.setText(mallsDetails.siteName +" ["+mallsDetails.site+"]" /*+ " ("+mallsDetails.partyName+")"*/);
	}

	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("enterPasscode"))
		{
			if(lvPreviewOrder.isScrolled())
			{
				lvPreviewOrder.setScrolled(false);
				lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount()-1).getTop());
				etCustomerPasscode.requestFocus();
			}
		}
		else if(from.equalsIgnoreCase("payment"))
		{
			Intent intent = new Intent(SalesmanReturnOrderPreview.this, PendingInvoices.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("AR", false);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("scroll"))
		{
			if(lvPreviewOrder.isScrolled())
			{
				lvPreviewOrder.setScrolled(false);
				lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount()-1).getTop());
			}
		}
		else if(from.equalsIgnoreCase("orderposted"))
		{
			setResult(10000);
			finish();
		}
		else if(from.equalsIgnoreCase("served"))
		{
			if(isMenu)
				performCheckouts(false);
			else
				performCustomerServed();	
		}
		else if(from.equalsIgnoreCase("servedtemp"))
		{
			orderId = orderDO.OrderId;
			Intent intent = new Intent(SalesmanReturnOrderPreview.this, ReturnOrderActivity.class);
			intent.putExtra("object", mallsDetails);
			intent.putExtra("orderId", orderId);
			startActivity(intent);
			finish();
			
		}
		else if(from.equalsIgnoreCase("payment_new"))
		{
			Intent intent = new Intent(SalesmanReturnOrderPreview.this, PendingInvoices.class);
			intent.putExtra("arcollection", false);
			intent.putExtra("isReturnOrder", true);
			intent.putExtra("AR", false);
			
			if(orderId != null && orderId.length() > 0)
			{
				orderInvoice = orderInvoice - totalInvoicedPrice;
				
				intent.putExtra("selectedAmount", orderInvoice);
				intent.putExtra("invoiceAmount", orderInvoice);
				intent.putExtra("invoiceNo", orderId);
			}
			else
			{
				intent.putExtra("selectedAmount", totalInvoicedPrice);
				intent.putExtra("invoiceAmount", totalInvoicedPrice);
				intent.putExtra("invoiceNo", orderDO.OrderId);
			}
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
	}
	
	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		
		if(from.equalsIgnoreCase("payment_new"))
		{
			showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.successful), "Return order confirmed successfully. You served this customer.", "OK",null, "served", false);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 2222)
    	{
    		AppConstants.isServeyCompleted = true;
    	}
    	else if(resultCode == 20000)
    	{
			showCustomDialog(SalesmanReturnOrderPreview.this, getString(R.string.successful), getString(R.string.your_sales_order_printed), getString(R.string.OK), null , "");
    	}
    }
	 
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPosted || isSalesOrderGerenaterd)
			btnOrderPreviewContinue.performClick();
		else 
			finish();
	}
	
	public class MyView extends View 
	{
		private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private String strFrom= "";
        float x,y;
        
        public MyView(Context c, String strFrom)
        {
            super(c);
            this.strFrom = strFrom;
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();
            int height = display.getHeight();
            if(mBitmap != null)
            {
            	mBitmap.recycle();
            	mBitmap = null;
            	System.gc();
            }
            
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(2);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y)
        {
        	lvPreviewOrder.setScrollable(false);
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) 
        {
          float dx = Math.abs(x - mX);
          float dy = Math.abs(y - mY);
          if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
          {
	           mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	           mX = x;
	           mY = y;
          }
      }
      private void touch_up()
      {
          mPath.lineTo(mX, mY);
          mCanvas.drawPath(mPath, mPaint);
          mPath.reset();
          lvPreviewOrder.setScrollable(true);
          if(strFrom.equalsIgnoreCase("psign"))
        	  isPresellerSigned = true;
          else if(strFrom.equalsIgnoreCase("csign"))
        	  isCustomerSigned = true;
      }
      @Override
      public boolean onTouchEvent(MotionEvent event) 
      {
    	  x = event.getX();
    	  y = event.getY();
	            
    	  switch (event.getAction()) 
    	  {
    	  case MotionEvent.ACTION_DOWN:
    		  touch_start(x, y);
    		  invalidate();
    		  break;
    		  
    	  case MotionEvent.ACTION_MOVE:
    		  touch_move(x, y);
    		  invalidate();
    		  break;
	                
    	  case MotionEvent.ACTION_UP:
    		  touch_up();
    		  invalidate();
    		  break;
    	  }
	        	return true;
      	}
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(SalesmanReturnOrderPreview.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1		  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Return Order Placed");
		tv_poptitle1.setText("Successfully");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment		  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_returnreq		  = (Button) view.findViewById(R.id.btn_popup_returnreq);
		Button btn_popup_task	  = (Button) view.findViewById(R.id.btn_popup_task);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		Button btn_popup_survey		  = (Button) view.findViewById(R.id.btn_popup_survey);
		Button btnPlaceNewOrder		  = (Button) view.findViewById(R.id.btnPlaceNewOrder);
		
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.GONE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_MODERN))
			btnPlaceNewOrder.setVisibility(View.GONE);
		
		if(AppConstants.isServeyCompleted)
		{
			btn_popup_survey.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.order_order), null, getResources().getDrawable(R.drawable.check1_new), null);
			btn_popup_survey.setClickable(false);
			btn_popup_survey.setEnabled(false);
		}
		
		if(AppConstants.isTaskCompleted)
		{
			btn_popup_task.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.taks_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		}
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
//				Intent intent = new Intent(SalesmanReturnOrderPreview.this, WoosimPrinterActivity.class);
//    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_RETURN);
//    			intent.putExtra("mallsDetails", mallsDetails);
//    			intent.putExtra("totalCases", totalCases);
//    			intent.putExtra("totalUnits", totalUnits);
//    			intent.putExtra("OrderId", orderDO.OrderId);
//    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
//    			intent.putExtra("totalPrice", totalPrice);
//    			intent.putExtra("from", from);
//				startActivityForResult(intent, 1000);
				showToast("Print functionality is in progress.");
//				//Harcoded
			}
		});
		
		btn_popup_survey.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
//				Intent intent = new Intent(SalesmanReturnOrderPreview.this, ConsumerBehaviourSurveyActivityNew.class);
//				intent.putExtra("object", mallsDetails);
//				startActivityForResult(intent, 2222);
				
				Intent intent = new Intent(SalesmanReturnOrderPreview.this, ServeyListActivity.class);
				startActivity(intent);
			}
		});
		
		if(mallsDetails != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.VISIBLE);
		}
		
		btnPlaceNewOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
					AppConstants.hmCapturedInventory.clear();
				
				Intent intent = new Intent(SalesmanReturnOrderPreview.this, SalesManTakeOrder.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("from", "checkin");
				startActivity(intent);
			}
		});
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			btn_popup_collectpayment.setVisibility(View.GONE);
		else
		{
			if(totalInvoicedPrice > 0)
			{
//				btn_popup_collectpayment.setVisibility(View.VISIBLE);
//				btn_popup_collectpayment.setText("Return Amount");
				
				btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check2_new), null);
				btn_popup_collectpayment.setClickable(true);
				btn_popup_collectpayment.setEnabled(true);
			}
			else 
			{
				btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
				btn_popup_collectpayment.setClickable(false);
				btn_popup_collectpayment.setEnabled(false);
			}
		}
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("payment_new");
			}
		});
		
		btn_popup_returnreq.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.return_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_returnreq.setClickable(false);
		btn_popup_returnreq.setEnabled(false);
		
		if(from != null && (from.equalsIgnoreCase("replacement")))
		{
			btn_popup_returnreq.setText("Replacement Order");
			tv_poptitle.setText("Replacement Order Placed");
		}
		
		btn_popup_task.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				Intent intent = new Intent(SalesmanReturnOrderPreview.this, TaskToDoActivity.class);
				intent.putExtra("object", mallsDetails);
				startActivity(intent);
			}
		});
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				if(!isMenu && mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && orderId != null && orderId.length() > 0)
				{
					if(! new PaymentDetailDA().isPaymentDone(orderId))
						onButtonYesClick("payment");
					else
						onButtonYesClick("served");
				}
				else
					onButtonYesClick("served");
			}
		});
		
		if(from != null && from.equalsIgnoreCase("replacement"))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			if(isTask)
			{
				btn_popup_survey.setVisibility(View.VISIBLE);
				btn_popup_task.setVisibility(View.VISIBLE);
			}
			else 
			{
				btn_popup_survey.setVisibility(View.GONE);
				btn_popup_task.setVisibility(View.GONE);
			}
		}
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	
//	protected void ShowLanguagePopup(){
//
//		View view = inflater.inflate(R.layout.custom_popup_language, null);
//		final CustomDialog mCustomDialog = new CustomDialog(SalesmanReturnOrderPreview.this, view, preference
//				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
//				LayoutParams.WRAP_CONTENT, true);
//		mCustomDialog.setCancelable(true);
//
//		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
//
//		final Button btn_popup_English		  = (Button) view.findViewById(R.id.btn_popup_English);
//		final Button btn_popup_Arabic	 		  = (Button) view.findViewById(R.id.btn_popup_Arabic);
//		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);
//
//		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btn_popup_English.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btn_popup_Arabic.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btn_popup_cancel.setTypeface(AppConstants.Roboto_Condensed_Bold);
//
//		btn_popup_English.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
////				tvlanguage.setText("English");
//				lvPreviewOrder.removeHeaderView(llHeaderLayout);
////				llHeaderLayout.removeAllViews();
//				llHeaderLayout = null;
//				llHeaderLayout   = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header, null);
//
//				lvPreviewOrder.addHeaderView(llHeaderLayout);
//				
//				tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
//				tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
//				tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
//				TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
//				TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
//				TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
//				TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
//				orderPreviewAdapter.refresh1(vecMainProducts,"english");
//				
//				tvTotalPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvInvoiceAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvDiscount.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvPresellerSign.setText(getString(R.string.Preseller_Signature));
//				tvCustomerSign.setText(getString(R.string.Customer_Signature));
//				btnCustomerSignClear.setText(getString(R.string.Clear));
//				btnPresellerSignClear.setText(getString(R.string.Clear));
//				mCustomDialog.dismiss();
//			}
//		});
//
//		btn_popup_Arabic.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
////				Intent in = new Intent(SalesmanOrderPreview.this, SalesmanOrderPreview.class);
//				lvPreviewOrder.removeHeaderView(llHeaderLayout);
////				llHeaderLayout.removeAllViews();
//				llHeaderLayout = null;
//				llHeaderLayout   = (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_ar, null);
//
//				lvPreviewOrder.addHeaderView(llHeaderLayout);
//				
//				tvItemName    				= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
//				tvOrderPreviewunits			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
//				tvOrderPreviewCases			= (TextView)llHeaderLayout.findViewById(R.id.tvOrderPreviewCases);
//				TextView tvTotalPrice		= (TextView)llHeaderLayout.findViewById(R.id.tvTotalPrice);
//				TextView tvPrice			= (TextView)llHeaderLayout.findViewById(R.id.tvPrice);
//				TextView tvInvoiceAmount	= (TextView)llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
//				TextView tvDiscount			= (TextView)llHeaderLayout.findViewById(R.id.tvDiscount);
//				
//				orderPreviewAdapter.refresh1(vecMainProducts,"arabic");
//				tvTotalPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvInvoiceAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvDiscount.setTypeface(AppConstants.Roboto_Condensed_Bold);
//				tvPresellerSign.setText(getString(R.string.salesmansign_ar));
//				tvCustomerSign.setText(getString(R.string.customersign_ar));
////				tvlanguage.setText("Arabic");
//				btnCustomerSignClear.setText(getString(R.string.clear_ar));
//				btnPresellerSignClear.setText(getString(R.string.clear_ar));
//				mCustomDialog.dismiss();
//			}
//		});
//		btn_popup_cancel.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
//				mCustomDialog.dismiss();
//			}
//		});
//
//		try{
//			if (!mCustomDialog.isShowing())
//				mCustomDialog.show();
//		}catch(Exception e){}
//
//	}
}
