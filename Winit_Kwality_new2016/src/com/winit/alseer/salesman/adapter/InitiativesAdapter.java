package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.InitiativeExecutionActivity;

	
public class InitiativesAdapter extends BaseAdapter
{
    private Context context;
    private Vector<InitiativeDO> vecInitiativeDOs;
    private JourneyPlanDO journeyPlanDO;
    
    public InitiativesAdapter(Context context,Vector<InitiativeDO> vecInitiativeDOs,JourneyPlanDO journeyPlanDO) 
	{
		this.vecInitiativeDOs = vecInitiativeDOs;
		this.context     = context;
		this.journeyPlanDO = journeyPlanDO;
	}
	@Override
	public int getCount() 
	{
		if(vecInitiativeDOs != null && vecInitiativeDOs.size() > 0)
			return vecInitiativeDOs.size();
		return 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return vecInitiativeDOs.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}
	
	public void refreshAdapter(Vector<InitiativeDO> vecStores,JourneyPlanDO journeyPlanDO)
    {
    	this.vecInitiativeDOs = vecStores;
    	this.journeyPlanDO = journeyPlanDO;
    	notifyDataSetChanged();
    }
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		InitiativeDO do1 = vecInitiativeDOs.get(position);
		
		convertView = LayoutInflater.from(context).inflate(R.layout.item_load_view_stock, null);
		
		
		TextView tvBrandName		= (TextView)convertView.findViewById(R.id.tvCode);
		TextView tvDuration		= (TextView)convertView.findViewById(R.id.tvDate);
		TextView tvStatusText		= (TextView)convertView.findViewById(R.id.tvQty);
		ImageView ivStatusIcon		= (ImageView)convertView.findViewById(R.id.ivStatus);
		
/*		TextView tvBrandName = (TextView) convertView.findViewById(R.id.tvBrandName);
		TextView tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
		TextView tvStatusText = (TextView) convertView.findViewById(R.id.tvStatusText);
		ImageView ivStatusIcon = (ImageView) convertView.findViewById(R.id.ivStatusIcon);
*/		
		
		tvBrandName.setText(do1.Title);
//		tvDuration.setText(CalendarUtils.getDateCurrentTimeZone(Long.parseLong(do1.StartDate))+" - "+ CalendarUtils.getDateCurrentNextWeekTimeZone(Long.parseLong(do1.StartDate)));
		//vinod
		if(do1.Status.equalsIgnoreCase("0"))
		{
			tvStatusText.setText("Pending");
			ivStatusIcon.setBackgroundResource(R.drawable.initiative_pending);
			tvStatusText.setTextColor(Color.RED);
		}
		else
		{
			tvStatusText.setText("Executed");
			ivStatusIcon.setBackgroundResource(R.drawable.initiative_executed);
			tvStatusText.setTextColor(Color.BLUE);
		}
		
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context,InitiativeExecutionActivity.class);
				intent.putExtra("InitiativeDo", vecInitiativeDOs.get(position));
				intent.putExtra("Object", journeyPlanDO);
				context.startActivity(intent);
			}
		});
		
	return convertView;
	}
	
}
