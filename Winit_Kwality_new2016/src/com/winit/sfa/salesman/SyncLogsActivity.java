package com.winit.sfa.salesman;

import java.util.Vector;

import android.R.color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.CustomerHistoryParser;
import com.winit.alseer.parsers.CustomerPendingInvoiceParser;
import com.winit.alseer.parsers.GenerateOfflineDataParser;
import com.winit.alseer.parsers.GetAllDeletedCustomers;
import com.winit.alseer.parsers.GetAllDeletedItems;
import com.winit.alseer.parsers.GetCustomerGroupByUserIdParser;
import com.winit.alseer.parsers.GetPricingParser;
import com.winit.alseer.parsers.GetVehiclesParser;
import com.winit.alseer.parsers.PresellerPassCodeParser;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class SyncLogsActivity extends BaseActivity
{
	private LinearLayout llMain;
	private TextView tvTitleSync, tvServiceName, tvSyncTime, tvResultOfSearch;
	private ListView lvSyncLog;
	private SyncLogAdapter customeListAdapter;
	private Vector<NameIDDo> vecSyncLog;
	private ConnectionHelper connectionHelper;
	
	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.sync_log_layout, null);
		llBody.addView(llMain,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		connectionHelper = new ConnectionHelper(SyncLogsActivity.this);
		
		intialiseControls();
		setTypeFaceRobotoNormal(llMain);
		showListdata();
	}
	
	public void showListdata()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				loadSyncLogData();
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(customeListAdapter != null && vecSyncLog != null && vecSyncLog.size() > 0)
							customeListAdapter.refresh(vecSyncLog);
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void loadSyncLogData()
	{
		vecSyncLog  = new Vector<NameIDDo>();
		
		NameIDDo nameIDDo = new NameIDDo();
		nameIDDo.strId    = "Deleted Customers";
		nameIDDo.strName  = ServiceURLs.GET_HH_DELETED_CUSTOMERS;
		nameIDDo.strType  = ""+preference.getStringFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		  = new NameIDDo();
		nameIDDo.strId    = "Deleted Items";
		nameIDDo.strName  = ServiceURLs.GET_ALL_DELETED_ITEMS;
		nameIDDo.strType  = ""+preference.getStringFromPreference(ServiceURLs.GET_ALL_DELETED_ITEMS+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Item Price";
		nameIDDo.strName = ServiceURLs.GET_All_PRICE_WITH_SYNC;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_All_PRICE_WITH_SYNC+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Master Data";
		nameIDDo.strName = ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Household Master Data";
		nameIDDo.strName = ServiceURLs.GetHouseHoldMastersWithSync;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Customer Group";
		nameIDDo.strName = ServiceURLs.GET_CUSTOMER_GROUP;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_GROUP+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Customer Sites";
		nameIDDo.strName = ServiceURLs.GET_CUSTOMER_SITE;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_SITE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Pending Invoices";
		nameIDDo.strName = ServiceURLs.GET_PENDING_SALES_INVOICE;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
		
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Customer History";
		nameIDDo.strName = ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Landmarks";
		nameIDDo.strName = ServiceURLs.GetSalesmanLandmarkWithSync;
		nameIDDo.strType = ""+preference.getStringFromPreference(Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Sequence Numbers";
		nameIDDo.strName = ServiceURLs.GET_SEQUENCE_NO;
		nameIDDo.strType = ""+preference.getStringFromPreference(Preference.OFFLINE_DATE,"");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Vehicles";
		nameIDDo.strName = ServiceURLs.GET_ALL_VEHICLES;
		nameIDDo.strType = ""+preference.getStringFromPreference(ServiceURLs.GET_ALL_VEHICLES+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "");
		vecSyncLog.add(nameIDDo);
			
		nameIDDo 		 = new NameIDDo();
		nameIDDo.strId    = "Passcodes";
		nameIDDo.strName = ServiceURLs.GET_ALL_PASS_CODE;
		nameIDDo.strType = ""+preference.getStringFromPreference(Preference.PASSCODE_SYNC, "");
		vecSyncLog.add(nameIDDo);
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvTitleSync			=	(TextView)llMain.findViewById(R.id.tvTitleSync);
		tvServiceName		=	(TextView)llMain.findViewById(R.id.tvServiceName);
		tvSyncTime			=	(TextView)llMain.findViewById(R.id.tvSyncTime);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		lvSyncLog			=	(ListView)llMain.findViewById(R.id.lvSyncLog);
		
		
		lvSyncLog.setCacheColorHint(0);
		lvSyncLog.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSyncLog.setSelector(color.transparent);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		lvSyncLog.setVerticalScrollBarEnabled(false);
		lvSyncLog.setAdapter(customeListAdapter = new SyncLogAdapter(new Vector<NameIDDo>()));
		
		/*tvTitleSync.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSyncTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvServiceName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvResultOfSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public class SyncLogAdapter extends BaseAdapter
	{
		private Vector<NameIDDo> vecSyncLog;
		public SyncLogAdapter(Vector<NameIDDo> vecSyncLog) 
		{
			this.vecSyncLog = vecSyncLog;
		}
		@Override
		public int getCount() 
		{
			return vecSyncLog.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecSyncLog.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			NameIDDo nameIDDo = vecSyncLog.get(position);
			if(convertView == null)
				convertView = inflater.inflate(R.layout.sync_log_cell, null);
			
			TextView tvServiceName  = (TextView)convertView.findViewById(R.id.tvServiceName);
			TextView tvSyncTime 	= (TextView)convertView.findViewById(R.id.tvSyncTime);
			Button btnSync			= (Button)convertView.findViewById(R.id.btnSync);
			
//			tvServiceName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//			tvSyncTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			
			tvServiceName.setText(nameIDDo.strId);
			if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SEQUENCE_NO) || nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetSalesmanLandmarkWithSync))
			{
				if(nameIDDo.strType.contains(CalendarUtils.getOrderPostDate()))
					tvSyncTime.setTextColor(getResources().getColor(R.color.gray_dark));
				else
					tvSyncTime.setTextColor(getResources().getColor(R.color.red_dark));
			}
			else if(nameIDDo.strType.contains(CalendarUtils.getSyncDateFormat()))
				tvSyncTime.setTextColor(getResources().getColor(R.color.gray_dark));
			else
				tvSyncTime.setTextColor(getResources().getColor(R.color.red_dark));
			
			tvSyncTime.setText(nameIDDo.strType);
			if(nameIDDo.strType == null || nameIDDo.strType.equalsIgnoreCase(""))
			{
				tvSyncTime.setTextColor(getResources().getColor(R.color.red_dark));
				tvSyncTime.setText("Not Synced");
			}
			
			btnSync.setTag(nameIDDo);
			btnSync.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(isNetworkConnectionAvailable(SyncLogsActivity.this))
					{
						NameIDDo nameIDDo = (NameIDDo) v.getTag();
						syncData(nameIDDo);
					}
					else
						showCustomDialog(SyncLogsActivity.this, getResources().getString(R.string.warning), "Internet connection is not available.", getResources().getString(R.string.OK), null, "");
				}
			});
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)(45 * px)));
			return convertView;
		}
		public void refresh(Vector<NameIDDo> vecSyncLog) 
		{
			this.vecSyncLog = vecSyncLog;
			if(vecSyncLog!=null && vecSyncLog.size()>0)
			{
				lvSyncLog.setVisibility(View.VISIBLE);
				tvResultOfSearch.setVisibility(View.GONE);
				notifyDataSetChanged();
			}
			else
			{
				lvSyncLog.setVisibility(View.GONE);
				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void syncData(final NameIDDo nameIDDo)
	{
		showLoader("Syncing...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startServiceSync(nameIDDo);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						showListdata();
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void startServiceSync(NameIDDo nameIDDo)
	{
		if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_HH_DELETED_CUSTOMERS))
			getAllHHCustomerDeletedItems(preference.getStringFromPreference(Preference.EMP_NO, ""), "Deleting Customers...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_ALL_DELETED_ITEMS))
			deleteAllExpiredItems();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_All_PRICE_WITH_SYNC))
			getAllPriceWithSync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC))
			getSplashScreenDataforSync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetHouseHoldMastersWithSync))
			getHouseholdMastersWithSync();
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_CUSTOMER_GROUP))
			loadCustomerGroup("Loading Customer Group...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_PENDING_SALES_INVOICE))
			loadPendingInvoices("Loading Pending Invoices...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC))
			loadCustomerHistory("Loading Customer History...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GetSalesmanLandmarkWithSync))
			loadLandMarks("Loading Land Marks...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_SEQUENCE_NO))
			loadOfflineData("Loading offline data...", preference.getStringFromPreference(Preference.SALESMANCODE, ""));
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_ALL_VEHICLES))
			loadVehicleList(preference.getStringFromPreference(Preference.SALESMANCODE, ""), "Loading Vehicles...");
		else if(nameIDDo.strName.equalsIgnoreCase(ServiceURLs.GET_ALL_PASS_CODE))
			getVMPasscodes(preference.getStringFromPreference(Preference.SALESMANCODE, ""), "Loading Passcodes...");
	}
	
	private void loadCustomerGroup(String mgs)
	{
		showLoader(mgs);
		GetCustomerGroupByUserIdParser getCustomerGroupByUserIdParser 	= 	new GetCustomerGroupByUserIdParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getCustomerGroupById(preference.getStringFromPreference(Preference.USER_NAME,""), preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_GROUP+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), getCustomerGroupByUserIdParser, ServiceURLs.GET_CUSTOMER_GROUP);
	}
		
	private void loadPendingInvoices(String mgs)
	{
		showLoader(mgs);
		CustomerPendingInvoiceParser customerPendingInvoiceParser = new CustomerPendingInvoiceParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getetCustomersPendingInvoice(preference.getStringFromPreference(Preference.EMP_NO,""), preference.getStringFromPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), customerPendingInvoiceParser, ServiceURLs.GET_PENDING_SALES_INVOICE);
	}
	
	private void loadCustomerHistory(String mgs)
	{
		showLoader(mgs);
		CustomerHistoryParser customerHistoryParser 		= 	new CustomerHistoryParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getCustomerHistoryWithSync(preference.getStringFromPreference(Preference.EMP_NO,""),preference.getStringFromPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), customerHistoryParser, ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC);
	}
	
	private void loadLandMarks(String mgs)
	{
		showLoader(mgs);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getSalesManLandmarkSynce(preference.getStringFromPreference(Preference.SALESMANCODE, ""), preference.getStringFromPreference(Preference.LAST_SYNC_TIME, "")), new LandmarkParser(SyncLogsActivity.this), ServiceURLs.GetSalesmanLandmarkWithSync);
	}
	
	private void loadOfflineData(String mgs, String strSalesmanCode)
	{
		showLoader(mgs);
		GenerateOfflineDataParser getOfflineDataParser = new GenerateOfflineDataParser(SyncLogsActivity.this, strSalesmanCode);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getSequenceNoBySalesmanForHH(strSalesmanCode), getOfflineDataParser, ServiceURLs.GET_SEQUENCE_NO);
	}
	
	private void loadVehicleList(String strSalesmanCode,String mgs)
	{
		showLoader(mgs);
		GetVehiclesParser getVehiclesParser = new GetVehiclesParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getVehicles(strSalesmanCode , preference.getStringFromPreference(ServiceURLs.GET_ALL_VEHICLES+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, "")), getVehiclesParser, ServiceURLs.GET_ALL_VEHICLES);
	}
	private void getVMPasscodes(String strPresellerId,String mgs)
	{
		showLoader(mgs);
		PresellerPassCodeParser parser = new PresellerPassCodeParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllPassCode(strPresellerId), parser , ServiceURLs.GET_ALL_PASS_CODE);
	}
	
	private void getAllPriceWithSync()
	{
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_All_PRICE_WITH_SYNC);
		String lsd = synLogDO.UPMJ;
		String lst = synLogDO.UPMT;
	
		GetPricingParser getPricingParser = new GetPricingParser(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllPriceWithSync(preference.getStringFromPreference(Preference.EMP_NO,""), lsd, lst), getPricingParser, ServiceURLs.GET_All_PRICE_WITH_SYNC);
	}
	private void getSplashScreenDataforSync()
	{
		String UserCode = preference.getStringFromPreference(preference.EMP_NO, "");
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		String lsd = synLogDO.UPMJ;
		String lst = synLogDO.UPMT;
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getSplashScreenDataforSync(UserCode,lsd,lst), new SplashDataSyncParser(SyncLogsActivity.this), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
	}
	private void getHouseholdMastersWithSync()
	{
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getHouseholdMastersWithSync(preference.getStringFromPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, "")), new HouseholdMasterSyncParser(SyncLogsActivity.this), ServiceURLs.GetHouseHoldMastersWithSync);
	}
	
	private void deleteAllExpiredItems()
	{
		//connectionHelper //connectionHelper = new //connectionHelper(SyncLogsActivity.this);
		GetAllDeletedItems deletedItems = new GetAllDeletedItems(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllDeletedItems(preference.getStringFromPreference(ServiceURLs.GET_ALL_DELETED_ITEMS+Preference.LAST_SYNC_TIME, "")), 
//				deletedItems, ServiceURLs.GET_ALL_DELETED_ITEMS);
	}
	
	public void getAllHHCustomerDeletedItems(String strEmpId,String mgs)
	{
		GetAllDeletedCustomers getAllDeletedCustomers = new GetAllDeletedCustomers(SyncLogsActivity.this);
		//connectionHelper.sendRequest_Bulk(BuildXMLRequest.getAllHHCustomerDeletedItems(strEmpId, preference.getStringFromPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, "")), getAllDeletedCustomers, ServiceURLs.GET_HH_DELETED_CUSTOMERS);
	}
}
