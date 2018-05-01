package com.winit.sfa.salesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.kwalitysfa.salesman.R;

@SuppressLint("DefaultLocale") 
public class AssetsRequestCustomerList extends BaseActivity
{
	private LinearLayout llCusomerList;
	private TextView tvListViewHeader, tvNoRecorFound,tvCustCode,tvCustName;
	private ListView lvCustomerList;
	private CustomerDetailsDA objCustomerDetailsBL;
	private EditText etSearch;
	private CustomerListAdapter adapter;
	private ArrayList<JourneyPlanDO> arrayListEvent;
	private boolean isHistory;
	
	private ImageView ivSearchCross;
	
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() 
	{
		llCusomerList = (LinearLayout) inflater.inflate(R.layout.customer_list, null);
		llBody.addView(llCusomerList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		if(getIntent().getExtras() != null)
			isHistory = getIntent().getExtras().getBoolean("isHistory");
		
		intialiseControls();
		setTypeFaceRobotoNormal(llCusomerList);
		tvListViewHeader.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvListViewHeader.setText("Customers");
		
		objCustomerDetailsBL = new CustomerDetailsDA();
		loadCustomerList();
		lvCustomerList.setCacheColorHint(0);
		lvCustomerList.setFadingEdgeLength(0);
		//lvCustomerList.setDivider(getResources().getDrawable(R.drawable.dot_seperator));
		lvCustomerList.setSelector(getResources().getDrawable(R.drawable.list_item_selected));
		adapter = new CustomerListAdapter(new ArrayList<JourneyPlanDO>());
		lvCustomerList.setAdapter(adapter);
		
		lvCustomerList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				hideKeyBoard(etSearch);
				JourneyPlanDO mallsDetails = (JourneyPlanDO)arg1.getTag();
				
				Intent intent	=	new Intent(AssetsRequestCustomerList.this,CustomerAssetsListActivity.class);
				intent.putExtra("mallsDetails", mallsDetails);
				startActivity(intent);
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
		etSearch.setHint("Search by Site Name/ Site Id");
		etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
						JourneyPlanDO obj 	= arrayListEvent.get(index);
						String strText 		= (obj).siteName;
						String strText1 	= (obj).site;
						String strText2 	= (obj).partyName;
						
						if(strText.toLowerCase().contains(s.toString().toLowerCase()) 
								|| strText1.toLowerCase().contains(s.toString().toLowerCase())
								|| strText2.toLowerCase().contains(s.toString().toLowerCase()))
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
		tvCustCode	= (TextView)llCusomerList.findViewById(R.id.tvCustCode);
		tvCustName	= (TextView)llCusomerList.findViewById(R.id.tvCustName);
		lvCustomerList 		= (ListView)llCusomerList.findViewById(R.id.lvCustomerList);
		etSearch		= (EditText)llCusomerList.findViewById(R.id.etSearch);
		ivSearchCross			=	(ImageView)llCusomerList.findViewById(R.id.ivSearchCross);
		tvNoRecorFound		= (TextView)llCusomerList.findViewById(R.id.tvNoRecorFound);
	
		//setting type face
		/*tvListViewHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSearchText.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNoRecorFound.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
		tvCustCode.setText("Site ID");
		tvCustName.setText("Site Name");
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	public void loadCustomerList()
	{
		arrayListEvent = new ArrayList<JourneyPlanDO>();
		showLoader("Loading customers...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				arrayListEvent = objCustomerDetailsBL.getJourneyPlanForTeleOrder(preference.getStringFromPreference(Preference.SALESMANCODE, ""));
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
			
			if(convertView ==null)
				convertView					=	(LinearLayout)getLayoutInflater().inflate(R.layout.ar_collection_list_details_assets, null);
			
			TextView tvRoleName				=	(TextView)convertView.findViewById(R.id.tvSiteName);
			TextView tvCustomerSite			=	(TextView)convertView.findViewById(R.id.tvAddress);
			TextView tvBalanceAmountShow	=	(TextView)convertView.findViewById(R.id.tvCreditLimit);
			TextView tvSitenameTitle		=	(TextView)convertView.findViewById(R.id.tvSiteID);
			ImageView ivCustomerType		=	(ImageView)convertView.findViewById(R.id.ivCustomerPaymentType);
			LinearLayout llCredit			=	(LinearLayout)convertView.findViewById(R.id.llCredit);
			
			ivCustomerType.setVisibility(View.GONE);
			llCredit.setVisibility(View.GONE);
			
			tvRoleName.setGravity(Gravity.CENTER_VERTICAL);
			
			/*tvSitenameTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvCustomerSite.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvRoleName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
			tvBalanceAmountShow.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
			tvCustomerSite.setText(getAddress(mallsDetails));
//			tvCustomerSite.setText(mallsDetails.addresss1);
			tvRoleName.setText(mallsDetails.siteName /*+ " ("+mallsDetails.partyName+")"*/);
			tvSitenameTitle.setText(mallsDetails.site);
			
			convertView.setTag(mallsDetails);
			tvRoleName.setPadding(0, 4, 0, 4);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			setTypeFaceRobotoNormal((ViewGroup) convertView);
			return convertView;
			
		}
		
		private void refresh(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			arrListCustomers = sortedCustomerList(arrayListEvent);
			notifyDataSetChanged();
		}
		
		private ArrayList<JourneyPlanDO> sortedCustomerList(ArrayList<JourneyPlanDO> arrayListEvent) 
		{
			synchronized (arrayListEvent) {
				if (arrayListEvent.size() > 0) {
				    Collections.sort(arrayListEvent, new Comparator<JourneyPlanDO>() {
				        @Override
				        public int compare(final JourneyPlanDO object1, final JourneyPlanDO object2) {
				            return object1.siteName.compareTo(object2.siteName);
				        }
				       } );
				   }
			}
			
			return arrayListEvent;
		}
	}
}
