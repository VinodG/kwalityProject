package com.winit.sfa.salesman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.OrderPreviewAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomBuilderProduct;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.AgencyNewDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class OrderSummaryDetail extends BaseActivity
{
	private LinearLayout llstamentDiscValue,llSummaryofDay,llTotalAmount, llDeliveryStatus,llFOCTotalQty,llOrderValue,llVATValue,llNETValue,llDiscValue,llSplDiscValue,llDiscount;
	private Button btnOK, btnUpload;
	private ListView lvSalesOrder;
	private ArrayList<TrxDetailsDO> arrayList;
	private TrxHeaderDO trxHeaderDO;
	private OrderPreviewAdapter orderPreviewAdapter;
	private Bundle bundle;
	private TextView tvPageTitle,tvOrderStatusTitle,tvOrderStatusVal,tvCustomerName,tvCustomerLocation,
			tvOrderNoTitle,tvNoOrderFound,tvOrderNoVal, tvOrderValue,tvVATValue,tvNetValue, tvDiscValue,tvTotalValue,tvCurrentTime,tvSplDiscValue,tvstmntDiscValue,
			tvOrderQtyColon,tvOrderColon,tvDiscountColon,tvTotalQty,tvSplDiscountPer,
			tvSplDiscountColon,tvNETColon,tvSpclDiscount,tvOrderValueTitle,tvNETValueTitle,tvVATValueTitle,tvFOCTotalQty,llDiscountVal,Stmtntdiscountcolonn,llStatementDiscount;
	boolean isMissedOrder;
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();

//====================================newly added for food===============================kwality =200 and pic = 100
	 AgencyNewDo AgnCustom;
	 private Vector<AgencyNewDo> vecagn= null;
	 int printTypeIce =100;
	 private Vector<String> printOptions= null;
	HashMap<String, String> hmShiftCodes = new HashMap<String, String>();

	
	@Override
	public void initialize() 
	{
		bundle = getIntent().getExtras();
		if(bundle!= null)
		{
			trxHeaderDO	 	=	(TrxHeaderDO) bundle.get("trxHeaderDO");
			arrayList       =   trxHeaderDO.arrTrxDetailsDOs;
		}
//
//		if(trxHeaderDO.Division == TrxHeaderDO.get_DIVISION_FOOD())
//		{
//			trxHeaderDO.statementdiscountvalue = (trxHeaderDO)*100/trxHeaderDO.totalDiscountAmount
//			trxHeaderDO.statementDiscount= trxHeaderDO.totalDiscountAmount
//		}
//		if(trxHeaderDO.Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY())
//		{
//
//		}

		sortTrxDetail(trxHeaderDO.arrTrxDetailsDOs);
		
		isMissedOrder  = (trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_MISSED_ORDER());
		
		
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.order_summaryofday, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		
		intializeControls();
		loadData();
		if(trxHeaderDO != null)
		{
			tvOrderNoTitle.setText("Order No.");
			
			tvOrderStatusVal.setText(getOrderStatus(trxHeaderDO.trxStatus));
			
			
			if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			{
				int diff = CalendarUtils.getDiffBtwDatesInDays((trxHeaderDO.deliveryDate).split("T")[0], CalendarUtils.getCurrentDateAsStringforStoreCheck());
				if(diff > 0)
					tvOrderStatusVal.setText("Delivered");
				else
					tvOrderStatusVal.setText("Pending");
			}
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			{
				llDeliveryStatus.setVisibility(View.GONE);
			}
			else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) 
			{
				llDeliveryStatus.setVisibility(View.GONE);
			}
			else if (trxHeaderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER()) 
			{
				llDeliveryStatus.setVisibility(View.GONE);
			}
			
			if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			{
				llFOCTotalQty.setVisibility(View.VISIBLE);
				tvOrderQtyColon.setVisibility(View.VISIBLE);
				tvTotalQty.setVisibility(View.VISIBLE);
				
				tvOrderValueTitle.setVisibility(View.GONE);
				tvNETValueTitle.setVisibility(View.GONE);
				llDiscountVal.setVisibility(View.GONE);
				llOrderValue.setVisibility(View.GONE);
				llVATValue.setVisibility(View.GONE);
				llNETValue.setVisibility(View.GONE);
				llDiscValue.setVisibility(View.GONE);
				tvDiscountColon.setVisibility(View.GONE);
				tvOrderColon.setVisibility(View.GONE);
				tvSpclDiscount.setVisibility(View.GONE);
				llSplDiscValue.setVisibility(View.GONE);
				tvSplDiscountColon.setVisibility(View.GONE);
				tvNETColon.setVisibility(View.GONE);

				Stmtntdiscountcolonn.setVisibility(View.GONE);
				llStatementDiscount.setVisibility(View.GONE);
				llstamentDiscValue.setVisibility(View.GONE);
			}
			else
			{
				llFOCTotalQty.setVisibility(View.GONE);
				tvOrderQtyColon.setVisibility(View.GONE);
				tvTotalQty.setVisibility(View.GONE);
				
				tvOrderValueTitle.setVisibility(View.VISIBLE);
				tvNETValueTitle.setVisibility(View.VISIBLE);
				tvVATValueTitle.setVisibility(View.VISIBLE);
				llDiscountVal.setVisibility(View.VISIBLE);
				llOrderValue.setVisibility(View.VISIBLE);
				llVATValue.setVisibility(View.VISIBLE);
				llNETValue.setVisibility(View.VISIBLE);
				llDiscValue.setVisibility(View.VISIBLE);
				tvDiscountColon.setVisibility(View.VISIBLE);
				tvOrderColon.setVisibility(View.VISIBLE);
				tvSpclDiscount.setVisibility(View.VISIBLE);
				llSplDiscValue.setVisibility(View.VISIBLE);
				tvSplDiscountColon.setVisibility(View.VISIBLE);
				tvNETColon.setVisibility(View.VISIBLE);
			}
		}
		
		btnOK.setText(" Finish  ");
		btnOK.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
	    getTotalAmountWithDiscountAfterDelivery();
		
	    btnUpload.setText(" Print Order ");
		btnUpload.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print), null, null, null);
		btnUpload.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//===========================================newly added========================================	
				
				if(trxHeaderDO.Division==trxHeaderDO.get_DIVISION_FOOD()|| trxHeaderDO.Division==trxHeaderDO.get_DIVISION_THIRD_PARTY())
				{
					CustomBuilderProduct builder = new CustomBuilderProduct(OrderSummaryDetail.this, "Please Select\nRequest Type", true);
					builder.setSingleChoiceItems(vecagn, null,new CustomBuilderProduct.OnClickListener()
					 {
						
						@Override
						public void onClick(CustomBuilderProduct builder, Object selectedObject) {
							if(selectedObject!=null)
								AgnCustom = (AgencyNewDo) selectedObject;
							builder.dismiss();
							
							if(AgnCustom!=null && AgnCustom.Priority==200)
							{
								printTypeIce= 200;
								
							}
							else
								printTypeIce=100;
							
							goToWoosimPrintActivity();

						}
						});
						
						builder.show();	
				}
				else{
					goToWoosimPrintActivity();	
				}
				
				
			}

			
		});
		
		setTypeFaceRobotoNormal(llSummaryofDay);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btnOK.setTypeface(Typeface.DEFAULT_BOLD);
		tvOrderNoTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderStatusTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOK.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnUpload.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	protected void goToWoosimPrintActivity() {
		// TODO Auto-generated method stub
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				final JourneyPlanDO mallsDetails	=	new CustomerDetailsDA().getCustometBySiteId(trxHeaderDO.clientCode);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(arrayList != null && arrayList.size() > 0 && trxHeaderDO != null)
						{
							Intent intent = new Intent(OrderSummaryDetail.this, WoosimPrinterActivity.class);
							intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
							intent.putExtra("trxHeaderDO", trxHeaderDO);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("str", " - Reprint");
//							if(trxHeaderDO!=null   && (trxHeaderDO.Division==TrxHeaderDO.get_DIVISION_FOOD() ||trxHeaderDO.Division==TrxHeaderDO.get_DIVISION_THIRD_PARTY())) printTypeIce=200; //newly added for thirdpary
//======================================== Newly Added by for food=========================
							intent.putExtra("PrintTypeIce", printTypeIce);
//===================================================================							
							startActivity(intent);
						}
						else
							showCustomDialog(OrderSummaryDetail.this, "Warning !", "Error occurred while printing.", "OK", null, "");
					}
				});
			}
		}).start();
		
	}

	private void sortTrxDetail(ArrayList<TrxDetailsDO> arrTrxDetailsDOs) 
	{
		Collections.sort(arrTrxDetailsDOs, new Comparator<TrxDetailsDO>() {
		    @Override
		   public int compare(TrxDetailsDO s1, TrxDetailsDO s2) {
		    	return (s1.DisplayOrder) - (s2.DisplayOrder);
		    }
		});
	}
	
	int focItemCount = 0;
	float invoiceAmount = 0.0f;
	float totalvatAmt = 0.0f;

	private void loadData(){
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() {
			@Override
			public void run() {
				mallsDetailss = new CustomerDetailsDA().getCustometBySiteId(trxHeaderDO.clientCode);
				if(mallsDetailss!=null) {
					hashMapPricing = new CaptureInventryDA().getPricing(mallsDetailss.priceList, trxHeaderDO.trxType);
					hmShiftCodes=new CustomerDA().getShiftToCodes();
				}
				updateItemPrice();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						hideLoader();
 						orderPreviewAdapter = new OrderPreviewAdapter(OrderSummaryDetail.this, trxHeaderDO.arrTrxDetailsDOs,isMissedOrder,isMissedOrder, trxHeaderDO.trxType);
						lvSalesOrder.setAdapter(orderPreviewAdapter);
						tvCustomerLocation.setText(hmShiftCodes.get(mallsDetailss.site+"")+"");
						
						for (int i = 0; i < trxHeaderDO.arrTrxDetailsDOs.size(); i++) 
						{
							TrxDetailsDO objItem = trxHeaderDO.arrTrxDetailsDOs.get(i);
							focItemCount += objItem.quantityLevel1;
							totalvatAmt+=objItem.VATAmountNew;
							if((trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_ORDER()))
								invoiceAmount += (objItem.CSPrice * objItem.quantityBU)-objItem.totalDiscountAmount - objItem.promotionalDiscountAmount;
							else
								invoiceAmount += (objItem.CSPrice * objItem.quantityBU);
						}
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run() 
							{
								if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
								{
//									tvFOCTotalQty.setText(""+orderPreviewAdapter.getFOCQuantity());
//									tvTotalValue.setText(""+decimalFormat.format(orderPreviewAdapter.getInvoiceAmount()/* - trxHeaderDO.specialDiscount - getDiscountAmount(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))*/));
									
									tvFOCTotalQty.setText(""+focItemCount);
									tvTotalValue.setText(""+(invoiceAmount+totalvatAmt));
									tvVATValue.setText(""+totalvatAmt);
									tvNetValue.setText(""+invoiceAmount);
								}
							}
						}, 200);
//						showAddNewSkuPopUp();
					}
				});
			}
		}).start();
		
	}
	
	private double getDiscountAmount(double discountPercent)
	{
		double amount = 0;
		amount = (discountPercent * 100)/trxHeaderDO.totalAmount;
		return amount;
	}
	private void updateItemPrice(){
		try {
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
//			{
				for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
				{
					if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) 
					{
						if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
							trxDetailsDO.EAPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());
						
						if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
							trxDetailsDO.CSPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel1());
					}
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void intializeControls()
	{
		tvPageTitle					=	(TextView)llSummaryofDay.findViewById(R.id.tvPageTitle);
		
		tvOrderNoTitle		  		=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderNoTitle);
		tvOrderNoVal		 		=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderNoVal);
		tvOrderStatusTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderStatusTitle);
		btnOK				  		=	(Button)llSummaryofDay.findViewById(R.id.btnOK);
		btnUpload			 		=	(Button)llSummaryofDay.findViewById(R.id.btnUpload);
		
//		tvDeliveryAmount			=   (TextView)llSummaryofDay.findViewById(R.id.tvDeliveryAmount);
		tvNoOrderFound				=   (TextView)llSummaryofDay.findViewById(R.id.tvNoOrderFound);
		tvCustomerName		  	 	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerName);
		tvCustomerLocation		  	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerLocation);
		tvOrderStatusVal			=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderStatusVal);
		
		tvCurrentTime				=   (TextView)llSummaryofDay.findViewById(R.id.tvCurrentTime);
		tvSplDiscValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvSplDiscValue);
		tvstmntDiscValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvstmntDiscValue);
		lvSalesOrder				=   (ListView)llSummaryofDay.findViewById(R.id.lvOrderList);
		
		//Newly Added by sudheer Total amount shifted from bottom to top
		llTotalAmount				=	(LinearLayout)llSummaryofDay.findViewById(R.id.llTotalAmount);
		llDeliveryStatus			=	(LinearLayout) llSummaryofDay.findViewById(R.id.llDeliveryStatus);
		llFOCTotalQty				=	(LinearLayout) llSummaryofDay.findViewById(R.id.llFOCTotalQty);
		llDiscount					=   (LinearLayout)llSummaryofDay.findViewById(R.id.llDiscount);
		llOrderValue				=   (LinearLayout)llSummaryofDay.findViewById(R.id.llOrderValue);
		llVATValue				=   (LinearLayout)llSummaryofDay.findViewById(R.id.llVATValue);
		llNETValue				=   (LinearLayout)llSummaryofDay.findViewById(R.id.llNETValue);
		llDiscValue					=   (LinearLayout)llSummaryofDay.findViewById(R.id.llDiscValue);
		llSplDiscValue				=   (LinearLayout)llSummaryofDay.findViewById(R.id.llSplDiscValue);
		
		tvOrderValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderValue);
		tvVATValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvVATValue);
		tvNetValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvNetValue);
		tvDiscValue					=   (TextView)llSummaryofDay.findViewById(R.id.tvDiscValue);
		tvTotalValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvTotalValue);
		tvOrderQtyColon				=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderQtyColon);
		tvOrderColon				=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderColon);
		tvDiscountColon				=   (TextView)llSummaryofDay.findViewById(R.id.tvDiscountColon);
		tvTotalQty					=   (TextView)llSummaryofDay.findViewById(R.id.tvTotalQty);
		tvSplDiscountPer			=   (TextView)llSummaryofDay.findViewById(R.id.tvSplDiscountPer);
		tvSplDiscountColon			=   (TextView)llSummaryofDay.findViewById(R.id.tvSplDiscountColon);
		tvNETColon			=   (TextView)llSummaryofDay.findViewById(R.id.tvNETColon);
		tvSpclDiscount				=   (TextView)llSummaryofDay.findViewById(R.id.llSpclDiscount);
		tvOrderValueTitle			=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderValueTitle);
		tvVATValueTitle			=   (TextView)llSummaryofDay.findViewById(R.id.tvVATValueTitle);
		tvNETValueTitle			=   (TextView)llSummaryofDay.findViewById(R.id.tvNETValueTitle);
		tvFOCTotalQty				=   (TextView)llSummaryofDay.findViewById(R.id.tvFOCTotalQty);
		llDiscountVal				=   (TextView)llSummaryofDay.findViewById(R.id.llDiscountVal);
		Stmtntdiscountcolonn		=   (TextView)llSummaryofDay.findViewById(R.id.Stmtntdiscountcolonn);
		llStatementDiscount			=   (TextView)llSummaryofDay.findViewById(R.id.llStatementDiscount);
		llstamentDiscValue			=   (LinearLayout)llSummaryofDay.findViewById(R.id.llstamentDiscValue);
		
		lvSalesOrder.setCacheColorHint(0);
		lvSalesOrder.setVerticalScrollBarEnabled(false);
		lvSalesOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSalesOrder.setFadingEdgeLength(0);
		
		if(arrayList == null || arrayList.size() == 0)
		{
			tvNoOrderFound.setVisibility(View.VISIBLE);
			lvSalesOrder.setVisibility(View.GONE);
		}
		else
		{
			tvNoOrderFound.setVisibility(View.GONE);
			lvSalesOrder.setVisibility(View.VISIBLE);
		}
		
		if(trxHeaderDO!=null)
		{
			tvCustomerName.setText(trxHeaderDO.siteName+" ["+trxHeaderDO.clientCode+"]");
//			tvCustomerLocation.setText(trxHeaderDO.address);
		}
		
		if(isMissedOrder)
		{
			tvPageTitle.setText("Missed Order Details");
			tvPageTitle.setTextAppearance(OrderSummaryDetail.this, R.style.MissedOrderTextHeaderGray);
//			tvDeliveryAmount.setVisibility(View.GONE);
			llTotalAmount.setVisibility(View.GONE);
			
//			edtDeliveryTotalamt.setVisibility(View.VISIBLE);
		}
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
		{
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				tvPageTitle.setText("Presales Order Details");
			}
			else
			{
				tvPageTitle.setText("Advance Order Details");
			}
//			tvDeliveryAmount.setVisibility(View.GONE);
			
			//As edtDeliveryTotalamt set to gone in advanced order, Total amount layout is also set to gone
			// code by sudheer
			//Reverted back by Aritra
			llTotalAmount.setVisibility(View.VISIBLE);
		}
		else
			tvPageTitle.setText("Order Detail");
			
		tvOrderNoVal.setText(""+trxHeaderDO.trxCode);
		
		tvCurrentTime.setText(CalendarUtils.getFormattedSummaryDate(trxHeaderDO.trxDate+""));
	}
	
	
	private String getOrderStatus(int status)
	{
		String orderStatus = "";
		switch(status)
		{
//			case TrxHeaderDO.TRX_TYPE_GRV_STATUS_PENDING:
//				orderStatus = "Pending";
//				break;
//			case TrxHeaderDO.TRX_TYPE_GRV_STATUS_APPROVED:
//				orderStatus = "Approved";
//				break;
//			case TrxHeaderDO.TRX_TYPE_GRV_STATUS_COLLECTED:
//				orderStatus = "Collected";
//				break;
			case TrxHeaderDO.TRX_STATUS_GRV_APPROVED:
				orderStatus = "Rejected";
				break;
			default:
				if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PENDING_STATUS())
					orderStatus = "Pending";
				else if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PUSHED_STATUS())
				{
					if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
						orderStatus = "Saved";
					else
						orderStatus = "Delivered";
				}
				break;
		}
		
		return orderStatus;
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			finish();
			setResult(2000);
		}
	}
	
	private void getTotalAmountWithDiscountAfterDelivery()
	{// login is changed
		if(trxHeaderDO!=null)
		{	float StatementDiscount1=0.0f;
			float customerDiscount = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
			
//			if(mallsDetailss!=null)
//			 StatementDiscount1 = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))));
//			else
				 StatementDiscount1 =  (((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)) ;
			trxHeaderDO.specialDiscount = (StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalAmount)) * StringUtils.getFloat(decimalFormat.format(trxHeaderDO.specialDiscPercent)))/100  ;
			
			if( (customerDiscount)>0)
				tvDiscValue.setText(amountFormate.format(customerDiscount));// need to bind discount
			else
				tvDiscValue.setText(amountFormate.format(0));
			
			if( StatementDiscount1>0) {
				tvstmntDiscValue.setText(amountFormate.format(StatementDiscount1));// need to bind discount
			}
			else {
				tvstmntDiscValue.setText(amountFormate.format(0));
			}
			
			if((trxHeaderDO.specialDiscount) >= 0)
			{
//				tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.totalDiscountAmount - customerDiscount-StatementDiscount1));
//				trxHeaderDO.specialDiscount =trxHeaderDO.totalDiscountAmount - customerDiscount-StatementDiscount1; //vinod
//				trxHeaderDO.specialDiscPercent =(trxHeaderDO.specialDiscount / trxHeaderDO.totalAmount)*100; //vinod
				tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.specialDiscount ));
				trxHeaderDO.specialDiscPercent =(trxHeaderDO.specialDiscount / trxHeaderDO.totalAmount)*100; //vinod

			}
			else
			{
				tvSplDiscValue.setText(amountFormate.format(0));
				trxHeaderDO.specialDiscount=0;//vinod
			}

			tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
			tvVATValue.setText(amountFormate.format(trxHeaderDO.totalVATAmount));
//			tvTotalValue.setText(amountFormate.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount)));
			tvTotalValue.setText(amountFormate.format((trxHeaderDO.totalAmount-StatementDiscount1-customerDiscount-trxHeaderDO.specialDiscount+trxHeaderDO.totalVATAmount)));
			tvNetValue.setText(amountFormate.format((trxHeaderDO.totalAmount-StatementDiscount1-customerDiscount-trxHeaderDO.specialDiscount)));
		}
	}
/* this is old logic
	private void getTotalAmountWithDiscountAfterDelivery()
	{
		if(trxHeaderDO!=null)
		{	float StatementDiscount1=0.0f;
			float customerDiscount = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));

//			if(mallsDetailss!=null)
//			 StatementDiscount1 = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))));
//			else
				 StatementDiscount1 = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))));
			trxHeaderDO.specialDiscount = StringUtils.getFloat(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalAmount)) * StringUtils.getFloat(decimalFormat.format(trxHeaderDO.specialDiscPercent)))/100));

			if(trxHeaderDO.totalDiscountAmount >= (customerDiscount))
				tvDiscValue.setText(amountFormate.format(customerDiscount));// need to bind discount
			else
				tvDiscValue.setText(amountFormate.format(0));

			if((trxHeaderDO.totalDiscountAmount) >= StatementDiscount1) {
				tvstmntDiscValue.setText(amountFormate.format(StatementDiscount1));// need to bind discount
			}
			else {
				tvstmntDiscValue.setText(amountFormate.format(0));
			}

			if((trxHeaderDO.totalDiscountAmount - customerDiscount-StatementDiscount1) >= 0.1)
			{
				tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.totalDiscountAmount - customerDiscount-StatementDiscount1));
				trxHeaderDO.specialDiscount =trxHeaderDO.totalDiscountAmount - customerDiscount-StatementDiscount1; //vinod
				trxHeaderDO.specialDiscPercent =(trxHeaderDO.specialDiscount / trxHeaderDO.totalAmount)*100; //vinod

			}
			else
			{
				tvSplDiscValue.setText(amountFormate.format(0));
				trxHeaderDO.specialDiscount=0;//vinod
			}

			tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
			tvTotalValue.setText(amountFormate.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount)));
		}
	}
*/

//=========================================newly added for food============== kwality =200 and pic = 100 ================== 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		vecagn=new Vector<AgencyNewDo>();
		AgencyNewDo agencyone=new AgencyNewDo();
		agencyone.AgencyId="1";
		agencyone.AgencyName="PIC";
		agencyone.Priority=100;
		vecagn.add(agencyone);
		AgencyNewDo agencytwo=new AgencyNewDo();
		agencytwo.AgencyId="2";
        if(trxHeaderDO.Division!=0)
        {
            if(trxHeaderDO.Division==1)
            {
                agencytwo.AgencyName="Kwality";
            }else
            {
                agencytwo.AgencyName="TPT";
            }
        }

		agencytwo.Priority=200;
		vecagn.add(agencytwo);
//        printOptions=new Vector<String>();
//		 printOptions.add(0, "Kwality");
//		 printOptions.add(1,"PIC");
	
		
	}
	
}
