package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

public class ProductCategroyAdapter extends BaseAdapter
{

	private int productCategoryImages[] = {R.drawable.water,R.drawable.cup,R.drawable.popcorn,R.drawable.tishhu,R.drawable.shirt,R.drawable.rice,R.drawable.star};
	private String productCategoryName[]={"Water","Juice ","Chips ","Tissue "," Boutique "," Rice "," Robinson "};

	private Vector<View> views;
	private Context context;
	private boolean isFirstTime=true;
	public ProductCategroyAdapter(Context context) 
	{
		this.context = context;
		views = new Vector<View>();
	}
	@Override
	public int getCount() 
	{
		return productCategoryImages.length;
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
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		convertView           		=	(LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.product_category_cell, null);
		ImageView ivCategoryImage	=	(ImageView)convertView.findViewById(R.id.ivCategoryImage);
		TextView tvCategoryName		=	(TextView)convertView.findViewById(R.id.tvCategoryName);
		ivCategoryImage.setImageResource(productCategoryImages[position]);
		tvCategoryName.setText(productCategoryName[position]);
		views.add(convertView);
		
		convertView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isFirstTime = false;
				resetBackGrounds();
				v.setBackgroundResource(R.drawable.one);
			}
		});
		if(isFirstTime && position ==0)
		{
			convertView.performClick();
		}
		convertView.setLayoutParams(new ListView.LayoutParams((int)(79 * BaseActivity.px),(int)(65 * BaseActivity.px)));
		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		return convertView;
	}
	public void resetBackGrounds()
	{
		for(View convertView:views)
		{
			if(convertView!=null)
				convertView.setBackgroundResource(R.drawable.one2);
		}
	}
}
