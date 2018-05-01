package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;

public class WareHouseAdapter extends BaseAdapter
{

	Context context;
	Vector<WareHouseDetailDO> vecWareHouseDetail;
	
	public WareHouseAdapter(Context context,Vector<WareHouseDetailDO> vecWareHouseDetail) 
	{
		this.context = context;
		this.vecWareHouseDetail = vecWareHouseDetail;
	}

	@Override
	public int getCount() 
	{
		if(vecWareHouseDetail != null)
			return vecWareHouseDetail.size();
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
//		convertView =LayoutInflater.from(context).inflate(resource, root)
		return convertView;
	}

}
