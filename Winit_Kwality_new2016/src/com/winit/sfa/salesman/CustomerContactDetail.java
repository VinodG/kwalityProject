package com.winit.sfa.salesman;


import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.alseer.parsers.AddNewCustomerParser;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDocumentDL;
import com.winit.alseer.salesman.dataaccesslayer.MasterDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.AddCustomerFilesDO;
import com.winit.alseer.salesman.dataobject.CustomerDao;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.FileUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.UploadCustomerDocuments;
import com.winit.alseer.salesman.utilities.UploadImage;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CustomerContactDetail extends BaseActivity implements LocationResult
{
	//initializing variables
	private TextView tvcustomerTitle,tvCustomerName,tvSiteName,tvContactPerson,tvMobileNo,tvRegion,tvLandline,tvArea,tvAreaValue,tvAltPhone,
					tvCustomerType,tvCustomerTypeVal,tvSource,tvSourceVal,tvBillTo,tvShipTo,tvCountry,tvEmail,
					tvNationality,tvNationalityVal,tvDOB,tvDOBVal,tvAnniversary,tvAnniversaryVal,tvLongitude, tvLatitude, tvLongitudeDisplay,
					tvLatitudeDisplay, tvContactDetailHeader, tvAddress1, tvAddress2, tvCity,tvOutLetType,tvOutLetTypeVal,tvCompetitionBrand,tvCompetitionBrandVal,tvSKU,tvBuyer;
	private EditText et_Latlang,etCustomerName,etSiteName,etContactPerson,etMobileNo,etRegion,etLandline,etAltPhone,etBillTo,etShipTo
						,etCountry,etEmail, etAddress1, etAddress2, etCity , etSKU,etChannel,etSubChannel,etPoNumber;
	private Button btnContinue, btnPrint,btnAddDocuments;
	private LinearLayout  llHouseContactDetail, llSKU,btnCaptureLocation,llDocuments;
	private LocationUtility locationUtility;
	private String strPaymentType[] =  {"Cash","Credit"}, strPayment[] =  {"Cash"};
	private final int START_DATE_DIALOG_ID_DOB = 1, START_DATE_DIALOG_ID_ANN = 2;
	private Calendar calendar;
	private int month, year, day, monthAnn, yearAnn, dayAnn;
	private MasterDA masterDA;
	private Vector<NameIDDo> /*vecCustomerType,*/ vecCountry,vecChannel,vecSubChannel, /*vecSource,*/ vecRegion,vecOutLetTypes,vecCompetitionBrands;
	private StartTimer startTimer;
	private CustomerDao mallsDetails;
	private TextView radio_Yes, radio_No;
	private String buyerStatus = "";
	private String BUYER = "Buyer";
	private String NON_BUYER = "Non Buyer";
	private View vvSKU;
	String lat,lang;
	private Bitmap bitmapProcessed;
	private Dialog dialogDetails;
	private Bitmap mBtBitmap = null;
	private String camera_imagepath = "";
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private Vector<AddCustomerFilesDO> vecAddCustomerFilesDO;
	private UploadImage uploadImage;
	@Override
	public void initialize()
	{
		uploadImage = new UploadImage();
		vecAddCustomerFilesDO = new Vector<AddCustomerFilesDO>();
		llHouseContactDetail = (LinearLayout) inflater.inflate(R.layout.house_contact_detail_new, null);
		intialisecontrols();
		setTypeFaceRobotoNormal(llHouseContactDetail);
		tvcustomerTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				masterDA 		= 	new MasterDA();
//				vecCustomerType = masterDA.getCustomerType();
				vecCountry 		= masterDA.getCountry();
				vecChannel 		= masterDA.getChannel();
				vecSubChannel 	= masterDA.getSubChannel();
//				vecSource 		= masterDA.getSource();
				vecRegion 		= masterDA.getRegion();
				vecCompetitionBrands = getCompetitionBrands();
				vecOutLetTypes = getOutLetTypes();
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						if(vecCountry != null && vecCountry.size() > 0)
						{
							etCountry.setText(vecCountry.get(0).strName);
							etCountry.setTag(vecCountry.get(0));	
						}
					}
				});
			}
		}).start();
		
		calendar 	= 	Calendar.getInstance();
		year  		= 	calendar.get(Calendar.YEAR);
		month 		= 	calendar.get(Calendar.MONTH);
		day   		= 	calendar.get(Calendar.DAY_OF_MONTH);
		    
		yearAnn  	= 	calendar.get(Calendar.YEAR);
		monthAnn 	= 	calendar.get(Calendar.MONTH);
		dayAnn   	= 	calendar.get(Calendar.DAY_OF_MONTH);
		
		locationUtility  = new LocationUtility(CustomerContactDetail.this);
		
//		radio_Yes.setTag("Buyer");
//		radio_No.setTag("Non Buyer");
		
		btnCaptureLocation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(startTimer != null)
					startTimer.cancel();
				
				startTimer = new StartTimer(20000, 10000);
				startTimer.start();
				
				showLoader(getString(R.string.Capturing_please_wait));
				if(isNetworkConnectionAvailable(CustomerContactDetail.this) || isGPSEnable(CustomerContactDetail.this))
				{
					locationUtility.getLocation(CustomerContactDetail.this);
				}
				else
				{
					hideLoader();
					showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please check your location settings and turn on your GPS to capture the location co-ordinates.", getString(R.string.OK), null, "");
				}
			}
		});
		
		btnContinue.setTag("register");
		btnContinue.setText("Register");
		btnContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				if(v.getTag().toString().equalsIgnoreCase("register"))
				{
					if(etCustomerName.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter outlet name.", getString(R.string.OK), null, "");
					else if(etContactPerson.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter contact person name.", getString(R.string.OK), null, "");
//					else if(etMobileNo.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter mobile number.", getString(R.string.OK), null, "");
//					else if(etMobileNo.getText().toString().trim().length() < 10)
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Mobile number should be 10 digit number.", getString(R.string.OK), null, "");
//					else if(etRegion.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select region.", getString(R.string.OK), null, "");
//					else if(tvCustomerTypeVal.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select customer type.", getString(R.string.OK), null, "");
					else if(etLandline.getText().toString().equalsIgnoreCase(""))
						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter landline number.", getString(R.string.OK), null, "");
//					else if(tvOutLetTypeVal.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select outlet type.", getString(R.string.OK), null, "");
					else if(etCountry.getText().toString().equalsIgnoreCase("") || etCountry.getTag() == null)
						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select country.", getString(R.string.OK), null, "");
					
					
//					else if(etChannel.getText().toString().equalsIgnoreCase("") || etChannel.getTag() == null)
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select Channel.", getString(R.string.OK), null, "");
					
//					else if(etSubChannel.getText().toString().equalsIgnoreCase("") || etSubChannel.getTag() == null)
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select Sub Channel.", getString(R.string.OK), null, "");
					
//					else if(etPoNumber.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select Po Number.", getString(R.string.OK), null, "");
					
					
					
					
//					else if(tvCompetitionBrandVal.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please select competitor brand.", getString(R.string.OK), null, "");
//					else if(!tvCompetitionBrandVal.getText().toString().equalsIgnoreCase("NOT APPLICABLE") & etSKU.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter SKU.", getString(R.string.OK), null, "");
//					else if(etEmail.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter email address.", getString(R.string.OK), null, "");
//					else if(!StringUtils.isValidEmail(etEmail.getText().toString().trim()))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter valid email address.", getString(R.string.OK), null, "");
//					else if(etAddress1.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter address line 1.", getString(R.string.OK), null, "");
//					else if(etAddress2.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter address line 2.", getString(R.string.OK), null, "");
//					else if(etCity.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please enter city.", getString(R.string.OK), null, "");
					
//					else if(tvLatitudeDisplay.getText().toString().equalsIgnoreCase("") || tvLongitudeDisplay.getText().toString().equalsIgnoreCase(""))
//						showCustomDialog(CustomerContactDetail.this, getString(R.string.warning), "Please capture customer's location.", getString(R.string.OK), null, "");
					else
					{
						showLoader("Saving customer detail...");
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								NameIDDo nameIDDo = null,nameIDRe=null;
								
								String channelCodes[] = new CustomerDA().getChannelCodes();
								
								if(etCountry.getTag()!=null)
									nameIDDo 					= 	(NameIDDo) etCountry.getTag();
								
								mallsDetails					= 	new CustomerDao();
								
								if(lat != null)
									mallsDetails.GeoCodeX 			= 	lat/*tvLatitudeDisplay.getText().toString()*/;
								else
									mallsDetails.GeoCodeX 			= 	0+"";
								if(lang != null)
									mallsDetails.GeoCodeY 			= 	lang/*tvLongitudeDisplay.getText().toString()*/;
								else
									mallsDetails.GeoCodeY 			= 	0+"";
								
								mallsDetails.SiteName 			= 	""+etCustomerName.getText().toString().trim();
								mallsDetails.PartyName 			= 	""+etCustomerName.getText().toString().trim();
								mallsDetails.CustAcctCreationDate = CalendarUtils.getCurrentTimeInMilli()/1000;
								
								mallsDetails.Address1 			= 	""+etAddress1.getText().toString().trim();
								mallsDetails.Address2 			= 	""+etAddress2.getText().toString().trim();
								mallsDetails.City 				= 	""+etCity.getText().toString().trim();
								mallsDetails.CreditLimit		=	""+0;
								
								mallsDetails.PaymentTermCode	=	"60";
								mallsDetails.PaymentType		=	AppConstants.CUSTOMER_TYPE_CASH;
								mallsDetails.CustomerStatus		=	"True";
								mallsDetails.CreatedBy = preference.getStringFromPreference(Preference.EMP_NO, "");
								
								mallsDetails.ChannelCode    =	channelCodes[0];
								mallsDetails.SubChannelCode = 	channelCodes[1];
								mallsDetails.MobileNumber1			=	""+etMobileNo.getText().toString();
//								mallsDetails.customerPaymentType=	"";/*+tvCustomerTypeVal.getTag().toString();*/
								String vehicleNumber=new VehicleDA().getVechicleNO(preference.getStringFromPreference(Preference.EMP_NO, ""));
								if(etRegion.getTag()!=null && etRegion.getTag() instanceof NameIDDo)
									nameIDRe 					= 	(NameIDDo) etRegion.getTag();
								
								if(nameIDRe != null)
									mallsDetails.RegionCode		=	nameIDRe.strName;
								
								if(mallsDetails.RegionCode != null && mallsDetails.RegionCode.equalsIgnoreCase(""))
									mallsDetails.RegionCode     =	"SEEB SOUK";
								else if(mallsDetails.RegionCode == null)
									mallsDetails.RegionCode     =	"SEEB SOUK";
								
								if(nameIDDo!=null)
									mallsDetails.CountryCode	=	nameIDDo.strName;
								
								
								mallsDetails.ContactPersonName	=	""+etContactPerson.getText().toString().trim();
								mallsDetails.PhoneNumber		=	""+etLandline.getText().toString().trim();
								mallsDetails.Email				=	""+etEmail.getText().toString().trim();
								mallsDetails.PriceList			=	AppConstants.DEFAULT_CUSTOMER_PRICING_KEY;
								mallsDetails.SalesOrgCode		=   preference.getStringFromPreference(Preference.ORG_CODE, "");
								mallsDetails.createdDate		=	CalendarUtils.getOrderPostDate();
								mallsDetails.createdTime		=	CalendarUtils.getCurrentTime();
								
								final String customerSiteId	    =  new CustomerDA().insertCustomer(mallsDetails,vehicleNumber);
								
								if(customerSiteId != null && customerSiteId.length() > 0 && isNetworkConnectionAvailable(CustomerContactDetail.this))
								{
									new CustomerDA().insertCustomerSync(customerSiteId,0);
									new CustomerDocumentDL().insertCustomerDocuments(vecAddCustomerFilesDO, customerSiteId);
									uploadCustomer(customerSiteId);
								}
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() 
									{
										hideLoader();
										
										Intent intent=new Intent(CustomerContactDetail.this,UploadCustomerDocuments.class);
										startService(intent);
										
										if(customerSiteId == null || customerSiteId.length() <= 0)
											showCustomDialog(CustomerContactDetail.this, "Warning !", "Customer sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", "OK", null, "finish", false);
										else
										{
											showCustomDialog(CustomerContactDetail.this, "Successful !", "Customer registered successfully. Do you want to take order?", "YES", "NO", "order", false);
											((Button)v).setTag("Continue");
											((Button)v).setText(" Continue ");
//											setDisableViews();
										}
									}
								});
							}
						}).start();
					}
				}
			}
		});
		/*tvCustomerTypeVal.setTag(-1);
		tvCustomerTypeVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Customer Type", false);
				builder.setSingleChoiceItems(vecCustomerType, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						tvCustomerTypeVal.setText(objNameIDDo.strName);
						tvCustomerTypeVal.setTag(objNameIDDo.strId);
					}
				});
				builder.show();
			}
		});*/
		
		etRegion.setTag(-1);
		etRegion.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Sales Office", true);
				builder.setSingleChoiceItems(vecRegion, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						etRegion.setText(objNameIDDo.strName);
						etRegion.setTag(objNameIDDo);
					}
				});
				builder.show();
			}
		});
		
		
		/*tvNationalityVal.setTag(-1);
		tvNationalityVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Customer Type", true);
				builder.setSingleChoiceItems(vecCountry, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						tvNationalityVal.setText(objNameIDDo.strName);
						tvNationalityVal.setTag(objNameIDDo.strId);
					}
				});
				builder.show();
			}
		});*/
		
		
		etCountry.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Country", true);
				builder.setSingleChoiceItems(vecCountry, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						etCountry.setText(objNameIDDo.strName);
						etCountry.setTag(objNameIDDo);
					}
				});
				builder.show();
			}
		});
		etChannel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Channel", true);
				builder.setSingleChoiceItems(vecChannel, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						etChannel.setText(objNameIDDo.strName);
						etChannel.setTag(objNameIDDo);
					}
				});
				builder.show();
			}
		});
		etSubChannel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Customer Group", true);
				builder.setSingleChoiceItems(vecSubChannel, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						etSubChannel.setText(objNameIDDo.strName);
						etSubChannel.setTag(objNameIDDo);
					}
				});
				builder.show();
			}
		});
		
		
		/*tvSourceVal.setTag(-1);
		tvSourceVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Customer Type", true);
				builder.setSingleChoiceItems(vecSource, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						tvSourceVal.setText(objNameIDDo.strName);
						tvSourceVal.setTag(objNameIDDo.strId);
					}
				});
				builder.show();
			}
		});*/
		
		/*tvDOBVal.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_DOB);
			}
		});*/
		
		/*tvAnniversaryVal.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(START_DATE_DIALOG_ID_ANN);
			}
		});*/
		
		tvOutLetTypeVal.setTag(-1);
		tvOutLetTypeVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Outlet Type", true);
				builder.setSingleChoiceItems(vecOutLetTypes, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						tvOutLetTypeVal.setText(objNameIDDo.strName);
						tvOutLetTypeVal.setTag(objNameIDDo.strId);
					}
				});
				builder.show();
			}
		});
		
		btnAddDocuments.setOnClickListener(new OnClickListener() 
		{
		
			@Override
			public void onClick(View v) 
			{
				captureImage();
				
			}
		});
		
		/*tvCompetitionBrandVal.setTag(-1);
		tvCompetitionBrandVal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				CustomBuilder builder = new CustomBuilder(CustomerContactDetail.this, "Select Competitor Brand", true);
				builder.setSingleChoiceItems(vecCompetitionBrands, -1, new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						builder.dismiss();
						NameIDDo objNameIDDo = (NameIDDo) selectedObject;
						
						tvCompetitionBrandVal.setText(objNameIDDo.strName);
						tvCompetitionBrandVal.setTag(objNameIDDo.strId);
					}
				});
				builder.show();
			}
		});*/
		
//		radio_Yes.setOnClickListener(new OnClickListener() 
//		{
//			
//			@Override
//			public void onClick(View v) 
//			{
//				radio_Yes.setCompoundDrawablesWithIntrinsicBounds( R.drawable.radioclick, 0, 0, 0);
//				radio_No.setCompoundDrawablesWithIntrinsicBounds( R.drawable.radionor, 0, 0, 0);
//				buyerStatus = BUYER;
//			}
//		});
//		
//		radio_No.setOnClickListener(new OnClickListener() 
//		{
//			
//			@Override
//			public void onClick(View v) 
//			{
//				radio_No.setCompoundDrawablesWithIntrinsicBounds( R.drawable.radioclick, 0, 0, 0);
//				radio_Yes.setCompoundDrawablesWithIntrinsicBounds( R.drawable.radionor, 0, 0, 0);
//				buyerStatus = NON_BUYER;
//			}
//		});
//		btnPrint.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) 
//			{
//				Intent mIntent = new Intent(CustomerContactDetail.this , WoosimPrinterActivity.class);
//				mIntent.putExtra("CALLFROM", CONSTANTOBJ.NEW_CUSTOMER);
//				mIntent.putExtra("mallsDetails", mallsDetails);
//				startActivityForResult(mIntent, 1000);
//				
//				
//				
////				Intent mIntent = new Intent(CustomerContactDetail.this , BluetoothFilePrinter.class);
////				mIntent.putExtra("CALLFROM", CONSTANTOBJ.NEW_CUSTOMER);
////				mIntent.putExtra("mallsDetails", mallsDetails);
////				startActivityForResult(mIntent, 1000);
//			}
//		});
		
		//Inflating layout
		llBody.addView(llHouseContactDetail, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
//		llSKU.setVisibility(View.GONE);
//		vvSKU.setVisibility(View.GONE);
		setTypeFaceRobotoNormal(llHouseContactDetail);
//		llSKU.setVisibility(View.GONE);
//		vvSKU.setVisibility(View.GONE);
//		setTypeFace(llHouseContactDetail);
		
		etCity.setText("Dubai");
	}
	
	//getting id's and set type-faces
	public void intialisecontrols()
	{
		btnContinue    			= (Button)llHouseContactDetail.findViewById(R.id.btnContinueToPayment);
		btnAddDocuments			= (Button)llHouseContactDetail.findViewById(R.id.btnAddDocuments);
		llDocuments	   			= (LinearLayout)llHouseContactDetail.findViewById(R.id.llDocuments);
//		btnPrint	   			= (Button)llHouseContactDetail.findViewById(R.id.btnPrint);
		btnCaptureLocation	   	= (LinearLayout)llHouseContactDetail.findViewById(R.id.btnCaptureLocation);
		tvCustomerName  	 	= (TextView)llHouseContactDetail.findViewById(R.id.tvCustomerName);
//		tvSiteName  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvSiteName);
		tvContactPerson   		= (TextView)llHouseContactDetail.findViewById(R.id.tvContactPerson);
		tvMobileNo  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvMobileNo);
		tvRegion  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvRegion);
		tvLandline  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvLandline);
		tvcustomerTitle	 		= (TextView)llHouseContactDetail.findViewById(R.id.tvcustomerTitle);
//		tvArea  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvArea);
//		tvAreaValue  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvAreaValue);
//		tvAltPhone  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvAltPhone);
//		tvCustomerType  		= (TextView)llHouseContactDetail.findViewById(R.id.tvCustomerType);
//		tvCustomerTypeVal  		= (TextView)llHouseContactDetail.findViewById(R.id.tvCustomerTypeVal);
//		tvSource  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvSource);
//		tvSourceVal  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvSourceVal);
//		tvBillTo  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvBillTo);
//		tvShipTo  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvShipTo);
		tvCountry  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvCountry);
		tvEmail  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvEmail);
//		tvNationality  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvNationality);
//		tvNationalityVal  	 	= (TextView)llHouseContactDetail.findViewById(R.id.tvNationalityVal);
//		tvDOB  		 			= (TextView)llHouseContactDetail.findViewById(R.id.tvDOB);
//		tvDOBVal  		 		= (TextView)llHouseContactDetail.findViewById(R.id.tvDOBVal);
//		tvAnniversary  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvAnniversary);
//		tvAnniversaryVal  	 	= (TextView)llHouseContactDetail.findViewById(R.id.tvAnniversaryVal);
//		tvLongitude  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvLongitude);
//		tvLatitude  		 	= (TextView)llHouseContactDetail.findViewById(R.id.tvLatitude);
//		tvLongitudeDisplay   	= (TextView)llHouseContactDetail.findViewById(R.id.tvLongitudeDisplay);
//		tvLatitudeDisplay  	 	= (TextView)llHouseContactDetail.findViewById(R.id.tvLatitudeDisplay);
//		tvContactDetailHeader	= (TextView)llHouseContactDetail.findViewById(R.id.tvContactDetailHeader);
		tvOutLetType			= (TextView)llHouseContactDetail.findViewById(R.id.tvOutLetType);
		tvOutLetTypeVal			= (TextView)llHouseContactDetail.findViewById(R.id.tvOutLetTypeVal);
//		tvCompetitionBrand		= (TextView)llHouseContactDetail.findViewById(R.id.tvCompetitionBrand);
//		tvCompetitionBrandVal	= (TextView)llHouseContactDetail.findViewById(R.id.tvCompetitionBrandVal);
//		tvSKU					= (TextView)llHouseContactDetail.findViewById(R.id.tvSKU);
//		tvBuyer					= (TextView)llHouseContactDetail.findViewById(R.id.tvBuyer);
//		llSKU					= (LinearLayout)llHouseContactDetail.findViewById(R.id.llSKU);
//		vvSKU					= (View)llHouseContactDetail.findViewById(R.id.vvSKU);
		
		tvAddress1				= (TextView)llHouseContactDetail.findViewById(R.id.tvAddress1);
		tvAddress2				= (TextView)llHouseContactDetail.findViewById(R.id.tvAddress2);
		tvCity					= (TextView)llHouseContactDetail.findViewById(R.id.tvCity);
		
		etAddress1				= (EditText)llHouseContactDetail.findViewById(R.id.etAddress1);
		etAddress2				= (EditText)llHouseContactDetail.findViewById(R.id.etAddress2);
		etCity					= (EditText)llHouseContactDetail.findViewById(R.id.etCity);
		etCustomerName			= (EditText)llHouseContactDetail.findViewById(R.id.etCustomerName);
//		etSiteName				= (EditText)llHouseContactDetail.findViewById(R.id.etSiteName);
		etContactPerson			= (EditText)llHouseContactDetail.findViewById(R.id.etContactPerson);
		etMobileNo				= (EditText)llHouseContactDetail.findViewById(R.id.etMobileNo);
		etRegion				= (EditText)llHouseContactDetail.findViewById(R.id.etRegion);
		etLandline				= (EditText)llHouseContactDetail.findViewById(R.id.etLandline);
		et_Latlang				= (EditText)llHouseContactDetail.findViewById(R.id.et_Latlang);
//		etAltPhone				= (EditText)llHouseContactDetail.findViewById(R.id.etAltPhone);
//		etBillTo				= (EditText)llHouseContactDetail.findViewById(R.id.etBillTo);
//		etShipTo				= (EditText)llHouseContactDetail.findViewById(R.id.etShipTo);
		etCountry				= (EditText)llHouseContactDetail.findViewById(R.id.etCountry);
		etChannel				= (EditText)llHouseContactDetail.findViewById(R.id.etChannel);
		etSubChannel				= (EditText)llHouseContactDetail.findViewById(R.id.etSubChannel);
		etPoNumber				= (EditText)llHouseContactDetail.findViewById(R.id.etPoNumber);
		etEmail					= (EditText)llHouseContactDetail.findViewById(R.id.etEmail);
//		etSKU					= (EditText)llHouseContactDetail.findViewById(R.id.etSKU);
		
		
//		radio_Yes 				= (TextView) llHouseContactDetail.findViewById(R.id.radio_Yes);
//		radio_No 				= (TextView) llHouseContactDetail.findViewById(R.id.radio_No);

		
		/*btnContinue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnCaptureLocation.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvCustomerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSiteName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvContactPerson.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvMobileNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvRegion.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLandline.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvArea.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAreaValue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAltPhone.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerType.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCustomerTypeVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSource.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSourceVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvBillTo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvShipTo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCountry.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvEmail.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNationality.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvNationalityVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDOB.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvDOBVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAnniversary.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAnniversaryVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLongitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLatitude.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLongitudeDisplay.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvLatitudeDisplay.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvContactDetailHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOutLetType.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCompetitionBrand.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSKU.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvOutLetTypeVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCompetitionBrandVal.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvBuyer.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		etCustomerName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSiteName.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etContactPerson.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etMobileNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etRegion.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etLandline.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etAltPhone.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etBillTo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etShipTo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etCountry.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etEmail.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etSKU.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		radio_Yes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		radio_No.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvAddress1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvAddress2.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		etAddress1.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		etAddress2.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		etCity.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvCity.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
//		btnPrint.setVisibility(View.GONE);
		
		buyerStatus = NON_BUYER;
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
   }
	
	@Override
	public void onButtonYesClick(String from)
	{
		if(from.equalsIgnoreCase("order"))
		{
			showLoader("Please wait...");
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					final JourneyPlanDO j = new CustomerDetailsDA().getCustometBySiteId(mallsDetails.site);
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							hideLoader();
							if(j != null)
							{
								Intent intent = new Intent(CustomerContactDetail.this, SalesManRecommendedOrder.class);
								intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
								intent.putExtra("mallsDetails",j);
								intent.putExtra("TRX_TYPE", TrxHeaderDO.get_TRXTYPE_SALES_ORDER());
								intent.putExtra("TRX_SUB_TYPE", TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER());
								startActivity(intent);
								finish();
							}
						}
					});
				}
			}).start();
		}
		else if(from.equalsIgnoreCase("locationNotCaptured"))
		{
			btnCaptureLocation.performClick();
		}
		else if(from.equalsIgnoreCase("DeleteImage"))
			removeSeletedItem();
		else
			super.onButtonYesClick(from);
	}
	
	@Override
	public void onButtonNoClick(String from)
	{
		if(from.equalsIgnoreCase("order"))
		{
			finish();
		}
	}
	@Override
	public void gotLocation(final Location loc) 
	{
		if(loc!=null)
		{
			if(startTimer != null)
				startTimer.cancel();
			
			locationUtility.stopGpsLocUpdation();
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					hideLoader();
					DecimalFormat decimalFormat = new DecimalFormat("##.######");
					lat		=""+Double.parseDouble(decimalFormat.format(loc.getLatitude()));
					lang	=""+Double.parseDouble(decimalFormat.format(loc.getLongitude()));
					
					et_Latlang.setText(lat+" , "+lang);
				}
			});
		}
	}
	
	private void setDisableViews()
	{
//		tvCustomerTypeVal.setEnabled(false);
//		tvCustomerTypeVal.setClickable(false);
		
		tvCustomerName.setEnabled(false);
//		tvSiteName.setEnabled(false);
		tvContactPerson.setEnabled(false);
		tvMobileNo.setEnabled(false);
		tvRegion.setEnabled(false);
		tvLandline.setEnabled(false);
//		tvArea.setEnabled(false);
//		tvAreaValue.setEnabled(false);
//		tvAltPhone.setEnabled(false);
//		tvCustomerType.setEnabled(false);
//		tvCustomerTypeVal.setEnabled(false);
//		tvSource.setEnabled(false);
//		tvSourceVal.setEnabled(false);
//		tvBillTo.setEnabled(false);
//		tvShipTo.setEnabled(false);
		tvCountry.setEnabled(false);
		tvEmail.setEnabled(false);
//		tvNationality.setEnabled(false);
//		tvNationalityVal.setEnabled(false);
//		tvDOB.setEnabled(false);
//		tvDOBVal.setEnabled(false);
//		tvAnniversary.setEnabled(false);
//		tvAnniversaryVal.setEnabled(false);
//		tvLongitude.setEnabled(false);
//		tvLatitude.setEnabled(false);
//		tvLongitudeDisplay.setEnabled(false);
//		tvLatitudeDisplay.setEnabled(false);
		etCustomerName.setEnabled(false);
//		etSiteName.setEnabled(false);
		etContactPerson.setEnabled(false);
		etMobileNo.setEnabled(false);
		etRegion.setEnabled(false);
		etLandline.setEnabled(false);
//		etAltPhone.setEnabled(false);
//		etBillTo.setEnabled(false);
//		etShipTo.setEnabled(false);
		etCountry.setEnabled(false);
		etEmail.setEnabled(false);
//		etSKU.setEnabled(false);
		
		tvCustomerName.setClickable(false);
//		tvSiteName.setClickable(false);
		tvContactPerson.setClickable(false);
		tvMobileNo.setClickable(false);
		tvRegion.setClickable(false);
		tvLandline.setClickable(false);
//		tvArea.setClickable(false);
//		tvAreaValue.setClickable(false);
//		tvAltPhone.setClickable(false);
//		tvCustomerType.setClickable(false);
//		tvCustomerTypeVal.setClickable(false);
//		tvSource.setClickable(false);
//		tvSourceVal.setClickable(false);
//		tvBillTo.setClickable(false);
//		tvShipTo.setClickable(false);
		tvCountry.setClickable(false);
		tvEmail.setClickable(false);
//		tvNationality.setClickable(false);
//		tvNationalityVal.setClickable(false);
//		tvDOB.setClickable(false);
//		tvDOBVal.setClickable(false);
//		tvAnniversary.setClickable(false);
//		tvAnniversaryVal.setClickable(false);
//		tvLongitude.setClickable(false);
//		tvLatitude.setClickable(false);
//		tvLongitudeDisplay.setClickable(false);
//		tvLatitudeDisplay.setClickable(false);
		tvOutLetTypeVal.setClickable(false);
//		tvCompetitionBrandVal.setClickable(false);
		
		
		etCustomerName.setFocusable(false);
//		etSiteName.setFocusable(false);
		etContactPerson.setFocusable(false);
		etMobileNo.setFocusable(false);
		etRegion.setFocusable(false);
		etLandline.setFocusable(false);
//		etAltPhone.setFocusable(false);
//		etBillTo.setFocusable(false);
		etMobileNo.setFocusable(false);
//		etShipTo.setFocusable(false);
		etCountry.setFocusable(false);
		etEmail.setFocusable(false);
//		etSKU.setFocusable(false);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
	    switch (id) 
	    {
		    case START_DATE_DIALOG_ID_DOB:
		    	return new DatePickerDialog(this, dobListener,  year, month, day);
		    
		    case START_DATE_DIALOG_ID_ANN:
		    	return new DatePickerDialog(this, annListener, yearAnn, monthAnn, dayAnn);
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
	    	
//	    	tvDOBVal.setText(CalendarUtils.getFormatedDatefromString(str));
//	    	tvDOBVal.setTag(str);
	    }
    };
    
    private DatePickerDialog.OnDateSetListener annListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth) 
	    {
	    	yearAnn = yearSel;
	    	monthAnn = monthOfYear+1;
	    	dayAnn = dayOfMonth;
	    	
	    	String str = yearAnn + "-" + monthAnn + "-" + dayAnn;
	    	
//	    	tvAnniversaryVal.setText(CalendarUtils.getFormatedDatefromString(str));
//	    	tvAnniversaryVal.setTag(str);
	    }
    };
    
    public class StartTimer extends CountDownTimer
	{
		public StartTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			hideLoader();
			locationUtility.stopGpsLocUpdation();
			showCustomDialog(CustomerContactDetail.this, "Sorry !", "Unable to capture your current location. Do you want to continue without capturing your location?", getString(R.string.Yes),  getString(R.string.No), "locationNotCaptured");
		}
		@Override
		public void onTick(long millisUntilFinished) 
		{
			LogUtils.errorLog("millisUntilFinished", ""+millisUntilFinished);
		}
	}
    
    public boolean uploadCustomer(String customerSiteId)
	{
		Vector<CustomerDao> vector = new Vector<CustomerDao>();
		vector.add(mallsDetails);
		
		final AddNewCustomerParser insertCustomerParser = new AddNewCustomerParser(null);
        final ConnectionHelper connectionHelper 		= new ConnectionHelper(null);
        if(vector !=null && vector.size() > 0)
        {
        	String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
			connectionHelper.sendBlkInsertHHCustomer(BuildXMLRequest.insertNewTypeHHCustomer(vector, route_code), insertCustomerParser, ServiceURLs.BlkInsertHHCustomer);
			if(insertCustomerParser.getResponseStatus())
			{
				if(!insertCustomerParser.getStatus())
				{
					
				}
			}
        }
        return true;
	}
    
    private Vector<NameIDDo> getOutLetTypes()
    {
    	String outLetTypes[] = {"CATERING COMPANY",
    			"COFFEE SHOPS",
    			"EXPORTS",
    			"HOSPITALITY",
    			"HOTEL APARTMENT",
    			"HYPERMARKET",
    			"OTHERS",
    			"PETROL STATIONS",
    			"RESTAURANTS",
    			"SHIP CHANDLERS",
    			"STAR HOTEL",
    			"SUPERMARKET"};
    	Vector<NameIDDo> vecNameIDDos = new Vector<NameIDDo>();
    	for (int i = 0; i < outLetTypes.length; i++) 
    	{
			NameIDDo nameIDDo = new NameIDDo();
			nameIDDo.strId = i+"";
			nameIDDo.strName = outLetTypes[i];
			vecNameIDDos.add(nameIDDo);
		}
    	return vecNameIDDos;
    	
    }
    
    private Vector<NameIDDo> getCompetitionBrands()
    {
    	String outLetTypes[] = {"Aquafina","Kinley","Bailey","Others","NOT APPLICABLE"};
    	Vector<NameIDDo> vecNameIDDos = new Vector<NameIDDo>();
    	for (int i = 0; i < outLetTypes.length; i++) 
    	{
			NameIDDo nameIDDo = new NameIDDo();
			nameIDDo.strId = i+"";
			nameIDDo.strName = outLetTypes[i];
			vecNameIDDos.add(nameIDDo);
		}
    	return vecNameIDDos;
    	
    }
    private void captureImage()
	{
		if(isDeviceSupportCamera())
		{
			File file    = FileUtils.getOutputImageFile("Initiatives");
			if(file!=null)
			{
				camera_imagepath   = file.getAbsolutePath();
				Uri fileUri  = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				intent.putExtra("fileName",file.getName());
				intent.putExtra("filePath", file.getAbsolutePath());
				startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		}
		else
		{
			Toast.makeText(CustomerContactDetail.this,"Sorry Device not supported to camera", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
		{
			switch (resultCode) 
			{
			case RESULT_CANCELED:
				Log.i("Camera", "User cancelled");
				break;

			case RESULT_OK:

				File f = new File(camera_imagepath);
				Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
				mBtBitmap = bmp;
				showPopup(mBtBitmap);
				break;
			}
		}
	}
	
	private void showPopup(Bitmap mBtBitmap2)
	{
//		popupWindowStores=null;
//		popupWindowStores = popupWindowTypes(getBitMap(mBtBitmap,camera_imagepath));
//		popupWindowStores.showAtLocation(llPlanogram, Gravity.CENTER, 0, 0);
		showDetails(mBtBitmap2);
		
	}
	
	private void showDetails(final Bitmap bitmap)
	{
		LinearLayout llout = (LinearLayout) inflater.inflate(R.layout.capture_planogram_popup, null);
		Button btnSubmit = (Button)llout.findViewById(R.id.btnSubmit);
		TextView tvAddComments = (TextView)llout.findViewById(R.id.tvAddComments);
		final EditText etNotes = (EditText)llout.findViewById(R.id.edtNotes);
		ImageView ivCapturedPlanImage = (ImageView)llout.findViewById(R.id.ivCapturedPlanImage);

		tvAddComments.setText("Add File Name");
		etNotes.setHint("Enter File Name");
		etNotes.setText("");
		btnSubmit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(dialogDetails!=null)
					dialogDetails.dismiss();
				addFilesToLayout(bitmap, etNotes.getText().toString());

			}
		});

		
		if(bitmap!=null)
			ivCapturedPlanImage.setImageBitmap(bitmap);
		
		dialogDetails = new Dialog(CustomerContactDetail.this, android.R.style.Theme_Holo_Dialog);
		dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogDetails.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogDetails.addContentView(llout, new LayoutParams(AppConstants.DEVICE_WIDTH-20, AppConstants.DEVICE_HEIGHT-20));
		dialogDetails.show();
		
		setTypeFaceRobotoNormal(llout);

	}
	private Bitmap getBitMa(Bitmap bmp, String camera_imagepath)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			bitmapProcessed = BitmapUtilsLatLang.processBitmap22(bmp, lat, lang, "");
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
	}
	
	public void addFilesToLayout(final Bitmap bitmap,String name)
	{
		
		RelativeLayout llImage = (RelativeLayout) inflater.inflate(R.layout.image_bg_cell, null);
		
		LinearLayout llImageLL = (LinearLayout)llImage.findViewById(R.id.llImageLL);
		ImageView ivCross = (ImageView)llImage.findViewById(R.id.ivCross);
		ImageView ivImage = (ImageView)llImage.findViewById(R.id.ivImage);
		TextView tvName = (TextView)llImage.findViewById(R.id.tvFileName);
		
		tvName.setText(name);
		ivImage.setImageBitmap(bitmap);
		ivCross.setTag(llImage);
		
		AddCustomerFilesDO addCustomerFilesDO = new AddCustomerFilesDO();
		addCustomerFilesDO.ServerFilePath = camera_imagepath;
		addCustomerFilesDO.DocumentName=name;
		addCustomerFilesDO.Status="0";
		addCustomerFilesDO.CreatedOn = CalendarUtils.getCurrentDateTimeForAddCustomer();
		vecAddCustomerFilesDO.add(addCustomerFilesDO);
		
		ivCross.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				v.setTag(R.string.delete,"delete");
				showCustomDialog(CustomerContactDetail.this,getString(R.string.alert), "Are you sure you want to delete the image?", "Yes", "No", "DeleteImage");
				
				
			}
		});
		
		
		llImageLL.setTag(bitmap);
		llImageLL.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				
			}
		});
		
		setTypeFaceRobotoNormal(llImage);
		
		llDocuments.addView(llImage);
	}
	
	private void removeSeletedItem()
	{
		if(llDocuments!=null && llDocuments.getChildCount()>0)
		{
			for(int i=0;i<llDocuments.getChildCount();i++)
			{
				RelativeLayout llImage = (RelativeLayout) llDocuments.getChildAt(i);
				LinearLayout llImageLL = (LinearLayout)llImage.findViewById(R.id.llImageLL);
				ImageView ivCross = (ImageView)llImage.findViewById(R.id.ivCross);
				if(ivCross.getTag(R.string.delete)!=null)
				{
					if(ivCross.getTag(R.string.delete).toString().equalsIgnoreCase("delete"))
					{
						llDocuments.removeView(llImage);
						break;
					}
				}
			}
		}
	}
}
