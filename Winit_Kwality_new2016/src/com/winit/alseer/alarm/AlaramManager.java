package com.winit.alseer.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.utilities.StringUtils;

public class AlaramManager 
{
	private AlarmManager mAlarmManager;
	private Context context;
	
	public AlaramManager(Context context) 
	{
		this.context = context;
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE); 
	}
	public void saveMorningReminder(String strTime)
	{
/*		Intent intent = new Intent(context, EventReminderManager.class);
		intent.putExtra(AppConstants.ALARM_TIME, strTime);
		context.startService(intent);*/
		
		setRecurringAlarm(strTime);
	}
	public void removeAllReminder()
	{
		removeAllAlarm();
	}
	private void removeAllAlarm() 
	{
		Intent alarmIntent = new Intent(context, ReminderReciever.class);
	    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context,	AppConstants.ALARM_TIME_REQ_CODE1, alarmIntent,0);
	    mAlarmManager.cancel(alarmPendingIntent);
	    
	   /* Intent alarmIntent2 = new Intent(context, ReminderReciever.class);
	    PendingIntent alarmPendingIntent2 = PendingIntent.getBroadcast(context,	AppConstants.ALARM_TIME_REQ_CODE2, alarmIntent2,0);
	    mAlarmManager.cancel(alarmPendingIntent2);*/
	}
	
	
	private void setRecurringAlarm(String strTime) {
		
		// Set the alarm to start at approximately 2:00 p.m.
		// https://developer.android.com/training/scheduling/alarms.html
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, StringUtils.getInt(strTime.split(":")[0]));
	    calendar.set(Calendar.MINUTE, StringUtils.getInt(strTime.split(":")[1]));
	    int alarmID = 0;
	    if(calendar.getTimeInMillis()<System.currentTimeMillis())
	    	calendar.add(Calendar.DATE, 1);
	    
//	    if(strTime.equalsIgnoreCase(AppConstants.ALARM_TIME_LOAD_NEW_REQ_1))
//			alarmID = AppConstants.ALARM_TIME_REQ_CODE1;
//		else
//			alarmID = AppConstants.ALARM_TIME_REQ_CODE2;
	    	
		// With setInexactRepeating(), you have to use one of the AlarmManager interval
		// constants--in this case, AlarmManager.INTERVAL_DAY.
		Intent alarmIntent = new Intent(context, ReminderReciever.class);
	    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context,alarmID, alarmIntent,0);
		mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
		        AlarmManager.INTERVAL_DAY, alarmPendingIntent);
		
		
		
	}
	
}
