package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.common.Rotate3dAnimation;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.EOTDA;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.BaseComparableDO;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.kwalitysfa.salesman.R;

@SuppressLint("DefaultLocale") public class PresellerJourneyPlan extends BaseActivity implements  ConnectionExceptionListener
{
	//declaration of variables
	private LinearLayout llMain, llTimeTitle, llMap,llgreenbar;
	@SuppressWarnings("unused")
	private TextView tvSortBy,tvNoCustomers, tvJourneyPlanDate, tvJourneyPlanDateValue, tvResultOfSearch, tvSeprator, tvTime, tvCustomers;
	private Button btnTopCalIcon, btnAdvance;
	private EditText etSearch;
	private ListView lvEvents;
	private Intent CheckIN = null;
	private String strCurrentDate, strSelectedDate;
	private Vector<NameIDDo> vecReasons;
	private SetTimer objTimer;
	private GoogleMap mapview;
	private CustomCalEventsAdapter objCustomCalEventsAdapter;
	private LocationUtility  locationUtility = null;
	private CustomerDetailsDA objCustomerDetailsBL;
	private ViewGroup flContainer;
	private PopupWindow popupWindow;
	private ArrayList<String> vecServedCustomerWithDataNotPosted;
	private HashMap<String, Integer> hmVisits;
	
	private boolean isOnResume = false;
	private Vector<PostReasonDO> vecPostReasons = new Vector<PostReasonDO>();
	private PostReasonDO objPostReasonDO ;	
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private Vector<Marker> veMarkers;
	private HashMap<String, CustomerCreditLimitDo> hmCreditLimits;
	private HashMap<String, Float> hmOverDue;
	private ImageView btnGlobe;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
//		preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
//		preference.commitPreference();
		
		llMain				=	(LinearLayout) inflater.inflate(R.layout.calenderviewxml, null);
		llBody.addView(llMain, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		intialiseControls();
		setTypeFaceRobotoNormal(llMain);
		arrayListEvent= new ArrayList<JourneyPlanDO>();
		strCurrentDate 			= 	CalendarUtils.getCurrentDateAsString();
		locationUtility 		= 	new LocationUtility(this);
		objCustomerDetailsBL 	=   new CustomerDetailsDA();
		
		//inflate the calendar-view-xml layout
		hmVisits = new HashMap<String, Integer>();	
		vecServedCustomerWithDataNotPosted = new ArrayList<String>();
		LogUtils.errorLog("", "5 "+isEOTDone());
		
		lvEvents.setCacheColorHint(0);
		lvEvents.setFadingEdgeLength(0);
		lvEvents.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvEvents.setSelector(R.drawable.list_item_selected);
		
//		loadCustomerList();
		
		lvEvents.setAdapter(objCustomCalEventsAdapter = new CustomCalEventsAdapter(new ArrayList<JourneyPlanDO>()));
		
		btnGlobe.setTag("list");
		
		Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month 	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    String strMonth = "", strDate = "";
	    
	    if(month < 9)
	    	strMonth = "0"+(month+1);
		else
			strMonth = ""+(month+1);
		
		if(day < 10)
			strDate = "0"+(day);
		else
			strDate = ""+(day);
		
		//creating the required Date format
		strSelectedDate = year+"-"+strMonth+"-"+strDate;
	    tvJourneyPlanDateValue.setText(" "+CalendarUtils.getMonthAsString(month)+""+day+CalendarUtils.getDateNotation(day)+", "+year);
	    
	    if(preference.getStringFromPreference(Preference.DAY_VARIFICATION, "").equals(""))
	    {
		    preference.saveStringInPreference(Preference.DAY_VARIFICATION, CalendarUtils.getCurrentDate());
		    preference.commitPreference();
	    }
	    
	   // tvSortBy.setTag("sortby");
		btnGlobe.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(v.getTag().toString().equalsIgnoreCase("list"))
				{
					hideKeyBoard(v);
					llgreenbar.setVisibility(View.GONE);
					llMap.setVisibility(View.VISIBLE);
					v.setTag("Map");
					v.setBackgroundResource(R.drawable.list);
				}
				else
				{
					llgreenbar.setVisibility(View.VISIBLE);
					etSearch.setText(null);
					llMap.setVisibility(View.GONE);
					v.setTag("list");
					v.setBackgroundResource(R.drawable.map);
					if(objCustomCalEventsAdapter == null)
						lvEvents.setAdapter(objCustomCalEventsAdapter = new CustomCalEventsAdapter(arrayListEvent));
					else
						objCustomCalEventsAdapter.refresh(arrayListEvent);
				}
			}
		});
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				objCustomCalEventsAdapter.refresh(arrayListEvent);
			}
		});
		etSearch.setHintTextColor(Color.WHITE);
		etSearch.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(s.toString()!=null)
				{
					ArrayList <JourneyPlanDO> vecTemp = new ArrayList<JourneyPlanDO>();
					for(int index = 0; arrayListEvent != null && index < arrayListEvent.size(); index++)
					{
						JourneyPlanDO obj 	= (JourneyPlanDO) arrayListEvent.get(index);
						String strText 		= (obj).siteName;
						String strSite 		= (obj).site;
						String strText2 	= (obj).partyName;
						String shipTo 	= (obj).Attribute3;

						if(strText.toLowerCase().contains(s.toString().toLowerCase())
								|| strSite.toLowerCase().contains(s.toString().toLowerCase())
								|| strText2.toLowerCase().contains(s.toString().toLowerCase())
								|| shipTo.toLowerCase().contains(s.toString().toLowerCase()))


							vecTemp.add(arrayListEvent.get(index));
					}
					if(vecTemp!=null)
						objCustomCalEventsAdapter.refresh(vecTemp);
				}
				else
				{
					objCustomCalEventsAdapter.refresh(arrayListEvent);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) 
			{
				
			}
			@Override
			public void afterTextChanged(Editable s) 
			{
				
			}
		});
		
		Bundle bundle = getIntent().getExtras();
		final Boolean isFromLogin = bundle != null && bundle.containsKey("isFromLogin") && bundle.getBoolean("isFromLogin");
		if(isFromLogin)
		{
		}
		
		if(btnBack != null)
			btnBack.setVisibility(View.GONE);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		setUpMap();
		tvTime.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomers.setTypeface(AppConstants.Roboto_Condensed_Bold);
		//btnAdvance.setVisibility(View.GONE);
//		btnAdvance.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
//					showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
//				else
//				{
//				}
//			}
//		});
	}
	@Override	
	protected void onResume() 
	{
		super.onResume();
		AppConstants.CheckIN = false;
		isOnResume = true;
		LogUtils.errorLog("", "6 "+isEOTDone());
//		hideKeyBoard(llMain);
		boolean isAllServed = isAllServed();
		if(etSearch!=null)
			etSearch.setText(null);
		if( isAllServed && !AppConstants.isSumeryVisited && preference.getbooleanFromPreference("isReasonGiven", false) && !isEOTDone())
		{
			showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.end_of_trip), getResources().getString(R.string.you_have_taken_five_minute_more) , getResources().getString(R.string.Yes), getResources().getString(R.string.No), "allPresellerCustomer");
			preference.saveBooleanInPreference("isAllServed", true);
			preference.saveBooleanInPreference("isReasonGiven", false);
			preference.commitPreference();
		}
		else if(isAllServed && !isEOTDone())
		{
			showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.end_of_trip), getResources().getString(R.string.you_successfully_served_all_customers), getResources().getString(R.string.Yes), getResources().getString(R.string.No), "allPresellerCustomer");
			preference.saveBooleanInPreference("isAllServed", true);
			preference.saveStringInPreference("EOTReason", "");
			preference.saveStringInPreference("EOTType", "");
			preference.commitPreference();
		}
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{	
				hmOverDue						   = new CustomerDA().getOverDueAmountNew();
//				hmOverDue						   = new CustomerDA().getOverDueAmount();
				hmCreditLimits					   = new CustomerDA().getCreditLimits();
				hmVisits 				   		   = objCustomerDetailsBL.getServedCustomerList(preference.getStringFromPreference(Preference.SALESMANCODE, ""),strCurrentDate, preference);
				vecServedCustomerWithDataNotPosted = objCustomerDetailsBL.getOrderTobePost(preference.getStringFromPreference(Preference.EMP_NO, ""));		
				loadCustomerList();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						addMarkers(arrayListEvent);
						if(objCustomCalEventsAdapter!=null)
							objCustomCalEventsAdapter.refresh(arrayListEvent);
//						mallsDetails = null;
					}
				});
			}
		}).start();
		Log.e("", "7 "+isEOTDone());
	}

	/** initializing all the Controls  of PresellerJourneyPlan class **/
	public void intialiseControls()
	{
		flContainer			= 	(ViewGroup) llMain.findViewById(R.id.flContainer);

		// Since we are caching large views, we want to keep their cache
        // between each animation
        flContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);

        llMap					=	(LinearLayout)findViewById(R.id.llMap);
        llgreenbar				=	(LinearLayout)findViewById(R.id.llgreenbar);
        llMap.setVisibility(View.GONE);
        tvNoCustomers			= (TextView)llMain.findViewById(R.id.tvNoCustomers);
		//llTimeTitle			= (LinearLayout)llMain.findViewById(R.id.llTimTitle);
		
		tvTime					= 	(TextView)llMain.findViewById(R.id.tvTime);
		tvCustomers				= 	(TextView)llMain.findViewById(R.id.tvCustomers);
		lvEvents				=	(ListView)llMain.findViewById(R.id.lvEvents);
		//tvSeprator			=	(TextView)llMain.findViewById(R.id.tvSeprator);
		//tvSortBy			=	(TextView)llMain.findViewById(R.id.tvSortBy);
		tvJourneyPlanDate		=	(TextView)llMain.findViewById(R.id.tvJourneyPlanDate);
		//tvResultOfSearch	=	(TextView)llMain.findViewById(R.id.tvResultOfSearch);
		tvJourneyPlanDateValue 	= (TextView)llMain.findViewById(R.id.tvJourneyPlanDateValue);
		btnGlobe				=	(ImageView)llMain.findViewById(R.id.btnGlobe);
		//btnTopCalIcon		=	(Button)llMain.findViewById(R.id.btnTopCalIcon);
		//btnAdvance			=	(Button)llMain.findViewById(R.id.btnAdvance);
		etSearch				=	(EditText)llMain.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llMain.findViewById(R.id.ivSearchCross);
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		
		
		etSearch.setHint(getString(R.string.seacrch_customer_new));
	   /* tvJourneyPlanDate.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
	    tvJourneyPlanDateValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	    tvResultOfSearch.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	    tvSortBy.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	    btnAdvance.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	    tvTime.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	    tvCustomers.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
	    
		//btnTopCalIcon.setVisibility(View.GONE);
		
	}
	
	public void
	loadCustomerList()
	{
		strCurrentDate 		= 	CalendarUtils.getCurrentDateAsString();
		strSelectedDate		= 	strCurrentDate;
		
		if(arrayListEvent == null || !preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(strCurrentDate))
		{
			arrayListEvent = new ArrayList<JourneyPlanDO>();
			preference.saveBooleanInPreference("isAllServed", false);
			preference.commitPreference();
		}
		
		if(!preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase("")&&!preference.getStringFromPreference(Preference.LAST_JOURNEY_DATE, "").equalsIgnoreCase(strCurrentDate) && !isOnResume)
		{
			Log.e("IS_EOT_DONE", ""+isEOTDone());
//			preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
//			preference.commitPreference();
			
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),"False");					
				}
			}).start();
		}
		
		if(arrayListEvent.size() == 0)
		{
			Calendar c = Calendar.getInstance();
//			int date = c.get(Calendar.DAY_OF_MONTH);
			long timeStamp = c.getTimeInMillis();
			String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
//			arrayListEvent = objCustomerDetailsBL.getJourneyPlan(timeStamp, CalendarUtils.getCurrentDateAsStringforStoreCheck(), day, preference.getStringFromPreference(Preference.EMP_NO, "") );
			arrayListEvent = objCustomerDetailsBL.getJourneyPlanWithDiscount(timeStamp, CalendarUtils.getCurrentDateAsStringforStoreCheck(), day, preference.getStringFromPreference(Preference.EMP_NO, "") );
			preference.saveStringInPreference(Preference.LAST_JOURNEY_DATE, strCurrentDate);
			preference.commitPreference();
		}
		
//		addMarkers(arrayListEvent);
//		if(objCustomCalEventsAdapter!=null)
//			objCustomCalEventsAdapter.refresh(arrayListEvent);
	}
	
	private boolean isAllServed()
	{
		int count = new CustomerDetailsDA().getServedCustomerCount(strSelectedDate,preference.getStringFromPreference("UserID", ""));
		if(arrayListEvent != null && count!=0 && count == arrayListEvent.size())
			return true;
		
		return false;
	}
	
	public class CustomCalEventsAdapter extends BaseAdapter 
	{
		private ArrayList<JourneyPlanDO> vecMyAdapterEvent;
		
		public CustomCalEventsAdapter(ArrayList<JourneyPlanDO> vecMyAdapterEvent)
		{
			this.vecMyAdapterEvent = vecMyAdapterEvent;
//			if(vecMyAdapterEvent.size() == 0 && flContainer != null && tvResultOfSearch != null)
//				tvResultOfSearch.setVisibility(View.VISIBLE);
//			else
//				tvResultOfSearch.setVisibility(View.GONE);
		}
		
		@Override
		public int getCount()
		{
			if(vecMyAdapterEvent != null)
				return vecMyAdapterEvent.size();
			return 0;
		}
		private void refresh()
		{
			this.notifyDataSetChanged();
		}
		private void refresh(ArrayList<JourneyPlanDO> vecMyAdapterEvent)
		{
			this.vecMyAdapterEvent = vecMyAdapterEvent;
//			if(vecMyAdapterEvent != null && vecMyAdapterEvent.size() == 0 && flContainer != null && tvResultOfSearch != null)
//				tvResultOfSearch.setVisibility(View.VISIBLE);
//			else
//				tvResultOfSearch.setVisibility(View.GONE);
		if(vecMyAdapterEvent != null && vecMyAdapterEvent.size() == 0)
		{
			tvNoCustomers.setVisibility(View.VISIBLE);
			lvEvents.setVisibility(View.GONE);
		}
		else
		{
			tvNoCustomers.setVisibility(View.GONE);
			lvEvents.setVisibility(View.VISIBLE);
		}
			this.notifyDataSetChanged();
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
	
		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			BaseComparableDO oBaseComparableDO = vecMyAdapterEvent.get(position);
		
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.callistviewcellxml_cell, null);
			
			LinearLayout llCustomers = (LinearLayout)convertView.findViewById(R.id.llCustomers);
			LinearLayout llCustomerType = (LinearLayout)convertView.findViewById(R.id.llCustomerType);
			LinearLayout llCredit	=	(LinearLayout)convertView.findViewById(R.id.llCredit);
			TextView tvSiteName		=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvSiteID		=	(TextView)convertView.findViewById(R.id.tvSiteID);
			TextView tvAddress		=	(TextView)convertView.findViewById(R.id.tvAddress);
			//TextView tvPaymentType 	= 	(TextView)convertView.findViewById(R.id.tvPaymentType);
			TextView tvCreditLimit	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
			TextView tvInOutTime	=	(TextView)convertView.findViewById(R.id.tvInOutTime);
			TextView tvDuePayment	=	(TextView)convertView.findViewById(R.id.tvDuePayment);
		    ImageView ivCustomerType =	(ImageView)convertView.findViewById(R.id.ivCustomerPaymentType);
		    ImageView ivVisited 	=	(ImageView)convertView.findViewById(R.id.ivVisited);
		    ImageView ivCustBlocked =	(ImageView)convertView.findViewById(R.id.ivCustBlocked);
		    ImageView ivCustCategory =	(ImageView)convertView.findViewById(R.id.ivCustCategory);
		    ImageView ivPerfectStore =	(ImageView)convertView.findViewById(R.id.ivPerfectStore);
		    TextView tvLastVisit	=	(TextView)convertView.findViewById(R.id.tvLastVisit);
			TextView tvNextVisit	=	(TextView)convertView.findViewById(R.id.tvNextVisit);
		    
			
			llCustomerType.setBackgroundColor(getResources().getColor(R.color.journeyplan_screen_list_left_bg));
			if(oBaseComparableDO instanceof JourneyPlanDO)
			{
				final JourneyPlanDO objMallsDetails	=	(JourneyPlanDO)oBaseComparableDO;
				
				 if(hmVisits !=null && hmVisits.containsKey(objMallsDetails.site))
				 {
					 ivVisited.setVisibility(View.VISIBLE);
					 int isGoldenStore = hmVisits.get(objMallsDetails.site);
					 if(isGoldenStore == AppConstants.GOLDEN_STORE_VAL)
						 ivPerfectStore.setVisibility(View.VISIBLE);
					 else
						 ivPerfectStore.setVisibility(View.INVISIBLE);
					 
					 convertView.setTag(R.string.app_name, isGoldenStore);
					 
				 }
				 else
				 {
					 ivVisited.setVisibility(View.INVISIBLE);
					 ivPerfectStore.setVisibility(View.INVISIBLE);
					 convertView.setTag(R.string.app_name, 0);
				 }
					
				 
				objMallsDetails.mPosition = position;
				setTypeFaceRobotoNormal((ViewGroup) convertView);
				
				setTypeFaceRobotoNormal((ViewGroup) convertView);
				
				tvSiteName.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvSiteName.setText(objMallsDetails.siteName/* + " ("+objMallsDetails.partyName+")"*/);
				
				tvAddress.setTypeface(AppConstants.Roboto_Condensed);
//				tvAddress.setText(getAddress(objMallsDetails));
				if(objMallsDetails.Attribute3!=null && !TextUtils.isEmpty(objMallsDetails.Attribute3))
					tvAddress.setText(""+objMallsDetails.Attribute3);
				else
					tvAddress.setText("");
				if(objMallsDetails.customerPaymentType != null && objMallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
				{
					llCredit.setVisibility(View.VISIBLE);
//					ivCustCategory.setVisibility(View.VISIBLE);
					ivCustomerType.setImageResource(R.drawable.credirt_bg);
					float availLimit = 0 , overDue = 0;
					
//					if(hmCreditLimits != null && hmCreditLimits.containsKey(objMallsDetails.site))
//						availLimit = StringUtils.getFloat(hmCreditLimits.get(objMallsDetails.site).availbleLimit);
					
					if(hmCreditLimits != null && hmCreditLimits.containsKey(objMallsDetails.site))
						availLimit = StringUtils.getFloat(hmCreditLimits.get(objMallsDetails.site).outStandingAmount) + 
								StringUtils.getFloat(hmCreditLimits.get(objMallsDetails.site).outStandingFoodAmount)+
								StringUtils.getFloat(hmCreditLimits.get(objMallsDetails.site).outStandingTPTAmount);

					if(hmOverDue != null && hmOverDue.containsKey(objMallsDetails.site))
						overDue = hmOverDue.get(objMallsDetails.site);
					
//					if(objMallsDetails.blockedStatus.equalsIgnoreCase("true") || availLimit <= 0 || overDue > 0 )
//					{
//						objMallsDetails.blockedStatus="true";
//						ivCustBlocked.setVisibility(View.VISIBLE);
//						convertView.setAlpha((float) 0.6);
//					}
//					else
//					{
						ivCustBlocked.setVisibility(View.INVISIBLE);
						convertView.setAlpha((float) 1.0);
//					}
					
					tvCreditLimit.setText(curencyCode+" "+amountFormate.format(availLimit));
					tvDuePayment .setText(curencyCode+" "+amountFormate.format(overDue));
//					Random r = new Random();
//					int customerCategory = r.nextInt(3 - 1) + 1;
					
					if(objMallsDetails.Attribute9.equalsIgnoreCase("Gold"))
					{
						ivCustCategory.setVisibility(View.VISIBLE);
						ivCustCategory.setBackgroundResource(R.drawable.customer_gold);
					}
					else if(objMallsDetails.Attribute9.equalsIgnoreCase("Silver"))
					{
						ivCustCategory.setVisibility(View.VISIBLE);
						ivCustCategory.setBackgroundResource(R.drawable.customer_silver);
					}
					else if(objMallsDetails.Attribute9.equalsIgnoreCase("Platinum"))
					{
						ivCustCategory.setVisibility(View.VISIBLE);
						ivCustCategory.setBackgroundResource(R.drawable.customer_platinum);
					}
					else
						ivCustCategory.setVisibility(View.GONE);
					
//					switch (position % 3) 
//					{
//					
//					case 0:
//						ivCustCategory.setBackgroundResource(R.drawable.customer_gold);
//						break;
//					case 1:
//						ivCustCategory.setBackgroundResource(R.drawable.customer_silver);
//						break;
//					case 2:
//						ivCustCategory.setBackgroundResource(R.drawable.customer_platinum);
//						break;
//
//					default:
//						ivCustCategory.setBackgroundResource(R.drawable.customer_gold);
//						break;
//					}
					
				}
				else
				{
					llCredit.setVisibility(View.GONE);
					ivCustCategory.setVisibility(View.GONE);
					ivCustomerType.setImageResource(R.drawable.cash_bg);	
				}
				tvSiteID.setText(objMallsDetails.site);
			//	tvPaymentType    .setText(objMallsDetails.customerType);
				
				String strTime = objMallsDetails.timeIn;
				if(!TextUtils.isEmpty(strTime))
					tvInOutTime.setText(strTime+"");
				else
					tvInOutTime.setText("");
				convertView.setTag(objMallsDetails);
				
				convertView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(final View arg0)
					{
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								final int isDayStarted = new JourneyPlanDA().isDayStarted(preference.getStringFromPreference(preference.USER_ID, ""));
								
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										preference.saveStringInPreference(Preference.CURRENCY_CODE, objMallsDetails.currencyCode);
										preference.commitPreference();
										
										AppConstants.SKIPPED_CUSTOMERS = "";
										if(AppConstants.skippedCustomerSitIds !=null && AppConstants.skippedCustomerSitIds.size()>0)
											AppConstants.skippedCustomerSitIds.clear();
										((LinearLayout)arg0).setClickable(false);
										final JourneyPlanDO obj	= 	(JourneyPlanDO) arg0.getTag();
										Calendar c = Calendar.getInstance();
										int date = c.get(Calendar.DAY_OF_MONTH);
										long timeStamp = c.getTimeInMillis();
										String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
										
										int count  = 	 objCustomerDetailsBL.getServedCustomerCount(obj.stop, day, obj.userID, date, timeStamp, CalendarUtils.getCurrentDateAsString());
										if(obj != null)
										{
											AppConstants.strCheckIN = objMallsDetails.siteName;
											preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, objMallsDetails.site);
											preference.saveStringInPreference(Preference.CUSTOMER_NAME, objMallsDetails.siteName);
											preference.saveStringInPreference(Preference.PAYMENT_TYPE, objMallsDetails.customerPaymentMode);
											preference.saveStringInPreference(Preference.PAYMENT_TERMS_DESC, objMallsDetails.paymentTermCode);
											preference.saveStringInPreference(Preference.SUB_CHANNEL_CODE, objMallsDetails.subChannelCode);
											preference.saveStringInPreference(Preference.CHANNEL_CODE, objMallsDetails.channelCode);
											preference.saveStringInPreference(Preference.CUSTOMER_ID, objMallsDetails.customerId);
											preference.saveMallsDetailsObjectInPreference(Preference.CUSTOMER_DETAIL, obj);
											preference.commitPreference();
											
											mallsDetailss = obj;
										}
								
//										if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
										if(isEOTDone())
										{
											showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
										}
										else 
										{
											if(!preference.getStringFromPreference("lastservedcustomer", "").equalsIgnoreCase(objMallsDetails.siteName))
											{
												if(count == 1)
												{
													if(isDayStarted > 0)
													{
														preference.saveBooleanInPreference("isItemAdded", false);
														preference.saveBooleanInPreference("isReturned", false);
														preference.commitPreference();
														
														Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
														intent.putExtra("mallsDetails", obj);
														intent.putExtra("isPerfectStore", ""+arg0.getTag(R.string.app_name));
														startActivity(intent);
													}
													else
													{
														showCustomDialog(PresellerJourneyPlan.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
													}
												}
												/*else if(AppConstants.skippedCustomerSitIds != null && AppConstants.skippedCustomerSitIds.size() > 0)
												{
													ArrayList<String> temp = new ArrayList<String>();
													for(String s : AppConstants.skippedCustomerSitIds)
													{
														if(hmVisits.containsKey(s))
														{
															temp.add(s);
														}
													}
													
//													ArrayList<String> tempName = new ArrayList<String>();
//													for(String s : AppConstants.skippedCustomerSitIds)
//													{
//														if(hmVisits.containsKey(s))
//														{
//															tempName.add(s);
//														}
//													}
													for(String s: temp)
														AppConstants.skippedCustomerSitIds.remove(s);
													
													for(String s : AppConstants.skippedCustomerSitIds)
													{
														if(!hmVisits.containsKey(s))
														{
															temp.add(s);
														}
													}
													AppConstants.skippedCustomerSitIds.clear();
													AppConstants.skippedCustomerSitIds = temp;
													
													if(preference.getIntFromPreference(preference.TEMP_REASON, 0) >= obj.stop)
													{
														
													}
													else
													{
														preference.saveIntInPreference(preference.TEMP_REASON, obj.stop);										
													}
													
													if(AppConstants.skippedCustomerSitIds.size()>0)
													{
														if(isDayStarted > 0)
														{
															preference.saveBooleanInPreference("isItemAdded", false);
															preference.saveBooleanInPreference("isReturned", false);
															preference.commitPreference();
															
															showSkipJourneyPlanPopup(getResources().getString(R.string.warning), AppConstants.SKIPPED_CUSTOMERS);
															CheckIN  =	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
															CheckIN.putExtra("mallsDetails", obj);
															CheckIN.putExtra("isCustomer", false);
															CheckIN.putExtra("strDate", strCurrentDate);
														}
														else
														{
															showCustomDialog(PresellerJourneyPlan.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
														}
													}
													else
													{
														if(isDayStarted > 0)
														{
															preference.saveBooleanInPreference("isItemAdded", false);
															preference.saveBooleanInPreference("isReturned", false);
															preference.commitPreference();
															
															Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
															intent.putExtra("mallsDetails", obj);
															intent.putExtra("isCustomer", false);
															intent.putExtra("strDate", strCurrentDate);
															startActivity(intent);
														}
														else
														{
															showCustomDialog(PresellerJourneyPlan.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
														}
													}
												}*/
												else
												{
													if(isDayStarted > 0)
													{
														preference.saveBooleanInPreference("isItemAdded", false);
														preference.saveBooleanInPreference("isReturned", false);
														preference.commitPreference();
														
														Intent intent	=	new Intent(PresellerJourneyPlan.this,SalesmanCheckIn.class);
														intent.putExtra("mallsDetails", obj);
														intent.putExtra("isCustomer", false);
														intent.putExtra("strDate", strCurrentDate);
														startActivity(intent);
													}
													else
													{
														showCustomDialog(PresellerJourneyPlan.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
													}
												}
											}
											else 
											{
												final int status = new OrderDA().getAdvenceOrderBySiteId(AppConstants.LPO_ORDER, obj.site, CalendarUtils.getOrderPostDate());
												if(status <= 0 )
												{
													if(objMallsDetails != null)
													{
														AppConstants.strCheckIN = objMallsDetails.siteName;
														preference.saveStringInPreference("CHECKED_IN_CUSTOMER_ID", objMallsDetails.site);
														preference.commitPreference();
													}
													preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, objMallsDetails.site);
													preference.saveStringInPreference(Preference.SUB_CHANNEL_CODE, objMallsDetails.subChannelCode);
													preference.saveStringInPreference(Preference.CHANNEL_CODE, objMallsDetails.channelCode);
													preference.commitPreference();
													Intent intent =	new Intent(PresellerJourneyPlan.this,  SalesManTakeOrder.class);
													intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
													intent.putExtra("mallsDetails", obj);
													startActivity(intent);
												}
												else
												{
													Intent intent = new Intent(PresellerJourneyPlan.this, SalesmanOrderList.class);
													intent.putExtra("object",obj);
													startActivity(intent);
												}
											}
										}
										new Handler().postDelayed(new Runnable()
										{
											@Override
											public void run()
											{
												((LinearLayout)arg0).setClickable(true);
											}
										}, 300);
									}
								});
							}
						}).start();
					}
				});
				if(StringUtils.getDouble(objMallsDetails.balanceAmount) <AppConstants.DIFFERENCE_BW_CREDIT_AVAILABLE)
				{
					llCustomers.setBackgroundColor( getResources().getColor(R.color.red_normal_1));
					llCustomerType.setBackgroundColor( getResources().getColor(R.color.red_normal_1));
				}
				/*else
				{
					llCustomers.setBackgroundColor( getResources().getColor(android.R.color.transparent));
					llCustomerType.setBackgroundColor( getResources().getColor(android.R.color.transparent));
				}*/
				else if(hmOverDue.containsKey(objMallsDetails.site) && hmOverDue.get(objMallsDetails.site)>0)
				{
					tvDuePayment .setText(curencyCode+" "+amountFormate.format(hmOverDue.get(objMallsDetails.site)));
					llCustomers.setBackgroundColor( getResources().getColor(R.color.yellow_bg_dark));
					llCustomerType.setBackgroundColor( getResources().getColor(R.color.yellow_bg_dark));
//					tvDuePayment.setTextColor( getResources().getColor(R.color.over_due_color));
					tvDuePayment.setTypeface(Typeface.DEFAULT_BOLD);
				}
				else
				{
					tvDuePayment.setTextColor( getResources().getColor(R.color.blue_jp));
					llCustomers.setBackgroundColor( getResources().getColor(android.R.color.transparent));
					llCustomerType.setBackgroundColor( getResources().getColor(android.R.color.transparent));
				}

			}
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));


			return convertView;	
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100)
		{
			objCustomCalEventsAdapter.refresh(arrayListEvent);
		}
	}
	
	/**
     * Setup a new 3D rotation on the container view.
     *
     * @param position the item that was clicked to show a picture, or -1 to show the list
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(int position, float start, float end)
    {
        // Find the center of the container
        final float centerX = flContainer.getWidth() / 2.0f;
        final float centerY = flContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));

        flContainer.startAnimation(rotation);
    }
	
    
    /**
     * This class listens for the end of the first half of the animation.
     * It than posts a new action that effectively swaps the views when the container
     * is rotated 90 degrees and thus invisible.
     */
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;

        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            flContainer.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }
    
    /**
     * This class is responsible for swapping the views and start the second
     * half of the animation.
     */
    private final class SwapViews implements Runnable 
    {
        private final int mPosition;

        public SwapViews(int position) 
        {
            mPosition = position;
        }

        public void run() 
        {
            final float centerX = flContainer.getWidth() / 2.0f;
            final float centerY = flContainer.getHeight() / 2.0f;
            Rotate3dAnimation rotation;
            
            if (mPosition > -1) 
            {
                lvEvents.setVisibility(View.GONE);
                llMap.setVisibility(View.VISIBLE);
                llMap.requestFocus();
                rotation = new Rotate3dAnimation(-90, 0, centerX, centerY, 310.0f, false);
            } 
            else 
            {
            	llMap.setVisibility(View.GONE);
                lvEvents.setVisibility(View.VISIBLE);
                lvEvents.requestFocus();
                rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
            }

            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            rotation.setAnimationListener(new AnimationListener() 
            {
				@Override
				public void onAnimationStart(Animation animation) 
				{	}
				
				@Override
				public void onAnimationRepeat(Animation animation)
				{	}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					if (mPosition > -1) 
					 {
						llMap.postInvalidate();
					 }
				}
			});
            flContainer.startAnimation(rotation);
        }
    }
    
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		locationUtility.stopGpsLocUpdation();
	}
	
	@Override
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			btnMenu.performClick();
		else if(popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
		else
			super.onBackPressed();
	}
	
    @Override
    public void onButtonYesClick(String from) 
    {
    	super.onButtonYesClick(from);
    	if(from.equalsIgnoreCase("allPresellerCustomer"))
    	{
    		showLoader(getResources().getString(R.string.please_wait));
    		new Thread(new Runnable()
    		{
				@Override
				public void run()
				{
					preference.saveStringInPreference("EOTReason", "N/A");
					preference.saveStringInPreference("EOTType", "Normal");
					preference.commitPreference();
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							hideLoader();
							Intent intent				 =	new Intent(PresellerJourneyPlan.this, SalesmanSummaryofDay.class);
				    		AppConstants.isSumeryVisited = true;
				    		intent.putExtra("dateofJorney", strSelectedDate);
				    		
				    		intent.putExtra("SalesmanCode", preference.getStringFromPreference(Preference.SALESMANCODE, ""));
							intent.putExtra("EmpId", preference.getStringFromPreference(Preference.EMP_NO, ""));
							intent.putExtra("isSupervisor", false);
				    		startActivityForResult(intent, 1000);
						}
					});
				}
			}).start();
    	}
    	else if(from.equalsIgnoreCase("reportsubmitted"))
    	{
    		showLoader("Sending mail...");
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
					showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.successful), "E-mail sent successfully." , getResources().getString(R.string.OK), null, "finish");
				}
			},2000);
    	}
    	else if(from.equalsIgnoreCase("finish"))
    	{
    		objTimer.cancel();
			onButtonYesClick("logout");
    	}
    	else if(from.equalsIgnoreCase("StartDay"))
		{
			Intent intent = new Intent(PresellerJourneyPlan.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			startActivity(intent);
		}
    }
    
    private class SetTimer extends CountDownTimer
    {
		public SetTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() 
		{
			showLoader("Sending email...");
			new Handler().postDelayed(new Runnable() 
			{
				@Override
				public void run() 
				{
					hideLoader();
					onButtonYesClick("logout");
				}
			},2000);
		}
		@Override
		public void onTick(long millisUntilFinished) 
		{
		}
    }
    
    @Override
    protected void onPause()
    {
    	super.onPause();
    	if(objTimer!=null)
    		objTimer.cancel();
    }
    /**
     * Method to check the Sales Order is Generated or not for the particular customer on current dateofJorney
     * @param customerName
     * @param currentDate
     * @return boolean
     */
    private boolean isOrderGenerated(String customerID , String currentDate)
    {
    	//creating object of CustomerOrderDA class 
    	CustomerOrderDA objCustomerOrderBL = new CustomerOrderDA();
    	//getting the Sales order information
    	if(objCustomerOrderBL.isCustomerOrderGenerated(customerID, currentDate))
    		return true;
    	return false;
    }
    
	@Override
	public void onConnectionException(Object msg) 
	{
	}
	
	public void showSkipJourneyPlanPopup(String title, String strMessage)
	{
		if (customDialog != null && customDialog.isShowing())
			customDialog.dismiss();

		View view 					= 	inflater.inflate(R.layout.skip_journey_plan_popup_, null);
		customDialog 				= 	new CustomDialog(PresellerJourneyPlan.this, view, preference.getIntFromPreference(AppConstants.Device_Display_Width, AppConstants.DEVICE_DISPLAY_WIDTH_DEFAULT) - 320, LayoutParams.WRAP_CONTENT, true);
		TextView tvTitle 			= 	(TextView) view.findViewById(R.id.tvHead);
		TextView tvCustomerList		= 	(TextView) view.findViewById(R.id.tvCustomerList);
		TextView tvMessage			= 	(TextView) view.findViewById(R.id.tvMessages);
		Button btnYesPopup			=   (Button) view.findViewById(R.id.btnYesPopup);	
		Button btnNoPopup			=   (Button) view.findViewById(R.id.btnNoPopup);
		
		setTypeFaceRobotoNormal((ViewGroup) view);
//		tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTitle.setText(title);
//		tvCustomerList.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerList.setText(strMessage);
//		tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnYesPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnNoPopup.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		btnYesPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(customDialog != null)
					customDialog.dismiss();
				
	    		vecReasons = new CommonDA().getReasonsByType(AppConstants.SKIP_JOURNEY_PLAN);
	    		CustomBuilder builder = new CustomBuilder(PresellerJourneyPlan.this, "Select Reason", true);
	    		builder.setSingleChoiceItems(vecReasons, -1, new CustomBuilder.OnClickListener() 
	    		{
	    			@Override
	    			public void onClick(CustomBuilder builder, Object selectedObject) 
	    			{
	    				final NameIDDo objNameIDDo = (NameIDDo) selectedObject;
	    				showLoader(getResources().getString(R.string.please_wait));
	    	    		builder.dismiss();
	    	    		new Thread(new Runnable() 
	    	    		{
							@Override
							public void run() 
							{
								for(String strSiteId :  AppConstants.skippedCustomerSitIds)
								{
									objPostReasonDO 			   = new PostReasonDO();
									objPostReasonDO.customerSiteID = strSiteId;
				    				objPostReasonDO.presellerId    = preference.getStringFromPreference(Preference.EMP_NO, "");
				    				objPostReasonDO.reason         = ""+objNameIDDo.strName;
				    				objPostReasonDO.reasonType     = ""+objNameIDDo.strType;
				    				objPostReasonDO.reasonId     	= ""+objNameIDDo.strId;
				    				objPostReasonDO.skippingDate   = CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00";
				    				vecPostReasons.add(objPostReasonDO);
								}
								if(vecPostReasons != null && vecPostReasons.size() > 0)
								{
									new CommonDA().insertAllReasons(vecPostReasons);
									
									uploadData();
								}
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										if(CheckIN != null)
					    	    		{
											hideLoader();
											showPassCodeDialog(objNameIDDo, "normal", false);
					    	    		}
									}
								});
							}
						}).start();
	    		    }
	    	   }); 
	    		builder.show();
			}
		});
		btnNoPopup.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(customDialog != null)
					customDialog.dismiss();
			}
		});
		
		if(customDialog != null && !customDialog.isShowing())
			customDialog.show();
	}
	
	private void setUpMap()
	{
		if (mapview == null) {
			mapview = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1))
					.getMap();
			if(mapview != null)
			{
				mapview.setMyLocationEnabled(true);
				mapview.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
		            @Override
		            public void onInfoWindowClick(final Marker marker)
		            {
//		            	if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		            	if(isEOTDone())
						{
							showCustomDialog(PresellerJourneyPlan.this, getResources().getString(R.string.warning),"Trip has been already ended for the day ("+CalendarUtils.getCurrentDate()+")."  , getResources().getString(R.string.OK), null, "");
						}
						else
						{
							new Thread(new Runnable()
							{
								@Override
								public void run() 
								{
									final int isDayStarted = new JourneyPlanDA().isDayStarted(preference.getStringFromPreference(preference.USER_ID, ""));
									
									runOnUiThread(new Runnable()
									{
										@Override
										public void run() 
										{
											if(isDayStarted > 0)
											{
												JourneyPlanDO mallsDetails = arrayListEvent.get(veMarkers.indexOf(marker));
												Intent intent = new Intent(PresellerJourneyPlan.this, SalesmanCheckIn.class);
												intent.putExtra("mallsDetails", mallsDetails);
												startActivity(intent);
											}
											else
											{
												showCustomDialog(PresellerJourneyPlan.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
											}
										}
									});
								}
							}).start();
						}
		            	
		            }
		        });
				
				mapview.setInfoWindowAdapter(new InfoWindowAdapter() {
					
					@Override
					public View getInfoWindow(Marker marker) {
						JourneyPlanDO mallsDetails = arrayListEvent.get(veMarkers.indexOf(marker));
						ContextThemeWrapper cw = new ContextThemeWrapper(getApplicationContext(), R.style.Transparent);
	                    // AlertDialog.Builder b = new AlertDialog.Builder(cw);
	                    LayoutInflater inflater = (LayoutInflater) cw
	                            .getSystemService(LAYOUT_INFLATER_SERVICE);
						 View v = inflater.inflate(R.layout.map_popup, null);
			             TextView tvName = (TextView) v.findViewById(R.id.tvLocationTitle);
			             TextView tvTime = (TextView) v.findViewById(R.id.tvLocationAddressLine1);
			             
			             v.setLayoutParams(new LinearLayout.LayoutParams(preference.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 720)*2/3, LayoutParams.WRAP_CONTENT));
			             tvName.setText(mallsDetails.siteName+" ["+mallsDetails.site+"]");
			             tvTime.setText(mallsDetails.addresss2 +","+ mallsDetails.addresss3);
			             return v;
					}
					@Override
					public View getInfoContents(Marker marker) {
			             return null;
					}
				});
			}
		}
	}
	
	
	private void addMarkers(final ArrayList<JourneyPlanDO> vecmallsDetails)
	{
		if(vecmallsDetails != null && vecmallsDetails.size() > 0)
		{
//			runOnUiThread(new Runnable() 
//			{
//				@Override
//				public void run()
//				{
					if(mapview != null)
					{	
						veMarkers = new Vector<Marker>();
						mapview.clear();
						if(vecmallsDetails != null && vecmallsDetails.size() > 0) {
							for (final JourneyPlanDO mallsDetails : vecmallsDetails) 
							{
								LatLng latLang = new LatLng(mallsDetails.geoCodeX,mallsDetails.geoCodeY);
//							latLang = new LatLng(StringUtils.getFloat(mallsDetails.geoCodeX), StringUtils.getFloat(mallsDetails.geoCodeY));
								Marker marker = mapview.addMarker(new MarkerOptions()
								.position(latLang)
								.title(mallsDetails.siteName+" ["+mallsDetails.site+"]")
								.snippet(mallsDetails.addresss2+", "+mallsDetails.addresss3)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
								veMarkers.add(marker);
							}
							LatLng latLang = new LatLng(vecmallsDetails.get(0).geoCodeX,vecmallsDetails.get(0).geoCodeY);
//						latLang = new LatLng(24.9500, 55.3333);
							mapview.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, 10.0f));
						}
					}
//				}
//			});
		}
	}
	
	@Override
	public void performPasscodeAction(NameIDDo objNameIDDo, String from, boolean isCheckout)
	{
		if(CheckIN != null)
			CheckIN.putExtra("reason", objNameIDDo.strName);
		startActivityForResult(CheckIN, 0);
	}
}
