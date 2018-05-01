package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;
public class AddStockInVehicle extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader;
	private Button btnPrint;
//	private LinearLayout llOrdersheetVerify;
	private InventoryItemAdapter inventoryItemAdapter;
	public static ArrayList<VanLoadDO> vecOrdProduct;
	private TextView  tvUOM, tvNoItemFound,
					 tvTotalQt;
	private ImageView ivCheckAllItems,ivDivHeaderPrint;
	private Paint mPaint;
	private VehicleDO vehicleDO;
	private EditText etSearch;
	private ListView listView;
	private ScrollView svLoadStock;
	//header bar
	private TextView tvPageTitle;
	private Button btn;
	private ImageView ivDivHeaderBtn;
	private ArrayList<VanLoadDO> vecVanLoadTemp;
	private boolean isOdometerReadingDone=false;
	
	private ImageView ivSearchCross, ivLoadReq, ivUnLoadReq, ivContinue;
	private TextView tvItemAndDescription,tvRecomended,tvQty,tvPCS;
	
	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llOrderSheet.findViewById(R.id.tvPageTitle);
		btn	= (Button) llOrderSheet.findViewById(R.id.btn);
		ivDivHeaderBtn	= (ImageView) llOrderSheet.findViewById(R.id.ivDivHeaderBtn);
		
		tvPageTitle.setText("Store Check");
		btn.setText("SUBMIT");
		ivDivHeaderBtn.setVisibility(View.VISIBLE);
		btn.setVisibility(View.VISIBLE);
		btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.refresh), null, null, null);
	}
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.add_stock_inventory, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras() != null)
			vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
	
		preference.saveVehicleObjectInPreference(Preference.VEHICLE_DO, vehicleDO);
		preference.commitPreference();
		
		setHeaderBar();
		//function for getting id's and setting type-faces 
		intialiseControls();
		tvPageTitle.setText("Van Stock");
		
		ivLoadReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//				if(isEOTDone())
//				{
//					showCustomDialog(AddStockInVehicle.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
//				}
//				else
//				{
					Intent intent = new Intent(AddStockInVehicle.this, LoadRequestActivity.class);
					intent.putExtra("load_type", AppConstants.LOAD_STOCK);
					intent.putExtra("object", vehicleDO);
					intent.putExtra("isUnload",false);
					startActivity(intent);
//				}
			}
		});
		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vecOrdProduct!=null && vecOrdProduct.size()>0){
					Intent intent = new Intent(AddStockInVehicle.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_INVENTORY);
					startActivity(intent);
				}
			}
		});
		ivUnLoadReq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
				if(isEOTDone())
				{
					showCustomDialog(AddStockInVehicle.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
				}
				else
				{
					Intent intent = new Intent(AddStockInVehicle.this, LoadRequestActivity.class);
					intent.putExtra("load_type", AppConstants.UNLOAD_STOCK);
					intent.putExtra("object", vehicleDO);
					intent.putExtra("isUnload",true);
					startActivity(intent);
				}
			}
		});

		ivContinue.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
//				if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
				if(isEOTDone())
				{
					showCustomDialog(AddStockInVehicle.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")." , getResources().getString(R.string.OK), null, "");
				}
				else
				{
					if(isCheckin())
						finish();
					else
					{
						if(vecOrdProduct != null && vecOrdProduct.size() > 0 && inventoryItemAdapter!=null && inventoryItemAdapter.getModifiedData()!=null && inventoryItemAdapter.getModifiedData().size()>0)
							onButtonYesClick("verify");
						else
							showCustomDialog(AddStockInVehicle.this, getString(R.string.warning), "There are no item in your inventory.", "OK", null, "");
					}
				}
			}
		});
		
		btn.setText("Refresh");
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
						loadAllMovements_Sync("Refreshing data...",empNo);
						loadVanStock_Sync("Loading Stock...", empNo);
						
						loadSplashScreenData(empNo);
						runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								loadData();
							}
						});
					}
				}).start();
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		vecVanLoadTemp = new ArrayList<VanLoadDO>();
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				inventoryItemAdapter.refresh(vecOrdProduct);
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				
				vecVanLoadTemp.clear();
				String str = s.toString().toLowerCase();
				for (int i = 0; vecOrdProduct!=null && i < vecOrdProduct.size(); i++) {
					VanLoadDO VanLoadDO = vecOrdProduct.get(i);
					if(VanLoadDO.ItemCode.toLowerCase().contains(str.trim()) || VanLoadDO.Description.toLowerCase().contains(str.trim()))
					{
						if(!vecVanLoadTemp.contains(VanLoadDO))
							vecVanLoadTemp.add(VanLoadDO);
					}
				}
				if(str.length() > 0)
					inventoryItemAdapter.refresh(vecVanLoadTemp);
				else
					inventoryItemAdapter.refresh(vecOrdProduct);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		setTypeFaceRobotoNormal(llOrderSheet);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		btn.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		 tvItemAndDescription.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvRecomended.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvQty.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvPCS.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		svLoadStock	 		=	(ScrollView)llOrderSheet.findViewById(R.id.svLoadStock);
		svLoadStock.setVisibility(View.GONE);
		tvTotalQt				=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQt);
//		tvTotalQt.setVisibility(View.VISIBLE);
		
		tvUOM					=	(TextView)llOrderSheet.findViewById(R.id.tvUOM);
		
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		ivCheckAllItems			=	(ImageView)llOrderSheet.findViewById(R.id.ivCheckAllItems);
		
		btnPrint				=	(Button)llOrderSheet.findViewById(R.id.btnPrint);
		
		etSearch				=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		ivDivHeaderPrint		=	(ImageView)llOrderSheet.findViewById(R.id.ivDivHeaderPrint);
		
//		llOrdersheetVerify		=	(LinearLayout)llOrderSheet.findViewById(R.id.llOrdersheetVerify);
		
		ivLoadReq				=	(ImageView)llOrderSheet.findViewById(R.id.ivLoadReq);
		ivUnLoadReq				=	(ImageView)llOrderSheet.findViewById(R.id.ivUnLoadReq);
		ivContinue				=	(ImageView)llOrderSheet.findViewById(R.id.ivContinue);
		
//		llAdd.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		ivCheckAllItems.setVisibility(View.GONE);
		btn.setVisibility(View.VISIBLE);
		btnPrint.setVisibility(View.VISIBLE);
		btnPrint.setTypeface(AppConstants.Roboto_Condensed_Bold);
		ivDivHeaderPrint.setVisibility(View.VISIBLE);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        tvUOM.setText("UOM");
        
        btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
		btnMenu.setVisibility(View.VISIBLE);
		listView = 	(ListView)llOrderSheet.findViewById(R.id.lvStockItems);
		listView.setVisibility(View.VISIBLE);
		listView.setCacheColorHint(0);
		listView.setAdapter(inventoryItemAdapter = new InventoryItemAdapter(new ArrayList<VanLoadDO>()) );
		
		 tvItemAndDescription=(TextView) llOrderSheet.findViewById(R.id.tvItemAndDescription);
		 tvRecomended=(TextView) llOrderSheet.findViewById(R.id.tvRecomended);
		 tvQty=(TextView) llOrderSheet.findViewById(R.id.tvQty);
		 tvPCS=(TextView) llOrderSheet.findViewById(R.id.tvPCS);
		 
		 tvItemAndDescription.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvRecomended.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvQty.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 tvPCS.setTypeface(AppConstants.Roboto_Condensed_Bold);
		 
		
		 tvPCS.setVisibility(View.GONE);
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("stock"))
			updateStock();
		else if(from.equalsIgnoreCase("verify"))
		{
			if(!isOdometerReadingDone){
				Intent intent = new Intent(AddStockInVehicle.this, OdometerReadingActivity.class);
				intent.putExtra("isStartDay", true);
				startActivity(intent);
				setResult(1111);
				finish();
			}else{
				Intent intent = new Intent(AddStockInVehicle.this, PresellerJourneyPlan.class);
				intent.putExtra("Latitude", 25.522);
				intent.putExtra("Longitude", 78.522);
				intent.putExtra("mallsDetails", mallsDetailss);
				startActivity(intent);
				finish();
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		loadData();
	}
	
	private void loadData()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() {
			@Override
			public void run() {
				vecOrdProduct	=	new VehicleDA().getAllItemToVerify();
				isOdometerReadingDone = new JourneyPlanDA().isOdometerReadingDoneForTheDay(CalendarUtils.getOrderPostDate());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						inventoryItemAdapter.refresh(vecOrdProduct);
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
		else if(requestCode == 1000 && AppConstants.objScanResultObject!=null)
		{
			Intent intent = new Intent(AddStockInVehicle.this, ScanItemActivity.class);
			startActivityForResult(intent, 100);
		}
		else if(resultCode == 5000 && data!=null && data.getExtras() != null)
		{
			VanLoadDO dco = (VanLoadDO) data.getExtras().get("dco");
			if(dco != null)
			{
				vecOrdProduct.add(dco);
				inventoryItemAdapter.refresh(vecOrdProduct);
				etSearch.setText("");
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	ArrayList<VanLoadDO> vecUpdatedData;
	private void updateStock()
	{
		vecUpdatedData = inventoryItemAdapter.getModifiedData();
		
		if(vecUpdatedData != null && vecUpdatedData.size()> 0)
			updateValues();
		else
			showCustomDialog(AddStockInVehicle.this, getResources().getString(R.string.warning), "Error occured while verifying. Please try again.", getResources().getString(R.string.OK), null, "");
	}
	
	public class InventoryItemAdapter extends BaseAdapter
	{
		ArrayList<VanLoadDO> vecOrdProduct;
		
		public InventoryItemAdapter(ArrayList<VanLoadDO> vecOrdProduct)
		{
			this.vecOrdProduct = vecOrdProduct;
			if(this.vecOrdProduct != null && this.vecOrdProduct.size() > 0)
				sortVectorperDisplayOredr(this.vecOrdProduct);
		}
	
		public ArrayList<VanLoadDO> getModifiedData()
		{
			return vecOrdProduct;
		}

		//Method to refresh the List View
		private void refresh(ArrayList<VanLoadDO> vecOrdProducts) 
		{
			this.vecOrdProduct = vecOrdProducts;
			if(this.vecOrdProduct != null && this.vecOrdProduct.size() > 0)
				sortVectorperDisplayOredr(this.vecOrdProduct);
			notifyDataSetChanged();
		}
		
		private void sortVectorperDisplayOredr(ArrayList<VanLoadDO> vecOrdProduct2) 
		{
			Collections.sort(vecOrdProduct2, new Comparator<VanLoadDO>()
			{
				@Override
				public int compare(VanLoadDO lhs, VanLoadDO rhs) 
				{
					return lhs.ItemCode.equalsIgnoreCase(rhs.ItemCode)?1:0;
				}
			});
		}

		@Override
		public int getCount()
		{
			if(vecOrdProduct != null && vecOrdProduct.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				llOrderListView.setVisibility(View.VISIBLE);
				
				return vecOrdProduct.size();
			}
			else
			{
				tvNoItemFound.setVisibility(View.VISIBLE);
				llOrderListView.setVisibility(View.GONE);
			}
			
			return 0;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			VanLoadDO vanLoadDO			= vecOrdProduct.get(position);
			ViewHolder viewHolder=null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.item_list_add_stock,null);
				viewHolder.tvProductKey		= (TextView)convertView.findViewById(R.id.tvProductKey);
				viewHolder.tvVendorName		= (TextView)convertView.findViewById(R.id.tvVendorName);
				viewHolder.tvRecomendedLoad		= (TextView)convertView.findViewById(R.id.tvRecomendedLoad);
				viewHolder.evUOM				= (TextView)convertView.findViewById(R.id.etUOM);
				viewHolder.etQt				= (EditText)convertView.findViewById(R.id.etQt);
				viewHolder.etUNIT			= (EditText) convertView.findViewById(R.id.etUNIT);
				viewHolder.tvExpiryDate		= (TextView)convertView.findViewById(R.id.tvExpiryDate);
				viewHolder.tvItemBatchCodeText		= (TextView)convertView.findViewById(R.id.tvItemBatchCodeText);
				viewHolder.tvExpiryDateText		= (TextView)convertView.findViewById(R.id.tvExpiryDateText);
				
				viewHolder.etTotalQt			= (EditText)convertView.findViewById(R.id.etTotalQt);
				viewHolder.tvItemBatchCode				= (TextView)convertView.findViewById(R.id.tvItemBatchCode);
				viewHolder.ivAcceptCheckItems= (ImageView)convertView.findViewById(R.id.ivAcceptCheckItems);	
				convertView.setTag(viewHolder);
			}else
				viewHolder = (ViewHolder) convertView.getTag();
			
			viewHolder.etQt.setVisibility(View.GONE);
			
			if(vanLoadDO.HighlightItem != null && vanLoadDO.HighlightItem.toLowerCase().equalsIgnoreCase(AppConstants.HighlightItem))
				convertView.setBackgroundColor(getResources().getColor(R.color.yellow_bg));
			else
				convertView.setBackgroundColor(getResources().getColor(R.color.transparent));
			
			viewHolder.tvItemBatchCode.setVisibility(View.VISIBLE);
			viewHolder.tvExpiryDate.setVisibility(View.VISIBLE);
			
			if(vanLoadDO.ItemCode.equalsIgnoreCase(vecOrdProduct.get(vecOrdProduct.size()-1).ItemCode))
				viewHolder.etQt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			
			viewHolder.ivAcceptCheckItems.setVisibility(View.GONE);
			
			viewHolder.tvProductKey.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvVendorName.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvRecomendedLoad.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvExpiryDate.setTypeface(AppConstants.Roboto_Condensed_Bold);
			viewHolder.tvItemBatchCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			viewHolder.tvProductKey.setText(vanLoadDO.ItemCode);
			String dec=vanLoadDO.Description;
			Log.i("vanLoadDO.Description", dec);
			String[] str=dec.split(" \\(");
			
//			viewHolder.tvVendorName.setText(dec);
			if(str != null && str.length >= 2)
			{
				viewHolder.tvVendorName.setText(str[0]+"\n"+"("+str[1]	);
				Log.i("vanLoadDO.Description", str[0]);
				Log.i("vanLoadDO.Description", str[1]);
			}
			else
				viewHolder.tvVendorName.setText(dec	);
			
			if(vanLoadDO.RecomendedLoadQuantity != null)
				viewHolder.tvRecomendedLoad.setText(vanLoadDO.RecomendedLoadQuantity);
			else
				viewHolder.tvRecomendedLoad.setText("");
			viewHolder.tvItemBatchCode.setVisibility(View.GONE);
			viewHolder.tvExpiryDate.setVisibility(View.GONE);
			viewHolder.tvItemBatchCodeText.setVisibility(View.GONE);
			viewHolder.tvExpiryDateText.setVisibility(View.GONE);
			
			viewHolder.etQt.setTag(vanLoadDO);
			
			viewHolder.etTotalQt.setText(vanLoadDO.TotalQuantity+"");
			viewHolder.etTotalQt.setVisibility(View.GONE);
			viewHolder.etTotalQt.setFocusableInTouchMode(false);
			viewHolder.etTotalQt.setEnabled(false);
			
			if(vanLoadDO.SellableQuantity > 0)
				viewHolder.etQt.setText(""+((int)vanLoadDO.SellableQuantity % (int)vanLoadDO.eaConversion));
			else
				viewHolder.etQt.setText("");
			
			if(vanLoadDO.SellableQuantity > 0)
				viewHolder.etUNIT.setText(""+((int)vanLoadDO.SellableQuantity / (int)vanLoadDO.eaConversion));
			else
				viewHolder.etUNIT.setText("");
			
			viewHolder.etQt.setEnabled(false);
			viewHolder.etQt.setClickable(false);
			viewHolder.etQt.setFocusable(false);
			
			viewHolder.etUNIT.setEnabled(false);
			viewHolder.etUNIT.setClickable(false);
			viewHolder.etUNIT.setFocusable(false);
			
			viewHolder.evUOM.setText(""+vanLoadDO.UOM);
			viewHolder.evUOM.setEnabled(false);
			viewHolder.evUOM.setFocusable(false);
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
				}
			});
			
			return convertView;
		}
	}
	
	private void updateValues()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						Intent intent = new Intent(AddStockInVehicle.this, OdometerReadingActivity.class);
						intent.putExtra("isStartDay", true);
						startActivity(intent);
						setResult(1111);
						finish();
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		finish();
	}
	
	public class ViewHolder{
		TextView tvProductKey		;
		TextView tvVendorName		;
		TextView tvRecomendedLoad	;
		TextView evUOM				;
		EditText etQt				;
		EditText etUNIT				;
		TextView tvExpiryDate		;
		TextView tvItemBatchCodeText		;
		TextView tvExpiryDateText		;
		
		EditText etTotalQt			;
		TextView tvItemBatchCode				;
		ImageView ivAcceptCheckItems;
	}
}
