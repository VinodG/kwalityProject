package com.winit.sfa.salesman;

import java.util.HashMap;
import java.util.Vector;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.parsers.GetTrxHeaderForApp;
import com.winit.alseer.pinch.ListOrderFragment;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.viewpager.extensions.PagerSlidingTabStrip;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class ReturnOrderStatusActivity extends BaseActivity 
{
	private LinearLayout llDeliveryStatus;
	private Button btnAddRequest,btnRefresh;
	private HashMap<Integer,Vector<TrxHeaderDO>> hmOrders;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private CategoryPagerAdapter adapter;
	
	private String[] tabsName = {"PENDING","APPROVED","COLLECTED","REJECTED"};
	private Preference preference;
	private TextView tvAgenciesName;
	private String empNO;
	
	@Override
	public void initialize() 
	{
		llDeliveryStatus	=	(LinearLayout) inflater.inflate(R.layout.activity_return_order_status,null);
		llBody.addView(llDeliveryStatus ,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llBody.setBackgroundResource(R.drawable.bg4);
		
		intializeControls();
		loadReturnOrderList();
		
		btnAddRequest.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent  intent  = new Intent(ReturnOrderStatusActivity.this,SalesmanReturnOrder.class);
				intent.putExtra("mallsDetails", mallsDetailss);
				startActivity(intent);
				
			}
		});
		
		btnRefresh.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				loadTransactions(empNO,TrxHeaderDO.get_TRXTYPE_RETURN_ORDER());
			}
		});
		
	}
	
	/** initializing all the Controls  of SupervisorManageStaff class **/
	public void intializeControls()
	{
		tvAgenciesName			=	(TextView)llDeliveryStatus.findViewById(R.id.tvAgenciesName);
		btnAddRequest			=	(Button)llDeliveryStatus.findViewById(R.id.btnAddRequest);
		btnRefresh				=	(Button)llDeliveryStatus.findViewById(R.id.btnRefresh);
		preference				=   new Preference(ReturnOrderStatusActivity.this);
		
		tabs 	 				= 	(PagerSlidingTabStrip) llDeliveryStatus.findViewById(R.id.tabs);
		pager 					= 	(ViewPager) llDeliveryStatus.findViewById(R.id.pager);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setOnPageChangeListener(ReturnOrderStatusActivity.this);
		
		tvAgenciesName.setText(mallsDetailss.siteName+" ["+mallsDetailss.site+"]"/*+""*/);
		empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
		setTypeFaceRobotoBold(llDeliveryStatus);
	}
	
	private void loadTransactions(final String strEmpNo,
			final int trxType) {
		showLoader(getString(R.string.please_wait));
		new Thread(new Runnable() {
			@Override
			public void run() {
				GetTrxHeaderForApp getTrxHeaderForApp = new GetTrxHeaderForApp(
						ReturnOrderStatusActivity.this, strEmpNo);
				SynLogDO synLogDO = new SynLogDA()
						.getSynchLog(ServiceURLs.GetTrxHeaderForApp);
				String lsd = 0 + "";
				String lst = 0 + "";
				if (synLogDO != null) {
					lsd = synLogDO.UPMJ;
					lst = synLogDO.UPMT;
				}
				new ConnectionHelper(new ConnectionExceptionListener() {
					@Override
					public void onConnectionException(Object msg) {

					}
				}).sendRequest(ReturnOrderStatusActivity.this, BuildXMLRequest
						.getAllTrxHeaderForAppWithLastSynch(strEmpNo, lsd, lst,
								trxType), getTrxHeaderForApp,
						ServiceURLs.GetTrxHeaderForApp);
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoader();
						loadReturnOrderList();
					}
				});
			}
		}).start();
	}
	
	
	public void loadReturnOrderList()
	{
		
		showLoader(getResources().getString(R.string.loading));
			
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				
				hmOrders 	  = new ReturnOrderDA().getOrderSummary(empNO,mallsDetailss.site);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						int position = 0;
						
						if(pager!=null)
							position = pager.getCurrentItem();
						
						adapter  = new CategoryPagerAdapter(getSupportFragmentManager(),tabsName);
						pager.setAdapter(adapter);
						tabs.setViewPager(pager);
						
						hideLoader();
						
						int trxStatus = 0;
						
						for(int i=0;i<tabsName.length;i++)
						{
							if(i == 0)
								trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_PENDING();
							else if(i == 1)
								trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_APPROVED();
							else if(i == 2)
								trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
							else
								trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_REJECTED();
							
							if(hmOrders!=null && hmOrders.get(trxStatus)!=null)
								tabs.setTabText(i, hmOrders.get(trxStatus).size());
							else
								tabs.setTabText(i,0);
						}
						
						pager.setCurrentItem(position);
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
				trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_PENDING();
			else if(position == 1)
				trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_APPROVED();
			else if(position == 2)
				trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			else
				trxStatus = TrxHeaderDO.get_TRXTYPE_RETURN_ORDER_STATUS_REJECTED();
			
			
			return new ListOrderFragment(ReturnOrderStatusActivity.this,position,hmOrders.get(trxStatus),trxStatus);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK)
		{
			loadReturnOrderList();
		}
	}
}
