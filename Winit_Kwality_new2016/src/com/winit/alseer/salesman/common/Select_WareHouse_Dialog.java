package com.winit.alseer.salesman.common;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;


public class Select_WareHouse_Dialog extends Dialog
{
	public Select_WareHouse_Dialog(Context context) 
	{
		super(context,R.style.Dialog);
		LayoutInflater inflater =getLayoutInflater();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout llEntirechrt = (LinearLayout) inflater.inflate(R.layout.select_warehouse_popup, null);
		this.setCancelable(true);
		setContentView(llEntirechrt, new LayoutParams(new Preference(context).getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH,320)-30,LayoutParams.WRAP_CONTENT));
		
		setTypeFace(llEntirechrt);
	}
	
	public void setTypeFace(ViewGroup group) 
	{
	     int count = group.getChildCount();
	     View v;
	     for(int i = 0; i < count; i++) {
	         v = group.getChildAt(i);
	         if(v instanceof TextView || v instanceof Button || v instanceof EditText/*etc.*/)
	             ((TextView)v).setTypeface(AppConstants.Roboto_Condensed_Bold);
	         else if(v instanceof ViewGroup)
	        	 setTypeFace((ViewGroup)v);
	     }
	}
}