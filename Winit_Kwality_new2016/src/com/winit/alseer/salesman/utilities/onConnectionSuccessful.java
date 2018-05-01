package com.winit.alseer.salesman.utilities;

import com.google.android.gms.maps.model.LatLng;

public interface onConnectionSuccessful 
{
	public abstract void onsuccess(LatLng latLng,long gps_time,double speed);
	public abstract void onfail();
}
