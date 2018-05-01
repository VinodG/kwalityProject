package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("DefaultLocale") 
public class SalesmanCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearch;
	private String strCurrentDate = "";
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private boolean isHistory;
	private HashMap<String, CustomerCreditLimitDo> hmCreditLimits;
	private HashMap<String, Float> hmOverDue;
	private ArrayList<String> vecServedCustomerWithDataNotPosted;
	private HashMap<String, Integer> hmVisits;
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras() != null)
			isHistory = getIntent().getExtras().getBoolean("isHistory");
		
		intialiseControls();
		strCurrentDate 			= 	CalendarUtils.getCurrentDateAsString();
		hmVisits = new HashMap<String, Integer>();	
		setTypeFaceRobotoNormal(llCusomerList);
		tvListViewHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvListViewHeader.setText("My Customers");
		
		objCustomerDetailsBL = new CustomerDetailsDA();
		loadCustomerList();
		lvCustomerList.setCacheColorHint(0);
		lvCustomerList.setFadingEdgeLength(0);
		lvCustomerList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvCustomerList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
		adapter = new CustomerListAdapter(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(adapter);
		
		lvCustomerList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, final View view, int arg2,long arg3) 
			{
				hideKeyBoard(etSearch);
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
									JourneyPlanDO mallsDetailsScreen = (JourneyPlanDO)view.getTag();
									Intent intent	=	new Intent(SalesmanCustomerList.this,SalesmanCheckIn.class);
									intent.putExtra("isPerfectStore", ""+view.getTag(R.string.app_name));
									intent.putExtra("mallsDetails", mallsDetailsScreen);
									
									mallsDetailss = mallsDetailsScreen;
									startActivity(intent);
								}
								else
								{
									showCustomDialog(SalesmanCustomerList.this, getString(R.string.warning), "Your day haven't started yet.\nDo you wish to start your day?", getString(R.string.Yes), getString(R.string.No), "StartDay");
								}
							}
						});
					}
				}).start();
			}
		});
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				showAllData();
			}
		});

		etSearch.setHint(getString(R.string.seacrch_customer_new));
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		etSearch.setHintTextColor(Color.WHITE);
		//functionality for the search edit text
		etSearch.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(!TextUtils.isEmpty(s))
					ivSearchCross.setVisibility(View.VISIBLE);
				else
					ivSearchCross.setVisibility(View.GONE);
				if(!s.toString().equalsIgnoreCase(""))
				{
					ArrayList<JourneyPlanDO> arrayTemp = new ArrayList<JourneyPlanDO>();
					for(int index = 0; arrayListEvent != null && index < arrayListEvent.size(); index++)
					{
						JourneyPlanDO obj 	= (JourneyPlanDO) arrayListEvent.get(index);
						String strText 		= (obj).siteName;
						String strText1 	= (obj).site;
						String strText2 	= (obj).partyName;
						String shipTo 	= (obj).Attribute3;

						if(strText.toLowerCase().contains(s.toString().toLowerCase()) 
								|| strText1.toLowerCase().contains(s.toString().toLowerCase())
								|| strText2.toLowerCase().contains(s.toString().toLowerCase())
								|| shipTo.toLowerCase().contains(s.toString().toLowerCase()))
							arrayTemp.add(obj);
					}
					
					if(arrayTemp != null && arrayTemp.size() > 0)
					{
						adapter.refresh(arrayTemp);
						tvNoRecorFound.setVisibility(View.GONE);
						lvCustomerList.setVisibility(View.VISIBLE);
					}
					else
					{
						tvNoRecorFound.setVisibility(View.VISIBLE);
						lvCustomerList.setVisibility(View.GONE);
					}
				}
				else
				{
					showAllData();
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{}
			@Override
			public void afterTextChanged(Editable s) 
			{}
		});
	}
	
	private void showAllData()
	{
		if(arrayListEvent != null && arrayListEvent.size() > 0)
		{
			adapter.refresh(arrayListEvent);
			tvNoRecorFound.setVisibility(View.GONE);
			lvCustomerList.setVisibility(View.VISIBLE);
		}
		else
		{
			tvNoRecorFound.setVisibility(View.VISIBLE);
			lvCustomerList.setVisibility(View.GONE);
		}
	}
	
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		tvListViewHeader	= (TextView)llCusomerList.findViewById(R.id.tvListViewHeader);
		lvCustomerList 		= (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearch		= (EditText)llCusomerList.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llCusomerList.findViewById(R.id.ivSearchCross);
		tvNoRecorFound		= (TextView)llCusomerList.findViewById(R.id.tvNoRecorFound);
	
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoRecorFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		etSearch.setHint(getResources().getString(R.string.search_by_item_code_shiftto));
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public void loadCustomerList()
	{
		hmOverDue						   = new CustomerDA().getOverDueAmountNew();
		arrayListEvent = new ArrayList<JourneyPlanDO>();
		showLoader("Loading customers...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
//				arrayListEvent = objCustomerDetailsBL.getJourneyPlanForTeleOrder(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
				arrayListEvent = objCustomerDetailsBL.getJourneyPlanForTeleOrderWithDiscount(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(adapter!=null)
							adapter.refresh(arrayListEvent);
						hideLoader();
					}
				});
			}
		}).start();
	}

	public class CustomerListAdapter extends BaseAdapter
	{
		ArrayList<JourneyPlanDO> arrListCustomers;
		public CustomerListAdapter(ArrayList<JourneyPlanDO> arrayListEvent)
		{
			arrListCustomers = arrayListEvent;
		}
		@Override
		public int getCount() 
		{
			if(arrListCustomers != null)
				return arrListCustomers.size();
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

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			JourneyPlanDO mallsDetails = (JourneyPlanDO) arrListCustomers.get(position);
			
			if(convertView == null)
				convertView = (LinearLayout)inflater.inflate(R.layout.callistviewcellxml_cell, null);
			
			LinearLayout llCredit	=	(LinearLayout)convertView.findViewById(R.id.llCredit);
			LinearLayout llCustomers	=	(LinearLayout)convertView.findViewById(R.id.llCustomers);
			LinearLayout llCustomerCode = (LinearLayout)convertView.findViewById(R.id.llCustomerCode);
			TextView tvInOutTime	= 	(TextView)convertView.findViewById(R.id.tvInOutTime);
			TextView tvSiteName		=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvSiteID		=	(TextView)convertView.findViewById(R.id.tvSiteID1);
			TextView tvAddress      =   (TextView)convertView.findViewById(R.id.tvAddress);
//			TextView tvPaymentType 	= 	(TextView)convertView.findViewById(R.id.tvPaymentType);
			TextView tvCreditLimit	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
//			TextView tvInOutTime	=	(TextView)convertView.findViewById(R.id.tvInOutTime);
			LinearLayout llCustomerType =	(LinearLayout)convertView.findViewById(R.id.llCustomerType);
			TextView tvDuePayment	=	(TextView)convertView.findViewById(R.id.tvDuePayment);
			ImageView ivCustomerType	=	(ImageView)convertView.findViewById(R.id.ivCustomerPaymentType);
			ImageView ivCustCategory	=	(ImageView)convertView.findViewById(R.id.ivCustCategory);
//			ImageView ivPerfectStore 	=	(ImageView)convertView.findViewById(R.id.ivPerfectStore);
			ImageView ivVisited 	=	(ImageView)convertView.findViewById(R.id.ivVisited);
			ImageView ivCustBlocked =	(ImageView)convertView.findViewById(R.id.ivCustBlocked);
//			TextView tvLastVisit	=	(TextView)convertView.findViewById(R.id.tvLastVisit);
//			TextView tvNextVisit	=	(TextView)convertView.findViewById(R.id.tvNextVisit);
			View codemargin	        =	(View)convertView.findViewById(R.id.codemargin);
			
			setTypeFaceRobotoNormal(parent);
			tvSiteName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvAddress.setTypeface(AppConstants.Roboto_Condensed_Bold);
//			tvSiteID.setTypeface(AppConstants.Roboto_Condensed);
//			tvAddress.setTypeface(AppConstants.Roboto_Condensed);
//			tvCreditLimit.setTypeface(AppConstants.Roboto_Condensed);
			
//			llInOutTime.setVisibility(View.GONE);
//			ivCustCategory.setVisibility(View.GONE);
			llCustomerCode.setVisibility(View.GONE);
			llCustomerType.setVisibility(View.VISIBLE);
			tvInOutTime.setVisibility(View.GONE);
			tvSiteID.setVisibility(View.VISIBLE);
			codemargin.setVisibility(View.VISIBLE);
			tvSiteName.setText(mallsDetails.siteName/* + " ("+mallsDetails.partyName+")"*/);
			if(mallsDetails.Attribute3!=null && !TextUtils.isEmpty(mallsDetails.Attribute3))
				tvAddress.setText(""+mallsDetails.Attribute3);
			else
				tvAddress.setText("");
//				tvAddress.setText(getAddress(mallsDetails));
//			Random r = new Random();
//			int last = r.nextInt(7 - 1) + 1;
//			int next = r.nextInt(7 - 1) + 1;
//			Calendar c 	= 	Calendar.getInstance();
//			int year 	= 	c.get(Calendar.YEAR);
//			int month  	= 	c.get(Calendar.MONTH);
//			int day 	=	c.get(Calendar.DAY_OF_MONTH);

			/**
			 * Default Settings.
			 */
			ivVisited.setVisibility(View.INVISIBLE);
//			ivPerfectStore.setVisibility(View.INVISIBLE);
			
			
			// LogUtils.infoLog("SalesCustomerList", "Malls Site : "+mallsDetails.site); /** Sample Value : 5735 */
			
			 if(hmVisits !=null && hmVisits.containsKey(mallsDetails.site))
			 {
				 /**
				 * After Clearing the Data, it seems, we are not entering this
				 * if Block. Thats why we are losing the Visit & Perfect Store
				 * Data. i.e. hmVisits is null or it doesn't contain the Site as
				 * Key in the HaspMap.
				 */
				 ivVisited.setVisibility(View.VISIBLE);
				 // LogUtils.infoLog("Visited", "Malls Site : "+mallsDetails.site);
				 /**
				  * Type of Call value is matched with 1.
				  * If equal the Mall Store is a Golden Store, or
				  * Perfect Store.
				  */
//				 int isGoldenStore = hmVisits.get(mallsDetails.site);
//				 if(isGoldenStore == AppConstants.GOLDEN_STORE_VAL){
//					 ivPerfectStore.setVisibility(View.VISIBLE);
//					 // LogUtils.infoLog("Perfect Store", "Malls Site : "+mallsDetails.site);	 
//				 }
//				 else
//					 ivPerfectStore.setVisibility(View.INVISIBLE);
//				 convertView.setTag(R.string.app_name, isGoldenStore);
			 }
			 else
			 {
				 ivVisited.setVisibility(View.INVISIBLE);
				 convertView.setTag(R.string.app_name, 0);
			 }
			
			if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			{
				ivCustomerType.setImageResource(R.drawable.cash_bg);
				ivCustomerType.setVisibility(View.VISIBLE);
				llCredit.setVisibility(View.GONE);
				tvCreditLimit.setText("N/A");
			}
			else
			{
				ivCustomerType.setImageResource(R.drawable.credirt_bg);
				ivCustomerType.setVisibility(View.VISIBLE);
				llCredit.setVisibility(View.VISIBLE);
				float availLimit = 0 , overDue = 0;
				
//				if(hmCreditLimits != null && hmCreditLimits.containsKey(mallsDetails.site))
//					availLimit = StringUtils.getFloat(hmCreditLimits.get(mallsDetails.site).availbleLimit);
				if(hmCreditLimits != null && hmCreditLimits.containsKey(mallsDetails.site))
					availLimit = StringUtils.getFloat(hmCreditLimits.get(mallsDetails.site).outStandingAmount) +
							StringUtils.getFloat(hmCreditLimits.get(mallsDetails.site).outStandingFoodAmount)+
							StringUtils.getFloat(hmCreditLimits.get(mallsDetails.site).outStandingTPTAmount);

				if(hmOverDue != null && hmOverDue.containsKey(mallsDetails.site))
					overDue = hmOverDue.get(mallsDetails.site);
//				if(mallsDetails.blockedStatus.equalsIgnoreCase("true") || availLimit <= 0 || overDue > 0 )
//				{
//					mallsDetails.blockedStatus="true";
//					ivCustBlocked.setVisibility(View.VISIBLE);
//					convertView.setAlpha((float) 0.6);
//				}
//				else
//				{
					ivCustBlocked.setVisibility(View.INVISIBLE);
					convertView.setAlpha((float) 1.0);
//				}
				
				tvCreditLimit.setText(curencyCode+" "+amountFormate.format(availLimit));
				tvDuePayment .setText(curencyCode+" "+amountFormate.format(overDue));
				
				if(mallsDetails.Attribute9.equalsIgnoreCase("Gold"))
				{
					ivCustCategory.setVisibility(View.VISIBLE);
					ivCustCategory.setBackgroundResource(R.drawable.customer_gold);
				}
				else if(mallsDetails.Attribute9.equalsIgnoreCase("Silver"))
				{
					ivCustCategory.setVisibility(View.VISIBLE);
					ivCustCategory.setBackgroundResource(R.drawable.customer_silver);
				}
				else if(mallsDetails.Attribute9.equalsIgnoreCase("Platinum"))
				{
					ivCustCategory.setVisibility(View.VISIBLE);
					ivCustCategory.setBackgroundResource(R.drawable.customer_platinum);
				}
				else
					ivCustCategory.setVisibility(View.GONE);
			}
			

		
			
			tvSiteID     	 .setText(mallsDetails.site+"");
//			tvPaymentType    .setText(mallsDetails.customerType+"");
		
			convertView.setTag(mallsDetails);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			setTypeFaceRobotoNormal((ViewGroup) convertView);
//			If Customer's CreditLimit - AvailableLimit <=100 Then show this customer as Red background under My Customers and JP screen.
			if(  StringUtils.getDouble(mallsDetails.balanceAmount) <=AppConstants.DIFFERENCE_BW_CREDIT_AVAILABLE)
//			if(  StringUtils.getDouble(mallsDetails.balanceAmount) <=AppConstants.DIFFERENCE_BW_CREDIT_AVAILABLE
//					&&(hmOverDue.containsKey(mallsDetails.site) && hmOverDue.get(mallsDetails.site)>0))
			{
				llCustomers.setBackgroundColor( getResources().getColor(R.color.red_normal_1));
			}
//			else
//			{
//				llCustomers.setBackgroundColor( getResources().getColor(android.R.color.transparent));
//			}
			else if(hmOverDue.containsKey(mallsDetails.site) && hmOverDue.get(mallsDetails.site)>0)
			{
				tvDuePayment .setText(curencyCode+" "+amountFormate.format(hmOverDue.get(mallsDetails.site)));
//				tvDuePayment.setTextColor( getResources().getColor(R.color.over_due_color));
				llCustomers.setBackgroundColor( getResources().getColor(R.color.yellow_bg_dark));
			}
			else
			{
				tvDuePayment.setTextColor( getResources().getColor(R.color.blue_jp));
				llCustomers.setBackgroundColor( getResources().getColor(android.R.color.transparent));
			}


			
			return convertView;
		}
		private void refresh()
		{
			this.notifyDataSetChanged();
		}
		private void refresh(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			arrListCustomers = arrayListEvent;
			if(arrListCustomers != null && arrListCustomers.size() > 0)
			{
				tvNoRecorFound.setVisibility(View.GONE);
				lvCustomerList.setVisibility(View.VISIBLE);
			}
			else
			{
				tvNoRecorFound.setVisibility(View.VISIBLE);
				lvCustomerList.setVisibility(View.GONE);
			}
			this.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{	
//				hmOverDue						   = new CustomerDA().getOverDueAmount();
				hmOverDue						   = new CustomerDA().getOverDueAmountNew();
				hmCreditLimits					   = new CustomerDA().getCreditLimits();
				/**
				 * hmVisits is generated here using the DB got from Server.
				 * Table : tblCustomerVisit : Distinct {ClientCode, TypeOfCall}
				 */
				hmVisits 				   		= objCustomerDetailsBL.getServedCustomerList(preference.getStringFromPreference(Preference.SALESMANCODE, ""),strCurrentDate, preference);
				vecServedCustomerWithDataNotPosted = objCustomerDetailsBL.getOrderTobePost(preference.getStringFromPreference(Preference.EMP_NO, ""));		
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
//						loadCustomerList();
						/**
						 * Refresh of the Adapter after the
						 * generation of hmVisits.
						 */
						if(adapter!=null)
							adapter.refresh();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if(etSearch != null)
									etSearch.setText("");
//								mallsDetails = null;
							}
						}, 100);
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("StartDay"))
		{
			Intent intent = new Intent(SalesmanCustomerList.this, OdometerReadingActivity.class);
			intent.putExtra("isStartDay", true);
			intent.putExtra("customerlist", true);
			startActivity(intent);
		}
	}
	
	@Override
	public void onButtonNoClick(String from) 
	{
		if(from.equalsIgnoreCase("StartDay"))
		{
			
		}
	}
}
