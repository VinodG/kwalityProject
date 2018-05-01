package com.winit.sfa.salesman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.GalleryAdapter;
import com.winit.alseer.salesman.common.Add_new_SKU_Dialog;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Gallery_Dialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import httpimage.HttpImageManager;

@SuppressLint("DefaultLocale") 
public class SalesmanReturnOrder extends BaseActivity implements SyncProcessListner
{
	private LinearLayout llrecommended_order,llStatementDiscount,llstatemntDiscValue,llApplyVAt;
	private ExpandableListView expandableListView;
	private TextView  tvstatementDiscountPer,tvDiscountPer,dot,tvOrderValue, tvDiscValue,tvVATRetValue,tvTotalValue,tvNetRetValue,tvNoItems,
			tvSubCategory, tvSplDiscountPer, tvSplDiscValue,tvstatementDiscValue,tvApplyVAt;
	private Button btnSubmit, btnAddItem, btnPromotion;
	private HashMap<String, Vector<TrxDetailsDO>> hmModifiedItem;
	private CaptureInventaryAdapter adapterForCapture;
	private Vector<String> vecCategoryIds;
	private Vector<TrxDetailsDO> vecSearchedItemd;
	private EditText etSearch;
	private AddNewItemAdapter adapter;
	private ImageView ivIconSel,ivSearchCross,ivVatCheck;
	private int listScrollState;
	private Vector<BrandDO> vecBrands =null;
	private Vector<CategoryDO> vecCategories = null;
	private boolean isApprovedReturnOrder;
	private TrxHeaderDO trxHeaderDO;
	private String Return_Type = "";
	private Vector<TrxDetailsDO> vecItems = new Vector<TrxDetailsDO>();
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
	private String SYNCPRICECAL="SYNCPRICECAL";
	private String categoryID = "";
	
	private float rateDiffAmount = 0;
	public float promotionDisc = 0,rateDiffPercent = 0;
	private float splDiscount, customerDiscount,customerstatementdiscount;
	private int Division = 0;
	private JourneyPlanDO objJourneyPlan;
	private LinkedHashMap<String, TrxDetailsDO> hmDistinctModifiedItem;
	private EditText etAmountdecimal;
	Boolean applyVatFlag=true;
	HashMap<String, String> hmShiftCodes=new HashMap<String, String>();
	
	@Override
	public void initialize() 
	{
		llrecommended_order = (LinearLayout)inflater.inflate(R.layout.recommendedorder, null);
		llBody.addView(llrecommended_order,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		hmDistinctModifiedItem = new LinkedHashMap<String, TrxDetailsDO>();
		hmModifiedItem = new HashMap<String, Vector<TrxDetailsDO>>();
		
		Bundle bundle  = getIntent().getExtras();
		
		
		if(bundle!=null)
		{
			isApprovedReturnOrder  = bundle.getBoolean("isFromReturn");
			if(bundle.containsKey("trxHeaderDO"))
		 			trxHeaderDO = (TrxHeaderDO) bundle.get("trxHeaderDO");
			if(trxHeaderDO!=null)
				vecItems.addAll(trxHeaderDO.arrTrxDetailsDOs);
			if(bundle.containsKey("Return_Type"))
				Return_Type = (String) bundle.get("Return_Type");
			
			if(getIntent().hasExtra(AppConstants.DIVISION))
				Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
			
			if(bundle.containsKey("mallsDetails"))
				objJourneyPlan = (JourneyPlanDO) bundle.get("mallsDetails");
//
//			if(Division == TrxHeaderDO.get_DIVISION_FOOD() || Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY ())
//			{
//				objJourneyPlan.PromotionalDiscount = "";
//				objJourneyPlan.statementdiscount = "";
//			}
			if(Division == TrxHeaderDO.get_DIVISION_FOOD() )
			{
				objJourneyPlan.PromotionalDiscount = objJourneyPlan.FInvDiscYH;
				objJourneyPlan.statementdiscount = objJourneyPlan.FStatDiscYH;
			}
			if(Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY ())
			{
				objJourneyPlan.PromotionalDiscount = objJourneyPlan.TInvDiscYH;
				objJourneyPlan.statementdiscount = objJourneyPlan.TStatDiscYH;
			}
		}
		
		intializeControls();
		bindControls();
		
		btnAddItem.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showAddNewSkuPopUp();
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
		
		loadData();
		btnAddItem.setVisibility(View.GONE);
		btnSubmit.setText("Preview");
		
//		tvSubCategory.setText(vecCategories.get(0).categoryName);
//		tvSubCategory.setTag(vecCategories.get(0));
		tvSubCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder customBuilder = new CustomBuilder(SalesmanReturnOrder.this, "Select Category", true);
				customBuilder.setSingleChoiceItems(vecCategories, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						if(selectedObject != null && selectedObject instanceof CategoryDO)
						{
							//BrandDO brandDO = (BrandDO) selectedObject;
							CategoryDO categoryDO = (CategoryDO) selectedObject;
							builder.dismiss();
							((TextView)v).setText(categoryDO.categoryName);
							((TextView)v).setTag(categoryDO);
							categoryID = categoryDO.categoryId;
							Vector<TrxDetailsDO> vecTemp = getCategoryItems(categoryDO.categoryId);
							if(vecTemp!=null && vecTemp.size() > 0)
							{
								refreshList(vecTemp);
								tvNoItems.setVisibility(View.GONE);
								expandableListView.setVisibility(View.VISIBLE);
							}
							else
							{
								tvNoItems.setVisibility(View.VISIBLE);
								expandableListView.setVisibility(View.GONE);
							}
							
//							if(adapter != null)
//								adapter.deSelectAll();
							builder.dismiss();
						}
				    }
				}); 
				customBuilder.show();
			}
		});
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
//				Vector<TrxDetailsDO> vecTemp = getSearchItems("");
				Vector<TrxDetailsDO> vecTemp = getSearchedItems("");
				if(vecTemp!=null && vecTemp.size() > 0/* && adapter!= null*/)
				{
					refreshList(vecTemp);
					tvNoItems.setVisibility(View.GONE);
					expandableListView.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoItems.setVisibility(View.VISIBLE);
					expandableListView.setVisibility(View.GONE);
				}
			}
		});
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
//				Vector<TrxDetailsDO> vecTemp = getSearchItems(s.toString());
				Vector<TrxDetailsDO> vecTemp = getSearchedItems(s.toString());
				if(vecTemp!=null && vecTemp.size() > 0/* && adapter!= null*/)
				{
					refreshList(vecTemp);
					tvNoItems.setVisibility(View.GONE);
					expandableListView.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoItems.setVisibility(View.VISIBLE);
					expandableListView.setVisibility(View.GONE);
				}
			}
		});
		
		btnPromotion.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showValuePopup();
//				showPromotionPopup();
			}
		});

		llApplyVAt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(applyVatFlag){
					applyVatFlag=false;
					ivVatCheck.setImageResource(R.drawable.remeber_me_uncheck_box);
				}else{
					applyVatFlag=true;
					ivVatCheck.setImageResource(R.drawable.remeber_me_box);
				}
				calculatePrice();
			}
		});
	}
	
	float firstPromo = 3,secondPromo = 5;
	private void showPromotionPopup() 
	{
		LinearLayout viewDetails 			= (LinearLayout)LayoutInflater.from(SalesmanReturnOrder.this).inflate(R.layout.promotion_popup, null);
        
		 LinearLayout ll3percent   			= (LinearLayout)viewDetails.findViewById(R.id.ll3percent);
		 LinearLayout ll5percent        	= (LinearLayout)viewDetails.findViewById(R.id.ll5percent);
		 LinearLayout llValue	        	= (LinearLayout)viewDetails.findViewById(R.id.llValue);
		 LinearLayout llClear	        	= (LinearLayout)viewDetails.findViewById(R.id.llClear);
		 TextView tvTaskHeader		    	= (TextView)viewDetails.findViewById(R.id.tvTaskHeader);
		 TextView tvPrvsValue   		 	= (TextView) viewDetails.findViewById(R.id.tvPrvsValue);
		 TextView tvDisc3		   		 	= (TextView) viewDetails.findViewById(R.id.tvDisc3);
		 TextView tvDisc5		   		 	= (TextView) viewDetails.findViewById(R.id.tvDisc5);
		 ImageView ivSelected3				= (ImageView) viewDetails.findViewById(R.id.ivSelected3);
		 ImageView ivSelected5				= (ImageView) viewDetails.findViewById(R.id.ivSelected5);
		 ImageView ivSelectedVal			= (ImageView) viewDetails.findViewById(R.id.ivSelectedVal);
		 final Dialog dialogDetails = new Dialog(SalesmanReturnOrder.this, android.R.style.Theme_Holo_Dialog);
		 dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 dialogDetails.addContentView(viewDetails, new LayoutParams(preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 160, LayoutParams.WRAP_CONTENT));
		 dialogDetails.show();
		 
		 if(arrrateDiff != null && arrrateDiff.size() >= 2)
		 {
			 firstPromo = StringUtils.getFloat(arrrateDiff.get(0));
			 secondPromo = StringUtils.getFloat(arrrateDiff.get(1));
		 }
		 
		 tvDisc3.setText(firstPromo+"% Discount");
		 tvDisc5.setText(secondPromo+"% Discount");
		 
		 if(rateDiffAmount >= 0)
			 tvPrvsValue.setText(""+rateDiffAmount);
		 
		 if(StringUtils.getInt(""+promotionDisc) == StringUtils.getInt(""+firstPromo))
		 {
			 ivSelected3.setImageResource(R.drawable.rbtn);
			 ivSelected5.setImageResource(R.drawable.rbtn_h);
			 ivSelectedVal.setImageResource(R.drawable.rbtn_h);
		 }
		 else if(StringUtils.getInt(""+promotionDisc) == StringUtils.getInt(""+secondPromo))
		 {
			 ivSelected5.setImageResource(R.drawable.rbtn);
			 ivSelected3.setImageResource(R.drawable.rbtn_h);
			 ivSelectedVal.setImageResource(R.drawable.rbtn_h);
		 }
		 else if(StringUtils.getInt(""+promotionDisc) == 0)
		 {
			 ivSelected3.setImageResource(R.drawable.rbtn_h);
			 ivSelected5.setImageResource(R.drawable.rbtn_h);
			 ivSelectedVal.setImageResource(R.drawable.rbtn_h);
		 }
		 else
		 {
			 ivSelectedVal.setImageResource(R.drawable.rbtn);
			 ivSelected5.setImageResource(R.drawable.rbtn_h);
			 ivSelected3.setImageResource(R.drawable.rbtn_h);
		 }
		 ll3percent.setOnClickListener(new OnClickListener()
		 {
			@Override
			public void onClick(View v) 
			{
				LogUtils.debug("negative", ""+objJourneyPlan.PromotionalDiscount);
				LogUtils.debug("negative", ""+rateDiffPercent);
				LogUtils.debug("negative", ""+firstPromo);
				
				if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + rateDiffPercent + firstPromo) <= 100)
				{
					promotionDisc 	= firstPromo;
//				discAmount 		= -1;
					calculatePrice();
					
					updateTRXDetail();
					
					double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
					tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				}
				else
					showCustomDialog(SalesmanReturnOrder.this,getString(R.string.warning), "Please adjust Rate diff percent.", getString(R.string.OK), null, "");
				dialogDetails.dismiss();
			}
			
		 });
		 
		 ll5percent.setOnClickListener(new OnClickListener()
		 {
			@Override
			public void onClick(View v) 
			{
				LogUtils.debug("negative", ""+objJourneyPlan.PromotionalDiscount);
				LogUtils.debug("negative", ""+rateDiffPercent);
				LogUtils.debug("negative", ""+secondPromo);
				
				if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + rateDiffPercent + secondPromo) <= 100)
				{
					promotionDisc 	= secondPromo;
//				discAmount 		= -1;
					calculatePrice();
					
					updateTRXDetail();
					
					double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
					tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				}
				else
					showCustomDialog(SalesmanReturnOrder.this,getString(R.string.warning), "Please adjust Rate diff percent.", getString(R.string.OK), null, "");
				dialogDetails.dismiss();
			}
		 });
		 
		 llValue.setOnClickListener(new OnClickListener()
		 {
			@Override
			public void onClick(View v) 
			{
				showValuePopup();
				dialogDetails.dismiss();
			}

		 });
		 
		 llClear.setOnClickListener(new OnClickListener()
		 {
			@Override
			public void onClick(View v) 
			{
				promotionDisc 	= 0;
				rateDiffPercent	= 0;
				rateDiffAmount	= 0;
				calculatePrice();
				
				updateTRXDetail();
				
				double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
//				tvDiscountPer.setText("("+decimalFormat.format(totaldiscount)+")");
				tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				dialogDetails.dismiss();
			}
		 });
	}
	
	
	private void updateTRXDetail() 
	{
		Set<String> keys = hmDistinctModifiedItem.keySet();
		for(String itemcode : keys)
		{
//			Vector<TrxDetailsDO> vecDetailsDOs = hmModifiedItem.get(itemcode);
//			if(vecDetailsDOs != null && vecDetailsDOs.size() > 0)
//			{
//				for (TrxDetailsDO objtrxdetailDo : vecDetailsDOs)
//				{
				TrxDetailsDO objtrxdetailDo  = hmDistinctModifiedItem.get(itemcode);
					if(objtrxdetailDo.quantityBU > 0)
					{
						objtrxdetailDo.promotionalDiscountAmount = calculateDiscount(objtrxdetailDo);
						objtrxdetailDo.statementDiscountAmnt = calculatestatementDiscount(objtrxdetailDo);
						
						objtrxdetailDo.calculatedDiscountPercentage = calculateDiscountPercentage(objtrxdetailDo);
						objtrxdetailDo.calculatedstatementDiscountPercentage = calculatestatementDiscountPercentage(objtrxdetailDo);
						
						objtrxdetailDo.calculatedDiscountAmount = objtrxdetailDo.promotionalDiscountAmount+objtrxdetailDo.statementDiscountAmnt;
						objtrxdetailDo.calculatedstatementDiscountAmount = objtrxdetailDo.statementDiscountAmnt;
					}
//				}
//			}
		}				
	}
	
//	private float calculateDiscount(TrxDetailsDO objItem) 
//	{
//		float discount = 0.0f;
//		discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(mallsDetails.PromotionalDiscount) + promotionDisc + rateDiffPercent))/100);
//
//		return discount;
//	}
	
	private float calculateDiscountPercentage(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;//promotionalDiscountAmount
		discount = (float) StringUtils.round(((objItem.promotionalDiscountAmount * 100) / (objItem.EAPrice * objItem.quantityBU)), 2);

		return discount;
	}
	private float calculatestatementDiscountPercentage(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;//promotionalDiscountAmount
		discount = (float) StringUtils.round(((objItem.statementDiscountAmnt * 100) / (objItem.EAPrice * objItem.quantityBU)), 2);

		return discount;
	}
	@Override
	public void refreshPager(int position) 
	{
	}
	
	private void bindControls() 
	{
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
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
						String type = "";
//						if(Division <= 0)
//							type = AppConstants.GRV;
//						else
//							type = AppConstants.Food_GRV;
						if(Division <= 0)
							type = AppConstants.GRV;
						else if(Division ==1)
							type = AppConstants.Food_GRV;
						else
							type = AppConstants.TPT_GRV;
						
						String availableOrderId = new OrderDA().getOrderIdBasedOnType(type);
						
						if(TextUtils.isEmpty(availableOrderId))
							isSequenceNumberAvailable=false;
						
						if(isSequenceNumberAvailable)
							trxHeaderDO = prepareSalesOrder();
						
//						trxHeaderDO.statementDiscount= mallsDetails.statementdiscount;
						if(selableItem.equalsIgnoreCase(""))
						{
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									if(trxHeaderDO != null)
									{
										insertStmtAndInvoiceDiscountInTrxDetails(  trxHeaderDO);
										Intent intent = new Intent(SalesmanReturnOrder.this, SalesmanOrderPreview.class);
										intent.putExtra("trxHeaderDO", trxHeaderDO);
										intent.putExtra("mallsDetails", objJourneyPlan);
										intent.putExtra(AppConstants.DIVISION, Division);
										startActivityForResult(intent, 1000);
									}
									else if(!isSequenceNumberAvailable){
										showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Sequence numbers are not synced. Please sync now.", getString(R.string.sync_now), getString(R.string.not_now), "Syncnow");
									}
									else
										showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than 0.", getString(R.string.OK), null, "");
									
													
									new Handler().postDelayed(new Runnable() {
										
										@Override
										public void run() 
										{
											btnSubmit.setEnabled(true);
											btnSubmit.setClickable(true);
										}
									},200);
									hideLoader();
								}
							});
						}
						else
						{
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
											btnSubmit.setEnabled(true);
											btnSubmit.setClickable(true);
										}
									}, 200);
									hideLoader();
								}
							});
						}
					}
				}).start();
			}
		});
		
		
	}
	private void insertStmtAndInvoiceDiscountInTrxDetails(TrxHeaderDO trxHeaderDO)
	{
//        this is suggested by digen sir
		for (int i = 0; i < trxHeaderDO.arrTrxDetailsDOs.size(); i++)
		{
			TrxDetailsDO trxDetailsDO= trxHeaderDO.arrTrxDetailsDOs.get(i);
			if(trxDetailsDO!=null)
			{
				trxDetailsDO.totalDiscountPercentage= (StringUtils.getFloat(trxHeaderDO.promotionalDiscount));
				trxDetailsDO.promotionalDiscountAmount= (StringUtils.getFloat(trxHeaderDO.promotionalDiscount)*trxHeaderDO.totalAmount)/100;
				trxDetailsDO.calculatedDiscountPercentage= StringUtils.getFloat(trxHeaderDO.statementDiscount);
				trxDetailsDO.calculatedDiscountAmount= (StringUtils.getFloat(trxHeaderDO.statementDiscount)*trxHeaderDO.totalAmount)/100;
			}
//            stmtInsertOrder.bindDouble(21, StringUtils.getDouble(trxHeaderDO.promotionalDiscount));
//
//							stmtInsertOrder.bindDouble(22,  (StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*trxDetailsDO.basePrice)/100);
//
//							stmtInsertOrder.bindDouble(23,  StringUtils.getDouble(trxHeaderDO.statementDiscount));
//							stmtInsertOrder.bindDouble(24,  (StringUtils.getDouble(trxHeaderDO.statementDiscount)*trxDetailsDO.basePrice)/100);
		}

	}
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		
		if(from.equalsIgnoreCase("isApprovedReturnOrder"))
		{
			uploadData();
			setResult(RESULT_OK,new Intent());
			finish();
		}
		else if(from.equalsIgnoreCase("Syncnow")){
			if(isNetworkConnectionAvailable(this))
				syncData(SalesmanReturnOrder.this);
			else
				showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
		}
		
	}
	
	ArrayList<String> arrrateDiff = null;
	private void loadData(){
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				vecBrands = new CategoriesDA().getAllBrands("");
				CaptureInventryDA captureInventryDA = new CaptureInventryDA();
				vecCategories = captureInventryDA.getAllCategories(objJourneyPlan,3,Division);
//				vecCategories = new  CategoriesDA().getAllCategory();
//				vecCategories = new  CaptureInventryDA().getAllSubCategories();
				hmShiftCodes = new CustomerDA().getShiftToCodes();
				hashMapPricing = captureInventryDA.getPricing(objJourneyPlan.priceList,TrxHeaderDO.get_TRXTYPE_RETURN_ORDER());
				vecSearchedItemd = captureInventryDA.getReturnItems(null, "", objJourneyPlan, true,Return_Type,Division);
				
				arrrateDiff = new CaptureInventryDA().getRateDiffDiscount();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(vecBrands!=null)
						{
							BrandDO brandDO = new BrandDO();
							brandDO.brandName="All Brands";
							brandDO.brandId="All Brands";
							vecBrands.add(0, brandDO);
						}
						hideLoader();
//						showAddNewSkuPopUp();
						refreshList(vecSearchedItemd);
					}
				});
			}
		}).start();
	}
	
	private void loadPricing(){
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() {
			@Override
			public void run() {
				String priceList ="";
				if(trxHeaderDO!=null)
				{
					priceList =new CustomerDA().getCusotmerPriceClass(trxHeaderDO.clientCode);
					hashMapPricing = new CaptureInventryDA().getPricing(priceList,trxHeaderDO.trxType);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						hideLoader();
						calculatePrice();
					}
				});
			}
		}).start();
		
	}
	private void intializeControls() 
	{
		btnSubmit 	 		= (Button) llrecommended_order.findViewById(R.id.btnSubmit);
		btnAddItem	 		= (Button) llrecommended_order.findViewById(R.id.btnAddItem);
		tvOrderValue		= (TextView) llrecommended_order.findViewById(R.id.tvOrderValue);
		tvDiscValue			= (TextView) llrecommended_order.findViewById(R.id.tvDiscValue);
		tvVATRetValue			= (TextView) llrecommended_order.findViewById(R.id.tvVATRetValue);
		tvTotalValue		= (TextView) llrecommended_order.findViewById(R.id.tvTotalValue);
		tvNetRetValue		= (TextView) llrecommended_order.findViewById(R.id.tvNetRetValue);
		tvNoItems			= (TextView) llrecommended_order.findViewById(R.id.tvNoItems);
		tvSubCategory		= (TextView) llrecommended_order.findViewById(R.id.tvSubCategory);
		tvApplyVAt		= (TextView) llrecommended_order.findViewById(R.id.tvApplyVAt);
		ivVatCheck		= (ImageView) llrecommended_order.findViewById(R.id.ivVatCheck);
		tvSplDiscountPer	= (TextView) llrecommended_order.findViewById(R.id.tvSplDiscountPer);
		tvSplDiscValue		= (TextView) llrecommended_order.findViewById(R.id.tvSplDiscValue); 
		tvstatementDiscountPer		= (TextView) llrecommended_order.findViewById(R.id.tvstatementDiscountPer);
		tvDiscountPer		= (TextView) llrecommended_order.findViewById(R.id.tvDiscountPer);
		tvstatementDiscValue		= (TextView) llrecommended_order.findViewById(R.id.tvstatementDiscValue);
		dot					= (TextView) llrecommended_order.findViewById(R.id.dot); 
		etSearch			= (EditText) llrecommended_order.findViewById(R.id.etSearch);
		ivSearchCross		= (ImageView) llrecommended_order.findViewById(R.id.ivSearchCross);
		
		btnPromotion		= (Button) llrecommended_order.findViewById(R.id.btnPromotion);
		llStatementDiscount = (LinearLayout) findViewById(R.id.llStatementDiscount);
		llApplyVAt = (LinearLayout) findViewById(R.id.llApplyVAt);
		llstatemntDiscValue = (LinearLayout) findViewById(R.id.llstatemntDiscValue);
//		
//		if(Division==TrxHeaderDO.get_DIVISION_FOOD())
//		{
//			llStatementDiscount.setVisibility(View.GONE);
//			llstatemntDiscValue.setVisibility(View.GONE);
//			dot.setVisibility(View.GONE);
//		}
		
		expandableListView	= (ExpandableListView) llrecommended_order.findViewById(R.id.expandableListView);
		setTypeFaceRobotoBold(llrecommended_order);
		tvNoItems.setTypeface(AppConstants.Roboto_Condensed);
		
		adapterForCapture = new CaptureInventaryAdapter(hmModifiedItem);
		expandableListView.setAdapter(adapterForCapture);
		
		tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
		
	}
	
	
	
	public class CaptureInventaryAdapter extends BaseExpandableListAdapter {
		private View view;
		HashMap<String, Vector<TrxDetailsDO>> hmReturnItem;
		public CaptureInventaryAdapter(HashMap<String, Vector<TrxDetailsDO>> hmReturnItem) 
		{
			this.hmReturnItem = hmReturnItem;
			if (hmReturnItem != null) {
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmReturnItem.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext())
					vecCategoryIds.add(iterator.next());

			}
		}
		
		public void refresh(HashMap<String, Vector<TrxDetailsDO>> hmReturnItem) 
		{
			this.hmReturnItem = hmReturnItem;
			if (hmReturnItem != null && hmReturnItem.size()>0) {
				
				tvNoItems.setVisibility(View.GONE);
				expandableListView.setVisibility(View.VISIBLE);
				vecCategoryIds = new Vector<String>();
				Set<String> set = hmReturnItem.keySet();
				Iterator<String> iterator = set.iterator();
				while (iterator.hasNext())
					vecCategoryIds.add(iterator.next());
			}
			else
			{
				tvNoItems.setVisibility(View.VISIBLE);
				expandableListView.setVisibility(View.GONE);
			}
			notifyDataSetChanged();
		}

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

			}

			@Override
			public void afterTextChanged(Editable s) {
				
				try {
					
					if(listScrollState == 0)
						handleText(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


		private void handleText(CharSequence s) {

			synchronized (MyApplication.SALES_UNITS_LOCK) 
			{
				TrxDetailsDO objItem = null;
				if (view != null) 
				{
					objItem = (TrxDetailsDO) view.getTag(R.string.key_product);
				}

				if (objItem != null)
				{
					if(s==null)
						s="";
					
					int enteredQuantity = StringUtils.getInt(s.toString());;
					objItem.collectedBU    =  enteredQuantity;
					objItem.finalBU		   =  enteredQuantity;
					objItem.quantityLevel1 =  enteredQuantity;
					objItem.quantityBU	   =  enteredQuantity;
					objItem.requestedBU	   =  enteredQuantity;
					
					rateDiffAmount = 0;
					rateDiffPercent = 0;
					objItem.calculatedDiscountPercentage = 0;
					updateDistinctItem(objItem);
					calculatePrice();
					
					if(objItem.quantityBU>0){
						LinearLayout llGSVValue = (LinearLayout) view.getTag(R.string.key_gsv_ll);
						llGSVValue.setVisibility(View.VISIBLE);
						TextView tvTotalValue = (TextView) view.getTag(R.string.key_gsv);
//						String gsv="PCS "+objItem.quantityBU+" X "+decimalFormat.format(objItem.EAPrice)+" = "+decimalFormat.format(objItem.quantityBU*objItem.EAPrice);
						String gsv=objItem.UOM+" "+decimalFormat.format(objItem.quantityBU)+" X "+(objItem.EAPrice)+" = "+decimalFormat.format(objItem.quantityBU*objItem.EAPrice);
						tvTotalValue.setText(gsv);
						
						objItem.promotionalDiscountAmount = calculateDiscount(objItem);
					}else{
						LinearLayout llGSVValue = (LinearLayout) view.getTag(R.string.key_gsv_ll);
						llGSVValue.setVisibility(View.GONE);
					}
				}
			}
		}
		

		@Override
		public int getGroupCount() {
			return vecCategoryIds.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return hmReturnItem.get(vecCategoryIds.get(groupPosition)).size();
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
			convertView = inflater
					.inflate(R.layout.list_brand_group_item, null);
			TextView tvBrandName = (TextView) convertView
					.findViewById(R.id.tvBrandName);
			tvBrandName.setText(vecCategoryIds.get(groupPosition));
			tvBrandName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			
			expandableListView.expandGroup(groupPosition);
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView,ViewGroup parent) {
			
			final TrxDetailsDO trxDetailsDO 	= hmReturnItem.get(vecCategoryIds.get(groupPosition)).get(childPosition);
			
			if(convertView == null)
				convertView 					= inflater.inflate(R.layout.return_item_cell, null);
			
			TextView tvHeaderText 				= (TextView) convertView.findViewById(R.id.tvHeaderText);
			TextView tvDescription 				= (TextView) convertView.findViewById(R.id.tvDescription);
			TextView tvReturnType 				= (TextView) convertView.findViewById(R.id.tvDescription1);
			TextView tvDescription2 			= (TextView) convertView.findViewById(R.id.tvDescription2);
			TextView tvItemUOM 					= (TextView) convertView.findViewById(R.id.tvCases);
			LinearLayout llReason 				= (LinearLayout) convertView.findViewById(R.id.llReason);
			TextView tvReason 					= (TextView) convertView.findViewById(R.id.tvReason);
			TextView tvReasonHeader 			= (TextView) convertView.findViewById(R.id.tvReasonHeader);
			LinearLayout llExpiry 				= (LinearLayout)convertView.findViewById(R.id.llExpiry);
			TextView tvExpiryDateHeader 		= (TextView) convertView.findViewById(R.id.tvExpiryDateHeader);
			TextView tvExpiryDate 				= (TextView) convertView.findViewById(R.id.tvExpiryDate);
			final Button btnDelete 				= (Button) convertView.findViewById(R.id.btnDelete);
			final CustomEditText evUnits 		= (CustomEditText) convertView.findViewById(R.id.evUnits);
			final ImageView ivItem 				= (ImageView) convertView.findViewById(R.id.ivItem);

			final TextView tvDiscountAplied 	= (TextView) convertView.findViewById(R.id.tvDiscountAplied);
			
			TextView tvUnitPriceLabel 			= (TextView) convertView.findViewById(R.id.tvUnitPriceLabel);
			TextView tvUnitPrice 				= (TextView) convertView.findViewById(R.id.tvUnitPrice);
			TextView tvTotalPrice 				= (TextView) convertView.findViewById(R.id.tvTotalPrice);
			TextView tvTotalPriceLabel 			= (TextView) convertView.findViewById(R.id.tvTotalPriceLabel);
			LinearLayout llGRVLabel				=	(LinearLayout) convertView.findViewById(R.id.llGRVLabel);
			
			if(trxDetailsDO.EAPrice >0 && trxDetailsDO.CSPrice >0)
			{
//				viewHolder.tvUnitPrice.setText(((BaseActivity)getActivity()).decimalFormat.format(trxDetailsDO.priceUsedLevel1));
				tvUnitPrice.setText("PCS: "+decimalFormat.format(trxDetailsDO.EAPrice)+" | "+"UNIT: "+decimalFormat.format(trxDetailsDO.CSPrice));
			}
			else if(trxDetailsDO.EAPrice >0)
			{
				tvUnitPrice.setText("PCS: "+decimalFormat.format(trxDetailsDO.EAPrice));
			}
			else if(trxDetailsDO.CSPrice >0)
			{
				tvUnitPrice.setText("UNIT: "+decimalFormat.format(trxDetailsDO.CSPrice));
			}
			else
			{
//				viewHolder.tvUnitPrice.setText(decimalFormat.format(trxDetailsDO.basePrice));
				tvUnitPrice.setText("PCS: "+decimalFormat.format(trxDetailsDO.basePrice)+" | "+"UNIT: "+decimalFormat.format(trxDetailsDO.basePrice));
			}
			
			if(trxDetailsDO.quantityBU>0)
			{
				llGRVLabel.setVisibility(View.VISIBLE);
				String gsv="PCS "+trxDetailsDO.quantityBU+" X "+decimalFormat.format(trxDetailsDO.EAPrice)+" = "+decimalFormat.format(trxDetailsDO.quantityBU*trxDetailsDO.EAPrice);
				tvTotalPrice.setText(gsv);
			}
			else
			{
				llGRVLabel.setVisibility(View.INVISIBLE);
			}

			tvDescription2.setVisibility(View.GONE);
			llExpiry.setVisibility(View.GONE);
			
			tvHeaderText.setText(trxDetailsDO.itemCode);
			tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvDescription.setText(trxDetailsDO.itemDescription);
			tvItemUOM.setText("PCS");//return always in EA UOM only - Anil
			if(trxDetailsDO.itemType.equalsIgnoreCase("D"))
			{
				llReason.setVisibility(View.GONE);
				tvReason.setText(trxDetailsDO.reason);
				tvReturnType.setText(getString(R.string.non_sellable));
				tvReturnType.setTextColor(getResources().getColor(R.color.red_dark));
			}
			else
			{
				llReason.setVisibility(View.GONE);
				tvReturnType.setText(getString(R.string.sellable));
				tvReturnType.setTextColor(getResources().getColor(R.color.green_dark));
			}
			
			if(trxDetailsDO.expiryDate!=null && !trxDetailsDO.expiryDate.equalsIgnoreCase(""))
				tvExpiryDate.setText(""+CalendarUtils.getFormatedDatefromStringWithOnlyTime(trxDetailsDO.expiryDate+""));
			else
				tvExpiryDate.setText("N/A");

			evUnits.setTag(R.string.key_product, trxDetailsDO);
			
			String imageURL = "brandimages/"+trxDetailsDO.itemGroupLevel5+".png";
			final Uri uri = Uri.parse(imageURL);
			if (uri != null) {
				Bitmap bitmap = getHttpImageManager().loadImage(
						new HttpImageManager.LoadRequest(uri, ivItem,imageURL));
				if (bitmap != null) {
					ivItem.setImageBitmap(bitmap);
				}
			}
			
			if(isApprovedReturnOrder)
				evUnits.setText(""+trxDetailsDO.approvedBU);
			else if(trxDetailsDO.quantityBU > 0)
				evUnits.setText(""+trxDetailsDO.quantityBU);
			else
				evUnits.setText(""/*+trxDetailsDO.requestedBU*/);

			evUnits.setTag(R.string.key_gsv,tvTotalPrice);
			evUnits.setTag(R.string.key_gsv_ll,llGRVLabel);
			
			evUnits.addTextChangedListener(new TextChangeListener());
			
			tvDiscountAplied.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});
			
			evUnits.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						view  = v;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								hideKeyBoard(evUnits);
								onKeyboardFocus(evUnits, -1,false);
							}
						}, 10);
					}
				}
			});

			btnDelete.setTag(trxDetailsDO);
			btnDelete.setVisibility(View.GONE);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(trxDetailsDO.productStatus  == 1)
					{
						trxDetailsDO.productStatus  = 0;
						btnDelete.setVisibility(View.GONE);
					}
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) 
				{
					if(trxDetailsDO.productStatus  == 0)
					{
						trxDetailsDO.productStatus  = 1;
						btnDelete.setVisibility(View.VISIBLE);
						
						Animation animation  = AnimationUtils.loadAnimation(SalesmanReturnOrder.this, R.anim.right_to_left);
						
						btnDelete.startAnimation(animation);
					}
					return false;
				}
			});

			btnDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					trxDetailsDO.productStatus  = 2;
					removeProduct(groupPosition, childPosition);
					btnDelete.setVisibility(View.GONE);
				}
			});

			if(trxDetailsDO.productStatus==0)
				convertView.setBackgroundColor(Color.WHITE);
			else if(trxDetailsDO.productStatus==1)
				convertView.setBackgroundColor(Color.parseColor("#F0F0F0"));
			else if(trxDetailsDO.productStatus==2)
				convertView.setBackgroundColor(Color.parseColor("#FBE5E9"));
			
			setTypeFaceRobotoNormal((ViewGroup)convertView);
			tvReturnType.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvHeaderText.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvExpiryDateHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvReasonHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
			return convertView;
		}

		private void removeProduct(int groupPosition, int childPosition) {
			adapterForCapture.notifyDataSetChanged();
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}

	private void updateDistinctItem(TrxDetailsDO objItem) {
		hmDistinctModifiedItem.put(objItem.itemCode, objItem);
	}

	float orderTPrice, totalIPrice, totalDiscount,orderVATPrice;
	private void calculatePrice()
	{
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				calculatePriceWithSync();
			}
		}).start();
	}
	
	private float calculateDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
		if(objJourneyPlan != null)
//			discount = (float) ((objItem.EAPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent))/100);
			discount = (float) ((objItem.EAPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc ))/100);
		return discount;
	}
	private float calculatestatementDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
		if(objJourneyPlan != null)
			discount = (float) ((objItem.EAPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.statementdiscount)))/100);
		return discount;
	}
	private void calculatePriceWithSync(){
		synchronized (SYNCPRICECAL) {

			orderTPrice 		= 	0.0f;
			orderVATPrice 		= 	0.0f;
			totalIPrice 		= 	0.0f;
			totalDiscount 		= 	0.0f;
			splDiscount	= 0;

			customerDiscount = 0;
			customerstatementdiscount=0;
			
//			Set<String> set = hmModifiedItem.keySet();
			Set<String> set = hmDistinctModifiedItem.keySet();
			int count = 1;
			for(String key : set) {
				TrxDetailsDO trxDetailsDO = hmDistinctModifiedItem.get(key);
				if (trxDetailsDO.quantityBU > 0) {


//				for(TrxDetailsDO trxDetailsDO : vec)
//				{
					trxDetailsDO.lineNo = (int) count++;
					float price = trxDetailsDO.basePrice;
					if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) {
						if (hashMapPricing.get(trxDetailsDO.itemCode).containsKey(trxDetailsDO.UOM))
							price = hashMapPricing.get(trxDetailsDO.itemCode).get(trxDetailsDO.UOM);//getting price of selected UOM
					}
					trxDetailsDO.priceUsedLevel1 = price - trxDetailsDO.totalDiscountAmount;

					if (hashMapPricing.containsKey(trxDetailsDO.itemCode) && hashMapPricing.get(trxDetailsDO.itemCode) != null) {
						price = hashMapPricing.get(trxDetailsDO.itemCode).get(
								trxDetailsDO.UOM);// getting price of selected UOM
						if (hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
							trxDetailsDO.priceUsedLevel3 = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());

						if (hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
							trxDetailsDO.CSPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel1());
					}

					trxDetailsDO.EAPrice = trxDetailsDO.priceUsedLevel3;

					float discountAmount = (price * trxDetailsDO.calculatedDiscountPercentage) / 100.0f;
					trxDetailsDO.calculatedDiscountAmount = StringUtils.getFloat(decimalFormat.format(discountAmount));
//					trxDetailsDO.totalDiscountAmount=StringUtils.getFloat(decimalFormat.format(trxDetailsDO.calculatedDiscountAmount*trxDetailsDO.quantityLevel1));
					float	customerDiscountTemp = ((StringUtils.getFloat(objJourneyPlan.PromotionalDiscount) * price)/100);
					float	customerstatementdiscountTemp =  ((StringUtils.getFloat(objJourneyPlan.statementdiscount) * price)/100);
					if(applyVatFlag)
						trxDetailsDO.vatPercentage=trxDetailsDO.vatPercentagebackup;
					else
						trxDetailsDO.vatPercentage=0.0f;
					float	speacialDiscountOnEach =  ((rateDiffPercent * price)/100);
					float vatOnEach=(((price )-customerDiscountTemp-customerstatementdiscountTemp-speacialDiscountOnEach)*trxDetailsDO.vatPercentage)/100;
					trxDetailsDO.VATAmountNew=(vatOnEach* trxDetailsDO.quantityLevel1);
					orderVATPrice+=(vatOnEach* trxDetailsDO.quantityLevel1);
					orderTPrice += (price * trxDetailsDO.quantityLevel1);
					totalDiscount += StringUtils.getFloat(decimalFormat.format(trxDetailsDO.calculatedDiscountAmount * trxDetailsDO.quantityLevel1));


					totalIPrice += (price * trxDetailsDO.quantityLevel1);

					if (trxDetailsDO.quantityLevel1 > 0) {
						LogUtils.debug("returnPrice", "itemCode:" + trxDetailsDO.itemCode);
						LogUtils.debug("returnPrice", "quantityLevel1:" + trxDetailsDO.quantityLevel1);
						LogUtils.debug("returnPrice", "price:" + price);
					}

					calculateDiscount(price, trxDetailsDO.quantityLevel1);
//				}

				}
			}
				calculateRateDiffDiscount();
				calculateTotalDiscount();


			runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
//					tvDiscValue.setText(amountFormate.format(totalDiscount));
					tvDiscValue.setText(amountFormate.format(customerDiscount));
					tvVATRetValue.setText(amountFormate.format(orderVATPrice));
					tvTotalValue.setText(amountFormate.format(totalIPrice-totalDiscount+orderVATPrice));
					tvNetRetValue.setText(amountFormate.format(totalIPrice-totalDiscount));
					tvOrderValue.setText(amountFormate.format(orderTPrice));
					tvSplDiscValue.setText(decimalFormat.format(splDiscount));
					
					//tvstatementDiscValue.setText(resid);
					tvstatementDiscValue.setText(decimalFormat.format(customerstatementdiscount));
					tvstatementDiscountPer.setText("("+StringUtils.getDouble(objJourneyPlan.statementdiscount)+")");
					tvDiscountPer.setText("("+StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+")");
					
					tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				}
			});
		
		}
	}
	
	private void calculateRateDiffDiscount() 
	{
		if(rateDiffAmount > 0)
		{//StringUtils.getFloat(decimalFormat.format((csPrice * quanity)));
			double totalOrderAmount = totalIPrice;
			if(totalOrderAmount <= 0)
				totalOrderAmount = 1;
			rateDiffPercent = StringUtils.getFloat(decimalFormat.format(((StringUtils.getFloat(decimalFormat.format(rateDiffAmount)) * 100)/StringUtils.getFloat(amountFormate.format(totalOrderAmount)))));
			
			LogUtils.debug("rate_Diff", "totalOrderAmount:"+totalOrderAmount);
			LogUtils.debug("rate_Diff", "rateDiffAmount:"+rateDiffAmount);
			LogUtils.debug("rate_Diff", "rateDiffPercent:"+rateDiffPercent);
		}else{
			rateDiffPercent=0;
		}
	}
	
	
	private void calculateTotalDiscount()
	{
		if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent + StringUtils.getDouble(objJourneyPlan.statementdiscount)) > 0)
		{
			double totalOrderAmount = totalIPrice;
			if(totalOrderAmount > 0)
			{
				customerDiscount  = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(objJourneyPlan.PromotionalDiscount) * totalOrderAmount)/100))));
				customerstatementdiscount  =  StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(objJourneyPlan.statementdiscount) * totalOrderAmount)/100))));
				totalDiscount  = StringUtils.getFloat(decimalFormat.format((((promotionDisc) * totalOrderAmount)/100))) + StringUtils.getFloat(decimalFormat.format(rateDiffAmount)) + customerDiscount+customerstatementdiscount;
				splDiscount = StringUtils.getFloat(decimalFormat.format(((promotionDisc) * totalOrderAmount/100))) + StringUtils.getFloat(decimalFormat.format(rateDiffAmount));
			}
			else
			{
				customerDiscount = 0;
				totalDiscount = 0;
				splDiscount = 0;
				customerstatementdiscount=0;
			}
		}
	}
	private void calculateDiscount(float price3, float quanity) 
	{
		float calDiscount = 0.0f;
		float splDisc		= 0.0f;
		/**Promotional discount offered**/
//		if(StringUtils.getInt(mallsDetails.PromotionalDiscount) > 0)
//			calDiscount += ((price3 * quanity) * StringUtils.getFloat(mallsDetails.PromotionalDiscount)/100);
		
		if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent) > 0)
		{
			calDiscount += ((price3 * quanity) * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent)/100)/* + StringUtils.getFloat(decimalFormat.format(rateDiffAmount))*/;
			splDisc += ((price3 * quanity) * (promotionDisc + rateDiffPercent)/100)/* + StringUtils.getFloat(decimalFormat.format(rateDiffAmount))*/;
		}
		totalDiscount += calDiscount;
		splDiscount += splDisc;
		
		totalDiscount += calDiscount;
	}
	
	private void showValuePopup()
	{
		View view = inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialogs = new CustomDialog(SalesmanReturnOrder.this, view, preference	.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,LayoutParams.WRAP_CONTENT, true);
		customDialogs.setCancelable(true);
		TextView tvTitle 				= (TextView) view.findViewById(R.id.tvTitlePopup);
		Button btnOkPopup 				= (Button) 	 view.findViewById(R.id.btnOkPopup);
		final EditText etEnterValue 	= (EditText) view.findViewById(R.id.etEnterValue);
		EditText etEnterReasons			= (EditText) view.findViewById(R.id.etEnterReason);
		etAmountdecimal	= (EditText) view.findViewById(R.id.etAmountdecimal);
		etEnterReasons.setVisibility(View.GONE);
		
		TextView tvSelectReason		= (TextView) view.findViewById(R.id.tvSelectReason);
		tvSelectReason.setVisibility(View.GONE);
		etEnterValue.setVisibility(View.GONE);
		
		tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOkPopup.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etEnterValue.setTypeface(AppConstants.Roboto_Condensed);
		etAmountdecimal.setTypeface(AppConstants.Roboto_Condensed);
		
		etAmountdecimal.setVisibility(View.VISIBLE);
		etAmountdecimal.setHint("Enter value");
		
		int maxLength = 10;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		etAmountdecimal.setFilters(FilterArray); 
		
		tvTitle.setText("Discount value");
		btnOkPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(etAmountdecimal.getText().toString().equalsIgnoreCase(""))
					showCustomDialog(SalesmanReturnOrder.this,getString(R.string.warning), "Please enter discount amount.", getString(R.string.OK), null, "");
				else if(StringUtils.getFloat(etAmountdecimal.getText().toString()) > (totalIPrice - customerDiscount-customerstatementdiscount))
					showCustomDialog(SalesmanReturnOrder.this,getString(R.string.warning), "Please provide Rate diff amount less than Total Invoice Amount.", getString(R.string.OK), null, "");
				else
				{
//					promotionDisc   = 0;
//					calculatePrice(true);
					showReasonForPromotion(customDialogs);
//					new Handler().postDelayed(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							rateDiffAmount = StringUtils.getFloat(etAmountdecimal.getText().toString());
//
//							calculateRateDiffDiscount();
////							double dicount = rateDiffAmount + totalDiscount;
////							double discountpercent = (double)((dicount* 100)/totalIPrice);
////							rateDiffPercent = discountpercent - StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) - promotionDisc;
//
//							updateTRXDetail();
//							calculatePrice();
//
//							double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
////							tvDiscountPer.setText("("+decimalFormat.format(totaldiscount)+")");
//							tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
//							customDialogs.dismiss();
//						}
//
//					}, 200);
				}
			}
		});
		if (!customDialogs.isShowing())
			customDialogs.show();
	}

	private String promotionReason = "";
	public void showReasonForPromotion(final CustomDialog customDialogs)
	{
		final Vector<NameIDDo> vecReasons = new CommonDA().getReasonsByType(AppConstants.PROMOTION_REASON);
		CustomBuilder builder = new CustomBuilder(this, "Please Select Discount Reason.", false);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
				builder.dismiss();
				promotionReason = objNameIDDo.strName;
				new Handler().postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							rateDiffAmount = StringUtils.getFloat(etAmountdecimal.getText().toString());

							calculateRateDiffDiscount();
//							double dicount = rateDiffAmount + totalDiscount;
//							double discountpercent = (double)((dicount* 100)/totalIPrice);
//							rateDiffPercent = discountpercent - StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) - promotionDisc;

							updateTRXDetail();
							calculatePrice();

							double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
//							tvDiscountPer.setText("("+decimalFormat.format(totaldiscount)+")");
							tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
							customDialogs.dismiss();
						}

					}, 200);
			}
		});
		builder.show();
	}
	
	private String selableItem = "";
	private TrxHeaderDO prepareSalesOrder() 
	{
		TrxHeaderDO trxHeaderDO = null;
		selableItem = "";
		updateTRXDetail();
		if(hmDistinctModifiedItem == null || hmDistinctModifiedItem.size() <= 0)
			trxHeaderDO = null;
		else
		{
			ArrayList<TrxDetailsDO> arrTRXDetail = new ArrayList<TrxDetailsDO>();
			Set<String> keys = hmDistinctModifiedItem.keySet();
			for(String key : keys)
			{
				TrxDetailsDO trxDetailsDO = hmDistinctModifiedItem.get(key);
//				for(TrxDetailsDO trxDetailsDO : vector)
//				{
					if(trxDetailsDO.requestedBU > 0 && trxDetailsDO.productStatus != 2)
					{
						trxDetailsDO.requestedSalesBU = trxDetailsDO.requestedBU;
						
						if(!trxDetailsDO.itemType.equalsIgnoreCase("D"))
						{
							UOMConversionFactorDO uomFactorDO = trxDetailsDO.hashArrUoms.get(trxDetailsDO.itemCode+"UNIT");
							if(uomFactorDO!=null)
							{
								if((trxDetailsDO.requestedSalesBU%uomFactorDO.eaConversion) != 0)
								{
									selableItem = trxDetailsDO.itemCode;
									break;
								}
							}
						}
						trxDetailsDO.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
						trxDetailsDO.trxDetailsNote = "";
						
						arrTRXDetail.add(trxDetailsDO);
					}
//				}
				if(!selableItem.equalsIgnoreCase(""))
					break;
			}
			
			if(!selableItem.equalsIgnoreCase(""))
				showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Please select proper quantity for "+selableItem+".", getString(R.string.OK), null, "selableItem");
			
			if(arrTRXDetail.size() > 0)
			{
				trxHeaderDO = new TrxHeaderDO();
				trxHeaderDO.appTrxId 		=  StringUtils.getUniqueUUID();
				trxHeaderDO.clientBranchCode=  objJourneyPlan.site;
				trxHeaderDO.clientCode		=  objJourneyPlan.site;
				trxHeaderDO.currencyCode    =  AppConstants.CURRECNY_CODE;
				trxHeaderDO.deliveryDate	=  CalendarUtils.getCurrentDate();
				trxHeaderDO.journeyCode		=  objJourneyPlan.JourneyCode;
				trxHeaderDO.orgCode			=  preference.getStringFromPreference(Preference.ORG_CODE, "");
				trxHeaderDO.paymentType		=  objJourneyPlan.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) ? 1 : 0;//need to check
				trxHeaderDO.printingTimes	=  0;
				trxHeaderDO.status			=  0;
				trxHeaderDO.totalAmount		=  orderTPrice;
				trxHeaderDO.totalVATAmount		=  orderVATPrice;
				trxHeaderDO.totalDiscountAmount = totalDiscount;
				trxHeaderDO.statementdiscountvalue = customerstatementdiscount;
				trxHeaderDO.trxStatus		= TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();//need to hanged as pr client requirement
				trxHeaderDO.trxType			= TrxHeaderDO.get_TRXTYPE_RETURN_ORDER();
//				trxHeaderDO.trxSubType			= TrxHeaderDO.get_TRXTYPE_RETURN_ORDER();
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");
				trxHeaderDO.visitCode		= objJourneyPlan.VisitCode;
				trxHeaderDO.promotionalDiscount = objJourneyPlan.PromotionalDiscount;
				trxHeaderDO.specialDiscount = splDiscount;
				trxHeaderDO.specialDiscPercent = promotionDisc + rateDiffPercent;
				trxHeaderDO.rateDiff		 	= rateDiffAmount;
				trxHeaderDO.promotionalDiscount = objJourneyPlan.PromotionalDiscount;
				trxHeaderDO.Division		= Division;
				trxHeaderDO.statementDiscount= objJourneyPlan.statementdiscount;
				trxHeaderDO.PromotionReason = promotionReason;
				trxHeaderDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
		}
		return trxHeaderDO;
	}
	
	private void updateApprovedOrder()
	{
		trxHeaderDO.status			=  100;//status for approved order to post it from different service
		trxHeaderDO.totalAmount		=  orderTPrice;
		trxHeaderDO.totalVATAmount		=  orderVATPrice;
		trxHeaderDO.totalDiscountAmount = totalDiscount;
		trxHeaderDO.statementdiscountvalue = customerstatementdiscount;
		trxHeaderDO.trxStatus		= TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
	}
	
//	private void updateTRXDetail() 
//	{
//		Set<String> keys = hmDistinctModifiedItem.keySet();
//		for(String itemcode : keys)
//		{
//			TrxDetailsDO objtrxdetailDo = hmDistinctModifiedItem.get(itemcode);
//			objtrxdetailDo.promotionalDiscountAmount = calculateDiscount(objtrxdetailDo);
//			
//			objtrxdetailDo.calculatedDiscountPercentage = calculateDiscountPercentage(objtrxdetailDo);
//			objtrxdetailDo.calculatedDiscountAmount = objtrxdetailDo.promotionalDiscountAmount;
//		}				
//	}
	
//	public void updateDistinctItem(TrxDetailsDO objItem)
//	{
//		hmDistinctModifiedItem.put(objItem.itemCode, objItem);
//		calculatePrice();
//	}
	
	private ExpandableListView lvExPopupList;
	public void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(SalesmanReturnOrder.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		
		showFullScreenDialog(objAddNewSKUDialog);
	    
		if(vecTemp!=null && vecTemp.size()>0)
			vecTemp.clear();
		
		final LinearLayout llResult 			=	objAddNewSKUDialog.llResult;
		final LinearLayout llBottomButtons 		=	objAddNewSKUDialog.llBottomButtons;
		TextView tvItemCodeLabel				=	objAddNewSKUDialog.tvItemCodeLabel;
		TextView tvItem_DescriptionLabel		=	objAddNewSKUDialog.tvItem_DescriptionLabel;
		TextView tvAdd_New_SKU_Item				=	objAddNewSKUDialog.tvAdd_New_SKU_Item;
		final TextView tvCategory	 			=	objAddNewSKUDialog.tvCategory;
		final EditText etSearch	 				=	objAddNewSKUDialog.etSearch;
		final ImageView cbList 					=	objAddNewSKUDialog.cbList;
		final ListView lvPopup		 			=	objAddNewSKUDialog.lvPopupList;
		lvExPopupList							=	objAddNewSKUDialog.lvExPopupList;
		final LinearLayout llList				=	objAddNewSKUDialog.llList;
		Button btnAdd 							=	objAddNewSKUDialog.btnAdd;
		Button btnCancel 						=	objAddNewSKUDialog.btnCancel;
		final TextView tvNoItemFound			=	objAddNewSKUDialog.tvNoItemFound;
		
		final ImageView ivSearchCross	=	objAddNewSKUDialog.ivSearchCross;
		
		lvExPopupList.setCacheColorHint(0);
		lvExPopupList.setScrollbarFadingEnabled(true);
		lvExPopupList.setDividerHeight(0);
		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		
		//sellable
		tvNoItemFound.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItemCodeLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvItem_DescriptionLabel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCategory.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAdd_New_SKU_Item.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		
		final Button btnSearch = new Button(SalesmanReturnOrder.this);
		tvCategory.setText(vecCategories.get(0).categoryName);
		tvCategory.setTag(vecCategories.get(0));
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder customBuilder = new CustomBuilder(SalesmanReturnOrder.this, "Select Category", true);
				customBuilder.setSingleChoiceItems(vecCategories, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						if(selectedObject != null && selectedObject instanceof CategoryDO)
						{
							//BrandDO brandDO = (BrandDO) selectedObject;
							CategoryDO categoryDO = (CategoryDO) selectedObject;
							builder.dismiss();
							((TextView)v).setText(categoryDO.categoryName);
							((TextView)v).setTag(categoryDO);
							Vector<TrxDetailsDO> vecTemp = getCategoryItems(categoryDO.categoryId);
							if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
							{
								adapter.refresh(vecTemp);
								tvNoItemFound.setVisibility(View.GONE);
								lvExPopupList.setVisibility(View.VISIBLE);
							}
							else
							{
								tvNoItemFound.setVisibility(View.VISIBLE);
								lvExPopupList.setVisibility(View.GONE);
							}
							
							if(adapter != null)
								adapter.deSelectAll();
							builder.dismiss();
						}
				    }
				}); 
				customBuilder.show();
			}
		});
		
		Vector<TrxDetailsDO> tmp=getSearchItems("");
		if(tmp != null && tmp.size() > 0)
		{
			cbList.setVisibility(View.VISIBLE);
			tvNoItemFound.setVisibility(View.GONE);
			lvPopup.setVisibility(View.GONE);
			llBottomButtons.setVisibility(View.VISIBLE);
			lvExPopupList.setAdapter(adapter = new AddNewItemAdapter(tmp,SalesmanReturnOrder.this));
		}
		else
		{
			if(adapter == null)
				lvExPopupList.setAdapter(adapter = new AddNewItemAdapter( new Vector<TrxDetailsDO>(),SalesmanReturnOrder.this));
			else
				adapter.refresh(tmp);
			cbList.setVisibility(View.INVISIBLE);
			tvNoItemFound.setVisibility(View.VISIBLE);
			llBottomButtons.setVisibility(View.GONE);
		}
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				Vector<TrxDetailsDO> vecTemp = getSearchItems("");
				if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
				{
					adapter.refresh(vecTemp);
					tvNoItemFound.setVisibility(View.GONE);
					lvExPopupList.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoItemFound.setVisibility(View.VISIBLE);
					lvExPopupList.setVisibility(View.GONE);
				}
			}
		});
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				Vector<TrxDetailsDO> vecTemp = getSearchItems(s.toString());
				if(vecTemp!=null && vecTemp.size() > 0 && adapter!= null)
				{
					adapter.refresh(vecTemp);
					tvNoItemFound.setVisibility(View.GONE);
					lvExPopupList.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoItemFound.setVisibility(View.VISIBLE);
					lvExPopupList.setVisibility(View.GONE);
				}
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
					String error = validateReason(veciItems);
					if(TextUtils.isEmpty(error))
					{
						refreshList(veciItems);
						objAddNewSKUDialog.dismiss();
					}
					else
						showCustomDialog(SalesmanReturnOrder.this, "Warning !", "Please enter details for the following items: \n"+error, "OK", null, "");
				}
				else
					showCustomDialog(SalesmanReturnOrder.this, "Warning !", "Please select Items.", "OK", null, "");
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
	
	private void refreshList(Vector<TrxDetailsDO> veciItems)
	{
		if(hmModifiedItem == null)
			hmModifiedItem = new HashMap<String, Vector<TrxDetailsDO>>();
		for(int i=0; veciItems != null && i < veciItems.size(); i++)
		{
			TrxDetailsDO objProduct = veciItems.get(i);
			
			if(objProduct.requestedBU == 0 && !isApprovedReturnOrder)
			{
				objProduct.requestedBU	= 0;
				objProduct.quantityLevel1 = 0;
				objProduct.quantityBU	   = 0;
				
				objProduct.collectedBU    =  0;
				objProduct.finalBU		   =  0;
				objProduct.quantityLevel1 =  0;
				objProduct.quantityBU	   =  0;
			}
			
			objProduct.UOM="PCS";//Added by Aritra
			
			float eaPrice = 0;
			float csPrice = 0;

			if (hashMapPricing.containsKey(objProduct.itemCode)) {
				
				if(hashMapPricing.get(objProduct.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
					eaPrice = hashMapPricing.get(objProduct.itemCode).get(TrxDetailsDO.getItemUomLevel3());
				
				if(hashMapPricing.get(objProduct.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
					csPrice = hashMapPricing.get(objProduct.itemCode).get(TrxDetailsDO.getItemUomLevel1());
			}
			
			objProduct.EAPrice = eaPrice;
			objProduct.CSPrice = csPrice;
			
			objProduct.productStatus=-100;
			Vector<TrxDetailsDO> vecProducts = hmModifiedItem.get(objProduct.brandName);
			if(vecProducts == null)
			{
				vecProducts = new Vector<TrxDetailsDO>(); 
				vecProducts.add(objProduct);
				hmModifiedItem.put(objProduct.brandName, vecProducts);
			}
			else
			{
				int countCheck = 0, j = 0;
				for (j = 0; j < vecProducts.size(); j++) 
				{
					if(objProduct.itemCode.equalsIgnoreCase(vecProducts.get(j).itemCode))
					{
						countCheck = 1;
						break;
					}
				}
				if(countCheck > 0)
					vecProducts.set(j, objProduct);
				else
					vecProducts.add(objProduct);
			}
		}
		HashMap<String, Vector<TrxDetailsDO>> hmTemp = new HashMap<String, Vector<TrxDetailsDO>>();
		for (int i = 0; i < veciItems.size(); i++) 
		{
			TrxDetailsDO objTrxDetailDO = veciItems.get(i);
			Vector<TrxDetailsDO> vecProducts = hmTemp.get(objTrxDetailDO.brandName);
			if(vecProducts == null)
			{
				vecProducts = new Vector<TrxDetailsDO>(); 
				vecProducts.add(objTrxDetailDO);
				hmTemp.put(objTrxDetailDO.brandName, vecProducts);
			}
			else
				vecProducts.add(objTrxDetailDO);
		}
		if(veciItems.size() <= 0)
			hmTemp = hmModifiedItem;
		if(adapterForCapture != null)
		{
//			adapterForCapture.refresh(hmTemp);			
			
			adapterForCapture = new CaptureInventaryAdapter(hmTemp);
			expandableListView.setAdapter(adapterForCapture);
		}
		
//		calculatePrice();
	}
	
	private String validateReason(Vector<TrxDetailsDO> veciItems)
	{
		String error="";
		String todaysDate = CalendarUtils.getOrderPostDate();
//		for(TrxDetailsDO trxDetailsDO:veciItems){
//			if(trxDetailsDO.itemType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE())){
//				if(TextUtils.isEmpty(trxDetailsDO.trxDetailsNote) || CalendarUtils.getDiffBtwDatesInDays(todaysDate,trxDetailsDO.expiryDate)<1){
//					error = error+trxDetailsDO.itemCode+"\n";
//				}
//			}else if(trxDetailsDO.itemType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE())){
//				if(TextUtils.isEmpty(trxDetailsDO.reason)){
//					error = error+trxDetailsDO.itemCode+"\n";
//				}
//				else if(TextUtils.isEmpty(trxDetailsDO.expiryDate)){
//					error = error+trxDetailsDO.itemCode+"\n";
//				}
//				/*else if(TextUtils.isEmpty(trxDetailsDO.batchCode)){
//					error = error+trxDetailsDO.itemCode+"\n";
//				}*/else if(trxDetailsDO.reason.equalsIgnoreCase("Soon to Expire") && CalendarUtils.getDiffBtwDatesInDays(todaysDate,trxDetailsDO.expiryDate)<1){
//					error = error+trxDetailsDO.itemCode+"\n";
//				}
//			}
//		}
		
		return error;
	}
	
	private static class ViewHolder
	{
//		TextView tvnonsellable,tvUOM,tvsellable;
		ImageView btnCaptureImages,ivIcon;
		CheckBox ivReturnReason1,/*ivReturnReason2,*/ivReturnReason3/*,ivReturnReason4*/;
		LinearLayout llReasion,llBottom,llnonsellable,llHeader;
		EditText etRemark;
		
	}
	
	private static class ViewHolderGroup
	{
		TextView tvItemDescription,tvItemCode;
		ImageView cbList1;
	}
	
	
	public class AddNewItemAdapter extends BaseExpandableListAdapter
	{
		private Vector<TrxDetailsDO> vecSearchedItems;
		private Context context;
		boolean isAllSelected = false;
		private ImageView ivSelectAll;
		private Vector<TrxDetailsDO> vecSelectedItems;
		
		public AddNewItemAdapter(Vector<TrxDetailsDO> vecSearchedItemsS,Context context) 
		{
			this.vecSearchedItems = vecSearchedItemsS;
			this.vecSelectedItems = new Vector<TrxDetailsDO>();
			this.context= context;
		}
		
		public Vector<TrxDetailsDO> getAssignedItems()
 		{
 			return this.vecSearchedItems;
 		} 

		public void selectAll(ImageView ivSelectAll)
		{
			this.ivSelectAll = ivSelectAll;
			vecSelectedItems.clear();
			if(!isAllSelected)
			{
				ivSelectAll.setImageResource(R.drawable.checkbox_white);
				this.vecSelectedItems.addAll(vecSearchedItems);
				isAllSelected = true;
			}
			else if(isAllSelected)
			{
				ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
				isAllSelected = false;
			}
			notifyDataSetChanged();
		}
		
		
		public void deSelectAll()
		{
			if(vecSelectedItems!=null)
				vecSelectedItems.clear();
			notifyDataSetChanged();
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
		
		public void setReason(TrxDetailsDO productDO)
		{
			if(vecSelectedItems != null && vecSelectedItems.size() > 0)
			{
				for(TrxDetailsDO productDO2 : vecSelectedItems)
				{
					if(productDO2.itemCode.equalsIgnoreCase(productDO.itemCode))
						productDO2.reason = productDO.reason;
				}
			}
		}
		
		public Vector<TrxDetailsDO> getSearchedItems()
		{
			return vecSearchedItems;
		}
		
		public void refresh(Vector<TrxDetailsDO> vecSearchedItemd)
		{
			if(vecSearchedItems!=null)
				vecSearchedItems.clear();
			notifyDataSetChanged();
			this.vecSearchedItems = vecSearchedItemd;
			notifyDataSetChanged();
		}
		
//		public void refreshList(Vector<TrxDetailsDO> vecSearchedItemd)
//		{
//			this.vecSearchedItems = vecSearchedItemd;
//			notifyDataSetChanged();
//		}

		@Override
		public int getGroupCount() {
			if(vecSearchedItems != null && vecSearchedItems.size()>0)
				return vecSearchedItems.size();
			else
				return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
				return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) 
		{
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
		public View getGroupView(final int groupPosition, boolean isExpanded,View convertView, ViewGroup parent)
		{
			final ViewHolderGroup viewHolderGroup;
			
			if(convertView == null)
			{
				viewHolderGroup 						= 	new ViewHolderGroup();
				convertView 			    		    = 	inflater.inflate(R.layout.return_order_header, null);
				viewHolderGroup.tvItemDescription 		= 	(TextView)convertView.findViewById(R.id.tvItemDescription);
				viewHolderGroup.tvItemCode 	     		= 	(TextView)convertView.findViewById(R.id.tvItemCode);
				viewHolderGroup.cbList1     			= 	(ImageView)convertView.findViewById(R.id.cbList1);
				
				convertView.setTag(viewHolderGroup);
			}
			else
				viewHolderGroup = (ViewHolderGroup) convertView.getTag();
		
			
			if(vecSearchedItems.size() > groupPosition)
			{
				final TrxDetailsDO objiItem   = 	vecSearchedItems.get(groupPosition);
				objiItem.UOM="PCS";//for return orders UOM always EA - Anil
				
				if(vecSelectedItems.contains(objiItem))
				{
					viewHolderGroup.cbList1.setImageResource(R.drawable.checkbox);
					viewHolderGroup.cbList1.setTag("1");
					lvExPopupList.expandGroup(groupPosition);
				}
				else
				{
					viewHolderGroup.cbList1.setImageResource(R.drawable.uncheckbox);
					viewHolderGroup.cbList1.setTag("0");
					lvExPopupList.collapseGroup(groupPosition);
				}
				
				viewHolderGroup.tvItemCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
				viewHolderGroup.tvItemDescription.setTypeface(AppConstants.Roboto_Condensed_Bold);
				viewHolderGroup.tvItemCode.setText(objiItem.itemCode);
				viewHolderGroup.tvItemDescription.setText(objiItem.itemDescription);
				viewHolderGroup.tvItemDescription.setTextColor(context.getResources().getColor(R.color.gray_dark));
				viewHolderGroup.tvItemCode.setTextColor(context.getResources().getColor(R.color.gray_dark));
				
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
						if(viewHolderGroup.cbList1.getTag().toString().equalsIgnoreCase("0"))
						{
							vecSelectedItems.add(objiItem);
							viewHolderGroup.cbList1.setImageResource(R.drawable.checkbox);
							viewHolderGroup.cbList1.setTag("1");
							if(objiItem.itemType == null || objiItem.itemType.equalsIgnoreCase(""))
								objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
							lvExPopupList.expandGroup(groupPosition);
						}
						else
						{
							vecSelectedItems.remove(objiItem);
							viewHolderGroup.cbList1.setImageResource(R.drawable.uncheckbox);
							viewHolderGroup.cbList1.setTag("0");
							lvExPopupList.collapseGroup(groupPosition);
							
						}
					}
				});
			}
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final TrxDetailsDO objiItem   = 	vecSearchedItems.get(groupPosition);
			
			final ViewHolder viewHolder;
			if(convertView == null)
			{
				
				viewHolder 				        =   new ViewHolder();
				convertView			   		    = 	(LinearLayout)getLayoutInflater().inflate(R.layout.result_cell, null);
				
				viewHolder.llHeader				=	(LinearLayout)convertView.findViewById(R.id.llHeader);
				viewHolder.llReasion			= 	(LinearLayout)convertView.findViewById(R.id.llReasion);
				
				viewHolder.llBottom				= 	(LinearLayout)convertView.findViewById(R.id.llBottom);
				viewHolder.etRemark				= 	(EditText)convertView.findViewById(R.id.etRemark);	
				viewHolder.btnCaptureImages		= 	(ImageView)convertView.findViewById(R.id.btnCaptureImages);
				
				viewHolder.ivReturnReason1		= 	(CheckBox) convertView.findViewById(R.id.ivReturnReason1);
//				viewHolder.ivReturnReason2	 	= 	(CheckBox) convertView.findViewById(R.id.ivReturnReason2);
				viewHolder.ivReturnReason3  	= 	(CheckBox) convertView.findViewById(R.id.ivReturnReason3);
//				viewHolder.ivReturnReason4		=	(CheckBox) convertView.findViewById(R.id.ivReturnReason4);

				viewHolder.ivIcon          		 = 	(ImageView)convertView.findViewById(R.id.ivIcon);
				viewHolder.llnonsellable 		 = 	(LinearLayout)convertView.findViewById(R.id.llnonsellable);
				
				
				convertView.setTag(viewHolder);
			}
			else
				viewHolder = (ViewHolder) convertView.getTag();
			
			
			viewHolder.llHeader.setVisibility(View.GONE);
		
//			if(viewHolder.ivIcon.getTag()!=null)
//				objiItem.vecDamageImages = vecSearchedItems.get(((Integer) viewHolder.ivIcon.getTag())).vecDamageImages;
//			else
				
			viewHolder.ivIcon.setTag(R.string.iconPosition,groupPosition);
			
			viewHolder.ivIcon.setOnClickListener(new OnClickListener() 
			{
				
				@Override
				public void onClick(View v) 
				{
					showDialogForGalleryImages(objiItem.vecDamageImages);
					
				}
			});
			
			viewHolder.etRemark.setTag(objiItem);
			viewHolder.etRemark.setText(((TrxDetailsDO)viewHolder.etRemark.getTag()).trxDetailsNote);
			
//			viewHolder.etBatch.setTag(objiItem);
//			viewHolder.etBatch.setText(((TrxDetailsDO)viewHolder.etRemark.getTag()).batchCode);
			viewHolder.btnCaptureImages.setTag(viewHolder.ivIcon);
			viewHolder.btnCaptureImages.setTag(R.string.groupPosition,groupPosition);
			
			viewHolder.btnCaptureImages.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					ivIconSel		= (ImageView) v.getTag();
					TrxDetailsDO objiItem = vecSearchedItems.get(((Integer) v.getTag(R.string.groupPosition)));
					Intent objIntent = new Intent(context, CaptureDamagedItemImage.class);
					objIntent.putExtra("objiItem", objiItem);
					startActivityForResult(objIntent, 500);
				}
			});

			viewHolder.etRemark.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						
						viewHolder.etRemark.addTextChangedListener(new TextWatcher() {
							
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
								
							}
							
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count,
									int after) {
								
							}
							
							@Override
							public void afterTextChanged(Editable s) {
								
								((TrxDetailsDO)viewHolder.etRemark.getTag()).trxDetailsNote = s.toString();
							}
						});
						
						viewHolder.etRemark.postDelayed(new Runnable() {
				                @Override
				                public void run() {
				                    if (!viewHolder.etRemark.hasFocus()) {
				                    	viewHolder.etRemark.requestFocus();
				                    }
				                }
				            }, 200);
					}

				}
			});

			if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason1)))
			{
				viewHolder.ivReturnReason1.setChecked(true);
//				viewHolder.ivReturnReason2.setChecked(false);
				viewHolder.ivReturnReason3.setChecked(false);
//				viewHolder.ivReturnReason4.setChecked(false);
				
				objiItem.reason = getString(R.string.ReturnReason1);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
			}
			else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason2)))
			{
//				viewHolder.ivReturnReason2.setChecked(true);
				viewHolder.ivReturnReason1.setChecked(false);
				viewHolder.ivReturnReason3.setChecked(false);
//				viewHolder.ivReturnReason4.setChecked(false);
				
				objiItem.reason = getString(R.string.ReturnReason2);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
			}
			else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason3)))
			{
				viewHolder.ivReturnReason3.setChecked(true);
				viewHolder.ivReturnReason1.setChecked(false);
//				viewHolder.ivReturnReason2.setChecked(false);
//				viewHolder.ivReturnReason4.setChecked(false);
				
				objiItem.reason = getString(R.string.ReturnReason3);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
			}
			else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason4)))
			{
//				viewHolder.ivReturnReason4.setChecked(true);
				viewHolder.ivReturnReason1.setChecked(false);
//				viewHolder.ivReturnReason2.setChecked(false);
				viewHolder.ivReturnReason3.setChecked(false);
				
				objiItem.reason = getString(R.string.ReturnReason4);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
			}
			else
			{
				viewHolder.ivReturnReason1.setChecked(true);
//				viewHolder.ivReturnReason2.setChecked(false);
				viewHolder.ivReturnReason3.setChecked(false);
//				viewHolder.ivReturnReason4.setChecked(false);
				
				objiItem.reason = getString(R.string.ReturnReason1);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
			}
			
			if (objiItem.itemType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE())) {
				viewHolder.ivIcon.setVisibility(View.GONE);
//				viewHolder.llnonsellable.setVisibility(View.GONE);
				viewHolder.btnCaptureImages.setVisibility(View.GONE);
				objiItem.reason = getString(R.string.sellable);
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
			}
			else
			{
				viewHolder.llnonsellable.setVisibility(View.VISIBLE);
				viewHolder.btnCaptureImages.setVisibility(View.VISIBLE);
				
				objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
				
				if(objiItem.vecDamageImages!=null && objiItem.vecDamageImages.size()>0)
				{
					viewHolder.ivIcon.setVisibility(View.VISIBLE);
					final Uri uri = Uri.parse(objiItem.vecDamageImages.get(0).ImagePath);
					if (uri != null) 
					{
						Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,viewHolder.ivIcon,objiItem.vecDamageImages.get(0).ImagePath));
						if (bitmap != null) 
						{
							viewHolder.ivIcon.setImageBitmap(bitmap);
						}
					}
				}
				else
					viewHolder.ivIcon.setVisibility(View.INVISIBLE);
			}
			
			viewHolder.ivReturnReason1.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					if(((CheckBox) v).isChecked())
					{
						objiItem.reason = "";
							
//						viewHolder.ivReturnReason2.setChecked(false);
						viewHolder.ivReturnReason3.setChecked(false);
//						viewHolder.ivReturnReason4.setChecked(false);
						
						objiItem.reason = context.getResources().getString(R.string.ReturnReason1);
						objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
					}
					else
						objiItem.reason = "";
					
					objiItem.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
					objiItem.trxDetailsNote = "";
					setReason(objiItem);
					viewHolder.etRemark.setText("");
				}
			});
			
//			viewHolder.ivReturnReason2.setOnClickListener(new OnClickListener()
//			{
//				
//				@Override
//				public void onClick(View v) 
//				{
//					if(((CheckBox) v).isChecked())
//					{
//						objiItem.reason = "";
//							
//						viewHolder.ivReturnReason1.setChecked(false);
//						viewHolder.ivReturnReason3.setChecked(false);
//						viewHolder.ivReturnReason4.setChecked(false);
//						objiItem.reason = context.getResources().getString(R.string.ReturnReason2);
//						objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_SELLABLE();
//					}
//					else
//						objiItem.reason = "";
//					
//					objiItem.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
//					objiItem.trxDetailsNote = "";
//					setReason(objiItem);
//					viewHolder.etRemark.setText("");
//				}
//			});
			
			viewHolder.ivReturnReason3.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v) 
				{
					if(((CheckBox) v).isChecked())
					{
						objiItem.reason = "";
							
						viewHolder.ivReturnReason1.setChecked(false);
//						viewHolder.ivReturnReason2.setChecked(false);
//						viewHolder.ivReturnReason4.setChecked(false);
						objiItem.reason = context.getResources().getString(R.string.ReturnReason3);
						objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
					}
					else
						objiItem.reason = "";
					
					objiItem.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
					objiItem.trxDetailsNote = "";
					setReason(objiItem);
					viewHolder.etRemark.setText("");
				}
			});
			
//			viewHolder.ivReturnReason4.setOnClickListener(new OnClickListener()
//			{
//				
//				@Override
//				public void onClick(View v) 
//				{
//					if(((CheckBox) v).isChecked())
//					{
//						objiItem.reason = "";
//							
//						viewHolder.ivReturnReason1.setChecked(false);
//						viewHolder.ivReturnReason2.setChecked(false);
//						viewHolder.ivReturnReason3.setChecked(false);
//						objiItem.reason = context.getResources().getString(R.string.ReturnReason4);
//						objiItem.itemType = TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE();
//					}
//					else
//						objiItem.reason = "";
//					
//					objiItem.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
//					objiItem.trxDetailsNote = "";
//					setReason(objiItem);
//					viewHolder.etRemark.setText("");
//				}
//			});
			objiItem.expiryDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}
	
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) SalesmanReturnOrder.this)
				.getApplication()).getHttpImageManager();
	}
	
	private int cyear, cmonth, cday, mPosition = 0;
	private TextView tempView;
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
				  
				  TrxDetailsDO objiItem = (TrxDetailsDO) tempView.getTag();
				  
				  if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason1)) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) < 0)
				  {
					  objiItem.expiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Expired date can't be future date.", "OK", null, "");
				  }
				  else if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason2)) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
				  {
					  objiItem.expiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be past date.", "OK", null, "");
				  }
				  else if(objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason2)) && Math.abs(CalendarUtils.getDateDiffInInt(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime())) >=30)
				  {
					  objiItem.expiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Soon to expire date should not be more than one month.", "OK", null, "");
				  }
				  else if((objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.ReturnReason3)) || objiItem.reason.equalsIgnoreCase(getResources().getString(R.string.return_))) && CalendarUtils.getDateDifferenceInMinutesNew(year+"-"+strMonth+"-"+strDate, CalendarUtils.getCurrentDateTime()) > 0)
				  {
					  objiItem.expiryDate = "";
					  ((TextView)tempView).setText("");
					  showCustomDialog(SalesmanReturnOrder.this, getString(R.string.warning), "Expiry date can't be past date.", "OK", null, "");
				  }
				  else
				  {
					  ((TextView)tempView).setText(CalendarUtils.getCommonDateFormat(monthOfYear+1, dayOfMonth, year));
					  objiItem.expiryDate = year+"-"+strMonth+"-"+strDate;
					  
					  if(adapter != null)
						  adapter.setExpiryDate(objiItem);
				  }
			  }
		  }
	  };
	  
	  
	  protected void onActivityResult(int requestCode, int resultCode, Intent data)
	  {
		    if(resultCode == 500)
	    	{
				if(data != null)
				{
					if(adapter != null&&!AppConstants.iconpaths.equalsIgnoreCase("") && adapter.getSearchedItems()!=null)
					{
						try
						{
							TrxDetailsDO objiItem = adapter.getSearchedItems().get(((Integer) ivIconSel.getTag(R.string.iconPosition)));
							objiItem.vecDamageImages = ((TrxDetailsDO) data.getExtras().get("objIntent")).vecDamageImages;
							ivIconSel.setVisibility(View.VISIBLE);
							if(adapter!=null)
								adapter.notifyDataSetChanged();
							ivIconSel = null;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
	  }
	  
	  private void setBitmapImage(final ImageView imageView, String capturedImageFilePath)
		{
			
			
			Bitmap stampBitmap = decodeFile(new File(capturedImageFilePath), (int)(44 * px), (int)(44 * px));
	        if(stampBitmap != null)
	        {
	   	    	
	   	    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	   	    	stampBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	   	    	
				
				final WeakReference<Bitmap> reference = new WeakReference<Bitmap>(stampBitmap);
				
	   	    	runOnUiThread(new Runnable()
	   	    	{
					@Override
					public void run() 
					{
						imageView.setImageBitmap(reference.get());
					}
				});
	        }
		}
		
		public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
			try {
				// Decode image size
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(new FileInputStream(f), null, o);

				// The new size we want to scale to
				final int REQUIRED_WIDTH = WIDTH;
				final int REQUIRED_HIGHT = HIGHT;
				// Find the correct scale value. It should be the power of 2.
				int scale = 1;
				while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
						&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
					scale *= 2;

				// Decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			} catch (FileNotFoundException e) {
			}
			return null;
		}
	  
	  private void setDate(TextView tvView, TrxDetailsDO objiItem1)
	  {
//		  setDate((TextView) v, objiItem);
		  tempView = tvView;
			tempView.setTag(objiItem1);
			showDialog(0);
	  }
	  public Vector<TrxDetailsDO> getSearchItems(final String searchText) {
		  Predicate<TrxDetailsDO> searchItem =null;
		  if(!TextUtils.isEmpty(searchText))
		  {
			  searchItem = new Predicate<TrxDetailsDO>() 
			  {
					public boolean apply(TrxDetailsDO trxDetailsDO) 
					{
						return (trxDetailsDO.itemDescription.toLowerCase().contains(searchText.toLowerCase())
								|| trxDetailsDO.itemCode.toLowerCase().contains(searchText.toLowerCase())
								|| trxDetailsDO.categoryId.toLowerCase().contains(categoryID)) 
								&& trxDetailsDO.productStatus!=-100;
					}
				};
				
		  }
		  else
		  {
			  searchItem = new Predicate<TrxDetailsDO>() 
					  {
					public boolean apply(TrxDetailsDO trxDetailsDO) 
					{
						return trxDetailsDO.productStatus!=-100;
					}
				};
		  }
		  
		  Collection<TrxDetailsDO> filteredResult;
		  
		  if(vecTemp == null || vecTemp.size()==0)
			  filteredResult = filter(vecSearchedItemd, searchItem);
		  else
			  filteredResult = filter(vecTemp, searchItem);
		  if(filteredResult!=null)
			  return new Vector<TrxDetailsDO>((ArrayList<TrxDetailsDO>) filteredResult);
		  else return new Vector<TrxDetailsDO>();
	  }
	  
	  
	  Vector<TrxDetailsDO> vecTemp = null;
	  public Vector<TrxDetailsDO> getCategoryItems(final String searchText)
	  {
		  if(vecTemp == null)
			  vecTemp = new Vector<TrxDetailsDO>();
		  else
			  vecTemp.clear();
		  if(searchText.equalsIgnoreCase("ALL"))
			  vecTemp.addAll(vecSearchedItemd);
		  else
		  {
			  for(TrxDetailsDO trxDetailsDO : vecSearchedItemd)
			  {
				  if(trxDetailsDO.categoryId.equalsIgnoreCase(searchText))
					  vecTemp.add(trxDetailsDO);
			  }
		  }
		  
		  return vecTemp;
	  }
	  
	  public Vector<TrxDetailsDO> getSearchedItems(final String searchText)
	  {
		  synchronized (SYNCPRICECAL) 
		  {
			  vecTemp = new Vector<TrxDetailsDO>();
			  if(vecSearchedItemd != null && vecSearchedItemd.size() >0)
			  {
				  if(searchText != null && searchText.equalsIgnoreCase(""))
					  vecTemp.addAll(vecSearchedItemd);
				  else
				  {
					  vecTemp = new Vector<TrxDetailsDO>();
					  for(TrxDetailsDO trxDetailsDO : vecSearchedItemd)
					  {
//					  if(trxDetailsDO.categoryId.equalsIgnoreCase(searchText))
						  if(trxDetailsDO.itemDescription.toLowerCase().contains(searchText.toLowerCase())
								  || trxDetailsDO.itemCode.toLowerCase().contains(searchText.toLowerCase()))
							  vecTemp.add(trxDetailsDO);
					  }
				  }
			  }
			  return vecTemp;
		  }
	  }


	  public void showDialogForGalleryImages(final Vector<DamageImageDO> vecDamageImages)
	  {
			final Gallery_Dialog objGallery_Dialog = new Gallery_Dialog(SalesmanReturnOrder.this);
			objGallery_Dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			objGallery_Dialog.show();
			
			ViewPager pager = objGallery_Dialog.viewPager;
			final TextView tvCount = objGallery_Dialog.tvCount;
			
			GalleryAdapter adapter = new GalleryAdapter(SalesmanReturnOrder.this, vecDamageImages);
			
			pager.setAdapter(adapter);
			pager.setCurrentItem(0);
			StringBuilder sb = new StringBuilder();
			sb.append("(").append(1).append("/").append(vecDamageImages.size()).append(")");
			tvCount.setText(sb.toString());

			
			pager.setOnPageChangeListener(new OnPageChangeListener() 
			{
				
				@Override
				public void onPageSelected(int pos) 
				{
					StringBuilder sb = new StringBuilder();
					sb.append("(").append(pos+1).append("/").append(vecDamageImages.size()).append(")");
					tvCount.setText(sb.toString());
					
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2)
				{
					
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0)
				{
					
				}
			});
		  
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
