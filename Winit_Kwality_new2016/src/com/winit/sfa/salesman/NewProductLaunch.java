package com.winit.sfa.salesman;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.ViewFlow;
import com.winit.kwalitysfa.salesman.R;

public class NewProductLaunch extends BaseActivity
{
	private LinearLayout llMonthelyPerformance;
	private ViewFlow mViewFlow;
	private boolean isFromLogin = false;
	private Bitmap bmp;
	
	//header bar
	private TextView tvPageTitle;
	private Button btn;

	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llMonthelyPerformance.findViewById(R.id.tvPageTitle);
		btn	= (Button) llMonthelyPerformance.findViewById(R.id.btn);
		tvPageTitle.setText("Things to Focus");
		btn.setText("Start Day");
//		btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_submit), null, null, null);
//		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed);
//		btn.setTypeface(AppConstants.Roboto_Condensed);
	}
	private void initializeControles()
	{
		mViewFlow   = (ViewFlow) llMonthelyPerformance.findViewById(R.id.mViewFlow);
	}
	@Override
	public void initialize() 
	{
		llMonthelyPerformance = (LinearLayout) inflater.inflate(R.layout.monthely_performance, null);
		llBody.addView(llMonthelyPerformance, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		if(getIntent().getExtras() != null)
			isFromLogin = getIntent().getExtras().getBoolean("isFromLogin");

		setHeaderBar();
		if(!isFromLogin)
		{
			btn.setText("Finish");
//			btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.check5), null, null, null); 
		}
		else
		{
			btn.setText("Start Day");
		}
		
		btn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(isFromLogin)
				{
					Intent intent = new Intent(NewProductLaunch.this, PresellerJourneyPlan.class);
					intent.putExtra("Latitude", 25.522);
					intent.putExtra("Longitude", 78.522); //added from the previous code
					startActivity(intent);
					finish();
				}
				else
					finish();
			}
		});
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		initializeControles();
		
		mViewFlow.setAdapter(new GraphAdapter());
	}
	
	private class GraphAdapter extends BaseAdapter
	{
		@Override
		public int getCount() 
		{
			return 2;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			switch (position)
			{
				case 0:
					convertView 		= (ScrollView) inflater.inflate(R.layout.thingstofocus1, null);
					TextView tv_thingstofocus1,tv_newproduct1,tv_des,tv_axeanarchy,tv_des2,tv_des3;
					tv_newproduct1 = (TextView) convertView.findViewById(R.id.tv_newproduct1);
					tv_des = (TextView) convertView.findViewById(R.id.tv_des);
					tv_axeanarchy = (TextView) convertView.findViewById(R.id.tv_axeanarchy);
					tv_des2 = (TextView) convertView.findViewById(R.id.tv_des2);
					tv_des3 = (TextView) convertView.findViewById(R.id.tv_des3);
					break;
				case 1:
					convertView 		= (ScrollView) inflater.inflate(R.layout.thingstofocus2, null);
					TextView tv_thingstofocus2,tv_newproduct2,tv_surf,tv_surfdes1,tv_surfdes2,tv_surfdes3,tv_terms,tv_terms1;
					tv_newproduct2 = (TextView) convertView.findViewById(R.id.tv_newproduct2);
					tv_surf = (TextView) convertView.findViewById(R.id.tv_surf);
					tv_surfdes1 = (TextView) convertView.findViewById(R.id.tv_surfdes1);
					tv_surfdes2 = (TextView) convertView.findViewById(R.id.tv_surfdes2);
					tv_surfdes3 = (TextView) convertView.findViewById(R.id.tv_surfdes3);
					tv_terms1 = (TextView) convertView.findViewById(R.id.tv_terms1);
					
				break;
			}
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
	}
	
	public void setFont(ViewGroup viewGroup)
	{
		for(int i=0;i<viewGroup.getChildCount();i++)
		{
			View view = viewGroup.getChildAt(i);
			if(view instanceof TextView)
			{
				((TextView)view).setTypeface(AppConstants.Roboto_Condensed_Bold);
			}
			else if(view instanceof ViewGroup)
				setFont((ViewGroup)view);
		}
	}
    
    @Override
    public void onBackPressed()
    {
    	if(isFromLogin)
    	{
	    	Intent intent = new Intent(NewProductLaunch.this,PresellerJourneyPlan.class);
			intent.putExtra("isFromLogin", true);
			startActivity(intent);
			finish();
    	}
    	else
    		finish();
    }
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	if(bmp!=null && !bmp.isRecycled())
        	bmp.recycle();
    }
}
