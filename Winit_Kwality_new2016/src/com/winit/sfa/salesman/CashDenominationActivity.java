package com.winit.sfa.salesman;

import java.util.Calendar;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CashDenominationDA;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.Denominations;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStripNew;
import com.winit.kwalitysfa.salesman.R;

public class
CashDenominationActivity extends BaseActivity
{

	private LinearLayout llCashDenomination;
	private RelativeLayout rlFrom;
	private TextView tvFromDate,tvRouteNo,tvHeadTitle;
	private EditText edtSalesMan,edtHelper;
	private Button btnSubmit;
	private int monthTo, yearTo, dayTo;
	private CashDenomDO objCashDenom = null;
	private PagerSlidingTabStripNew tabs;
	private ViewPager pager;
	private String[] tabNames = {"Ice Cream","Food"};
	boolean isAlreadyDone = false;
	private CashDenomAdapter adapter;
	
	@Override
	public void initialize() 
	{
		llCashDenomination = (LinearLayout) inflater.inflate(R.layout.cash_denomination, null);
		llBody.addView(llCashDenomination,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		Calendar c = Calendar.getInstance();
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		String selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
		
		tvFromDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
    	tvFromDate.setTag(selectedDate);
    	
		bindControls();
		
		loadData();
	}
	

	private void loadData() 
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				objCashDenom = new CashDenominationDA().getCashDenom(tvFromDate.getText().toString());
				final String routeNo = new CashDenominationDA().getRouteNo(preference.getStringFromPreference(preference.USER_ID, ""));
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						tvRouteNo.setText(routeNo);
						if(objCashDenom != null)
						{
							btnSubmit.setText("Print");
							isAlreadyDone = true;
							
							edtSalesMan.setText(""+objCashDenom.UserCode);
							edtHelper.setText(""+objCashDenom.HelperUserCode);

							edtSalesMan.setClickable(false);
							edtSalesMan.setEnabled(false);
							edtSalesMan.setFocusable(false);
							edtHelper.setClickable(false);
							edtHelper.setEnabled(false);
							edtHelper.setFocusable(false);
						}
						else
						{
							objCashDenom = new CashDenomDO();
						}
						adapter  = new CashDenomAdapter();
						pager.setAdapter(adapter);
						tabs.setViewPager(pager);
					}
				});
			}
		}).start();
	}
	
	
	OnFocusChangeListener focusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(final View v, boolean hasFocus) {

			if (hasFocus) 
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							hideKeyBoard(v);
							onKeyboardFocus(v, 0, true);	
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 10);
			}
		}
	};
	

	private void bindControls() 
	{
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(!btnSubmit.getText().toString().equalsIgnoreCase("Print"))
				{
					if(edtSalesMan.getText().toString().equalsIgnoreCase("") && edtHelper.getText().toString().equalsIgnoreCase(""))
					{
						showCustomDialog(CashDenominationActivity.this,"Alert","Please enter salesman name.",getString(R.string.OK),null,"");
					}
					else
					{
						objCashDenom.UserCode = edtSalesMan.getText().toString();
						objCashDenom.CollectionDate = tvFromDate.getText().toString();
						objCashDenom.HelperUserCode =  edtHelper.getText().toString();
						objCashDenom.RouteNo = tvRouteNo.getText().toString();
						
						//save in DB
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								new CashDenominationDA().insertDenominations(objCashDenom);
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										uploadData();
										showDenominationCompletePopup();
										loadData();
									}
								});
							}
						}).start();
					}
				}
				else
				{
					Intent intent = new Intent(CashDenominationActivity.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_CASH_DENOMINATION);
					intent.putExtra("CashDenom", objCashDenom);
					startActivity(intent);
				}
			}
		});
	}
	
	private void calculateTotalCash(double total, EditText edtVoucher, EditText edtCashPaidCredit, TextView tvGrandTotal, int position)
	{
		Denominations objDenom;
		if(position > 0) {
			objDenom =  objCashDenom.objFoodDenom;
		} else {
			objDenom =  objCashDenom.objIceCreamDenom;
		}
		
		total += StringUtils.getDouble(edtVoucher.getText().toString());
		total += StringUtils.getDouble(edtCashPaidCredit.getText().toString());
		
		tvGrandTotal.setText(total+"");
		objDenom.GrandTotal = total;
	}
	
	private void calculateGrandTotal(EditText edtOtherCurrency, TextView tvTotalCash, EditText edtVoucher, EditText edtCashPaidCredit, TextView tvGrandTotal, int position)
	{
		Denominations objDenom;
		if(position > 0) {
			objDenom =  objCashDenom.objFoodDenom;
		} else {
			objDenom =  objCashDenom.objIceCreamDenom;
		}
		
		double total = 0;
		total += objDenom.Units1000*1000;
		total += objDenom.Units500*500;
		total += objDenom.Units200*200;
		total += objDenom.Units100*100;
		total += objDenom.Units50*50;
		total += objDenom.Units20*20;
		total += objDenom.Units10*10;
		total += objDenom.Units5*5;
		total += objDenom.UnitsCoin*1;
		total += StringUtils.getDouble(edtOtherCurrency.getText().toString());
		
		tvTotalCash.setText(total+"");
		objDenom.TotalAmount = total;
		
		calculateTotalCash(total,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
	}
	
	private void showDenominationCompletePopup() 
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(CashDenominationActivity.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 350) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);
		
		TextView tv_poptitle	 	      = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1			  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		Button btn_popup_print		 	  = (Button) view.findViewById(R.id.btn_popup_print);
		Button btn_popup_collectpayment	  = (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_returnreq		  = (Button) view.findViewById(R.id.btn_popup_returnreq);
		Button btn_popup_task      		  = (Button) view.findViewById(R.id.btn_popup_task);
		Button btn_popup_done			  = (Button) view.findViewById(R.id.btn_popup_done);
		Button btn_popup_survey     	  = (Button) view.findViewById(R.id.btn_popup_survey);
		Button btnPlaceNewOrder     	  = (Button) view.findViewById(R.id.btnPlaceNewOrder);
		
		btn_popup_returnreq.setVisibility(View.GONE);
		btn_popup_task.setVisibility(View.GONE);
		btn_popup_survey.setVisibility(View.GONE);
		btnPlaceNewOrder.setVisibility(View.GONE);
		btn_popup_collectpayment.setVisibility(View.GONE);
		
		if(AppConstants.isTaskCompleted)
			btn_popup_task.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.taks_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tv_poptitle.setText("Cash Denomination Submitted");
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				Intent intent = new Intent(CashDenominationActivity.this,WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_CASH_DENOMINATION);
				intent.putExtra("CashDenom", objCashDenom);
				startActivity(intent);
			}
		});
		
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				finish();
			}
		});
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}

	private void initializeControls() 
	{
		rlFrom				=	(RelativeLayout) llCashDenomination.findViewById(R.id.rlFrom);
		
		tvFromDate			=	(TextView) llCashDenomination.findViewById(R.id.tvFromDate);
		tvRouteNo			=	(TextView) llCashDenomination.findViewById(R.id.tvRouteNo);
		tvHeadTitle			=	(TextView) llCashDenomination.findViewById(R.id.tvHeadTitle);	
		
		edtSalesMan			=	(EditText) llCashDenomination.findViewById(R.id.edtSalesMan);
		edtHelper			=	(EditText) llCashDenomination.findViewById(R.id.edtHelper);
		
		btnSubmit			=	(Button) llCashDenomination.findViewById(R.id.btnSubmit);
		
		tabs 	 			= 	(PagerSlidingTabStripNew) llCashDenomination.findViewById(R.id.tabs);
		pager 				= 	(ViewPager) llCashDenomination.findViewById(R.id.pager);
		
		setTypeFaceRobotoNormal(llCashDenomination);
		tvHeadTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}

	private class CashDenomAdapter extends PagerAdapter {

		public void refresh() 
		{
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			
			if(tabNames == null || tabNames.length <= 0)
				return 0;
			return tabNames.length;
		}
		
		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(tabNames != null && tabNames.length > 0)
				return tabNames[position];
			
			return "N/A";
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			
			View convertView = LayoutInflater.from(CashDenominationActivity.this).inflate(R.layout.cash_denom, null);
			
			final TextView tvUnits1000			=	(TextView) convertView.findViewById(R.id.tvUnits1000);
			final TextView tvUnits500			=	(TextView) convertView.findViewById(R.id.tvUnits500);
			final TextView tvUnits200			=	(TextView) convertView.findViewById(R.id.tvUnits200);
			final TextView tvUnits100			=	(TextView) convertView.findViewById(R.id.tvUnits100);
			final TextView tvUnits50			=	(TextView) convertView.findViewById(R.id.tvUnits50);
			final TextView tvUnits20			=	(TextView) convertView.findViewById(R.id.tvUnits20);
			final TextView tvUnits10			=	(TextView) convertView.findViewById(R.id.tvUnits10);
			final TextView tvUnits5				=	(TextView) convertView.findViewById(R.id.tvUnits5);
			final TextView tvUnitsCoin			=	(TextView) convertView.findViewById(R.id.tvUnitsCoin);
			final TextView tvTotalCash			=	(TextView) convertView.findViewById(R.id.tvTotalCash);
			final TextView tvGrandTotal			=	(TextView) convertView.findViewById(R.id.tvGrandTotal);
			
			final EditText edtVoucher			=	(EditText) convertView.findViewById(R.id.edtVoucher);
			final EditText edtCashPaidCredit	=	(EditText) convertView.findViewById(R.id.edtCashPaidCredit);
			final EditText edtOtherCurrency	=	(EditText) convertView.findViewById(R.id.edtOtherCurrency);
			final EditText evUnitsCoin		=	(EditText) convertView.findViewById(R.id.evUnitsCoin);
			final EditText edtOtherCurrQty	=	(EditText) convertView.findViewById(R.id.edtOtherCurrQty);
			
			final CustomEditText evUnits1000		=	(CustomEditText) convertView.findViewById(R.id.evUnits1000);
			final CustomEditText evUnits500			=	(CustomEditText) convertView.findViewById(R.id.evUnits500);
			final CustomEditText evUnits200			=	(CustomEditText) convertView.findViewById(R.id.evUnits200);
			final CustomEditText evUnits100			=	(CustomEditText) convertView.findViewById(R.id.evUnits100);
			final CustomEditText evUnits50			=	(CustomEditText) convertView.findViewById(R.id.evUnits50);
			final CustomEditText evUnits20			=	(CustomEditText) convertView.findViewById(R.id.evUnits20);
			final CustomEditText evUnits10			=	(CustomEditText) convertView.findViewById(R.id.evUnits10);
			final CustomEditText evUnits5			=	(CustomEditText) convertView.findViewById(R.id.evUnits5);
			final CustomEditText evVoucher			=	(CustomEditText) convertView.findViewById(R.id.evVoucher);
			final CustomEditText evcashPaidCrNote	=	(CustomEditText) convertView.findViewById(R.id.evcashPaidCrNote);
			
			Denominations obj;
			if(position > 0)
				obj = (Denominations) objCashDenom.objFoodDenom;
			else
				obj = (Denominations) objCashDenom.objIceCreamDenom;
			final Denominations objdenomination = obj;
			
			if(isAlreadyDone) {
				
				evUnits1000.setText(""+objdenomination.Units1000);
				tvUnits1000.setText(objdenomination.Units1000*1000+"");
				
				evUnits500.setText(""+objdenomination.Units500);
				tvUnits500.setText(objdenomination.Units500*500+"");
				
				evUnits200.setText(""+objdenomination.Units200);
				tvUnits200.setText(objdenomination.Units200*200+"");
				
				evUnits100.setText(""+objdenomination.Units100);
				tvUnits100.setText(objdenomination.Units100*100+"");
				
				evUnits50.setText(""+objdenomination.Units50);
				tvUnits50.setText(objdenomination.Units50*50+"");
				
				evUnits20.setText(""+objdenomination.Units20);
				tvUnits20.setText(objdenomination.Units20*20+"");
				
				evUnits10.setText(""+objdenomination.Units10);
				tvUnits10.setText(objdenomination.Units10*10+"");
				
				evUnits5.setText(""+objdenomination.Units5);
				tvUnits5.setText(objdenomination.Units5*5+"");
				
				evUnitsCoin.setText(""+objdenomination.UnitsCoin);
				tvUnitsCoin.setText(objdenomination.UnitsCoin*1+"");
				
				edtOtherCurrQty.setText(""+objdenomination.OtherCurrencyNote);
				edtOtherCurrency.setText(""+objdenomination.OtherCurrency);
				
				evVoucher.setText(""+objdenomination.VoucherNo);
				edtVoucher.setText(""+objdenomination.Voucher);
				evcashPaidCrNote.setText(""+objdenomination.CashPaidCrNote);
				edtCashPaidCredit.setText(""+objdenomination.CashPaidCredit);
				
				calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				
				evUnits1000.setClickable(false);
				evUnits1000.setEnabled(false);
				evUnits1000.setFocusable(false);
				evUnits500.setClickable(false);
				evUnits500.setEnabled(false);
				evUnits500.setFocusable(false);
				evUnits200.setClickable(false);
				evUnits200.setEnabled(false);
				evUnits200.setFocusable(false);
				evUnits100.setClickable(false);
				evUnits100.setEnabled(false);
				evUnits100.setFocusable(false);
				evUnits50.setClickable(false);
				evUnits50.setEnabled(false);
				evUnits50.setFocusable(false);
				evUnits20.setClickable(false);
				evUnits20.setEnabled(false);
				evUnits20.setFocusable(false);
				evUnits10.setClickable(false);
				evUnits10.setEnabled(false);
				evUnits10.setFocusable(false);
				evUnits5.setClickable(false);
				evUnits5.setEnabled(false);
				evUnits5.setFocusable(false);
				evUnitsCoin.setClickable(false);
				evUnitsCoin.setEnabled(false);
				evUnitsCoin.setFocusable(false);
				edtOtherCurrQty.setClickable(false);
				edtOtherCurrQty.setEnabled(false);
				edtOtherCurrQty.setFocusable(false);
				edtOtherCurrency.setClickable(false);
				edtOtherCurrency.setEnabled(false);
				edtOtherCurrency.setFocusable(false);
				evVoucher.setClickable(false);
				evVoucher.setEnabled(false);
				evVoucher.setFocusable(false);
				edtVoucher.setClickable(false);
				edtVoucher.setEnabled(false);
				edtVoucher.setFocusable(false);
				evcashPaidCrNote.setClickable(false);
				evcashPaidCrNote.setEnabled(false);
				evcashPaidCrNote.setFocusable(false);
				edtCashPaidCredit.setClickable(false);
				edtCashPaidCredit.setEnabled(false);
				edtCashPaidCredit.setFocusable(false);
			}
			
			
			evUnits1000.setOnFocusChangeListener(focusListener);
			evUnits1000.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units1000 = StringUtils.getInt(evUnits1000.getText().toString());
					tvUnits1000.setText(objdenomination.Units1000*1000+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits500.setOnFocusChangeListener(focusListener);
			evUnits500.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units500 = StringUtils.getInt(evUnits500.getText().toString());
					tvUnits500.setText(objdenomination.Units500*500+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits200.setOnFocusChangeListener(focusListener);
			evUnits200.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units200 = StringUtils.getInt(evUnits200.getText().toString());
					tvUnits200.setText(objdenomination.Units200*200+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits100.setOnFocusChangeListener(focusListener);
			evUnits100.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units100 = StringUtils.getInt(evUnits100.getText().toString());
					tvUnits100.setText(objdenomination.Units100*100+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits50.setOnFocusChangeListener(focusListener);
			evUnits50.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units50 = StringUtils.getInt(evUnits50.getText().toString());
					tvUnits50.setText(objdenomination.Units50*50+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits20.setOnFocusChangeListener(focusListener);
			evUnits20.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units20 = StringUtils.getInt(evUnits20.getText().toString());
					tvUnits20.setText(objdenomination.Units20*20+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits10.setOnFocusChangeListener(focusListener);
			evUnits10.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units10 = StringUtils.getInt(evUnits10.getText().toString());
					tvUnits10.setText(objdenomination.Units10*10+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnits5.setOnFocusChangeListener(focusListener);
			evUnits5.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Units5 = StringUtils.getInt(evUnits5.getText().toString());
					tvUnits5.setText(objdenomination.Units5*5+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evUnitsCoin.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.UnitsCoin = StringUtils.getDouble(evUnitsCoin.getText().toString());
					tvUnitsCoin.setText(objdenomination.UnitsCoin*1+"");
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			edtOtherCurrency.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.OtherCurrency = StringUtils.getDouble(edtOtherCurrency.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			edtVoucher.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.Voucher = StringUtils.getDouble(edtVoucher.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evVoucher.setOnFocusChangeListener(focusListener);
			evVoucher.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.VoucherNo = StringUtils.getInt(evVoucher.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			edtCashPaidCredit.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.CashPaidCredit = StringUtils.getDouble(edtCashPaidCredit.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			evcashPaidCrNote.setOnFocusChangeListener(focusListener);
			evcashPaidCrNote.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.CashPaidCrNote = StringUtils.getInt(evcashPaidCrNote.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			edtOtherCurrQty.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) 
				{
					objdenomination.OtherCurrencyNote = StringUtils.getInt(edtOtherCurrQty.getText().toString());
					
					calculateGrandTotal(edtOtherCurrency,tvTotalCash,edtVoucher,edtCashPaidCredit,tvGrandTotal,position);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			container.addView(convertView);
			return convertView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((LinearLayout)object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
	}
}
