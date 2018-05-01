package com.winit.sfa.salesman;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MonthlyDataDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.map.RouteMapActivity;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LatlangUtility;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.onConnectionSuccessful;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class SalesmanCheckIn extends BaseActivity implements LocationResult,onConnectionSuccessful
{
	//declaration of variables
	private LinearLayout llMain,ll_lastorder,llLastFiveOrder,CutomerTypeInfo, llCreditLimit,llOSPLimit,llGraphLayout,
			llCustomerDiscount,llCustomerExcessAmount,llMonthWiseData,llSales;
	private HorizontalScrollView llSalesMonth;
	private Button btnCheckIn, btnFindRoute, btnEditDetail, btnTeleOrder;
	private TextView tvHeadTitle,tv_lastorderdatevalue,tv_availimittext12,tv_Aed,tv_Aed12,tv_monthlysalesAed,tv_availimittext1,tv_lastamountvalue,tv_lastavgamountvalue,tv_editdate,tv_channeltype,tv_paymenttype,tv_storegrowth,tv_paymentrem,/*tvtitle,*/tvmonthlysalesvalue, tvCustomerAddress, tvCustomerName,tvCustomerCreditAvailValue, tvCustomerCreditValue,
			tvCustomerOutStandingBalanceValue, tvHeadTitleHistory,
			tv_storegrowthpercentage, tv_channelName, tv_paymentName,
			tv_creditAed,tv_availAed,tvCustomerFoodOutStandingBalanceValue, tvSpecialDay, tvMobileNumber, tvContactPerson,tvCustomerCodeValue,
			tv_lastorderdatevalue12,tv_Paymenttermvalue,tvCustomerDiscount,tvCustomerExcess,tvCustomerFoodDiscount,tvCustomerTPTOutStandingBalanceValue,tvCustomerTPTDiscount;
	private JourneyPlanDO object;
	private LocationUtility locationUtility;
	private CustomerCreditLimitDo creditLimit;
	private ImageView img_edit;
	private int month, year, day;
	private Calendar calendar;
	private boolean edit=false;
	private EditText et_editlatlang;
	private final int START_DATE_DIALOG_ID_DOB = 1;
	private Vector<TrxHeaderDO> vecLastOrders;
	private boolean fromCustomerInfo=false;
	private CustomerDao objCustomerDao;
	private CustomerDao dummy,dummyCustomerDao2;
	private LatlangUtility latlangUtility;
	String sdcardpath = Environment.getExternalStorageDirectory().toString() +"/"+"VisibilityAgreement.pdf";
	String fileurl ="https://app.box.com/s/j1u7aizf560wnjsgd0ym";
	private static final int GPS_NETWORK_ENABLED = 100;
	private static final int GPS_ONLY_ENABLED = 200;
	private static final int GPS_NETWORK_DISABLED = 300;
	private boolean checkedIn = false;
	private String isPerfectStore= "0";
	private View view;
	private boolean showGeoCodePopUp=true;
	private GoogleMap googleMap;


	private ArrayList<MonthlyDataDO> arrMonthlyDataDO,arrStoreGrowthDataDO;
	private String str="";

	boolean fromForceCheckin=false;


	@Override
	public void initialize()
	{
		llMain 		= (LinearLayout)inflater.inflate(R.layout.checkin_new1, null);
		llBody.addView(llMain,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

		if(btnCheckOut != null)
		{
			ivLogOut.setVisibility(View.GONE);
			btnCheckOut.setVisibility(View.GONE);
		}

		locationUtility  = new LocationUtility(SalesmanCheckIn.this);


		intialiseControls();
		showLoader("Loading customer data...");
		startLocationUpdates();

		calendar 	= 	Calendar.getInstance();
		year  		= 	calendar.get(Calendar.YEAR);
		month 		= 	calendar.get(Calendar.MONTH);
		day   		= 	calendar.get(Calendar.DAY_OF_MONTH);

		if(getIntent().getExtras() != null)
		{
			object	            	= 	(JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");

			object.reasonForSkip 	= 	getIntent().getExtras().getString("reason");

			if(getIntent().hasExtra("fromCustomerInfo"))
				fromCustomerInfo = getIntent().getExtras().getBoolean("fromCustomerInfo");

			if(getIntent().hasExtra("isPerfectStore"))
				isPerfectStore = getIntent().getExtras().getString("isPerfectStore");
		}

		isPerfectStoreBase = isPerfectStore;
//		loadSettingsforGeoCode();

//		tvtitle.setText("Checkin");
		if(fromCustomerInfo)
		{
			btnCheckIn.setVisibility(View.GONE);
			btnTeleOrder.setVisibility(View.GONE);
			tvHeadTitle.setText("Customer Info");

			StatusDO statusDO = new StatusDO();
			statusDO.UUid ="";
			statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
			statusDO.Customersite =mallsDetailss.site;
			statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
			statusDO.Visitcode=mallsDetailss.VisitCode;
			statusDO.JourneyCode = mallsDetailss.JourneyCode;
			statusDO.Status = "0";
			statusDO.Action = AppConstants.Action_CheckIn;
			statusDO.Type = AppConstants.Type_CustomerInfo;
			new StatusDA().insertOptionStatus(statusDO);

			uploadData();
		}
		else
		{
			tvHeadTitle.setText("Checkin");
		}
		if(object != null)
		{
//			tv_channelName.setText(object.channelCode);
			if(object.customerPaymentType.equals(""))
				tv_paymentName.setText("N/A");
			else
				tv_paymentName.setText(object.customerPaymentType+"");

			tvCustomerName.setText(object.siteName.trim());
			tvCustomerAddress.setText(getAddress(object));
			tv_Paymenttermvalue.setText(object.paymentTermCode);
			tvContactPerson	.setText("N/A");
			tvMobileNumber.setText("N/A");
			tvSpecialDay.setText("N/A");

			if(object.PromotionalDiscount == null || object.PromotionalDiscount.equalsIgnoreCase(""))
				tvCustomerDiscount.setText("N/A");
			else
				tvCustomerDiscount.setText(object.PromotionalDiscount+"%");

			if(object.FInvDiscYH == null || object.FInvDiscYH.equalsIgnoreCase(""))
				tvCustomerFoodDiscount.setText("N/A");
			else
				tvCustomerFoodDiscount.setText(object.FInvDiscYH+"%");
			if(object.TInvDiscYH == null || object.TInvDiscYH.equalsIgnoreCase(""))

				tvCustomerTPTDiscount.setText("N/A");
			else
				tvCustomerTPTDiscount.setText(object.TInvDiscYH+"%");

		/*commented by vinod about replace by above statements
		* *//**********Change later when food discount is available***************//*
//			if(object.PromotionalDiscount == null || object.PromotionalDiscount.equalsIgnoreCase(""))
			tvCustomerFoodDiscount.setText("N/A");
//			else
//				tvCustomerFoodDiscount.setText(object.PromotionalDiscount+"%");*/

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					CustomerDA customerDA 	= new CustomerDA();
					objCustomerDao 			= customerDA.getCustomerDetails(object.site);
					arrMonthlyDataDO 		= customerDA.getMonthlyData(object.site);
					arrStoreGrowthDataDO 	= customerDA.getStoreGrowthData(object.site);
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							if(objCustomerDao != null)
								bindData(objCustomerDao);

							if(arrMonthlyDataDO != null && arrMonthlyDataDO.size() > 0)
								setupMonthlyData();
							else
								llGraphLayout.setVisibility(View.GONE);
							float customerGrowth = 0.0f;
							if(arrStoreGrowthDataDO != null && arrStoreGrowthDataDO.size() > 1)
							{
								customerGrowth = (StringUtils.getFloat(arrStoreGrowthDataDO.get(0).month_sales)/
										StringUtils.getFloat(arrStoreGrowthDataDO.get(1).month_sales));
								if(customerGrowth > 1)
									customerGrowth = 1 - customerGrowth;
							}
							tv_storegrowthpercentage.setText((int)(customerGrowth*100)+"%");
						}

					});
				}
			}).start();
			tvCustomerCodeValue.setText(object.site);
		}

		llGraphLayout.setVisibility(View.VISIBLE);
		if(object.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			CutomerTypeInfo.setVisibility(View.GONE);
			llCreditLimit.setVisibility(View.GONE);
			llOSPLimit.setVisibility(View.GONE);
		}

		if(object.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
		{
			tvCustomerOutStandingBalanceValue.setText("N/A");
			tvCustomerCreditAvailValue.setText("N/A");
			tvCustomerCreditValue.setText("N/A");
		}

		btnTeleOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SalesmanCheckIn.this, SalesManRecommendedOrder.class);
				intent.putExtra("mallsDetails",object);
				intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER());
				intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER());
				startActivity(intent);
			}
		});

		btnCheckIn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

                if(object.GeoCodeFlag!=null && 	object.GeoCodeFlag.equalsIgnoreCase("false"))
                {
					prePerformCheck(v);
				}
				if(objCustomerDao != null)
				{
//					doCheckIn(str);
					if(StringUtils.getFloat(objCustomerDao.GeoCodeX) == 0 && StringUtils.getFloat(objCustomerDao.GeoCodeY) == 0)
					{
						showCustomDialog(SalesmanCheckIn.this, getString(R.string.warning), "Customer GeoCode is not set.\nDo you wish to set now?", getString(R.string.Yes), getString(R.string.No), "geocode");
					}
					else
					{
						prePerformCheck(v);
					}
				}
			}
			private void prePerformCheck (View v)
			{
				v.setEnabled(false);
				v.setClickable(false);
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						btnCheckIn.setEnabled(true);
						btnCheckIn.setClickable(true);
					}
				}, 200);
				performCheckIn();

				//Testing
//				showLoader("Validating your current location !","Checking you in !");
//				new Handler().postDelayed(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						performCheckIn();
//					}
//				}, 1000);
			}
		});


		img_edit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				img_edit.setClickable(false);
				img_edit.setEnabled(false);

				LinearLayout view  = (LinearLayout) LayoutInflater.from(SalesmanCheckIn.this).inflate(R.layout.chekin_edit_popup, null);

				final EditText et_editcontactperson = (EditText)view.findViewById(R.id.et_editcontactperson);
				final EditText et_editcontactnumber = (EditText)view.findViewById(R.id.et_editcontactnumber);
				final EditText et_editspecialday = (EditText)view.findViewById(R.id.et_editspecialday);
				et_editlatlang = (EditText)view.findViewById(R.id.et_editlatlang);
				LinearLayout llCurrentLocation = (LinearLayout)view.findViewById(R.id.llCurrentLocation);
				tv_editdate = (TextView)view.findViewById(R.id.tv_editdate);
				final Button btn_Saveedit = (Button)view.findViewById(R.id.btn_Saveedit);
				final Button btn_Canceledit = (Button)view.findViewById(R.id.btn_Canceledit);

				googleMap       =  ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mMapFragment)).getMap();


				ImageView ivCapLocETBG = (ImageView)view.findViewById(R.id.ivCapLocETBG);

				if(objCustomerDao != null)
				{
					if(objCustomerDao.ContactPersonName != null && !objCustomerDao.ContactPersonName.equalsIgnoreCase(""))
						et_editcontactperson.setText(objCustomerDao.ContactPersonName);

					if(objCustomerDao.MobileNumber1 != null && !objCustomerDao.MobileNumber1.equalsIgnoreCase(""))
						et_editcontactnumber.setText(objCustomerDao.MobileNumber1);

					if(objCustomerDao.vecCustomerSplDay != null && objCustomerDao.vecCustomerSplDay.size() > 0)
					{
						if(objCustomerDao.vecCustomerSplDay.get(0).description != null && !objCustomerDao.vecCustomerSplDay.get(0).description.equalsIgnoreCase(""))
							et_editspecialday.setText(objCustomerDao.vecCustomerSplDay.get(0).description);

						if(objCustomerDao.vecCustomerSplDay.get(0).specialDay != null && !objCustomerDao.vecCustomerSplDay.get(0).specialDay.equalsIgnoreCase(""))
							tv_editdate.setText(objCustomerDao.vecCustomerSplDay.get(0).specialDay);
					}
					if(!TextUtils.isEmpty(objCustomerDao.GeoCodeX) && !TextUtils.isEmpty(objCustomerDao.GeoCodeY))
					{
						et_editlatlang.setText(""+objCustomerDao.GeoCodeX+" , "+objCustomerDao.GeoCodeY);
//						showMapWithMarkers(StringUtils.getDouble(objCustomerDao.GeoCodeX), StringUtils.getDouble(objCustomerDao.GeoCodeY), object.siteName.trim());

						if(googleMap!=null)
						{
							googleMap.clear();
							MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(StringUtils.getDouble(objCustomerDao.GeoCodeX), StringUtils.getDouble(objCustomerDao.GeoCodeY)));
							markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));
							markerOptions.title(object.siteName);
							googleMap.addMarker(markerOptions);
							googleMap.animateCamera(  CameraUpdateFactory.newLatLngZoom(new LatLng(StringUtils.getDouble(objCustomerDao.GeoCodeX), StringUtils.getDouble(objCustomerDao.GeoCodeY)),16.0f));
							googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
								public void onMapClick(LatLng CLICKED_LOC) {
									double lx,ly;
									lx = CLICKED_LOC.latitude;
									ly = CLICKED_LOC.longitude;
//									showToast(lx+" : "+ly);
								}
							});
						}
					}

				}


				final Dialog dialogVoice = new Dialog(SalesmanCheckIn.this,android.R.style.Theme_Holo_Dialog);
				dialogVoice.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogVoice.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				dialogVoice.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				dialogVoice.setContentView(view);
				dialogVoice.show();
				dialogVoice.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						FragmentManager fm = getSupportFragmentManager();
						Fragment fragment = (fm.findFragmentById(R.id.mMapFragment));
						FragmentTransaction ft = fm.beginTransaction();
						ft.remove(fragment);
						ft.commit();
					}
				});
//			    llCurrentLocation.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//
//						edit = true;
//						showLoader(getString(R.string.Capturing_please_wait));
//						
//						if(isGPSEnable(SalesmanCheckIn.this))
//						{
//							if(edit)
//							{
//								new Handler().postDelayed(new Runnable() 
//								{
//								   @Override
//								   public void run() 
//								   {
//									   hideLoader();
//										edit = false;
//										if(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0) > 0 && preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0) > 0)
//											et_editlatlang.setText(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0)+","+ preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0));
//										else
//											showCustomDialog(SalesmanCheckIn.this, "Alert", "Unable to fetch your current location please try again.", "ok", "", "LatLongs");
//								   }
//								}, 2 * 1000);
//							}
//						}
//						else
//						{
//							hideLoader();
//							showCustomDialog(SalesmanCheckIn.this, getString(R.string.warning), "Please check your location settings and turn on your GPS to capture the location co-ordinates.", getString(R.string.OK), null, "");
//						}
//					}
//				});

				ivCapLocETBG.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						et_editlatlang.performClick();
					}
				});
				et_editlatlang.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						edit = true;
						showLoader(getString(R.string.Capturing_please_wait));

						if(isGPSEnable(SalesmanCheckIn.this))
						{
							if(edit)
							{
								new Handler().postDelayed(new Runnable()
								{
									@Override
									public void run()
									{
										hideLoader();
										edit = false;
										if(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0) > 0 && preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0) > 0)
										{
											et_editlatlang.setText(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0)+","+ preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0));
//											showMapWithMarkers(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0), object.siteName.trim());
											str= "GeoCodeUpdated";
											if(googleMap!=null)
											{
												googleMap.clear();
												MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0)));
												markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map));
												markerOptions.title(object.siteName);
												googleMap.addMarker(markerOptions);
												googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0)),16.0f));
											}

										}
										else
											showCustomDialog(SalesmanCheckIn.this, "Alert", "Unable to fetch your current location please try again.", "ok", "", "LatLongs");
									}
								}, 2 * 1000);
							}
						}
						else
						{
							hideLoader();
							showCustomDialog(SalesmanCheckIn.this, getString(R.string.warning), "Please check your location settings and turn on your GPS to capture the location co-ordinates.", getString(R.string.OK), null, "");
						}

					}
				});

				tv_editdate.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						showDialog(START_DATE_DIALOG_ID_DOB);;
					}
				});
				btn_Saveedit.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(isNetworkConnectionAvailable(SalesmanCheckIn.this))
						{
							et_editcontactperson.isEnabled();
							et_editcontactnumber.isEnabled();
//						   xxx
							//-----------------
							str = (String) "UpdateGeoCode";
							CustomerDA customerDA 	= new CustomerDA();
							dummy 			= customerDA.getCustomerDetails(object.site);
							dummyCustomerDao2  	= customerDA.getCustomerDetails(object.site);

							if(et_editcontactperson.getText().toString().trim()!=null && !et_editcontactperson.getText().toString().equalsIgnoreCase(""))
								dummyCustomerDao2.ContactPersonName =  et_editcontactperson.getText().toString();
							if(et_editcontactnumber.getText().toString().trim()!=null && !et_editcontactnumber.getText().toString().equalsIgnoreCase(""))
								dummyCustomerDao2.MobileNumber1 = et_editcontactnumber.getText().toString();

							if(!preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0").equalsIgnoreCase("0") &&  !preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0").equalsIgnoreCase("0"))
							{
								dummy.GeoCodeX=preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0");
								dummy.GeoCodeY=preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0");
								if(str !=null &&(str.equalsIgnoreCase("UpdateGeoCode") || str.equalsIgnoreCase("Outlet moved") ))
								{

									new Thread(new Runnable() {

										@Override
										public void run() {
											if(isNetworkConnectionAvailable(SalesmanCheckIn.this))
											{
												str="UpdateGeoCode";
												uploadCustomerspeialDay();
												uploadCustomerDetails();
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														img_edit.setClickable(true);
														img_edit.setEnabled(true);
														dialogVoice.dismiss();
													}
												});
											}
											else
											{
												showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),"Your Request Is Not Sent To Admin..Please, Turn On Your Internet To Send A Request To Admin For Force CheckIn ", "Ok","", "");
											}

										}
										private boolean uploadCustomerDetails()
										{


											try
											{
												Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
												vecCustomerDao.add(dummyCustomerDao2);
												if(vecCustomerDao != null && vecCustomerDao.size() > 0)
												{



													ConnectionHelper connectionHelper = new ConnectionHelper(null);
													boolean isUploaded = connectionHelper.sendRequest(SalesmanCheckIn.this, BuildXMLRequest.updateCustomerDetails(vecCustomerDao), ServiceURLs.UpdateCustomerDetails);
													if(isUploaded)
													{
														new CustomerDA().updateTBLCustomer(vecCustomerDao);
														return true;
													}


												}
											}
											catch(Exception e)
											{
												e.printStackTrace();

											}
											return false;
										}
									}).start();

								}
								else {
									doCheckIn(str);
								}
							}
							else {
								showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),"Current Location not Capture Successfully! Please Try Again..", "Ok","", "showEditscreen");
							}
							//-----------------

						}
						else
						{
							showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),getString(R.string.no_internet),getString(R.string.OK),null,"");
						}




//						if(et_editcontactperson.getText().toString().trim()!=null && !et_editcontactperson.getText().toString().equalsIgnoreCase(""))
//							object.contectPersonName = et_editcontactperson.getText().toString();
//						if(et_editcontactnumber.getText().toString().trim()!=null && !et_editcontactnumber.getText().toString().equalsIgnoreCase(""))
//							object.mobileNo1 = et_editcontactnumber.getText().toString();
//						if(et_editspecialday.getText().toString().trim()!=null && !et_editspecialday.getText().toString().equalsIgnoreCase(""))
//							object.dateOfJourny = et_editspecialday.getText().toString();
//
//						new Thread(new Runnable()
//						{
//							private boolean uploadCustomerDetails()
//							{
//
//
//								try
//								{
////			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerDao();
//									Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerExcess();
//									if(vecCustomerDao != null && vecCustomerDao.size() > 0)
//									{
//
//
//
//										ConnectionHelper connectionHelper = new ConnectionHelper(null);
//										boolean isUploaded = connectionHelper.sendRequest(SalesmanCheckIn.this, BuildXMLRequest.updateCustomerDetails(vecCustomerDao), ServiceURLs.UpdateCustomerDetails);
//										if(isUploaded)
//										{
////					new CustomerDA().updateCustomerDao(vecCustomerDao);
//											new CustomerDA().updateTBLCustomer(vecCustomerDao);
//											return true;
//										}
//
//
//									}
//								}
//								catch(Exception e)
//								{
//									e.printStackTrace();
//
//								}
//								return false;
//							}
//							@Override
//							public void run()
//							{
//								try
//								{
//									objCustomerDao 								= new CustomerDao();
//									objCustomerDao.site 							= object.site;
//									objCustomerDao.MobileNumber1 				= object.mobileNo1;
//									objCustomerDao.ContactPersonName				= object.contectPersonName;
//									String geoLocation[] 						= et_editlatlang.getText().toString().split(",");
//
//									if(geoLocation!=null && geoLocation.length == 2)
//									{
//										objCustomerDao.GeoCodeX = geoLocation[0].trim();
//										objCustomerDao.GeoCodeY = geoLocation[1].trim();
//									}
//
//									CustomerSpecialDayDO objCustomerSpecialDayDO	= new CustomerSpecialDayDO();
//									objCustomerSpecialDayDO.customerCode			= object.site;
//									objCustomerSpecialDayDO.specialDay 			= tv_editdate.getText().toString();
//									objCustomerSpecialDayDO.description 			= et_editspecialday.getText().toString();//reason for being special
//									objCustomerSpecialDayDO.status		 		= "N";
//									objCustomerDao.vecCustomerSplDay.add(objCustomerSpecialDayDO);
//									new CustomerDA().updateCustomerLocation(objCustomerDao);
//
//									runOnUiThread(new Runnable()
//									{
//										@Override
//										public void run()
//										{
//											if(uploadCustomerDetails())
//											showToast(getString(R.string.customer_details_are_updated_successfully));
//
//											if(objCustomerDao != null)
//												bindData(objCustomerDao);
//										}
//									});
//
//								}
//								catch(Exception e)
//								{
//									e.printStackTrace();
//								}
//								finally
//								{
//									runOnUiThread(new Runnable()
//									{
//										@Override
//										public void run() {
//											img_edit.setClickable(true);
//											img_edit.setEnabled(true);
//											dialogVoice.dismiss();
//										}
//
//									});
//								}
//							}
//						}).start();
//					   }
//					   else
//					   {
//						   showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),getString(R.string.no_internet),getString(R.string.OK),null,"");
//					   }
					}
				});

				btn_Canceledit.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						img_edit.setClickable(true);
						img_edit.setEnabled(true);

						dialogVoice.dismiss();
					}
				});
			}
		});



		new Thread(new Runnable() {
			@Override
			public void run() {

				String empNO  = preference.getStringFromPreference(Preference.EMP_NO,"");
				vecLastOrders 		= new CommonDA().getLastFiveOrder(empNO,object.site);
				final String channelName = new CustomerDA().getChannelName(object.channelCode);
				runOnUiThread(new Runnable() {

					@Override
					public void run()
					{
						tv_channelName.setText(channelName+"");
						if(vecLastOrders!=null && vecLastOrders.size()>0)
						{
							String date = vecLastOrders.get(0).trxDate;
							tv_lastorderdatevalue.setText(CalendarUtils.getFormatedDatefromString(date));
							float avgval = 0;
							int lastInvoiceCount = 0;
							if(vecLastOrders != null && vecLastOrders.size() > 5)
							{
								for(int i=0;i<vecLastOrders.size();i++)
									avgval = avgval+(vecLastOrders.get(i).totalAmount - vecLastOrders.get(i).totalDiscountAmount);
								lastInvoiceCount = 5;
							}
							else if(vecLastOrders != null)
							{
								for(int i=0;i<vecLastOrders.size();i++)
									avgval = avgval+(vecLastOrders.get(i).totalAmount - vecLastOrders.get(i).totalDiscountAmount);
								lastInvoiceCount = vecLastOrders.size();
							}
							float lastavgval =avgval/vecLastOrders.size();
//							tv_lastorderdatevalue12.setText("Last "+lastInvoiceCount+" Invoices");
							llLastFiveOrder.setClickable(true);
							llLastFiveOrder.setEnabled(true);
							ll_lastorder.setClickable(true);
							ll_lastorder.setEnabled(true);
//							tv_lastavgamountvalue.setText(amountFormate.format(lastavgval)+"");
							tv_lastavgamountvalue.setText(vecLastOrders.size()+"");
							tv_lastamountvalue.setText(amountFormate.format((vecLastOrders.get(0).totalAmount - vecLastOrders.get(0).totalDiscountAmount + vecLastOrders.get(0).totalVATAmount))+"");
						}else{
							tv_lastorderdatevalue.setText("N/A");
							tv_lastorderdatevalue12.setText("N/A");
							llLastFiveOrder.setClickable(false);
							llLastFiveOrder.setEnabled(false);
							ll_lastorder.setClickable(false);
							ll_lastorder.setEnabled(false);
//							tv_lastavgamountvalue.setText(amountFormate.format(0.00)+"");
							tv_lastavgamountvalue.setText(0+"");
							tv_lastamountvalue.setText(amountFormate.format(0.00)+"");
						}

					}
				});
			}
		}).start();
		btnFindRoute.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Need to work on it
				Intent intent = new Intent(SalesmanCheckIn.this, RouteMapActivity.class);
				intent.putExtra("Latitude", object.geoCodeX);
				intent.putExtra("Longitude", object.geoCodeY);
				startActivity(intent);
			}
		});
		ll_lastorder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if(vecLastOrders.size()>0)
				{
					Intent intent = new Intent(SalesmanCheckIn.this, OrderSummaryDetail.class);
					intent.putExtra("trxHeaderDO", vecLastOrders.get(0));
					intent.putExtra("mallsDetails", mallsDetailss);
					startActivity(intent);
				}

			}
		});
		llLastFiveOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{

				Intent in = new Intent(SalesmanCheckIn.this, LastFiveOrdersActivity.class);
				in.putExtra("mallsDetails", object);
				startActivity(in);

			}
		});
		btnEditDetail.setVisibility(View.GONE);
		btnEditDetail.setText(" Update Location ");
		btnEditDetail.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				//Need to work on it
				Intent intent = new Intent(SalesmanCheckIn.this, UpdateCustomerDetail.class);
				intent.putExtra("mallsDetails", object);
				startActivityForResult(intent, 5000);
			}
		});
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						
//						TextView a6	= (TextView) llMain.findViewById(R.id.a1);
//						TextView a7	= (TextView) llMain.findViewById(R.id.a3);
//						
//						a6.setBackgroundColor(Color.parseColor("#0A94D1"));
//						a7.setBackgroundColor(Color.parseColor("#545454"));
//					}
//				});
//			}
//		}).start();
	}

	private boolean loadSettingsforGeoCode()
	{
		new Thread(new Runnable() {

			@Override
			public void run() {

				showGeoCodePopUp = new CommonDA().getGeoCodeStatusfromSettings();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {


					}
				});

			}
		}).start();

		return showGeoCodePopUp;
	}

	private void setupMonthlyData()
	{
		for (int i = 0; i < arrMonthlyDataDO.size(); i++)
		{
			View view = LayoutInflater.from(SalesmanCheckIn.this).inflate(R.layout.monthlyhistory, null);
			TextView tvMonth		=	(TextView) view.findViewById(R.id.tvMonth);
			TextView tvSales		=	(TextView) view.findViewById(R.id.tvSales);
			TextView tvBills		=	(TextView) view.findViewById(R.id.tvBills);
			ImageView ivVerticalDiv	=	(ImageView) view.findViewById(R.id.ivVerticalDiv);

			if(i == (arrMonthlyDataDO.size()-1))
				ivVerticalDiv.setVisibility(View.INVISIBLE);

			tvMonth.setText(arrMonthlyDataDO.get(i).month_year);
			tvSales.setText(arrMonthlyDataDO.get(i).month_sales);
			tvBills.setText(arrMonthlyDataDO.get(i).month_bills);

			if(StringUtils.getFloat(arrMonthlyDataDO.get(i).month_sales) > 0 &&
					StringUtils.getInt(arrMonthlyDataDO.get(i).month_bills) > 0)
				llMonthWiseData.addView(view);
		}
	}

	public void getCustomerLimit()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				creditLimit = new CustomerDA().getCustomerCreditLimit(object.site);

				final String customerGrowth =  new CustomerDA().getCustomerStoreGrowth(object.customerId);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						tvCustomerCreditValue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.creditLimit))+"");
						tvCustomerCreditAvailValue.setText(getAvailableLimit());
						tvCustomerOutStandingBalanceValue.setText(getOutStandingAmount());
						tvCustomerFoodOutStandingBalanceValue.setText(getOutStandingFoodAmount());
						tvCustomerTPTOutStandingBalanceValue.setText(getOutStandingTPTAmount());
						tvmonthlysalesvalue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.monthlySalesAmount))+"");
//						tv_storegrowthpercentage.setText(customerGrowth+"%");

						if(getExcessPayment() != null && !getExcessPayment().equalsIgnoreCase(""))
							tvCustomerExcess.setText(getExcessPayment() + " AED");

						if(object.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
						{
							tvCustomerOutStandingBalanceValue.setText("N/A");
							tvCustomerCreditAvailValue.setText("N/A");
							tvCustomerCreditValue.setText("N/A");
							tv_creditAed.setText("");
							tv_availAed.setText("");
						}
					}
				});
			}

		}).start();
	}

	private String getAvailableLimit()
	{
		float value = StringUtils.getFloat(creditLimit.availbleLimit);
		if(value > 0)
			return amountFormate.format(value)+"";
		return amountFormate.format(0)+"";
	}
	private String getExcessPayment()
	{
		if(creditLimit != null && creditLimit.excessPayment != null)
		{
			float value = StringUtils.getFloat(creditLimit.excessPayment);
			if(value > 0)
				return amountFormate.format(value)+"";
		}
		return amountFormate.format(0)+"";
	}
	private String getOutStandingAmount()
	{
		float valueoutStandingAmount = StringUtils.getFloat(creditLimit.outStandingAmount);
		return amountFormate.format(valueoutStandingAmount)+"";
	}
	private String getOutStandingFoodAmount()//need to change as per food
	{
		float valueoutStandingAmount = StringUtils.getFloat(creditLimit.outStandingFoodAmount);
		return amountFormate.format(valueoutStandingAmount)+"";
	}
	private String getOutStandingTPTAmount()//need to change as per food
	{
		float valueoutStandingAmount = StringUtils.getFloat(creditLimit.outStandingTPTAmount );
		return amountFormate.format(valueoutStandingAmount)+"";
	}
	public void performCheckIn()
	{
		object.ActualArrivalTime = CalendarUtils.getCurrentDateTime();
		object.reasonForSkip = object.reasonForSkip;


		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if(!preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
						showLoader("Validating Credit Limit");
					CustomerDao customerDao = new CustomerDA().getCustomerDetails(object.site);
					boolean  isBlock = new CustomerDA().getCustomerExcessNew(object.site);

					double outletLat = StringUtils.getDouble(customerDao.GeoCodeX);
					double outletLon = StringUtils.getDouble(customerDao.GeoCodeY);

//					double dist =haversine(outletLat,outletLon,preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.261987), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.398717));
					double dist=0.0f;
					if(object.GeoCodeFlag!=null && object.GeoCodeFlag.equalsIgnoreCase("false"))
					{
						doCheckIn(str);
					}
					else if((!isBlock) && dist <= AppConstants.DISTANCE )// considering 5 meters /*LocationUtils.getDist(outletLat, outletLon, preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.261987), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.398717)) > 2000)*/
						doCheckIn(str);
					else
					{
						hideLoader();
						fromForceCheckin=false;
						showCustomDialog(SalesmanCheckIn.this, "Warning !", "Current location is not matching with customer location, please try again","OK", "Force checkin", "ForceCheckIn");
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();

//		new Handler().postDelayed(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				try
//				{
//					CustomerDao customerDao = new CustomerDA().getCustomerDetails(object.site);
//					boolean  isBlock = new CustomerDA().getCustomerExcessNew(object.site);
//
//					double outletLat = StringUtils.getDouble(customerDao.GeoCodeX);
//					double outletLon = StringUtils.getDouble(customerDao.GeoCodeY);
//
//					double dist =haversine(outletLat,outletLon,preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.261987), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.398717));
//					if(object.GeoCodeFlag!=null && object.GeoCodeFlag.equalsIgnoreCase("false"))
//					{
//						doCheckIn(str);
//					}
//					else if((!isBlock) && dist < 50 )// considering 5 meters /*LocationUtils.getDist(outletLat, outletLon, preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.261987), preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.398717)) > 2000)*/
//						doCheckIn(str);
//					else
//					{
//						hideLoader();
//						fromForceCheckin=false;
//						showCustomDialog(SalesmanCheckIn.this, "Warning !", "Current location is not matching with customer location, please try again","OK", "Force checkin", "ForceCheckIn");
//					}
//
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		},1000);
	}
	public double haversine(double lat1, double lon1, double lat2, double lon2) {
		double Rad = 6372.8; //Earth's Radius In kilometers
		// TODO Auto-generated method stub
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
//        haverdistanceKM = Rad * c;
		return  Rad * c*1000;

	}
	private void doCheckIn(final String Reason){
		try {
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					checkedIn = true;
					stopLocationUpdate();
					object.JourneyCode = preference.getStringFromPreference(Preference.USER_ID, "")+CalendarUtils.getOrderPostDate();
					object.VisitCode   = preference.getStringFromPreference(Preference.USER_ID, "")+object.site+CalendarUtils.getCurrentDateTime();
					//to save checked in customer detail in preference for future use
					saveCustomerDetail();
					new CustomerDA().updateLastJourneyLog();
					insertCustomerVisit(Reason);

					preference.saveStringInPreference(Preference.LAST_CUSTOMER_SITE_ID, object.site);
					preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, object.site);
					preference.saveMallsDetailsObjectInPreference(Preference.CUSTOMER_DETAIL, object);
					preference.saveBooleanInPreference(Preference.IS_TELE_ORDER, false);
					preference.commitPreference();
					object.reasonForSkip = "";

					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							hideLoader();
							if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_AM))
							{
								Intent intent = new Intent(SalesmanCheckIn.this, CustomerAssetsListActivity.class);
								intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
								intent.putExtra("mallsDetails",object);
								startActivity(intent);
								finish();
							}
							else
							{
								Intent intent = new Intent(SalesmanCheckIn.this, CheckInOptionActivity.class);
								intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
								intent.putExtra("mallsDetails",object);
								intent.putExtra("from", "checkin");
								intent.putExtra("isForceCheckIn", fromForceCheckin);
								startActivity(intent);
								finish();
							}
						}
					});
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void saveCustomerDetail()
	{
		if(object != null)
		{
			AppConstants.hmCapturedInventory = null;
			AppConstants.VendingMachineName =  object.siteName;
			preference.saveStringInPreference(Preference.CUSTOMER_SITE_ID, object.site);
			preference.commitPreference();
		}
	}
	private void bindData(CustomerDao objCustomerDao){
		try {
			if(objCustomerDao.ContactPersonName != null && !objCustomerDao.ContactPersonName.equalsIgnoreCase(""))
				tvContactPerson.setText(objCustomerDao.ContactPersonName);
			else
				tvContactPerson	.setText("N/A");

			if(objCustomerDao.MobileNumber1 != null && !objCustomerDao.MobileNumber1.equalsIgnoreCase(""))
				tvMobileNumber.setText(objCustomerDao.MobileNumber1);
			else
				tvMobileNumber.setText("N/A");

			if(objCustomerDao.vecCustomerSplDay != null && objCustomerDao.vecCustomerSplDay.size() > 0)
			{
				if(objCustomerDao.vecCustomerSplDay.get(0).specialDay != null && !objCustomerDao.vecCustomerSplDay.get(0).specialDay.equalsIgnoreCase(""))
				{
					if(objCustomerDao.vecCustomerSplDay.get(0).description != null && !objCustomerDao.vecCustomerSplDay.get(0).description.equalsIgnoreCase(""))
						tvSpecialDay.setText(objCustomerDao.vecCustomerSplDay.get(0).description+", "+objCustomerDao.vecCustomerSplDay.get(0).specialDay);
					else
						tvSpecialDay.setText(objCustomerDao.vecCustomerSplDay.get(0).specialDay);
				}
				else if(objCustomerDao.vecCustomerSplDay.get(0).description != null && !objCustomerDao.vecCustomerSplDay.get(0).description.equalsIgnoreCase(""))
					tvSpecialDay.setText(objCustomerDao.vecCustomerSplDay.get(0).description);
				else
					tvSpecialDay.setText("N/A");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** initializing all the Controls  of PresellerCheckIn class **/
	public void intialiseControls()
	{
		btnCheckIn					=	(Button)llMain.findViewById(R.id.btnCheckIn);
		btnFindRoute				=	(Button)llMain.findViewById(R.id.btnFindRoute);
		btnEditDetail				=	(Button)llMain.findViewById(R.id.btnEditDetail);
		btnTeleOrder				=	(Button)llMain.findViewById(R.id.btnTeliOrder);
//		tvtitle          			=	(TextView)llMain.findViewById(R.id.tvHeadTitle);
		tvCustomerName              =	(TextView)llMain.findViewById(R.id.tvCustomerMallName);
		tvCustomerAddress			=	(TextView)llMain.findViewById(R.id.tvCustomerAddress);
		tv_channeltype				=	(TextView)llMain.findViewById(R.id.tv_channeltype);
		tv_paymenttype				=	(TextView)llMain.findViewById(R.id.tv_paymenttype);
		tv_storegrowth				=	(TextView)llMain.findViewById(R.id.tv_storegrowth);
		tv_paymentrem				=	(TextView)llMain.findViewById(R.id.tv_paymentrem);
		tv_Paymenttermvalue			=	(TextView)llMain.findViewById(R.id.tv_Paymenttermvalue);
		tv_lastorderdatevalue		=	(TextView)llMain.findViewById(R.id.tv_lastorderdatevalue);
		tvCustomerCodeValue			=	(TextView)llMain.findViewById(R.id.tvCustomerCodeValue);
		tv_lastamountvalue			=	(TextView)llMain.findViewById(R.id.tv_lastamountvalue);
		tv_lastavgamountvalue		=	(TextView)llMain.findViewById(R.id.tv_lastavgamountvalue);
		img_edit                    =	(ImageView)llMain.findViewById(R.id.img_edit);
		tv_monthlysalesAed			=	(TextView)llMain.findViewById(R.id.tv_monthlysalesAed);
		tv_availimittext12			=	(TextView)llMain.findViewById(R.id.tv_availimittext12);
		tv_Aed						=	(TextView)llMain.findViewById(R.id.tv_Aed);
		tv_Aed12					=	(TextView)llMain.findViewById(R.id.tv_Aed12);
		tvHeadTitle					=	(TextView)llMain.findViewById(R.id.tvHeadTitle);
		tvCustomerCreditValue				=	(TextView)llMain.findViewById(R.id.tvCustomerCreditValue);
		tvCustomerOutStandingBalanceValue 	=	(TextView)llMain.findViewById(R.id.tvCustomerOutStandingBalanceValue);
		tvmonthlysalesvalue 				=	(TextView)llMain.findViewById(R.id.tvmonthlysalesvalue);
		tvCustomerCreditAvailValue			=	(TextView)llMain.findViewById(R.id.tvCustomerCreditAvailValue);
		tvHeadTitleHistory					=	(TextView)llMain.findViewById(R.id.tvHeadTitleHistory);
		tv_storegrowthpercentage			=	(TextView)llMain.findViewById(R.id.tv_storegrowthpercentage);

		tvSpecialDay						=	(TextView)llMain.findViewById(R.id.tvSpecialDay);
		tvMobileNumber						=	(TextView)llMain.findViewById(R.id.tvMobileNumber);
		tv_lastorderdatevalue12				=	(TextView)llMain.findViewById(R.id.tv_lastorderdatevalue12);
		tvContactPerson						=	(TextView)llMain.findViewById(R.id.tvContactPerson);
		tv_creditAed						=   (TextView)llMain.findViewById(R.id.tv_creditAed);
		tv_availAed							=   (TextView)llMain.findViewById(R.id.tv_availAed);
		tvCustomerFoodOutStandingBalanceValue	=   (TextView)llMain.findViewById(R.id.tvCustomerFoodOutStandingBalanceValue);
		tvCustomerTPTOutStandingBalanceValue	=   (TextView)llMain.findViewById(R.id.tvCustomerTPTOutStandingBalanceValue);
		tv_availimittext1					=   (TextView)llMain.findViewById(R.id.tv_availimittext1);
		tv_channelName						=	(TextView)llMain.findViewById(R.id.tv_channelName);
		tv_paymentName						=	(TextView)llMain.findViewById(R.id.tv_paymentName);
		tvCustomerDiscount					=	(TextView)llMain.findViewById(R.id.tvCustomerDiscount);
		tvCustomerFoodDiscount				=	(TextView)llMain.findViewById(R.id.tvCustomerFoodDiscount);
		tvCustomerTPTDiscount				=	(TextView)llMain.findViewById(R.id.tvCustomerTPTDiscount);
		tvCustomerExcess					=	(TextView)llMain.findViewById(R.id.tvCustomerExcess);

		ll_lastorder						=   (LinearLayout)llMain.findViewById(R.id.ll_lastorder);
		CutomerTypeInfo						=   (LinearLayout)llMain.findViewById(R.id.CutomerTypeInfo);
		llGraphLayout						=   (LinearLayout)llMain.findViewById(R.id.llGraphLayout);
		llLastFiveOrder						=	(LinearLayout)llMain.findViewById(R.id.llLastFiveOrder);
		llCustomerDiscount					=	(LinearLayout)llMain.findViewById(R.id.llCustomerDiscount);
		llCustomerExcessAmount				=	(LinearLayout)llMain.findViewById(R.id.llCustomerExcessAmount);
		llMonthWiseData						=	(LinearLayout)llMain.findViewById(R.id.llMonthWiseData);
		llSales								=	(LinearLayout)llMain.findViewById(R.id.llSales);

		llSalesMonth						=	(HorizontalScrollView)llMain.findViewById(R.id.llSalesMonth);

		btnFindRoute.setVisibility(View.VISIBLE);

		setTypeFaceRobotoNormal(llMain);

		tv_creditAed.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerCreditValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerFoodOutStandingBalanceValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerTPTOutStandingBalanceValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerOutStandingBalanceValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_availAed.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerCreditAvailValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_monthlysalesAed.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvmonthlysalesvalue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_availimittext1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_Aed.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_lastamountvalue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_availimittext12.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_Aed12.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_lastavgamountvalue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCustomerName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_channeltype.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_paymenttype.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_storegrowth.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_paymentrem.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeadTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvHeadTitleHistory.setTypeface(AppConstants.Roboto_Condensed_Bold);
	}


	@Override
	public void onResume()
	{
		super.onResume();
		getCustomerLimit();
	}

	@Override
	public void onDestroy()
	{
		stopLocationUpdate();
		super.onDestroy();
	}

	@Override
	public void gotLocation(final Location loc)
	{
		if(loc!=null)
		{
			if(loc.getLatitude() > 0 && loc.getLongitude() > 0)
			{
				preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, ""+loc.getLatitude());
				preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, ""+loc.getLongitude());
//				preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, "58");
//				preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, "90");
				preference.commitPreference();

				LogUtils.errorLog("Lat longs from got location", loc.getLatitude()+"---"+loc.getLongitude());

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(edit)
						{
							edit = false;
							if(loc != null && loc.getLatitude() > 0 && loc.getLongitude() > 0)
								et_editlatlang.setText(""+loc.getLatitude()+" , "+loc.getLongitude());
						}

						if(!checkedIn)
						{
							new Handler().postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									if(locationUtility != null)
										locationUtility.getLocation(SalesmanCheckIn.this);
								}
							}, 30 * 1000);
						}
					}
				});


			}
		}
		else if(!checkedIn)
		{
//			new Handler().postDelayed(new Runnable() 
//			{
//			   @Override
//			   public void run() 
//			   {
			try
			{
				if(locationUtility != null)
					locationUtility.getLocation(SalesmanCheckIn.this);

				Thread.sleep(10 * 1000);
			} catch (InterruptedException e)
			{

				e.printStackTrace();
			}
//			   }
//			}, 10 * 1000);
		}
	}

	@Override
	public void onConnectionException(Object msg)
	{
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 5000 && resultCode == 5000)
			finish();
		else if(requestCode == 1000 && resultCode == RESULT_OK)
		{
//			if(pendingInvoicesPopup!=null&&pendingInvoicesPopup.isShowing())
//				pendingInvoicesPopup.dismiss();
////			performCheckIn();
//			Intent intent = new Intent(SalesmanCheckIn.this, CheckInOptionActivity.class);
//			intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
//			intent.putExtra("mallsDetails",object);
//			intent.putExtra("from", "checkin");
//			startActivity(intent);
//			finish();
		}
	}



	private void insertCustomerVisit(String reason)
	{

		TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
		trxLogDetailsDO.CustomerCode = mallsDetailss.site;
		trxLogDetailsDO.CustomerName = mallsDetailss.siteName;
		trxLogDetailsDO.columnName  = TrxLogHeaders.COL_TOTAL_ACTUAL_CALLS;
		trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
		trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
		trxLogDetailsDO.IsJp  = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetailss.site,trxLogDetailsDO.Date)?"True":"False";
		new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);

		CustomerVisitDO visitDO 		= new CustomerVisitDO();
		visitDO.CustomerVisitId			= StringUtils.getUniqueUUID();
		visitDO.UserCode				= preference.getStringFromPreference(Preference.SALESMANCODE, "");
		visitDO.JourneyCode				= object.JourneyCode;
		visitDO.VisitCode				= object.VisitCode;
		visitDO.ClientCode				= object.site;
		visitDO.Date					= CalendarUtils.getCurrentDateTime();/** Sample : 2014-12-19T12:07:29 */
		visitDO.ArrivalTime				= CalendarUtils.getCurrentDateTime();
		visitDO.OutTime					= "";
		visitDO.TotalTimeInMins			= "";


//		visitDO.Latitude				= ""+preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0);
//		visitDO.Longitude				= ""+preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0);
		if(!TextUtils.isEmpty(reason) && !(preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0)==0))
		{
			visitDO.Latitude				= preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.23568956);
			visitDO.Longitude				= preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 35.56895623);
		}
		else
		{
			visitDO.Latitude				= object.geoCodeX;
			visitDO.Longitude				= object.geoCodeY;
		}

		visitDO.reasonType  = "Force-Checkin";

		visitDO.CustomerVisitAppId		= visitDO.CustomerVisitId;
		visitDO.IsProductiveCall		= "";
		visitDO.TypeOfCall				= isPerfectStore;
		visitDO.Status					= "";
		visitDO.reason					= reason;

		visitDO.vehicleNo				= ""+preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
		visitDO.UserId					= ""+preference.getStringFromPreference(Preference.EMP_NO, "");



		preference.saveStringInPreference(Preference.VISIT_CODE, visitDO.VisitCode);
		preference.commitPreference();
		new CustomerDA().insertCustomerVisits(visitDO);

		uploadData();
	}


	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case START_DATE_DIALOG_ID_DOB:
				return new DatePickerDialog(this, dobListener,  year, month, day);

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener dobListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth)
		{
			year = yearSel;
			month = monthOfYear+1;
			day = dayOfMonth;

			String str = year + "-" + month + "-" + day;

			tv_editdate.setText(CalendarUtils.getFormatedDatefromString(str));
			tv_editdate.setTag(str);
		}
	};

	public double calculateDistance(double lat1,double lat2,double lon1,double lon2)
	{
		int R = 6371; // Radius of the earth in km
		double dLat = Math.toRadians(lat2-lat1);  // Javascript functions 
		double dLon =  Math.toRadians(lon2-lon1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos( Math.toRadians(lat1)) * Math.cos(
						Math.toRadians(lat2)) *
						Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
		d = d*1000;
		return d;

	}

	@Override
	public void onsuccess(final LatLng latLng, long gps_time, double accuracy)
	{
		if(latLng !=null)
		{
			if(latLng.latitude > 0 && latLng.longitude > 0)
			{
				preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, ""+latLng.latitude);
				preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, ""+latLng.longitude);
				preference.commitPreference();

				LogUtils.errorLog("Lat longs from onsuccess", latLng.latitude+"---"+latLng.longitude);

				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(edit)
						{
							edit = false;
							if(latLng != null && latLng.latitude > 0 && latLng.longitude > 0)
								et_editlatlang.setText(""+latLng.latitude+" , "+latLng.longitude);
						}
					}
				});

				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(latlangUtility != null && latlangUtility.getlocationClient().isConnected())
							latlangUtility.getLatLang();
					}
				}, 10 * 1000);
			}
		}
		else if(!checkedIn)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if(latlangUtility != null && latlangUtility.getlocationClient().isConnected())
						latlangUtility.getLatLang();
				}
			}, 10 * 1000);
		}
	}

	@Override
	public void onfail()
	{
		if(latlangUtility != null && latlangUtility.getlocationClient().isConnected())
			latlangUtility.getlocationClient().disconnect();

//		locationUtility.getLocation(SalesmanCheckIn.this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		stopLocationUpdate();

		if(latlangUtility != null && !latlangUtility.getlocationClient().isConnected())
			latlangUtility.getlocationClient().connect();
	}

	private void startLocationUpdates()
	{
		if(checkPlayStoreAvailability())
		{
			if(checkWhichServicesEnabled() != GPS_NETWORK_DISABLED)
			{
				latlangUtility = LatlangUtility.getInstance(SalesmanCheckIn.this);
				latlangUtility.setListner(SalesmanCheckIn.this);

//				Handler handler = new Handler();
//				handler.postDelayed(new Runnable() 
//				{
//					@Override
//					public void run() 
//					{
				if(latlangUtility!=null && latlangUtility.getlocationClient().isConnected())
					latlangUtility.getLatLang();
				else
					locationUtility.getLocation(SalesmanCheckIn.this);

				hideLoader();
//					}
//				}, 2000);
			}
			else
			{
				hideLoader();
				showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),getString(R.string.enable_location_access), "Turn on GPS","No", "GpsTurnOn");
			}
		}
		else
		{
			hideLoader();
//			locationUtility.getLocation(SalesmanCheckIn.this);
		}
	}

	private void stopLocationUpdate()
	{
		if(latlangUtility != null)
			latlangUtility.getlocationClient().disconnect();

		if(locationUtility != null)
			locationUtility.stopGpsLocUpdation();
	}

	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("GpsTurnOn"))
		{
			enableLocationSettings();
		}
		else if(from.equalsIgnoreCase("geocode"))
		{
			img_edit.performClick();
		}else if(from.equalsIgnoreCase("showEditscreen"))
		{
//			img_edit.performClick();
		}if(from.equalsIgnoreCase("ForceCheckIn")){
//				doCheckIn();
		fromForceCheckin= false;


	}
	}

	@Override
	public void onButtonNoClick(String from) {
		super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("ForceCheckIn")){
//				doCheckIn();
			fromForceCheckin= true;
			if(showGeoCodePopUp)
				showForceCheckReason();
			else
				doCheckIn(str);

		}
	}
	private void showForceCheckReason()
	{
		Vector<String> vecStrings = new CommonDA().getReasonBasedOnType(AppConstants.FORCE_CHECKIN);
		if(vecStrings == null || vecStrings.size() == 0)
		{
			vecStrings = new Vector<String>();
			vecStrings.add("Other Reasons");
			vecStrings.add("UpdateGeoCode");
		}
//		vecStrings.add("UpdateGeoCode"); 
		CustomBuilder builder = new CustomBuilder(SalesmanCheckIn.this, "Select Reason", true);
		builder.setSingleChoiceItems(vecStrings, "", new CustomBuilder.OnClickListener()
		{
			@Override
			public void onClick(CustomBuilder builder, Object selectedObject)
			{
//				xxx
				str = (String) selectedObject;
				builder.dismiss();

				CustomerDA customerDA 	= new CustomerDA();
				objCustomerDao 			= customerDA.getCustomerDetails(object.site);
				/*if(!preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0").equalsIgnoreCase("0") &&  !preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0").equalsIgnoreCase("0"))
					doCheckIn(str);
				else {
					showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),"Current Location not Capture Successfully! Please Try Again..", "Ok","", "showEditscreen");
				}*/
				if(!preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0").equalsIgnoreCase("0") &&  !preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0").equalsIgnoreCase("0"))
				{
					final String x =objCustomerDao.GeoCodeX;
					final  String y = objCustomerDao.GeoCodeY;
					objCustomerDao.GeoCodeX=preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0");
					objCustomerDao.GeoCodeY=preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0");

					dummy = new CustomerDao();
					dummy.GeoCodeX = preference.getStringFromPreference(Preference.CUREENT_LATTITUDE,"0");
					dummy.GeoCodeY = preference.getStringFromPreference(Preference.CUREENT_LONGITUDE,"0");
					dummy.site = object.site;
					dummy.ContactPersonName = objCustomerDao.ContactPersonName;
					dummy.MobileNumber1 = objCustomerDao.MobileNumber1;


					if(str !=null &&(str.equalsIgnoreCase("UpdateGeoCode") || str.equalsIgnoreCase("Outlet moved") ))
					{
						builder.dismiss();
						new CustomerDA().updateCustomerLocationNew(objCustomerDao); //changed lat and lng and Blockedstatus
						new Thread(new Runnable() {
							@Override
							public void run() {
								if(isNetworkConnectionAvailable(SalesmanCheckIn.this))
								{
									//code for service
									uploadCustomerspeialDay();

								}
								else
								{
									showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),"Your Request Is Not Sent To Admin..Please, Turn On Your Internet To Send A Request To Admin For Force CheckIn ", "Ok","", "");
								}
								runOnUiThread
										(

												new Runnable() {
													@Override
													public void run() {
														objCustomerDao.GeoCodeX = x;
														objCustomerDao.GeoCodeY = y;
														Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
														vecCustomerDao.add(objCustomerDao);
														new CustomerDA().updateCustomerLocationNew(objCustomerDao); //Changes not done in BlockedStatus
														new CustomerDA().updateTBLCustomer(vecCustomerDao); // Change the blockedstatus column to false
													}
												}
										);
							}
						}).start();

					}
					else {
						doCheckIn(str);
					}
				}
				else {
					showCustomDialog(SalesmanCheckIn.this,getString(R.string.alert),"Current Location not Capture Successfully! Please Try Again..", "Ok","", "showEditscreen");
				}
			}

		});
		builder.show();
	}
	private boolean uploadCustomerspeialDay()
	{

		try
		{
//			Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerDao();
//					Vector<CustomerDao> vecCustomerDao = new CustomerDA().getCustomerExcess();
			Vector<CustomerDao> vecCustomerDao = new Vector<CustomerDao>();
			vecCustomerDao.add(dummy);
			if(vecCustomerDao != null && vecCustomerDao.size() > 0)
			{


				ConnectionHelper connectionHelper = new ConnectionHelper(null);
				boolean isUploaded = connectionHelper.sendRequest(SalesmanCheckIn.this, BuildXMLRequest.updateCustomerGeoDetails(vecCustomerDao,preference.getStringFromPreference(Preference.SALESMANCODE, ""),str), ServiceURLs.UpdateCustomerGeoDetails);
				if(isUploaded)
				{

//					new CustomerDA().updateTBLCustomer(vecCustomerDao);
					showToast("GeoLocations are sent for approval");
					return  true;
				}

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	private int checkWhichServicesEnabled()
	{
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean netWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gpsEnabled && netWorkEnabled)
			return GPS_NETWORK_ENABLED;
		else if (gpsEnabled && (gpsEnabled || netWorkEnabled))
			return GPS_ONLY_ENABLED;
		else
			return GPS_NETWORK_DISABLED;
	}

	public void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(settingsIntent, 112);
	}


}
