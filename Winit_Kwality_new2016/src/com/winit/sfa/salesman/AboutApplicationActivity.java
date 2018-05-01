package com.winit.sfa.salesman;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.kwalitysfa.salesman.R;

public class AboutApplicationActivity extends BaseActivity
{
	//.layout.preseller_receive_payment.xml
	private LinearLayout llAssetsList ;
	private TextView tvHeader,tvVersion;
	
	@Override
	public void initialize() 
	{
		
		llAssetsList			=	(LinearLayout)inflater.inflate(R.layout.about_application,null);
		llBody.addView(llAssetsList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		tvVersion.setText("Kwality SFA: LIVE"+getVersionName());
		
		setTypeFaceRobotoNormal(llAssetsList);
		tvHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	private void initializeControls()
	{
		tvHeader  = (TextView) llAssetsList.findViewById(R.id.tvHeader);
		tvVersion  = (TextView) llAssetsList.findViewById(R.id.tvVersion);
	}
	//AlarmService(1191): called1

}
