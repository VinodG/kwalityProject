package com.winit.sfa.salesman;

import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.CustomerStatmentDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.kwalitysfa.salesman.R;

public class PaymentSummaryReportActivity extends BaseActivity 
{
	private LinearLayout llSummaryofDay;
	private ListView lvSalesOrder;
	private TextView tvPageTitle,tvCustomerName,tvCustomerLocation,
	tvNoOrderFound,tvFromDate,tvToDate, tvRcptAmountTag,tvInvAmountTag;
	private PaymentSummaryReportAdapter customerStatementAdapter;
	private Vector<CustomerStatmentDO> vecDetails;
	private String fromDate,toDate;
	
	@Override
	public void initialize() 
	{
		
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.payment_summary_report, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intializeControls();
		loadCustomerStatement();
		
		setTypeFaceRobotoNormal(llSummaryofDay);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	public void intializeControls()
	{
		tvPageTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvHeader);
		
		tvNoOrderFound		=   (TextView)llSummaryofDay.findViewById(R.id.tvNoOrderFound);
		tvCustomerName		=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerName);
		tvCustomerLocation	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerLocation);
		
		lvSalesOrder		=   (ListView)llSummaryofDay.findViewById(R.id.lvOrderList);
		
		tvRcptAmountTag 	= (TextView)llSummaryofDay.findViewById(R.id.tvRcptAmountTag);
		tvInvAmountTag		=   (TextView)llSummaryofDay.findViewById(R.id.tvInvAmountTag);
		
		tvFromDate			=   (TextView)llSummaryofDay.findViewById(R.id.tvFromDate);
		tvToDate			=   (TextView)llSummaryofDay.findViewById(R.id.tvToDate);
		
		customerStatementAdapter = new PaymentSummaryReportAdapter();
		
		lvSalesOrder.setCacheColorHint(0);
		lvSalesOrder.setVerticalScrollBarEnabled(false);
		lvSalesOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSalesOrder.setFadingEdgeLength(0);
		lvSalesOrder.setAdapter(customerStatementAdapter);

		if(mallsDetailss!=null)
		{
			tvCustomerName.setText(mallsDetailss.siteName+" ["+mallsDetailss.clientCode+"]");
			tvCustomerLocation.setText(mallsDetailss.addresss1);
		}
		
		vecDetails	= new Vector<CustomerStatmentDO>();
		tvPageTitle.setText("Payment Summary Report");
		
		Calendar c = Calendar.getInstance();
		monthFrom = c.get(Calendar.MONTH);
		yearFrom = c.get(Calendar.YEAR);
		dayFrom = c.get(Calendar.DAY_OF_MONTH)-1;
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		
		String selectedDate = CalendarUtils.getOrderSummaryDate(yearFrom,monthFrom,dayFrom);
		
		fromDate = selectedDate;
		tvFromDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
    	tvFromDate.setTag(selectedDate);
    	
    	selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
    	
    	toDate = selectedDate;
		tvToDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
		tvToDate.setTag(selectedDate);
		
		tvToDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID_TO);
			}
		});
		tvFromDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID_FROM);
			}
		});
	}
	
	private void loadCustomerStatement()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecDetails = new OrderDA().getPaymentSummaryReport(fromDate, toDate,toDate,mallsDetailss.site);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						tvRcptAmountTag.setText("AED");
						tvInvAmountTag.setText("AED");
						if(vecDetails == null || vecDetails.size() == 0)
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
				});
			}
		}).start();
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
	
	public class PaymentSummaryReportAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount() 
		{
			if(vecDetails != null && vecDetails.size() > 0)
				return vecDetails.size();
			
			return 6;
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
		public View getView(final int position, View convertView, ViewGroup parent)
		{
//			final CustomerStatmentDO customerStatmentDO = vecDetails.get(position);
			final ViewHolder viewHolder;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.payment_summary_report_cell, null);
				
				viewHolder.tvSlNo 	 		= (TextView)convertView.findViewById(R.id.tvSlNo);
				viewHolder.tvRcptNo 	 	= (TextView)convertView.findViewById(R.id.tvRcptNo);
				viewHolder.tvRcptDate 		= (TextView)convertView.findViewById(R.id.tvRcptDate);
				viewHolder.tvRcptAmount 	= (TextView)convertView.findViewById(R.id.tvRcptAmount);
				viewHolder.tvChk 	 		= (TextView)convertView.findViewById(R.id.tvChk);
				viewHolder.tvCase 	 		= (TextView)convertView.findViewById(R.id.tvCase);
				viewHolder.tvInvAmount 	 	= (TextView)convertView.findViewById(R.id.tvInvAmount);
				viewHolder.tvInvNo 	 		= (TextView)convertView.findViewById(R.id.tvInvNo);

				convertView.setTag(viewHolder);
			}else
				viewHolder	= (ViewHolder) convertView.getTag();
				
//			viewHolder.tvSlNo.setText(customerStatmentDO.invoiceType);
//			viewHolder.tvRcptNo.setText(customerStatmentDO.invoiceType);
//			viewHolder.tvRcptDate.setText(customerStatmentDO.trxNumber);
//			viewHolder.tvRcptAmount.setText(customerStatmentDO.trxDate);
//			viewHolder.tvChk.setText(customerStatmentDO.amount);
//			viewHolder.tvCase.setText(customerStatmentDO.refNO);
//			viewHolder.tvInvAmount.setText(customerStatmentDO.amount);
//			viewHolder.tvInvNo.setText(customerStatmentDO.refNO);
			
			return convertView;
		}
		
		public class ViewHolder{
			TextView tvSlNo;
			TextView tvRcptNo;
			TextView tvRcptDate;
			TextView tvRcptAmount;
			TextView tvChk;
			TextView tvCase;
			TextView tvInvAmount;
			TextView tvInvNo;
		}
	}
	
	
	
	
	//----------------------------------------------------------------------------------------------------
	// for date filter
	//----------------------------------------------------------------------------------------------------

	private final int START_DATE_DIALOG_ID_FROM = 1, START_DATE_DIALOG_ID_TO = 2;
	private int monthFrom, yearFrom, dayFrom, monthTo, yearTo, dayTo;

	/**
	 * Open date picker for "From Date" and "To Date"
	 */
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id) 
		{
		case START_DATE_DIALOG_ID_FROM:
			return new DatePickerDialog(this, fromDateListner,  yearFrom, monthFrom, dayFrom);

		case START_DATE_DIALOG_ID_TO:
			return new DatePickerDialog(this, toDateListner, yearTo, monthTo, dayTo);
		}	
		return null;
	}

	/**
	 * date set listener for "From Date"
	 */
	private DatePickerDialog.OnDateSetListener fromDateListner = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) 
		{
			String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);

			if(!selectedDate.equalsIgnoreCase(fromDate))
			{

				if(CalendarUtils.getDiffBtwDatesInDays(selectedDate,toDate) >= 1)
				{
					yearFrom = yearSel;
					monthFrom = monthOfYear;
					dayFrom = dayOfMonth;


					fromDate = selectedDate;
					tvFromDate.setText(CalendarUtils.getFormatedDatefromString(fromDate));
					tvFromDate.setTag(fromDate);
					//		    			loadOrderList();
				}
				else
					showCustomDialog(PaymentSummaryReportActivity.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);

			}

		}
	};

	/**
	 * date set listener for "To Date"
	 */
	private DatePickerDialog.OnDateSetListener toDateListner = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) 
		{

			String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);

			if(!selectedDate.equalsIgnoreCase(toDate))
			{
				if(CalendarUtils.getDiffBtwDatesInDays(fromDate,selectedDate) >= 1)
				{
					yearTo = yearSel;
					monthTo = monthOfYear;
					dayTo = dayOfMonth;

					toDate = selectedDate;
					tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
					tvToDate.setTag(toDate);
					//		    			loadOrderList();
				}
				else
					showCustomDialog(PaymentSummaryReportActivity.this,"Alert",getString(R.string.to_date_should_not_be_lesser_than_from_date),getString(R.string.OK),null,null);
			}


		}
	};}
