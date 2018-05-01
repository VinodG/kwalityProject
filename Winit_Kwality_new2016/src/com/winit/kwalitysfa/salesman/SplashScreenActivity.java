package com.winit.kwalitysfa.salesman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.winit.alseer.parsers.VersionCheckingHandler;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.CheckVersionDO;
import com.winit.alseer.salesman.listeners.VersionChangeListner;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.NetworkUtility;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.LoginAcivity;

public class SplashScreenActivity extends BaseActivity  
{
    /** Called when the activity is first created. Order_Table*/
	private LinearLayout llSplash;
	private Preference preference;
	private  CustomDialog upgradeDialog;
	VersionCheckingHandler versionCheckingHandler;
	private String apkFilePath="";
	private boolean isNewAPK = false;
   
	@Override
	public void initialize() 
	{
		
		llSplash = (LinearLayout)inflater.inflate(R.layout.main, null);
		llBody.addView(llSplash,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		llHeader.setVisibility(View.GONE);
		initializeControlls();
		int diffrenceIndays = CalendarUtils.getDiffBtwDatesInDays(CalendarUtils.getOrderPostDate(), "2014-12-12");
		
		LogUtils.debug("diffrenceIndays", ""+diffrenceIndays);
	}
    public void initializeControlls()
    {
		preference	= new Preference(getApplicationContext());
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		preference.saveIntInPreference(Preference.DEVICE_DISPLAY_WIDTH, displaymetrics.widthPixels);
		if(TextUtils.isEmpty(preference.getStringFromPreference(Preference.BUILD_INSTALLATIONDATE, "")))
			preference.saveStringInPreference(Preference.BUILD_INSTALLATIONDATE, CalendarUtils.getOrderPostDate());
		preference.saveIntInPreference(Preference.DEVICE_DISPLAY_HEIGHT,displaymetrics.heightPixels);
		preference.commitPreference();
		
		// setting divice width and height
		AppConstants.DIVICE_WIDTH 		= 	displaymetrics.widthPixels;
		AppConstants.DIVICE_HEIGHT 		= 	displaymetrics.heightPixels;
		AppConstants.CategoryIconsPath 	= 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Kwality/CategoryIcons/";
	    AppConstants.productCatalogPath = 	Environment.getExternalStorageDirectory().getAbsolutePath()+"/Kwality/";
	    AppConstants.KwalityLogoPath 	= 	AppConstants.productCatalogPath+AppConstants.APPMEDIALOGOFOLDERNAME;
	    
	    deleteInsertedInvoice();
	    
	    if(isNetworkConnectionAvailable(SplashScreenActivity.this))
	    {
	    	LogUtils.debug("VERSION_CONTROL", "network");
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
//				else if(status == AppConstants.VER_NO_BUTTON_CLICK)
//				{
//					afterVersionCheck(loginUserInfo);
//				}
							else if(status == AppConstants.VER_UNABLE_TO_UPGRADE)
							{
								LogUtils.debug("VERSION_CONTROL", "VER_UNABLE_TO_UPGRADE");
								showCustomDialog(SplashScreenActivity.this, "Warning !", "Unable to upgrade, please try again.", " OK ", "", "relogin", false);
							}
							else if(status == AppConstants.VER_NOT_CHANGED)
							{
								LogUtils.debug("VERSION_CONTROL", "VER_NOT_CHANGED");
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										moveToNext();
									}
								});
//					boolean isDownloadedQS = loadSqlite(loginUserInfo.strEmpNo, loginUserInfo);
//					preference.saveBooleanInPreference(Preference.IS_SQLITE_DOWNLOADED, isDownloadedQS);
//					preference.commitPreference();
							}
						}
					}, AppConstants.CALL_FROM_LOGIN);
				}
			}).start();
	    }
	    else
	    {
	    	LogUtils.debug("VERSION_CONTROL", "no network");
	    	moveToNext();
	    }
	
    }
    
    private void deleteInsertedInvoice(){
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					new CustomerDetailsDA().deletePendingInvoiceFromPending();	
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void moveToNext()
	{
		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				AppConstants.DATABASE_PATH = getApplication().getFilesDir().toString() + "/";
				preference.saveStringInPreference(preference.DataBasePath, AppConstants.DATABASE_PATH);
				AppConstants.Roboto_Condensed        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed.ttf");
				AppConstants.Roboto_Condensed_Bold        = Typeface.createFromAsset(getApplicationContext().getAssets(), "Roboto_Condensed_bold.ttf");
				copyLogotoSdCard();
				finish();
				/*Intent intent = new Intent(SplashScreenActivity.this,PrinterConnector.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SAMPLE);
				startActivity(intent);*/
				
				Intent intent		=	new Intent(SplashScreenActivity.this, LoginAcivity.class);//CompetitorsListActivity
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
			}

			
		},2500);
		gcmRegister();
		
		//startserviceforsms();
	}
	public void gcmRegister()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {

				/**
				 * If the registration registration id is null, then we can
				 * register as follows
				 */
				if (!GCMRegistrar.isRegistered(SplashScreenActivity.this)) {
					if (NetworkUtility.isNetworkConnectionAvailable(SplashScreenActivity.this)) {
						try {
							/**
							 * To receive GCM push notifications, device must be
							 * at least API Level 8
							 */
							GCMRegistrar.checkDevice(SplashScreenActivity.this);

							/**
							 * Check manifest whether it is having
							 * "permission.C2D_MESSAGE",
							 * "com.google.android.c2dm.permission.SEND",
							 * "com.google.android.c2dm.intent.REGISTRATION",
							 * "com.google.android.c2dm.intent.RECEIVE"
							 * permissions or not. By using manifest tag
							 * "<service android:name=".GCMIntentService" />" we
							 * can start GCM service,
							 * 
							 */
							GCMRegistrar.checkManifest(SplashScreenActivity.this);
							AppConstants.GCMRegistrationAttempts++;
							GCMRegistrar.register(SplashScreenActivity.this,
									AppConstants.SENDER_ID);
						} catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				} else {
				}
			}
		}).start();
	}
	
//	public void startserviceforsms()
//	{
//		Intent in = new Intent(SplashScreenActivity.this, SMSservice.class);
//		startService(in);
//	}
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		GCMRegistrar.onDestroy(this);
	}
	
	//method to copy Masafi logos to sdcard from assests 
	private boolean copyLogotoSdCard()
	{
		if(!(new File(AppConstants.KwalityLogoPath).exists()))
			new File(AppConstants.KwalityLogoPath).mkdirs();
		//Creating the file if not exist
		File file 		= new File(AppConstants.KwalityLogoPath);
		
//		if(!preference.getbooleanFromPreference(Preference.IS_INSTALLED, false))
//		{
			if(file.exists())
			{
				if (file.isDirectory()) 
				{
			        String[] children = file.list();
			        for (int i = 0; i < children.length ; i++) 
			        {
			            new File(file, children[i]).delete();
			        }
			    }
//			}
			preference.saveBooleanInPreference(Preference.IS_INSTALLED, true);
			preference.commitPreference();
		}

		if(!file.exists())
		{
			file.mkdir();
		}
		
		File[] fileslist = file.listFiles();
		if(fileslist != null && fileslist.length > 0)
		{
			for(File tempFile : fileslist)
			{
				if(tempFile.getName().toString().contains(".bmp"))
				return false;
			}
		}
		//creating instance to AssetManager
	    AssetManager assetManager = getAssets();
	    //to getting the all files from sdcard
	    String[] files = null;
	    try 
	    {
	    	//getting the all files from sdcard
	        files = assetManager.list("");
	    }
	    catch (IOException e) 
	    {
	    }
	    //loop up to file count
	    for(String filename : files)
	    {
	    	//creating instance to InputStream and OutputStream
	        InputStream inputStream   = null;
	        OutputStream outputStream = null;
	        try 
	        {
	        	//coping only the files having the extension .bmp
	        	if(filename.contains(".bmp"))
	        	{
		          inputStream 	= assetManager.open(filename);
		          outputStream 	= new FileOutputStream(AppConstants.KwalityLogoPath+ "/"+filename);
		          copyFile(inputStream, outputStream);
		          inputStream.close();
		          inputStream = null;
		          outputStream.flush();
		          outputStream.close();
		          outputStream = null;
	        	}
	        } 
	        catch(Exception e) 
	        {
	        	e.printStackTrace();
	        }  
	    }
		return true;
	}
	/**
	 * method to writing files 
	 * @param InputStream
	 * @param OutputStream
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException 
	{
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1)
	    {
	      out.write(buffer, 0, read);
	    }
	}
	
}