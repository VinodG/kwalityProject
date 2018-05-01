package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.AddItemVanAdapter;
import com.winit.alseer.salesman.common.Add_new_SKU_Dialog;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CategoriesDA;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.TransferUserDA;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
public class TransferLoadActivity extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader, llTopLayout;
	private Button btnOrdersheetVerify,btnOrdersheetAddNew;
	private InventoryItemAdapter inventoryItemAdapter;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView tvItemCode, tvDescription, tvUOM, tvQty, tvTotalQt, tvNoItemFoundBase;
	private ImageView ivCheckAllItems;
	private Vector<String> vecCategory;
	private Vector<VanLoadDO> vecSearchedItemd;
	private Paint mPaint;
	private AddItemVanAdapter adapter;
	private ScrollView svLoadStock;
	private int load_type = -1;
	private LoadRequestDO loadRequestDO;
	private HashMap<String, HHInventryQTDO> hmInventory;
	private boolean isEditable = true;
	
	private boolean isSalable = false, isUnload = false;
	
	private EditText etSearch;
	
	private ArrayList<VanLoadDO> vecVanLoadTemp;
	private TextView tvWareHouse;
	private RelativeLayout rlWareHouse;
	private Vector<WareHouseDetailDO> vecTransferUserDO;
	private WareHouseDetailDO objTransferUserDO;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.add_stock_inventory_new, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		Bundle bundle  = getIntent().getExtras();
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
		}
		
		//function for getting id's and setting type-faces 
		intialiseControls();
		
		svLoadStock.setVisibility(View.GONE);
		tvNoItemFoundBase.setVisibility(View.VISIBLE);
		
		if(load_type == AppConstants.UNLOAD_STOCK)
			tvNoItemFoundBase.setText("Please tap on Add Item button to add items for unload request.");
		else
			tvNoItemFoundBase.setText("Please tap on Add Item button to add items for load request.");
		
		if(isUnload)
		{
			tvNoItemFoundBase.setText("No items to display.");
		}
	
		inventoryItemAdapter = new InventoryItemAdapter(new ArrayList<VanLoadDO>());
		//showing Loader
		showLoader(getResources().getString(R.string.loading));
		
		if(isUnload && loadRequestDO == null)
		{
			loadUnLaodData();
		}
		else
		{
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					hmInventory = new OrderDetailsDA().getAvailInventoryQtys();
					if(loadRequestDO != null)
						vecOrdProduct	=	new InventoryDA().getAllItemToVerifyByLoadId(loadRequestDO.MovementCode);

					vecTransferUserDO = new TransferUserDA().getAllUser(preference.getStringFromPreference(preference.USER_ID, ""));
					if(AppConstants.vecCategories == null || AppConstants.vecCategories.size() <= 0)
						AppConstants.vecCategories = new CategoriesDA().getAllCategory();
					
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							if(vecOrdProduct != null && vecOrdProduct.size() > 0)
							{
								inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
								svLoadStock.setVisibility(View.VISIBLE);
								tvNoItemFoundBase.setVisibility(View.GONE);
							}
							else
							{
								showUserSelector();
//								showAddNewSkuPopUp();
							}
							hideLoader();
						}
					});
				}
			}).start();
		}
		btnOrdersheetVerify.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Refresh"))
					loadRequestStatus();
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Finish"))
					finish();
				else if(vecOrdProduct != null && vecOrdProduct.size() > 0)
				{
					if(validateItems(vecOrdProduct))
						insertUpdateRequest();
					else
						showCustomDialog(TransferLoadActivity.this, getString(R.string.warning), "Please select atleat one item having quantity greater than zero.", "OK", null, "");
				}
				else
				{
					if(load_type == AppConstants.LOAD_STOCK)
						showCustomDialog(TransferLoadActivity.this, getString(R.string.warning), "Please select atleast one item for load request.", "OK", null, "");
					else
						showCustomDialog(TransferLoadActivity.this, getString(R.string.warning), "Please select atleast one item for unload request.", "OK", null, "");
				}
			}
		});
		
		
//		btnAdd.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				adapter = null;
//				showAddNewSkuPopUp();
//			}
//		});
		
		if(vecOrdProduct == null)
			vecOrdProduct = new ArrayList<VanLoadDO>();
		
		setTypeFaceRobotoNormal(llOrderSheet);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
//		btnMenu.setVisibility(View.INVISIBLE);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
			}
		});
		
		vecVanLoadTemp = new ArrayList<VanLoadDO>();
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				vecVanLoadTemp.clear();
				String str = s.toString().toLowerCase();
				for (int i = 0; i < vecOrdProduct.size(); i++) {
					VanLoadDO VanLoadDO = vecOrdProduct.get(i);
					String[] arrStr = vecOrdProduct.get(i).Description.toLowerCase().split(" ");
					for (int k = 0; k < arrStr.length; k++) {
						
						if(arrStr[k].startsWith(str))
						{
							if(!vecVanLoadTemp.contains(VanLoadDO))
								vecVanLoadTemp.add(VanLoadDO);
						}
					}
					if(VanLoadDO.ItemCode.toLowerCase().startsWith(str))
					{
						if(!vecVanLoadTemp.contains(VanLoadDO))
							vecVanLoadTemp.add(VanLoadDO);
					}
				}
				if(str.length() > 0)
					inventoryItemAdapter.refresh(vecVanLoadTemp, llOrderListView);
				else
					inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		rlWareHouse.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showUserSelector();
			}
		});
		
		btnOrdersheetAddNew.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(tvWareHouse.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(TransferLoadActivity.this, getResources().getString(R.string.warning), "Please select a User.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
					showAddNewSkuPopUp();
				}
			}
		});
	}
	
	private void showUserSelector() 
	{
		CustomBuilder builder = new CustomBuilder(TransferLoadActivity.this, "Select User", true);
		builder.setSingleChoiceItems(vecTransferUserDO, null, new CustomBuilder.OnClickListener() 
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject) 
			{
				objTransferUserDO = (WareHouseDetailDO) selectedObject;
				tvWareHouse.setText(objTransferUserDO.WareHouseName);
	    		builder.dismiss();
    			builder.dismiss();
    			showAddNewSkuPopUp();
		    }
		}); 
		builder.show();				
	}
	
	protected void insertUpdateRequest() 
	{
		showLoader("Uploading...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				InventoryDA inventoryDA = new InventoryDA();
				
				String movementId = ""+inventoryDA.getMovementId(preference.getStringFromPreference(Preference.SALESMANCODE, ""),0);
				
				if(movementId != null && !movementId.equals(""))
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
					loadRequestDO.Amount		=	getTotalQty();
					loadRequestDO.ModifiedDate	=	CalendarUtils.getOrderPostDate();
					loadRequestDO.ModifiedTime	=	CalendarUtils.getRetrunTime();
					loadRequestDO.PushedOn		=	loadRequestDO.MovementDate;
					loadRequestDO.ModifiedOn	=	loadRequestDO.MovementDate;
					loadRequestDO.WHCode		=	""+objTransferUserDO.WareHouseCode;
					
					if(isUnload && isSalable)
						loadRequestDO.ProductType	=	"Sellable";
					else if(isUnload && !isSalable)
						loadRequestDO.ProductType	=	"Non Sellable";
					else
						loadRequestDO.ProductType	=	"";
						
					loadRequestDO.vecItems 		= 	getVector(vecOrdProduct, loadRequestDO);
					
					inventoryDA.insertLoadRequest(loadRequestDO, load_type);
					
					if(isUnload)
						new ProductsDA().updateUnlod(isSalable, loadRequestDO.vecItems);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							showCustomDialog(TransferLoadActivity.this, "Successful", "Stock has been submitted successfully.", "OK", null, "finish", false);
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
							showCustomDialog(TransferLoadActivity.this, "Alert !", "Sequence number is not sync properly. Please sync sequence number.", "OK", null, "");
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
		for(VanLoadDO object : vecOrdProduct)
		{
			if(object.SellableQuantity > 0)
			{
				LoadRequestDetailDO loDetailDO = new LoadRequestDetailDO();
				loDetailDO.LineNo			= 	""+count++;
				loDetailDO.MovementCode		=	""+loadRequestDO.MovementCode;
				loDetailDO.ItemCode			=	""+object.ItemCode;
				loDetailDO.OrgCode			=	""+loadRequestDO.OrgCode;
				loDetailDO.ItemDescription	=	""+object.Description;
				loDetailDO.ItemAltDescription=	""+object.Description;
				loDetailDO.MovementStatus	=	"Pending";
				loDetailDO.UOM				=	""+object.UOM;
				loDetailDO.QuantityLevel1	=	(int)object.SellableQuantity;
				loDetailDO.QuantityLevel2	=	(int)object.SellableQuantity;
				loDetailDO.QuantityLevel3	=	(int)object.SellableQuantity;
				loDetailDO.QuantityBU		=	(int)object.SellableQuantity;
				loDetailDO.QuantitySU		=	(int)object.SellableQuantity;
				loDetailDO.NonSellableQty	=	0;
				loDetailDO.CurrencyCode		=	""+preference.getStringFromPreference(Preference.CURRENCY_CODE, "");
				loDetailDO.PriceLevel1		=	0;
				loDetailDO.PriceLevel2		=	0;
				loDetailDO.PriceLevel3		=	0;
				loDetailDO.MovementReasonCode=	"NONE";
				loDetailDO.ExpiryDate		=	""+loadRequestDO.CreatedOn;
				loDetailDO.Note				=	"NONE";
				loDetailDO.AffectedStock	=	""+(int)object.SellableQuantity;
				loDetailDO.Status			=	"Pending";
				loDetailDO.DistributionCode	=	""+loadRequestDO.OrgCode;;
				loDetailDO.CreatedOn		=	""+loadRequestDO.CreatedOn;;
				loDetailDO.ModifiedDate		=	""+loadRequestDO.ModifiedDate;
				loDetailDO.ModifiedTime		=	""+loadRequestDO.ModifiedTime;
				loDetailDO.PushedOn			=	""+loadRequestDO.PushedOn;
				loDetailDO.CancelledQuantity=	(int)object.SellableQuantity;
				loDetailDO.InProcessQuantity=	(int)object.SellableQuantity;
				loDetailDO.ShippedQuantity	=	(int)object.SellableQuantity;
				loDetailDO.ModifiedOn		= 	""+loadRequestDO.ModifiedOn;
				vec.add(loDetailDO);
			}
		}
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
		tvItemCode				=	(TextView)llOrderSheet.findViewById(R.id.tvItemCode);
		
		tvWareHouse				=	(TextView)llOrderSheet.findViewById(R.id.tvWareHouse);
		
		tvDescription			=	(TextView)llOrderSheet.findViewById(R.id.tvDescription);
		tvUOM					=	(TextView)llOrderSheet.findViewById(R.id.tvUOM);
		tvQty					=	(TextView)llOrderSheet.findViewById(R.id.tvQty);
		tvTotalQt				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQt);
		tvNoItemFoundBase		=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		llTopLayout				=	(LinearLayout)llOrderSheet.findViewById(R.id.llTopLayout);
		
		svLoadStock				=	(ScrollView)llOrderSheet.findViewById(R.id.svLoadStock);
		
		etSearch			=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		
		ivCheckAllItems.setVisibility(View.GONE);
		llTopLayout.setVisibility(View.VISIBLE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        tvUOM.setText("UOM");
        
        tvWareHouse.setText("Select User");
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if(loadRequestDO != null)
        {
        	if(loadRequestDO.MovementStatus != null && loadRequestDO.MovementStatus.equalsIgnoreCase("Approved"))
        		btnOrdersheetVerify.setText("Finish");
        	else
        		btnOrdersheetVerify.setText("Refresh");
        }
        else
        	btnOrdersheetVerify.setText("Submit ");
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
		else if(from.equalsIgnoreCase("Alert"))
		{
			finish();
		}
	}
	
	private void showAddNewSkuPopUp()
	{
		final Add_new_SKU_Dialog objAddNewSKUDialog = new Add_new_SKU_Dialog(TransferLoadActivity.this);
		objAddNewSKUDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		showFullScreenDialog(objAddNewSKUDialog);
		
//		TextView tvItemCodeLabel			=	objAddNewSKUDialog.tvItemCodeLabel;
//		TextView tvItem_DescriptionLabel	=	objAddNewSKUDialog.tvItem_DescriptionLabel;
//		TextView tvAdd_New_SKU_Item			=	objAddNewSKUDialog.tvAdd_New_SKU_Item;
		final TextView tvCategory			=	objAddNewSKUDialog.tvCategory;
		final EditText etSearch	 			=	objAddNewSKUDialog.etSearch;
		final ImageView cbList 				=	objAddNewSKUDialog.cbList;
		final ListView lvPopupList		 	=	objAddNewSKUDialog.lvPopupList;
		final LinearLayout llList			=	objAddNewSKUDialog.llList;
		Button btnAdd 						=	objAddNewSKUDialog.btnAdd;
		Button btnCancel 					=	objAddNewSKUDialog.btnCancel;
		final TextView tvNoItemFound		=	objAddNewSKUDialog.tvNoItemFound;
		final ImageView ivSearchCross	=	objAddNewSKUDialog.ivSearchCross;
		
		btnAdd.setText(getString(R.string.Add));
		btnCancel.setText(getString(R.string.cancel));

		llList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbList.performClick();
			}
		});
		lvPopupList.setCacheColorHint(0);
		lvPopupList.setScrollbarFadingEnabled(true);
		lvPopupList.setDividerHeight(0);
		
		etSearch.setHint("Search by item code/ description");
		vecCategory = new Vector<String>();
		
		vecCategory = new CategoriesDA().getAvailableCategory();

		tvCategory.setTag(-1);
		tvCategory.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				tvCategory.setClickable(false);
				tvCategory.setEnabled(false);
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						tvCategory.setClickable(true);
						tvCategory.setEnabled(true);
					}
				}, 500);
				
				CustomBuilder builder = new CustomBuilder(TransferLoadActivity.this, "Select Category", true);
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
						
						getProductBycategories(str,cbList,tvNoItemFound,lvPopupList);
				    }
				}); 
				builder.show();
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
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				adapter.refresh(vecSearchedItemd,cbList);
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
					if(tvCategory.getText().toString().equalsIgnoreCase(""))
					{
						showCustomDialog(TransferLoadActivity.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
					}
					else
					{
						Vector<VanLoadDO> vecTemp = new Vector<VanLoadDO>();
						for(int index = 0; vecSearchedItemd != null && index < vecSearchedItemd.size(); index++)
						{
							VanLoadDO obj = (VanLoadDO) vecSearchedItemd.get(index);
							String strText = ((VanLoadDO)obj).ItemCode;
							String strTextDesc = ((VanLoadDO)obj).Description;
							
							if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strTextDesc.toLowerCase().contains(s.toString().toLowerCase()))
								vecTemp.add(vecSearchedItemd.get(index));
						}
						if(vecTemp!=null && vecTemp.size() >0 && adapter!= null)
						{
							adapter.refresh(vecTemp,cbList);
							tvNoItemFound.setVisibility(View.GONE);
							lvPopupList.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoItemFound.setVisibility(View.VISIBLE);
							lvPopupList.setVisibility(View.GONE);
						}
					}
				}
				else if(adapter!= null)
					adapter.refresh(vecSearchedItemd,cbList);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		btnAdd.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Vector<VanLoadDO> veciItems= new Vector<VanLoadDO>();
				if(adapter != null)
					veciItems = adapter.getSelectedItems();
				
				if(tvCategory.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(TransferLoadActivity.this, getResources().getString(R.string.warning), "Please select category.", getResources().getString(R.string.OK), null, "search");
				}
				else
				{
					if(veciItems != null && veciItems.size() > 0)
					{
						if(vecOrdProduct == null)
							vecOrdProduct = new ArrayList<VanLoadDO>();
						
						vecOrdProduct.addAll(veciItems);
						
						objAddNewSKUDialog.dismiss();
						if(vecOrdProduct != null && vecOrdProduct.size() >0)
						{
							inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
							svLoadStock.setVisibility(View.VISIBLE);
							tvNoItemFoundBase.setVisibility(View.GONE);
						}
					}
					else
					{
						showCustomDialog(TransferLoadActivity.this, "Warning !", "Please select Items.", "OK", null, "");
					}
				}
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
	
	private void getProductBycategories(final String strCategorie, final ImageView cbList, final TextView tvNoItemFound, final ListView lvPopupList)
	{
		if(strCategorie.equalsIgnoreCase(""))
		{
			showCustomDialog(TransferLoadActivity.this, getResources().getString(R.string.warning), "Category field should not be empty.", getResources().getString(R.string.OK), null, "search");
		}
		else
		{
			showLoader(getResources().getString(R.string.loading));
			
			new Thread(new  Runnable() 
			{
				public void run()
				{
					String catgId = "", catgName = strCategorie, orderedItems = "";
					
					for(int i=0; AppConstants.vecCategories != null && i<AppConstants.vecCategories.size(); i++)
					{
						if(catgName.equalsIgnoreCase(AppConstants.vecCategories.get(i).categoryName))
						{
							catgId = AppConstants.vecCategories.get(i).categoryId;
							break;
						}
					}
					
					for(int i=0; i<vecOrdProduct.size(); i++)
					{
						for(VanLoadDO objProductDO : vecOrdProduct)
							orderedItems = orderedItems + "'"+objProductDO.ItemCode+"',";
					}
					
					vecSearchedItemd = new ProductsDA().getProductsVanByCategoryId(catgId, orderedItems, load_type, preference,0);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							if(vecSearchedItemd != null && vecSearchedItemd.size() > 0)
							{
								cbList.setVisibility(View.VISIBLE);
								tvNoItemFound.setVisibility(View.GONE);
								lvPopupList.setAdapter(adapter = new AddItemVanAdapter(vecSearchedItemd,TransferLoadActivity.this));
							}
							else
							{
								lvPopupList.setAdapter(adapter = new AddItemVanAdapter(new Vector<VanLoadDO>(),TransferLoadActivity.this));
								cbList.setVisibility(View.INVISIBLE);
								tvNoItemFound.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}).start();
		}
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
		else if(requestCode == 1000 && AppConstants.objScanResultObject!=null)
		{
			Intent intent = new Intent(TransferLoadActivity.this, ScanItemActivity.class);
			startActivityForResult(intent, 100);
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			VanLoadDO dco = (VanLoadDO) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
				inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
			}
		}
	}
	
	public class InventoryItemAdapter
	{
		ArrayList<VanLoadDO> vecOrdProduct;
		private View view;
		class TextChangeListener implements TextWatcher
		{
			String type = "";
			private TextView tvDiference;
			public TextChangeListener(String type, TextView tvDiference)
			{
				LogUtils.infoLog("TextChangeListener","TextChangeListener");
				this.type = type;
				this.tvDiference = tvDiference;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				VanLoadDO objItem = null;
				if(view != null)
				{
					objItem = (VanLoadDO) view.getTag();
				}
				if(type.equalsIgnoreCase("unit"))
				{
					if(objItem != null)
					{
						objItem.SellableQuantity = StringUtils.getInt(s.toString());
						if(load_type == AppConstants.UNLOAD_STOCK && !isInventoryAvail(objItem))
						{
							objItem.SellableQuantity = 0;
							showToast("Entered quantity should not be greater than available quantity.");
//							if(objItem.etQty != null)
//								objItem.etQty.setText("");
						}
						else
							objItem.SellableQuantity = StringUtils.getInt(s.toString());
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
				{
					view = v;
				}
			}
		}
		
		public InventoryItemAdapter(ArrayList<VanLoadDO> vecOrdProduct)
		{
			this.vecOrdProduct = vecOrdProduct;
		}
	
		public ArrayList<VanLoadDO> getModifiedData()
		{
			return vecOrdProduct;
		}

		public void getChildView(LinearLayout llLayoutMiddle)
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			for(VanLoadDO productDO : vecOrdProduct)
			{
				LinearLayout convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_add_stock,null);
				
				TextView tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
				TextView tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
				EditText evUOM				= (EditText)convertView.findViewById(R.id.etUOM);
				EditText etQt				= (EditText)convertView.findViewById(R.id.etQt);
				EditText etTotalQt			= (EditText)convertView.findViewById(R.id.etTotalQt);
				ImageView ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);
				
				if(productDO.ItemCode.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).ItemCode))
					etQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
				
				ivAcceptCheckItems.setVisibility(View.GONE);
				
				tvProductKey.setText(productDO.ItemCode);
				tvProductKey.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvVendorName.setText(productDO.Description);
				tvVendorName.setTypeface(AppConstants.Roboto_Condensed);
				etQt.setTag(productDO);
				evUOM.setTag(productDO);
				
//				productDO.etQty = etQt;
				
				etTotalQt.setText((productDO.TotalQuantity <= 50 ? 100 : (int)productDO.TotalQuantity)+"");
				etTotalQt.setVisibility(View.VISIBLE);
				etTotalQt.setFocusableInTouchMode(false);
				etTotalQt.setEnabled(false);
				
				if(isEditable);
				else
				{
					etQt.setClickable(false);
					etQt.setEnabled(false);
					etQt.setFocusableInTouchMode(false);
				}
				
				if(isUnload)
				{
					etQt.setClickable(false);
					etQt.setEnabled(false);
					etQt.setFocusableInTouchMode(false);
				}
				
				if(productDO.SellableQuantity > 0)
					etQt.setText(""+(int)productDO.SellableQuantity);
				else
					etQt.setText("");
				
				evUOM.setText(productDO.UOM);
				evUOM.setFocusable(false);
				evUOM.setEnabled(false);
				
				etQt.setOnFocusChangeListener(new FocusChangeListener());
				etQt.addTextChangedListener(new TextChangeListener("unit", etQt));
				
//				convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT, (int)(39 * px)));
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						
					}
				});
				
				etTotalQt.setVisibility(View.GONE);
				llLayoutMiddle.addView(convertView);
			}
		}
		
		//Method to refresh the List View
		private void refresh(ArrayList<VanLoadDO> vecOrdProducts,LinearLayout llLayoutMiddle) 
		{
			if(llLayoutMiddle != null && llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			this.vecOrdProduct = vecOrdProducts;
			if(vecOrdProduct != null && vecOrdProducts.size() > 0)
			{
				tvNoItemFoundBase.setVisibility(View.GONE);
				llLayoutMiddle.setVisibility(View.VISIBLE);
				getChildView(llLayoutMiddle);
			}
			else
			{
				tvNoItemFoundBase.setVisibility(View.VISIBLE);
				llLayoutMiddle.setVisibility(View.GONE);
			}
		}
	}
	
	private float getTotalQty()
	{
		float totalQty = 0.0f;
		
		if(inventoryItemAdapter != null)
		{
			ArrayList<VanLoadDO> vecOrdProduct = inventoryItemAdapter.getModifiedData();
			if(vecOrdProduct != null && vecOrdProduct.size() > 0)
			{
				for(VanLoadDO dco : vecOrdProduct)
					totalQty += dco.SellableQuantity;
			}
		}
		return totalQty;
	}
	
	private void loadRequestStatus()
	{
		if(isNetworkConnectionAvailable(TransferLoadActivity.this))
		{
			showLoader("Refreshing...");
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
					loadAllMovements_Sync("Refreshing data...",empNo);
					loadSplashScreenData(empNo);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							int status = new InventoryDA().getPendingStatus(loadRequestDO.MovementCode);
							if(status == 1)
							{
								if(load_type == AppConstants.LOAD_STOCK)
									showCustomDialog(TransferLoadActivity.this, "Successful", "Request has been approved, Quantity has been added in stock.", "OK", null, "finish");
								else
									showCustomDialog(TransferLoadActivity.this, "Successful", "Request has been approved, Quantity has been deducted from stock.", "OK", null, "finish");
							}
							else if(status == 2)
							{
								if(load_type == AppConstants.LOAD_STOCK)
									showCustomDialog(TransferLoadActivity.this, "Alert!", "Your Load request has been rejected by admin.", "OK", null, "finish");
								else
									showCustomDialog(TransferLoadActivity.this, "Alert!", "Your Unload request has been rejected by admin.", "OK", null, "finish");
								
								if(isUnload)
								{
									new ProductsDA().updateUnlodStatus(vecOrdProduct);
								}
							}
							else
								showCustomDialog(TransferLoadActivity.this, "Warning !", "Request is still pending, Please try after some time.", "OK", null, "");
						}
					});
				}
			}).start();
		}
		else
			showCustomDialog(TransferLoadActivity.this, "Warning !", getString(R.string.no_internet), "OK", null, "");
	}
	
	private boolean isInventoryAvail(VanLoadDO objItem)
	{
		boolean isAvail = false;
		if(hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.ItemCode))
		{
			float availQty = hmInventory.get(objItem.ItemCode).totalQt;
			if(objItem.SellableQuantity> availQty)
			{
				isAvail = false;
			}
			else
			{
				isAvail = true;
			}
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
			if(vanLoadDO.SellableQuantity > 0)
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
								inventoryItemAdapter.refresh(vecOrdProduct, llOrderListView);
								svLoadStock.setVisibility(View.VISIBLE);
								tvNoItemFoundBase.setVisibility(View.GONE);
							}
						}
					}
				});
			}
		}).start();
	}
}
