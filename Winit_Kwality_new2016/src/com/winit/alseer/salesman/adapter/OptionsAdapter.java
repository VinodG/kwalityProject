package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.kwalitysfa.salesman.R;

public class OptionsAdapter extends BaseAdapter
{
    private Context context;
	private Vector<OptionsDO> vecStores;
	public OptionsAdapter(Context context,Vector<OptionsDO> vecTypes) 
	{
		this.vecStores = vecTypes;
		this.context     = context;
	}
	@Override
	public int getCount() {
		if(vecStores != null && vecStores.size() > 0)
			return vecStores.size();
		return 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return vecStores.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}
    public void refreshAdapter(Vector<OptionsDO> vecStores)
    {
    	this.vecStores = vecStores;
    	notifyDataSetChanged();
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
			convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
			TextView tvBrandName = (TextView) convertView.findViewById(R.id.tvBrandName);
			tvBrandName.setText(vecStores.get(position).OptionName);
			tvBrandName.setTextSize(18);
			
		return convertView;
	}
	
}