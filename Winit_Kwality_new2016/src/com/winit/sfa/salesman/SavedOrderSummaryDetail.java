package com.winit.sfa.salesman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.OrderPreviewAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.SlabBasedDiscountDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.kwalitysfa.salesman.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class SavedOrderSummaryDetail extends BaseActivity implements SyncProcessListner
{
	private LinearLayout llSummaryofDay,llTotalAmount, llDeliveryStatus,llFOCTotalQty, llSplDiscValue, llOrderValue, llDiscValue;
	private Button btnOK, btnUpload,btnAddItem,btnSubmit;
	private ListView lvSalesOrder;
	private ArrayList<TrxDetailsDO> arrayList;
	private TrxHeaderDO trxHeaderDO;
	private OrderPreviewAdapter orderPreviewAdapter;
	private Bundle bundle;
	private TextView tvPageTitle,tvOrderStatusVal,tvCustomerName,tvCustomerLocation,tvstmntDiscValue,
		tvOrderNoTitle,tvVATValueTitle,tvNoOrderFound,tvOrderNoVal, tvOrderValue,tvNetValue,tvDiscValue,tvTotalValue,tvVATValue,tvSplDiscValue,tvOrderQtyColon,tvTotalQty, tvSplDiscountColon, tvSpclDiscount, tvOrderColon, tvDiscountColon,tvNETValueTitle, tvOrderValueTitle, llDiscountVal, tvFOCTotalQty;
	boolean isEditable = true;
	private HashMap<String, TrxDetailsDO> hmSavedItems;
	private int TRXTYPE_ORDER;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private String SYNCPRICECAL="SYNCPRICECAL";
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
	private HashMap<String, Vector<SlabBasedDiscountDO>> hashSlabBasedDis;
	private TextView tvCurrentTime;
	private boolean fromOrderSummary = false;
	private JourneyPlanDO malldetailDO, malldetailDONew;
	private boolean isQtyExceed = false;
	private float  totalstatementdisc = 0.00f;
	float customerstatementDiscount=0.0F;
	HashMap<String, String> hmShiftCodes=new HashMap<String, String>();
	
	
	@Override
	public void initialize() 
	{
		bundle = getIntent().getExtras();
		if(bundle!= null)
		{
			trxHeaderDO	 		=	(TrxHeaderDO) bundle.get("trxHeaderDO");
			
			if(getIntent().hasExtra("fromOrderSummary"))
				fromOrderSummary=	getIntent().getExtras().getBoolean("fromOrderSummary");
			
			if(getIntent().hasExtra("mallsDetails"))
			{
				malldetailDO 	=	(JourneyPlanDO) getIntent().getExtras().getSerializable("mallsDetails");
				malldetailDONew =	malldetailDO;
			}
			
			arrayList       	=   trxHeaderDO.arrTrxDetailsDOs;
			TRXTYPE_ORDER		=   trxHeaderDO.trxType;
			
			/*if((trxHeaderDO.Division == TrxHeaderDO.get_DIVISION_FOOD() || trxHeaderDO.Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY () )&& malldetailDO != null && malldetailDONew != null) {
				malldetailDO.PromotionalDiscount = "";
				malldetailDONew.PromotionalDiscount = "";
				malldetailDO.statementdiscount = "";
				malldetailDONew.statementdiscount = "";
				
			}*/
		}
		
		
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.order_summaryofday, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		
		intializeControls();
		
		tvOrderNoTitle.setText("Saved Order No");
			
		tvOrderStatusVal.setText(getOrderStatus(trxHeaderDO.trxStatus));
			
			
		if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			int diff = CalendarUtils.getDiffBtwDatesInDays((trxHeaderDO.deliveryDate).split("T")[0], CalendarUtils.getCurrentDateAsStringforStoreCheck());
			if(diff > 0)
				tvOrderStatusVal.setText("Delivered");
			else
				tvOrderStatusVal.setText("Pending");
		}
		else if (trxHeaderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER()) 
		{
			llDeliveryStatus.setVisibility(View.GONE);
		}
		
		btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
	    getTotalAmountWithDiscountAfterDelivery();
			
	    
	    btnAddItem.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				showAddNewSkuPopUp();
				trxHeaderDO = prepareSalesOrder();
				loadSavedItems();
				Intent intent = new Intent(SavedOrderSummaryDetail.this, SalesManRecommendedOrder.class);
				intent.putExtra("hmSavedItems",hmSavedItems);
				intent.putExtra("mallsDetails", malldetailDONew);
				intent.putExtra("trxSavedHeader", trxHeaderDO);
				
				intent.putExtra("TRX_TYPE", trxHeaderDO.trxType);
				intent.putExtra("TRX_SUB_TYPE", trxHeaderDO.trxSubType);
				intent.putExtra(AppConstants.DIVISION, trxHeaderDO.Division);
				startActivityForResult(intent, 1000);
			}
		});
	    
	    btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(((TextView)v).getText().toString().equalsIgnoreCase("Finish"))
				{
					finish();
				}
				else
				{
					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
					{
						trxHeaderDO = prepareSalesOrder();						
						loadSavedItems();						
						Intent intent = new Intent(SavedOrderSummaryDetail.this, SalesManRecommendedOrder.class);
						intent.putExtra("hmSavedItems",hmSavedItems);
						intent.putExtra("mallsDetails", malldetailDONew);
						intent.putExtra("trxSavedHeader", trxHeaderDO);
						intent.putExtra("TRX_TYPE", trxHeaderDO.trxType);
						intent.putExtra("TRX_SUB_TYPE", trxHeaderDO.trxSubType);
						intent.putExtra(AppConstants.DIVISION, trxHeaderDO.Division);
						startActivityForResult(intent, 1000);
					}
					else
					{
						btnSubmit.setEnabled(false);
						btnSubmit.setClickable(false);
						
						showLoader("Please wait...");
						new Thread(new Runnable()
						{
							boolean isSequenceNumberAvailable=true;
							@Override
							public void run() 
							{
								String availableOrderId = new OrderDA().getOrderId(trxHeaderDO.Division);
								if(TextUtils.isEmpty(availableOrderId))
									isSequenceNumberAvailable=false;
								
								if(isSequenceNumberAvailable)
									trxHeaderDO = prepareSalesOrder();
								
								isQtyExceed = checkItemAvialbility(trxHeaderDO);
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										if(isQtyExceed)
										{
											btnSubmit.setClickable(true);
											btnSubmit.setEnabled(true);//Do you want to continue?
											showCustomDialog(SavedOrderSummaryDetail.this, getString(R.string.warning), "Some of item(s) not in inventory for "+errorMessage+" Please modify order to continue ", getString(R.string.OK),null, "QtyExceed", false);
										}
										else
										{
											if(trxHeaderDO != null)
											{
												
												if(missingItem.length()>0 && trxHeaderDO.trxType!=TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
													showCustomDialog(SavedOrderSummaryDetail.this,getString(R.string.alert),"Following Items\n "+missingItem.toString()+" Are Not Available In Van Inventory\n. Do you want to continue?",getString(R.string.Yes),getString(R.string.No),"SavedOrderSummary");
												else
												{
													Intent intent = new Intent(SavedOrderSummaryDetail.this, SalesmanOrderPreview.class);
													if(StringUtils.getFloat(trxHeaderDO.statementDiscount)>0)
													trxHeaderDO.statementdiscountvalue=StringUtils.getFloat(decimalFormat.format(customerstatementDiscount));
//													trxHeaderDO.statementdiscountvalue=StringUtils.getFloat((decimalFormat.format(trxHeaderDO.totalDiscountAmount-StringUtils.getFloat(decimalFormat.format(trxHeaderDO.specialDiscount)))));
													if(trxHeaderDO.statementdiscountvalue==0.01f)
														trxHeaderDO.statementdiscountvalue=0.00f;
													if(trxHeaderDO.Division==1)
														trxHeaderDO.statementDiscount="";
													if(malldetailDONew.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
														intent.putExtra("SalesOrder", true);
													
													
													intent.putExtra("trxHeaderDO", trxHeaderDO);
													intent.putExtra("mallsDetails", mallsDetailss);
													intent.putExtra(AppConstants.DIVISION, trxHeaderDO.Division);
													startActivity(intent);													
												}
											}
											else if(!isSequenceNumberAvailable){
												btnSubmit.setClickable(true);
												btnSubmit.setEnabled(true);
												showCustomDialog(SavedOrderSummaryDetail.this, getString(R.string.warning), "Sequence numbers are not synced. Please sync now.", getString(R.string.sync_now), getString(R.string.not_now), "Syncnow");
											}
											else
												showCustomDialog(SavedOrderSummaryDetail.this, getString(R.string.warning), "Van qunatity is not available for selected items.", getString(R.string.OK), null, "");
											
											
											new Handler().postDelayed(new Runnable() {
												
												@Override
												public void run() 
												{
													btnSubmit.setEnabled(true);
													btnSubmit.setClickable(true);
												}
											}, 200);
										}
										hideLoader();
									}
								});
							}
						}).start();
					}
				}
			}
		});
		
	    loadData();
		setTypeFaceRobotoNormal(llSummaryofDay);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btnOK.setTypeface(Typeface.DEFAULT_BOLD);
		tvOrderNoTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvDiscountTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvOrderTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvOrderStatusTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvOrderStatusTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddItem.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSubmit.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		tvInvoicePriceTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		setTypeFaceRobotoBold(llTotalAmount);
		
		tvCurrentTime.setText(CalendarUtils.getFormattedSummaryDate(trxHeaderDO.trxDate+""));
		
		if(!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER) 
				&& (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()))
		{
			btnAddItem.setVisibility(View.GONE);
			if(!fromOrderSummary)
				btnSubmit.setVisibility(View.VISIBLE);
			else
				btnSubmit.setVisibility(View.GONE);
		}
		else if(malldetailDO != null && malldetailDO.site != null && trxHeaderDO != null && trxHeaderDO.clientCode != null
				&& malldetailDO.site.equalsIgnoreCase(trxHeaderDO.clientCode) && /*!fromOrderSummary  &&*/ isCheckin())
		{
			btnAddItem.setVisibility(View.VISIBLE);
			btnSubmit.setVisibility(View.VISIBLE);
		}
		else
		{
			btnAddItem.setVisibility(View.GONE);
			btnSubmit.setVisibility(View.GONE);
		}
		
		if(!TextUtils.isEmpty(trxHeaderDO.referenceCode) && !trxHeaderDO.referenceCode.equalsIgnoreCase(trxHeaderDO.trxCode))
		{
			btnAddItem.setVisibility(View.GONE);
			btnSubmit.setText("Finish");
		}
		
		if(malldetailDO != null)
			LogUtils.debug("fromOrderSummary", ""+malldetailDO.site);
		else
			LogUtils.debug("fromOrderSummary", "malldetailDO null");
		
		if(trxHeaderDO != null)
			LogUtils.debug("fromOrderSummary", ""+trxHeaderDO.clientCode);
		else
			LogUtils.debug("fromOrderSummary", "trxHeaderDO null");
		
		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			llFOCTotalQty.setVisibility(View.VISIBLE);
			tvOrderQtyColon.setVisibility(View.VISIBLE);
			tvTotalQty.setVisibility(View.VISIBLE);
			
			tvOrderValueTitle.setVisibility(View.GONE);
			tvNETValueTitle.setVisibility(View.GONE);
			llOrderValue.setVisibility(View.GONE);
			llDiscValue.setVisibility(View.GONE);
			tvDiscountColon.setVisibility(View.GONE);
			tvOrderColon.setVisibility(View.GONE);
			tvSpclDiscount.setVisibility(View.GONE);
			llSplDiscValue.setVisibility(View.GONE);
			tvSplDiscountColon.setVisibility(View.GONE);
			llDiscValue.setVisibility(View.GONE);
			llDiscountVal.setVisibility(View.GONE);
		}
		else
		{
			llFOCTotalQty.setVisibility(View.GONE);
			tvOrderQtyColon.setVisibility(View.GONE);
			tvTotalQty.setVisibility(View.GONE);
			
			tvOrderValueTitle.setVisibility(View.VISIBLE);
			tvNETValueTitle.setVisibility(View.VISIBLE);
			llOrderValue.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			tvDiscountColon.setVisibility(View.VISIBLE);
			tvOrderColon.setVisibility(View.VISIBLE);
			tvSpclDiscount.setVisibility(View.VISIBLE);
			llSplDiscValue.setVisibility(View.VISIBLE);
			tvSplDiscountColon.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			llDiscountVal.setVisibility(View.VISIBLE);
		}
	}
	
	private String errorMessage = "";
	private HashMap<String, Integer> hmOrderedItem;
//	private HashMap<String, Integer> hmOfferItem;
	private boolean checkItemAvialbility(TrxHeaderDO trxHeaderDO)
	{
		errorMessage = "";
		hmOrderedItem = new HashMap<String, Integer>();
//		hmOfferItem   = new HashMap<String, Integer>();
		boolean isQtyExceed = false;
		if(trxHeaderDO != null && trxHeaderDO.arrTrxDetailsDOs != null && trxHeaderDO.arrTrxDetailsDOs.size() >0)
		{
			for (TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs)
			{
//				if(hmOrderedItem.containsKey(trxDetailsDO.itemCode))
//				{
//					int qty = hmOrderedItem.get(trxDetailsDO.itemCode);
//					qty = qty + trxDetailsDO.quantityBU;
//					hmOrderedItem.put(trxDetailsDO.itemCode, qty);
//				}
//				else
//				{
//					hmOrderedItem.put(trxDetailsDO.itemCode, trxDetailsDO.quantityBU);
//				}
				int qty = 0;
				if(hmOrderedItem.containsKey(trxDetailsDO.itemCode))
				{
					qty = hmOrderedItem.get(trxDetailsDO.itemCode);
					qty = qty + trxDetailsDO.quantityBU;
					hmOrderedItem.put(trxDetailsDO.itemCode, qty);
				}
				else
				{
					qty = qty + trxDetailsDO.quantityBU;
					hmOrderedItem.put(trxDetailsDO.itemCode, qty);
				}
				int missed = isInventoryAvail(trxDetailsDO, qty);
				if(missed > 0)
				{
					isQtyExceed = true;
					errorMessage = errorMessage +" "+trxDetailsDO.itemCode+",";
				}
			}
//			if(hmOrderedItem != null && hmOrderedItem.size() >0)
//			{
//				Set<String> itemCodes = hmOrderedItem.keySet();
//				for (String string : itemCodes) 
//				{
//					int orderQty = 0;
//					int offerQty = hmOrderedItem.get(string);
//					
////					if(hmOfferItem.containsKey(string))
//					{
//						if(hmOrderedItem.containsKey(string))
//							orderQty = hmOrderedItem.get(string);
//						int totalQty = 0;
//						if(hmInventory.containsKey(string))
//							totalQty = hmInventory.get(string).totalQt;
//						if((orderQty + offerQty) > totalQty)
//						{
//							isQtyExceed = true;
//							errorMessage = errorMessage +" "+string+",";
//						}
//					}
////					else
////					{
////						isQtyExceed = true;
////						errorMessage = errorMessage +" "+string+",";
////					}
//				}
//				
//				if(errorMessage != null && errorMessage.length() > 1)
//					errorMessage = errorMessage.substring(0, errorMessage.length()-1);
//				
//			}
			
			
			/*********************************************/
			
//			if(hmOfferItem != null && hmOfferItem.size() >0)
//			{
//				Set<String> itemCodes = hmOfferItem.keySet();
//				for (String string : itemCodes) 
//				{
//					int orderQty = 0;
//					int offerQty = hmOfferItem.get(string);
//					
//					if(hmOfferItem.containsKey(string))
//					{
//						if(hmOrderedItem.containsKey(string))
//							orderQty = hmOrderedItem.get(string);
//						int totalQty = 0;
//						if(hmInventory.containsKey(string))
//							totalQty = hmInventory.get(string).totalQt;
//						if((orderQty + offerQty) > totalQty)
//						{
//							isQtyExceed = true;
//							errorMessage = errorMessage +" "+string+",";
//						}
//					}
//					else
//					{
//						isQtyExceed = true;
//						errorMessage = errorMessage +" "+string+",";
//					}
//				}
//				
//				if(errorMessage != null && errorMessage.length() > 1)
//					errorMessage = errorMessage.substring(0, errorMessage.length()-1);
//				
//			}
		}
		return isQtyExceed;
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		if(from.equalsIgnoreCase("SavedOrderSummary"))
		{
			Intent intent = new Intent(SavedOrderSummaryDetail.this, SalesmanOrderPreview.class);
			if(StringUtils.getFloat(trxHeaderDO.statementDiscount)>0)
				trxHeaderDO.statementdiscountvalue=StringUtils.getFloat(decimalFormat.format(customerstatementDiscount));
//				trxHeaderDO.statementdiscountvalue=trxHeaderDO.totalDiscountAmount-StringUtils.getFloat(decimalFormat.format(trxHeaderDO.specialDiscount));
			if(trxHeaderDO.Division==1)
				trxHeaderDO.statementDiscount="";
			
			if(malldetailDONew.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
			intent.putExtra("SalesOrder", true);
			
			intent.putExtra("trxHeaderDO", trxHeaderDO);
			intent.putExtra("mallsDetails", mallsDetailss);
			intent.putExtra(AppConstants.DIVISION, trxHeaderDO.Division);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("Syncnow")){
			if(isNetworkConnectionAvailable(this))
				syncData(SavedOrderSummaryDetail.this);
			else
				showCustomDialog(SavedOrderSummaryDetail.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
		}
		
	}
	
	int focItemCount = 0;
	float invoiceAmount = 0.0f;
	float totalvatAmt = 0.0f;

	private void loadData(){
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() {
			@Override
			public void run() {
				hmInventory  = new OrderDetailsDA().getAvailInventoryQtys();
				mallsDetailss =new CustomerDetailsDA().getCustometBySiteId(trxHeaderDO.clientCode);
				hmShiftCodes = new CustomerDA().getShiftToCodes();
				hashSlabBasedDis = new CaptureInventryDA().getSlabBasedDisCount(preference.getStringFromPreference(Preference.ORG_CODE, ""),mallsDetailss.channelCode);
				hashMapPricing = new CaptureInventryDA().getPricing(mallsDetailss.priceList,trxHeaderDO.trxType);
				updateItemPrice();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						hideLoader();
						tvCustomerLocation.setText(hmShiftCodes.get(trxHeaderDO.clientCode+"")+"");
						orderPreviewAdapter = new OrderPreviewAdapter(SavedOrderSummaryDetail.this, trxHeaderDO.arrTrxDetailsDOs,isEditable,mallsDetailss,TrxHeaderDO.get_TRXTYPE_SAVED_ORDER());
						lvSalesOrder.setAdapter(orderPreviewAdapter);
						
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
								if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
								{
//									tvFOCTotalQty.setText(""+orderPreviewAdapter.getFOCQuantity());
//									tvTotalValue.setText(""+decimalFormat.format(orderPreviewAdapter.getInvoiceAmount()/* - trxHeaderDO.specialDiscount - getDiscountAmount(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))*/));
									
									tvFOCTotalQty.setText(""+focItemCount);
									tvTotalValue.setText(""+(invoiceAmount+totalvatAmt));
									tvVATValue.setText(""+totalvatAmt);
								}
							}
						}, 200);
						
//						showAddNewSkuPopUp();
					}
				});
			}
		}).start();
		
	}
	private void updateItemPrice(){
		try {
			for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs){
				if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) {
					if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
						trxDetailsDO.EAPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());
					
					if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
						trxDetailsDO.CSPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel1());
				}
				
//				if(trxHeaderDO.trxType!=TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
//				{
//					updateSavedObject(trxDetailsDO);
//					calculatePrice();					
//				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void intializeControls()
	{
		tvPageTitle					=	(TextView)llSummaryofDay.findViewById(R.id.tvPageTitle);
		
		tvOrderNoTitle		  		=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderNoTitle);
		tvOrderNoVal		 		=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderNoVal);
//		tvOrderStatusTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderStatusTitle);
		btnOK				  		=	(Button)llSummaryofDay.findViewById(R.id.btnOK);
		btnUpload			 		=	(Button)llSummaryofDay.findViewById(R.id.btnUpload);
		btnAddItem				  	=	(Button)llSummaryofDay.findViewById(R.id.btnAddItem);
		btnSubmit			 		=	(Button)llSummaryofDay.findViewById(R.id.btnSubmit);
		
		llSplDiscValue				=	(LinearLayout)llSummaryofDay.findViewById(R.id.llSplDiscValue);
		llOrderValue				=	(LinearLayout)llSummaryofDay.findViewById(R.id.llOrderValue);
		llDiscValue					=	(LinearLayout)llSummaryofDay.findViewById(R.id.llDiscValue);
		
		tvTotalValue				=	(TextView)llSummaryofDay.findViewById(R.id.tvTotalValue);
		tvVATValue				=	(TextView)llSummaryofDay.findViewById(R.id.tvVATValue);
		tvSplDiscValue				=	(TextView)llSummaryofDay.findViewById(R.id.tvSplDiscValue);
		tvOrderQtyColon				=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderQtyColon);
		tvTotalQty					=	(TextView)llSummaryofDay.findViewById(R.id.tvTotalQty);
		tvSplDiscountColon			=	(TextView)llSummaryofDay.findViewById(R.id.tvSplDiscountColon);
		tvSpclDiscount				=	(TextView)llSummaryofDay.findViewById(R.id.llSpclDiscount);
		tvOrderColon				=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderColon);
		tvDiscountColon				=	(TextView)llSummaryofDay.findViewById(R.id.tvDiscountColon);
		tvOrderValueTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderValueTitle);
		tvNETValueTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvNETValueTitle);
		llDiscountVal				=	(TextView)llSummaryofDay.findViewById(R.id.llDiscountVal);
		tvFOCTotalQty				=	(TextView)llSummaryofDay.findViewById(R.id.tvFOCTotalQty);
		
//		tvOrderTitle				=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderTitle);
//		tvDiscountTitle			 	=	(TextView)llSummaryofDay.findViewById(R.id.tvDiscountTitle);
//		tvInvoicePriceTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvInvoicePriceTitle);
		
		tvCurrentTime               = (TextView)llSummaryofDay.findViewById(R.id.tvCurrentTime);
		
		btnOK.setVisibility(View.GONE);
		btnUpload.setVisibility(View.GONE);
		
		
//		tvDeliveryAmount			=   (TextView)llSummaryofDay.findViewById(R.id.tvDeliveryAmount);
		tvNoOrderFound				=   (TextView)llSummaryofDay.findViewById(R.id.tvNoOrderFound);
		tvCustomerName		  	 	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerName);
		tvCustomerLocation		  	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerLocation);
		tvOrderStatusVal			=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderStatusVal);
		tvSplDiscValue				=   (TextView)llSummaryofDay.findViewById(R.id.tvSplDiscValue);
		tvTotalQty					=   (TextView)llSummaryofDay.findViewById(R.id.tvTotalQty);
		tvOrderQtyColon				=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderQtyColon);
		
		lvSalesOrder				=   (ListView)llSummaryofDay.findViewById(R.id.lvOrderList);
		
		//Newly Added by sudheer Total amount shifted from bottom to top
		llTotalAmount				=	(LinearLayout)llSummaryofDay.findViewById(R.id.llTotalAmount);
		llDeliveryStatus			=	(LinearLayout) llSummaryofDay.findViewById(R.id.llDeliveryStatus);
		llFOCTotalQty				=	(LinearLayout) llSummaryofDay.findViewById(R.id.llFOCTotalQty);
		
		tvOrderValue	=   (TextView)llSummaryofDay.findViewById(R.id.tvOrderValue);
		tvNetValue	=   (TextView)llSummaryofDay.findViewById(R.id.tvNetValue);
		tvDiscValue	=   (TextView)llSummaryofDay.findViewById(R.id.tvDiscValue);
		tvTotalValue	=   (TextView)llSummaryofDay.findViewById(R.id.tvTotalValue);
		tvstmntDiscValue	=   (TextView)llSummaryofDay.findViewById(R.id.tvstmntDiscValue);
		
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
		
		if((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
				&& trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED())
			tvPageTitle.setText("Presales Order Detail");
		else
			tvPageTitle.setText("Saved Order Detail");
			
		tvOrderNoVal.setText(""+trxHeaderDO.trxCode);
		
		String strDateTime  = CalendarUtils.getDateSpecificFormat()+", "+CalendarUtils.getTimeSpecificFormat();
		tvCurrentTime.setText(strDateTime);
		
		llFOCTotalQty.setVisibility(View.GONE);
		tvOrderQtyColon.setVisibility(View.GONE);
		tvTotalQty.setVisibility(View.GONE);
		
	}
	
	
	private String getOrderStatus(int status)
	{
		String orderStatus = "";
		
		if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PENDING_STATUS())
			orderStatus = "Pending";
		else if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PUSHED_STATUS())
		{
			if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
				orderStatus = "Saved";
			else
				orderStatus = "Delivered";
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
	{
		if(trxHeaderDO!=null)
		{	
			 customerstatementDiscount=0.0F;
			orderTPrice 		= 	trxHeaderDO.totalAmount;
			totalDiscount 		=	trxHeaderDO.totalDiscountAmount;
			totalIPrice 		= 	(trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount);
			float customerDiscount =  (((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100)) ;
//			if(malldetailDO!=null)
//			 customerstatementDiscount =  (((StringUtils.getFloat(trxHeaderDO.statementdiscount) * trxHeaderDO.totalAmount)/100)) ;
//			else
				 customerstatementDiscount =  (((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)) ;
			
			trxHeaderDO.specialDiscount = (trxHeaderDO.totalAmount * trxHeaderDO.specialDiscPercent)/100;
			
				tvDiscValue.setText(amountFormate.format(customerDiscount));
				tvstmntDiscValue.setText(amountFormate.format(customerstatementDiscount));// need to bind discountcustomerstatementDiscount
			float x = (trxHeaderDO.totalDiscountAmount - customerDiscount-customerstatementDiscount)  ;
			tvSplDiscValue.setText(amountFormate.format(x>=0.01 ? x: 0.0f));
			tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
			tvTotalValue.setText(amountFormate.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount)));
			tvNetValue.setText(amountFormate.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount)));
			tvVATValue.setText(amountFormate.format((trxHeaderDO.totalVATAmount)));
		}
	}
	
	private void loadSavedItems()
	{
		hmSavedItems = new HashMap<String, TrxDetailsDO>();
		if(trxHeaderDO!=null)
		{
			for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
			{
				trxDetailsDO.promotionalDiscountAmount = calculateDiscount(trxDetailsDO);
				trxDetailsDO.statementDiscountAmnt = calculateStatementDiscount(trxDetailsDO);
				totalstatementdisc=totalstatementdisc+trxDetailsDO.statementDiscountAmnt;
				hmSavedItems.put(trxDetailsDO.itemCode,trxDetailsDO);
			}
					
		}
	}
	
	private float calculateDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
		if(mallsDetailss != null && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
			discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(mallsDetailss.PromotionalDiscount)))/100);

		return discount;
	}
	private float calculateStatementDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
		if(mallsDetailss != null && TRXTYPE_ORDER != TrxHeaderDO.get_TYPE_FREE_DELIVERY())
			discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(mallsDetailss.statementdiscount)))/100);

		return discount;
	}
	
	StringBuilder missingItem;
	
	private TrxHeaderDO prepareSalesOrder() 
	{
		missingItem=new StringBuilder();
		TrxHeaderDO trSubDO = null;
		
//		if(mallsDetails.PromotionalDiscount == null || mallsDetails.PromotionalDiscount.equalsIgnoreCase(""))
			mallsDetailss.PromotionalDiscount = new CaptureInventryDA().getPromotionDiscountForcustomer(mallsDetailss.site,trxHeaderDO.Division);
			mallsDetailss.statementdiscount = new CaptureInventryDA().getStatementDiscountForcustomer(mallsDetailss.site,trxHeaderDO.Division);
		if(trxHeaderDO == null)
			trSubDO = null;
		else
		{
			ArrayList<TrxDetailsDO> arrTRXDetail = new ArrayList<TrxDetailsDO>();
			for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
			{
					if(trxDetailsDO.requestedSalesBU > 0 && trxDetailsDO.productStatus != 2)
						arrTRXDetail.add(trxDetailsDO);
					if(trxDetailsDO.quantityBU<=0){
						missingItem.append(trxDetailsDO.itemCode).append(",");
					}
			}
			
			if(arrTRXDetail.size() > 0)
			{
				trSubDO = new TrxHeaderDO();
				trSubDO.trxCode			=	trxHeaderDO.trxCode;
				trSubDO.appTrxId 		=  trxHeaderDO.appTrxId;
				trSubDO.clientBranchCode=  mallsDetailss.site;
				trSubDO.clientCode		=  mallsDetailss.site;
				trSubDO.currencyCode    =  AppConstants.CURRECNY_CODE;
				trSubDO.deliveryDate	=  CalendarUtils.getCurrentDate();
				trSubDO.journeyCode		=  mallsDetailss.JourneyCode;
				trSubDO.orgCode			=  preference.getStringFromPreference(Preference.ORG_CODE, "");
				trSubDO.paymentType		=  mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) ? 1 : 0;//need to check
				trSubDO.printingTimes	=  0;
				trSubDO.status			=  trxHeaderDO.status;
				trSubDO.totalAmount		=  orderTPrice;
				trSubDO.totalDiscountAmount = totalDiscount;
				trSubDO.trxStatus		=	 trxHeaderDO.trxStatus;
				trSubDO.branchPlantCode	=	trxHeaderDO.branchPlantCode;
				trSubDO.trxType			= trxHeaderDO.trxType;
				trSubDO.trxSubType		= trxHeaderDO.trxSubType;
				
				if((trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_ORDER()))
					trSubDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");
				else
					trSubDO.userCode		= trxHeaderDO.userCode;
				
				trSubDO.visitCode		= mallsDetailss.VisitCode;
				trSubDO.specialDiscount	= trxHeaderDO.specialDiscount;
				trSubDO.specialDiscPercent	= trxHeaderDO.specialDiscPercent;
				trSubDO.statementdiscountvalue 	= StringUtils.getFloat(decimalFormat.format(customerstatementDiscount));
				trSubDO.totalVATAmount=trxHeaderDO.totalVATAmount;
				trSubDO.LPONo				 = 	trxHeaderDO.LPONo;
				trSubDO.Narration			 = 	trxHeaderDO.Narration;
				trSubDO.returnReason		 = 	trxHeaderDO.returnReason;
				trSubDO.rateDiff			 = 	trxHeaderDO.rateDiff;
				trSubDO.Division			 = 	trxHeaderDO.Division;
				
				trSubDO.promotionalDiscount = mallsDetailss.PromotionalDiscount;
				trSubDO.statementDiscount = mallsDetailss.statementdiscount;
				
				
				trSubDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
		}
		return trSubDO;
	}
	
	float orderTPrice, totalIPrice, totalDiscount,orderVATPrice;
	
	public int isInventoryAvail(TrxDetailsDO objItem,int quantity)
	{
		int missedQTY = 0;
		if(TRXTYPE_ORDER ==TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER()
				|| TRXTYPE_ORDER ==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() 
				|| TRXTYPE_ORDER ==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()
				|| TRXTYPE_ORDER ==TrxHeaderDO.get_TYPE_FREE_ORDER())
			return missedQTY;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.itemCode))
		{
			HHInventryQTDO inventryDO = hmInventory.get(objItem.itemCode);
			objItem.expiryDate = inventryDO.expiryDate;
			objItem.batchCode 	  = inventryDO.batchCode;
			
			float availQty = inventryDO.totalQt/((objItem.hashArrUoms.get(objItem.itemCode+"UNIT")).eaConversion);
			if(quantity > availQty)
			{
				missedQTY = (int) ((quantity) - (int) availQty);
				hmInventory.get(objItem.itemCode).tempTotalQt = 0;
			}
			else
				hmInventory.get(objItem.itemCode).tempTotalQt = quantity;
		}
		else
			missedQTY = quantity;
		
		return missedQTY;
	}
	
	public void  calculatePrice()
	{
		LogUtils.debug("calculatePrice", "called");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				calculatePriceInSync();
			}
		}).start();
	}
	
	private void calculatePriceInSync(){
		try {
			synchronized (SYNCPRICECAL) {
				orderTPrice = 0.0f;
				totalIPrice = 0.0f;
				totalDiscount = 0.0f;
				orderVATPrice = 0.0f;

				for (TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs) {
					LogUtils.debug("priceloop", "running");
//					trxDetailsDO.lineNo = (int) count++;
					trxDetailsDO.itemType = "" + AppConstants.ITEM_TYPE_ORDER;
					float price = trxDetailsDO.basePrice;
					float price3 = 0;
					float quanity = 0.0f;

					if(trxDetailsDO.requestedSalesBU>0)
						updateSavedObject(trxDetailsDO);
					
					if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) {
						price = hashMapPricing.get(trxDetailsDO.itemCode).get(
								trxDetailsDO.UOM);// getting price of selected UOM
						if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
							price3 = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());
					}

					trxDetailsDO.priceUsedLevel1 = price;
					trxDetailsDO.priceUsedLevel3 = price3;
					quanity = trxDetailsDO.quantityLevel1;

					if(hashSlabBasedDis!=null && hashSlabBasedDis.containsKey(trxDetailsDO.itemCode)){
						UOMConversionFactorDO uomFactorDO = trxDetailsDO.hashArrUoms.get(trxDetailsDO.itemCode+"UNIT");
						float disc = getApplicableDisc(trxDetailsDO.quantityBU, hashSlabBasedDis.get(trxDetailsDO.itemCode),uomFactorDO.eaConversion);
						LogUtils.debug("ApplicableDiscount", ""+disc);
						trxDetailsDO.calculatedDiscountPercentage =disc;
					}
					
//					float discountAmount = (price*trxDetailsDO.calculatedDiscountPercentage)/100.0f;
					float discountAmount = ((price*trxDetailsDO.calculatedDiscountPercentage))/100.0f;
					trxDetailsDO.calculatedDiscountAmount=StringUtils.getFloat(decimalFormat.format(discountAmount));
					float vatOnEach=(((price )-discountAmount*trxDetailsDO.quantityLevel1)*trxDetailsDO.vatPercentage)/100;
					trxDetailsDO.VATAmountNew=(vatOnEach* quanity);
					orderVATPrice+=(vatOnEach* quanity);
//					trxDetailsDO.totalDiscountAmount=StringUtils.getFloat(decimalFormat.format(trxDetailsDO.calculatedDiscountAmount*quanity));
					
//					float discount = price*trxDetailsDO.calculatedDiscountPercentage/100.0f;
//					trxDetailsDO.priceUsedLevel1 = price - discount;
//					trxDetailsDO.totalDiscountAmount = discount*trxDetailsDO.quantityLevel1;
					
					/*trxDetailsDO.totalDiscountAmount=StringUtils.getFloat(decimalFormat.format(trxDetailsDO.promotionalDiscountAmount));
					
					float discount = StringUtils.getFloat(decimalFormat.format(trxDetailsDO.promotionalDiscountAmount));
					trxDetailsDO.priceUsedLevel1 = price - discount;*/
					
					orderTPrice 		+= 	(price * trxDetailsDO.quantityLevel1);
					totalDiscount 		+=	(discountAmount*trxDetailsDO.quantityLevel1);
					totalIPrice 		+= 	(price * trxDetailsDO.quantityLevel1);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						trxHeaderDO.totalDiscountAmount = totalDiscount;
						trxHeaderDO.totalAmount = orderTPrice;
						
						if(trxHeaderDO.totalDiscountAmount >= trxHeaderDO.specialDiscount)
						{
							tvDiscValue.setText(amountFormate.format(trxHeaderDO.totalDiscountAmount - trxHeaderDO.specialDiscount));// need to bind discount
							
							if(trxHeaderDO.specialDiscount >= 0.1)
								tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.specialDiscount));
							else
								tvSplDiscValue.setText(amountFormate.format(0));
						}
						
						tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
						tvTotalValue.setText(amountFormate.format(totalIPrice-totalDiscount+orderVATPrice));
						tvNetValue.setText(amountFormate.format(totalIPrice-totalDiscount));
						tvVATValue.setText(amountFormate.format(orderVATPrice));
					}
				});
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void updateSavedObject(TrxDetailsDO obj) {
		try {
			TrxDetailsDO objItem = obj;
			if (objItem != null) {
				UOMConversionFactorDO uomFactorDO = objItem.hashArrUoms
						.get(objItem.itemCode + objItem.UOM + "");
				int quanity = 0;
				if (uomFactorDO != null) {
					quanity = (int) (/*uomFactorDO.eaConversion * */objItem.requestedSalesBU);
					objItem.missedBU = isInventoryAvail(objItem, quanity);
					if (uomFactorDO != null && uomFactorDO.eaConversion != 0)
						objItem.quantityLevel1 = (quanity - objItem.missedBU)/*
								/ uomFactorDO.eaConversion*/;
					else
						objItem.quantityLevel1 = (quanity - objItem.missedBU);
					objItem.quantityLevel2 = 0;
					objItem.quantityLevel3 = 0;
				}
				objItem.quantityBU = quanity - objItem.missedBU;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public float getApplicableDisc(final int qunityBu,Vector<SlabBasedDiscountDO> vec,final float eaConversion){
		float disc=0.0f;
		Predicate<SlabBasedDiscountDO> searchItem = new Predicate<SlabBasedDiscountDO>() {
			public boolean apply(SlabBasedDiscountDO slabBasedDiscountDO) {
				return slabBasedDiscountDO.miniMumQty*eaConversion<=qunityBu&& qunityBu<slabBasedDiscountDO.maxQty*eaConversion;
			}
		};
		Collection<SlabBasedDiscountDO> filteredResult = filter(vec, searchItem);
		if(filteredResult!=null && filteredResult.size()>0){
			Vector<SlabBasedDiscountDO> tmp = new Vector<SlabBasedDiscountDO>((ArrayList<SlabBasedDiscountDO>) filteredResult);
			SlabBasedDiscountDO obj=tmp.get(0);
			disc = obj.discPercent;
		}else{
			SlabBasedDiscountDO obj=vec.get(vec.size()-1);
			if(obj.miniMumQty*eaConversion<=qunityBu)
				disc = obj.discPercent;
		}
		return disc;
	}
	
	@Override
	public void start() {
		showLoader("Syncing Data...0%");
	}
	@Override
	public void error() {
	}
	@Override
	public void end() {
		hideLoader();
	}

	@Override
	public void progress(String msg) {
		showLoader(msg);
	}
}
