package com.winit.alseer.pinch;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.OrderTraceSummaryDetail;

@SuppressLint("ValidFragment")
public class ListOrderTraceFragment extends Fragment
{
	private ListView lvOrderList;
	private OrderListAdapter orderListAdapter;
	private Vector<TrxHeaderDO> vecOrderList;
	private Context context;
	private TextView tvNoDataFound;
	private LinearLayout llMain;
	private int type,trxStatus=-1;
	
	public ListOrderTraceFragment(Context context,int trxStatus, Vector<TrxHeaderDO> vecOrderList) {
		this.vecOrderList = vecOrderList;
		this.trxStatus    = trxStatus;
		this.context = context;
	}
	

	public ListOrderTraceFragment()
	{
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		llMain 	= (LinearLayout) inflater.inflate(R.layout.listview,null);
		initializeControls();
		return llMain;
	}
	
	private void initializeControls()
	{
		tvNoDataFound		=	(TextView)llMain.findViewById(R.id.tvNoDataFound);
		lvOrderList			=	(ListView)llMain.findViewById(R.id.lv);
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setScrollbarFadingEnabled(true);
		
		if(vecOrderList!=null && vecOrderList.size()>0)
		{
			type  = vecOrderList.get(0).trxType;
			
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
		@SuppressLint("ResourceAsColor")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			TrxHeaderDO trxHeaderDO			    = (TrxHeaderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					    =	(LinearLayout)LayoutInflater.from(context).inflate(R.layout.delivery_status_list_item, null);
			
			TextView tvJournyeDate			 	=	(TextView)convertView.findViewById(R.id.tvJournyeDate);
			TextView tvJournyeDateYear		  	=	(TextView)convertView.findViewById(R.id.tvJournyeDateYear);
			ImageView sideView  				=	(ImageView)convertView.findViewById(R.id.sideView);
			
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
			if(trxHeaderDO.status>0)
				sideView.setBackgroundColor(context.getResources().getColor(R.color.status_delievered));
			else
				sideView.setBackgroundColor(context.getResources().getColor(R.color.status_pending));
			
			tvOrderNo.setText("Order No: "+trxHeaderDO.trxCode);
			
			if(type == TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			{
				tvCustomerPrice.setText("N/A");
				tvCustomerPriceUnit.setVisibility(View.GONE);
			}
			else
			{
				tvCustomerPriceUnit.setVisibility(View.VISIBLE);
				tvCustomerPrice.setText(""+(((BaseActivity)getActivity()).decimalFormat).format(trxHeaderDO.totalAmount));
				tvCustomerPriceUnit.setText(""+(((BaseActivity)getActivity()).curencyCode)+" ");
			}
			
			
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
					intent = new Intent(context , OrderTraceSummaryDetail.class);
					intent.putExtra("trxHeaderDO", objOrderList);
					intent.putExtra("trxStatus", trxStatus);
					startActivity(intent);
				
				}
			});
			
			((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
			
			tvOrderNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			return convertView;
		}
	}
}