package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.kwalitysfa.salesman.R;

public class OrderTraceSummaryDetail extends BaseActivity
{
	private LinearLayout llSummaryofDay;
	private ListView lvSalesOrder;
	private ArrayList<TrxDetailsDO> arrayList;
	private TrxHeaderDO trxHeaderDO;
	private OrderDetailAdapter orderDetailAdapter;
	private Bundle bundle;
	private TextView tvOrderTag,tvOrder,tvCustomerTag,tvCustomer,tvStatusTag,tvStatus,tvNoOrderFound,tvCancelled,tvInProcess,tvShipped;
	private int trxStatus;
	private Button btnPrintOrderSummary;
	@Override
	public void initialize() 
	{
		bundle = getIntent().getExtras();
		if(bundle!= null)
		{
			trxHeaderDO	 	=	(TrxHeaderDO) bundle.get("trxHeaderDO");
			arrayList       =   trxHeaderDO.arrTrxDetailsDOs;
			trxStatus		=   bundle.getInt("trxStatus");
		}
		
		
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.order_trace_summary, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		
		intializeControls();
		
		if(trxHeaderDO != null)
			tvOrder.setText(trxHeaderDO.trxCode);
		
		tvStatus.setText(getStatus(trxStatus));
		tvCustomer.setText(trxHeaderDO.clientCode);
		orderDetailAdapter = new OrderDetailAdapter(arrayList);
		lvSalesOrder.setAdapter(orderDetailAdapter);
		setTypeFaceRobotoNormal(llSummaryofDay);
		btnPrintOrderSummary.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader(getResources().getString(R.string.loading));
				new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						final JourneyPlanDO mallsDetails	=	new CustomerDetailsDA().getCustometBySiteId(trxHeaderDO.clientCode);
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								hideLoader();
								if(arrayList != null && arrayList.size() > 0 && trxHeaderDO != null)
								{
									Intent intent = new Intent(OrderTraceSummaryDetail.this, WoosimPrinterActivity.class);
									intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
									intent.putExtra("trxHeaderDO", trxHeaderDO);
									intent.putExtra("mallsDetails", mallsDetails);
									startActivity(intent);
								}
								else
									showCustomDialog(OrderTraceSummaryDetail.this, "Warning !", "Error occurred while printing.", "OK", null, "");
							}
						});
					}
				}).start();
			}
		});
	}
	
	private String getStatus(int trxStatus)
	{
		String status = "";
		if(trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
		{
			status = "Saved";
		}
		else if(trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED())
		{
			status = "Settled";
		}
		else if(trxStatus == TrxHeaderDO.get_TRX_STATUS_CANCELLED())
		{
			status = "Cancelled";
		}
		else if(trxStatus == TrxHeaderDO.get_TRX_STATUS_WAREHOUSE())
		{
			status = "Warehouse";
		}
		else 
		{
			status = "Delivered";
		}
		return status;
		
	}
	
	public void intializeControls()
	{
		tvOrderTag				=	(TextView)llSummaryofDay.findViewById(R.id.tvOrderTag);
		
		tvOrder		  		    =	(TextView)llSummaryofDay.findViewById(R.id.tvOrder);
		tvCustomerTag		 	=	(TextView)llSummaryofDay.findViewById(R.id.tvCustomerTag);
		
		tvCustomer				=	(TextView)llSummaryofDay.findViewById(R.id.tvCustomer);
		btnPrintOrderSummary	=	(Button)llSummaryofDay.findViewById(R.id.btnPrintOrderSummary);
		tvStatusTag				=   (TextView)llSummaryofDay.findViewById(R.id.tvStatusTag);
		tvNoOrderFound			=   (TextView)llSummaryofDay.findViewById(R.id.tvNoOrderFound);
		tvStatus		  	 	=   (TextView)llSummaryofDay.findViewById(R.id.tvStatus);
		lvSalesOrder			=   (ListView)llSummaryofDay.findViewById(R.id.lvOrderList);
		
		tvShipped				=  (TextView)llSummaryofDay.findViewById(R.id.tvShipped);
		tvInProcess				=  (TextView)llSummaryofDay.findViewById(R.id.tvInProcess);
		tvCancelled				=  (TextView)llSummaryofDay.findViewById(R.id.tvCancelled);
		
		lvSalesOrder.setCacheColorHint(0);
		lvSalesOrder.setVerticalScrollBarEnabled(false);
		lvSalesOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSalesOrder.setFadingEdgeLength(0);
		
		if(arrayList == null || arrayList.size() == 0)
		{
			tvNoOrderFound.setVisibility(View.VISIBLE);
			lvSalesOrder.setVisibility(View.GONE);
		}
		else
		{
			tvNoOrderFound.setVisibility(View.GONE);
			lvSalesOrder.setVisibility(View.VISIBLE);
		}
		
			
		
	}
	
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard.isShown())
			TopBarMenuClick();
		else
		{
			finish();
			setResult(2000);
		}
	}
	
	private class OrderDetailAdapter extends BaseAdapter
	{
		ArrayList<TrxDetailsDO> arrayList;
		public OrderDetailAdapter(ArrayList<TrxDetailsDO> arrayList)
		{
			this.arrayList = arrayList;
		}

		@Override
		public int getCount() 
		{
			if(arrayList != null && arrayList.size() > 0)
				return arrayList.size();
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			TrxDetailsDO trxDetailsDO  = 	arrayList.get(position);

			if(convertView == null)
				convertView				= 	(LinearLayout)getLayoutInflater().inflate(R.layout.order_trace_summary_detail,null);

			TextView tvHeaderText		= 	(TextView)convertView.findViewById(R.id.tvHeaderText);
			TextView tvDescription		= 	(TextView)convertView.findViewById(R.id.tvDescription);
			EditText evUOMType			= 	(EditText)convertView.findViewById(R.id.evUOMType);
			EditText evUnits			= 	(EditText)convertView.findViewById(R.id.evUnits);
			EditText evShipped			= 	(EditText)convertView.findViewById(R.id.evShipped);
			EditText evCancelled		= 	(EditText)convertView.findViewById(R.id.evCancelled);
			EditText evInProcess		= 	(EditText)convertView.findViewById(R.id.evInProcess);
	    	
			tvHeaderText.setText(trxDetailsDO.itemCode);
			tvDescription.setText(trxDetailsDO.itemDescription);
			
			evUOMType.setText(trxDetailsDO.UOM);
			evUnits.setText(""+trxDetailsDO.quantityBU);
			
			if(trxHeaderDO.trxType == 1)
			{
				evShipped.setVisibility(View.VISIBLE);
				evCancelled.setVisibility(View.VISIBLE);
				evInProcess.setVisibility(View.VISIBLE);
				
				evShipped.setText(""+trxDetailsDO.requestedBU);
				evCancelled.setText(""+trxDetailsDO.missedBU);
				evInProcess.setText(""+trxDetailsDO.approvedBU);
				
				tvShipped.setVisibility(View.VISIBLE);				
				tvInProcess.setVisibility(View.VISIBLE);			
				tvCancelled.setVisibility(View.VISIBLE);
				
				tvShipped.setText("Requested \nQty");
				tvInProcess.setText("Missed \nQty");
				tvCancelled.setText("Approved \nQty");
			}
			else if(trxStatus!= TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && trxStatus!= TrxHeaderDO.get_TRX_STATUS_SAVED())
			{
				evShipped.setText(""+trxDetailsDO.shippedQuantity);
				evCancelled.setText(""+trxDetailsDO.cancelledQuantity);
				evInProcess.setText(""+trxDetailsDO.inProcessQuantity);
				
				evShipped.setVisibility(View.VISIBLE);
				evCancelled.setVisibility(View.VISIBLE);
				evInProcess.setVisibility(View.VISIBLE);
				
				tvShipped.setVisibility(View.VISIBLE);				
				tvInProcess.setVisibility(View.VISIBLE);			
				tvCancelled.setVisibility(View.VISIBLE);
				
				tvShipped.setText("Shipped \nQty");
				tvInProcess.setText("InProcess \nQty");
				tvCancelled.setText("Cancelled \nQty");
			}
			else
			{
				evShipped.setVisibility(View.GONE);
				evCancelled.setVisibility(View.GONE);
				evInProcess.setVisibility(View.GONE);
				
				tvShipped.setVisibility(View.GONE);				
				tvInProcess.setVisibility(View.GONE);	
				tvCancelled.setVisibility(View.GONE);
			}
			

			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)(50 * BaseActivity.px)));
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
	}
	
	
	

}
