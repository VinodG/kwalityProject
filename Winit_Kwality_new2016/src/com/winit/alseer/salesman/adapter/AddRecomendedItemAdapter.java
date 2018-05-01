package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.CaptureDamagedItemImage;
import com.winit.sfa.salesman.SalesManTakeReturnOrder.DatePickerListner;

public class AddRecomendedItemAdapter extends BaseAdapter
{
	Vector<ProductDO> vecSearchedItems;
	Vector<ProductDO> vecSelectedItems = new Vector<ProductDO>();
	Context context;
	boolean isAllSelected = false;
	ImageView ivSelectAll;
	private boolean isReturn = false;
	private DatePickerListner datePicker;
	private String from = "";
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
	}
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context, boolean isReturn) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isReturn = isReturn;
	}
	public AddRecomendedItemAdapter(Vector<ProductDO> vecSearchedItems,Context context, boolean isReturn, String from, DatePickerListner datePicker) 
	{
		this.vecSearchedItems = vecSearchedItems;
		this.context= context;
		this.isReturn = isReturn;
		this.datePicker = datePicker;
		this.from = from;
	}
	@Override
	public int getCount() 
	{
		if(vecSearchedItems != null && vecSearchedItems.size() >0)
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
		vecSelectedItems.clear();
		if(!isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.uncheckbox_white);
			vecSelectedItems = (Vector<ProductDO>) vecSearchedItems.clone();
			isAllSelected = true;
		}
		else if(isAllSelected)
		{
			ivSelectAll.setImageResource(R.drawable.checkbox_all);
			isAllSelected = false;
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final ProductDO objiItem   = 	vecSearchedItems.get(position);
		
		if(convertView == null || isReturn)
			convertView			   = 	(LinearLayout)((Activity) context).getLayoutInflater().inflate(R.layout.result_cell, null);
		
		TextView tvItemDescription = 	(TextView)convertView.findViewById(R.id.tvItemDescription);
		TextView tvItemCode 	   = 	(TextView)convertView.findViewById(R.id.tvItemCode);
		final ImageView cbList1    = 	(ImageView)convertView.findViewById(R.id.cbList1);
		final LinearLayout llReasion = 	(LinearLayout)convertView.findViewById(R.id.llReasion);
		
		final LinearLayout llBottom			= 	(LinearLayout)convertView.findViewById(R.id.llBottom);
	//	final LinearLayout llReturn		 	= 	(LinearLayout)convertView.findViewById(R.id.llReturn);
	//	final LinearLayout llReturnReason1 	= 	(LinearLayout)convertView.findViewById(R.id.llReturnReason1);
	//	final LinearLayout llReturnReason2 	= 	(LinearLayout)convertView.findViewById(R.id.llReturnReason2);
	//	final LinearLayout llReturnReason3 	= 	(LinearLayout)convertView.findViewById(R.id.llReturnReason3);
	//	final LinearLayout llCaptureImage	= 	(LinearLayout)convertView.findViewById(R.id.llCaptureImage);
		
		final TextView tvExpiryDate			= 	(TextView)convertView.findViewById(R.id.tvExpiryDate);
		final EditText etRemark				= 	(EditText)convertView.findViewById(R.id.etRemark);	
		final ImageView btnCaptureImages		= 	(ImageView)convertView.findViewById(R.id.btnCaptureImages);
		final TextView tvUOM				= 	(TextView)convertView.findViewById(R.id.tvUOM);
		
		//final ImageView ivReturn		 = 	(ImageView)convertView.findViewById(R.id.ivReturn);
		final TextView ivReturnReason1	 = 	(TextView)convertView.findViewById(R.id.ivReturnReason1);
//		final TextView ivReturnReason2	 = 	(TextView)convertView.findViewById(R.id.ivReturnReason2);
		final TextView ivReturnReason3  = 	(TextView)convertView.findViewById(R.id.ivReturnReason3);
		
		
		ivReturnReason1.setTag(0);
//		ivReturnReason2.setTag(0);
		ivReturnReason3.setTag(0);
		
		objiItem.isReccomended     =	false;
		tvItemCode.setText(objiItem.SKU);
		tvItemDescription.setText(objiItem.Description);
		tvItemDescription.setTextColor(context.getResources().getColor(R.color.gray_dark));
		tvItemCode.setTextColor(context.getResources().getColor(R.color.gray_dark));
		
		tvExpiryDate.setText(objiItem.strExpiryDate);
		etRemark.setText(objiItem.remarks);
		btnCaptureImages.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent objIntent = new Intent(context, CaptureDamagedItemImage.class);
				objIntent.putExtra("vecImagePaths", objiItem.vecDamageImages);
				objIntent.putExtra("position", position);
				objIntent.putExtra("itemCode", objiItem.SKU);
				objIntent.putExtra("desc", objiItem.Description);
				((Activity)context).startActivityForResult(objIntent, 500);
			}
		});
		if(vecSelectedItems.contains(objiItem))
		{
			cbList1.setImageResource(R.drawable.checkbox);
			cbList1.setTag("1");
			if(isReturn)
				llReasion.setVisibility(View.VISIBLE);
			else
				llReasion.setVisibility(View.GONE);
		}
		else
		{
			cbList1.setImageResource(R.drawable.uncheckbox);
			cbList1.setTag("0");
			llReasion.setVisibility(View.GONE);
		}

		etRemark.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				objiItem.remarks = s.toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
//		tvExpiryDate.setOnClickListener(new OnClickListener() 
//		{
//			@Override
//			public void onClick(final View v) 
//			{
//				datePicker.setDate((TextView) v, objiItem);
////				objiItem.itemExpiryDate = ((TextView) v).getText().toString();
//			}
//		});
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
					
					if(isReturn)
						llReasion.setVisibility(View.VISIBLE);
					else
						llReasion.setVisibility(View.GONE);
				}
				else
				{
					vecSelectedItems.remove(objiItem);
					cbList1.setImageResource(R.drawable.uncheckbox);
					cbList1.setTag("0");
					
					llReasion.setVisibility(View.GONE);
				}
			}
		});
		
		
		if(from != null && from.equalsIgnoreCase("replacement"))
		{
		//	llReturn.setVisibility(View.GONE);
			btnCaptureImages.setVisibility(View.VISIBLE);
//			llBottom.setVisibility(View.VISIBLE);
		}
		else if(from != null && from.equalsIgnoreCase("checkINOption"))
		{
			objiItem.reason = context.getResources().getString(R.string.return_);
		//	ivReturn.setBackgroundResource(R.drawable.rbtn1);
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
			btnCaptureImages.setVisibility(View.GONE);
//			llReturn.performClick();
//			llReturnReason1.setVisibility(View.GONE);
//			llReturnReason2.setVisibility(View.GONE);
//			llReturnReason3.setVisibility(View.GONE);
//			llBottom.setVisibility(View.GONE);
		}
		
		if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.return_)))
		{
		//	ivReturn.setBackgroundResource(R.drawable.rbtn1);
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
		}
		else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason1)))
		{
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn1);
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
		//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
		}
		else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason2)))
		{
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn1);
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
		//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
		}
		else if(objiItem.reason.equalsIgnoreCase(context.getResources().getString(R.string.ReturnReason3)))
		{
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn1);
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
		//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
		}
		else
		{
			ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
			ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//			ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
		//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
		}
//		llReturn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				if((Integer)v.getTag() == 0)
//				{
//					objiItem.reason = context.getResources().getString(R.string.return_);
//					ivReturn.setBackgroundResource(R.drawable.rbtn1);
//					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
//					llLotNo.setVisibility(View.VISIBLE);
//					v.setTag(1);
//				}
//				else
//				{
//					objiItem.reason = "";
//					ivReturn.setBackgroundResource(R.drawable.rbtn2);
//					llLotNo.setVisibility(View.GONE);
//					v.setTag(0);
//				}
//			}
//		});
		ivReturnReason1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if((Integer)v.getTag() == 0)
				{
					if(tvExpiryDate.getText().toString() != null 
							&&!tvExpiryDate.getText().toString().equals("")
							&& CalendarUtils.getDateDifferenceInMinutesNew(tvExpiryDate.getText().toString(), CalendarUtils.getCurrentDateTime()) < 0)
					{
						tvExpiryDate.setText("");
						objiItem.reason = "";
					}
						
					objiItem.reason = context.getResources().getString(R.string.ReturnReason1);
					ivReturnReason1.setBackgroundResource(R.drawable.rbtn1);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
				//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(1);
				}
				else
				{
					objiItem.reason = "";
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
//					if(tvExpiryDate.getText().toString() != null 
//							&&!tvExpiryDate.getText().toString().equals("")
//							&& CalendarUtils.getDateDifferenceInMinutesNew(tvExpiryDate.getText().toString(), CalendarUtils.getCurrentDateTime()) > 0)
//					{
//						tvExpiryDate.setText("");
//						objiItem.reason = "";
//					}
//					
//					objiItem.reason = context.getResources().getString(R.string.ReturnReason2);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn1);
//					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
//				//	ivReturn.setBackgroundResource(R.drawable.rbtn2);
//					btnCaptureImages.setVisibility(View.VISIBLE);
//					v.setTag(1);
//				}
//				else
//				{
//					objiItem.reason = "";
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
					
					objiItem.reason = context.getResources().getString(R.string.ReturnReason3);
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn1);
					ivReturnReason1.setBackgroundResource(R.drawable.rbtn2);
//					ivReturnReason2.setBackgroundResource(R.drawable.rbtn2);
			//		ivReturn.setBackgroundResource(R.drawable.rbtn2);
					btnCaptureImages.setVisibility(View.VISIBLE);
					v.setTag(1);
				}
				else
				{
					objiItem.reason = "";
					ivReturnReason3.setBackgroundResource(R.drawable.rbtn2);
					v.setTag(0);
				}
			}
		});
		
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		((BaseActivity)context).setTypeFaceRobotoNormal((ViewGroup) convertView);
		return convertView;
	}
	public Vector<ProductDO> getSelectedItems()
	{
		return vecSelectedItems;
	}
	
	public void setItems(int pos, ArrayList<String> vecImagePaths)
	{
		vecSearchedItems.get(pos).vecDamageImages = vecImagePaths;
	}
	
	public void setExpiryDate(ProductDO productDO)
	{
		if(vecSelectedItems != null && vecSelectedItems.size() > 0)
		{
			for(ProductDO productDO2 : vecSelectedItems)
			{
				if(productDO2.SKU.equalsIgnoreCase(productDO.SKU))
					productDO2.strExpiryDate = productDO.strExpiryDate;
			}
		}
	}
	
	public void refresh(Vector<ProductDO> vecSearchedItemd)
	{
		this.vecSearchedItems = vecSearchedItemd;
		notifyDataSetChanged();
	}
	
	public interface DamageItemProduct
    {
	   public void getProducts(int position, ProductDO productDO);
    }
}
