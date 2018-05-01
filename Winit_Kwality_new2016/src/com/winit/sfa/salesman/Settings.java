package com.winit.sfa.salesman;


import java.util.ArrayList;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.parsers.AllDataSyncParser;
import com.winit.alseer.parsers.InsertCustomerParser;
import com.winit.alseer.parsers.InsertOrdersParser;
import com.winit.alseer.parsers.InsertPaymentParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.listeners.VersionChangeListner;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionProcessListner;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionSatus;
import com.winit.alseer.salesman.utilities.UploadTransactions.Transactions;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class Settings extends BaseActivity implements TransactionProcessListner,SyncProcessListner
{
	//declaration of variables
	private LinearLayout llSettings;
	private TextView tvChangePassword, tvSettings, tvSplashScreenTheme, tvUploadData, tvUpdateJourneyPlan, 
					 tvServiceSyncTimeLog,tvSettingVersionNo, tvUploadAllData,tvUpgradeApp;
	private EditText etDefaultLoadQty;
	private Button btnSync;
	
	@Override
	public void initialize()
	{
		//inflate the settings layout
		llSettings = (LinearLayout)inflater.inflate(R.layout.settings, null);
		llBody.addView(llSettings,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intialiseControls();
		
		bindingControl();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppConstants.ACTION_GOTO_SETTINGS_FINISH);
		registerReceiver(FinishReceiver, filter);
	}
	BroadcastReceiver FinishReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equalsIgnoreCase(AppConstants.ACTION_GOTO_SETTINGS_FINISH))
				finish();
		}
	};
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		unregisterReceiver(FinishReceiver);
	}
	
	private void intialiseControls() 
	{
		tvChangePassword		=	(TextView) llSettings.findViewById(R.id.tvChangePassword);
		tvSettings				=	(TextView) llSettings.findViewById(R.id.tvSettings);
		tvSplashScreenTheme		=	(TextView) llSettings.findViewById(R.id.tvSplashScreenTheme);
		tvUploadData			=	(TextView) llSettings.findViewById(R.id.tvUploadData);
		tvUpdateJourneyPlan		=	(TextView) llSettings.findViewById(R.id.tvUpdateJourneyPlan);
		tvServiceSyncTimeLog	=	(TextView) llSettings.findViewById(R.id.tvServiceSyncTimeLog);
		tvSettingVersionNo		=	(TextView) llSettings.findViewById(R.id.tvSettingVersionNo);
		etDefaultLoadQty		=	(EditText) llSettings.findViewById(R.id.etDefaultLoadQty);
		btnSync					=	(Button) llSettings.findViewById(R.id.btnSync);
		tvUploadAllData			=	(TextView) llSettings.findViewById(R.id.tvUploadAllData);
		tvUpgradeApp			=	(TextView) llSettings.findViewById(R.id.tvUpgradeApp);
		
		tvSplashScreenTheme.setVisibility(View.GONE);
		tvUpdateJourneyPlan.setVisibility(View.GONE);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		setTypeFaceRobotoNormal(llSettings);
		
		tvSettings.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSync.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	private void bindingControl()
	{
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		etDefaultLoadQty.setText(""+preference.getIntFromPreference(Preference.DefaultLoad_Quantity, 20));
		etDefaultLoadQty.addTextChangedListener(new TextWatcher() 
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
				preference.saveIntInPreference(Preference.DefaultLoad_Quantity,StringUtils.getInt(s.toString()));
				preference.commitPreference();
			}
		});
		tvChangePassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showChangePasswordDialog();
//				Intent intent = new Intent(Settings.this, ChangePasswordActivity.class);
//				startActivity(intent);
			}
		});
		tvSplashScreenTheme.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Settings.this, SplashScreenTheme.class);
				startActivity(intent);
			}
		});
		
		tvUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				uploadDatabaseIntoSDCARD();
			}
		});
		
		tvUploadAllData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
			/*	if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
				else 
				{
					UploadTransactions.setTransactionProcessListner(Settings.this);
					UploadData uploadDeliveredOrder = new UploadData(Settings.this, null, null);
				}*/
				
				if(isNetworkConnectionAvailable(Settings.this))
					uploadAllData();
				else
					showCustomDialog(Settings.this, "Warning!", getResources().getString(R.string.no_internet), "OK", null, "");
			}
		});
		
		tvServiceSyncTimeLog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
				else
				{
					showLoader("Syncing...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
							
							loadAllMovements_Sync("Refreshing data...",empNo);
							loadIncrementalData(empNo);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									hideLoader();
								}
							});
						}
					}).start();
				}
				
				/*Intent intent = new Intent(Settings.this, SyncLogsActivityNew.class);
				startActivity(intent);*/
			
				/*
				showLoader("Syncing...");
				new Thread(new Runnable() {
					@Override
					public void run() {
						temploadIncrementalData();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								hideLoader();
							}
						});
					}
				}).start();
				Intent intent = new Intent(Settings.this, SyncLogsActivityNew.class);
				startActivity(intent);
			*/}
		});
		
		btnSync.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{

				if(!isNetworkConnectionAvailable(Settings.this))
					showCustomDialog(Settings.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
				else
				{
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
							loadAllMovements_Sync("Sync data...",empNo);
							loadVanStock_Sync("Loading Stock...", empNo);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									syncData(Settings.this);
								}
							});
						}
					}).start();
				}
			}
		});
		
		tvUpgradeApp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if(isNetworkConnectionAvailable(Settings.this))
				{
					showLoader("Please wait...");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							callCheckVersionWebService(new VersionChangeListner()
							{
								@Override
								public void onVersionChanged(int status) 
								{
									if(status == AppConstants.VER_DO_EOT_ADEOT)
									{
										showCustomDialog(Settings.this, "Alert!", "You have not done EOT and Advance EOT, please do EOT and Advance EOT and upgrade app.", "OK", null, "");
									}
									else if(status == AppConstants.VER_DO_EOT)
									{
										showCustomDialog(Settings.this, "Alert!", "You have not done EOT, please do EOT and upgrade app.", "OK", null, "");
									}
									else if(status == AppConstants.VER_DO_ADEOT)
									{
										showCustomDialog(Settings.this, "Alert!", "You have not done Advance EOT, please do Advance EOT and upgrade app.", "OK", null, "");
									}
									else if(status == AppConstants.VER_UNABLE_TO_UPGRADE)
									{
										showCustomDialog(Settings.this, "Warning !", getResources().getString(R.string.no_internet), " OK ", "", "relogin", false);
									}
									else if(status == AppConstants.VER_NOT_CHANGED)
									{
										showCustomDialog(Settings.this, "Alert!", "The latest updates have already been installed.", "OK", null, "");
									}
								}
							}, AppConstants.CALL_FROM_SETTINGS);
							hideLoader();
						}
					}).start();
				}
				else
				{
					showCustomDialog(Settings.this, "Alert!", getResources().getString(R.string.no_internet), "OK", "", "");
				}
			}
		});

		tvUpdateJourneyPlan.setVisibility(View.GONE);
	}
	private ConnectionHelper connectionHelper = new ConnectionHelper(Settings.this);;
	private void temploadIncrementalData()
	{
		showLoader("Loading Data...");
		AllDataSyncParser allDataSyncParser = new AllDataSyncParser(Settings.this, preference.getStringFromPreference(Preference.EMP_NO, ""));
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetCommonMasterDataSync);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		else
		{
			lsd = preference.getStringFromPreference(Preference.LSD, lsd);
			lst = preference.getStringFromPreference(Preference.LST, lst);
		}
		connectionHelper.sendRequest(Settings.this,BuildXMLRequest.getAllDataSync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetAllDataSync);
		connectionHelper.sendRequestForSurveyModule(Settings.this, BuildXMLRequest.getSurveyListByUserIdBySync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC);
		
	}
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			finish();
			overridePendingTransition(R.anim.hold, R.anim.push_up_out);
		}
	}
	private String strOldPassword;
	private String strNewPassword;
	private String strConfirmPassword;
	private String strUserName = "";
	private void showChangePasswordDialog()
	{
		preference = new Preference(getApplicationContext());
		View llChangePassword = (LinearLayout)getLayoutInflater().inflate(R.layout.popup_changepassword, null);
		final CustomDialog customDialog = new CustomDialog(Settings.this, llChangePassword,LayoutParams.WRAP_CONTENT/*preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 100*/,
				LayoutParams.WRAP_CONTENT, true);
		try{
			if (!customDialog.isShowing())
				customDialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		final Button btnPassChange,btnCancel;
		final TextView tvDownloadPlanogramHeader,tvConfirmPasswordLabel,tvNewPasswordLabel,tvOldPasswordLabel;
		final EditText et_OldPassword,et_NewPassword,et_ConfirmPassword;
		
		
		strUserName = preference.getStringFromPreference(Preference.USER_NAME, "");
		
		tvDownloadPlanogramHeader	=	(TextView)llChangePassword.findViewById(R.id.tvDownloadPlanogramHeader);
		tvConfirmPasswordLabel		=	(TextView)llChangePassword.findViewById(R.id.tvConfirmPasswordLabel);
		tvOldPasswordLabel			=	(TextView)llChangePassword.findViewById(R.id.tvOldPasswordLabel);
		tvNewPasswordLabel			=	(TextView)llChangePassword.findViewById(R.id.tvNewPasswordLabel);
		et_OldPassword				=	(EditText)llChangePassword.findViewById(R.id.etOldPassword);
		et_NewPassword				=	(EditText)llChangePassword.findViewById(R.id.etNewPassword1);
		et_ConfirmPassword			=	(EditText)llChangePassword.findViewById(R.id.etConfirmPassword);
		btnPassChange 				=	(Button)llChangePassword.findViewById(R.id.btnPassChange);
		btnCancel					=	(Button)llChangePassword.findViewById(R.id.btnCancel);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		
		btnPassChange.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				strOldPassword		=	et_OldPassword.getText().toString();
				strNewPassword		=	et_NewPassword.getText().toString();
				strConfirmPassword	=	et_ConfirmPassword.getText().toString();
				
				if(strOldPassword.equals("")||strNewPassword.equals("")||strConfirmPassword.equals(""))
				{
					showCustomDialog(Settings.this, getResources().getString(R.string.warning), "Please enter all the fields.", getResources().getString(R.string.OK), null,"");
					et_OldPassword.requestFocus();
				}
				else if(!strNewPassword.equals(strConfirmPassword))
				{
					showCustomDialog(Settings.this, getResources().getString(R.string.warning), "Please enter same password.", getResources().getString(R.string.OK), null,"");
					et_ConfirmPassword.requestFocus();
				}
				else
				{
					showLoader(getResources().getString(R.string.loading));
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							final boolean responce = new ConnectionHelper(Settings.this).sendRequest(Settings.this,BuildXMLRequest.changePasswordRequest(preference.getStringFromPreference(Preference.USER_NAME, ""), strOldPassword, strNewPassword), ServiceURLs.CHANGE_PASSWORD);
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									customDialog.dismiss();
									if(responce)
										Toast.makeText(Settings.this, "Password changed successfully.",Toast.LENGTH_SHORT).show();
									else
										Toast.makeText(Settings.this, "Unable to change Password. Please try again later.",Toast.LENGTH_SHORT).show();
								}
							});
						}
					}).start();
				}
			}	
		});
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				customDialog.dismiss();
			}
		});
		
		setTypeFaceRobotoNormal((ViewGroup)llChangePassword);
		tvDownloadPlanogramHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPassChange.setTypeface(Typeface.DEFAULT_BOLD);
		btnCancel.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	private void uploadAllData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				uploadOrders(0);
				uploadPayments();
				uploadCustomer();
				updateUploadedCustomer();
				uploadCustomerVisit();
				uploadSkipReason();
				uploadCustomerspeialDay();

				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
					}
				});
			}
		}).start();
	}
	private boolean uploadCustomerspeialDay()
	{

		try
		{
//			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerDao();
			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerExcess();
			if(vecCustomerDao != null && vecCustomerDao.size() > 0)
			{


				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(this, BuildXMLRequest.updateCustomerDetails(vecCustomerDao), ServiceURLs.UpdateCustomerDetails);
				if(isUploaded)
				{
//					new CustomerDA().updateCustomerDao(vecCustomerDao);
					new CustomerDA().updateTBLCustomer(vecCustomerDao);
				}

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	private void uploadSkipReason()
	{
		try
		{
			ArrayList<PostReasonDO> vecArrayList = new CommonDA().getSkipReasonsToPost();
			if(vecArrayList != null && vecArrayList.size() > 0)
			{
				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.postReasons(vecArrayList), ServiceURLs.InsertSkippingReason))
				{
					new CommonDA().updateSkipReasonNew(vecArrayList,  CalendarUtils.getOrderPostDate() );
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
	}

	private void uploadCustomerVisit()
	{
		try
		{
			Vector<CustomerVisitDO> vecCusotmerVisit = new CustomerDA().getCustomerVisit();
			if(vecCusotmerVisit.size()>0 && new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.getCustomerVisitXML(vecCusotmerVisit), ServiceURLs.PostClientVisits))
			{
				for(CustomerVisitDO journey : vecCusotmerVisit)
					new CustomerDA().updateCustomerVisitUploadStatus(true, journey.CustomerVisitAppId);


			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
	}
	
	private boolean uploadOrders(int count)
	{
		if(count < 3)
		{
			try
			{
				final Vector<TrxHeaderDO> vecSalesOrders  	 = 	new CommonDA().getAllSalesOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
				final InsertOrdersParser insertOrdersParser  = new InsertOrdersParser(this);
				count = count +1;
				if(vecSalesOrders != null && vecSalesOrders.size() > 0)
				{
					for (TrxHeaderDO trxHeaderDO : vecSalesOrders) 
					{
						ConnectionHelper connectionHelper = new ConnectionHelper(null);
						connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postTrxDetailFromXML(trxHeaderDO, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXML);
					}
					
					boolean isOrderPending = new CommonDA().isAllOrderPushed(preference.getStringFromPreference(Preference.EMP_NO, ""));
					if(!isOrderPending)
						count = 3;
				}
				else
					count = 3;
				
				uploadOrders(count);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
        return true;
	}
	
	
	public boolean uploadPayments() {
		boolean isPaymentsUploaded = false;
		Vector<PostPaymentDONew> vecPayments = new Vector<PostPaymentDONew>();
		vecPayments = new CommonDA().getAllPaymentsToPostNew(preference.getStringFromPreference(
						Preference.SALESMANCODE, ""), CalendarUtils
						.getOrderPostDate(), preference
						.getStringFromPreference(Preference.USER_ID, ""));
		ConnectionHelper connectionHelper = new ConnectionHelper(null);
		if (vecPayments != null && vecPayments.size() > 0) {
			InsertPaymentParser insertPaymentParser = new InsertPaymentParser(
					Settings.this);
			connectionHelper.sendRequest_Bulk(Settings.this,
					BuildXMLRequest.postPayments(vecPayments),
					insertPaymentParser, ServiceURLs.PostPaymentDetailsFromXML);
			isPaymentsUploaded = insertPaymentParser.getStatus();

			if (isPaymentsUploaded)
				new CommonDA().updatePaymentStatus(vecPayments);
		}
		return isPaymentsUploaded;
	}
	
	public void updateUploadedCustomer() {
		ArrayList<NameIDDo> arrayList = new CommonDA().getUnpostedCustomerId();
		if (arrayList != null && arrayList.size() > 0)
			new CommonDA().updateCreatedCustomers(arrayList);
	}

	public boolean uploadCustomer() {
		Vector<NewCustomerDO> vector = new CommonDA().getNewCustomerToUpload();
		final InsertCustomerParser insertCustomerParser = new InsertCustomerParser(Settings.this);
		final ConnectionHelper connectionHelper = new ConnectionHelper(null);
		if (vector != null && vector.size() > 0) 
		{
			String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
			connectionHelper.sendRequest_Bulk(Settings.this,BuildXMLRequest.insertHHCustomer(vector, route_code),insertCustomerParser,ServiceURLs.INSERTHH_CUSTOMER_OFFLINE);
		}
		return true;
	}

	@Override
	public void transactionStatus(Transactions transactions, TransactionSatus transactionSatus) 
	{
		if(transactions == Transactions.NONE && transactionSatus == TransactionSatus.START)
			showLoader("Uploading data...");
		else if(transactions == Transactions.NONE && transactionSatus == TransactionSatus.END)
			hideLoader();
	}

	@Override
	public void error(TransactionSatus transactionSatus)
	{
		hideLoader();
	}

	@Override
	public void currentTransaction(Transactions transactions) 
	{
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(preference.getIntFromPreference(Preference.SYNC_STATUS, 0)==1){
					btnSync.setTextColor(Color.WHITE);
				}else{
					btnSync.setTextColor(Color.RED);
					showCustomDialog(Settings.this, getString(R.string.warning), "Data sync failed, please sync again.", getString(R.string.OK), null, null);
				}				
			}
		});
		
	}

	@Override
	public void progress(String msg) {
		showLoader(msg);
	}
}
