package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

public class ItemPricingAdapter extends BaseAdapter {
	
	private Vector<ProductsDO> vecItemsPrice;
	private Context con;
	private  ViewHolder holder ;

	public ItemPricingAdapter(Vector<ProductsDO> vecProductsDo,Context itemPricingActivity) 
	{
		this.vecItemsPrice = vecProductsDo;
		this.con = itemPricingActivity;
	}

	@Override
	public int getCount() 
	{
		if(vecItemsPrice!=null && vecItemsPrice.size()>0)
			return vecItemsPrice.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int arg0) 
	{
		return arg0;
	}

	@Override
	public long getItemId(int arg0) 
	{
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{

		if(convertView == null)
		{
			convertView = LayoutInflater.from(con).inflate(R.layout.itemprice_listcell, null);
			holder = new ViewHolder();
			holder.tvItemCode  			= (TextView) convertView.findViewById(R.id.tvItempriceCode);
			holder.tvItemDescription	= (TextView) convertView.findViewById(R.id.tvItemDescription);
			holder.tvEAPrice			= (TextView) convertView.findViewById(R.id.tvEAPrice);
			holder.tvCSPrice 		= (TextView) convertView.findViewById(R.id.tvCSPrice);
			holder.tvItempriceEA	= (TextView) convertView.findViewById(R.id.tvItempriceEA);
			convertView.setTag(holder);
		
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvItemCode.setText(vecItemsPrice.get(position).ItemCode);
		holder.tvItemDescription.setText(vecItemsPrice.get(position).Description);
		holder.tvEAPrice.setText(""+((BaseActivity)con).decimalFormat.format(StringUtils.getFloat(vecItemsPrice.get(position).ItemPriceEA)));
		holder.tvCSPrice.setText(""+((BaseActivity)con).decimalFormat.format(StringUtils.getFloat(vecItemsPrice.get(position).ItemPriceCS)));
		holder.tvItempriceEA.setText(" ("+vecItemsPrice.get(position).EAconversion+") ");
		((BaseActivity)con).setTypeFaceRobotoNormal((ViewGroup) convertView);
		holder.tvItemCode.setTypeface(AppConstants.Roboto_Condensed_Bold);
		return convertView;
	}

	
	class ViewHolder
	{
		TextView tvItemCode,tvItemDescription,tvCSPrice,tvEAPrice,tvItempriceEA;
	}


	public void refresh(Vector<ProductsDO> vecProductsDoTemp) 
	{
		this.vecItemsPrice = vecProductsDoTemp;
		notifyDataSetChanged();
	}
}
