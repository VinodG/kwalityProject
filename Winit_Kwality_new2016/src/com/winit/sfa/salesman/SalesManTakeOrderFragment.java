package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
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
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesManTakeOrderFragment extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave, llOrderVal, llPricing, llCreditLimit;
	private TextView tvLu, tvCI, tvHeaderText, tvOrderValue, tvOrder, evTotalValue, 
					 etDiscValue , tvNoOrder, tvDisHeader, tvCreditLimitVal;
	private Button  btnAddIetem, btnAddItems, btnCancel1, btnConfirmOrder;
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
					showCustomDialog(SalesManTakeOrderFragment.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
				}
				else if(!isAvail)
				{
					btnConfirmOrder.setEnabled(true);
					btnConfirmOrder.setClickable(true);
					showCustomDialog(SalesManTakeOrderFragment.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
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
							if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0 && AppConstants.hmCapturedInventory.containsKey("PROMO"))
								AppConstants.hmCapturedInventory.remove("PROMO");
							
							if(mallsDetails.isSchemeAplicable == 1)
							{
								filterItems(AppConstants.hmCapturedInventory);
								PromotionalItemsDA promotionalItemsDA = new PromotionalItemsDA();
								promotionalItemsDA.getPromotionInTemp(preference.getStringFromPreference(Preference.SALESMANCODE, ""), mallsDetails.site);
								promotionalItemsDA.insertOrderItemsInTemp(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
								promotionalItemsDA.insertOrderItemsInTemp_New(AppConstants.hmCapturedInventory, "1", mallsDetails.site);
								offerVectorNew =  promotionalItemsDA.getPromotionItems();
							}
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									if(offerVectorNew != null && offerVectorNew.size() >0 )
									{
										btnConfirmOrder.setEnabled(true);
										btnConfirmOrder.setClickable(true);
										
										showSchemeProductDetail(offerVectorNew);
									}
									else
									{
										if(mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
											performCashCustomerPayment(null);
										else
										{
											Intent intent = new Intent(SalesManTakeOrderFragment.this, SalesmanOrderPreview.class);
											intent.putExtra("isReturnRequest", true);
											intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
											intent.putExtra("Discount", etDiscValue.getText().toString().trim());
											intent.putExtra("objOrder",objOrder);
											intent.putExtra("mallsDetails",mallsDetails);
											startActivityForResult(intent, 1000);
										}
										
										new Handler().postDelayed(new Runnable() {
											
											@Override
											public void run() 
											{
												btnConfirmOrder.setEnabled(true);
												btnConfirmOrder.setClickable(true);
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
				/****************************************************/
//				HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
//				hhInventryQTDO.totalQt 	  = 100;
//				hhInventryQTDO.totalUnits = Math.round(100/12);
//				hmInventory.put("FE001", hhInventryQTDO);
				/****************************************************/
				AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
				
				if(objOrder != null && objOrder.OrderId != null)
					AppConstants.hmCapturedInventory  = new CaptureInventryDA().getLastOrderByOrderAndPreseller(objOrder.OrderId, hmInventory, CaptureInventryDA.DELIVERY, mallsDetails.priceList);
				
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
		
		etDiscValue			= (TextView)llCapture_Inventory.findViewById(R.id.etDiscValue);
		tvDisHeader			= (TextView)llCapture_Inventory.findViewById(R.id.tvDisHeader);
		llBottomButtons		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llBottomButtons);
		llReturnSave		= (LinearLayout)llCapture_Inventory.findViewById(R.id.llReturnSave);
		tvHeaderText		= (TextView)llCapture_Inventory.findViewById(R.id.tvHeaderText);
		
		tvCreditLimitVal	= (TextView)llCapture_Inventory.findViewById(R.id.tvCreditLimitVal);
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		tvOrder				= (TextView)llCapture_Inventory.findViewById(R.id.tvOrder);
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		
		
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
			tvLu.setText(mallsDetails.siteName +" ["+mallsDetails.site+"]"/* + " ("+mallsDetails.partyName+")"*/);
		
		btnAddIetem.setVisibility(View.GONE);
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
						adapterForCapture = new CaptureInventaryAdapter(null);
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
							tvCreditLimitVal.setText(mallsDetails.currencyCode +" "+decimalFormat.format(StringUtils.getFloat(creditLimit.availbleLimit)));
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
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManTakeOrderFragment.this);
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
		
		
		final Button btnSearch = new Button(SalesManTakeOrderFragment.this);
		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeOrderFragment.this, "Select Category", true);
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
					showCustomDialog(SalesManTakeOrderFragment.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
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
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManTakeOrderFragment.this));
									}
									else
									{
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManTakeOrderFragment.this));
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
							Vector<ProductDO> vecProducts = AppConstants.hmCapturedInventory.get(objProduct.CategoryId);
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
					showCustomDialog(SalesManTakeOrderFragment.this, "Warning !", "Please select Items.", "OK", null, "");
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
		private HashMap<String, Vector<TrxDetailsDO>> hmItems;
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
				try 
				{
					handleText(type, s);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
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
			TrxDetailsDO objItem = null;
			if(view != null)
			{
				objItem = (TrxDetailsDO) view.getTag();
			}
			
			if(objItem != null)
			{
				objItem.requestedSalesBU = StringUtils.getFloat(s.toString());
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
							
							Vector<TrxDetailsDO> vecItems = hmItems.get(key);
							
							for(TrxDetailsDO objProductDO : vecItems)
							{
								if(Thread.interrupted())
									break outer;
								
//								objProductDO.totalPrice 	= ((objProductDO.itemPrice*objProductDO.totalUnits));
//								objProductDO.invoiceAmount 	= 	StringUtils.getFloat((objProductDO.unitSellingPrice*objProductDO.totalUnits)+"");
//								objProductDO.invoiceAmount 	=	objProductDO.invoiceAmount - objProductDO.depositPrice;
//								
//								tOrderVal 		+= 	objProductDO.invoiceAmount;
//								tPrice 			+=	(objProductDO.totalPrice);
//								tDeposit 		+=	objProductDO.depositPrice;
//								tDiscVal 		+=	(objProductDO.DiscountAmt * objProductDO.totalUnits);
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
								etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
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
		
		public CaptureInventaryAdapter(HashMap<String, Vector<TrxDetailsDO>> hmItems)
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
				final ProductDO objProduct = null;//hmItems.get(vecCategoryIds.get(groupPosition)).get(childPosition);
				
				view = null;
				final LinearLayout convertView= (LinearLayout)getLayoutInflater().inflate(R.layout.inventory_cell,null);
				TextView tvHeaderText		= (TextView)convertView.findViewById(R.id.tvHeaderText);
				TextView tvDescription		= (TextView)convertView.findViewById(R.id.tvDescription);
				final Button btnDelete		= (Button)convertView.findViewById(R.id.btnDelete);
				final EditText evCases		= (EditText)convertView.findViewById(R.id.evCases);
				final EditText evUnits		= (EditText)convertView.findViewById(R.id.evUnits);
				
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
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preCases);
				
				
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
				
				evUnits.setOnFocusChangeListener(new FocusChangeListener());
				evUnits.addTextChangedListener(new TextChangeListener("units", groupPosition, childPosition));
				
				evCases.setOnFocusChangeListener(new FocusChangeListener());
				evCases.addTextChangedListener(new TextChangeListener("cases", groupPosition, childPosition));
				
				tvHeaderText.setTag(childPosition);
				
				convertView.setTag(objProduct);
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(40 * px)));
				
				((LinearLayout)(btnDelete.getParent())).setTag(childPosition);
				btnDelete.setTag(groupPosition);
				btnDelete.setVisibility(View.GONE);
				btnDelete.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						int groupPosition = StringUtils.getInt(v.getTag().toString());
						hmItems.get(vecCategoryIds.get(groupPosition)).remove(objProduct);
						TextView view= (TextView) ((LinearLayout)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(0)).getChildAt(1);
						if(view !=null)
						{
							((TextView)view).setText("("+ hmItems.get(vecCategoryIds.get(groupPosition)).size() +" Sub Products)");
						}
						if(hmItems.get(vecCategoryIds.get(groupPosition)).size() == 0)
						{
							((ImageView)((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(0)).getChildAt(1)).setImageResource(R.drawable.arro);
							((LinearLayout)((LinearLayout)((LinearLayout)llChildViews.getParent()).getParent()).getChildAt(1)).setVisibility(View.GONE);
						}
						llChildViews.removeView(convertView);
					}
				});
				
				convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(40 * px)));
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
		private void refresh(HashMap<String, Vector<TrxDetailsDO>> hmItems) 
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
			Intent intent = new Intent(SalesManTakeOrderFragment.this, SalesManTakeOrderFragment.class);
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
					showCustomDialog(SalesManTakeOrderFragment.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
		else if(from.equalsIgnoreCase("notInstock"))
		{
			adapterForCapture = new CaptureInventaryAdapter(null/*AppConstants.hmCapturedInventory*/);
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
									Intent intent = new Intent(SalesManTakeOrderFragment.this, SalesmanOrderPreview.class);
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
				adapterForCapture.refresh(null);
				calTotPrices(false, true);
				tvNoOrder.setVisibility(View.GONE);
			}
			else
				tvNoOrder.setVisibility(View.VISIBLE);
		}
		else if(resultCode == 20000)
    	{
			showCustomDialog(SalesManTakeOrderFragment.this, getString(R.string.successful), getString(R.string.your_recommended_order_printed), getString(R.string.OK), null , "");
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
	
	private ProductDO checkOfferedItemNew(ProductDO productDO)
	{
		Set<String> keSet = AppConstants.hmCapturedInventory.keySet();
		for (String string : keSet) {
			Vector<ProductDO> vecOrderedItems = AppConstants.hmCapturedInventory.get(string);
			for (ProductDO productDONew : vecOrderedItems) {
				if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(productDONew.SKU))
				{
					float availQty = hmInventory.get(productDONew.SKU).totalQt-productDONew.totalCases;
					if(productDO.SKU.equalsIgnoreCase(productDONew.SKU))
					{
						if(productDO.totalCases > availQty)
						{
							productDO.preCases = 0+"";
							productDO.preUnits = 0+"";
							productDO.totalCases = 0;
						}
					}
				}
			}
		}
		if(hmInventory != null && hmInventory.size() > 0 && !hmInventory.containsKey(productDO.SKU))
		{
			productDO.preCases = 0+"";
			productDO.preUnits = 0+"";
			productDO.totalCases = 0;
		}
		
		return productDO;
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
						float availQty = hmInventory.get(productDO.SKU).totalQt;
						if(StringUtils.getInt(productDO.preUnits) > availQty)
						{
							productDO.preUnits = availQty+"";
						}
						else if(availQty == 0)
						{
							vecProductDOsTemp.add(productDO);
						}
					}
					else
					{
						vecProductDOsTemp.add(productDO);
					}
				}
				
				for(ProductDO p : vecProductDOsTemp)
				{
					vecProductDOs.remove(p);
				}
				if(vecProductDOs.size()==0)
				{
					temp.add(s);
				}
				
			}
			
			for(String s : temp)
				AppConstants.hmCapturedInventory.remove(s);
		}
		
		
	}
	
	private boolean checkItem(ProductDO objItem)
	{
		boolean isAvail = false;
		if(StringUtils.getFloat(objItem.preUnits) > StringUtils.getFloat(objItem.ActpreUnits))
		{
			isAvail = false;
		}
		else
			isAvail = true;
		
		return isAvail;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(GotoTeleOrders);
	}
	
	private void showSchemeProductDetail(final HashMap<String, ArrayList<ProductDO>> offerVectorNew)
	{
		final HashMap<String, Vector<ProductDO>> offerVectorSelectedItem = new HashMap<String, Vector<ProductDO>>();
		final HashMap<String, ArrayList<ImageView>> map = new HashMap<String, ArrayList<ImageView>>();
		View view = inflater.inflate(R.layout.custom_popup_upcomming_detail, null);
		
		final CustomDialog mCustomDialog = new CustomDialog(SalesManTakeOrderFragment.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		Set<String> keys = offerVectorNew.keySet();
		
		LinearLayout llProductDetailMiddle = (LinearLayout)view.findViewById(R.id.llProductDetailMiddle);
		Button btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
		Button btnCancell = (Button) view.findViewById(R.id.btnCancell);
		
		ArrayList<ImageView> arrayView ;
		for (final String string : keys) 
		{
			int selDefaultPos = -1;
			arrayView = new ArrayList<ImageView>();
			ArrayList<ProductDO> offerVector = offerVectorNew.get(string);
			
			final LinearLayout llProductDetailMiddleContentGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_popup_upcomming_detail_content, null);
			TextView tvItemcode = (TextView) llProductDetailMiddleContentGroup.findViewById(R.id.tvItemcode);
			final LinearLayout llProductDetailMiddleChield = (LinearLayout) llProductDetailMiddleContentGroup.findViewById(R.id.llProductDetailMiddleChield);
			
			TextView tvOrderPreviewPrice = (TextView) llProductDetailMiddleContentGroup.findViewById(R.id.tvOrderPreviewPrice);
			TextView tvOrderPreviewTotalAmt = (TextView) llProductDetailMiddleContentGroup.findViewById(R.id.tvOrderPreviewTotalAmt);
			
			if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			{
				tvOrderPreviewPrice.setVisibility(View.GONE);
				tvOrderPreviewTotalAmt.setVisibility(View.GONE);
			}
			else
			{
				tvOrderPreviewPrice.setVisibility(View.VISIBLE);
				tvOrderPreviewTotalAmt.setVisibility(View.VISIBLE);
			}
			
			if(offerVector != null && offerVector.size() > 0)
			{
				tvItemcode.setText("Ordered Item: "+string);
				int position = -1;
				for (ProductDO productDO : offerVector)
				{
					position++;
					final LinearLayout llProductDetailMiddleContent = (LinearLayout) getLayoutInflater().inflate(R.layout.product_detail_content, null);
					final TextView tvOrderedItemName 	 = (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedItemName);
					TextView tvAmount 			 = (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedPrice);
					TextView tvOrderedTotalAmount= (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedTotalAmount);
					TextView tvOrderedItemDesc 	 = (TextView)llProductDetailMiddleContent.findViewById(R.id.tvOrderedItemDesc);
					EditText etQuantity 		 = (EditText)llProductDetailMiddleContent.findViewById(R.id.etOrderedQuantity);
					EditText etCases 	 		 = (EditText)llProductDetailMiddleContent.findViewById(R.id.etOrderedCases);
					EditText etAvailQuantity 	 = (EditText)llProductDetailMiddleContent.findViewById(R.id.etAvailQuantity);
					
					final ImageView ivSelected	 = (ImageView)llProductDetailMiddleContent.findViewById(R.id.ivSelected);
					//setting type-faces here
					tvOrderedItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
					etQuantity.setTypeface(AppConstants.Roboto_Condensed_Bold);
					etCases.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvOrderedTotalAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvOrderedItemDesc.setTypeface(AppConstants.Roboto_Condensed);
					etAvailQuantity.setTypeface(AppConstants.Roboto_Condensed);
					//managing the Decimal format up to  ##.##

					selDefaultPos = checkPromotionInventory(etAvailQuantity, tvOrderedItemName, ivSelected, llProductDetailMiddleContent, productDO, position, selDefaultPos);
					
					//setting texts
					tvOrderedItemName.setText(productDO.SKU);
					if(productDO.isMusthave)
						tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.star1), null);
					else
						tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
					
					if(productDO.preUnits != null && !productDO.preUnits.equalsIgnoreCase(""))
						etQuantity.setText(productDO.preUnits);
					else
						etQuantity.setText("0");
					
					if(productDO.UOM != null && !productDO.UOM.equalsIgnoreCase(""))
						etCases.setText(productDO.UOM);
					else
						etCases.setText("0");
					
					tvOrderedItemDesc.setText(((""+productDO.Description)).trim());
					
					tvAmount.setText(Html.fromHtml("<font color = #808080>AED  </font>"+amountFormate.format(productDO.itemPrice)));
				
					
					if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
					{
						tvAmount.setVisibility(View.GONE);
						tvOrderedTotalAmount.setVisibility(View.GONE);
					}
					else
					{
						tvAmount.setVisibility(View.VISIBLE);
						tvOrderedTotalAmount.setVisibility(View.VISIBLE);
					}
					
					tvOrderedTotalAmount.setText(Html.fromHtml("<font color = #808080>AED  </font>"+amountFormate.format(productDO.totalPrice)));
					tvOrderedItemName.setTag(position);
					ivSelected.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							llProductDetailMiddleContent.performClick();
						}
					});
					
					if(selDefaultPos == position)
					{
						ivSelected.setBackgroundResource(R.drawable.check_hover);
						ivSelected.setTag(1);
						new Thread(new Runnable() {
							@Override
							public void run()
							{
								ProductDO mProductDO = (ProductDO) llProductDetailMiddleContent.getTag();
								
								Vector<ProductDO> vector = offerVectorSelectedItem.get(string);
								vector = new Vector<ProductDO>();
								vector.add(mProductDO);
								if(vector != null && vector.size() > 0)
									offerVectorSelectedItem.put(string, vector);
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
					
					arrayView.add(ivSelected);
					ivSelected.setTag(0);
					llProductDetailMiddleContent.setTag(productDO);
					llProductDetailMiddleContent.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v)
						{
							ivSelected.setBackgroundResource(R.drawable.check_hover);
							ivSelected.setTag(1);
							new Thread(new Runnable() {
								@Override
								public void run()
								{
									ProductDO mProductDO = (ProductDO) llProductDetailMiddleContent.getTag();
									
									Vector<ProductDO> vector = offerVectorSelectedItem.get(string);
									vector = new Vector<ProductDO>();
									vector.add(mProductDO);
									if(vector != null && vector.size() > 0)
										offerVectorSelectedItem.put(string, vector);
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
					});
					setTypeFaceRobotoNormal((ViewGroup)llProductDetailMiddleContent);
					llProductDetailMiddleChield.addView(llProductDetailMiddleContent);
				}
				map.put(string, arrayView);
				llProductDetailMiddle.addView(llProductDetailMiddleContentGroup);
			}
		}
		setTypeFaceRobotoNormal((ViewGroup)view);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showLoader("Please wait...");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
//						if(!getSelectedAvailability(offerVectorSelectedItem, offerVectorNew))
//						{
//							showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Stock is not available!. Please reduce item quantity to apply scheme.", getString(R.string.OK), null, "");
//							hideLoader();
//						}
//						else
						{
							final Vector<ProductDO> vecProductDOs = getSelectedScheme(offerVectorSelectedItem);
							Vector<ProductDO> vecProductDOs2 = AppConstants.hmCapturedInventory.get("PROMO");
							if(vecProductDOs2 == null)
								vecProductDOs2 = new Vector<ProductDO>();
							if(vecProductDOs != null && vecProductDOs.size() > 0)
							{
								vecProductDOs2.addAll(vecProductDOs);
								AppConstants.hmCapturedInventory.put("PROMO", vecProductDOs2);
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									hideLoader();
//									if(vecProductDOs != null && vecProductDOs.size() > 0)
									{
										if(mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
											performCashCustomerPayment(mCustomDialog);
										else
										{
											mCustomDialog.dismiss();
											Intent intent = new Intent(SalesManTakeOrderFragment.this, SalesmanOrderPreview.class);
											intent.putExtra("isReturnRequest", true);
											intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
											intent.putExtra("Discount", etDiscValue.getText().toString().trim());
											intent.putExtra("objOrder",objOrder);
											intent.putExtra("mallsDetails",mallsDetails);
											startActivityForResult(intent, 1000);
										}
									}
//									else
//									{
//										showCustomDialog(SalesManTakeOrder.this, getString(R.string.warning), "Stock is not available!. Please reduce item quantity to apply scheme.", getString(R.string.OK), null, "");
//									}
								}
							});
						}
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
	
	private void filterItems( HashMap<String, Vector<ProductDO>> hmCapturedInventory) 
	{
		int count = 1;
		Set<String> set = AppConstants.hmCapturedInventory.keySet();
		for(String key : set)
		{
			Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(key);
			for(ProductDO objProductDO : vecOrderedProduct)
			{
				if(StringUtils.getFloat(objProductDO.preUnits) > 0 && !objProductDO.isAdvanceOrder)
				{
					objProductDO.LineNo = ""+count++;
				}
			}
		}
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
	
	private int checkPromotionInventory(EditText etAvailQuantity, TextView tvItemcode, ImageView ivSelected, LinearLayout llProductDetailMiddleContent, ProductDO productDO, int position, int selDefaultPos)
	{
		int availQty = 0;
		if(hmInventory != null && hmInventory.containsKey(productDO.SKU))
		{
			availQty = hmInventory.get(productDO.SKU).totalQt;
			
			if((hmInventory.get(productDO.SKU).totalQt- hmInventory.get(productDO.SKU).tempTotalQt) < StringUtils.getFloat(productDO.preUnits))
			{
				availQty = Math.round(hmInventory.get(productDO.SKU).totalQt- hmInventory.get(productDO.SKU).tempTotalQt);
				if(availQty < 0)
					availQty = 0;
				ivSelected.setEnabled(false);
				llProductDetailMiddleContent.setEnabled(false);
				tvItemcode.setTextColor(Color.RED);
			}
			else
			{
				availQty = Math.round(hmInventory.get(productDO.SKU).totalQt- hmInventory.get(productDO.SKU).tempTotalQt);
				if(selDefaultPos == -1)
					selDefaultPos = position;
			}
		}
		else
		{
			llProductDetailMiddleContent.setEnabled(false);
			ivSelected.setEnabled(false);
			tvItemcode.setTextColor(Color.RED);
		}
			 
		etAvailQuantity.setText(""+availQty);
		return selDefaultPos;
	}
	
	private boolean checkCreditLimit(float amount)
	{
		if(creditLimit != null && StringUtils.getFloat(creditLimit.availbleLimit) < amount)
			return false;
		else
			return true;
	}
	
	private void performCashCustomerPayment(final CustomDialog mCustomDialog) 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				orderID = new OrderDA().getOrderId(0);
				if(orderID != null && orderID.length() > 0)
					new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site, ""+totalIPrice, orderID,0);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(orderID == null || orderID.length() <= 0)
							showCustomDialog(SalesManTakeOrderFragment.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
						else
						{
							if(mCustomDialog != null)
								mCustomDialog.dismiss();
							
							hideLoader();
							Intent intent = new Intent(SalesManTakeOrderFragment.this, PendingInvoices.class);
							intent.putExtra("mallsDetails", mallsDetails);
							intent.putExtra("AR", true);
							intent.putExtra("isExceed", true);
							startActivityForResult(intent, 5000);
						}
					}
				});
			}
		}).start();
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
}