package com.winit.alseer.alarm;


import java.io.IOException;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.ShakeEventListener;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.AddStockInVehicle;

public class ShowReminder extends Activity
{
	private TextView tvPopupTitle,tvPopupMsg;
	private Button btnContinue,btnCancel;
	private MediaPlayer mediaPlayer;
	
	private Ringtone r;
//	private String fileName = "";

	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout llLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.popup_reminder, null);
		setContentView(llLayout,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		this.setFinishOnTouchOutside(false);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		tvPopupTitle = (TextView)findViewById(R.id.tvPopupTitle);
		tvPopupMsg = (TextView)findViewById(R.id.tvPopupMsg);
		btnContinue = (Button)findViewById(R.id.btnContinue);
		btnCancel = (Button)findViewById(R.id.btnCancel);

		btnContinue.setOnClickListener(click);
		btnCancel.setOnClickListener(click);
		
		setFontNormal(llLayout);
		setFontBoldView(tvPopupTitle);
		
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		
//		play();
		defaultAlert();
//		smsAlert();
	}


	OnClickListener click=new OnClickListener() {

		public void onClick(View v) 
		{
			stopPlayer();
			switch (v.getId()) {
			case R.id.btnContinue:
				Preference preference 	= 	new Preference(getApplicationContext());;
				Intent intent = new Intent(ShowReminder.this, AddStockInVehicle.class);
				intent.putExtra(Preference.VEHICLE_DO, (VehicleDO)preference.getVehicleObjectFromPreference("VehicleDO"));
				intent.putExtra("isPreview", true);
				startActivity(intent);
				finish();
				
				break;
			case R.id.btnCancel:
				finish();
				break;
			}
		}
	};

	public void play() {
		mediaPlayer = new MediaPlayer();
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		try {
			
//			mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.etisalat); // local etisalat.wav
//			mediaPlayer.setDataSource(getFilepath(fileName));
			
//			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  // TYPE_NOTIFICATION
			
			Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); // TYPE_ALARM
	        mediaPlayer = MediaPlayer.create(getApplicationContext(), alarm);
	        r = RingtoneManager.getRingtone(getApplicationContext(), alarm);
			r.play();
			
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			LogUtils.errorLog("AudioRecorder", "prepare() failed");
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				if(mediaPlayer != null)
				{
					mediaPlayer.release(); 
					mediaPlayer.stop(); 
				}
			}
		});
	}

	public void stopPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if(r != null)
		{
			r.stop();
			r = null;
		}
	}

	public void smsAlert(){
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
		} catch (Exception e) {}
	}
	public void defaultAlert(){
		try {
			Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			
//			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = RingtoneManager.getRingtone(getApplicationContext(), alarm);
			r.play();
		} catch (Exception e) {}
	}

	/**Method for set font to layout **/
	public void setFontNormal(ViewGroup group) {
		int count = group.getChildCount();
		View v;
		for (int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if (v instanceof TextView)
				((TextView) v).setTypeface(Typeface.createFromAsset(
						getAssets(), "Roboto_Condensed.ttf"));
			else if (v instanceof EditText)
				((EditText) v).setTypeface(Typeface.createFromAsset(
						getAssets(), "Roboto_Condensed.ttf"));
			else if (v instanceof Button)
				((Button) v).setTypeface(Typeface.createFromAsset(getAssets(),
						"Roboto_Condensed.ttf"));
			else if (v instanceof ViewGroup)
				setFontNormal((ViewGroup) v);
		}
	}
	/**Method for set font to layout **/
//	public void setFontBold(ViewGroup group) {
//		int count = group.getChildCount();
//		View v;
//		for (int i = 0; i < count; i++) {
//			v = group.getChildAt(i);
//			if (v instanceof TextView)
//				((TextView) v).setTypeface(Typeface.createFromAsset(
//						getAssets(), "Roboto_Condensed_bold.ttf"));
//			else if (v instanceof EditText)
//				((EditText) v).setTypeface(Typeface.createFromAsset(
//						getAssets(), "Roboto_Condensed_bold.ttf"));
//			else if (v instanceof Button)
//				((Button) v).setTypeface(Typeface.createFromAsset(getAssets(),
//						"Roboto_Condensed_bold.ttf"));
//			else if (v instanceof ViewGroup)
//				setFontBold((ViewGroup) v);
//		}
//	}
	
	/**Method for set font to layout **/
	public void setFontBoldView(View vw) {
		View v = vw;
//		for (int i = 0; i < count; i++) {
//			v = group.getChildAt(i);
			if (v instanceof TextView)
				((TextView) v).setTypeface(Typeface.createFromAsset(
						getAssets(), "Roboto_Condensed_bold.ttf"));
			else if (v instanceof EditText)
				((EditText) v).setTypeface(Typeface.createFromAsset(
						getAssets(), "Roboto_Condensed_bold.ttf"));
			else if (v instanceof Button)
				((Button) v).setTypeface(Typeface.createFromAsset(getAssets(),
						"Roboto_Condensed_bold.ttf"));
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();
	}

//	private String getFilepath(String name)
//	{
//		String path = "";
//		if(name.length() > 0){
//
//			path = new AudioRecorder().getFileDirPath();
//			path += name;
//			LogUtils.errorLog("mFileName", path);
//		}
//		return path;
//	}
}
