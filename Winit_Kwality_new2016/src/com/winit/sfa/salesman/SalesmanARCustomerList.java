package com.winit.sfa.salesman;

import java.util.ArrayList;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class SalesmanARCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private String callfrom="";
	private EditText etSearch;
	private CustomerListAdapter obCustomerListAdapter;
	private ArrayList<JourneyPlanDO> vecJourneyPlanDO;
	private String strText = "";
	
	private ImageView ivSearchCross;
	
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		intialiseControls();
		setTypeFaceRobotoNormal(llCusomerList);
		tvListViewHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(getIntent().getExtras() != null)
			callfrom =	getIntent().getExtras().getString("callfrom");
		
		
		tvListViewHeader.setText("Customers");
		objCustomerDetailsBL = new CustomerDetailsDA();
		loadCustomerList();
//		new CaptureInventryDA().getPendingAmount(preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, ""));
		lvCustomerList.setCacheColorHint(0);
		lvCustomerList.setFadingEdgeLength(0);
		lvCustomerList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvCustomerList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
		obCustomerListAdapter = new CustomerListAdapter(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(obCustomerListAdapter);
		
		ivSearchCross.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSearch.setText("");
				ivSearchCross.setVisibility(View.GONE);
				obCustomerListAdapter.refreshList(vecJourneyPlanDO);
			}
		});

		etSearch.setHint("Search by Site Name/ Site Id");
		
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
					for(int index = 0; vecJourneyPlanDO != null && index < vecJourneyPlanDO.size(); index++)
					{
						JourneyPlanDO obj 	= (JourneyPlanDO) vecJourneyPlanDO.get(index);
						String strText 		= (obj).siteName;
						String strText1 	= (obj).site;
						String strText2 	= (obj).partyName;
						if(strText.toLowerCase().contains(s.toString().toLowerCase())
							|| strText1.toLowerCase().contains(s.toString().toLowerCase())
							|| strText2.toLowerCase().contains(s.toString().toLowerCase()))
							vecTemp.add(vecJourneyPlanDO.get(index));
					}
					if(vecTemp!=null)
						obCustomerListAdapter.refreshList(vecTemp);
				}
				else
					obCustomerListAdapter.refreshList(vecJourneyPlanDO);
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
	}
	
	private void intialiseControls() 
	{
		tvListViewHeader = (TextView)llCusomerList.findViewById(R.id.tvListViewHeader);
		lvCustomerList 	 = (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearch	 = (EditText)llCusomerList.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llCusomerList.findViewById(R.id.ivSearchCross);
		tvNoRecorFound	 = (TextView)llCusomerList.findViewById(R.id.tvNoRecorFound);
		//setting type-face 
		/*etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoRecorFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	public void loadCustomerList()
	{
		showLoader("Loading customers...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				vecJourneyPlanDO = objCustomerDetailsBL.getAllCreditCustomerList(AppConstants.CUSTOMER_TYPE_CREDIT);
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						obCustomerListAdapter.refreshList(vecJourneyPlanDO);
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			JourneyPlanDO JourneyPlanDO = (JourneyPlanDO) arrListCustomers.get(position);
			
			if(convertView ==null)   
				convertView				=	(LinearLayout)getLayoutInflater().inflate(R.layout.ar_collection_list_details, null);
			TextView tvRoleName			=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvCustomerSite		=	(TextView)convertView.findViewById(R.id.tvAddress);
			TextView tvSitenameTitle	=	(TextView)convertView.findViewById(R.id.tvSiteID);
			TextView tvBalanceAmount	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
			ImageView ivCustomerType	=	(ImageView)convertView.findViewById(R.id.ivCustomerPaymentType);
			
			 setTypeFaceRobotoNormal(parent);
			 tvRoleName.setTypeface(AppConstants.Roboto_Condensed_Bold);
			 tvCustomerSite.setTypeface(AppConstants.Roboto_Condensed_Bold);
//			 tvCustomerSite.setTypeface(AppConstants.Roboto_Condensed);
//			 tvSitenameTitle.setTypeface(AppConstants.Roboto_Condensed);
//			 tvBalanceAmount.setTypeface(AppConstants.Roboto_Condensed);
			
			tvRoleName.setGravity(Gravity.CENTER_VERTICAL);
			/*tvBalanceAmount.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvSitenameTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCustomerSite.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvRoleName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			tvRoleName.setText(JourneyPlanDO.siteName/*+" ("+JourneyPlanDO.partyName+")"*/);
			tvSitenameTitle.setText(JourneyPlanDO.site);
			
			tvCustomerSite.setText(getAddress(JourneyPlanDO));
			if(JourneyPlanDO.customerPaymentType!=null && JourneyPlanDO.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
				ivCustomerType.setBackgroundResource(R.drawable.credirt_bg);
			else
				ivCustomerType.setBackgroundResource(R.drawable.cash_bg);
			
			if(StringUtils.getFloat(JourneyPlanDO.balanceAmount) > 0)
				tvBalanceAmount.setText(""+curencyCode+amountFormate.format(StringUtils.getFloat(JourneyPlanDO.balanceAmount))+" " + curencyCode+"");
			else
				tvBalanceAmount.setText(""+curencyCode+" "+"0.00 "+"");
			
			convertView.setTag(JourneyPlanDO);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			convertView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(callfrom.equalsIgnoreCase("ArCollection"))
					{
						strText = etSearch.getText().toString();
						JourneyPlanDO JourneyPlanDO  = (JourneyPlanDO) v.getTag();
						Intent intent = new Intent(SalesmanARCustomerList.this, PendingInvoices.class);
						intent.putExtra("AR", true);
						intent.putExtra("mallsDetails", JourneyPlanDO);
						intent.putExtra("fromMenu", true);
						startActivity(intent);
					}
				}
			});
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
		}
		
		public void refreshList(ArrayList<JourneyPlanDO> arrayListEvent)
		{
			this.arrListCustomers = arrayListEvent;
			notifyDataSetChanged();
			if(arrListCustomers != null && arrListCustomers.size() > 0)
				tvNoRecorFound.setVisibility(View.GONE);
			else
				tvNoRecorFound.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		loadCustomerList();
		if(etSearch != null)
			etSearch.setText("");
	}
}
