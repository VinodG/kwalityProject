package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collection;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class VanStockMovementDetail extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet,llOrderListView, llItemHeader, llTopLayout;
	private Button btnOrdersheetVerify;
	private LinearLayout llOrdersheetVerify,llApproved,llCollectedQty;
	private ArrayList<VanLoadDO> vecOrdProduct;
	private TextView  tvOrdersheetHeader, tvNoItemFoundBase;
	private int load_type = -1;
	private LoadRequestDO loadRequestDO;
	private ImageView ivApprovedSep,ivCollectedSep;
	
	private EditText etSearch;
	
	private RelativeLayout rlWareHouse;
	
	private TextView tvTotalQtUN,tvVanQty;
	private ImageView ivSepUN,ivSeprator;
	
	private ListView lvItems;
	private ItemListAdapter ordersheetadapter;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.movementdetail, null);
		llBody.addView(llOrderSheet,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		Bundle bundle  = getIntent().getExtras();
		if(bundle != null)
		{
			load_type	 	= 	bundle.getInt("load_type");
			loadRequestDO	= 	(LoadRequestDO)bundle.get("object");
		}
		intialiseControls();
		tvOrdersheetHeader.setText(tvOrdersheetHeader.getText().toString());
		lvItems.setVisibility(View.GONE);
		tvNoItemFoundBase.setVisibility(View.VISIBLE);
	
		lvItems.setVerticalScrollBarEnabled(false);
		lvItems.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvItems.setCacheColorHint(0);
		lvItems.setFadingEdgeLength(0);
		lvItems.setAdapter(ordersheetadapter = new ItemListAdapter(new ArrayList<VanLoadDO>()));
		loadData();
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
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							loadRequestDO.MovementStatus=	""+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VANSTOCK;
							loadRequestDO.UserLoadType	=	""+AppConstants.UNLOAD_VAN_TRANSFER_STOCK;
							loadRequestDO.Status		=	"N";
							prepareTransferLoadRequest();
							new InventoryDA().updateLoadRequestVantransfer(loadRequestDO, load_type);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									uploadData();
									finish();
								}
							});
						}
					}).start();
				}
				else if(btnOrdersheetVerify.getText().toString().equalsIgnoreCase("Finish"))
				{
					finish();
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
					ordersheetadapter.refreshList(vecOrdProduct);
				}
			}
		});
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
				ArrayList<VanLoadDO> vecVanLoadTemp = searchUnloadItems(s.toString());
				if(ordersheetadapter!=null){
					ordersheetadapter.refreshList(vecVanLoadTemp);
				}
			}
		});
		
		setTypeFaceRobotoNormal(llOrderSheet);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnOrdersheetVerify.setTypeface(Typeface.DEFAULT_BOLD);
	}
	private void prepareTransferLoadRequest() 
	{
		if(vecOrdProduct != null && vecOrdProduct.size() > 0)
		{
			LoadRequestDetailDO objloadDetailDO = null;
			for (int i = 0; i < vecOrdProduct.size();i++) 
			{
				VanLoadDO objvanDo = vecOrdProduct.get(i);
				
				objloadDetailDO = new LoadRequestDetailDO();
				
				objloadDetailDO.ItemCode = objvanDo.ItemCode;
				objloadDetailDO.QuantityLevel1 = objvanDo.quantityLevel1;
				objloadDetailDO.inProcessQuantityLevel1 = objvanDo.inProcessQuantityLevel1;
				
				loadRequestDO.vecItems.add(objloadDetailDO);
			}
		}
	}
	
	private void loadData(){
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
							ordersheetadapter.refreshList(vecOrdProduct);
							lvItems.setVisibility(View.VISIBLE);
							tvNoItemFoundBase.setVisibility(View.GONE);
							hideLoader();
						}
					}
				});
			}
		}).start();
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids add_stock_inventory
		llOrderListView 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llordersheet);
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		llApproved	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llApproved);
		llCollectedQty	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llCollectedQty);
		ivCollectedSep	 		=	(ImageView)llOrderSheet.findViewById(R.id.ivCollectedSep);
		ivApprovedSep	 		=	(ImageView)llOrderSheet.findViewById(R.id.ivApprovedSep);
		rlWareHouse				=	(RelativeLayout) llOrderSheet.findViewById(R.id.rlWareHouse);
		btnOrdersheetVerify		=	(Button)llOrderSheet.findViewById(R.id.btnOrdersheetVerify);
		llOrdersheetVerify		=	(LinearLayout)llOrderSheet.findViewById(R.id.llOrdersheetVerify);
		lvItems					=	(ListView)llOrderSheet.findViewById(R.id.lvItems);
		
		tvOrdersheetHeader		=	(TextView)llOrderSheet.findViewById(R.id.tvOrdersheetHeader);
		tvNoItemFoundBase		=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		
		llTopLayout				=	(LinearLayout)llOrderSheet.findViewById(R.id.llTopLayout);
		
		etSearch			=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		
		llHeader 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llHeader);
		
		tvTotalQtUN		=	(TextView)llOrderSheet.findViewById(R.id.tvTotalQtUN);
		tvVanQty		=	(TextView)llOrderSheet.findViewById(R.id.tvVanQty);
		ivSeprator		=	(ImageView)llOrderSheet.findViewById(R.id.ivSeprator);
		ivSepUN			=	(ImageView)llOrderSheet.findViewById(R.id.ivSepUN);
		
		llTopLayout.setVisibility(View.VISIBLE);
		
		tvOrdersheetHeader.setVisibility(View.VISIBLE);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
        refreshLoad();
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
						
						if(loadRequestDO!=null){
							switch (StringUtils.getInt(loadRequestDO.MovementStatus)) {
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
								llApproved.setVisibility(View.VISIBLE);
								llCollectedQty.setVisibility(View.VISIBLE);
								ivApprovedSep.setVisibility(View.VISIBLE);
								ivCollectedSep.setVisibility(View.VISIBLE);
								break;
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
								llApproved.setVisibility(View.VISIBLE);
								ivApprovedSep.setVisibility(View.VISIBLE);
								break;
							default:
								break;
							}
						}
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
        	if(loadRequestDO.MovementStatus != null && loadRequestDO.MovementStatus.equalsIgnoreCase(""+LoadRequestDO.MOVEMENT_STATUS_PENDING))
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
        	btnOrdersheetVerify.setText("Submit ");
		
		if(loadRequestDO == null)
		{
			tvOrdersheetHeader.setText("Add New Request");
			
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
				&& ordersheetadapter.getCount()> 0) {
			showCustomDialog(VanStockMovementDetail.this, getString(R.string.warning), "Are you sure you don't want to save current data?", getString(R.string.Yes), getString(R.string.No), "GoBack");
		} else{
			super.onBackPressed();
		}
	}
	public void sortCategory()
	{
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
			  return vecOrdProduct;
		  }
		  if(vecOrdProduct != null && searchItem != null)
		  {
			  Collection<VanLoadDO> filteredResult = filter(vecOrdProduct, searchItem);
			  if(filteredResult!=null)
				  return (ArrayList<VanLoadDO>) filteredResult;
		  }
		  
		  return new ArrayList<VanLoadDO>();
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
			Intent intent = new Intent(VanStockMovementDetail.this, ScanItemActivity.class);
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
		if(isNetworkConnectionAvailable(VanStockMovementDetail.this))
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
							switch (status) {
							case LoadRequestDO.MOVEMENT_STATUS_PENDING:
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Pending)");
								showCustomDialog(VanStockMovementDetail.this, "Warning !", "Request is still pending, Please try after some time.", "OK", null, "");
								break;
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
								showCustomDialog(VanStockMovementDetail.this, "Successful", "Request has been approved.", "OK", null, "Verify");
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Approved)");
								break;
							case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Collected)");
								break;	
							case LoadRequestDO.MOVEMENT_STATUS_REJECTED:{
								tvOrdersheetHeader.setText("Movement Code: - "+loadRequestDO.MovementCode+" (Rejected)");
								if(load_type == AppConstants.LOAD_STOCK)
									showCustomDialog(VanStockMovementDetail.this, "Alert!", "Your Load request has been rejected by admin.", "OK", null, "finish");
								else
									showCustomDialog(VanStockMovementDetail.this, "Alert!", "Your Unload request has been rejected by admin.", "OK", null, "finish");
								
								
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
			showCustomDialog(VanStockMovementDetail.this, "Warning !", getString(R.string.no_internet), "OK", null, "");
	}

	
	private class ItemListAdapter extends BaseAdapter
	{
		private View view;
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
				convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.vanstock_movement_detail_cell,null);
				viewHolder					 			=  new ViewHolder();
				viewHolder.tvItemCode					= (TextView)convertView.findViewById(R.id.tvItemCode);
				viewHolder.tvItemDescription			= (TextView)convertView.findViewById(R.id.tvItemDescription);
				viewHolder.tvReqQtyInCs					= (TextView)convertView.findViewById(R.id.tvQtyInCS);
				viewHolder.tvReqQtyInEA					= (TextView)convertView.findViewById(R.id.tvQtyInEA);
				viewHolder.tvAppQtyInEA					= (TextView)convertView.findViewById(R.id.tvApprovedQtyInEA);
				viewHolder.tvCollQtyInCs				= (TextView)convertView.findViewById(R.id.tvCollectedQtyInCS);
				viewHolder.tvCollQtyInEA				= (TextView)convertView.findViewById(R.id.tvCollectedQtyInEA);
				
				viewHolder.llApprovedQty				= (LinearLayout)convertView.findViewById(R.id.llApprovedQty);
				viewHolder.llCollectedQty				= (LinearLayout)convertView.findViewById(R.id.llCollectedQty);
				
				viewHolder.edtAppQtyInCs				= (CustomEditText)convertView.findViewById(R.id.edtApprovedQtyInCS);
				convertView.setTag(viewHolder);
			}
			else
			{
				viewHolder	= (ViewHolder) convertView.getTag();
			}
			 
			viewHolder.tvItemCode.setText(productDO.ItemCode);
			viewHolder.tvItemCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			viewHolder.tvReqQtyInCs.setText(String.valueOf(productDO.quantityLevel1));
			viewHolder.tvReqQtyInEA.setText(String.valueOf(productDO.quantityLevel3));
			if(productDO.inProcessQuantityLevel1 > 0)
				viewHolder.edtAppQtyInCs.setText(String.valueOf(productDO.inProcessQuantityLevel1));
			else
				viewHolder.edtAppQtyInCs.setHint("0");
			viewHolder.tvAppQtyInEA.setText(String.valueOf(productDO.inProcessQuantityLevel3));
			viewHolder.tvCollQtyInCs.setText(String.valueOf(productDO.shippedQuantityLevel1));
			viewHolder.tvCollQtyInEA.setText(String.valueOf(productDO.shippedQuantityLevel3));
			viewHolder.tvItemDescription.setText(productDO.Description);
			viewHolder.tvItemDescription.setTypeface(AppConstants.Roboto_Condensed);

			switch (StringUtils.getInt(loadRequestDO.MovementStatus)) {
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
				viewHolder.llApprovedQty.setVisibility(View.VISIBLE);
				viewHolder.llCollectedQty.setVisibility(View.VISIBLE);
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
				viewHolder.llApprovedQty.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			
			viewHolder.edtAppQtyInCs.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {

					if (hasFocus) {
						
						view  = v;
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								try {
									hideKeyBoard(viewHolder.edtAppQtyInCs);
									onKeyboardFocus(viewHolder.edtAppQtyInCs,0,false);	
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, 10);
					}
				}
			});
			
			viewHolder.edtAppQtyInCs.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
				
				@Override
				public void afterTextChanged(Editable s) 
				{
					productDO.inProcessQuantityLevel1 = StringUtils.getInt(viewHolder.edtAppQtyInCs.getText().toString()); 
				}
			});
			
			return convertView;
		}
	}
	
	private static class ViewHolder
	{
		TextView tvItemCode, tvItemDescription,tvReqQtyInCs,tvReqQtyInEA,tvAppQtyInEA,tvCollQtyInCs,tvCollQtyInEA;
		CustomEditText edtAppQtyInCs;
		LinearLayout llApprovedQty,llCollectedQty;
	}
}

