package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.kwalitysfa.salesman.R;

	
public class InitiativesProductAdapter extends BaseAdapter
{
    private Context context;
    private Vector<ProductsDO> vecInitiativeDOs;
    
	public InitiativesProductAdapter(Context context,Vector<ProductsDO> vecInitiativeDOs) 
	{
		this.vecInitiativeDOs = vecInitiativeDOs;
		this.context     = context;
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
    public void refreshAdapter(Vector<ProductsDO> vecStores)
    {
    	this.vecInitiativeDOs = vecStores;
    	notifyDataSetChanged();
    }
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ProductsDO do1 = vecInitiativeDOs.get(position);
		
		convertView = LayoutInflater.from(context).inflate(R.layout.planogram_execution_cell, null);
		
		TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
		TextView tvItemCode = (TextView) convertView.findViewById(R.id.tvItemCode);
		TextView tvPacking = (TextView) convertView.findViewById(R.id.tvPacking);
		TextView tvBarcode = (TextView) convertView.findViewById(R.id.tvBarcode);
		
		
		tvDescription.setText(do1.Description);
		tvItemCode.setText(do1.ItemCode);
		tvPacking.setText(do1.UnitsPerCases+"");
		tvBarcode.setText(do1.Brand);
		
		
	return convertView;
	}
	
}
