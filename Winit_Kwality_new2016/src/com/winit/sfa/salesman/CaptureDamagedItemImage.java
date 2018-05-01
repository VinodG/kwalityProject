package com.winit.sfa.salesman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.Vector;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class CaptureDamagedItemImage extends BaseActivity implements LocationResult
{
	private GridView gvImages;
	private LinearLayout llCaptureImages;
	private  Uri mCapturedImageURI;
	private static final int CAMERA_PIC_REQUEST = 2500;
	private GridViewAdapter gridViewAdapter;
	private Button btnTakeNew, btnContinue;
	private TextView tvPageTitle;
	private String itemCode = "", desc = "";
	private TrxDetailsDO trxDetailsDO;
	private Bitmap mBtBitmap = null;
	private Bitmap bitmapProcessed;
	private String capturedImageFilePath;
	private String lat = "", lang = "";
	private LocationUtility locationUtility;
	
	@Override
	public void initialize()
	{
		llCaptureImages = (LinearLayout) inflater.inflate(R.layout.capture_damaeged_image, null);
		llBody.addView(llCaptureImages, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if(getIntent().getExtras() != null)
		{
			trxDetailsDO = (TrxDetailsDO) getIntent().getExtras().get("objiItem");
		}
		
		initializeControls();
		
		
		tvPageTitle.setText(itemCode+"- "+desc);
		gvImages.setAdapter(gridViewAdapter = new GridViewAdapter(new Vector<DamageImageDO>()));
		
		if(trxDetailsDO.vecDamageImages != null && trxDetailsDO.vecDamageImages.size() > 0)
		{
			gridViewAdapter.refreshGridView(trxDetailsDO.vecDamageImages);
		}
		
		startCamera();
		
		btnTakeNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				startCamera();
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent objIntent = new Intent();
				objIntent.putExtra("objIntent", trxDetailsDO);
				setResult(500, objIntent);
				finish();
			}
		});
		setTypeFaceRobotoNormal(llCaptureImages);
		btnTakeNew.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	private void initializeControls()
	{
		locationUtility = new LocationUtility(CaptureDamagedItemImage.this);
		
		gvImages = (GridView) llCaptureImages.findViewById(R.id.gvImages);
		btnTakeNew = (Button) llCaptureImages.findViewById(R.id.btnTakeNew);
		btnContinue = (Button) llCaptureImages.findViewById(R.id.btnContinue);
		
		tvPageTitle = (TextView) llCaptureImages.findViewById(R.id.tvPageTitle);
	}
	
	private class GridViewAdapter extends BaseAdapter
	{

		private Vector<DamageImageDO> vecImagePaths;
		public GridViewAdapter(Vector<DamageImageDO> vecImagePaths)
		{
			this.vecImagePaths = vecImagePaths;
		}
		@Override
		public int getCount() 
		{
			if(vecImagePaths != null && vecImagePaths.size() > 0)
				return vecImagePaths.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			if(convertView == null)
				convertView = inflater.inflate(R.layout.capture_image_content, null);
			final ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
			
			setBitmapImage(ivImage, vecImagePaths.get(position).ImagePath);
			
			convertView.setOnLongClickListener(new OnLongClickListener() 
			{
				@Override
				public boolean onLongClick(View v)
				{
					showCustomDialog(CaptureDamagedItemImage.this, "Warning!", "Are you sure you want to delete this image?", "Yes", "No", "delete");
					return true;
				}
			});
			
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Bitmap bitmap = ((BitmapDrawable)ivImage.getDrawable()).getBitmap();
					showImagePopUp(bitmap);
				}
			});
			
			return convertView;
		}
		
		private void refreshGridView(Vector<DamageImageDO> vecImagePaths)
		{
			this.vecImagePaths = vecImagePaths;
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == CAMERA_PIC_REQUEST) && mCapturedImageURI != null && resultCode==RESULT_OK) 
    	{
    		try
    		{
    			showLoader("Please wait...");
            	new Thread(new Runnable()
            	{
    				@Override
    				public void run()
    				{
    					System.gc();
    		        	String[] projection = { MediaStore.Images.Media.DATA}; 
    		        	
    		            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null); 
    		            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
    		            cursor.moveToFirst(); 
    		            capturedImageFilePath = cursor.getString(column_index_data);
    		            AppConstants.iconpaths=capturedImageFilePath;
    		            
    		            File f = new File(capturedImageFilePath);
    					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
    					Bitmap bitmapProcessed = getBitMap(bmp,capturedImageFilePath);
    					mBtBitmap = bitmapProcessed;
    					
    					Cursor cursor1 = managedQuery(getImageUri(CaptureDamagedItemImage.this,mBtBitmap), projection, null, null, null); 
    		            int column_index_data1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
    		            cursor1.moveToFirst(); 
    		            capturedImageFilePath = cursor1.getString(column_index_data1);
    		            
    		            
    		            if(trxDetailsDO.vecDamageImages == null)
    		            	trxDetailsDO.vecDamageImages = new Vector<DamageImageDO>();
    		            
    		            
    		            DamageImageDO damageImageDO = new DamageImageDO();
    		            damageImageDO.CapturedDate	= CalendarUtils.getOrderPostDate();
    		            damageImageDO.ImagePath		= capturedImageFilePath;
    		            trxDetailsDO.vecDamageImages.add(damageImageDO);
    		            
    		            
    		            runOnUiThread(new Runnable() 
    		            {
							@Override
							public void run() {
								hideLoader();
								gridViewAdapter.refreshGridView(trxDetailsDO.vecDamageImages);
							}
						});
    				}
    			}).start();
			}
    		catch (OutOfMemoryError e)
    		{
    			hideLoader();
    			showCustomDialog(CaptureDamagedItemImage.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    		catch (Exception e)
    		{
    			hideLoader();
    			showCustomDialog(CaptureDamagedItemImage.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    	}else{
    		finish();
    	}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) 
	{
//		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//		  inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		  String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		  return Uri.parse(path);
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
	
	public void startCamera()
	{
		String fileName = "temp.jpg";  
		ContentValues values = new ContentValues();  
		values.put(MediaStore.Images.Media.TITLE, fileName);  
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);  
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
		
		if(locationUtility != null)
			   locationUtility.getLocation(CaptureDamagedItemImage.this);
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		
//		if(from.equalsIgnoreCase("delete"))
//		{
//			vecImagePaths.remove(mPosition);
//			gridViewAdapter.refreshGridView(vecImagePaths);
//		}
		super.onButtonYesClick(from);
	}
	
	private void setBitmapImage(final ImageView imageView, String capturedImageFilePath)
	{
		
		
		Bitmap stampBitmap = decodeFile(new File(capturedImageFilePath), (int)(180 * px), (int)(180 * px));
        if(stampBitmap != null)
        {
   	    	
   	    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
   	    	stampBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
   	    	
			
			final WeakReference<Bitmap> reference = new WeakReference<Bitmap>(stampBitmap);
			
   	    	runOnUiThread(new Runnable()
   	    	{
				@Override
				public void run() 
				{
					imageView.setImageBitmap(reference.get());
					hideLoader();
				}
			});
        }
	}
	
	public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			lat	 = loc.getLatitude()+"";
			lang = loc.getLongitude()+"";

			DisplayMetrics metrics = getResources().getDisplayMetrics();

			Log.e("Density ", metrics.density+"");

			AppConstants.DEVICE_DENSITY = metrics.density;

//			runOnUiThread(new Runnable()
//			{
//				@Override
//				public void run() 
//				{
//					File f = new File(capturedImageFilePath);
//					Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
//					bitmapProcessed = getBitMap(bmp, capturedImageFilePath);
//					mBtBitmap = bitmapProcessed;
//				}
//			});
		}
	}
	
	private void showImagePopUp(Bitmap bitmap)
	{
		ImageView ivZoom = new ImageView(CaptureDamagedItemImage.this);
		final Dialog dialog = new Dialog(CaptureDamagedItemImage.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(ivZoom);
		ivZoom.setScaleType(ScaleType.FIT_XY);
		ivZoom.setImageBitmap(bitmap);
		
		dialog.getWindow().setLayout(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 800)-200,preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 1200)-400);
		dialog.show();
		
		ivZoom.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
		
	}
}
