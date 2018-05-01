package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.kwalitysfa.salesman.R;

	
public class AssetsBrandAdapter extends BaseAdapter
{
    private Context context;
    private Vector<String> vecBrandNewDOs;
	public AssetsBrandAdapter(Context context,Vector<String> vecTypes) 
	{
		this.vecBrandNewDOs = vecTypes;
		this.context     = context;
	}
	@Override
	public int getCount() {
		if(vecBrandNewDOs != null && vecBrandNewDOs.size() > 0)
			return vecBrandNewDOs.size();
		return 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return vecBrandNewDOs.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}
    public void refreshAdapter(Vector<String> vecStores)
    {
    	this.vecBrandNewDOs = vecStores;
    	notifyDataSetChanged();
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
			convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);
			TextView tvBrandNam = (TextView) convertView.findViewById(R.id.tvBrandName);
			tvBrandNam.setText(vecBrandNewDOs.get(position).toString());
			
		return convertView;
	}
	
}
