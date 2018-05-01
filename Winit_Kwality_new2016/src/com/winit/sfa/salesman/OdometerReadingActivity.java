package com.winit.sfa.salesman;

import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;

public class OdometerReadingActivity extends BaseActivity
{

	@SuppressWarnings("unused")
	private LinearLayout llOdometerReading, llEndDay, llStartDay, llTotalValue,llthingsfocusheading,llPagerTab;
	
	private TextView tv_thingstofocusnotAvialable,tvtvTotalValue, tvEndDayTimeValuew, tvStartDayTimeValuew, tvStartDayTimeValuewResion,
					tvEndDayTimeValuewResion;
	private View thingsfocusdivider;

	private EditText etEndDayReading, etStartDayReading, etVehicleNo;
	private Button btnFinish;
	private boolean isStartDay = false;
	private String imagePath;
	private MyPagerAdapter adapter;
	private ViewPager pager;
	private Vector<String> vecThingstofocusDO;
	public static final String IMAGE_CACHE_DIR = "thumbs";
	public String htmlfiles[] = {"Page1.html","Page2.html","Page3.html"};
	private boolean customerlist = false;
	private JouneyStartDO j=null;
	
	@Override
	public void initialize()
	{
		llOdometerReading = (LinearLayout) getLayoutInflater().inflate(R.layout.odometer_reading, null);
		llBody.addView(llOdometerReading, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
		initializeControles();
		
		if(getIntent().getExtras() != null)
		{
			isStartDay = getIntent().getExtras().getBoolean("isStartDay");
			
			imagePath = getIntent().getExtras().getString("image_path");
			
			customerlist = getIntent().getExtras().getBoolean("customerlist");
		}
		
		if(isStartDay)
		{
			llEndDay.setVisibility(View.GONE);
			llTotalValue.setVisibility(View.GONE);
			tvStartDayTimeValuew.setText(CalendarUtils.getCurrentTime().split(" ")[0]);
			tvStartDayTimeValuewResion.setText(CalendarUtils.getCurrentTime().split(" ")[1]);
			
			btnFinish.setText("Start Day");
		}
		else
		{
			writeLogForEOT("\nEnter into odometeractivity for end reading :"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");

			btnFinish.setText("End Day");
			if(preference.getStringFromPreference(Preference.STARTDAY_TIME, "") != null && !preference.getStringFromPreference(Preference.STARTDAY_TIME, "").equalsIgnoreCase(""))
			{
				if(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ").length > 1)
				{
					tvStartDayTimeValuew.setText(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ")[0]);
					tvStartDayTimeValuewResion.setText(preference.getStringFromPreference(Preference.STARTDAY_TIME, "").split(" ")[1]);
				}
			}
			
			if(preference.getStringFromPreference(Preference.ENDAY_TIME, "") != null && !preference.getStringFromPreference(Preference.ENDAY_TIME, "").equalsIgnoreCase(""))
			{
				if(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ").length > 1)
				{
					tvEndDayTimeValuew.setText(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ")[0]);
					tvEndDayTimeValuewResion.setText(preference.getStringFromPreference(Preference.ENDAY_TIME, "").split(" ")[1]);
				}
			}
		}
		
		/**
		 * Commented because End Day design is not good. End Day Functionality not working
		 * Revert back once End day functionality is finalised
		 * By Aritra
		 */
//		if(!isStartDay)
//		{
//			etStartDayReading.setEnabled(false);
//			etStartDayReading.setFocusable(false);
//			etStartDayReading.setFocusableInTouchMode(false);
//			etStartDayReading.setCursorVisible(false);
//			etStartDayReading.setSingleLine(false);
//			etStartDayReading.clearFocus();
//		}
		
		tvEndDayTimeValuew.setText(CalendarUtils.getCurrentTime().split(" ")[0]);
		tvEndDayTimeValuewResion.setText(CalendarUtils.getCurrentTime().split(" ")[1]);
		btnFinish.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				

				if(!etStartDayReading.getText().toString().equalsIgnoreCase("") && 
						!etVehicleNo.getText().toString().equalsIgnoreCase(""))
				{
					if(isStartDay)
					{
						startJouney();
						Intent intent;
						if(customerlist)
							finish();
						else
						{
							intent = new Intent(OdometerReadingActivity.this, PresellerJourneyPlan.class);
							intent.putExtra("Latitude", 25.522);
							intent.putExtra("Longitude", 78.522);
							intent.putExtra("mallsDetails", mallsDetailss);
							startActivity(intent);
							finish();
						}
					}
					else
					{
						if(StringUtils.getInt(etStartDayReading.getText().toString().trim()) > preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0))
						{
							startJouney();
						}
						else
						{
							showCustomDialog(OdometerReadingActivity.this, "Alert!", "End Day Odometer reading should be greater than Start Day Odometer reading.", "OK", null, "");
						}
					}
				}
				else
				{
					if(isStartDay)
					{
						if(etStartDayReading.getText().toString().equalsIgnoreCase(""))
							Toast.makeText(OdometerReadingActivity.this, "Please enter Start Day Odometer Reading.", Toast.LENGTH_LONG).show();
						else if(etVehicleNo.getText().toString().equalsIgnoreCase(""))
							Toast.makeText(OdometerReadingActivity.this, "Please enter your Vehicle Number.", Toast.LENGTH_LONG).show();
						return;
					}
					else
					{
						
						if(etStartDayReading.getText().toString().equalsIgnoreCase(""))
							Toast.makeText(OdometerReadingActivity.this, "Please enter End Day Odometer Reading.", Toast.LENGTH_LONG).show();
						else if(etVehicleNo.getText().toString().equalsIgnoreCase(""))
							Toast.makeText(OdometerReadingActivity.this, "Please enter your Vehicle Number.", Toast.LENGTH_LONG).show();
						return;
					}
				}
			
				
				/*
				if(isStartDay)
				{
					if(!etStartDayReading.getText().toString().equalsIgnoreCase(""))
					{
						startJouney();
						Intent intent = new Intent(OdometerReadingActivity.this, PresellerJourneyPlan.class);
						intent.putExtra("Latitude", 25.522);
						intent.putExtra("Longitude", 78.522);
						intent.putExtra("mallsDetails", mallsDetails);
						startActivity(intent);
						finish();
					}
					else
					{
						Toast.makeText(OdometerReadingActivity.this, "Please enter Start Day Odometer Reading.", Toast.LENGTH_LONG).show();
						return;
					}
				}
				else
				{
					if(!etEndDayReading.getText().toString().equalsIgnoreCase(""))
					{
						if(StringUtils.getInt(etEndDayReading.getText().toString().trim())>StringUtils.getInt(etStartDayReading.getText().toString().trim()))
						{
							startJouney();
//							setResult(5000);
//							finish();
						}
						else
						{
							showCustomDialog(OdometerReadingActivity.this, "Alert!", "End Day Odometer reading should be greater than Start Day Odometer reading.", "OK", null, "");
						}
					}
					else
					{
						Toast.makeText(OdometerReadingActivity.this, "Please enter End Day Odometer Reading.", Toast.LENGTH_LONG).show();
						return;
					}
				}
			*/}
		});
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
//				vecThingstofocusDO 		= new ThingstoFocusDA().getAllThingstofocus();
				vecThingstofocusDO = new Vector<String>();

				for (int i = 0; i < htmlfiles.length; i++) 
				{
					vecThingstofocusDO.add(htmlfiles[i]);
				}
				
				runOnUiThread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						if(vecThingstofocusDO!=null && vecThingstofocusDO.size()>0)
						{
							tv_thingstofocusnotAvialable.setVisibility(View.GONE);
							llthingsfocusheading.setVisibility(View.VISIBLE);
							thingsfocusdivider.setVisibility(View.VISIBLE);
							
							adapter = new MyPagerAdapter(vecThingstofocusDO);
							pager.setAdapter(adapter);
							
							refreshPageController(false);
							
							pager.setOnPageChangeListener(new OnPageChangeListener() 
							{
								
								@Override
								public void onPageSelected(int arg0) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void onPageScrolled(int arg0, float arg1, int arg2) {
									refreshPageController(true);
								}
								
								@Override
								public void onPageScrollStateChanged(int arg0) {
									// TODO Auto-generated method stub
									
								}
							});
						}
						else
						{
							tv_thingstofocusnotAvialable.setVisibility(View.GONE); //need to change
							llthingsfocusheading.setVisibility(View.GONE);
							thingsfocusdivider.setVisibility(View.GONE);
						}
						
					}
				});
			}
		}).start();
		
		int startValue = preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0);
		int endValue = preference.getIntFromPreference(Preference.ENDDAY_VALUE, 0);
		if(startValue > 0)
			etStartDayReading.setText(preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0)+"");
		
		if(endValue >0)
		{
			etEndDayReading.setText(preference.getIntFromPreference(Preference.ENDDAY_VALUE, 0)+"");
			
			if(endValue > startValue)
				tvtvTotalValue.setText(endValue - startValue+" KM");
		}
		
		etStartDayReading.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(s.toString().trim() != null && !s.toString().trim().equals(""))
				{
					if(isStartDay)
					{
						preference.saveIntInPreference(Preference.STARTDAY_VALUE, StringUtils.getInt(s.toString().trim()));
						preference.saveStringInPreference(Preference.STARTDAY_TIME, tvStartDayTimeValuew.getText().toString()+" "+tvStartDayTimeValuewResion.getText().toString());
						preference.commitPreference();
					}
					else
					{
						preference.saveIntInPreference(Preference.ENDDAY_VALUE, StringUtils.getInt(s.toString().trim()));
						preference.saveStringInPreference(Preference.ENDAY_TIME, tvEndDayTimeValuew.getText().toString()+" "+tvEndDayTimeValuewResion.getText().toString());
						preference.commitPreference();
						
						if(StringUtils.getInt(s.toString().trim()) > preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0)/*StringUtils.getInt(etStartDayReading.getText().toString())*/)
						{
							tvtvTotalValue.setText(StringUtils.getInt(s.toString().trim())- preference.getIntFromPreference(Preference.STARTDAY_VALUE, 0)/*StringUtils.getInt(etStartDayReading.getText().toString())*/+" KM");
						}
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			{
			} 
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				if(!isStartDay)
				{
					if(s.length() >= etStartDayReading.getText().toString().length())
					{
						if(StringUtils.getInt(s.toString().trim()) < StringUtils.getInt(etStartDayReading.getText().toString()))
						{
							showToast("End Odometer reading should be greater than start odometer reading.");
							etStartDayReading.setText("");  
						}	
					}
				}
			}
		});
		
		etEndDayReading.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				if(s.toString().trim() != null && !s.toString().trim().equals(""))
				{
					preference.saveIntInPreference(Preference.ENDDAY_VALUE, StringUtils.getInt(s.toString().trim()));
					preference.saveStringInPreference(Preference.ENDAY_TIME, tvEndDayTimeValuew.getText().toString()+" "+tvEndDayTimeValuewResion.getText().toString());
					preference.commitPreference();
					
					if(StringUtils.getInt(s.toString().trim()) > StringUtils.getInt(etEndDayReading.getText().toString()))
					{
						tvtvTotalValue.setText(StringUtils.getInt(s.toString().trim())- StringUtils.getInt(etEndDayReading.getText().toString())+" KM");
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{
			}
			@Override
			public void afterTextChanged(Editable s)
			{
				if(s.length() >= etStartDayReading.getText().toString().length())
				{
					if(StringUtils.getInt(s.toString().trim()) < StringUtils.getInt(etStartDayReading.getText().toString()))
					{
						showToast("End Odometer reading should be greater than start odometer reading.");
						etEndDayReading.setText("");  
					}	
				}
				
			}
		});
		
		if(btnCheckOut != null)
		{
			btnCheckOut.setVisibility(View.GONE);
			ivLogOut.setVisibility(View.GONE);
		}
		
		setTypeFaceRobotoNormal(llOdometerReading);
		btnFinish.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}
	
	private void startJouney()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				 j = new JouneyStartDO();
				j.date = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
				if(isStartDay)
				{
					j.startTime = j.endTime =  CalendarUtils.getCurrentDateTime();
					preference.saveBooleanInPreference(Preference.IsStockVerified, true);
					preference.saveStringInPreference(Preference.STARTDAY_TIME_ACTUAL, CalendarUtils.getCurrentDateTime());
					preference.commitPreference();
				}
				else
				{
					/*j.startTime = */j.endTime = CalendarUtils.getCurrentDateTime();
//					j.startTime = CalendarUtils.getOdometerDate(preference.getStringFromPreference(Preference.STARTDAY_TIME_ACTUAL, ""));
				}
				j.IsVanStockVerified = "1";
				j.journeyAppId = StringUtils.getUniqueUUID();
				if(isStartDay)
					j.journeyCode = preference.getStringFromPreference(preference.SALESMANCODE, "")+CalendarUtils.getCurrentDateAsString();
				else
					j.journeyCode = preference.getStringFromPreference(preference.SALESMANCODE, "")+CalendarUtils.getCurrentDateForEOT_PATTERN();
				
				j.odometerReading = etStartDayReading.getText().toString();
				j.TotalTimeInMins = 0;
				j.userCode = preference.getStringFromPreference(preference.SALESMANCODE, "");
				j.VerifiedBy = "";
				
				if(isStartDay)
					j.OdometerReadingStart = etStartDayReading.getText().toString(); 

				if(!isStartDay)
					j.OdometerReadingEnd = etStartDayReading.getText().toString(); 
//				j.vehicleCode = preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
				j.vehicleCode = etVehicleNo.getText().toString();
//				j.OdometerReadingEnd = 0+"";
				Log.e("imagePath","imagePath = "+imagePath);
				
				if(isStartDay)
					new JourneyPlanDA().insertJourneyStarts(j);
//				else //commented by vinod it is done in salesmansummaryofday.java
//					new JourneyPlanDA().updaateJourneyEnds(j);
				
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						uploadData();	
						if(!isStartDay)
						{
							writeLogForEOT("\nReturning to Salesmansummary of day activity:"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");

							Intent intent = new Intent();
							intent.putExtra("JouneyEND",j); //passing this result to salesmansummary of day activity where we do update tbljourney, tbltrxlogdetails based on response of insertEOT service..
							setResult(5000, intent);
							finish();
						}
					}
				});
			}
		}).start();
	}
	
	private void initializeControles()
	{
		btnFinish	= (Button) llOdometerReading.findViewById(R.id.btnFinish);
	
		tvtvTotalValue			= (TextView) llOdometerReading.findViewById(R.id.tvtvTotalValue);
		tvEndDayTimeValuew		= (TextView) llOdometerReading.findViewById(R.id.tvEndDayTimeValuew);
		tvStartDayTimeValuew	= (TextView) llOdometerReading.findViewById(R.id.tvStartDayTimeValuew);
		tv_thingstofocusnotAvialable= (TextView) llOdometerReading.findViewById(R.id.tv_thingstofocusnotAvialable);
		etEndDayReading			= (EditText) llOdometerReading.findViewById(R.id.etEndDayReading);
		etStartDayReading		= (EditText) llOdometerReading.findViewById(R.id.etStartDayReading);
		etVehicleNo				= (EditText) llOdometerReading.findViewById(R.id.etVehicleNo);
		
		tvStartDayTimeValuewResion	= (TextView) llOdometerReading.findViewById(R.id.tvStartDayTimeValuewResion);
		tvEndDayTimeValuewResion	= (TextView) llOdometerReading.findViewById(R.id.tvEndDayTimeValuewResion);
		
		llEndDay				= (LinearLayout) llOdometerReading.findViewById(R.id.llEndDay);
		llStartDay				= (LinearLayout) llOdometerReading.findViewById(R.id.llStartDay);
		llTotalValue			= (LinearLayout) llOdometerReading.findViewById(R.id.llTotalValue);
		llthingsfocusheading	= (LinearLayout) llOdometerReading.findViewById(R.id.llthingsfocusheading);
		llPagerTab 					=   (LinearLayout) llOdometerReading.findViewById(R.id.llPagerTab);
		
		thingsfocusdivider = (View) llOdometerReading.findViewById(R.id.thingsfocusdivider);
		
		btnLogo.setEnabled(false);
		btnLogo.setClickable(false);
		pager 				= (ViewPager) llOdometerReading.findViewById(R.id.pager);
		
		pager.setOffscreenPageLimit(3);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		pager.setEnabled(false);
		pager.setVisibility(View.GONE);
		pager.setClickable(false);
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private class MyPagerAdapter extends PagerAdapter 
	{
		Vector<String> vectorthingstofocus;

		public MyPagerAdapter(Vector<String> vecThingstofocusDO) 
		{
			this.vectorthingstofocus = vecThingstofocusDO;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}


		@Override
		public Object instantiateItem(final View collection, final int position)
		{
			
			LinearLayout llCell;
//			TextView tv_title,tv_content;
			llCell = (LinearLayout) getLayoutInflater().inflate(R.layout.thingstofocus_pagercell,null);
//			final ImageView iv_contentimg = (ImageView) llCell.findViewById(R.id.iv_contentimg);
//			tv_title    = (TextView) llCell.findViewById(R.id.tv_title);
//			tv_content  = (TextView) llCell.findViewById(R.id.tv_content);
			
			WebView wvHtml = (WebView)llCell.findViewById(R.id.wvHtml);
			wvHtml.getSettings().setJavaScriptEnabled(true);
			wvHtml.loadUrl("file:///android_asset/"+vecThingstofocusDO.get(position));
			
			llCell.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			iv_contentimg.setImageResource(vectorthingstofocus.);
//			UrlImageViewHelper.setUrlDrawable(iv_contentimg, vectorthingstofocus.get(position).ImagePath, R.drawable.a1, new UrlImageViewCallback() 
//			{
//				
//				@Override
//				public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
//						boolean loadedFromCache) 
//				{
//					// TODO Auto-generated method stub
//					if(!loadedFromCache)
//						iv_contentimg.setImageBitmap(loadedBitmap);
//				}
//			});
			
//			../Data/ThingsToFocus/keep-focus635503491792344941.PNG
//			String imagePath = "http://208.109.154.54/alseersfav3";
//			http://208.109.154.54/alseersfav3../Data/ThingsToFocus/Penguins635502777846944329.PNG
//			String imgpathfromserver = vectorthingstofocus.get(position).ImagePath.replace("~","");
//			imgpathfromserver = imgpathfromserver.replaceAll(".", "");
			
//			Log.e("imageurl",imagePath+imgpathfromserver);
			
//			 UrlImageViewHelper.setUrlDrawable(iv_contentimg, imagePath+vectorthingstofocus.get(position).ImagePath.replace("~",""), R.drawable.empty,UrlImageViewHelper.CACHE_DURATION_ONE_DAY);
			
			((ViewPager) collection).addView(llCell);
//			tv_title.setText(vectorthingstofocus.get(position).Title);
//			tv_content.setText(vectorthingstofocus.get(position).Content);
			setTypeFaceRobotoNormal(llCell);
            return llCell;
		}

		@Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((LinearLayout) view);
        }

		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view == ((LinearLayout) object);
		}

		@Override
		public int getCount() 
		{
			return vecThingstofocusDO.size();
		}
		
	}
	
	private void refreshPageController(boolean onscroll) 
	{
		int pagerPosition = 0;
		
		if(!onscroll)
		{
			llPagerTab.removeAllViews();
			
			for (int i = 0; i <= (adapter.getCount()-1); i++)
			{
				final ImageView imgvPagerController = new ImageView(OdometerReadingActivity.this);
				imgvPagerController.setPadding(2,2,2,2);
				
				imgvPagerController.setImageResource(R.drawable.pager_dot);
				llPagerTab.addView(imgvPagerController);
			}	
			
			pagerPosition = pager.getCurrentItem();
			
			if(((ImageView)llPagerTab.getChildAt(pagerPosition)) != null)
				((ImageView)llPagerTab.getChildAt(pagerPosition)).setImageResource(R.drawable.pager_dot_h);
			
		}
		else
		{
			pagerPosition = pager.getCurrentItem();
			
			for (int i = 0; i <= (adapter.getCount()-1); i++)
			{
				if(i == pagerPosition)
				{
					if(((ImageView)llPagerTab.getChildAt(i)) != null)
						((ImageView)llPagerTab.getChildAt(i)).setImageResource(R.drawable.pager_dot_h);
				}
				else
				{
					if(((ImageView)llPagerTab.getChildAt(i)) != null)
						((ImageView)llPagerTab.getChildAt(i)).setImageResource(R.drawable.pager_dot);
				}
			}
		}
			
	}
	
}
