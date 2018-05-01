package com.winit.kwalitysfa.salesman;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
   
//        try {
//           SmsManager smsManager = SmsManager.getDefault();
//           smsManager.sendTextMessage("121", null, "BAL", null, null);
//           Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show();
//        } 
//        
//        catch (Exception e) {
//           Toast.makeText(context, "SMS faild, please try again.", Toast.LENGTH_LONG).show();
//           e.printStackTrace();
//        } 
        
        try {
//        	Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("1233", null, "rewards", null, null);
//            smsManager.sendTextMessage("121", null, "bal", null, null);
            Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show();
         } 
         
         catch (Exception e) {
            Toast.makeText(context, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
         } 
    }
 }