package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.GetAllMovements;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class TransferINRequestActivity extends BaseActivity
{
	private LinearLayout llLoadRequestScreen;
	private TextView tvCode, tvDate, tvQty, tvOrdersheetHeader, tvNoItemFound;
	private ListView lvLoadRequest;
	private Button btnAdd, btnFinish/*,btnRefresh*/;
	private int load_type;
	private ArrayList<LoadRequestDO> arrayList;
	private LoadViewRequestAdapter loadViewRequestAdapter;
	private boolean isFirst = true;
	private VehicleDO vehicleDO;
	private String date = "";
	private boolean isSalable = false;
	private boolean isUnload = false;
	private boolean isSummary = false;
	
	@Override
	public void initialize()
	{
		llLoadRequestScreen = (LinearLayout) inflater.inflate(R.layout.load_request_screen, null);
		llBody.addView(llLoadRequestScreen, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		isFirst = true;
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
			
			if(getIntent().getExtras().containsKey("isSummary"))
				isSummary = getIntent().getExtras().getBoolean("isSummary");
			
		}
		
		loadViewRequestAdapter = new LoadViewRequestAdapter(arrayList);
		initializeControles();
		
		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		if(isUnload && !isSummary && !new ProductsDA().checkUnload(isSalable))
		{
			Intent intent = new Intent(TransferINRequestActivity.this, TransferLoadActivity.class);
			intent.putExtra("load_type", load_type);
			intent.putExtra("vehicle", vehicleDO);
			intent.putExtra("date", date);
			intent.putExtra("isSalable", isSalable);
			intent.putExtra("isUnload", isUnload);
			startActivity(intent);
		}
		
		btnAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TransferINRequestActivity.this, TransferLoadActivity.class);
				intent.putExtra("load_type", load_type);
				intent.putExtra("vehicle", vehicleDO);
				intent.putExtra("date", date);
				startActivity(intent);
			}
		});
		
//		btnRefresh.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				loadAllMovements_Sync("Loading All Movements...", preference.getStringFromPreference(preference.EMP_NO, ""));
//			}
//		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		setTypeFaceRobotoNormal(llLoadRequestScreen);
//		btnMenu.setVisibility(View.INVISIBLE);
		tvOrdersheetHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
//		btnRefresh.setTypeface(Typeface.DEFAULT_BOLD);
//		btnRefresh.setVisibility(View.VISIBLE);
	}
	
	private void initializeControles()
	{
		tvNoItemFound		= (TextView)llLoadRequestScreen.findViewById(R.id.tvNoItemFound);
		tvCode				= (TextView)llLoadRequestScreen.findViewById(R.id.tvCode);
		tvDate				= (TextView)llLoadRequestScreen.findViewById(R.id.tvDate);
		tvQty				= (TextView)llLoadRequestScreen.findViewById(R.id.tvQty);
		tvOrdersheetHeader	= (TextView)llLoadRequestScreen.findViewById(R.id.tvOrdersheetHeader);
		lvLoadRequest		= (ListView)llLoadRequestScreen.findViewById(R.id.lvLoadRequest);
		
		btnAdd				= (Button)llLoadRequestScreen.findViewById(R.id.btnAdd);
		btnFinish			= (Button)llLoadRequestScreen.findViewById(R.id.btnFinish);
//		btnRefresh			= (Button)llLoadRequestScreen.findViewById(R.id.btnRefresh);
		
		
		lvLoadRequest.setAdapter(loadViewRequestAdapter);
		tvNoItemFound.setVisibility(View.GONE);
		
		if(load_type == AppConstants.UNLOAD_STOCK)
		{
			tvCode.setText("Unload Req. Code");
			tvOrdersheetHeader.setText("Unload View Request");
		}
		else
		{
			tvCode.setText("Load Req. Code");
			tvOrdersheetHeader.setText("Load View Request");
		}
		tvOrdersheetHeader.setText("Transfer Request");
		
	}

	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(isUnload || isSummary)
		{
			btnAdd.setVisibility(View.GONE);
		}
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
			tvDate.setText(CalendarUtils.getFormatedDatefromString(loadRequestDO.MovementDate));
		
			switch (StringUtils.getInt(loadRequestDO.MovementStatus)) {
			case LoadRequestDO.MOVEMENT_STATUS_PENDING:
				tvQty.setText("Pending");
				tvQty.setTextColor(getResources().getColor(R.color.red_dark));
				ivStatus.setBackgroundResource(R.drawable.failure);
				break;
			case LoadRequestDO.MOVEMENT_STATUS_APPROVED:
				tvQty.setText("Approved");
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

			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(TransferINRequestActivity.this, AddNewLoadRequest.class);
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
			return convertView;
		}
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
	
	/**
	 * Method to get the refreshed van load data
	 */
	public void loadAllMovements_Sync(String mgs, String empNo)
	{
		showLoader(mgs);
		GetAllMovements getAllMovements = new GetAllMovements(TransferINRequestActivity.this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);
		
		String lsd = "0";
		String lst = "0";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		new ConnectionHelper(null).sendRequest(TransferINRequestActivity.this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus);
	}
}
