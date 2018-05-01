package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.ListOrderFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class OrderSummary extends BaseActivity 
{
	private LinearLayout llDeliveryStatus;
	private TextView tvPageTitle;
	private HashMap<Integer,Vector<TrxHeaderDO>> hmOrders;
	private HashMap<Integer,Vector<TrxHeaderDO>> hmOrdersData;
	private RelativeLayout rlTo,rlFrom,rlGRV;
	
	private TextView  tvSalesOrder, tvSalesOrderCount,tvSalesOrderPrice,tvSalesOrderCurrency,
	tvReturnOrder, tvReturnOrderCount,tvReturnOrderPrice,tvReturnOrderCurrency,
	tvSavedOrder, tvSavedOrderCount,tvSavedOrderPrice,tvSavedOrderCurrency;
	
	private EditText etSearch;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private boolean isInvoice;
	
	private String[] tabsName = {"Sales","SAV ORD","GRV","MISSED","FOC","Pre Sale", "PreSaleFOC"};
	private String[] tabsNamePreseller = {"PRESALES","SALES",/*"SAVED",*/"FOC"};
	private Preference preference;
	
//	private LinearLayout llDateFilterFrom,llDateFilterTo;
	private TextView tvFromDate,tvToDate;
	private String fromDate,toDate;
	private String SUMMARY_LOCK = "SUMMARY_LOCK";
	
	private ImageView ivSearchCross;
	private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String,Float>>();
	HashMap<String, String> hmShiftCodes=new HashMap<String, String>();

	@Override
	public void initialize() 
	{
		llDeliveryStatus	=	(LinearLayout) inflater.inflate(R.layout.summary_list,null);
		llBody.addView(llDeliveryStatus ,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llBody.setBackgroundResource(R.drawable.bg4);
		
		if(getIntent().getExtras() != null )
			isInvoice = getIntent().getExtras().getBoolean("isInvoice");

		
		intializeControls();
		
		if(isInvoice)
			tvPageTitle.setText("Invoice Summary");
		else
		{
			tvPageTitle.setText("Order Summary");
			etSearch.setHint("Search by ShiftTo/Customer Name / Customer Code / Invoice No.");
		}
		
		loadOrderList();
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				getsearchItem("");
			}
		});
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) 
			{
				getsearchItem(s.toString());
			}
		});
		
		
		rlTo.setOnClickListener(new OnClickListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_TO);
			}
		});
		rlFrom.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_FROM);
			}
		});
		
	}
	
	private final String ORDER_SUMMARY_SEARCH = "ORDER_SUMMARY_SEARCH";
	public void getsearchItem(String searchText)
	{
		synchronized (ORDER_SUMMARY_SEARCH) 
		{
			try
			{
				final String strText = searchText;
				HashMap<Integer,Vector<TrxHeaderDO>> tmpSearched = new HashMap<Integer, Vector<TrxHeaderDO>>();
				Predicate<TrxHeaderDO> searchItem = null;
				
				for (Integer key : hmOrdersData.keySet()) 
				{

					searchItem = new Predicate<TrxHeaderDO>() 
							{
						public boolean apply(TrxHeaderDO trxDetailsDO) 
						{
							return (trxDetailsDO.siteName.toLowerCase().contains(strText.toLowerCase()) 
									|| trxDetailsDO.clientCode.toLowerCase().contains(strText.toLowerCase()))
									|| trxDetailsDO.trxCode.toLowerCase().contains(strText.toLowerCase())
									|| hmShiftCodes.get(trxDetailsDO.clientCode).toLowerCase().contains(strText.toLowerCase()) /*vinod*/
									|| trxDetailsDO.referenceCode.toLowerCase().equalsIgnoreCase(strText.toLowerCase());
						}
					};	
					
					if (searchItem!=null &&(!TextUtils.isEmpty(searchText))) 
					{
						Collection<TrxHeaderDO> filteredResult = filter(hmOrdersData.get(key), searchItem);
						if (filteredResult != null && filteredResult.size() > 0) {
							tmpSearched.put(key, new Vector<TrxHeaderDO>((ArrayList<TrxHeaderDO>) filteredResult));
						}
					} else {
						tmpSearched = hmOrdersData;
					}
				}
				final HashMap<Integer,Vector<TrxHeaderDO>> tmp = tmpSearched;
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adapter!=null){
							adapter.refresh(tmp);
							tabs.notifyDataSetChanged();
						}
						
						int trxType = 0;
						if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)){
							for(int position=0;position<tabsNamePreseller.length;position++)
							{
								if(position == 0)
									trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
								else if(position == 1)
									trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
//									trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
								
								if(tmp!=null && tmp.get(trxType)!=null)
									tabs.setTabText(position, tmp.get(trxType).size());
								else
									tabs.setTabText(position,0);
							}
						}
						else
						{
							for (int position = 0; position < tabsName.length; position++) 
							{
								if(position == 0)
									trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
								else if(position == 1)
									trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
								else if(position == 2)
									trxType = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER();
								else if(position == 3)
									trxType = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
								else if(position == 4)
									trxType = TrxHeaderDO.get_TYPE_FREE_DELIVERY();
								else if(position == 5)
									trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
								else if(position == 6)
									trxType = TrxHeaderDO.get_TYPE_FREE_ORDER();
								if(tmp!=null && tmp.get(trxType)!=null)
									tabs.setTabText(position, tmp.get(trxType).size());
								else
									tabs.setTabText(position,0);
							}
						}
					}
				});
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				
			}
		}
	}
	/** initializing all the Controls  of SupervisorManageStaff class **/
	public void intializeControls()
	{
		tvPageTitle				=	(TextView)llDeliveryStatus.findViewById(R.id.tvPageTitle);
		preference				=   new Preference(OrderSummary.this);
		
		tvSalesOrder			=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrder);
		tvSalesOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderCount);
		tvSalesOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderPrice);
		tvSalesOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderCurrency);
		tvReturnOrder			=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrder);
		tvReturnOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderCount);
		tvReturnOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderPrice);
		tvReturnOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderCurrency);
		
		tvSavedOrder			=	(TextView)llDeliveryStatus.findViewById(R.id.tvSavedOrder);
		tvSavedOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSavedOrderCount);
		tvSavedOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSavedOrderPrice);
		tvSavedOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvSavedOrderCurrency);
		
		etSearch				=	(EditText)llDeliveryStatus.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llDeliveryStatus.findViewById(R.id.ivSearchCross);
		
		rlTo					=	(RelativeLayout)llDeliveryStatus.findViewById(R.id.rlTo);
		rlFrom					=	(RelativeLayout)llDeliveryStatus.findViewById(R.id.rlFrom);
		rlGRV					=	(RelativeLayout)llDeliveryStatus.findViewById(R.id.rlGRV);
		
		tabs 	 				= 	(PagerSlidingTabStrip) llDeliveryStatus.findViewById(R.id.tabs);
		pager 					= 	(ViewPager) llDeliveryStatus.findViewById(R.id.pager);
		
		tvFromDate			=	(TextView) llDeliveryStatus.findViewById(R.id.tvFromDate);
		tvToDate			=	(TextView) llDeliveryStatus.findViewById(R.id.tvToDate);
		
//		llDateFilterTo		=	(LinearLayout) llDeliveryStatus.findViewById(R.id.llDateFilterTo);
//		llDateFilterFrom	=	(LinearLayout) llDeliveryStatus.findViewById(R.id.llDateFilterFrom);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		etSearch.setHint(getString(R.string.search_by_item_code_order_summary));
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(OrderSummary.this);
		
		setTypeFaceRobotoNormal(llDeliveryStatus);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
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
		
		if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			rlGRV.setVisibility(View.GONE);
	}
	
	
	public void loadOrderList()
	{
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				synchronized (SUMMARY_LOCK) 
				{
					String empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
					hmOrders = hmOrdersData = new CommonDA().getOrderSummary(empNO,fromDate,toDate);
					hmShiftCodes = new CustomerDA().getShiftToCodes();
					
					final Object obj[] =  new CommonDA().getSumupOfOrders(empNO,fromDate,toDate,preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER));
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							if(adapter == null)
							{
								if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
									adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsNamePreseller);
								else
									adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsName);
								pager.setAdapter(adapter);
								tabs.setViewPager(pager);
							}
							else
							{
								adapter.refresh(hmOrders);
							}
							try
							{
								tvSavedOrderPrice.setText(""+amountFormate.format(((Double)obj[0])));
								tvSavedOrderCount.setText(""+((Long)obj[1]));
								
								tvSalesOrderPrice.setText(""+amountFormate.format(((Double)obj[2])));
								tvSalesOrderCount.setText(""+((Long)obj[3]));
								
								tvReturnOrderPrice.setText(""+amountFormate.format(((Double)obj[4])));
								tvReturnOrderCount.setText(""+((Long)obj[5]));
								
								if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
								{
									tvSalesOrder.setText("Pre Sales Order");
									tvSavedOrder.setText("Sales Order");
								}
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							
							int trxType = 0;
							if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)){
								for(int position=0;position<tabsNamePreseller.length;position++)
								{
									if(position == 0)
										trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
									else if(position == 1)
										trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
//									else if(position == 2)
//										trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
									else if(position == 2)
										trxType = TrxHeaderDO.get_TYPE_FREE_ORDER();
									
									if(hmOrdersData!=null && hmOrdersData.get(trxType)!=null)
										tabs.setTabText(position, hmOrdersData.get(trxType).size());
									else
										tabs.setTabText(position,0);
								}
							}
							else{
								for(int position=0;position<tabsName.length;position++)
								{
									
									if(position == 0)
										trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
									else if(position == 1)
										trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
									else if(position == 2)
										trxType = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER();
									else if(position == 3)
										trxType = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
									else if(position == 4)
										trxType = TrxHeaderDO.get_TYPE_FREE_DELIVERY();
									else if(position == 5)
										trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
									else if(position == 6)
										trxType = TrxHeaderDO.get_TYPE_FREE_ORDER();
									
									if(hmOrders!=null && hmOrders.get(trxType)!=null)
										tabs.setTabText(position, hmOrders.get(trxType).size());
									else
										tabs.setTabText(position,0);
									
									if(hmOrdersData!=null && hmOrdersData.get(trxType)!=null)
										tabs.setTabText(position, hmOrdersData.get(trxType).size());
									else
										tabs.setTabText(position,0);
								}
							}
							hideLoader();
						}
					});
				}
			}
		}).start();
	}
	
	public class CategoryPagerAdapter extends FragmentStatePagerAdapter 
	{
		private  String[] tabsName;
		
		public CategoryPagerAdapter(FragmentManager fm,String[] tabsName)
		{
			super(fm);
			this.tabsName = tabsName;
		}

		public void refresh(HashMap<Integer, Vector<TrxHeaderDO>> tmpSearched) 
		{
			hmOrders = tmpSearched;
			notifyDataSetChanged();
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			if(tabsName != null && tabsName.length > 0)
				return tabsName[position];
			
			return "N/A";
		}
		
		@Override
		public int getCount() 
		{
			if(tabsName == null || tabsName.length <= 0)
				return 0;
			return tabsName.length;
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position)
		{
			int trxType = 0;
			if(getCount()==/*4*/3){
				if(position == 0)
					trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				else if(position == 1)
					trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
//				else if(position == 2)
//					trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
				else if(position == 2)
					trxType = TrxHeaderDO.get_TYPE_FREE_ORDER();
			}else{
				if(position == 0)
					trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
				else if(position == 1)
					trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
				else if(position == 2)
					trxType = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER();
				else if(position == 3)
					trxType = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
				else if(position == 4)
					trxType = TrxHeaderDO.get_TYPE_FREE_DELIVERY();
				else if(position == 5)
					trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				else if(position == 6)
					trxType = TrxHeaderDO.get_TYPE_FREE_ORDER();
			}
			return new ListOrderFragment(OrderSummary.this,position,hmOrders.get(trxType),tabs,true);
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
	    			loadOrderList();
	    		}
	    		else
	    			showCustomDialog(OrderSummary.this,"Alert",getString(R.string.from_date_should_not_be_greater_than_to_date),getString(R.string.OK),null,null);
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
	    			loadOrderList();
	    		}
	    		else
	    			showCustomDialog(OrderSummary.this,"Alert",getString(R.string.to_date_should_not_be_lesser_than_from_date),getString(R.string.OK),null,null);
	    	}
	    }
    };
    
    public void onButtonYesClick(String from) 
    {
    	super.onButtonYesClick(from);
    	if(from.contains("delete_saved"))
    	{
    		String[] splitCode = from.split(",");
    		if(splitCode != null && splitCode.length > 1)
    			cancelSavedOrder(splitCode[1]);
    	}
    };
    
    public void cancelSavedOrder(final String trxCode)
	{
		showLoader("cancelling order...");
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				new OrderDA().cancelSavedOrder(trxCode);
				final Vector<TrxHeaderDO> vecOrderList = hmOrders.get(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER());
				for (int i = 0; i < vecOrderList.size(); i++) 
				{
					if(vecOrderList.get(i).trxCode.equalsIgnoreCase(trxCode))
					{
						vecOrderList.remove(i);
						break;
					}
				}
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						adapter.notifyDataSetChanged();
						if(tabs != null && vecOrderList != null)
							tabs.setTabText(1, vecOrderList.size());//1 for saved order position
						
						uploadData();
					}
				});
			}
		}).start();
	}
}
