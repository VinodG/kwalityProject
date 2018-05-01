package com.winit.alseer.salesman.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

public class BitmapUtils 
{
	public static Bitmap  resizeBitmap(Bitmap bitmapOrg,int newWidth,int newHeight)
	{
		Bitmap resizedBitmap = null;
		try
		{
        
		int width=bitmapOrg.getWidth();
        int height=bitmapOrg.getHeight();
        
        // calculate the scale - in this case = 0.4f 
        float scaleWidth = ((float) newWidth) / width; 
        float scaleHeight = ((float) newHeight) / height;
        
        // createa matrix for the manipulation 
        Matrix matrix = new Matrix(); 
        // resize the bit map 
        matrix.postScale(scaleWidth, scaleHeight); 
        // rotate the Bitmap 
        //matrix.postRotate(45); 
        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,width, height, matrix, true);
        bitmapOrg.recycle();
		
		}
		catch(Throwable e)
		{
			
		}
        
        return resizedBitmap;
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
	 
	 @SuppressWarnings("resource")
	public void copyFile(String sourceFilePath,String destFilePath) throws IOException 
	    {
		 	File sourceFile = new File(sourceFilePath);
		 	File destFile 	= new File(destFilePath);
		 	
			if (!sourceFile.exists())
			{
			    return;
			}
			if (!destFile.exists()) {
			    destFile.createNewFile();
			}
			FileChannel source = null;
			FileChannel destination = null;
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			if (destination != null && source != null) {
			    destination.transferFrom(source, 0, source.size());
			}
			if (source != null)
			{
			    source.close();
			}
			if (destination != null)
			{
			    destination.close();
			}

	      }
	 
	 public static void CopyStream(InputStream is, OutputStream os)
	 {
	        final int buffer_size=1024;
	        try
	        {
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	            if(Thread.currentThread().isInterrupted()) return;
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){ex.printStackTrace();}
	    }

	 public static Bitmap getBitmap(String path)
	 {
		 Bitmap bmp = null;
		 try
		 {
			 bmp = BitmapFactory.decodeFile(path);
		 }
		 catch(OutOfMemoryError e)
		 {
			 e.printStackTrace();
			 System.gc();
		 }
		 catch (Exception e) 
		 {
			e.printStackTrace();
		 }
		 return bmp;
	 }
	 
	 public static void recycleBitmap(Bitmap bmp)
	 {
		 WeakReference<Bitmap> weak = new WeakReference<Bitmap>(bmp);
		 if(weak.get()!=null && !weak.get().isRecycled())
		 {
			 weak.get().recycle();
		 }
	 }
	 
	 
	 
	 public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                || (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
	 
	 public static Bitmap decodeSampledBitmapFromResource(File f, int reqWidth, int reqHeight) {

		 try 
		 {
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    
			BitmapFactory.decodeStream(new FileInputStream(f),null,options);
		 
		    // Calculate inSampleSize
		    options.inSampleSize = 1;

		    // Decode bitmap with inSampleSize set
		    
		    options.inJustDecodeBounds = false;
		    Bitmap tmpBitmap=BitmapFactory.decodeStream(new FileInputStream(f), null, options);
		    tmpBitmap=getResizedBitmap(tmpBitmap, reqWidth, reqHeight);
		    float rotation = rotationForImage(null, Uri.fromFile(f));
		    if (rotation != 0f) 
		    {
		    	Matrix matrix = new Matrix();
		    	matrix.preRotate(rotation);
		    	tmpBitmap = Bitmap.createBitmap(
		    		tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix, true);
		    }
		    return tmpBitmap;
		 } 
		 catch (FileNotFoundException e) 
		 {
			e.printStackTrace();
		 }
		 return null;
	}
	 public static float rotationForImage(Context context, Uri uri) {
		 try {
			 if (context!=null && uri.getScheme().equals("content")) {
				 String[] projection = { Images.ImageColumns.ORIENTATION };
				 Cursor c = context.getContentResolver().query(
						 uri, projection, null, null, null);
				 if (c.moveToFirst()) {
					 return c.getInt(0);
				 }
			 } else if (uri.getScheme().equals("file")) {
				 try {
					 ExifInterface exif = new ExifInterface(uri.getPath());
					 int rotation = (int)exifOrientationToDegrees(
							 exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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
	 
	 public static Bitmap getResizedBitmap(Bitmap bitmap, float width, float height)
	 {
		 if(bitmap!=null){
			 
		 }
		 float bmpHieght = bitmap.getHeight();
		 float bmpWidth  = bitmap.getWidth();
	   
		 Bitmap scaledBitmap = null;
		 if(bmpHieght < height && bmpWidth < width)
		 {
			 return bitmap ;
		 }
	    
		 int scaledWidth=0;
		 int scaledHeight=0;
	   
		 if(bmpWidth/width  < bmpHieght/ height)
		 {
			 scaledWidth = convertPixelToDp((int)(bmpWidth * height/ bmpHieght));
			 scaledHeight = convertPixelToDp((int)height);
			 scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		 }
		 else
		 {
			 scaledWidth = convertPixelToDp((int)width);
			 scaledHeight = convertPixelToDp((int)(bmpHieght * width / bmpWidth));
			 scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);			
		 }
	     return scaledBitmap;
	 }
	 public static int convertPixelToDp(int px)
	 {
	      return (int) (px *(160 / 160f));
	 }
	 
	  public static String saveVerifySignature(Bitmap bitmap)
	  {
	   Random generator = new Random();
	   int n = 10000;
	   n = generator.nextInt(n);
	   File f = new File(Environment.getExternalStorageDirectory()+"/F&N");
	   f.mkdirs();
	   String filePath = f.getAbsolutePath()+"/F&N_"+System.currentTimeMillis()+".png";
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
