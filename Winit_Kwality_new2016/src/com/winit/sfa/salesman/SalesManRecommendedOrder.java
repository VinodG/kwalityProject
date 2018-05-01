package com.winit.sfa.salesman;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.ListBrandRecomendedFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.ClassificationDO;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PromotionDO;
import com.winit.alseer.salesman.dataobject.SlabBasedDiscountDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

public class SalesManRecommendedOrder extends BaseActivity implements SyncProcessListner
{
	private LinearLayout llStatementDiscValue,llStatementDiscount,llrecommended_order,llLimit,llFOCTotalQty,llDiscount,llOrderValue,llNETValue,llDiscValue,llSpclDiscount,llSplDiscValue,llAvailableLimit;
	private JourneyPlanDO objJourneyPlan;
	private TextView tvHeader,tvSubCategory, tvAgenciesName,tvOrderValue,tvNetValuet,tvVATValuet, tvDiscValue,tvTotalValue,tvDiscountPer,tvTotalQty,
			tvFOCTotalQty,tvOrderValueTitle,tvNetValueTitle,tvVATValueTitle,tvOrderQtyColon,tvOrderColon,tvDiscountColon,tvNetColon,tvSplDiscountPer,tvSplDiscValue,tvSptatementDiscountPer,
			tvSplDiscountColon,tvInvoiceAmount,tvAvailableText,tvstmntDiscValue;
	private Button btnSubmit,btnPromotion;
	private  EditText etAmountdecimal;
	private PagerSlidingTabStrip tabs;
//	private PagerSlidingTabStripWithYelloBar tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private LinkedHashMap<String, TrxDetailsDO> hmDistinctModifiedItem;
	public int TRX_TYPE = 0,TRX_SUB_TYPE=0;
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
	private String SYNCPRICECAL="SYNCPRICECAL";
	private TrxHeaderDO trxHeaderDO;
	private Vector<CategoryDO> vecAllCategories=new Vector<CategoryDO>();
//	private LinearLayout llCategory,llSubCategory;
	private ImageView ivAllItems,ivFilterByInventoryQty;
	private EditText etSearch;
	private String selectedCategory="",selectedSubCategory="";
	private HashMap<String, TrxDetailsDO> hmSavedItems = null;
	private TrxHeaderDO trxSavedHeader = null;
	
	private String strFilter;
	private final String VAN_INVENTORY = "Van Inventory",ALL_CATEGORIES = "All Categories";
	
	private ImageView ivSearchCross;
	private TextView tvAvailableLimitValue,dot;/*, tvCreditLimitValue*/;
	private CustomerCreditLimitDo creditLimit;
	private String Invoice_Type = "";
	public float promotionDisc = 0,rateDiffPercent = 0;
	private float /*discAmount = 0,*/ rateDiffAmount = 0;
	private boolean isFromSaved = false;
	
	private String strVisitCode = "";
	private int Division = 0;
	private boolean isfromsalesorder=false;//tblCustomer
	
	@Override
	public void initialize()
	{
		
//		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
//			strFilter = ALL_CATEGORIES;
//		else
			strFilter = VAN_INVENTORY;
		
		LogUtils.debug("initialize", "start");
		ivDivider.setVisibility(View.VISIBLE);
		btnCartCount.setVisibility(View.VISIBLE);
		ivShoppingCart.setVisibility(View.VISIBLE);
		btnCartCount.bringToFront();
		
		llrecommended_order = (LinearLayout)inflater.inflate(R.layout.recommended_order, null);
		llBody.addView(llrecommended_order,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	
		hmDistinctModifiedItem = new LinkedHashMap<String, TrxDetailsDO>();
		
		if(getIntent().getExtras() != null)
		{
			objJourneyPlan 	= (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			if(getIntent().hasExtra("TRX_TYPE"))
				TRX_TYPE	= getIntent().getIntExtra("TRX_TYPE",0); 
			
			if(getIntent().hasExtra("TRX_SUB_TYPE"))
				TRX_SUB_TYPE	= getIntent().getIntExtra("TRX_SUB_TYPE",0);
			
			if(getIntent().hasExtra("hmSavedItems"))
				hmSavedItems = (HashMap<String, TrxDetailsDO>) getIntent().getExtras().get("hmSavedItems");
				
			if(getIntent().hasExtra("trxSavedHeader"))
				trxSavedHeader = (TrxHeaderDO)getIntent().getExtras().get("trxSavedHeader");
			
			if(getIntent().hasExtra("Invoice_Type"))
				Invoice_Type = (String)getIntent().getExtras().get("Invoice_Type");
			
			if(getIntent().hasExtra(AppConstants.DIVISION))
				Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
			
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
		
		
		if(TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
		{
			llLimit.setVisibility(View.GONE);
			tvHeader.setText("Tele Order");
			ivFilterByInventoryQty.setVisibility(View.GONE);
			ivAllItems.setVisibility(View.GONE);
		}
		else if(TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER())
		{
			tvHeader.setText("Saved Order");
			TRX_TYPE = trxSavedHeader.trxType; 
			rateDiffAmount = trxSavedHeader.rateDiff;
			promotionDisc = (float)StringUtils.getFloat(decimalFormat.format(trxSavedHeader.specialDiscPercent))-StringUtils.getFloat(decimalFormat.format(((rateDiffAmount*100)/trxSavedHeader.totalAmount)));
			tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc)+")");
			
			llFOCTotalQty.setVisibility(View.GONE);
			tvOrderQtyColon.setVisibility(View.GONE);
			tvTotalQty.setVisibility(View.GONE);
			
			tvOrderValueTitle.setVisibility(View.VISIBLE);
			tvNetValueTitle.setVisibility(View.VISIBLE);
			llDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscValue.setVisibility(View.VISIBLE);
			llOrderValue.setVisibility(View.VISIBLE);
			llNETValue.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			llSpclDiscount.setVisibility(View.VISIBLE);
			llSplDiscValue.setVisibility(View.VISIBLE);
			tvSplDiscountColon.setVisibility(View.VISIBLE);
			tvDiscountColon.setVisibility(View.VISIBLE);
			tvNetColon.setVisibility(View.VISIBLE);
			tvOrderColon.setVisibility(View.VISIBLE);
			
			tvDiscValue.setVisibility(View.VISIBLE);
			tvTotalValue.setVisibility(View.VISIBLE);
			tvOrderValue.setVisibility(View.VISIBLE);
			tvNetValuet.setVisibility(View.VISIBLE);
			tvVATValuet.setVisibility(View.VISIBLE);
		}
		else if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
		{
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				tvHeader.setText("Presales Order");	
			}
			else
			{
				tvHeader.setText("Advance Order");
			}
			llLimit.setVisibility(View.VISIBLE);
			
			llFOCTotalQty.setVisibility(View.GONE);
			tvOrderQtyColon.setVisibility(View.GONE);
			tvTotalQty.setVisibility(View.GONE);
			
			tvOrderValueTitle.setVisibility(View.VISIBLE);
			tvNetValueTitle.setVisibility(View.VISIBLE);
			tvVATValueTitle.setVisibility(View.VISIBLE);
			llDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscValue.setVisibility(View.VISIBLE);
			llOrderValue.setVisibility(View.VISIBLE);
			llNETValue.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			llSpclDiscount.setVisibility(View.VISIBLE);
			llSplDiscValue.setVisibility(View.VISIBLE);
			tvSplDiscountColon.setVisibility(View.VISIBLE);
			tvDiscountColon.setVisibility(View.VISIBLE);
			tvNetColon.setVisibility(View.VISIBLE);
			tvOrderColon.setVisibility(View.VISIBLE);
			
			tvDiscValue.setVisibility(View.VISIBLE);
			tvTotalValue.setVisibility(View.VISIBLE);
			tvOrderValue.setVisibility(View.VISIBLE);
			tvNetValuet.setVisibility(View.VISIBLE);
			tvVATValuet.setVisibility(View.VISIBLE);
		}
		else if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				tvHeader.setText("Presales Order");	
			}
			//have to check condition.
			if(trxSavedHeader != null)
			{
				rateDiffAmount = trxSavedHeader.rateDiff;
				promotionDisc = (float)StringUtils.getFloat(decimalFormat.format(trxSavedHeader.specialDiscPercent))-StringUtils.getFloat(decimalFormat.format(((rateDiffAmount*100)/trxSavedHeader.totalAmount)));
				tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc)+")");
			}
			llLimit.setVisibility(View.VISIBLE);
			
			llFOCTotalQty.setVisibility(View.GONE);
			tvOrderQtyColon.setVisibility(View.GONE);
			tvTotalQty.setVisibility(View.GONE);
			
			tvOrderValueTitle.setVisibility(View.VISIBLE);
			tvNetValueTitle.setVisibility(View.VISIBLE);
			tvVATValueTitle.setVisibility(View.VISIBLE);
			llDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscount.setVisibility(View.VISIBLE);
			llOrderValue.setVisibility(View.VISIBLE);
			llNETValue.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			llSpclDiscount.setVisibility(View.VISIBLE);
			llSplDiscValue.setVisibility(View.VISIBLE);
			tvSplDiscountColon.setVisibility(View.VISIBLE);
			tvDiscountColon.setVisibility(View.VISIBLE);
			tvNetColon.setVisibility(View.VISIBLE);
			tvOrderColon.setVisibility(View.VISIBLE);
			
			tvDiscValue.setVisibility(View.VISIBLE);
			tvTotalValue.setVisibility(View.VISIBLE);
			tvOrderValue.setVisibility(View.VISIBLE);
			tvNetValuet.setVisibility(View.VISIBLE);
			tvVATValuet.setVisibility(View.VISIBLE);

			if(Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Cash) || Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Credit))
				isfromsalesorder=true;
		}
		else if(TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			tvHeader.setText("FOC Order");
			tvInvoiceAmount.setText("FOC Amount");
			llLimit.setVisibility(View.GONE);
			
			llFOCTotalQty.setVisibility(View.VISIBLE);
			tvOrderQtyColon.setVisibility(View.VISIBLE);
			tvTotalQty.setVisibility(View.VISIBLE);
			
			ivFilterByInventoryQty.setVisibility(View.GONE);
			ivAllItems.setVisibility(View.GONE);
			
			tvOrderValueTitle.setVisibility(View.GONE);
			tvNetValueTitle.setVisibility(View.GONE);
			tvVATValueTitle.setVisibility(View.VISIBLE);
			llDiscount.setVisibility(View.GONE);
			llStatementDiscount.setVisibility(View.GONE);
			llStatementDiscValue.setVisibility(View.GONE);
			dot.setVisibility(View.GONE);
			
			llOrderValue.setVisibility(View.GONE);
			llNETValue.setVisibility(View.GONE);
			llDiscValue.setVisibility(View.GONE);
			tvDiscountColon.setVisibility(View.GONE);
			tvNetColon.setVisibility(View.GONE);
			tvOrderColon.setVisibility(View.GONE);
			llSpclDiscount.setVisibility(View.GONE);
			llSplDiscValue.setVisibility(View.GONE);
			tvSplDiscountColon.setVisibility(View.GONE);
			
//			objJourneyPlan.statementdiscount=0.00+"";
		}
		else
		{
			if(Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Cash))
				tvHeader.setText("Cash Invoice");
			else if(Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Credit))
//				tvHeader.setText("Credit Invoice");
				tvHeader.setText("Sales Invoice");
			else 
				tvHeader.setText("Recommended Order");
			
			llFOCTotalQty.setVisibility(View.GONE);
			tvOrderQtyColon.setVisibility(View.GONE);
			tvTotalQty.setVisibility(View.GONE);
			
			llSpclDiscount.setVisibility(View.VISIBLE);
			llSplDiscValue.setVisibility(View.VISIBLE);
			tvSplDiscountColon.setVisibility(View.VISIBLE);
			
			tvOrderValueTitle.setVisibility(View.VISIBLE);
			tvNetValueTitle.setVisibility(View.VISIBLE);
			tvVATValueTitle.setVisibility(View.VISIBLE);
			llDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscount.setVisibility(View.VISIBLE);
			llOrderValue.setVisibility(View.VISIBLE);
			llNETValue.setVisibility(View.VISIBLE);
			llDiscValue.setVisibility(View.VISIBLE);
			tvDiscountColon.setVisibility(View.VISIBLE);
			tvNetColon.setVisibility(View.VISIBLE);
			tvOrderColon.setVisibility(View.VISIBLE);
			
			tvDiscValue.setVisibility(View.VISIBLE);
			tvTotalValue.setVisibility(View.VISIBLE);
			tvOrderValue.setVisibility(View.VISIBLE);
			tvNetValuet.setVisibility(View.VISIBLE);
			tvVATValuet.setVisibility(View.VISIBLE);
			if(Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Cash) || Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Credit))
						isfromsalesorder=true;
		}
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				hideCustomKeyBoard();
				hideKeyBoard(tvUserName);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		LogUtils.debug("initialize", "end");
		
		loadData();
		
		tvSubCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Vector<CategoryDO> filteredCategory=getCategoryByType(1);
				if(filteredCategory!=null && filteredCategory.size() > 0){
					filteredCategory.get(0).categoryName="All Categories";
					showCategories(tvSubCategory, filteredCategory, "Select Category",1);
				}
			}
		});
		
		ivFilterByInventoryQty.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				if(!strFilter.equalsIgnoreCase(VAN_INVENTORY))
				{
					ivFilterByInventoryQty.setBackgroundResource(R.drawable.vaninventory_h);
					ivAllItems.setBackgroundResource(R.drawable.allitems);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							strFilter = VAN_INVENTORY;
							
							getSearchItems(etSearch.getText().toString(), false);
						}
					}, 2);
				}
			}
		});
		ivAllItems.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				if(!strFilter.equalsIgnoreCase(ALL_CATEGORIES))
				{
					ivFilterByInventoryQty.setBackgroundResource(R.drawable.vaninventory);
					ivAllItems.setBackgroundResource(R.drawable.allitems_h);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							strFilter = ALL_CATEGORIES;
							getSearchItems(etSearch.getText().toString(), true);
						}
					}, 2);
				}
			}
		});
		
		creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailss.site);
		if(creditLimit!=null)
		{
//			tvCreditLimitValue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.creditLimit))+"");
			tvAvailableLimitValue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.availbleLimit))+"");
		}
	}
	private boolean isItemAvail(String itemCode){
		boolean filterInventoryItems = strFilter.equalsIgnoreCase(ALL_CATEGORIES)?false:true;
		if(filterInventoryItems)
			return hmInventory.containsKey(itemCode);
		else
			return true;
	}
	private final String SALES_RECOMEMDED_SEARCH = "SALES_RECOMEMDED_SEARCH";
	public void getSearchItems(final String searchText, final boolean isByCategory) {

		synchronized (SALES_RECOMEMDED_SEARCH)
		{
			try
			{
				final String strText = searchText; // edtSearch.getText().toString();
				final Vector<ClassificationDO> tmp =new Vector<ClassificationDO>();
				if(vecDetails!=null){
					for(ClassificationDO classificationDO:vecDetails) {
						ClassificationDO obj=new ClassificationDO();
						HashMap<String, Vector<TrxDetailsDO>> tmpSearched = new HashMap<String, Vector<TrxDetailsDO>>();
						for (String key : classificationDO.hmProducts.keySet()) {
							Predicate<TrxDetailsDO> searchItem = null;
							if(TextUtils.isEmpty(strText)){
								
								if(!TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return trxDetailsDO.categoryId.toLowerCase()
													.equalsIgnoreCase(selectedCategory)&& trxDetailsDO.subcategory.toLowerCase()
													.equalsIgnoreCase(selectedSubCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};	
								}else if(TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return  trxDetailsDO.subcategory.toLowerCase()
													.equalsIgnoreCase(selectedSubCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};	
								}else if(!TextUtils.isEmpty(selectedCategory) && TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return trxDetailsDO.categoryId.toLowerCase()
													.equalsIgnoreCase(selectedCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};	
								}else{
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return isItemAvail(trxDetailsDO.itemCode);
										}
									};}
							}else{
								if(!TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return (trxDetailsDO.itemDescription.toLowerCase()
													.contains(strText.toLowerCase()) || trxDetailsDO.itemCode
													.toLowerCase().contains(
															strText.toLowerCase()))
															&& trxDetailsDO.categoryId.toLowerCase()
															.equalsIgnoreCase(selectedCategory)&& trxDetailsDO.subcategory.toLowerCase()
															.equalsIgnoreCase(selectedSubCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};	
								}else if(TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return (trxDetailsDO.itemDescription.toLowerCase()
													.contains(strText.toLowerCase()) || trxDetailsDO.itemCode
													.toLowerCase().contains(
															strText.toLowerCase()))
															&&  trxDetailsDO.subcategory.toLowerCase()
															.equalsIgnoreCase(selectedSubCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};	
								}else if(!TextUtils.isEmpty(selectedCategory) && TextUtils.isEmpty(selectedSubCategory)){
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return (trxDetailsDO.itemDescription.toLowerCase()
													.contains(strText.toLowerCase()) || trxDetailsDO.itemCode
													.toLowerCase().contains(
															strText.toLowerCase()))&&  trxDetailsDO.categoryId.toLowerCase()
															.equalsIgnoreCase(selectedCategory)&&isItemAvail(trxDetailsDO.itemCode);
										}
									};
									
								}
								else{
									searchItem = new Predicate<TrxDetailsDO>() {
										public boolean apply(TrxDetailsDO trxDetailsDO) {
											return (trxDetailsDO.itemDescription.toLowerCase()
													.contains(strText.toLowerCase()) || trxDetailsDO.itemCode
													.toLowerCase().contains(
															strText.toLowerCase()))&&isItemAvail(trxDetailsDO.itemCode);
										}
									};
									
								}
							}
							boolean filterInventoryItems = strFilter.equalsIgnoreCase(ALL_CATEGORIES)?false:true;
							if (searchItem!=null &&(!TextUtils.isEmpty(searchText)
									|| !TextUtils.isEmpty(selectedCategory) || !TextUtils.isEmpty(selectedSubCategory)||filterInventoryItems)) {
								
								Collection<TrxDetailsDO> filteredResult = filter(classificationDO.hmProducts.get(key), searchItem);
								if (filteredResult != null && filteredResult.size() > 0) {
									tmpSearched.put(key, new Vector<TrxDetailsDO>(
											(ArrayList<TrxDetailsDO>) filteredResult));
								}
							} else {
								tmpSearched = classificationDO.hmProducts;
							}
						}
						obj.hmProducts=tmpSearched;
						obj.Code=classificationDO.Code;
						obj.Description=classificationDO.Description;
						obj.arrBrands=classificationDO.arrBrands;
						obj.SellingSKUClassificationId=classificationDO.SellingSKUClassificationId;
						obj.Sequence=classificationDO.Sequence;
						tmp.add(obj);
					}			
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adapter!=null){
							adapter.refresh(tmp);
							tabs.notifyDataSetChanged();
						}
						
					}
				});
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	private void showCategories(final View v, Vector<CategoryDO> vacCategoryDOs,String title,final int type) 
	 {
		 CustomBuilder customBuilder = new CustomBuilder(SalesManRecommendedOrder.this, title, false);
		 customBuilder.setSingleChoiceItems(vacCategoryDOs, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				etSearch.setText("");
				builder.dismiss();
				switch (type) {
				case 1:
					if(selectedCategory!=null && !selectedCategory.equalsIgnoreCase(((CategoryDO)selectedObject).categoryId)){
						selectedSubCategory=null;
//						tvSubCategory.setText("All Category");
					}
					((TextView)v).setText(((CategoryDO)selectedObject).categoryName);
					v.setTag((CategoryDO)selectedObject);
					selectedCategory=((CategoryDO)selectedObject).categoryId;
					if(selectedCategory.equalsIgnoreCase("ALL"))
						selectedCategory=null;
					getSearchItems(null, true);

					break;
				case 2:
					selectedSubCategory=((CategoryDO)selectedObject).categoryId;
					if(selectedSubCategory.equalsIgnoreCase("ALL"))
						selectedSubCategory=null;
					getSearchItems(null, true);
					break;
				default:
					break;
				}
				
			}
		});
		 customBuilder.show();
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
				if(TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_DELIVERY())
					showReasonForFOC();
				else if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER) && TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER())
					showReasonForFOC();
				else
					gotoNextActivity();
			}
		});
		btnPromotion.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				showPromotionPopup();
				showValuePopup();
			}
		});
	}
	float firstPromo = 3,secondPromo = 5;
	private void showPromotionPopup() 
	{
		LinearLayout viewDetails 			= (LinearLayout)LayoutInflater.from(SalesManRecommendedOrder.this).inflate(R.layout.promotion_popup, null);
        
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
		 final Dialog dialogDetails = new Dialog(SalesManRecommendedOrder.this, android.R.style.Theme_Holo_Dialog);
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
				LogUtils.debug("negative", ""+objJourneyPlan.statementdiscount);
				LogUtils.debug("negative", ""+rateDiffPercent);
				LogUtils.debug("negative", ""+firstPromo);
				
				if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + rateDiffPercent + firstPromo) <= 100)
				{
					promotionDisc 	= firstPromo;
//				discAmount 		= -1;
					calculatePrice(true);					
					updateTRXDetail();					
					double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
					tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				}
				else
					showCustomDialog(SalesManRecommendedOrder.this,getString(R.string.warning), "Please adjust Rate diff percent.", getString(R.string.OK), null, "");
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
					calculatePrice(true);
					
					updateTRXDetail();
					
					double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
					tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
				}
				else
					showCustomDialog(SalesManRecommendedOrder.this,getString(R.string.warning), "Please adjust Rate diff percent.", getString(R.string.OK), null, "");
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
				calculatePrice(true);
				
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
			TrxDetailsDO objtrxdetailDo = hmDistinctModifiedItem.get(itemcode);
			objtrxdetailDo.promotionalDiscountAmount = calculateDiscount(objtrxdetailDo);
			objtrxdetailDo.statementDiscountAmnt = calculatestatementDiscount(objtrxdetailDo);
			
			objtrxdetailDo.calculatedstatementDiscountPercentage = calculatestatementDiscountPercentage(objtrxdetailDo);
			objtrxdetailDo.calculatedDiscountPercentage = calculateDiscountPercentage(objtrxdetailDo);
			
			
			objtrxdetailDo.calculatedDiscountAmount = objtrxdetailDo.promotionalDiscountAmount + objtrxdetailDo.statementDiscountAmnt;
			objtrxdetailDo.calculatedstatementDiscountAmount = objtrxdetailDo.statementDiscountAmnt;
		}				
	}
	
	private void showValuePopup()
	{
		View view = inflater.inflate(R.layout.eot_popup, null);
		final CustomDialog customDialogs = new CustomDialog(SalesManRecommendedOrder.this, view, preference	.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,LayoutParams.WRAP_CONTENT, true);
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
					showCustomDialog(SalesManRecommendedOrder.this,getString(R.string.warning), "Please enter discount amount.", getString(R.string.OK), null, "");
				else if(StringUtils.getFloat(etAmountdecimal.getText().toString()) > (totalIPrice - customerDiscount-customerstatementdiscount))//Abhijit
					showCustomDialog(SalesManRecommendedOrder.this,getString(R.string.warning), "Please provide Rate diff amount less than Total Invoice Amount.", getString(R.string.OK), null, "");
				else
				{
					showReasonForPromotion(customDialogs);
//					promotionDisc   = 0;
//					calculatePrice(true);
					
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
//							calculatePrice(true);
//							double totaldiscountamount1 = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
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
	private void calculateRateDiffDiscount() 
	{
		if(rateDiffAmount > 0)
		{//StringUtils.getFloat(decimalFormat.format((csPrice * quanity)));
			if(trxSavedHeader!=null && trxSavedHeader.specialDiscPercent>0)
			{
				rateDiffPercent=trxSavedHeader.specialDiscPercent;
			}
			else
			{
				double totalOrderAmount = totalIPrice;
				if (totalOrderAmount <= 0)
					totalOrderAmount = 1;
				rateDiffPercent = StringUtils.getFloat(decimalFormat.format(((StringUtils.getFloat(decimalFormat.format(rateDiffAmount)) * 100) / totalOrderAmount)));

				LogUtils.debug("rate_Diff", "totalOrderAmount:" + totalOrderAmount);
				LogUtils.debug("rate_Diff", "rateDiffAmount:" + rateDiffAmount);
				LogUtils.debug("rate_Diff", "rateDiffPercent:" + rateDiffPercent);
			}
		}
		else
			rateDiffPercent = 0;
	}
	
	private void gotoNextActivity() 
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
				String availableOrderId = new OrderDA().getOrderIdBasedOnType(AppConstants.Order);
				if(TextUtils.isEmpty(availableOrderId))
					isSequenceNumberAvailable=false;
				
				if(isSequenceNumberAvailable)
					trxHeaderDO = prepareSalesOrder();
				
				runOnUiThread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						if(trxHeaderDO!=null)
						{
							Intent intent = new Intent(SalesManRecommendedOrder.this, SalesmanOrderPreview.class);
							intent.putExtra("trxHeaderDO", trxHeaderDO);
							intent.putExtra("mallsDetails", objJourneyPlan);
							intent.putExtra("TRX_TYPE",trxHeaderDO.trxType);
							intent.putExtra("TRX_SUB_TYPE",trxHeaderDO.trxSubType);
							intent.putExtra("Invoice_Type", Invoice_Type);
							intent.putExtra("focItemCount", focItemCount);
							if((!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER) && objJourneyPlan.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT) && TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()) || (!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER) && isfromsalesorder && objJourneyPlan.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)))
							intent.putExtra("SalesOrder", true);
							intent.putExtra(AppConstants.DIVISION, Division);
							startActivityForResult(intent, 1000);
						 }
						else if(!isSequenceNumberAvailable){
							showCustomDialog(SalesManRecommendedOrder.this, getString(R.string.warning), "Sequence numbers are not synced. Please sync now.", getString(R.string.sync_now), getString(R.string.not_now), "Syncnow");
						}
						 else
							showCustomDialog(SalesManRecommendedOrder.this, getString(R.string.warning), "Please select atleast one item having quantity more than 0.", getString(R.string.OK), null, "");
						
						new Handler().postDelayed(new Runnable() {
							
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
		}).start();
	}
	private void intializeControls() 
	{
		tvHeader 	 		= (TextView)llrecommended_order.findViewById(R.id.tvHeader);
		btnSubmit 	 		= (Button) llrecommended_order.findViewById(R.id.btnSubmit);
		btnPromotion 		= (Button) llrecommended_order.findViewById(R.id.btnPromotion);
		tvAgenciesName		= (TextView) llrecommended_order.findViewById(R.id.tvAgenciesName);
		tabs 	 			= (PagerSlidingTabStrip) llrecommended_order.findViewById(R.id.tabs);
		pager 				= (ViewPager) llrecommended_order.findViewById(R.id.pager);
		tvOrderValue		= (TextView) llrecommended_order.findViewById(R.id.tvOrderValue);
		tvNetValuet		= (TextView) llrecommended_order.findViewById(R.id.tvNetValuet);
		tvVATValuet		= (TextView) llrecommended_order.findViewById(R.id.tvVATValuet);
		tvDiscValue			= (TextView) llrecommended_order.findViewById(R.id.tvDiscValue);
		tvstmntDiscValue			= (TextView) llrecommended_order.findViewById(R.id.tvstmntDiscValue);
		tvTotalValue		= (TextView) llrecommended_order.findViewById(R.id.tvTotalValue);
		ivAllItems			= (ImageView) llrecommended_order.findViewById(R.id.ivAllItems);
		tvAvailableLimitValue = (TextView) llrecommended_order.findViewById(R.id.tvAvailableLimitValue);
		tvDiscountPer		= (TextView) llrecommended_order.findViewById(R.id.tvDiscountPer);
		tvTotalQty			= (TextView) llrecommended_order.findViewById(R.id.tvTotalQty);
		tvFOCTotalQty		= (TextView) llrecommended_order.findViewById(R.id.tvFOCTotalQty);
		tvOrderValueTitle	= (TextView) llrecommended_order.findViewById(R.id.tvOrderValueTitle);
		tvNetValueTitle	= (TextView) llrecommended_order.findViewById(R.id.tvNetValueTitle);
		tvVATValueTitle	= (TextView) llrecommended_order.findViewById(R.id.tvVATValueTitle);
		tvOrderQtyColon		= (TextView) llrecommended_order.findViewById(R.id.tvOrderQtyColon);
		tvOrderColon		= (TextView) llrecommended_order.findViewById(R.id.tvOrderColon);
		tvDiscountColon		= (TextView) llrecommended_order.findViewById(R.id.tvDiscountColon);
		tvNetColon		= (TextView) llrecommended_order.findViewById(R.id.tvNetColon);
		tvSplDiscountColon	= (TextView) llrecommended_order.findViewById(R.id.tvSplDiscountColon);
		tvSplDiscountPer	= (TextView) llrecommended_order.findViewById(R.id.tvSplDiscountPer);
		tvSptatementDiscountPer	= (TextView) llrecommended_order.findViewById(R.id.tvSptatementDiscountPer);
		tvSplDiscValue		= (TextView) llrecommended_order.findViewById(R.id.tvSplDiscValue);
		tvInvoiceAmount		= (TextView) llrecommended_order.findViewById(R.id.tvInvoiceAmount);
		tvAvailableText		= (TextView) llrecommended_order.findViewById(R.id.tvAvailableText);
		llDiscount			= (LinearLayout) llrecommended_order.findViewById(R.id.llDiscount);
		llOrderValue		= (LinearLayout) llrecommended_order.findViewById(R.id.llOrderValue);
		llNETValue		= (LinearLayout) llrecommended_order.findViewById(R.id.llNETValue);
		llDiscValue			= (LinearLayout) llrecommended_order.findViewById(R.id.llDiscValue);
		llSpclDiscount		= (LinearLayout) llrecommended_order.findViewById(R.id.llSpclDiscount);
		llStatementDiscount		= (LinearLayout) llrecommended_order.findViewById(R.id.llStatementDiscount);
		llStatementDiscValue		= (LinearLayout) llrecommended_order.findViewById(R.id.llStatementDiscValue);
		llSplDiscValue		= (LinearLayout) llrecommended_order.findViewById(R.id.llSplDiscValue);
		llAvailableLimit	= (LinearLayout) llrecommended_order.findViewById(R.id.llAvailableLimit);
		dot	= (TextView) llrecommended_order.findViewById(R.id.dot);
//		tvCreditLimitValue	  = (TextView) llrecommended_order.findViewById(R.id.tvCreditLimitValue);
//		llCategory=  (LinearLayout) llrecommended_order.findViewById(R.id.llCategory);
//		llSubCategory=  (LinearLayout) llrecommended_order.findViewById(R.id.llSubcategory);
//		if(Division== TrxHeaderDO.get_DIVISION_FOOD())
//		{
//			llStatementDiscount.setVisibility(View.GONE);
//			llStatementDiscValue.setVisibility(View.GONE);
//			dot.setVisibility(View.GONE);
//			
//		}
		tvSubCategory		=  (TextView) llrecommended_order.findViewById(R.id.tvSubCategory);
		ivFilterByInventoryQty=  (ImageView) llrecommended_order.findViewById(R.id.ivFilterByInventoryQty);
//		ivFilterByInventoryQty.setTag(vecItemFilter.get(0));
		tvAgenciesName.setText(objJourneyPlan.siteName+" ["+mallsDetailss.site+"]"/*+""*/);
		setTypeFaceRobotoBold(llrecommended_order);
		etSearch = (EditText) llrecommended_order.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llrecommended_order.findViewById(R.id.ivSearchCross);
		
		llLimit			=	(LinearLayout)llrecommended_order.findViewById(R.id.llLimit);
		llFOCTotalQty	=	(LinearLayout)llrecommended_order.findViewById(R.id.llFOCTotalQty);
		
		double totaldiscount = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc + rateDiffPercent;
		double totalstatementdiscount = StringUtils.getDouble(objJourneyPlan.statementdiscount)+promotionDisc + rateDiffPercent;
		tvDiscountPer.setText("("+decimalFormat.format(totaldiscount)+")");
		tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
		tvSptatementDiscountPer.setText("("+decimalFormat.format(totalstatementdiscount)+")");
		
		
		if(mallsDetailss.customerPaymentType!= null && mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			tvAvailableText.setVisibility(View.INVISIBLE);
			llAvailableLimit.setVisibility(View.INVISIBLE);
		}
		if(TRX_TYPE ==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() || TRX_TYPE ==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			strFilter = ALL_CATEGORIES;
			ivFilterByInventoryQty.setVisibility(View.GONE);
			ivAllItems.setVisibility(View.GONE);
		}
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				getSearchItems("",false);
			}
		});
		etSearch.addTextChangedListener(new TextWatcher()
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
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				getSearchItems(s.toString(),false);
			}
		});
		
	}
	public Vector<CategoryDO> getCategoryByType(int type){
		switch (type) {
		case 1:
		{
			Predicate<CategoryDO> searchItem = new Predicate<CategoryDO>() {
				public boolean apply(CategoryDO categoryDO) {
					return !TextUtils.isEmpty(categoryDO.parentCode) ||categoryDO.parentCode.equalsIgnoreCase("ALL");
				}
			};
			Collection<CategoryDO> filteredResult = filter(vecAllCategories, searchItem);
			return new Vector<CategoryDO>(filteredResult);
		}
		case 2:{
			Predicate<CategoryDO> searchItem = null;
			if(!TextUtils.isEmpty(selectedCategory)){
				searchItem = new Predicate<CategoryDO>() {
					public boolean apply(CategoryDO categoryDO) {
						return (!TextUtils.isEmpty(categoryDO.parentCode)&& categoryDO.parentCode.equalsIgnoreCase(selectedCategory))||categoryDO.parentCode.equalsIgnoreCase("ALL");
					}
				};
			}else{
				searchItem = new Predicate<CategoryDO>() {
					public boolean apply(CategoryDO categoryDO) {
						return !TextUtils.isEmpty(categoryDO.parentCode)||categoryDO.parentCode.equalsIgnoreCase("ALL");
					}
				};
			}
			
			Collection<CategoryDO> filteredResult = filter(vecAllCategories, searchItem);
			return new Vector<CategoryDO>(filteredResult);
		}
			
		default:
			return null;
		}
	
	}
	CaptureInventryDA captureInventryDA =null;
	Vector<ClassificationDO> vecDetails =null;
	ArrayList<String> arrrateDiff = null;
	private void loadData() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hmInventory  = new OrderDetailsDA().getAvailInventoryQtys();
				captureInventryDA = new CaptureInventryDA();
				vecAllCategories=captureInventryDA.getAllCategories(objJourneyPlan,2,Division);
//				vecAllCategories=captureInventryDA.getAllSubCategories();
				hashMapPricing =captureInventryDA.getPricing(objJourneyPlan.priceList,TRX_TYPE);
				arrrateDiff = captureInventryDA.getRateDiffDiscount();
				vecDetails =new Vector<ClassificationDO>();
				captureInventryDA.getRecommendeOrder(objJourneyPlan,hmSavedItems, 
						hmInventory,
						CalendarUtils.getOrderPostDate(),
						hmDistinctModifiedItem,hashMapPricing,TRX_TYPE,
						new DataFetchListner() {
							@Override
							public void dataFetched(ClassificationDO classificationDO) {
								if(classificationDO!=null && !vecDetails.contains(classificationDO)){
									vecDetails.add(classificationDO);
									runOnUiThread(new Runnable()
									{
										@Override
										public void run()
										{
											if(vecDetails!=null && vecDetails.size()>0 && isActive)
											{
												if(adapter==null){
													adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),vecDetails);
													pager.setAdapter(adapter);
													tabs.setViewPager(pager);
												}
												else{
													adapter.refresh(vecDetails);
													tabs.notifyDataSetChanged();
												}
												
											}
											calculatePrice(true);
										}
									});
								}
							}

							@Override
							public void completed() {
								if(isActive)
								{
									if(TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER())
									{
										strFilter = ALL_CATEGORIES;
										getSearchItems("", true);
									}
									else
										getSearchItems("", false);
								}
								hideLoader();
							}
						},Division);
				
			}
		}).start();
	}
	public static boolean isActive;
	@Override
	protected void onStart() {
		super.onStart();
		isActive=true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		isActive=false;
		hideLoader();
		if(captureInventryDA!=null)
			captureInventryDA.cancelRequest(true);
	}
	@Override
	public void onStop() {
		super.onStop();
		isActive=false;
	}
	public int isInventoryAvail(TrxDetailsDO objItem,int quantity)
	{
		int missedQTY = 0;
		if(TRX_TYPE ==TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER() || TRX_TYPE ==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() ||
				preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)
				/*|| TRX_TYPE ==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()*/)
			return missedQTY;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.itemCode))
		{
			HHInventryQTDO inventryDO = hmInventory.get(objItem.itemCode);
			objItem.expiryDate = inventryDO.expiryDate;
			objItem.batchCode 	  = inventryDO.batchCode;
			
//			float availQty = inventryDO.totalQt;
			float availQty = (int)(inventryDO.totalQt/objItem.hashArrUoms.get(objItem.itemCode+"UNIT").eaConversion);
			if(quantity > availQty)
			{
				missedQTY = (int) ((quantity) - availQty);
				hmInventory.get(objItem.itemCode).tempTotalQt = 0;
			}
			else
				hmInventory.get(objItem.itemCode).tempTotalQt = quantity;
		}
		else
			missedQTY = quantity;
		
		return missedQTY;
	}
	
	
	public class CategoryPagerAdapter extends FragmentStatePagerAdapter 
	{
		private  Vector<ClassificationDO> vecClassifications;
		
		public CategoryPagerAdapter(FragmentManager fm,Vector<ClassificationDO> vecClassifications)
		{
			super(fm);
			this.vecClassifications = (Vector<ClassificationDO>) vecClassifications.clone();
		}

		public void refresh(Vector<ClassificationDO> vecClassifications) 
		{
			this.vecClassifications = (Vector<ClassificationDO>) vecClassifications.clone();
			notifyDataSetChanged();
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(vecClassifications != null && vecClassifications.size() > 0){
				ClassificationDO objTemp=vecClassifications.get(position);
				if(objTemp.hmProducts!=null && objTemp.hmProducts.size()>0){
					tabs.updateTextColor(position, R.color.red_dark);
				}else{
					
				}
				return objTemp.Description;
			}
			
			return "N/A";
		}

		
		@Override
		public int getCount() 
		{
			if(vecClassifications == null || vecClassifications.size() <= 0)
				return 0;
			
			return vecClassifications.size();
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position)
		{
			ClassificationDO tmp =vecClassifications.get(position);
			ListBrandRecomendedFragment listBrandFragment = new ListBrandRecomendedFragment(objJourneyPlan, position,tmp.hmProducts,hmInventory,tmp.arrBrands,TRX_TYPE);
			return listBrandFragment;
		}
	}
	
	
	public void updateDistinctItem(TrxDetailsDO objItem)
	{
		rateDiffAmount = 0;
		rateDiffPercent = 0;
		objItem.calculatedDiscountPercentage = 0;
		hmDistinctModifiedItem.put(objItem.itemCode, objItem);
		calculatePrice(false);
	}
	
	float orderTPrice, totalIPrice, totalDiscount,splDiscount,customerDiscount,customerstatementdiscount,orderVATPrice;
	private void  calculatePrice(final boolean updateSavedOrder)
	{
		LogUtils.debug("calculatePrice", "called");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				calculatePriceInSync(updateSavedOrder);
			}
		}).start();
	}
	int focItemCount = 0;
	private void calculatePriceInSync(boolean updateSavedOrder){
		try {
			synchronized (SYNCPRICECAL) {
				orderTPrice = 0.0f;
				orderVATPrice = 0.0f;
				totalIPrice = 0.0f;
				totalDiscount = 0.0f;

				Set<String> set = hmDistinctModifiedItem.keySet();
				int count = 1;
				focItemCount = 0;
				splDiscount	= 0;
				customerstatementdiscount=0;
				for (String key : set) {
					LogUtils.debug("priceloop", "running");
					TrxDetailsDO trxDetailsDO = hmDistinctModifiedItem.get(key);
					if(updateSavedOrder && trxDetailsDO.requestedSalesBU>0)
						updateSavedObject(trxDetailsDO);
					trxDetailsDO.lineNo = (int) count++;
					trxDetailsDO.itemType = "" + AppConstants.ITEM_TYPE_ORDER;
					float price = trxDetailsDO.basePrice;
					float price3 = 0;
					float csPrice = 0;
					float quanity = 0.0f;

					if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) 
					{
						price = hashMapPricing.get(trxDetailsDO.itemCode).get(trxDetailsDO.UOM);
						// getting price of selected UOM
						
						if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
							price3 = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());
						
						if(hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel1()))
							csPrice = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel1());
					}
					trxDetailsDO.priceUsedLevel1 = price;
					trxDetailsDO.priceUsedLevel3 = price3;
					
					trxDetailsDO.EAPrice = price3;
					trxDetailsDO.CSPrice = csPrice;
					
					quanity = trxDetailsDO.quantityBU;
					float discountAmount = (price*trxDetailsDO.calculatedDiscountPercentage)/100.0f;
					trxDetailsDO.calculatedDiscountAmount=StringUtils.getFloat(decimalFormat.format(discountAmount));
//					trxDetailsDO.totalDiscountAmount=StringUtils.getFloat(decimalFormat.format(trxDetailsDO.calculatedDiscountAmount*quanity));
					
					LogUtils.debug("discount_check", "quanity"+quanity);
					LogUtils.debug("discount_check", "price3"+price3);
					LogUtils.debug("discount_check", "discountAmount"+discountAmount);
					LogUtils.debug("discount_check", "calculatedDiscountAmount"+trxDetailsDO.calculatedDiscountAmount);
					LogUtils.debug("discount_check", "totalDiscountAmount"+trxDetailsDO.totalDiscountAmount);

				 float	customerDiscountTemp = ((StringUtils.getFloat(objJourneyPlan.PromotionalDiscount) * csPrice)/100);
					float	customerstatementdiscountTemp =  ((StringUtils.getFloat(objJourneyPlan.statementdiscount) * csPrice)/100);
					float	speacialDiscountOnEach =  ((rateDiffPercent * csPrice)/100);
					float vatOnEach=(((csPrice)-customerDiscountTemp-customerstatementdiscountTemp-speacialDiscountOnEach)*trxDetailsDO.vatPercentage)/100;
//					float vatOnEach=(((csPrice)-customerDiscountTemp-customerstatementdiscountTemp)*trxDetailsDO.vatPercentage)/100;
					trxDetailsDO.VATAmountNew=(vatOnEach* quanity);
					orderVATPrice+=(vatOnEach* quanity);
					orderTPrice += StringUtils.getFloat(decimalFormat.format((csPrice * quanity)));
					totalDiscount +=  StringUtils.getFloat(decimalFormat.format(discountAmount*trxDetailsDO.quantityLevel1));
					totalIPrice += StringUtils.getFloat(decimalFormat.format((csPrice * quanity)));
					
					Log.e("orderTPrice", trxDetailsDO.itemCode+"---"+decimalFormat.format((csPrice * quanity)));
					Log.e("totalIPrice",totalIPrice+"");
					
//					calculateDiscount(csPrice,quanity);
					
					focItemCount += quanity;
				}
				
				calculateRateDiffDiscount();
				calculateTotalDiscount();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LogUtils.infoLog("rate_Diff", "focItemCount:"+focItemCount);
						LogUtils.infoLog("rate_Diff", "totalDiscount:"+totalDiscount);
						LogUtils.infoLog("rate_Diff", "splDiscount:"+splDiscount);
						LogUtils.infoLog("rate_Diff", "totalIPrice:"+totalIPrice);
						LogUtils.infoLog("rate_Diff", "orderTPrice:"+orderTPrice);
						
						
						tvFOCTotalQty.setText(focItemCount+"");
//						tvDiscValue.setText(amountFormate.format(totalDiscount-splDiscount));
//						tvSplDiscValue.setText(amountFormate.format(splDiscount));
//						tvTotalValue.setText(amountFormate.format(totalIPrice-totalDiscount));
//						tvOrderValue.setText(amountFormate.format(orderTPrice));
						tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
						
						
//						tvDiscValue.setText(decimalFormat.format(totalDiscount-splDiscount));
						tvDiscValue.setText(decimalFormat.format(customerDiscount));
						tvstmntDiscValue.setText(decimalFormat.format(customerstatementdiscount));
						
						tvSplDiscValue.setText(decimalFormat.format(splDiscount));
						tvTotalValue.setText(decimalFormat.format(totalIPrice-totalDiscount+orderVATPrice));
						tvNetValuet.setText(decimalFormat.format(totalIPrice-totalDiscount));
						tvOrderValue.setText(decimalFormat.format(orderTPrice));
						tvVATValuet.setText(decimalFormat.format(orderVATPrice));
					}
				});
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void calculateTotalDiscount()
	{
		if((TRX_TYPE != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && TRX_TYPE != TrxHeaderDO.get_TYPE_FREE_ORDER()) && (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+(StringUtils.getDouble(objJourneyPlan.statementdiscount)) + promotionDisc + rateDiffPercent) > 0)
		{
			double totalOrderAmount = totalIPrice;
			if(totalOrderAmount > 0)
			{
				customerDiscount = StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(objJourneyPlan.PromotionalDiscount) * totalOrderAmount)/100))));
				customerstatementdiscount =  StringUtils.getFloat(decimalFormat.format((((StringUtils.getFloat(objJourneyPlan.statementdiscount) * totalOrderAmount)/100))));
				totalDiscount = StringUtils.getFloat(decimalFormat.format(((( promotionDisc) * totalOrderAmount)/100))) + StringUtils.getFloat(decimalFormat.format(rateDiffAmount)) + customerDiscount +customerstatementdiscount;//Abhijit
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
	
//	private void calculateDiscount(float price, float quanity) 
//	{
//		float calDiscount 	= 0.0f;
//		float splDisc		= 0.0f;
//		Log.e("totalIPrice","PromotionalDiscount:"+objJourneyPlan.PromotionalDiscount);
//		/**Promotional discount offered**/
//		if((StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent) > 0)
//		{
//			calDiscount += ((price * quanity) * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent)/100)/* + StringUtils.getFloat(decimalFormat.format(rateDiffAmount))*/;
//			splDisc += ((price * quanity) * (promotionDisc + rateDiffPercent)/100)/* + StringUtils.getFloat(decimalFormat.format(rateDiffAmount))*/;
//		}
//		totalDiscount += calDiscount;
//		splDiscount += splDisc;
//	}
	
	private float calculateDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
//		discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc + rateDiffPercent))/100);
		discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.PromotionalDiscount) + promotionDisc    ))/100);

		return discount;
	}
	
	private float calculatestatementDiscount(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;
//		discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.statementdiscount)  + rateDiffPercent))/100);
		discount = (float) ((objItem.CSPrice * objItem.quantityBU * (StringUtils.getDouble(objJourneyPlan.statementdiscount)))/100);

		return discount;
	}
	
	private float calculateDiscountPercentage(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;//promotionalDiscountAmount
		if(objItem.CSPrice * objItem.quantityBU != 0)
			discount = (float) (((objItem.promotionalDiscountAmount+ objItem.statementDiscountAmnt) * 100) / (objItem.CSPrice * objItem.quantityBU));

		return discount;
	}
	private float calculatestatementDiscountPercentage(TrxDetailsDO objItem) 
	{
		float discount = 0.0f;//promotionalDiscountAmount
		if(objItem.CSPrice * objItem.quantityBU != 0)
			discount = (float) ((objItem.statementDiscountAmnt * 100) / (objItem.CSPrice * objItem.quantityBU));

		return discount;
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
	public void getModifiedData(TrxDetailsDO objItem)
	{
		if(hmDistinctModifiedItem.size() > 0 && hmDistinctModifiedItem.containsKey(objItem.itemCode))
		{
			TrxDetailsDO tempDO 		= hmDistinctModifiedItem.get(objItem.itemCode);
			objItem.requestedSalesBU 	= tempDO.requestedSalesBU;
			objItem.missedBU 			= tempDO.missedBU;
			objItem.UOM					= tempDO.UOM;
			objItem.quantityLevel1		= tempDO.quantityLevel1;
			objItem.quantityBU 			= tempDO.quantityBU;
			objItem.quantityLevel3 		= tempDO.quantityLevel3;
			objItem.productStatus 		= tempDO.productStatus;
		}
	}
	
	PromotionDO poPromotionDO=null;
	private TrxHeaderDO prepareSalesOrder() 
	{
//		orderTPrice = 0;
//		totalDiscount =0;
//		totalIPrice =0;
		 poPromotionDO=new PromotionDO();
		boolean isVaildSalesOrder = false;
		
		//For setting discount for each item
		updateTRXDetail();
		
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
				if(trxDetailsDO.requestedSalesBU > 0 && trxDetailsDO.productStatus != 2){//to check atleast he need to order one item which is available in invenotry
					arrTRXDetail.add(trxDetailsDO);
					trxDetailsDO.itemType=TrxDetailsDO.get_TRX_NOREMAL_ITEM();

//					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
//						float discountAmount = ((trxDetailsDO.priceUsedLevel1 * trxDetailsDO.calculatedDiscountPercentage) / 100.0f) * trxDetailsDO.quantityBU;
//						trxDetailsDO.calculatedDiscountAmount = StringUtils.getFloat(decimalFormat.format(discountAmount));
//
//						orderTPrice += StringUtils.getFloat(decimalFormat.format((trxDetailsDO.priceUsedLevel1 * trxDetailsDO.quantityLevel1)));
//						totalDiscount += StringUtils.getFloat(decimalFormat.format(discountAmount * trxDetailsDO.quantityLevel1));
//						totalIPrice += StringUtils.getFloat(decimalFormat.format((trxDetailsDO.priceUsedLevel1 * trxDetailsDO.quantityLevel1)));
//					}
					
				}
				
				if(trxDetailsDO.quantityBU>0)
					isVaildSalesOrder = true;
				
				trxDetailsDO.reason = focReason;
				java.util.Collections.sort(arrTRXDetail, new Comparator<TrxDetailsDO>() {
			        @Override
			        public int compare(TrxDetailsDO  trxDetailsDO1, TrxDetailsDO  trxDetailsDO2)
			        {
			            return  trxDetailsDO1.DisplayOrder - trxDetailsDO2.DisplayOrder;
			        }
			    });
			}
			
			if(isVaildSalesOrder && trxSavedHeader!=null)
			{
				trxHeaderDO = new TrxHeaderDO();
				trxHeaderDO.trxCode			=	trxSavedHeader.trxCode;
				trxHeaderDO.appTrxId 		=  trxSavedHeader.appTrxId;
				trxHeaderDO.clientBranchCode=  mallsDetailss.site;
				trxHeaderDO.clientCode		=  mallsDetailss.site;
				trxHeaderDO.currencyCode    =  AppConstants.CURRECNY_CODE;
				trxHeaderDO.deliveryDate	=  CalendarUtils.getCurrentDate();
			
				if(objJourneyPlan != null && !objJourneyPlan.JourneyCode.equalsIgnoreCase("0") && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
					trxHeaderDO.journeyCode		=  objJourneyPlan.JourneyCode;
				else
					trxHeaderDO.journeyCode		=  mallsDetailss.JourneyCode;
				
				trxHeaderDO.orgCode			=  preference.getStringFromPreference(Preference.ORG_CODE, "");
				trxHeaderDO.paymentType		=  mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) ? 1 : 0;//need to check
				trxHeaderDO.printingTimes	=  0;
				trxHeaderDO.status			=  trxSavedHeader.status;
				trxHeaderDO.totalAmount		=  orderTPrice;
				trxHeaderDO.totalVATAmount		=  orderVATPrice;
				trxHeaderDO.totalDiscountAmount = totalDiscount;
				trxHeaderDO.trxStatus		= trxSavedHeader.trxStatus;
				trxHeaderDO.trxType			= trxSavedHeader.trxType;
				trxHeaderDO.trxSubType		= trxSavedHeader.trxSubType;
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");
				
				if(objJourneyPlan != null && !objJourneyPlan.VisitCode.equalsIgnoreCase("0") && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
					trxHeaderDO.visitCode		= objJourneyPlan.VisitCode;
				else
					trxHeaderDO.visitCode		= mallsDetailss.VisitCode;
				
				if(TRX_TYPE != TrxHeaderDO.get_TYPE_FREE_ORDER())
					trxHeaderDO.reason 				= focReason;
				else
				{
					if(trxSavedHeader.arrTrxDetailsDOs != null && trxSavedHeader.arrTrxDetailsDOs.size()>0)
					trxHeaderDO.reason 				= trxSavedHeader.arrTrxDetailsDOs.get(0).reason;
				}
				trxHeaderDO.specialDiscount 	= StringUtils.getFloat(decimalFormat.format(splDiscount));
				trxHeaderDO.statementdiscountvalue 	= StringUtils.getFloat(decimalFormat.format(customerstatementdiscount));
				trxHeaderDO.specialDiscPercent 	= StringUtils.getFloat(decimalFormat.format(promotionDisc + rateDiffPercent));
				trxHeaderDO.rateDiff		 	= StringUtils.getFloat(decimalFormat.format(rateDiffAmount));
				trxHeaderDO.promotionalDiscount = objJourneyPlan.PromotionalDiscount;
				trxHeaderDO.statementDiscount   = objJourneyPlan.statementdiscount;
				trxHeaderDO.Division 			= Division;
				trxHeaderDO.PromotionReason = promotionReason;
				trxHeaderDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
			else if(arrTRXDetail.size() > 0 && isVaildSalesOrder)
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
				trxHeaderDO.trxStatus		= TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
				trxHeaderDO.lPOCode			= "";
				trxHeaderDO.referenceCode	= "";
				trxHeaderDO.userCreditAccountCode	= "";
				
				if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
					trxHeaderDO.trxType			= TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER();
				else if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
					trxHeaderDO.trxType			= TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				else if(TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_DELIVERY())
					trxHeaderDO.trxType			= TrxHeaderDO.get_TYPE_FREE_DELIVERY();
				else if(TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER())
					trxHeaderDO.trxType			= TrxHeaderDO.get_TYPE_FREE_ORDER();
				else
					trxHeaderDO.trxType			= TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
				
				if(TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
					trxHeaderDO.trxSubType		= TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
				else
					trxHeaderDO.trxSubType		= TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER();
				
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");;
				trxHeaderDO.visitCode		= objJourneyPlan.VisitCode;
				
				trxHeaderDO.reason = focReason;
				trxHeaderDO.specialDiscount = splDiscount;
				trxHeaderDO.statementdiscountvalue=customerstatementdiscount;
				trxHeaderDO.specialDiscPercent = promotionDisc + rateDiffPercent;
				trxHeaderDO.rateDiff		 	= rateDiffAmount;
				trxHeaderDO.promotionalDiscount = objJourneyPlan.PromotionalDiscount;
				trxHeaderDO.statementDiscount = objJourneyPlan.statementdiscount;
				trxHeaderDO.Division 			= Division;
				trxHeaderDO.PromotionReason = promotionReason;
				trxHeaderDO.arrTrxDetailsDOs.addAll(arrTRXDetail);
			}
		}
		return trxHeaderDO;
	}
	private TrxDetailsDO getFocItem(TrxDetailsDO trxDetailsDO2){
		TrxDetailsDO trxDetailsDO=null;
		try {
			for(PromotionDO obj:poPromotionDO.arrPromotion)
			{
				if(obj.PromoItemCode.trim().toLowerCase().equalsIgnoreCase(trxDetailsDO2.itemCode.toLowerCase()))
				{
					int qty = (int) (Math.floor(trxDetailsDO2.quantityBU / obj.PromoItemQty) * obj.FOCItemQty);
					if(qty>0){
						trxDetailsDO = new TrxDetailsDO();
						trxDetailsDO.itemCode=obj.FOCItemCode;
						trxDetailsDO.itemDescription=obj.Description+" (Promo)";
						trxDetailsDO.UOM="PCS";
						trxDetailsDO.quantityLevel1=qty;
						trxDetailsDO.priceUsedLevel1=0;
						trxDetailsDO.promoType=TrxDetailsDO.get_TRX_FOC_ITEM();
						trxDetailsDO.arrUoms.add("PCS");
						trxDetailsDO.quantityBU=qty;
						trxDetailsDO.priceUsedLevel3=0;
						trxDetailsDO.itemType=TrxDetailsDO.get_TRX_FOC_ITEM();
						trxDetailsDO.calculatedDiscountPercentage=100;
						trxDetailsDO.expiryDate=CalendarUtils.getOrderPostDate();
						float price=0.0f;
						if (hashMapPricing.containsKey(trxDetailsDO.itemCode) &&hashMapPricing.get(trxDetailsDO.itemCode).containsKey("PCS")) 
							price = hashMapPricing.get(trxDetailsDO.itemCode).get("PCS");
						trxDetailsDO.calculatedDiscountAmount=price;
						trxDetailsDO.totalDiscountAmount=price;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trxDetailsDO;
	}
	
	
	@Override
	public void onBackPressed() {
		showCustomDialog(SalesManRecommendedOrder.this, getString(R.string.warning), "Are you sure you don't want to save current data?", getString(R.string.Yes), getString(R.string.No), "GoBack");
//		checkCartSaveAvailability();
	}
	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("GoBack"))
			finish();
		else if(from.equalsIgnoreCase("Syncnow")){
			if(isNetworkConnectionAvailable(this))
				syncData(SalesManRecommendedOrder.this);
			else
				showCustomDialog(SalesManRecommendedOrder.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
		}
	}
	public void checkCartSaveAvailability()
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
	
	private TrxHeaderDO prepareCartOrder() 
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
				trxHeaderDO.trxCode         =  objJourneyPlan.site;
				trxHeaderDO.preTrxCode	 	=  objJourneyPlan.site;
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
				trxHeaderDO.trxStatus		= 0;
				trxHeaderDO.trxType			= TrxHeaderDO.get_TRXTYPE_CART();
				trxHeaderDO.userCode		= preference.getStringFromPreference(Preference.EMP_NO, "");;
				trxHeaderDO.visitCode		= objJourneyPlan.VisitCode;
				
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
	public interface DataFetchListner{
		public void dataFetched(ClassificationDO classificationDO);
		public void completed();
	}
	
	private String focReason = "";
	public void showReasonForFOC()
	{
		final Vector<NameIDDo> vecReasons = new CommonDA().getReasonsByType(AppConstants.FOC_REASON);
		CustomBuilder builder = new CustomBuilder(this, "Please Select FOC Reason.", true);
		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
				showLoader(getResources().getString(R.string.please_wait));
	    		builder.dismiss();
	    		focReason = objNameIDDo.strName;
	    		gotoNextActivity();
		    }
	   }); 
		builder.show();
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
						calculatePrice(true);
						double totaldiscountamount1 = StringUtils.getDouble(objJourneyPlan.PromotionalDiscount)+promotionDisc;
//							tvDiscountPer.setText("("+decimalFormat.format(totaldiscount)+")");
						tvSplDiscountPer.setText("("+decimalFormat.format(promotionDisc + rateDiffPercent)+")");
						hideLoader();
						if (customDialogs.isShowing())
							customDialogs.dismiss();

					}

				}, 200);
			}
		});
		builder.show();
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
