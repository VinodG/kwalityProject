package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.Add_new_SKU_Dialog;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.TrxDA;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesmanAddedItemCart extends BaseActivity 
{
	
	private LinearLayout llCart;
	private Button btnAddItem,btnPreviewOrder;
	private CartAdapter cartAdapter;
	private AddNewItemAdapter adapter;
	private ExpandableListView expandableListView;
	private HashMap<String,Vector<TrxDetailsDO>> hmCartDetails  = new HashMap<String, Vector<TrxDetailsDO>>();
	private Vector<String> vecHeaders = new Vector<String>();
	private TextView tvAgenciesName,tvHeader;
	private int listScrollState;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private HashMap<String, TrxDetailsDO> hmDistinctModifiedItem;
	private HashMap<String,String> hmBrandDetails;
	private TextView tvDiscValue,tvTotalValue,tvOrderValue;
	private Vector<TrxDetailsDO> vecSearchedItemd;
	private String orderedItems ="", orderedItemsList;
	private EditText edtSearch;
	private HashMap<String, UOMConversionDO> hashUomConveriosn=new HashMap<String, UOMConversionDO>();
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
	
	@Override
	public void initialize() 
	{
		llCart = (LinearLayout)inflater.inflate(R.layout.salesman_cart, null);
		llBody.addView(llCart,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		intializeControls();
		getCartDetails();
		
		btnAddItem.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showAddNewSkuPopUp();
			}
		});
		
		btnPreviewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						final TrxHeaderDO trxHeaderDO = prepareSalesOrder();
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(trxHeaderDO != null)
								{
									Intent intent = new Intent(SalesmanAddedItemCart.this, SalesmanOrderPreview.class);
									intent.putExtra("trxHeaderDO", trxHeaderDO);
									intent.putExtra("mallsDetails",mallsDetailss);
									startActivityForResult(intent, 1000);
								}
								else
									showCustomDialog(SalesmanAddedItemCart.this, getString(R.string.warning), "Please select atleast one item having quantity more than 0.", getString(R.string.OK), null, "");
								
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() 
									{
										btnPreviewOrder.setEnabled(true);
										btnPreviewOrder.setClickable(true);
									}
								}, 200);
								hideLoader();
							}
						});
					}
				}).start();
				
			}
		});
		
		expandableListView.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				Log.e("scrollState", ""+scrollState);
				listScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				
			}
		});
		
		edtSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
			{
			}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				getSearchItems(s.toString());
			}
		});
		
	}
	
	public void getSearchItems(final String searchText) 
	{
		Log.d("edtSearch", edtSearch.getText().toString());
			
		HashMap<String, Vector<TrxDetailsDO>> tmpSearched = new HashMap<String, Vector<TrxDetailsDO>>();
		for(String key:hmCartDetails.keySet())
		{
			if(!TextUtils.isEmpty(searchText)){
				Predicate<TrxDetailsDO> searchItem = new Predicate<TrxDetailsDO>() {
					public boolean apply(TrxDetailsDO trxDetailsDO) {
						return trxDetailsDO.itemDescription.toLowerCase().contains(searchText.toLowerCase())||
								trxDetailsDO.itemCode.toLowerCase().contains(searchText.toLowerCase());
					}
				};
				Collection<TrxDetailsDO> filteredResult = filter(hmCartDetails.get(key),
						searchItem);
				if(filteredResult!=null && filteredResult.size()>0){
					tmpSearched.put(key, new Vector<TrxDetailsDO>((ArrayList<TrxDetailsDO>)filteredResult));
				}
			}else{
				tmpSearched = hmCartDetails;
			}
		}
			
		if(cartAdapter != null)
			cartAdapter.refresh(tmpSearched,hmBrandDetails);
		
	}
	
	Vector<String> vecCategory = new Vector<String>();
	
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesmanAddedItemCart.this);
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
		
		new CategoriesDA().getCategoryList();
		
		for(int i=0; AppConstants.vecCategories != null && i < AppConstants.vecCategories.size(); i++)
			vecCategory.add(AppConstants.vecCategories.get(i).categoryName);
		
//		if(vecCategory.size()==0)
//			vecCategory = new CategoriesDA().getAllCategoryName();
		
		final Button btnSearch = new Button(SalesmanAddedItemCart.this);
		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(SalesmanAddedItemCart.this, "Select Category", true);
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
					Vector<TrxDetailsDO> vecTemp = new Vector<TrxDetailsDO>();
					for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
					{
						TrxDetailsDO obj  = (TrxDetailsDO) vecSearchedItemd.get(index);
						String strText = obj.itemCode;
						String strDesc = obj.itemDescription;
						
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
				InputMethodManager inputManager =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(tvCategory.getApplicationWindowToken() ,InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(tvCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(SalesmanAddedItemCart.this, getString(R.string.warning), "Category field should not be empty.", getString(R.string.OK), null, "search");
				}
				else
				{
					if(vecSearchedItemd == null)
						vecSearchedItemd = new Vector<TrxDetailsDO>();
					else
						vecSearchedItemd.clear();
					orderedItems ="";
					orderedItemsList = "";
					if(hmDistinctModifiedItem!=null)
					{
						Set<String> set = hmDistinctModifiedItem.keySet();
						Iterator<String> iterator = set.iterator();
						while(iterator.hasNext())
						{
							orderedItems = orderedItems + "'"+iterator.next()+"',";
						}
						
						if(orderedItems.contains(","))
							orderedItemsList = orderedItems.substring(0, orderedItems.lastIndexOf(","));
						else
							orderedItemsList = orderedItems;
					}
					
					final CaptureInventryDA objItemDetailBL = new CaptureInventryDA(); 
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
							
							vecSearchedItemd = objItemDetailBL.getReturnItems(catgId, orderedItemsList, mallsDetailss, false,"",0);
							
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
										lvPopupList.setAdapter(adapter = new AddNewItemAdapter(vecSearchedItemd,SalesmanAddedItemCart.this));
									}
									else
									{
										if(adapter == null)
											lvPopupList.setAdapter(adapter = new AddNewItemAdapter( new Vector<TrxDetailsDO>(),SalesmanAddedItemCart.this));
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
				Vector<TrxDetailsDO> veciItems= new Vector<TrxDetailsDO>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(veciItems != null && veciItems.size() > 0)
				{
					if(hmCartDetails == null)
						hmCartDetails = new HashMap<String, Vector<TrxDetailsDO>>();
					
					for(int i=0; veciItems != null && i < veciItems.size(); i++)
					{
						TrxDetailsDO objProduct = veciItems.get(i);
						Vector<TrxDetailsDO> vecProducts = hmCartDetails.get(objProduct.categoryId);
						if(vecProducts == null)
						{
							vecProducts = new Vector<TrxDetailsDO>(); 
							vecProducts.add(objProduct);
							hmBrandDetails.put(objProduct.categoryId, objProduct.categoryName);
							hmCartDetails.put(objProduct.categoryId, vecProducts);
							vecHeaders.add(objProduct.categoryId);
						}
						else
							vecProducts.add(objProduct);
						hmDistinctModifiedItem.put(objProduct.itemCode,objProduct);
						
					}
					preference.saveBooleanInPreference("isItemAdded", true);
					preference.commitPreference();
			
					if(cartAdapter != null)
						cartAdapter.refresh(hmCartDetails,hmBrandDetails);
					
					calculatePrice();
					objAddNewSKUDialog.dismiss();
				}
				else
					showCustomDialog(SalesmanAddedItemCart.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		checkCartSaveAvailability();
	}
	
	private void checkCartSaveAvailability()
	{
		showLoader("Saving data in cart...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final TrxHeaderDO trxHeaderDO = prepareCartOrder();
				if(trxHeaderDO != null)
					new OrderDA().insertCartOrderDetails_Promo(trxHeaderDO);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						finish();
					}
				});
			}
		}).start();
	}
	
	
	private void intializeControls() 
	{
		tvHeader			= (TextView)llCart.findViewById(R.id.tvHeader);
		tvAgenciesName		= (TextView)llCart.findViewById(R.id.tvAgenciesName);
		btnAddItem	 		= (Button) llCart.findViewById(R.id.btnAddItem);
		btnPreviewOrder	 	= (Button) llCart.findViewById(R.id.btnPreviewOrder);
		edtSearch			= (EditText)llCart.findViewById(R.id.etSearch);
		tvOrderValue		= (TextView) llCart.findViewById(R.id.tvOrderValue);
		tvDiscValue			= (TextView) llCart.findViewById(R.id.tvDiscValue);
		tvTotalValue		= (TextView) llCart.findViewById(R.id.tvTotalValue);
		
		expandableListView	= (ExpandableListView) llCart.findViewById(R.id.expandableList);
		cartAdapter			= new CartAdapter();
		expandableListView.setAdapter(cartAdapter);
		setTypeFaceRobotoNormal(llCart);
		
		tvAgenciesName.setText(mallsDetailss.siteName+" ["+mallsDetailss.site+"]");
		tvAgenciesName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	private void getCartDetails()
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				Object obj[]=new CaptureInventryDA().getPriceAndUOMConversion(mallsDetailss.priceList);
				hashUomConveriosn = (HashMap<String, UOMConversionDO>) obj[0];
				hashMapPricing = (HashMap<String, HashMap<String, Float>>) obj[1];
				
				hmInventory  	  		  =  new OrderDetailsDA().getAvailInventoryQtys();
				Object[] objects 		  =  new TrxDA().getCartDetails(mallsDetailss.site,hashUomConveriosn,hmInventory);
				
				hmCartDetails	  		  = (HashMap<String, Vector<TrxDetailsDO>>) objects[0];
				hmDistinctModifiedItem	  = (HashMap<String, TrxDetailsDO>) objects[1];
				hmBrandDetails			  = (HashMap<String, String>) objects[2];
			
				runOnUiThread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						cartAdapter.refresh(hmCartDetails,hmBrandDetails);
						calculatePrice();
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	
	private TrxHeaderDO prepareCartOrder() 
	{
		TrxHeaderDO trxHeaderDO = null;
		if(hmCartDetails == null || hmCartDetails.size() <= 0)
			trxHeaderDO = null;
		else
		{
			ArrayList<TrxDetailsDO> arrTRXDetail = new ArrayList<TrxDetailsDO>();
			Set<String> keys = hmCartDetails.keySet();
			for(String key : keys)
			{
				Vector<TrxDetailsDO> vecDetails = hmCartDetails.get(key);
				
				for(TrxDetailsDO trxDetailsDO : vecDetails)
				{
					if(trxDetailsDO.requestedSalesBU > 0 && trxDetailsDO.productStatus != 2)
						arrTRXDetail.add(trxDetailsDO);
				}
			}
			
			if(arrTRXDetail.size() > 0)
			{
				trxHeaderDO = new TrxHeaderDO();
				trxHeaderDO.trxCode         =  mallsDetailss.site;
				trxHeaderDO.preTrxCode	 	=  mallsDetailss.site;
				trxHeaderDO.appTrxId 		=  StringUtils.getUniqueUUID();
				trxHeaderDO.clientBranchCode=  mallsDetailss.site;
				trxHeaderDO.clientCode		=  mallsDetailss.site;
				trxHeaderDO.currencyCode    =  AppConstants.CURRECNY_CODE;
				trxHeaderDO.deliveryDate	=  CalendarUtils.getCurrentDate();
				trxHeaderDO.journeyCode		=  mallsDetailss.JourneyCode;
				trxHeaderDO.orgCode			=  preference.getStringFromPreference(Preference.ORG_CODE, "");
				trxHeaderDO.paymentType		=  mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) ? 1 : 0;//need to check
				trxHeaderDO.printingTimes	=  0;
				trxHeaderDO.status			=  0;
				trxHeaderDO.totalAmount		=  orderTPrice;
				trxHeaderDO.totalDiscountAmount = totalDiscount;
				trxHeaderDO.trxStatus		= 0;
				trxHeaderDO.trxType			= trxHeaderDO.get_TRXTYPE_CART();
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");;
				trxHeaderDO.visitCode		= mallsDetailss.VisitCode;
				
				trxHeaderDO.trxDate 	= CalendarUtils.getCurrentDateTime();
				trxHeaderDO.deliveryDate= trxHeaderDO.trxDate;
				trxHeaderDO.createdOn	= trxHeaderDO.trxDate;
				trxHeaderDO.salesmanSignature = "";
				trxHeaderDO.clientSignature = "";
				
				trxHeaderDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
		}
		return trxHeaderDO;
	}
	
	
	private TrxHeaderDO prepareSalesOrder() 
	{
		TrxHeaderDO trxHeaderDO = null;
		if(hmDistinctModifiedItem == null || hmDistinctModifiedItem.size() <= 0)
			trxHeaderDO = null;
		else
		{
			ArrayList<TrxDetailsDO> arrTRXDetail = new ArrayList<TrxDetailsDO>();
			Set<String> keys = hmDistinctModifiedItem.keySet();
			for(String key : keys)
			{
				TrxDetailsDO trxDetailsDO = hmDistinctModifiedItem.get(key);
				if(trxDetailsDO.requestedSalesBU > 0 && trxDetailsDO.productStatus != 2)
					arrTRXDetail.add(trxDetailsDO);
			}
			
			if(arrTRXDetail.size() > 0)
			{
				trxHeaderDO = new TrxHeaderDO();
				trxHeaderDO.appTrxId 		=  StringUtils.getUniqueUUID();
				trxHeaderDO.clientBranchCode=  mallsDetailss.site;
				trxHeaderDO.clientCode		=  mallsDetailss.site;
				trxHeaderDO.currencyCode    =  AppConstants.CURRECNY_CODE;
				trxHeaderDO.deliveryDate	=  CalendarUtils.getCurrentDate();
				trxHeaderDO.journeyCode		=  mallsDetailss.JourneyCode;
				trxHeaderDO.orgCode			=  preference.getStringFromPreference(Preference.ORG_CODE, "");
				trxHeaderDO.paymentType		=  mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) ? 1 : 0;//need to check
				trxHeaderDO.printingTimes	=  0;
				trxHeaderDO.status			=  0;
				trxHeaderDO.totalAmount		=  orderTPrice;
				trxHeaderDO.totalDiscountAmount = totalDiscount;
				trxHeaderDO.trxStatus		= 0;
				trxHeaderDO.trxType			= trxHeaderDO.get_TRXTYPE_SALES_ORDER();
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");;
				trxHeaderDO.visitCode		= mallsDetailss.VisitCode;
				trxHeaderDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
		}
		return trxHeaderDO;
	}
	
	
	float orderTPrice, totalIPrice, totalDiscount;
	private synchronized void  calculatePrice()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				orderTPrice 		= 	0.0f;
				totalIPrice 		= 	0.0f;
				totalDiscount 		= 	0.0f;
				
				Set<String> set = hmDistinctModifiedItem.keySet();
				int count = 1;
				for(String key : set)
				{
					TrxDetailsDO trxDetailsDO = hmDistinctModifiedItem.get(key);
					trxDetailsDO.lineNo 	  = (int) count++;
					trxDetailsDO.itemType     = ""+AppConstants.ITEM_TYPE_ORDER;
				
					float price = trxDetailsDO.basePrice;
					float quanity=0.0f;
					if(hashMapPricing.containsKey(trxDetailsDO.itemCode)){
						price = hashMapPricing.get(trxDetailsDO.itemCode).get(trxDetailsDO.UOM);//getting price of selected UOM
					}
					/*switch (trxDetailsDO.uomLevelUsed) {
					case TrxDetailsDO.ITEM_UOM_LEVELINT1: {
						trxDetailsDO.priceUsedLevel1 = price;
						trxDetailsDO.priceUsedLevel2 = 0.0f;
						trxDetailsDO.priceUsedLevel3 = 0.0f;
						quanity = trxDetailsDO.quantityLevel1;
					}

						break;
					case TrxDetailsDO.ITEM_UOM_LEVELINT2: {
						trxDetailsDO.priceUsedLevel1 = 0.0f;
						trxDetailsDO.priceUsedLevel2 = price;
						trxDetailsDO.priceUsedLevel3 = 0.0f;
						quanity = trxDetailsDO.quantityLevel2;
					}

						break;
					case TrxDetailsDO.ITEM_UOM_LEVELINT3: {
						trxDetailsDO.priceUsedLevel1 = 0.0f;
						trxDetailsDO.priceUsedLevel2 = 0.0f;
						trxDetailsDO.priceUsedLevel3 = price;
						quanity = trxDetailsDO.quantityLevel3;
					}

						break;
					case -1: {
						if (trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO
								.getItemUomLevel1())) {
							trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
							trxDetailsDO.priceUsedLevel1 = price;
							trxDetailsDO.priceUsedLevel2 = 0.0f;
							trxDetailsDO.priceUsedLevel3 = 0.0f;
							quanity = trxDetailsDO.quantityLevel1;
						} else if (trxDetailsDO.UOM
								.equalsIgnoreCase(TrxDetailsDO
										.getItemUomLevel2())) {
							trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
							trxDetailsDO.priceUsedLevel1 = 0.0f;
							trxDetailsDO.priceUsedLevel2 = price;
							trxDetailsDO.priceUsedLevel3 = 0.0f;
							quanity = trxDetailsDO.quantityLevel2;
						} else if (trxDetailsDO.UOM
								.equalsIgnoreCase(TrxDetailsDO
										.getItemUomLevel3())) {
							trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;
							trxDetailsDO.priceUsedLevel1 = 0.0f;
							trxDetailsDO.priceUsedLevel2 = 0.0f;
							trxDetailsDO.priceUsedLevel3 = price;
							quanity = trxDetailsDO.quantityLevel3;
						}
					}
						break;
					default:
						break;
					}*/
					
//					DiscountDO objDiscount = new CaptureInventryDA().getDisocunt(mallsDetails.site, objProductDO.CategoryId, objProductDO.SKU);
					
//					if(objDiscount.discountType == AppConstants.DISCOUNT_PERCENTAGE)
//						objProductDO.discountAmount = objProductDO.totalCases * StringUtils.getFloat(objDiscount.perCaseValue) * (objDiscount.discount/100);
//					else
//						objProductDO.discountAmount = objProductDO.totalCases * objDiscount.discount;
					
					orderTPrice 		+= 	(price * quanity);
					totalDiscount 		+=	trxDetailsDO.totalDiscountAmount;
					totalIPrice 		+= 	(price * quanity);
				}
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(curencyCode==null||curencyCode.equalsIgnoreCase(""))
							curencyCode = "AED";
						tvDiscValue.setText(""+curencyCode+" "+amountFormate.format(totalDiscount));
						tvTotalValue.setText(""+curencyCode+" "+amountFormate.format(totalIPrice));
						tvOrderValue.setText(""+curencyCode+" "+amountFormate.format(orderTPrice));		
					}
				});
			}
		}).start();
	}
	
	public void updateDistinctItem(TrxDetailsDO objItem)
	{
		if(objItem.requestedSalesBU <= 0)
			hmDistinctModifiedItem.remove(objItem.itemCode);
		else
			hmDistinctModifiedItem.put(objItem.itemCode, objItem);
		
		calculatePrice();
	}
	
	public int isInventoryAvail(TrxDetailsDO objItem,int quanity)
	{
		int missedQTY = 0;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.itemCode))
		{
			HHInventryQTDO inventryDO = hmInventory.get(objItem.itemCode);
			objItem.expiryDate = inventryDO.expiryDate;
			objItem.batchCode 	  = inventryDO.batchCode;
			
			float availQty = inventryDO.totalQt;
			if(quanity > availQty)
			{
				missedQTY = (int) (quanity - availQty);
				hmInventory.get(objItem.itemCode).tempTotalQt = 0;
			}
			else
				hmInventory.get(objItem.itemCode).tempTotalQt = quanity;
		}
		else
			missedQTY = quanity;
		
		return missedQTY;
	}
	
	
	public class CartAdapter extends BaseExpandableListAdapter {
		
		private View view;
		private HashMap<String, Vector<TrxDetailsDO>> hmCartDetails;
		private HashMap<String, String> hmBrandDetails;
		
		class TextChangeListener implements TextWatcher {

			public TextChangeListener() {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

					try {
						if(listScrollState == 0)
							handleText(s,null);
					} catch (Exception e) {
						e.printStackTrace();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		}


		private void handleText(CharSequence s,TrxDetailsDO trxDetailsDO) {

			synchronized (MyApplication.SALES_UNITS_LOCK) {
				TrxDetailsDO objItem = null;
				if (view != null) {
					objItem = (TrxDetailsDO) view.getTag(R.string.key_product);
				}
				else if(trxDetailsDO!=null)
					objItem = trxDetailsDO;
				if (objItem != null)
				{
					if(s==null)
						s="";
					objItem.requestedSalesBU = StringUtils.getFloat(s.toString());
					UOMConversionDO uomConversionDO =getUomConversionFactor(objItem.itemCode);
					int quanity=0;
					/*if(objItem.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel1())){
						objItem.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
						if(uomConversionDO!=null)
							quanity =(int) (uomConversionDO.conversion13*objItem.requestedSalesBU);
						objItem.missedBU = isInventoryAvail(objItem,quanity);
						if(uomConversionDO!=null && uomConversionDO.conversion13!=0)
							objItem.quantityLevel1 = (quanity-objItem.missedBU)/uomConversionDO.conversion13;
						else
							objItem.quantityLevel1 = (quanity-objItem.missedBU);
						objItem.quantityLevel2=0;
						objItem.quantityLevel3=0;
						
					}else if(objItem.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel2())){
						objItem.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
						if(uomConversionDO!=null)
							quanity =(int) (uomConversionDO.conversion12*objItem.requestedSalesBU);
						objItem.missedBU = isInventoryAvail(objItem,quanity);
						if(uomConversionDO!=null && uomConversionDO.conversion12!=0)
							objItem.quantityLevel2 = (quanity-objItem.missedBU)/uomConversionDO.conversion12;
						else
							objItem.quantityLevel2 = (quanity-objItem.missedBU);
						
						objItem.quantityLevel1=0;
						objItem.quantityLevel3=0;
					}else if(objItem.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel3())){
						objItem.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;
						quanity =(int) objItem.requestedSalesBU;
						objItem.missedBU = isInventoryAvail(objItem,quanity);
						objItem.quantityLevel3 = quanity - objItem.missedBU;
						objItem.quantityLevel1=0;
						objItem.quantityLevel2=0;
					}*/
					
					objItem.quantityBU = quanity-objItem.missedBU;
					
					
					updateDistinctItem(objItem);
				}
			}
		}


		@Override
		public int getGroupCount() {
			return vecHeaders.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			
			if(hmCartDetails!=null && hmCartDetails.containsKey(vecHeaders.get(groupPosition)))
				return hmCartDetails.get(vecHeaders.get(groupPosition)).size();
			
			return 0;
		}
		
		public void refresh(HashMap<String, Vector<TrxDetailsDO>> hmCartDetails, HashMap<String, String> hmBrandDetails)
		{
			this.hmCartDetails = hmCartDetails;
			this.hmBrandDetails = hmBrandDetails;
			initializeHeader();
			this.notifyDataSetChanged();
		}

		public void initializeHeader(){
			for(String key:hmBrandDetails.keySet())
			{
				if(!vecHeaders.contains(key))
					vecHeaders.add(key);
			}
			
		}
		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			if(convertView == null)
			convertView = inflater .inflate(R.layout.list_brand_group_item, null);
			TextView tvBrandName = (TextView) convertView .findViewById(R.id.tvBrandName);
			tvBrandName.setText(""+hmBrandDetails.get(vecHeaders.get(groupPosition)));
			tvBrandName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			expandableListView.expandGroup(groupPosition);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			final TrxDetailsDO objItem = hmCartDetails.get(vecHeaders.get(groupPosition)).get(childPosition);
			
			if(convertView == null)
				convertView = getLayoutInflater().inflate(R.layout.preview_order_list_cell, null);
			
			

			TextView tvOrderedItemName 	 	= (TextView)convertView.findViewById(R.id.tvOrderedItemName);
			TextView tvOrderedItemDesc 	 	= (TextView)convertView.findViewById(R.id.tvOrderedItemDesc);
			EditText etOrderedQuantity 	 	= (EditText)convertView.findViewById(R.id.etOrderedQuantity);
			final CustomEditText etQuantity 		= (CustomEditText)convertView.findViewById(R.id.etQuantity);
			
			EditText etPrice 	 		 = (EditText)convertView.findViewById(R.id.etPrice);
			EditText etTotalPrice 	 	 = (EditText)convertView.findViewById(R.id.etTotalPrice);
			EditText etInvoiceAmount 	 = (EditText)convertView.findViewById(R.id.etInvoiceAmount);
			EditText etDiscount 	 	 = (EditText)convertView.findViewById(R.id.etDiscount);
			EditText etItemUOM 	 		 = (EditText)convertView.findViewById(R.id.etItemUOM);
			etItemUOM.setText(objItem.UOM);
			etItemUOM.setTag(objItem.UOM);
			TextView tvOrderedItemMeasurment 	 = (TextView)convertView.findViewById(R.id.tvOrderedItemMeasurment);
			TextView tvOrderedItemDesc2 	 	= (TextView)convertView.findViewById(R.id.tvOrderedItemDesc2);
			ImageView ivSep						= (ImageView)convertView.findViewById(R.id.ivSep);
			ImageView ivImage					= (ImageView)convertView.findViewById(R.id.ivImage);
			
			ivSep.setVisibility(View.VISIBLE);
			
			if(objItem.itemType.equalsIgnoreCase("F"))
				tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.star), null);
			else
				tvOrderedItemName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			
			etOrderedQuantity.setVisibility(View.GONE);
			etQuantity.setVisibility(View.VISIBLE);
			etQuantity.setTag(R.string.key_product, objItem);
			tvOrderedItemName.setText(objItem.itemCode);
			tvOrderedItemMeasurment.setText(objItem.itemCode);
			tvOrderedItemDesc2.setText(objItem.itemDescription);
			etQuantity.setText(""+objItem.quantityLevel3 );
			etDiscount.setText(""+ decimalFormat.format(objItem.totalDiscountAmount));
			etPrice.setText(""+decimalFormat.format(objItem.priceUsedLevel3));
			etTotalPrice.setText(""+decimalFormat.format(objItem.basePrice * objItem.quantityLevel3));
			etInvoiceAmount.setText(""+decimalFormat.format(objItem.priceUsedLevel3 * objItem.quantityLevel3));
			tvOrderedItemDesc.setText(((""+objItem.itemDescription)).trim());
			
			tvOrderedItemDesc2.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvOrderedItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			etItemUOM.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showItemUOM(v, objItem);
				}
			});
//			if(childPosition%3 == 0)
//				ivImage.setImageResource(R.drawable.brand1);
//			else if(childPosition%3 == 1)
//				ivImage.setImageResource(R.drawable.brand2);
//			else
//				ivImage.setImageResource(R.drawable.brand3);
			
			ivImage.setImageResource(getResId(objItem.itemGroupLevel5));
			
			etQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						
						view  = v;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								
								hideKeyBoard(etQuantity);
								onKeyboardFocus(etQuantity, -1,false);
							}
						}, 100);
					}
					else
						view = null;

				}
			});
			
			
			etQuantity.addTextChangedListener(new TextChangeListener());
			
			return convertView;
			
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
	
	public class AddNewItemAdapter extends BaseAdapter
	{
		private Vector<TrxDetailsDO> vecSearchedItems;
		private Vector<TrxDetailsDO> vecSelectedItems = new Vector<TrxDetailsDO>();
		private Context context;
		boolean isAllSelected = false;
		private ImageView ivSelectAll;
		private String from = "";
		
		public AddNewItemAdapter(Vector<TrxDetailsDO> vecSearchedItems,Context context) 
		{
			this.vecSearchedItems = vecSearchedItems;
			this.context= context;
		}
		
		@Override
		public int getCount() 
		{
			if(vecSearchedItems != null && vecSearchedItems.size() >0)
				return vecSearchedItems.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) 
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		public void selectAll(ImageView ivSelectAll)
		{
			this.ivSelectAll = ivSelectAll;
			vecSelectedItems.clear();
			if(!isAllSelected)
			{
				ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
				vecSelectedItems = (Vector<TrxDetailsDO>) vecSearchedItems.clone();
				isAllSelected = true;
			}
			else if(isAllSelected)
			{
				ivSelectAll.setImageResource(R.drawable.checkbox_all);
				isAllSelected = false;
			}
			notifyDataSetChanged();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			final TrxDetailsDO objiItem   = 	vecSearchedItems.get(position);
			
			if(convertView == null)
				convertView			   = 	(LinearLayout)getLayoutInflater().inflate(R.layout.result_cell, null);
			
			TextView tvItemDescription = 	(TextView)convertView.findViewById(R.id.tvItemDescription);
			TextView tvItemCode 	   = 	(TextView)convertView.findViewById(R.id.tvItemCode);
			final ImageView cbList1    = 	(ImageView)convertView.findViewById(R.id.cbList1);
			final LinearLayout llReasion = 	(LinearLayout)convertView.findViewById(R.id.llReasion);
			
			final TextView tvExpiryDate			= 	(TextView)convertView.findViewById(R.id.tvExpiryDate);
			final EditText etRemark				= 	(EditText)convertView.findViewById(R.id.etRemark);	
			final ImageView btnCaptureImages		= 	(ImageView)convertView.findViewById(R.id.btnCaptureImages);
			
			
			tvItemCode.setText(objiItem.itemCode);
			tvItemDescription.setText(objiItem.itemDescription);
			tvItemDescription.setTextColor(context.getResources().getColor(R.color.gray_dark));
			tvItemCode.setTextColor(context.getResources().getColor(R.color.gray_dark));
			
			tvExpiryDate.setText(objiItem.expiryDate);
			etRemark.setText(objiItem.trxDetailsNote);
			btnCaptureImages.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent objIntent = new Intent(context, CaptureDamagedItemImage.class);
					objIntent.putExtra("position", position);
					objIntent.putExtra("itemCode", objiItem.itemCode);
					objIntent.putExtra("desc", objiItem.itemDescription);
					startActivityForResult(objIntent, 500);
				}
			});
			
			if(vecSelectedItems.contains(objiItem))
			{
				cbList1.setImageResource(R.drawable.checkbox);
				cbList1.setTag("1");
			}
			else
			{
				cbList1.setImageResource(R.drawable.uncheckbox);
				cbList1.setTag("0");
				llReasion.setVisibility(View.GONE);
			}

			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if(ivSelectAll!=null)
					{
						ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
						isAllSelected = false;
					}
					if(cbList1.getTag().toString().equalsIgnoreCase("0"))
					{
						vecSelectedItems.add(objiItem);
						cbList1.setImageResource(R.drawable.checkbox);
						cbList1.setTag("1");
						
					}
					else
					{
						vecSelectedItems.remove(objiItem);
						cbList1.setImageResource(R.drawable.uncheckbox);
						cbList1.setTag("0");
						
						llReasion.setVisibility(View.GONE);
					}
				}
			});
			
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
		public Vector<TrxDetailsDO> getSelectedItems()
		{
			return vecSelectedItems;
		}
		
		
		public void setExpiryDate(TrxDetailsDO productDO)
		{
			if(vecSelectedItems != null && vecSelectedItems.size() > 0)
			{
				for(TrxDetailsDO productDO2 : vecSelectedItems)
				{
					if(productDO2.itemCode.equalsIgnoreCase(productDO.itemCode))
						productDO2.expiryDate = productDO.expiryDate;
				}
			}
		}
		
		public void refresh(Vector<TrxDetailsDO> vecSearchedItemd)
		{
			this.vecSearchedItems = vecSearchedItemd;
			notifyDataSetChanged();
		}
		
	}
	private void showItemUOM(final View v, final TrxDetailsDO trxDetailsDO) 
	{
		if(trxDetailsDO.arrUoms.size()>0 ){
			CustomBuilder customBuilder = new CustomBuilder(SalesmanAddedItemCart.this, "Select UOM", false);
			customBuilder.setSingleChoiceItems(trxDetailsDO.arrUoms, v.getTag(), new CustomBuilder.OnClickListener()
			{
				@Override
				public void onClick(CustomBuilder builder, Object selectedObject) 
				{
					v.setTag((String)selectedObject);
					((TextView)v).setText((String)selectedObject);
					trxDetailsDO.UOM = (String)selectedObject;
					/*if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel1()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT1;
					else if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel2()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT2;
					else if(trxDetailsDO.UOM.equalsIgnoreCase(TrxDetailsDO.getItemUomLevel3()))
						trxDetailsDO.uomLevelUsed = TrxDetailsDO.ITEM_UOM_LEVELINT3;*/
					if(cartAdapter!=null)
						cartAdapter.handleText((String)v.getTag(R.string.request_Bu),trxDetailsDO);
					try {
						TextView tvMissedQty = (TextView) v.getTag(R.string.missed_Bu);
						tvMissedQty.setText(""+trxDetailsDO.missedBU);
					} catch (Exception e) {
						e.printStackTrace();
					}
					builder.dismiss();
				}
			});
			 customBuilder.show();
		 }
    }
	public UOMConversionDO getUomConversionFactor(String itemCode){
		return hashUomConveriosn.get(itemCode);
	}
}
