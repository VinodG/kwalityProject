package com.winit.alseer.salesman.utilities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class LatlangUtility implements	GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener 
{
	private LocationRequest locationRequest;
	private LocationClient locationClient;
	private Location loc;
	private static final String TAG = "LatlangUtility";
	private float accuracy = 0f;
	private boolean isLocationGot=false;
	private long gps_time;
	private double  speed;
	private int ACCURATE = 1;
	private int LESS_ACCURATE = 2;
	private int MORE_ACCURATE = 3 ;
	private int WEEK_SIGNAL_STRENGHT = 4 ;
	private int HIGH_SIGNAL_STRENGHT = 5;

	public LocationClient getlocationClient() 
	{
		return locationClient;
	}

	private double latti, longi;
	private Context context;
	private static LatlangUtility instance;
	private onConnectionSuccessful onConnectionSuccessful;

	// Singleton Instance for Latlang
	public synchronized static LatlangUtility getInstance(Context context) {
		if (instance == null) {
			instance = new LatlangUtility(context);
		}
		return instance;
	}

	
	public void setListner(onConnectionSuccessful listner) {
		this.onConnectionSuccessful = listner;
	}

	// private constructor
	private LatlangUtility(Context context) {
		this.context = context;
		init();
	}

	private void init() 
	{
		// Initializing a Location Request object.
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(10000);

		// Installing a location Client.
		locationClient = new LocationClient(context, this, this);
	}

	// Using this method we can get Location
	public void getLatLang() 
	{
		isLocationGot=false;
		locationClient.requestLocationUpdates(locationRequest,new LocationListener() 
		{
			@Override
			public void onLocationChanged(Location loc) 
			{
				if (loc != null) 
				{
					if(!isLocationGot)
					{
						isLocationGot=true;
						Log.d("getAccuracy", "" + loc.getAccuracy());
						Log.d("LAt", "" + loc.getLatitude());
						Log.d("LOn", "" + loc.getLongitude());
						accuracy = loc.getAccuracy();
						latti = loc.getLatitude();
						longi = loc.getLongitude();
						gps_time = loc.getTime();
						speed = loc.getSpeed();
						
						LogUtils.errorLog("Lat longs from OnLocationChanged", loc.getLatitude()+"---"+loc.getLongitude());
						
						if (onConnectionSuccessful != null)
							onConnectionSuccessful.onsuccess(new LatLng(latti, longi),gps_time,accuracy);
					}
					
				}
				else
				{
					if (onConnectionSuccessful != null)
						onConnectionSuccessful.onfail();
				}

			}
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) 
	{
		if (onConnectionSuccessful != null)
			onConnectionSuccessful.onfail();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// Upon Connection, we are able to retrieve the Lat n Lng values.

		loc = locationClient.getLastLocation();
		if (loc != null) {
//			latti = loc.getLatitude();
//			longi = loc.getLongitude();
//			if (onConnectionSuccessful != null)
//				onConnectionSuccessful.onsuccess(new LatLng(latti, longi));

		}
		else
		{
			if (onConnectionSuccessful != null)
				onConnectionSuccessful.onfail();
		}
		

	}

	@Override
	public void onDisconnected()
	{
		instance=null;
		
		if (onConnectionSuccessful != null)
			onConnectionSuccessful.onfail();
		
	}

}
