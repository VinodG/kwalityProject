package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.kwalitysfa.salesman.R;

public class GridAdapter extends BaseAdapter
{
	public Vector<BrandDO> vecCategory = new Vector<BrandDO>();
	public Context context;
    public static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    
	public GridAdapter(Vector<BrandDO> vecCategory,Context context) 
	{
		this.vecCategory = vecCategory;
		this.context= context;
	}
	
	@Override
	public int getCount() 
	{
		if(vecCategory != null && vecCategory.size() > 0)
			return vecCategory.size();
			else
				return 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final BrandDO objiItem  = vecCategory.get(position);
		convertView			   	= (RelativeLayout)((Activity) context).getLayoutInflater().inflate(R.layout.grid_view_cell, null);
//		RecyclingImageView ivGridImage 	= (RecyclingImageView)convertView.findViewById(R.id.ivGridImage);
		
		if(objiItem.isDone)
			convertView.setBackgroundResource(R.drawable.round_transparent_green_bg);
		else
			convertView.setBackgroundResource(R.drawable.round_transparent_bg);
		
//		if(objiItem.brandName.contains(AppConstants.MUST_HAVE) || objiItem.brandName.contains(AppConstants.NEW_LAUNCHES))
//			ivGridImage.setBackgroundResource(getResId(objiItem.image));
//		else if(objiItem.image != null && objiItem.image.length() > 0)
//			mImageFetcher.loadImage(objiItem.image, ivGridImage);
//		else
//			ivGridImage.setImageDrawable(context.getResources().getDrawable(R.drawable.empty_photo));
		
		convertView.setTag(objiItem);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return convertView;
	}
	
	public int getResId(String image) 
	{
		int id = -1;
		try
		{
			id = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
			
			if(id <= 0)
				id = R.drawable.app_logo;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			id = R.drawable.app_logo;
		}
		
		return id;
	}
}
