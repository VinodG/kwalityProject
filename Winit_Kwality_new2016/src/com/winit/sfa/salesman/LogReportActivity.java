package com.winit.sfa.salesman;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.parsers.TrxLogResponseParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;
public class LogReportActivity extends BaseActivity
{
	//Initialization and declaration of variables
	@SuppressWarnings("unused")
	private LinearLayout llOrderSheet, llItemHeader;
	private Button btnPrint;
	
	//header bar
	private TextView tvPageTitle;
	private Button btn;
	private Vector<TrxLogDetailsDO> vecVanLoadTemp;
	
	//list
	private ImageView ivSearchCross;
	private EditText etSearch;
	private ListView listView;
	private TextView  tvNoItemFound;
	private LogOrderListAdapter inventoryItemAdapter;
	
	//for date filter
	private LinearLayout llDateFilterTo;
	private TextView tvToDate,tvToDateTag;
	private String fromDate,toDate;
	
	private ImageView ivMtd,ivToday;
	private int isMTD = 0;
	
	// top blocks
	
	private TextView tvTotalScheduledCalls,tvTotalActualCalls,tvTotalProductiveCalls,
	tvTotalSales,tvTotalCreditNotes,tvCollectionReceived,
	tvCurrentMonthSales;
	
	private TextView tvTotalScheduledCallsValue,tvTotalActualCallsValue,tvTotalProductiveCallsValue,
	tvTotalSalesValue,tvTotalCreditNotesValue,tvCollectionReceivedValue,
	tvCurrentMonthSalesValue;
	Vector<TrxLogHeaders> vecTrxhHeaderDOs = new Vector<TrxLogHeaders>(); 
	private void setHeaderBar()
	{
		tvPageTitle	= (TextView) llOrderSheet.findViewById(R.id.tvPageTitle);
		btn	= (Button) llOrderSheet.findViewById(R.id.btn);
		tvPageTitle.setText("Store Check");
		btn.setText("SUBMIT");
		btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.refresh), null, null, null);
	}
	
	@Override
	public void initialize() 
	{
		//inflate the order-sheet layout
		llOrderSheet 	 = (LinearLayout)inflater.inflate(R.layout.log_report, null);
		llBody.addView(llOrderSheet,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		vecVanLoadTemp=new Vector<TrxLogDetailsDO>();
		
		setHeaderBar();
		//function for getting id's and setting type-faces 
		intialiseControls();
		tvPageTitle.setText("Log Report");
		
		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				TrxLogHeaders trxLogHeaders = vecTrxhHeaderDOs.get(0);
				if(trxLogHeaders!=null && trxLogHeaders.vecTrxLogDetailsDO!=null && trxLogHeaders.vecTrxLogDetailsDO.size()>0)
				{
					Intent intent = new Intent(LogReportActivity.this,WoosimPrinterActivity.class);
					intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_LOG_REPORT);
					intent.putExtra("trxLogHeaders", trxLogHeaders);
					intent.putExtra("isMTD", isMTD);
					if(isMTD == 0)//Today
					{
						intent.putExtra("fromDate",toDate);
						intent.putExtra("toDate", toDate);
					}
					else//MTD
					{
						intent.putExtra("fromDate", fromDate);
						intent.putExtra("toDate", CalendarUtils.getOrderPostDate());
					}
						
					startActivity(intent);
				}
			}
		});
		//Log Reports
		
		btn.setText("Refresh");
		btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showLoader(getResources().getString(R.string.loading));
				new Thread(new Runnable() {
					@Override
					public void run() 
					{
						String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
						getTrxLogDataSync(empNo);
						loadData();
					}
				}).start();
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				filter(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		
		Calendar c = Calendar.getInstance();
		
		monthTo = c.get(Calendar.MONTH);
		yearTo = c.get(Calendar.YEAR);
		dayTo = c.get(Calendar.DAY_OF_MONTH);
		
		String selectedDate = CalendarUtils.getCurrentMonthFisrtDay();
		fromDate = selectedDate;
    	
    	selectedDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
    	
    	toDate = selectedDate;
		tvToDate.setText(CalendarUtils.getFormatedDatefromString(selectedDate));
		tvToDate.setTag(selectedDate);
		
		tvToDateTag.setText("Select Date");
		
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
				new DatePickerDialog(LogReportActivity.this, toDateListner,  yearTo, monthTo, dayTo).show();
			}
		});
		
		ivMtd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				isMTD = 1;
				ivMtd.setImageResource(R.drawable.mtd_select);
				ivToday.setImageResource(R.drawable.today_unselect);
				loadData();
			}
		});
		
		ivToday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				isMTD = 0;
				ivMtd.setImageResource(R.drawable.mtd_unselect);
				ivToday.setImageResource(R.drawable.today_select);
				
				loadData();
			}
		});
		
		setTypeFaceRobotoNormal(llOrderSheet);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		btn.setTypeface(Typeface.DEFAULT_BOLD);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		loadData();
	}
	
	private void filter(String strText)
	{
		if(!TextUtils.isEmpty(strText))
			ivSearchCross.setVisibility(View.VISIBLE);
		else
			ivSearchCross.setVisibility(View.GONE);
		
		vecVanLoadTemp.clear();
		if(vecTrxhHeaderDOs!=null && vecTrxhHeaderDOs.size()>0)
		{
			Vector<TrxLogDetailsDO> vecTrxDetails = vecTrxhHeaderDOs.get(0).vecTrxLogDetailsDO;
			for (int i = 0; vecTrxDetails!=null && i < vecTrxDetails.size(); i++) 
			{
				TrxLogDetailsDO VanLoadDO = vecTrxDetails.get(i);
				if(VanLoadDO.CustomerName.toLowerCase().contains(strText.trim()) || VanLoadDO.CustomerCode.toLowerCase().contains(strText.trim()))
				{
					if(!vecVanLoadTemp.contains(VanLoadDO))
						vecVanLoadTemp.add(VanLoadDO);
				}
			}
			if(strText.length() > 0)
				inventoryItemAdapter.refresh(vecVanLoadTemp);
			else
				inventoryItemAdapter.refresh(vecTrxDetails);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/** initializing all the Controls  of DeliveryVerifyItemList class **/
	public void intialiseControls()
	{
		//getting ids
		llItemHeader	 		=	(LinearLayout)llOrderSheet.findViewById(R.id.llItemHeader);
		tvNoItemFound			=	(TextView)llOrderSheet.findViewById(R.id.tvNoItemFound);
		btnPrint				=	(Button)llOrderSheet.findViewById(R.id.btnPrint);
		etSearch				=	(EditText)llOrderSheet.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llOrderSheet.findViewById(R.id.ivSearchCross);
		tvToDate				=	(TextView) llOrderSheet.findViewById(R.id.tvToDate);
		tvToDateTag				=	(TextView) llOrderSheet.findViewById(R.id.tvToDateTag);
		llDateFilterTo			=	(LinearLayout) llOrderSheet.findViewById(R.id.llDateFilterTo);
		ivMtd               	=   (ImageView)llOrderSheet.findViewById(R.id.ivMtd);
		ivToday             	=   (ImageView)llOrderSheet.findViewById(R.id.ivToday);
		
		
		tvTotalScheduledCallsValue		=	(TextView) llOrderSheet.findViewById(R.id.tvTotalScheduledCallsValue);
		tvTotalActualCallsValue			=	(TextView) llOrderSheet.findViewById(R.id.tvTotalActualCallsValue);
		tvTotalProductiveCallsValue		=	(TextView) llOrderSheet.findViewById(R.id.tvTotalProductiveCallsValue);
		tvTotalSalesValue				=	(TextView) llOrderSheet.findViewById(R.id.tvTotalSalesValue);
		tvTotalCreditNotesValue			=	(TextView) llOrderSheet.findViewById(R.id.tvTotalCreditNotesValue);
		tvCollectionReceivedValue		=	(TextView) llOrderSheet.findViewById(R.id.tvCollectionReceivedValue);
		tvCurrentMonthSalesValue		=	(TextView) llOrderSheet.findViewById(R.id.tvCurrentMonthSalesValue);
		
		tvTotalScheduledCalls		=	(TextView) llOrderSheet.findViewById(R.id.tvTotalScheduledCalls);
		tvTotalActualCalls			=	(TextView) llOrderSheet.findViewById(R.id.tvTotalActualCalls);
		tvTotalProductiveCalls		=	(TextView) llOrderSheet.findViewById(R.id.tvTotalProductiveCalls);
		tvTotalSales				=	(TextView) llOrderSheet.findViewById(R.id.tvTotalSales);
		tvTotalCreditNotes			=	(TextView) llOrderSheet.findViewById(R.id.tvTotalCreditNotes);
		tvCollectionReceived		=	(TextView) llOrderSheet.findViewById(R.id.tvCollectionReceived);
		tvCurrentMonthSales		=	(TextView) llOrderSheet.findViewById(R.id.tvCurrentMonthSales);
		
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		btn.setVisibility(View.VISIBLE);
		
        btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
		btnMenu.setVisibility(View.VISIBLE);
		listView = 	(ListView)llOrderSheet.findViewById(R.id.lvStockItems);
		listView.setVisibility(View.VISIBLE);
		listView.setCacheColorHint(0);
		etSearch.setHint(getString(R.string.search_by_customer_code_or_name));
		listView.setAdapter(inventoryItemAdapter = new LogOrderListAdapter(new Vector<TrxLogDetailsDO>()) );
	}
	
	private void loadData()
	{
		
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				if(isMTD == 0)
				{
					vecTrxhHeaderDOs = new TransactionsLogsDA().getTrxLogHeaders(toDate,toDate,isMTD);
				}
				else
				{
					vecTrxhHeaderDOs = new TransactionsLogsDA().getTrxLogHeaders(fromDate,toDate,isMTD);
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() 
					{
						if(vecTrxhHeaderDOs!=null && vecTrxhHeaderDOs.size()>0)
						{
							listView.setVisibility(View.VISIBLE);
							tvNoItemFound.setVisibility(View.GONE);
							btnPrint.setVisibility(View.VISIBLE);
							bindHeaderData(vecTrxhHeaderDOs.get(0));
							
							if(vecTrxhHeaderDOs!=null && vecTrxhHeaderDOs.size()>0
									&& vecTrxhHeaderDOs.get(0).vecTrxLogDetailsDO != null 
									&& vecTrxhHeaderDOs.get(0).vecTrxLogDetailsDO.size() > 0)
							{
								listView.setVisibility(View.VISIBLE);
								tvNoItemFound.setVisibility(View.GONE);
								btnPrint.setVisibility(View.VISIBLE);
								inventoryItemAdapter.refresh(vecTrxhHeaderDOs.get(0).vecTrxLogDetailsDO);
							}
							else
							{
								listView.setVisibility(View.GONE);
								tvNoItemFound.setVisibility(View.VISIBLE);
								btnPrint.setVisibility(View.GONE);
							}
						}
						else
						{
							TrxLogHeaders trxLogHeader=new TrxLogHeaders();
							bindHeaderData(trxLogHeader);
							listView.setVisibility(View.GONE);
							tvNoItemFound.setVisibility(View.VISIBLE);
							btnPrint.setVisibility(View.GONE);
						}
						
						if(etSearch!=null && !TextUtils.isEmpty(etSearch.getText().toString()))
							filter(etSearch.getText().toString());
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	private void bindHeaderData(TrxLogHeaders trxLogHeader)
	{
		
		
		amountFormate  = new DecimalFormat("#,##,##,##,###.##");
		amountFormate.setMinimumFractionDigits(2);
		amountFormate.setMaximumFractionDigits(2);
		
		if(isMTD==0)
		{
			tvTotalScheduledCalls.setText("Today's Scheduled Calls");
			tvTotalActualCalls.setText("Today's Actual Calls");
			tvTotalProductiveCalls.setText("Today's Productive Calls");
			tvTotalSales.setText("Today's Sale");
			tvTotalCreditNotes.setText("Today's Credit Note");
			tvCollectionReceived.setText("Today's Collection");
			tvCurrentMonthSales.setText("Today's Current Month Sale");
		}
		else
		{
			tvTotalScheduledCalls.setText("MDT Scheduled Calls");
			tvTotalActualCalls.setText("MDT Actual Calls");
			tvTotalProductiveCalls.setText("MDT Productive Calls");
			tvTotalSales.setText("MDT Sales");
			tvTotalCreditNotes.setText("MDT Credit Notes");
			tvCollectionReceived.setText("MDT Collections");
			tvCurrentMonthSales.setText("MDT Current Month Sale");
		}
		tvTotalScheduledCallsValue.setText(""+trxLogHeader.TotalScheduledCalls);
		tvTotalActualCallsValue.setText(""+trxLogHeader.TotalActualCalls);
		tvTotalProductiveCallsValue.setText(""+trxLogHeader.TotalProductiveCalls);
		tvTotalSalesValue.setText(""+amountFormate.format(trxLogHeader.TotalSales));
		tvTotalCreditNotesValue.setText(""+amountFormate.format(trxLogHeader.TotalCreditNotes));
		tvCollectionReceivedValue.setText(""+amountFormate.format(trxLogHeader.TotalCollections));
		tvCurrentMonthSalesValue.setText(""+amountFormate.format(trxLogHeader.CurrentMonthlySales));
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == 1111 && resultCode == 1111)
		{
			finish();
		}
		else if(requestCode == 1000 && AppConstants.objScanResultObject!=null)
		{
			Intent intent = new Intent(LogReportActivity.this, ScanItemActivity.class);
			startActivityForResult(intent, 100);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	ArrayList<VanLoadDO> vecUpdatedData;
	public class LogOrderListAdapter extends BaseAdapter
	{
		Vector<TrxLogDetailsDO> vecTrxLogDetails;
		
		public LogOrderListAdapter(Vector<TrxLogDetailsDO> vecOrdProduct)
		{
			this.vecTrxLogDetails = vecOrdProduct;
		}

		private void refresh(Vector<TrxLogDetailsDO> vecOrdProducts) 
		{
			this.vecTrxLogDetails = vecOrdProducts;
			notifyDataSetChanged();
		}
		

		@Override
		public int getCount()
		{
			if(vecTrxLogDetails!=null && vecTrxLogDetails.size()>0)
				return vecTrxLogDetails.size();
			else
			return 0;
		}
		public Vector<TrxLogDetailsDO> getAdapterDataOfLog()
		{
			return this.vecTrxLogDetails;
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int position) 
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			TrxLogDetailsDO trxLogDetailsDO			= vecTrxLogDetails.get(position);
			ViewHolder viewHolder=null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView	= (LinearLayout)getLayoutInflater().inflate(R.layout.log_report_item,null);
				
				viewHolder.tvCustmCodeName		= (TextView)convertView.findViewById(R.id.tvCustmCodeName);
				viewHolder.tvCustmCodeCode		= (TextView)convertView.findViewById(R.id.tvCustmCodeCode);	
				viewHolder.tvTrxType			= (TextView)convertView.findViewById(R.id.tvType);
				viewHolder.tvTrxNo				= (TextView)convertView.findViewById(R.id.tvTrxNoTag);
				viewHolder.tvAmt				= (TextView)convertView.findViewById(R.id.tvAmount);
				viewHolder.tvTimeStamp			= (TextView)convertView.findViewById(R.id.tvTimeStamp);
				convertView.setTag(viewHolder);
			}else
				viewHolder = (ViewHolder) convertView.getTag();
			
			viewHolder.tvCustmCodeName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			
			trxLogDetailsDO.TrxType = trxLogDetailsDO.TrxType.replace("s", "");
			
			if(trxLogDetailsDO.TrxType.equalsIgnoreCase(AppConstants.CREDITNOTE))
				trxLogDetailsDO.TrxType ="Credit Note";
			String strName = trxLogDetailsDO.CustomerName.trim();
			if(!strName.equalsIgnoreCase("") && strName.length() >= 1 && strName.charAt(strName.length()-1) ==',')
			{
				strName = strName.substring(0, strName.length()-1);
			}
			String name = /*"["+trxLogDetailsDO.CustomerCode+"] \n"+*/strName;
			viewHolder.tvCustmCodeCode.setText(""+trxLogDetailsDO.CustomerCode);
			if(trxLogDetailsDO.IsJp.trim().equalsIgnoreCase("True"))
				viewHolder.tvCustmCodeCode.setTextColor(getResources().getColor(R.color.green));
			else
				viewHolder.tvCustmCodeCode.setTextColor(getResources().getColor(R.color.black));
			viewHolder.tvCustmCodeName.setText(name);
			viewHolder.tvTrxType.setText(trxLogDetailsDO.TrxType);
			viewHolder.tvTrxNo.setText(trxLogDetailsDO.DocumentNumber);
			
			if(trxLogDetailsDO.Amount>0)
				viewHolder.tvAmt.setText(""+decimalFormat.format(trxLogDetailsDO.Amount));
			else
				viewHolder.tvAmt.setText("N/A");
			
//			String isJp = trxLogDetailsDO.IsJp.trim().equalsIgnoreCase("True")?"Yes":"No";
			
			
			viewHolder.tvTimeStamp.setText(CalendarUtils.getDateToShow(trxLogDetailsDO.TimeStamp));
//			viewHolder.tvIsJP.setText(isJp);
			return convertView;
		}
	}
	
	private void updateValues()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						Intent intent = new Intent(LogReportActivity.this, OdometerReadingActivity.class);
						intent.putExtra("isStartDay", true);
						startActivity(intent);
						setResult(1111);
						finish();
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		finish();
	}
	
	public class ViewHolder{
		TextView tvCustmCodeName;
		TextView tvTrxType;
		TextView tvTrxNo;
		TextView tvAmt;
		TextView tvTimeStamp;
		TextView tvCustmCodeCode;
	}
	

	//----------------------------------------------------------------------------------------------------
	// for date filter
	//----------------------------------------------------------------------------------------------------

	private int monthTo, yearTo, dayTo;

	/**
	 * date set listener for "From Date"
	 */
//	private DatePickerDialog.OnDateSetListener fromDateListner = new DatePickerDialog.OnDateSetListener()
//	{
//		public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) 
//		{
//			String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);
//
//			if(!selectedDate.equalsIgnoreCase(fromDate))
//			{
//				if(CalendarUtils.getDiffBtwDatesFromCurrentDate(selectedDate) > 30)
//	    		{
//	    			showAlertForOneMonthDateDiff();
//	    		}
//	    		else if(CalendarUtils.getDiffBtwDatesInDays(selectedDate,toDate) >= 0)
//				{
////					if(CalendarUtils.getDiffBtwDatesInDays(selectedDate,toDate) <= 30)
////					{
//						yearFrom = yearSel;
//						monthFrom = monthOfYear;
//						dayFrom = dayOfMonth;
//	
//	
//						fromDate = selectedDate;
//						tvFromDate.setText(CalendarUtils.getFormatedDatefromString(fromDate));
//						tvFromDate.setTag(fromDate);
//						loadData();
////					}
////					else
////					{
////						showCustomDialog(LogReportActivity.this,"Alert",getString(R.string.log_date_validation),getString(R.string.OK),null,null);
////					}
//				}
//				else
//					showCustomDialog(LogReportActivity.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);
//			}
//
//		}
//	};

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
					loadData();
				}
				else
					showCustomDialog(LogReportActivity.this,"Alert",getString(R.string.log_date_validation_minimum_date),getString(R.string.OK),null,"ONE_DAY_DATA");
			}
		}
	};
	
	
	public void getTrxLogDataSync(String empNo)
	{
		LogUtils.debug("loadIncrementalData", "started");
		ConnectionHelper connectionHelper = new ConnectionHelper(LogReportActivity.this);;
		showLoader("Syncing Data...");
		TrxLogResponseParser allDataSyncParser = new TrxLogResponseParser(LogReportActivity.this, empNo);
		
		SynLogDO synLogDO = new SynLogDA().getSynchLog(ServiceURLs.GetCommonMasterDataSync);
		
		String lsd = 0+"";
		String lst = 0+"";
		if(synLogDO != null)
		{
			lsd = synLogDO.UPMJ;
			lst = synLogDO.UPMT;
		}
		else
		{
			lsd = preference.getStringFromPreference(Preference.LSD, lsd);
			lst = preference.getStringFromPreference(Preference.LST, lst);
		}
		LogUtils.debug("loadIncrementalData", "lsd =>"+lsd);
		LogUtils.debug("loadIncrementalData", "lst =>"+lst);
		connectionHelper.sendRequest(LogReportActivity.this,BuildXMLRequest.GetTrxLogDataSync(preference.getStringFromPreference(Preference.EMP_NO, ""), StringUtils.getInt(lsd), StringUtils.getInt(lst)), allDataSyncParser, ServiceURLs.GetTrxLogDataSync);
		
	}
	
	
	public void onButtonYesClick(String from) {
		if(from.equalsIgnoreCase(AppConstants.ONE_MONTH_DATA)){
			Calendar ca = Calendar.getInstance();
    		ca.getMaximum(Calendar.DAY_OF_MONTH);
    		ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH));
    		
			yearTo = ca.get(Calendar.YEAR);
			monthTo = ca.get(Calendar.MONTH);
			dayTo = ca.get(Calendar.DAY_OF_MONTH);

			toDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
			tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
			tvToDate.setTag(toDate);
			
			loadData();
    	}
		else if(from.equalsIgnoreCase("ONE_DAY_DATA")){
			Calendar ca = Calendar.getInstance();
    		ca.getMinimum(Calendar.DAY_OF_MONTH);
    		ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH));
    		
			yearTo = ca.get(Calendar.YEAR);
			monthTo = ca.get(Calendar.MONTH);
			dayTo = ca.get(Calendar.DAY_OF_MONTH);

			toDate = CalendarUtils.getOrderSummaryDate(yearTo,monthTo,dayTo);
			tvToDate.setText(CalendarUtils.getFormatedDatefromString(toDate));
			tvToDate.setTag(toDate);
			
			loadData();
    	}
		else
		{
			super.onButtonYesClick(from);
		}
	};
}
