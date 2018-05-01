package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;

public class CheckoutAdapter extends BaseAdapter
{

	private Context context;
	private Vector<String> arrReason;
	private String selectedSalesOutlet = "";
	public CheckoutAdapter(Context context, Vector<String> arrReason) 
	{
		this.context = context;
		this.arrReason = arrReason;
	}

	@Override
	public int getCount() 
	{
		if(arrReason != null)
			return arrReason.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		
		if(convertView == null)
			convertView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.custom_builder_cell_checkout, null);
		
		ImageView ivImageDialog 		= (ImageView)convertView.findViewById(R.id.ivImageDialog);
		TextView tvSelectUrCountry  	= (TextView)convertView.findViewById(R.id.tvSelectName);
		TextView tvCount	 			= (TextView)convertView.findViewById(R.id.tvCount);
		final ImageView ivSelected 			= (ImageView)convertView.findViewById(R.id.ivSelected);
		
		ivImageDialog.setVisibility(View.GONE);
		tvSelectUrCountry.setVisibility(View.VISIBLE);
		tvCount.setVisibility(View.GONE);
		ivSelected.setVisibility(View.VISIBLE);
		
		tvSelectUrCountry.setText(arrReason.get(position));
		
		if(selectedSalesOutlet.equalsIgnoreCase(arrReason.get(position)))
			ivSelected.setBackgroundResource(R.drawable.rbtn);
		else
			ivSelected.setBackgroundResource(R.drawable.rbtn_h);
		
		convertView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ivSelected.setBackgroundResource(R.drawable.rbtn);
				selectedSalesOutlet = arrReason.get(position);
				notifyDataSetChanged();
			}
		});
		
		return convertView;
	}
	
	public String getSelecteSalesOutlet()
	{
		return selectedSalesOutlet;
	}
}
