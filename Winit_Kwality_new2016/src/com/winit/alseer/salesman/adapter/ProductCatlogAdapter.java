package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.Rotate3dAnimation;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.ProductCatlogDetailActivity;

public class ProductCatlogAdapter extends BaseAdapter
{
	private Context con;
	private Vector<ProductDO> vecProductDOs;
	
	public ProductCatlogAdapter(Context productCatalogActivity, Vector<ProductDO> vecProductDOs)
	{
		this.con = productCatalogActivity;
		this.vecProductDOs = vecProductDOs;
	}

	@Override
	public int getCount() 
	{
		if(vecProductDOs != null && vecProductDOs.size() > 0)
			return vecProductDOs.size();
		return 0;
	}

	@Override
	public Object getItem(int pos) 
	{
		return pos;
	}

	@Override
	public long getItemId(int pos) 
	{
		return pos;
	}

	@Override
	public View getView(final int pos, View view, ViewGroup arg2) 
	{
		if(view == null)
//		{
			view = ((Activity)con).getLayoutInflater().inflate(R.layout.coverflow_cell, null);
			ImageView iv 	= (ImageView) view.findViewById(R.id.ivCoverFlowChild);
			TextView tv 	= (TextView) view.findViewById(R.id.tvInfo);
			final LinearLayout llInfo			= (LinearLayout) view.findViewById(R.id.llInfo);
			LinearLayout llCoverImage			= (LinearLayout) view.findViewById(R.id.llCoverImage);
			
			iv.setImageResource(R.drawable.empty_photo);
			iv.setScaleType(ScaleType.CENTER_INSIDE);
//			if(info.length-1>pos)
		    tv.setText(vecProductDOs.get(pos).Description);
//			((LinearLayout)(view)).setLayoutParams(new Gallery.LayoutParams(AppConstants.DIVICE_WIDTH/3 + 50, AppConstants.DIVICE_HEIGHT/4 + 30));
			
		    view.setOnClickListener(new OnClickListener() 
		    {
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(con,ProductCatlogDetailActivity.class);
					intent.putExtra("ProductDo", vecProductDOs.get(pos));
					con.startActivity(intent);
				}
			});
		    
			BitmapDrawable b = (BitmapDrawable)(iv.getDrawable());
			b.setAntiAlias(true);
			final View convertView = view;
			llCoverImage.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if(llInfo.isShown())
						applyRotation(true, 0, -90, convertView);
					else
						applyRotation(false, 0, -90, convertView);
				}
			});
			llInfo.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					applyRotation(true, 0, -90, convertView);
				}
			});
			
//		}
		
		return view;
	}
	
	private void applyRotation(boolean isInfoShown, float start, float end, View llRotateView) 
	{
		// Find the center of the container
		final float centerX = llRotateView.getWidth() / 2.0f;
		final float centerY = llRotateView.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(isInfoShown, llRotateView));


		View image =  llRotateView.findViewById(R.id.llCoverImage);

//		ImageView temp = (ImageView) llRotateView.findViewById(R.id.ivTemp);
        	 image.startAnimation(rotation);
        	
//        image.startAnimation(rotation);
		
//		llRotateView.startAnimation(rotation);
	}
//
	private final class DisplayNextView implements Animation.AnimationListener 
	{
		private boolean isInfoShown;
		private View llRotateView;
		private DisplayNextView(boolean isInfoShown, View llRotateView) 
		{
			this.isInfoShown = isInfoShown;
			this.llRotateView =llRotateView;
		}

		public void onAnimationStart(Animation animation) 
		{
			
		}

		public void onAnimationEnd(Animation animation) 
		{
			llRotateView.post(new SwapViews(isInfoShown, llRotateView));
		}

		public void onAnimationRepeat(Animation animation) 
		{
		}
	}
//	
	 private final class SwapViews implements Runnable 
	 {
	        private boolean isInfoShown;
	        View llRotateView;
	        public SwapViews(boolean isInfoShown, View llRotateView) 
	        {
	        	this.isInfoShown 		= isInfoShown;
	            this.llRotateView 	= llRotateView;
	        }

	        public void run() 
	        {
	            final float centerX = llRotateView.getWidth() / 2.0f;
	            final float centerY = llRotateView.getHeight() / 2.0f;
	            Rotate3dAnimation rotation;
	            
	            View image  =  llRotateView.findViewById(R.id.llCoverImage);
//	            View llParentInfo =  llRotateView.findViewById(R.id.llInfo);
	            
	            View scInfo 	=  llRotateView.findViewById(R.id.scInfo);
//	            View llInfo 	=  llRotateView.findViewById(R.id.llInfo);
	            View iv 	= llRotateView.findViewById(R.id.ivCoverFlowChild);
//	            View tv 	= llRotateView.findViewById(R.id.tvInfo);
	            if (isInfoShown) 
	            {
//	            	image.setVisibility(View.VISIBLE);
//	            	llParentInfo.setVisibility(View.GONE);
	            	
	            	scInfo.setVisibility(View.GONE);
//	            	tv.setVisibility(View.GONE);
	            	iv.setVisibility(View.VISIBLE);
//		            llInfo.setVisibility(View.GONE);
//	            	
	                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
	            } 
	            else 
	            {
//	            	image.setVisibility(View.GONE);
//	            	llParentInfo.setVisibility(View.VISIBLE);	
	            	
	            	scInfo.setVisibility(View.VISIBLE);
//	            	tv.setVisibility(View.VISIBLE);
	            	iv.setVisibility(View.GONE);
//		            llInfo.setVisibility(View.VISIBLE);
	                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
	            }

	            rotation.setDuration(500);
	            rotation.setFillAfter(true);
	            rotation.setInterpolator(new DecelerateInterpolator());
	            rotation.setAnimationListener(new AnimationListener() 
	            {
					@Override
					public void onAnimationStart(Animation animation) 
					{	}
					
					@Override
					public void onAnimationRepeat(Animation animation)
					{	}
					
					@Override
					public void onAnimationEnd(Animation animation) 
					{
						
					}
				});
	            image.startAnimation(rotation);
	            	
	        }
	    }
}
