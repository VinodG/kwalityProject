/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.listeners.StoreCheck;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.BaseActivity;
import com.winit.sfa.salesman.CaptureInventoryActivity;

/**
 * @author Aritra.Pal
 *
 */
public class CaptureInventoryGridAdapter extends BaseAdapter 
{

	Context context;
	StoreCheck storeCheck;
	ArrayList<ProductDO> arrProductDOs;
	private boolean isStroreCheckDone;
	private View view;
	CustomBuilder builder;
	Vector<String> vecUOM;

	/**
	 * @param context2
	 * @param vectorProductDOs2
	 * @param storeCheck2
	 * @param searchText2
	 * @param isBrandSearched2
	 */
	public CaptureInventoryGridAdapter(Context context,ArrayList<ProductDO> arrProductDOs, StoreCheck storeCheck)
	{
		this.context = context;
		this.arrProductDOs = arrProductDOs;
		this.storeCheck = storeCheck;
		
		vecUOM = new Vector<String>();
		vecUOM.add("PCS");
		vecUOM.add("UNIT");
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() 
	{
		if(arrProductDOs != null && arrProductDOs.size() > 0)
			return arrProductDOs.size();
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.storecheck_qty_gridview_cell_new, null);
//		convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, (int)context.getResources().getDimension(R.dimen.size90)));
		final RelativeLayout llStoreCheckgrid	= (RelativeLayout) convertView.findViewById(R.id.llStoreCheckgrid);
		TextView tvGridCellheader 			=	(TextView) convertView.findViewById(R.id.tvGridCellheader);
		TextView tvGridCellDetail 			=	(TextView) convertView.findViewById(R.id.tvGridCellDetail);
		final TextView tvUOM						=	(TextView) convertView.findViewById(R.id.tvUOM);
		final LinearLayout llItemBottom		=	(LinearLayout) convertView.findViewById(R.id.llItemBottom);
		final LinearLayout llUOM			=	(LinearLayout) convertView.findViewById(R.id.llUOM);
		final CustomEditText tvPcsQTY		=	(CustomEditText) convertView.findViewById(R.id.tvPcsQTY);

		tvGridCellheader.setTypeface(AppConstants.Roboto_Condensed);
		final ProductDO objProductDO = arrProductDOs.get(position);
		tvGridCellheader.setText(objProductDO.SKU);
		tvGridCellDetail.setText(objProductDO.Description);
		tvGridCellheader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvGridCellDetail.setTypeface(AppConstants.Roboto_Condensed);
		((CaptureInventoryActivity)context).isItemMissing(objProductDO);
		
		if(objProductDO.isCaptured)
		{
			llStoreCheckgrid.setBackgroundResource(R.drawable.green);
			llItemBottom.setBackgroundResource(R.drawable.grypat);		
			
			llUOM.setEnabled(true);
			tvPcsQTY.setEnabled(true);
		}
		else
		{
			llStoreCheckgrid.setBackgroundResource(R.drawable.red);
			llItemBottom.setBackgroundResource(R.drawable.red_storecheckbottom);
			
			llUOM.setEnabled(false);
			tvPcsQTY.setEnabled(false);
		}
		
		if(StringUtils.getInt(objProductDO.PcsQTY) >= 0)
			tvPcsQTY.setText(""+objProductDO.PcsQTY);
		else
			tvPcsQTY.setText("");
			
		tvUOM.setText(""+objProductDO.StoreUOM);
		
		llStoreCheckgrid.setTag(objProductDO);
		
//		convertView.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				if(!((CaptureInventoryActivity)context).isStoreCheckSumbitted)
//				{
//					llStoreCheckgrid.performClick();
//				}
//			}
//		});
		
		llStoreCheckgrid.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{/*
				if(!((CaptureInventoryActivity)context).isStoreCheckSumbitted)
				{
					ProductDO productDO = (ProductDO) v.getTag();
					if(productDO.isCaptured)
					{
						productDO.isCaptured = false;
						productDO.reason = "";
						llStoreCheckgrid.setBackgroundResource(R.drawable.red);
						
						llItemBottom.setBackgroundResource(R.drawable.red_storecheckbottom);
						tvPcsQTY.setEnabled(false);
						llUOM.setEnabled(false);
						
						tvPcsQTY.setText("");
					}
					else
					{
						productDO.isCaptured = true;
						llStoreCheckgrid.setBackgroundResource(R.drawable.green);
						
						llItemBottom.setBackgroundResource(R.drawable.grypat);
						llUOM.setEnabled(true);
						tvPcsQTY.setEnabled(true);
					}
					if(storeCheck != null)
						storeCheck.getStoreCheckItem(productDO.SKU,productDO);
				}
			*/}
		});
//		llUOM.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				builder = new CustomBuilder(context, "Select UOM", false);
//				builder.setSingleChoiceItems(vecUOM, tvUOM.getText().toString(), new CustomBuilder.OnClickListener() 
//				{
//					@Override
//					public void onClick(CustomBuilder builder, Object selectedObject) 
//					{
//						String strSelected = (String) selectedObject;
//						tvUOM.setText(strSelected);
//						objProductDO.StoreUOM = strSelected;
//			    		builder.dismiss();
//		    			builder.dismiss();
//				    }
//				}); 
//				builder.show();	
//			}
//		});
		tvPcsQTY.setOnFocusChangeListener(new OnFocusChangeListener() 
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus) 
			{
				if (hasFocus) 
				{
					view  = v;
				
					new Handler().postDelayed(new Runnable() 
					{
						@Override
						public void run() 
						{
							((BaseActivity) context).hideKeyBoard(tvPcsQTY);
							/**Pointer of Custom Keypad when clicked on Quantity in Capture Inventory.**/
							if(position == 0)
								((BaseActivity) context).onKeyboardFocus(tvPcsQTY,StringUtils.getInt(objProductDO.PcsQTY),true);
							else if(position%2 == 0)
								((BaseActivity) context).onKeyboardFocus(tvPcsQTY,StringUtils.getInt(objProductDO.PcsQTY),true);
							else
								((BaseActivity) context).onKeyboardFocus(tvPcsQTY,StringUtils.getInt(objProductDO.PcsQTY),false);
						}
					}, 0);
				}
				else
				{
				view = null;
				((BaseActivity) context).hideKeyBoard(tvPcsQTY);
				}
			}
		});
		tvPcsQTY.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((BaseActivity) context).hideKeyBoard(tvPcsQTY);
				return false;
			}
		});

		tvPcsQTY.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(!TextUtils.isEmpty(tvPcsQTY.getText().toString()))
					objProductDO.PcsQTY = tvPcsQTY.getText().toString();
				else
					objProductDO.PcsQTY = "-1";
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				((BaseActivity) context).hideKeyBoard(tvPcsQTY);
			}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		if(convertView.getHeight() >= childSize)
			childSize = convertView.getMeasuredHeight();
		
		return convertView;
	}

	private int childSize = 0;
	/**
	 * @return int
	 * 
	 */
	public int getChildSize() 
	{
		return childSize;
	}

	public void refresh() 
	{
		notifyDataSetChanged();
	}

	/**
	 * @param vecProductDOs
	 */
	public void refresh(ArrayList<ProductDO> arrProductDOs) 
	{
		this.arrProductDOs = arrProductDOs;
		notifyDataSetChanged();
	}
}
