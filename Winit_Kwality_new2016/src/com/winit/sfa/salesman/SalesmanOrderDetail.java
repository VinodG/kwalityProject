package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
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

import com.winit.alseer.parsers.OrderDeleteParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class SalesmanOrderDetail extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llItemHeader, llPaymentLayout;
	private TextView tvHead, tvDeliveryAmount;
	private ListView lvReturnorder;
	private orderDetailAdapter orListAdapter;
	private Button btnAddNewOrder,btnModify, btnDelete;
	private ArrayList<ProductDO> vecOrdProduct;
	private OrderDO objOrders;
	private JourneyPlanDO mallsDetails;
	private String receiptNumber = "";
	private EditText edtDeliveryTotalamt;
	
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout
		llOrder_List = (LinearLayout)getLayoutInflater().inflate(R.layout.delivery_agent_order_detail, null);
		llBody.addView(llOrder_List, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		//getting data from Intent
		if(getIntent().getExtras() != null)
		{
			objOrders 		= 	(OrderDO) getIntent().getSerializableExtra("orderid");
			mallsDetails 	=	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
		}
		
		//function for getting id'ss and setting type-faces
		intialiseControls();
		
		lvReturnorder.setFadingEdgeLength(0);
		lvReturnorder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvReturnorder.setVerticalScrollBarEnabled(false);
		lvReturnorder.setAdapter(orListAdapter);
		llLayoutMiddle.addView(lvReturnorder , LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//click event for button AddNewOrder 
		
		btnAddNewOrder.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.check5), null, null, null);
		
		btnAddNewOrder.setText("Finish");
		btnAddNewOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		btnDelete.setVisibility(View.GONE);
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT)
			&& (!objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || !objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)))
			btnModify.setVisibility(View.VISIBLE);
		else
			btnModify.setVisibility(View.GONE);
		
		btnModify.setVisibility(View.GONE);
		btnModify.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader("Loading...");
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						receiptNumber = new CommonDA().getOrderStatus(objOrders.OrderId);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if(receiptNumber == null || receiptNumber.length() <= 0)
								{
									Intent intent = new Intent(SalesmanOrderDetail.this, SalesManTakeOrder.class);
									intent.putExtra("orderid", objOrders);
									intent.putExtra("mallsDetails", mallsDetails);
									startActivity(intent);
								}
								else
									showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Payment has been made for this invoice with receipt number - "+receiptNumber +". Please delete the receipt before modifying invoice.", "Delete Receipt", "Cancel", "DeleteReceipt");
								
								hideLoader();
							}
						});
					}
				}).start();
			}
		});

		setTypeFaceRobotoNormal(llOrder_List);
		tvHead.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddNewOrder.setTypeface(Typeface.DEFAULT_BOLD);
		btnModify.setTypeface(Typeface.DEFAULT_BOLD);
		btnDelete.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	/** initializing all the Controls  of DeliveryAgentOrderList class **/
	public void intialiseControls()
	{
		//getting id's
		tvHead 				= 	(TextView)llOrder_List.findViewById(R.id.tvHead);
		llAddNewOrder		=	(LinearLayout)llOrder_List.findViewById(R.id.llAddNewOrder);
		llLayoutMiddle 		=	(LinearLayout)llOrder_List.findViewById(R.id.llLayoutMiddle);
		btnModify			=	(Button)llOrder_List.findViewById(R.id.btnModify);
		llItemHeader		=	(LinearLayout)llOrder_List.findViewById(R.id.llItemHeader);
		llPaymentLayout		=	(LinearLayout)llOrder_List.findViewById(R.id.llPaymentLayout);
		TextView tvItemCode	= 	(TextView)llOrder_List.findViewById(R.id.tvItemCode);
		TextView tvUnits	= 	(TextView)llOrder_List.findViewById(R.id.tvUnits);
		TextView tvCases	= 	(TextView)llOrder_List.findViewById(R.id.tvCases);
		
		tvDeliveryAmount	=	(TextView)llOrder_List.findViewById(R.id.tvDeliveryAmount);
		edtDeliveryTotalamt	=	(EditText)llOrder_List.findViewById(R.id.edtDeliveryTotalamt);
		
		
		btnDelete			=	(Button)llOrder_List.findViewById(R.id.btnGRVNote);
		Button btnCollectPayment	=	(Button)llOrder_List.findViewById(R.id.btnCollectPayment);
		btnAddNewOrder = (Button)llOrder_List.findViewById(R.id.btnAddNewOrder);
		
		llOrder_List. findViewById(R.id.viewDividerOne).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llMonthForfarmance).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.viewDividerTwo).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llRoutePerformance).setVisibility(View.GONE);
		
		
		//Setting visibility for the Bottom button
		llAddNewOrder.setVisibility(View.VISIBLE);
		btnDelete.setVisibility(View.VISIBLE);
		btnCollectPayment.setVisibility(View.GONE);
		btnModify.setVisibility(View.VISIBLE);
		
		//setting type-faces
		btnDelete.setText(" Delete Order ");
		
		orListAdapter 		= 	new orderDetailAdapter(vecOrdProduct);
		lvReturnorder 		= 	new ListView(SalesmanOrderDetail.this);
		if(objOrders!=null && (objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))))
		{
			tvHead.setText("Order Details (Order No "+objOrders.OrderId+")");
			llItemHeader.setVisibility(View.VISIBLE);
		}
		else if(objOrders != null )
		{  
			if(objOrders.orderSubType != null && !objOrders.orderSubType.equalsIgnoreCase(AppConstants.Receipt))
				tvHead.setText("Payment details (Receipt No. - "+objOrders.OrderId+")");
			else
				tvHead.setText("Payment details ");
			
			llPaymentLayout.setVisibility(View.VISIBLE);
			
			if(llLayoutMiddle.getChildCount() > 0)
				llLayoutMiddle.removeAllViews();
			
			LinearLayout llPayment 			= (LinearLayout) inflater.inflate(R.layout.payment_detail_cell, null);
			//getting Id's
			TextView tvInvoiceNumber 		=	(TextView)llPayment.findViewById(R.id.tvInvoiceNumber);
			TextView tvAmountCell 			=	(TextView)llPayment.findViewById(R.id.tvAmountCell);
			TextView tvPaidAmount			=	(TextView)llPayment.findViewById(R.id.tvPaidAmount);
			TextView tvPaymentType			=	(TextView)llPayment.findViewById(R.id.tvPaymentType);
			TextView tvPaymentTypeTitle		=	(TextView)llPayment.findViewById(R.id.tvPaymentTypeTitle);
			
			//setting Type-faces
			/*tvPaymentType.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			tvPaymentTypeTitle.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			tvInvoiceNumber.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			tvPaidAmount.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
			tvAmountCell.setTypeface(AppConstants.Helvetica_LT_57_Condensed);*/
			
			tvPaymentType.setText(objOrders.orderType);
			tvInvoiceNumber.setText("Invoice number: "+objOrders.InvoiceNumber);
			tvAmountCell.setText(""+objOrders.TotalAmount);
			tvPaidAmount.setText(""+objOrders.BalanceAmount);
			
			llLayoutMiddle.addView(llPayment , LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public class orderDetailAdapter extends BaseAdapter
	{
		ArrayList<ProductDO> vecOrderList;
		public orderDetailAdapter(ArrayList<ProductDO> vecOrderList)
		{
			this.vecOrderList = vecOrderList;
		}
		
		@Override
		public int getCount() 
		{
			if(vecOrderList == null)
				return 0;
			return vecOrderList.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ProductDO orderDetail = vecOrderList.get(position);
			
			//inflate invoice_list_cell layout here
			if(convertView == null)
				convertView			=	(LinearLayout)getLayoutInflater().inflate(R.layout.invoice_list_cell,null);
			//getting id's here
			TextView tvProductKey	=	(TextView)convertView.findViewById(R.id.tvProductKey);
			TextView tvVendorName	=	(TextView)convertView.findViewById(R.id.tvVendorName);
			EditText etInvoice1		=	(EditText)convertView.findViewById(R.id.etInvoice1);
			EditText etInvoice2		=	(EditText)convertView.findViewById(R.id.etInvoice2);
			convertView.setTag(orderDetail);
			
			tvProductKey.setText(orderDetail.SKU);
			tvVendorName.setText(""+orderDetail.Description);
			etInvoice1.setText(orderDetail.preCases);
			etInvoice2.setText(""+StringUtils.getInt(orderDetail.preUnits));
			
			//Setting Type-faces here
			/*tvProductKey.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvVendorName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			etInvoice1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			etInvoice2.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			etInvoice1.setEnabled(false);
			etInvoice2.setEnabled(false);
			
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT , (int)(45 * BaseActivity.px)));
			return convertView;
		}
		
		public void refresh(ArrayList<ProductDO> vecOrderList)
		{
			this.vecOrderList = vecOrderList;
			notifyDataSetChanged();
		}
	}	
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if(objOrders!=null && (objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER)))
			tvHead.setText("Order Details (Order No "+objOrders.OrderId+")");
		else if(objOrders!=null && (objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)))
			tvHead.setText("Replacement Order Details (Order No "+objOrders.OrderId+")");
		else
		{
			if(objOrders.orderSubType != null && !objOrders.orderSubType.equalsIgnoreCase(AppConstants.Receipt))
				tvHead.setText("Payment details (Receipt No. - "+objOrders.OrderId+")");
			else
				tvHead.setText("Payment details ");
		}
		
		if(objOrders.orderType.equalsIgnoreCase(AppConstants.HHOrder) || (objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)) )
		{
			showLoader(getResources().getString(R.string.loading));
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					vecOrdProduct	=	new CommonDA().getDeliveryStatusOrderProducts(objOrders.OrderId);
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							orListAdapter.refresh(vecOrdProduct);
							
							if((objOrders.orderType.equalsIgnoreCase(AppConstants.RETURNORDER) || objOrders.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER)) && objOrders.orderSubType.equalsIgnoreCase(AppConstants.APPORDER))
								getTotalAmountWithDiscountAfterDeliveryReturn(vecOrdProduct);
							else
								getTotalAmountWithDiscountAfterDelivery(vecOrdProduct);
							hideLoader();
						}
					});
				}
			}).start();
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("deleted"))
		{
			Intent intent = new Intent(SalesmanOrderDetail.this, SalesManTakeOrder.class);
			intent.putExtra("orderid", objOrders);
			intent.putExtra("mallsDetails", mallsDetails);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("DeleteReceipt"))
		{
			deleteReceipt(receiptNumber);
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else
			super.onBackPressed();
	}
	
	int count = 0;
	boolean isDeleted = false;
	public void deleteReceipt(final String receiptNumber)
	{
		showLoader(getResources().getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String status = new CommonDA().getReceiptStatus(receiptNumber);
				
				count = 0;
				if(status != null && status.equalsIgnoreCase("1"))
				{
					if(isNetworkConnectionAvailable(SalesmanOrderDetail.this))
					{
						OrderDeleteParser orderDelete = new OrderDeleteParser(SalesmanOrderDetail.this);
						new ConnectionHelper(SalesmanOrderDetail.this).sendRequest_Bulk(SalesmanOrderDetail.this, BuildXMLRequest.deleteReceipt(receiptNumber),orderDelete,ServiceURLs.DELETE_RECEIPT_FROMAPP);
						if(orderDelete.getOrderDeleteStatus())
							count = 0;
						else
							count = -1;
					}
					else
						count = 10;
				}
				
				if(count == 0)
				{
					isDeleted = new CommonDA().updatePendingInvoices(receiptNumber);
					isDeleted = new CommonDA().deleteReceipt(receiptNumber);
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(count == 10)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), getString(R.string.no_internet), getResources().getString(R.string.OK), null, "");
						else if(isDeleted)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.successful), "Receipt deleted successfully.", getResources().getString(R.string.OK), null, "deleted",false);
						if(count == 1)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Receipt is already pushed to oracle.", getResources().getString(R.string.OK), null, "");
						else if(count == -1)
							showCustomDialog(SalesmanOrderDetail.this, getString(R.string.warning), "Error occured while deleting Receipt. Please try again.", getResources().getString(R.string.OK), null, "");
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void getTotalAmountWithDiscountAfterDelivery(ArrayList<ProductDO> vecdata)
	{
		float strDiscount     = 0.0f;
		float strTotalAmount  = 0.0f;
		
		if(vecdata != null && vecdata.size() > 0)
		{
			for(ProductDO obj : vecdata)
			{
				
				obj.isDiscountApplied = true;
				strDiscount    +=  obj.Discount;
				strTotalAmount +=  obj.invoiceAmount;
			}
			strDiscount = StringUtils.getFloat(decimalFormat.format((float)strDiscount/vecdata.size()));
			edtDeliveryTotalamt.setText(""+amountFormate.format(strTotalAmount));
		}
	}
	
	private void getTotalAmountWithDiscountAfterDeliveryReturn(ArrayList<ProductDO> vecdata)
	{
		float strDiscount     = 0.0f;
		float strTotalAmount  = 0.0f;
		
		if(vecdata != null && vecdata.size() > 0)
		{
			for(ProductDO obj : vecdata)
			{
				if(obj.reason.equalsIgnoreCase(getResources().getString(R.string.return_)))
				{
					obj.isDiscountApplied = true;
					strDiscount    +=  obj.Discount;
					strTotalAmount +=  obj.invoiceAmount;
				}
			}
			strDiscount = StringUtils.getFloat(decimalFormat.format((float)strDiscount/vecdata.size()));
			edtDeliveryTotalamt.setText(""+amountFormate.format(strTotalAmount));
		}
	}
}
