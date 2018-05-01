package com.winit.sfa.salesman;

import java.util.Calendar;
import java.util.Timer;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winit.alseer.parsers.AllDataSyncParser;
import com.winit.alseer.parsers.GetMasterDataParser;
import com.winit.alseer.parsers.UserLoginParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
import com.winit.alseer.salesman.dataobject.LoginUserInfo;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.listeners.VersionChangeListner;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils.DownloadListner;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncData.SyncProcessListner;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.AlarmReceiver;
import com.winit.kwalitysfa.salesman.R;

public class LoginAcivity extends BaseActivity implements ConnectionExceptionListener,DownloadListner,SyncProcessListner
{
	//declaration of variables 
	private EditText etUserName,etPassword;
	private Button btnLogin;
	private ImageView ivCheck_RememberMe;
	private TextView tvForgotPassword, tvVersion;
	private String strUserName,strPassword;
	private LinearLayout llLogin;
	private ConnectionHelper connectionHelper;
	private LinearLayout ll_rememberme;//SMS sent
	private LinearLayout llLive,llTest;
	
	
	@Override
	public void initialize() 
	{
		//inflate the login layout
		llLogin = (LinearLayout)inflater.inflate(R.layout.login,null); 
		llBody.addView(llLogin,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llHeader.setVisibility(View.GONE);
		lockDrawer("LoginAcivity");
		intialiseControls();
		
		ivCheck_RememberMe.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(preference.getbooleanFromPreference(Preference.REMEMBER_ME, false))
				{
					ivCheck_RememberMe.setImageResource(R.drawable.remeber_me_uncheck_box);
					preference.saveBooleanInPreference(Preference.REMEMBER_ME, false);
					preference.commitPreference();
				}
				else
				{
					ivCheck_RememberMe.setImageResource(R.drawable.remeber_me_box);
					preference.saveBooleanInPreference(Preference.REMEMBER_ME, true);
					preference.commitPreference();
				}
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				
				hideKeyBoard(etPassword);
//				showDownloadProgressBar();
				strUserName		= 	etUserName.getText().toString();
		        strPassword		=   etPassword.getText().toString();
		       
		        if(strUserName.equals("") || strPassword.equals(""))
		        {
		        	if(strUserName.equals("") && strPassword.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_username_password), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strUserName.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_username), getString(R.string.OK), null, "");
		        		etUserName.requestFocus();
		        	}
		        	else if(strPassword.equals(""))
		        	{
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), getString(R.string.enter_password), getString(R.string.OK), null, "");
		        		etPassword.requestFocus();
		        	}
		        }
		        else if(!isNetworkConnectionAvailable(LoginAcivity.this))
				{
		        	new CustomerDA().updateLastJourneyLog(preference.getStringFromPreference(Preference.VISIT_CODE, ""));
		        	String strLastUserName = preference.getStringFromPreference(Preference.USER_ID, "");
		        	String strPassword 	   = preference.getStringFromPreference(Preference.PASSWORD, "");
		        	String date            = preference.getStringFromPreference(Preference.SQLITE_DATE, "");
		        	
		        	if(strLastUserName != null && strPassword != null && !strLastUserName.equalsIgnoreCase("") 
		        	   && !strPassword.equalsIgnoreCase("") && strLastUserName.equalsIgnoreCase(etUserName.getText().toString())
		        	   && strPassword.equals(etPassword.getText().toString())
		        	   && date != null && date.length() > 0)
		        	{
		        		showLoader(getString(R.string.please_wait)); 
		        		 new Thread(new Runnable() 
		        		 {
		 					@Override
		 					public void run() 
		 					{
		 						checkAndInsertForTodayLogReport();
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										hideLoader();
										
										
										/**********For testing purpose************/
//										setforPreseller();
										/*****************************************/
										if(!preference.getbooleanFromPreference(Preference.smssend, false))
										{
										startsmsbroadcast();
										preference.saveBooleanInPreference(Preference.smssend, true);
										preference.commitPreference();
										}
										
											
										if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_AM))
										{
											Intent intent	=	new Intent(LoginAcivity.this, PresellerJourneyPlan.class);
											startActivity(intent);
										}
										else if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
										{
											Intent intent	=	new Intent(LoginAcivity.this, VehicleListPreseller.class);
											startActivity(intent);
										}
										else
										{
											Intent intent	=	new Intent(LoginAcivity.this, VehicleList.class);
											startActivity(intent);
										}
									}
								});
		 					}
		 				}).start();
		        	}
		        	else
		        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Internet connection is not available.", getString(R.string.OK), null, "");
				}
		        else
		        	validateUser(strUserName, strPassword);
			}
		});
		
		tvForgotPassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
		
		if(preference.getbooleanFromPreference(Preference.REMEMBER_ME, false))
		{
			etUserName.setText(preference.getStringFromPreference(Preference.USER_ID, ""));
			etPassword.setText(preference.getStringFromPreference(Preference.PASSWORD, ""));
			ivCheck_RememberMe.setImageResource(R.drawable.remeber_me_box);
		}
		
		ll_rememberme.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ivCheck_RememberMe.performClick();
			}
		});
		
		setTypeFaceRobotoNormal(llLogin);
	}
	
	/** initializing all the Controls  of Login class **/
	public void intialiseControls()
	{
		// all the fields from the login.xml is taken here Record
		etUserName					=	(EditText)llLogin.findViewById(R.id.etUserName);
		etPassword					=	(EditText)llLogin.findViewById(R.id.etPassword);
		ivCheck_RememberMe			=	(ImageView)llLogin.findViewById(R.id.ivCheck_rememberMe);
		btnLogin					=	(Button)llLogin.findViewById(R.id.btnLogin);
		tvForgotPassword    		=   (TextView)llLogin.findViewById(R.id.tvForgotPassword);
		tvVersion		    		=   (TextView)llLogin.findViewById(R.id.tvVersion);
		ll_rememberme				=   (LinearLayout)llLogin.findViewById(R.id.ll_rememberme);
		
		llLive						=   (LinearLayout)llLogin.findViewById(R.id.llLive);
		llTest						=   (LinearLayout)llLogin.findViewById(R.id.llTest);
		etUserName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etPassword.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvForgotPassword.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvVersion.setText(""+getVersionName());
		
		if(ServiceURLs.IMAGE_GLOBAL_URL.contains("208.109.154.54") || ServiceURLs.IMAGE_GLOBAL_URL.contains("http://kwalitypic.dyndns.org") ){
			llLive.setVisibility(View.VISIBLE);
			llTest.setVisibility(View.GONE);
		}
		else{
			llTest.setVisibility(View.VISIBLE);
			llLive.setVisibility(View.GONE);
		}
	}
	LoginUserInfo loginUserInfo=null;
	private boolean isNewAPK = false;
	public void validateUser(final String strUserName, final String strPassword)
	{
		connectionHelper = new ConnectionHelper(LoginAcivity.this);
		showLoader(getString(R.string.Validating));
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				final UserLoginParser userLoginParser 	= new UserLoginParser(LoginAcivity.this);
				String gcmId = preference.getStringFromPreference(Preference.gcmId, "");
		        connectionHelper.sendRequest_Bulk(LoginAcivity.this,BuildXMLRequest.loginRequest(strUserName, strPassword, gcmId), userLoginParser, ServiceURLs.LOGIN_METHOD);
		        
		        runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						loginUserInfo = userLoginParser.getLoggedInUserInfo();//new UserInfoDA().getValidateUser(strUserName,strPassword);
						if(loginUserInfo!= null)
						{
							//Getting the LoggedIn User information
							if(!loginUserInfo.strStatus.equalsIgnoreCase("Failure") )
							{
						    	LogUtils.debug("VERSION_CONTROL", "network");
						    	
						    	String oldEmpNo = preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
								String newEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
								LogUtils.errorLog("oldUserId", oldEmpNo+"newEmpNo "+newEmpNo);
									
								if(!oldEmpNo.equalsIgnoreCase("") && oldEmpNo.equalsIgnoreCase(newEmpNo))
								{
									if(!preference.getStringFromPreference(ServiceURLs.GET_HH_INVENTORY+Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase("") && 
									!preference.getStringFromPreference(ServiceURLs.GET_HH_INVENTORY+Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(CalendarUtils.getOrderPostDate()))
									{
										dayFirstLogin();
									}
									else
									{
										afterSuccessFullLogin();
									}
								}
								else
								{
									dayFirstLogin();
								}
								if(!preference.getbooleanFromPreference(Preference.smssend, false))
								{
								startsmsbroadcast();
								preference.saveBooleanInPreference(Preference.smssend, true);
								preference.commitPreference();
								}
								else
									preference.saveBooleanInPreference(Preference.smssend, false);
							}
				        	else
				        	{
				        		hideLoader();
				        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), loginUserInfo.strMessage, getString(R.string.OK), null, "");
				        	}
						}
						else
						{
							hideLoader();
			        		showCustomDialog(LoginAcivity.this, getString(R.string.warning), "Unable to login, please try again.", getString(R.string.OK), null, "");
						}
					}
				});
			}
		}).start();
	}
	
	private void dayFirstLogin()
	{
		new Thread(new Runnable() 
    	{
			@Override
			public void run() 
			{
				callCheckVersionWebService(new VersionChangeListner() {
					
					@Override
					public void onVersionChanged(int status) {
						if(status == AppConstants.VER_CHANGED)
						{
							LogUtils.debug("VERSION_CONTROL", "VER_CHANGED");
							isNewAPK = true;
						}
						else if(status == AppConstants.VER_UNABLE_TO_UPGRADE)
						{
							LogUtils.debug("VERSION_CONTROL", "VER_UNABLE_TO_UPGRADE");
							showCustomDialog(LoginAcivity.this, "Warning !", "Unable to upgrade, please try again.", " OK ", "", "relogin", false);
						}
						else if(status == AppConstants.VER_NOT_CHANGED)
						{
							LogUtils.debug("VERSION_CONTROL", "VER_NOT_CHANGED");
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									afterSuccessFullLogin();
								}
							});
						}
					}
				}, AppConstants.CALL_FROM_LOGIN);
			}
		}).start();
	}
	
	private void afterSuccessFullLogin()
	{
		new Thread(new Runnable()
		{
			boolean isIncrementalSync=false;
			@Override
			public void run() 
			{
				preference.saveStringInPreference(Preference.USER_ID, strUserName);
				preference.saveStringInPreference(Preference.PASSWORD, strPassword);
				preference.removeFromPreference("lastservedcustomer");
				String oldEmpNo = preference.getStringFromPreference(Preference.TEMP_EMP_NO,"");
				String newEmpNo = preference.getStringFromPreference(Preference.EMP_NO,"");
				LogUtils.errorLog("oldUserId", oldEmpNo+"newEmpNo "+newEmpNo);
					
				final int isexpired = new CommonDA().isBuildExpired(preference.getStringFromPreference(Preference.BUILD_INSTALLATIONDATE, ""));
				if(isexpired==0){
					if(!oldEmpNo.equalsIgnoreCase("") && oldEmpNo.equalsIgnoreCase(newEmpNo))
					{
						isIncrementalSync=true;
						//loadIncrementalData(loginUserInfo.strEmpNo);
						syncPromotions();
					}
					else
					{
						clearSynchTime(oldEmpNo);
						boolean isDownloaded = loadMasterData(loginUserInfo.strEmpNo, "Loading master data file...", loginUserInfo);
						if(!isDownloaded)
						{
							hideLoader();
							onComplete();
							showCustomDialog(LoginAcivity.this, "Warning !", "Error occurred while downloading master data file. Please press 'OK' to try again.", " OK ", "", "relogin", false);
							return;
						}
					}
//						new CustomerDA().updateLastJourneyLog(preference.getStringFromPreference(Preference.VISIT_CODE, ""));
						new CustomerDA().updateLastJourneyLog();
					new UserInfoDA().insertLoggedUserInfo(loginUserInfo);
					drawTextToBitmap(getString(R.string.Address_Information));
					checkAndInsertForTodayLogReport();
					preference.saveBooleanInPreference(Preference.smssend, false);
					preference.commitPreference();
					
					if(!preference.getbooleanFromPreference(Preference.smssend, false))
					{
					startsmsbroadcast();
					preference.saveBooleanInPreference(Preference.smssend, true);
					preference.commitPreference();
					}
			
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(isIncrementalSync){
							syncData(LoginAcivity.this);
						}else{
							moveToNextPage(loginUserInfo,isexpired);
						}
					}
				});
			}
		}).start();
	}
	
	private void moveToNextPage(LoginUserInfo loginUserInfo,int isexpired){
		preference.saveStringInPreference(Preference.LAST_SYNC_TIME, CalendarUtils.getCurrentDateAsString());
		preference.saveBooleanInPreference(Preference.IS_DATA_SYNCED_FOR_USER+loginUserInfo.strSalesmanCode, true);
		preference.commitPreference();
		
		if(preference.getbooleanFromPreference("isRememberChecked", false))
			preference.saveStringInPreference(Preference.PASSWORD, strPassword);
		else
			preference.saveStringInPreference(Preference.PASSWORD, "");
		
		preference.saveStringInPreference(Preference.EMP_NO, loginUserInfo.strEmpNo);
		preference.saveStringInPreference(Preference.SALESMANCODE, loginUserInfo.strSalesmanCode);
		preference.saveStringInPreference(Preference.USER_NAME, loginUserInfo.strUserName);
		preference.saveStringInPreference(Preference.REGION, loginUserInfo.strREGION);
    	preference.saveStringInPreference(Preference.USER_TYPE,loginUserInfo.strRole);
		preference.saveStringInPreference(Preference.PASSWORD, strPassword);
		preference.saveStringInPreference(Preference.USER_ID, loginUserInfo.strUserId);
		
		preference.saveStringInPreference(ServiceURLs.GET_HH_INVENTORY+Preference.LAST_JOURNEY_DATE, CalendarUtils.getOrderPostDate());
		
		preference.commitPreference();
		hideLoader();
		scheduleBackgroundtask();
		startsmsbroadcast();
		
		
		onComplete();
		/**********For testing purpose************/
//		setforPreseller();
		/*****************************************/
		if(isexpired==0){
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_AM))
			{
				Intent intent	=	new Intent(LoginAcivity.this, PresellerJourneyPlan.class);
				startActivity(intent);
			}
			else if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				Intent intent	=	new Intent(LoginAcivity.this, VehicleListPreseller.class);
				startActivity(intent);
			}
			else
			{
				Intent intent	=	new Intent(LoginAcivity.this, VehicleList.class);
				startActivity(intent);
			}
		}else{
			finish();
		}
	}
	
//	public void startsmsbroadcast()
//	{
//		Log.d("kwality","SMS CALLED");
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY,9);
//        calendar.set(Calendar.MINUTE,59);
//        Intent myIntent = new Intent(LoginAcivity.this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(LoginAcivity.this, 0, myIntent, 0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//	}
	
	private boolean loadMasterData(String strEmpNo, String mgs, LoginUserInfo loginUserInfo)
	{
		try 
		{
			showLoader("Initializing...");
//			String url="cc";
			GetMasterDataParser getMasterDataParser 	= new GetMasterDataParser(LoginAcivity.this);
			connectionHelper.sendRequest_Bulk(LoginAcivity.this,BuildXMLRequest.getMasterDate(strEmpNo), getMasterDataParser, ServiceURLs.GetMasterDataFile);
//
			String url = getMasterDataParser.getMasterDataURL();
//			if(url !=null && TextUtils.isEmpty(url))
//			  url = "https://kwalitysfa1.winitsoftware.com/Data/SqliteDb/DXB1/SalesMan636458457781369083.sqlite";
//			String url = "http://dev4.winitsoftware.com/Bisleri/data/SalesMan635938050410073120.zip";//test purpose http://208.109.154.54/KwalityPublised/Data/SqliteDb/DDMMT1/SalesMan636189647112202111.sqlite
			
			
			hideLoader();
			
			if(url != null && url.length() >= 0)
			{
				String mainURL 	= 	ServiceURLs.IMAGE_GLOBAL_URL;
				url 			= 	String.format(url, mainURL);
			}
			
			showDownloadProgressBar();
			
			if(!downloadSQLITE(url,LoginAcivity.this))
			{
//				clearSynchTime(strEmpNo);
//				loadIncrementalData(loginUserInfo);
				return false;
			}
			else
			{
				preference.saveStringInPreference(Preference.TEMP_EMP_NO, preference.getStringFromPreference(Preference.EMP_NO, ""));
				preference.saveStringInPreference(Preference.SQLITE_DATE, CalendarUtils.getOrderPostDate());
				preference.commitPreference();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return true;
	}

	private void temploadIncrementalData(LoginUserInfo loginUserInfo)
	{
		showLoader("Loading Data...");
		AllDataSyncParser allDataSyncParser = new AllDataSyncParser(LoginAcivity.this, loginUserInfo.strEmpNo);
		
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
		connectionHelper.sendRequest(LoginAcivity.this,BuildXMLRequest.getAllDataSync(loginUserInfo.strEmpNo, StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetAllDataSync);
		connectionHelper.sendRequestForSurveyModule(LoginAcivity.this, BuildXMLRequest.getSurveyListByUserIdBySync(loginUserInfo.strEmpNo, StringUtils.getInt(lsd), StringUtils.getInt(lst)), ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC);
		
	}
	private void drawTextToBitmap(String gText)
	{
	   try 
	   {
//		  LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.address, null);
//		  TextView tvTextView = (TextView) layout.findViewById(R.id.tvText);
//		  tvTextView.setDrawingCacheEnabled(true);
//		  tvTextView.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_AUTO);
//		  tvTextView.setText(gText);
//		  tvTextView.buildDrawingCache(true);
//		  
//		  final Bitmap bitmap =  Bitmap.createScaledBitmap(tvTextView.getDrawingCache(true), 380, 130, false);
//		  BitmapConvertor convertor = new BitmapConvertor(LoginAcivity.this, new OnMonochromeCreated() 
//		  {
//			@Override
//			public void onCompleted(String path)
//			{
//				if(bitmap != null && !bitmap.isRecycled())
//					bitmap.recycle();
//			}
//		 });
//	      convertor.convertBitmap(bitmap, "address");
	   }
	   catch (Exception e)
	   {
		   e.printStackTrace();
	   }
	}
	
	private TextView tvProgress;
	private ProgressBar progressBar;
	private Timer timer;
	private Dialog dialogDownload;
	
	@SuppressLint("NewApi")
	private void showDownloadProgressBar()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				View v = inflater.inflate(R.layout.progressdialog, null);
				
				progressBar  = (ProgressBar) v.findViewById(R.id.prgbar);
				tvProgress = (TextView) v.findViewById(R.id.tvprogress);
				
				if(dialogDownload==null)
				{
					dialogDownload = new Dialog(LoginAcivity.this);
					dialogDownload.setTitle("Downloading master data file...");
//					dialogDownload.setIndeterminate(false);
//					dialogDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//					dialogDownload.setProgressDrawable(getResources().getDrawable(R.drawable.colorprogress));
					dialogDownload.setCancelable(false);
				}
				int w = (int) (preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 600) * (2f/3f));
				dialogDownload.setContentView(v,new LayoutParams(w,LayoutParams.WRAP_CONTENT));
//				dialogDownload.getWindow().setLayout(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 600)-160,LayoutParams.WRAP_CONTENT);
				dialogDownload.getWindow().setGravity(Gravity.CENTER);
				
				progressBar.setMax(100);
				progressBar.setProgress(0);
				tvProgress.setText("0 %");
				dialogDownload.show();
			}
		});
		
	}
	
//	private void showDownloadProgressBar()
//	{
//		runOnUiThread(new Runnable() 
//		{
//			
//			@Override
//			public void run() 
//			{
//				View v = inflater.inflate(R.layout.progressdialog, null);
//				if(downloadDialog==null)
//				{
//					downloadDialog = new ProgressDialog(LoginAcivity.this);
//					downloadDialog.setTitle("Downloading master data file...");
//					downloadDialog.setIndeterminate(false);
//					downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//					downloadDialog.setMax(100);
//					downloadDialog.setProgress(0);
//					downloadDialog.setProgressDrawable(getResources().getDrawable(R.drawable.colorprogress));
//					downloadDialog.setCancelable(false);
//				}
//				downloadDialog.setView(v);
//				downloadDialog.getWindow().setLayout(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
//				downloadDialog.getWindow().setGravity(Gravity.CENTER);
//				progressBar  = (ProgressBar) v.findViewById(R.id.prgbar);
//				tvProgress = (TextView) v.findViewById(R.id.tvprogress);
//				tvProgress.setText("0/100");
//				downloadDialog.show();
//			}
//		});
//		
//	}
	
	/*@SuppressLint("NewApi")
	private void showDownloadProgressBar()
	{
		
		runOnUiThread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
//				View v = inflater.inflate(R.layout.progressdialog, null);
				if(downloadDialog==null)
				{
					downloadDialog = new ProgressDialog(LoginAcivity.this);
					downloadDialog.setTitle("Downloading master data file...");
					downloadDialog.setIndeterminate(false);
					downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					downloadDialog.setMax(100);
					downloadDialog.setProgress(0);
					downloadDialog.setProgressDrawable(getResources().getDrawable(R.drawable.colorprogress));
					downloadDialog.setCancelable(false);
//					downloadDialog.setProgressStyle(andr,okl90oid.R.attr.progressBarStyleSmall); 
				}
//				float twoHundDP = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
//				v.setPadding((int)twoHundDP, 0, (int)twoHundDP, 0);
//				downloadDialog.setView(v);
				downloadDialog.getWindow().setLayout(AppConstants.DEVICE_WIDTH/2, android.view.WindowManager.LayoutParams.WRAP_CONTENT);
				downloadDialog.getWindow().setGravity(Gravity.CENTER);
//				progressBar  = (ProgressBar) v.findViewById(R.id.prgbar);
//				tvProgress = (TextView) v.findViewById(R.id.tvprogress);
//				tvProgress.setText("0/100");
				downloadDialog.show();
			}
		});
		
	}*/

	@Override
	public void onProgrss(final int count) {
		
		runOnUiThread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				if(dialogDownload!=null)
				{
					progressBar.setProgress(count);
					tvProgress.setText(count+" %");
				}
//					dialogDownload.setProgress(count);
			}
		});
		
	}

	@Override
	public void onComplete() {
		
		runOnUiThread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				if(dialogDownload!=null && dialogDownload.isShowing())
					dialogDownload.dismiss();
			}
		});
	}

	@Override
	public void onError() {
		runOnUiThread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				if(dialogDownload!=null && dialogDownload.isShowing())
					dialogDownload.dismiss();
			}
		});
	}
	private void checkAndInsertForTodayLogReport()
	{
		TrxLogHeaders header = new TrxLogHeaders();
		String userCode = preference.getStringFromPreference(Preference.EMP_NO, "");
		if(new TransactionsLogsDA().getTransactionsLogCount(CalendarUtils.getOrderPostDate())>0)
		{
			
		}
		else
		{
			header.TotalActualCalls = 0;
			header.TotalProductiveCalls=0;
			header.TotalCollections=0;
			header.TotalCreditNotes=0;
			header.TotalSales=0;
			header.TotalScheduledCalls=new CustomerDetailsDA().getJourneyPlanCount();
			header.CurrentMonthlySales=0;
			header.TrxDate = CalendarUtils.getOrderPostDate();
			Vector<TrxLogHeaders> vecTrxLogHeaders= new Vector<TrxLogHeaders>();
			vecTrxLogHeaders.add(header);
			new TransactionsLogsDA().insertTrxLogHeaders(vecTrxLogHeaders);
		}
		
	}
//	private void setforPreseller()
//	{
//		preference.saveStringInPreference(Preference.SALESMAN_TYPE, preference.PRESELLER);
//		preference.commitPreference();
//	}

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
		moveToNextPage(loginUserInfo,0);
	}

	@Override
	public void progress(String msg) {
		showLoader(msg);		
	}
}
