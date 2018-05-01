package com.winit.sfa.salesman;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.kwalitysfa.salesman.R;

public class ReturnStockOption extends BaseActivity
{
	//declaration of variables
	private LinearLayout llSummaryofDay;
	private TextView tvDaySummary, tvSellable, tvNonSellable;

	@Override
	public void initialize() 
	{
		//inflate the summary-of-day layout
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.return_stock_option, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intialiseControls();
		
		setTypeFaceRobotoNormal(llSummaryofDay);
		
		tvSellable.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(ReturnStockOption.this, LoadRequestActivity.class);
				intent.putExtra("load_type", AppConstants.UNLOAD_STOCK);
				intent.putExtra("isSalable", true);
				intent.putExtra("isUnload", true);
				startActivity(intent);
			}
		});
		
		tvNonSellable.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(ReturnStockOption.this, LoadRequestActivity.class);
				intent.putExtra("load_type", AppConstants.UNLOAD_STOCK);
				intent.putExtra("isSalable", false);
				intent.putExtra("isUnload", true);
				startActivity(intent);
			}
		});
		
	}
	
	
	public void intialiseControls()
	{
		//getting Id's of TextView
		tvDaySummary		=	(TextView)llSummaryofDay.findViewById(R.id.tvDaySummary);
		tvSellable			=	(TextView)llSummaryofDay.findViewById(R.id.tvSellable);
		tvNonSellable		=	(TextView)llSummaryofDay.findViewById(R.id.tvNonSellable);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			setResult(2000);
			finish();
		}
	}
}
