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
import com.winit.sfa.salesman.SavedOrderSummaryDetail;

@SuppressLint("ValidFragment")
public class SavedOrderFragment extends Fragment
{
	private ListView lvOrderList;
	private OrderListAdapter orderListAdapter;
	private Vector<TrxHeaderDO> vecOrderList;
	private Context context;
	private TextView tvNoDataFound;
	private LinearLayout llMain;
	private String trxCode = "";
	public SavedOrderFragment(Context context,int position, Vector<TrxHeaderDO> vecOrderList) {
		this.vecOrderList = vecOrderList;
		this.context = context;
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
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			TrxHeaderDO trxHeaderDO			    = (TrxHeaderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					    =	LayoutInflater.from(context).inflate(R.layout.delivery_status_list_item, null);
			
			TextView tvJournyeDate			 	=	(TextView)convertView.findViewById(R.id.tvJournyeDate);
			TextView tvJournyeDateYear		  	=	(TextView)convertView.findViewById(R.id.tvJournyeDateYear);
			ImageView sideView					=	(ImageView)convertView.findViewById(R.id.sideView);
			ImageView ivDelete					=	(ImageView)convertView.findViewById(R.id.ivDelete);
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
			
			tvCustomerPrice.setText(""+((BaseActivity)getActivity()).decimalFormat.format(trxHeaderDO.totalAmount));
			tvCustomerPriceUnit.setVisibility(View.VISIBLE);			
			
			tvCustomerName.setText(trxHeaderDO.siteName+"");
			tvCustomerLocation.setText(trxHeaderDO.address+"");
			
			convertView.setTag(trxHeaderDO);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					TrxHeaderDO objOrderList = 	(TrxHeaderDO) v.getTag();
					
					objOrderList = (TrxHeaderDO) v.getTag();
					
					Intent intent = new Intent(context ,SavedOrderSummaryDetail.class);
					intent.putExtra("trxHeaderDO", objOrderList);
					intent.putExtra("mallsDetails", ((BaseActivity)getActivity()).mallsDetailss);
					startActivity(intent);
				}
			});
			
			if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PENDING_STATUS())
				sideView.setBackgroundColor(getResources().getColor(R.color.status_pending));
			else if(trxHeaderDO.status == TrxHeaderDO.get_TRX_DATA_PUSHED_STATUS())
				sideView.setBackgroundColor(getResources().getColor(R.color.status_delievered));
			else
				sideView.setVisibility(View.INVISIBLE);
			
			if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
				ivDelete.setVisibility(View.VISIBLE);
			else
				ivDelete.setVisibility(View.GONE);
			
			((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
			ivDelete.setTag(trxHeaderDO.trxCode);
			tvOrderNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			ivDelete.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v) 
				{
					trxCode = (String)v.getTag();
					((BaseActivity)context).showCustomDialog(getActivity(),"Cancel Order","Do you want to cancel the order?","OK","Cancel","cancelorder",trxCode);
				}
			});
			
			return convertView;
		}
		
	}
	
	
}