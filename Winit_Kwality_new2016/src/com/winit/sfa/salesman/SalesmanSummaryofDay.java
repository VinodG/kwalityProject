package com.winit.sfa.salesman;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winit.alseer.parsers.ImageUploadParser;
import com.winit.alseer.parsers.InsertCustomerParser;
import com.winit.alseer.parsers.InsertOrdersParser;
import com.winit.alseer.parsers.InsertPaymentParser;
import com.winit.alseer.parsers.UserLoginParser;
import com.winit.alseer.pinch.SignatureView;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.FilesStorage;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.EOTDA;
import com.winit.alseer.salesman.dataaccesslayer.JourneyPlanDA;
import com.winit.alseer.salesman.dataaccesslayer.StoreCheckDA;
import com.winit.alseer.salesman.dataobject.JouneyStartDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.LoginUserInfo;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.ServiceCaptureDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.BitmapsUtiles;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.UploadImage;
import com.winit.alseer.salesman.utilities.UploadTransactions;
import com.winit.alseer.salesman.utilities.ZipUtils;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionProcessListner;
import com.winit.alseer.salesman.utilities.UploadTransactions.TransactionSatus;
import com.winit.alseer.salesman.utilities.UploadTransactions.Transactions;
import com.winit.alseer.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.R;

public class SalesmanSummaryofDay extends BaseActivity implements ConnectionExceptionListener, TransactionProcessListner {
	// declaration of variables
	private LinearLayout llSummaryofDay;
	private TextView tvPageTitle, tvOrderReceived, tvReturnRequest,tvAmountCollected, tvUnUploadedData, tvUndeliveredQT,
			tvReturnOrderQT, tvReplacement, tvTransactionType,tvUnvisitedcustomer,tvPendingImage;
	private String strSalesmanCode, strEmpId;
	private Button btnOK, btnUpload, btnStockUnload, btnInventory;
	private boolean isSupervisor = false;
	private CommonDA commonDA;
	private boolean isOrdersUploaded = true;
	private boolean isEot = false;
	private SignatureView driveSignatureView, managerSignatureView;
	private ProgressBar ivProgressBar;
	private View inHeader, inDivider;
	private int resultCode = 0;
	private boolean isDatatoUpload = false;
	private ArrayList<JourneyPlanDO> arrJP;
	JouneyStartDO jouneyStartDO=null;
	@Override
	public void initialize() {
		// inflate the summary-of-day layout
		llSummaryofDay = (LinearLayout) inflater.inflate(R.layout.summaryofday,null);
		llBody.addView(llSummaryofDay, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

		if (getIntent().getExtras() != null) 
		{
			strSalesmanCode = getIntent().getExtras().getString("SalesmanCode");
			strEmpId = getIntent().getExtras().getString("EmpId");
			isSupervisor = getIntent().getExtras().getBoolean("isSupervisor");
			isEot = getIntent().getExtras().getBoolean("isEot");
		}
		// Initialization of business layer
		commonDA = new CommonDA();

		// Initialing all the controls
		intialiseControls();

		setTypeFaceRobotoNormal(llSummaryofDay);

		if (isEot) {
			tvUnUploadedData.setVisibility(View.GONE);
			tvUndeliveredQT.setVisibility(View.VISIBLE);
			btnOK.setVisibility(View.GONE);
		} else {
			tvUnUploadedData.setVisibility(View.GONE);
			tvUndeliveredQT.setVisibility(View.GONE);
			btnOK.setVisibility(View.VISIBLE);
		}
		btnStockUnload.setVisibility(View.GONE);
		tvReturnOrderQT.setVisibility(View.GONE);
		tvReplacement.setVisibility(View.GONE);
		tvOrderReceived.setText("Order Summary");
		tvUnUploadedData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SalesmanSummaryofDay.this,
						UnloadDataActivity.class);
				startActivity(intent);
			}
		});

		tvOrderReceived.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				((TextView) v).setClickable(false);
				Intent intent = new Intent(SalesmanSummaryofDay.this,
						OrderSummary.class);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						((TextView) v).setClickable(true);
					}
				}, 500);
			}
		});

		tvReturnRequest.setVisibility(View.GONE);
		tvAmountCollected.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				((TextView) v).setClickable(false);

				Intent intent = new Intent(SalesmanSummaryofDay.this,
						PaymentSummaryActivity.class);
				intent.putExtra("Ispreseller", true);
				startActivity(intent);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						((TextView) v).setClickable(true);
					}
				}, 500);
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				setResult(2000);
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				setResult(2000);
			}
		});
		
//		if (!preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		if(!isEOTDone())
			btnUpload.setVisibility(View.VISIBLE);
		// btnUpload.setText(" Submit EOT ");
		btnUpload.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				writeLogForEOT("\n btnupload is Clicked at :"+CalendarUtils.getCurrentDateAsStringForJourneyPlan());
				
				if (isNetworkConnectionAvailable(SalesmanSummaryofDay.this)) 
				{
					if(arrJP != null && arrJP.size()>0)
					{
						showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Please select Skip Reason for all unvisited customers.", getString(R.string.Yes), getString(R.string.No), "skipReason");
					}
					else
					{
						showLoader("Please wait...");
						new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
									isDatatoUpload = new EOTDA().getEOTDetailsForPreSales();
								else
									isDatatoUpload = new EOTDA().getEOTDetailsForVansales();
								resultCode = 5;
								runOnUiThread(new Runnable() 
								{
									@Override
									public void run() 
									{
										UploadTransactions.setTransactionProcessListner(SalesmanSummaryofDay.this);
										if (isDatatoUpload)
										{
											uploadData();
											isDatatoUpload = false;
										}
										else 
										{
											hideLoader();
											showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Are you sure you want to submit EOT?",getString(R.string.Yes),getString(R.string.No),"EOTSubmit");
										}
									}
								});
							}
						}).start();
					}
				
				} else {
					showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Please connect to internet before submitting EOT.",getString(R.string.OK), null, "");
				}
				
//				if (isNetworkConnectionAvailable(SalesmanSummaryofDay.this)) 
//				{
//					new Thread(new Runnable() 
//					{
//						@Override
//						public void run() 
//						{
//							final ArrayList<JourneyPlanDO> arrJP = new CustomerDetailsDA().getUnvisitedCustomers();
//							runOnUiThread(new Runnable()
//							{
//								@Override
//								public void run() 
//								{
//									if(arrJP != null && arrJP.size()>0)
//									{
//										showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Please select Skip Reason for all unvisited customers.", getString(R.string.Yes), getString(R.string.No), "skipReason");
//									}
//									else
//									{
//										new Thread(new Runnable() 
//										{
//											@Override
//											public void run() 
//											{
//												showLoader("Please wait...");
//												if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
//													isDatatoUpload = new EOTDA().getEOTDetailsForPreSales();
//												else
//													isDatatoUpload = new EOTDA().getEOTDetailsForVansales();
//												resultCode = 5;
//												runOnUiThread(new Runnable() 
//												{
//													@Override
//													public void run() 
//													{
//														UploadTransactions.setTransactionProcessListner(SalesmanSummaryofDay.this);
//														if (isDatatoUpload)
//														{
//															uploadData();
//															isDatatoUpload = false;
//														}
//														else 
//														{
//															showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Are you sure you want to submit EOT?",getString(R.string.Yes),getString(R.string.No),"EOTSubmit");
//														}
//														hideLoader();
//													}
//												});
//											}
//										}).start();
//									}
//								}
//							});
//						}
//					}).start();
//				} else {
//					showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Please connect to internet before submitting EOT.",getString(R.string.OK), null, "");
//				}
			}
		});

		btnStockUnload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SalesmanSummaryofDay.this,
						ReturnStockOption.class);
				startActivity(intent);
			}
		});
		if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "")
				.equalsIgnoreCase(preference.PRESELLER))
			tvUndeliveredQT.setVisibility(View.GONE);
		tvUndeliveredQT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SalesmanSummaryofDay.this,Inventory_QTY.class);
				startActivity(intent);
			}
		});
		tvUnvisitedcustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(SalesmanSummaryofDay.this,UnVisitedCustomerActivity.class);
				startActivity(intent);
			}
		});
		tvReturnOrderQT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SalesmanSummaryofDay.this,Return_Order_QTY.class);
				intent.putExtra("OrderSubType", AppConstants.RETURNORDER);
				startActivity(intent);
			}
		});

		tvReplacement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SalesmanSummaryofDay.this,Return_Order_QTY.class);
				intent.putExtra("OrderSubType", AppConstants.REPLACEMETORDER);
				startActivity(intent);
			}
		});

		btnInventory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showInventoryPopup(v);
			}
		});

		if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "")
				.equalsIgnoreCase(AppConstants.SALESMAN_PD))
			tvAmountCollected.setVisibility(View.GONE);
		UploadTransactions.setTransactionProcessListner(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UploadTransactions.resetTransactionProcessListner();
	}

	public void intialiseControls() {
		// getting Id's of TextView
		tvPageTitle = (TextView) llSummaryofDay.findViewById(R.id.tvPageTitle);
		tvOrderReceived = (TextView) llSummaryofDay.findViewById(R.id.tvOrderReceived);
		tvReturnRequest = (TextView) llSummaryofDay.findViewById(R.id.tvReturnRequest);
		tvAmountCollected = (TextView) llSummaryofDay.findViewById(R.id.tvAmountCollected);
		tvUnUploadedData = (TextView) llSummaryofDay.findViewById(R.id.tvUnUploadedData);
		tvUndeliveredQT = (TextView) llSummaryofDay.findViewById(R.id.tvUndeliveredQT);
		tvReturnOrderQT = (TextView) llSummaryofDay.findViewById(R.id.tvReturnOrderQT);
		tvTransactionType = (TextView) llSummaryofDay.findViewById(R.id.tvTransactionType);
		ivProgressBar = (ProgressBar) llSummaryofDay.findViewById(R.id.ivProgressBar);
		tvReplacement = (TextView) llSummaryofDay.findViewById(R.id.tvReplacement);
		tvUnvisitedcustomer = (TextView) llSummaryofDay.findViewById(R.id.tvUnvisitedcustomer);
		tvPendingImage = (TextView) llSummaryofDay.findViewById(R.id.tvPendingImage);
		
		// getting Id's of Button
		tvTransactionType.setVisibility(View.GONE);
		ivProgressBar.setVisibility(View.GONE);
		btnOK = (Button) llSummaryofDay.findViewById(R.id.btnOK);
		btnUpload = (Button) llSummaryofDay.findViewById(R.id.btnUpload);
		btnStockUnload = (Button) llSummaryofDay.findViewById(R.id.btnStockUnload);
		btnInventory = (Button) llSummaryofDay.findViewById(R.id.btnInventory);

		inHeader = llSummaryofDay.findViewById(R.id.inHeader);
		inDivider = llSummaryofDay.findViewById(R.id.inDivider);

		inHeader.setVisibility(View.GONE);
		inDivider.setVisibility(View.GONE);

		tvPageTitle.setText("EOT");
		tvReturnRequest.setText("Goods Return Report");
		if (isSupervisor) {
			btnUpload.setVisibility(View.GONE);
			btnStockUnload.setVisibility(View.GONE);
		}

		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		if (llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else {
			setResult(2000);
			finish();
		}
		if (btnUpload != null) {
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
		}
	}

	@Override
	public void onConnectionException(Object msg) {
	}

	public boolean uploadOrders() {
		Vector<TrxHeaderDO> vecSalesOrders = commonDA.getAllSalesOrderToPost(strSalesmanCode);

		final InsertOrdersParser insertOrdersParser = new InsertOrdersParser(
				SalesmanSummaryofDay.this);
		if (vecSalesOrders != null && vecSalesOrders.size() > 0) {
			showLoader("Submitting orders...");
			
			ConnectionHelper connectionHelper = new ConnectionHelper(SalesmanSummaryofDay.this);
//			connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postTrxDetailsFromXML(vecSalesOrders, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXML);
			int limit = 0;
			do
			{
				for(TrxHeaderDO trxHeaderDO : vecSalesOrders)
				{
					connectionHelper.sendRequest_Bulk(this,BuildXMLRequest.postTrxDetailsFromXML(trxHeaderDO, preference.getStringFromPreference(Preference.EMP_NO, "")), insertOrdersParser, ServiceURLs.PostTrxDetailsFromXML);
				}
			
				vecSalesOrders  	 = 	new CommonDA().getAllSalesOrderToPost(preference.getStringFromPreference(Preference.EMP_NO, ""));
				limit++;
				
			}while(vecSalesOrders.size() > 0 && AppConstants.MAX_ORDER_PUSH_LIMIT>=limit);
			
			
			isOrdersUploaded = insertOrdersParser.getStatus();
		}
		return isOrdersUploaded;
	}

	public boolean uploadPayments() {
		boolean isPaymentsUploaded = false;
		Vector<PostPaymentDONew> vecPayments = new Vector<PostPaymentDONew>();
		vecPayments = commonDA
				.getAllPaymentsToPostNew(preference.getStringFromPreference(
						Preference.SALESMANCODE, ""), CalendarUtils
						.getOrderPostDate(), preference
						.getStringFromPreference(Preference.USER_ID, ""));
		ConnectionHelper connectionHelper = new ConnectionHelper(null);
		if (vecPayments != null && vecPayments.size() > 0) {
			InsertPaymentParser insertPaymentParser = new InsertPaymentParser(
					SalesmanSummaryofDay.this);
			connectionHelper.sendRequest_Bulk(SalesmanSummaryofDay.this,
					BuildXMLRequest.postPayments(vecPayments),
					insertPaymentParser, ServiceURLs.PostPaymentDetailsFromXML);
			isPaymentsUploaded = insertPaymentParser.getStatus();

			if (isPaymentsUploaded)
				commonDA.updatePaymentStatus(vecPayments);
		}
		return isPaymentsUploaded;
	}

	public void updateUploadedCustomer() {
		ArrayList<NameIDDo> arrayList = new CommonDA().getUnpostedCustomerId();
		if (arrayList != null && arrayList.size() > 0)
			new CommonDA().updateCreatedCustomers(arrayList);
	}

	public boolean uploadCustomer() {
		Vector<NewCustomerDO> vector = new CommonDA().getNewCustomerToUpload();
		final InsertCustomerParser insertCustomerParser = new InsertCustomerParser(
				SalesmanSummaryofDay.this);
		final ConnectionHelper connectionHelper = new ConnectionHelper(null);
		if (vector != null && vector.size() > 0) 
		{
			String route_code = preference.getStringFromPreference(Preference.ROUTE_CODE, "");
			connectionHelper.sendRequest_Bulk(SalesmanSummaryofDay.this,BuildXMLRequest.insertHHCustomer(vector, route_code),insertCustomerParser,ServiceURLs.INSERTHH_CUSTOMER_OFFLINE);
		}
		return true;
	}

	public boolean uploadEot(String signature) 
	{
		writeLogForEOT("\n Enter into uploadEOT() function : "+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");

		final ConnectionHelper connectionHelper = new ConnectionHelper(SalesmanSummaryofDay.this);
		// start
		String strEOTType = preference.getStringFromPreference("EOTType", "");
		String strReason = preference.getStringFromPreference("EOTReason", "");
		String strDateTime = CalendarUtils.getOrderPostDate() + "T" + CalendarUtils.getRetrunTime() + ":00";
		boolean response = false;
		if (isNetworkConnectionAvailable(SalesmanSummaryofDay.this))
		{
			showLoader("Submitting EOT...");
			isOrdersUploaded = commonDA.isAllOrderPushed(strSalesmanCode);
			if (isOrdersUploaded) 
			{
				writeLogForEOT("\n Orders are uploaded...:"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n EOT request is forming\n");

				response = connectionHelper.sendRequest_Bulk(SalesmanSummaryofDay.this, BuildXMLRequest.insertEOT(preference.getStringFromPreference(Preference.EMP_NO, ""), strEOTType,strReason, strDateTime, "", "", "", signature),ServiceURLs.INSERT_EOT);
				if (response && isOrdersUploaded) 
				{
//					new JourneyPlanDA().updaateJourneyEnds(jouneyStartDO); // jouneyStartDO is come from onActivityresult()
//					new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),"True");
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
//							preference.saveBooleanInPreference(Preference.IS_EOT_DONE, true);
//							preference.commitPreference();
							new JourneyPlanDA().updaateJourneyEnds(jouneyStartDO); // jouneyStartDO is come from onActivityresult()
							boolean isJourneyDetailPosted= postJourneyDetails();
							if(isJourneyDetailPosted)
							{
								writeLogForEOT("\nJourney details are posted...  ");
							}
							new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),"True");
							btnUpload.setVisibility(View.GONE);
							btnStockUnload.setVisibility(View.GONE);
							writeLogForEOT("\nPopupmessege- EOT is Submitted successfully"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n  ");
							showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.successful),"EOT has been submitted successfully.",getString(R.string.OK), null, "EOT", false);
						}
					});
				}
				else {
					showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "EOT has not been submitted, please try again.", getString(R.string.OK), null, "");
					writeLogForEOT("\n Popupmessege- EOT has not been submitted, please try again."+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n  ");
				}
			} 
			else {
				showCustomDialog(SalesmanSummaryofDay.this, getString(R.string.warning), "Some orders are not submitted, please do upload data", getString(R.string.OK), null, "");
				writeLogForEOT("\n Popupmessege- Some orders are not submitted, please go through order summary."+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n  ");

			}
		}
		return response;
	}

	@Override
	public void onButtonNoClick(String from) 
	{
		super.onButtonNoClick(from);
		if (from.equalsIgnoreCase("EOTSubmit")) 
		{
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
		}
	}

	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if (from.equalsIgnoreCase("finisheot"))
			finish();
		else if (from.equalsIgnoreCase("movement")) 
		{
			Intent intent = new Intent(SalesmanSummaryofDay.this,
					LoadRequestActivity.class);
			intent.putExtra("load_type", AppConstants.UNLOAD_STOCK);
			intent.putExtra("isSummary", true);
			startActivity(intent);
		}
		else if (from.equalsIgnoreCase("signature")) 
		{
			Intent obIntent = new Intent();
			obIntent.setAction(AppConstants.ACTION_GOTO_HOME1);
			sendBroadcast(obIntent);
		}
		else if (from.equalsIgnoreCase("EOTSubmit")) 
		{
			customDialog.dismiss();
			showSignatureDialog();
		}
		else if(from.equalsIgnoreCase("skipReason"))
		{
			tvUnvisitedcustomer.performClick();
		}
	}

	private Vector<ServiceCaptureDO> vecServiceCaptureDOs;
	@Override
	protected void onResume() 
	{
		super.onResume();
//		if (preference.getbooleanFromPreference(Preference.IS_EOT_DONE, false))
		if(isEOTDone())
		{
			btnUpload.setVisibility(View.GONE);
			if (!preference.getbooleanFromPreference(Preference.SIGNATURE + CalendarUtils.getOrderPostDate(), false))
				showSignatureDialog();
		}
		
		showLoader("Please wait...");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				arrJP = new CustomerDetailsDA().getUnvisitedCustomers();
				vecServiceCaptureDOs = new StoreCheckDA().getServiceCapture();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						hideLoader();
						
						if(vecServiceCaptureDOs != null && vecServiceCaptureDOs.size() > 0)
							tvPendingImage.setText("Upload pending: "+(vecServiceCaptureDOs.size() * 2)+"");
						else
							tvPendingImage.setVisibility(View.GONE);
					}
				});
			}
		}).start();
	}

	private void showInventoryPopup(View view) 
	{
		LinearLayout llBaseMenuPopup = (LinearLayout) inflater.inflate(R.layout.inventory_popup, null);

		TextView tvUndeliveredQT = (TextView) llBaseMenuPopup.findViewById(R.id.tvUndeliveredQT);
		TextView tvReturnOrderQT = (TextView) llBaseMenuPopup.findViewById(R.id.tvReturnOrderQT);
		final PopupWindow popupWindowBaseMenu = new PopupWindow(llBaseMenuPopup, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		popupWindowBaseMenu.setOutsideTouchable(true);
		popupWindowBaseMenu.setTouchInterceptor(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) 
				{
					popupWindowBaseMenu.dismiss();
					return true;
				}
				return false;
			}
		});
		popupWindowBaseMenu.setBackgroundDrawable(new BitmapDrawable());
		popupWindowBaseMenu.showAsDropDown(view, 0, (int) (2 * px));

		tvUndeliveredQT.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvReturnOrderQT.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvUndeliveredQT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				popupWindowBaseMenu.dismiss();
				Intent intent = new Intent(SalesmanSummaryofDay.this,Inventory_QTY.class);
				startActivity(intent);
			}
		});

		tvReturnOrderQT.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				popupWindowBaseMenu.dismiss();
				Intent intent = new Intent(SalesmanSummaryofDay.this,Return_Order_QTY.class);
				startActivity(intent);
			}
		});
	}

	private void showSignatureDialog() 
	{
		final Dialog dialog = new Dialog(this, R.style.Dialog);
		LinearLayout llSignature = (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		final LinearLayout llSignSupervisor = (LinearLayout) llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout) llSignature.findViewById(R.id.llSignDriver);

		Button btnOK = (Button) llSignature.findViewById(R.id.btnOK);
		Button btnSKCear = (Button) llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear = (Button) llSignature.findViewById(R.id.btnDriverCear);
		Button btnCancle = (Button) llSignature.findViewById(R.id.btnCancle);
		TextView tvLogisticsSignature	= (TextView) llSignature.findViewById(R.id.tvLogisticsSignature);
		final LinearLayout llLPO		= (LinearLayout)llSignature.findViewById(R.id.llLPO);

		tvLogisticsSignature.setText("ASM Signature");
		dialog.addContentView(llSignature, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		dialog.show();
		
		llLPO.setVisibility(View.GONE);
		
		managerSignatureView = new SignatureView(this);
		managerSignatureView.setDrawingCacheEnabled(true);
		managerSignatureView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (180 * px)));
		managerSignatureView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignSupervisor.addView(managerSignatureView);

		driveSignatureView = new SignatureView(this);
		driveSignatureView.setDrawingCacheEnabled(true);
		driveSignatureView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (180 * px)));
		driveSignatureView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llSignDriver.addView(driveSignatureView);

		setTypeFaceRobotoNormal(llSignature);
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				writeLogForEOT("\n btnOk is clicked in popup for EOT :"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");
				if (!managerSignatureView.isSigned() && !driveSignatureView.isSigned()) 
				{
					showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Please take store keeper's signature.", "OK",null, "");
				}
				else if (!managerSignatureView.isSigned())
					showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Please take store keeper's signature.", "OK",null, "");
				else if (!driveSignatureView.isSigned())
					showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),"Please sign before submitting the stock verification.","OK", null, "");
				else 
				{
					showLoader("Please wait...");
					new Thread(new Runnable() 
					{
						String driveSignatureFilePath,managerSignatureFilePath;

						@Override
						public void run() 
						{
							Bitmap bitmap = driveSignatureView.getBitmap();
							if (bitmap != null)
								driveSignatureFilePath = BitmapsUtiles.saveVerifySignature(bitmap);

							bitmap = managerSignatureView.getBitmap();
							if (bitmap != null)
								managerSignatureFilePath = BitmapsUtiles.saveVerifySignature(bitmap);

//							final boolean isUpdated = uploadEot("");

							runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									dialog.dismiss();

//									if (isUpdated) 
//									{
//										preference.saveBooleanInPreference(Preference.SIGNATURE + CalendarUtils.getOrderPostDate(),true);
//										preference.commitPreference();
//									}

									//commented by vinod
//									btnUpload.setVisibility(View.GONE);
//									btnStockUnload.setVisibility(View.GONE);
									writeLogForEOT("\n moved to OdometerActivity:"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");

									Intent intent = new Intent(SalesmanSummaryofDay.this,OdometerReadingActivity.class);
									intent.putExtra("isStartDay", false);
									intent.putExtra("image_path",managerSignatureFilePath);
									intent.putExtra("image_path_driver",driveSignatureFilePath);
									startActivityForResult(intent, 5000);
									hideLoader();
								}
							});
						}
					}).start();
				}
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				dialog.dismiss();
			}
		});
		btnDriverCear.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (driveSignatureView != null)
					driveSignatureView.resetSign();
			}
		});

		btnSKCear.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (managerSignatureView != null)
					managerSignatureView.resetSign();
			}
		});

		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				btnUpload.setEnabled(true);
				btnUpload.setClickable(true);
			}
		}, 500);
	}

	private void performEOT() 
	{
		if (isNetworkConnectionAvailable(SalesmanSummaryofDay.this)) 
		{
			showLoader("This may take time please wait...");
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					uploadData();
					final boolean isUpdated = uploadEot("");
//					runOnUiThread(new Runnable() 
//					{
//						@Override
//						public void run() 
//						{
//							if (isUpdated) 
//							{
//								preference.saveBooleanInPreference(Preference.SIGNATURE + CalendarUtils.getOrderPostDate(),true);
//								preference.commitPreference();
//							}
//							btnUpload.setEnabled(true);
//							btnUpload.setClickable(true);
//							hideLoader();
//						}
//					});
				}
			}).start();
		} else {
			btnUpload.setEnabled(true);
			btnUpload.setClickable(true);
			showCustomDialog(
					SalesmanSummaryofDay.this,
					getString(R.string.warning),
					"EOT is not submitted due to problem in network connection. Please check your internet connection and try again.",
					getString(R.string.OK), null, "finisheot");
            writeLogForEOT("\n bacause of bad internet problem : EOT status has changed -->False <--called insertEOT():"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");
//			preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
//			preference.commitPreference();
			/*new Thread(new Runnable()
			{
				@Override
				public void run()
				{


					new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),"False");
				}
			}).start();*/
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.resultCode = resultCode;
		 if(resultCode == 5000)
		 {
			 writeLogForEOT("\ncome back to salesmansummary of day activity:"+CalendarUtils.getCurrentDateAsStringForJourneyPlan()+"\n");

			 performEOT();
			 jouneyStartDO = (JouneyStartDO) data.getExtras().get("JouneyEND");
		 }
		 else
		 {
			 btnUpload.setEnabled(true);
			 btnUpload.setClickable(true);
		 }
	}

	int uploadCount = 3;
	@Override
	public void transactionStatus(Transactions transactions,TransactionSatus transactionSatus) {
		LogUtils.debug("resultCode", "" + resultCode);
		LogUtils.debug("transactions", "" + transactions +":"+ Transactions.JOURNEYSTART);
		if (transactionSatus == TransactionSatus.END && resultCode == 5)
		{
			if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
				isDatatoUpload = new EOTDA().getEOTDetailsForPreSales();
			else
				isDatatoUpload = new EOTDA().getEOTDetailsForVansales();
			
			if(isDatatoUpload/* && uploadCount > 0*/)
			{
				uploadData();
				isDatatoUpload = false;
			}
//			else
			else
			{
				hideLoader();
				// btnUpload.setEnabled(false);
				// btnUpload.setClickable(false);
				showCustomDialog(SalesmanSummaryofDay.this,getString(R.string.warning),
						"Are you sure you want to submit EOT?",getString(R.string.Yes), getString(R.string.No),"EOTSubmit");
				resultCode = 0;
			}
		}
		else if (JOURNEYSTART && transactionSatus == TransactionSatus.SUCCESS && resultCode == 5000) 
		{
			if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
				isDatatoUpload = new EOTDA().getEOTDetailsForPreSales();
			else
				isDatatoUpload = new EOTDA().getEOTDetailsForVansales();
			
			/*if(isDatatoUpload && uploadCount > 0)
			{
//				UploadTransactions.setTransactionProcessListner(SalesmanSummaryofDay.this);
				uploadData();
				uploadCount--;
				isDatatoUpload = false;
			}
			else*/ if(isDatatoUpload/* && uploadCount <= 0*/)
			{
				new Thread(new Runnable()
				{
					@Override
					public void run() 
					{
						try
						{
							FilesStorage.copy(AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME, Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME);
							String empNo=preference.getStringFromPreference(Preference.EMP_NO, "");
							ArrayList<File> arr=new ArrayList<File>();
							arr.add(new File(Environment.getExternalStorageDirectory().toString()+ "/" + AppConstants.DATABASE_NAME));
							String zipFiilePath=Environment.getExternalStorageDirectory().toString()+ "/"+empNo+"_"+CalendarUtils.getOrderPostDate()+"_"+System.currentTimeMillis()+".zip";
							ZipUtils.zipFiles(arr, new File(zipFiilePath));
							
							uploadDB(zipFiilePath);
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}).start();
			}
		// commented by vinod since it calling 2 times.. one is from this transactionstatus() another is from onActivityResult()->performEOT()->uploadEOT()
//			else
//			{
//				final boolean isUpdated = uploadEot("");
//
//				runOnUiThread(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if (isUpdated)
//						{
//							preference.saveBooleanInPreference(Preference.SIGNATURE + CalendarUtils.getOrderPostDate(),true);
//							preference.commitPreference();
//						}
//						btnUpload.setEnabled(true);
//						btnUpload.setClickable(true);
//						hideLoader();
//					}
//				});
//				resultCode = 0;
//			}
		}
	}
	
	@Override
	public void error(TransactionSatus transactionSatus) {
		hideLoader();
		switch (transactionSatus) {
		case ERROR_NO_INTERNETCONNECTION:
			showCustomDialog(SalesmanSummaryofDay.this,
					getString(R.string.warning),
					getString(R.string.no_internet), getString(R.string.OK),
					null, "");
			break;
		default:
			showCustomDialog(SalesmanSummaryofDay.this,
					getString(R.string.warning), "EOT FAILURE",
					getString(R.string.OK), null, "");
			break;
		}
	}

	boolean isDisplay = true;
	String message = "";

	boolean JOURNEYSTART = false;
	@Override
	public void currentTransaction(Transactions transaction) {
		isDisplay = true;
		message = "";
		JOURNEYSTART = false;
		switch (transaction) {
		case ORDERS:
			message = "Uploading Orders";
			break;
		case PAYMENTS:
			message = "Uploading Payments";
			break;
		case COLLECTEDRETURNORDER:
			message = "Uploading Payments";
			break;
		case JOURNEYSTART:
			message = "Uploading Journey";
			JOURNEYSTART = true;
			break;
		case NONE:
			isDisplay = false;

			break;
		default:
			break;
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tvTransactionType.setText(message);
				if (isDisplay) {
					tvTransactionType.setVisibility(View.VISIBLE);
					ivProgressBar.setVisibility(View.VISIBLE);
				} else {
					tvTransactionType.setVisibility(View.GONE);
					ivProgressBar.setVisibility(View.GONE);
				}
			}
		});
	}

	EditText edtLogistic, edtSalesman;
	private ConnectionHelper connectionHelper;


	
	private boolean uploadDB(String dbPath) {
		boolean isError = false;
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(String.format(
					ServiceURLs.UPLOAD_DB, "competitor"));
			File filePath = new File(dbPath);

			if (filePath.exists()) {
				Log.e("uplaod", "called");
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody cbFile = new FileBody(filePath, "image/png");
				mpEntity.addPart("FileName", cbFile);
				httppost.setEntity(mpEntity);
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				is = resEntity.getContent();

			}
			String serverUrl = parseImageUploadResponse(SalesmanSummaryofDay.this, is);
			LogUtils.debug("serverUrl", "serverUrl "+serverUrl);
			if(!TextUtils.isEmpty(serverUrl))
				isError=false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			isError = true;
		} catch (IOException e) {
			e.printStackTrace();
			isError = true;
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
		} finally {
		}
		return isError;
	}
	
	public static String parseImageUploadResponse(Context context,
			InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ImageUploadParser handler = new ImageUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private boolean  postJourneyDetails()
	{
		boolean isJourneyDetailPosted =false;
		try
		{
			final Vector<JouneyStartDO> vecJourneyStart = new JourneyPlanDA().getJourneyStart();



			boolean isSuccess = true, isSuccessDriver = true;
			for(JouneyStartDO journey : vecJourneyStart)
			{
				if(journey.StoreKeeperSignatureStartDay != null)
				{
					if(new File(journey.StoreKeeperSignatureStartDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.StoreKeeperSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.StoreKeeperSignatureStartDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.STORE_SIGN_START, server_path, journey.journeyAppId);
						}
						else
							isSuccess = false;
					}

					if(new File(journey.StoreKeeperSignatureEndDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.StoreKeeperSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.StoreKeeperSignatureEndDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.STORE_SIGN_END, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = true;
					}

					if(new File(journey.SalesmanSignatureStartDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.SalesmanSignatureStartDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.SalesmanSignatureStartDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.SALES_SIGN_START, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = false;
					}

					if(new File(journey.SalesmanSignatureEndDay).exists())
					{
						String server_path = new UploadImage().uploadImage(this, journey.SalesmanSignatureEndDay, ServiceURLs.stockverifiedsignature, true);
						if(server_path != null && server_path.length() > 0)
						{
							journey.SalesmanSignatureEndDay = server_path;
							new JourneyPlanDA().updateJourneyStartSignature(AppConstants.SALES_SIGN_END, server_path, journey.journeyAppId);
						}
						else
							isSuccessDriver = true;
					}
				}
			}
			if(isSuccess && isSuccessDriver && vecJourneyStart != null && vecJourneyStart.size()>0)
			{
				if(new ConnectionHelper(null).sendRequest(this,BuildXMLRequest.getStartJournyStart(vecJourneyStart), ServiceURLs.PostJourneyDetails))
				{
					for(JouneyStartDO journey : vecJourneyStart)
						new JourneyPlanDA().updateJourneyStartUploadStatus(true, journey.journeyAppId);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return  isJourneyDetailPosted;
	}
}
