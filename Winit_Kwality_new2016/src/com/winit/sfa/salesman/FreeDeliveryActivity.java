package com.winit.sfa.salesman;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;

public class FreeDeliveryActivity extends BaseActivity
{
	private LinearLayout llFreeDelivery;
	private TextView tvPageTitle;
	private String SiteName = "";
	private Button btnAddItems, btnSave;
	private TextView tvCustomerName;
	@Override
	public void initialize()
	{
		llFreeDelivery = (LinearLayout) inflater.inflate(R.layout.free_delivery_list, null);
		llBody.addView(llFreeDelivery,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras() != null)
		{
			SiteName = getIntent().getExtras().getString("SiteName");
		}
		intialiseControls();
		
		tvCustomerName.setText(SiteName);
		
		setTypeFaceRobotoNormal(llFreeDelivery);
	}
	private void intialiseControls() 
	{
		tvPageTitle	= (TextView)llFreeDelivery.findViewById(R.id.tvPageTitle);
		tvCustomerName		= (TextView)llFreeDelivery.findViewById(R.id.tvCustomerName);
		btnAddItems			= (Button)llFreeDelivery.findViewById(R.id.btnAddItems);
		btnSave				= (Button)llFreeDelivery.findViewById(R.id.btnSave);
		
		tvPageTitle.setText("Free Delivery");
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		btnAddItems.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSave.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
}
