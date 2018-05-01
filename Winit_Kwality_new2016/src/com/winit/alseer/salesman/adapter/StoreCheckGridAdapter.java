/**
 * 
 */
package com.winit.alseer.salesman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.listeners.StoreCheck;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.StoreCheckCatagoryActivity;

/**
 * @author Aritra.Pal
 *
 */
public class StoreCheckGridAdapter extends BaseAdapter 
{

	Context context;
	StoreCheck storeCheck;
	ArrayList<ProductDO> arrProductDOs;
	private boolean isStroreCheckDone;

	/**
	 * @param context2
	 * @param vectorProductDOs2
	 * @param storeCheck2
	 * @param searchText2
	 * @param isBrandSearched2
	 */
	public StoreCheckGridAdapter(Context context,ArrayList<ProductDO> arrProductDOs, StoreCheck storeCheck)
	{
		this.context = context;
		this.arrProductDOs = arrProductDOs;
		this.storeCheck = storeCheck;
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
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = LayoutInflater.from(context).inflate(R.layout.store_check_gridview_cell_new, null);
		final RelativeLayout llStoreCheckgrid	= (RelativeLayout) convertView.findViewById(R.id.llStoreCheckgrid);
		TextView tvGridCellheader 			=	(TextView) convertView.findViewById(R.id.tvGridCellheader);
		TextView tvGridCellDetail 			=	(TextView) convertView.findViewById(R.id.tvGridCellDetail);

		tvGridCellheader.setTypeface(AppConstants.Roboto_Condensed);
		ProductDO objProductDO = arrProductDOs.get(position);
		tvGridCellheader.setText(objProductDO.SKU);
		tvGridCellDetail.setText(objProductDO.Description);
		tvGridCellheader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		((StoreCheckCatagoryActivity)context).isItemMissing(objProductDO);
		
		if(objProductDO.isCaptured)
			llStoreCheckgrid.setBackgroundResource(R.drawable.green);
		else
			llStoreCheckgrid.setBackgroundResource(R.drawable.red);
		
		llStoreCheckgrid.setTag(objProductDO);
		
		convertView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!((StoreCheckCatagoryActivity)context).isStoreCheckSumbitted)
				{
					llStoreCheckgrid.performClick();
				}
				
			}
		});
		
		llStoreCheckgrid.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!((StoreCheckCatagoryActivity)context).isStoreCheckSumbitted)
				{
					ProductDO productDO = (ProductDO) v.getTag();
					if(productDO.isCaptured)
					{
						productDO.isCaptured = false;
						productDO.reason = "";
						llStoreCheckgrid.setBackgroundResource(R.drawable.red);
					}
					else
					{
						productDO.isCaptured = true;
						llStoreCheckgrid.setBackgroundResource(R.drawable.green);
					}
					//				if(!((BaseActivity)context).preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(Preference.PRESELLER))
					if(storeCheck != null)
						storeCheck.getStoreCheckItem(productDO.SKU,productDO);
				}
			}
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
