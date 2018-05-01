package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.R.color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.InventoryObject;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class Replacement_Order_QTY extends BaseActivity
{
	private LinearLayout llMain;
	private TextView tvPageTitle, tvItemCode, tvTotalQty, tvUOM_Title, tvResultOfSearch;
	private ListView lvInventoryItems;
	private ArrayList<InventoryObject> vecInventoryItems;
	private CustomeListAdapter customeListAdapter;
	private OrderDetailsDA orderDetailsDA;
	private String OrderSubType = "";
	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.return_inventory_qty, null);
		llBody.addView(llMain,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		OrderSubType = AppConstants.APPORDER;
		
		if(getIntent().getExtras() != null)
		{
			OrderSubType = getIntent().getExtras().getString("OrderSubType");
		}
		intialiseControls();
		setTypeFaceRobotoNormal(llMain);
		lvInventoryItems.setCacheColorHint(0);
		lvInventoryItems.setDivider(null);
		lvInventoryItems.setSelector(color.transparent);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		orderDetailsDA = new OrderDetailsDA();
		lvInventoryItems.setAdapter(customeListAdapter = new CustomeListAdapter(new ArrayList<InventoryObject>()));
		showLoader(getResources().getString(R.string.loading));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				vecInventoryItems = orderDetailsDA.getReturnInventoryQtyNew(CalendarUtils.getOrderPostDate(), OrderSubType);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						customeListAdapter.refresh(vecInventoryItems);
						hideLoader();
					}
				});
			}
		}).start();
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvPageTitle		=	(TextView)llMain.findViewById(R.id.tvPageTitle);
		tvTotalQty			=	(TextView)llMain.findViewById(R.id.tvTotalQty);
		tvItemCode			=	(TextView)llMain.findViewById(R.id.tvItemCode);
		lvInventoryItems	=	(ListView)llMain.findViewById(R.id.lvInventoryItems);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvUOM_Title			=	(TextView)llMain.findViewById(R.id.tvUOM_Title);
		
		/*tvPageTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTotalQty.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvItemCode.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvResultOfSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvUOM_Title.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		tvPageTitle.setText("Return Inventory");
	}
	public class CustomeListAdapter extends BaseAdapter
	{

		ArrayList<InventoryObject> vecInventoryItems;
		public CustomeListAdapter(ArrayList<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
		}
		@Override
		public int getCount() 
		{
			return vecInventoryItems.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return vecInventoryItems.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			InventoryObject inventoryObject = vecInventoryItems.get(position);
			if(convertView == null)
				convertView = inflater.inflate(R.layout.inventory_qty_cell_old, null);
			
			TextView tvItemCodeText = (TextView)convertView.findViewById(R.id.tvItemCodeText);
			TextView tvDescription 	= (TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvTotalQty 	= (TextView)convertView.findViewById(R.id.tvTotalQty);
			TextView tvDeliveredQty = (TextView)convertView.findViewById(R.id.tvDeliveredQty);
			TextView tvAvailQty 	= (TextView)convertView.findViewById(R.id.tvAvailQty);
			TextView tvUOM 			= (TextView)convertView.findViewById(R.id.tvUOM);
			
		/*	tvItemCodeText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvUOM.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvTotalQty.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			
			tvDeliveredQty.setVisibility(View.GONE);
			tvAvailQty.setVisibility(View.GONE);
			
			tvItemCodeText.setText(inventoryObject.itemCode);
			tvDescription.setText(inventoryObject.itemDescription);
			tvTotalQty.setText(""+(inventoryObject.PrimaryQuantity >= 0 ? (int)inventoryObject.PrimaryQuantity : 0));
			tvUOM.setText(""+inventoryObject.UOM);
			
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
		public void refresh(ArrayList<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
			if(vecInventoryItems!=null && vecInventoryItems.size()>0)
			{
				lvInventoryItems.setVisibility(View.VISIBLE);
				tvResultOfSearch.setVisibility(View.GONE);
				notifyDataSetChanged();
			}
			else
			{
				lvInventoryItems.setVisibility(View.GONE);
				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
		}
	}
}
