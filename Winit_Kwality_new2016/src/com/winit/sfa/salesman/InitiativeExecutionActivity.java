package com.winit.sfa.salesman;

import httpimage.HttpImageManager;

import java.io.File;
import java.util.Vector;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.salesman.adapter.InitiativePagerAdapter;
import com.winit.alseer.salesman.adapter.InitiativesProductAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.BrandsDL;
import com.winit.alseer.salesman.dataaccesslayer.InitiativesDA;
import com.winit.alseer.salesman.dataaccesslayer.InitiativesTradePalnDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.alseer.salesman.dataobject.InitiativeProductsDO;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanImageDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.alseer.salesman.imageloader.UrlImageViewHelper;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.UploadImage;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class InitiativeExecutionActivity extends BaseActivity implements LocationResult
{
	private LinearLayout llPlanogram,llExecute,llSubmit,llInitiatives;
	private InitiativeDO initiativeDO;
	private ListView lvPlanogram;
	private ViewPager pagerInitiative;
	private Vector<InitiativeProductsDO> vecInitiativeProductsDOs;
	private InitiativesProductAdapter initiativesProductAdapter;
	private InitiativePagerAdapter initiativePagerAdapter;
	private Vector<ProductsDO> vecProductsDOs;
	private Vector<InitiativeTradePlanImageDO> vecExecutionDOs;
	private String camera_imagepath = "";
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private Bitmap bitmapProcessed;
	private Bitmap mBtBitmap = null;
	private String lat = "", lang = "";
	private PopupWindow popupWindowStores;
	private Dialog dialogDetails;
	private String replace ="%s/../";
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private UploadImage uploadImage;
    private String brandName,brandImage;
    private JourneyPlanDO journeyPlanDO;
	
	@Override
	public void initialize() 
	{
		llPlanogram = (LinearLayout)inflater.inflate(R.layout.planogram_execution, null);
		
		if(getIntent().hasExtra("InitiativeDo"))
		{
			initiativeDO = (InitiativeDO) getIntent().getExtras().getSerializable("InitiativeDo");
		}
		
		if(getIntent().hasExtra("Object"))
		{
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("Object");
		}
		
		llBody.addView(llPlanogram, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		initializeControls();
		loadData();
	}
	
	private void initializeControls()
	{
		llExecute = (LinearLayout)llPlanogram.findViewById(R.id.llExecute);
		lvPlanogram = (ListView)llPlanogram.findViewById(R.id.lvPlanogram);
		//pagerInitiative = (ViewPager)llPlanogram.findViewById(R.id.pagerInitiative); //by dalayya.
		llInitiatives = (LinearLayout)llPlanogram.findViewById(R.id.llPlanograms);
		llSubmit	= (LinearLayout)llPlanogram.findViewById(R.id.llSubmit);
		
		uploadImage = new UploadImage();
		
		vecProductsDOs = new Vector<ProductsDO>();
		initiativesProductAdapter = new InitiativesProductAdapter(InitiativeExecutionActivity.this, vecProductsDOs);
		lvPlanogram.setAdapter(initiativesProductAdapter);
		
		vecExecutionDOs = new Vector<InitiativeTradePlanImageDO>();
		
		llExecute.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				captureImage();
			}
		});
		
		llSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(new InitiativesDA().getInitiativesStatus(journeyPlanDO.site,initiativeDO.InitiativeId).equalsIgnoreCase("1"))
				{
					showCustomDialog(InitiativeExecutionActivity.this, "Success", "Initiative has been executed successfully.", "OK", "", "Initiative");
				}
			}
		});
		
//		if(initiativeDO.Status.equalsIgnoreCase("1"))
//			llExecute.setVisibility(View.GONE);
//		else
//			llExecute.setVisibility(View.VISIBLE);
		
		setTypeFaceRobotoNormal(llPlanogram);
		
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		
		if(from.equalsIgnoreCase("Initiative"))
		{
			finish();
		}
	}
	
	private void captureImage()
	{
		if(isDeviceSupportCamera())
		{
			File file    = FileUtils.getOutputImageFile("Initiatives");
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
		else
		{
			Toast.makeText(InitiativeExecutionActivity.this,"Sorry Device not supported to camera", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void loadData()
	{
		if(initiativeDO != null)
		{
			vecInitiativeProductsDOs = new InitiativesDA().getAllInitiativeProducts(initiativeDO.InitiativeId+"");
			 
			for (int i = 0; i < vecInitiativeProductsDOs.size(); i++) 
			{
				ProductsDO productsDO = new ProductsDA().getProductsDetailsByItemCode(vecInitiativeProductsDOs.get(i).ItemCode);
				
				if(productsDO != null)
					vecProductsDOs.add(productsDO);
			}
			
			if(vecProductsDOs != null && vecProductsDOs.size() > 0)
				initiativesProductAdapter.refreshAdapter(vecProductsDOs);
		}
		
		if(initiativeDO != null)
		{
			brandName = new BrandsDL().getBrandNamefromBrandID(initiativeDO.Brand);
			brandImage = new BrandsDL().getBrandNamefromBrandImage(initiativeDO.Brand);
			
			vecExecutionDOs = new InitiativesTradePalnDA().getAllInitiativesById(initiativeDO.InitiativeId+"",journeyPlanDO.site);
			
			infaltePlanogram(vecExecutionDOs);
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
	private void infaltePlanogram(Vector<InitiativeTradePlanImageDO> vecExecutionDOs)
	{
		if(vecExecutionDOs.size() > 0)
		{
			for (int i = 0; i < vecExecutionDOs.size()+1; i++) 
			{
				LinearLayout llPlano = (LinearLayout)inflater.inflate(R.layout.initiativeimage_cell, null);
				TextView tvTitle = (TextView)llPlano.findViewById(R.id.tvTitle);
				ImageView ivInitiativeImage = (ImageView)llPlano.findViewById(R.id.ivInitiativeImage);
				ImageView ivBrandImage = (ImageView)llPlano.findViewById(R.id.ivBrandImage);
				TextView tvRecommendedImage = (TextView)llPlano.findViewById(R.id.tvRecommendedImage);
				LinearLayout llExecuted = (LinearLayout)llPlano.findViewById(R.id.llExecuted);
				TextView tvDate = (TextView)llPlano.findViewById(R.id.tvDate);
				TextView tvView = (TextView)llPlano.findViewById(R.id.tvRecommendedImage);
				
				
				if(i == 0)
				{
					tvTitle.setVisibility(View.VISIBLE);
					tvTitle.setText(brandName);
					llExecuted.setVisibility(View.GONE);
					tvRecommendedImage.setVisibility(View.VISIBLE);
					
					if(!initiativeDO.Planogram.contains(replace) && !initiativeDO.Planogram.contains(ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]) )
					{
						String path= gettingOriginalPath(initiativeDO.Planogram);
						final Uri uri = Uri.parse(path);
//						final Uri uri = Uri.parse(initiativeDO.Planogram);
						
						if (uri != null) 
						{
//							Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,initiativeDO.Planogram));
							Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,path));
							if (bitmap != null)
							{
								ivInitiativeImage.setImageBitmap(bitmap);
							}
						}
					}
					else if(initiativeDO.Planogram != null)
					{
						if(initiativeDO.Planogram.contains(replace))
						{
							initiativeDO.Planogram = initiativeDO.Planogram.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
						}
						UrlImageViewHelper.setUrlDrawable(ivInitiativeImage, initiativeDO.Planogram, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
					}
					
					if(!initiativeDO.Image.equalsIgnoreCase(""))
					{
						ivBrandImage.setVisibility(View.VISIBLE);
						if(!initiativeDO.Image.contains(replace) && !initiativeDO.Image.contains(ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]) )
						{
							String path= gettingOriginalPath(initiativeDO.Image);
							final Uri uri = Uri.parse(path);
//							final Uri uri = Uri.parse(initiativeDO.Image);
							
							if (uri != null) 
							{
//								Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivBrandImage,initiativeDO.Image));
								Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivBrandImage,path));
								if (bitmap != null)
								{
									ivBrandImage.setImageBitmap(bitmap);
								}
							}
						}
						else if(initiativeDO.Image != null)
						{
							if(initiativeDO.Image.contains(replace))
							{
								initiativeDO.Image = initiativeDO.Image.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
							}
							
							UrlImageViewHelper.setUrlDrawable(ivBrandImage, initiativeDO.Image, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
						}
					}
					else
						ivBrandImage.setVisibility(View.GONE);
					
					llInitiatives.addView(llPlano);
				}
				else
				{
					if(i-1 >= 0)
					{
						final InitiativeTradePlanImageDO executionDO =  vecExecutionDOs.get(i-1);
						
						llPlano.setTag(executionDO);
						tvTitle.setVisibility(View.VISIBLE);
						llExecuted.setVisibility(View.VISIBLE);
						tvRecommendedImage.setVisibility(View.GONE);
						tvDate.setText(executionDO.ImplementedOn);
						
						if(!executionDO.ExecutionPicture.contains(replace))
						{
							String path= gettingOriginalPath(executionDO.ExecutionPicture);
							final Uri uri = Uri.parse(path);
//							final Uri uri = Uri.parse(executionDO.ExecutionPicture);
							
							if (uri != null) 
							{
//								Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,executionDO.ExecutionPicture));
								Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,path));
								if (bitmap != null)
								{
									ivInitiativeImage.setImageBitmap(bitmap);
								}
							}
						}
						else if(executionDO.ExecutionPicture != null)
						{
							if(executionDO.ExecutionPicture.contains(replace))
							{
								executionDO.ExecutionPicture = executionDO.ExecutionPicture.replace(replace, ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]);
							}
							UrlImageViewHelper.setUrlDrawable(ivInitiativeImage, executionDO.ExecutionPicture, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
						}
					}
					
					llPlano.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							if(v.getTag() != null && (InitiativeTradePlanImageDO)v.getTag() != null)
								showViewDetails((InitiativeTradePlanImageDO)v.getTag());
						}
					});
					
					llInitiatives.addView(llPlano);
				}
					
		    }
		}
		else
		{
			LinearLayout llPlano = (LinearLayout)inflater.inflate(R.layout.initiativeimage_cell, null);
			TextView tvTitle = (TextView)llPlano.findViewById(R.id.tvTitle);
			ImageView ivInitiativeImage = (ImageView)llPlano.findViewById(R.id.ivInitiativeImage);
			TextView tvRecommendedImage = (TextView)llPlano.findViewById(R.id.tvRecommendedImage);
			LinearLayout llExecuted = (LinearLayout)llPlano.findViewById(R.id.llExecuted);
			TextView tvDate = (TextView)llPlano.findViewById(R.id.tvRecommendedImage);
			TextView tvView = (TextView)llPlano.findViewById(R.id.tvRecommendedImage);
			ImageView ivBrandImage = (ImageView)llPlano.findViewById(R.id.ivBrandImage);
			
			tvTitle.setText(brandName);
			tvTitle.setVisibility(View.VISIBLE);
			llExecuted.setVisibility(View.GONE);
			tvRecommendedImage.setVisibility(View.VISIBLE);
			
			if(!initiativeDO.Planogram.contains(replace))
			{
//				final Uri uri = Uri.parse(initiativeDO.Planogram);
				String path= gettingOriginalPath(initiativeDO.Planogram);
				final Uri uri = Uri.parse(path);


				if (uri != null) 
				{
//					Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,initiativeDO.Planogram));
					Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,path));
					if (bitmap != null)
					{
						ivInitiativeImage.setImageBitmap(bitmap);
					}
				}
			}
			else if(initiativeDO.Planogram != null)
			{
				if(initiativeDO.Planogram.contains(replace))
				{
					initiativeDO.Planogram = initiativeDO.Planogram.replace(replace, ServiceURLs.IMAGE_GLOBAL_URL.split("Services")[0]);
				}
				
				UrlImageViewHelper.setUrlDrawable(ivInitiativeImage, initiativeDO.Planogram, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
			}
			
			if(!initiativeDO.Image.equalsIgnoreCase(""))
			{
				ivBrandImage.setVisibility(View.VISIBLE);
				if(!initiativeDO.Image.contains(replace) && !initiativeDO.Image.contains(ServiceURLs.MAIN_GLOBAL_URL.split("Services")[0]) )
				{
					String path= gettingOriginalPath(initiativeDO.Image);
					final Uri uri = Uri.parse(path);

					if (uri != null) 
					{
//						Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivBrandImage,initiativeDO.Image));
						Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivBrandImage,path));
						if (bitmap != null)
						{
							ivBrandImage.setImageBitmap(bitmap);
						}
					}
				}
				else if(initiativeDO.Image != null)
				{
					if(initiativeDO.Image.contains(replace))
					{
						initiativeDO.Image = initiativeDO.Image.replace(replace, ServiceURLs.IMAGE_GLOBAL_URL.split("Services")[0]);
					}
					
					UrlImageViewHelper.setUrlDrawable(ivBrandImage, initiativeDO.Image, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
				}
			}
			else
				ivBrandImage.setVisibility(View.GONE);
			
			llInitiatives.addView(llPlano);
		}
	}
	public String gettingOriginalPath(String oldPath)
	{
		String newPath=oldPath;
		if(oldPath != null && oldPath.length() > 0 && oldPath.contains("../"))
		{
			try {
				newPath = oldPath.replace("..", ServiceURLs.IMAGE_GLOBAL_URL+"");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return  newPath;
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
				Bitmap bitmapProcessed = getBitMap(bmp,camera_imagepath);
				mBtBitmap = bitmapProcessed;
				showPopup();
				break;
			}
		}
	}
	
	private void showPopup()
	{
//		popupWindowStores=null;
//		popupWindowStores = popupWindowTypes(getBitMap(mBtBitmap,camera_imagepath));
//		popupWindowStores.showAtLocation(llPlanogram, Gravity.CENTER, 0, 0);
		showDetails(getBitMap(mBtBitmap,camera_imagepath));
		
	}
	
	private void showDetails(Bitmap bitmap)
	{
		LinearLayout llout = (LinearLayout) inflater.inflate(R.layout.capture_planogram_popup, null);
		Button btnSubmit = (Button)llout.findViewById(R.id.btnSubmit);
		final EditText etNotes = (EditText)llout.findViewById(R.id.edtNotes);
		ImageView ivCapturedPlanImage = (ImageView)llout.findViewById(R.id.ivCapturedPlanImage);


		etNotes.setText("");
		ivCapturedPlanImage.setBackgroundResource(0);
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(dialogDetails!=null)
					dialogDetails.dismiss();

				refreshData("NewOne",etNotes.getText().toString());

			}
		});

		
		if(bitmap!=null)
			ivCapturedPlanImage.setImageBitmap(bitmap);
		
		llout.setGravity(Gravity.CENTER_HORIZONTAL);
		dialogDetails = new Dialog(InitiativeExecutionActivity.this, android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogDetails.addContentView(llout, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
		dialogDetails.show();
		setTypeFaceRobotoNormal(llout);

	}
	
	private void showViewDetails(InitiativeTradePlanImageDO ecExecutionDO)
	{
//		LinearLayout llout = (LinearLayout) inflater.inflate(R.layout.capture_planogram_popup, null);
//		Button btnSubmit = (Button)llout.findViewById(R.id.btnSubmit);
//		final EditText etNotes = (EditText)llout.findViewById(R.id.edtNotes);
//		ImageView ivCapturedPlanImage = (ImageView)llout.findViewById(R.id.ivCapturedPlanImage);
//
//
//		etNotes.setText(ecExecutionDO.Reason);
//		ivCapturedPlanImage.setBackgroundResource(0);
//		btnSubmit.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				if(dialogDetails!=null)
//					dialogDetails.dismiss();
//			}
//		});
//
//		
////		if(bitmap!=null)
////			ivCapturedPlanImage.setImageBitmap(bitmap);
//		
//		if(!ecExecutionDO.ExecutionPicture.contains(replace))
//		{
//			final Uri uri = Uri.parse(ecExecutionDO.ExecutionPicture);
//			
//			if (uri != null) 
//			{
//				Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivCapturedPlanImage,ecExecutionDO.ExecutionPicture));
//				if (bitmap != null) 
//				{
//					ivCapturedPlanImage.setImageBitmap(bitmap);
//				}
//			}
//		}
//		else if(ecExecutionDO.ExecutionPicture != null)
//		{
//			if(ecExecutionDO.ExecutionPicture.contains(replace))
//			{
//				ecExecutionDO.ExecutionPicture = ecExecutionDO.ExecutionPicture.replace(replace, "http://10.20.53.27/AlSeer");
//			}
//			
//			mImageFetcher.loadImage(ecExecutionDO.ExecutionPicture, ivCapturedPlanImage);
//		}
//		
//		dialogDetails = new Dialog(InitiativeExecutionActivity.this, android.R.style.Theme_Holo_Dialog);
//		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//		dialogDetails.addContentView(llout, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
//		dialogDetails.show();
//		
//		setTypeFaceRobotoNormal(llout);
		
		// Details
       LinearLayout viewDetails = (LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.competitor_details, null);
        
       Button btn_submit       = (Button)viewDetails.findViewById(R.id.btnSubmit);
       ImageView ivCompetitor 	 = (ImageView)viewDetails.findViewById(R.id.ivCompetitor);
		
       TextView tvBrandOur 		 = (TextView)viewDetails.findViewById(R.id.tvBrandOur);
       TextView tvCompany 		 = (TextView)viewDetails.findViewById(R.id.tvBrandComp);
       TextView tvDateTime       = (TextView)viewDetails.findViewById(R.id.tvDateTime);
       TextView tvNote           = (TextView)viewDetails.findViewById(R.id.tvNote);
       
       LinearLayout llCompany 	= (LinearLayout)viewDetails.findViewById(R.id.llCompany);
       LinearLayout llPrice 	= (LinearLayout)viewDetails.findViewById(R.id.llPrice);
       
       llCompany.setVisibility(View.GONE);
       llPrice.setVisibility(View.GONE);
       
       tvBrandOur.setText(brandName);
       tvCompany.setText(brandName);
       tvNote.setText(ecExecutionDO.Reason);
       tvDateTime.setText(ecExecutionDO.ImplementedOn);
       
       
       if(!ecExecutionDO.ExecutionPicture.contains(replace))
   		{
			String path= gettingOriginalPath(ecExecutionDO.ExecutionPicture);
			final Uri uri = Uri.parse(path);
//   			final Uri uri = Uri.parse(ecExecutionDO.ExecutionPicture);
   			
   			if (uri != null) 
   			{
   				Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivCompetitor,path));
//   				Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivCompetitor,ecExecutionDO.ExecutionPicture));
   				if (bitmap != null)
   				{
   					ivCompetitor.setImageBitmap(bitmap);
   				}
   			}
   		}
   		else if(ecExecutionDO.ExecutionPicture != null)
   		{
   			if(ecExecutionDO.ExecutionPicture.contains(replace))
   			{
   				ecExecutionDO.ExecutionPicture = ecExecutionDO.ExecutionPicture.replace(replace, "http://10.20.53.27/AlSeer");
   			}
   			
   			UrlImageViewHelper.setUrlDrawable(ivCompetitor, ecExecutionDO.ExecutionPicture, R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
   		}
       
       btn_submit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(dialogDetails!=null)
					dialogDetails.dismiss();
			}
		});
		
		dialogDetails = new Dialog(InitiativeExecutionActivity.this, android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogDetails.addContentView(viewDetails, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
		dialogDetails.show();
		
		setTypeFaceRobotoNormal(viewDetails);
			

	}
	
	private Bitmap getBitMap(Bitmap bmp, String camera_imagepath)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			bitmapProcessed = BitmapUtilsLatLang.processBitmap22(bmp, lat, lang, "");
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
	}
	
	@Override
	public void gotLocation(Location loc) 
	{
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
				}
			});
		}
	}
	
	private PopupWindow popupWindowTypes(Bitmap bitmap) 
	{

		final PopupWindow popupWindow = new PopupWindow(InitiativeExecutionActivity.this);
		LinearLayout llout = (LinearLayout) inflater.inflate(R.layout.capture_planogram_popup, null);
		Button btnSubmit = (Button)llout.findViewById(R.id.btnSubmit);
		final EditText etNotes = (EditText)llout.findViewById(R.id.edtNotes);
		ImageView ivCapturedPlanImage = (ImageView)llout.findViewById(R.id.ivCapturedPlanImage);


		etNotes.setText("");
		ivCapturedPlanImage.setBackgroundResource(0);
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(popupWindowStores!=null)
					popupWindowStores.dismiss();

				refreshData("NewOne",etNotes.getText().toString());

			}
		});

		
		if(bitmap!=null)
			ivCapturedPlanImage.setImageBitmap(bitmap);

		popupWindow.setFocusable(true);
		popupWindow.setWidth(AppConstants.DEVICE_WIDTH/2);
		popupWindow.setHeight(((AppConstants.DEVICE_HEIGHT/3)*2)+50);
		popupWindow.setContentView(llout);

		return popupWindow;
	}
	
	public void refreshData(final String ImgDesc,final String comments)
	{
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				Log.i("Camera", "Need to add viewpager");
				
				final InitiativeTradePlanImageDO planogramDO = new InitiativeTradePlanImageDO();
				
				planogramDO.InitiativeTradePlanImageId = initiativeDO.InitiativeId;
				planogramDO.InitiativeTradePlanId = initiativeDO.InitiativeId;
				planogramDO.Type = FileUtils.getFileNameFromPath(camera_imagepath);
				planogramDO.ExecutionPicture = camera_imagepath;
				planogramDO.Reason = comments;
				planogramDO.ExecutionStatus = 1;
				planogramDO.ImplementedBy = preference.getStringFromPreference(Preference.USER_NAME, "");
				planogramDO.ImplementedOn = CalendarUtils.getCurrentDate();
				planogramDO.VerifiedOn = CalendarUtils.getCurrentDate().split("T")[0];
				planogramDO.JourneyCode = preference.getStringFromPreference(Preference.JOURNEYCODE, "");
				planogramDO.VisitCode = journeyPlanDO.site;
				
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						planogramDO.ExecutionUploadPicture = new UploadImage().uploadImage(InitiativeExecutionActivity.this, camera_imagepath, "Initiative", true);
						
						ConnectionHelper connectionHelper = new ConnectionHelper(null);
						connectionHelper.sendRequest(getApplicationContext(), BuildXMLRequest.initiativeImage(initiativeDO.Brand,journeyPlanDO.site,planogramDO), ServiceURLs.InsertInitiatives);
					}
				}).start();
				
				new InitiativesDA().updateInitiative(initiativeDO.InitiativeId+"",journeyPlanDO.site);
				new InitiativesTradePalnDA().insertInitiative(planogramDO);
				
				vecExecutionDOs.add(planogramDO);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						llInitiatives.removeAllViews();
						infaltePlanogram(vecExecutionDOs);
//						initiativePagerAdapter.refreshViewPager(vecExecutionDOs);
					}
				});
			}
		}).start();
	}
	
	public Bitmap setImageFromCam()
	{
		
		File f = new File(camera_imagepath);
		Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 570, 330);
		
		Bitmap bitmapProcessed = getBitMap(bmp,camera_imagepath);
		return bitmapProcessed;
		
//		ivCaptureImg.setBackgroundResource(0);
//		mBtBitmap = bitmapProcessed;
//		ivCaptureImg.setImageBitmap(bitmapProcessed);
	}
	
	public HttpImageManager getHttpImageManager () 
	{
		return ((MyApplicationNew) (InitiativeExecutionActivity.this).getApplication()).getHttpImageManager();
	}
}
