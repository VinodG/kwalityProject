package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;

public class BitmapsUtiles 
{
	public static Bitmap processBitmap2(Bitmap bmpOrig, String lat, String lng,String altitude)
	{
		int width = 2;
		Bitmap bmpProcessed = Bitmap.createBitmap(bmpOrig.getWidth()+ (width * 2), bmpOrig.getHeight() + (width * 2), bmpOrig.getConfig());
		
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bmpProcessed);
		
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		if(AppConstants.DEVICE_DENSITY <= 0.75)
			paint.setTextSize(12);
		else
			paint.setTextSize(30);
		
		canvas.drawBitmap(bmpOrig, width, width, paint);
		
		String strDate= new SimpleDateFormat("dd MMM, yyyy").format(new Date());
		canvas.drawText("Date: "+strDate, bmpOrig.getWidth()*2/3 - 110, bmpOrig.getHeight() - 80, paint);
		if(AppConstants.DEVICE_DENSITY <= 0.75)
			paint.setTextSize(12);
		else
			paint.setTextSize(30);
		canvas.drawText("Latitude: " + lat, bmpOrig.getWidth()*2/3 - 110, bmpOrig.getHeight()-40, paint);
		canvas.drawText("Langitude: " + lng , bmpOrig.getWidth()*2/3 - 110, bmpOrig.getHeight(), paint);
		
		 if(bmpOrig!=null && ! bmpOrig.isRecycled())
		 {
			WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bmpOrig);
			if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
			{
				weakBitmap.get().recycle();
			}
		 }
		
		return bmpProcessed;
	}

	
	public static Bitmap processBitmap(Bitmap bmpOrig)
	{
		int width = 2;
		Bitmap bmpProcessed = Bitmap.createBitmap(100, 120, bmpOrig.getConfig());
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		Canvas canvas = new Canvas(bmpProcessed);
		
		
		Rect rectSrc = new Rect(0, 0, bmpOrig.getWidth(), bmpOrig.getHeight());
		Rect rectDest = new Rect(0, 0, 100, 120);

	    paint.setAntiAlias(true);
//	    canvas.drawRect(0, 0, 100, 120, paint);
		canvas.drawBitmap(bmpOrig, rectSrc, rectDest, paint);
		
		 if(bmpOrig!=null && ! bmpOrig.isRecycled())
		 {
			WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bmpOrig);
			if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
			{
				weakBitmap.get().recycle();
			}
		 }
	    
	    
//		paint.setColor(Color.WHITE);
//		paint.setTextSize(15);
//		paint.setColor(Color.BLACK);
//		canvas.drawText("IMG", 30, 115, paint);
		
		return bmpProcessed;
	}
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPxRadius)
	{
	 
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
            bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
     
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx =roundPxRadius;
     
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
     
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
     
        return output;
	}
	
	public static Bitmap  resizeBitmap(Bitmap bitmapOrg,int newWidth,int newHeight)
	{
		Bitmap resizedBitmap = null;
		try {

			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();

			/**
			 *  calculate the scale - in this case = 0.4f
			 */
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;

			/**
			 *  createa matrix for the manipulation
			 */
			Matrix matrix = new Matrix();
			/**
			 *  resize the bit map
			 */
			matrix.postScale(scaleWidth, scaleHeight);
			/**
			 *  rotate the Bitmap
			 */
			resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height,
					matrix, true);

		} catch (Throwable e) {
		}
        
        return resizedBitmap;
	}
	
	public static Bitmap  resizeWithoutBitmap(Bitmap bitmapOrg,int newWidth,int newHeight)
	{
		Bitmap resizedBitmap = null;
		try {

			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();

			/**
			 *  calculate the scale - in this case = 0.4f
			 */
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;

			/**
			 *  createa matrix for the manipulation
			 */
			Matrix matrix = new Matrix();
			/**
			 *  resize the bit map
			 */
			matrix.postScale(scaleWidth, scaleHeight);
			/**
			 *  rotate the Bitmap
			 */
			resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height,
					matrix, true);

		} catch (Throwable e) {
		}
        
        return resizedBitmap;
	}
	
	
	public static Bitmap getResizedBmp(Bitmap bitmap, float width, float height) {

		float bmpHieght = 600;
		float bmpWidth  = 1024;
		Log.i("bmpHieght ", bmpHieght+" bmpWidth "+bmpWidth);
		
		int scaledWidth=0;
		int scaledHeight=0;
		Bitmap scaledBitmap = null;
//		if(bmpWidth/width  > bmpHieght/ height)
//		{
			scaledWidth = (int) width;
			scaledHeight = (int)(bmpHieght * width / bmpWidth);
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
//		}
//		else
//		{
//			scaledWidth = (int)(bmpWidth * height/ bmpHieght);
//			scaledHeight = (int)height;
//			scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
//		}
	  
	   return scaledBitmap;
	}

	public static Bitmap decodeSampledBitmapFromResource(File f, int reqWidth,
			int reqHeight) {
		try {
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(new FileInputStream(f), null, options);

			// Calculate inSampleSize
			options.inSampleSize = 1;

			// Decode bitmap with inSampleSize set

			options.inJustDecodeBounds = false;
			Bitmap tmpBitmap = BitmapFactory.decodeStream(
					new FileInputStream(f), null, options);
			tmpBitmap = getResizedBitmap(tmpBitmap, reqWidth, reqHeight);
			float rotation = rotationForImage(null, Uri.fromFile(f));
			if (rotation != 0f) {
				Matrix matrix = new Matrix();
				matrix.preRotate(rotation);
				tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0,
						tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix,
						true);
			}
			return tmpBitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Bitmap getResizedBitmap(Bitmap bitmap, float width, float height)
	{
		if (bitmap != null) {

		}
		float bmpHieght = bitmap.getHeight();
		float bmpWidth = bitmap.getWidth();

		Bitmap scaledBitmap = null;
		if (bmpHieght < height && bmpWidth < width) {
			return bitmap;
		}

		int scaledWidth = 0;
		int scaledHeight = 0;

		if (bmpWidth / width < bmpHieght / height) {
			scaledWidth = convertPixelToDp((int) (bmpWidth * height / bmpHieght));
			scaledHeight = convertPixelToDp((int) height);
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
					scaledHeight, true);
		} else {
			scaledWidth = convertPixelToDp((int) width);
			scaledHeight = convertPixelToDp((int) (bmpHieght * width / bmpWidth));
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
					scaledHeight, true);
		}
		return scaledBitmap;
	}

	public static float rotationForImage(Context context, Uri uri) {
		try {
			if (context != null && uri.getScheme().equals("content")) {
				String[] projection = { Images.ImageColumns.ORIENTATION };
				Cursor c = context.getContentResolver().query(uri, projection,
						null, null, null);
				if (c.moveToFirst()) {
					return c.getInt(0);
				}
			} else if (uri.getScheme().equals("file")) {
				try {
					ExifInterface exif = new ExifInterface(uri.getPath());
					int rotation = (int) exifOrientationToDegrees(exif
							.getAttributeInt(ExifInterface.TAG_ORIENTATION,
									ExifInterface.ORIENTATION_NORMAL));
					return rotation;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0f;
	}

	public static int convertPixelToDp(int px) {
		return (int) (px * (160 / 160f));
	}
	 private static float exifOrientationToDegrees(int exifOrientation) {
		   if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
		    return 90; 
		   } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
		    return 180;
		   } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
		         return 270;
		     }
		     return 0;
		 }
	 
	 public static String saveVerifySignature(Bitmap bitmap)
	 {
		 Random generator = new Random();
			int n = 10000;
			n = generator.nextInt(n);
			File f = new File(Environment.getExternalStorageDirectory()+"/Kwality");
			f.mkdirs();
			String filePath = f.getAbsolutePath()+"/alseer_image_"+System.currentTimeMillis()+".png";
			File file = new File (filePath);
			if (file.exists ()) file.delete (); 
			try
			{
				
		       FileOutputStream out = new FileOutputStream(file);
		       bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		       out.flush();
		       out.close();
			} 
			catch (Exception e) 
			{
		       e.printStackTrace();
			}
			
			return filePath;
	 }
	 
}
