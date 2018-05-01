package com.winit.sfa.salesman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.winit.alseer.parsers.GetAllServey;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.dataobject.CustomerSurveyDONew;
import com.winit.alseer.salesman.dataobject.ServeyListDO;
import com.winit.alseer.salesman.dataobject.ServeyOptionsDO;
import com.winit.alseer.salesman.dataobject.ServeyQuestionsDO;
import com.winit.alseer.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.alseer.salesman.imageloader.UrlImageViewCallback;
import com.winit.alseer.salesman.imageloader.UrlImageViewHelper;
import com.winit.alseer.salesman.utilities.BitmapsUtiles;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.kwalitysfa.salesman.R;

public class ServeyDetailsActivity extends BaseActivity implements LocationResult{
	private LinearLayout llTaskDetials;
	private LinearLayout llsmallques,llbigques,llnumericques,llcheckbox,llradiobtn,llyesorno,llcamera,llvideo,llcheckAns,llradioAns, llEmmotions;
	private CustomerSurveyDONew  cussurveydo;
	private Button btnsubmit;
	private String errorMessage = "";
	private boolean isValid = false ;
	private TextView tv_Emotions, tv_taskName,tv_numericques,tv_smallques,tv_bigques,tv_checkboxque,tv_radiobtnque,tv_yesnoque,tv_camera,tv_video;
	private ListView lvServeyQue;
	private String date = "";
	private EditText et_smallinput,et_biginput,et_numericinput;
	private double latti, longi;
	private String lat = "", lang = "";
	private String userRole="";
	private ImageView iv_imagepic,iv_yesno;
	private View  tempView;
	private final int DATE_DIALOG_ID = 0;
    private LocationRequest locationRequest;
    private String camera_imagepath;
	public static String APPFOLDERNAME ="Panada";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "PanadaImages";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	private int  CAMERA_REQUESTCODE = 2;
	private int  IMAGE_REQUESTCODE = 4;
	private int ACTION_TAKE_VIDEO = 3;
	private LocationClient locationClient;
	private Uri mVideoUri;
	private int mPosition = 0;
	private String thumbnailuri;
	private LocationUtility locationUtility;
	private Bitmap bitmapProcessed;
	private String lattitude,longitude;
	private ImageView buttonplay;
	private VideoView video_view;
	SurveyCustomerDeatislDO cusdetailDo;
	private RelativeLayout rlVideo;
	Location loc;
	private int tempCount;
	private ServeyListDO serveyListDO ;
	
	private String projetcId = "";
	
	private ImageView iv1, iv2, iv3, iv4, iv5;
	private ImageView ivRadio1, ivRadio2, ivRadio3, ivRadio4, ivRadio5;
	
	@Override
	public void initialize() 
	{
		
		llTaskDetials = (LinearLayout) inflater.inflate(R.layout.taskdetails, null);
		llBody.addView(llTaskDetials, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		initializeControlls();
		try
		{
			new File(APPMEDIAFOLDERPATH).mkdirs();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		if(getIntent().getExtras() != null)
		{
			serveyListDO = (ServeyListDO) getIntent().getExtras().get("Object");
			projetcId = getIntent().getExtras().getString("projetcId");
		}
		
		
		llcamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				final CharSequence[] options = new String[] {"Take Photo","Choose From Gallery" };
				AlertDialog.Builder builder = new AlertDialog.Builder(ServeyDetailsActivity.this);
				builder.setItems(options,new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog,int selected) 
					{
						if (options[selected].equals("Take Photo")) 
						{
							File imageDir = new File(APPMEDIAFOLDERPATH);
							if(!imageDir.exists())
								imageDir.mkdirs();
							
//				            try 
//				            {
				            	camera_imagepath = APPMEDIAFOLDERPATH+"PANDA"+ System.currentTimeMillis()+ ".jpg";
				            	File camera_image = new File(camera_imagepath);
//				                camera_image.createNewFile();
				                Uri outputFileUri = Uri.fromFile(camera_image);
				                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
				                startActivityForResult(intent,CAMERA_REQUESTCODE);
//				            } 
//				            catch (IOException e) 
//				            {
//				            	e.printStackTrace();
//				            }   
						}
							
						
						else if (options[selected].equals("Choose From Gallery"))
						{
							Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent,IMAGE_REQUESTCODE);
						}
					}
				});
				builder.show();
			}
		});
		
		llvideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,mVideoUri);
				startActivityForResult(intent,ACTION_TAKE_VIDEO);
			}
		});
		
		
		
		if(serveyListDO != null && serveyListDO.vecQuestionsDOs != null && serveyListDO.vecQuestionsDOs.size() >0)
		{
			
			for (final ServeyQuestionsDO serveyQuestionsDO : serveyListDO.vecQuestionsDOs) 
			{
				tempCount = 0;
				if(serveyQuestionsDO.AnswerType.equals("checkboxes"))
				{
					tv_checkboxque.setText(serveyQuestionsDO.Question);
					llcheckbox.setVisibility(View.VISIBLE);
					 int temp = serveyQuestionsDO.vecOptionsDOs.size();
					 if(temp%2==0)
							temp = temp/2;
						else
							temp = (temp+1)/2;
					Log.e("temp size", ""+temp);
					for (int i = 0; i < temp; i++) 
					{
						final int k = i;
						LinearLayout llAnsOption 		= (LinearLayout)getLayoutInflater().inflate(R.layout.survey_answer_cell, null);
						TextView tvAns1 				= (TextView) llAnsOption.findViewById(R.id.tvAns1);
						TextView tvAns2 				= (TextView) llAnsOption.findViewById(R.id.tvAns2);
						ImageView ivAns1 				= (ImageView) llAnsOption.findViewById(R.id.ivAns1);
						ImageView ivAns2 				= (ImageView) llAnsOption.findViewById(R.id.ivAns2);
						
						ivAns1.setImageResource(R.drawable.unchecked1);
						ivAns2.setImageResource(R.drawable.unchecked1);
						
						tvAns1.setText(serveyQuestionsDO.vecOptionsDOs.get(i*2).SurveyOption);
						
						llAnsOption.findViewById(R.id.llOption1).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								UnselctAllView1(llcheckAns,v, tempCount, serveyQuestionsDO.vecOptionsDOs.get(k*2));
							}
						});
						
						llAnsOption.findViewById(R.id.llOption2).setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								UnselctAllView1(llcheckAns,v, tempCount, serveyQuestionsDO.vecOptionsDOs.get(k*2+1));
							}
						});
						
						if((i+1)*2 >serveyQuestionsDO.vecOptionsDOs.size())
							 llAnsOption.findViewById(R.id.llOption2).setVisibility(View.GONE);
						else
						{
							tvAns2.setText(serveyQuestionsDO.vecOptionsDOs.get(i*2+1).SurveyOption);
						}
						
						llcheckAns.addView(llAnsOption);
					}
				}
				else if(serveyQuestionsDO.AnswerType.equals("shortanswer"))
				{
					tv_smallques.setText(serveyQuestionsDO.Question);
					llsmallques.setVisibility(View.VISIBLE);
					et_smallinput.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							
						}
						
						@Override
						public void afterTextChanged(Editable s) 
						{
							serveyQuestionsDO.strAnswer = s.toString();
						}
					});
				}
				else if(serveyQuestionsDO.AnswerType.equals("longanswer"))
				{
					llbigques.setVisibility(View.VISIBLE);
					tv_bigques.setText(serveyQuestionsDO.Question);
					et_biginput.addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							
						}
						
						@Override
						public void afterTextChanged(Editable s) 
						{

							serveyQuestionsDO.strAnswer = s.toString();
						}
					});
				}
				else if(serveyQuestionsDO.AnswerType.equals("radiobutton"))
				{
					tv_radiobtnque.setText(serveyQuestionsDO.Question);
					llradiobtn.setVisibility(View.VISIBLE);
					 int temp = serveyQuestionsDO.vecOptionsDOs.size();
					 if(temp%2==0)
							temp = temp/2;
						else
							temp = (temp+1)/2;
						for (int i = 0; i < temp; i++) 
						{
							LinearLayout llAnsOption 		=  (LinearLayout)getLayoutInflater().inflate(R.layout.survey_answer_cell, null);
							TextView tvAns1 				= (TextView) llAnsOption.findViewById(R.id.tvAns1);
							TextView tvAns2 				= (TextView) llAnsOption.findViewById(R.id.tvAns2);
							ImageView ivAns1 				= (ImageView) llAnsOption.findViewById(R.id.ivAns1);
							ImageView ivAns2 				= (ImageView) llAnsOption.findViewById(R.id.ivAns2);
							tvAns1.setText(serveyQuestionsDO.vecOptionsDOs.get(i*2).SurveyOption);
							ivAns1.setImageResource(R.drawable.radiobtn);
							ivAns2.setImageResource(R.drawable.radiobtn);
							
							
							if((i+1)*2 >serveyQuestionsDO.vecOptionsDOs.size())
								 llAnsOption.findViewById(R.id.llOption2).setVisibility(View.GONE);
							else
							{
								tvAns2.setText(serveyQuestionsDO.vecOptionsDOs.get(i*2+1).SurveyOption);
								llAnsOption.findViewById(R.id.llOption2).setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) 
									{
										serveyQuestionsDO.strAnswer = UnselctAllView(llradioAns,v, tempCount, serveyQuestionsDO);
									}
								});
							}
						
							llAnsOption.findViewById(R.id.llOption1).setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) 
								{
									serveyQuestionsDO.strAnswer = UnselctAllView(llradioAns,v, tempCount, serveyQuestionsDO);
								}
							});
							llradioAns.addView(llAnsOption);
						}
				}
				else if(serveyQuestionsDO.AnswerType.equalsIgnoreCase("numeric"))
				{
					tv_numericques.setText(serveyQuestionsDO.Question);
					llnumericques.setVisibility(View.VISIBLE);
					
					et_numericinput.addTextChangedListener(new TextWatcher() 
					{
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) 
						{
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,int after) 
						{
							
						}
						
						@Override
						public void afterTextChanged(Editable s) 
						{
							serveyQuestionsDO.strAnswer = s.toString();
							
						}
					});
				
				tempCount++;
			}
			else if(serveyQuestionsDO.AnswerType.equalsIgnoreCase("yesno"))
			{
				tv_yesnoque.setText(serveyQuestionsDO.Question);
				llyesorno.setVisibility(View.VISIBLE);
			
				serveyQuestionsDO.strAnswer = "No";
				
				iv_yesno.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						boolean isChecked = false;
						if(v.getTag()!=null)
							isChecked = (Boolean) v.getTag();
						if(!isChecked)
						{
							iv_yesno.setImageResource(R.drawable.yes);
							 v.setTag(true);
							 
							 serveyQuestionsDO.strAnswer = "Yes"; 
						}
						else
						{
							iv_yesno.setImageResource(R.drawable.no);
							v.setTag(false);
							serveyQuestionsDO.strAnswer = "No"; 
						}
					}
				});
			}
			else if(serveyQuestionsDO.AnswerType.equalsIgnoreCase("emotion") && AppConstants.vecFeelingsDOs != null && AppConstants.vecFeelingsDOs.size()>0)
			{
				tv_Emotions.setText(serveyQuestionsDO.Question);
				llEmmotions.setVisibility(View.VISIBLE);
				
				try 
				{
					UrlImageViewHelper.setUrlDrawable(iv1, AppConstants.vecFeelingsDOs.get(0).ImageUrl, R.drawable.app_logo, new UrlImageViewCallback() {
		                   @Override
		                   public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
		                       if (!loadedFromCache) {
		                           ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		                           scale.setDuration(300);
		                           scale.setInterpolator(new OvershootInterpolator());
		                           imageView.startAnimation(scale);
		                       }
		                   }
		             });
					
					UrlImageViewHelper.setUrlDrawable(iv2, AppConstants.vecFeelingsDOs.get(1).ImageUrl, R.drawable.app_logo, new UrlImageViewCallback() {
		                   @Override
		                   public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
		                       if (!loadedFromCache) {
		                           ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		                           scale.setDuration(300);
		                           scale.setInterpolator(new OvershootInterpolator());
		                           imageView.startAnimation(scale);
		                       }
		                   }
		             });
					
					UrlImageViewHelper.setUrlDrawable(iv3, AppConstants.vecFeelingsDOs.get(2).ImageUrl, R.drawable.app_logo, new UrlImageViewCallback() {
		                   @Override
		                   public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
		                       if (!loadedFromCache) {
		                           ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		                           scale.setDuration(300);
		                           scale.setInterpolator(new OvershootInterpolator());
		                           imageView.startAnimation(scale);
		                       }
		                   }
		             });
					
					UrlImageViewHelper.setUrlDrawable(iv4, AppConstants.vecFeelingsDOs.get(3).ImageUrl, R.drawable.app_logo, new UrlImageViewCallback() {
		                   @Override
		                   public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
		                       if (!loadedFromCache) {
		                           ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		                           scale.setDuration(300);
		                           scale.setInterpolator(new OvershootInterpolator());
		                           imageView.startAnimation(scale);
		                       }
		                   }
		             });
					
					UrlImageViewHelper.setUrlDrawable(iv5, AppConstants.vecFeelingsDOs.get(4).ImageUrl, R.drawable.app_logo, new UrlImageViewCallback() {
		                   @Override
		                   public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
		                       if (!loadedFromCache) {
		                           ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
		                           scale.setDuration(300);
		                           scale.setInterpolator(new OvershootInterpolator());
		                           imageView.startAnimation(scale);
		                       }
		                   }
		             });
				} 
				catch (Exception e)
				{
					// TODO: handle exception
				}
				iv1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ivRadio1.setBackgroundResource(R.drawable.radiobtnchecked);
						ivRadio2.setBackgroundResource(R.drawable.radiobtn);
						ivRadio3.setBackgroundResource(R.drawable.radiobtn);
						ivRadio4.setBackgroundResource(R.drawable.radiobtn);
						ivRadio5.setBackgroundResource(R.drawable.radiobtn);
						serveyQuestionsDO.strAnswer = AppConstants.vecFeelingsDOs.get(0).ImageUrl; 
					}
				});
				iv2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ivRadio1.setBackgroundResource(R.drawable.radiobtn);
						ivRadio2.setBackgroundResource(R.drawable.radiobtnchecked);
						ivRadio3.setBackgroundResource(R.drawable.radiobtn);
						ivRadio4.setBackgroundResource(R.drawable.radiobtn);
						ivRadio5.setBackgroundResource(R.drawable.radiobtn);
						serveyQuestionsDO.strAnswer = AppConstants.vecFeelingsDOs.get(1).ImageUrl;
					}
				});
				iv3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ivRadio1.setBackgroundResource(R.drawable.radiobtn);
						ivRadio2.setBackgroundResource(R.drawable.radiobtn);
						ivRadio3.setBackgroundResource(R.drawable.radiobtnchecked);
						ivRadio4.setBackgroundResource(R.drawable.radiobtn);
						ivRadio5.setBackgroundResource(R.drawable.radiobtn);
						serveyQuestionsDO.strAnswer = AppConstants.vecFeelingsDOs.get(2).ImageUrl;
					}
				});
				iv4.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ivRadio1.setBackgroundResource(R.drawable.radiobtn);
						ivRadio2.setBackgroundResource(R.drawable.radiobtn);
						ivRadio3.setBackgroundResource(R.drawable.radiobtn);
						ivRadio4.setBackgroundResource(R.drawable.radiobtnchecked);
						ivRadio5.setBackgroundResource(R.drawable.radiobtn);
						serveyQuestionsDO.strAnswer = AppConstants.vecFeelingsDOs.get(3).ImageUrl;
					}
				});
				iv5.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ivRadio1.setBackgroundResource(R.drawable.radiobtn);
						ivRadio2.setBackgroundResource(R.drawable.radiobtn);
						ivRadio3.setBackgroundResource(R.drawable.radiobtn);
						ivRadio4.setBackgroundResource(R.drawable.radiobtn);
						ivRadio5.setBackgroundResource(R.drawable.radiobtnchecked);
						serveyQuestionsDO.strAnswer = AppConstants.vecFeelingsDOs.get(4).ImageUrl;
					}
				});

			}
			else if(serveyQuestionsDO.AnswerType.equalsIgnoreCase("image"))
			{
				tv_camera.setText(serveyQuestionsDO.Question);
				llcamera.setVisibility(View.VISIBLE);
			}
			else if(serveyQuestionsDO.AnswerType.equalsIgnoreCase("video"))
			{
				tv_video.setText(serveyQuestionsDO.Question);
				llvideo.setVisibility(View.VISIBLE);
			}
		}
	}
		
//		btnCheckOut.setVisibility(View.GONE);
//		ivLogOut.setVisibility(View.GONE);
		
		tv_taskName.setText(serveyListDO.SurveyTitle);
//		
//		tv_smallques.setText("Enter Single Line Comments.");
//		tv_bigques.setText("Enter Description.");
//		tv_checkboxque.setText("Select Options.");
//		tv_radiobtnque.setText("Select a option.");
//		tv_yesnoque.setText(taskDO.Title);
//		tv_camera.setText("Capture Image");
//		tv_video.setText("Capture Video");
		
		btnsubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				showLoader("Please wait...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						
						ConnectionHelper connectionHelper = new ConnectionHelper(ServeyDetailsActivity.this);
						final GetAllServey getAllServey = new GetAllServey();
						connectionHelper.startDataDownloadNew(serveyAnswerParameter(serveyListDO), ServeyDetailsActivity.this, 1+"", getAllServey);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{
								if(AppConstants.hmSurvey == null)
									AppConstants.hmSurvey = new HashMap<String, String>();
								
								AppConstants.hmSurvey.put(serveyListDO.SurveyId, serveyListDO.SurveyId);
								hideLoader();
								showCustomDialog(ServeyDetailsActivity.this, "Success!", "Survey submited successfully.", getString(R.string.OK), null, "success");
							}
						});
					
//						ConnectionHelper connectionHelper = new ConnectionHelper(ServeyDetailsActivity.this);
//						final SaveServeyAnswerParser projectParser = new SaveServeyAnswerParser(ServeyDetailsActivity.this);
//						connectionHelper.sendRequest_Bulk(ServeyDetailsActivity.this,BuildXMLRequest.postServeyAnswer(preference.getStringFromPreference(Preference.EMP_NO, ""),serveyListDO), projectParser, ServiceURLs.PostSurveyAnswers);
//						runOnUiThread(new Runnable() 
//						{
//							@Override
//							public void run() 
//							{
//								hideLoader();
//								String status = projectParser.getStatus();
//								if(status != null && status.equalsIgnoreCase("Success"))
//								{
//									showCustomDialog(ServeyDetailsActivity.this, "Success!", "Answer posted successfully.", getString(R.string.OK), null, "success");
//								}
//								else
//									showCustomDialog(ServeyDetailsActivity.this, "Alert!", "Unable to  post answer. Please try again.", getString(R.string.OK), null, "");
//							}
//						});
					}
				}).start();
			}
		});
		
		setTitle("Survey Details");
		
		setTypeFaceRobotoNormal(llTaskDetials);
	}
	
	
	private void initializeControlls() 
	{
		tv_Emotions			= (TextView) llTaskDetials.findViewById(R.id.tv_Emotions);
		tv_taskName 		= (TextView) llTaskDetials.findViewById(R.id.tv_taskName);
		tv_smallques 		= (TextView) llTaskDetials.findViewById(R.id.tv_smallques);
		tv_bigques 		= (TextView) llTaskDetials.findViewById(R.id.tv_bigques);
		tv_checkboxque 		= (TextView) llTaskDetials.findViewById(R.id.tv_checkboxque);
		tv_radiobtnque 		= (TextView) llTaskDetials.findViewById(R.id.tv_radiobtnque);
		tv_yesnoque 		= (TextView) llTaskDetials.findViewById(R.id.tv_yesnoque);
		tv_camera 		= (TextView) llTaskDetials.findViewById(R.id.tv_camera);
		tv_video 		= (TextView) llTaskDetials.findViewById(R.id.tv_video);
		tv_numericques	= (TextView) llTaskDetials.findViewById(R.id.tv_numericques);
		llsmallques 	= (LinearLayout) llTaskDetials.findViewById(R.id.llsmallques);
		llbigques 	= (LinearLayout) llTaskDetials.findViewById(R.id.llbigques);
		llcheckbox 	= (LinearLayout) llTaskDetials.findViewById(R.id.llcheckbox);
		llradiobtn 	= (LinearLayout) llTaskDetials.findViewById(R.id.llradiobtn);
		llyesorno 	= (LinearLayout) llTaskDetials.findViewById(R.id.llyesorno);
		llcamera 	= (LinearLayout) llTaskDetials.findViewById(R.id.llcamera);
		llvideo 	= (LinearLayout) llTaskDetials.findViewById(R.id.llvideo);
		llnumericques= (LinearLayout) llTaskDetials.findViewById(R.id.llnumericques);
		llcheckAns  = (LinearLayout) llTaskDetials.findViewById(R.id.llcheckAns);
		llradioAns  = (LinearLayout) llTaskDetials.findViewById(R.id.llradioAns);
		llEmmotions = (LinearLayout) llTaskDetials.findViewById(R.id.llEmmotions);
		et_smallinput = (EditText) llTaskDetials.findViewById(R.id.et_smallinput);
		et_numericinput= (EditText) llTaskDetials.findViewById(R.id.et_numericinput);
		iv_imagepic = (ImageView) llTaskDetials.findViewById(R.id.iv_imagepic);
		iv_yesno = (ImageView) llTaskDetials.findViewById(R.id.iv_yesno);
		et_biginput = (EditText) llTaskDetials.findViewById(R.id.et_biginput);
		btnsubmit  	 	= (Button) llTaskDetials.findViewById(R.id.btnsubmit);
		buttonplay = (ImageView) llTaskDetials.findViewById(R.id.buttonplay);
		video_view  = (VideoView) llTaskDetials.findViewById(R.id.video_view);
		rlVideo = (RelativeLayout) llTaskDetials.findViewById(R.id.rlVideo);
		iv1	= (ImageView) llTaskDetials.findViewById(R.id.iv1);
		iv2	= (ImageView) llTaskDetials.findViewById(R.id.iv2);
		iv3	= (ImageView) llTaskDetials.findViewById(R.id.iv3);
		iv4	= (ImageView) llTaskDetials.findViewById(R.id.iv4);
		iv5	= (ImageView) llTaskDetials.findViewById(R.id.iv5);
		
		ivRadio1	= (ImageView) llTaskDetials.findViewById(R.id.ivRadio1);
		ivRadio2	= (ImageView) llTaskDetials.findViewById(R.id.ivRadio2);
		ivRadio3	= (ImageView) llTaskDetials.findViewById(R.id.ivRadio3);
		ivRadio4	= (ImageView) llTaskDetials.findViewById(R.id.ivRadio4);
		ivRadio5	= (ImageView) llTaskDetials.findViewById(R.id.ivRadio5);
	}
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
			setResult(1000);
			finish();
		}
		else if(from.equalsIgnoreCase("served"))
		{
		}
	}
	@Override
	protected void onStart()
	{
		super.onStart();

	}
	@Override
	public void onStop()
	{
		super.onStop();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		 if (resultCode == RESULT_OK && requestCode == CAMERA_REQUESTCODE)
			{
				switch (resultCode) 
				{
					case 5000:
						if(data != null)
						{
//							projectDO.vecTicketDOs.get(mPosition).remainingCount = data.getExtras().getString("remainingCount");
						}
						break;
					case RESULT_CANCELED:
						Log.i("Camera", "User cancelled");
						break;
					case RESULT_OK:
						
						iv_imagepic.setVisibility(View.VISIBLE);
						AppConstants.imagePath = camera_imagepath;
						locationUtility  = new LocationUtility(ServeyDetailsActivity.this);
						
						locationUtility.getLocation(ServeyDetailsActivity.this);
//						iv_imagepic.seti
						
	//					String strTitle = taskToDoDO.TaskName;
//						projectDO.vecTicketDOs.get(mPosition).strImagePath = AppConstants.imagePath;
//						Intent in = new Intent(TaskDetailsActivity.this, CaptureShelfPhotoActivity.class);
//						in.putExtra("imagepath", AppConstants.imagePath);
//						in.putExtra("projectDO", projectDO);
//						in.putExtra("position", mPosition);
//						in.putExtra("isEdit", isEdit);
//						startActivity(in);
						
//						isEdit = false;
						break;
				}
				
			} 
		 	else if (resultCode == RESULT_OK && requestCode == ACTION_TAKE_VIDEO) 
			{
		 		if(data.getData() != null)
		 		{
		 			mVideoUri = data.getData();
		 			rlVideo.setVisibility(View.VISIBLE);
		 			DisplayMetrics displaymetrics = new DisplayMetrics();
		 			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		 			final int h = displaymetrics.heightPixels;
		 			final int w = displaymetrics.widthPixels;
		 			
		 			
		 			thumbnailuri = getRealPathFromURI1(ServeyDetailsActivity.this,mVideoUri);
		 			
//				 serveyListDO.VideoUrl = new UploadImage().uploadVideo(ServeyDetailsActivity.this, saveVideoPath(mVideoUri), ServiceURLs.assetservicerequest, true);
		 			
		 			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(thumbnailuri,
		 					MediaStore.Images.Thumbnails.MINI_KIND); 
		 			
		 			BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
		 			video_view.setBackgroundDrawable(bitmapDrawable); 
		 			
		 			
		 			video_view.setOnClickListener(new OnClickListener()
		 			{
		 				@Override
		 				public void onClick(View v) 
		 				{
		 					
		 					video_view.setLayoutParams(new RelativeLayout.LayoutParams(w,215));
		 					video_view.setBackgroundDrawable(null);
		 					video_view.setMediaController(new MediaController(ServeyDetailsActivity.this));       
		 					video_view.setVideoURI(mVideoUri);
		 					video_view.requestFocus();
		 					video_view.start();
		 				}
		 			});
		 			
		 			
		 			buttonplay.setOnClickListener(new OnClickListener() {
		 				
		 				@Override
		 				public void onClick(View v)
		 				{
		 					if(mVideoUri!=null && mVideoUri.toString().length()>0)
		 						video_view.performClick();
		 					buttonplay.setVisibility(View.INVISIBLE);
		 				}
		 			});
//				Intent intent = new Intent(TaskDetailsActivity.this, VideoRecordingActivity.class);
//				intent.putExtra("videouri", mVideoUri);
//				intent.putExtra("projectDO", projectDO);
//				intent.putExtra("position", mPosition);
//				startActivity(intent);
		 		}
			}
		 	else if (resultCode == RESULT_OK && requestCode == IMAGE_REQUESTCODE) 
			{
		 		iv_imagepic.setVisibility(View.VISIBLE);
		 		File selected_image = new File(getRealPathFromURI(data.getData()));

		 		AppConstants.imagePath = selected_image.getAbsolutePath();
		 		locationUtility  = new LocationUtility(ServeyDetailsActivity.this);
				
				locationUtility.getLocation(ServeyDetailsActivity.this);
//		 		projectDO.vecTicketDOs.get(mPosition).strImagePath = AppConstants.imagePath;
//		 		Intent in = new Intent(TaskDetailsActivity.this, CaptureShelfPhotoActivity.class);
//				in.putExtra("imagepath", AppConstants.imagePath);
//				in.putExtra("projectDO", projectDO);
//				in.putExtra("position", mPosition);
////				in.putExtra("isEdit", isEdit);
//				startActivity(in);
			}
				
			

	}
	
	private String getRealPathFromURI(Uri contentUri) 
	{
		String[] proj = { MediaStore.Video.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	public String getRealPathFromURI1(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}

	@Override
	public void gotLocation(Location loc) 
	{

		if(loc!=null)
		{
			lat	 = decimalFormat.format(loc.getLatitude());
			lang = decimalFormat.format(loc.getLongitude());
			
			
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			
			Log.e("Density ", metrics.density+"");
			
			AppConstants.DEVICE_DENSITY = metrics.density;
//			processImageAndUpload();
			
			runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					
						File f = new File(AppConstants.imagePath);
						
						Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
						
						bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
//						serveyListDO.ImageUrl = new UploadImage().uploadImage(ServeyDetailsActivity.this, BitmapsUtiles.saveVerifySignature(bitmapProcessed), ServiceURLs.assetservicerequest, true);
						if(bmp!=null && !bmp.isRecycled())
							bmp.recycle();
						iv_imagepic.setBackgroundResource(0);
						iv_imagepic.setImageBitmap(bitmapProcessed);
				}
			});
		}
	}
	
	public void processImageAndUpload()
	{
//		Log.d("loc", "lat: "+AppConstants.LATITUDE + "long: "+AppConstants.LONGITUDE);
		Bitmap bitmapThumbnail = null;
//		showLoder("Saving image...");
		
    	BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
	    File f = new File(AppConstants.imagePath);
	    Bitmap bmp = decodeFile(f);// BitmapFactory.decodeFile(f.getPath(), options);
	   
	    bmp = flip(bmp, getImageRotation(f.getPath()));
	    
//	    bitmapThumbnail = BitmapsUtiles.processBitmap(bmp);
	    
//	    File fl = new File(Environment.getExternalStorageDirectory()+"/thumnail_jkbap");
//	    if(!fl.exists())
//	    {
//	    	fl.mkdir();
//	    }
//	    try
//	    {
//	    	Matrix matrix = new Matrix();
////				    	 matrix.postRotate(90);
//	    	Bitmap bitmap =  Bitmap.createBitmap(bitmapThumbnail, 0, 0, bitmapThumbnail.getWidth(), bitmapThumbnail.getHeight(), matrix, true); 
//	    	
//	    	if(bitmapThumbnail!=null)
//	    		bitmapThumbnail.recycle();
//		    File f1 = new File(fl,new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg");
//		    FileOutputStream fos = new FileOutputStream(f1);
//		    bitmap.compress(CompressFormat.JPEG, 90,fos);
//		    
////			 String path = Preferences.getPrefStringData(MainScreen.this, Preferences.IMAGE_PATH);;
//			 if(imagepath.length()==0)
//				 imagepath=f1.getAbsolutePath();
//			 else
//				 imagepath+="||"+f1.getAbsolutePath();
////			 Preferences.storeData(MainScreen.this, Preferences.IMAGE_PATH, path);
//			 
//			 if(bitmap!=null)
//				 bitmap.recycle();
//	    }
//	    catch(Exception e)
//	    {
//	    	e.printStackTrace();
//	    }
	    
	    
	    if(bmp!=null && ! bmp.isRecycled())
		 {
			WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bmp);
			if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
			{
				weakBitmap.get().recycle();
			}
		 }
		
    	try 
	    {
	    	DropBoxUploader dropboxUploader = new DropBoxUploader("");
	    	new Thread(dropboxUploader).start();
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
//		hideLoder();
	}
	
	class DropBoxUploader implements Runnable
	{
		private String imageName;
		
		public DropBoxUploader(String imageName)
		{
			this.imageName = imageName;
		}
		
		@Override
		public void run() 
		{
			try
			{
				
				File f = new File(AppConstants.imagePath);
	   	        Bitmap bmp = decodeFile(f);//BitmapFactory.decodeFile(f.getPath());
	   	        
	   	        bmp = flip(bmp, getImageRotation(f.getPath()));
	   	        
	   	        bmp = BitmapsUtiles.getResizedBmp(bmp, 720, 1280);
	   	        
			    Bitmap bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
			    if(bmp!=null && !bmp.isRecycled())
			    	bmp.recycle();
//			    String str = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
////			    
//			    File file = new File(Environment.getExternalStorageDirectory(),AppConstants.APPMEDIAFOLDERNAME); 
//			    if(!file.exists())
//			    {
//					file.mkdir();
//				}
//				file = new File(file,str); 
////				
//		    	FileOutputStream fos = new FileOutputStream(file); 
//		    	bitmapProcessed.compress(CompressFormat.JPEG, 90 /*ignored for PNG*/, fos); 
//		    	fos.close();
//		    	String strToken  =" ";
		    	
//		    	 String path = Preferences.getPrefStringData(MainScreen.this, Preferences.FULL_IMAGE_PATH);;
//				 if(imagepath.length()==0)
//					 imagepath=file.getAbsolutePath();
//				 else
//					 imagepath+="||"+file.getAbsolutePath();
//				 Preferences.storeData(MainScreen.this, Preferences.FULL_IMAGE_PATH, path);
				 
//		    	if(isNetworkAvailable())
//		    		strToken =  UploadImage.uploadImage(file.getAbsolutePath());
//		    	hideLoder();
//		    	String imageToken = Preferences.getPrefStringData(MainScreen.this, Preferences.IMAGE_TOKEN);
//		    	if(imageToken.length()>0)
//		    	{
//		    		imageToken+="||"+strToken;
//		    	}
//		    	else
//		    	{
//		    		imageToken = strToken;
//		    	}
//		    	Log.e("imageToken",""+imageToken);
//		    	Preferences.storeData(MainScreen.this, Preferences.IMAGE_TOKEN, imageToken);
		    	
		    	 if(bitmapProcessed!=null && ! bitmapProcessed.isRecycled())
				 {
					WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bitmapProcessed);
					if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
					{
						weakBitmap.get().recycle();
					}
				 }
		    	 
//		    	 runOnUiThread(new Runnable()
//		    	 {
//					@Override
//					public void run() 
//					{
//						getVectorToRefressList();
//					}
//				});
		    	 
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private Bitmap decodeFile(File f)
	{
	    try 
	    {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=90;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        	Log.e("outWidth", ""+o.outWidth);
	        	Log.e("scale", ""+scale);
//	        	Log.e("devicewidth", ""+AppConstants.DEVICE_WIDTH);
	        	
	        	if(AppConstants.DEVICE_WIDTH == 0)
	        	{
	        		Display display = ((WindowManager)ServeyDetailsActivity.this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	        		AppConstants.DEVICE_WIDTH 	= display.getWidth();
	        		AppConstants.DEVICE_HEIGHT 	= display.getHeight();
//		    		Log.e("changed devicewidth", ""+AppConstants.DEVICE_WIDTH);
	        	}
		        	while(o.outWidth/scale/2>=AppConstants.DEVICE_WIDTH /*&& o.outHeight/scale/2>=AppConstants.DEVICE_HEIGHT*/)
			            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	public int getImageRotation(String ImagePath)
	{
		
		  int rotate = 0;
          try 
          {
//              getContentResolver().notifyChange(imageUri, null);
              File imageFile = new File(ImagePath);
              ExifInterface exif = new ExifInterface(
                      imageFile.getAbsolutePath());
              int orientation = exif.getAttributeInt(
                      ExifInterface.TAG_ORIENTATION,
                      ExifInterface.ORIENTATION_NORMAL);

              switch (orientation) 
              {
	              case ExifInterface.ORIENTATION_ROTATE_270:
	                  rotate = 270;
	                  break;
	              case ExifInterface.ORIENTATION_ROTATE_180:
	                  rotate = 180;
	                  break;
	              case ExifInterface.ORIENTATION_ROTATE_90:
	                  rotate = 90;
	                  break;
              }
              Log.v("TAG", "Exif orientation: " + orientation);
          } 
          catch (Exception e) 
          {
              e.printStackTrace();
          }
          Log.v("TAG", "rotation " + rotate);
          return rotate;

	}
	
	Bitmap flip(Bitmap d, int rotate)
	{
	    Matrix m = new Matrix();
	    m.postRotate(rotate);
	    Bitmap src = d;
	    Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
//	    dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
	    return dst;
	}
	
	private String UnselctAllView(LinearLayout gvAnswer, View v, int temcount, ServeyQuestionsDO serveyQuestionsDO) 
	{
		for (int j = 0; j < gvAnswer.getChildCount(); j++) 
		{
			LinearLayout llView = ((LinearLayout) gvAnswer.getChildAt(j));
			for (int i = 0; i < llView.getChildCount(); i++) 
			{
				LinearLayout llOpt = (LinearLayout) llView.getChildAt(i);
				ImageView ivAns 	= (ImageView) llOpt.getChildAt(0);
				if(llOpt==v)
				{
					ivAns.setImageResource(R.drawable.radiobtnchecked);
					if(serveyQuestionsDO.vecOptionsDOs.size() > (2*j)+i)
						serveyQuestionsDO.vecOptionsDOs.get((2*j)+i).isSelected = true;
				}
				else
				{
					ivAns.setImageResource(R.drawable.radiobtn);
					if(serveyQuestionsDO.vecOptionsDOs.size() > (2*j)+i)
						serveyQuestionsDO.vecOptionsDOs.get((2*j)+i).isSelected = false;
				}
			}
		}
		return serveyQuestionsDO.strAnswer;
	}
	
	private void UnselctAllView1(LinearLayout gvAnswer,View v, int count, ServeyOptionsDO serveyOptionsDO) 
	{
		boolean isChecked = false;
		ImageView ivAns 	= (ImageView) ((LinearLayout) v).getChildAt(0);
		
		if(v.getTag()!=null)
			isChecked = (Boolean) v.getTag();
		
		if(!isChecked)
		{
			 ivAns.setImageResource(R.drawable.checked1);
			 v.setTag(true);
			 
			 serveyOptionsDO.isSelected = true;
		}
		else
		{
			ivAns.setImageResource(R.drawable.unchecked1);
			v.setTag(false);
			
			 serveyOptionsDO.isSelected = false;
		}
	}

	private void UnselctAllView2(LinearLayout gvAnswer,View v) 
	{
		boolean isChecked = false;
		ImageView ivAns 	= (ImageView) ((LinearLayout) v).getChildAt(0);
		
		if(v.getTag()!=null)
			isChecked = (Boolean) v.getTag();
		
		if(!isChecked)
		{
			 ivAns.setImageResource(R.drawable.agree);
			 v.setTag(true);
		}
		else
		{
			ivAns.setImageResource(R.drawable.disagree);
			v.setTag(false);
		}
	}
	
	private String serveyAnswerParameter(ServeyListDO serveyListDO)
	{
		String strParameter = "SurveyId:"+serveyListDO.SurveyId;
		int count1 = 0;
		int mCount =0;
		for(ServeyQuestionsDO serveyQuestionsDO : serveyListDO.vecQuestionsDOs)
		{
//			if(count1 > 0)
//				strParameter = strParameter +",";
			if(serveyQuestionsDO.vecOptionsDOs != null && serveyQuestionsDO.vecOptionsDOs.size()>0)
			{
				int count2 = 0;
				for (ServeyOptionsDO serveyOptionsDO : serveyQuestionsDO.vecOptionsDOs)
				{
					Log.d("SurveyQuestionId", "SurveyQuestionId"+serveyQuestionsDO.SurveyQuestionId);
					Log.i("SurveyQuestionId", "SurveyOptionId"+serveyOptionsDO.SurveyOptionId);
					if(serveyOptionsDO.isSelected)
					{
//						if(count2 > 0)
//							strParameter = strParameter +",";
						strParameter = strParameter+"&PostSurveyAnswers["+count2+"].SurveyQuestionId:"+serveyQuestionsDO.SurveyQuestionId+""+
						"&PostSurveyAnswers["+count2+"].SurveyOptionId:"+serveyOptionsDO.SurveyOptionId+""+
						"&PostSurveyAnswers["+count2+"].SurveyAnswer:"+"\"\""+"";
						count2++;
					}
					else if(!serveyQuestionsDO.AnswerType.equals("checkboxes") && !serveyQuestionsDO.AnswerType.equals("radiobutton"))
					{
						strParameter = strParameter+"&PostSurveyAnswers["+count1+"].SurveyQuestionId:"+serveyQuestionsDO.SurveyQuestionId+""+
						"&PostSurveyAnswers["+count1+"].SurveyOptionId:"+serveyOptionsDO.SurveyOptionId+""+
						"&PostSurveyAnswers["+count1+"].SurveyAnswer:"+serveyQuestionsDO.strAnswer+"";
					}
				}
			}
			else
			{
				strParameter = strParameter+"&PostSurveyAnswers["+count1+"].SurveyQuestionId:"+serveyQuestionsDO.SurveyQuestionId+""+
								"&PostSurveyAnswers["+count1+"].SurveyOptionId:"+"\"\""+""+
								"&PostSurveyAnswers["+count1+"].SurveyAnswer:"+serveyQuestionsDO.strAnswer+"";
			}
			count1++;
			mCount++;
		}
//		strParameter.replaceFirst(strParameter.charAt(strParameter.length()-1)+"", "");
		strParameter = strParameter+"";
		return strParameter;
	}
}
