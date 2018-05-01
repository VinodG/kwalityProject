package com.winit.sfa.salesman;


import java.util.ArrayList;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.winit.alseer.parsers.GetCompetitorDetail;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CompetitorDetailDA;
import com.winit.alseer.salesman.dataobject.CompBrandDO;
import com.winit.alseer.salesman.dataobject.CompCategoryDO;
import com.winit.alseer.salesman.dataobject.CompDetailDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class CaptureCompetitorDetail extends BaseActivity
{
	//initializing variables
	private TextView tvPageTitle, tvBrand, tvBrandVal, tvCategory, tvCategoryVal, tvPrice, tvCurrency, 
					 tvCurrencyVal, tvDescription;
	private Button btnSubmit, btnCancel, btnSyncData;
	private EditText etPriceVal, etDescriptionVal;
	private LinearLayout  llHouseContactDetail;
	private Vector<CompBrandDO> vecBrandDO;
	private Vector<CompCategoryDO> vecCategoryDO;
	private String [] currency = {"AED"};
	@Override
	public void initialize()
	{
		llHouseContactDetail = (LinearLayout) inflater.inflate(R.layout.competitor_detail, null);
		intialisecontrols();
		
		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				boolean isValid = validateFeilds();
				
				if(isValid)
					postData();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		//Inflating layout
		llBody.addView(llHouseContactDetail, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		loadData();
		
		tvBrandVal.setTag(-1);
		tvBrandVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				CustomBuilder customBuilder = new CustomBuilder(CaptureCompetitorDetail.this, "Select Brand", true);
				customBuilder.setSingleChoiceItems(vecBrandDO, v.getTag(), new CustomBuilder.OnClickListener()
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject)
					{
						CompBrandDO compBrandDO = (CompBrandDO) selectedObject;
						((TextView)v).setText(compBrandDO.Brand);
						((TextView)v).setTag(compBrandDO);
						builder.dismiss();
					}
				});
				customBuilder.show();
			}
		});
		
		tvCategoryVal.setTag(-1);
		tvCategoryVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				CustomBuilder customBuilder = new CustomBuilder(CaptureCompetitorDetail.this, "Select Category", true);
				customBuilder.setSingleChoiceItems(vecCategoryDO, v.getTag(), new CustomBuilder.OnClickListener()
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject)
					{
						CompCategoryDO compCategoryDO = (CompCategoryDO) selectedObject;
						((TextView)v).setText(compCategoryDO.Category);
						((TextView)v).setTag(compCategoryDO);
						builder.dismiss();
					}
				});
				customBuilder.show();
			}
		});
		
		tvCurrencyVal.setTag(-1);
		tvCurrencyVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				int id = StringUtils.getInt(v.getTag().toString());
				AlertDialog.Builder builder = new AlertDialog.Builder(CaptureCompetitorDetail.this);
				builder.setSingleChoiceItems(currency, id, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						String str = currency[which];
						((TextView)v).setText(str);
						((TextView)v).setTag(which);
						dialog.dismiss();
					}
				});
				
				builder.create().show();
			}
		});
		
		btnSyncData.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				syncData();				
			}
		});
		
		setTypeFaceRobotoNormal(llHouseContactDetail);
		tvPageTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnSyncData.setTypeface(Typeface.DEFAULT_BOLD);
		btnSubmit.setTypeface(Typeface.DEFAULT_BOLD);
		btnCancel.setTypeface(Typeface.DEFAULT_BOLD);
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}
	
	//getting id's and setting type-faces
	public void intialisecontrols()
	{
		tvPageTitle                = (TextView)llHouseContactDetail.findViewById(R.id.tvPageTitle);
		tvBrand                 = (TextView)llHouseContactDetail.findViewById(R.id.tvBrand);
		tvBrandVal            	= (TextView)llHouseContactDetail.findViewById(R.id.tvBrandVal);
		tvCategory            	= (TextView)llHouseContactDetail.findViewById(R.id.tvCategory);
		tvCategoryVal		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvCategoryVal);
		tvPrice					= (TextView)llHouseContactDetail.findViewById(R.id.tvPrice);
		tvCurrency             	= (TextView)llHouseContactDetail.findViewById(R.id.tvCurrency);
		tvCurrencyVal           = (TextView)llHouseContactDetail.findViewById(R.id.tvCurrencyVal);
		tvDescription           = (TextView)llHouseContactDetail.findViewById(R.id.tvDescription);
		btnSubmit             	= (Button)llHouseContactDetail.findViewById(R.id.btnSubmit);
		btnCancel               = (Button)llHouseContactDetail.findViewById(R.id.btnCancel);
		btnSyncData            	= (Button)llHouseContactDetail.findViewById(R.id.btnSyncData);
		etPriceVal			    = (EditText)llHouseContactDetail.findViewById(R.id.etPriceVal);
		etDescriptionVal		= (EditText)llHouseContactDetail.findViewById(R.id.etDescriptionVal);
		
		tvPageTitle.setText("Add a Competitor Activity");
		/*
		tvHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvBrand.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvBrandVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCategory.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCategoryVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvPrice.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCurrency.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCurrencyVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDescription.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSubmit.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCancel.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnSyncData.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etPriceVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etDescriptionVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
   }
	
	private boolean validateFeilds()
	{
		if(TextUtils.isEmpty(tvBrandVal.getText().toString()))
			showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Please select brand.", "OK", null, "");
		
		else if(TextUtils.isEmpty(tvCategoryVal.getText().toString()))
			showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Please select category.", "OK", null, "");
		
		else if(TextUtils.isEmpty(etPriceVal.getText().toString()))
			showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Please enter price.", "OK", null, "");
		
		else if(TextUtils.isEmpty(tvCurrencyVal.getText().toString()))
			showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Please select currency type.", "OK", null, "");
		
		else if(TextUtils.isEmpty(etDescriptionVal.getText().toString()))
			showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Please enter description.", "OK", null, "");
		else
			return true;
		
		return false;
	}
	private void loadData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final ArrayList<Object> arrList = new CompetitorDetailDA().loadData();
				if(arrList != null && arrList.size() > 1)
				{
					vecCategoryDO = (Vector<CompCategoryDO>) arrList.get(0);
					vecBrandDO    = (Vector<CompBrandDO>) arrList.get(1);
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						if(arrList == null || arrList.size() <= 0)
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Competitor data is not available. Please press 'Sync Data' to sync Competitor data.", "OK", null, "");
						else if(vecCategoryDO == null || vecCategoryDO.size() <= 0)
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Competitor Category data is not available. Please press 'Sync Data' to sync Competitor data.", "OK", null, "");
						else if(vecBrandDO == null || vecBrandDO.size() <= 0)
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Competitor Brand data is not available. Please press 'Sync Data' to sync Competitor data.", "OK", null, "");
						
						hideLoader();
					}
				});
			}
		}).start();
	}
	
	private void postData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				CompDetailDO compDetailDO 	= 	new CompDetailDO();
				compDetailDO.appID 			= 	StringUtils.getUniqueUUID();
				compDetailDO.BrandId 		= 	((CompBrandDO)tvBrandVal.getTag()).BrandId;
				compDetailDO.CategoryId		=	((CompCategoryDO)tvCategoryVal.getTag()).CategoryId;
				compDetailDO.price   		= 	etPriceVal.getText().toString();
				compDetailDO.currency 		= 	tvCurrencyVal.getText().toString();
				compDetailDO.description	= 	etDescriptionVal.getText().toString();
				compDetailDO.empNo			= 	preference.getStringFromPreference(Preference.EMP_NO, "");
				compDetailDO.date			= 	CalendarUtils.getCurrentDateTime();
				
				final boolean isInserted  	= 	new CompetitorDetailDA().insertActivity(compDetailDO);
				
				if(isNetworkConnectionAvailable(CaptureCompetitorDetail.this))
					uploadData();
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isInserted)
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.successful), "Competitor details has been saved successfully.", "OK", null, "finish");
						else
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), "Error occured while saving data, Please try again.", "OK", null, "");
					}
				});
			}
		}).start();
	}
	
	boolean isSynced = false;
	private void syncData()
	{
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isNetworkConnectionAvailable(CaptureCompetitorDetail.this))
				{
					isSynced = true;
					String empNo = preference.getStringFromPreference(Preference.EMP_NO, "");
					GetCompetitorDetail getCompetitorDetail 		= 	new GetCompetitorDetail(CaptureCompetitorDetail.this, empNo);
					new ConnectionHelper(CaptureCompetitorDetail.this).sendRequest(CaptureCompetitorDetail.this, BuildXMLRequest.getCompetitorDetail(empNo, preference.getStringFromPreference(ServiceURLs.GetCompetitorDetail+empNo+Preference.LAST_SYNC_TIME, "")), getCompetitorDetail, ServiceURLs.GetCompetitorDetail);
				}
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isSynced)
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.successful), "Competitor details have been synced successfully..", "OK", null, "");
						else
							showCustomDialog(CaptureCompetitorDetail.this, getString(R.string.warning), getString(R.string.no_internet), "OK", null, "");
						isSynced  = false;
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		if(from.equalsIgnoreCase("finish"))
			finish();
	}
	
}
