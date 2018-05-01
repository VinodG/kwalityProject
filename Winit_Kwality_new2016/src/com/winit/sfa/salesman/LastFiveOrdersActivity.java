package com.winit.sfa.salesman;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class LastFiveOrdersActivity extends BaseActivity{

	private LinearLayout llLastorders;
	private TextView tvlastordergheadTitle,tv_nolastorders,tv_lastordersitename;
	private ListView lv_lastorders;
	private LastOrdersAdapter lastorderadap;
	private JourneyPlanDO object;
	private Vector<TrxHeaderDO> vecLastOrders;
	@SuppressWarnings("unchecked")
	@Override
	public void initialize() 
	{

		llLastorders = (LinearLayout) inflater.inflate(R.layout.lastorders, null);
		llBody.addView(llLastorders, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		if(getIntent().getExtras() != null)
		{
			object	 = 	(JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
		}
		tv_lastordersitename.setText(object.siteName.trim()+" ["+mallsDetailss.site.trim()+"]" /*+ " (" +object.partyName.trim()+")"*/);
		lastorderadap = new LastOrdersAdapter(LastFiveOrdersActivity.this,new Vector<TrxHeaderDO>());
		lv_lastorders.setAdapter(lastorderadap);
	}
	
	private void initializeControls() 
	{

		tvlastordergheadTitle 	= (TextView) llLastorders.findViewById(R.id.tvlastordergheadTitle);
		tv_nolastorders 		= (TextView) llLastorders.findViewById(R.id.tv_nolastorders);
		tv_lastordersitename 	= (TextView) llLastorders.findViewById(R.id.tv_lastordersitename);
		lv_lastorders 			= (ListView) llLastorders.findViewById(R.id.lv_lastorders);
		lv_lastorders.setDividerHeight(0);
	}
	
	public class LastOrdersAdapter extends BaseAdapter
	{

		Context con;
		 Vector<TrxHeaderDO> veclastorderdetails;
		public LastOrdersAdapter(Context lastFiveOrdersActivity,Vector<TrxHeaderDO> vecLastOrders) 
		{
			this.con =lastFiveOrdersActivity;
			this.veclastorderdetails =vecLastOrders;
			
		}
		public void refresh(Vector<TrxHeaderDO> vecLastOrders) 
		{
			this.veclastorderdetails =vecLastOrders;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if(veclastorderdetails!=null&&veclastorderdetails.size()>0)
			{
				return veclastorderdetails.size();
			}
			else
			{
				return 0;
			}
			
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			convertView = inflater.inflate(R.layout.lastorderdetailcell, null);

			TextView tvorderdate,tvOrdernumber,tvOrderAmount;
			tvorderdate = (TextView) convertView.findViewById(R.id.tvorderdate);
			tvOrdernumber = (TextView) convertView.findViewById(R.id.tvOrdernumber);
			tvOrderAmount = (TextView) convertView.findViewById(R.id.tvOrderAmount);
			final TrxHeaderDO lastOrderHeaderDO =  veclastorderdetails.get(position);
			tvorderdate.setText(CalendarUtils.getFormatedDatefromString(""+lastOrderHeaderDO.trxDate));
			tvOrdernumber.setText(""+lastOrderHeaderDO.trxCode);
			tvOrderAmount.setText(curencyCode+" "+amountFormate.format(lastOrderHeaderDO.totalAmount - lastOrderHeaderDO.totalDiscountAmount +lastOrderHeaderDO.totalVATAmount));
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(LastFiveOrdersActivity.this, OrderSummaryDetail.class);
					intent.putExtra("trxHeaderDO", lastOrderHeaderDO);
					intent.putExtra("mallsDetails", mallsDetailss);
					startActivity(intent);
//					Intent in = new Intent(LastFiveOrdersActivity.this, LastOrderDetailsActivity.class);
//					in.putExtra("orderDetails", lastOrderHeaderDO);
//					in.putExtra("Sitename", lastOrderHeaderDO);
//					in.putExtra("SiteAddress", getAddress(object));
//					startActivity(in);
				}
			});
			return convertView;
			
		}
		
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		getListOfFiveOrder();
	}

	/**
	 * 
	 */
	private void getListOfFiveOrder() 
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				showLoader("Please wait...");
				String empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
				vecLastOrders 		= new CommonDA().getLastFiveOrder(empNO,object.site);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						if(vecLastOrders != null)
						{
							if(vecLastOrders.size()==1)
								tvlastordergheadTitle.setText("Current Month Invoice");
							else
								tvlastordergheadTitle.setText("Current Month Invoices");
						}
						if(vecLastOrders!=null&&vecLastOrders.size()>0)
						{
							tv_nolastorders.setVisibility(View.GONE);
							lastorderadap.refresh(vecLastOrders);
						}
						else
						{
							tv_nolastorders.setVisibility(View.VISIBLE);
						}
					}
				});
				
			}
		}).start();
	}

}
