package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.alseer.salesman.dataobject.CustomerStatmentDO;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class CustomerStatementActivity extends BaseActivity 
{
	private LinearLayout llSummaryofDay;
	private ListView lvSalesOrder;
	private TextView tvPageTitle,tvCustomerName,tvCustomerLocation,
	tvNoOrderFound,tvFromDate,tvToDate, tvAmountTag;
	private CustomerStatementAdapter customerStatementAdapter;
	private Vector<CustomerStatmentDO> vecDetails;
	private String fromDate,toDate;
	private Button btnSubmit;
	
	private String finalFromDate,finalToDate,tempDate;
	private LinearLayout llFrom,llTo;
	
	@Override
	public void initialize() 
	{
		llSummaryofDay 	= (LinearLayout) inflater.inflate(R.layout.customer_statement, null);
		llBody.addView(llSummaryofDay,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intializeControls();
		loadCustomerStatement(fromDate,toDate);
		
		setTypeFaceRobotoNormal(llSummaryofDay);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(vecDetails!=null && vecDetails.size()>0)
				{
				   Intent intent = new Intent(CustomerStatementActivity.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_CUSTOMER_STATEMENT);
					intent.putExtra("mallsDetails", mallsDetailss);
					ArrayList<CustomerStatmentDO> arrDetails =  new ArrayList<CustomerStatmentDO>();
					arrDetails.addAll(vecDetails);
					intent.putExtra("vecDetails", arrDetails);
					intent.putExtra("fromDate", fromDate);
					intent.putExtra("toDate", toDate);
					startActivity(intent);
				}
			}
		});
		tvToDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_TO);
			}
		});
		
		tvFromDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_FROM);
			}
		});
	}
	
	public void intializeControls()
	{
		tvPageTitle			=	(TextView)llSummaryofDay.findViewById(R.id.tvHeader);
		
		tvNoOrderFound		=   (TextView)llSummaryofDay.findViewById(R.id.tvNoOrderFound);
		tvCustomerName		=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerName);
		tvCustomerLocation	=   (TextView)llSummaryofDay.findViewById(R.id.tvCustomerLocation);
		
		lvSalesOrder		=   (ListView)llSummaryofDay.findViewById(R.id.lvOrderList);
		
		tvAmountTag 	 	= (TextView)llSummaryofDay.findViewById(R.id.tvAmountTag);
		
		tvFromDate			=   (TextView)llSummaryofDay.findViewById(R.id.tvFromDate);
		tvToDate			=   (TextView)llSummaryofDay.findViewById(R.id.tvToDate);
		btnSubmit			=   (Button)llSummaryofDay.findViewById(R.id.btnSubmit);
		llFrom=(LinearLayout) llSummaryofDay.findViewById(R.id.llFrom);
		llTo=(LinearLayout) llSummaryofDay.findViewById(R.id.llTo);
		btnSubmit.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		vecDetails = new Vector<CustomerStatmentDO>();
		customerStatementAdapter = new CustomerStatementAdapter(vecDetails);
		
		lvSalesOrder.setCacheColorHint(0);
		lvSalesOrder.setVerticalScrollBarEnabled(false);
		lvSalesOrder.setDivider(getResources().getDrawable(R.drawable.saparetor));
		lvSalesOrder.setFadingEdgeLength(0);
		lvSalesOrder.setAdapter(customerStatementAdapter);

		if(mallsDetailss!=null)
		{
			tvCustomerName.setText(mallsDetailss.siteName+" ["+mallsDetailss.site+"]");
			tvCustomerLocation.setText(mallsDetailss.addresss1);
		}
		
		tvPageTitle.setText("Customer Statement");
		
		Calendar c = Calendar.getInstance();
		monthFrom = c.get(Calendar.MONTH);
		yearFrom = c.get(Calendar.YEAR);
		dayFrom = c.get(Calendar.DAY_OF_MONTH)-1;
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		
		String selectedDate = CalendarUtils.getPreviousDate();
		
//		fromDate = selectedDate;
//		tvFromDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
//    	tvFromDate.setTag(selectedDate);
    	
    	selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
    	
    	fromDate = selectedDate;
		tvFromDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
    	tvFromDate.setTag(selectedDate);
    	
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
		llFrom.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				tvFromDate.performClick();	
			}
		});
		llTo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				tvToDate.performClick();	
			}
		});
	}
	
	private void validateDateFields(String fromDate, String toDate)
	{
		Calendar calFrom = CalendarUtils.getCalender(fromDate);
		Calendar calTo = CalendarUtils.getCalender(toDate);
		
		if(CalendarUtils.getDiffBtwDatesInDays(fromDate,toDate)<2)
		{
			finalFromDate=fromDate;
			calTo.add(Calendar.DAY_OF_MONTH, 1);
			finalToDate = CalendarUtils.getDateForCustomerStatement(calTo);
			
		}
		else
		{
			finalFromDate=fromDate;
			calTo.add(Calendar.DAY_OF_MONTH, 1);
			finalToDate = CalendarUtils.getDateForCustomerStatement(calTo);
			
			calTo.add(Calendar.DAY_OF_MONTH, -3);
			tempDate=CalendarUtils.getDateForCustomerStatement(calTo);
		}
		
	}
	
	private void loadCustomerStatement(final String fromDate, final String toDate)
	{
		
		showLoader("");
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
			
				boolean isLessthan=false;
				Calendar calTo = CalendarUtils.getCalender(toDate);
				
				if(CalendarUtils.getDiffBtwDatesInDays(fromDate,toDate)<2)
				{
					isLessthan=true;
					finalFromDate=fromDate;
					calTo.add(Calendar.DATE, 1);
					finalToDate = CalendarUtils.getDateForCustomerStatement(calTo);
					
				}
				else
				{
					isLessthan=false;
					finalFromDate=fromDate;
					calTo.add(Calendar.DATE, 1);
					finalToDate = CalendarUtils.getDateForCustomerStatement(calTo);
					
					calTo.add(Calendar.DATE, -3);
					tempDate=CalendarUtils.getDateForCustomerStatement(calTo);
				}
				
				
				vecDetails = new OrderDA().getCustomerStatement(finalFromDate, finalToDate,tempDate,mallsDetailss.site,isLessthan);
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						hideLoader();
						tvAmountTag.setText("Debit");
						if(vecDetails == null || vecDetails.size() == 0)
						{
							tvNoOrderFound.setVisibility(View.VISIBLE);
							lvSalesOrder.setVisibility(View.GONE);
						}
						else
						{
							tvNoOrderFound.setVisibility(View.GONE);
							lvSalesOrder.setVisibility(View.VISIBLE);
							if(customerStatementAdapter!=null)
							{
								customerStatementAdapter.refresh(vecDetails);
							}
							else
							{
								customerStatementAdapter = new CustomerStatementAdapter(vecDetails);
								lvSalesOrder.setAdapter(customerStatementAdapter);
							}
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
	
	public class CustomerStatementAdapter extends BaseAdapter
	{
		Vector<CustomerStatmentDO> vecCustomerStmtDO;
		Customer_InvoiceDO customer_InvoiceDO;
		public CustomerStatementAdapter(Vector<CustomerStatmentDO> vecDetails)
		{
			this.vecCustomerStmtDO=vecDetails;
		}
		@Override
		public int getCount() 
		{
			if(vecCustomerStmtDO != null && vecCustomerStmtDO.size() > 0)
				return vecCustomerStmtDO.size();
			else
			return 0;
		}
		public void refresh(Vector<CustomerStatmentDO> vecDetails)
		{
			this.vecCustomerStmtDO=vecDetails;
			notifyDataSetChanged();
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
			final CustomerStatmentDO customerStatmentDO = vecCustomerStmtDO.get(position);
			final ViewHolder viewHolder;
			String date="";
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.customer_statement_cell, null);
				
				viewHolder.tvSlNo 	 				 = (TextView)convertView.findViewById(R.id.tvSlNo);
				viewHolder.tvTrxType 	 			 = (TextView)convertView.findViewById(R.id.tvTrxType);
				viewHolder.tvTrxNo 				 	 = (TextView)convertView.findViewById(R.id.tvTrxNo);
				viewHolder.tvTrxDate 				 = (TextView)convertView.findViewById(R.id.tvTrxDate);
				viewHolder.tvDebitAmount 	 		 = (TextView)convertView.findViewById(R.id.tvDebitAmount);
				viewHolder.tvCreditAmount 	 		 = (TextView)convertView.findViewById(R.id.tvCreditAmount);

				convertView.setTag(viewHolder);
			}else
				viewHolder	= (ViewHolder) convertView.getTag();
			
			if(!customerStatmentDO.trxDate.equalsIgnoreCase("") && customerStatmentDO.trxDate.contains("T"))
			{
				String[] dt = customerStatmentDO.trxDate.split("T");
				date=dt[0];
			}
			else
			{
				date = customerStatmentDO.trxDate;
			}
				
			viewHolder.tvSlNo.setText(""+(position+1));
			viewHolder.tvTrxType.setText(customerStatmentDO.invoiceType);
			viewHolder.tvTrxNo.setText(customerStatmentDO.trxNumber);
			viewHolder.tvTrxDate.setText(date);
			if(customerStatmentDO.invoiceType.equalsIgnoreCase("Payment") 
					|| customerStatmentDO.invoiceType.equalsIgnoreCase("GRV"))
			{
				viewHolder.tvCreditAmount.setText(decimalFormat.format(StringUtils.getFloat(customerStatmentDO.amount)));
				viewHolder.tvDebitAmount.setText(decimalFormat.format(0.0));
			}
			else
			{
				viewHolder.tvCreditAmount.setText(decimalFormat.format(0.0));
				viewHolder.tvDebitAmount.setText(decimalFormat.format(StringUtils.getFloat(customerStatmentDO.amount)));
			}
			
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
							Calendar c = Calendar.getInstance();
							monthFrom = c.get(Calendar.MONTH);
							yearFrom = c.get(Calendar.YEAR);
							dayFrom = c.get(Calendar.DAY_OF_MONTH) - 1;
							monthTo = c.get(Calendar.MONTH);
							yearTo = c.get(Calendar.YEAR);
							dayTo = c.get(Calendar.DAY_OF_MONTH);
							String fromDate = CalendarUtils.getOrderSummaryDate(yearFrom,monthFrom,dayFrom);
							String toDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
							
							if(customerStatmentDO.invoiceType.equalsIgnoreCase("Sales Order"))
							{
							}
							else 
								customer_InvoiceDO = new PaymentSummeryDA().getCustomerPaymentSummaryByReceiptID(fromDate, toDate, mallsDetailss.site, customerStatmentDO.trxNumber);
							
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									showDetails(customer_InvoiceDO);
								}
							});
						}
					}).start();
				}
			});
			viewHolder.tvCreditAmount.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvDebitAmount.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvSlNo.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvTrxDate.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvTrxNo.setTypeface(AppConstants.Roboto_Condensed);
			viewHolder.tvTrxType.setTypeface(AppConstants.Roboto_Condensed);
			return convertView;
		}
		
		Dialog dialogDetails;
		private void showDetails(Customer_InvoiceDO customer_InvoiceDO)
		{
			if(customer_InvoiceDO != null)
			{
				// Details
				LinearLayout viewDetails = (LinearLayout)inflater.inflate(R.layout.payment_summary_list_item_cell, null);
				
				TextView tvReceiptNO 			= (TextView) viewDetails.findViewById(R.id.tvReceiptNO);
				TextView tvReceiptNOValue 		= (TextView) viewDetails.findViewById(R.id.tvReceiptNOValue);
				
				TextView tvCustomerName 		= (TextView) viewDetails.findViewById(R.id.tvCustomerName);
				
				TextView tvCashAmountHeader     = (TextView) viewDetails.findViewById(R.id.tvCashAmountHeader);
				TextView tvCashAmount 		    = (TextView) viewDetails.findViewById(R.id.tvCashAmount);
				TextView tvTotalInvoiceAmount    = (TextView) viewDetails.findViewById(R.id.tvTotalInvoiceAmount);
				TextView tvTotalInvoiceAmountTag = (TextView) viewDetails.findViewById(R.id.tvTotalInvoiceAmountTag);
				LinearLayout llInvoiceDetailsSale   = (LinearLayout) viewDetails.findViewById(R.id.llInvoiceDetailsSale);
				LinearLayout llInvoiceDetailsReturn = (LinearLayout) viewDetails.findViewById(R.id.llInvoiceDetailsReturn);
				LinearLayout llCheck				   = (LinearLayout) viewDetails.findViewById(R.id.llCheck);
				LinearLayout llCash				   = (LinearLayout) viewDetails.findViewById(R.id.llCash);
				TextView tvPaymentDate		    = (TextView) viewDetails.findViewById(R.id.tvPaymentDate);
				ImageView ivLineCheckTopBar		= (ImageView) viewDetails.findViewById(R.id.ivLineCheckTopBar);
				ImageView ivPrintReceipt		= (ImageView) viewDetails.findViewById(R.id.ivPrintReceipt);
				
				if(customer_InvoiceDO.paymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				{
					llCheck.setVisibility(View.GONE);
					llCash.setVisibility(View.VISIBLE);
					ivLineCheckTopBar.setVisibility(View.GONE);
				}
				else
				{
					if(llCheck.getChildCount() > 0)
						llCheck.removeAllViews();
					llCheck.setVisibility(View.VISIBLE);
					llCash.setVisibility(View.GONE);
					ivLineCheckTopBar.setVisibility(View.VISIBLE);
					
					for(int i=0; customer_InvoiceDO.vecChequeDetails != null &&  i<customer_InvoiceDO.vecChequeDetails.size(); i++ )
					{
						NameIDDo objDetailDO 	    		= 	customer_InvoiceDO.vecChequeDetails.get(i);
						LinearLayout llChequeDetailCell 	= 	(LinearLayout) inflater.inflate(R.layout.cheque_details_cell, null);
						TextView tvCheckNumber 				= 	(TextView) llChequeDetailCell.findViewById(R.id.tvCheckNumber);
						TextView tvCheckAmount 				= 	(TextView) llChequeDetailCell.findViewById(R.id.tvCheckAmount);
						TextView tvDate 					= 	(TextView) llChequeDetailCell.findViewById(R.id.tvDate);
						TextView tvBankNameNumber 			= 	(TextView) llChequeDetailCell.findViewById(R.id.tvBankNameNumber);
						
						ImageView ivDivCheckItem			= 	(ImageView) llChequeDetailCell.findViewById(R.id.ivDivCheckItem);
						
						tvCheckNumber.setText(objDetailDO.chequeNumber);
						tvCheckAmount.setText((CustomerStatementActivity.this).decimalFormat.format(StringUtils.getFloat(objDetailDO.chequeAmount)));
						
						tvDate.setText(CalendarUtils.getDateToShow(customer_InvoiceDO.chequeDate));
						tvBankNameNumber.setText(customer_InvoiceDO.bankName);
						
						if(i == customer_InvoiceDO.vecChequeDetails.size()-1)
							ivDivCheckItem.setVisibility(View.GONE);
						
						llCheck.addView(llChequeDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
					}
				}
				tvPaymentDate.setText(CalendarUtils.getDateToShow(customer_InvoiceDO.reciptDate));
				(CustomerStatementActivity.this).setTypeFaceRobotoNormal((ViewGroup)viewDetails);
				tvCashAmountHeader.setTypeface(AppConstants.Roboto_Condensed);
				tvReceiptNOValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
				
				tvReceiptNO.setText("Receipt No: ");
				tvReceiptNOValue.setText(""+customer_InvoiceDO.receiptNo);
				
				tvCustomerName.setText(customer_InvoiceDO.siteName+"["+customer_InvoiceDO.customerSiteId+"]");
				
				tvCashAmount.setText((CustomerStatementActivity.this).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount)));
				tvTotalInvoiceAmount.setText((CustomerStatementActivity.this).decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.invoiceTotal)));
				
				llInvoiceDetailsSale.removeAllViews();
				for(int i=0; customer_InvoiceDO.vecPaymentDetailDOs != null &&  i<customer_InvoiceDO.vecPaymentDetailDOs.size(); i++ )
				{
					PaymentDetailDO objDetailDO 	    = 	customer_InvoiceDO.vecPaymentDetailDOs.get(i);
					LinearLayout llPaymentDetailCell 	= 	(LinearLayout) inflater.inflate(R.layout.payment_details_cell, null);
					TextView tvInvoiceNumber 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceNumber);
					TextView tvInvoiceAmmount 			= 	(TextView) llPaymentDetailCell.findViewById(R.id.tvInvoiceAmmount);
					
					ImageView ivDivChild = (ImageView)llPaymentDetailCell.findViewById(R.id.ivDivChild);
					tvInvoiceNumber.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvInvoiceAmmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
					
					if(i == customer_InvoiceDO.vecPaymentDetailDOs.size()-1)
						ivDivChild.setVisibility(View.GONE);
					
					tvInvoiceNumber.setText(objDetailDO.invoiceNumber);
					float amt = StringUtils.getFloat((CustomerStatementActivity.this).decimalFormat.format(StringUtils.getFloat(objDetailDO.invoiceAmount)));
					tvInvoiceAmmount.setText(""+amt);
					llInvoiceDetailsSale.addView(llPaymentDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
				}
				
				dialogDetails = new Dialog(CustomerStatementActivity.this, android.R.style.Theme_Holo_Dialog);
				dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialogDetails.addContentView(viewDetails, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
				dialogDetails.show();
				
				(CustomerStatementActivity.this).setTypeFaceRobotoNormal(viewDetails);
			}
		}
		
		public class ViewHolder{
			TextView tvSlNo;
			TextView tvTrxType;
			TextView tvTrxNo;
			TextView tvTrxDate;
			TextView tvDebitAmount;
			TextView tvCreditAmount;
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
					loadCustomerStatement(fromDate,toDate);
				}
				else
					showCustomDialog(CustomerStatementActivity.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);

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
					loadCustomerStatement(fromDate,toDate);
				}
				else
					showCustomDialog(CustomerStatementActivity.this,"Alert",getString(R.string.to_date_should_not_be_lesser_than_from_date),getString(R.string.OK),null,null);
			}
		}
	};
}
