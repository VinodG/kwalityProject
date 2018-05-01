package com.winit.kwalitysfa.salesman;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class SMSservice extends Service{
	
	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,0);
        Intent myIntent = new Intent(SMSservice.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(SMSservice.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
