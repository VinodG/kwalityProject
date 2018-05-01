package com.winit.alseer.salesman.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.winit.alseer.salesman.utilities.BitmapUtils;

public class ImageLoader
{
    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
    public ConcurrentHashMap<String, Bitmap> cache=new ConcurrentHashMap<String, Bitmap>();
    
    private File cacheDir;
    
    private int REQUIRED_WIDTH,REQUIRED_HEIGHT;
    
    public boolean isFistIN = false;
    
    public ImageLoader(Context context,int imgWidth,int imgHeight)
    {
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        REQUIRED_WIDTH  = imgWidth;
        REQUIRED_HEIGHT = imgHeight;
//        Find the dir to save cached images
        cacheDir= new File(context.getExternalFilesDir(null),"product_catalog");
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        isFistIN = false;
    }
    
//    final int stub_id = R.drawable.ic_launcher;
    public void DisplayImage(String id, String url, Activity activity, View imageView)
    {
        if(cache.containsKey(id))
            ((ImageView)imageView).setImageBitmap(cache.get(id));
        else
        {
//        	((ImageView)imageView).setImageResource(stub_id);
            queuePhoto(id, url, activity, imageView);
        }    
    }
    private void queuePhoto(String id, String url, Activity activity, View imageView)
    {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
        photosQueue.Clean(imageView);
        PhotoToLoad p = new PhotoToLoad(id, url, imageView);
        synchronized(photosQueue.photosToLoad)
        {
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
        {
        	if(!photoLoaderThread.isAlive())
        	photoLoaderThread.start();
        }
    }
    
    private Bitmap getBitmap(String url) 
    {
        //I identify images by hash code. Not a perfect solution, good for the demo.
    	
    	File f=new File(cacheDir, url.hashCode()+"");
        
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
//        //from web
        try 
        {
            Bitmap bitmap = null;
            
            InputStream is = null;
            
            is = new URL(url).openStream();
            
            if(is != null)
            {
            	OutputStream os = new FileOutputStream(f);
                BitmapUtils.CopyStream(is, os);
                os.close();
                
                bitmap = decodeFile(f);
            }
            return bitmap;
        } 
        catch (Exception ex)
        {
        	stopThread();
        	photoLoaderThread.start();
           ex.printStackTrace();
           return null;
        }
        catch(Throwable e)
        {
        	stopThread();
        	photoLoaderThread.start();
        	 return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f)
    {
        try 
        {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true)
            {
                if(width_tmp/2<REQUIRED_WIDTH || height_tmp/2<REQUIRED_HEIGHT)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            o2.inDither = true;
            
            o2.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            o2.inInputShareable=true;  //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

            Bitmap bitmap = null;
		   try
		   {
			    bitmap = BitmapFactory.decodeFileDescriptor(new FileInputStream(f).getFD(), null, o2);
		   }
		   catch (IOException e)
		   {
			   stopThread();
			   photoLoaderThread.start();
			    e.printStackTrace();
		   }
		   catch (OutOfMemoryError e)
		   {
			   stopThread();
			   photoLoaderThread.start();
			    e.printStackTrace();
		   }
            
            return bitmap;
        } 
        catch (FileNotFoundException e) 
        {
        	stopThread();
        	photoLoaderThread.start();
        }
        catch(OutOfMemoryError e)
        {
        	stopThread();
        	photoLoaderThread.start();
        }
        return null;
    }
    
    public static Bitmap processBitmap(Bitmap bmpOrig)
	{
		Bitmap bmpProcessed = Bitmap.createBitmap(bmpOrig.getWidth(), bmpOrig.getHeight(), bmpOrig.getConfig());
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		Canvas canvas = new Canvas(bmpProcessed);
		
		
		Rect rectSrc = new Rect(0, 0, bmpOrig.getWidth(), bmpOrig.getHeight());
		Rect rectDest = new Rect(5, 5, bmpOrig.getWidth()-5, bmpOrig.getHeight()-5);

	    paint.setAntiAlias(true);
	    canvas.drawRect(0, 0, bmpOrig.getWidth()+10, bmpOrig.getHeight()+10, paint);
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
		
		return getRoundedCornerBitmap(bmpProcessed,10);
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
    //Task for the queue
    private class PhotoToLoad
    {
        public String url, id;
        public View imageView;
        
        public PhotoToLoad(String id, String u, View i)
        {
            this.id= id;
        	url=u; 
            imageView=i;
        }
    }
    
    PhotosQueue photosQueue=new PhotosQueue();
    
    public void stopThread()
    {
        photoLoaderThread.interrupt();
        photoLoaderThread=new PhotosLoader();
    }
    
    //stores list of photos to download
    class PhotosQueue
    {
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
        //removes all instances of this ImageView
        public void Clean(View image)
        {
        	try
        	{
        		for(int j=0 ;j<photosToLoad.size();){
                    if(photosToLoad.get(j).imageView==image)
                        photosToLoad.remove(j);
                    else
                        ++j;
                }	
        	}
        	catch(Exception e)
        	{ }
        }
    }
    
    class PhotosLoader extends Thread 
    {
        public void run() 
        {
            try 
            {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad)
                        {
                        	if(isFistIN)
                        	{
                        		photoToLoad=photosQueue.photosToLoad.firstElement();
                        		photosQueue.photosToLoad.remove(0);
                        	}
                        	else
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp=getBitmap(photoToLoad.url);
                        
                        cache.put(photoToLoad.id, bmp);
                        if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url) || ((String)photoToLoad.imageView.getTag()).equals(photoToLoad.id))
                        {
                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
                            Activity a=(Activity)photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                        }
                    }
                    if(Thread.interrupted())
                        break;
                }
            } 
            catch (InterruptedException e) 
            {
            }
            catch (Exception e)
            { }
        }
    }
    
    PhotosLoader photoLoaderThread=new PhotosLoader();
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        View imageView;
        public BitmapDisplayer(Bitmap b, View i){bitmap=b;imageView=i;}
        public void run()
        {
            if(imageView instanceof ImageView)
            {
            	if(bitmap!=null)
            	{
                    ((ImageView)imageView).setImageBitmap(bitmap);
            	}
//                else
//                    ((ImageView)imageView).setImageResource(stub_id);	
            }
            else
            {
            	if(bitmap!=null)
            	{
                    imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            	}
                else
                	imageView.setBackgroundResource(0);
            }
        }
    }

//    public void clearCache()
//    {
//        //clear memory cache
//        cache.clear();
//        
//        //clear SD cache
//        File[] files=cacheDir.listFiles();
//        for(File f:files)
//            f.delete();
//    }
}
