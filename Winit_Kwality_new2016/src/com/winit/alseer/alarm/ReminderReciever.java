package com.winit.alseer.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.WareHouseDA;

public class ReminderReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		sendNotification(context, intent);
	}
	private void sendNotification(Context context, Intent intent)
	{
		Preference preference = new Preference(context);
		String strEmpNo = preference.getStringFromPreference(Preference.EMP_NO, "");
		if(new WareHouseDA().getLoadStatus(strEmpNo))
		{
			Intent reminderIntent;
			reminderIntent = new Intent(context, ShowReminder.class);
			reminderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(reminderIntent);
		}
	}
}