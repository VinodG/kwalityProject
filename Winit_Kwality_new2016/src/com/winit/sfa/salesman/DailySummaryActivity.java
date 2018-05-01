package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.adapter.DaliySummaryAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomEditText;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CashDenominationDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;
import com.winit.alseer.salesman.dataobject.DailySumDO;
import com.winit.alseer.salesman.dataobject.DailySummaryDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.winit.sfa.salesman.LogReportActivity.LogOrderListAdapter;

public class DailySummaryActivity extends BaseActivity
{
	private LinearLayout llDailySummary, llItemHeader;
	private Button btnPrintdailySummary;
	//header bar
	private TextView tvPageTitle;
	private ListView lvDailySummary;
	private TextView  tvNoItemFound,tvTotalCashVal,tvTotalChequeval,tvTotalZeroSalesVal,tvTotalSalesValue,
		tvTotalCreditNotesValue,tvTotalUnvisitedVal,tvTotalTPT	;
	
	//for date filter
	private LinearLayout llDateFilterTo;
	private TextView tvToDate,tvToDateTag;
	private String fromDate,toDate;
	private ArrayList<DailySummaryDO> arrDailySummary;
	private DailySumDO objDailySumDO;
	
	@Override
	public void initialize() 
	{
		llDailySummary = (LinearLayout) inflater.inflate(R.layout.daily_summary, null);
		llBody.addView(llDailySummary,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		bindControls();
		tvPageTitle.setText("Daily Summary");
		
		loadData();
	}
	
	boolean isAlreadyDone = false;
	private DaliySummaryAdapter adapter;
	private void loadData() 
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
//				arrDailySummary = new CashDenominationDA().getDailySummary(toDate);
				
				
				objDailySumDO = new CashDenominationDA().getDailySummaryWithQuantity(toDate,preference.getStringFromPreference(preference.USER_ID, ""));
				objDailySumDO.targetAcheivement = new UserInfoDA().getUserTargetAndAcheive(preference.getStringFromPreference(preference.USER_ID, ""));
				
				arrDailySummary = new ArrayList<DailySummaryDO>();
				Set<String> hashKeys = objDailySumDO.hashDailySummary.keySet();
				for(String key : hashKeys)
				{
					arrDailySummary.addAll(objDailySumDO.hashDailySummary.get(key));
				}
//				arrDailySummary = objDailySumDO.arrDailySummary;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(arrDailySummary != null && arrDailySummary.size() > 0)
						{
							adapter = new DaliySummaryAdapter(DailySummaryActivity.this,arrDailySummary);
							lvDailySummary.setAdapter(adapter);
							
							tvNoItemFound.setVisibility(View.GONE);
							lvDailySummary.setVisibility(View.VISIBLE);
						}
						else
						{
							tvNoItemFound.setVisibility(View.VISIBLE);
							lvDailySummary.setVisibility(View.GONE);
						}
						
//						double[] total = {0,0,0,0,0,0,0};
//						for (int i = 0; i < arrDailySummary.size(); i++) 
//						{
//							if(arrDailySummary.get(i).Priority.equalsIgnoreCase("1"))//SALES
//								total[0] += StringUtils.getFloat(arrDailySummary.get(i).amount);
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("2"))//GRV
//								total[1] += StringUtils.getFloat(arrDailySummary.get(i).amount);
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("3"))//FOC
//								total[2] += StringUtils.getFloat(arrDailySummary.get(i).amount);
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("4"))//CASH
//								total[3] += StringUtils.getFloat(arrDailySummary.get(i).amount);
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("5"))//CHEQUE
//								total[4] += StringUtils.getFloat(arrDailySummary.get(i).amount);
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("6"))//ZERO SALES
//								total[5] += 1;
//							else if(arrDailySummary.get(i).Priority.equalsIgnoreCase("7"))//UNVISITED
//								total[6] += 1;
//						}
						
						double[] total = objDailySumDO.total;
						
						tvTotalSalesValue.setText(""+decimalFormat.format(total[0]));//SALES
						tvTotalCashVal.setText(""+decimalFormat.format(total[3]));//CASH
						tvTotalChequeval.setText(""+decimalFormat.format(total[4]));//CHEQUE
						tvTotalZeroSalesVal.setText(""+total[5]);//ZEROSALES

						tvTotalCreditNotesValue.setText(""+decimalFormat.format(total[8]));//SALES ICE CREAM
						tvTotalUnvisitedVal.setText(""+decimalFormat.format(total[9]));//SALES FOOD
						tvTotalTPT.setText(""+decimalFormat.format(total[10]));//SALES TPT
					}
				});
			}

		}).start();
	}
	
	private int monthTo, yearTo, dayTo;
	private void bindControls() 
	{
		btnPrintdailySummary.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showCustomDialog(DailySummaryActivity.this, getString(R.string.alert), "Please select the Type of print you wish to take.", "Main Summary", "Both", "print_type");
			}
		});
		
		Calendar c = Calendar.getInstance();
		
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		
//		String selectedDate = CalendarUtils.getCurrentMonthFisrtDay();
		String selectedDate = CalendarUtils.getCurrentDateAsStringforStoreCheck();
		fromDate = selectedDate;
    	
    	selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
    	
    	toDate = selectedDate;
		tvToDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
		tvToDate.setTag(selectedDate);
		
		tvToDateTag.setText("Select Date:");
		
		tvToDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				llDateFilterTo.performClick();
			}
		});
		
		llDateFilterTo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DatePickerDialog(DailySummaryActivity.this, toDateListner,  yearTo, monthTo, dayTo).show();
			}
		});
	}
	
	@Override
	public void onButtonYesClick(String from) {
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("print_type")){
			startPrint(DailySummaryDO.get_DAILY_SUMMARY_ICECREAM());
		}
	}
	
	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("print_type")){
			startPrint(DailySummaryDO.get_DAILY_SUMMARY_FOOD());
		}
	}
	
	private void startPrint(int type){
		Intent intent = new Intent(DailySummaryActivity.this,WoosimPrinterActivity.class);
		intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_DAILY_SUMMARY);
//			intent.putExtra("daily_summary", arrDailySummary);
		intent.putExtra("daily_summary", objDailySumDO);
		intent.putExtra("select_date", toDate);
		intent.putExtra("print_type", type);
		startActivity(intent);
	}
	
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
				if(CalendarUtils.getDiffBtwDatesInDays(fromDate,selectedDate) >= -30)
				{
					
					yearTo = yearSel;
					monthTo = monthOfYear;
					dayTo = dayOfMonth;

					toDate = selectedDate;
					tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
					tvToDate.setTag(toDate);
					loadData();
				}
				else
					showCustomDialog(DailySummaryActivity.this,"Alert",getString(R.string.log_date_validation_minimum_date),getString(R.string.OK),null,"ONE_DAY_DATA");
			}
		}
	};
	
	private void initializeControls() 
	{
		//getting ids
		llItemHeader	 		=	(LinearLayout)llDailySummary.findViewById(R.id.llItemHeader);
		tvNoItemFound			=	(TextView)llDailySummary.findViewById(R.id.tvNoItemFound);
		btnPrintdailySummary	=	(Button)llDailySummary.findViewById(R.id.btnPrintdailySummary);
		tvToDate				=	(TextView) llDailySummary.findViewById(R.id.tvToDate);
		tvToDateTag				=	(TextView) llDailySummary.findViewById(R.id.tvToDateTag);
		llDateFilterTo			=	(LinearLayout) llDailySummary.findViewById(R.id.llDateFilterTo);
		
		tvTotalCashVal			=	(TextView) llDailySummary.findViewById(R.id.tvTotalCashVal);
		tvTotalChequeval		=	(TextView) llDailySummary.findViewById(R.id.tvTotalChequeval);
		tvTotalZeroSalesVal		=	(TextView) llDailySummary.findViewById(R.id.tvTotalZeroSalesVal);
		tvTotalSalesValue		=	(TextView) llDailySummary.findViewById(R.id.tvTotalSalesValue);
		tvTotalCreditNotesValue	=	(TextView) llDailySummary.findViewById(R.id.tvTotalCreditNotesValue);
		tvTotalUnvisitedVal		=	(TextView) llDailySummary.findViewById(R.id.tvTotalUnvisitedVal);
		tvTotalTPT		=	(TextView) llDailySummary.findViewById(R.id.tvTotalTPT);
		tvPageTitle				=	(TextView) llDailySummary.findViewById(R.id.tvPageTitle);

		lvDailySummary 			= (ListView)llDailySummary.findViewById(R.id.lvStockItems);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
        btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
		btnMenu.setVisibility(View.VISIBLE);
		
		lvDailySummary.setVisibility(View.VISIBLE);
		lvDailySummary.setCacheColorHint(0);
	}
}
