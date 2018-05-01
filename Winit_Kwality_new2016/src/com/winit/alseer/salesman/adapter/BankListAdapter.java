package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.kwalitysfa.salesman.R;

public class BankListAdapter extends BaseAdapter
{
    private Context context;
	private Vector<NameIDDo> vecBankList;
	public BankListAdapter(Context context,Vector<NameIDDo> vecBankList) 
	{
		this.context = context;
		this.vecBankList = vecBankList;
	}
	@Override
	public int getCount() 
	{
		if(vecBankList != null && vecBankList.size() > 0)
			return vecBankList.size();
		else 
		  return 0;
	}

	@Override
	public Object getItem(int position) 
	{
		return vecBankList.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		NameIDDo nameIDDo = vecBankList.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_list_cell, null);
		TextView tvSpinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
		tvSpinnerItem.setText(nameIDDo.strName);
		return convertView;
	}
	

}
