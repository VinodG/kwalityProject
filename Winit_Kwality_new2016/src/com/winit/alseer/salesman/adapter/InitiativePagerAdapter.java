package com.winit.alseer.salesman.adapter;

import httpimage.HttpImageManager;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class InitiativePagerAdapter extends PagerAdapter
{
	private Context context;
	private Vector<InitiativeDO> vecExecutionDOs;
	private String replace ="%s/..";
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
	
	public InitiativePagerAdapter(Context context,Vector<InitiativeDO> vecInitiativeDOs)
	{
		this.context = context;
		this.vecExecutionDOs = vecInitiativeDOs;
	}
	
	@Override
	public int getCount() 
	{
		return vecExecutionDOs.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view == ((LinearLayout) object);
	}
	
	public void refreshViewPager(Vector<InitiativeDO> vecPlanogramDOs)
	{
		this.vecExecutionDOs = vecPlanogramDOs;
		notifyDataSetChanged();
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) 
	{
		((ViewPager) container).removeView((LinearLayout) object);

	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) 
	{
		InitiativeDO executionDO = vecExecutionDOs.get(position);
		
		View convertView = LayoutInflater.from(context).inflate(R.layout.initiativeimage_cell, container,false);
		
		TextView tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
		ImageView ivInitiativeImage = (ImageView)convertView.findViewById(R.id.ivInitiativeImage);
		TextView tvRecommendedImage = (TextView)convertView.findViewById(R.id.tvRecommendedImage);
		LinearLayout llExecuted = (LinearLayout)convertView.findViewById(R.id.llExecuted);
		TextView tvDate = (TextView)convertView.findViewById(R.id.tvRecommendedImage);
		TextView tvView = (TextView)convertView.findViewById(R.id.tvRecommendedImage);
		
		if(position == 0)
		{
			tvTitle.setVisibility(View.GONE);
			llExecuted.setVisibility(View.GONE);
			tvRecommendedImage.setVisibility(View.VISIBLE);
		}
		else
		{
			tvTitle.setVisibility(View.VISIBLE);
			llExecuted.setVisibility(View.VISIBLE);
			tvRecommendedImage.setVisibility(View.GONE);
		}
		
		if(!executionDO.Planogram.contains(replace))
		{
			final Uri uri = Uri.parse(executionDO.Planogram);
			
			if (uri != null) 
			{
				Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivInitiativeImage,executionDO.Planogram));
				if (bitmap != null) 
				{
					ivInitiativeImage.setImageBitmap(bitmap);
				}
			}
		}
		else if(executionDO.Planogram != null)
		{
			if(executionDO.Planogram.contains(replace))
			{
				executionDO.Planogram = executionDO.Planogram.replace(replace, "http://10.20.53.27/AlSeer");
			}
//			mImageFetcher.loadImage(executionDO.Planogram, ivInitiativeImage);
		}
	    	
		
		((ViewPager) container).addView(convertView);

		return convertView;
	}
	
	public HttpImageManager getHttpImageManager () 
	{
		return ((MyApplicationNew) ((Activity) context).getApplication()).getHttpImageManager();
	}

}
