package com.winit.sfa.salesman;

import java.util.Vector;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
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
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesmanOrderList extends BaseActivity
{
	//Initialing and declaration of variables
	private LinearLayout llOrder_List, llLayoutMiddle, llAddNewOrder, llCreditLimitLayout;
	private TextView tvNoOrderFound, tvCustomerCredit, tvCustomerOutStandingBalance, tvCustomerCreditAvail,tvPaymentType, tvPaymentTermDesc;
	private ListView lvOrderList;
	private OrderListAdapter orListAdapter;
	private Button btnAddNewOrder, btnGRVNote, btnCollectPayment, btnFinishActivity, btnTakeAdvanceOrder;
	private JourneyPlanDO mallsDetails;
	//header bar
	private TextView tvPageTitle;
	private Vector<TrxHeaderDO> vecTRXHeaderDO;

	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llOrder_List.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText("");
	}
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout
		llOrder_List = (LinearLayout) inflater.inflate(R.layout.delivery_agent_order_list, null);
		
		if(getIntent().getExtras() != null)
			mallsDetails	=	(JourneyPlanDO) getIntent().getExtras().get("object");
		
		setHeaderBar();
		//function for getting id's and setting type-faces
		intialiseControls();
		
//		btnCollectPayment.setVisibility(View.GONE);
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
		
		btnTakeAdvanceOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SalesmanOrderList.this, SalesManRecommendedOrder.class);
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER());
				intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());
				startActivity(intent);
			}
		});
		
		llBody.addView(llOrder_List, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setTypeFaceRobotoNormal(llOrder_List);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnAddNewOrder.setTypeface(Typeface.DEFAULT_BOLD);
		btnGRVNote.setTypeface(Typeface.DEFAULT_BOLD);
		btnCollectPayment.setTypeface(Typeface.DEFAULT_BOLD);
		btnFinishActivity.setTypeface(Typeface.DEFAULT_BOLD);
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
		btnTakeAdvanceOrder =	(Button) llOrder_List.findViewById(R.id.btnTakeAdvanceOrder);
		
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
		
		orListAdapter 		= 	new OrderListAdapter(new Vector<TrxHeaderDO>());
		lvOrderList 		= 	new ListView(SalesmanOrderList.this);
		lvOrderList.setFadingEdgeLength(0);
		lvOrderList.setCacheColorHint(0);
		/******Removed by Aritra as per issues by Prashant sir**********/
//		lvOrderList.setDivider(null);
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
	
	public class OrderListAdapter extends BaseAdapter
	{
		private Vector<TrxHeaderDO> vecOrderList;
		public OrderListAdapter(Vector<TrxHeaderDO> vecOrderList) 
		{
			this.vecOrderList = vecOrderList;
		}
		@Override
		public int getCount() 
		{
			if(vecOrderList!=null && vecOrderList.size()>0)
				return vecOrderList.size();
			
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
		public void refresh(Vector<TrxHeaderDO> vecOrder)
		{
			this.vecOrderList=vecOrder;
			notifyDataSetChanged();
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			TrxHeaderDO trxHeaderDO			    = (TrxHeaderDO) vecOrderList.get(position);
			
			if(convertView == null)
				convertView					    =	LayoutInflater.from(SalesmanOrderList.this).inflate(R.layout.delivery_status_list_item, null);
			
			TextView tvJournyeDate			 	=	(TextView)convertView.findViewById(R.id.tvJournyeDate);
			TextView tvJournyeDateYear		  	=	(TextView)convertView.findViewById(R.id.tvJournyeDateYear);
			
			
			TextView tvOrderNo					=	(TextView)convertView.findViewById(R.id.tvOrderNo);
			TextView tvCustomerName				=	(TextView)convertView.findViewById(R.id.tvCustomerName);
			TextView tvCustomerLocation			=	(TextView)convertView.findViewById(R.id.tvCustomerLocation);
			TextView tvSatus					=	(TextView)convertView.findViewById(R.id.tvStatus);
			
			TextView tvCustomerPrice			=	(TextView)convertView.findViewById(R.id.tvCustomerPrice);
			TextView tvCustomerPriceUnit		=	(TextView)convertView.findViewById(R.id.tvCustomerPriceUnit);
			
			tvSatus.setVisibility(View.GONE);
			String date[] = CalendarUtils.getRequiredDateFormat(""+trxHeaderDO.trxDate).split("yy");
			tvJournyeDate.setText(date[0]);
			tvJournyeDateYear.setText(date[1]);
		
			tvOrderNo.setText("Order No : "+trxHeaderDO.trxCode);
			
			tvCustomerPrice.setText(""+(((BaseActivity)SalesmanOrderList.this).decimalFormat).format(trxHeaderDO.totalAmount));
			tvCustomerPriceUnit.setVisibility(View.VISIBLE);
			
			tvCustomerName.setText(trxHeaderDO.siteName+"");
			tvCustomerLocation.setText(trxHeaderDO.address+"");
			
			convertView.setTag(trxHeaderDO);
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = null;
					TrxHeaderDO objOrderList = 	(TrxHeaderDO) v.getTag();
					
					objOrderList = (TrxHeaderDO) v.getTag();
					intent = new Intent(SalesmanOrderList.this , OrderSummaryDetail.class);
					intent.putExtra("trxHeaderDO", objOrderList);
					intent.putExtra("mallsDetails", mallsDetails);
					startActivity(intent);
				}
			});
			
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			
			tvOrderNo.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvCustomerPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			return convertView;
		}
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
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
				vecTRXHeaderDO = new CommonDA().getOrderDetailsBasedOnTrxType(preference.getStringFromPreference(preference.EMP_NO, ""), mallsDetails.site, TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER());
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						orListAdapter.refresh(vecTRXHeaderDO);
						hideLoader();
					}
				});
			}
		}).start();
	}
}
