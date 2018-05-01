package com.winit.alseer.salesman.utilities;

import android.app.IntentService;
import android.content.Intent;

import com.winit.alseer.parsers.GetAllMovements;
import com.winit.alseer.parsers.SplashDataSyncParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class SyncMovements extends IntentService 
{
	private Preference preference;
	public SyncMovements() 
	{
		super("SyncMovements");
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		preference 						 = 	new Preference(this);
		loadRequestStatus();
		Intent intentRefreshLoad = new Intent();
		intent.setAction(AppConstants.ACTION_REFRESH_LOAD_REQUEST);
		sendBroadcast(intentRefreshLoad);
	}
	private void loadRequestStatus() {
		if (NetworkUtility.isNetworkConnectionAvailable(this)) {
			String empNo = preference.getStringFromPreference(
					Preference.EMP_NO, "");
			loadAllMovements_Sync("Refreshing data...", empNo);
			// loadVanStock_Sync("Loading Stock...", empNo);

			loadSplashScreenData(empNo);
		} 
	}
	public void loadSplashScreenData(String userCode)
	{
		String lsd = "0", lst = "0";
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		new ConnectionHelper(null).sendRequest_Bulk(this,BuildXMLRequest.getSplashScreenDataforSync(userCode,lsd,  lst), new SplashDataSyncParser(this), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
	}
	public void loadAllMovements_Sync(String mgs, String empNo)
	{
		GetAllMovements getAllMovements = new GetAllMovements(this, empNo);
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAppActiveStatus);
		
		String lsd = "1";
		String lst = "1";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		
		new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.getActiveStatus(empNo, lsd, lst), getAllMovements, ServiceURLs.GetAppActiveStatus);
	}
}
