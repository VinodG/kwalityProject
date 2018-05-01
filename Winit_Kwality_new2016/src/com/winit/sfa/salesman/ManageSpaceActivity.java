package com.winit.sfa.salesman;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.parsers.ImageUploadParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.FilesStorage;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.common.UploadData;
import com.winit.alseer.salesman.dataaccesslayer.ClearDataDA;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.UploadTransactions;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionProcessListner;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionSatus;
import com.winit.alseer.salesman.utilities.UploadTransactions.Transactions;
import com.winit.alseer.salesman.utilities.ZipUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class ManageSpaceActivity extends BaseActivity implements TransactionProcessListner
{
	//declaration of variables
	private LinearLayout llSettings,llClearData;
	private TextView  tvSettings,  tvUploadData;
	private Button btnSync, btnUploadData;
	private boolean isSyncCompleted=true;
	
	@Override
	public void initialize()
	{
		//inflate the settings layout
		llSettings = (LinearLayout)inflater.inflate(R.layout.managespace, null);
		llBody.addView(llSettings,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		lockDrawer("ManageSpaceActivity");
		tvUserName.setVisibility(View.GONE);
		tvUserType.setVisibility(View.GONE);
		
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
		if(!isSyncCompleted)
			showLoader("Syncing...");
		btnMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}	
		});
		initializeRequiredVariables();
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
		tvSettings				=	(TextView) llSettings.findViewById(R.id.tvSettings);
		tvUploadData			=	(TextView) llSettings.findViewById(R.id.tvUploadData);
		btnSync					=	(Button) llSettings.findViewById(R.id.btnSync);
		btnUploadData			=	(Button) llSettings.findViewById(R.id.btnUploadData);
		llClearData				=	(LinearLayout) llSettings.findViewById(R.id.llClearData);
		btnUploadData.setVisibility(View.VISIBLE);
		
		setTypeFaceRobotoBold(llSettings);
		
		tvSettings.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSync.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	private void bindingControl()
	{
		btnSetting.setEnabled(false);
		btnSetting.setClickable(false);
		tvUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo))
					uploadDatabaseIntoSDCARD();
			}
		});
		
		llClearData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo))
					clearData();
			}
		});
		
		btnSync.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo)){
					if(!isNetworkConnectionAvailable(ManageSpaceActivity.this))
						showCustomDialog(ManageSpaceActivity.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
					else
					{
						showLoader("Syncing...");
						isSyncCompleted = false;
						new Thread(new Runnable() {
							@Override
							public void run() {
								loadIncrementalData(preference.getStringFromPreference(Preference.EMP_NO, ""));
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										isSyncCompleted = true;
										hideLoader();
									}
								});
							}
						}).start();
					}
				}
			}
		});
		
		//Sending date as null to upload all the data.
		btnUploadData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
				if(!TextUtils.isEmpty(empNo)){
					if(!isNetworkConnectionAvailable(ManageSpaceActivity.this))
						showCustomDialog(ManageSpaceActivity.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
					else 
					{
						UploadTransactions.setTransactionProcessListner(ManageSpaceActivity.this);
						UploadData uploadDeliveredOrder = new UploadData(ManageSpaceActivity.this, null, null);
					}
				}
				
			}
		});
		
	}
	private void initializeRequiredVariables(){
		try {
			AppConstants.DATABASE_PATH = getApplication().getFilesDir().toString() + "/";
			AppConstants.Roboto_Condensed        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed.ttf");
			AppConstants.Roboto_Condensed_Bold        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed_bold.ttf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean uploadDB(String dbPath) {
		boolean isError = false;
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(String.format(
					ServiceURLs.UPLOAD_DB, "competitor"));
			File filePath = new File(dbPath);

			if (filePath.exists()) {
				Log.e("uplaod", "called");
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody cbFile = new FileBody(filePath, "image/png");
				mpEntity.addPart("FileName", cbFile);
				httppost.setEntity(mpEntity);
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				is = resEntity.getContent();

			}
			String serverUrl = parseImageUploadResponse(
					ManageSpaceActivity.this, is);
			LogUtils.debug("serverUrl", "serverUrl "+serverUrl);
			if(!TextUtils.isEmpty(serverUrl))
				isError=false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			isError = true;
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
		} finally {
		}
		return isError;
	}
	public static String parseImageUploadResponse(Context context,
			InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ImageUploadParser handler = new ImageUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	protected void clearData() {
		final ClearDataDA clearDataDA=new ClearDataDA();
		try {
			if(isNetworkConnectionAvailable(this)){
				showLoader(getString(R.string.please_wait));
				new Thread(new Runnable() {
					boolean isCanDoClearData=true;
					boolean isClearDataAllowed=false;
					boolean isDataCleared=false;
					boolean isErrorWhilUploading=false;
					String zipFiilePath=null;
					@Override
					public void run() {
						try {
							
							isClearDataAllowed=new ConnectionHelper(ManageSpaceActivity.this).checkClearDataPermission(ManageSpaceActivity.this,BuildXMLRequest.getClearDataPermission(preference.getStringFromPreference(Preference.EMP_NO, "")), null, ServiceURLs.GetClearDataPermission);
							if(isClearDataAllowed){
								FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
								String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
								ArrayList<File> arr=new ArrayList<File>();
								arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME));
								zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
								ZipUtils.zipFiles(arr, new File(zipFiilePath));
								
								if(!TextUtils.isEmpty(zipFiilePath)){
									File databaseFile = new File(zipFiilePath);
									if(databaseFile.exists()){
										isErrorWhilUploading = uploadDB(zipFiilePath);
										databaseFile.delete();
									}
								}
								if(!isErrorWhilUploading){
									isCanDoClearData = clearDataDA.isCanDoClearData();
									if(isCanDoClearData){
										clearApplicationData();
										preference.clearPreferences();
										isDataCleared=true;
									}else{
										
									}	
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								hideLoader();
								if(!isClearDataAllowed){
									showCustomDialog(ManageSpaceActivity.this, getString(R.string.alert), "You don't have access permissions to use clear data option.", "OK", null, "");
								}else{
									if(isDataCleared){
										Toast.makeText(ManageSpaceActivity.this, "Data got cleared please do re-login to use the app.", Toast.LENGTH_LONG).show();
										Intent intentBrObj = new Intent();
										intentBrObj.setAction(AppConstants.ACTION_LOGOUT);
										sendBroadcast(intentBrObj);
										finish();
										
									}else if(!isCanDoClearData){
										Toast.makeText(ManageSpaceActivity.this, "Please try after sometime.", Toast.LENGTH_LONG).show();
										UploadTransactions.setTransactionProcessListner(ManageSpaceActivity.this);
										UploadData uploadDeliveredOrder = new UploadData(ManageSpaceActivity.this, null, null);
									}else{
										Toast.makeText(ManageSpaceActivity.this, "Error while clearing the data, please check your internet connection and try again.", Toast.LENGTH_LONG).show();
									}
								}
								
							}
						});
					}
				}).start();
			}else{
				showCustomDialog(ManageSpaceActivity.this, getString(R.string.alert), getString(R.string.no_internet), "OK", null, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				if (!s.equals("lib")) {
					deleteDir(new File(appDir, s));
					Log.i("TAG", "File /data/data/APP_PACKAGE/" + s
							+ " DELETED");
				}
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
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

	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("Datacleared")){
			finish();
			System.exit(0);
		}
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
}
