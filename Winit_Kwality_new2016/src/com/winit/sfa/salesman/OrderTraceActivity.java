package com.winit.sfa.salesman;

import java.util.HashMap;
import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.ListOrderTraceFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.OrderTraceDA;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class OrderTraceActivity extends BaseActivity 
{
	private LinearLayout llDeliveryStatus;
	private TextView tvPageTitle;
	
	private TextView tvSalesOrder, tvSalesOrderCount,tvSalesOrderPrice,tvSalesOrderCurrency,
	tvReturnOrder, tvReturnOrderCount,tvReturnOrderPrice,tvReturnOrderCurrency,
	tvMissedOrderCount,tvMissedOrderPrice,tvMissedOrderCurrency;
	
	private EditText etSearch;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	private boolean isInvoice;
	private String[] tabsName = {"DRAFT","FINALIZE","CANCELLED","WH PROCESSED","DELIVERY"};
	private Preference preference;
	private int user;
	private HashMap<Integer,Vector<TrxHeaderDO>> hmDetails = new HashMap<Integer, Vector<TrxHeaderDO>>();
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llDeliveryStatus	=	(LinearLayout) inflater.inflate(R.layout.activity_order_trace,null);
		llBody.addView(llDeliveryStatus ,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llBody.setBackgroundResource(R.drawable.bg4);
		
		intializeControls();
		tvPageTitle.setText("Order Trace");
		
		loadOrderList();
		
	}
	
	/** initializing all the Controls  of SupervisorManageStaff class **/
	public void intializeControls()
	{
		tvPageTitle				=	(TextView)llDeliveryStatus.findViewById(R.id.tvPageTitle);
		preference				=   new Preference(OrderTraceActivity.this);
		
		tvSalesOrder			=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrder);
		tvSalesOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderCount);
		tvSalesOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderPrice);
		tvSalesOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvSalesOrderCurrency);
		tvReturnOrder			=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrder);
		tvReturnOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderCount);
		tvReturnOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderPrice);
		tvReturnOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvReturnOrderCurrency);
//		tvMissedOrderCount		=	(TextView)llDeliveryStatus.findViewById(R.id.tvMissedOrderCount);
//		tvMissedOrderPrice		=	(TextView)llDeliveryStatus.findViewById(R.id.tvMissedOrderPrice);
//		tvMissedOrderCurrency	=	(TextView)llDeliveryStatus.findViewById(R.id.tvMissedOrderCurrency);
		etSearch				=	(EditText)llDeliveryStatus.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llDeliveryStatus.findViewById(R.id.ivSearchCross);
		
		tabs 	 				= 	(PagerSlidingTabStrip) llDeliveryStatus.findViewById(R.id.tabs);
		pager 					= 	(ViewPager) llDeliveryStatus.findViewById(R.id.pager);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		/********************no use of user so commented***************************/
//		user = preference.getIntFromPreference(preference.SALESMAN_TYPE, preference.PRESELLER);
//		preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER);
		setTypeFaceRobotoNormal(llDeliveryStatus);
		
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(OrderTraceActivity.this);
		
ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
	}
	
	
	public void loadOrderList()
	{
		
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{

				hmDetails = new OrderTraceDA().getOrderTraceDetails();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsName);
						pager.setAdapter(adapter);
						tabs.setViewPager(pager);
						hideLoader();
					}
				});
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

		public void refresh() 
		{
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
			
			int trxStatus = 0;
			if(position == 0)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_SAVED();
			else if(position == 1)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			else if(position == 2)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_CANCELLED();
			else if(position == 3)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_WAREHOUSE();
			else if(position == 4)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_DELIVERY();
			
			return new ListOrderTraceFragment(OrderTraceActivity.this,trxStatus,hmDetails.get(trxStatus));
		}
	}
}
