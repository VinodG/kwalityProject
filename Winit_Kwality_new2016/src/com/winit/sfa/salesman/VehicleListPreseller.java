package com.winit.sfa.salesman;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.alarm.AlaramManager;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.StoreCheckDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataaccesslayer.UserInfoDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.CoverageReport;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.PerfectStore;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.GraphImageView;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class VehicleListPreseller extends BaseActivity
{
	//Initializing and declaration of variables
	private LinearLayout llTruck_List, llLayoutMiddle, llSubheader,llMonthForfarmance,llPagerTab,llMfdToday,llTotalCollection;
	private TextView tvHead,tvMonthPerformance,tvFrequncyHeader,tvRoutePerformance,tvSchedulecallTitle, /*tvFrequncy, */tvDeliveryDateValue, tvDateOfJourney, tvNoOrderFound/*,tvFirstSunDate,tvSecondSunDate,tvThirdSunDate,tvFourthSunDate,tvTargetAchived,tvFirstSunTarget,tvSecondSunTarget,tvThirdSunTarget,tvFourthSunTarget*/;
	private ListView lvTruckList;
	private TruckListAdapter orListAdapter;
	private Vector<VehicleDO> vecTruckList;
	private int scheduledCalls=0, productiveCalls=0, actualCalls=0, zeroSalesOutlet=0;
	private String coveragePersantage = "0", sellingEfficiency= "0";
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private CustomerDetailsDA objCustomerDetailsBL;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private HashMap<String, Integer> hmVisits;
	private String CoverageHtml,perfectStoreHtml;
	private ImageView ivMtd,ivToday;
	private TextView tvSellingEfficiency,tvCovragePercentage,tvActualCalls,tvSchudeduledCalls,tvProductiveCallsPlanned,tvProductiveCallsUnplanned,
		tvZeroSalesPlanned,tvZeroSalesUnplanned,tvActualCallsPlanned,tvTotalActualCallsUnplanned, tvTotalCollection, tvTotalSales,
		tvActualCallsTitle;
	private int actualCallsScheduled;
	private int productiveCallsPlanned=0,actualCallsUnScheduled=0,productiveCallsUnPlanned=0,zeroSalesOutletPlanned=0,zeroSalesOutletunPlanned=0;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		//Inflating delivery_agent_order_list layout to show the Trucks list
		llTruck_List = (LinearLayout)getLayoutInflater().inflate(R.layout.delivery_agent_order_list_new, null);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		objCustomerDetailsBL 	=   new CustomerDetailsDA();
		hmVisits = new HashMap<String, Integer>();	
		//function for getting id's and setting type-faces
		initializeLayout();
		llLayoutMiddle.addView(lvTruckList , LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		llBody.addView(llTruck_List, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		setTypeFaceRobotoNormal(llTruck_List);
		loadData();
		btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
		
		//need to remove
		llMenu.setVisibility(View.VISIBLE);
		btnMenu.setVisibility(View.VISIBLE);
		//need to remove
		new AlaramManager(getApplicationContext()).removeAllReminder();
		
	ivMtd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				ivMtd.setImageResource(R.drawable.mtd_select);
				ivToday.setImageResource(R.drawable.today_unselect);
				
				tvSchedulecallTitle.setText("Total Scheduled \nCalls");
				
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						loadOrderData(false);
						
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								tvSchudeduledCalls.setText(""+scheduledCalls);
								tvTotalSales.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[0])));
								tvTotalCollection.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[1])));
								tvProductiveCallsPlanned.setText(""+productiveCallsPlanned);
								tvZeroSalesPlanned.setText(""+zeroSalesOutletPlanned);
								tvProductiveCallsUnplanned.setText(""+productiveCallsUnPlanned);
								tvZeroSalesUnplanned.setText(""+zeroSalesOutletunPlanned);
								tvCovragePercentage.setText(""+coveragePersantage);
							    tvSellingEfficiency.setText(""+sellingEfficiency);
								tvActualCallsPlanned.setText(""+actualCallsScheduled);
								tvTotalActualCallsUnplanned.setText(""+actualCallsUnScheduled);
							}
						});
					}
				}).start();
			}
		});
		
		ivToday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				ivMtd.setImageResource(R.drawable.mtd_unselect);
				ivToday.setImageResource(R.drawable.today_select);
				
				tvSchedulecallTitle.setText("Today's Scheduled \nCalls");
				
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						loadOrderData(true);
						
						runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								tvSchudeduledCalls.setText(""+scheduledCalls);
								tvTotalSales.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[0])));
								tvTotalCollection.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[1])));
								tvProductiveCallsPlanned.setText(""+productiveCallsPlanned);
								tvZeroSalesPlanned.setText(""+zeroSalesOutletPlanned);
								tvProductiveCallsUnplanned.setText(""+productiveCallsUnPlanned);
								tvZeroSalesUnplanned.setText(""+zeroSalesOutletunPlanned);
								tvCovragePercentage.setText(""+coveragePersantage);
							    tvSellingEfficiency.setText(""+sellingEfficiency);
								tvActualCallsPlanned.setText(""+actualCallsScheduled);
								tvTotalActualCallsUnplanned.setText(""+actualCallsUnScheduled);
							}
						});
					}
				}).start();
			}
		});
//		new AlaramManager(getApplicationContext()).saveMorningReminder(AppConstants.ALARM_TIME_LOAD_NEW_REQ_1);
//		new AlaramManager(getApplicationContext()).saveMorningReminder(AppConstants.ALARM_TIME_LOAD_NEW_REQ_2);
		
	}
	
	/** initializing all the Controls  of DeliveryAgentTruckList class **/
	public void initializeLayout()
	{
		//getting id's
		tvHead 				= 	(TextView)llTruck_List.findViewById(R.id.tvHead);
		tvDeliveryDateValue	= 	(TextView)llTruck_List.findViewById(R.id.tvDeliveryDateValue);
		tvDateOfJourney		= 	(TextView)llTruck_List.findViewById(R.id.tvDateOfJourney);
		tvNoOrderFound		= 	(TextView)llTruck_List.findViewById(R.id.tvNoOrderFound);
		llLayoutMiddle 		=	(LinearLayout)llTruck_List.findViewById(R.id.llLayoutMiddle);
		llSubheader			=	(LinearLayout)llTruck_List.findViewById(R.id.llDateSelector);
		llMonthForfarmance  =   (LinearLayout) llTruck_List.findViewById(R.id.llMonthForfarmance);
		tvMonthPerformance  =   (TextView) llTruck_List.findViewById(R.id.tvMonthPerformance);
		tvFrequncyHeader	=   (TextView) llTruck_List.findViewById(R.id.tvFrequncyHeader);
		tvRoutePerformance	=   (TextView) llTruck_List.findViewById(R.id.tvRoutePerformance);
		llPagerTab 			=   (LinearLayout) llTruck_List.findViewById(R.id.llPagerTab);
		pager 				= (ViewPager) llTruck_List.findViewById(R.id.pager);
		llMfdToday          =   (LinearLayout)llTruck_List.findViewById(R.id.llMfdToday);
		ivMtd               =   (ImageView)llTruck_List.findViewById(R.id.ivMtd);
		ivToday             =   (ImageView)llTruck_List.findViewById(R.id.ivToday);
		tvMonthPerformance.setTypeface(AppConstants.Roboto_Condensed);
		if(preference.getStringFromPreference(Preference.USER_NAME, "") != null && !preference.getStringFromPreference(Preference.USER_NAME, "").equalsIgnoreCase(""))
			tvRoutePerformance.setText("Hi " +preference.getStringFromPreference(Preference.USER_NAME, "")+",");
		
		tvFrequncyHeader.setTypeface(AppConstants.Roboto_Condensed);
		tvRoutePerformance.setTypeface(AppConstants.Roboto_Condensed);
		tvDeliveryDateValue.setText("Vehicle List");
		tvDeliveryDateValue.setVisibility(View.GONE);
		orListAdapter 		= 	new TruckListAdapter(new Vector<VehicleDO>());
		lvTruckList 		= 	new ListView(VehicleListPreseller.this);
		lvTruckList.setFadingEdgeLength(0);
		lvTruckList.setDivider(null);
		lvTruckList.setSelector(R.color.transparent);
		lvTruckList.setVerticalScrollBarEnabled(false);
		lvTruckList.setAdapter(orListAdapter);
		SetRoutePerformance(0, 20000, 13000);
		//Thread to load the data 
	}
	
	/*setting route and month performance of Salesman*/
	private void SetRoutePerformance(int initial, int target, int achived) 
	{
		//int [] targetAcheivement;
		try
		{
//			targetAcheivement = new UserInfoDA().getUserTargetAndAcheive();
//			graphImageView.setUpCustomIV();
//			Calendar c = Calendar.getInstance();
//			int targetValue = (targetAcheivement[0]/targetAcheivement[2])* c.get(Calendar.DAY_OF_MONTH);
//			graphImageView.moveNeedle(0, targetAcheivement[0],  targetAcheivement[1], targetValue);
//			graphImageView.setVisibility(View.VISIBLE);
//			llMonthForfarmance.setVisibility(View.VISIBLE);
//			
//			if(targetAcheivement[0] == 0)
//			{
//				graphImageView.setVisibility(View.GONE);
//				llMonthForfarmance.setVisibility(View.GONE);
//			}
			
//			int fisrtTarget,secondTarget,thirdTarget,FourthTarget;
//			int maxx		= (target*121)/100;
//			fisrtTarget 	= target/4;
//			secondTarget 	= fisrtTarget*2;
//			thirdTarget 	= fisrtTarget*3;
//			FourthTarget 	= target;
//			DecimalFormat decimalFormat = new DecimalFormat("##,##,###.##");
//			decimalFormat.setMinimumFractionDigits(2);
//			decimalFormat.setMaximumFractionDigits(7);
//			tvFirstSunTarget.setText(decimalFormat.format(fisrtTarget) );
//			tvFirstSunTarget.setText(""+fisrtTarget);
//			tvSecondSunTarget.setText(decimalFormat.format(secondTarget) );
//			tvSecondSunTarget.setText(""+secondTarget);
//			tvThirdSunTarget.setText(decimalFormat.format(thirdTarget) );
//			tvThirdSunTarget.setText(""+thirdTarget);
//			tvFourthSunTarget.setText(decimalFormat.format(FourthTarget) );
//			tvFourthSunTarget.setText(""+FourthTarget);
//			tvTargetAchived.setText(decimalFormat.format(achived) );
//			tvTargetAchived.setText(""+achived);
//			customProgressBar.moveProgress(initial, target, achived, maxx);
			
//			tvFirstSunDate.setText("04th Sep");
//			tvSecondSunDate.setText("11th Sep");
//			tvThirdSunDate.setText("18th Sep");
//			tvFourthSunDate.setText("25th Sep");
		}
		catch(Exception e)
		{
//			graphImageView.setVisibility(View.GONE);
//			llMonthForfarmance.setVisibility(View.GONE);
			e.printStackTrace();
		}
	}

	/**
	 * Adapter class for the List view
	 */
	public class TruckListAdapter extends BaseAdapter
	{
		//vector to get the main vector
		private Vector<VehicleDO> vecTruckList;
		//constructor for the TruckListAdapter class having Vector parameter
		public TruckListAdapter(Vector<VehicleDO> vecTruckList) 
		{
			this.vecTruckList = vecTruckList;
		}

		@Override
		public int getCount() 
		{
			return vecTruckList.size();
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
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			//getting the current Object
			VehicleDO objTrucks  	= 	 vecTruckList.get(position);
			//inflating the mng_stf_cell Layout
			convertView 			=	 (LinearLayout)getLayoutInflater().inflate(R.layout.mng_stf_cell, null);
			//getting Id's from mng_stf_cell Layout
			TextView tvOerder		= 	 (TextView)convertView.findViewById(R.id.tvMngStfName);
			TextView tvOerderId		= 	 (TextView)convertView.findViewById(R.id.tvMngStfMemberId);
			TextView tvTodayDate	= 	 (TextView)convertView.findViewById(R.id.tvTodayDate);
			//setting text
			if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
			{
				tvOerder.setText("Start Day");
				tvOerderId.setText("");
			}
			else
			{
				tvOerder.setText("Vehicle No: ");
				tvOerderId.setText(""+objTrucks.VEHICLE_NO);
			}
			Calendar c 	= 	Calendar.getInstance();
		    int year 	= 	c.get(Calendar.YEAR);
		    int month 	= 	c.get(Calendar.MONTH);
		    int day 	=	c.get(Calendar.DAY_OF_MONTH);
		    
		    tvTodayDate.setText(CalendarUtils.getMonthAsString(month)+" "+day+CalendarUtils.getDateNotation(day)+" "+year);
			//setting the Type-face
			tvOerder.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvOerderId.setTypeface(AppConstants.Roboto_Condensed_Bold);
			tvTodayDate.setTypeface(AppConstants.Roboto_Condensed);
			
			
			//click event for convertView
			convertView.setTag(objTrucks);
			convertView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					//getting the object of NameIDDo class by Tag
					final VehicleDO objTrucks 	=  (VehicleDO) v.getTag();
					
					new Thread(new Runnable()
					{
						@Override
						public void run() 
						{
//							String rout_Code = new CommonDA().getRouteCode(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
//							objTrucks.ROUTE  = rout_Code;
							preference.saveStringInPreference(Preference.ROUTE_CODE, objTrucks.ROUTE);
							preference.commitPreference();
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() 
								{
									if(new VehicleDA().isAnyItemAvail(CalendarUtils.getOrderPostDate()) <= 0 || new VehicleDA().isAnyItemToVerify(CalendarUtils.getOrderPostDate()))
									{
//										if(preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false)/* || preference.getbooleanFromPreference(Preference.IsStockVerified, false)*/)
										if(isEOTDone())
										{
											Intent intent = new Intent(VehicleListPreseller.this, PresellerJourneyPlan.class);
											intent.putExtra("Latitude", 25.522);
											intent.putExtra("Longitude", 78.522);
											preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
											preference.commitPreference();
											startActivity(intent);
										}
										else if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
										{
											Intent intent = new Intent(VehicleListPreseller.this, PresellerJourneyPlan.class);
											intent.putExtra("Latitude", 25.522);
											intent.putExtra("Longitude", 78.522);
											preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
											preference.commitPreference();
											startActivity(intent);
										}
										else
										{
											preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
											Intent intent = new Intent(VehicleListPreseller.this, AddStockInVehicle.class);
											preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
											preference.commitPreference();
											intent.putExtra("object", objTrucks);
											startActivity(intent);
										}
									}
									else if(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0) <= 0)
									{
										preference.saveBooleanInPreference(Preference.IS_VANSTOCK_FROM_MENU_OPTION, false);
										Intent intent = new Intent(VehicleListPreseller.this, AddStockInVehicle.class);
										preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
										preference.commitPreference();
										intent.putExtra("object", objTrucks);
										startActivity(intent);
									}
									else
									{
										Intent intent = new Intent(VehicleListPreseller.this, PresellerJourneyPlan.class);
										intent.putExtra("Latitude", 25.522);
										intent.putExtra("Longitude", 78.522);
										preference.saveStringInPreference(Preference.CURRENT_VEHICLE, objTrucks.VEHICLE_NO);
										preference.commitPreference();
										startActivity(intent);
									}
								}
							});
						}
					}).start();
				}
			});
			//setting LayoutParams to  convertView (List Cell)
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			return convertView;	
		}
		/**
		 * Method to refresh the List View
		 * @param vecTruckList
		 */
		public void refresh(Vector<VehicleDO> vecTruckList)
		{
			this.vecTruckList = vecTruckList;
			notifyDataSetChanged();
			
			if(this.vecTruckList != null && this.vecTruckList.size() > 0)
				tvNoOrderFound.setVisibility(View.GONE);
			else
				tvNoOrderFound.setVisibility(View.VISIBLE);
		}
	}
	
	public void onBackPressed() 
	{
		if(llDashBoard != null && llDashBoard.isShown())
			btnMenu.performClick();
		else 
			btnLoginLogout.performClick();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		if(btnBack!=null)
			btnBack.setVisibility(View.GONE);
		getpieChartData();
		if(vecTruckList == null || vecTruckList.size()==0)
		{
			loadData();
		}
		setUserPerformanceAdapter();
		
		if(pager != null)
		{
			int postion = pager.getCurrentItem();
			if(postion==0)
			{
				tvMonthPerformance.setText("Month Performance");
				llMfdToday.setVisibility(View.GONE);					
			}
			else if(postion==1)
			{
				llMfdToday.setVisibility(View.VISIBLE);
				tvMonthPerformance.setText("Coverage and Productivity");
			}
		}
	}
	
	private void loadData() 
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				//to get the Truck list to be delivered
				vecTruckList = 	new VehicleDA().getTruckListByDelievryAgentId(preference.getStringFromPreference(Preference.EMP_NO, ""), CalendarUtils.getOrderPostDate());
				vecTruckList.add(new VehicleDO());
				//getValue()
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
					    orListAdapter.refresh(vecTruckList);
						hideLoader();
					}
				});
			}
		}).start();
	}
	private void setUserPerformanceAdapter()
	{
		adapter = new MyPagerAdapter();
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(5);
		refreshPageController();
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
	
		pager.setPageMargin(pageMargin);
		pager.setEnabled(false);
		pager.setClickable(false);
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int postion) 
			{
				
				for (int i = 0; i <= (adapter.getCount()-1); i++) 
				{
					((ImageView)llPagerTab.getChildAt(i)).setImageResource(R.drawable.pager_dot);
					Log.d("debug_networks", "i"+i);
				}
				((ImageView)llPagerTab.getChildAt(postion)).setImageResource(R.drawable.pager_dot_h);
				Log.d("debug_networks", "postion"+postion);
				if(postion==0)
				{
					tvMonthPerformance.setText("Month Performance");
					llMfdToday.setVisibility(View.GONE);	
				}
				else if(postion==1)
				{
					tvMonthPerformance.setText("Coverage and Productivity");
					llMfdToday.setVisibility(View.VISIBLE);	
				}
				else if(postion==2)
				{
					tvMonthPerformance.setText("Coverage Report");
					pager.requestChildFocus(pager.getChildAt(postion), pager.getChildAt(postion).findViewById(R.id.webView1));
					llMfdToday.setVisibility(View.GONE);	
				}
				else if(postion==3)
				{
					tvMonthPerformance.setText("Perfect Store");
					pager.requestChildFocus(pager.getChildAt(postion), pager.getChildAt(postion).findViewById(R.id.webView1));
					llMfdToday.setVisibility(View.GONE);	
				}
				else if(postion==4)
				{
					tvMonthPerformance.setText("Bar Chart");
					llMfdToday.setVisibility(View.GONE);	
				}
				else if(postion==5)
				{
					tvMonthPerformance.setText("Line Chart");
					llMfdToday.setVisibility(View.GONE);	
				}
			}
			
			@Override
			public void onPageScrolled(int postion, float arg1, int arg2) 
			{
			}
			
			@Override
			public void onPageScrollStateChanged(int postion) 
			{
			}
		});
	}
	private class MyPagerAdapter extends PagerAdapter 
	{

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object instantiateItem(final View collection, final int position)
		{
            LinearLayout llFlightStatusContent = getProductCatlogView(position);
            ((ViewPager) collection).addView(llFlightStatusContent, position);
            return llFlightStatusContent;
		}

		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((LinearLayout) view);
        }



		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view == ((LinearLayout) object);
		}
	}
	private float targetValue = 0;
	private float [] targetAcheivement;
	private GraphImageView graphImageView;
	@SuppressLint({ "SetJavaScriptEnabled", "CutPasteId" })
	private LinearLayout getProductCatlogView(int position)
	{
		LinearLayout llCatLOgView = null;
		switch (position) {
		case 0:
			llCatLOgView 				= (LinearLayout) getLayoutInflater().inflate(R.layout.monthperformance, null);
			setTypeFaceRobotoBold(llCatLOgView);
			graphImageView 				=   (GraphImageView)llCatLOgView. findViewById(R.id.imageView);
			TextView tvAchived  		= (TextView) llCatLOgView.findViewById(R.id.tvAchived);
			TextView tvProjected 		= (TextView) llCatLOgView.findViewById(R.id.tvProjected);
			TextView tvShortFall 		= (TextView) llCatLOgView.findViewById(R.id.tvShortFall);
			TextView tvTarget	 		= (TextView) llCatLOgView.findViewById(R.id.tvTarget);
			
			TextView tvFoodAchived 		= (TextView) llCatLOgView.findViewById(R.id.tvFoodAchived);
			TextView tvFoodShortFall 	= (TextView) llCatLOgView.findViewById(R.id.tvFoodShortFall);
			TextView tvFoodProjected 	= (TextView) llCatLOgView.findViewById(R.id.tvFoodProjected);
			TextView tvFoodTarget	 	= (TextView) llCatLOgView.findViewById(R.id.tvFoodTarget);

			TextView tvTPB	 	= (TextView) llCatLOgView.findViewById(R.id.tvTPB);
			TextView tvTPBAchived	 	= (TextView) llCatLOgView.findViewById(R.id.tvTPBAchived);
			TextView tvTPBShortFall	 	= (TextView) llCatLOgView.findViewById(R.id.tvTPBShortFall);
			TextView tvTPBProjected	 	= (TextView) llCatLOgView.findViewById(R.id.tvTPBProjected);
			
			try
			{
				/*********************Ice Cream******************************/
				targetAcheivement = new UserInfoDA().getUserTargetAndAcheive(preference.getStringFromPreference(Preference.USER_ID, ""));
				if(targetAcheivement[3]==0)
					targetAcheivement[3] =CalendarUtils.getNumberODaysInMonth(); 
				targetValue			 = (targetAcheivement[0] - targetAcheivement[1])/(targetAcheivement[3]-targetAcheivement[2]);
				
				/*tvAchived.setText(decimalFormat.format(targetAcheivement[1]));
				tvProjected.setText(decimalFormat.format(targetAcheivement[4]));*/
				tvAchived.setText(""+StringUtils.getInt(""+targetAcheivement[1]));
				tvProjected.setText(""+StringUtils.getInt(""+targetAcheivement[4]));
				
				float shortFall=targetAcheivement[0]-targetAcheivement[1];
				shortFall=shortFall>0?shortFall:0;
				
				/*tvShortFall.setText(decimalFormat.format(shortFall));
				tvTarget.setText(decimalFormat.format(targetAcheivement[0]));*/
				tvShortFall.setText(""+StringUtils.getInt(""+shortFall));
				tvTarget.setText(""+StringUtils.getInt(""+targetAcheivement[0]));
				
				float maxSalesPerDay = targetAcheivement[0]/targetAcheivement[3];
				float currentSalesRate = targetAcheivement[1]/targetAcheivement[3] >= targetValue ? targetValue : targetAcheivement[1]/targetAcheivement[2];
				float dailyTarget = targetValue < maxSalesPerDay ? maxSalesPerDay : targetValue;
				
				/*********************Food******************************/
				targetValue			 = (targetAcheivement[5] - targetAcheivement[6])/(targetAcheivement[3]-targetAcheivement[2]);
				
				/*tvFoodAchived.setText(decimalFormat.format(targetAcheivement[5]));
				tvFoodProjected.setText(decimalFormat.format(targetAcheivement[7]));*/
				tvFoodAchived.setText(""+StringUtils.getInt(""+targetAcheivement[6]));
				tvFoodProjected.setText(""+StringUtils.getInt(""+targetAcheivement[7]));
				
				float shortFallFood=targetAcheivement[5]-targetAcheivement[6];
				shortFallFood=shortFallFood>0?shortFallFood:0;
				
				/*tvFoodShortFall.setText(decimalFormat.format(shortFallFood));
				tvFoodTarget.setText(decimalFormat.format(targetAcheivement[5]));*/
				tvFoodShortFall.setText(""+StringUtils.getInt(""+shortFallFood));
				tvFoodTarget.setText(""+StringUtils.getInt(""+targetAcheivement[5]));

				tvTPB.setText(""+StringUtils.getInt(""+targetAcheivement[10]));
				tvTPBAchived.setText(""+StringUtils.getInt(""+targetAcheivement[11]));
				tvTPBShortFall.setText(""+StringUtils.getInt(""+(targetAcheivement[10]-targetAcheivement[11])));
				float tpbPerDay = (targetAcheivement[10] - targetAcheivement[11])/(targetAcheivement[3]-targetAcheivement[2]);
				tvTPBProjected.setText(""+StringUtils.getInt(""+(tpbPerDay )));
				
				animateNeedle();
				graphImageView.setVisibility(View.VISIBLE);
				llMfdToday.setVisibility(View.GONE);
			}
			catch(Exception e)
			{
				graphImageView.setVisibility(View.GONE);
				e.printStackTrace();
			}
			break;
		case 1:
			
			llCatLOgView 					= (LinearLayout) getLayoutInflater().inflate(R.layout.coverage_productivity, null);
			setTypeFaceRobotoBold(llCatLOgView);
			llTotalCollection				= (LinearLayout)llCatLOgView. findViewById(R.id.llTotalCollection);
			tvSellingEfficiency    			= (TextView)llCatLOgView. findViewById(R.id.tvSellingEfficiency);
			tvCovragePercentage 	    	= (TextView)llCatLOgView. findViewById(R.id.tvCovragePercentage);
			tvTotalSales 					= (TextView)llCatLOgView. findViewById(R.id.tvTotalSales);
			tvTotalCollection 				= (TextView)llCatLOgView. findViewById(R.id.tvTotalCollection);
			tvActualCalls 					= (TextView)llCatLOgView. findViewById(R.id.tvActualCalls);
//			tvProductiveCalls 		= (TextView)llCatLOgView. findViewById(R.id.tvProductiveCalls);
			tvSchudeduledCalls 	    		= (TextView)llCatLOgView. findViewById(R.id.tvSchudeduledCalls);
			tvProductiveCallsPlanned 		= (TextView)llCatLOgView. findViewById(R.id.tvProductiveCallsPlanned);
			tvProductiveCallsUnplanned		= (TextView)llCatLOgView. findViewById(R.id.tvProductiveCallsUnplanned);
			tvZeroSalesPlanned 				= (TextView)llCatLOgView. findViewById(R.id.tvZeroSalesPlanned);
			tvZeroSalesUnplanned 			= (TextView)llCatLOgView. findViewById(R.id.tvZeroSalesUnplanned);
			tvSchedulecallTitle            	= (TextView)llCatLOgView. findViewById(R.id.tvSchedulecallTitle);
			tvActualCallsTitle             	= (TextView)llCatLOgView. findViewById(R.id.tvActualCallsTitle);
//			tvActualCallsPlannedTitle      = (TextView)llCatLOgView. findViewById(R.id.tvActualCallsPlannedTitle);
//			tvActualCallsUnplannedTitle    = (TextView)llCatLOgView. findViewById(R.id.tvActualCallsUnplannedTitle);
			
			tvActualCallsPlanned			=	(TextView) llCatLOgView.findViewById(R.id.tvActualCallsPlanned);
			tvTotalActualCallsUnplanned 	=	(TextView) llCatLOgView.findViewById(R.id.tvTotalActualCallsUnplanned);
			
			llMfdToday.setVisibility(View.VISIBLE);
			llTotalCollection.setVisibility(View.INVISIBLE);
			
			new Thread(new Runnable()
			{
				@Override
				public void run() 
				{
					loadOrderData(true);
					
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							tvSchudeduledCalls.setText(""+scheduledCalls);
							tvTotalSales.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[0])));
							tvTotalCollection.setText(""+decimalFormat.format(StringUtils.getDouble(totalSaleColl[1])));
							tvProductiveCallsPlanned.setText(""+productiveCallsPlanned);
							tvZeroSalesPlanned.setText(""+zeroSalesOutletPlanned);
							tvProductiveCallsUnplanned.setText(""+productiveCallsUnPlanned);
							tvZeroSalesUnplanned.setText(""+zeroSalesOutletunPlanned);
							tvCovragePercentage.setText(""+coveragePersantage);
						    tvSellingEfficiency.setText(""+sellingEfficiency);
							tvActualCallsPlanned.setText(""+actualCallsScheduled);
							tvTotalActualCallsUnplanned.setText(""+actualCallsUnScheduled);
						}
					});
				}
			}).start();
			
			break;
			case 2:
				llCatLOgView = (LinearLayout) getLayoutInflater().inflate(R.layout.fusion_chart, null);
				final WebView pieChartWebView 	= (WebView)llCatLOgView. findViewById(R.id.webView1);
				pieChartWebView.getSettings().setJavaScriptEnabled(true);
//				String pieChartHTMLCode = getPieChartDataValues();
     			pieChartWebView.loadDataWithBaseURL(null, perfectStoreHtml, "text/html", "utf8", null);
				//pieChartWebView.loadUrl("file:///android_asset/FusionCharts/Pie3D1_Perfect_store.html");
     			llMfdToday.setVisibility(View.GONE);
				break;
				
			case 3:
				llCatLOgView = (LinearLayout) getLayoutInflater().inflate(R.layout.agencywise_performance, null);
				LinearLayout llBarChart =  (LinearLayout) llCatLOgView.findViewById(R.id.llBarChart);
				
//				XYMultipleSeriesRenderer renderer = getTruitonBarRenderer();
//				myChartSettings(renderer);
//				View view = ChartFactory.getBarChartView(VehicleList.this, getTruitonBarDataset(), renderer, Type.DEFAULT);
//				llBarChart.addView(view);
			    
		default:
			break;
			
		}
		return llCatLOgView;
	}
	
	private void animateNeedle()
	{
		if(targetAcheivement != null)
		{
//			float needlePosition = targetAcheivement[1] + targetAcheivement[6];
//			if(needlePosition > (targetAcheivement[0] + targetAcheivement[5]))
//				needlePosition = (targetAcheivement[0] + targetAcheivement[5]);
//			graphImageView.moveNeedle(0, (targetAcheivement[0] + targetAcheivement[5]), needlePosition, (targetAcheivement[0] + targetAcheivement[5]));

			float needlePosition = targetAcheivement[1] + targetAcheivement[6]+targetAcheivement[11];
			if(needlePosition > (targetAcheivement[0] + targetAcheivement[5]+ targetAcheivement[10]))
				needlePosition = (targetAcheivement[0] + targetAcheivement[5]+ targetAcheivement[10]);
			/*
			 * if Achieved is Negative
			 */
			if(needlePosition<0)
				needlePosition=0;
			graphImageView.moveNeedle(0, (targetAcheivement[0] + targetAcheivement[5]+ targetAcheivement[10]), needlePosition, (targetAcheivement[0] + targetAcheivement[5]+ targetAcheivement[10]));
		}
//		if(targetAcheivement != null)
//		{
//			float needlePosition = targetAcheivement[1];
//			if(needlePosition > targetAcheivement[0])
//				needlePosition = targetAcheivement[0];
//			graphImageView.moveNeedle(0, targetAcheivement[0], needlePosition, targetAcheivement[0]);
//		}
	}
	
	private void getpieChartData()
	{
		int Totalcount=1,visitedcount=0;
		Calendar c = Calendar.getInstance();
		long timeStamp = c.getTimeInMillis();
		int normalstore=0;
		int date = c.get(Calendar.DAY_OF_MONTH);
		String strCurrentDate;
		int goldenvalue=0;
		int perfectcount=0,unperfectcount=0;
		PerfectStore perfectStore;
		CoverageReport	coverageReport;
		String day = CalendarUtils.getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
		arrayListEvent =new ArrayList<JourneyPlanDO>();
		strCurrentDate 			= 	CalendarUtils.getCurrentDateAsString();
		coverageReport =new CoverageReport();
		perfectStore  = new PerfectStore(); 
		
		arrayListEvent=	objCustomerDetailsBL.getJourneyPlan(timeStamp, CalendarUtils.getCurrentDateAsStringforStoreCheck(), day, preference.getStringFromPreference(Preference.EMP_NO, "") );
		if(arrayListEvent!=null&&arrayListEvent.size()>0)
			Totalcount=arrayListEvent.size();
		
		hmVisits 				   		= objCustomerDetailsBL.getServedCustomerList(preference.getStringFromPreference(Preference.SALESMANCODE, ""),strCurrentDate, preference);
		
		if(hmVisits!=null&&hmVisits.size()>0)
			visitedcount=hmVisits.size();
		
		
	//	visitedcount=15;
	//	Totalcount=25;
	//Regions	
		coverageReport.visitstorecount=visitedcount;
		coverageReport.unvisitstorecount=Totalcount-visitedcount;
		
		coverageReport.visitcount      =  (visitedcount*100)/Totalcount;
		
		coverageReport.unvisitcount    = ((Totalcount-visitedcount)*100)/Totalcount;
		
		
		
		CoverageHtml=getDataCoverage(coverageReport);
		
		goldenvalue=new StoreCheckDA().getGoldenstoreCount();
		
		if(hmVisits!=null&&hmVisits.size()>0)
		{
			Set<String> hmkey = hmVisits.keySet();
			for(String key:hmkey)
			{
				int count =new StoreCheckDA().getVisitvalue(key,CalendarUtils.getOrderPostDate());
				
				if(count==0)
					unperfectcount++;
				else if(count>=goldenvalue)
					perfectcount++;
				else
					unperfectcount++;
			}
		}
		
	//	perfectcount=2;
		
		
		normalstore=Totalcount-perfectcount;
		
		perfectStore.perfectstorecount=perfectcount;
		perfectStore.unperfectStorecount=normalstore;
		
		float count=1.0f;
		
//		perfectcount=10;
//		unperfectcount=12;
				
		count=perfectcount+normalstore;
		if(count==0)
			count=1;
		
		perfectStore.perfectStore=(perfectcount*100)/(count);
		perfectStore.unperfectStore=(normalstore*100)/(count);
		
		perfectStoreHtml=getDataPerfectStore(perfectStore);
	}
	
	private void refreshPageController() 
	{
		int pagerPosition = 0;
		llPagerTab.removeAllViews();
		for (int i = 0; i <= (adapter.getCount()-1); i++)
		{
			final ImageView imgvPagerController = new ImageView(VehicleListPreseller.this);
			imgvPagerController.setPadding(2,2,2,2);
			
			imgvPagerController.setImageResource(R.drawable.pager_dot);
			
			
			llPagerTab.addView(imgvPagerController);
		}		
		
		pagerPosition = pager.getCurrentItem();
		
		if(((ImageView)llPagerTab.getChildAt(pagerPosition)) != null)
			((ImageView)llPagerTab.getChildAt(pagerPosition)).setImageResource(R.drawable.pager_dot_h);
		
	}
	
	/**
	 * Sample Data for Bar Graph
	 */
	String[] arr = { "Sunsilk", "Panteen", "Detol", "Lux", "Lifeboy" };
	
	private String getPieChartValuesLine()
	{
		StringBuffer stringBuffer = new StringBuffer();
		
		Random r = new Random();
		
		for(int i = 0; i < arr.length ; i++)
		{
			int num = r.nextInt(200000 - 10000) + 10000;
			stringBuffer.append("<set label=\"Item "+i+"\" value=\""+num+"\" isSliced=\"0\" />");
		}
		stringBuffer.append("<styles>");
		stringBuffer.append("<definition>");
		stringBuffer.append("<style type=\"font\" name=\"CaptionFont\" color=\"666666\" size=\"15\" />");
		stringBuffer.append("<style type=\"font\" name=\"SubCaptionFont\" bold=\"0\" />");
		stringBuffer.append("</definition>");
		stringBuffer.append("<application>");
		stringBuffer.append("<apply toObject=\"caption\" styles=\"CaptionFont\" />");
		stringBuffer.append("<apply toObject=\"SubCaption\" styles=\"SubCaptionFont\" />");
		stringBuffer.append("</application>");
		stringBuffer.append("</styles>");
		
		return stringBuffer.toString();
	}
	
	private String getBarChartValuesLine() 
	{
		StringBuffer stringBuffer = new StringBuffer();

		// Categories
		stringBuffer.append("<categories>");
		
		for (int i = 0; i < arr.length; i++) 
		{
			stringBuffer
			.append("<category Label =\'")
			.append(arr[i])
			.append("\'/>");
		}
		
		stringBuffer.append("</categories>");

		// Left values
		stringBuffer
				.append("<dataset seriesName=\'Orders\' color=\'#666666\'>");

		for (int i = 0; i < arr.length; i++) 
		{
			Random r = new Random();
			int num = r.nextInt(100 - 10) + 10;

			stringBuffer.append("<set  value=\'").append(num)
					.append("\' color=\'").append("#009900").append("\' />");

		}
		stringBuffer.append("</dataset>");
		
		// Removing Loading Animation From Bottom to Top 
		
		stringBuffer.append("<styles>");
		
		stringBuffer.append("<definition>"+
					        "<style name=\'MyXScaleAnim\' type=\'animation\' duration=\'1\' start=\'0\' param=\'\' easing =\'Bounce\' />"+
					        "</definition>");
		
		stringBuffer.append("<application>"+
						     "<apply toObject=\'Plot\' styles=\'MyXScaleAnim\' />"+
						     "</application>");
		
		stringBuffer.append("</styles>");

		stringBuffer.append("<styles>");
		stringBuffer
				.append("<definition>"
						+ "<style name=\'myCaptionFont\' type=\'font\' font=\'Arial\' size=\'16\' color=\'666666\' bold=\'1\' underline=\'0\'/>"
						+ "</definition>");

		stringBuffer.append("<application>"
				+ "<apply toObject=\'Caption\' styles=\'myCaptionFont\' />"
				+ "</application>");
		stringBuffer.append("</styles>");
		
		return stringBuffer.toString();
	}

	
	private String getBarChartDataValues() 
	{
		String str = "<html>"
				+ "<head>"
				+ "<script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/FusionCharts.js\"></script></head> <body> <div id=\"chartContainer\">Bar Charts will load here!</div><script type=\"text/javascript\"> var myChart = new FusionCharts( \"ScrollColumn2D\",\"myChartId\", \"560\",\"340\", \"0\", \"0\" );"
				+ "myChart.setXMLData(\"<chart  animation=\'1\' baseFontSize='12' YAxisMaxValue='150'  labelDisplay='ROTATE' caption=\'Order Status \' xAxisName=\'\' yAxisName=\'\' showValues=\'1\' numberPrefix=\'\' useRoundEdges=\'1\'  suseroundedges=\'1\'  >"
				+ getBarChartValuesLine() + "</chart>\");" 
				+ "myChart.render(\"chartContainer\");</script>" + "</body>"
				+ "<html>";
		return str;
	}
	
	
		
	//*******************************************pie chart*************************************
	
	private String getDataCoverage(CoverageReport coverageReport) {

		// Pie 3D Chart numberSuffix="p.a"
		String xmlData = "<chart animation=\'1\' showValues=\'0\' numberSuffix=\'%\' formatNumberScale=\'0\' palette=\'2\' outCnvBaseFontSize='15' outCnvBaseFontColor='333333'>"
				+ "<set label=\'\' value=\'"
				+ coverageReport.visitcount
				+ "\' isSliced=\'0\' color=\'"
				+ "#68C0A0"
				+ "\'/>"
				+ "<set label=\'\' value=\'"
				+ coverageReport.unvisitcount
				+ "\' isSliced=\'0\' color=\'"
				+ "#F8C898"
				+ "\'/>"
				+ "</chart>";

		String test = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ " <head>\n"
				+ " <link href=\"file:///android_asset/FusionCharts/utils/style.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
				+ " <link href=\"file:///android_asset/FusionCharts/utils/prettify.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
				+ " <script language=\"JavaScript\" src=\"file:///android_asset/FusionCharts/JS/FusionCharts.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/jquery-1.4.2.min.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/prettify.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/json2.js\"></script>\n"
				+ "\n"
				+ " <!--[if IE 6]>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/DD_belatedPNG_0.0.8a-min.js\"></script>\n"
				+ " <script>\n"
				+ " /* select the element name, css selector, background etc */\n"
				+ " DD_belatedPNG.fix(\'img\');\n"
				+ "\n"
				+ " /* string argument can be any CSS selector */\n"
				+ " </script>\n"
				+ "\t\t <p>&nbsp;</p>\n"
				+ "\t\t <P align=\"center\"></P>\n"
				+ " <![endif]-->\n"
				+ " </head> \n"
				+ "\n"
				+ "<body>\n"
				+ "<p>&nbsp;</p>\n"
				+ "<div id=\"chartdiv\" align=\"center\"> \n"
				+ " FusionCharts. </div>\n"
				+

				"<script type=\"text/javascript\">\n"
				+ "var chart = new FusionCharts(\"file:///android_asset/FusionCharts/SWF/Pie3D.swf\", \"ChartId\", \"540\", \"250\", \"0\", \"0\");\n"
				+ "chart.setXMLData(\" "
				+ xmlData
				+ " \");"
				+
				// "\t\t chart.setXMLUrl(\"file:///android_asset/FusionCharts/Data/Pie3D.xml\");\t\t \n"+

				"\t\t chart.render(\"chartdiv\");\n"
				+ "\t\t</script>\n"
				+ "\t\t <!-- <p>&nbsp;</p> -->\n"
				+ "\t\t <!-- <P align=\"center\">3D Pie chart showing top 5 employees for 1996. Click on a pie slice or its legend item to highlight it or use right-click option.</P> \n"
				+ " <br/> -->\n"
				+ "<!-- <div class=\"qua-button-holder\">\n"
				+ " <a class=\"qua qua-button view-chart-data\" href=\"javascript:void(0)\"><span>View XML</span></a>\n"
				+ " <a class=\"qua qua-button view-chart-data\" href=\"javascript:void(0)\"><span>View JSON</span></a>\n"
				+ " </div>\n"
				+ " \n"
				+ " <div class=\"show-code-block\">\n"
				+ " <div class=\"show-code-close-btn\"><a class=\"qua qua-button\" href=\"javascript:void(0)\"><span>Close</span></a></div>\n"
				+ " <pre class=\"prettyprint\"></pre>\n"
				+ " </div> -->\n"
				+ "<br/>"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"margin: 0px 0px 0px 100px;\">\n"
				+ "<div class=\"MainDiv\" style=\"width: 200px; height: 150px; float: left;\">\n<div class=\"Box\" style=\"width: 25px; height: 25px; background:#68C0A0; float: left; margin: 0px 5px;\">\n</div>\n<label style=\"width: auto; float: left; font-size: 13px; font-family: arial;margin-top:5px;\">\nCovered Customer - "
				+ coverageReport.visitstorecount
				+ "</label>\n</div>"
				+ " </div>\n"
				+ "<div style=\"margin: 0px 0px 0px 0px;\">\n"
				+ "<div class=\"MainDiv\" style=\"width: 200px; height: 150px; float: left;\">\n<div class=\"Box\" style=\"width: 25px; height: 25px; background:#F8C898; float: left; margin: 0px 5px;\">\n</div>\n<label style=\"width: auto; float: left; font-size: 13px; font-family: arial;margin-top:5px;\">\nNon Covered Customer - "
				+ coverageReport.unvisitstorecount
				+ "</label>\n</div>"
				+ "</div>\n" + " </body>\n" + " </html>\n" + "";

		return test;
	}
	

	private String getDataPerfectStore(PerfectStore perfectStore) {

		String xmlData = "";

		if (perfectStore != null & perfectStore.perfectStore == 0
				&& perfectStore.unperfectStore == 0) {

			xmlData = "<chart animation=\'1\' showValues=\'0\' numberSuffix=\'%\' formatNumberScale=\'0\' palette=\'2\' outCnvBaseFontSize='15' outCnvBaseFontColor='333333'>"
					+ "<set label=\'\' value=\'"
					+ 0
					+ "\' isSliced=\'0\' color=\'"
					+ "#009900"
					+ "\'/>"
					+ "<set label=\'\' value=\'"
					+ 1
					+ "\' isSliced=\'0\' color=\'"
					+ "#FFFF33"
					+ "\'/>"
					+ "</chart>";
		} else {
			xmlData = "<chart animation=\'1\' showValues=\'0\' numberSuffix=\'%\' formatNumberScale=\'0\' palette=\'2\' outCnvBaseFontSize='15' outCnvBaseFontColor='333333'>"
					+ "<set label=\'\' value=\'"
					+ perfectStore.perfectStore
					+ "\' isSliced=\'0\' color=\'"
					+ "#009900"
					+ "\'/>"
					+ "<set label=\'\' value=\'"
					+ perfectStore.unperfectStore
					+ "\' isSliced=\'0\' color=\'"
					+ "#FFFF33"
					+ "\'/>"
					+ "</chart>";
		}
		String test = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ " <head>\n"
				+ " <link href=\"file:///android_asset/FusionCharts/utils/style.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
				+ " <link href=\"file:///android_asset/FusionCharts/utils/prettify.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
				+ " <script language=\"JavaScript\" src=\"file:///android_asset/FusionCharts/JS/FusionCharts.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/jquery-1.4.2.min.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/prettify.js\"></script>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/json2.js\"></script>\n"
				+ "\n"
				+ " <!--[if IE 6]>\n"
				+ " <script type=\"text/javascript\" src=\"file:///android_asset/FusionCharts/JS/DD_belatedPNG_0.0.8a-min.js\"></script>\n"
				+ " <script>\n"
				+ " /* select the element name, css selector, background etc */\n"
				+ " DD_belatedPNG.fix(\'img\');\n"
				+ "\n"
				+ " /* string argument can be any CSS selector */\n"
				+ " </script>\n"
				+ "\t\t <p>&nbsp;</p>\n"
				+ "\t\t <P align=\"center\"></P>\n"
				+ " <![endif]-->\n"
				+ " </head> \n"
				+ "\n"
				+ "<body>\n"
				+ "<p>&nbsp;</p>\n"
				+ "<div id=\"chartdiv\" align=\"center\"> \n"
				+ " FusionCharts. </div>\n"
				+

				"<script type=\"text/javascript\">\n"
				+ "var chart = new FusionCharts(\"file:///android_asset/FusionCharts/SWF/Pie3D.swf\", \"ChartId\", \"300\", \"220\", \"0\", \"-160\");\n"
				+ "chart.setXMLData(\" "
				+ xmlData
				+ " \");"
				+
				// "\t\t chart.setXMLUrl(\"file:///android_asset/FusionCharts/Data/Pie3D.xml\");\t\t \n"+

				"\t\t chart.render(\"chartdiv\");\n"
				+ "\t\t</script>\n"
				+ "\t\t <!-- <p>&nbsp;</p> -->\n"
				+ "\t\t <!-- <P align=\"center\">3D Pie chart showing top 5 employees for 1996. Click on a pie slice or its legend item to highlight it or use right-click option.</P> \n"
				+ " <br/> -->\n"
				+ "<!-- <div class=\"qua-button-holder\">\n"
				+ " <a class=\"qua qua-button view-chart-data\" href=\"javascript:void(0)\"><span>View XML</span></a>\n"
				+ " <a class=\"qua qua-button view-chart-data\" href=\"javascript:void(0)\"><span>View JSON</span></a>\n"
				+ " </div>\n"
				+ " \n"
				+ " <div class=\"show-code-block\">\n"
				+ " <div class=\"show-code-close-btn\"><a class=\"qua qua-button\" href=\"javascript:void(0)\"><span>Close</span></a></div>\n"
				+ " <pre class=\"prettyprint\"></pre>\n"
				+ " </div> -->\n"
				+ "<br/>"
				+ "<br/>"
				+ "<br/>"
				+ "<div style=\"margin: 0px 0px 0px 0px;\">\n"
				+ "<div class=\"MainDiv\" style=\"width: 150px; height: 60px; float: left;\">\n<div class=\"Box\" style=\"width: 25px; height: 25px; background:#009900; float: left; margin:-30px 5px 0px 0px;\">\n</div>\n<label style=\"width: auto; float: left; font-size: 13px; font-family: arial;margin-top:-25px;margin-left:30px;\">\nPerfect Stores - "
				+ perfectStore.perfectstorecount
				+ "</label>\n</div>"
				+ " </div>\n"
				+ "<div style=\"margin: 0px 0px 0px 0px;\">\n"
				+ "<div class=\"MainDiv\" style=\"width: 150px; height: 60px; float: left;\">\n<div class=\"Box\" style=\"width: 25px; height: 25px; background:#FFFF33; float: left; margin:-30px 15px 0px 0px;\">\n</div>\n<label style=\"width: auto; float: left; font-size: 13px; font-family: arial;margin-top:-25px;margin-left:30px;\">\nOther Stores - "
				+ perfectStore.unperfectStorecount
				+ "</label>\n</div>"
				+ " </div>\n" + " </body>\n" + " </html>\n" + "";

		return test;
	}
	
	private String[] totalSaleColl;
	private void loadOrderData(boolean today) 
	{

		TrxLogHeaders trxHeaderLog = new TrxLogHeaders();
		TransactionsLogsDA objTrxTransactionsLogsDA = new TransactionsLogsDA(); 
		if(today)
			trxHeaderLog = objTrxTransactionsLogsDA.getCurrentMonthDetails("","",1);
		else
			trxHeaderLog = objTrxTransactionsLogsDA.getCurrentMonthDetails("","",0);
			
		totalSaleColl = objTrxTransactionsLogsDA.getTotalSalesCollection(today);
		
		
		
			scheduledCalls 				=  trxHeaderLog.TotalScheduledCalls;
			actualCalls					=  trxHeaderLog.TotalActualCalls;
			actualCallsScheduled 		=  trxHeaderLog.TotalActualCallsPlanned;
			productiveCalls				=  trxHeaderLog.TotalProductiveCalls;
			productiveCallsPlanned 		=  trxHeaderLog.TotalProductiveCallsPlanned;
			actualCallsUnScheduled		=  actualCalls-actualCallsScheduled ;
			productiveCallsUnPlanned	=  productiveCalls -productiveCallsPlanned;
			zeroSalesOutlet				=  actualCalls - productiveCalls;
			productiveCallsUnPlanned	=  productiveCalls-productiveCallsPlanned ;
			zeroSalesOutletPlanned		=  actualCallsScheduled-productiveCallsPlanned ;
			zeroSalesOutletunPlanned	=  zeroSalesOutlet-zeroSalesOutletPlanned ;
			
			coveragePersantage = getPerValue(actualCallsScheduled + "", scheduledCalls + "");
			sellingEfficiency = getPerValue(productiveCallsPlanned + "", actualCallsScheduled + "");
		
		LogUtils.debug("values_check", "scheduledCalls:"+scheduledCalls);
		LogUtils.debug("values_check", "productiveCalls:"+productiveCalls);
		LogUtils.debug("values_check", "actualCallsScheduled:"+actualCallsScheduled);
		LogUtils.debug("values_check", "actualCallsUnScheduled:"+actualCallsUnScheduled);
		LogUtils.debug("values_check", "zeroSalesOutlet:"+zeroSalesOutlet);
		LogUtils.debug("values_check", "actualCalls:"+actualCalls);
		LogUtils.debug("values_check", "coveragePersantage:"+coveragePersantage);
		LogUtils.debug("values_check", "sellingEfficiency:"+sellingEfficiency);
	}
	
	public String getPerValue(String firstValue, String LastValue)
	{
		String output = "0";
		try 
		{
			NumberFormat formatter = NumberFormat.getNumberInstance();
			formatter.setMinimumFractionDigits(2);
			formatter.setMaximumFractionDigits(2);
			if(!firstValue.equalsIgnoreCase("0"))
			{
				if( StringUtils.getFloat(LastValue) <= 0)
					LastValue = "1";
				
				output = formatter.format(StringUtils.getFloat(firstValue)/ StringUtils.getFloat(LastValue) * 100);
				
				if(output.contains("100"))
					output = "100";
			}
		} 
		catch (Exception e) 
		{
			output = "0";
		}
		
		output = output.replace("NaN", "0");
		output = output.length() > 5 ? output.substring(0,5):output;
		
		return output.endsWith(".") == true ? output.substring(0,output.length()-1):output;
	}
}

