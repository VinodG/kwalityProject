package com.winit.sfa.salesman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.salesman.adapter.CatalogBrandsAdapter;
import com.winit.alseer.salesman.adapter.CompetetorInfoGalleryAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Gallery_Dialog;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.PlayVideoView;
import com.winit.alseer.salesman.dataaccesslayer.BrandsDL;
import com.winit.alseer.salesman.dataaccesslayer.CompetitorDL;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.CompetitorItemDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.UploadImage;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class CompetitorInfoMerchandiserActivity  extends BaseActivity implements LocationResult
{
	private LinearLayout llCompetitorInfoView,llMedia,llAudiButtons;
	private EditText edtItem_name,edtPrice,edtNotes,etCompanyName,etBrandNameCompetitor,edtTypeofPromotion;
	private TextView tvTitle,tvBrandNameOur,tvCompanyName,tvBrandNameCompetitor;
	private ImageView btnPlay,ivCompanyName,ivBrandNameCompetitor;
	private PlayVideoView vVCaptureVedio;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final int CAMERA_CAPTURE_AUDIO_REQUEST_CODE = 300;
	private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 2200;
	private CompetitorItemDO competitorItemDO;
	private PopupWindow popupWindowCategory,popupWindowBrands;
	private String currentDateAndTime;
	private String lat = "", lang = "";
	private Bitmap mBtBitmap = null;
	private Bitmap bitmapProcessed;
	private Vector<BrandDO> vecBrandDOs;
	private Vector<CompetitorItemDO> vecComCompany;
	private CatalogBrandsAdapter brandsAdapter;
	private String camera_imagepath = "",audio_filepath="",video_filepaht;
	private Uri mVideoUri;
	private FrameLayout flVideo;
	public static final String IMAGE_CACHE_DIR = "thumbs";
	private int mImageThumbSize;
	private PopupWindow popupWindow ;
	private JourneyPlanDO journeyPlanDO;
//	private LinearLayout llSubmitNew;
	private Button btnSubmitNew,btnCaptureMedia;
	private MediaRecorder myRecorder;
	final Vector<CompetitorItemDO> vecCompetitorItemDO = new Vector<CompetitorItemDO>();
	public AlertDialog alertDialog;
	
	private TextView tvTime,tvSurveyName;
	private CountDownTimer cdt;
	 private MediaPlayer myPlayer;
	 private ImageView stopBtn,startBtn,playbtn;
	private long startTime = 0L;

	private Handler customHandler = new Handler();
	private String outputFile = null;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	long totalTime = 0L;
	long remaingTime = 0L;
	private String thumbnailuri;
	private Vector<DamageImageDO> vecAddCustomerFilesDO;
	
//UncaughtExceptionHandler
	@Override
	public void initialize() 
	{
		llCompetitorInfoView = (LinearLayout) inflater.inflate(R.layout.competitor_info, null);
		llBody.addView(llCompetitorInfoView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		vecAddCustomerFilesDO = new Vector<DamageImageDO>();
		if(getIntent().getExtras() != null)
		{
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("Object");
		}
		initializeControls();
		bindingControls();
		getBrands();
		loadData();
		
		tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSubmitNew.setTypeface(Typeface.DEFAULT_BOLD);
	}

	private void initializeControls() 
	{
		//		ivAppTitle.setVisibility(View.GONE);
		//		tvModuleTitle.setText("Competitor Activities");
		tvTitle 				= (TextView) llCompetitorInfoView.findViewById(R.id.tvTitle);
		tvBrandNameOur 			= (TextView) llCompetitorInfoView.findViewById(R.id.tvBrandNameOur);
		tvCompanyName 			= (TextView) llCompetitorInfoView.findViewById(R.id.tvCompanyName);
		tvBrandNameCompetitor 	= (TextView) llCompetitorInfoView.findViewById(R.id.tvBrandNameCompetitor);
		edtItem_name 			= (EditText) llCompetitorInfoView.findViewById(R.id.edtItem_name);
		edtPrice 				= (EditText) llCompetitorInfoView.findViewById(R.id.edtPrice);
		edtNotes 				= (EditText) llCompetitorInfoView.findViewById(R.id.edtNotes);
		etCompanyName			= (EditText) llCompetitorInfoView.findViewById(R.id.etCompanyName);
		etBrandNameCompetitor	= (EditText) llCompetitorInfoView.findViewById(R.id.etBrandNameCompetitor);
		edtTypeofPromotion		= (EditText) llCompetitorInfoView.findViewById(R.id.edtTypeofPromotion);
//		ivCaptureImg 			= (ImageView) llCompetitorInfoView.findViewById(R.id.ivCaptureImg);
		ivCompanyName 			= (ImageView) llCompetitorInfoView.findViewById(R.id.ivCompanyName);
		ivBrandNameCompetitor	= (ImageView) llCompetitorInfoView.findViewById(R.id.ivBrandNameCompetitor);
		llMedia					= (LinearLayout) llCompetitorInfoView.findViewById(R.id.llMedia);
		llAudiButtons			= (LinearLayout) llCompetitorInfoView.findViewById(R.id.llAudiButtons);
		vVCaptureVedio 			= (PlayVideoView) llCompetitorInfoView.findViewById(R.id.vVCaptureVedio);
		flVideo 				= (FrameLayout) llCompetitorInfoView.findViewById(R.id.flVideo);
		btnPlay					= (ImageView) llCompetitorInfoView.findViewById(R.id.btnPlay);
		btnCaptureMedia			= (Button) llCompetitorInfoView.findViewById(R.id.btnCaptureMedia);
		 stopBtn = (ImageView)llCompetitorInfoView.findViewById(R.id.stop);
		 playbtn = (ImageView)llCompetitorInfoView.findViewById(R.id.play);
		 tvTime= (TextView)llCompetitorInfoView.findViewById(R.id.tvTime);
//		llSubmitNew				= (LinearLayout) llCompetitorInfoView.findViewById(R.id.llSubmitNew);
		btnSubmitNew			= (Button) llCompetitorInfoView.findViewById(R.id.btnSubmitNew);

		setTypeFaceRobotoBold(llCompetitorInfoView);
	}

	private void bindingControls()
	{
		tvBrandNameOur.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
//				popupWindowBrandOur(vecBrandDOs);
//				final Vector<String> vecBrandStrings = new Vector<String>();
//				
//				for (int i = 0; i < vecBrandDOs.size(); i++) 
//				{
//					vecBrandStrings.add(vecBrandDOs.get(i).brandName);
//				}
				
				CustomBuilder builder = new CustomBuilder(CompetitorInfoMerchandiserActivity.this, "Select Brand", true);
				builder.setSingleChoiceItems(vecBrandDOs, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						if(selectedObject != null && selectedObject instanceof BrandDO)
						{
							BrandDO brandDO = (BrandDO)selectedObject;
							builder.dismiss();
							((TextView)v).setText(brandDO.brandName);
						}
				    }
				}); 
				builder.show();
				
//				ListView listViewStores = new ListView(CompetitorInfoMerchandiserActivity.this);
//				popupWindow = new PopupWindow(CompetitorInfoMerchandiserActivity.this);
//				popupWindow.setFocusable(true);
//				popupWindow.setWidth(tvBrandNameOur.getWidth());
//				popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//				popupWindow.setContentView(listViewStores);
//				popupWindow.showAsDropDown(v, 0, 9);
//				
//				listViewStores.setAdapter(new AssetsBrandAdapter(CompetitorInfoMerchandiserActivity.this,vecBrandStrings));
//				
//				listViewStores.setOnItemClickListener(new OnItemClickListener() 
//				{
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//							long arg3) 
//					{
//						tvBrandNameOur.setText(vecBrandStrings.get(position).toString());
//						popupWindow.dismiss();
//					}
//				});
			}
		});
		
		btnCaptureMedia.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{

				showCapturePopup();
			}
		});
		tvCompanyName.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				if(!isOtherComapanyContains())
				{
					if(!vecComCompany.contains(addOther()))
						vecComCompany.add(addOther());
				}
				
				CustomBuilder builder = new CustomBuilder(CompetitorInfoMerchandiserActivity.this, "Select Company", true);
				builder.setSingleChoiceItems(vecComCompany, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						if(selectedObject != null && selectedObject instanceof CompetitorItemDO)
						{
							CompetitorItemDO competitorItemDO = (CompetitorItemDO)selectedObject;
							builder.dismiss();
							((TextView)v).setText(competitorItemDO.company);
							
							if(tvCompanyName.getText().toString().equalsIgnoreCase("Others"))
							{
								etCompanyName.setVisibility(View.VISIBLE);
								ivCompanyName.setVisibility(View.VISIBLE);
							}
							else
							{
								etCompanyName.setVisibility(View.GONE);
								ivCompanyName.setVisibility(View.GONE);
							}
						}
				    }
				}); 
				builder.show();
				
//				ListView listViewStores = new ListView(CompetitorInfoMerchandiserActivity.this);
//				popupWindow = new PopupWindow(CompetitorInfoMerchandiserActivity.this);
//				popupWindow.setFocusable(true);
//				popupWindow.setWidth(tvCompanyName.getWidth());
//				popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//				popupWindow.setContentView(listViewStores);
//				popupWindow.showAsDropDown(v, 0, 9);
//				
//				listViewStores.setAdapter(new AssetsBrandAdapter(CompetitorInfoMerchandiserActivity.this,vecComCompany));
//				
//				listViewStores.setOnItemClickListener(new OnItemClickListener() 
//				{
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//							long arg3) 
//					{
//						tvCompanyName.setText(vecComCompany.get(position).toString());
//						
//						if(tvCompanyName.getText().toString().equalsIgnoreCase("Others"))
//						{
//							etCompanyName.setVisibility(View.VISIBLE);
//							ivCompanyName.setVisibility(View.VISIBLE);
//						}
//						else
//						{
//							etCompanyName.setVisibility(View.GONE);
//							ivCompanyName.setVisibility(View.GONE);
//						}
//						popupWindow.dismiss();
//					}
//				});
			}
		});
		
		tvBrandNameCompetitor.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				if(!isOtherBrandContains())
				{
					if(!vecComCompany.contains(addOther()))
						vecComCompany.add(addOther());
				}
				
				CustomBuilder builder = new CustomBuilder(CompetitorInfoMerchandiserActivity.this, "Select Brand", true);
				builder.setSingleChoiceItems(vecComCompany, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						if(selectedObject != null && selectedObject instanceof CompetitorItemDO)
						{
							CompetitorItemDO competitorItemDO = (CompetitorItemDO)selectedObject;
							builder.dismiss();
							((TextView)v).setText(competitorItemDO.brandname);
							
							if(tvBrandNameCompetitor.getText().toString().equalsIgnoreCase("Others"))
							{
								etBrandNameCompetitor.setVisibility(View.VISIBLE);
								ivBrandNameCompetitor.setVisibility(View.VISIBLE);
							}
							else
							{
								etBrandNameCompetitor.setVisibility(View.GONE);
								ivBrandNameCompetitor.setVisibility(View.GONE);
							}
						}
				    }
				}); 
				builder.show();
				
//				ListView listViewStores = new ListView(CompetitorInfoMerchandiserActivity.this);
//				popupWindow = new PopupWindow(CompetitorInfoMerchandiserActivity.this);
//				popupWindow.setFocusable(true);
//				popupWindow.setWidth(tvBrandNameCompetitor.getWidth());
//				popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
//				popupWindow.setContentView(listViewStores);
//				popupWindow.showAsDropDown(v, 0, 9);
//				
//				listViewStores.setAdapter(new AssetsBrandAdapter(CompetitorInfoMerchandiserActivity.this,vecComBrands));
//				
//				listViewStores.setOnItemClickListener(new OnItemClickListener() 
//				{
//					@Override
//					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//							long arg3) 
//					{
//						tvBrandNameCompetitor.setText(vecComBrands.get(position).toString());
//						
//						if(tvBrandNameCompetitor.getText().toString().equalsIgnoreCase("Others"))
//						{
//							etBrandNameCompetitor.setVisibility(View.VISIBLE);
//							ivBrandNameCompetitor.setVisibility(View.VISIBLE);
//						}
//						else
//						{
//							etBrandNameCompetitor.setVisibility(View.GONE);
//							ivBrandNameCompetitor.setVisibility(View.GONE);
//						}
//						popupWindow.dismiss();
//					}
//				});
			}
		});

		btnSubmitNew.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) 
			{
//				if(tvCompanyName.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(), getString(R.string.warning), "Please enter company.", getString(R.string.OK), "", "");
//				}
//				else if(tvBrandNameCompetitor.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(),getString(R.string.warning), "Please enter brand name.", getString(R.string.OK), "", "");
//				}
//				else if(edtItem_name.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(),getString(R.string.warning), "Please enter item name.", getString(R.string.OK), "", "");
//				}
//				else if(edtTypeofPromotion.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(),getString(R.string.warning), "Please enter Promotion type.", getString(R.string.OK), "", "");
//				}
//				else if(edtPrice.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(),getString(R.string.warning), "Please enter price.", getString(R.string.OK), "", "");
//				}
				
//				if(edtNotes.getText().toString().equalsIgnoreCase(""))
//				{
//					showCustomDialog(getApplicationContext(), getString(R.string.warning), "Please enter notes.", getString(R.string.OK), "", "");
//				}
				if(etBrandNameCompetitor.getText().toString().equalsIgnoreCase(""))
				{
					showCustomDialog(getApplicationContext(), getString(R.string.warning), "Please enter Brands.", getString(R.string.OK), "", "");
				}
				else if(llMedia == null || llMedia.getChildCount() < 1)
				{
					showCustomDialog(getApplicationContext(), getString(R.string.warning), "Please take atleast one image/video.", getString(R.string.OK), "", "");
				}
				else
				{
					Random random = new Random();

					currentDateAndTime = CalendarUtils.getCurrentDateTime();
					competitorItemDO = new CompetitorItemDO();
					competitorItemDO.competetor_id = StringUtils.getString(random.nextInt(1234));
					competitorItemDO.activity = edtNotes.getText().toString();
					
					if(journeyPlanDO != null)
						competitorItemDO.store = journeyPlanDO.siteName;
					else
						competitorItemDO.store = "";
					
					competitorItemDO.promotiontype = edtTypeofPromotion.getText().toString();
					competitorItemDO.ourBrand = tvBrandNameOur.getText().toString();
					
//					if(tvBrandNameCompetitor.getText().toString().equalsIgnoreCase("Others"))
						competitorItemDO.brandname = etBrandNameCompetitor.getText().toString();
//					else
//						competitorItemDO.brandname = tvBrandNameCompetitor.getText().toString();
					
					competitorItemDO.category = tvBrandNameCompetitor.getText().toString();
					
					if(tvCompanyName.getText().toString().equalsIgnoreCase("Others"))
						competitorItemDO.company = etCompanyName.getText().toString();
					else
						competitorItemDO.company = tvCompanyName.getText().toString();
					
					competitorItemDO.createdon = CalendarUtils.getCurrentDateTime();
					competitorItemDO.item_name = edtItem_name.getText().toString();

					if(mBtBitmap != null)
						competitorItemDO.imagepath	  = getBsae64Image(mBtBitmap);
					else
						competitorItemDO.imagepath = camera_imagepath;

					competitorItemDO.price = edtPrice.getText().toString();
					if(competitorItemDO.price.contains(AppConstants.CURRECNY_CODE))
						competitorItemDO.price = competitorItemDO.price.replace(AppConstants.CURRECNY_CODE, "").trim();

					competitorItemDO.createdon = CalendarUtils.getCurrentDateTime();
					
					

					competitorItemDO.price = /*AppConstants.AED+" "+*/edtPrice.getText().toString();
					competitorItemDO.imagepath = camera_imagepath;
//					competitorItemDO.createdon = currentDateAndTime;
//					vecCompetitorItemDO.add(competitorItemDO);
					new Thread(new Runnable() 
					{
						public void run() 
						{
							if(camera_imagepath != null && !camera_imagepath.equalsIgnoreCase(""))
							{
								competitorItemDO.imageUploadpath = new UploadImage().uploadImage(CompetitorInfoMerchandiserActivity.this, camera_imagepath, "Competitor", true);
							}
							else if(mVideoUri != null)
							{
								competitorItemDO.imagepath = getRealPathFromURI(CompetitorInfoMerchandiserActivity.this, mVideoUri);
								competitorItemDO.imageUploadpath = new UploadImage().uploadImage(CompetitorInfoMerchandiserActivity.this, competitorItemDO.mvideoUri, "Competitor", true);
							}
							vecCompetitorItemDO.add(competitorItemDO);
							ConnectionHelper connectionHelper = new ConnectionHelper(null);
							connectionHelper.sendRequest(getApplicationContext(), BuildXMLRequest.postCompetitor(vecCompetitorItemDO), ServiceURLs.InsertCompetitor);
						}
					}).start();

					new CompetitorDL().insertCompetitor(competitorItemDO);
					finish();
				}
			}
		});

//		ivCaptureImg.setOnClickListener(new OnClickListener() 
//		{
//
//			@Override
//			public void onClick(View v)
//			{
////				showCapturePopup();
//			}
//		});

		flVideo.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v)
			{
//				showCapturePopup();
			}
		});

		btnPlay.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				btnPlay.setVisibility(View.INVISIBLE);

				vVCaptureVedio.setBackgroundDrawable(null);
				vVCaptureVedio.setMediaController(new MediaController(CompetitorInfoMerchandiserActivity.this));       
				vVCaptureVedio.setVideoURI(mVideoUri);
				vVCaptureVedio.requestFocus();
				vVCaptureVedio.start();
			}
		});

		// video finish listener
		vVCaptureVedio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) 
			{
				btnPlay.setVisibility(View.VISIBLE);
			}
		});
		
		llAudiButtons.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{

//				showCapturePopup();
			}
		});
	}
	
	private void loadData()
	{
//		vecComBrands = new CompetitorDL().getAllCompetitorBrands();
//		vecComCompany = new CompetitorDL().getAllCompetitorCompanys();
		
		vecComCompany = new CompetitorDL().getAllCompetitorBrandDO();
	}

	private void getBrands()
	{
		showLoader("");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecBrandDOs = new BrandsDL().getAllBrands();
				hideLoader();
			}
		}).start();
	}

	private Dialog popupWindowBrandOur(final Vector<BrandDO> vecBrandDOs) 
	{

		final Dialog popupWindow =  new Dialog(CompetitorInfoMerchandiserActivity.this,android.R.style.Theme_Holo_Dialog);
		View brandsPopUp 		 =  LayoutInflater.from(CompetitorInfoMerchandiserActivity.this).inflate(R.layout.brands_popup, null);
		GridView gvBrand 		 =  (GridView) brandsPopUp.findViewById(R.id.gvBrandPopup);
		ImageView ivBrandPopup   =  (ImageView)brandsPopUp.findViewById(R.id.ivBrandPopup);
		brandsAdapter   		 =  new CatalogBrandsAdapter(CompetitorInfoMerchandiserActivity.this,vecBrandDOs);
		gvBrand.setAdapter(brandsAdapter);

		gvBrand.setNumColumns(4);
		gvBrand.setHorizontalSpacing(20);
		gvBrand.setVerticalSpacing(20);
		popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		LayoutParams layoutParams   = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		popupWindow.setContentView(brandsPopUp,layoutParams);
		//		popupWindow.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		if(popupWindow != null && !popupWindow.isShowing())
			popupWindow.show();

		ivBrandPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				if(popupWindow != null && popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});

		gvBrand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				tvBrandNameOur.setText(vecBrandDOs.get(position).brandName);
				popupWindow.dismiss();
			}
		});
		//        loadBrandsData();
		return popupWindow;
	}
	
	private Dialog popupWindowCompany(final Vector<BrandDO> vecBrandDOs) 
	{

		final Dialog popupWindow =  new Dialog(CompetitorInfoMerchandiserActivity.this,android.R.style.Theme_Holo_Dialog);
		View brandsPopUp 		 =  LayoutInflater.from(CompetitorInfoMerchandiserActivity.this).inflate(R.layout.brands_popup, null);
		GridView gvBrand 		 =  (GridView) brandsPopUp.findViewById(R.id.gvBrandPopup);
		ImageView ivBrandPopup   =  (ImageView)brandsPopUp.findViewById(R.id.ivBrandPopup);
		brandsAdapter   		 =  new CatalogBrandsAdapter(CompetitorInfoMerchandiserActivity.this,vecBrandDOs);
		gvBrand.setAdapter(brandsAdapter);

		gvBrand.setNumColumns(4);
		gvBrand.setHorizontalSpacing(20);
		gvBrand.setVerticalSpacing(20);
		popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		LayoutParams layoutParams   = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		popupWindow.setContentView(brandsPopUp,layoutParams);
		//		popupWindow.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		if(popupWindow != null && !popupWindow.isShowing())
			popupWindow.show();

		ivBrandPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				if(popupWindow != null && popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});

		gvBrand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				tvCompanyName.setText(vecBrandDOs.get(position).brandName);
				popupWindow.dismiss();
			}
		});
		//        loadBrandsData();
		return popupWindow;
	}
	
	private Dialog popupWindowBrandCompetitor(final Vector<BrandDO> vecBrandDOs) 
	{

		final Dialog popupWindow =  new Dialog(CompetitorInfoMerchandiserActivity.this,android.R.style.Theme_Holo_Dialog);
		View brandsPopUp 		 =  LayoutInflater.from(CompetitorInfoMerchandiserActivity.this).inflate(R.layout.brands_popup, null);
		GridView gvBrand 		 =  (GridView) brandsPopUp.findViewById(R.id.gvBrandPopup);
		ImageView ivBrandPopup   =  (ImageView)brandsPopUp.findViewById(R.id.ivBrandPopup);
		brandsAdapter   		 =  new CatalogBrandsAdapter(CompetitorInfoMerchandiserActivity.this,vecBrandDOs);
		gvBrand.setAdapter(brandsAdapter);

		gvBrand.setNumColumns(4);
		gvBrand.setHorizontalSpacing(20);
		gvBrand.setVerticalSpacing(20);
		popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
		popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		LayoutParams layoutParams   = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		popupWindow.setContentView(brandsPopUp,layoutParams);
		//		popupWindow.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		if(popupWindow != null && !popupWindow.isShowing())
			popupWindow.show();

		ivBrandPopup.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				if(popupWindow != null && popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});

		gvBrand.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				tvBrandNameCompetitor.setText(vecBrandDOs.get(position).brandName);
				popupWindow.dismiss();
			}
		});
		//        loadBrandsData();
		return popupWindow;
	}

	private void showCapturePopup()
	{
		if (alertDialog != null && alertDialog.isShowing())
			alertDialog.dismiss();

		alertBuilder = new AlertDialog.Builder(CompetitorInfoMerchandiserActivity.this);
		alertBuilder.setCancelable(true);

		final LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.capture_popup, null);

		LinearLayout llImage = (LinearLayout)ll.findViewById(R.id.llImage);
		LinearLayout llVideo  = (LinearLayout)ll.findViewById(R.id.llVideo);
		LinearLayout llAudio  = (LinearLayout)ll.findViewById(R.id.llAudio);

		llAudio.setVisibility(View.GONE);
		llImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				captureImage();
			}
		});

		llVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();	
				recordVideo();
			}
		});

		llAudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if (alertDialog != null && alertDialog.isShowing())
					alertDialog.dismiss();
				captureAudio();
			}
		});

		try
		{
			alertDialog = alertBuilder.create();
			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.setView(ll,0,0,0,0);
			alertDialog.setInverseBackgroundForced(true);
			alertDialog.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		setTypeFaceRobotoNormal(ll);
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
	
					flVideo.setVisibility(View.GONE);
	
					File f = new File(camera_imagepath);
					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
					Bitmap bitmapProcessed = getBitMap(bmp,camera_imagepath);
					mBtBitmap = bitmapProcessed;
					
					addFilesToLayout(bitmapProcessed, "Image", camera_imagepath);
					 llMedia.setVisibility(View.VISIBLE);
//					ivCaptureImg.setBackgroundResource(0);
//					ivCaptureImg.setImageBitmap(bitmapProcessed);
					llAudiButtons.setVisibility(View.GONE);
					flVideo.setVisibility(View.GONE);
					break;
			}

		}
		else if (resultCode == RESULT_OK && requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) 
		{
			mVideoUri = data.getData();

			if(mVideoUri != null)
			{
				 String thumbnailuri = getRealPathFromURI(CompetitorInfoMerchandiserActivity.this,mVideoUri);
			     Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(thumbnailuri,MediaStore.Images.Thumbnails.MINI_KIND); 
//			     ivCaptureImg.setBackgroundResource(0);
//				 ivCaptureImg.setImageBitmap(thumbnail);
			     llMedia.setVisibility(View.VISIBLE);
			     llAudiButtons.setVisibility(View.GONE);
				 BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
//				 vVCaptureVedio.setBackgroundDrawable(bitmapDrawable); 
				 
				 addFilesToLayout(thumbnail, "Video", mVideoUri.toString());
				 
				flVideo.setVisibility(View.GONE);
			}
		}
		else if(requestCode == CAMERA_CAPTURE_AUDIO_REQUEST_CODE)
		{
			switch (resultCode) 
			{
			case RESULT_CANCELED:
				Log.i("Camera", "User cancelled");
				break;

			case RESULT_OK:

				if(data.hasExtra("QuestionsDO"))
				{
					SurveyQuestionNewDO questionsDO = (SurveyQuestionNewDO) data.getExtras().getSerializable("QuestionsDO");
					audio_filepath = questionsDO.Answer;
					
					outputFile = audio_filepath;
					flVideo.setVisibility(View.GONE);
					llMedia.setVisibility(View.GONE);
					llAudiButtons.setVisibility(View.VISIBLE);
					playbtn.setTag("true");
					playbtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(((String)v.getTag()).equalsIgnoreCase("true"))
							{
								v.setTag("false");
//								startBtn.setEnabled(false);
								stopBtn.setEnabled(true);
								playbtn.setEnabled(true);
								refreshViews();
								play(v);
							}
							else
							{
								v.setTag("true");
//								startBtn.setEnabled(false);
								stopBtn.setEnabled(true);
								playbtn.setEnabled(true);
								refreshViews();
								pause(v);
							}
							
						}
					});
					
					
					stopBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) 
						{

							stop(v);
//							startBtn.setEnabled(true);
						    stopBtn.setEnabled(false);
						    playbtn.setEnabled(true);
						    playbtn.setTag("true");
							refreshViews();
						}
					});
					
					
					
				}

				break;
			}

		}
	}

	private Bitmap getBitMap(Bitmap bmp, String camera_imagepath)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			bitmapProcessed = BitmapUtilsLatLang.processBitmap2(bmp, lat, lang, "");
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
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
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private static String getBsae64Image(Bitmap mBitmap)
	{

		ByteArrayOutputStream streams = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, streams);

		return Base64.encodeBytes(streams.toByteArray());
	}

	@Override
	public void gotLocation(Location loc) {
		if(loc!=null)
		{
			lat	 = StringUtils.getStringFromDouble(loc.getLatitude());
			lang = StringUtils.getStringFromDouble(loc.getLongitude());

			DisplayMetrics metrics = getResources().getDisplayMetrics();

			Log.e("Density ", metrics.density+"");

			AppConstants.DEVICE_DENSITY = metrics.density;

			runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					File f = new File(camera_imagepath);
					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
					bitmapProcessed = getBitMap(bmp, camera_imagepath);
					mBtBitmap = bitmapProcessed;
//					ivCaptureImg.setBackgroundResource(0);
//					ivCaptureImg.setImageBitmap(bitmapProcessed);
				}
			});
		}
	}

	public void recordVideo()
	{
		Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,mVideoUri);
		startActivityForResult(intent,CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
	}

	private void captureImage()
	{
		if(isDeviceSupportCamera())
		{
			File file    = FileUtils.getOutputImageFile("competitors");
			if(file!=null){
				camera_imagepath   = file.getAbsolutePath();
				Uri fileUri  = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				intent.putExtra("fileName",file.getName());
				intent.putExtra("filePath", file.getAbsolutePath());
				startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		}
		else
		{
			Toast.makeText(CompetitorInfoMerchandiserActivity.this,"Sorry Device not supported to camera", Toast.LENGTH_SHORT).show();
		}
	}
	private void captureAudio()
	{
		Intent intent = new Intent(CompetitorInfoMerchandiserActivity.this,MediaOptionsActivity.class);
		SurveyQuestionNewDO question = new SurveyQuestionNewDO();
		question.AnswerType="AUDIO";
		intent.putExtra("QuestionsDO", question);
		startActivityForResult(intent, CAMERA_CAPTURE_AUDIO_REQUEST_CODE);
	}
	
	@Override
	public void onBackPressed() {
		if(alertDialog != null && alertDialog.isShowing())
			alertDialog.dismiss();
		super.onBackPressed();
	}
	
	private CompetitorItemDO addOther()
	{
		CompetitorItemDO comItemDO = new CompetitorItemDO();
		comItemDO.company = "Others";
		comItemDO.brandname = "Others";
		
		return comItemDO;
	}
	
	private boolean isOtherComapanyContains()
	{
		int count = 0;
		
		if(vecComCompany != null && vecComCompany.size() > 0)
		{
			for (int i = 0; i < vecComCompany.size(); i++) 
			{
				CompetitorItemDO itemDO = vecComCompany.get(i);
				
				if(itemDO.company.equalsIgnoreCase("Others"))
				{
					count++;
				}
			}
		}
		
		if(count == 0)
			return false;
		else
			return true;
		
	}
	
	private boolean isOtherBrandContains()
	{
		int count = 0;
		
		if(vecComCompany != null && vecComCompany.size() > 0)
		{
			for (int i = 0; i < vecComCompany.size(); i++) 
			{
				CompetitorItemDO itemDO = vecComCompany.get(i);
				
				if(itemDO.brandname.equalsIgnoreCase("Others"))
				{
					count++;
				}
			}
		}
		
		if(count == 0)
			return false;
		else
			return true;
		
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
				

//				int secs = (int) (updatedTime / 1000);
//				int mins = secs / 60;
//				secs = secs % 60;
//				int milliseconds = (int) (updatedTime % 1000);
//				tvTime.setText("" + mins + ":"
//						+ String.format("%02d", secs) + ":"
//						+ String.format("%03d", milliseconds));
				
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
		
		public void refreshViews()
		{
			
			
//			if(startBtn.isEnabled())
//				startBtn.setBackgroundResource(R.drawable.rec);
//			else
//				startBtn.setBackgroundResource(R.drawable.rec_h);
			
			if(stopBtn.isEnabled())
				stopBtn.setBackgroundResource(R.drawable.stop);
			else
				stopBtn.setBackgroundResource(R.drawable.stop_h);
			
			if(playbtn.getTag().toString().equalsIgnoreCase("true"))
			{
//				if(!startBtn.isEnabled())
//					playbtn.setBackgroundResource(R.drawable.play_h);
//				else
					playbtn.setBackgroundResource(R.drawable.play);
			}
			else
				playbtn.setBackgroundResource(R.drawable.pause);
		}
		
		
		public void addFilesToLayout(final Bitmap bitmap,String FileType,String filePath)
		{
			
			RelativeLayout llImage = (RelativeLayout) inflater.inflate(R.layout.image_bg_cell_competetor, null);
			
			LinearLayout llImageLL = (LinearLayout)llImage.findViewById(R.id.llImageLL);
			
			
			
			ImageView ivCross = (ImageView)llImage.findViewById(R.id.ivCross);
			ImageView ivImage = (ImageView)llImage.findViewById(R.id.ivImage);
			ImageView btnPlay = (ImageView)llImage.findViewById(R.id.btnPlay);
			
			if(FileType.equalsIgnoreCase("Image"))
				btnPlay.setVisibility(View.GONE);
			else if(FileType.equalsIgnoreCase("Video"))
				btnPlay.setVisibility(View.VISIBLE);
			
			
			ivImage.setImageBitmap(bitmap);
			ivCross.setTag(llImage);
			
			
			DamageImageDO addCustomerFilesDO = new DamageImageDO();
			addCustomerFilesDO.ImagePath = filePath;
			addCustomerFilesDO.FileType = FileType;
			
			vecAddCustomerFilesDO.add(addCustomerFilesDO);
			ivCross.setTag(R.string.Van_qty,addCustomerFilesDO);
			ivCross.setOnClickListener(new OnClickListener() 
			{
				
				@Override
				public void onClick(View v) 
				{
					v.setTag(R.string.delete,"delete");
					showCustomDialog(CompetitorInfoMerchandiserActivity.this,getString(R.string.alert), "Are you sure you want to delete this file?", "Yes", "No", "DeleteImage");
					
					
				}
			});
			
			
			llImageLL.setTag(addCustomerFilesDO);
			llImageLL.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showDialogForGalleryImages(vecAddCustomerFilesDO, 0);
				}
			});
			
			setTypeFaceRobotoNormal(llImage);
			
			llMedia.addView(llImage);
		}
		
		private void removeSeletedItem()
		{
			if(llMedia!=null && llMedia.getChildCount()>0)
			{
				for(int i=0;i<llMedia.getChildCount();i++)
				{
					RelativeLayout llImage = (RelativeLayout) llMedia.getChildAt(i);
					LinearLayout llImageLL = (LinearLayout)llImage.findViewById(R.id.llImageLL);
					ImageView ivCross = (ImageView)llImage.findViewById(R.id.ivCross);
					if(ivCross.getTag(R.string.delete)!=null)
					{
						if(ivCross.getTag(R.string.delete).toString().equalsIgnoreCase("delete"))
						{
							llMedia.removeView(llImage);
							
							if(ivCross.getTag(R.string.Van_qty)!=null)
							{
								DamageImageDO addCustomerFilesDO= (DamageImageDO) ivCross.getTag(R.string.Van_qty);
								if(vecAddCustomerFilesDO!=null && vecAddCustomerFilesDO.size()>0 && addCustomerFilesDO!=null)
									vecAddCustomerFilesDO.remove(addCustomerFilesDO);
							}
							break;
						}
					}
				}
			}
		}
		@Override
		public void onButtonYesClick(String from)
		{
			
			if(from.equalsIgnoreCase("DeleteImage"))
				removeSeletedItem();
		}
		 public void showDialogForGalleryImages(final Vector<DamageImageDO> vecDamageImages,int position)
		  {
				final Gallery_Dialog objGallery_Dialog = new Gallery_Dialog(CompetitorInfoMerchandiserActivity.this);
				objGallery_Dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
				objGallery_Dialog.show();
				
				ViewPager pager = objGallery_Dialog.viewPager;
				final TextView tvCount = objGallery_Dialog.tvCount;
				
				CompetetorInfoGalleryAdapter adapter = new CompetetorInfoGalleryAdapter(CompetitorInfoMerchandiserActivity.this, vecDamageImages);
				
				pager.setAdapter(adapter);
				pager.setCurrentItem(position);
				StringBuilder sb = new StringBuilder();
				sb.append("(").append(1).append("/").append(vecDamageImages.size()).append(")");
				tvCount.setText(sb.toString());

				
				pager.setOnPageChangeListener(new OnPageChangeListener() 
				{
					
					@Override
					public void onPageSelected(int pos) 
					{
						StringBuilder sb = new StringBuilder();
						sb.append("(").append(pos+1).append("/").append(vecDamageImages.size()).append(")");
						tvCount.setText(sb.toString());
						
					}
					
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2)
					{
						
						
					}
					
					@Override
					public void onPageScrollStateChanged(int arg0)
					{
						
					}
				});
			  
		  }
}