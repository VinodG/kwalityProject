package com.winit.sfa.salesman;

import java.util.Vector;

import android.R.color;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataobject.InventoryObject;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class Inventory_QTY extends BaseActivity
{
	private LinearLayout llMain;
	private TextView tvTotalopeningLoadPcs,tvPageTitle,tvTotalStockLoad,tvOrderQty,tvSchemeQty,tvTotalFOCDelieved,tvTotalStockAvailable,tvResultOfSearch;
	private ListView lvInventoryItems;
	private Vector<InventoryObject> vecInventoryItems;
	private CustomeListAdapter customeListAdapter;
	private OrderDetailsDA orderDetailsDA;
	private EditText etSearch;
	private Button btnPrint;
	private ImageView ivDivHeaderPrint;
	
	private ImageView ivSearchCross;
	private int totalopeningqty=0;
	
	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.inventory_qty, null);
		llBody.addView(llMain,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intialiseControls();
		lvInventoryItems.setCacheColorHint(0);
		lvInventoryItems.setDivider(null);
		lvInventoryItems.setSelector(color.transparent);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		orderDetailsDA = new OrderDetailsDA();
		lvInventoryItems.setAdapter(customeListAdapter = new CustomeListAdapter(new Vector<InventoryObject>()));
		showLoader(getResources().getString(R.string.loading));
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				vecInventoryItems = orderDetailsDA.getInventoryQty(CalendarUtils.getOrderPostDate());
				final Object obj[] = orderDetailsDA.getInventorySumUp();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						try {
							int totalStockLoad  = 0,totalOrderDeliver  = 0,totalStockAvailable  = 0;
							for (int i = 0; i < vecInventoryItems.size(); i++) 
							{
								InventoryObject inventoryObject =vecInventoryItems.get(i);
								int orderQty = (int)(getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) : 0);
								int availQty = (int)(getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) : 0);
								int unloadQty = (int)(getUnitfromUOM(inventoryObject.UnloadedQty,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.UnloadedQty,inventoryObject.uomFactor) : 0);
								int vanQty = availQty + orderQty+unloadQty;
								int loaded=vanQty-(StringUtils.getInt(inventoryObject.openingQTY));
								totalopeningqty = totalopeningqty+StringUtils.getInt(""+inventoryObject.openingQTY);
								
								totalOrderDeliver += orderQty;
								totalStockAvailable += availQty;
								totalStockLoad += loaded;
								LogUtils.debug("stockInventory", "totalOrderDeliver"+totalOrderDeliver+"\n totalStockAvailable"+totalStockAvailable+"\n totalStockLoad"+totalStockLoad);
							}
							tvTotalStockLoad.setText("" + totalStockLoad);
							tvTotalStockAvailable.setText("" + totalStockAvailable);
							tvOrderQty.setText("" + totalOrderDeliver);
							tvTotalopeningLoadPcs.setText(""+totalopeningqty);
//							tvTotalStockLoad.setText("" + ((Long)(obj[0]==null?0:obj[0])));
//							tvTotalStockAvailable.setText("" + ((Long)(obj[1]==null?0:obj[1])));
//							tvOrderQty.setText("" + ((Long)(obj[2]==null?0:obj[2])));
						} catch (Exception e) {
							e.printStackTrace();
						}
						long totalStockLoad = 0;
						long orderQty = 0;
						long schemeQty = 0;
						long totalFOC = 0;
						long totalStockAvailable = 0;
						
						InventoryObject inventoryDO = null;
						for (int i = 0; i < vecInventoryItems.size(); i++) 
						{
							inventoryDO = vecInventoryItems.get(i);
							totalStockLoad += totalStockLoad + (getUnitfromUOM(inventoryDO.availCases,inventoryDO.uomFactor) >= 0 ? getUnitfromUOM(inventoryDO.availCases,inventoryDO.uomFactor) : 0);
							orderQty += orderQty + (getUnitfromUOM(inventoryDO.deliveredCases,inventoryDO.uomFactor) >= 0 ? getUnitfromUOM(inventoryDO.deliveredCases,inventoryDO.uomFactor) : 0);
							totalStockAvailable += totalStockAvailable + (getUnitfromUOM(inventoryDO.availQty,inventoryDO.uomFactor) >= 0 ? getUnitfromUOM(inventoryDO.availQty,inventoryDO.uomFactor) : 0);
						}
						
						
						customeListAdapter.refresh(vecInventoryItems);
						hideLoader();
					}
				});
			}
		}).start();
		
		setTypeFaceRobotoNormal(llMain);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				if(customeListAdapter!= null)
					customeListAdapter.refresh(vecInventoryItems);
			}
		});

		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(s.toString()!=null || s.length() > 0)
				{
					Vector<InventoryObject> vecTemp = new Vector<InventoryObject>();
					for(int index = 0; vecInventoryItems != null && index < vecInventoryItems.size(); index++)
					{
						InventoryObject obj = vecInventoryItems.get(index);
						String strText =  obj.itemCode;
						String strDes =  ""+obj.itemDescription;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) || strDes.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecInventoryItems.get(index));
					}
					customeListAdapter.refresh(vecTemp);
				}
				else if(customeListAdapter!= null)
					customeListAdapter.refresh(vecInventoryItems);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Inventory_QTY.this, WoosimPrinterActivity.class);
				intent.putExtra("vecInventoryItems", vecInventoryItems);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_STOCK_INVENTORY);
				startActivity(intent);				
			}
		});
		
		btnPrint.setVisibility(View.VISIBLE);
	}
	/** initializing all the Controls  of Inventory_QTY class **/
	public void intialiseControls()
	{
		tvTotalStockLoad		=	(TextView)llMain.findViewById(R.id.tvTotalStockLoad);
		tvOrderQty				=	(TextView)llMain.findViewById(R.id.tvOrderQty);
		tvSchemeQty				=	(TextView)llMain.findViewById(R.id.tvSchemeQty);
		tvTotalFOCDelieved		=	(TextView)llMain.findViewById(R.id.tvTotalFOCDelieved);
		tvTotalStockAvailable	=	(TextView)llMain.findViewById(R.id.tvTotalStockAvailable);
		tvResultOfSearch		=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvPageTitle				=	(TextView)llMain.findViewById(R.id.tvPageTitle);
		lvInventoryItems		=	(ListView)llMain.findViewById(R.id.lvInventoryItems);
		tvTotalopeningLoadPcs		=	(TextView)llMain.findViewById(R.id.tvTotalopeningLoadPcs);
		
		etSearch				=	(EditText)llMain.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llMain.findViewById(R.id.ivSearchCross);
		btnPrint				=   (Button)llMain.findViewById(R.id.btnPrint);
		ivDivHeaderPrint		=   (ImageView)llMain.findViewById(R.id.ivDivHeaderPrint);
		
		ivDivHeaderPrint.setVisibility(View.VISIBLE);
		tvPageTitle.setText("Van Stock");
	}
	
	public class CustomeListAdapter extends BaseAdapter
	{
		Vector<InventoryObject> vecInventoryItems;
		public CustomeListAdapter(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
		}
		@Override
		public int getCount() 
		{
			if(vecInventoryItems!=null && vecInventoryItems.size()>0)
			{
				lvInventoryItems.setVisibility(View.VISIBLE);
				tvResultOfSearch.setVisibility(View.GONE);
				return vecInventoryItems.size();
			}
			else
			{
				lvInventoryItems.setVisibility(View.GONE);
				tvResultOfSearch.setVisibility(View.VISIBLE);
			}
			return 0;
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
				convertView 	= inflater.inflate(R.layout.inventory_qty_cell, null);
			
			TextView tvItemCodeText 	= (TextView)convertView.findViewById(R.id.tvItemCodeText);
			TextView tvDescription 		= (TextView)convertView.findViewById(R.id.tvDescription);
			TextView tvLoadedPcs 		= (TextView)convertView.findViewById(R.id.tvLoadedPcs);
			TextView tvOpeningPcs 		= (TextView)convertView.findViewById(R.id.tvOpeningPcs);
			TextView tvOrderQtyList 	= (TextView)convertView.findViewById(R.id.tvOrderQtyList);
			TextView tvSchemeQtyList 	= (TextView)convertView.findViewById(R.id.tvSchemeQtyList);
			TextView tvFocPcs 			= (TextView)convertView.findViewById(R.id.tvFocPcs);
			TextView tvAvailPcs 		= (TextView)convertView.findViewById(R.id.tvAvailPcs);
			TextView  tvUnitPrecase 	= (TextView)convertView.findViewById(R.id.tvUnitPrecase);
			
			tvUnitPrecase.setVisibility(View.GONE);
			tvItemCodeText.setText(inventoryObject.itemCode);
			tvDescription.setText(inventoryObject.itemDescription);
			
			int orderQty = (int)(getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) : 0);
			int availQty = (int)(getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) : 0);
			int unloadQty = (int)(getUnitfromUOM(inventoryObject.UnloadedQty,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.UnloadedQty,inventoryObject.uomFactor) : 0); 
			int vanQty = orderQty + availQty;
			
//			tvLoadedPcs.setText(""+vanQty);
			tvLoadedPcs.setText(""+((availQty+orderQty+unloadQty)-(StringUtils.getInt(inventoryObject.openingQTY))));
			tvOrderQtyList.setText(""+orderQty);
			tvAvailPcs.setText(""+availQty);
			if(inventoryObject.openingQTY!=null)
			tvOpeningPcs.setText(""+inventoryObject.openingQTY);
			else
				tvOpeningPcs.setText("0");
				
			
			
			tvSchemeQtyList.setText("0");
			tvFocPcs.setText("0");
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
		public void refresh(Vector<InventoryObject> vecInventoryItems) 
		{
			this.vecInventoryItems = vecInventoryItems;
			notifyDataSetChanged();
		}
	}
	
	private int getUnitfromUOM(float availCases,int uomFactor)
	{
		int units = 0;
		if((int)uomFactor > 0)
			units = (int)availCases/(int)uomFactor;
		return units;
	}
}
