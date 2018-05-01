package com.winit.sfa.salesman;

import java.util.Vector;

import android.content.Intent;
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
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class ReturnOrderActivity extends BaseActivity
{
	//Initialing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llCreditLimitLayout;
	private TextView tvNoOrderFound, tvCustomerCredit, tvCustomerOutStandingBalance, tvCustomerCreditAvail,tvPaymentType, tvPaymentTermDesc;
	private ListView lvOrderList;
	private orderListAdapter orListAdapter;
	private Vector<OrderDO> vecOrderList;
	private Button btnAddNewOrder, btnGRVNote, btnCollectPayment, btnFinishActivity,btnRefreshActivity;
	private JourneyPlanDO mallsDetails;
	private TextView tvPageTitle;

	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llOrder_List.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText("");
	}
	@Override
	public void initialize() 
	{
		llOrder_List = (LinearLayout) inflater.inflate(R.layout.delivery_agent_order_list, null);
		
		if(getIntent().getExtras() != null)
		{
			mallsDetails	=	(JourneyPlanDO) getIntent().getExtras().get("object");
			
		}
		
		setHeaderBar();
		intialiseControls();
		
		btnGRVNote.setVisibility(View.GONE);
		btnCollectPayment.setVisibility(View.GONE);
		btnAddNewOrder.setVisibility(View.GONE);
		
		btnFinishActivity.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		
		btnRefreshActivity.setVisibility(View.GONE);
		
		btnRefreshActivity.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				showLoader("pending for approval...");
				
//				new Handler().postDelayed(new Runnable() 
//				{
//					
//					@Override
//					public void run() 
//					{
//						hideLoader();
//						showOrderCompletePopup();
//					}
//				},3000);
				
				
			}
		});
		
		llBody.addView(llOrder_List, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setTypeFaceRobotoNormal(llOrder_List);
	}
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(ReturnOrderActivity.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1		  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Return Order Placed");
		tv_poptitle1.setText("Successfully");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment		  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_returnreq		  = (Button) view.findViewById(R.id.btn_popup_returnreq);
		Button btn_popup_task	  = (Button) view.findViewById(R.id.btn_popup_task);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		Button btn_popup_survey		  = (Button) view.findViewById(R.id.btn_popup_survey);
		Button btnPlaceNewOrder		  = (Button) view.findViewById(R.id.btnPlaceNewOrder);
		
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.GONE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_MODERN))
			btnPlaceNewOrder.setVisibility(View.GONE);
		
		if(AppConstants.isServeyCompleted)
		{
			btn_popup_survey.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.order_order), null, getResources().getDrawable(R.drawable.check1_new), null);
			btn_popup_survey.setClickable(false);
			btn_popup_survey.setEnabled(false);
		}
		
		if(AppConstants.isTaskCompleted)
		{
			btn_popup_task.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.taks_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		}
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
//				Intent intent = new Intent(ReturnOrderActivity.this, WoosimPrinterActivity.class);
//    			intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_RETURN);
//    			intent.putExtra("mallsDetails", mallsDetails);
//    			intent.putExtra("totalCases", totalCases);
//    			intent.putExtra("totalUnits", totalUnits);
//    			intent.putExtra("OrderId",orderId);
//    			intent.putExtra("postDate", orderDO.InvoiceDate.split("T")[0]);
//    			intent.putExtra("totalPrice", totalPrice);
//    			intent.putExtra("from", from);
//				startActivityForResult(intent, 1000);
//				showToast("Print functionality is in progress.");
//				//Harcoded
			}
		});
		
		btn_popup_survey.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
//				Intent intent = new Intent(SalesmanReturnOrderPreview.this, ConsumerBehaviourSurveyActivityNew.class);
//				intent.putExtra("object", mallsDetails);
//				startActivityForResult(intent, 2222);
				
				Intent intent = new Intent(ReturnOrderActivity.this, ServeyListActivity.class);
				startActivity(intent);
			}
		});
		
		if(mallsDetails != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.VISIBLE);
		}
		
		btnPlaceNewOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if(AppConstants.hmCapturedInventory != null && AppConstants.hmCapturedInventory.size() > 0)
					AppConstants.hmCapturedInventory.clear();
				
				Intent intent = new Intent(ReturnOrderActivity.this, SalesManTakeOrder.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("from", "checkin");
				startActivity(intent);
			}
		});
		
		if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			btn_popup_collectpayment.setVisibility(View.GONE);
		else
		{
			btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
			btn_popup_collectpayment.setClickable(false);
			btn_popup_collectpayment.setEnabled(false);
		}
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("payment_new");
			}
		});
		
		btn_popup_returnreq.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.return_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_returnreq.setClickable(false);
		btn_popup_returnreq.setEnabled(false);
		
		
		btn_popup_task.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				Intent intent = new Intent(ReturnOrderActivity.this, TaskToDoActivity.class);
				intent.putExtra("object", mallsDetails);
				startActivity(intent);
			}
		});
//		btn_popup_done.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
//				mCustomDialog.dismiss();
//				if(mallsDetails.customerType != null && mallsDetails.customerType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && orderId != null && orderId.length() > 0)
//				{
//					if(! new PaymentDetailDA().isPaymentDone(orderId))
//						onButtonYesClick("payment");
//					else
//						onButtonYesClick("served");
//				}
//				else
//					onButtonYesClick("served");
//				
//				finish();
//			}
//		});
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
	
	/*setting route and month performance of Salesman*/
	
	public void intialiseControls()
	{
		//getting id's
		tvNoOrderFound		= 	(TextView)llOrder_List.findViewById(R.id.tvNoOrderFound);
		llAddNewOrder		=	(LinearLayout)llOrder_List.findViewById(R.id.llAddNewOrder);
		llLayoutMiddle 		=	(LinearLayout)llOrder_List.findViewById(R.id.llLayoutMiddle);
		llCreditLimitLayout	=	(LinearLayout)llOrder_List.findViewById(R.id.llCreditLimitLayout);
		btnAddNewOrder		=	(Button)llOrder_List.findViewById(R.id.btnAddNewOrder);
		btnGRVNote			=	(Button)llOrder_List.findViewById(R.id.btnGRVNote);
		btnCollectPayment	=	(Button)llOrder_List.findViewById(R.id.btnCollectPayment);
		btnFinishActivity	=	(Button)llOrder_List.findViewById(R.id.btnFinishActivity);
		btnRefreshActivity  =   (Button)llOrder_List.findViewById(R.id.btnRefreshActivity);
		
		tvNoOrderFound.setText("No Order available.");
		tvCustomerCredit				=	(TextView)llOrder_List.findViewById(R.id.tvCustomerCredit);
		tvCustomerOutStandingBalance	=	(TextView)llOrder_List.findViewById(R.id.tvCustomerOutStandingBalance);
		tvCustomerCreditAvail			=	(TextView)llOrder_List.findViewById(R.id.tvCustomerCreditAvail);
		tvPaymentType					=	(TextView)llOrder_List.findViewById(R.id.tvPaymentType);
		tvPaymentTermDesc				=	(TextView)llOrder_List.findViewById(R.id.tvPaymentTermDesc);
		
		llOrder_List. findViewById(R.id.viewDividerOne).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llMonthForfarmance).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.viewDividerTwo).setVisibility(View.GONE);
		llOrder_List. findViewById(R.id.llRoutePerformance).setVisibility(View.GONE);
		
		//setting type-faces
		tvPageTitle.setText(mallsDetails.siteName+"'s Order List");
	
		if(mallsDetails != null)
		{
			tvCustomerCredit.setText("Site Name: "+mallsDetails.siteName);
			tvCustomerCreditAvail.setText("Customer Site No.: "+mallsDetails.site);
			tvCustomerOutStandingBalance.setText("Customer Type: "+mallsDetails.customerPaymentType);
			
			if(mallsDetails.addresss1 != null && !mallsDetails.addresss1.equalsIgnoreCase(""))
				tvPaymentType.setText(mallsDetails.addresss1);
			else
				tvPaymentType.setVisibility(View.GONE);
			
			if(mallsDetails.addresss2 != null && !mallsDetails.addresss2.equalsIgnoreCase(""))
			{
				if(mallsDetails.addresss2 != null)
				{
					
					if(mallsDetails.addresss2.endsWith(","))
						mallsDetails.addresss2 = mallsDetails.addresss2.substring(0, mallsDetails.addresss2.lastIndexOf(","));
					else
						tvPaymentTermDesc.setText(mallsDetails.addresss2);
				}
			}
			else
				tvPaymentTermDesc.setVisibility(View.GONE);
		}
		tvCustomerOutStandingBalance.setVisibility(View.VISIBLE);
		
		//setting visibility for button Add new customer
		llAddNewOrder.setVisibility(View.GONE);
		llCreditLimitLayout.setVisibility(View.VISIBLE);
		
		orListAdapter 		= 	new orderListAdapter(new Vector<OrderDO>());
		lvOrderList 		= 	new ListView(ReturnOrderActivity.this);
		lvOrderList.setFadingEdgeLength(0);
		lvOrderList.setCacheColorHint(0);
		lvOrderList.setDivider(null);
		lvOrderList.setVerticalScrollBarEnabled(false);
		lvOrderList.setAdapter(orListAdapter);
		llLayoutMiddle.addView(lvOrderList , LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		btnGRVNote.setVisibility(View.GONE);
//		if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		if(isEOTDone())
		{
			btnGRVNote.setVisibility(View.GONE);
			btnCollectPayment.setVisibility(View.GONE);
			btnAddNewOrder.setVisibility(View.GONE);
			btnFinishActivity.setVisibility(View.VISIBLE);
		}
	}
	
	
	public class orderListAdapter extends BaseAdapter
	{
		Vector<OrderDO> vecorOrderList;
		public orderListAdapter(Vector<OrderDO> vecorOrderListTmp) 
		{
			this.vecorOrderList = vecorOrderListTmp;
		}

		@Override
		public int getCount() 
		{
			if(vecorOrderList != null && vecorOrderList.size() > 0)
			{
				tvNoOrderFound.setVisibility(View.GONE);
				return vecorOrderList.size();
			}
			else
				tvNoOrderFound.setVisibility(View.VISIBLE);
			
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			OrderDO objCustomerOrder = 	 vecorOrderList.get(position);
			
			if(convertView ==null)
				convertView         =	 (LinearLayout)getLayoutInflater().inflate(R.layout.mng_stf_cell, null);
			TextView tvOerder		= 	 (TextView)convertView.findViewById(R.id.tvMngStfName);
			TextView tvOerderId		= 	 (TextView)convertView.findViewById(R.id.tvMngStfMemberId);
			View ivServedOrder		= 	 (View)convertView.findViewById(R.id.ivServedOrder);
			TextView tvPendingStatus =    (TextView)convertView.findViewById(R.id.tvPendingStatus);
			
			
			if(objCustomerOrder.returnOrderStatus == 0)
				tvPendingStatus.setText(getResources().getString(R.string.pending));
			else
				tvPendingStatus.setText(getResources().getString(R.string.approved));
			
			
			if(objCustomerOrder.orderSubType != null)
			{
				if(objCustomerOrder.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER))
				{
					tvOerder.setText("LPO Order no. ");
					tvOerderId.setText(""+objCustomerOrder.OrderId);
					if(objCustomerOrder.TRXStatus != null && (objCustomerOrder.TRXStatus.equalsIgnoreCase("E") || objCustomerOrder.TRXStatus.equalsIgnoreCase("0")))
						ivServedOrder.setVisibility(View.INVISIBLE);
					else
						ivServedOrder.setVisibility(View.VISIBLE);
				}
				else if(objCustomerOrder.orderSubType.equalsIgnoreCase(AppConstants.MOVE_ORDER))
				{
					tvOerder.setText("MOVE Order no. ");
					tvOerderId.setText(""+objCustomerOrder.OrderId);
					
					if(objCustomerOrder.TRXStatus != null && (objCustomerOrder.TRXStatus.equalsIgnoreCase("E") || objCustomerOrder.TRXStatus.equalsIgnoreCase("0")))
						ivServedOrder.setVisibility(View.INVISIBLE);
					else
						ivServedOrder.setVisibility(View.VISIBLE);
				}
				else if(objCustomerOrder.orderType.equalsIgnoreCase(AppConstants.RETURNORDER))
				{
					tvOerder.setText("Return Order no. ");
					tvOerderId.setText(""+objCustomerOrder.OrderId);
					ivServedOrder.setVisibility(View.VISIBLE);
				}
				else if(objCustomerOrder.orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
				{
					tvOerder.setText("Replacement Order no. ");
					tvOerderId.setText(""+objCustomerOrder.OrderId);
					ivServedOrder.setVisibility(View.VISIBLE);
				}
				else 
				{
					if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_MT))
						tvOerder.setText("LPO Order no. ");
					else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
						tvOerder.setText("MOVE Order no. ");
					else
						tvOerder.setText("Sales Order no. ");
					
					tvOerderId.setText(""+objCustomerOrder.OrderId);
					ivServedOrder.setVisibility(View.VISIBLE);
				}
			}
			
			tvOerder.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvOerderId.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			convertView.setTag(objCustomerOrder);
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					OrderDO objOrder = (OrderDO) v.getTag();
					
//					if(objOrder.orderSubType != null && (objOrder.orderSubType.equalsIgnoreCase(AppConstants.LPO_ORDER) || objOrder.orderSubType.equalsIgnoreCase(AppConstants.MOVE_ORDER) )&& 
//							(objOrder.TRXStatus != null && (objOrder.TRXStatus.equalsIgnoreCase("E") && objOrder.DeliveryDate.contains(CalendarUtils.getCurrentDateAsString()))))
//					{
//						Intent intent = new Intent(ReturnOrderActivity.this, SalesManTakeOrder.class);
//						intent.putExtra("orderid", objOrder);
//						intent.putExtra("mallsDetails", mallsDetails);
//						startActivity(intent);
//					}
//					else
//					{
//						Intent intent = new Intent(ReturnOrderActivity.this, SalesmanOrderDetail.class);
//						intent.putExtra("mallsDetails", mallsDetails);
//						intent.putExtra("orderid", objOrder);
//						startActivity(intent);
//					}
					
					Intent intent = new Intent(ReturnOrderActivity.this, SalesmanPendingReturnOrderPreview.class);
					intent.putExtra("objOrder", objOrder);
					intent.putExtra("mallsDetails", mallsDetails);
					startActivity(intent);
				}
			});
			
			ivServedOrder.setLayoutParams(new LinearLayout.LayoutParams((int)(5*px),(int)(43*px)));
			//setting LayoutParams to  convertView (List Cell)
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, (int)(45 * BaseActivity.px)));
			return convertView;	
		}
		//Method to refresh the ListView
		public void refresh(Vector<OrderDO> vecorOrderList)
		{
			this.vecorOrderList = vecorOrderList;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		
		if(from.equalsIgnoreCase("payment"))
		{
			Intent intent = new Intent(ReturnOrderActivity.this, PendingInvoices.class);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("AR", false);
			startActivity(intent);
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();	
		}
		
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if(tvPageTitle != null)
			tvPageTitle.setText(""+mallsDetails.siteName+"'s Order List");
		
		showLoader(getResources().getString(R.string.please_wait));
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				vecOrderList = 	new OrderDA().getCustomerReturnOrderList(mallsDetails.site, CalendarUtils.getOrderPostDate());
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						orListAdapter.refresh(vecOrderList);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
