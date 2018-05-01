/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.winit.kwalitysfa.salesman;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.sfa.salesman.NotificationActivity;

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService 
{
	public static int notificationCount = 0;
	Preference preference;

	// private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(AppConstants.SENDER_ID);// 956495984703
	}

	@Override
	protected void onError(Context context, String arg1) {
		LogUtils.errorLog("notification","*****************onError**********************");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		LogUtils.errorLog("notification","*****************onMessage**********************");
		String title = intent.getExtras().getString("type");
		String subject = intent.getExtras().getString("subject");
		String messageType = intent.getExtras().getString("MessageType");
		String releasetype = intent.getExtras().getString("releasetype");
		
		preference = new Preference(context);
//		if(!preference.getStringFromPreference(preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
		if(!TextUtils.isEmpty(title) && !TextUtils.equals(title, "null")
				&& !TextUtils.isEmpty(subject) && !TextUtils.equals(subject, "null"))
			generateNotification(context, title, subject,releasetype);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		if (registrationId.length() > 0) {
			LogUtils.errorLog("registrationId", "" + registrationId);
			register(registrationId);
			Preference pre = new Preference(GCMIntentService.this);
			pre.saveStringInPreference(Preference.gcmId, registrationId);
			pre.commitPreference();
		} else {
			if (AppConstants.GCMRegistrationAttempts > AppConstants.MaximumGCMRegistrationAttempts) {
			} else {
				AppConstants.GCMRegistrationAttempts++;
				GCMRegistrar.register(this, AppConstants.SENDER_ID);
			}
		}

	}

	public void register(final String gcmId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String userID = new Preference(GCMIntentService.this)
						.getStringFromPreference(Preference.EMP_NO, "");
				if (!userID.equalsIgnoreCase("")) {
					new ConnectionHelper(null).sendRequest(
							GCMIntentService.this,
							BuildXMLRequest.registerGCMOnServer(userID, gcmId),
							ServiceURLs.updateDeviceId);
				}
			}
		}).start();
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {

	}

	@SuppressWarnings("deprecation")
	private void generateNotification(Context context, String title,String message, String releasetype) 
	{
		// String id = getValue(message, "package_id");
		// String msg = getValue(message, "message");
		// int icon = R.drawable.app_logo;
		// long when = System.currentTimeMillis();
		// NotificationManager notificationManager =
		// (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		// @SuppressWarnings("deprecation")
		// Notification notification = new Notification(icon, message, when);
		// String title = context.getString(R.string.app_name);
		//
		// Intent notificationIntent = new Intent(context,
		// NotificationActivity.class);
		// notificationIntent.putExtra("message", message);
		// // set intent so it does not start a new activity
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// int requestcount = (int) System.currentTimeMillis();
		// PendingIntent intent = PendingIntent.getActivity(context,
		// requestcount, notificationIntent, 0);
		// notification.setLatestEventInfo(context, title, message, intent);
		//
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// notificationManager.notify(notificationId++, notification);

		Intent i = new Intent();
		i.setClass(this, NotificationActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("message", message);
		i.putExtra("title", title);
		i.putExtra("releasetype", releasetype);
		startActivity(i);
		
		LogUtils.errorLog("Notification Call Success-", "true");
		 
//		 if(preference == null)
//			 preference = new Preference(GCMIntentService.this);
//		 
//		 preference.saveBooleanInPreference(Preference.IS_UPGRADE_POPUP_SHOWN, false);
//		 preference.commitPreference();
//			
//		 Intent objIntent = new Intent();
//		 objIntent.setAction(AppConstants.ACTION_APP_UPGRADE);
//		 objIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		 objIntent.putExtra("message", message);
//		 objIntent.putExtra("title", title);
//		 objIntent.putExtra("releasetype", releasetype);
//		 sendBroadcast(objIntent);
	}

	private void broadcastNotification(String msg, String action) {
		Intent intent2 = new Intent();
		intent2.setAction(action);
		intent2.putExtra("result", "" + msg);
		getApplication().sendBroadcast(intent2);
	}

}
