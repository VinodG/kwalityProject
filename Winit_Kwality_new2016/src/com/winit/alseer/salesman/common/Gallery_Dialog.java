package com.winit.alseer.salesman.common;


import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;


public class Gallery_Dialog extends Dialog
{
	public ViewPager viewPager;
	public TextView tvCount;
	public Preference preference;
	public Gallery_Dialog(Context context) 
	{
		super(context,R.style.Dialog);
		LayoutInflater inflater =getLayoutInflater();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		preference 	= 		new Preference(context);
		LinearLayout llEntirechrt = (LinearLayout) inflater.inflate(R.layout.gallery_pager, null);
		this.setCancelable(true);
		setContentView(llEntirechrt, new LayoutParams(LayoutParams.MATCH_PARENT,preference.getIntFromPreference(Preference.DEVICE_DISPLAY_HEIGHT, 320)/2));
		((BaseActivity)context).setTypeFaceRobotoNormal(llEntirechrt);

		viewPager			=	(ViewPager)llEntirechrt.findViewById(R.id.galleryPager);
		tvCount			=	(TextView)llEntirechrt.findViewById(R.id.tvCount);
	}

}