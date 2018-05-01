package com.winit.sfa.salesman;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.PaymentSummaryFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class ChequeDetailsActivity extends BaseActivity
{
	private Button btnFinish,btnPrint_Summarry;
	private TextView tvPreviewHead, tvPreviewHeadDate, tvTotalCollection,tvTotalCollectionTag,tvTotalCollectionCurrency,tvPaymentDate;
	private LinearLayout llCustomerInvoice;
	private static final int DATE_DIALOG_ID = 0;
	private String strSelectedDateToPrint = "";
	private HashMap<String, String> hsTotal;

	private PaymentPagerAdapter paymentPagerAdapter;
	private String[] TITLES = {"Cheque", "Cash"};
	private HashMap<String,Customer_InvoiceDO> hmCashDetails = new HashMap<String,Customer_InvoiceDO>();
	private HashMap<String,Customer_InvoiceDO> hmChequeDetails = new HashMap<String,Customer_InvoiceDO>();
	private float totalCollection=0.0f;
	//for date filter
	private LinearLayout llDateFilterFrom,llDateFilterTo;
	private String fromDate,toDate;
	private LinearLayout llCheck;
	private TextView tvReceiptNOValue,tvCheckAmount,tvCustomerName,tvCashAmountHeader,tvCashAmount,tvTotalInvoiceAmount,tvBankNameNumber;
	
	@Override
	public void initialize() 
	{
		llCustomerInvoice   = (LinearLayout)inflater.inflate(R.layout.cheque_details_summary, null);
		llBody.addView(llCustomerInvoice , LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		intialiseControls();
		Customer_InvoiceDO objCustomer_InvoiceDO=(com.winit.alseer.salesman.dataobject.Customer_InvoiceDO) getIntent().getSerializableExtra("Customer_InvoiceDO");
		
		loaddata(objCustomer_InvoiceDO);
		Calendar c = Calendar.getInstance();
		
    	fromDate = objCustomer_InvoiceDO.chequeDate;
    	toDate = objCustomer_InvoiceDO.reciptDate;

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
					showCustomDialog(ChequeDetailsActivity.this, "Alert!", "There is no summary to print.", "OK", "", "");
				else
				{
					Intent intent = new Intent(ChequeDetailsActivity.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_SUMMARY);
					if((hmCashDetails != null && !hmCashDetails.isEmpty())	)
						intent.putExtra("hmCashDetails", hmCashDetails);
					if(hmChequeDetails != null && !hmChequeDetails.isEmpty())
						intent.putExtra("hmChequeDetails", hmChequeDetails);
					
					intent.putExtra("fromDate", fromDate);
					intent.putExtra("toDate", toDate);
					startActivity(intent);
				}
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
		
		setTypeFaceRobotoNormal(llCustomerInvoice);
		
		tvPreviewHead.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollectionTag.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollectionCurrency.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalCollection.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		btnPrint_Summarry.setTypeface(Typeface.DEFAULT_BOLD);
		btnFinish.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}

	
	private void loaddata(Customer_InvoiceDO customer_InvoiceDO) {
		tvReceiptNOValue.setText(""+customer_InvoiceDO.receiptNo);
		
		tvCustomerName.setText(customer_InvoiceDO.siteName+"["+customer_InvoiceDO.customerSiteId+"]");
		tvPaymentDate.setText(customer_InvoiceDO.reciptDate);
//		tvCashAmount.setText(this.decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.amount)));
		tvTotalInvoiceAmount.setText(this.decimalFormat.format(StringUtils.getFloat(customer_InvoiceDO.invoiceTotal)));
		tvBankNameNumber.setText(customer_InvoiceDO.bankName);
		float chequeAmount = 0.0f;
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
			tvCheckAmount.setText(this.decimalFormat.format(StringUtils.getFloat(objDetailDO.chequeAmount)));
			chequeAmount += StringUtils.getFloat(objDetailDO.chequeAmount);
			
			tvDate.setText(CalendarUtils.getDateToShow(objDetailDO.chequeDate));
			tvBankNameNumber.setText(objDetailDO.BankName);
			
			if(i == customer_InvoiceDO.vecChequeDetails.size()-1)
				ivDivCheckItem.setVisibility(View.GONE);
			
			llCheck.addView(llChequeDetailCell, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		}		
		tvCheckAmount.setText(decimalFormat.format(chequeAmount));
	}


	private void intialiseControls()
	{
		tvReceiptNOValue = (TextView) llCustomerInvoice.findViewById(R.id.tvReceiptNOValue);
		
		tvCustomerName 		  = (TextView) llCustomerInvoice.findViewById(R.id.tvCustomerName);
		
		tvCashAmountHeader     = (TextView) llCustomerInvoice.findViewById(R.id.tvCashAmountHeader);
//		tvCashAmount 		   = (TextView) llCustomerInvoice.findViewById(R.id.tvCashAmount);
		tvTotalInvoiceAmount    = (TextView) llCustomerInvoice.findViewById(R.id.tvTotalInvoiceAmount);
		tvBankNameNumber = (TextView) llCustomerInvoice.findViewById(R.id.tvBankNameNumber );
		llCheck				   = (LinearLayout) llCustomerInvoice.findViewById(R.id.llCheck);
		
		btnFinish 			 = 	(Button) llCustomerInvoice.findViewById(R.id.btnFinish);
		btnPrint_Summarry 	 =	(Button) llCustomerInvoice.findViewById(R.id.btnPrint_Summarry);
		tvPreviewHead 		 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPreviewHead);
		tvPreviewHeadDate	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPreviewHeadDate);
		tvTotalCollection 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollection);
		tvPaymentDate	 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvPaymentDate);
		tvCheckAmount	 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvCheckAmount);

		tvTotalCollectionTag = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollectionTag);
		tvTotalCollectionCurrency 	 = 	(TextView) llCustomerInvoice.findViewById(R.id.tvTotalCollectionCurrency);
		
		llDateFilterTo		=	(LinearLayout) llCustomerInvoice.findViewById(R.id.llDateFilterTo);
		llDateFilterFrom	=	(LinearLayout) llCustomerInvoice.findViewById(R.id.llDateFilterFrom);
		
		Calendar c 	= 	Calendar.getInstance();
		int year 	= 	c.get(Calendar.YEAR);
		int month  	= 	c.get(Calendar.MONTH);
		int day 	=	c.get(Calendar.DAY_OF_MONTH);

		strSelectedDateToPrint = (day < 10 ? "0"+day : day)+" "+CalendarUtils.getMonthFromNumber(month+1)+", "+year;
		tvPreviewHeadDate.setText(""+strSelectedDateToPrint);
		tvPreviewHead.setText("Cheque Details");
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvPaymentDate.setTypeface(AppConstants.Roboto_Condensed_Bold);
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
		final CustomDialog mCustomDialog = new CustomDialog(ChequeDetailsActivity.this, view, preference .getIntFromPreference("DEVICE_DISPLAY_WIDTH", 320) - 40,
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
				
			PaymentSummaryFragment paymentSummaryFragment = new PaymentSummaryFragment(hmDetails, ChequeDetailsActivity.this);
			return paymentSummaryFragment;
		}
	}
}
