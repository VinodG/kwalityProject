package com.winit.sfa.salesman;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.Rotate3dAnimation;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.kwalitysfa.salesman.R;

public class ProductCatlogDetailActivity extends BaseActivity
{
	private LinearLayout llProductDetail,llInfo,llCoverImage;
	private ProductDO productDo;
	private TextView tvProductTitle,tvProductDescription,tv;
	private ImageView iv;
	
	@Override
	public void initialize() 
	{
		llProductDetail = (LinearLayout)inflater.inflate(R.layout.grid_product_cell, null);
		
		tvProductTitle = (TextView) llProductDetail.findViewById(R.id.tvProductTitle);
		tvProductDescription = (TextView) llProductDetail.findViewById(R.id.tvProductDescrip);
		
		iv			  	= (ImageView) llProductDetail.findViewById(R.id.ivCoverFlowChild);
		tv			  	= (TextView) llProductDetail.findViewById(R.id.tvInfo);
		llInfo 			= (LinearLayout) llProductDetail.findViewById(R.id.llInfo);
		llCoverImage	= (LinearLayout) llProductDetail.findViewById(R.id.llCoverImage);
		
		iv.setImageResource(R.drawable.empty_photo);
		iv.setScaleType(ScaleType.CENTER_INSIDE);
		
		if(getIntent().hasExtra("ProductDo"))
			productDo = (ProductDO) getIntent().getExtras().get("ProductDo"); 
		
		if(productDo != null)
		{
			tv.setText(productDo.Description);
			tvProductTitle.setText(productDo.Description);
			tvProductDescription.setText(productDo.Description);
		}
		
		BitmapDrawable b = (BitmapDrawable)(iv.getDrawable());
		b.setAntiAlias(true);
		final View convertView = llCoverImage;
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
		
		llBody.addView(llProductDetail, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
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

        image.startAnimation(rotation);
        	
	}
	
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
	            View scInfo 	=  llRotateView.findViewById(R.id.scInfo);
	            View iv 	= llRotateView.findViewById(R.id.ivCoverFlowChild);
	            
	            if (isInfoShown) 
	            {
	            	scInfo.setVisibility(View.GONE);
	            	iv.setVisibility(View.VISIBLE);
	                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
	            } 
	            else 
	            {
	            	scInfo.setVisibility(View.VISIBLE);
	            	iv.setVisibility(View.GONE);
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
