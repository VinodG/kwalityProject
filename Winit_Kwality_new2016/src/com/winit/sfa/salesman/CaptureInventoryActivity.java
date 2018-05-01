/**
 * 
 */
package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.app.ActionBar.LayoutParams;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.CaptureInventoryExpandableListViewAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.MasterDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.InventoryDetailDO;
import com.winit.alseer.salesman.dataobject.InventoryGroupDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.SettingsDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.listeners.StoreCheck;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

/**
 * @author Aritra.Pal
 *
 */
public class CaptureInventoryActivity extends BaseActivity 
{

	private LinearLayout llLayout;
	private Button btn;
	private TextView tvPageTitle,tvCategory,txtvStoreItemPercent, txtvName, tvNoSearchResult/*,tvSubCategory*/;
	private EditText etSearch;
	private ImageView ivSearchCross;
	private ProgressBar pbunrefectStore,pbperfectStore;
	
	private SettingsDO settingsDO;
	private double storePercent= 0;
	private float goldenSrorepercent = 0;
//	private LinkedHashMap<SellingSKU, HashMap<BrandDO, ArrayList<ProductDO>>> hashProductWithClassification;
	private Vector<CategoryDO> vecAllCategories=new Vector<CategoryDO>();
//	private HashMap<BrandDO, ArrayList<ProductDO>> hashProductWithClassification;
	private HashMap<String, ArrayList<ProductDO>> hashProductWithClassification;
	private HashMap<String, ProductDO> hashItemMissingInInventory=new HashMap<String, ProductDO>();
	private HashMap<String, ProductDO> hashItemAllInInventory=new HashMap<String, ProductDO>();	
	private HashMap<String, Vector<String>> tempTotal=new HashMap<String, Vector<String>>();
	HashMap<String, Vector<String>> tempMissing=new HashMap<String, Vector<String>>();
	private int totalDistinctItemsInStore = 0;
//	private Vector<BrandDO> vecbrandDOs;
	private Vector<String> vecCustomerGroupCodes;
	private ExpandableListView expandableList;
	public CaptureInventoryExpandableListViewAdapter storeCheckEXLVAdapter;
	private static final String SYNC_SEARCH_LOACK="sync_search_loack";
	private String selectedCategory = "",selectedSubCategory = "";
	private boolean isDone = false;
	private String startTime="";
	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llLayout.findViewById(R.id.tvPageTitle);
		btn	= (Button) llLayout.findViewById(R.id.btn);
		tvPageTitle.setText("Store Check");
		btn.setText("SUBMIT");
	}
	
	/* (non-Javadoc)
	 * @see com.winit.alseer.salesman.BaseActivity#initialize()
	 */
	@Override
	public void initialize() 
	{
		llLayout 		= (LinearLayout) inflater.inflate(R.layout.storecheck_categories, null);
		initialiseScreenControls();
		setHeaderBar();
		
		if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
			AppConstants.hmCapturedInventory.clear();
		startTime=CalendarUtils.getCurrentDateTime();
		
		llBody.addView(llLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		setTypeFaceRobotoNormal(llLayout);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn.setTypeface(Typeface.DEFAULT_BOLD);
		txtvName.setTypeface(Typeface.DEFAULT_BOLD);
		
		if(getIntent().getExtras() != null)
		{
			mallsDetailss = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			isDone			=	getIntent().getExtras().getBoolean("isDone");			
		}
		if(mallsDetailss != null)
			txtvName.setText(mallsDetailss.siteName+" ["+mallsDetailss.site+"]");//ask whose name it is supposed to show by aritra
		loadData();
		
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(validateCaptureInventory()) {
					
					if(settingsDO != null)
					{
						CustomerVisitDO userJourneyPlan = new CustomerVisitDO();
						userJourneyPlan.TypeOfCall 	=  	""+AppConstants.GOLDEN_STORE_VAL;
						userJourneyPlan.JourneyCode =	mallsDetailss.JourneyCode;
						userJourneyPlan.VisitCode 	= 	mallsDetailss.VisitCode;
						new CommonDA().updateCustomerVisit_StoreCheck(userJourneyPlan);
						postdata();
					}
					else
					{
						postdata();
					}
				} else {
					showCustomDialog(CaptureInventoryActivity.this, "Warning !", "Please enter quantity for all the items.", "OK", null, "validateCaptureInv");
				}
			}
		});
		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				Vector<CategoryDO> filteredCategory=getCategoryByType(1);
				if(filteredCategory!=null && filteredCategory.size() > 0){
					filteredCategory.get(0).categoryName="All Categories";
					showCategories(view, filteredCategory, "Category",1);
				}
			}
		});
		
//		tvSubCategory.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(final View v) 
//			{
//				Vector<CategoryDO> filteredCategory=getCategoryByType(2);
//				if(filteredCategory!=null && filteredCategory.size() > 0){
//					filteredCategory.get(0).categoryName="All Subcategory";
//					showCategories(v, filteredCategory, "Sub Category",2);
//				}
//			}
//		});
		
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
		
		ivSearchCross.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				getSearchItems("",false);
			}
		});
	}
	
	private boolean validateCaptureInventory() {
		boolean isValid = true;
		for(String key : hashProductWithClassification.keySet()){
			ArrayList<ProductDO> vec = hashProductWithClassification.get(key);
			for(ProductDO objProductDO : vec){
				if(StringUtils.getInt(objProductDO.PcsQTY) < 0) {
					isValid = false;
					break;
				}
			}
			if(!isValid)
				break;
		}
		return isValid;
	}
	
	public boolean isStoreCheckSumbitted=false;
	/**
	 * 
	 */
	private void loadData() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				CategoriesDA categoriesDA=new CategoriesDA();
				CaptureInventryDA objCaptureInventryDA = new CaptureInventryDA();
				
				settingsDO = new MasterDA().getSettings(AppConstants.GOLDEN_STORE);
				if(settingsDO != null)
					goldenSrorepercent = StringUtils.getFloat(settingsDO.SettingValue);
				
				vecAllCategories = objCaptureInventryDA.getAllCategories(mallsDetailss,1,0);	
//				vecAllCategories = objCaptureInventryDA.getAllSubCategories();	
				hashProductWithClassification = categoriesDA.getStoreCheckByCategories(mallsDetailss,null);
				hashItemMissingInInventory = objCaptureInventryDA.getStoreCheckedItems(mallsDetailss.site,null);
//				hashBrand = new CaptureInventryDA().getStoreCheckedBrands(mallsDetails.site);
				if((hashItemMissingInInventory!=null && hashItemMissingInInventory.size()>0) || isDone)
					isStoreCheckSumbitted=true;
				else
					isStoreCheckSumbitted=false;
				vecCustomerGroupCodes = new Vector<String>();
//				BrandDO objbrand = new BrandDO();
//				objbrand.brandName = "Select Brand";
//				vecCustomerGroupCodes.add(objbrand);
				vecCustomerGroupCodes.add("Select Category");
				
				HashMap<String, ArrayList<ProductDO>> hash = hashProductWithClassification;
				Set<String> branDOs = hash.keySet();
				for(String objbraBrandDO: branDOs)
				{
					vecCustomerGroupCodes.add(objbraBrandDO);
				}
				
				tempTotal=new HashMap<String, Vector<String>>();
				Vector<String> vecTotalDistinctItesmInStore = new Vector<String>();

				HashMap<String, ArrayList<ProductDO>> tmp=hashProductWithClassification;
				Set<String> brands = tmp.keySet();
				for(String brandDO:brands){
					ArrayList<ProductDO> productDOs=tmp.get(brandDO);
					for(ProductDO obj: productDOs)
					{
						Vector<String> vec = tempTotal.get(brandDO);
						if(vec==null)
							vec = new Vector<String>();
						if(!vec.contains(obj.SKU))
						{
							vec.add(obj.SKU);
						}
						
						ProductDO objproduct = hashItemAllInInventory.get(brandDO);
						if(objproduct == null)
							hashItemAllInInventory.put(brandDO, obj);
						
						if(hashItemMissingInInventory != null)
						{
							ProductDO tempProductDO = hashItemMissingInInventory.get(obj.SKU);
							if(tempProductDO != null)
							{
//									obj.isCaptured = tempProductDO.isCaptured;
								obj = tempProductDO;									
							}
						}
						
						if(!vecTotalDistinctItesmInStore.contains(obj.SKU))
							vecTotalDistinctItesmInStore.add(obj.SKU);
						tempTotal.put(brandDO, vec);
					}
				}
				
				totalDistinctItemsInStore=vecTotalDistinctItesmInStore.size();
				vecTotalDistinctItesmInStore=null;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(vecCustomerGroupCodes != null && vecCustomerGroupCodes.size() > 0)
						{
							tvCategory.setText("All Brands");
							tvCategory.setTag(vecCustomerGroupCodes.get(0));
							tvCategory.setText(vecCustomerGroupCodes.get(0));
						}
//						if(vecAgencies != null && vecAgencies.size() > 0)
//						{
//							tvAgencies.setTag(vecAgencies.get(0));
//							tvAgencies.setText(vecAgencies.get(0).strName);
//						}
						if(hashProductWithClassification!=null && hashProductWithClassification.size()>0)
						{
							storeCheckEXLVAdapter.refreshExpandableAdapter(hashProductWithClassification);
						}
						/***Once StoreCheck is submitted user cannot submit second time***/
						if(isStoreCheckSumbitted)
							btn.setVisibility(View.GONE);
						else
							btn.setVisibility(View.VISIBLE);
						new Handler().postDelayed(new Runnable() 
						{
							@Override
							public void run() 
							{
								getStorePercent();
							}
						}, 50);
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void postdata() 
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				StatusDO statusDO = new StatusDO();
				statusDO.UUid ="";
				statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
				statusDO.Customersite =mallsDetailss.site;
				statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
				statusDO.Visitcode=mallsDetailss.VisitCode;
				statusDO.JourneyCode = mallsDetailss.JourneyCode;
				statusDO.Status = "0";
				statusDO.Action = AppConstants.Action_CheckIn;
				statusDO.Type = AppConstants.Type_StoreCheck;
				new StatusDA().insertOptionStatus(statusDO);
				
				if(AppConstants.hmCapturedInventory == null)
					AppConstants.hmCapturedInventory  = new HashMap<String, Vector<ProductDO>>();
				
				Set<String> keys  =  AppConstants.hmCapturedInventory.keySet();
				for(String key : keys)
				{
					Vector<ProductDO>vecItems = AppConstants.hmCapturedInventory.get(key);
					
					for(ProductDO objProductDO : vecItems)
					{
						objProductDO.isMusthave = true;
						
						objProductDO.isReccomended = true;
						objProductDO.inventoryQty= StringUtils.getFloat(objProductDO.preUnits);
						objProductDO.preUnits = ""+Math.abs(Math.floor((10 - StringUtils.getFloat(objProductDO.preUnits))*1.5));
						
						objProductDO.preCases = ""+Math.abs(Math.floor((10 - StringUtils.getFloat(objProductDO.preCases))*1.5));
						//If recommended units are less than mustHave quantity, use mustHaveQty
						objProductDO.units = String.valueOf(objProductDO.preUnits);
						
						objProductDO.cases = String.valueOf(objProductDO.preCases);
						
						if(objProductDO.cases.equalsIgnoreCase("") || StringUtils.getFloat(objProductDO.cases) <= 0)
							objProductDO.preCases = "0";
						else
							objProductDO.preCases = String.valueOf(decimalFormat.format(StringUtils.getFloat(objProductDO.cases)));
						
						objProductDO.recomCases = objProductDO.preCases;
						objProductDO.recomUnits = StringUtils.getFloat(objProductDO.preUnits);
					
						objProductDO.totalCases = StringUtils.getFloat(objProductDO.preCases) + StringUtils.getInt(objProductDO.preUnits)/objProductDO.UnitsPerCases;
					}
				}
				
				addEnteredData();
				
				TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
				trxLogDetailsDO.CustomerCode = mallsDetailss.site;
				trxLogDetailsDO.CustomerName = mallsDetailss.siteName;
				trxLogDetailsDO.TrxType = AppConstants.STORECHECK;
				trxLogDetailsDO.IsJp  = "";
				trxLogDetailsDO.columnName = TrxLogHeaders.COL_STORE_CHECK;
				trxLogDetailsDO.DocumentNumber = "N/A";
				trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
				trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
				trxLogDetailsDO.IsJp  = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetailss.site,trxLogDetailsDO.Date)?"True":"False";
				new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						//Uploading data here.
						uploadData();
						hideLoader();
						showCustomDialog(CaptureInventoryActivity.this, "Successful !", "Store check\n Successful !", "OK", null, "Storecheck");
					}
				});
			}
		}).start();
	}
	
	private String getAlertMessage(float floatDiffrence) 
	{
		String strDiffrence = percentFormat.format(floatDiffrence);
		StringBuilder strMessage = new StringBuilder();
		strMessage.append("You are ");
		if(floatDiffrence<=10)
			strMessage.append("just ");
		strMessage.append(strDiffrence + "% short from making this store as Perfect Store.");
		strMessage.append("\n \n");
		strMessage.append("Do you still want to continue?");
		return strMessage.toString();
	}
	
	private void addEnteredData() 
	{
		final InventoryDO inventoryDO = new InventoryDO();
		inventoryDO.inventoryId = StringUtils.getUniqueUUID();
		inventoryDO.site = mallsDetailss.site;
		inventoryDO.date = CalendarUtils.getCurrentDateTime();
		inventoryDO.uplaodStatus = 0;
		inventoryDO.Status = "" + inventoryDO.uplaodStatus;
		inventoryDO.createdBy = preference.getStringFromPreference(Preference.USER_ID, "");
		inventoryDO.VisitCode = mallsDetailss.VisitCode;
		inventoryDO.JourneyCode = mallsDetailss.JourneyCode;
		inventoryDO.starTime = startTime;

		inventoryDO.SalesmanName = preference.getStringFromPreference(preference.USER_NAME, "");
		inventoryDO.ClientName = mallsDetailss.siteName;

		CustomerDao objcuCustomerDao = new CustomerDA().getCustomerforStoreCheck(mallsDetailss.site);
		if (objcuCustomerDao != null) 
		{
			inventoryDO.ChannelCode = objcuCustomerDao.ChannelCode;
			inventoryDO.Region = objcuCustomerDao.RegionCode;
			inventoryDO.RegionCode = objcuCustomerDao.RegionCode;
		}
		inventoryDO.Role = new UserInfoDA().getUserRoleID(preference.getStringFromPreference(Preference.USER_ID, ""));

		Set<String> keys = hashProductWithClassification.keySet();
		for(String key: keys)
		{
			ArrayList<ProductDO> arrProductDO = hashProductWithClassification.get(key);
			for (int i = 0; i < arrProductDO.size(); i++) 
			{
				ProductDO productDO = arrProductDO.get(i);
				InventoryDetailDO inventoryDetailDO = new InventoryDetailDO();
				inventoryDetailDO.itemCode = productDO.SKU;
				inventoryDetailDO.ItemDescription = productDO.Description;
				
				inventoryDetailDO.CategoryCode = productDO.CategoryId;
				inventoryDetailDO.CategoryName = productDO.categoryName;
				
				inventoryDetailDO.BrandCode = productDO.brandcode;
				inventoryDetailDO.BrandName = productDO.brand;
				inventoryDetailDO.inventoryQty = StringUtils.getFloat(productDO.preUnits);
				inventoryDetailDO.recmQty = StringUtils.getFloat(productDO.preUnits);
				if(hashItemMissingInInventory.get(productDO.SKU) != null)
					inventoryDetailDO.status = 1;
				else
					inventoryDetailDO.status = 0;
				
				inventoryDetailDO.UOM = productDO.StoreUOM;
				inventoryDetailDO.QTY = StringUtils.getInt(productDO.PcsQTY);
				
//				if((inventoryDetailDO.status == 1) || (inventoryDetailDO.status == 0 && inventoryDetailDO.QTY > 0))
					if((inventoryDetailDO.status == 1) || (inventoryDetailDO.status == 0 /*&& inventoryDetailDO.QTY > 0*/))
					inventoryDO.vecInventoryDOs.add(inventoryDetailDO);
			}
		}
		
		InventoryGroupDO inventoryGroupDO = null;
		
		Set<String> groupKeyset = hashItemAllInInventory.keySet();
		for(String key: groupKeyset)
		{
			inventoryGroupDO = new InventoryGroupDO();
			inventoryGroupDO.ItemGroupCode = hashItemAllInInventory.get(key).brandcode;
			inventoryGroupDO.ItemGroupLevelName = hashItemAllInInventory.get(key).brand;
			inventoryGroupDO.TotalCount = tempTotal.get(key).size();
			Vector<String> vec = tempMissing.get(key);
			if(vec != null)
				inventoryGroupDO.TotalNotAvailableCount = vec.size();
			inventoryDO.vecGroupDOs.add(inventoryGroupDO);
		}
		
		inventoryDO.TotalCount = totalDistinctItemsInStore;
		inventoryDO.NotApplicableItemCount = hashItemMissingInInventory.size();
		
		new CaptureInventryDA().insertInventory(inventoryDO);
		
		inventoryDO.BrandCode = "";
	}
	
	private Vector<String> getBrands() 
	{
		Vector<String> vecCatBrands = new Vector<String>();
		
//		BrandDO brandDO  = new BrandDO();
//		brandDO.brandId	 = "-1";
//		brandDO.brandName = "All Brand";
		vecCatBrands.add("All Category");
		if (hashProductWithClassification != null)
		{
			HashMap<String, ArrayList<ProductDO>> hash1=hashProductWithClassification;
			for(String key:hash1.keySet())
			{
				vecCatBrands.add(key);
			}
		}
		return vecCatBrands;
	}
	
	private void getSearchItems(final String searchText, boolean isByCategory) 
	{
		HashMap<String, ArrayList<ProductDO>> hash1=hashProductWithClassification;
		HashMap<String, ArrayList<ProductDO>> tmpSearched = new HashMap<String, ArrayList<ProductDO>>();
		final String strText = searchText; // edtSearch.getText().toString();

			for (String key : hash1.keySet()) 
			{
				Predicate<ProductDO> searchItem = null;
				if(TextUtils.isEmpty(strText))
				{
					if(!TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>(){
							public boolean apply(ProductDO objproductDO) 
							{
								return objproductDO.SKU.toLowerCase()
												.equalsIgnoreCase(selectedCategory)&& objproductDO.subCategoryName.toLowerCase()
												.equalsIgnoreCase(selectedSubCategory);
							}
						};	
					}
					else if(TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return  objproductDO.subCategoryName.toLowerCase()
												.equalsIgnoreCase(selectedSubCategory);
							}
						};	
					}
					else if(!TextUtils.isEmpty(selectedCategory) && TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return objproductDO.categoryName.toLowerCase()
												.equalsIgnoreCase(selectedCategory);
							}
						};	
					}
					else
					{
						tmpSearched = hash1;
					}
				}
				else
				{
					if(!TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return (objproductDO.Description.toLowerCase()
										.contains(strText.toLowerCase()) || objproductDO.SKU
										.toLowerCase().contains(
												strText.toLowerCase()))
												&& objproductDO.categoryName.toLowerCase()
												.equalsIgnoreCase(selectedCategory)&& objproductDO.subCategoryName.toLowerCase()
												.equalsIgnoreCase(selectedSubCategory);
							}
						};	
					}
					else if(TextUtils.isEmpty(selectedCategory) && !TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return (objproductDO.Description.toLowerCase()
										.contains(strText.toLowerCase()) || objproductDO.SKU
										.toLowerCase().contains(
												strText.toLowerCase()))
												&&  objproductDO.subCategoryName.toLowerCase()
												.equalsIgnoreCase(selectedSubCategory);
							}
						};	
					}
					else if(!TextUtils.isEmpty(selectedCategory) && TextUtils.isEmpty(selectedSubCategory))
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return (objproductDO.Description.toLowerCase()
										.contains(strText.toLowerCase()) || objproductDO.SKU
										.toLowerCase().contains(
												strText.toLowerCase()))&&  objproductDO.categoryName.toLowerCase()
												.equalsIgnoreCase(selectedCategory);
							}
						};
					}
					else
					{
						searchItem = new Predicate<ProductDO>() {
							public boolean apply(ProductDO objproductDO) {
								return (objproductDO.Description.toLowerCase()
										.contains(strText.toLowerCase()) || objproductDO.SKU
										.toLowerCase().contains(
												strText.toLowerCase()));
							}
						};
					}
				}
				
				if (searchItem!=null &&(!TextUtils.isEmpty(searchText)
						|| !TextUtils.isEmpty(selectedCategory) || !TextUtils.isEmpty(selectedSubCategory))) {

					Collection<ProductDO> filteredResult = filter(hash1.get(key), searchItem);
					if (filteredResult != null && filteredResult.size() > 0) {
						tmpSearched.put(key, new ArrayList<ProductDO>((ArrayList<ProductDO>) filteredResult));
					}
				} else {
					tmpSearched = hash1;
			}
			if(storeCheckEXLVAdapter != null)
			{
				if(tmpSearched.size() > 0)
				{
					storeCheckEXLVAdapter.refreshExpandableAdapter(tmpSearched);
					tvNoSearchResult.setVisibility(View.GONE);
					expandableList.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoSearchResult.setVisibility(View.VISIBLE);
					expandableList.setVisibility(View.GONE);
				}
			}
		}
	}
	
	private void getStorePercent() 
	{
		getDeselectedSet();
		int itemCount = hashItemMissingInInventory.size();
		if(totalDistinctItemsInStore<=0)
			totalDistinctItemsInStore=1;
		
		storePercent = (((double) totalDistinctItemsInStore- (double) itemCount)/(double)totalDistinctItemsInStore )*100;
		if(storePercent < goldenSrorepercent)
		{
			if(!pbunrefectStore.isShown())
			{
				pbperfectStore.setVisibility(View.GONE);
				pbunrefectStore.setVisibility(View.VISIBLE);
			}
			pbunrefectStore.setProgress((int)storePercent);
		}
		else if(storePercent >= goldenSrorepercent)
		{
			if(!pbperfectStore.isShown())
			{
				pbunrefectStore.setVisibility(View.GONE);
				pbperfectStore.setVisibility(View.VISIBLE);
			}
			pbperfectStore.setProgress((int)storePercent);
		}
		String strPercent = percentFormat.format((float)storePercent);
		txtvStoreItemPercent.setText(strPercent + "%");
	}

	/**
	 * 
	 */
	private void getDeselectedSet() 
	{
		HashMap<String, ArrayList<ProductDO>> tmp=hashProductWithClassification;
		Set<String> brands = tmp.keySet();
		for(String brandDO:brands){
			ArrayList<ProductDO> productDOs=tmp.get(brandDO);
			for(ProductDO obj: productDOs)
			{
				if(!obj.isCaptured)
				{
					Vector<String> vec = tempMissing.get(brandDO);
					if(vec==null)
						vec = new Vector<String>();
					if(!vec.contains(obj.SKU))
						vec.add(obj.SKU);
					tempMissing.put(brandDO, vec);
				}
				else
				{
					Vector<String> vec = tempMissing.get(brandDO);
					if(vec != null && vec.contains(obj.SKU))
						vec.remove(obj.SKU);
				}
			}
		}
	}
	
	public void isItemMissing(ProductDO productDO)
	{
		ProductDO objProductDO = hashItemMissingInInventory.get(productDO.SKU);
		if(objProductDO!= null)
		{
			if(objProductDO.status == 1)
				productDO.isCaptured = false;
			productDO.StoreUOM = objProductDO.StoreUOM;
			productDO.PcsQTY = objProductDO.PcsQTY;
		}
		else
			productDO.isCaptured = true;
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		if(from.equalsIgnoreCase("Storecheck"))
			finish();
		else if(from.equalsIgnoreCase("checkout"))
			performCheckouts(true);
		else if(from.equalsIgnoreCase("goldenStoreCheck"))
			postdata();
	}
	@Override
	public void onButtonNoClick(String from) 
	{
		super.onButtonNoClick(from);
	}

	/**
	 * 
	 */
	private void initialiseScreenControls() 
	{
		txtvName				= (TextView) llLayout.findViewById(R.id.txtvName);
		tvCategory				= (TextView) llLayout.findViewById(R.id.tvCategory);
//		tvSubCategory			= (TextView) llLayout.findViewById(R.id.tvSubCategory);
		txtvStoreItemPercent 	= (TextView) llLayout.findViewById(R.id.txtvStoreItemPercent);
		etSearch				= (EditText) llLayout.findViewById(R.id.etSearch);		
		ivSearchCross			= (ImageView) llLayout.findViewById(R.id.ivSearchCross);
		pbperfectStore			= (ProgressBar) llLayout.findViewById(R.id.pbperfectStore);
		pbunrefectStore			= (ProgressBar) llLayout.findViewById(R.id.pbunrefectStore);
		tvNoSearchResult		= (TextView) llLayout.findViewById(R.id.tvNoSearchResult);
		expandableList			= (ExpandableListView) llLayout.findViewById(R.id.expandableList);
		
		expandableList.setCacheColorHint(0);
		tvCategory.requestFocus();
		
		etSearch.setFocusable(true);
		hideKeyBoard(etSearch);
		
		storeCheckEXLVAdapter = new CaptureInventoryExpandableListViewAdapter(CaptureInventoryActivity.this,hashProductWithClassification,new StoreCheck() 
		{
			@Override
			public void getStoreCheckItem(String ProductId, ProductDO productDO) 
			{
				addInMissingInVentory(ProductId, productDO);
				getStorePercent(/*ProductId,productDO*/);
			}
		});
		expandableList.setAdapter(storeCheckEXLVAdapter);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	}
	
	private void addInMissingInVentory(String itemCode,ProductDO productDO) 
	{
		if(productDO.isCaptured && hashItemMissingInInventory.containsKey(itemCode))
			hashItemMissingInInventory.remove(itemCode);
		else if(!productDO.isCaptured)
			hashItemMissingInInventory.put(itemCode, productDO);
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
	
	private void showCategories(final View v, Vector<CategoryDO> vacCategoryDOs,String title,final int type) 
	{
		 CustomBuilder customBuilder = new CustomBuilder(CaptureInventoryActivity.this, title, false);
		 customBuilder.setSingleChoiceItems(vacCategoryDOs, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				v.setTag((CategoryDO)selectedObject);
				((TextView)v).setText(((CategoryDO)selectedObject).categoryName);
				
				etSearch.setText("");
				builder.dismiss();
				switch (type) {
				case 1:
					if(selectedCategory!=null && !selectedCategory.equalsIgnoreCase(((CategoryDO)selectedObject).categoryId)){
						selectedSubCategory=null;
//						tvSubCategory.setText("All Subcategory");
					}
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
}
