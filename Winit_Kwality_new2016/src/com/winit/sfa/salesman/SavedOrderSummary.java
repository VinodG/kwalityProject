package com.winit.sfa.salesman;

import java.util.HashMap;
import java.util.Vector;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.pinch.SavedOrderFragment;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.kwalitysfa.salesman.R;

public class SavedOrderSummary extends BaseActivity 
{
	private LinearLayout llDeliveryStatus;
	private TextView tvPageTitle;
	private HashMap<Integer,Vector<TrxHeaderDO>> hmOrders;
	
	
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	
	private String[] tabNames = {"SAVED","ADVANCE","TELE"};
	private Preference preference;
	
	@Override
	public void initialize() 
	{
		llDeliveryStatus	=	(LinearLayout) inflater.inflate(R.layout.saved_order_summary,null);
		llBody.addView(llDeliveryStatus ,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llBody.setBackgroundResource(R.drawable.bg4);
		
		intializeControls();
		
		tvPageTitle.setText(getString(R.string.saved_orders));
		
		loadOrderList();
		
	}
	
	/** initializing all the Controls  of SupervisorManageStaff class **/
	public void intializeControls()
	{
		tvPageTitle				=	(TextView)llDeliveryStatus.findViewById(R.id.tvPageTitle);
		preference				=   new Preference(SavedOrderSummary.this);
		
		tabs 	 				= 	(PagerSlidingTabStrip) llDeliveryStatus.findViewById(R.id.tabs);
		pager 					= 	(ViewPager) llDeliveryStatus.findViewById(R.id.pager);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(SavedOrderSummary.this);
		
		setTypeFaceRobotoNormal(llDeliveryStatus);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	public void cancelSavedOrder(final String trxCode)
	{
		showLoader("cancelling order...");
		new Thread(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				new OrderDA().cancelSavedOrder(trxCode);
				loadOrderList();
				
				runOnUiThread(new Runnable() 
				{
					
					@Override
					public void run() {
						
						hideLoader();
						uploadData();
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
	
	public void loadOrderList()
	{
		
		showLoader(getResources().getString(R.string.loading));
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
				String empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
				hmOrders 	  = new CommonDA().getSavedOrder(empNO,mallsDetailss.site);

				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabNames);

						pager.setAdapter(adapter);
						tabs.setViewPager(pager);
						
						int trxType = 0;
						
						for(int position=0;position<tabNames.length;position++)
						{
							
							if(position == 0)
							{
								if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
									trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
								else
									trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
							}
							else if(position == 1)
								trxType = TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER();
							else if(position == 2)
								trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
							
							if(hmOrders!=null && hmOrders.get(trxType)!=null)
								tabs.setTabText(position, hmOrders.get(trxType).size());
							else
								tabs.setTabText(position,0);
						}
						
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onButtonYesClick(String from,String trxCode) 
	{

		if(from.equalsIgnoreCase("cancelorder") && !TextUtils.isEmpty(trxCode))
			cancelSavedOrder(trxCode);
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
			
			int trxType = 0;
			if(position == 0)
			{
				if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
					trxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				else
					trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
			}
			else if(position == 1)
				trxType = TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER();
			else if(position == 2)
				trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
			
			return new SavedOrderFragment(SavedOrderSummary.this,position,hmOrders.get(trxType));
		}
	}
}
