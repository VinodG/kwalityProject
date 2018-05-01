package com.winit.sfa.salesman;

import android.content.Context;


public class MyApplication extends android.app.Application
{
	 public static String MyLock = "Lock";
	 public static String SALES_UNITS_LOCK = "SALES_UNITS_LOCK";
	 public static String APP_DB_LOCK = "lock";
	 public static String Service_Lock = "ServiceLock";
	 public static Context mContext;
	@Override
    public void onCreate() 
	{
        super.onCreate();
        if(mContext ==null)
			mContext = this;
    }
}
