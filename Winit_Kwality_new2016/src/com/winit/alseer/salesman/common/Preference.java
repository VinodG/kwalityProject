package com.winit.alseer.salesman.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.winit.alseer.salesman.dataobject.CustomerDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.VehicleDO;

@SuppressLint("CommitPrefEdits") public class Preference 
{
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor edit;
	public static String IS_INSTALLED 					=	"isInstalled";
	
	public static String IS_UNLOADSTOCK 				=	"IS_UNLOADSTOCK";
	public static String SYNC_STATUS 					=	"SYNCSTATUS";
	public static String DEVICE_DISPLAY_WIDTH 			=	"DEVICE_DISPLAY_WIDTH";
	public static String DEVICE_DISPLAY_HEIGHT 			=	"DEVICE_DISPLAY_HEIGHT";
	
	public static String USER_ID 						=	"user_id";
	public static String USER_NAME 						=	"USERNAME";
	public static String REGION 						=	"REGION";
	public static String PASSWORD 						=	"PASSWORD";
	public static String USER_TYPE 						=	"USER_TYPE";
	public static String CUREENT_LATTITUDE 				=	"CUREENT_LATTITUDE";
	public static String CUREENT_LONGITUDE 				=	"CUREENT_LONGITUDE";
	public static String REMEMBER_ME					=	"REMEMBER_ME";
	public static String RECIEPT_NO						=	"RECIEPT_NO";
	public static String CUSTOMER_SITE_ID				=	"CUSTOMER_SITE_ID";
	public static String LAST_CUSTOMER_SITE_ID			=	"LAST_CUSTOMER_SITE_ID";
	public static String INVOICE_NO						=	"INVOICE_NO";
	public static String CUSTOMER_DETAIL				=	"CUSTOMER_DETAIL";
	public static String LAST_JOURNEY_DATE				=	"LAST_JOURNEY_DATE";
	public static String LAST_SYNC_TIME 				= 	"LAST_SYNC_TIME";
	public static String TEMP_EMP_NO 					= 	"TEMP_EMP_NO";
	public static String EMP_NO 						= 	"EMP_NO";
	public static String SALESMANCODE 					= 	"SALESMANCODE";
	public static String JOURNEYCODE 					= 	"JOURNEYCODE";
	public static String SALESMAN_TYPE 					= 	"SALESMAN_TYPE";
	public static String IS_DATA_SYNCED_FOR_USER 		= 	"IS_DATA_SYNCED_FOR_USER";
	public static String IS_TELE_ORDER 					= 	"IS_TELE_ORDER";
	public static String IS_EOT_DONE					=	"IS_EOT_DONE";
	public static String IsStockVerified				=	"IsStockVerified";
	public static final String CURRENT_VEHICLE			= 	"LAST_TRUCK";
	public static final String OFFLINE_DATE 			= 	"OFFLINE_DATE";
	
	public static String INT_USER_ID 					=	"int_user_id";
	public static final String DefaultLoad_Quantity 	= 	"DefaultLoad_Quantity";
	public static final String CUSTOMER_NAME 			= 	"CUSTOMER_NAME";
	public static final String PAYMENT_TYPE 			= 	"PAYMENT_TYPE";
	public static final String PAYMENT_TERMS_DESC 		= 	"PAYMENT_TERMS_DESC";
	public static final String SUB_CHANNEL_CODE 		= 	"SUB_CHANNEL_CODE";
	public static final String CHANNEL_CODE 			= 	"CHANNEL_CODE";
	public static final String GET_ALL_TRANSFER_SYNCHTIME = 	"GET_ALL_TRANSFER_SYNCHTIME";
	public static final String CUSTOMER_ID 				= 	"CUSTOMER_ID";
	public static final String LSD 						= 	"LSD";
	public static final String LST 						= 	"LST";
	
	public static final String LSDActiveStatus			= 	"LSDActiveStatus";
	public static final String LSTActiveStatus			= 	"LSTActiveStatus";
	
	public static final String SIGNATURE 				= 	"SIGNATURE";
	public static final String ROUTE_CODE 				= 	"ROUTE_CODE";
	
	public static final String GetAllPromotions			= 	"GetAllPromotions";
	
	public static final String GetSurveyMasters			= 	"GetSurveyMasters";
	public static final String GetCustomersByUserID		= 	"GetCustomersByUserID";
	public static final String GetTrxHeaderForApp		= 	"GetTrxHeaderForApp";
	public static final String GetJPAndRouteDetails		= 	"GetJPAndRouteDetails";
	
	
	public static final String STARTDAY_TIME			= 	"STARTDAY_TIME";
	
	public static final String STARTDAY_TIME_ACTUAL		= 	"STARTDAY_TIME_ACTUAL";
	
	public static final String ENDAY_TIME				= 	"STARTDAY_TIME";
	public static final String STARTDAY_VALUE			= 	"STARTDAY_VALUE";
	
	public static final String ENDDAY_VALUE				= 	"ENDDAY_VALUE";
	/** This variable Decides whether the coupon will apply or not */
	public static String PASSCODE_SYNC 					=	"PASSCODE_SYNC";
	public static String TOTAL_TIME_TO_SERVE			=	"TOTAL_TIME_TO_SERVE";
	
	public static final String DAY_VARIFICATION			= 	"DAY_VARIFICATION";
	public static String GetAllAcknowledgedTask		 	=	"GetAllAcknowledgedTask";
	public static String GetAllTask		 				=	"GetAllTask";
	
	public static final String CURRENCY_CODE			= 	"Currency_Code";
	
	public static final String VEHICLE_DO				= 	"VehicleDO";
	public static final String IS_VANSTOCK_FROM_MENU_OPTION	= 	"IS_VANSTOCK_FROM_MENU_OPTION";
	public static final String gcmId					= 	"gcmId";
	public static final String SQLITE_DATE              = 	"SQLITE_DATE";
	public static final String ORG_CODE		            = 	"ORG_CODE";
	public static final String TEMP_REASON		        = 	"TEMP_REASON";
	
	public static final String PRESELLER	            = 	"Preseller";
	public static final String BUILD_INSTALLATIONDATE   ="BUILD_INSTALLATION_DATE";
	public static final String DataBasePath 			= "DATABASE_PATH";
	public static final String smssend 					= "false";
	
	
	public static final String VISIT_CODE    ="VISIT_CODE";
	
	/********VERSION UPDATE***********/
	public static String IS_SQLITE_DOWNLOADED			=	"IS_SQLITE_DOWNLOADED";
	public static final String IS_UPGRADE_POPUP_SHOWN	= 	"IS_UPGRADE_POPUP_SHOWN";
	
	public Preference(Context context) 
	{
		preferences		=	PreferenceManager.getDefaultSharedPreferences(context);
		edit			=	preferences.edit();
	}
	
	public void saveStringInPreference(String strKey,String strValue)
	{
		edit.putString(strKey, strValue);
	}
	public void saveIntInPreference(String strKey,int value)
	{
		edit.putInt(strKey, value);
	}
	public void saveBooleanInPreference(String strKey,boolean value)
	{
		edit.putBoolean(strKey, value);
	}
	public void saveLongInPreference(String strKey,Long value)
	{
		edit.putLong(strKey, value);
	}
	public void saveDoubleInPreference(String strKey,String value)
	{
		edit.putString(strKey, value);
	}
	
	public void removeFromPreference(String strKey)
	{
		edit.remove(strKey);
	}
	public void commitPreference()
	{
		edit.commit();
	}
	public String getStringFromPreference(String strKey,String defaultValue )
	{
		return preferences.getString(strKey, defaultValue);
	}
	public boolean getbooleanFromPreference(String strKey,boolean defaultValue)
	{
		return preferences.getBoolean(strKey, defaultValue);
	}
	public int getIntFromPreference(String strKey,int defaultValue)
	{
		return preferences.getInt(strKey, defaultValue);
	}
	public double getDoubleFromPreference(String strKey,double defaultValue)
	{
		return	Double.parseDouble(preferences.getString(strKey, ""+defaultValue));
	}
	
	public long getLongInPreference(String strKey)
	{
		return preferences.getLong(strKey, 0);
	}
	
	public CustomerDO getMallsDetailsObjectFromPreference(String strKey)
	{
		Gson gson 			= 	new Gson();
		String json 		= 	preferences.getString(strKey, "");
		CustomerDO obj 		=	gson.fromJson(json, CustomerDO.class);
		return obj;
	}
	
	public JourneyPlanDO getMallsDetailsObjectFromPreferenceNew1(String strKey)
	{
		Gson gson 			= 	new Gson();
		String json 		= 	preferences.getString(strKey, "");
		JourneyPlanDO obj 	=	gson.fromJson(json, JourneyPlanDO.class);
		return obj;
	}
	public void saveMallsDetailsObjectInPreference(String strKey, JourneyPlanDO obj)
	{
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		edit.putString(strKey, json);
	}
	
	public void saveVehicleObjectInPreference(String strKey, VehicleDO obj)
	{
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		edit.putString(strKey, json);
	}
	
	public VehicleDO getVehicleObjectFromPreference(String strKey)
	{
		Gson gson 			= 	new Gson();
		String json 		= 	preferences.getString(strKey, "");
		VehicleDO obj 	=	gson.fromJson(json, VehicleDO.class);
		return obj;
	}
	
	public MallsDetails getMallsDetailsObjectFromPreferenceNew(String strKey)
	{
		Gson gson 			= 	new Gson();
		String json 		= 	preferences.getString(strKey, "");
		MallsDetails obj 	=	gson.fromJson(json, MallsDetails.class);
		return obj;
	}
	public void saveMallsDetailsObjectInPreference(String strKey, MallsDetails obj)
	{
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		edit.putString(strKey, json);
	}
	public void clearPreferences() {
		edit.clear();
		edit.commit();
	}
}
