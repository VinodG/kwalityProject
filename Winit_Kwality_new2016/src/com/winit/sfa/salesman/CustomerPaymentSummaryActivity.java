package com.winit.sfa.salesman;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.PaymentSummaryFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class CustomerPaymentSummaryActivity extends BaseActivity
{
	private Button btnFinish,btnPrint_Summarry;
	private TextView tvPreviewHead, tvPreviewHeadDate, tvTotalCollection,tvTotalCollectionTag,tvTotalCollectionCurrency;
	private LinearLayout llCustomerInvoice;
	private static final int DATE_DIALOG_ID = 0;
	private String strSelectedDateToPrint = "";
	private HashMap<String, String> hsTotal;

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private PaymentPagerAdapter paymentPagerAdapter;
	private String[] TITLES = {"Cheque", "Cash"};
	private HashMap<String,Customer_InvoiceDO> hmCashDetails = new HashMap<String,Customer_InvoiceDO>();
	private HashMap<String,Customer_InvoiceDO> hmChequeDetails = new HashMap<String,Customer_InvoiceDO>();
	private float totalCollection=0.0f;
	//for date filter
	private LinearLayout llDateFilterFrom,llDateFilterTo;
	private TextView tvFromDate,tvToDate,tvFromDateTag,tvToDateTag;
	private String fromDate,toDate;
	
	@Override
	public void initialize() 
	{
		llCustomerInvoice   = (LinearLayout)inflater.inflate(R.layout.activity_payment_summary, null);
		llBody.addView(llCustomerInvoice , LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		intialiseControls();
		
		Calendar c = Calendar.getInstance();
		monthFrom = c.get(Calendar.MONTH);
		yearFrom = c.get(Calendar.YEAR);
		dayFrom = c.get(Calendar.DAY_OF_MONTH) - 1;
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
		
		loadPayments();

		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});

		btnPrint_Summarry.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((hmCashDetails == null || hmCashDetails.isEmpty()) && (hmChequeDetails == null || hmChequeDetails.isEmpty()))
					showCustomDialog(CustomerPaymentSummaryActivity.this, "Alert!", "There is no summary to print.", "OK", "", "");
				else
				{
					Intent intent = new Intent(CustomerPaymentSummaryActivity.this, WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
					if((hmCashDetails != null && !hmCashDetails.isEmpty())	)
						intent.putExtra("hmCashDetails", hmCashDetails);
					if(hmChequeDetails != null && !hmChequeDetails.isEmpty())
						intent.putExtra("hmChequeDetails", hmChequeDetails);
					startActivity(intent);
					
				}
//					ShowOptionPopup();
			}
		});

		tvPreviewHeadDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		tvFromDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				llDateFilterFrom.performClick();
			}
		});
		tvToDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				llDateFilterTo.performClick();
			}
		});
		
		llDateFilterFrom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID_FROM);
			}
		});
		llDateFilterTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(START_DATE_DIALOG_ID_TO);
			}
		});
		
		setTypeFaceRobotoNormal(llCustomerInvoice);
		
		tvPreviewHead.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollectionTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollectionCurrency.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollection.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		btnPrint_Summarry.setTypeface(Typeface.DEFAULT_BOLD);
		btnFinish.setTypeface(Typeface.DEFAULT_BOLD);
		tvFromDateTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvToDateTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}

	
	private void intialiseControls()
	{
		btnFinish 			 = 	(Button) llCustomerInvoice.findViewById(R.id.btnFinish);
		btnPrint_Summarry 	 =	(Button) llCustomerInvoice.findViewById(R.id.btnPrint_Summarry);
		tvPreviewHead 		 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPreviewHead);
		tvPreviewHeadDate	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPreviewHeadDate);
		tvTotalCollection 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollection);

		tvTotalCollectionTag = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollectionTag);
		tvTotalCollectionCurrency 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollectionCurrency);
		tabs 				= (PagerSlidingTabStrip)llCustomerInvoice.findViewById(R.id.tabs);
		pager 				= (ViewPager) llCustomerInvoice.findViewById(R.id.pager);
		
		tvFromDate			=	(TextView) llCustomerInvoice.findViewById(R.id.tvFromDate);
		tvToDate			=	(TextView) llCustomerInvoice.findViewById(R.id.tvToDate);
		tvFromDateTag		=	(TextView) llCustomerInvoice.findViewById(R.id.tvFromDateTag);
		tvToDateTag			=	(TextView) llCustomerInvoice.findViewById(R.id.tvToDateTag);
		
		llDateFilterTo		=	(LinearLayout) llCustomerInvoice.findViewById(R.id.llDateFilterTo);
		llDateFilterFrom	=	(LinearLayout) llCustomerInvoice.findViewById(R.id.llDateFilterFrom);
		
//		paymentPagerAdapter = new PaymentPagerAdapter(getSupportFragmentManager());

		Calendar c 	= 	Calendar.getInstance();
		int year 	= 	c.get(Calendar.YEAR);
		int month  	= 	c.get(Calendar.MONTH);
		int day 	=	c.get(Calendar.DAY_OF_MONTH);

		strSelectedDateToPrint = (day < 10 ? "0"+day : day)+" "+CalendarUtils.getMonthFromNumber(month+1)+", "+year;
		tvPreviewHeadDate.setText(""+strSelectedDateToPrint);

		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	private void loadPayments()
	{
		showLoader("Loading data...");
		
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				final Object[] objects = new PaymentSummeryDA().getCustomerPaymentSummary(fromDate,toDate,mallsDetailss.site);
				hmCashDetails = (HashMap<String,Customer_InvoiceDO>) objects[0];
				hmChequeDetails = (HashMap<String,Customer_InvoiceDO>) objects[1];
				totalCollection= (Float) objects[2];
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(objects!=null && objects.length>0)
						{
							if(paymentPagerAdapter == null)
							{
								paymentPagerAdapter  = new PaymentPagerAdapter(getSupportFragmentManager());
								pager.setAdapter(paymentPagerAdapter);
								tabs.setViewPager(pager);
							}
							else
							{
								paymentPagerAdapter.refresh();
							}
						}
						tvTotalCollection.setText(decimalFormat.format(totalCollection));
						hideLoader();
					}
				});
			}
		}).start();
	}

	

	private void setTotalAmount()
	{
		float total = 0.0f;

		if(hsTotal != null && hsTotal.size() > 0)
		{
			Set<String> keys = hsTotal.keySet();
			for (String key : keys)
			{
				total = total+StringUtils.getFloat(hsTotal.get(key));
			}
		}
		tvTotalCollection.setText(""+total);
		tvTotalCollectionCurrency.setText(curencyCode);
	}

	private void printSummary(String type)
	{
//		if(arrayListCustomerInvoiceCheck != null && arrayListCustomerInvoiceCheck.size() > 0)
//		{
//			if(arrayListCustomerInvoiceCash != null && arrayListCustomerInvoiceCash.size() > 0)
//			{
//				arrayListCustomerInvoiceCheck.addAll(arrayListCustomerInvoiceCash);
//			}
//			Intent intent = new Intent(PaymentSummaryActivity.this, WoosimPrinterActivity.class);
//			intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
//			intent.putExtra("arrayList", arrayListCustomerInvoiceCheck);
//			intent.putExtra("strSelectedDateToPrint", strSelectedDateToPrint);
//			intent.putExtra("type", type);
//			startActivityForResult(intent, 1000);
//		}
//		else if(arrayListCustomerInvoiceCash != null && arrayListCustomerInvoiceCash.size() > 0)
//		{
//			Intent intent = new Intent(PaymentSummaryActivity.this, WoosimPrinterActivity.class);
//			intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
//			intent.putExtra("arrayList", arrayListCustomerInvoiceCash);
//			intent.putExtra("strSelectedDateToPrint", strSelectedDateToPrint);
//			intent.putExtra("type", type);
//			startActivityForResult(intent, 1000);
//		}
//		else
//			showCustomDialog(PaymentSummaryActivity.this, getResources().getString(R.string.warning), "There is no payment summary to print.", getResources().getString(R.string.OK), null, "");
	}

	protected void ShowOptionPopup()
	{

		View view = inflater.inflate(R.layout.custom_popup_language, null);
		final CustomDialog mCustomDialog = new CustomDialog(CustomerPaymentSummaryActivity.this, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);

		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		final Button btn_popup_English	  = (Button) view.findViewById(R.id.btn_popup_English);
		final Button btn_popup_Arabic	  = (Button) view.findViewById(R.id.btn_popup_Arabic);
		Button btn_popup_cancel		      = (Button) view.findViewById(R.id.btn_popup_cancel);

		setTypeFaceRobotoNormal((ViewGroup)view);
		tv_poptitle.setText("Select Print Type");
		btn_popup_English.setText("Accounts Copy");
		btn_popup_Arabic.setText("Collection Department Copy");

		btn_popup_English.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				printSummary(AppConstants.ACCOUNT_COPY);
				mCustomDialog.dismiss();
			}
		});

		btn_popup_Arabic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				printSummary(AppConstants.COLLECTION_COPY);
				mCustomDialog.dismiss();
			}
		});

		btn_popup_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
			}
		});

		try{
			if (!mCustomDialog.isShowing())
				mCustomDialog.show();
		}catch(Exception e){}
	}


	public class PaymentPagerAdapter extends FragmentStatePagerAdapter 
	{
		private HashMap<String,Customer_InvoiceDO> hmDetails;
		
		public PaymentPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}


		public void refresh() 
		{
			notifyDataSetChanged();
		}


		@Override
		public CharSequence getPageTitle(int position) 
		{
			return TITLES[position];
		}

		
		@Override
		public int getCount() 
		{
			return TITLES.length;
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position)
		{
			if(position == 0)
				hmDetails = hmChequeDetails;
			else if(position == 1)
				hmDetails = hmCashDetails;
				
			PaymentSummaryFragment paymentSummaryFragment = new PaymentSummaryFragment(hmDetails, CustomerPaymentSummaryActivity.this);
			return paymentSummaryFragment;
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
		    		
		    		if(CalendarUtils.getDiffBtwDatesInDays(selectedDate,toDate) >= 0)
		    		{
		    			yearFrom = yearSel;
		    	    	monthFrom = monthOfYear;
		    	    	dayFrom = dayOfMonth;
		    	    	
		    	    	
		    			fromDate = selectedDate;
		    			tvFromDate.setText(CalendarUtils.getFormatedDatefromString(fromDate));
		    			tvFromDate.setTag(fromDate);
		    			loadPayments();
		    		}
		    		else
		    			showCustomDialog(CustomerPaymentSummaryActivity.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);
		    		
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
		    		if(CalendarUtils.getDiffBtwDatesInDays(fromDate,selectedDate) >= 0)
		    		{
		    			yearTo = yearSel;
		    	    	monthTo = monthOfYear;
		    	    	dayTo = dayOfMonth;
		    	    	
		    			toDate = selectedDate;
		    			tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
		    			tvToDate.setTag(toDate);
		    			loadPayments();
		    		}
		    		else
		    			showCustomDialog(CustomerPaymentSummaryActivity.this,"Alert",getString(R.string.to_date_should_not_be_lesser_than_from_date),getString(R.string.OK),null,null);
		    	}
		    }
	    };
}
