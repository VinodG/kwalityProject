package com.winit.sfa.salesman;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.winit.kwalitysfa.salesman.R;

public class ThingsToFocusActivity extends BaseActivity
{
	private LinearLayout llThingstoFocus,llPagerTab;
	private ViewPager pager;
	private Vector<String> vecThingstofocusDO = new Vector<String>();
	private MyPagerAdapter adapter;
	public String htmlfiles[] = {"Page1.html","Page2.html","Page3.html"};
	
	@Override
	public void initialize() 
	{
		llThingstoFocus  = (LinearLayout)inflater.inflate(R.layout.thingstofocus, null);
		pager 				= (ViewPager) llThingstoFocus.findViewById(R.id.pager);
		llPagerTab 					=   (LinearLayout) llThingstoFocus.findViewById(R.id.llPagerTab);
		
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				for (int i = 0; i < htmlfiles.length; i++) 
				{
					vecThingstofocusDO.add(htmlfiles[i]);
				}
				
				if(vecThingstofocusDO!=null && vecThingstofocusDO.size()>0)
				{
					adapter = new MyPagerAdapter(vecThingstofocusDO);
					pager.setAdapter(adapter);
					
					refreshPageController(false);
					
					pager.setOnPageChangeListener(new OnPageChangeListener() 
					{
						@Override
						public void onPageSelected(int arg0) 
						{
							
						}
						
						@Override
						public void onPageScrolled(int arg0, float arg1, int arg2) 
						{
							refreshPageController(true);
						}
						
						@Override
						public void onPageScrollStateChanged(int arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				}				
				
			}
		});
		
		pager.setOffscreenPageLimit(3);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setEnabled(false);
		pager.setClickable(false);
		
		
		
		llBody.addView(llThingstoFocus, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private class MyPagerAdapter extends PagerAdapter 
	{
		Vector<String> vectorthingstofocus;

		public MyPagerAdapter(Vector<String> vecThingstofocusDO) 
		{
			this.vectorthingstofocus = vecThingstofocusDO;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}


		@Override
		public Object instantiateItem(final View collection, final int position)
		{
			
			LinearLayout llCell;
//			TextView tv_title,tv_content;
			llCell = (LinearLayout) getLayoutInflater().inflate(R.layout.thingstofocus_pagercell,null);
//			final ImageView iv_contentimg = (ImageView) llCell.findViewById(R.id.iv_contentimg);
//			tv_title    = (TextView) llCell.findViewById(R.id.tv_title);
//			tv_content  = (TextView) llCell.findViewById(R.id.tv_content);
			
			WebView wvHtml = (WebView)llCell.findViewById(R.id.wvHtml);
			wvHtml.getSettings().setJavaScriptEnabled(true);
			wvHtml.loadUrl("file:///android_asset/"+vecThingstofocusDO.get(position));
			
			llCell.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			iv_contentimg.setImageResource(vectorthingstofocus.);
//			UrlImageViewHelper.setUrlDrawable(iv_contentimg, vectorthingstofocus.get(position).ImagePath, R.drawable.a1, new UrlImageViewCallback() 
//			{
//				
//				@Override
//				public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
//						boolean loadedFromCache) 
//				{
//					// TODO Auto-generated method stub
//					if(!loadedFromCache)
//						iv_contentimg.setImageBitmap(loadedBitmap);
//				}
//			});
			
//			../Data/ThingsToFocus/keep-focus635503491792344941.PNG
//			String imagePath = "http://208.109.154.54/alseersfav3";
//			http://208.109.154.54/alseersfav3../Data/ThingsToFocus/Penguins635502777846944329.PNG
//			String imgpathfromserver = vectorthingstofocus.get(position).ImagePath.replace("~","");
//			imgpathfromserver = imgpathfromserver.replaceAll(".", "");
			
//			Log.e("imageurl",imagePath+imgpathfromserver);
			
//			 UrlImageViewHelper.setUrlDrawable(iv_contentimg, imagePath+vectorthingstofocus.get(position).ImagePath.replace("~",""), R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
			
			((ViewPager) collection).addView(llCell);
//			tv_title.setText(vectorthingstofocus.get(position).Title);
//			tv_content.setText(vectorthingstofocus.get(position).Content);
			setTypeFaceRobotoNormal(llCell);
            return llCell;
		}

		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((LinearLayout) view);
        }

		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view == ((LinearLayout) object);
		}

		@Override
		public int getCount() 
		{
			return vecThingstofocusDO.size();
		}
		
	}
	
	private void refreshPageController(boolean onscroll) 
	{
		int pagerPosition = 0;
		
		if(!onscroll)
		{
			llPagerTab.removeAllViews();
			
			for (int i = 0; i <= (adapter.getCount()-1); i++)
			{
				final ImageView imgvPagerController = new ImageView(ThingsToFocusActivity.this);
				imgvPagerController.setPadding(2,2,2,2);
				
				imgvPagerController.setImageResource(R.drawable.pager_dot);
				llPagerTab.addView(imgvPagerController);
			}	
			
			pagerPosition = pager.getCurrentItem();
			
			if(((ImageView)llPagerTab.getChildAt(pagerPosition)) != null)
				((ImageView)llPagerTab.getChildAt(pagerPosition)).setImageResource(R.drawable.pager_dot_h);
			
		}
		else
		{
			pagerPosition = pager.getCurrentItem();
			
			for (int i = 0; i <= (adapter.getCount()-1); i++)
			{
				if(i == pagerPosition)
				{
					if(((ImageView)llPagerTab.getChildAt(i)) != null)
						((ImageView)llPagerTab.getChildAt(i)).setImageResource(R.drawable.pager_dot_h);
				}
				else
				{
					if(((ImageView)llPagerTab.getChildAt(i)) != null)
						((ImageView)llPagerTab.getChildAt(i)).setImageResource(R.drawable.pager_dot);
				}
			}
		}
			
	}

}
