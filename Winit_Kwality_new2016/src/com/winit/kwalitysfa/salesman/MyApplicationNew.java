package com.winit.kwalitysfa.salesman;

import httpimage.FileSystemPersistence;
import httpimage.HttpImageManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.winit.alseer.salesman.common.FilesStorage;


public class MyApplicationNew extends Application
{
	 public static String MyLock = "Lock";
	 public static String APP_DB_LOCK = "lock";
	 public static Context mContext;
	@Override
    public void onCreate() 
	{
        super.onCreate();
        if(mContext ==null)
			mContext = this;
		MultiDex.install(this);
        mHttpImageManager = new HttpImageManager(
        	    HttpImageManager.createDefaultMemoryCache(),
        	    new FileSystemPersistence(FilesStorage.getImageCacheDirectory()));

    }
	
	// ////PRIVATE
	private HttpImageManager mHttpImageManager;
	public HttpImageManager getHttpImageManager() {
		return mHttpImageManager;
	}
}
