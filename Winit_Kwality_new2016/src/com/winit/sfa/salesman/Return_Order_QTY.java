package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.R.color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.InventoryObject;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class Return_Order_QTY extends BaseActivity
{
	private LinearLayout llMain;
	private TextView tvItemCode, tvTotalQty, tvUOM_Title, tvResultOfSearch;
	private ListView lvInventoryItems;
	private ArrayList<InventoryObject> vecInventoryItems;
	private CustomeListAdapter customeListAdapter;
	private OrderDetailsDA orderDetailsDA;
	private String OrderSubType = "";
	private Button btnPrint;
	
	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.return_inventory_qty, null);
		llBody.addView(llMain,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		OrderSubType = AppConstants.APPORDER;
		
		if(getIntent().getExtras() != null)
			OrderSubType = getIntent().getExtras().getString("OrderSubType");
		
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
				if(OrderSubType != null && OrderSubType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
					vecInventoryItems = orderDetailsDA.getReturnInventoryQtyNew(CalendarUtils.getOrderPostDate(), AppConstants.REPLACEMETORDER);
				else	
					vecInventoryItems = orderDetailsDA.getReturnInventoryQtyNew(CalendarUtils.getOrderPostDate(), AppConstants.RETURNORDER);
				
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
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				if(vecInventoryItems == null || vecInventoryItems.size() == 0)
//					showCustomDialog(Return_Order_QTY.this, "Alert!", "There is no item to print.", "OK", "", "");
//				else	
//				{
//					Intent intent = new Intent(Return_Order_QTY.this, WoosimPrinterActivity.class);
//					intent.putExtra("vec", vecInventoryItems);
//					intent.putExtra("type", OrderSubType);
//					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_RETURN_INVENTORY);
//					startActivityForResult(intent, 1000);
//				}
			}
		});
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvTotalQty			=	(TextView)llMain.findViewById(R.id.tvTotalQty);
		tvItemCode			=	(TextView)llMain.findViewById(R.id.tvItemCode);
		lvInventoryItems	=	(ListView)llMain.findViewById(R.id.lvInventoryItems);
		tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvUOM_Title			=	(TextView)llMain.findViewById(R.id.tvUOM_Title);
		btnPrint			=	(Button)llMain.findViewById(R.id.btnPrint);
//		if(OrderSubType != null && OrderSubType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
//			tvPageTitle.setText("Replaced Inventory");
//		else
//			tvPageTitle.setText("Return Inventory");
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
