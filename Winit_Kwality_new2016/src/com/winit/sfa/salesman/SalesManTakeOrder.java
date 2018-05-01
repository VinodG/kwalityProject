package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.salesman.adapter.AddRecomendedItemAdapter;
import com.winit.alseer.salesman.common.Add_new_SKU_Dialog;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ARCollectionDA;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.PromotionalItemsDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.DiscountDO;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.Item;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.PromotionOrderDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesManTakeOrder extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave, llOrderVal, llPricing, llCreditLimit;
	private TextView tvLu, tvCI, tvHeaderText, tvOrderValue, tvOrder, evTotalValue, 
					 etDiscValue , tvNoOrder, tvDisHeader, tvCreditLimitVal, tvSynchPromotion;
	private Button  btnAddIetem, btnAddItems, btnCancel1, btnConfirmOrder, btnPromotions;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd;
	private Thread threadForCheckDiscount ;
	private CaptureInventaryAdapter adapterForCapture;
	private AddRecomendedItemAdapter adapter;
	private float orderTPrice = 0.0f, totalDiscount = 0.0f, totalIPrice = 0.0f;
	public static Vector<ProductDO> vecRecommended ;
	private String orderedItems ="",orderedItemsList = "", orderID = "";
	private LinearLayout llLayoutMiddle;
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, Float> hmConversion; 
	private OrderDO objOrder;
	private HashMap<String, ArrayList<ProductDO>> offerVectorNew;
	private CustomerCreditLimitDo creditLimit;
	private HashMap<String, HashMap<String, Vector<ProductDO>>> hmMainCat = new HashMap<String, HashMap<String,Vector<ProductDO>>>();
	private HashMap<String, Vector<PromotionOrderDO>> vecPromotions;
	private ImageView ivPromoSep;
	private String [] arrString = {"1", "2", "3", "4", "5", "6", "7", "8" ,"9"};
	
	@Override
	public void initialize()
	{
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.recommendedorder, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			objOrder  	 = (OrderDO) getIntent().getExtras().get("orderid"); 
		}
		
		intialiseControls();
		
		if(objOrder == null)
			tvCI.setText("Recommended Order");
		else
			tvCI.setText("Order Fulfillment");
	
		if(AppConstants.hmCapturedInventory == null)
			AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
		else
			AppConstants.hmCapturedInventory.clear();
		
		llBottomButtons.setVisibility(View.GONE);
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.VISIBLE);
		etDiscValue.setEnabled(true);
		etDiscValue.setFocusable(true);
		
		if(objOrder != null)
			btnAddItems.setVisibility(View.GONE);
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			llPricing.setVisibility(View.GONE);
		else
			llPricing.setVisibility(View.VISIBLE);
		
		btnCancel1.setText("Cancel");
		btnCancel1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnPromotions.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(vecPromotions != null && vecPromotions.size() > 0)
					showSchemeProductDetail(vecPromotions, null);
			}
		});
		
		tvSynchPromotion.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				showLoader("Syncing...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						String empId = preference.getStringFromPreference(Preference.EMP_NO, "");
						loadPromotions(empId);
						vecPromotions = new PromotionalItemsDA().getAllPromotions();
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run()
							{
								hideLoader();
								showCustomDialog(SalesManTakeOrder.this, "Alert !", "Promotions has been synced successfully.", "OK", null, "");
							}
						});
					}
				}).start();
			}
		});
		
		btnConfirmOrder.setText("Confirm Order ");
		btnConfirmOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				btnConfirmOrder.setEnabled(false);
				btnConfirmOrder.setClickable(false);
				final boolean isAvail  = getAddedItemAvailability(AppConstants.hmCapturedInventory);
				if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() <= 0 )
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
				}
				else if(!isAvail)
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
				}
				else if(!checkCreditLimit(totalIPrice))
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCreditExceededPopup();
				}
				else
				{
					showLoader("Please wait...");
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							if(mallsDetails.isSchemeAplicable == 1)
							{
//								filterItems(AppConstants.hmCapturedInventory);
//								PromotionalItemsDA promotionalItemsDA = new PromotionalItemsDA();
//								promotionalItemsDA.getPromotionInTemp(preference.getStringFromPreference(Preference.SALESMANCODE, ""), mallsDetails.site);
//								promotionalItemsDA.insertOrderItemsInTemp(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
//								promotionalItemsDA.insertOrderItemsInTemp_New(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
//								offerVectorNew =  promotionalItemsDA.getPromotionItems();
							}
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									Intent intent = new Intent(SalesManTakeOrder.this, SalesmanOrderPreview.class);
									intent.putExtra("isReturnRequest", true);
									intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
									intent.putExtra("Discount", etDiscValue.getText().toString().trim());
									intent.putExtra("objOrder",objOrder);
									intent.putExtra("mallsDetails",mallsDetails);
									startActivityForResult(intent, 1000);
									
									new Handler().postDelayed(new Runnable() {
										
										@Override
										public void run() 
										{
											btnConfirmOrder.setEnabled(true);
											btnConfirmOrder.setClickable(true);
										}
									}, 200);
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		
		IntentFilter filterForJourney = new IntentFilter();
		filterForJourney.addAction(AppConstants.ACTION_GOTO_TELEORDERS);
		registerReceiver(GotoTeleOrders, filterForJourney);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hmInventory  = new OrderDetailsDA().getAvailInventoryQtys();
				hmConversion = new OrderDetailsDA().getUOMFactor();
				vecPromotions = new PromotionalItemsDA().getAllPromotions();
				/****************************************************/
//				HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
//				hhInventryQTDO.totalQt 	  = 100;
//				hhInventryQTDO.totalUnits = Math.round(100/12);
//				hmInventory.put("FE001", hhInventryQTDO);
				/****************************************************/
				AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
				
				CaptureInventryDA captureInventryDA = new CaptureInventryDA();
				if(objOrder != null && objOrder.OrderId != null)
					AppConstants.hmCapturedInventory  =  captureInventryDA.getLastOrderByOrderAndPreseller(objOrder.OrderId, hmInventory, CaptureInventryDA.DELIVERY, mallsDetails.priceList);
				else
				{
					HashMap<String, Vector<ProductDO>> hmSelectedItems;
					hmSelectedItems = captureInventryDA.getBrandItems_Default(AppConstants.MUST_HAVE, mallsDetails.priceList,"");	
					
					if(hmSelectedItems != null && hmSelectedItems.size() > 0)
					{
						hmMainCat.put(AppConstants.MUST_HAVE, hmSelectedItems);
						addItems(AppConstants.MUST_HAVE, AppConstants.hmCapturedInventory, hmSelectedItems);
					}
					
					hmSelectedItems = captureInventryDA.getBrandItems_Default(AppConstants.NEW_LAUNCHES, mallsDetails.priceList,"");
					
					if(hmSelectedItems != null && hmSelectedItems.size() > 0)
					{
						hmMainCat.put(AppConstants.NEW_LAUNCHES, hmSelectedItems);
						addItems(AppConstants.NEW_LAUNCHES, AppConstants.hmCapturedInventory, hmSelectedItems);
					}
					
					hmSelectedItems = captureInventryDA.getBrandItems_Default(AppConstants.FAVOURRITE, mallsDetails.priceList,"");
					
					if(hmSelectedItems != null && hmSelectedItems.size() > 0)
					{
						hmMainCat.put(AppConstants.FAVOURRITE, hmSelectedItems);
						addItems(AppConstants.FAVOURRITE, AppConstants.hmCapturedInventory, hmSelectedItems);
					}
				}
				
				checkAvailableInventory();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						onViewClickListners();
						
						if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
						{
							tvNoOrder.setVisibility(View.GONE);
							calTotPrices(true, true);
						}
						else
							tvNoOrder.setVisibility(View.VISIBLE);
						
						etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
						
						if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
						{
							evTotalValue.setText(""+curencyCode+" "+"0.00");
							tvOrderValue.setText(""+curencyCode+" "+"0.00");
							etDiscValue.setText(""+curencyCode+" "+"0.00");
						}
						else
						{
							etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
						}
						
						hideLoader();
					}
				});
			}
		}).start();
		
		setTypeFaceRobotoNormal(llCapture_Inventory);
	}
	BroadcastReceiver GotoTeleOrders = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			finish();
		}
	};
	
	public void intialiseControls()
	{
		llLayoutMiddle		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llLayoutMiddle);
		llTotalValue		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llTotalValue);
		llOrderVal			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llOrderVal);
		llCreditLimit		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llCreditLimit);
		
		btnConfirmOrder 	= (Button)llCapture_Inventory.findViewById(R.id.btnFinalize);
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddItems			= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
		evTotalValue		= (TextView)llCapture_Inventory.findViewById(R.id.evTotalValue);
		btnCancel1 			= (Button)llCapture_Inventory.findViewById(R.id.btnSave);
		tvLu				= (TextView)llCapture_Inventory.findViewById(R.id.tvLu);
		tvCI				= (TextView)llCapture_Inventory.findViewById(R.id.tvCI);
		tvNoOrder			= (TextView)llCapture_Inventory.findViewById(R.id.tvNoOrder);
//		btnPromotions 		= (Button)llCapture_Inventory.findViewById(R.id.btnPromotions);
		etDiscValue			= (TextView)llCapture_Inventory.findViewById(R.id.etDiscValue);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		tvHeaderText		= (TextView)llCapture_Inventory.findViewById(R.id.tvHeaderText);
//		ivPromoSep			= (ImageView)llCapture_Inventory.findViewById(R.id.ivPromoSep);
		tvCreditLimitVal	= (TextView)llCapture_Inventory.findViewById(R.id.tvCreditLimitVal);
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		tvOrder				= (TextView)llCapture_Inventory.findViewById(R.id.tvOrder);
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		
		tvSynchPromotion	= (TextView)llCapture_Inventory.findViewById(R.id.tvSynchPromotion);
		tvSynchPromotion.setVisibility(View.VISIBLE);
		
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		/*tvNoOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnConfirmOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddIetem.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddItems.setTypeface(AppConstants.Roboto_Condensed_Bold);*/
		btnAddIetem.setTextColor(Color.WHITE);
		/*tvLu.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCI.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDisHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCancel1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		evTotalValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrderValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etDiscValue.setTypeface(AppConstants.Roboto_Condensed_Bold);*/
		
		llOrderVal.setVisibility(View.VISIBLE);
		llTotalValue.setVisibility(View.VISIBLE);
		btnAddItems.setVisibility(View.GONE);
		
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName +" ["+mallsDetails.site+"]" /*+ " ("+mallsDetails.partyName+")"*/);
		
		btnAddIetem.setVisibility(View.GONE);
		
		ivPromoSep.setVisibility(View.VISIBLE);
		btnPromotions.setVisibility(View.VISIBLE);
	}
	String strErrorMessage = "";
	private void calTotPrices(boolean isLoaderShown, final boolean isRefresh)
	{
		strErrorMessage = "";
		if(isLoaderShown)
		showLoader(getString(R.string.loading));
		orderTPrice 		= 	0.0f;
		totalIPrice 		= 	0.0f;
		totalDiscount 	= 	0.0f;
		
		if(isLoaderShown)
			showLoader("Please wait...");
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				Set<String> set = AppConstants.hmCapturedInventory.keySet();
				int count = 1;
				for(String key : set)
				{
					Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
					for(ProductDO objProductDO : vecOrderedProduct)
					{
//						checkInventoryAvail(objProductDO);
						objProductDO.LineNo 			= ""+count++;
						DiscountDO temp = new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
						DiscountDO objDiscount = new CaptureInventryDA().getDisocunt(mallsDetails.site, objProductDO.CategoryId, objProductDO.SKU);
						
						if(objDiscount == null)
							objDiscount = new DiscountDO();
						
						if(temp == null)
							temp = new DiscountDO();
						
						if(objProductDO.isPromotional)
							temp.perCaseValue = "0";
						
						objDiscount.perCaseValue = temp.perCaseValue;
						objDiscount.TaxGroupCode = temp.TaxGroupCode;
						objDiscount.TaxPercentage = temp.TaxPercentage;
						
						if(objDiscount.discountType == AppConstants.DISCOUNT_PERCENTAGE)
							objProductDO.discountAmount = objProductDO.totalCases * StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100);
						else
							objProductDO.discountAmount = objProductDO.totalCases * objDiscount.discount;
						
						objProductDO.TaxGroupCode		=	objDiscount.TaxGroupCode;
						objProductDO.TaxPercentage		=	objDiscount.TaxPercentage;
						objProductDO.inventoryQty		=	0;	
						objProductDO.itemPrice  		= 	StringUtils.getFloat(objDiscount.perCaseValue);//objDiscount.fPricePerCase + objDiscount.fDiscountAmt;
						objProductDO.totalPrice 		= 	objProductDO.totalCases * StringUtils.getFloat(objDiscount.perCaseValue); 
						objProductDO.unitSellingPrice 	= 	StringUtils.getFloat(objDiscount.perCaseValue) - StringUtils.getFloat(objDiscount.perCaseValue)* (objDiscount.discount/100);
						objProductDO.invoiceAmount 		= 	StringUtils.getFloat(decimalFormat.format(objProductDO.unitSellingPrice*objProductDO.totalCases));
					
						orderTPrice 		+= 	objProductDO.totalPrice;
						totalDiscount 		+=	objProductDO.discountAmount;
						totalIPrice 		+= 	objProductDO.invoiceAmount;
						if(objDiscount.discountType == AppConstants.DISCOUNT_PERCENTAGE)
						{
							objProductDO.DiscountAmt =  StringUtils.getFloat(objDiscount.perCaseValue)*objDiscount.discount/100;
						}
						else
						{
							objProductDO.DiscountAmt =  objDiscount.discount;
						}
						objProductDO.Discount = objDiscount.discount;
						objProductDO.discountType = objDiscount.discountType;
					}
				}
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(isRefresh)
						adapterForCapture = new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
						etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
						evTotalValue.setText(""+curencyCode+" "+amountFormate.format(totalIPrice));
						tvOrderValue.setText(""+curencyCode+" "+amountFormate.format(orderTPrice));				
					}
				});

			}
		}).start();

	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
			tvNoOrder.setVisibility(View.GONE);
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(mallsDetails!= null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
					creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetails.site);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(creditLimit != null)
						{
							llCreditLimit.setVisibility(View.VISIBLE);
							tvCreditLimitVal.setText(mallsDetails.currencyCode +" "+amountFormate.format(StringUtils.getFloat(creditLimit.availbleLimit)));
						}
					}
				});
			}
		}).start();
	}
	
	public void ShowDeleteButton(Vector<Item> vecItems)
	{
		for(int i = 0; vecItems!= null && i <vecItems.size() ; i++)
		{
			Button btnDelete = (Button)findViewById(i);
			if(btnDelete != null)
			{
				btnDelete.setVisibility(View.GONE);
				btnDelete.setTag("false");
			}
		}
	}
	
	public void onViewClickListners()
	{
		btnAddItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				adapter = null;
				showAddNewSkuPopUp();
			}
		});
	}
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManTakeOrder.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		showFullScreenDialog(objAddNewSKUDialog);
		
		final LinearLayout llResult 		=	objAddNewSKUDialog.llResult;
		final LinearLayout llBottomButtons 	=	objAddNewSKUDialog.llBottomButtons;
		TextView tvItemCodeLabel			=	objAddNewSKUDialog.tvItemCodeLabel;
		TextView tvItem_DescriptionLabel	=	objAddNewSKUDialog.tvItem_DescriptionLabel;
		TextView tvAdd_New_SKU_Item			=	objAddNewSKUDialog.tvAdd_New_SKU_Item;
		final TextView tvCategory	 		=	objAddNewSKUDialog.tvCategory;
		final EditText etSearch	 			=	objAddNewSKUDialog.etSearch;
		final ImageView cbList 				=	objAddNewSKUDialog.cbList;
		final ListView lvPopupList		 	=	objAddNewSKUDialog.lvPopupList;
		final LinearLayout llList			=	objAddNewSKUDialog.llList;
		Button btnAdd 						=	objAddNewSKUDialog.btnAdd;
		Button btnCancel 					=	objAddNewSKUDialog.btnCancel;
		final TextView tvNoItemFound		=	objAddNewSKUDialog.tvNoItemFound;
		
		final ImageView ivSearchCross	=	objAddNewSKUDialog.ivSearchCross;
		
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		tvNoItemFound.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etSearch.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCategory.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		vecCategory = new Vector<String>();
//		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
//			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
		vecCategory = new CategoriesDA().getAvailableCategory();
		
		
		final Button btnSearch = new Button(SalesManTakeOrder.this);
		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeOrder.this, "Select Category", true);
				builder.setSingleChoiceItems(vecCategory, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						String str = (String) selectedObject;
			    		builder.dismiss();
		    			builder.dismiss();
		    			((TextView)v).setText(str);
						((TextView)v).setTag(str);
						
						btnSearch.performClick();
				    }
				}); 
				builder.show();
			}
		});
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				if(adapter!= null)
					adapter.refresh(vecSearchedItemd);
			}
		});
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(s.toString()!=null)
				{
					Vector<ProductDO> vecTemp = new Vector<ProductDO>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						ProductDO obj = (ProductDO) vecSearchedItemd.get(index);
						String strText = ((ProductDO)obj).SKU;
						String strDes = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecSearchedItemd.get(index));
					}
					if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
					{
						adapter.refresh(vecTemp);
						tvNoItemFound.setVisibility(View.GONE);
						lvPopupList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoItemFound.setVisibility(View.VISIBLE);
						lvPopupList.setVisibility(View.GONE);
					}
				}
				else if(adapter!= null)
					adapter.refresh(vecSearchedItemd);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		cbList.setTag("False");
		cbList.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(adapter != null)
					adapter.selectAll(cbList);
			}
		});
		
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				//while tapping on the List Cell to hide the keyboard first
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(tvCategory.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(tvCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
				}
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<ProductDO>();
					else
						vecSearchedItemd.clear();
					orderedItems ="";
					orderedItemsList = "";
					if(AppConstants.hmCapturedInventory!=null)
					{
						Set<String> set = AppConstants.hmCapturedInventory.keySet();
						Iterator<String> iterator = set.iterator();
						Vector<String> vecCategoryIds = new Vector<String>();
						while(iterator.hasNext())
							vecCategoryIds.add(iterator.next());
						
						for(int i=0; i<vecCategoryIds.size(); i++)
						{
							Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(vecCategoryIds.get(i));
							for(ProductDO objProductDO : vecOrderedProduct)
							{
								orderedItems = orderedItems + "'"+objProductDO.SKU+"',";
							}
						}
						if(orderedItems.contains(","))
							orderedItemsList = orderedItems.substring(0, orderedItems.lastIndexOf(","));
						else
							orderedItemsList = orderedItems;
					}
					final ProductsDA objItemDetailBL = new ProductsDA(); 
					showLoader(getString(R.string.loading));
					
					new Thread(new  Runnable() 
					{
						public void run()
						{
							String catgId = "", catgName = tvCategory.getText().toString();
							
							for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
							{
								if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
								{
									catgId = AppConstants.vecCategories.get(i).categoryId;
									break;
								}
							}
							vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(catgId, "", "",orderedItemsList, mallsDetails.priceList, AppConstants.SALES_ORDER_TYPE, true);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									llResult.setVisibility(View.VISIBLE);
									if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
									{
										cbList.setVisibility(View.VISIBLE);
										tvNoItemFound.setVisibility(View.GONE);
										llBottomButtons.setVisibility(View.VISIBLE);
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManTakeOrder.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManTakeOrder.this));
										cbList.setVisibility(View.INVISIBLE);
										tvNoItemFound.setVisibility(View.VISIBLE);
										llBottomButtons.setVisibility(View.GONE);
									}
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Vector<ProductDO> veciItems= new Vector<ProductDO>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
				{
						tvNoOrder.setVisibility(View.GONE);
						if(AppConstants.hmCapturedInventory == null)
							AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
						
						for(int i=0; veciItems != null && i < veciItems.size(); i++)
						{
							ProductDO objProduct = veciItems.get(i);
							Vector<ProductDO> vecProducts;
							if(objProduct.CategoryId.equalsIgnoreCase("GENERAL"))
								vecProducts = AppConstants.hmCapturedInventory.get("CA001");
							else
								vecProducts = AppConstants.hmCapturedInventory.get(objProduct.CategoryId);
							if(vecProducts == null)
							{
								vecProducts = new Vector<ProductDO>(); 
								vecProducts.add(objProduct);
								AppConstants.hmCapturedInventory.put(objProduct.CategoryId, vecProducts);
							}
							else
								vecProducts.add(objProduct);
						}
						preference.saveBooleanInPreference("isItemAdded", true);
						preference.commitPreference();
				
						objAddNewSKUDialog.dismiss();
						
						calTotPrices(true, true);
				}
				else
					showCustomDialog(SalesManTakeOrder.this, "Warning !", "Please select Items.", "OK", null, "");
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				objAddNewSKUDialog.dismiss();
			}
		});
	}
	
	
	public class CaptureInventaryAdapter
	{
		private HashMap<String, Vector<ProductDO>> hmItems;
		private Vector<String> vecCategoryIds;
		private View view;
		private boolean isCaseEdited = true;
		
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			int groupPosition = -1;
			int childPosition = -1;
			public TextChangeListener(String type, int groupPosition, int childPosition)
			{
				this.type = type;
				this.groupPosition = groupPosition;
				this.childPosition = childPosition;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				
				if(s.length()>0)
				{
					try 
					{
						handleText(type, s);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}

			@Override
			public void afterTextChanged(Editable s) 
			{
				
					
			}
		}
		
		class FocusChangeListener implements OnFocusChangeListener
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
					view = v;
				else
					view = null;
			}
		}
		
		private void handleText(String type, CharSequence s)
		{
			
			synchronized (MyApplication.MyLock) 
			{
				ProductDO objItem = null;
				if(view != null)
				{
					objItem = (ProductDO) view.getTag();
				}
				
				if(objItem != null)
				{
					if(type.equalsIgnoreCase("units"))
					{
						objItem.preUnits = s.toString();
						if(!isInventoryAvail(objItem))
						{
							objItem.preUnits = "";
							showToast("Entered quantity should not be greater than available quantity.");
							
							if(objItem.etUnits != null)
								objItem.etUnits.setText("");    
						}
						
					}
					else if(type.equalsIgnoreCase("cases"))
					{
						
							objItem.preCases = s.toString();
							if(!isInventoryAvail(objItem))
							{
								objItem.preCases = "";
								showToast("Entered quantity should not be greater than available quantity.");
										
								if(objItem.etCases != null)
									objItem.etCases.setText("");   
							}
					
					}
				
			}
			}
			/////////////////////////////////
			
			if(threadForCheckDiscount != null && threadForCheckDiscount.isAlive())
				threadForCheckDiscount.interrupt();
			
			threadForCheckDiscount = new Thread(new Runnable() 
			{
				public void run() 
				{
					float tPrice = 0.0f, tDeposit = 0.0f, tOrderVal = 0.0f, tDiscVal = 0.0f;
					
					if(hmItems == null || hmItems.size() == 0)
						return;

					Set<String> set = hmItems.keySet();
					
					vecCategoryIds.clear();
					
					outer :
					{
						tPrice 		= 0.0f;
						tDeposit 	= 0.0f;
						tOrderVal 	= 0.0f;
						for(String key : set)
						{
							vecCategoryIds.add(key);
							
							Vector<ProductDO> vecItems = hmItems.get(key);
							
							for(ProductDO objProductDO : vecItems)
							{
								if(Thread.interrupted())
									break outer;
								
								if(!objProductDO.isAdvanceOrder)
								{
									objProductDO.totalPrice 	= ((objProductDO.itemPrice*objProductDO.totalCases));
									objProductDO.invoiceAmount 	= 	StringUtils.getFloat((objProductDO.unitSellingPrice*objProductDO.totalCases)+"");
									objProductDO.invoiceAmount 	=	objProductDO.invoiceAmount - objProductDO.depositPrice;
									
									tOrderVal 		+= 	objProductDO.invoiceAmount;
									tPrice 			+=	(objProductDO.totalPrice);
									tDeposit 		+=	objProductDO.depositPrice;
									tDiscVal 		+=	(objProductDO.DiscountAmt * objProductDO.totalCases);
								}
								else
								{
									
									objProductDO.invoiceAmount 	= 	StringUtils.getFloat((objProductDO.unitSellingPrice*objProductDO.totalCases)+"");
									tOrderVal 					+= 	objProductDO.invoiceAmount;
									objProductDO.invoiceAmount 	= 	objProductDO.invoiceAmount - objProductDO.depositPrice;
									objProductDO.totalPrice 	= 	objProductDO.invoiceAmount;
									tPrice += objProductDO.totalPrice;
									tDeposit 	+=	objProductDO.depositPrice;
								}
							}
						}
					}
					
					if(!Thread.interrupted())
					{
						totalDiscount = tDiscVal; 
						runOnUiThread(new  Runnable()
						{
							public void run()
							{
								etDiscValue.setText(""+curencyCode+" "+decimalFormat.format(totalDiscount));
							}
						});
					}
					
					if(!Thread.interrupted())
					{
						orderTPrice	 = tPrice;
						totalIPrice  = tOrderVal;
						
						runOnUiThread(new  Runnable() 
						{
							public void run()
							{
								LogUtils.errorLog("totPrice", "totPrice last "+orderTPrice );
								evTotalValue.setText(""+curencyCode+" "+amountFormate.format(totalIPrice));
								tvOrderValue.setText(""+curencyCode+" "+amountFormate.format(orderTPrice));
								
								if(!checkCreditLimit(totalIPrice))
									evTotalValue.setTextColor(Color.RED);
								else
									evTotalValue.setTextColor(getResources().getColor(R.color.blue));
							}
						});
					}
				}
			});
			threadForCheckDiscount.start();
		}
		
		public CaptureInventaryAdapter(HashMap<String, Vector<ProductDO>> hmItems)
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				
				getGroupView(llLayoutMiddle);
			}
		}

		@SuppressWarnings("deprecation")
		public View getChildView(int groupPosition) 
		{
			final LinearLayout llChildViews = new LinearLayout(getApplicationContext());
			llChildViews.setOrientation(1);
			for(int childPosition =0;childPosition<hmItems.get(vecCategoryIds.get(groupPosition)).size();childPosition++)
			{
				final ProductDO objProduct = hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				
				view = null;
				final LinearLayout convertView= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
				TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
				TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				final Button btnDelete		= (Button)convertView.findViewById(R.id.btnDelete);
				final EditText evCases		= (EditText)convertView.findViewById(R.id.evCases);
				final EditText evUnits		= (EditText)convertView.findViewById(R.id.evUnits);
				
				final TextView tvDiscountAplied	= (TextView)convertView.findViewById(R.id.tvDiscountAplied);
				
				objProduct.etCases			=  evCases;
				objProduct.etUnits			=  evUnits;
				
				tvHeaderText.setText(objProduct.SKU);
				tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvDescription.setText(objProduct.Description);
				tvDescription.setTypeface(AppConstants.Roboto_Condensed);
				
				evUnits.setTag(objProduct);
				evCases.setTag(objProduct);
				
				if(objProduct.preUnits.equalsIgnoreCase("") || objProduct.preUnits.equalsIgnoreCase("0"))
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preUnits);
				
				if(objProduct.preCases.equalsIgnoreCase("") || objProduct.preCases.equalsIgnoreCase("0"))
					evCases.setText("");
				else
					evCases.setText(objProduct.preCases);
				
				if(objProduct.isMusthave)
					tvHeaderText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.star_red), null, null, null);
				else if(objProduct.isNew)
					tvHeaderText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.star_green), null, null, null);
				else if(objProduct.isFavourite)
					tvHeaderText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.star), null, null, null);
				else
					tvHeaderText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				
			
				tvDiscountAplied.setText("Promotions");
				 if(vecPromotions.containsKey(objProduct.SKU) && !objProduct.isPromotional)
					tvDiscountAplied.setVisibility(View.VISIBLE);
				else
					tvDiscountAplied.setVisibility(View.GONE);
				
				tvDiscountAplied.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) 
					{
						showSchemeProductDetail(vecPromotions, objProduct.SKU);
					}
				});
				
				
				if(objProduct.isPromotional || objProduct.isPromoOrder)
				{
					evCases.setEnabled(false);
					evCases.setFocusable(false);
					evCases.setFocusableInTouchMode(false);
					evCases.setCursorVisible(false);
					evCases.setSingleLine(false);
					
					evUnits.setEnabled(false);
					evUnits.setFocusable(false);
					evUnits.setFocusableInTouchMode(false);
					evUnits.setCursorVisible(false);
					evUnits.setSingleLine(false);
				}
				else
				{
					evCases.setEnabled(true);
					evCases.setFocusable(true);
					evCases.setFocusableInTouchMode(true);
					evCases.setCursorVisible(true);
					evCases.setSingleLine(true);
					
					evUnits.setEnabled(true);
					evUnits.setFocusable(true);
					evUnits.setFocusableInTouchMode(true);
					evUnits.setCursorVisible(true);
					evUnits.setSingleLine(true);
				}
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
				
				evCases.setOnFocusChangeListener(new FocusChangeListener());
				evCases.addTextChangedListener(new TextChangeListener("cases", groupPosition, childPosition));
				
				tvHeaderText.setTag(childPosition);
				
				convertView.setTag(objProduct);
//				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(80 * px)));
				
				((LinearLayout)(btnDelete.getParent())).setTag(childPosition);
				btnDelete.setVisibility(View.GONE);
//				btnDelete.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v) 
//					{
//						int groupPosition = StringUtils.getInt(v.getTag().toString());
//						hmItems.get(vecCategoryIds.get(groupPosition)).remove(objProduct);
//						TextView view= (TextView) ((LinearLayout)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(0)).getChildAt(1);
//						if(view !=null)
//						{
//							((TextView)view).setText("("+ hmItems.get(vecCategoryIds.get(groupPosition)).size() +" Sub Products)");
//						}
//						if(hmItems.get(vecCategoryIds.get(groupPosition)).size() == 0)
//						{
//							((ImageView)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(1)).setImageResource(R.drawable.arro);
//							((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(1)).setVisibility(View.GONE);
//						}
//						llChildViews.removeView(convertView);
//					}
//				});
				
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(65 * px)));
//				llChildViews.addView(convertView);
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
					}
				});
				
				 // Create a generic swipe-to-dismiss touch listener.
				convertView.setOnTouchListener(new SwipeDismissTouchListener(
						convertView,
	                    null,
	                    new SwipeDismissTouchListener.DismissCallbacks() {
	                        @Override
	                        public boolean canDismiss(Object token)
	                        {
	                        	return true;
	                        }

	                        @Override
	                        public void onDismiss(View view, Object token) {
	                        	
	                        	try 
	                        	{
	                        		ProductDO objProduct = (ProductDO) convertView.getTag();
	                        		removeProduct(objProduct);
	                        		llChildViews.removeView(convertView);
		                            calTotPrices(false, false);
								}
	                        	catch (Exception e)
	                        	{
	                        		e.printStackTrace();
								}
	                        }

							private void removeProduct(ProductDO objProduct)
							{
								boolean isToRemove = false;
								if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0 )
								{
									Set<String> keys = AppConstants.hmCapturedInventory.keySet();
									outer:
									for(String key : keys)
									{
										Vector<ProductDO> vec = AppConstants.hmCapturedInventory.get(key);
										if(vec != null)
										{
											inner:
											for(ProductDO productDO : vec)
											{
												if(productDO.SKU.equalsIgnoreCase(objProduct.SKU))
												{
													isToRemove = true;
													break inner;
												}
											}
										}
										
										if(isToRemove)
										{
											vec.remove(objProduct);
											break outer;
										}
									}
								}
							}
	                    }));
				llChildViews.addView(convertView);
			}
			return llChildViews;
		}


		public void getGroupView(LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle!=null)
				llLayoutMiddle.removeAllViews();
			for(int groupPosition = 0;groupPosition<vecCategoryIds.size();groupPosition++)
			{
				String strCategory = "";
				CategoryDO objCategoryDO = null;
				if(AppConstants.hmCateogories != null && vecCategoryIds != null)
					objCategoryDO = AppConstants.hmCateogories.get(vecCategoryIds.get(groupPosition));
				
				int childItemsSize = 0;
				
				if(vecCategoryIds != null)
					childItemsSize = hmItems.get(vecCategoryIds.get(groupPosition)).size();
				else 
					childItemsSize = hmItems.size();
				
				try 
				{
					if(objCategoryDO != null)
						strCategory = objCategoryDO.categoryName;
					else
						strCategory = vecCategoryIds.get(groupPosition);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					strCategory = "Others";
				}
				final LinearLayout convertView		=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llBottomLayout 		= (LinearLayout)convertView.findViewById(R.id.llBottomLayout);
				final LinearLayout llCode = (LinearLayout)convertView.findViewById(R.id.llCode);
				TextView tvCode 	= (TextView)convertView.findViewById(R.id.tvCode);
				TextView tvCases 	= (TextView)convertView.findViewById(R.id.tvCases);
				final ImageView ivArrow	= (ImageView)convertView.findViewById(R.id.ivArrow);
				TextView tvUnits 	= (TextView)convertView.findViewById(R.id.tvUnits);
				
				tvCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvCases.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvUnits.setTypeface(AppConstants.Roboto_Condensed_Bold);
				
				TextView tvTitleText	= (TextView)convertView.findViewById(R.id.tvInventryText);
				TextView tvNo			= (TextView)convertView.findViewById(R.id.tvNo);
				tvNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvTitleText.setTypeface(AppConstants.Roboto_Condensed_Bold);
				
				if(childItemsSize > 0 && objCategoryDO != null)
				{
					tvNo.setText("("+ childItemsSize +" Sub Products)");
					tvTitleText.setText(objCategoryDO.categoryName);
				}
				else if(!strCategory.equalsIgnoreCase("") && childItemsSize > 0)
				{
					tvTitleText.setText(strCategory);
					tvNo.setText("("+childItemsSize+" Sub Products)");
				}
				else
					tvNo.setText("(0 Sub Products)");
				
				llBottomLayout.setTag(groupPosition);
				llBottomLayout.setVisibility(View.GONE);
				llBottomLayout.addView(getChildView(groupPosition));
				convertView.setTag("closed");
			
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						if( vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString())) != null && vecCategoryIds.size() > 0 && hmItems.get(vecCategoryIds.get(StringUtils.getInt(llBottomLayout.getTag().toString()))).size()>0)
						{
							if(v.getTag().toString().equalsIgnoreCase("closed"))
							{
								ivArrow.setImageResource(R.drawable.arro2);
								v.setTag("open");
								if(llBottomLayout.getChildCount() > 0)
								{
									llCode.setVisibility(View.VISIBLE);
									llBottomLayout.setVisibility(View.VISIBLE);
								}
							}
							else
							{
								v.setTag("closed");
								llBottomLayout.setVisibility(View.GONE);
								ivArrow.setImageResource(R.drawable.arro);
								llCode.setVisibility(View.GONE);
							}
						}
					}
				});
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						convertView.performClick();
					}
				}, 100);
				llLayoutMiddle.addView(convertView);
			}
		}

		//Method to refresh the List View
		private void refresh(HashMap<String, Vector<ProductDO>> hmItems) 
		{
			this.hmItems = hmItems;
			if(hmItems != null)
			{
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmItems.keySet();
				Iterator<String> iterator = set.iterator();
				while(iterator.hasNext())
					vecCategoryIds.add(iterator.next());
				if(llLayoutMiddle != null)
					getGroupView(llLayoutMiddle);
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			super.onBackPressed();
		}
	}
	
	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("notInstock"))
		{
			finish();
		}
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from); 
		if(from.equalsIgnoreCase("validate"))
		{
			Intent intent = new Intent(SalesManTakeOrder.this, SalesManTakeOrder.class);
			intent.putExtra("name",""+getString(R.string.Recommended_Order) );
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("alreadyprinted"))
		{
			showLoader(getString(R.string.your_recommended_order_is_printing));
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
					showCustomDialog(SalesManTakeOrder.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
		else if(from.equalsIgnoreCase("notInstock"))
		{
			adapterForCapture = new CaptureInventaryAdapter(AppConstants.hmCapturedInventory);
			etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
			evTotalValue.setText(""+curencyCode+" "+amountFormate.format(totalIPrice));
			tvOrderValue.setText(""+curencyCode+" "+amountFormate.format(orderTPrice));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 5000 && resultCode == RESULT_OK)
		{
			showLoader("Please wait...");
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					new OrderDA().updateOrderNumber(orderID);
					
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							new Handler().postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									hideLoader();
									Intent intent = new Intent(SalesManTakeOrder.this, SalesmanOrderPreview.class);
									intent.putExtra("isReturnRequest", true);
									intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
									intent.putExtra("Discount", etDiscValue.getText().toString().trim());
									intent.putExtra("objOrder",objOrder);
									intent.putExtra("mallsDetails",mallsDetails);
									intent.putExtra("orderID",orderID);
									startActivityForResult(intent, 1000);
								}
							}, 300);
						}
					});
				}
			}).start();
		}
		else if(requestCode == 5000 && resultCode == 100)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					if(orderID != null && orderID.length() > 0)
						new CustomerDetailsDA().deletePendingInvoice(orderID);
				}
			}).start();
		}
		else if(requestCode == 200)
		{
			if(AppConstants.hmCapturedInventory != null && adapterForCapture != null)
			{
				adapterForCapture.refresh(AppConstants.hmCapturedInventory);
				calTotPrices(false, true);
				tvNoOrder.setVisibility(View.GONE);
			}
			else
				tvNoOrder.setVisibility(View.VISIBLE);
		}
		else if(resultCode == 20000)
    	{
			showCustomDialog(SalesManTakeOrder.this, getString(R.string.successful), getString(R.string.your_recommended_order_printed), getString(R.string.OK), null , "");
    	}
		else if(resultCode == 10000)
		{
			finish();
		}
	}
	
	private boolean getAddedItemAvailability(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isAvail = false;
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size()>0)
		{
			Set<String> set = AppConstants.hmCapturedInventory.keySet();
			for(String strKey : set)
			{
				Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(strKey);
				if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
				{
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(objProductDO.totalUnits > 0)
						{
							isAvail  = true;
							break;
						}
					}
				}
			}
		}
		return isAvail;
	}
	
	
	private boolean isInventoryAvail(ProductDO objItem)
	{
		
			boolean isAvail = false;
			if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.SKU))
			{
				HHInventryQTDO inventryDO = hmInventory.get(objItem.SKU);
				objItem.strExpiryDate = inventryDO.expiryDate;
				objItem.BatchCode 	  = inventryDO.batchCode;
				
				float availQty = inventryDO.totalQt;
				float factor   = getUOMFactor(objItem);
				if(objItem.totalUnits*factor > availQty)
				{
					isAvail = false;
					hmInventory.get(objItem.SKU).tempTotalQt = 0;
				}
				else
				{
					isAvail = true;
					hmInventory.get(objItem.SKU).tempTotalQt = StringUtils.getInt(objItem.preUnits);
				}
			}
			else
				isAvail = false;
			
			return isAvail;
		
	}
	private void checkAvailableInventory()
	{
		ArrayList<String> temp = new ArrayList<String>();
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
		{
			Set<String> Keys = AppConstants.hmCapturedInventory.keySet();
			for(String s : Keys)
			{
				Vector<ProductDO> vecProductDOs = AppConstants.hmCapturedInventory.get(s);
				Vector<ProductDO> vecProductDOsTemp = new Vector<ProductDO>();				
				for (ProductDO productDO : vecProductDOs)
				{
					if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(productDO.SKU))
					{
						HHInventryQTDO hhInventryQTDO = hmInventory.get(productDO.SKU);
						float availQty = hhInventryQTDO.totalQt;
						
						productDO.strExpiryDate   = hhInventryQTDO.expiryDate;
						productDO.BatchCode 	  = hhInventryQTDO.batchCode;
						if(productDO.totalUnits > availQty)
						{
							productDO.preUnits = (int)availQty+"";
							productDO.preCases = "0";
						}
						else if(availQty == 0)
							vecProductDOsTemp.add(productDO);
					}
					else
						vecProductDOsTemp.add(productDO);
				}
				
				for(ProductDO p : vecProductDOsTemp)
					vecProductDOs.remove(p);
				
				if(vecProductDOs.size()==0)
					temp.add(s);
			}
			
			for(String s : temp)
				AppConstants.hmCapturedInventory.remove(s);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(GotoTeleOrders);
	}
	
	private void refreshList(ArrayList<ImageView> arrayList, int position)
	{
		if(arrayList != null && arrayList.size() > 0)
		{
			for(int i = 0; i < arrayList.size(); i++)
			{
				if(i != position)
				{
					ImageView ivSelector = arrayList.get(i);
					ivSelector.setBackgroundResource(R.drawable.check_normal);
					ivSelector.setTag(0);
				}
			}
		}
	}
	
	private Vector<ProductDO> getSelectedScheme(HashMap<String, Vector<ProductDO>> selectedScheme)
	{
		Vector<ProductDO> vecProductDOs = new Vector<ProductDO>();
		Set<String> keStrings = selectedScheme.keySet();
		for (String string : keStrings)
		{
			Vector<ProductDO> vecProductDOs2 = selectedScheme.get(string);
			if(vecProductDOs2 != null && vecProductDOs2.size() > 0)
				vecProductDOs.addAll(vecProductDOs2);
		}
		return vecProductDOs;
	}
	
	/**
	 * Method to check the promotion availability
	 * @param selectedScheme
	 * @param offerVectorNew 
	 * @return
	 */
	private boolean getSelectedAvailability(HashMap<String, Vector<ProductDO>> selectedScheme, HashMap<String, ArrayList<ProductDO>> offerVectorNew)
	{
		boolean isAvail  = false;
		Set<String> key1 = offerVectorNew.keySet();
		Set<String> key2 = selectedScheme.keySet();
		
		if(key1.size() == key2.size())
			isAvail = true;
		return isAvail;
	}
	

	
	private boolean checkCreditLimit(float amount)
	{
		if(creditLimit != null && StringUtils.getFloat(creditLimit.availbleLimit) < amount)
			return false;
		else
			return true;
	}
	
		
	private void showCreditExceededPopup() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final int count = new ARCollectionDA().getPendingInvoicesCountBySite(mallsDetails.site);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						creditLimitPopup(mallsDetails, count,false,null);
					}
				});
			}
		}).start();
	}
	
	private float getUOMFactor(ProductDO objItem)
	{
		String key = objItem.SKU + objItem.UOM;
		if(key.contains("GAL"))
			key = key.replace("GAL", "GLN");
		
		float factor = 1;
		if(hmConversion != null && hmConversion.size() > 0 && hmConversion.containsKey(key))
			factor = hmConversion.get(key);
		
		return factor;
	}
	
	private void addItems(String cat, HashMap<String, Vector<ProductDO>> hmCapturedInventory, HashMap<String, Vector<ProductDO>> hmCaptured)
	{
		if(AppConstants.hmCapturedInventory == null)
			AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
		
		Set<String> keys = hmCaptured.keySet();
	
		for(String key : keys)
		{
			Vector<ProductDO> vector = hmCaptured.get(key);
			
			if(vector != null && vector.size() > 0 )
			{
				for(ProductDO productDO : vector)
				{
					Vector<ProductDO> vecProducts = AppConstants.hmCapturedInventory.get(key);
					if(vecProducts == null)
					{
						vecProducts = new Vector<ProductDO>(); 
						vecProducts.add(productDO);
						AppConstants.hmCapturedInventory.put(key, vecProducts);
					}
					else
					{
						boolean isAlready = false;
						for(ProductDO product : vecProducts)
						{
							if(product.SKU.equalsIgnoreCase(productDO.SKU))
							{
								isAlready = true;
								
								if(cat.equalsIgnoreCase(AppConstants.MUST_HAVE))
									product.isMusthave   = true;
								else if(cat.equalsIgnoreCase(AppConstants.FAVOURRITE))
									product.isFavourite   = true;
								else if(cat.equalsIgnoreCase(AppConstants.NEW_LAUNCHES))
									product.isNew   = true;
								break;
							}
						}
						if(!isAlready)
							vecProducts.add(productDO);
					}
				}
			}
		}
	}
	
	boolean isAdd = true;
	private void showSchemeProductDetail(final HashMap<String, Vector<PromotionOrderDO>> vecPromotions, String itemCode)
	{
		final HashMap<String, Vector<ProductDO>> offerVectorSelectedItem = new HashMap<String, Vector<ProductDO>>();
		final HashMap<String, ProductDO> orderVectorSelectedItem = new HashMap<String, ProductDO>();
		final HashMap<String, ArrayList<ImageView>> map = new HashMap<String, ArrayList<ImageView>>();
		View view = inflater.inflate(R.layout.custom_popup_upcomming_detail, null);
		
		final CustomDialog mCustomDialog = new CustomDialog(SalesManTakeOrder.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		ArrayList<String> arrStrin = new ArrayList<String>();
		Set<String> keys = vecPromotions.keySet();
		if(itemCode != null && itemCode.length() > 0)
		{
			arrStrin.add(itemCode);
		}
		else
		{
			for (String string : keys) {
				arrStrin.add(string);
			}
		}
		
		LinearLayout llProductDetailMiddle = (LinearLayout)view.findViewById(R.id.llProductDetailMiddle);
		Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
		Button btnCancell = (Button) view.findViewById(R.id.btnCancell);
		
		ArrayList<ImageView> arrayView ;
		for (final String string : arrStrin) 
		{
			Vector<PromotionOrderDO> vecOrder = vecPromotions.get(string);
			
			for (final PromotionOrderDO promotionOrderDO : vecOrder)
			{
				int selDefaultPos = -1;
				arrayView = new ArrayList<ImageView>();
				Vector<ProductDO> vecOffers = promotionOrderDO.vecOffers;
				
				final LinearLayout llProductDetailMiddleContentGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_popup_upcomming_detail_content_new, null);
				TextView tvItemcode = (TextView) llProductDetailMiddleContentGroup.findViewById(R.id.tvItemcode);
				final LinearLayout llProductDetailMiddleChield = (LinearLayout) llProductDetailMiddleContentGroup.findViewById(R.id.llProductDetailMiddleChield);
				
				if(vecOffers != null && vecOffers.size() > 0)
				{
					tvItemcode.setText("Ordered Item: "+string);
					int position = -1;
					for (final ProductDO productDO : vecOffers)
					{
						position++;
						final LinearLayout llProductDetailMiddleContent = (LinearLayout) getLayoutInflater().inflate(R.layout.product_detail_content_new, null);
					
						final TextView tvOrderedItemName	= (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedItemName);
						TextView tvOrderedItemDesc			= (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedItemDesc);
						EditText etOrderedUOM				= (EditText)llProductDetailMiddleContent.findViewById(R.id.etOrderedUOM);
						final EditText etOrderedQuantity	= (EditText)llProductDetailMiddleContent.findViewById(R.id.etOrderedQuantity);
						final TextView tvOfferedItemName	= (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOfferedItemName);
						TextView tvOfferedItemDesc			= (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOfferedItemDesc);
						EditText etOfferedUOM				= (EditText)llProductDetailMiddleContent.findViewById(R.id.etOfferedUOM);
						final EditText etOfferedQuantity	= (EditText)llProductDetailMiddleContent.findViewById(R.id.etOfferedQuantity);
						
						final ImageView ivSelected	 		= (ImageView)llProductDetailMiddleContent.findViewById(R.id.ivSelected);
						//setting type-faces here
						
						tvOrderedItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
						tvOrderedItemDesc.setTypeface(AppConstants.Roboto_Condensed_Bold);
						etOrderedUOM.setTypeface(AppConstants.Roboto_Condensed_Bold);
						etOrderedQuantity.setTypeface(AppConstants.Roboto_Condensed_Bold);
						tvOfferedItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
						tvOfferedItemDesc.setTypeface(AppConstants.Roboto_Condensed_Bold);
						etOfferedUOM.setTypeface(AppConstants.Roboto_Condensed_Bold);
						etOfferedQuantity.setTypeface(AppConstants.Roboto_Condensed_Bold);
						
						tvOrderedItemName.setText(promotionOrderDO.ItemCode);
						tvOrderedItemDesc.setText(promotionOrderDO.productDO.Description);
						etOrderedUOM.setText(promotionOrderDO.productDO.UOM+"");
						promotionOrderDO.productDO.preUnits = promotionOrderDO.productDO.units;
						etOrderedQuantity.setText(promotionOrderDO.productDO.preUnits+"");
						
						productDO.preUnits = productDO.units;
						tvOfferedItemName.setText(productDO.SKU);
						tvOfferedItemDesc.setText(productDO.Description);
						etOfferedUOM.setText(productDO.UOM);
						etOfferedQuantity.setText(productDO.preUnits+"");
						
						tvOrderedItemName.setTag(position);
						
						ivSelected.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								llProductDetailMiddleContent.performClick();
							}
						});
						arrayView.add(ivSelected);
						ivSelected.setTag(0);
						llProductDetailMiddleContent.setTag(productDO);
						llProductDetailMiddleContent.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v)
							{
								
								if((Integer)ivSelected.getTag() == 0)
								{
									ivSelected.setBackgroundResource(R.drawable.check_hover);
									ivSelected.setTag(1);
									
									Builder alertDialog = new AlertDialog.Builder(SalesManTakeOrder.this);
									alertDialog.setSingleChoiceItems(arrString, -1, new DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(final DialogInterface dialog, int which) 
										{
											final int count = StringUtils.getInt(arrString[which]);
											
											new Thread(new Runnable() {
												@Override
												public void run()
												{
													final ProductDO mProductDO = (ProductDO) llProductDetailMiddleContent.getTag();
													
													promotionOrderDO.productDO.preUnits = StringUtils.getInt(promotionOrderDO.productDO.units)*count+"";
													mProductDO.preUnits = StringUtils.getInt(mProductDO.units)*count+"";
												
													isAdd = true;
													
													if(!isInventoryAvail(promotionOrderDO.productDO) || !isInventoryAvail(mProductDO))
													{
														promotionOrderDO.productDO.preUnits = promotionOrderDO.productDO.units;
														mProductDO.preUnits = mProductDO.units;
														isAdd = false;
													}
													
													if(isAdd)
													{
														orderVectorSelectedItem.put(promotionOrderDO.productDO.SKU, promotionOrderDO.productDO);
														Vector<ProductDO> vector = offerVectorSelectedItem.get(string);
														vector = new Vector<ProductDO>();
														vector.add(mProductDO);
														if(vector != null && vector.size() > 0)
															offerVectorSelectedItem.put(string, vector);
													}
													
													runOnUiThread(new Runnable()
													{
														@Override
														public void run() 
														{
															etOfferedQuantity.setText(mProductDO.preUnits);
															etOrderedQuantity.setText(promotionOrderDO.productDO.preUnits);
															if(!isAdd)
															{
																Toast.makeText(SalesManTakeOrder.this, "Quantity for "+mProductDO.SKU+" is not available is stock, please reduce the quantity.", Toast.LENGTH_SHORT).show();
																llProductDetailMiddleContent.performClick();
															}
															else 
																refreshList(map.get(string), (Integer)tvOrderedItemName.getTag());
															dialog.dismiss();
														}
													});
												}
											}).start();
										}
									});
									
									alertDialog.setCancelable(false);
									alertDialog.create().show();
								}
								else
								{
									ivSelected.setBackgroundResource(R.drawable.check_normal);
									ivSelected.setTag(0);
									new Thread(new Runnable() {
										@Override
										public void run()
										{
											orderVectorSelectedItem.remove(promotionOrderDO.productDO.CategoryId);
											offerVectorSelectedItem.remove(string);
											runOnUiThread(new Runnable()
											{
												@Override
												public void run() 
												{
													refreshList(map.get(string), (Integer)tvOrderedItemName.getTag());
												}
											});
										}
									}).start();
								}
							}
						});
						setTypeFaceRobotoNormal((ViewGroup)llProductDetailMiddleContent);
						llProductDetailMiddleChield.addView(llProductDetailMiddleContent);
					}
					map.put(string, arrayView);
					llProductDetailMiddle.addView(llProductDetailMiddleContentGroup);
				}
			}
		}
		setTypeFaceRobotoNormal((ViewGroup)view);
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				
				showLoader("Please wait...");
				new Thread(new Runnable() {
					
					@Override
					public void run() 
					{
						if(AppConstants.hmCapturedInventory == null)
							AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
						
//						AppConstants.hmCapturedInventory.remove("PROMO");
						
						Set<String> keys = orderVectorSelectedItem.keySet();
						for(String key : keys)
						{
							ProductDO pDo = orderVectorSelectedItem.get(key);
							Vector<ProductDO> vecProduct;
						
							if(pDo.CategoryId.equalsIgnoreCase("GENERAL"))
								vecProduct = AppConstants.hmCapturedInventory.get("CA001");
							else
								vecProduct = AppConstants.hmCapturedInventory.get(pDo.CategoryId);
							
							if(vecProduct != null && vecProduct.size() > 0)
							{
								boolean isAvail = false;
								ProductDO temDo = null;
								inner:
								for(ProductDO productDO : vecProduct)
								{
									if(productDO.SKU.equalsIgnoreCase(pDo.SKU))
									{
										temDo = productDO;
										isAvail = true;
										break inner;
									}
								}
								if(!isAvail)
									vecProduct.add(pDo);
								else
								{
									if(temDo != null)
									{
										vecProduct.remove(temDo);
										vecProduct.add(pDo);
									}
								}
							}
							else
							{
								vecProduct = new Vector<ProductDO>();
								vecProduct.add(pDo);
							}
							
							if(pDo.CategoryId.equalsIgnoreCase("GENERAL"))
								AppConstants.hmCapturedInventory.put("CA001", vecProduct);
							else
								AppConstants.hmCapturedInventory.put(pDo.CategoryId, vecProduct);
						}
						final Vector<ProductDO> vecProductDOs = getSelectedScheme(offerVectorSelectedItem);
						Vector<ProductDO> vecProductDOs2 = AppConstants.hmCapturedInventory.get("PROMO");
					
						if(vecProductDOs2 == null)
						{
							vecProductDOs2 = new Vector<ProductDO>();
							if(vecProductDOs != null && vecProductDOs.size() > 0)
								vecProductDOs2.addAll(vecProductDOs);
						}
						else
						{
							for (ProductDO productDO : vecProductDOs) 
							{
								boolean isFound = false;
								for (ProductDO productDO1 : vecProductDOs2)
								{
									if(productDO1.SKU.equalsIgnoreCase(productDO.SKU))
									{
										isFound = true;
										productDO1 = productDO;
										break;
									}
								}
								if(!isFound)
									vecProductDOs2.add(productDO);
							}
						}
						AppConstants.hmCapturedInventory.put("PROMO", vecProductDOs2);
						runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
								{
									mCustomDialog.dismiss();
									calTotPrices(false, true);
									tvNoOrder.setVisibility(View.GONE);
								}
								hideLoader();
							}
						});
					}
				}).start();
			}
		});
		btnCancell.setVisibility(View.GONE);
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
}

