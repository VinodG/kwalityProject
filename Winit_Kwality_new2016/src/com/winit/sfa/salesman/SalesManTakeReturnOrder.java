package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.DiscountDO;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.Item;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesManTakeReturnOrder extends BaseActivity
{
	//declaration of variables
	private LinearLayout llCapture_Inventory, llTotalValue,llBottomButtons , llReturnSave,llPricing, llOrderVal, llLayoutMiddle;
	private TextView tvLu, tvCI, tvHeaderText, tvOrderValue, tvDisHeader, etDiscValue , tvNoOrder, evTotalValue;
	private Button  btnAddIetem, btnAddIetems,  btnCancel1, btnConfirmOrder;
	private Vector<String> vecCategory;
	private Vector<ProductDO> vecSearchedItemd;
	private CaptureInventaryAdapter adapterForCapture;
	private AddRecomendedItemAdapter adapter;
	private float orderTPrice = 0.0f, totalDiscount = 0.0f, totalIPrice = 0.0f, totalInvoicedPrice = 0.0f;
	private String orderedItems ="",orderedItemsList = "", orderId, strErrorMsg, 
			       strKeyNew = "",  from;
	private JourneyPlanDO mallsDetails ;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private Thread threadForCheckDiscount ;
	private boolean isAdvance = false, isMenu = false, isTask = false;
	private int cyear, cmonth, cday, mPosition = 0;
	private TextView tempView;
	private ProductDO productDOImage;
	OrderDO orderDO = null;
	
	@Override
	public void initialize()
	{
		
		llCapture_Inventory = (LinearLayout)inflater.inflate(R.layout.recommendedorder, null);
		llBody.addView(llCapture_Inventory,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails 			= (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isAdvance				= (boolean) getIntent().getExtras().getBoolean("isAdvance");
			orderId					= getIntent().getExtras().getString("invoicenum");
			totalInvoicedPrice		= getIntent().getExtras().getFloat("invoiceamt");
			from					= getIntent().getExtras().getString("from");
			isMenu					= getIntent().getExtras().getBoolean("isMenu");
			
			isTask = false;
			if((orderId == null || orderId.length() <= 0) && (from == null || from.length() <= 0))
			{
				from = "replacement";
				isTask = true;
			}
			else if(from == null)
				from = "";
				 
			if(isMenu)
			{
				btnCheckOut.setVisibility(View.GONE);
				ivLogOut.setVisibility(View.GONE);
			}
		}
		
		intialiseControls();
		setTypeFaceRobotoNormal(llCapture_Inventory);
		
		if(from.equalsIgnoreCase("replacement"))
			tvCI.setText("Capture Replacement Order");
		else 
			tvCI.setText("Capture Return Order");
		
		llBottomButtons.setVisibility(View.GONE);
		llReturnSave.setVisibility(View.VISIBLE);
		btnAddIetems.setVisibility(View.VISIBLE);
		etDiscValue.setEnabled(true);
		etDiscValue.setFocusable(true);
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				if(from != null && from.equalsIgnoreCase("replacement"))
					AppConstants.hmCateogories = new CategoriesDA().getCategoryListForReturn();
				else
					AppConstants.hmCateogories = new CategoriesDA().getCategoryList();
			}
		}).start();
		
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			llPricing.setVisibility(View.GONE);
		
		btnCancel1.setText("Cancel");
		btnCancel1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		btnConfirmOrder.setText(" Confirm Order ");
		btnConfirmOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						final boolean isAvail  = getAddedItemAvailability(AppConstants.hmCapturedInventory);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() <= 0 )
								{
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please select atleast one item.", getString(R.string.OK), null, "");
								}
								else if(!isAvail)
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than zero.", getString(R.string.OK), null, "");
								else if(!isAmountValid())
									showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Please decrease the quantity. Amount should not be greater then "+mallsDetails.currencyCode+" "+amountFormate.format(totalInvoicedPrice)+".", getString(R.string.OK), null, "");
								else if((from != null && from.equalsIgnoreCase("replacement")) && !checkImageValidation(AppConstants.hmCapturedInventory))
								{
								}
								else
								{
//									showPassCodeDialog(null, from, false);
									
									new Thread(new Runnable()
									{
										@Override
										public void run() 
										{
											NameIDDo nameIDDO	=  new CommonDA().validatePassCode(preference.getStringFromPreference(Preference.EMP_NO,""),"1234");
											//Need to remove it.
											if(nameIDDO == null)
											{
												nameIDDO = new NameIDDo();
												nameIDDO.strId = "0";
												nameIDDO.strName = "1234";
											}
											if (nameIDDO != null && nameIDDO.strId!= null && nameIDDO.strId.equalsIgnoreCase("0"))
											{
												postOrder();
											}
											hideLoader();
										}
									}).start();
								}
							}
						});
					}
				}).start();
			}
		});
		
		onViewClickListners();

		if(AppConstants.hmCapturedInventory != null)
			AppConstants.hmCapturedInventory.clear();
		else 
			AppConstants.hmCapturedInventory = new HashMap<String, Vector<ProductDO>>();
		
	
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
		{
			tvNoOrder.setVisibility(View.GONE);
			calTotPrices(true);
		}
		else
			tvNoOrder.setVisibility(View.VISIBLE);
		
		etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
		if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
		{
			evTotalValue.setText(""+curencyCode+" "+"0.00");
			etDiscValue.setText(""+curencyCode+" "+"0.00");
		}
		else
		{
			etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
		}
		
		IntentFilter filterForJourney = new IntentFilter();
		filterForJourney.addAction(AppConstants.ACTION_GOTO_TELEORDERS);
		registerReceiver(GotoTeleOrders, filterForJourney);
		
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hmInventory = new OrderDetailsDA().getAvailInventoryQtys();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
					}
				});
			}
		}).start();
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
		btnConfirmOrder 	= (Button)llCapture_Inventory.findViewById(R.id.btnFinalize);
		btnAddIetem			= (Button)llCapture_Inventory.findViewById(R.id.btnAddIetem);
		btnAddIetems		= (Button)llCapture_Inventory.findViewById(R.id.btnAddItems);
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
		llPricing			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llPricing);
		llOrderVal			= (LinearLayout)llCapture_Inventory.findViewById(R.id.llOrderVal);
		
		tvOrderValue		= (TextView)llCapture_Inventory.findViewById(R.id.tvOrderValue);
		
		btnConfirmOrder.setVisibility(View.VISIBLE);
		tvNoOrder.setText("Please add items.");
		
		tvOrderValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvNoOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnConfirmOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddIetem.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddIetems.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddIetem.setTextColor(Color.WHITE);
		tvLu.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCI.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDisHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCancel1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		llTotalValue.setVisibility(View.VISIBLE);
		btnAddIetems.setVisibility(View.VISIBLE);
		etDiscValue.setVisibility(View.VISIBLE);
		tvDisHeader.setVisibility(View.VISIBLE);
		llOrderVal.setVisibility(View.VISIBLE);
		if(mallsDetails != null)
			tvLu.setText(mallsDetails.siteName.trim() +" ["+mallsDetails.site+"]"/*+ " (" +mallsDetails.partyName.trim()+")"*/);
		
		btnAddIetems.setVisibility(View.GONE);
	}
	String strErrorMessage = "";
	private void calTotPrices(boolean isLoaderShown)
	{
		strErrorMessage = "";
		if(isLoaderShown)
		showLoader(getResources().getString(R.string.loading));
		orderTPrice 		= 	0.0f;
		totalIPrice 		= 	0.0f;
		totalDiscount 	= 	0.0f;
		showLoader("Wait...");
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
						objProductDO.LineNo 			= ""+count++;
						DiscountDO temp        = new CaptureInventryDA().getCaseVAlueAndTax(objProductDO.SKU, mallsDetails.priceList, objProductDO.UOM);
						
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
							objProductDO.DiscountAmt =  StringUtils.getFloat(objDiscount.perCaseValue)*objDiscount.discount/100;
						else
							objProductDO.DiscountAmt =  objDiscount.discount;
						
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
		btnAddIetems.setOnClickListener(new OnClickListener()
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
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesManTakeReturnOrder.this);
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
		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
		final Button btnSearch = new Button(SalesManTakeReturnOrder.this);
		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesManTakeReturnOrder.this, "Select Category", true);
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
						ProductDO obj  = (ProductDO) vecSearchedItemd.get(index);
						String strText = ((ProductDO)obj).SKU;
						String strDesc = ((ProductDO)obj).Description;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDesc.toLowerCase().contains(s.toString().toLowerCase()))
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
					showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
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
							if(from != null && from.equalsIgnoreCase("replacement"))
								vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(catgId, "", "",orderedItemsList, mallsDetails.priceList, AppConstants.SALES_ORDER_TYPE, true);
							else
								vecSearchedItemd = objItemDetailBL.getProductsDetailsByCategoryId(catgId, "", "",orderedItemsList, mallsDetails.priceList, AppConstants.SALES_ORDER_TYPE, false);
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
										lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter(vecSearchedItemd,SalesManTakeReturnOrder.this, true, from, new DatePickerListner() {
											
											@Override
											public void setDate(TextView tvView, ProductDO objiItem1) 
											{
												tempView = tvView;
												tempView.setTag(objiItem1);
												showDialog(0);
											}
										}));
									}
									else
									{
										if(adapter == null)
											lvPopupList.setAdapter(adapter = new AddRecomendedItemAdapter( new Vector<ProductDO>(),SalesManTakeReturnOrder.this, true, from, new DatePickerListner() {
												
												@Override
												public void setDate(TextView tvView, ProductDO objiItem1)
												{
													tempView = tvView;
													tempView.setTag(objiItem1);
													showDialog(0);
												}
											}));
										else
											adapter.refresh(vecSearchedItemd);
										
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
					if(validateReason(veciItems))
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
						
						calTotPrices(true);
					}
					else
					{
						showCustomDialog(SalesManTakeReturnOrder.this, "Warning !", strErrorMsg, "OK", null, "");
					}
				}
				else
					showCustomDialog(SalesManTakeReturnOrder.this, "Warning !", "Please select Items.", "OK", null, "");
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
			ProductDO objItem = null;
			if(view != null)
			{
				objItem = (ProductDO) view.getTag();
			}
			if(objItem != null)
			{
				if(type.equalsIgnoreCase("Units"))
				{
					objItem.preUnits = s.toString();
					if(!objItem.reason.equalsIgnoreCase(getString(R.string.return_)))
					{
						if(!isInventoryAvail(objItem))
						{
							objItem.preUnits = "0";
							objItem.totalCases = StringUtils.getFloat(objItem.preCases);
							showToast("Entered quantity should not be greater than inventory quantity.");
							if(objItem.etUnits != null)
								objItem.etUnits.setText("");    
						}
					}
				}
				else if(type.equalsIgnoreCase("Cases"))
				{
					objItem.preCases = s.toString();
					if(!objItem.reason.equalsIgnoreCase(getString(R.string.return_)))
					{
						if(!isInventoryAvail(objItem))
						{
							objItem.preCases = "0";
							showToast("Entered quantity should not be greater than inventory quantity.");
							if(objItem.etCases != null)
								objItem.etCases.setText("");    
						}
					}
				}
			}
			
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
								objProductDO.totalPrice 	= ((objProductDO.itemPrice*objProductDO.totalCases));
								objProductDO.invoiceAmount 	= StringUtils.getFloat(objProductDO.unitSellingPrice*objProductDO.totalCases+"");
								if(objProductDO.reason.equalsIgnoreCase(getString(R.string.return_)))
								{
									tOrderVal 		+= 	objProductDO.invoiceAmount;
									tPrice 			+=	(objProductDO.totalPrice);
									tDeposit 		+=	objProductDO.depositPrice;
									tDiscVal 		+=	(objProductDO.DiscountAmt * objProductDO.totalCases);
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
								etDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
							}
						});
					}
					
					if(!Thread.interrupted())
					{
						orderTPrice	 = tPrice;
						totalIPrice 	 = tOrderVal;
						
						runOnUiThread(new  Runnable() 
						{
							public void run()
							{
								LogUtils.errorLog("totPrice", "totPrice last "+orderTPrice );
								evTotalValue.setText(""+curencyCode+" "+amountFormate.format(totalIPrice));
								tvOrderValue.setText(""+curencyCode+" "+amountFormate.format(orderTPrice));
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
				LinearLayout llClickToDownLoad			= (LinearLayout)convertView.findViewById(R.id.llClickToDownLoad);
				EditText evCases			= (EditText)convertView.findViewById(R.id.evCases);
				EditText evUnits			= (EditText)convertView.findViewById(R.id.evUnits);
				objProduct.etCases			=  evCases;
				objProduct.etUnits			=  evUnits;
				tvHeaderText.setText(objProduct.SKU);
				tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvDescription.setText(objProduct.Description);
				tvDescription.setTypeface(AppConstants.Roboto_Condensed);
				
				evCases.setTag(objProduct);
				evUnits.setTag(objProduct);
				
				
				if(StringUtils.getInt(objProduct.preCases) <= 0 )
					evCases.setText("");
				else
					evCases.setText(objProduct.preCases);
				
				if(StringUtils.getInt(objProduct.preUnits) <= 0 )
					evUnits.setText("");
				else
					evUnits.setText(objProduct.preUnits);
				
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
				llClickToDownLoad.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View arg0) 
					{
					}
				});
				
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
						handleText("","");
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
			for(int groupPosition = 0; groupPosition<vecCategoryIds.size(); groupPosition++)
			{
				String strCategory = "";
				CategoryDO objCategoryDO = null;
				if(AppConstants.hmCateogories != null && AppConstants.hmCateogories.size()>0)
					objCategoryDO = AppConstants.hmCateogories.get(vecCategoryIds.get(groupPosition));
				
				int childItemsSize = hmItems.get(vecCategoryIds.get(groupPosition)).size();
				
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
					strCategory = "Tubs";
				}
				final LinearLayout convertView		=	(LinearLayout)getLayoutInflater().inflate(R.layout.capture_inventry_layout,null);
				final LinearLayout llBottomLayout 	= (LinearLayout)convertView.findViewById(R.id.llBottomLayout);
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
		else if(from != null && (from.equalsIgnoreCase("checkINOption") || from.equalsIgnoreCase("replacement")))
		{
			finish();
		}
		else
		{
			showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Are you sure you want to cancel the return request?", getString(R.string.Yes), getString(R.string.No), "returnprocess");
		}
	}
	
	@Override
	public void onButtonNoClick(String from)
	{
		if(from.equalsIgnoreCase("captureImage"))
			showPassCodeDialog(null, from, false);
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("validate"))
		{
			Intent intent = new Intent(SalesManTakeReturnOrder.this, SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Recommended_Order) );
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("captureImage"))
		{
			Intent objIntent = new Intent(SalesManTakeReturnOrder.this, CaptureDamagedItemImage.class);
			objIntent.putExtra("vecImagePaths", productDOImage.vecDamageImages);
			objIntent.putExtra("position", mPosition);
			objIntent.putExtra("fromActivity", true);
			objIntent.putExtra("itemCode", productDOImage.SKU);
			objIntent.putExtra("desc", productDOImage.Description);
			startActivityForResult(objIntent, 500);
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
					showCustomDialog(SalesManTakeReturnOrder.this,getString(R.string.successful),getString(R.string.your_recommended_order_printed), getString(R.string.OK),null,"print");
				}
			}, 1000);
		}
		else if(from.equalsIgnoreCase("returnprocess"))
		{
			if(isAdvance)
			{
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_GOTO_CRLMAIN);
				sendBroadcast(intentBrObj);
			}
			else
			{
				Intent intentBrObj = new Intent();
    			intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST);
    			sendBroadcast(intentBrObj);
			}
		}
		else if(from.equalsIgnoreCase("servedtemp"))
		{
			orderId = orderDO.OrderId;
			Intent intent = new Intent(SalesManTakeReturnOrder.this, ReturnOrderActivity.class);
			intent.putExtra("object", mallsDetails);
			intent.putExtra("orderId", orderId);
			startActivity(intent);
			finish();
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 200)
		{
			if(AppConstants.hmCapturedInventory != null && adapterForCapture != null)
			{
				adapterForCapture.refresh(AppConstants.hmCapturedInventory);
				calTotPrices(false);
				tvNoOrder.setVisibility(View.GONE);
			}
			else
				tvNoOrder.setVisibility(View.VISIBLE);
		}
		else if(resultCode == 500)
    	{
			if(data != null)
			{
				int position 		= data.getExtras().getInt("position");
				ArrayList<String> vecImagePaths   = (ArrayList<String>) data.getExtras().get("vecImagePaths");
				boolean fromActivity = data.getExtras().getBoolean("fromActivity");
				
				if(fromActivity)
				{
					Vector<ProductDO> vecProductDOs = AppConstants.hmCapturedInventory.get(strKeyNew);
					vecProductDOs.get(mPosition).vecDamageImages = vecImagePaths;
					AppConstants.hmCapturedInventory.put(strKeyNew, vecProductDOs);
				}
				else
				{
					if(adapter != null)
					{
						adapter.setItems(position, vecImagePaths);
					}
				}
			}
    	}
		else if(resultCode == 20000)
    	{
			showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.successful), getString(R.string.your_recommended_order_printed), getString(R.string.OK), null , "");
    	}
		else if(resultCode == 10000)
		{
			finish();
		}
	}
	
	private boolean getAddedItemAvailability(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isAvail = false;
		
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
						isAvail = true;
						break;
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
			objItem.BatchCode 	  = inventryDO.batchCode;
			
			float availQty = inventryDO.totalQt;
			
			if(objItem.totalUnits > availQty)
				isAvail = false;
			else
				isAvail = true;
		}
		else
			isAvail = false;
		
		return isAvail;
	}
	
	private boolean validateReason(Vector<ProductDO> veciItems)
	{
		strErrorMsg = "Please select reason for the item(s) ";
		boolean isFound = false;
		int count = -1;
		int count1 = -1;
		int count2 = -1;
		for(int i= 0; i< veciItems.size(); i++)
		{
			if(veciItems.get(i).reason.equalsIgnoreCase(""))
			{
				count++;
				isFound = true;
				if(count == 0)
				{
					strErrorMsg = strErrorMsg +"'"+veciItems.get(i).SKU;
				}
				else
					strErrorMsg = strErrorMsg +"\n"+veciItems.get(i).SKU;
			}
		}
		if(isFound)
		{
			strErrorMsg = strErrorMsg +"'.";
			return false;
		}
		else
		{
			strErrorMsg = "";
			String string = "";
			String string1 = "";
			isFound = false;
			count1 = -1;
			count2 = -1;
			for(int i= 0; i< veciItems.size(); i++)
			{
				if((veciItems.get(i).reason.equalsIgnoreCase(getString(R.string.ReturnReason1)) && veciItems.get(i).strExpiryDate.equalsIgnoreCase(""))
						|| (veciItems.get(i).reason.equalsIgnoreCase(getString(R.string.ReturnReason2)) && veciItems.get(i).strExpiryDate.equalsIgnoreCase(""))
						|| (veciItems.get(i).reason.equalsIgnoreCase(getString(R.string.return_)) && veciItems.get(i).strExpiryDate.equalsIgnoreCase("")))
				{
					count1++;
					isFound = true;
					if(count1 == 0)
					{
						string = string +"'"+veciItems.get(i).SKU;
					}
					else
						string = string +"\n"+veciItems.get(i).SKU;
				}
				if((veciItems.get(i).reason.equalsIgnoreCase(getString(R.string.return_)) && veciItems.get(i).strExpiryDate.equalsIgnoreCase("")))
				{
					count2++;
					isFound = true;
					if(count2 == 0)
					{
						string1 = string1 +"'"+veciItems.get(i).SKU;
					}
					else
						string1 = string1 +"\n"+veciItems.get(i).SKU;
				}
			}
			if(isFound)
			{
				if(!string1.equals("") && !string.equals(""))
				{
					if(strErrorMsg != null && strErrorMsg.length() > 0)
						strErrorMsg = strErrorMsg +" and enter expiry date for the item(s) "+string+"'.";
					else
						strErrorMsg = strErrorMsg+"Please enter expiry date for the item(s) "+string+"'.";
				}
				else if(!string.equals(""))
					strErrorMsg = strErrorMsg +"Please enter expiry date for the item(s) "+string+"'.";
				else
					strErrorMsg = strErrorMsg+".";
				
				return false;
			}
			else 
				return true;
		}
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id) 
	  {
		  //getting current dateofJorney from Calendar
		  Calendar c 	= 	Calendar.getInstance();
		  c.add(Calendar.DAY_OF_MONTH, 0);
		  
		  cyear 	= 	c.get(Calendar.YEAR);
		  cmonth 	= 	c.get(Calendar.MONTH);
		  cday 		=	c.get(Calendar.DAY_OF_MONTH);
		    
		  return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	  }
		/** method for dateofJorney picker **/
	  private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener(){
		  public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		  {
			  Calendar currentCal = Calendar.getInstance();
			  Calendar tempCal 	= Calendar.getInstance();
			  tempCal.set(year, monthOfYear, dayOfMonth);
		    	
//			  if(!currentCal.after(tempCal) && tempCal.compareTo(currentCal) > 0)
//			  {
				  if(tempView != null)
				  {
					  String strDate = "", strMonth ="";
					  if(monthOfYear <9)
						  strMonth = "0"+(monthOfYear+1);
					  else
						  strMonth = ""+(monthOfYear+1);
			    		
					  if(dayOfMonth <10)
						  strDate = "0"+(dayOfMonth);
					  else
						  strDate = ""+(dayOfMonth);
					  
					  ProductDO objiItem = (ProductDO) tempView.getTag();
					  
					  if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason1)) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) < 0)
					  {
						  objiItem.strExpiryDate = "";
						  ((TextView)tempView).setText("");
						  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Expired date can not be future date.", "Ok", null, "");
					  }
					  else if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason2)) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
					  {
						  objiItem.strExpiryDate = "";
						  ((TextView)tempView).setText("");
						  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be past date", "Ok", null, "");
					  }
					  else if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason2)) && Math.abs(CalendarUtils.getDateDiffInInt(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime())) >=30)
					  {
						  objiItem.strExpiryDate = "";
						  ((TextView)tempView).setText("");
						  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be more than one month.", "Ok", null, "");
					  }
					  else if((objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason3)) || objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.return_))) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
					  {
						  objiItem.strExpiryDate = "";
						  ((TextView)tempView).setText("");
						  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Expiry date can not be past date.", "Ok", null, "");
					  }
					  else
					  {
						  ((TextView)tempView).setText(year+"-"+strMonth+"-"+strDate);
						  objiItem.strExpiryDate = year+"-"+strMonth+"-"+strDate;
						  
						  if(adapter != null)
							  adapter.setExpiryDate(objiItem);
					  }
				  }
//			  }
//			  else
//			  {
//				  showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Selected date should be greater than current date.", getString(R.string.OK), null, "");
//			  }
		  }
	  };
	  
	  public interface DatePickerListner
	  {
		  public void setDate(TextView tvView, ProductDO objiItem);
	  }
	  
	  private boolean isAmountValid()
	  {
		  boolean isValid = true;
		  if(orderId != null && orderId.length() > 0 && totalInvoicedPrice > 0)
		  {
			  if(totalIPrice > totalInvoicedPrice)
				  isValid = false;
		  }
		  
		  return isValid;
	  }
	  
	private boolean checkImageValidation(HashMap<String, Vector<ProductDO>> hmCapturedInventory)
	{
		boolean isTrue = true;
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size()>0)
		{
			Set<String> set = AppConstants.hmCapturedInventory.keySet();
			for(String strKey : set)
			{
				Vector<ProductDO> vecOrderedProduct = AppConstants.hmCapturedInventory.get(strKey);
				if(vecOrderedProduct != null && vecOrderedProduct.size() > 0)
				{
					int count = 0;
					for(ProductDO objProductDO : vecOrderedProduct)
					{
						if(objProductDO.vecDamageImages == null || (StringUtils.getFloat(objProductDO.preUnits) > objProductDO.vecDamageImages.size()))
						{
							productDOImage = objProductDO;
							strKeyNew = strKey;
							mPosition = count;
							showCustomDialog(SalesManTakeReturnOrder.this, "Alert!", "You have not captured image(s) of item "+objProductDO.SKU+". Please tap on OK button to capture image(s).", "OK", "Skip", "captureImage");
							isTrue  = false;
							break;
						}
						count++;
					}
				}
			}
		}
		return isTrue;
	}
	 
	@Override
	public void performPasscodeAction(NameIDDo nameIDDo, String fromText, boolean isCheckOut)
	{
//		Intent intent = new Intent(SalesManTakeReturnOrder.this, SalesmanReturnOrderPreview.class);
//		intent.putExtra("isReturnRequest", true);
//		intent.putExtra("TotalPrice", evTotalValue.getText().toString().trim());
//		intent.putExtra("Discount", etDiscValue.getText().toString().trim());
//		intent.putExtra("from", from);	
//		intent.putExtra("isTask", isTask);	
//		intent.putExtra("isMenu", isMenu);
//		intent.putExtra("mallsDetails",mallsDetails);
//		
//		if(orderId != null && orderId.length() > 0)
//		{
//			intent.putExtra("invoicenum", orderId);
//			intent.putExtra("invoiceamt", totalInvoicedPrice);
//		}
////		startActivityForResult(intent, 1000);
//		startActivity(intent);
//		finish();
		
		
//		Intent intent = new Intent(SalesManTakeReturnOrder.this, ReturnOrderActivity.class);
//		intent.putExtra("object", mallsDetails);
//		intent.putExtra("orderId", orderId);
//		startActivity(intent);
//		finish();
	}
	
	boolean isSalesOrderGerenaterd = false;
	
	private void postOrder()
	{
		showLoader(getString(R.string.please_wait));
			if(mallsDetails.currencyCode == null || mallsDetails.currencyCode.length() <= 0)
				mallsDetails.currencyCode = curencyCode;
			
			//getting all the values for Order Table
			OrderDO orderDO 				= 	new OrderDO();
			orderDO.BalanceAmount 	= 	StringUtils.getFloat(evTotalValue.getText().toString().trim());
			orderDO.CustomerSiteId 	= 	mallsDetails.site;
			orderDO.strCustomerName	= 	mallsDetails.siteName;
			orderDO.DeliveryAgentId = 	"1";
			orderDO.DeliveryStatus 	= 	"E";
			orderDO.Discount 		= 	StringUtils.getFloat(etDiscValue.getText().toString().trim());
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
			
			
			showLoader(getResources().getString(R.string.please_wait_order_pushing));
			
			
			orderDO.OrderId = ""+new OrderDA().insertOrderDetails_PromoNoOffer(orderDO, getMainProductsVector(), preference);
			
			
			if(orderDO.OrderId != null && !orderDO.OrderId.trim().equalsIgnoreCase(""))
			{
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
						Intent intent = new Intent(SalesManTakeReturnOrder.this, ReturnOrderActivity.class);
						intent.putExtra("object", mallsDetails);
						startActivity(intent);
						finish();
					}
					else
						showCustomDialog(SalesManTakeReturnOrder.this, getString(R.string.warning), "Error occurred while taking order.", getString(R.string.OK), null, "");
				}
			});
	}
	
	
	private Vector<ProductDO> getMainProductsVector()
	{
			
		Vector<ProductDO> vecMainProducts = new Vector<ProductDO>();

		Vector<String> vecCategoryIds = new Vector<String>();
		if(AppConstants.hmCapturedInventory == null || AppConstants.hmCapturedInventory.size() == 0)
		{
			hideLoader();
			return null;
		}
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
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				
				hideLoader();
			}
		});
		
		return vecMainProducts;
	}
}