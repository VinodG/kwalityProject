package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class UnVisitedCustomerActivity extends BaseActivity
{

	private LinearLayout llUnVisitedCustomer;
	private Button btnSubmit;
	private TextView tvPageTitle;
	private ListView lvCustomerList;
	private UnVisitedCustomers adapter;
	private ArrayList<JourneyPlanDO> arrJP;
	private Vector<String> arrReason;
	
	@Override
	public void initialize() 
	{
		llUnVisitedCustomer 	= (LinearLayout) inflater.inflate(R.layout.unvisited_customer_list, null);
		llBody.addView(llUnVisitedCustomer,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		bindControls();
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				showLoader("Loading UnVisited Customers..");
				arrJP = new CustomerDetailsDA().getUnvisitedCustomers();
				arrReason = new CommonDA().getReasonBasedOnType("Skip Customer");
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						adapter.refresh(arrJP);
						hideLoader();
					}

				});
			}
		}).start();
	}

	private void initializeControls() 
	{
		tvPageTitle		=	(TextView) llUnVisitedCustomer.findViewById(R.id.tvPageTitle);
		
		btnSubmit		=	(Button) llUnVisitedCustomer.findViewById(R.id.btnSubmit);
		
		lvCustomerList	=	(ListView) llUnVisitedCustomer.findViewById(R.id.lvCustomerList);
	}

	private void bindControls() 
	{
		tvPageTitle.setText("Un Visited Customers");
		
		adapter = new UnVisitedCustomers(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(adapter);
		
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				JourneyPlanDO objJourneyPlan;
				int count = 0;
				for (int i = 0; i < arrJP.size(); i++) 
				{
					objJourneyPlan = arrJP.get(i);
					if(objJourneyPlan != null && 
							(objJourneyPlan.reasonForSkip == null || objJourneyPlan.reasonForSkip.equalsIgnoreCase("")))
					{
						count++;
						break;
					}
				}
				if(count > 0)
				{
					showCustomDialog(UnVisitedCustomerActivity.this, getString(R.string.warning), "Please select Skip Reason for all unvisited customers.", getString(R.string.OK), null, "skipReason");
				}
				else
				{
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							showLoader("Saving Unvisited customers..");
							new CustomerDA().insertUnVisitedCutomers(arrJP);
							uploadData();
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									hideLoader();
									finish();
								}
							});
						}
					}).start();
				}
			}
		});
	}
	
	class UnVisitedCustomers extends BaseAdapter
	{
		private ArrayList<JourneyPlanDO> arrayList;
		
		public UnVisitedCustomers(ArrayList<JourneyPlanDO> arrayList) 
		{
			this.arrayList	=	arrayList;
		}

		public void refresh(ArrayList<JourneyPlanDO> arrJP) 
		{
			this.arrayList = arrJP;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if(arrayList != null)
				return arrayList.size();
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
			final JourneyPlanDO objJourneyPlan = arrayList.get(position);
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.unvisited_customers_cell, null);
			
			TextView tvSiteID		=	(TextView)convertView.findViewById(R.id.tvSiteID1);
			TextView tvSiteName		=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvReason		=	(TextView) convertView.findViewById(R.id.tvReason);
			
			tvSiteID.setText(objJourneyPlan.site);
			tvSiteID.setVisibility(View.VISIBLE);
			tvSiteName.setText(objJourneyPlan.siteName);//tblReasons
			if(objJourneyPlan.reasonForSkip != null && !objJourneyPlan.reasonForSkip.equalsIgnoreCase(""))
				tvReason.setText(objJourneyPlan.reasonForSkip);
			else
				tvReason.setText("");
			objJourneyPlan.JourneyCode = preference.getStringFromPreference(Preference.USER_ID, "")+CalendarUtils.getOrderPostDate();
			objJourneyPlan.reasonType = "Skip Customer";
			objJourneyPlan.userID = preference.getStringFromPreference(preference.USER_ID, "");
			objJourneyPlan.JourneyDate = CalendarUtils.getCurrentDateTime();
			objJourneyPlan.clientCode = objJourneyPlan.site;
			
			tvReason.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v) 
				{
					CustomBuilder customBuilder = new CustomBuilder(UnVisitedCustomerActivity.this, "Select Skip Reason", true);
					customBuilder.setSingleChoiceItems(arrReason, objJourneyPlan.reasonForSkip, new CustomBuilder.OnClickListener() 
					{
						@Override
						public void onClick(CustomBuilder builder, Object selectedObject) 
						{
							v.setTag((String)selectedObject);
							((TextView)v).setText((String)selectedObject);
							objJourneyPlan.reasonForSkip = (String)selectedObject;
							builder.dismiss();
						}
					});
					customBuilder.show();
				}
			});
			return convertView;
		}
		
	}
}
