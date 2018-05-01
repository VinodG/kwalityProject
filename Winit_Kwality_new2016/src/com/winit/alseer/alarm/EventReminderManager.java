package com.winit.alseer.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
public class EventReminderManager extends Service{

	private AlarmManager mAlarmManager;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.errorLog("EventReminderManager", "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.errorLog("EventReminderManager", "onStartCommand");
		mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE); 
		String strTime = intent.getStringExtra(AppConstants.ALARM_TIME);
		setReminder(strTime);
		return super.onStartCommand(intent, flags, startId);
	}

	public void setReminder(String strTime)
	{ 
//		strTime= 14:00;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, StringUtils.getInt(strTime.split(":")[0]));
		calendar.set(Calendar.MINUTE, StringUtils.getInt(strTime.split(":")[1]));
		calendar.set(Calendar.SECOND, 00);
		
		int alarmId = 0;
//		int alarmId = AppConstants.ALARM_TIME_REQ_CODE1;
//		if(strTime.equalsIgnoreCase(AppConstants.ALARM_TIME_LOAD_NEW_REQ_1))
//		{
//			alarmId = AppConstants.ALARM_TIME_REQ_CODE1;
//		}
//		else
//			alarmId = AppConstants.ALARM_TIME_REQ_CODE2;
		Intent alarmintent = new Intent(this,ReminderReciever.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, alarmId, alarmintent,PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, sender);
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
}
