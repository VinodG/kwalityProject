package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.DailySummaryDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

public class DaliySummaryAdapter extends BaseAdapter
{

	private Context context;
	private ArrayList<DailySummaryDO> vecDailySummary;
	private HashMap<String, DailySummaryDO> hashDailySum;
	
	public DaliySummaryAdapter(Context context,HashMap<String, DailySummaryDO> hashDailySum) 
	{
		this.context = context;
		this.hashDailySum = hashDailySum;
		
		getHashItem();
	}

	public DaliySummaryAdapter(Context context,	ArrayList<DailySummaryDO> vecDailySummary)
	{
		this.context = context;
		this.vecDailySummary = vecDailySummary;
	}

	private void getHashItem() 
	{
		Set<String> keys = hashDailySum.keySet();
		vecDailySummary = new ArrayList<DailySummaryDO>();
		for(String key : keys)
		{
			vecDailySummary.add(hashDailySum.get(key));
		}
	}

	@Override
	public int getCount() 
	{
		if(vecDailySummary != null)
			return vecDailySummary.size();
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
		convertView = LayoutInflater.from(context).inflate(R.layout.daily_summary_cell, null);
		
		LinearLayout llItemHeader	= (LinearLayout) convertView.findViewById(R.id.llItemHeader);
		TextView tvSrlNo		 	= (TextView) convertView.findViewById(R.id.tvSrlNo);
		
		TextView tvDocNo 			= (TextView) convertView.findViewById(R.id.tvDocNo);
		TextView tvCustmCode 		= (TextView) convertView.findViewById(R.id.tvCustmCode);
		TextView tvCustmName 		= (TextView) convertView.findViewById(R.id.tvCustmName);
		TextView tvType 			= (TextView) convertView.findViewById(R.id.tvType);
		TextView tvAmount 			= (TextView) convertView.findViewById(R.id.tvAmount);
		
		((BaseActivity)context).setTypeFaceRobotoNormal(llItemHeader);

		tvSrlNo.setText(""+(position+1));
		tvCustmCode.setText(vecDailySummary.get(position).siteID);
		tvCustmName.setText(vecDailySummary.get(position).siteName);
		
		tvDocNo.setText(vecDailySummary.get(position).DocNo);
		tvType.setText(vecDailySummary.get(position).type);
		tvAmount.setText(""+((BaseActivity)context).decimalFormat.format(StringUtils.getDouble(vecDailySummary.get(position).amount)));
		
		return convertView;
	}
}
