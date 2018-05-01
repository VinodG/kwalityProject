package com.winit.sfa.salesman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.winit.alseer.salesman.common.UploadData;
import com.winit.alseer.salesman.utilities.CalendarUtils;

public class MyScheduleReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		try
		{
			if(isNetworkConnectionAvailable(context))
			{
				UploadData uploadData = new UploadData(context, null, CalendarUtils.getOrderPostDate());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public boolean isNetworkConnectionAvailable(Context context) 
	{
		// checking the Internet availability
		boolean isNetworkConnectionAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context	.getSystemService("connectivity");
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo != null)
		{
			isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
		}

		return isNetworkConnectionAvailable;
	}

} 