package com.winit.sfa.salesman;

import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.LastOrderHeaderDO;
import com.winit.alseer.salesman.dataobject.OrderDetailsDO;
import com.winit.kwalitysfa.salesman.R;

public class LastOrderDetailsActivity extends BaseActivity {
	private LinearLayout llLastorders;
	private LastOrderHeaderDO orderDetails;
	private OrdersAdapter orderadap;
	private ListView lv_lastorders;
	private String SiteName, SiteAddress;
	private Vector<OrderDetailsDO> vecProductDO;
	private TextView tv_sitename,tv_sitenameAddress,tvorderdate,tvOrdernumber,tvOrderAmount,tv_nolastorders;

	@Override
	public void initialize() {
		llLastorders = (LinearLayout) inflater.inflate(R.layout.lastordersdetails,null);
		llBody.addView(llLastorders, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		initializeControls();

		if (getIntent().getExtras() != null) {
			orderDetails = (LastOrderHeaderDO) getIntent().getExtras().getSerializable("orderDetails");
			SiteName = getIntent().getStringExtra("Sitename");
			SiteAddress = getIntent().getStringExtra("SiteAddress");
		}
	}

	private void initializeControls() 
	{
		
		tv_sitename 		= (TextView) llLastorders.findViewById(R.id.tv_sitename);
		tv_sitenameAddress = (TextView) llLastorders.findViewById(R.id.tv_sitenameAddress);
		tvorderdate 				= (TextView) llLastorders.findViewById(R.id.tvorderdate);
		tvOrdernumber 				= (TextView) llLastorders.findViewById(R.id.tvOrdernumber);
		tvOrderAmount 				= (TextView) llLastorders.findViewById(R.id.tvOrderAmount);
		tv_nolastorders 			= (TextView) llLastorders.findViewById(R.id.tv_nolastorders);
		lv_lastorders 				= (ListView) llLastorders.findViewById(R.id.lv_lastorders);
		if(SiteName!=null)
			tv_sitename.setText(SiteName);
		if(SiteAddress!=null)
			tv_sitenameAddress.setText(SiteAddress);
			
		if(orderDetails!=null)
		{
			tvorderdate.setText(orderDetails.Order_Date);
			tvOrdernumber.setText(orderDetails.OrderNumber);
			tvOrderAmount.setText(orderDetails.TotalAmount);
		}
		orderadap = new OrdersAdapter(LastOrderDetailsActivity.this,new Vector<OrderDetailsDO>());
		lv_lastorders.setAdapter(orderadap);
	}

	public class OrdersAdapter extends BaseAdapter 
	{
		Context con;
		Vector<OrderDetailsDO> vecsProductDO;

		public OrdersAdapter(Context lastFiveOrdersActivity,Vector<OrderDetailsDO> vecProductDO) 
		{
			this.con = lastFiveOrdersActivity;
			this.vecsProductDO = vecProductDO;

		}

		public void refresh(Vector<OrderDetailsDO> vecProductDO) 
		{
			this.vecsProductDO = vecProductDO;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (vecsProductDO != null && vecsProductDO.size() > 0) 
				return vecsProductDO.size();
			else 
				return 0;

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
			convertView = inflater.inflate(R.layout.lastordercell, null);

			TextView tvItemId, tvItemDesc, tvItemUOM, tvItemOty;
			tvItemId = (TextView) convertView.findViewById(R.id.tvItemId);
			tvItemDesc = (TextView) convertView.findViewById(R.id.tvItemDesc);
			tvItemUOM = (TextView) convertView.findViewById(R.id.tvItemUOM);
			tvItemOty = (TextView) convertView.findViewById(R.id.tvItemOty);
			OrderDetailsDO productDO = vecsProductDO.get(position);
			tvItemId.setText(productDO.SKU);
			tvItemDesc.setText(productDO.description);
			tvItemUOM.setText(productDO.UOM);
			tvItemOty.setText(productDO.Units);

			return convertView;

		}

	}
	
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
				vecProductDO 		= new OrderDetailsDA().getOrderDetails(orderDetails.OrderNumber);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						if(vecProductDO!=null&&vecProductDO.size()>0)
						{
							tv_nolastorders.setVisibility(View.GONE);
							orderadap.refresh(vecProductDO);
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
