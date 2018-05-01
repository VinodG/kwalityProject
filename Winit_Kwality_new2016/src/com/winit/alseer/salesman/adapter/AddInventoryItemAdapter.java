package com.winit.alseer.salesman.adapter;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;

public class AddInventoryItemAdapter extends BaseAdapter
{
	Vector<DeliveryAgentOrderDetailDco> vecSearchedItems;
	Vector<DeliveryAgentOrderDetailDco> vecSelectedItems = new Vector<DeliveryAgentOrderDetailDco>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	private boolean isReturn = false;
	public AddInventoryItemAdapter(Vector<DeliveryAgentOrderDetailDco> vecSearchedItems,Context context) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
	}
	public AddInventoryItemAdapter(Vector<DeliveryAgentOrderDetailDco> vecSearchedItems,Context context, boolean isReturn) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isReturn = isReturn;
	}
	@Override
	public int getCount() 
	{
		if(vecSearchedItems != null && vecSearchedItems.size() > 0)
		return vecSearchedItems.size();
		else return 0;
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
		vecSelectedItems.clear();
		if(!isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.check_hover);
			vecSelectedItems = (Vector<DeliveryAgentOrderDetailDco>) vecSearchedItems.clone();
			isAllSelected = true;
		}
		else if(isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.check_normal);
			isAllSelected = false;
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final DeliveryAgentOrderDetailDco objiItem   = 	vecSearchedItems.get(position);
		convertView				   = 	(LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.result_cell, null);
		TextView tvItemDescription = 	(TextView)convertView.findViewById(R.id.tvItemDescription);
		TextView tvItemCode 	   = 	(TextView)convertView.findViewById(R.id.tvItemCode);
		final ImageView cbList1    = 	(ImageView)convertView.findViewById(R.id.cbList1);
		final LinearLayout llReasion = 	(LinearLayout)convertView.findViewById(R.id.llReasion);
		
	//	final LinearLayout llReturnReason1 = 	(LinearLayout)convertView.findViewById(R.id.llReturnReason1);
	//	final LinearLayout llReturnReason2 = 	(LinearLayout)convertView.findViewById(R.id.llReturnReason2);
	//	final LinearLayout llReturnReason3 = 	(LinearLayout)convertView.findViewById(R.id.llReturnReason3);
		
		final TextView ivReturnReason1 = 	(TextView)convertView.findViewById(R.id.ivReturnReason1);
//		final TextView ivReturnReason2 = 	(TextView)convertView.findViewById(R.id.ivReturnReason2);
		final TextView ivReturnReason3 = 	(TextView)convertView.findViewById(R.id.ivReturnReason3);
		
	/*	llReturnReason1.setTag(0);
		llReturnReason2.setTag(0);
		llReturnReason3.setTag(0);*/
		
		
		cbList1.setId(position);
		tvItemCode.setText(objiItem.itemCode);
		tvItemDescription.setText(objiItem.itemDescription);
		tvItemDescription.setTextColor(context.getResources().getColor(R.color.gray_dark));
		tvItemCode.setTextColor(context.getResources().getColor(R.color.gray_dark));
		if(vecSelectedItems.contains(objiItem))
		{
			cbList1.setImageResource(R.drawable.check_hover);
			cbList1.setTag("1");
			if(isReturn)
				llReasion.setVisibility(View.VISIBLE);
			else
				llReasion.setVisibility(View.GONE);
		}
		else
		{
			cbList1.setImageResource(R.drawable.check_normal);
			cbList1.setTag("0");
			llReasion.setVisibility(View.GONE);
		}
		convertView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(ivSelectAll!=null)
				{
					ivSelectAll.setImageResource(R.drawable.check_normal);
					isAllSelected = false;
				}
					
				if(cbList1.getTag().toString().equalsIgnoreCase("0"))
				{
					vecSelectedItems.add(objiItem);
					cbList1.setImageResource(R.drawable.check_hover);
					cbList1.setTag("1");
					
					if(isReturn)
						llReasion.setVisibility(View.VISIBLE);
					else
						llReasion.setVisibility(View.GONE);
				}
				else
				{
					vecSelectedItems.remove(objiItem);
					cbList1.setImageResource(R.drawable.check_normal);
					cbList1.setTag("0");
					
					llReasion.setVisibility(View.GONE);
				}
			}
		});
		
		ivReturnReason1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if((Integer)v.getTag() == 0)
				{
					ivReturnReason1.setBackgroundResource(R.drawable.rbtn1);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(1);
				}
				else
				{
					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(0);
				}
			}
		});
//		ivReturnReason2.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if((Integer)v.getTag() == 0)
//				{
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn1);
//					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
//					v.setTag(1);
//				}
//				else
//				{
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
//					v.setTag(0);
//				}
//			}
//		});
		ivReturnReason3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((Integer)v.getTag() == 0)
				{
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn1);
					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(1);
				}
				else
				{
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(0);
				}
			}
		});
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		return convertView;
	}
	public Vector<DeliveryAgentOrderDetailDco> getSelectedItems()
	{
		return vecSelectedItems;
	}
	public void refresh(Vector<DeliveryAgentOrderDetailDco> vecSearchedItemd)
	{
		this.vecSearchedItems = vecSearchedItemd;
		notifyDataSetChanged();
	}
}
