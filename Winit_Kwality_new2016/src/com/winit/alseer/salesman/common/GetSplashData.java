package com.winit.alseer.salesman.common;

import android.content.Context;
import android.os.AsyncTask;

import com.winit.alseer.parsers.SplashDataSyncParser;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetSplashData extends AsyncTask<String, String, String> 
{
	private Preference preference;
	private Context context;
	
	public GetSplashData(Context context)
	{
		this.context					 = 	context;
		preference 						 = 	new Preference(context);
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		getSplashData(context);
		return "";
	}
	
	@Override
	protected void onPostExecute(String result) 
	{
		super.onPostExecute(result);
	}
	
	private void getSplashData(Context context)
	{
		String UserCode = preference.getStringFromPreference(preference.EMP_NO, "");
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
		String lsd = synLogDO.UPMJ;
		String lst = synLogDO.UPMT;
		new ConnectionHelper(null).sendRequest_Bulk(context,BuildXMLRequest.getSplashScreenDataforSync(UserCode,lsd,lst), new SplashDataSyncParser(context), ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC);
	}
}
