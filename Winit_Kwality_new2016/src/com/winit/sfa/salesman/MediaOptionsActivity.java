package com.winit.sfa.salesman;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.kwalitysfa.salesman.R;

public class MediaOptionsActivity extends BaseActivity implements OnClickListener
{

	private MediaRecorder myRecorder;
	private String outputFile = null;
	private Button mediaSubmit,mediaCancel;
	private ImageView stopBtn,startBtn,playbtn;
	private VideoView vVCaptureVedio;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
	private static final int CAMERA_CAPTURE_AUDIO_REQUEST_CODE = 300;
	private ImageView ivMedia,btnPlay;
	private LinearLayout llMediaOptions,llAudiButtons;
	private Bitmap mBtBitmap = null;
	private Bitmap bitmapProcessed;
	private SurveyQuestionNewDO questionDO;
	private String camera_imagepath = "";
	private Uri mVideoUri;
	private RelativeLayout rlVideo;
	private TextView tvTime,tvSurveyName;
	private CountDownTimer cdt;
	 private MediaPlayer myPlayer;
	 
	private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	long totalTime = 0L;
	long remaingTime = 0L;
	private String thumbnailuri;
	
	@Override
	public void initialize() 
	{
		llMediaOptions = (LinearLayout) inflater.inflate(R.layout.media, null);
		llBody.addView(llMediaOptions, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		if(getIntent().getExtras().containsKey("QuestionsDO"))
			questionDO = (SurveyQuestionNewDO) getIntent().getExtras().getSerializable("QuestionsDO");
		
		initializeControlls();
		captureMedia(questionDO);
		
		tvSurveyName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		mediaSubmit.setTypeface(AppConstants.Roboto_Condensed_Bold);
		mediaCancel.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	private void captureMedia(SurveyQuestionNewDO questionDO)
	{
		
		switch (AppConstants.getMediaOptionsTypes(questionDO.AnswerType))
		{
			case IMAGE:
				ivMedia.setVisibility(View.VISIBLE);
				llAudiButtons.setVisibility(View.GONE);
				rlVideo.setVisibility(View.GONE);
				tvSurveyName.setText("Capture Image");
				captureImage();
				break;
			case VIDEO:
				rlVideo.setVisibility(View.VISIBLE);
				ivMedia.setVisibility(View.GONE);
				llAudiButtons.setVisibility(View.GONE);
				tvSurveyName.setText("Video Record");
				captureVideo();
				break;
			case AUDIO:
				llAudiButtons.setVisibility(View.VISIBLE);
				ivMedia.setVisibility(View.GONE);
				rlVideo.setVisibility(View.GONE);
				tvSurveyName.setText("Audio Record");
				captureAudio();
				break;

		}
	}
	
	private void initializeControlls() 
	{
		
		 llAudiButtons = (LinearLayout)llMediaOptions.findViewById(R.id.llAudiButtons);
		 ivMedia = (ImageView)llMediaOptions.findViewById(R.id.ivMedia);
		 
		 startBtn = (ImageView)llMediaOptions.findViewById(R.id.start);
		 stopBtn = (ImageView)llMediaOptions.findViewById(R.id.stop);
		 playbtn = (ImageView)llMediaOptions.findViewById(R.id.play);
		 tvTime= (TextView)llMediaOptions.findViewById(R.id.tvTime);
		 mediaSubmit = (Button)llMediaOptions.findViewById(R.id.mediaSubmit);
		 mediaCancel = (Button)llMediaOptions.findViewById(R.id.mediaCancel);
		 
		 rlVideo = (RelativeLayout) llMediaOptions.findViewById(R.id.rlVideo);
		 btnPlay	= (ImageView) llMediaOptions.findViewById(R.id.btnPlay);
		 vVCaptureVedio = (VideoView) llMediaOptions.findViewById(R.id.vVCaptureVedio);
		 
		 tvSurveyName = (TextView)llMediaOptions.findViewById(R.id.tvSurveyName);
		
		 vVCaptureVedio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

	           @Override
	           public void onCompletion(MediaPlayer mp) 
	           {
	           	btnPlay.setVisibility(View.VISIBLE);
	           }
	       });
		 
		 btnPlay.setOnClickListener(this);
		 startBtn.setOnClickListener(this);
		 stopBtn.setOnClickListener(this);
		 playbtn.setOnClickListener(this);
		 mediaSubmit.setOnClickListener(this);
		 mediaCancel.setOnClickListener(this);
		 
		 playbtn.setEnabled(false);
		 stopBtn.setEnabled(false);
		 
		 playbtn.setTag("true");
			
	}
	
	public void refreshViews()
	{
		
		
		if(startBtn.isEnabled())
			startBtn.setBackgroundResource(R.drawable.rec);
		else
			startBtn.setBackgroundResource(R.drawable.rec_h);
		
		if(stopBtn.isEnabled())
			stopBtn.setBackgroundResource(R.drawable.stop);
		else
			stopBtn.setBackgroundResource(R.drawable.stop_h);
		
		if(playbtn.getTag().toString().equalsIgnoreCase("true"))
		{
			if(!startBtn.isEnabled())
				playbtn.setBackgroundResource(R.drawable.play_h);
			else
				playbtn.setBackgroundResource(R.drawable.play);
		}
		else
			playbtn.setBackgroundResource(R.drawable.pause);
	}
	
	public void captureVideo()
	{
		Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT,mVideoUri);
	    startActivityForResult(intent,CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
	}
	
	private void captureImage()
	{
			File file    = FileUtils.getOutputImageFile("competitors");
			if(file!=null)
			{
				camera_imagepath   = file.getAbsolutePath();
				Uri fileUri  = Uri.fromFile(file);
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			    intent.putExtra("fileName",file.getName());
			    intent.putExtra("filePath", file.getAbsolutePath());
			    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
	}
	private void captureAudio()
	{
		llAudiButtons.setVisibility(View.VISIBLE);
		
		File file    = FileUtils.getOutputAudioFile("Record");
		
		if(file!=null)
			outputFile   = file.getAbsolutePath();

	      myRecorder = new MediaRecorder();
	      myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	      myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	      myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
	      myRecorder.setOutputFile(outputFile);
	      
	}
	
	public void start(View view)
	{
		  myRecorder = new MediaRecorder();
	      myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	      myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	      myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
	      myRecorder.setOutputFile(outputFile);
	      
		   try 
		   {
	          myRecorder.prepare();
	          myRecorder.start();
	          recordAudioTime();
	       }
		   catch (IllegalStateException e) 
	       {
	          // start:it is called before prepare()
	    	  // prepare: it is called after start() or before setOutputFormat() 
	          e.printStackTrace();
	       } catch (IOException e) {
	           // prepare() fails
	           e.printStackTrace();
	        }
		   
	       startBtn.setEnabled(false);
	       stopBtn.setEnabled(true);
	       
	       Toast.makeText(getApplicationContext(), "Start recording...", 
	    		   Toast.LENGTH_SHORT).show();
	   }

	  

	public void stop(View view)
	   {
		   try 
		   {
			   canelTimer();
			   cancelCountDown();
			   
		      myRecorder.stop();
		      myRecorder.release();
		      myRecorder  = null;
		      
		      stopPlay(view);
		      stopBtn.setEnabled(false);
		      
		      Toast.makeText(getApplicationContext(), "Stop recording...",
		    		  Toast.LENGTH_SHORT).show();
		   } catch (IllegalStateException e) {
				//  it is called before start()
				e.printStackTrace();
		   } catch (RuntimeException e) {
				// no valid audio/video data has been received
				e.printStackTrace();
		   }
	   }
	   
	   public void play(View view) 
	   {
		   try
		   {
			   myPlayer = new MediaPlayer();
			   myPlayer.setDataSource(outputFile);
			   myPlayer.prepare();
			   myPlayer.start();
			   playbtn.setEnabled(true);
			   stopBtn.setEnabled(true);
			   runTimerForPlayingAudio(totalTime);
		   }
		   catch (Exception e) 
		   {
				e.printStackTrace();
			}
	   }
	   
	   public void pause(View view) 
	   {
		   try
		   {
			   myPlayer.prepare();
			   myPlayer.start();
			   playbtn.setEnabled(true);
			   stopBtn.setEnabled(true);
			   runTimerForPlayingAudio(remaingTime);
		   }
		   catch (Exception e) 
		   {
				e.printStackTrace();
			}
	   }
	   
	   public void stopPlay(View view) 
	   {
		   try 
		   {
			   
		       if (myPlayer != null) 
		       {
		    	   myPlayer.stop();
		           myPlayer.release();
		           myPlayer = null;
		           playbtn.setEnabled(true);
		           stopBtn.setEnabled(false);
		       }
		   } catch (Exception e)
		   {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	   
   @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
		{
			   switch (resultCode) 
			   {
					case RESULT_CANCELED:
						Log.i("Camera", "User cancelled");
						break;
	
					case RESULT_OK:
						File f = new File(camera_imagepath);
						Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
						mBtBitmap = bmp;
						ivMedia.setBackgroundResource(0);
						ivMedia.setImageBitmap(mBtBitmap);
						break;
				}

		}
		else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) 
		{
		    mVideoUri = data.getData();
		    
		    thumbnailuri = getRealPathFromURI(MediaOptionsActivity.this,mVideoUri);
	      Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(thumbnailuri,MediaStore.Images.Thumbnails.MINI_KIND); 
	      BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
	      vVCaptureVedio.setBackgroundDrawable(bitmapDrawable);
		    
		    if(mVideoUri != null)
		    {
		    	ivMedia.setVisibility(View.GONE);
		    	rlVideo.setVisibility(View.VISIBLE);
		    }
		}
	}

	  
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.start:
				start(v);
				startBtn.setEnabled(false);
			    stopBtn.setEnabled(true);
			    playbtn.setEnabled(false);
				refreshViews();
				
				break;
				
			case R.id.play:
				
			if(((String)v.getTag()).equalsIgnoreCase("true"))
			{
				v.setTag("false");
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				playbtn.setEnabled(true);
				refreshViews();
				play(v);
			}
			else
			{
				v.setTag("true");
				startBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				playbtn.setEnabled(true);
				refreshViews();
				pause(v);
			}
				break;
				
			case R.id.stop:
				stop(v);
				startBtn.setEnabled(true);
			    stopBtn.setEnabled(false);
			    playbtn.setEnabled(true);
			    playbtn.setTag("true");
				refreshViews();
				break;
			case R.id.btnPlay:
				
			   btnPlay.setVisibility(View.INVISIBLE);
			   vVCaptureVedio.setMediaController(new MediaController(MediaOptionsActivity.this));       
			   vVCaptureVedio.setVideoURI(mVideoUri);
			   vVCaptureVedio.requestFocus();
			   vVCaptureVedio.start();
			   break;
			case R.id.mediaCancel:
				finish();
				break;
			case R.id.mediaSubmit:
				switch (AppConstants.getMediaOptionsTypes(questionDO.AnswerType))
				{
					case IMAGE:
						questionDO.Answer=camera_imagepath;
						break;
					case VIDEO:
						if(mVideoUri!=null)
						questionDO.Answer=thumbnailuri;
						break;
					case AUDIO:
						questionDO.Answer=outputFile;
						break;

				}
				
				Intent intent = new Intent();
				intent.putExtra("QuestionsDO", questionDO);
				setResult(RESULT_OK,intent);
				finish();
				break;
		}
		
	}
	
	
	 private void recordAudioTime()
	  {
		
		 startTime = SystemClock.uptimeMillis();
		 customHandler.postDelayed(updateTimerThread, 0);
	  }
	 private void canelTimer()
	 {
		 customHandler.removeCallbacks(updateTimerThread);
	 }
	
	private Runnable updateTimerThread = new Runnable() 
	{

		public void run() {
			
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			
			updatedTime = timeSwapBuff + timeInMilliseconds;
			totalTime = updatedTime;
			
			
			
			int days = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            String sDate = "";

            if(updatedTime > DateUtils.DAY_IN_MILLIS)
            {
                days = (int) (updatedTime / DateUtils.DAY_IN_MILLIS);
                sDate += days+"d";
            }

            updatedTime -= (days*DateUtils.DAY_IN_MILLIS);

            if(updatedTime > DateUtils.HOUR_IN_MILLIS)
            {
                hours = (int) (updatedTime / DateUtils.HOUR_IN_MILLIS);
            }

            updatedTime -= (hours*DateUtils.HOUR_IN_MILLIS);

            if(updatedTime > DateUtils.MINUTE_IN_MILLIS)
            {
                minutes = (int) (updatedTime / DateUtils.MINUTE_IN_MILLIS);
            }

            updatedTime -= (minutes*DateUtils.MINUTE_IN_MILLIS);

            if(updatedTime > DateUtils.SECOND_IN_MILLIS)
            {
                seconds = (int) (updatedTime / DateUtils.SECOND_IN_MILLIS);
            }

            sDate += " "+String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds);
            tvTime.setText(sDate.trim());
			

//			int secs = (int) (updatedTime / 1000);
//			int mins = secs / 60;
//			secs = secs % 60;
//			int milliseconds = (int) (updatedTime % 1000);
//			tvTime.setText("" + mins + ":"
//					+ String.format("%02d", secs) + ":"
//					+ String.format("%03d", milliseconds));
			
			customHandler.postDelayed(this, 0);
		}

	};
	
	private void runTimerForPlayingAudio(final long updatedTime)
	{
	        if(cdt!=null)
	        {
	            cdt.cancel();
	            cdt=null;
	        }

	        long difference = updatedTime;

	        if(difference>0)
	        {
		        cdt = new CountDownTimer(difference, 1000)
		        {
		            @Override
		            public void onTick(long millisUntilFinished) 
		            {
		                int days = 0;
		                int hours = 0;
		                int minutes = 0;
		                int seconds = 0;
		                String sDate = "";
		                
		                remaingTime = updatedTime-millisUntilFinished;
		                
		                if(millisUntilFinished > DateUtils.DAY_IN_MILLIS)
		                {
		                    days = (int) (millisUntilFinished / DateUtils.DAY_IN_MILLIS);
		                    sDate += days+"d";
		                }
		
		                millisUntilFinished -= (days*DateUtils.DAY_IN_MILLIS);
		
		                if(millisUntilFinished > DateUtils.HOUR_IN_MILLIS)
		                {
		                    hours = (int) (millisUntilFinished / DateUtils.HOUR_IN_MILLIS);
		                }
		
		                millisUntilFinished -= (hours*DateUtils.HOUR_IN_MILLIS);
		
		                if(millisUntilFinished > DateUtils.MINUTE_IN_MILLIS)
		                {
		                    minutes = (int) (millisUntilFinished / DateUtils.MINUTE_IN_MILLIS);
		                }
		
		                millisUntilFinished -= (minutes*DateUtils.MINUTE_IN_MILLIS);
		
		                if(millisUntilFinished > DateUtils.SECOND_IN_MILLIS)
		                {
		                    seconds = (int) (millisUntilFinished / DateUtils.SECOND_IN_MILLIS);
		                }
		
		                sDate += " "+String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds);
		                tvTime.setText(sDate.trim());
		            }

					@Override
					public void onFinish()
					{
						
						playbtn.setBackgroundResource(R.drawable.play);
						playbtn.setTag("true");
						playbtn.setEnabled(true);
						
						
					}
		
		            
		        };
	        }

	        if(cdt!=null)
	        {
		        cdt.start();
	        }
	}
	
	private void cancelCountDown()
	{
		if(cdt!=null)
        {
	        cdt.cancel();
	        cdt=null;
        }
	}
	public String getRealPathFromURI(Context context, Uri contentUri) 
	{
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
