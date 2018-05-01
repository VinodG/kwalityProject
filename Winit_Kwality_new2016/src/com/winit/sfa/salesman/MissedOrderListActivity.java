package com.winit.sfa.salesman;

import java.util.Vector;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class MissedOrderListActivity extends BaseActivity
{
	private LinearLayout llMissedOrderList ;
	private TextView tvHeader, tvAgenciesName,tvNoDataFound;
	private ListView lvOrderList;
	
	private Vector<TrxHeaderDO> vecOrderList;
	private OrderListAdapter orderListAdapter;
	
	@Override
	public void initialize() 
	{
		llMissedOrderList			=	(LinearLayout)inflater.inflate(R.layout.missedorderlist,null);
		llBody.addView(llMissedOrderList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		setTypeFaceRobotoNormal(llMissedOrderList);
		initializeControls();
		loadData();
		
	}
	
	private void initializeControls()
	{
		tvHeader 	 		= (TextView)llMissedOrderList.findViewById(R.id.tvHeader);
		tvAgenciesName		= (TextView) llMissedOrderList.findViewById(R.id.tvAgenciesName);
		tvNoDataFound		=	(TextView)llMissedOrderList.findViewById(R.id.tvNoDataFound);
		lvOrderList			= (ListView) llMissedOrderList.findViewById(R.id.lv);
		
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setScrollbarFadingEnabled(true);
		tvAgenciesName.setText(mallsDetailss.siteName+" ["+mallsDetailss.site+"]");
		
	}
	private void loadData()
	{
		
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				String empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
				vecOrderList = new CommonDA().getMissedOrder(empNO, mallsDetailss.site);
				
				runOnUiThread(new Runnable() 
				{
					public void run() 
					{
						if(vecOrderList!=null && vecOrderList.size()>0)
						{
							
							orderListAdapter = new OrderListAdapter(vecOrderList);
							lvOrderList.setAdapter(orderListAdapter);
							
							tvNoDataFound.setVisibility(View.GONE);
							lvOrderList.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoDataFound.setVisibility(View.VISIBLE);
							lvOrderList.setVisibility(View.GONE);
						}
					}
				});
			}
		}).start();
	}
	
	
	public class OrderListAdapter extends BaseAdapter
	{
		private Vector<TrxHeaderDO> vecOrderList;
		public OrderListAdapter(Vector<TrxHeaderDO> vecOrderList) 
		{
			this.vecOrderList = vecOrderList;
		}
		@Override
		public int getCount() 
		{
			if(vecOrderList!=null && vecOrderList.size()>0)
				return vecOrderList.size();
			
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
		public void refresh(Vector<TrxHeaderDO> vecOrder)
		{
			this.vecOrderList=vecOrder;
			notifyDataSetChanged();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			TrxHeaderDO trxHeaderDO			    = (TrxHeaderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					    =	LayoutInflater.from(MissedOrderListActivity.this).inflate(R.layout.delivery_status_list_item, null);
			
			TextView tvJournyeDate			 	=	(TextView)convertView.findViewById(R.id.tvJournyeDate);
			TextView tvJournyeDateYear		  	=	(TextView)convertView.findViewById(R.id.tvJournyeDateYear);
			
			
			TextView tvOrderNo					=	(TextView)convertView.findViewById(R.id.tvOrderNo);
			TextView tvCustomerName				=	(TextView)convertView.findViewById(R.id.tvCustomerName);
			TextView tvCustomerLocation			=	(TextView)convertView.findViewById(R.id.tvCustomerLocation);
			TextView tvSatus					=	(TextView)convertView.findViewById(R.id.tvStatus);
			
			TextView tvCustomerPrice			=	(TextView)convertView.findViewById(R.id.tvCustomerPrice);
			TextView tvCustomerPriceUnit		=	(TextView)convertView.findViewById(R.id.tvCustomerPriceUnit);
			
		
			
			tvSatus.setVisibility(View.GONE);
			String date[] = CalendarUtils.getRequiredDateFormat(""+trxHeaderDO.trxDate).split("yy");
			tvJournyeDate.setText(date[0]);
			tvJournyeDateYear.setText(date[1]);
			
			tvOrderNo.setText("Order No: "+trxHeaderDO.trxCode);
			
			tvCustomerPrice.setText(""+trxHeaderDO.totalAmount);
			tvCustomerPriceUnit.setVisibility(View.VISIBLE);			
			
			tvCustomerName.setText(trxHeaderDO.siteName+"");
			tvCustomerLocation.setText(trxHeaderDO.address+"");
			
			convertView.setTag(trxHeaderDO);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = null;
					TrxHeaderDO objOrderList = 	(TrxHeaderDO) v.getTag();
					
					objOrderList = (TrxHeaderDO) v.getTag();
					intent = new Intent(MissedOrderListActivity.this , OrderSummaryDetail.class);
					intent.putExtra("trxHeaderDO", objOrderList);
					intent.putExtra("mallsDetails", mallsDetailss);
					startActivity(intent);
				}
			});
			
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			
			tvOrderNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			return convertView;
		}
	}
}
