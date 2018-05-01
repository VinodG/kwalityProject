package com.winit.sfa.salesman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.listeners.VersionChangeListner;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.SyncMovements;
import com.winit.kwalitysfa.salesman.R;

public class NotificationActivity extends BaseActivity
{
	private boolean boolisFirst = true, isAppRunning;
	private ProgressDialog progressDialog;
	private Button btnYesPopup,btnNoPopup;
	private TextView tvMsg, tvTitlePopup;
	private LinearLayout layout;
	private CustomDialog customDialog;
	private Preference preference;
	private String msg,title;
	private boolean isUpgrade = false;
	private String releasetype;
	
	
	@Override
	public void initialize() 
	{
		layout = (LinearLayout) getLayoutInflater().inflate(R.layout.notification_popup, null);
		setContentView(layout);
		preference 				= 		new Preference(getApplicationContext());
		btnYesPopup				= (Button)layout.findViewById(R.id.btnYesPopup);
		btnNoPopup 				= (Button)layout.findViewById(R.id.btnNoPopup);
		tvTitlePopup 			= (TextView)layout.findViewById(R.id.tvTitlePopup);
		tvMsg 					= (TextView)layout.findViewById(R.id.tvMessagePopup);
		
		msg = getIntent().getExtras().getString("message");
		title = getIntent().getExtras().getString("title");
		
		isUpgrade = getIntent().getExtras().getBoolean("isUpgrade");
		
		releasetype  = getIntent().getExtras().getString("releasetype");
		
		if(StringUtils.getInt(releasetype) == AppConstants.MAJOR_APP_UPDATE)
			btnNoPopup.setVisibility(View.GONE);
		else
			btnNoPopup.setVisibility(View.VISIBLE);
		
		tvTitlePopup.setText(title);
		tvMsg.setText(msg);
	
		
		btnYesPopup.setText("OK");
		btnYesPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isUpgrade)
				{
					new Thread(new Runnable() {
						
						@Override
						public void run() 
						{
							showLoader("Please wait...");
							callCheckVersionWebService(new VersionChangeListner() 
							{
								@Override
								public void onVersionChanged(int status) 
								{
									
								}
							}, AppConstants.CALL_FROM_NOTIFICATION);
							hideLoader();
						}
					}).start();
				}
				else
					finish();
			}
		});
		Intent uploadTraIntent=new Intent(this,SyncMovements.class);
		this.startService(uploadTraIntent);
		
		setTypeFace(layout);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showCustomDialog(NotificationActivity.this, title, msg, getResources().getString(R.string.OK), null, null);
	}
	public void showCustomDialog(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,String from) 
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage,firstBtnName, secondBtnName, from, true));
	}
	@Override
	protected void onPause()
	{
		super.onPause();
	}
	
	public void setTypeFace(ViewGroup group) 
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
	             ((TextView)v).setTypeface(AppConstants.Roboto_Condensed_Bold);
	         else if(v instanceof ViewGroup)
	        	 setTypeFace((ViewGroup)v);
	     }
	}
	class RunshowCustomDialogs implements Runnable
	{
		private String strTitle;// Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private String firstBtnName;
		private String secondBtnName;
		private String from;
		private boolean isCancelable=false;
		private OnClickListener posClickListener;
		private OnClickListener negClickListener;

		public RunshowCustomDialogs(Context context, String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, boolean isCancelable)
		{
			this.strTitle 		= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName 	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			this.isCancelable 	= isCancelable;
			if (from != null)
				this.from = from;
			else
				this.from = "";
		}

		@Override
		public void run() 
		{
			if (customDialog != null && customDialog.isShowing())
				customDialog.dismiss();

			View view = getLayoutInflater().inflate(R.layout.custom_common_popup, null);
			customDialog = new CustomDialog(NotificationActivity.this, view, preference
					.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,LayoutParams.WRAP_CONTENT, true,1);
			customDialog.setCancelable(isCancelable);
			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
			TextView tvMessage = (TextView) view.findViewById(R.id.tvMessagePopup);
			Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
			Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
			tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alert, 0, 0, 0);
			tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvMessage.setTypeface(AppConstants.Roboto_Condensed_Bold);
			btnYes.setTypeface(AppConstants.Roboto_Condensed_Bold);
			btnNo.setTypeface(AppConstants.Roboto_Condensed_Bold);

			tvTitle.setText("" + strTitle);
			tvMessage.setText("" + strMessage);
			btnYes.setText("" + firstBtnName);

			if (secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
				btnNo.setText("" + secondBtnName);
			else
				btnNo.setVisibility(View.GONE);

			if(posClickListener == null)
			{
				btnYes.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						customDialog.dismiss();
						finish();
					}
				});
			}
			else 
				btnYes.setOnClickListener(posClickListener);
			
			if(negClickListener == null)
				btnNo.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						customDialog.dismiss();
						finish();
					}
				});
			else 
				btnNo.setOnClickListener(negClickListener);
			try{
				if (!customDialog.isShowing())
					customDialog.show();
			}catch(Exception e){}
		}
	}
}
