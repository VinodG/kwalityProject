package com.winit.alseer.salesman.adapter;

import java.util.HashMap;
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

import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.kwalitysfa.salesman.R;

public class AddItemVanAdapter extends BaseAdapter
{
	Vector<VanLoadDO> vecSearchedItems;
	Vector<VanLoadDO> vecSelectedItems = new Vector<VanLoadDO>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	private boolean isUnload = false;
	private HashMap<String, HHInventryQTDO> hmInventory;
	public AddItemVanAdapter(Vector<VanLoadDO> vecSearchedItems,Context context,boolean isUnload,
			HashMap<String, HHInventryQTDO> hmInventory) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isUnload= isUnload;
		this.hmInventory=hmInventory;
	}
	/**
	 * @param vecSearchedItemd
	 * @param transferLoadActivity
	 */
	public AddItemVanAdapter(Vector<VanLoadDO> vecSearchedItems, Context context) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
	}
	@Override
	public int getCount() 
	{
		if(vecSearchedItems != null && vecSearchedItems.size() > 0)
			return vecSearchedItems.size();
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

	public void selectAll(ImageView ivSelectAll)
	{
		this.ivSelectAll = ivSelectAll;
//		vecSelectedItems.clear();
		if(!isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.checkbox_white);
			for(VanLoadDO vanLoadDO: vecSearchedItems){
				if(!vecSelectedItems.contains(vanLoadDO))
					vecSelectedItems.add(vanLoadDO);
			}
			isAllSelected = true;
		}
		else if(isAllSelected)
		{
			for(VanLoadDO vanLoadDO: vecSearchedItems){
				if(vecSelectedItems.contains(vanLoadDO))
					vecSelectedItems.remove(vanLoadDO);
			}
			ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
			isAllSelected = false;
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final VanLoadDO objiItem    = vecSearchedItems.get(position);
		if(convertView == null)
			convertView			    = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.result_cell, null);
		
		LinearLayout llReasion		= (LinearLayout)convertView.findViewById(R.id.llReasion);
		TextView tvItemDescription  = (TextView)convertView.findViewById(R.id.tvItemDescription);
		TextView tvItemCode 	    = (TextView)convertView.findViewById(R.id.tvItemCode);
		TextView tvVanQty 	    = (TextView)convertView.findViewById(R.id.tvVanQty);
		final ImageView cbList1     = (ImageView)convertView.findViewById(R.id.cbList1);
		tvVanQty.setVisibility(View.VISIBLE);
		if(isUnload)
		{
			llReasion.setVisibility(View.VISIBLE);
		}
		else
		{
			tvVanQty.setVisibility(View.VISIBLE);
			llReasion.setVisibility(View.GONE);
		}
		
//		tvVanQty.setTextColor(context.getResources().getColor(R.color.gray_light));
	
		tvVanQty.setText(""+objiItem.inventoryQty);	
		tvItemCode.setText(objiItem.ItemCode);
		tvItemDescription.setText(objiItem.Description);
		tvItemDescription.setTextColor(context.getResources().getColor(R.color.gray_light));
		tvItemCode.setTextColor(context.getResources().getColor(R.color.gray_light));
		if(vecSelectedItems.contains(objiItem))
		{
			cbList1.setImageResource(R.drawable.checkbox);
			cbList1.setTag("1");
		}
		else
		{
			cbList1.setImageResource(R.drawable.uncheckbox);
			cbList1.setTag("0");
		}
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(ivSelectAll!=null)
				{
					ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
					isAllSelected = false;
				}
					
				if(cbList1.getTag().toString().equalsIgnoreCase("0"))
				{
					vecSelectedItems.add(objiItem);
					cbList1.setImageResource(R.drawable.checkbox);
					cbList1.setTag("1");
				}
				else
				{
					vecSelectedItems.remove(objiItem);
					cbList1.setImageResource(R.drawable.uncheckbox);
					cbList1.setTag("0");
				}
			}
		});
		
//		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		return convertView;
	}
	
	public Vector<VanLoadDO> getSelectedItems()
	{
		return vecSelectedItems;
	}
	
	public void refresh(Vector<VanLoadDO> vecTemp,ImageView ivSelectAll)
	{
		this.vecSearchedItems = vecTemp;
		if(vecSearchedItems!=null && vecSearchedItems.size()>0){
			boolean isAllSelected=true;
			for(VanLoadDO vanLoadDO:vecSearchedItems){
				if(!vecSelectedItems.contains(vanLoadDO)){
					isAllSelected=false;
					break;
				}
			
			}
			if(isAllSelected){
				this.isAllSelected=true;
				ivSelectAll.setImageResource(R.drawable.checkbox_white);
			}else{
				this.isAllSelected=false;
				ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
			}
		}
		notifyDataSetChanged();
	}
}
