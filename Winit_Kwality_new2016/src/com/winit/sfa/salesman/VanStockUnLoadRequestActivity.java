package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.GetUserLoadRequestsPerDayParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class VanStockUnLoadRequestActivity extends BaseActivity
{
	private LinearLayout llLoadRequestScreen,llAdd,llFinish;
	private TextView tvCode, tvOrdersheetHeader, tvNoItemFound;
	private ListView lvLoadRequest;
	private Button btnRefresh, btnFinish;
	private int load_type;
	private ArrayList<LoadRequestDO> arrayList;
	private LoadViewRequestAdapter loadViewRequestAdapter;
	private VehicleDO vehicleDO;
	private String date = "";
	private boolean isSalable = false;
	private boolean isUnload = false;
	private boolean isVanStockRequest = false;
	BroadcastReceiver refreshList =new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadRequestStatus(false);	
		}
	};
	
	@Override
	public void initialize()
	{
		llLoadRequestScreen = (LinearLayout) inflater.inflate(R.layout.vanstock_load_request_screen, null);
		llBody.addView(llLoadRequestScreen, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(getIntent().getExtras() != null)
		{
			load_type = getIntent().getExtras().getInt("load_type");
			
			if(getIntent().getExtras().get("object")!=null)
				vehicleDO = (VehicleDO) getIntent().getExtras().get("object");
			if(getIntent().getExtras().containsKey("date"))
				date = getIntent().getExtras().getString("date");
			
			if(getIntent().getExtras().containsKey("isSalable"))
				isSalable = getIntent().getExtras().getBoolean("isSalable");
			if(getIntent().getExtras().containsKey("isUnload"))
				isUnload = getIntent().getExtras().getBoolean("isUnload");
			if(getIntent().getExtras().containsKey("isVanStockRequest"))
				isVanStockRequest = getIntent().getExtras().getBoolean("isVanStockRequest");
			
		}
		
		loadViewRequestAdapter = new LoadViewRequestAdapter(arrayList);
		initializeControles();
		llFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				btnFinish.performClick();
			}
		});
		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
//		if(isUnload && !isSummary && !new ProductsDA().checkUnload(isSalable))
//		{
//			Intent intent = new Intent(LoadRequestActivity.this, AddNewLoadRequest.class);
//			intent.putExtra("load_type", load_type);
//			intent.putExtra("vehicle", vehicleDO);
//			intent.putExtra("date", date);
//			intent.putExtra("isSalable", isSalable);
//			intent.putExtra("isUnload", isUnload);
//			startActivity(intent);
//		}
		llAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				btnRefresh.performClick();
			}
		});
		btnRefresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/**Get requested load from other users**/
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						showLoader("Please wait..");
						GetUserLoadRequestsPerDayParser parser = new GetUserLoadRequestsPerDayParser(VanStockUnLoadRequestActivity.this);
						new ConnectionHelper(VanStockUnLoadRequestActivity.this).sendRequest(VanStockUnLoadRequestActivity.this,BuildXMLRequest.getUserLoadRequestsPerDay(preference.getStringFromPreference(preference.EMP_NO, "")), parser, ServiceURLs.GetUserLoadRequestsPerDay);
						
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
//								hideLoader();
								refreshLoadRequest();
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
		
		setTypeFaceRobotoNormal(llLoadRequestScreen);
//		btnMenu.setVisibility(View.INVISIBLE);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnRefresh.setTypeface(Typeface.DEFAULT_BOLD);
		btnFinish.setTypeface(Typeface.DEFAULT_BOLD);
		
//		llMenu.setClickable(false);
//		llMenu.setEnabled(false);
	}
	
	private void initializeControles()
	{
		tvNoItemFound		= (TextView)llLoadRequestScreen.findViewById(R.id.tvNoItemFound);
		tvCode				= (TextView)llLoadRequestScreen.findViewById(R.id.tvCode);
		tvOrdersheetHeader	= (TextView)llLoadRequestScreen.findViewById(R.id.tvOrdersheetHeader);
		lvLoadRequest		= (ListView)llLoadRequestScreen.findViewById(R.id.lvLoadRequest);
		
		btnRefresh				= (Button)llLoadRequestScreen.findViewById(R.id.btnRefresh);
		btnFinish			= (Button)llLoadRequestScreen.findViewById(R.id.btnFinish);
		llAdd				= (LinearLayout)llLoadRequestScreen.findViewById(R.id.llAdd);
		llFinish			= (LinearLayout)llLoadRequestScreen.findViewById(R.id.llFinish);
		lvLoadRequest.setAdapter(loadViewRequestAdapter);
		tvNoItemFound.setVisibility(View.GONE);
		
		tvCode.setText("Van Transfer Unload Req. Code");
		tvOrdersheetHeader.setText("View Van Transfer Unload Request");
//		if(load_type == AppConstants.UNLOAD_STOCK)
//		{
//		}
//		else
//		{
//			tvCode.setText("Load Req. Code");
//			tvOrdersheetHeader.setText("View Load Request");
//		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		refreshLoadRequest();
	}
	private void refreshLoadRequest() 
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AppConstants.ACTION_REFRESH_LOAD_REQUEST);
		registerReceiver(refreshList, intentFilter);

		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				arrayList = new InventoryDA().getAllRequestByType(""+load_type);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(arrayList != null && arrayList.size() >0)
						{
							if(loadViewRequestAdapter != null)
								loadViewRequestAdapter.refreshList(arrayList);
						}
					}
				});
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(refreshList);
	}
	
	private class LoadViewRequestAdapter extends BaseAdapter
	{
		private ArrayList<LoadRequestDO> arrayList;
		public LoadViewRequestAdapter(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
		}

		public void refreshList(ArrayList<LoadRequestDO> arrayList) 
		{
			this.arrayList = arrayList;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount()
		{
			if(arrayList != null && arrayList.size() > 0)
			{
				tvNoItemFound.setVisibility(View.GONE);
				return arrayList.size();
			}
			else
				tvNoItemFound.setVisibility(View.VISIBLE);
			
			return 0;
		}

		@Override
		public Object getItem(int arg0)
		{
			return arg0;
		}
//Stock Verification
		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final LoadRequestDO loadRequestDO = arrayList.get(position);
		
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.item_load_view_stock, null);
			
			TextView tvCode		= (TextView)convertView.findViewById(R.id.tvCode);
			TextView tvDate		= (TextView)convertView.findViewById(R.id.tvDate);
			TextView tvQty		= (TextView)convertView.findViewById(R.id.tvQty);
			ImageView ivStatus		= (ImageView)convertView.findViewById(R.id.ivStatus);
			
			tvCode.setText(loadRequestDO.MovementCode);
			tvDate.setText(CalendarUtils.getFormatedDatefromStringWithTime(loadRequestDO.MovementDate));
			switch (StringUtils.getInt(loadRequestDO.MovementStatus)) {
			case LoadRequestDO.MOVEMENT_STATUS_PENDING:
				tvQty.setText("Pending");
				tvQty.setTextColor(getResources().getColor(R.color.red_dark));
				ivStatus.setBackgroundResource(R.drawable.failure);
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VANSTOCK:
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
				tvQty.setText("Approved");
				tvQty.setTextColor(getResources().getColor(R.color.greenText));
				ivStatus.setBackgroundResource(R.drawable.success);
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY:
				tvQty.setText("Collected");
				tvQty.setTextColor(getResources().getColor(R.color.greenText));
				ivStatus.setBackgroundResource(R.drawable.success);
				break;	
			case LoadRequestDO.MOVEMENT_STATUS_REJECTED:
				tvQty.setText("Rejected");
				tvQty.setTextColor(getResources().getColor(R.color.red_dark));
				ivStatus.setBackgroundResource(R.drawable.failure);
				break;

			default:
				break;
			}
			
			if(LoadRequestDO.MOVEMENT_STATUS_PENDING == StringUtils.getInt(loadRequestDO.MovementStatus))
				convertView.setClickable(true);
			else
				convertView.setClickable(false);
			
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(VanStockUnLoadRequestActivity.this, VanStockMovementDetail.class);
					intent.putExtra("load_type", load_type);
					intent.putExtra("object", loadRequestDO);
					intent.putExtra("isEditable", false);
					intent.putExtra("vehicle", vehicleDO);
					intent.putExtra("isUnload", isUnload);
					intent.putExtra("isSalable", isSalable);
					startActivity(intent);
				}
			});
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			tvCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvDate.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvQty.setTypeface(AppConstants.Roboto_Condensed_Bold);
			return convertView;
		}
	}
	private void loadRequestStatus(final boolean isFetchFromServer) {
		if (isNetworkConnectionAvailable(VanStockUnLoadRequestActivity.this)) {
			showLoader("Refreshing...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(isFetchFromServer){
						String empNo = preference.getStringFromPreference(
								Preference.EMP_NO, "");
	
						loadAllMovements_Sync("Refreshing data...", empNo);
						// loadVanStock_Sync("Loading Stock...", empNo);
	
						loadSplashScreenData(empNo);
					}
					arrayList = new InventoryDA()
							.getAllRequestApprovedByType("" + load_type);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							hideLoader();
							if (arrayList != null && arrayList.size() > 0) {
								if (loadViewRequestAdapter != null)
									loadViewRequestAdapter
											.refreshList(arrayList);
							} else {
								if (loadViewRequestAdapter != null)
									loadViewRequestAdapter
											.refreshList(new ArrayList<LoadRequestDO>());
							}

						}
					});
				}
			}).start();
		} else
			showCustomDialog(VanStockUnLoadRequestActivity.this, "Warning !",
					getString(R.string.no_internet), "OK", null, "");
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
	}
}
