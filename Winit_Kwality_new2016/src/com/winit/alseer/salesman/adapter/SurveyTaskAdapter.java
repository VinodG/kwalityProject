package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.listeners.SurveyPopupListner;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.SurveyQuestionActivity;

public class SurveyTaskAdapter extends BaseAdapter{

	Context context;
	int requestCode;
	private Vector<SurveyListDO> vecTasks;
	private SurveyPopupListner surveyPopupListner;
	public SurveyTaskAdapter(Context context,SurveyPopupListner surveyPopupListner,Vector<SurveyListDO> vecTasks) 
	{
		this.context = context;
		this.vecTasks = vecTasks;
		this.surveyPopupListner = surveyPopupListner;
	}

	@Override
	public int getCount() 
	{
		return vecTasks.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	public void refresh(Vector<SurveyListDO> vecTasks)
	{
		this.vecTasks = vecTasks;
		this.notifyDataSetChanged();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.survey_list_cell, null);
		
		TextView tv_task;
		
		tv_task = (TextView) convertView.findViewById(R.id.tvTask);
		LinearLayout llStatusCell = (LinearLayout) convertView.findViewById(R.id.llStatusCell);
		TextView tvQty		= (TextView)convertView.findViewById(R.id.tvQty);
		ImageView ivStatus		= (ImageView)convertView.findViewById(R.id.ivStatus);
		
		tvQty.setText(""+vecTasks.get(position).Status);
		
		tvQty.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(vecTasks.get(position).Status.equalsIgnoreCase("Pending"))
		{
			tvQty.setTextColor(context.getResources().getColor(R.color.red_dark));
			ivStatus.setBackgroundResource(R.drawable.failure);
		}
		else if(vecTasks.get(position).Status.equalsIgnoreCase("Approved"))
		{
			tvQty.setTextColor(context.getResources().getColor(R.color.greenText));
			ivStatus.setBackgroundResource(R.drawable.success);
		}
		else
		{
			tvQty.setText("Pending");
			tvQty.setTextColor(context.getResources().getColor(R.color.red_dark));
			ivStatus.setBackgroundResource(R.drawable.failure);
		}
		
		tv_task.setText(""+(position+1)+". "+vecTasks.get(position).surveyName);
		
		
		if(StringUtils.getInt(vecTasks.get(position).userSurveyCount)>0)
		{
			llStatusCell.setClickable(true);
			tvQty.setText("Excecuted");
			tvQty.setTextColor(context.getResources().getColor(R.color.greenText));
			ivStatus.setBackgroundResource(R.drawable.success);
		}
		else
		{
			llStatusCell.setClickable(false);
			tvQty.setText("Pending");
			tvQty.setTextColor(context.getResources().getColor(R.color.red_dark));
			ivStatus.setBackgroundResource(R.drawable.failure);
		}
		
		
		llStatusCell.setTag(position);
		llStatusCell.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				surveyPopupListner.onSelectedSurveyCount(vecTasks.get((Integer) v.getTag()));
				
			}
		});
		
		convertView.setTag(vecTasks.get(position));
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				SurveyListDO surveyListDO = (SurveyListDO) v.getTag();
//				String surveyId = surveyListDO.surveyId;
				Intent intent = new Intent(context, SurveyQuestionActivity.class);
				intent.putExtra("For","Questions");
				intent.putExtra("SurveyListDO",surveyListDO);
				context.startActivity(intent);
				
			}
		});
		
		
		return convertView;
	}

}
