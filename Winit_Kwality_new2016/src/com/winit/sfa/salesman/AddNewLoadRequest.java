package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.AddItemVanAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.WareHouseDA;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.PreviewVanloadDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class AddNewLoadRequest extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader, llTopLayout,llSearchBar;
	private Button btnOrdersheetVerify,btnOrdersheetAddNew;
	private LinearLayout llOrdersheetVerify,llOrdersheetAddNew;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView  tvUOM, tvQty, tvTotalQt, tvOrdersheetHeader, tvNoItemFoundBase,tvFullFilQty;
	private ImageView ivCheckAllItems;
//	private Vector<VanLoadDO> vecSearchedItemd;
	private ArrayList<VanLoadDO> vecSearchedItemd;
	private Paint mPaint;
	private AddItemVanAdapter adapter;
	private int load_type = -1;
	private LoadRequestDO loadRequestDO;
	private boolean isEditable = true;
	
	private boolean isSalable = false, isUnload = false;
	
	private EditText etSearch;
	
	private ArrayList<VanLoadDO> vecVanLoadTemp;
	private WareHouseDetailDO objWareHouseDetail;
	private Vector<WareHouseDetailDO> vecWareHouseDetail;
	private TextView tvWareHouse;
	private RelativeLayout rlWareHouse;
	
	private TextView tvTotalQtUN,tvVanQty, tvRecomendedLoad;
	private ImageView ivSepUN,ivSeprator, ivRecomendedLoad;
	
	private ImageView ivCheckAllItems1, ivSepCheck;
	private int resource = -1;
	private String selectedCategory,selectedSubCategory;
	private ListView lvItems;
	private ItemListAdapter ordersheetadapter;
	Vector<CategoryDO> vecAllCategories;
	private int listScrollState;
	private HashMap<String, HHInventryQTDO> hmInventory;
	HashMap<String,UOMConversionFactorDO> hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
	private static final String SYNC_EDIT="sync_edit";
	
	private ImageView ivSearchCross;
	private int Division = 0;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.add_stock_inventory_new, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		Bundle bundle  = getIntent().getExtras();
		loadData();
		if(bundle != null)
		{
			load_type	 	= 	bundle.getInt("load_type");
			loadRequestDO	= 	(LoadRequestDO)bundle.get("object");
			
			if(bundle.containsKey("isEditable"))
				isEditable = bundle.getBoolean("isEditable");
			
			if(bundle.containsKey("isSalable"))
				isSalable = bundle.getBoolean("isSalable");
			if(bundle.containsKey("isUnload"))
				isUnload = bundle.getBoolean("isUnload");
			
			if(getIntent().hasExtra(AppConstants.DIVISION))
				Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
		}
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		
		if(isUnload)
		{
			tvOrdersheetHeader.setText("Unload Items");
		}
		if(load_type== AppConstants.LOAD_STOCK)
			tvOrdersheetHeader.setText("Load Request");
		else if(load_type== AppConstants.UNLOAD_STOCK)
			tvOrdersheetHeader.setText("Unload Request");
//			tvOrdersheetHeader.setText(tvOrdersheetHeader.getText().toString());
		
		lvItems.setVisibility(View.GONE);
		tvNoItemFoundBase.setVisibility(View.VISIBLE);
		
		if(load_type == AppConstants.UNLOAD_STOCK)
			tvNoItemFoundBase.setText("Please tap on Add Item button to add items for unload request.");
		else
			tvNoItemFoundBase.setText("No items to display.");
		
		if(isUnload)
			tvNoItemFoundBase.setText("No items to display.");
	
		if(load_type==AppConstants.LOAD_STOCK){
			tvVanQty.setVisibility(View.VISIBLE);
			ivSeprator.setVisibility(View.VISIBLE);
		}
		lvItems.setVerticalScrollBarEnabled(false);
		lvItems.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvItems.setCacheColorHint(0);
		lvItems.setFadingEdgeLength(0);
		lvItems.setAdapter(ordersheetadapter = new ItemListAdapter(new ArrayList<VanLoadDO>()));
		
		lvItems.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				listScrollState = scrollState;
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				if(ordersheetadapter!=null && listScrollState!=0)
					ordersheetadapter.view=null;
			}
		});
		isUnload = false;
		llOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				btnOrdersheetVerify.performClick();
			}
		});
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Refresh"))
					loadRequestStatus();
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Continue"))
				{
					Intent intent = new Intent(AddNewLoadRequest.this, VerifyItemInVehicle.class);
//					intent.putExtra("updatededDate", vecOrdProduct);
					intent.putExtra("movementId", loadRequestDO.MovementCode);
					intent.putExtra("movementType", loadRequestDO.MovementType);
					intent.putExtra("isMenu", false);
					intent.putExtra(AppConstants.DIVISION, Division);
					startActivityForResult(intent, 1111);
				}
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Finish"))
				{
					finish();
				}
				else if(load_type == AppConstants.UNLOAD_STOCK)
				{
					if(validateItems(vecOrdProduct))
					{
						PreviewVanloadDO objPreviewVanloadDO = getSelectedItems(vecOrdProduct);
						Intent intent = new Intent(AddNewLoadRequest.this, PreviewNewLoadRequest.class);
						intent.putExtra("load_type", load_type);
						intent.putExtra("arrvanload", objPreviewVanloadDO);
						intent.putExtra("objWareHouseDetail", objWareHouseDetail);
						intent.putExtra(AppConstants.DIVISION, Division);
						startActivityForResult(intent,2222);						
//						insertUpdateRequest(vecOrdProduct);
					}
					else
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item for unload request.", "OK", null, "");
				}
				else if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
				{
					if(validateItems(vecSearchedItemd))
					{
						PreviewVanloadDO objPreviewVanloadDO = getSelectedItems(vecSearchedItemd);
						Intent intent = new Intent(AddNewLoadRequest.this, PreviewNewLoadRequest.class);
						intent.putExtra("load_type", load_type);
						intent.putExtra("arrvanload", objPreviewVanloadDO);
						intent.putExtra(AppConstants.DIVISION, Division);
						startActivityForResult(intent,2222);
//						insertUpdateRequest(vecOrdProduct);
					}
					else
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item having quantity greater than zero.", "OK", null, "");
				}
				else
				{
					if(load_type == AppConstants.LOAD_STOCK)
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item for load request.", "OK", null, "");
					else
						showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Please select atleast one item for unload request.", "OK", null, "");
				}
			}
		});
		
		if(vecOrdProduct == null)
			vecOrdProduct = new ArrayList<VanLoadDO>();
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				if(ordersheetadapter!=null){
					ordersheetadapter.view=null;
					ordersheetadapter.refreshList(vecSearchedItemd);
				}
			}
		});
		
		vecVanLoadTemp = new ArrayList<VanLoadDO>();
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				vecVanLoadTemp = searchUnloadItems(s.toString());
				if(ordersheetadapter!=null){
					ordersheetadapter.view=null;
					ordersheetadapter.refreshList(vecVanLoadTemp);
				}
			}
		});
		tvWareHouse.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showWareHouseSelector();
			}
		});
		llOrdersheetAddNew.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				btnOrdersheetAddNew.performClick();
			}
		});
		
		btnOrdersheetAddNew.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(tvWareHouse.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(AddNewLoadRequest.this, getResources().getString(R.string.warning), "Please select a Warehouse.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
//					showAddNewSkuPopUp();
				}
			}
		});
		
		ivCheckAllItems1.setTag("unchecked");
		ivCheckAllItems1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				showLoader("Please wait....");
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(v.getTag().toString().equalsIgnoreCase("unchecked"))
						{
							v.setTag("checked");
							resource = R.drawable.checkbox_white;
							for(int j = 0; j < vecOrdProduct.size() ; j++)
							{
								vecOrdProduct.get(j).itemChecked = true;
							}
						}
						else
						{
							v.setTag("unchecked");
							resource = R.drawable.uncheckbox_white;
							for(int j = 0; j < vecOrdProduct.size() ; j++)
							{
								vecOrdProduct.get(j).itemChecked = false;
							}
						}
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								hideLoader();
								((ImageView)v).setImageResource(resource);
//								inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
								ordersheetadapter.refreshList(vecOrdProduct);
							}
						});
					}
				}).start();
			}
		});
		
		setTypeFaceRobotoNormal(llOrderSheet);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(Typeface.DEFAULT_BOLD);
		btnOrdersheetAddNew.setTypeface(Typeface.DEFAULT_BOLD);
	}

	private PreviewVanloadDO getSelectedItems(ArrayList<VanLoadDO> vecOrdProduct) 
	{
		PreviewVanloadDO objprevDo = new PreviewVanloadDO();
		for (VanLoadDO vanLoadDO : vecOrdProduct) 
		{
			if(vanLoadDO.quantityLevel1 > 0 || vanLoadDO.quantityLevel3 > 0)
			{
				objprevDo.arrvanload.add(vanLoadDO);
			}
		}
		return objprevDo;
	}
	
//	private ArrayList<VanLoadDO> getSelectedItems() 
//	{
//		ArrayList<VanLoadDO> arrvanload = new ArrayList<VanLoadDO>();
//		for (VanLoadDO vanLoadDO : vecOrdProduct) 
//		{
//			if(vanLoadDO.quantityLevel1 > 0 || vanLoadDO.quantityLevel3 > 0)
//			{
//				arrvanload.add(vanLoadDO);
//			}
//		}
//		return arrvanload;
//	}
	
	private void loadData()
	{
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				hashArrUoms =new ProductsDA().getUOMConversion(TrxDetailsDO.getItemUomLevel1());
				vecAllCategories=new CaptureInventryDA().getAllCategories();
//				vecSearchedItemd = new ProductsDA().getProductsVanByCategoryId(null, "", load_type, preference);
				vecSearchedItemd = new ProductsDA().getVanProductsByCategoryId(null, "", load_type, preference,Division);
				if(loadRequestDO == null && load_type == AppConstants.UNLOAD_STOCK)
				{
					hmInventory = new OrderDetailsDA().getAvailInventoryQtys();		
//					showLoader(getString(R.string.please_wait));
					Vector<VanLoadDO> vecOrdProductNew = new ProductsDA().getProductsVanByCategoryId(null, "", load_type, preference,Division);
					vecOrdProduct = getArrVanLoad(vecOrdProductNew);
				}else{
					if(loadRequestDO != null)
						vecOrdProduct	=	new InventoryDA().getAllItemToVerifyByLoadId(loadRequestDO.MovementCode);

					vecWareHouseDetail = new WareHouseDA().getAllWareHouseforthisSalesPerson(preference.getStringFromPreference(preference.ORG_CODE, ""));
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						if(isUnload && loadRequestDO == null)
						{
							loadUnLaodData();
							hideLoader();
						}
						else
						{
							if(vecOrdProduct != null && vecOrdProduct.size() > 0)
							{
								ordersheetadapter.refreshList(vecOrdProduct);
								lvItems.setVisibility(View.VISIBLE);
								tvNoItemFoundBase.setVisibility(View.GONE);
								hideLoader();
							}
							else
							{
								if(load_type != AppConstants.UNLOAD_STOCK){
									if(vecWareHouseDetail!=null && vecWareHouseDetail.size()==1){
										objWareHouseDetail = vecWareHouseDetail.get(0);
										tvWareHouse.setText(objWareHouseDetail.WareHouseName+" ("+objWareHouseDetail.WareHouseCode+")");
										new Handler().postDelayed(new Runnable() {
											@Override
											public void run() {
//												showAddNewSkuPopUp();
												
//												ordersheetadapter.refreshList(vecVanLoadDOs);
												ordersheetadapter.refreshList(vecSearchedItemd);
												hideLoader();
											}
										}, 100);
									}else{
										/***********Temporary*******************/
//										vecOrdProduct = vecSearchedItemd;
//										ordersheetadapter.refreshList(vecSearchedItemd);
										/******************************/
										showWareHouseSelector();
									}
								}
							}
						}
						
					}
				});
			}
		}).start();
		
	}
	private void showWareHouseSelector() 
	{
		CustomBuilder builder = new CustomBuilder(AddNewLoadRequest.this, "Select Warehouse", true);
		builder.setSingleChoiceItems(vecWareHouseDetail, null, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				objWareHouseDetail = (WareHouseDetailDO) selectedObject;
				tvWareHouse.setText(objWareHouseDetail.WareHouseName+" ("+objWareHouseDetail.WareHouseCode+")");
	    		builder.dismiss();
    			
    			if(load_type == AppConstants.UNLOAD_STOCK && vecOrdProduct != null && vecOrdProduct.size() >0)
				{
//					inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
    				ordersheetadapter.refreshList(vecOrdProduct);
    				lvItems.setVisibility(View.VISIBLE);
					tvNoItemFoundBase.setVisibility(View.GONE);
				}
    			else
    			{
					ordersheetadapter.refreshList(vecSearchedItemd);
//    				showAddNewSkuPopUp();
    			}
    			
		    }
		}); 
		builder.show();				
	}
	
	protected void insertUpdateRequest(final ArrayList<VanLoadDO> vecOrdProductNew) 
	{
		showLoader("Uploading...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				InventoryDA inventoryDA = new InventoryDA();
				
				String movementId = ""+inventoryDA.getMovementId(preference.getStringFromPreference(Preference.SALESMANCODE, ""),Division);
				
				if(!TextUtils.isEmpty(movementId))
				{
					loadRequestDO 				= 	new LoadRequestDO();
					loadRequestDO.MovementCode	=	""+movementId;
					loadRequestDO.PreMovementCode=	""+loadRequestDO.MovementCode;
					loadRequestDO.AppMovementId	=	""+StringUtils.getUniqueUUID();
					loadRequestDO.OrgCode		=	""+preference.getStringFromPreference(Preference.ORG_CODE, "");
					loadRequestDO.UserCode		=	""+preference.getStringFromPreference(Preference.EMP_NO, "");
					loadRequestDO.WHKeeperCode	=	""+preference.getStringFromPreference(Preference.EMP_NO, "");
					loadRequestDO.CurrencyCode	=	""+preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
					loadRequestDO.JourneyCode	=	"DUB";
					loadRequestDO.MovementDate	=	CalendarUtils.getCurrentDateTime();
					loadRequestDO.MovementNote	=	"TEST";
					loadRequestDO.MovementType	=	""+load_type;
					loadRequestDO.SourceVehicleCode			=""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					loadRequestDO.DestinationVehicleCode	=""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					loadRequestDO.Status		=	"N";
					loadRequestDO.VisitID		= 	"0";
					loadRequestDO.MovementStatus=	""+LoadRequestDO.MOVEMENT_STATUS_PENDING;
					loadRequestDO.CreatedOn		=	loadRequestDO.MovementDate;
					loadRequestDO.ApproveByCode	=	"0";
					loadRequestDO.ApprovedDate	=	loadRequestDO.MovementDate;
					loadRequestDO.JDETRXNumber	=	loadRequestDO.MovementCode;
					loadRequestDO.ISStampDate	=	loadRequestDO.MovementDate;
					loadRequestDO.ISFromPC		=	"0";
					loadRequestDO.OperatorCode	=	"0";
					loadRequestDO.IsDummyCount	=	"0";
					loadRequestDO.ModifiedDate	=	CalendarUtils.getOrderPostDate();
					loadRequestDO.ModifiedTime	=	CalendarUtils.getRetrunTime();
					loadRequestDO.PushedOn		=	loadRequestDO.MovementDate;
					loadRequestDO.ModifiedOn	=	loadRequestDO.MovementDate;
					
					if(objWareHouseDetail != null)
						loadRequestDO.WHCode	=	""+objWareHouseDetail.WareHouseCode;//Change surely ask Basant
					
					if(isUnload && isSalable)
						loadRequestDO.ProductType	=	"Sellable";
					else if(isUnload && !isSalable)
						loadRequestDO.ProductType	=	"Non Sellable";
					else
						loadRequestDO.ProductType	=	"";
						
					
					loadRequestDO.vecItems 		= 	getVector(vecOrdProductNew, loadRequestDO);
					
					inventoryDA.insertLoadRequest(loadRequestDO, load_type);
					
					if(isUnload)
						new ProductsDA().updateUnlod(isSalable, loadRequestDO.vecItems);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							if(load_type == AppConstants.LOAD_STOCK)
								showCustomDialog(AddNewLoadRequest.this, "Successful !", "Load request has been made successfully.", "OK", null, "finish", false);

							else
								showCustomDialog(AddNewLoadRequest.this, "Successful !", "Unload request has been made successfully.", "OK", null, "finish", false);
								uploadData();
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
							hideLoader();
							showCustomDialog(AddNewLoadRequest.this, "Alert !", "Please try after some time.", "OK", null, "");
						}
					});
				}
			}
			
		}).start();
	}
	
	private ArrayList<LoadRequestDetailDO> getVector(ArrayList<VanLoadDO> vecOrdProduct, LoadRequestDO loadRequestDO) 
	{
		int count = 1;
		ArrayList<LoadRequestDetailDO> vec = new ArrayList<LoadRequestDetailDO>();
		long totalQty=0;
		for(VanLoadDO object : vecOrdProduct)
		{
			if(object.quantityLevel1 > 0||object.quantityLevel3>0)
			{
				LoadRequestDetailDO loDetailDO = new LoadRequestDetailDO();
				loDetailDO.LineNo			= 	""+count++;
				loDetailDO.MovementCode		=	""+loadRequestDO.MovementCode;
				loDetailDO.ItemCode			=	""+object.ItemCode;
				loDetailDO.OrgCode			=	""+loadRequestDO.OrgCode;
				loDetailDO.ItemDescription	=	""+object.Description;
				loDetailDO.ItemAltDescription=	""+object.Description;
				loDetailDO.MovementStatus	=	""+LoadRequestDO.MOVEMENT_STATUS_PENDING;
				loDetailDO.UOM				=	""+object.UOM;
				loDetailDO.QuantityLevel1	=	object.quantityLevel1;
				loDetailDO.QuantityLevel2	=	object.quantityLevel2;
				loDetailDO.QuantityLevel3	=	object.quantityLevel3;
				UOMConversionFactorDO uomConversionFactorDO=hashArrUoms.get(loDetailDO.ItemCode);
				int eaConversion=1;
				if(uomConversionFactorDO!=null)
					eaConversion=(int)uomConversionFactorDO.eaConversion;
				loDetailDO.QuantityBU		=	loDetailDO.QuantityLevel1*eaConversion+loDetailDO.QuantityLevel3;
				loDetailDO.QuantitySU		=	loDetailDO.QuantityBU;
				totalQty = totalQty+loDetailDO.QuantityBU;
				loDetailDO.inProcessQuantityLevel1	=	object.inProcessQuantityLevel1;
				loDetailDO.inProcessQuantityLevel2	=	object.inProcessQuantityLevel2;
				loDetailDO.inProcessQuantityLevel3	=	object.inProcessQuantityLevel3;
				loDetailDO.InProcessQuantity		=	loDetailDO.inProcessQuantityLevel1*eaConversion+loDetailDO.inProcessQuantityLevel3;
				
				loDetailDO.shippedQuantityLevel1	=	object.shippedQuantityLevel1;
				loDetailDO.shippedQuantityLevel2	=	object.shippedQuantityLevel2;
				loDetailDO.shippedQuantityLevel3	=	object.shippedQuantityLevel3;
				loDetailDO.ShippedQuantity			=	loDetailDO.shippedQuantityLevel1*eaConversion+loDetailDO.shippedQuantityLevel3;
				
				
				loDetailDO.NonSellableQty	=	0;
				loDetailDO.CurrencyCode		=	""+preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
				loDetailDO.PriceLevel1		=	0;
				loDetailDO.PriceLevel2		=	0;
				loDetailDO.PriceLevel3		=	0;
				loDetailDO.MovementReasonCode=	"0";/*"NONE";*/
				loDetailDO.ExpiryDate		=	""+loadRequestDO.CreatedOn;
				loDetailDO.Note				=	"NONE";
				loDetailDO.AffectedStock	=	""+(int)object.SellableQuantity;
				loDetailDO.Status			=	"Pending";
				loDetailDO.DistributionCode	=	""+loadRequestDO.OrgCode;;
				loDetailDO.CreatedOn		=	""+loadRequestDO.CreatedOn;;
				loDetailDO.ModifiedDate		=	""+loadRequestDO.ModifiedDate;
				loDetailDO.ModifiedTime		=	""+loadRequestDO.ModifiedTime;
				loDetailDO.PushedOn			=	""+loadRequestDO.PushedOn;
				loDetailDO.CancelledQuantity=	loDetailDO.InProcessQuantity-loDetailDO.ShippedQuantity;
				
				loDetailDO.ModifiedOn		= 	""+loadRequestDO.ModifiedOn;
				vec.add(loDetailDO);
			}
		}
		loadRequestDO.Amount=totalQty;
		return vec;
	}

	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids add_stock_inventory
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		rlWareHouse				=	(RelativeLayout) llOrderSheet.findViewById(R.id.rlWareHouse);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		btnOrdersheetAddNew		=	(Button) llOrderSheet.findViewById(R.id.btnOrdersheetAddNew);
		llOrdersheetVerify		=	(LinearLayout)llOrderSheet.findViewById(R.id.llOrdersheetVerify);
		llOrdersheetAddNew		=	(LinearLayout) llOrderSheet.findViewById(R.id.llOrdersheetAddNew);
		llSearchBar				=	(LinearLayout) llOrderSheet.findViewById(R.id.llSearchBar);
		lvItems					=	(ListView)llOrderSheet.findViewById(R.id.lvItems);
		
		tvFullFilQty			=	(TextView)llOrderSheet.findViewById(R.id.tvFullFilQty);
		tvWareHouse				=	(TextView)llOrderSheet.findViewById(R.id.tvWareHouse);
		
		
		tvUOM					=	(TextView)llOrderSheet.findViewById(R.id.tvUOM);
		tvQty					=	(TextView)llOrderSheet.findViewById(R.id.tvQty);
		tvTotalQt				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQt);
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFoundBase		=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		llTopLayout				=	(LinearLayout)llOrderSheet.findViewById(R.id.llTopLayout);
		
		ivCheckAllItems1		=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems1);
		ivSepCheck				=	(ImageView)llOrderSheet.findViewById(R.id.ivSepCheck);
		
		
		
		etSearch			=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		
		llHeader 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llHeader);
		
		tvTotalQtUN		=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQtUN);
		tvTotalQtUN.setText(Html.fromHtml("Van Qty"));
		tvVanQty		=	(TextView)llOrderSheet.findViewById(R.id.tvVanQty);
		tvVanQty.setText(Html.fromHtml("Van Qty"));
		ivSeprator		=	(ImageView)llOrderSheet.findViewById(R.id.ivSeprator);
		ivSepUN			=	(ImageView)llOrderSheet.findViewById(R.id.ivSepUN);
		
		ivRecomendedLoad			=	(ImageView)llOrderSheet.findViewById(R.id.ivRecomendedLoad);
		tvRecomendedLoad			=	(TextView)llOrderSheet.findViewById(R.id.tvRecomendedLoad);
		
		ivCheckAllItems.setVisibility(View.GONE);
		llTopLayout.setVisibility(View.VISIBLE);
		
		tvOrdersheetHeader.setVisibility(View.VISIBLE);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        tvUOM.setText("UOM");
        
        refreshLoad();
        
        if(loadRequestDO == null && load_type == AppConstants.UNLOAD_STOCK)
        {
        	tvTotalQtUN.setVisibility(View.VISIBLE);
    		ivSepUN.setVisibility(View.VISIBLE);
    		
    		ivRecomendedLoad.setVisibility(View.VISIBLE);
    		tvRecomendedLoad.setVisibility(View.VISIBLE);
    		
    		tvQty.setText(getResources().getString(R.string.unload_qty));
    		tvQty.setLines(2);
//    		ivCheckAllItems1.setVisibility(View.VISIBLE);
//    		ivSepCheck.setVisibility(View.VISIBLE);
    		
    		rlWareHouse.setVisibility(View.GONE);
    		llSearchBar.setVisibility(View.GONE);
    		btnOrdersheetAddNew.setVisibility(View.GONE);
        }
        else
        {
        	tvTotalQtUN.setVisibility(View.GONE);
    		ivSepUN.setVisibility(View.VISIBLE);
    		
    		ivRecomendedLoad.setVisibility(View.VISIBLE);
    		tvRecomendedLoad.setVisibility(View.VISIBLE);
    		
//    		ivCheckAllItems1.setVisibility(View.GONE);
//    		ivSepCheck.setVisibility(View.GONE);
    		
    		rlWareHouse.setVisibility(View.VISIBLE);
    		btnOrdersheetAddNew.setVisibility(View.VISIBLE);
        }
	}
	
	
	private void refreshLoad()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				if(loadRequestDO != null)
					vecOrdProduct	=	new InventoryDA().getAllItemToVerifyByLoadId(loadRequestDO.MovementCode);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(vecOrdProduct != null && vecOrdProduct.size() > 0)
						{
//							inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
							ordersheetadapter.refreshList(vecOrdProduct);
							lvItems.setVisibility(View.VISIBLE);
							tvNoItemFoundBase.setVisibility(View.GONE);
						}
						
						if(loadRequestDO != null && (loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED) || loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY)))
				        {
				        	if(loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED)){
				        		tvTotalQt.setVisibility(View.VISIBLE);
				        		tvTotalQt.setText("App. Qty");
				        	}
				        	else{
				        		tvTotalQt.setVisibility(View.VISIBLE);
				        		tvTotalQt.setText("App. Qty");
				        		tvFullFilQty.setText("Fulfill Qty");
				        		tvFullFilQty.setVisibility(View.VISIBLE);
				        	}
				        	tvQty.setText("Req. Qty");
				        }
				        else
				        	tvTotalQt.setVisibility(View.GONE);
						hideLoader();
					}
				});
				
			}
		}).start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(loadRequestDO != null)
        {
        	if(loadRequestDO.MovementStatus != null && loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED))
        		btnOrdersheetVerify.setText("Continue");
        	else if(loadRequestDO.MovementStatus != null && loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY))
        		btnOrdersheetVerify.setText("Finish");
        	else
        		btnOrdersheetVerify.setText("Refresh");
        	if(StringUtils.getInt(loadRequestDO.MovementStatus)==LoadRequestDO.MOVEMENT_STATUS_REJECTED){
        		btnOrdersheetVerify.setText("Finish");
        	}
        }
        else
        	btnOrdersheetVerify.setText("Preview");
		
		if(loadRequestDO == null)
		{
//			tvOrdersheetHeader.setText("Add New Request");
			if(load_type== AppConstants.LOAD_STOCK)
				tvOrdersheetHeader.setText("Load Request");
			else if(load_type== AppConstants.UNLOAD_STOCK)
				tvOrdersheetHeader.setText("Unload Request");
			
			if(load_type != AppConstants.UNLOAD_STOCK)
				rlWareHouse.setVisibility(View.VISIBLE);
		}
		else
		{
			switch (StringUtils.getInt(loadRequestDO.MovementStatus)) {
			case LoadRequestDO.MOVEMENT_STATUS_PENDING:
				tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Pending)");
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
				tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Approved)");
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
				tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Collected)");
				break;	
			case LoadRequestDO.MOVEMENT_STATUS_REJECTED:
				tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Rejected)");
				break;

			default:
				break;
			}
			
			rlWareHouse.setVisibility(View.GONE);			
			btnOrdersheetAddNew.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("finish"))
		{
			setResult(1111);
			finish();
		}
		else if(from.equalsIgnoreCase("Verify"))
		{
			loadRequestDO.MovementStatus = LoadRequestDO.MOVEMENT_STATUS_APPROVED+"";
			btnOrdersheetVerify.setText("Continue");
			refreshLoad();
		}
		else if(from.equalsIgnoreCase("Alert")||from.equalsIgnoreCase("GoBack"))
		{
			finish();
		}
	}
	@Override
	public void onBackPressed() {
		if (loadRequestDO==null && load_type == AppConstants.LOAD_STOCK && ordersheetadapter != null
				&& ordersheetadapter.getCount()> 0 && loadRequestDO == null) {
			showCustomDialog(AddNewLoadRequest.this, getString(R.string.warning), "Are you sure you don't want to save current data?", getString(R.string.Yes), getString(R.string.No), "GoBack");
		} else{
			super.onBackPressed();
		}
	}
	public void sortCategory()
	{
	}
	
	private EditText etSeacrhField;
	private TextView tvSubCategory;

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
						return !TextUtils.isEmpty(categoryDO.parentCode)||(!TextUtils.isEmpty(categoryDO.parentCode)&&categoryDO.parentCode.equalsIgnoreCase("ALL"));
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
	public ArrayList<VanLoadDO> searchUnloadItems(final String searchText) 
	  {
		  Predicate<VanLoadDO> searchItem =null;
		  if(!TextUtils.isEmpty(searchText) )
		  {
			  searchItem = new Predicate<VanLoadDO>() {
					public boolean apply(VanLoadDO trxDetailsDO) 
					{
						return (trxDetailsDO.Description.toLowerCase().contains(searchText.toLowerCase())
								|| trxDetailsDO.ItemCode.toLowerCase().contains(searchText.toLowerCase()));
					}
				};
				
		  }
		  else{
			  return vecSearchedItemd;
		  }
		  if(vecSearchedItemd != null && searchItem != null)
		  {
			  Collection<VanLoadDO> filteredResult = filter(vecSearchedItemd, searchItem);
			  if(filteredResult!=null)
				  return (ArrayList<VanLoadDO>) filteredResult;
		  }
		  
		  return new ArrayList<VanLoadDO>();
	  }
	  public Vector<VanLoadDO> getSearchItems(final String searchText) 
	  {
		  Predicate<VanLoadDO> searchItem =null;
		  if(TextUtils.isEmpty(searchText)){
			  if(!TextUtils.isEmpty(selectedCategory)&& !TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.CategoryId.equalsIgnoreCase(selectedCategory) 
									&& trxDetailsDO.subCategoryId.equalsIgnoreCase(selectedSubCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO);//trxDetailsDO.ischecked!=true
						}
					};  
			  }else if(TextUtils.isEmpty(selectedCategory)&& !TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.subCategoryId.equalsIgnoreCase(selectedSubCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO);//trxDetailsDO.ischecked!=true
						}
					}; 
			  }else if(!TextUtils.isEmpty(selectedCategory)&& TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.CategoryId.equalsIgnoreCase(selectedCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO);//trxDetailsDO.ischecked!=true
						}
					}; 
			  }else{
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return !vecOrdProduct.contains(trxDetailsDO);//trxDetailsDO.ischecked!=true
						}
					}; 
			  }
		  }else{
			  if(!TextUtils.isEmpty(selectedCategory)&& !TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.CategoryId.equalsIgnoreCase(selectedCategory) 
									&& trxDetailsDO.subCategoryId.equalsIgnoreCase(selectedSubCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO)&&(trxDetailsDO.Description.toLowerCase().contains(searchText.toLowerCase())||trxDetailsDO.ItemCode.toLowerCase().contains(searchText.toLowerCase()));//trxDetailsDO.ischecked!=true
						}
					};
			  }else if(TextUtils.isEmpty(selectedCategory)&& !TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.subCategoryId.equalsIgnoreCase(selectedSubCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO)&&(trxDetailsDO.Description.toLowerCase().contains(searchText.toLowerCase())||trxDetailsDO.ItemCode.toLowerCase().contains(searchText.toLowerCase()));
						}
					}; 
			  }else if(!TextUtils.isEmpty(selectedCategory)&& TextUtils.isEmpty(selectedSubCategory)){
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return  trxDetailsDO.CategoryId.equalsIgnoreCase(selectedCategory) 
									&& !vecOrdProduct.contains(trxDetailsDO)&&(trxDetailsDO.Description.toLowerCase().contains(searchText.toLowerCase())||trxDetailsDO.ItemCode.toLowerCase().contains(searchText.toLowerCase()));
						}
					}; 
			  }else{
				  searchItem = new Predicate<VanLoadDO>() {
						public boolean apply(VanLoadDO trxDetailsDO) 
						{
							return (trxDetailsDO.Description.toLowerCase().contains(searchText.toLowerCase())||trxDetailsDO.ItemCode.toLowerCase().contains(searchText.toLowerCase())) &&!vecOrdProduct.contains(trxDetailsDO);//trxDetailsDO.ischecked!=true
						}
					};
			  }
		  }
		  if(vecSearchedItemd != null && searchItem != null)
		  {
			  Collection<VanLoadDO> filteredResult = filter(vecSearchedItemd, searchItem);
			  if(filteredResult!=null)
				  return new Vector<VanLoadDO>((ArrayList<VanLoadDO>) filteredResult);
		  }
		  
		  return new Vector<VanLoadDO>();
	  }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1111 && resultCode == 1111)
		{
			setResult(1111);
			finish();
		}
		else if(requestCode == 2222 && resultCode == 2222)
		{
			setResult(1111);
			finish();
		}
		else if(requestCode == 1000 && AppConstants.objScanResultObject!=null)
		{
			Intent intent = new Intent(AddNewLoadRequest.this, ScanItemActivity.class);
			startActivityForResult(intent, 100);
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			VanLoadDO dco = (VanLoadDO) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
				ordersheetadapter.refreshList(vecOrdProduct);
			}
		}
	}
	
	private void loadRequestStatus()
	{
		if(isNetworkConnectionAvailable(AddNewLoadRequest.this))
		{
			showLoader("Refreshing...");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
					
					loadAllMovements_Sync("Refreshing data...",empNo);
//					loadVanStock_Sync("Loading Stock...", empNo);
					
					loadSplashScreenData(empNo);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							int status = new InventoryDA().getPendingStatus(loadRequestDO.MovementCode);
							switch (status) {
							case LoadRequestDO.MOVEMENT_STATUS_PENDING:
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Pending)");
								showCustomDialog(AddNewLoadRequest.this, "Warning !", "Request is still pending, Please try after some time.", "OK", null, "");
								break;
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
								showCustomDialog(AddNewLoadRequest.this, "Successful", "Request has been approved.", "OK", null, "Verify");
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Approved)");
								break;
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Collected)");
								break;	
							case LoadRequestDO.MOVEMENT_STATUS_REJECTED:{
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Rejected)");
								if(load_type == AppConstants.LOAD_STOCK)
									showCustomDialog(AddNewLoadRequest.this, "Alert!", "Your Load request has been rejected by admin.", "OK", null, "finish");
								else
									showCustomDialog(AddNewLoadRequest.this, "Alert!", "Your Unload request has been rejected by admin.", "OK", null, "finish");
								
								if(isUnload)
								{
									new ProductsDA().updateUnlodStatus(vecOrdProduct);
								}
							}
								break;

							default:
								break;
							}
							
						}
					});
				}
			}).start();
		}
		else
			showCustomDialog(AddNewLoadRequest.this, "Warning !", getString(R.string.no_internet), "OK", null, "");
	}
	
	private boolean isInventoryAvail(VanLoadDO objItem)
	{
		boolean isAvail = false;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.ItemCode))
		{
			float availQty = hmInventory.get(objItem.ItemCode).totalQt;
			UOMConversionFactorDO umConversionFactorD=hashArrUoms.get(objItem.ItemCode);
			int eaConversion=1;
			if(umConversionFactorD!=null)
				eaConversion=(int) umConversionFactorD.eaConversion;
			int totalRequestQty=objItem.quantityLevel1*eaConversion+objItem.quantityLevel3;
			
			if(totalRequestQty> availQty)
				isAvail = false;
			else
				isAvail = true;
		}
		else
			isAvail = false;
		return isAvail;
	}
	
	private boolean validateItems(ArrayList<VanLoadDO> vecOrdProduct)
	{
		boolean isFound = false;
		for (VanLoadDO vanLoadDO : vecOrdProduct) 
		{
			if(vanLoadDO.quantityLevel1 > 0||vanLoadDO.quantityLevel3 > 0)
			{
				isFound = true ;
				break;
			}
		}
		
		return isFound;
	}
	
	private void loadUnLaodData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				final Vector<VanLoadDO> veciItems = new ProductsDA().getProductsUnload(isSalable);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						hideLoader();
						if(veciItems != null && veciItems.size() > 0)
						{
							if(vecOrdProduct == null)
								vecOrdProduct = new ArrayList<VanLoadDO>();
							
							vecOrdProduct.addAll(veciItems);
							if(vecOrdProduct != null && vecOrdProduct.size() >0)
							{
								ordersheetadapter.refreshList(vecOrdProduct);
								lvItems.setVisibility(View.VISIBLE);
								tvNoItemFoundBase.setVisibility(View.GONE);
							}
						}
					}
				});
			}
		}).start();
	}
	
	private ArrayList<VanLoadDO> getArrVanLoad(Vector<VanLoadDO> vecVanLoadDOs)
	{
		ArrayList<VanLoadDO> arrVanLoadDOs = new ArrayList<VanLoadDO>();
		if(vecVanLoadDOs != null && vecVanLoadDOs.size() > 0)
		{
			for (VanLoadDO vanLoadDO : vecVanLoadDOs)
			{
				arrVanLoadDOs.add(vanLoadDO);
			}
		}
		
		return arrVanLoadDOs;
	}
	
	private ArrayList<VanLoadDO> getCheckedItem(ArrayList<VanLoadDO> vecVanLoadDOs)
	{
		ArrayList<VanLoadDO> veCHecked = new ArrayList<VanLoadDO>();
		if(vecVanLoadDOs != null && vecVanLoadDOs.size() > 0)
		{
			for (VanLoadDO vanLoadDO : vecVanLoadDOs) {
				if(vanLoadDO.itemChecked)
					veCHecked.add(vanLoadDO);
			}
		}
		return veCHecked;
	}
	
	private void isAllChecked()
	{
		boolean isAllChecked = true;
		if(vecOrdProduct != null && vecOrdProduct.size()>0)
		{
			for (VanLoadDO vanLoadDO : vecOrdProduct) {
				if(!vanLoadDO.itemChecked)
				{
					isAllChecked = false;
					break;
				}
			}
		}
		else
		{
			isAllChecked = false;
		}
		if(isAllChecked)
		{
			ivCheckAllItems1.setTag("checked");
			ivCheckAllItems1.setImageResource(R.drawable.checkbox_white);
		}
	}
	
	private class ItemListAdapter extends BaseAdapter
	{

		private ArrayList<VanLoadDO> vecVanLoadDOs;
		public ItemListAdapter(ArrayList<VanLoadDO> vecVanLoadDOs) 
		{
			this.vecVanLoadDOs = vecVanLoadDOs;
		}
		@Override
		public int getCount() 
		{
			if(vecVanLoadDOs != null && vecVanLoadDOs.size() > 0)
				return vecVanLoadDOs.size();
			return 0;
		}
		
		private View view;
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
				synchronized (SYNC_EDIT) {

					if(view!=null && listScrollState == 0)
					{
						VanLoadDO vanLoadDO = (VanLoadDO) view.getTag();
						if(vanLoadDO != null)
						{
							if(load_type == AppConstants.UNLOAD_STOCK)
							{
								if(((String)view.getTag(R.string.key_field_type)).equalsIgnoreCase("UNIT")){
									vanLoadDO.quantityLevel1 = StringUtils.getInt(s.toString());
									if(!isInventoryAvail(vanLoadDO)){
										vanLoadDO.quantityLevel1=0;
										vanLoadDO.SellableQuantity = 0;
										showToast("Entered quantity should not be greater than available quantity.");
										((CustomEditText)view).setText("");	
									}
									
								}
								else{
									vanLoadDO.quantityLevel3 = StringUtils.getInt(s.toString());
									if(!isInventoryAvail(vanLoadDO)){
										vanLoadDO.quantityLevel3=0;
										vanLoadDO.SellableQuantity = 0;
										showToast("Entered quantity should not be greater than available quantity.");
										((CustomEditText)view).setText("");	
									}
								}
								
							}
							else{
								if(((String)view.getTag(R.string.key_field_type)).equalsIgnoreCase("UNIT"))
									vanLoadDO.quantityLevel1 = StringUtils.getInt(s.toString());
								else
									vanLoadDO.quantityLevel3 = StringUtils.getInt(s.toString());
//								vecOrdProduct.add(vanLoadDO);
							}
						}
						LogUtils.debug("quantityLevel1", ""+vanLoadDO.quantityLevel1);
						LogUtils.debug("quantityLevel3", ""+vanLoadDO.quantityLevel3);
					}
					
				
				}
			}
		}
		

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void refreshList(ArrayList<VanLoadDO> vecVanLoadDOs) 
		{
			view = null;
			this.vecVanLoadDOs = vecVanLoadDOs;
			if(vecVanLoadDOs!=null && vecVanLoadDOs.size()>0){
				lvItems.setVisibility(View.VISIBLE);
				tvNoItemFoundBase.setVisibility(View.GONE);
			}else{
				lvItems.setVisibility(View.GONE);
				tvNoItemFoundBase.setVisibility(View.VISIBLE);
			}
			notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ViewHolder viewHolder;
			final VanLoadDO productDO = vecVanLoadDOs.get(position);
			if(convertView == null)
			{
				convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.add_load_request_cell,null);
			
				viewHolder					 	=  new ViewHolder();
				
				viewHolder.tvProductKey			= (TextView)convertView.findViewById(R.id.tvProductKey);
				viewHolder.tvVendorName			= (TextView)convertView.findViewById(R.id.tvVendorName);
				viewHolder.tvVanQty				= (TextView)convertView.findViewById(R.id.tvVanQty);
				
				viewHolder.tvExpiryDate			= (TextView)convertView.findViewById(R.id.tvExpiryDate);
				viewHolder.tvItemBatchCodeText	= (TextView)convertView.findViewById(R.id.tvItemBatchCodeText);
				viewHolder.tvItemBatchCode		= (TextView)convertView.findViewById(R.id.tvItemBatchCode);
				viewHolder.tvExpiryDateText		= (TextView)convertView.findViewById(R.id.tvExpiryDateText);
				viewHolder.tvVanRecomended		= (TextView)convertView.findViewById(R.id.tvVanRecomended);
				viewHolder.ivTotal				= (ImageView)convertView.findViewById(R.id.ivTotal);
				viewHolder.ivShippedQty			= (ImageView)convertView.findViewById(R.id.ivShippedQty);
				viewHolder.ivTotalUN			= (ImageView)convertView.findViewById(R.id.ivTotalUN);
				
				viewHolder.ivAcceptCheckItems11 = (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems11);
				viewHolder.sep11				= (ImageView)convertView.findViewById(R.id.sep11);
				
				viewHolder.evUOM				= (EditText)convertView.findViewById(R.id.etUOM);
				viewHolder.etQt					= (CustomEditText)convertView.findViewById(R.id.etQt);
				viewHolder.etQtInCs					= (CustomEditText)convertView.findViewById(R.id.etQtInCs);
				viewHolder.etQtView				= (EditText)convertView.findViewById(R.id.etQtView);
				viewHolder.etTotalQt		    = (EditText)convertView.findViewById(R.id.etTotalQt);
				viewHolder.etShippedQty		    = (EditText)convertView.findViewById(R.id.etShippedQty);
				viewHolder.etTotalQtUn			= (EditText)convertView.findViewById(R.id.etTotalQtUn);
				viewHolder.ivAcceptCheckItems   = (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
				
				viewHolder.etQtView.setClickable(false);
				viewHolder.etQtView.setEnabled(false);
				
				if(isEditable)
				{
					viewHolder.etQt.setClickable(true);
					viewHolder.etQt.setEnabled(true);
					viewHolder.etQtInCs.setClickable(true);
					viewHolder.etQtInCs.setEnabled(true);
				}
				else
				{
					viewHolder.etQt.setClickable(false);
					viewHolder.etQt.setEnabled(false);
					viewHolder.etQtInCs.setClickable(false);
					viewHolder.etQtInCs.setEnabled(false);
				}
				
				if(isUnload)
				{
					viewHolder.etQt.setClickable(false);
					viewHolder.etQt.setEnabled(false);
					
					viewHolder.etQt.setVisibility(View.GONE);
					viewHolder.etQtView.setVisibility(View.VISIBLE);
				}
				
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder	= (ViewHolder) convertView.getTag();
			}
			
			if(productDO.HighlightItem != null && productDO.HighlightItem.toLowerCase().equalsIgnoreCase(AppConstants.HighlightItem))
				convertView.setBackgroundColor(getResources().getColor(R.color.yellow_bg));
			else
				convertView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			if(TextUtils.isEmpty(productDO.BatchCode)){
				viewHolder.tvItemBatchCodeText.setVisibility(View.GONE);
				viewHolder.tvItemBatchCode.setVisibility(View.GONE);
			}else{
				viewHolder.tvItemBatchCode.setText(productDO.BatchCode);
			}
			if(TextUtils.isEmpty(productDO.ExpiryDate)){
				viewHolder.tvExpiryDate.setVisibility(View.GONE);
				viewHolder.tvExpiryDateText.setVisibility(View.GONE);
			}else{
				viewHolder.tvExpiryDate.setText(productDO.BatchCode);
			}
			
			viewHolder.etQt.setTag(R.string.key_field_type,"PCS");
			viewHolder.etQtInCs.setTag(R.string.key_field_type,"UNIT");
			
			viewHolder.etQtInCs.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						view  = v;
						viewHolder.etQtInCs.addTextChangedListener(new TextChangeListener());
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								hideKeyBoard(viewHolder.etQtInCs);
								onKeyboardFocus(viewHolder.etQtInCs,0,false);
							}
						}, 10);
					}
				}
			});
			
			if(vecOrdProduct.size() > 1 && productDO.ItemCode.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).ItemCode))
				viewHolder.etQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			
			viewHolder.ivAcceptCheckItems.setVisibility(View.GONE);
			
			viewHolder.tvProductKey.setText(productDO.ItemCode);
			viewHolder.tvProductKey.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvVendorName.setText(productDO.Description);
			viewHolder.tvVendorName.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvVanRecomended.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.etQt.setTag(productDO);
			viewHolder.etQtInCs.setTag(productDO);
			viewHolder.evUOM.setVisibility(View.GONE);
//			productDO.etQty = viewHolder.etQt;
			
			viewHolder.etTotalQt.setText((int)(productDO.inProccessQty)+"");
			viewHolder.etShippedQty.setText((int)(productDO.ShippedQuantity)+"");
			
			if(loadRequestDO != null && (loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED) || loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY)))
			{
				viewHolder.ivTotal.setVisibility(View.VISIBLE);
				viewHolder.etTotalQt.setVisibility(View.VISIBLE);
				if(loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY)){
					viewHolder.etShippedQty.setVisibility(View.VISIBLE);
					viewHolder.ivShippedQty.setVisibility(View.VISIBLE);
				}
			}
			else
				viewHolder.etTotalQt.setVisibility(View.GONE);
			
			if(load_type == AppConstants.LOAD_STOCK){
				if(productDO != null && productDO.eaConversion > 0)
					viewHolder.tvVanQty.setText(""+((int)productDO.inventoryQty/(int)productDO.eaConversion));
				else
					viewHolder.tvVanQty.setText(""+productDO.inventoryQty);
				
				viewHolder.tvVanQty.setVisibility(View.VISIBLE);
			}else{
				viewHolder.tvVanQty.setVisibility(View.GONE);
			}
			if(loadRequestDO == null && load_type == AppConstants.UNLOAD_STOCK)
			{
				if(productDO != null && productDO.eaConversion > 0)
					viewHolder.etTotalQtUn.setText((int)productDO.TotalQuantityToUnload/(int)productDO.eaConversion+"");
				else
					viewHolder.etTotalQtUn.setText(productDO.TotalQuantityToUnload+"");
				viewHolder.etTotalQtUn.setVisibility(View.VISIBLE);
				viewHolder.ivTotalUN.setVisibility(View.VISIBLE);
			}
			else
			{
				viewHolder.etTotalQtUn.setVisibility(View.GONE);
				viewHolder.ivTotalUN.setVisibility(View.GONE);
			}
			
			viewHolder.ivAcceptCheckItems11.setTag(productDO);
			viewHolder.ivAcceptCheckItems11.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					VanLoadDO vanLoadDO = (VanLoadDO) v.getTag();
					if(vanLoadDO.itemChecked)
					{
						vanLoadDO.itemChecked = false;
						((ImageView)v).setImageResource(R.drawable.uncheckbox);
						
						ivCheckAllItems1.setTag("unchecked");
						ivCheckAllItems1.setImageResource(R.drawable.uncheckbox_white);
					}
					else
					{
						vanLoadDO.itemChecked = true;
						((ImageView)v).setImageResource(R.drawable.checkbox);
						isAllChecked();
					}
				}
			});
			
			viewHolder.etTotalQt.setFocusableInTouchMode(false);
			viewHolder.etTotalQt.setEnabled(false);
			
			viewHolder.etShippedQty.setFocusableInTouchMode(false);
			viewHolder.etShippedQty.setEnabled(false);
			
			viewHolder.etTotalQtUn.setFocusableInTouchMode(false);
			viewHolder.etTotalQtUn.setEnabled(false);
		
			if(productDO.quantityLevel3 > 0)
			{
				viewHolder.etQt.setText(""+productDO.quantityLevel3);
				viewHolder.etQtView.setText(""+productDO.quantityLevel3);
			}
			else
			{
				viewHolder.etQt.setText("");
				viewHolder.etQtView.setText("");
			}
			if(productDO.quantityLevel1 > 0)
				viewHolder.etQtInCs.setText(""+productDO.quantityLevel1);
			else
				viewHolder.etQtInCs.setText("");
			
			if(productDO.RecomendedLoadQuantity != null)
				viewHolder.tvVanRecomended.setText(productDO.RecomendedLoadQuantity);
			else
				viewHolder.tvVanRecomended.setText("");
			
			viewHolder.etQt.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						view  = v;
						viewHolder.etQt.addTextChangedListener(new TextChangeListener());
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								hideKeyBoard(viewHolder.etQt);
								onKeyboardFocus(viewHolder.etQt,0,false);
								
							}
						}, 100);
					}

				}
			});
			return convertView;
		}
		
		public ArrayList<VanLoadDO> getModifiedData()
		{
			return vecVanLoadDOs;
		}
		
	}
	
	private static class ViewHolder
	{
		public CustomEditText etQtInCs;
		TextView tvProductKey, tvVendorName, tvExpiryDate, tvItemBatchCodeText, 
		tvItemBatchCode,tvExpiryDateText,tvVanQty,tvVanRecomended;
		ImageView ivTotal, ivTotalUN,ivShippedQty;
		
		ImageView ivAcceptCheckItems11, sep11;
		EditText evUOM, etTotalQt, etTotalQtUn, etQtView,etShippedQty;
		CustomEditText etQt;
		ImageView ivAcceptCheckItems;
	}
	 private void showCategories(final View v, 
			 Vector<CategoryDO> vacCategoryDOs,
			 String title,
			 final int type,
			 final ImageView ivSelectAll,
			 final ListView lvItems,
			 final TextView tvNoItemFound) 
	 {
		 CustomBuilder customBuilder = new CustomBuilder(AddNewLoadRequest.this, title, false);
		 customBuilder.setSingleChoiceItems(vacCategoryDOs, v.getTag(), new CustomBuilder.OnClickListener()
		 {
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				v.setTag((CategoryDO)selectedObject);
				((TextView)v).setText(((CategoryDO)selectedObject).categoryName);
				
				etSeacrhField.setText("");
				builder.dismiss();
				switch (type) {
				case 1:
					if(selectedCategory!=null && !selectedCategory.equalsIgnoreCase(((CategoryDO)selectedObject).categoryId)){
						selectedSubCategory=null;
						tvSubCategory.setText("All Subcategory");
					}
					selectedCategory=((CategoryDO)selectedObject).categoryId;
					if(selectedCategory.equalsIgnoreCase("ALL"))
						selectedCategory=null;
				
					break;
				case 2:
					selectedSubCategory=((CategoryDO)selectedObject).categoryId;
					if(selectedSubCategory.equalsIgnoreCase("ALL"))
						selectedSubCategory=null;
					break;
				default:
					break;
				}
				Vector<VanLoadDO> vecTemp = getSearchItems(null);
				if(vecTemp!=null && vecTemp.size() >0 && adapter!= null)
				{
					adapter.refresh(vecTemp,ivSelectAll);
					ivSelectAll.setVisibility(View.VISIBLE);
					tvNoItemFound.setVisibility(View.GONE);
					lvItems.setVisibility(View.VISIBLE);
				}
				else
				{
					ivSelectAll.setVisibility(View.GONE);
					tvNoItemFound.setVisibility(View.VISIBLE);
					lvItems.setVisibility(View.GONE);
				}
			}
		});
		 customBuilder.show();
     }
}

