package com.winit.alseer.salesman.utilities;

import android.app.IntentService;
import android.content.Intent;

import com.winit.alseer.parsers.AllDataSyncParser;
import com.winit.alseer.parsers.GetAllDeleteLogsParser;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.sfa.salesman.BaseActivity;

public class SyncData extends IntentService{
	private Preference preference;
	private static SyncProcessListner syncProcessListner;
	public SyncData() {
		super("SyncData");
	}
	public static void setListner(SyncProcessListner listner){
		syncProcessListner=listner;
	}
	public static void removeListner(){
		syncProcessListner=null;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		preference 						 = 	new Preference(this);
		if(syncProcessListner!=null)
			syncProcessListner.start();
		preference.saveIntInPreference(Preference.SYNC_STATUS, 0);
		preference.commitPreference();
		syncDeletedLogs();
		loadIncrementalData(preference.getStringFromPreference(Preference.EMP_NO,""));
	}
	public void loadIncrementalData(String empNo)
	{
		try {
			ConnectionHelper connectionHelper = new ConnectionHelper(null);;
			AllDataSyncParser allDataSyncParser = new AllDataSyncParser(this, empNo,syncProcessListner);
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetCommonMasterDataSync);//please don't change this entity
			String lsd = 0+"";
			String lst = 0+"";
			if(synLogDO != null)
			{
				lsd =synLogDO.UPMJ;
				lst = synLogDO.UPMT;
			}
			else
			{
				lsd = preference.getStringFromPreference(Preference.LSD, lsd);
				lst = preference.getStringFromPreference(Preference.LST, lst);
				
			}
//			lsd = "118120";
//			lst = "0";
			
			connectionHelper.sendRequest(this,BuildXMLRequest.getAllDataSync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetAllDataSync);
			new ConnectionHelper(null).sendRequestForSurveyModule( this, BuildXMLRequest.getSurveyListByUserIdBySync(empNo, StringUtils.getInt(lsd), 0), ServiceURLs.GET_SURVEY_LIST_BY_USER_ID_BY_SYNC);//vinod
			if(syncProcessListner!=null)
				syncProcessListner.end();
		} catch (Exception e) {
			e.printStackTrace();
			if(syncProcessListner!=null)
				syncProcessListner.error();
		}
		
	}
	public void syncDeletedLogs()
	{
		try {
			ConnectionHelper connectionHelper= new ConnectionHelper(null);
			GetAllDeleteLogsParser allDeleteLogsParser = new GetAllDeleteLogsParser(this);
			SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetAllDeleteLogs);
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
			connectionHelper.sendRequest(this,BuildXMLRequest.getAllDeleteLogs(lsd, lst), allDeleteLogsParser, ServiceURLs.GetAllDeleteLogs);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	public interface SyncProcessListner{
		public void start();
		public void progress(String msg);
		public void error();
		public void end();
	}
}
