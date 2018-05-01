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

import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class GalleryAdapter extends PagerAdapter
{
	private Context context;
	private LayoutInflater inflater;
	private Vector<DamageImageDO> vecGallery;
	public GalleryAdapter(Context context,Vector<DamageImageDO> vecGallery) 
	{
		this.context = context;
		this.vecGallery = vecGallery;
	}

	@Override
	public int getCount() 
	{
		if(vecGallery!=null && vecGallery.size()>0)
			return vecGallery.size();
		else
			return 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{

		return view == ((LinearLayout) object);
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) 
	{
		DamageImageDO galObject = vecGallery.get(position);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View itemView = inflater.inflate(R.layout.gallery_layout_cell, container, false);
		ImageView ivFullImage = (ImageView)itemView.findViewById(R.id.ivFullImage);
		
		ivFullImage.setTag(galObject.ImagePath);
		final Uri uri = Uri.parse(galObject.ImagePath);
		if (uri != null) 
		{
			Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivFullImage,galObject.ImagePath));
			if (bitmap != null) 
			{
				ivFullImage.setImageBitmap(bitmap);
			}
		}
		
		((ViewPager) container).addView(itemView);
		return itemView;
	}
	@Override
	public void destroyItem(View container, int position, Object object) 
	{
		((ViewPager) container).removeView((LinearLayout) object);
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) context)
				.getApplication()).getHttpImageManager();
	}
	
}
