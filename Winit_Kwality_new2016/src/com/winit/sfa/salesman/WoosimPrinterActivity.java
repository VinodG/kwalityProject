package com.winit.sfa.salesman;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.EOTDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentSummeryDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.BaseComparableDO;
import com.winit.alseer.salesman.dataobject.CashDenomDO;
import com.winit.alseer.salesman.dataobject.CustomerStatmentDO;
import com.winit.alseer.salesman.dataobject.Customer_InvoiceDO;
import com.winit.alseer.salesman.dataobject.DailySalesData;
import com.winit.alseer.salesman.dataobject.DailySumDO;
import com.winit.alseer.salesman.dataobject.DailySummaryDO;
import com.winit.alseer.salesman.dataobject.Denominations;
import com.winit.alseer.salesman.dataobject.InventoryObject;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PaymentHeaderDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.utilities.BitmapConvertor;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.NumberUtils;
import com.winit.alseer.salesman.utilities.OnMonochromeCreated;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
import com.woosim.bt.WoosimPrinter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class WoosimPrinterActivity extends Activity implements OnClickListener
{

	//	private static final int INVOICE = 1, GRV = 2, PAYMENT = 3;
	private static String EXTRA_DEVICE_ADDRESS = "device_address";
	private final static byte DATA = 0x44, ETX = 0x03, EOT = 0x04, NACK	= 0x15, MSR_FAIL = 0x4d, ACK  = 0x06;;
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter, mNewDevicesArrayAdapter;
	private static String address/*, type = "",strSelectedDate*/;
	private WoosimPrinter woosim;
	private TrxHeaderDO trxHeaderDO;
	private Button btnOpen, btnClose, btnPrint2, btnPrint3, btnMSR23, btnMSR123, btnCardCancel, btnFinish, btnPrintImg1, btnPrintImg2, btnReprint , btnFinishPrint;
	private CheckBox cheProtocol;
	private EditText editTrack1, editTrack2, editTrack3;
	public  ProgressDialog progressdialog;
	private TextView tvPrintHeader;
	private final static String EUC_KR = "EUC-KR";
//	private final static String EUC_KR = "EUC-UN";
	private byte[] extractdata = new byte[300], cardData;
	private CONSTANTOBJ CALLFROM ;
	private boolean isPrinted = false/*, isDuplicate*/;
	private PaymentHeaderDO postPaymentDO, objPaymentDO;
	private Preference preference;
	private int isMTD;
	private TrxLogHeaders trxLogHeaders,trxMonthDetails;
	private HashMap<String, Customer_InvoiceDO> hmCashDetails = new HashMap<String, Customer_InvoiceDO>();
	private HashMap<String, Customer_InvoiceDO> hmChequeDetails = new HashMap<String, Customer_InvoiceDO>();
	private String strReceiptNo = "123", cardDetail="",cardName = "",strCardName = "",fromDate="",toDate="";
	/*, strSelectedDateToPrint, strInvoiceNo, strOrderno, strPresellerName, orderRefNumbers, invoiceNumbers, paymentDueDate, strInvoiceDate = "", presellerName*/
	private Vector<Customer_InvoiceDO> veCollection;
	private ArrayList<CustomerStatmentDO> vecDetails;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers;
	private ArrayList<InventoryObject> vecInventoryItems;
	private CashDenomDO objCashDenom;
	private ArrayList<DailySummaryDO> arrDailySummary = null;
	public ArrayList<BaseComparableDO> customerList;
	public ArrayList<VanLoadDO> vecOrdProduct;
	private JourneyPlanDO mallsDetails;
	private String paymode="";
	private DecimalFormat deffAmt;
	private DailySumDO objDailySumDO;
	private int dailySummary = 0;
	private int MOVEMENT_STATUS = 0;
	private Vector<Customer_InvoiceDO> vecCustomerInvoiceDo= new Vector<Customer_InvoiceDO>();
	private Vector<DailySalesData> vecDailySalesData;
	private String salesmanContact = "";

	//===================================newly added for food	==============================================
	int printTypeIce =100;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		preference = new Preference(WoosimPrinterActivity.this);

		deffAmt = new DecimalFormat("##.##");
		deffAmt.setMinimumFractionDigits(2);
		deffAmt.setMaximumFractionDigits(2);
		showLoader("Searching devices...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String path = Environment.getExternalStorageDirectory() +"/invoice.bmp";
				if(new File(path).exists())
					new File(path).delete();

				salesmanContact = new EOTDA().getSalesmanContactNo(preference.getStringFromPreference(preference.USER_ID, ""));
				runOnUiThread(new Runnable()
				{

					@SuppressWarnings("unchecked")
					@Override
					public void run()
					{
						//Initializing the WoosimPrinter object AlSeer Trading
						woosim = new WoosimPrinter();
						woosim.setHandle(acthandler);
						//getting vector from intent
						if(getIntent().getExtras() != null)
						{
							//this variable will get the the call from
							CALLFROM 			  =		(CONSTANTOBJ) getIntent().getExtras().get("CALLFROM");
							switch (CALLFROM)
							{
								//condition to print the Sales Order
								case PRINT_SALES:
									mallsDetails    =   (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									trxHeaderDO 	=	(TrxHeaderDO) getIntent().getExtras().getSerializable("trxHeaderDO");

//									if(getIntent().hasExtra("str"))
//										 str  = getIntent().getExtras().getString("str");
//=============================nely addded for food======================PrintTypeIce
									if(getIntent().hasExtra("PrintTypeIce"))
										printTypeIce  = getIntent().getExtras().getInt("PrintTypeIce");
									break;
								case PRINT_SALES_PREVIEW:
									mallsDetails    =   (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									trxHeaderDO 	= 	(TrxHeaderDO) getIntent().getExtras().getSerializable("trxHeaderDO");
//									if(getIntent().hasExtra("str"))
//										 str  = getIntent().getExtras().getString("str");
									break;
								//condition to print the Payment Reciept
								case PAYMENT_RECEIPT:
									arrInvoiceNumbers = (ArrayList<PendingInvicesDO>) getIntent().getExtras().getSerializable("arrInvoiceNumbers");
									strReceiptNo 	= 	getIntent().getExtras().getString("strReceiptNo");
									mallsDetails  	=   (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									objPaymentDO 	= 	(PaymentHeaderDO) getIntent().getExtras().get("paymentHeaderDO");
									if(getIntent().hasExtra("paymode"))
										paymode		=	getIntent().getExtras().getString("paymode");
//=============================nely addded for food======================PrintTypeIce
									if(getIntent().hasExtra("PrintTypeIce"))
										printTypeIce  = getIntent().getExtras().getInt("PrintTypeIce");

									break;
								//condition to print the Stock Summary
								case PRINT_INVENTORY:
									vecOrdProduct=AddStockInVehicle.vecOrdProduct;
									break;
								case PRINT_STOCK_DETAILS:
									vecOrdProduct = (ArrayList<VanLoadDO>) getIntent().getExtras().getSerializable("STOCK_DETAILS");
									MOVEMENT_STATUS  		=     getIntent().getExtras().getInt("MOVEMENT_STATUS");
									break;
								case PRINT_VERIFY_INVENTOTY:
									vecOrdProduct=VerifyItemInVehicle.vecOrdProduct;
									break;
								//condition to print the Payment Summary
								case PAYMENT_SUMMARY:
									if(getIntent().hasExtra("hmCashDetails"))
										hmCashDetails = (HashMap<String, Customer_InvoiceDO>) getIntent().getExtras().getSerializable("hmCashDetails");
									if(getIntent().hasExtra("hmChequeDetails"))
										hmChequeDetails = (HashMap<String, Customer_InvoiceDO>) getIntent().getExtras().getSerializable("hmChequeDetails");
									fromDate  		=     getIntent().getExtras().getString("fromDate");
									toDate  		=     getIntent().getExtras().getString("toDate");
									break;
								//condition to print the Pending Payment
								case PRINT_PENDING_INVOICE:
									arrInvoiceNumbers = (ArrayList<PendingInvicesDO>) getIntent().getExtras().getSerializable("arrInvoiceNumbers");
									mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									break;
								//condition to print the Customer Statement
								case PRINT_CUSTOMER_STATEMENT:
									mallsDetails  	=     (JourneyPlanDO)getIntent().getExtras().getSerializable("mallsDetails");
									vecDetails 		= 	  (ArrayList<CustomerStatmentDO>)getIntent().getExtras().getSerializable("vecDetails");
									fromDate  		=     getIntent().getExtras().getString("fromDate");
									toDate  		=     getIntent().getExtras().getString("toDate");
									break;
								case PRINT_LOG_REPORT:
									fromDate  		=     getIntent().getExtras().getString("fromDate");
									toDate  		=     getIntent().getExtras().getString("toDate");
									isMTD			= 	  getIntent().getExtras().getInt("isMTD");
									trxLogHeaders 	=     (TrxLogHeaders) getIntent().getExtras().getSerializable("trxLogHeaders");
									break;
								case PRINT_CASH_DENOMINATION:
									objCashDenom 	=     (CashDenomDO) getIntent().getExtras().getSerializable("CashDenom");
									break;
								case PRINT_DAILY_SUMMARY:
//									arrDailySummary =  (ArrayList<DailySummaryDO>) getIntent().getExtras().getSerializable("daily_summary");
									objDailySumDO	=   (DailySumDO) getIntent().getExtras().getSerializable("daily_summary");
									fromDate		=	getIntent().getExtras().getString("select_date");
									dailySummary	=	getIntent().getIntExtra("print_type", 0);
									break;
								case PRINT_STOCK_INVENTORY:
									vecInventoryItems =  (ArrayList<InventoryObject>) getIntent().getExtras().getSerializable("vecInventoryItems");
									fromDate		=	getIntent().getExtras().getString("select_date");
									break;
								default:
									break;
							}
						}

						hideLoader();
						showDeviceLists(true);
					}
				});
			}
		}).start();

	}

	//Total
	/**
	 * Method to print the generated sales order in 3 inch mode
	 */
	String format = "";
	private void printBarcodeImage(final String strNo, int type, String siteNo)
	{
		Bitmap bitmap = null;
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		try
		{
			com.google.zxing.Writer c9 = new Code128Writer();
			try {
				format = strNo;
				BitMatrix bm = c9.encode(format, BarcodeFormat.CODE_128, 550, 130);
				bitmap = Bitmap.createBitmap(550, 130, Config.RGB_565);

				for (int i = 0; i < 550; i++) {
					for (int j = 0; j < 130; j++) {

						bitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK
								: Color.WHITE);
					}
				}
			} catch (WriterException e)
			{
				hideLoader();
				e.printStackTrace();
			}

			BitmapConvertor convertor = new BitmapConvertor(WoosimPrinterActivity.this, new OnMonochromeCreated()
			{
				@Override
				public void onCompleted(String path)
				{
					try
					{
						woosim.saveSpool(EUC_KR, "\r\n", 0, false);
						woosim.saveSpool(EUC_KR, "         ", 0, false);
						woosim.printBitmap(path);
						woosim.saveSpool(EUC_KR, "\r\n", 0, false);
						woosim.saveSpool(EUC_KR, "                         "+format+"                       ", 0, false);
						byte[] ff ={0x0c};
						woosim.controlCommand(ff, 1);
						byte[] lf = {0x0a};
						woosim.controlCommand(lf, lf.length);
						woosim.saveSpool(EUC_KR, "\r\n\r\n\r\n", 0, false);
						woosim.printSpool(true);
						cardData = null;
						isPrinted = true;
						hideLoader();
					}
					catch (Exception e)
					{
						hideLoader();
						e.printStackTrace();
					}

				}
			});
			convertor.convertBitmap(bitmap, "invoice");
		}
		catch (Exception e)
		{
			hideLoader();
			e.printStackTrace();
		}
	}

	public void showDeviceLists(final boolean isPaired)
	{
		setContentView(R.layout.device_list);
		setResult(Activity.RESULT_CANCELED);

		mPairedDevicesArrayAdapter 	= 	new ArrayAdapter<String>(WoosimPrinterActivity.this,	R.layout.device_name);
		mNewDevicesArrayAdapter 	= 	new ArrayAdapter<String>(WoosimPrinterActivity.this, R.layout.device_name);

		// Find and set up the ListView for paired devices
		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setCacheColorHint(0);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		// Find and set up the ListView for newly discovered devices
		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setCacheColorHint(0);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		WoosimPrinterActivity.this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		WoosimPrinterActivity.this.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		if(mBtAdapter!=null)
		{
			doDiscovery();
			// Get a set of currently paired devices
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();


			// If there are paired devices, add each one to the ArrayAdapter
			if (pairedDevices.size() > 0)
			{
				findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
				for (BluetoothDevice device : pairedDevices)
				{
					mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					if(device.getName().equalsIgnoreCase("woosim") && isPaired)
					{
						mBtAdapter.cancelDiscovery();
						// Get the device MAC address, which is the last 17 chars in the
						// View
						address = device.getAddress();
						// Create the result Intent and include the MAC address
						Intent intent = new Intent();
						intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
						System.out.println("address" + address);
						setContentView(R.layout.woosim);
						createButton();
						// Set result and finish this Activity
						setResult(Activity.RESULT_OK, intent);
						btnOpen.performClick();
						break;
					}
				}
			}
		}
	}

	public Handler acthandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == DATA)
			{
				LogUtils.errorLog("+++Activity+++", "******0x01");
				Object obj1 = msg.obj;
				cardData = (byte[]) obj1;
				ToastMessage();
				hideLoader();

				if(cardDetail.equalsIgnoreCase("") || cardName.equalsIgnoreCase("") || strCardName.equalsIgnoreCase(""))
				{
					showCustomDialog(WoosimPrinterActivity.this, "Sorry", "Card is not swiped successfully.Do you want swipe again?", "Yes", "No", "cardswipeAgain", false);
					LogUtils.errorLog("+++Activity+++", "MSRFAIL: [" + msg.arg1 + "]: ");
					editTrack1.setText("MSRFAIL");

				}
				else
				{
					showCustomDialog(WoosimPrinterActivity.this, "Thankyou", "Your card swiped successfully.", "Ok", null, "cardswipesuccessfully", false);
				}

			}
			else if (msg.what == MSR_FAIL)
			{
				hideLoader();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry", "Card is not swiped successfully.Do you want swipe again?", "Yes", "No", "cardswipe");
				LogUtils.errorLog("+++Activity+++", "MSRFAIL: [" + msg.arg1 + "]: ");
				editTrack1.setText("MSRFAIL");
			}
			else if (msg.what == EOT)
			{
				LogUtils.errorLog("+++Activity+++", "******EOT");
				editTrack1.setText("EOT");
			}
			else if (msg.what == ETX)
			{
				LogUtils.errorLog("+++Activity+++", "******ETX");
				editTrack2.setText("ETX");
			}
			else if (msg.what == NACK)
			{
				LogUtils.errorLog("+++Activity+++", "******NACK");
				editTrack3.setText("NACK");
			}
			else if (msg.what == ACK)
			{
				LogUtils.errorLog("+++Activity+++", "******ACK");
				editTrack3.setText("ACK");
			}
		}
	};

	private void ToastMessage()
	{
		byte[] track1Data = new byte[76];
		byte[] track2Data = new byte[37];
		byte[] track3Data = new byte[104];

		int dataLength = woosim.extractCardData(cardData, extractdata);
		int i = 0, j = 0, k = 0;
		if (dataLength == 76) {
			LogUtils.errorLog("dataLength == 76", "dataLength == 76");
			for (i = 0; i < 76; i++) {
				track1Data[i] = extractdata[i];
			}
			String str = new String(track1Data);
			editTrack1.setText(str);
			cardDetail =str;


			editTrack2.setText("No Data");
			editTrack3.setText("No Data");
		} else if (dataLength == 37) {
			LogUtils.errorLog("dataLength == 37", "dataLength == 37");
			for (i = 0; i < 37; i++) {
				track2Data[i] = extractdata[i];
			}
			String str = new String(track2Data);
			editTrack1.setText("No Data");
			editTrack2.setText(str);
			editTrack3.setText("No Data");
		} else if (dataLength == 104) {
			LogUtils.errorLog("dataLength == 104", "dataLength == 104");
			for (i = 0; i < 104; i++) {
				track3Data[i] = extractdata[i];
			}
			String str = new String(track3Data);
			editTrack1.setText("No Data");
			editTrack2.setText("No Data");
			editTrack3.setText(str);
		}
		// 1,2track
		else if (dataLength == 113) {
			LogUtils.errorLog("+++Activitiy+++", "dataLength: " + dataLength);
			for (i = 0; i < 113; i++) {
				if (i < 76) {
					track1Data[i] = extractdata[i];
				} else {
					track2Data[j++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = new String(track2Data);
			String str3 = "No Data";
			cardName = str1;
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
		}
		// 1,3track
		else if (dataLength == 180) {
			for (i = 0; i < 180; i++) {
				if (i < 76) {
					track1Data[i] = extractdata[i];
				} else {
					track3Data[j++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = "No Data";
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
		}
		// 2,3track
		else if (dataLength == 141)
		{
			for (i = 0; i < 141; i++)
			{
				if (i < 37)
				{
					track2Data[i] = extractdata[i];
				}
				else
				{
					track3Data[j++] = extractdata[i];
				}
			}

			String str1 = "No Data";
			String str2 = new String(track2Data);
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);

		}
		// 1,2,3track
		else if (dataLength == 217)
		{
			for (i = 0; i < 217; i++)
			{
				if (i < 76)
				{
					track1Data[i] = extractdata[i];
				}
				else if (i >= 76 && i < 113)
				{
					track2Data[j++] = extractdata[i];
				}
				else
				{
					track3Data[k++] = extractdata[i];
				}
			}

			String str1 = new String(track1Data);
			String str2 = new String(track2Data);
			String str3 = new String(track3Data);
			cardDetail =str2;
			editTrack1.setText(str1);
			editTrack2.setText(str2);
			editTrack3.setText(str3);
			LogUtils.errorLog("str2",""+str2);
		}
		if(cardName != null && cardName.contains("^") && cardName.contains("/") )
		{
			strCardName = cardName.substring(cardName.indexOf("^")+1, cardName.indexOf("/"));
			if(strCardName != null)
				strCardName = strCardName.trim();
		}
		if(cardDetail.contains("="))
		{
//			postPaymentDO = new PaymentHeaderDO();
//			String cardDetai[] = cardDetail.split("=");
//			if(cardDetai.length == 2)
//			{
//				postPaymentDO.CreditCardNumber 	= 	cardDetai[0];
//				postPaymentDO.ExpiryDate 		= 	"20"+cardDetai[1].substring(0,2)+"/"+cardDetai[1].substring(2,4);
//				postPaymentDO.strCardName  		= 	strCardName;
//			}
		}
		else if(cardDetail.contains("^"))
		{
//			postPaymentDO = new PaymentHeaderDO();
//			LogUtils.errorLog("cardDetail", "cardDetail - "+cardDetail);
//			postPaymentDO.CreditCardNumber = cardDetail.substring(0, cardDetail.indexOf("^"));
//			if(!Character.isDigit(postPaymentDO.CreditCardNumber.charAt(0)))
//				postPaymentDO.CreditCardNumber = postPaymentDO.CreditCardNumber.substring(1);
//			postPaymentDO.ExpiryDate = "20"+cardDetail.substring(cardDetail.lastIndexOf("^")+1).substring(0, 2)+"-"+cardDetail.substring(cardDetail.lastIndexOf("^")+1).substring(2,4)+"-01";
//			postPaymentDO.strCardName  		= 	strCardName;
		}
	}

	public void onClick(View v)
	{
		if (v.getId() == R.id.btn_open)
		{
			int reVal = woosim.BTConnection(address, cheProtocol.isChecked());
			if (reVal == 1)
			{
				Toast t = Toast.makeText(this, "SUCCESS CONNECTION!", Toast.LENGTH_SHORT);
//				if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//				{
//					btnFinishPrint.setVisibility(View.GONE);
//					btnReprint.setVisibility(View.GONE);
//					btnMSR23.performClick();
//				}
//				else
//				{
				btnFinishPrint.setVisibility(View.VISIBLE);
				btnReprint.setVisibility(View.VISIBLE);
				btnPrint3.performClick();
//				}
				t.show();
			}
			else if (reVal == -2)
			{
				Toast t = Toast.makeText(this, "NOT CONNECTED",		Toast.LENGTH_SHORT);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry ", "Printer not connected search for printer?", "Search Printer", "", "printer");
			}
			else if (reVal == -5)
			{
				Toast t = Toast.makeText(this, "DEVICE IS NOT BONDED",	Toast.LENGTH_SHORT);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry ", "Printer not paired. Please pair and try again.", "OK", "", "");
			}
			else if (reVal == -6)
			{
				Toast t = Toast.makeText(this, "ALREADY CONNECTED",	Toast.LENGTH_SHORT);
				t.show();
//				if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//				{
//					btnMSR23.performClick();
//				}
//				else
//				{
				btnPrint3.performClick();
//				}
			}
			else if (reVal == -8)
			{
				Toast t = Toast	.makeText(this,"Please enable your Bluetooth and re-run this program!",	Toast.LENGTH_LONG);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry ", "Printer not connected search for printer?", "Search Printer", "", "printer");
			}
			else
			{
				Toast t = Toast.makeText(this, "ELSE", Toast.LENGTH_SHORT);
				t.show();
				showCustomDialog(WoosimPrinterActivity.this, "Sorry ", "Printer not connected search for printer?", "Search Printer", "", "printer");
			}

		}

		if (v.getId() == R.id.btn_close)
		{
			closeForm();
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

		}

		if (v.getId() == R.id.btn_print2inch)
		{
//			Print_2inch();
			//TEST();
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

		}

		if (v.getId() == R.id.btn_print3inch)
		{

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					showLoader("Printing...");
					switch (CALLFROM)
					{
						case PRINT_SALES:
							if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
								printSalesNew(0);
							printSalesNew(1);
//							if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
//								printSales(0);
//							printSales(1);
							break;
						case PRINT_SALES_PREVIEW:
							printSalesPreview();
							break;
						case PAYMENT_RECEIPT:
							printReceipt(0);
							printReceipt(1);
							break;
						case PRINT_INVENTORY:
							printInventory();
							break;
						case PRINT_STOCK_DETAILS: //newly added for movement details
							printInventoryDetails();
							break;
						case PRINT_VERIFY_INVENTOTY:
							printInventory();
							break;
						case PAYMENT_SUMMARY:
							veCollection =new Vector<Customer_InvoiceDO>();
							Vector<String> vecKeys = new Vector<String>();
							if((hmCashDetails != null && !hmCashDetails.isEmpty()))
							{
								Set<String> set = hmCashDetails.keySet();
								Iterator<String> iterator = set.iterator();
								while (iterator.hasNext())
									vecKeys.add(iterator.next());
								for(int  i = 0; i < vecKeys.size(); i++)
								{
									if(hmCashDetails.get(vecKeys.get(i))!=null)
										veCollection.add(hmCashDetails.get(vecKeys.get(i)));
								}

							}
							if(hmChequeDetails != null && !hmChequeDetails.isEmpty())
							{
								Set<String> set = hmChequeDetails.keySet();
								Iterator<String> iterator = set.iterator();
								while (iterator.hasNext())
									vecKeys.add(iterator.next());
								for(int  i = 0; i < vecKeys.size(); i++)
								{
									if(hmChequeDetails.get(vecKeys.get(i))!=null)
										veCollection.add(hmChequeDetails.get(vecKeys.get(i)));
								}
							}
							sort(veCollection);
							printPaymentSummary();
							break;
						case PRINT_PENDING_INVOICE:
							printPendingPayment();
							break;
						case PRINT_CUSTOMER_STATEMENT:
							printCustomerStatement();
							break;
						case PRINT_LOG_REPORT:
							trxMonthDetails = new TransactionsLogsDA().getCurrentMonthDetails("", "",isMTD);
							printLogReport();
							break;
						case PRINT_CASH_DENOMINATION:
							printCashDenomention(0);
							printCashDenomention(1);
							break;
						case PRINT_DAILY_SUMMARY:
//						printDailySummary();
							vecCustomerInvoiceDo = new PaymentSummeryDA().getPaymentSummaryDetail(fromDate);
							vecDailySalesData = new OrderDA().getTotalTrasactionDetail(fromDate);
							printDailySummaryWithCashDenom();
							break;
						case PRINT_STOCK_INVENTORY:
							printStockInventory();
							break;
						default:
							break;
					}
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							hideLoader();
						}
					});
				}
			}).start();

			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");
		}
		if (v.getId() == R.id.btn_msr23)
		{
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			byte[] track23 = { 0x1b, 0x4d, 0x45 };
			woosim.controlCommand(track23, track23.length);
			woosim.printSpool(true);
			showLoader("Please Swipe Your Card");
		}


		if (v.getId() == R.id.btn_msr123)
		{
			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			byte[] track123 = { 0x1b, 0x4d, 0x46 };
			woosim.controlCommand(track123, track123.length);
			woosim.printSpool(true);
		}

		if (v.getId() == R.id.btn_cardcancel)
		{

			editTrack1.setText("");
			editTrack2.setText("");
			editTrack3.setText("");

			woosim.cardCancel();

		}

		if (v.getId() == R.id.btn_finish)
		{
			woosim.BTDisConnection();
			hideLoader();
//			if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//			{
//				setResult(-10000);
//				finish();
//			}
//			else
//			{

//				if(isPrinted)
//					setResult(20000);
//				else
			setResult(Activity.RESULT_OK);
//			}
			finish();

			// ÃƒÂ¢Â´Ã�Â±Ã¢
		}

		if(v.getId() == R.id.btn_printimg1)
		{
			printSavedImage();
		}

		if(v.getId() == R.id.btn_printimg2)
		{
			printConvertedImage();
		}
		if(v.getId() == R.id.btnReprint)
		{
			btnPrint3.performClick();
		}
		if(v.getId() == R.id.btnFinishPrint)
		{
			btnFinish.performClick();
		}
	}

	public void sort(Vector<Customer_InvoiceDO> vec) {
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			@Override
			public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2) {
				return s2.receiptNo.compareToIgnoreCase(s1.receiptNo);
			}
		});
	}


	/**
	 * Method to print the Sales Order in 4 inch mode
	 */
	private void printSales(int isCustomerCopy)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-3.3s %3$-9.9s %4$-37.37s %5$-14.14s %6$6.6s %7$7.7s %8$7.7s\r\n";
		String formatReturn		= "%1$1.0s %2$-3.3s %3$-8.8s %4$-33.33s %5$-14.14s %6$4.4s %7$6.6s %8$7.7s %9$7.7s\r\n";
		String formater			= "%1$10.10s";
		String total 			= "%1$1.0s %2$-61.61s %3$4.4s %4$6.6s %5$7.7s %6$7.7s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String discountFormat	= "%1$1.0s %2$-80.80s %3$8.8s\r\n";
		String formatForSignature = "%1$1.1s %2$44.44s %3$44.44s\r\n";
		String formatRubbish	= "%1$1.1s %2$-89.89s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatInvoiceCopy 	= "%1$-34.34s %2$-23.23s  %3$-1.1s \n";
		formatInvoiceCopy 	= "%1$-34.34s %2$-31.31s  %3$-1.1s \n"; // "third party brand office/customer  copy"
		String label = "Order No";
		String trxCode 		="";
		String labelHeader 	= "";
		if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
		{
			if(trxHeaderDO.trxSubType==TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
				labelHeader ="Advance Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
				labelHeader ="Sales Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
				labelHeader ="FOC Order";
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			labelHeader ="Return Order";
			labelHeader ="TAX CREDIT NOTE";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
			labelHeader ="Advance Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || (trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() &&  AppConstants.customertype.equalsIgnoreCase("CASH")))
		{
//			  if((trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode)))
			if((trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode)) || (trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode) && AppConstants.customertype.equalsIgnoreCase("CASH")))
			{
				labelHeader ="TAX Invoice";
//				labelHeader ="Sales Invoice";
				label ="Inv No";
			}
			else if(trxHeaderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else
				labelHeader ="Sales Order";
		}
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER() && !TextUtils.isEmpty(trxHeaderDO.trxCode))
			{
				labelHeader ="Presales Order";
				label ="Inv No";
			}
		}

		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			labelHeader ="FOC Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			labelHeader ="Missed Order";
		if(!TextUtils.isEmpty(trxHeaderDO.trxCode))
			trxCode=trxHeaderDO.trxCode;
		else
			trxCode = "N/A";

		if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
		{
			if(printTypeIce ==200){
				printHeaderNewKwalityNew();
			}else{
				boolean isFromAbudabhii=mallsDetails!=null &&  mallsDetails.regionCode.toLowerCase().equalsIgnoreCase("abu dhabi")? true:false ;
				printHeaderNew(isFromAbudabhii);
			}
		}

		String division = "";
		if(trxHeaderDO.Division == 1)
			division = "Food";
		else if(trxHeaderDO.Division == 2)
            division = "Third Party Brand";
			else
			division = "Ice Cream";

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",(labelHeader+"").toUpperCase(),""), 0x11, true);
		if(isCustomerCopy==0)
		{
			woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy,"",(" Customer Copy").toUpperCase(),""), 0x8, true);
		}
		else
		{
			woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy,"",(" Office Copy").toUpperCase(),""), 0x8, true);
		}

		String mydate = CalendarUtils.getDateToShow((trxHeaderDO.trxDate));
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;


		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bill To:","Ship To:"),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,""+mallsDetails.Attribute3),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site," "+mallsDetails.city ),0, false);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site,"Cust Code: "+mallsDetails.site),0, false);
//        if (mallsDetails!=null && mallsDetails.addresss1 !=null && !TextUtils.isEmpty(mallsDetails.addresss1))
//        {
//            woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1),0, false);
//        }
        String trnNo="";
        woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN No: "+mallsDetails.VATNo, "Area:"+mallsDetails.regionCode),0, false);

        woosim.saveSpool(EUC_KR, String.format("%1$-21.21s" ,  " "+label +": "+trxCode )    ,1, false);
        woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n" , "     SM NO: "+preference.getStringFromPreference(Preference.USER_ID, "")  ),0,false);

        if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
        {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "CN Date: " + mydate, "SM Name: " + preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);
			String returnType = trxHeaderDO.arrTrxDetailsDOs.get(0).itemType;
			if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE()))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Return Type: Salable", "Reason for Return : "+trxHeaderDO.returnReason),0, false);

			else if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE()))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Return Type: Non Salable", "Reason for Return : "+trxHeaderDO.returnReason),0, false);
//			Org Tax Inv No. and Org Tax Inv date
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Org Tax Inv No.: ", "Org Tax Inv date: " ),0, false);
		}
        else {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "INV Date: " + mydate, "SM Name: " + preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);

			if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()) {
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "", "LPO No: " + trxHeaderDO.LPONo), 0, false);
			} else if (!TextUtils.isEmpty(AppConstants.customertype) && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && !mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type: " + AppConstants.customertype, "LPO No: " + trxHeaderDO.LPONo), 0, false);
			} else if (mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type:" + AppConstants.CUSTOMER_TYPE_CASH, "LPO No: " + trxHeaderDO.LPONo), 0, false);
			else
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type:" + AppConstants.CUSTOMER_TYPE_CREDIT, "LPO No: " + trxHeaderDO.LPONo), 0, false);
		}
		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","  BARCODE","QTY","RATE ","FOC AMT"),0, true);
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			woosim.saveSpool(EUC_KR, String.format(formatReturn,"","SR#","CODE","DESCRIPTION","  BARCODE","UOM","QTY","RATE ","AMOUNT"),0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","  BARCODE","QTY","RATE ","AMOUNT"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, false);

		String returnType = "";
		float totalPrice  = 0,quantity = 0,gsv = 0, disc = 0;
		HashMap<String,Float> hmVAT= new HashMap<>();
		for( int i = 0 ; i < trxHeaderDO.arrTrxDetailsDOs.size() ; i++ )
		{
			String discount = "";
			if((trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount) > 0)
				discount = ""+(StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount))+StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount)));

             if(hmVAT.containsKey(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"")){
				 Float tempVatOnEACH=hmVAT.get(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"");
				 tempVatOnEACH+=trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew;
				 hmVAT.remove(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage);
				 hmVAT.put(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"",tempVatOnEACH);
			 }else{
				 hmVAT.put(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"",trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew);
			 }

			quantity	+=	trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1;
			gsv			+=	StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1));
			disc		+=	StringUtils.getDouble(discount);
//	  		if(trxHeaderDO.trxType==TrxHeaderDO.get_TYPE_FREE_DELIVERY())
//	  			trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 = 0;
			totalPrice 	+= (trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1*trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1) - (trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount);

			if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			{
				returnType = trxHeaderDO.arrTrxDetailsDOs.get(i).itemType;

				woosim.saveSpool(EUC_KR, String.format(formatReturn,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,
						(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim(),
						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						trxHeaderDO.arrTrxDetailsDOs.get(i).UOM,
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)),0, false);
			}
			else
			{
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,
						(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim(),
						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)),0, false);
			}
		}
		String Currency="AED";
		if(mallsDetails.currencyCode!=null&&!mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency=""+mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

//		woosim.saveSpool(EUC_KR, String.format(total,"","Total ("+Currency+")",
		woosim.saveSpool(EUC_KR, String.format(total,"","Sub Total ("+Currency+")",
				"",deffAmt.format(quantity),"",
				deffAmt.format(gsv)),0, true);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount 100 %-",deffAmt.format(gsv)),0, true);

			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total FOC Value ("+Currency+")",deffAmt.format(0)),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(0)))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Reason of FOC:",trxHeaderDO.freeNote),0, true);
		}
		else
		{
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getFloat(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount) / 100)),0, true);
//
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			{
//				float customerDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
//				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount - customerDiscount)),0, true);
//			}
//
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value ("+Currency+")",deffAmt.format(trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount)),0, true);
//
//			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(totalPrice)))),0, true);

			float customerDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
			float customerstatementDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))));
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(customerDiscount)),0, true);//Abhijit
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))),0, true);//vinod
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.statementDiscount))+" %",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod

//            woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Stm Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.statementDiscount))+" %-",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			{
		/* commented by vinod
		if((trxHeaderDO.totalDiscountAmount - customerDiscount-customerstatementDiscount) >= 0.1)
//				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount - customerDiscount-customerstatementDiscount)),0, true);
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.specialDiscount)),0, true);
			else
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(0)+" %-",deffAmt.format(0)),0, true);
//			}*/
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %",deffAmt.format(trxHeaderDO.specialDiscount)),0, true); //added by vinod
//			}

//			for total amount, statement discount is not consdiered  as per client requirement(for printing only but in invoice we should consider stamt discount)
			double totaldiscount1 = trxHeaderDO.totalAmount-
					StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
//					-((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)
					- StringUtils.getFloat( trxHeaderDO.specialDiscount+""  );
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Taxable Value of Supply ("+Currency+")",deffAmt.format((totaldiscount1))),0, true);
			Set<String> keys=hmVAT.keySet();
			for (String key :keys){
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","VAT @("+key+")%",deffAmt.format((hmVAT.get(key)))),0, true);
			}
			double totaldiscount = trxHeaderDO.totalVATAmount+trxHeaderDO.totalAmount-
					StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
//					-((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)
					- StringUtils.getFloat( trxHeaderDO.specialDiscount+""  );
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value (Incl TAX) ("+Currency+")",deffAmt.format((totaldiscount))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(totaldiscount)))),0, true);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
		{
//			if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE()))
//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Return Type: Salable"),0, true);
//			else if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE()))
//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Return Type: Non Salable"),0, true);
//
//			woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Reason for Return of Stock: "+trxHeaderDO.returnReason),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Customer Complaint:_______________________________"),0, false);
		}
		else if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER()/* &&
				trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY()*/)
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Narration: "+trxHeaderDO.Narration,""),0, false);

			if(trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_ORDER())
			{ //need to change if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
				if(printTypeIce ==200){
					if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
						woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of TASTY BITE FOODSTUFF TRADING LLC."),0, false);
						else
					woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of Kwality International Foodstuff L.L.C."),0, false);
				}else{
					woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."),0, false);
				}

//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."),0, false);

				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* E & O E"),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Company will not be responsible for payment without an official receipt."),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Received complete invoice quantity in good condition."),0, false);
			}
		}

		if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
		{
//need to change
			if(printTypeIce ==200){
				if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
					woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For TASTY BITE FOODSTUFF TRADING LLC.\r\n"),0, false);
				else
				woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Kwality International Foodstuff LLC.\r\n"),0, false);
			}else{
				woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Pure Ice Cream Co. LLC.\r\n"),0, false);
			}

//			woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Pure Ice Cream Co. LLC.\r\n"),0, false);

			woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
		}

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	/**
	 * Method to print the Sales Order in 4 inch mode
	 */
	private void printSalesNew(int isCustomerCopy)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-3.3s %3$-9.9s %4$-14.14s %5$-4.4s %6$6.6s %7$7.7s %8$7.7s %9$7.7s %10$5.5s %11$6.6s %12$7.7s \r\n";
		String formatReturn		= "%1$1.0s %2$-3.3s %3$-9.9s %4$-14.14s %5$-4.4s %6$4.4s %7$6.6s %8$7.7s %9$5.5s %10$7.7s %11$5.5s %12$6.6s %13$7.7s \r\n";
//		String format 			= "%1$1.0s %2$-3.3s %3$-9.9s %4$-37.37s %5$-14.14s %6$6.6s %7$7.7s %8$7.7s\r\n";
//		String formatReturn		= "%1$1.0s %2$-3.3s %3$-8.8s %4$-33.33s %5$-14.14s %6$4.4s %7$6.6s %8$7.7s %9$7.7s\r\n";
		String formater			= "%1$10.10s";
		String formaterItemDesc			= "%1$1.0s %2$-3.3s %3$-50.50s\r\n";
		String total 			= "%1$1.0s %2$-21.21s %3$4.4s %4$6.6s %5$47.47s %6$7.7s\r\n";
//		String total 			= "%1$1.0s %2$-61.61s %3$4.4s %4$6.6s %5$7.7s %6$7.7s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String discountFormat	= "%1$1.0s %2$-80.80s %3$8.8s\r\n";
		String formatForSignature = "%1$1.1s %2$44.44s %3$44.44s\r\n";
		String formatRubbish	= "%1$1.1s %2$-89.89s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatInvoiceCopy 	= "%1$-34.34s %2$-23.23s  %3$-1.1s \n";
		formatInvoiceCopy 	= "%1$-34.34s %2$-31.31s  %3$-1.1s \n"; // "third party brand office/customer  copy"
		String label = "Order No";
		String trxCode 		="";
		String labelHeader 	= "";
		if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
		{
			if(trxHeaderDO.trxSubType==TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
				labelHeader ="Advance Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
				labelHeader ="Sales Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
				labelHeader ="FOC Order";
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			labelHeader ="Return Order";
			labelHeader ="TAX CREDIT NOTE";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
			labelHeader ="Advance Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || (trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() &&  AppConstants.customertype.equalsIgnoreCase("CASH")))
		{
//			  if((trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode)))
			if((trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode)) || (trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode) && AppConstants.customertype.equalsIgnoreCase("CASH")))
			{
				labelHeader ="TAX Invoice";
//				labelHeader ="Sales Invoice";
				label ="Inv No";
			}
			else if(trxHeaderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else
				labelHeader ="Sales Order";
		}
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER() && !TextUtils.isEmpty(trxHeaderDO.trxCode))
			{
				labelHeader ="Presales Order";
				label ="Inv No";
			}
		}

		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			labelHeader ="FOC Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			labelHeader ="Missed Order";
		if(!TextUtils.isEmpty(trxHeaderDO.trxCode))
			trxCode=trxHeaderDO.trxCode;
		else
			trxCode = "N/A";

		if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
		{
			if(printTypeIce ==200){
				printHeaderNewKwalityNew();
			}else{
				boolean isFromAbudabhii=mallsDetails!=null &&  mallsDetails.regionCode.toLowerCase().equalsIgnoreCase("abu dhabi")? true:false ;
				printHeaderNew(isFromAbudabhii);
			}
		}

		String division = "";
		if(trxHeaderDO.Division == 1)
			division = "Food";
		else if(trxHeaderDO.Division == 2)
            division = "Third Party Brand";
			else
			division = "Ice Cream";

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",(labelHeader+"").toUpperCase(),""), 0x11, true);
		if(isCustomerCopy==0)
		{
			woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy,"",(" Customer Copy").toUpperCase(),""), 0x8, true);
		}
		else
		{
			woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy,"",(" Office Copy").toUpperCase(),""), 0x8, true);
		}

		String mydate = CalendarUtils.getDateToShow((trxHeaderDO.trxDate));
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;


		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bill To:","Ship To:"),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,""+mallsDetails.Attribute3),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site,""+ mallsDetails.city),0, false);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site,"Cust Code: "+mallsDetails.site),0, false);
//        if (mallsDetails!=null && mallsDetails.addresss1 !=null && !TextUtils.isEmpty(mallsDetails.addresss1))
//        {
//            woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1),0, false);
//        }
        String trnNo="";
        woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN No: "+mallsDetails.VATNo, "Area:"+mallsDetails.regionCode),0, false);

        woosim.saveSpool(EUC_KR, String.format("%1$-21.21s" ,  " "+label +": "+trxCode )    ,1, false);
        woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n" , "     SM NO: "+preference.getStringFromPreference(Preference.USER_ID, "")  ),0,false);

        if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
        {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "CN Date: " + mydate, "SM Name: " + preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);
			String returnType = trxHeaderDO.arrTrxDetailsDOs.get(0).itemType;
			if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE()))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Return Type: Salable", "Reason for Return : "+trxHeaderDO.returnReason),0, false);

			else if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE()))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Return Type: Non Salable", "Reason for Return : "+trxHeaderDO.returnReason),0, false);
//			Org Tax Inv No. and Org Tax Inv date
				woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Org Tax Inv No.: "+(TextUtils.isEmpty(trxHeaderDO.oriSRINVNO)?"":((trxHeaderDO.oriSRINVNO!=null&&trxHeaderDO.oriSRINVNO.equalsIgnoreCase("null"))?"":trxHeaderDO.oriSRINVNO)), "Org Tax Inv date: "+(TextUtils.isEmpty(trxHeaderDO.oriSRINVDATE)?"":((trxHeaderDO.oriSRINVDATE!=null&&trxHeaderDO.oriSRINVDATE.equalsIgnoreCase("null"))?"":trxHeaderDO.oriSRINVDATE)) ),0, false);
		}
        else {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "INV Date: " + mydate, "SM Name: " + preference.getStringFromPreference(Preference.USER_NAME, "")), 0, false);

			if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()) {
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "", "LPO No: " + trxHeaderDO.LPONo), 0, false);
			} else if (!TextUtils.isEmpty(AppConstants.customertype) && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && !mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type: " + AppConstants.customertype, "LPO No: " + trxHeaderDO.LPONo), 0, false);
			} else if (mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type:" + AppConstants.CUSTOMER_TYPE_CASH, "LPO No: " + trxHeaderDO.LPONo), 0, false);
			else
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "Inv Type:" + AppConstants.CUSTOMER_TYPE_CREDIT, "LPO No: " + trxHeaderDO.LPONo), 0, false);
		}
		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE/DESC","  BARCODE","QTY","RATE ","Gross","Disc Amt","Net Amt","VAT %","VAT Amt","FOC AMT"),0, true);
//			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","  BARCODE","QTY","RATE ","FOC AMT"),0, true);
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			woosim.saveSpool(EUC_KR, String.format(formatReturn,"","SR#","CODE/DESC","  BARCODE","UOM","QTY","RATE ","Gross","Disc Amt","Net Amt","VAT %","VAT Amt","AMOUNT"),0, true);
//			woosim.saveSpool(EUC_KR, String.format(formatReturn,"","SR#","CODE","DESCRIPTION","  BARCODE","UOM","QTY","RATE ","AMOUNT"),0, true);
		else
//			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","  BARCODE","QTY","RATE ","AMOUNT"),0, true);
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE/DESC","  BARCODE","QTY","RATE ","Gross","Disc Amt","Net Amt","VAT %","VAT Amt","AMOUNT"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, false);

		String returnType = "";
		float totalPrice  = 0,quantity = 0,gsv = 0, disc = 0;
		HashMap<String,Float> hmVAT= new HashMap<>();
		for( int i = 0 ; i < trxHeaderDO.arrTrxDetailsDOs.size() ; i++ )
		{
			String discount = "";
			if((trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount) > 0)
				discount = ""+(StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount))+StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount)));

             if(hmVAT.containsKey(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"")){
				 Float tempVatOnEACH=hmVAT.get(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"");
				 tempVatOnEACH+=trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew;
				 hmVAT.remove(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage);
				 hmVAT.put(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"",tempVatOnEACH);
			 }else{
				 hmVAT.put(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage+"",trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew);
			 }

			quantity	+=	trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1;
			gsv			+=	StringUtils.getFloat(deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1));
			disc		+=	StringUtils.getDouble(discount);
//	  		if(trxHeaderDO.trxType==TrxHeaderDO.get_TYPE_FREE_DELIVERY())
//	  			trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 = 0;
			totalPrice 	+= (trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1*trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1) - (trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount);

			if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			{
				returnType = trxHeaderDO.arrTrxDetailsDOs.get(i).itemType;
				float dis  = ((trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountPercentage+trxHeaderDO.arrTrxDetailsDOs.get(i).totalDiscountPercentage)*trxHeaderDO.arrTrxDetailsDOs.get(i).EAPrice * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityBU/100);;
				woosim.saveSpool(EUC_KR, String.format(formatReturn,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,

						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						trxHeaderDO.arrTrxDetailsDOs.get(i).UOM,
						""+StringUtils.getInt(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1+""),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format((dis)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100))),
						""+deffAmt.format(((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1))
								-((dis)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100)))),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew),
						""+deffAmt.format(((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)
								-((dis)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100))))+trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew)
				),0, false);
			}
			else
			{
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,

						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						""+StringUtils.getInt(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1+""),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format((trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100))),
//						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).totalDiscountAmount),
						""+deffAmt.format(((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)
								-((trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100))))),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).vatPercentage),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew),
						""+deffAmt.format(((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)
								-((trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount)+((trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)*(trxHeaderDO.specialDiscPercent/100))))+trxHeaderDO.arrTrxDetailsDOs.get(i).VATAmountNew)
				),0, false);
			}
			woosim.saveSpool(EUC_KR, String.format(formaterItemDesc,"","",""+(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim()),0, false);
		}
		String Currency="AED";
		if(mallsDetails.currencyCode!=null&&!mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency=""+mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

//		woosim.saveSpool(EUC_KR, String.format(total,"","Total ("+Currency+")",
		woosim.saveSpool(EUC_KR, String.format(total,"","Sub Total ("+Currency+")",
				"",deffAmt.format(quantity),"",
				deffAmt.format(gsv)),0, true);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount 100 %-",deffAmt.format(gsv)),0, true);

			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total FOC Value ("+Currency+")",deffAmt.format(0)),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(0)))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Reason of FOC:",trxHeaderDO.freeNote),0, true);
		}
		else
		{
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getFloat(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount) / 100)),0, true);
//
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			{
//				float customerDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
//				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount - customerDiscount)),0, true);
//			}
//
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value ("+Currency+")",deffAmt.format(trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount)),0, true);
//
//			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(totalPrice)))),0, true);

			float customerDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
			float customerstatementDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))));
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(customerDiscount)),0, true);//Abhijit
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))),0, true);//vinod
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.statementDiscount))+" %",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod
  if(StringUtils.getDouble(trxHeaderDO.statementDiscount)>0)
            woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Stm Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.statementDiscount))+" %",deffAmt.format(((((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)))  )),0, true);//vinod
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			{
		/* commented by vinod
		if((trxHeaderDO.totalDiscountAmount - customerDiscount-customerstatementDiscount) >= 0.1)
//				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount - customerDiscount-customerstatementDiscount)),0, true);
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.specialDiscount)),0, true);
			else
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(0)+" %-",deffAmt.format(0)),0, true);
//			}*/
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Less Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %",deffAmt.format(trxHeaderDO.specialDiscount)),0, true); //added by vinod
//			}

//			for total amount, statement discount is not consdiered  as per client requirement(for printing only but in invoice we should consider stamt discount)
			double totaldiscount1 = trxHeaderDO.totalAmount-
					StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
					-(((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100))
					- StringUtils.getFloat( trxHeaderDO.specialDiscount+""  );
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Taxable Value of Supply ("+Currency+")",deffAmt.format((totaldiscount1))),0, true);
			Set<String> keys=hmVAT.keySet();
			for (String key :keys){
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","VAT @("+key+")%",deffAmt.format((hmVAT.get(key)))),0, true);
			}
			double totaldiscount = trxHeaderDO.totalVATAmount+trxHeaderDO.totalAmount-
					StringUtils.getDouble(StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*StringUtils.getDouble(trxHeaderDO.totalAmount+"")/100+"")
					-((StringUtils.getFloat(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount)/100)
					- StringUtils.getFloat( trxHeaderDO.specialDiscount+""  );
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value (Incl TAX) ("+Currency+")",deffAmt.format((totaldiscount))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(totaldiscount)))),0, true);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
		{
//			if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_SELLABLE()))
//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Return Type: Salable"),0, true);
//			else if(returnType.equalsIgnoreCase(TrxDetailsDO.get_TRX_ITEM_NON_SELLABLE()))
//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Return Type: Non Salable"),0, true);
//
//			woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Reason for Return of Stock: "+trxHeaderDO.returnReason),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","Customer Complaint:_______________________________"),0, false);
		}
		else if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER()/* &&
				trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY()*/)
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Narration: "+trxHeaderDO.Narration,""),0, false);

			if(trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_DELIVERY() && trxHeaderDO.trxType != TrxHeaderDO.get_TYPE_FREE_ORDER())
			{ //need to change if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
				if(printTypeIce ==200){
					if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
						woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of TASTY BITE FOODSTUFF TRADING LLC."),0, false);
						else
					woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of Kwality International Foodstuff L.L.C."),0, false);
				}else{
					woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."),0, false);
				}

//				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."),0, false);

				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* E & O E"),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Company will not be responsible for payment without an official receipt."),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Received complete invoice quantity in good condition."),0, false);
			}
		}

		if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
		{
//need to change
			if(printTypeIce ==200){
				if((trxHeaderDO !=null && trxHeaderDO.Division==2) || (objPaymentDO!=null  && objPaymentDO.Division ==2))
					woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For TASTY BITE FOODSTUFF TRADING LLC.\r\n"),0, false);
				else
				woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Kwality International Foodstuff LLC.\r\n"),0, false);
			}else{
				woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Pure Ice Cream Co. LLC.\r\n"),0, false);
			}

//			woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Pure Ice Cream Co. LLC.\r\n"),0, false);

			woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
		}

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	/**
	 * Method to print the Order Preview in 4 inch mode
	 */
	private void printSalesPreview()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-3.3s %3$-6.6s %4$-37.37s %5$-16.16s %6$6.6s %7$7.7s %8$7.7s\r\n";
		String formatReturn		= "%1$1.0s %2$-3.3s %3$-6.6s %4$-35.35s %5$-14.14s %6$4.4s %7$6.6s %8$7.7s %9$7.7s\r\n";
		String formater			= "%1$10.10s";
		String total 			= "%1$1.0s %2$-61.61s %3$4.4s %4$6.6s %5$7.7s %6$7.7s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String discountFormat	= "%1$1.0s %2$-80.80s %3$8.8s\r\n";
		String formatForSignature = "%1$1.1s %2$44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatInvoiceCopy = "%1$-34.34s %2$-20.20s  %3$-1.1s \n";
		String label = "Order No";
		String trxCode 		="";
		String labelHeader 	= "";
		if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
		{
			if(trxHeaderDO.trxSubType==TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
				labelHeader ="Advance Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
				labelHeader ="Sales Order";
			else if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
				labelHeader ="FOC Order";
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			labelHeader ="Return Order";
			labelHeader ="TAX CREDIT NOTE";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
			labelHeader ="Advance Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
		{
			if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !TextUtils.isEmpty(trxHeaderDO.trxCode))
			{
				labelHeader ="Invoice";
				label ="Inv No";
			}
			else if(trxHeaderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
				labelHeader ="Tele Order";
			else
				labelHeader ="Sales Order";
		}
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			labelHeader ="Presales Order";
			label ="Inv No";

		}
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
			labelHeader ="FOC Order";
		else if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_MISSED_ORDER())
			labelHeader ="Missed Order";
		if(!TextUtils.isEmpty(trxHeaderDO.trxCode))
			trxCode=trxHeaderDO.trxCode;
		else
			trxCode = "N/A";


		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",(labelHeader+"").toUpperCase(),""), 0x11, true);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			woosim.saveSpool(EUC_KR, String.format(formatReturn,"","SR#","CODE","DESCRIPTION","  BARCODE","UOM","QTY","RATE ","AMOUNT"),0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","  BARCODE","QTY","RATE ","AMOUNT"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, false);

		float totalPrice  = 0,quantity = 0,gsv = 0, disc = 0;
		for( int i = 0 ; i < trxHeaderDO.arrTrxDetailsDOs.size() ; i++ )
		{
			String discount = "";
			if((trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount) > 0)
				discount = ""+(trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount);

			quantity	+=	trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1;
			gsv			+=	trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1;
//	  		gsv			+=	trxHeaderDO.arrTrxDetailsDOs.get(i).CSPrice * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityBU;
			disc		+=	StringUtils.getDouble(discount);
			totalPrice 	+= (trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1*trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1) - (trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount+trxHeaderDO.arrTrxDetailsDOs.get(i).promotionalDiscountAmount);


			if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
			{
				woosim.saveSpool(EUC_KR, String.format(formatReturn,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,
						(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim(),
						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						trxHeaderDO.arrTrxDetailsDOs.get(i).UOM,
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)),0, false);
			}
			else
			{
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,
						(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim(),
						trxHeaderDO.arrTrxDetailsDOs.get(i).barCode,
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
						""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1 * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1)),0, false);
			}

//	  		woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),trxHeaderDO.arrTrxDetailsDOs.get(i).itemCode,
//	  				(trxHeaderDO.arrTrxDetailsDOs.get(i).itemDescription).trim(),
//	  				"",
//					""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).quantityLevel1),
//					""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).priceUsedLevel1),
//					""+deffAmt.format(trxHeaderDO.arrTrxDetailsDOs.get(i).CSPrice * trxHeaderDO.arrTrxDetailsDOs.get(i).quantityBU)),0, false);
		}
		String Currency="AED";
		if(mallsDetails.currencyCode!=null&&!mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency=""+mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(total,"","Total ("+Currency+")",
				"",deffAmt.format(quantity),"",
				deffAmt.format(gsv)),0, true);

		if(trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount 100 %-",deffAmt.format(gsv)),0, true);

			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total FOC Value ("+Currency+")",deffAmt.format(0)),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(0)))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Reason of FOC:",trxHeaderDO.freeNote),0, true);
		}
		else
		{
//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(disc - trxHeaderDO.specialDiscount)),0, true);

//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.specialDiscount)),0, true);

//			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value ("+Currency+")",deffAmt.format(totalPrice)),0, true);
//
//			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format(totalPrice)))),0, true);

			float customerDiscount = StringUtils.getFloat(deffAmt.format((((StringUtils.getFloat(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount)/100))));
			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Discount "+deffAmt.format(StringUtils.getDouble(trxHeaderDO.promotionalDiscount))+" %-",deffAmt.format(customerDiscount)),0, true);
//			if(trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
//			{
			if(trxHeaderDO.Division==0)
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount-trxHeaderDO.statementdiscountvalue - customerDiscount)),0, true);
			else
				woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Spl Discount "+deffAmt.format(trxHeaderDO.specialDiscPercent)+" %-",deffAmt.format(trxHeaderDO.totalDiscountAmount - customerDiscount)),0, true);
//			}

			woosim.saveSpool(EUC_KR, String.format(discountFormat,"","Total Invoice Value ("+Currency+")",deffAmt.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount+trxHeaderDO.statementdiscountvalue))),0, true);

			woosim.saveSpool(EUC_KR, String.format(price,"","Amount in Words:",new NumberUtils().convertNumtoLetter(StringUtils.getFloat(deffAmt.format((trxHeaderDO.totalAmount-trxHeaderDO.totalDiscountAmount+trxHeaderDO.statementdiscountvalue))))),0, true);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;

	}

	/**
	 * Method to print the Payment Summary in 4 inch mode
	 */
	private void printPaymentSummary()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-18.18s %4$-10.10s %5$-42.42s %6$11.11s\r\n";
		String format 			= "%1$1.0s %2$-4.4s %3$-12.12s %4$-10.10s %5$-36.36s %6$-12.12s %7$11.11s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-10.10s %2$-30.30s  %3$-1.1s \n";
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("PAYMENT RECEIVED SUMMARY").toUpperCase(),""), 0x11, true);

		String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
		String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Period: From "+stratDate+" to "+endDate),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","DOC NO","CODE","NAME","INV NO","AMOUNT"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);


		float totalPrice  = 0;
		int k=1;
		for( int i = 0 ; i < veCollection.size() ; i++ )
		{
			Customer_InvoiceDO customerInvoiceDO = veCollection.get(i);
			totalPrice += StringUtils.getFloat(""+customerInvoiceDO.invoiceTotal);

			if(customerInvoiceDO.vecPaymentDetailDOs.size()==1) {
				woosim.saveSpool(EUC_KR, String.format(format,"",""+k,""+customerInvoiceDO.receiptNo,""+customerInvoiceDO.customerSiteId,""+customerInvoiceDO.siteName,customerInvoiceDO.vecPaymentDetailDOs.get(0).invoiceNumber,""+deffAmt.format(StringUtils.getFloat(""+customerInvoiceDO.invoiceTotal))),0, false);
				k++;
			}
			else
			{
				for(PaymentDetailDO paymentdetailDo : customerInvoiceDO.vecPaymentDetailDOs)
				{
					woosim.saveSpool(EUC_KR, String.format(format,"",""+k,""+customerInvoiceDO.receiptNo,""+customerInvoiceDO.customerSiteId,""+customerInvoiceDO.siteName,paymentdetailDo.invoiceNumber,""+deffAmt.format(StringUtils.getFloat(""+paymentdetailDo.invoiceAmount))),0, false);
					k++;
				}
			}

//			woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),""+customerInvoiceDO.receiptNo,""+customerInvoiceDO.customerSiteId,""+customerInvoiceDO.siteName,customerInvoiceDO.vecPaymentDetailDOs.get(0).invoiceNumber,""+deffAmt.format(StringUtils.getFloat(""+customerInvoiceDO.invoiceTotal))),0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(price,"","Total",deffAmt.format(totalPrice)),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	/**
	 * Method to print the Stock Summary in 4 inch mode
	 */

	private void printStockInventory()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-10.10s %4$-60.60s %5$11.11s\r\n";
		String format 			= "%1$1.0s %2$-3.3s %3$-8.8s %4$-40.40s %5$5.5s %6$8.8s %7$9.9s %8$10.10s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
		printHeader();
		/*try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Stock Inventory Summary").toUpperCase(),""), 0x11, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
//			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","QTY/PCS"),0, true);
		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","OPEN","LOADED","DELIVER","AVAIL"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		int totalStockLoad  = 0,totalOrderDeliver  = 0,totalStockAvailable  = 0, totalopeningqty=0;

		if(vecInventoryItems != null)
		{
			for( int i = 0 ; i < vecInventoryItems.size() ; i++ )
			{
				InventoryObject inventoryObject =vecInventoryItems.get(i);
				int orderQty = (int)(getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.deliveredCases,inventoryObject.uomFactor) : 0);
				int availQty = (int)(getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) >= 0 ? getUnitfromUOM(inventoryObject.availQty,inventoryObject.uomFactor) : 0);
				int vanQty = availQty + orderQty;
				int loaded=vanQty-(StringUtils.getInt(inventoryObject.openingQTY));

				totalOrderDeliver += orderQty;
				totalStockAvailable += availQty;
				totalStockLoad += loaded;
				totalopeningqty += StringUtils.getInt(""+inventoryObject.openingQTY);

//			if(i<4)//remove while giving build
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),""+inventoryObject.itemCode,""+(inventoryObject.itemDescription).trim(),""+inventoryObject.openingQTY,""+loaded,""+orderQty,""+availQty),0, false);
			}
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Opening Stock:",deffAmt.format(totalopeningqty)),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Stock Load:",deffAmt.format(totalStockLoad)),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Order Delivered:",deffAmt.format(totalOrderDeliver)),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total Stock Available:",deffAmt.format(totalStockAvailable)),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}



	private void printInventory()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-10.10s %4$-60.60s %5$11.11s\r\n";
		String format 			= "%1$1.0s %2$-6.6s %3$-9.9s %4$-56.56s %5$6.6s %6$6.6s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
		printHeader();

//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "          ", 1, false);
//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Van Stock Summary Report").toUpperCase(),""), 0x11, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
//		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","REC","UNIT"),0, true);
		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","REC",""),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		int totalPrice  = 0;
		for( int i = 0 ; i < vecOrdProduct.size() ; i++ )
		{
			VanLoadDO vanLoadDO =vecOrdProduct.get(i);
//				totalPrice += StringUtils.getFloat(""+vanLoadDO.SellableQuantity);
			String strLoad = "0";
			String strUnit = "0";
			String strPcs = "0";//deffAmt.format(StringUtils.getFloat(""+vanLoadDO.SellableQuantity))
			if(StringUtils.getInt(vanLoadDO.RecomendedLoadQuantity)>0)
				strLoad = ""+StringUtils.getInt(vanLoadDO.RecomendedLoadQuantity);
			if(vanLoadDO.SellableQuantity > 0)
			{
				if(vanLoadDO.eaConversion>0)
				{
					totalPrice += (int)vanLoadDO.SellableQuantity / (int)vanLoadDO.eaConversion;
					strUnit = ""+((int)vanLoadDO.SellableQuantity / (int)vanLoadDO.eaConversion);
					strPcs = ""+((int)vanLoadDO.SellableQuantity % (int)vanLoadDO.eaConversion);
				}
				else
					strPcs = ""+(int)vanLoadDO.SellableQuantity;
			}
//			woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),""+vanLoadDO.ItemCode,""+(vanLoadDO.Description).trim(),""+strLoad,""+strUnit),0, false);
			woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),""+vanLoadDO.ItemCode,""+(vanLoadDO.Description).trim(),""+strLoad,""),0, false);

		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total",deffAmt.format(totalPrice)),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	private void printInventoryDetails()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-10.10s %4$-60.60s %5$11.11s\r\n";
		String format 			= "%1$1.0s %2$-6.6s %3$-9.9s %4$-50.50s%5$6.6s %6$6.6 %7$6.6s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
		printHeader();

//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "          ", 1, false);
//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Van Stock Details").toUpperCase(),""), 0x11, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
//			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","QTY/PCS"),0, true);
		if(MOVEMENT_STATUS == LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY)
		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","REQ.","APP.","COL."),0, true);
		else
			woosim.saveSpool(EUC_KR, String.format(format,"","SR#","CODE","DESCRIPTION","","","REC"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		int totalPrice  = 0;
		for( int i = 0 ; i < vecOrdProduct.size() ; i++ )
		{
			VanLoadDO vanLoadDO =vecOrdProduct.get(i);
//				totalPrice += StringUtils.getFloat(""+vanLoadDO.SellableQuantity);
			String strLoad = "0";
			String strUnit = "0";
			String strPcs = "0";//deffAmt.format(StringUtils.getFloat(""+vanLoadDO.SellableQuantity))
			if(StringUtils.getInt(vanLoadDO.RecomendedLoadQuantity)>0)
				strLoad = ""+StringUtils.getInt(vanLoadDO.RecomendedLoadQuantity);
			if(vanLoadDO.SellableQuantity > 0)
			{
				if(vanLoadDO.eaConversion>0)
				{
					totalPrice += (int)vanLoadDO.SellableQuantity / (int)vanLoadDO.eaConversion;
					strUnit = ""+((int)vanLoadDO.SellableQuantity / (int)vanLoadDO.eaConversion);
					strPcs = ""+((int)vanLoadDO.SellableQuantity % (int)vanLoadDO.eaConversion);
				}
				else
					strPcs = ""+(int)vanLoadDO.SellableQuantity;
			}

			String approved ="0";
			if(vanLoadDO.inProcessQuantityLevel1 == 0 && vanLoadDO.shippedQuantityLevel1 > 0)
				 approved  = String.valueOf(vanLoadDO.inProcessQuantityLevel1 + vanLoadDO.shippedQuantityLevel1);
			else
				 approved = (String.valueOf(vanLoadDO.inProcessQuantityLevel1));
			String shippedQuantity= String.valueOf(vanLoadDO.shippedQuantityLevel1);

			if(MOVEMENT_STATUS == LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY)
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),""+vanLoadDO.ItemCode,""+(vanLoadDO.Description).trim(),""+vanLoadDO.quantityLevel1, approved,shippedQuantity),0, true);
			else
				woosim.saveSpool(EUC_KR,  String.format(format,"",""+(i+1),""+vanLoadDO.ItemCode,""+(vanLoadDO.Description).trim(),"","",""+vanLoadDO.quantityLevel1),0, false);

		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total",deffAmt.format(totalPrice)),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}


	/**
	 * Method to print the Pending Payment in 4 inch mode
	 */
	private void printPendingPayment()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-4.4s %3$-18.18s %4$-18.18s %5$-32.32s %6$11.11s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
//		printHeader();
		boolean isFromAbudabhii=mallsDetails!=null &&  mallsDetails.regionCode.toLowerCase().equalsIgnoreCase("abu dhabi")? true:false ;
		printHeaderNew(isFromAbudabhii);
		//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "          ", 1, false);
//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Pending Payment List").toUpperCase(),""), 0x11, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;

		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,"Cust No: "+mallsDetails.site),0, false);
		//vinod
		if (mallsDetails!=null && mallsDetails.addresss1 !=null && !TextUtils.isEmpty(mallsDetails.addresss1))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,""),0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","S Name: "+preference.getStringFromPreference(Preference.USER_NAME, ""),""),0, false);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receipt No: "+strReceiptNo,"Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM),0, false);
 //vinod
		woosim.saveSpool(EUC_KR, String.format("%1$-21.21s" ,  " "+"Rec. No:" +strReceiptNo )    ,1, false);
//		woosim.saveSpool(EUC_KR,  " "+"ReceiptNo:" +strReceiptNo  ,1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n" , "     Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM  ),0,false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","INVOICE NO","INVOICE DATE","REMARK","AMOUNT"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines), 0, false);


		float totalPrice  = 0;
		for( int i = 0 ; i < arrInvoiceNumbers.size() ; i++ )
		{
			PendingInvicesDO pendingInvicesDO=arrInvoiceNumbers.get(i);
			totalPrice += StringUtils.getFloat(pendingInvicesDO.balance);
			String invoiceDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, pendingInvicesDO.invoiceDate);

			woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),pendingInvicesDO.invoiceNo, invoiceDate,"",""+deffAmt.format(StringUtils.getFloat(pendingInvicesDO.balance))),0, false);
		}

		String Currency="AED";
		if(mallsDetails.currencyCode!=null&&!mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency=""+mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(price,"","Total",deffAmt.format(totalPrice)+" "+Currency),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	/**
	 * Method to print the Customer Statement in 4 inch mode
	 */
	private void printCustomerStatement()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-4.4s %3$-21.21s %4$-17.17s %5$17.17s %6$12.12s %7$12.12s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$43.43s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";

//		printHeader();
		boolean isFromAbudabhii=mallsDetails!=null &&  mallsDetails.regionCode.toLowerCase().equalsIgnoreCase("abu dhabi")? true:false ;
		printHeaderNew(isFromAbudabhii);
		/*try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Customer Statement").toUpperCase(),""), 0x11, true);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;

		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,"Cust No: "+mallsDetails.site),0, false);
		//vinod
		if (mallsDetails!=null && mallsDetails.addresss1 !=null && !TextUtils.isEmpty(mallsDetails.addresss1))
		{
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,""),0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, ""),"SM NO: "+preference.getStringFromPreference(Preference.USER_ID, "")),0, false);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receipt No: "+strReceiptNo,"Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM),0, false);
//vinod
//		woosim.saveSpool(EUC_KR,  " "+"ReceiptNo:" +strReceiptNo  ,1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-21.21s" ,  " "+"Receipt No:" +strReceiptNo )    ,1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n" , "     Date: "+mydate.substring(0, mydate.lastIndexOf(":")) +AM_PM  ),0,false);


		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","SR#","TRX TYPE","TRX NO","TRX DATE","DEBIT","CREDIT"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);


		float totalPrice  = 0;
		for( int i = 0 ; i < vecDetails.size() ; i++ )
		{
			CustomerStatmentDO customerStatmentDO = vecDetails.get(i);

			String invoiceDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT,customerStatmentDO.trxDate);
			if(customerStatmentDO.invoiceType.equalsIgnoreCase("Payment")
					|| customerStatmentDO.invoiceType.equalsIgnoreCase("GRV"))
			{
				totalPrice -= StringUtils.getFloat(customerStatmentDO.amount);
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),customerStatmentDO.invoiceType, customerStatmentDO.trxNumber,invoiceDate,""+deffAmt.format(0),""+deffAmt.format(StringUtils.getFloat(customerStatmentDO.amount))),0, false);
			}
			else
			{
				totalPrice +=StringUtils.getFloat(customerStatmentDO.amount);
				woosim.saveSpool(EUC_KR, String.format(format,"",""+(i+1),customerStatmentDO.invoiceType, customerStatmentDO.trxNumber,invoiceDate,""+deffAmt.format(StringUtils.getFloat(customerStatmentDO.amount)),""+deffAmt.format(0)),0, false);
			}

		}

		String Currency="AED";
		if(mallsDetails.currencyCode!=null&&!mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency=""+mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(price,"","Balance ("+Currency+")",deffAmt.format(totalPrice)),0, false);
		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}
	private void printCashDenomention(int divisor)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatHeader     = "%1$1.0s %2$-22.22s %3$-4.4s %4$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-39.39s %3$-4.4s %4$-20.20s %5$20.20s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
		printHeader();
		/*
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
*/


		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Cash Denomination").toUpperCase(),""), 0x11, true);

		String mydate =objCashDenom.CollectionDate;//java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//			String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;




		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Date",":",""+mydate),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Salesman",":",""+objCashDenom.UserCode),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Helper",":",""+objCashDenom.HelperUserCode),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","Route No.",":",""+objCashDenom.RouteNo),0, false);

		String lines = "=======================================================================================================";
		String linesSingle = "-------------------------------------------------------------------------------------------------------------";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Curreny","","Notes","Amount"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		double totalPrice  = 0;
		Denominations objDenom;
		if(divisor > 0)
			objDenom = objCashDenom.objFoodDenom;
		else
			objDenom = objCashDenom.objIceCreamDenom;

		woosim.saveSpool(EUC_KR, String.format(format,"","1000","*",""+objDenom.Units1000,""+deffAmt.format(objDenom.Units1000*1000)),0, true);
		totalPrice +=objDenom.Units1000*1000;
		woosim.saveSpool(EUC_KR, String.format(format,"","500","*",""+objDenom.Units500,""+deffAmt.format(objDenom.Units500*500)),0, true);
		totalPrice +=objDenom.Units500*500;
		woosim.saveSpool(EUC_KR, String.format(format,"","200","*",""+objDenom.Units200,""+deffAmt.format(objDenom.Units200*200)),0, true);
		totalPrice +=objDenom.Units200*200;
		woosim.saveSpool(EUC_KR, String.format(format,"","100","*",""+objDenom.Units100,""+deffAmt.format(objDenom.Units100*100)),0, true);
		totalPrice +=objDenom.Units100*100;
		woosim.saveSpool(EUC_KR, String.format(format,"","50","*",""+objDenom.Units50,""+deffAmt.format(objDenom.Units50*50)),0, true);
		totalPrice +=objDenom.Units50*50;
		woosim.saveSpool(EUC_KR, String.format(format,"","20","*",""+objDenom.Units20,""+deffAmt.format(objDenom.Units20*20)),0, true);
		totalPrice +=objDenom.Units20*20;
		woosim.saveSpool(EUC_KR, String.format(format,"","10","*",""+objDenom.Units10,""+deffAmt.format(objDenom.Units10*10)),0, true);
		totalPrice +=objDenom.Units10*10;
		woosim.saveSpool(EUC_KR, String.format(format,"","5","*",""+objDenom.Units5,""+deffAmt.format(objDenom.Units5*5)),0, true);
		totalPrice +=objDenom.Units5*5;
		woosim.saveSpool(EUC_KR, String.format(format,"","coins","",""+objDenom.UnitsCoin,""+deffAmt.format(objDenom.UnitsCoin)),0, true);
		totalPrice +=objDenom.UnitsCoin;
		woosim.saveSpool(EUC_KR, String.format(format,"","other currencies","",""+deffAmt.format(objDenom.OtherCurrencyNote),""+deffAmt.format(objDenom.OtherCurrency)),0, true);
		totalPrice +=objDenom.OtherCurrency;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		woosim.saveSpool(EUC_KR, String.format(format,"","Total Cash","*","",""+deffAmt.format(totalPrice)),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Vouchers","",""+objDenom.VoucherNo,""+deffAmt.format(objDenom.Voucher)),0, true);
		totalPrice +=objDenom.Voucher;
		woosim.saveSpool(EUC_KR, String.format(format,"","Cash paid Cr.Note","",""+objDenom.CashPaidCrNote,""+deffAmt.format(objDenom.CashPaidCredit)),0, true);
		totalPrice +=objDenom.CashPaidCredit;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		woosim.saveSpool(EUC_KR, String.format(format,"","Grand Total","*","",""+deffAmt.format(totalPrice)),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Balance Return","","",""),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Cashier Signature","Salesman Signature\r\n"),0, false);

		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	private void printCashDenominationForDailySummary(final int divisor)
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);

		String formatHeader     = "%1$1.0s %2$-22.22s %3$-4.4s %4$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String format 			= "%1$1.0s %2$-39.39s %3$-4.4s %4$-20.20s %5$20.20s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
		String formatForAddress 	= "%1$1.0s %2$-44.44s %3$-44.44s\r\n";

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Cash Denomination").toUpperCase(),""), 0x11, true);
		objCashDenom = objDailySumDO.objCashDenom;
		String Date= CalendarUtils.getCurrentDateAsStringInFormatPrint();
		String Time= CalendarUtils.getCurrentTime();
		String mydate =objCashDenom.CollectionDate;//java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Date: "+mydate,"Salesman: "+objCashDenom.UserCode),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Helper: "+objCashDenom.HelperUserCode,"Route No.: "+objCashDenom.RouteNo),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Print Date: "+Date,"Print Time:"+Time),0, false);

		String lines = "=======================================================================================================";
		String linesSingle = "-------------------------------------------------------------------------------------------------------------";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Currency","","Notes","Amount"),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		double totalPrice  = 0;
		Denominations objDenom;
		if(divisor > 0)
			objDenom = objCashDenom.objFoodDenom;
		else
			objDenom = objCashDenom.objIceCreamDenom;

		woosim.saveSpool(EUC_KR, String.format(format,"","1000","*",""+objDenom.Units1000,""+deffAmt.format(objDenom.Units1000*1000)),0, true);
		totalPrice +=objDenom.Units1000*1000;
		woosim.saveSpool(EUC_KR, String.format(format,"","500","*",""+objDenom.Units500,""+deffAmt.format(objDenom.Units500*500)),0, true);
		totalPrice +=objDenom.Units500*500;
		woosim.saveSpool(EUC_KR, String.format(format,"","200","*",""+objDenom.Units200,""+deffAmt.format(objDenom.Units200*200)),0, true);
		totalPrice +=objDenom.Units200*200;
		woosim.saveSpool(EUC_KR, String.format(format,"","100","*",""+objDenom.Units100,""+deffAmt.format(objDenom.Units100*100)),0, true);
		totalPrice +=objDenom.Units100*100;
		woosim.saveSpool(EUC_KR, String.format(format,"","50","*",""+objDenom.Units50,""+deffAmt.format(objDenom.Units50*50)),0, true);
		totalPrice +=objDenom.Units50*50;
		woosim.saveSpool(EUC_KR, String.format(format,"","20","*",""+objDenom.Units20,""+deffAmt.format(objDenom.Units20*20)),0, true);
		totalPrice +=objDenom.Units20*20;
		woosim.saveSpool(EUC_KR, String.format(format,"","10","*",""+objDenom.Units10,""+deffAmt.format(objDenom.Units10*10)),0, true);
		totalPrice +=objDenom.Units10*10;
		woosim.saveSpool(EUC_KR, String.format(format,"","5","*",""+objDenom.Units5,""+deffAmt.format(objDenom.Units5*5)),0, true);
		totalPrice +=objDenom.Units5*5;
		woosim.saveSpool(EUC_KR, String.format(format,"","coins","",""+objDenom.UnitsCoin,""+deffAmt.format(objDenom.UnitsCoin)),0, true);
		totalPrice +=objDenom.UnitsCoin;
		woosim.saveSpool(EUC_KR, String.format(format,"","other currencies","",""+deffAmt.format(objDenom.OtherCurrencyNote),""+deffAmt.format(objDenom.OtherCurrency)),0, true);
		totalPrice +=objDenom.OtherCurrency;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		woosim.saveSpool(EUC_KR, String.format(format,"","Total Cash","*","",""+deffAmt.format(totalPrice)),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Vouchers","",""+objDenom.VoucherNo,""+deffAmt.format(objDenom.Voucher)),0, true);
		totalPrice +=objDenom.Voucher;
		woosim.saveSpool(EUC_KR, String.format(format,"","Cash paid Cr.Note","",""+objDenom.CashPaidCrNote,""+deffAmt.format(objDenom.CashPaidCredit)),0, true);
		totalPrice +=objDenom.CashPaidCredit;

		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		woosim.saveSpool(EUC_KR, String.format(format,"","Grand Total","*","",""+deffAmt.format(totalPrice)),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);

		woosim.saveSpool(EUC_KR, String.format(format,"","Balance Return","","",""),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Cashier Signature","Salesman Signature\r\n"),0, false);

		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);



		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	private void printDailySummary()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
		String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
		String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-34.34s %6$-12.12s %7$10.10s\n";
		String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
		String formatFooter  		= "%1$1.1s %2$-22.22s %3$1.1s %4$15.15s        %5$-22.22s %6$1.1s %7$15.15s\r\n";

		try
		{
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			e.printStackTrace();
		}

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Daily Summary").toUpperCase(),""), 0x11, true);

		String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+stratDate),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","DOCUMENT NO","CODE","CUST. NAME","TRX TYPE","AMOUNT"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		DailySummaryDO objDailySummary = null;
		float cash = 0, cheque = 0,sales = 0,grv = 0,foc = 0;
		int zeroSales = 0,unVisited = 0;
		for(int  i =  0 ; i < arrDailySummary.size()  ; i++)
		{
			objDailySummary = arrDailySummary.get(i);
			if(objDailySummary != null)
			{
				String strDocNo = "";
				if(objDailySummary.DocNo!=null && objDailySummary.DocNo.equalsIgnoreCase(""))
					strDocNo = "N/A";
				else
					strDocNo = ""+objDailySummary.DocNo;
				String strAmount = "";
				if((""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount))).equalsIgnoreCase(""))
					strAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount));
				else
					strAmount = "0.00";

				woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+(i+1),strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName,""+objDailySummary.type,strAmount),0, false);

				if(objDailySummary.Priority.equalsIgnoreCase("1"))//SALES
					sales += StringUtils.getFloat(objDailySummary.amount);
				else if(objDailySummary.Priority.equalsIgnoreCase("2"))//GRV
					grv += StringUtils.getFloat(objDailySummary.amount);
				else if(objDailySummary.Priority.equalsIgnoreCase("3"))//FOC
					foc += StringUtils.getFloat(objDailySummary.amount);
				else if(objDailySummary.Priority.equalsIgnoreCase("4"))//CASH
					cash += StringUtils.getFloat(objDailySummary.amount);
				else if(objDailySummary.Priority.equalsIgnoreCase("5"))//CHEQUE
					cheque += StringUtils.getFloat(objDailySummary.amount);
				else if(objDailySummary.Priority.equalsIgnoreCase("6"))//ZERO SALES
					zeroSales += 1;
				else if(objDailySummary.Priority.equalsIgnoreCase("7"))//UNVISITED
					unVisited += 1;
			}
		}

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL CASH",":","AED "+deffAmt.format(StringUtils.getFloat(""+cash)),"TOTAL SALES",":","AED "+deffAmt.format(StringUtils.getFloat(""+sales))),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL CHEQUE",":","AED "+deffAmt.format(StringUtils.getFloat(""+cheque)),"TOTAL GRV",":","AED "+deffAmt.format(StringUtils.getFloat(""+grv))),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL ZERO SALES",":",""+zeroSales,"TOTAL UNVISITED",":",""+unVisited),0, false);


		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	private void printDailySummaryWithCashDenom()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress 	= "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String formatForDate		= "%1$1.0s %2$-44.44s %3$-20.20s %4$-22.22s\r\n";
		String LINE 				= "%1$1.1s %2$-89.89s \r\n";
		String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
//		String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-34.34s %6$-12.12s %7$10.10s\n";
//		String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-11.11s %7$3.3s %8$10.10s\n";
		String formatItem		 	= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
		String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-9.9s %4$-7.7s %5$-30.30s %6$-9.9s %7$5.5s %8$10.10s %9$8.8s \n";
		String formatItemreason	 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-26.26s\n";
		String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
		String formatFooter  		= "%1$1.1s %2$-22.22s %3$1.1s %4$15.15s %5$-22.22s %6$1.1s %7$15.15s\r\n";

		String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
		printHeader();
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Daily Summary").toUpperCase(),""), 0x11, true);
		String Date= CalendarUtils.getCurrentDateAsStringInFormatPrint();
		String Time= CalendarUtils.getCurrentTime();
		String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
		woosim.saveSpool(EUC_KR, String.format(formatForDate,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+stratDate,"Day: "+objDailySumDO.dayOfWeek),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Vehicle No.: "+objDailySumDO.vehicleNo,"Helper Name: "+objDailySumDO.helperName),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Print Date: "+Date,"Print Time:"+Time),0, false);

		String lines = "=======================================================================================================";
		String linesSingle = "-------------------------------------------------------------------------------------------------------------";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","DOCUMENT NO","CODE","CUST. NAME","TRX TYPE","QTY","GRSAMNT","NETAMNT"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		DailySummaryDO objDailySummary = null;
		float cash = 0, cheque = 0,sales = 0,grv = 0,foc = 0;
		int zeroSales = 0,unVisited = 0,qty=0;

		arrDailySummary = objDailySumDO.arrDailySummary;
		LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = objDailySumDO.hashDailySummary;
		for(int j = 1 ; j <= 7 ; j++)
		{
			arrDailySummary = hashDailySummary.get(""+j);
			if(arrDailySummary != null)
			{
				for(int  i =  0 ; i < arrDailySummary.size()  ; i++)
				{
					objDailySummary = arrDailySummary.get(i);
					if(objDailySummary != null)
					{
						String strDocNo = "";
						if(objDailySummary.DocNo!=null && objDailySummary.DocNo.equalsIgnoreCase(""))
							strDocNo = "N/A";
						else
							strDocNo = ""+objDailySummary.DocNo;
						String strAmount = "";
						String GrsstrAmount = "";
						if((""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount))).equalsIgnoreCase(""))
							strAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount));
						else
							strAmount = "0.00";
						if((objDailySummary.type!=null&&objDailySummary.type.equals("Sales"))||(objDailySummary.type!=null&&objDailySummary.type.equalsIgnoreCase("FOC")))
							qty+=StringUtils.getInt(objDailySummary.Quantity);

						GrsstrAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.TotalAmnt));

						String type = "";
						if(objDailySummary.type != null)
							type += objDailySummary.type;
						if(StringUtils.getInt(objDailySummary.Priority) > 5)
							woosim.saveSpool(EUC_KR, String.format(formatItemreason,"",""+(i+1),strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type),0, false);
						else
						{
							if(j==4 || j==5)
								woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+(i+1),strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type,""+objDailySummary.Quantity,"",strAmount),0, false);
							else
								woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+(i+1),strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type,""+objDailySummary.Quantity,GrsstrAmount,strAmount),0, false);

						}

						//	arrDailySummary.get(i).Divider = "0";
					}
				}
			}
			if(j == StringUtils.getInt("1"))//SALES
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//	woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL SALES",":",""+deffAmt.format(objDailySumDO.total[0])),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL SALES","",""+qty,""+deffAmt.format(objDailySumDO.grstotal[0]),""+deffAmt.format(objDailySumDO.total[0])),0, true);
				qty=0;
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}
			else if(j == StringUtils.getInt("2"))//GRV
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL GRV",":",""+deffAmt.format(objDailySumDO.total[1])),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL GRV","","",""+deffAmt.format(objDailySumDO.grstotal[1]),""+deffAmt.format(objDailySumDO.total[1])),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}
			else if(j == StringUtils.getInt("3"))//FOC
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL FOC","",""+qty,""+deffAmt.format(objDailySumDO.grstotal[2]),""+deffAmt.format(objDailySumDO.total[2])),0, true);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL FOC",":",""+deffAmt.format(objDailySumDO.total[2])),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				qty=0;
			}
			else if(j == StringUtils.getInt("4"))//CASH
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CASH",":",""+deffAmt.format(objDailySumDO.total[3])),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CASH","","",""/*+deffAmt.format(objDailySumDO.grstotal[3])*/,""+deffAmt.format(objDailySumDO.total[3])),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}
			else if(j == StringUtils.getInt("5"))//CHEQUE
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CHEQUE",":",""+deffAmt.format(objDailySumDO.total[4])),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CHEQUE","","",""/*deffAmt.format(objDailySumDO.grstotal[4])*/,""+deffAmt.format(objDailySumDO.total[4])),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}
			else if(j == StringUtils.getInt("6"))//ZERO SALES
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL ZERO SALES",":",""+deffAmt.format(0)),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL ZERO SALES","","",""+deffAmt.format(0),""+deffAmt.format(0)),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}
			else if(j == StringUtils.getInt("7"))//UNVISITED
			{
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL UNVISITED",":",""+deffAmt.format(0)),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","Unvisited as per JP","","",""+deffAmt.format(0),""+deffAmt.format(0)),0, true);
				woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
			}

			//objDailySumDO.arrDailySummary.get(j).Divider = "0";
		}

		printDailySales_Pay_Summary();

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","SALES ICE CREAM",":","AED "+deffAmt.format(objDailySumDO.total[8]),"SALES FOOD",":","AED "+deffAmt.format(objDailySumDO.total[9])),0, true);
		if(objDailySumDO.targetAcheivement != null && objDailySumDO.targetAcheivement.length > 6) {

			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TARGET ICECREAM",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[0])),"NET ACHIEVED ICECREAM",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[1]))),0, true);
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TARGET FOOD",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[5])),"NET ACHIEVED FOOD",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[6]))),0, true);
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TARGET TPB ",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[13])),"NET ACHIEVED TPB ",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.targetAcheivement[11]))),0, true);
		}

		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL CASH",":","AED "+deffAmt.format(objDailySumDO.total[3]),"TOTAL SALES",":","AED "+deffAmt.format(objDailySumDO.total[0])),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL CHEQUE",":","AED "+deffAmt.format(objDailySumDO.total[4]),"TOTAL GRV",":","AED "+deffAmt.format(objDailySumDO.total[1])),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL ZERO SALES",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.total[5])),"UNVISITED AS PER JP",":",""+StringUtils.getInt(deffAmt.format(objDailySumDO.total[6]))),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TOTAL VISIT",":",""+objDailySumDO.totalVisit,"TOT QTY (SALES + FOC)",":",""+(int)objDailySumDO.total[7]),0, true);

		if(!(objDailySumDO.startTime.isEmpty() && objDailySumDO.endTime.isEmpty()))
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","START TIME",":",""+CalendarUtils.getTimeToShow(objDailySumDO.startTime),"END TIME",":",""+CalendarUtils.getTimeToShow(objDailySumDO.endTime)),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","START KM",":",""+objDailySumDO.startKM,"END KM",":",""+objDailySumDO.endKM),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter,"","Sch. Call as per JP",":",""+objDailySumDO.totalScheduledCall,"Act. Call as per JP",":",""+objDailySumDO.totalActualCall),0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);

		woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","ASM Signature","Salesman Signature\r\n"),0, false);

		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","|                                          | |                                         |"),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE,"","-------------------------------------------- -------------------------------------------"),0, false);

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;

		if(dailySummary == DailySummaryDO.get_DAILY_SUMMARY_FOOD() /*|| dailySummary == DailySummaryDO.get_DAILY_THIRD_PARTY_BRAND()*/ ){
			DailySummaryIcecream();
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
			printCashDenominationForDailySummary(0);
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
			DailySummaryfood();
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
			printCashDenominationForDailySummary(1);
			DailySummaryTPB();
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
			printCashDenominationForDailySummary(2);
		} else {
			printCashDenominationForDailySummary(0);
		}
		byte[] ff1 ={0x0c};
		woosim.controlCommand(ff1, 1);
		byte[] lf1 = {0x0a};
		woosim.controlCommand(lf1, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);

	}

	private void printDailySales_Pay_Summary() {
		String LINE 				= "%1$1.1s %2$-89.89s \r\n";
		String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-9.9s %4$-7.7s %5$-30.30s %6$-9.9s %7$5.5s %8$10.10s %9$8.8s \n";
		String linesSingle = "-------------------------------------------------------------------------------------------------------------";
		String formatForAddress1 = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE1 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-18.18s %4$-10.10s %5$-42.42s %6$11.11s\r\n";
		String format1 			= "%1$1.0s %2$-4.4s %3$-12.12s %4$-10.10s %5$-25.25s %6$-12.12s %7$-10.10s %8$11.11s\r\n";
		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview1 = "%1$-10.10s %2$-30.30s  %3$-1.1s \n";
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		// Print Daily Sales, Return, payment in Print as suggested by Fayaz in 16-06-2017 vecDailySalesData
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview1,"",("TRIP DETAIL").toUpperCase(),""), 0x11, true);

		String lines1 = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);
		String format2 			= "%1$1.0s %2$-3.3s %3$-7.7s %4$-21.21s %5$7.7s %6$7.7s %7$7.7s %8$8.8s %9$8.8s %10$12.12s\r\n";
		woosim.saveSpool(EUC_KR, String.format(format2,"","SR#","CODE","NAME","T.SALES","COLL","GRV","CHECKIN","CHECKOUT","T.SPEND(MIN)"),0, true);
		int toatalmin=0;
		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);
		for( int i = 0 ; i < vecDailySalesData.size() ; i++ )
		{
			DailySalesData dailesalesdetail = vecDailySalesData.get(i);
			toatalmin+=StringUtils.getInt(dailesalesdetail.Tspend);
			woosim.saveSpool(EUC_KR, String.format(format2, "", "" + dailesalesdetail.SlNo, "" + dailesalesdetail.Code,""+dailesalesdetail.SiteName,dailesalesdetail.TSales!=null?dailesalesdetail.TSales:"-", dailesalesdetail.Collection!=null?dailesalesdetail.Collection:"-", dailesalesdetail.Return!=null?dailesalesdetail.Return:"-", dailesalesdetail.Checkin,dailesalesdetail.Checkout,dailesalesdetail.Tspend), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL UNVISITED",":",""+deffAmt.format(0)),0, true);
		woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","Total Visit in Min","","","",""+toatalmin),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		//=========================================================================================================
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview1,"",("PAYMENT RECEIVED SUMMARY").toUpperCase(),""), 0x11, true);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress1,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Period: "+fromDate),0, false);
		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);


		woosim.saveSpool(EUC_KR, String.format(format1,"","SR#","DOC NO","CODE","NAME","INV NO","INV DATE","AMOUNT"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);
		float totalPrice1  = 0;
		int k=1;
		String date="";
		for( int i = 0 ; i < vecCustomerInvoiceDo.size() ; i++ )
		{
			Customer_InvoiceDO customerInvoiceDO = vecCustomerInvoiceDo.get(i);
			if(customerInvoiceDO.vecPaymentDetailDOs.get(0).invoiceDate!=null)
			date=CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, customerInvoiceDO.vecPaymentDetailDOs.get(0).invoiceDate.split("T")[0]);
			totalPrice1 += StringUtils.getFloat(""+customerInvoiceDO.invoiceTotal);
			if(customerInvoiceDO.vecPaymentDetailDOs.size()==1) {
				woosim.saveSpool(EUC_KR, String.format(format1, "", "" + k, "" + customerInvoiceDO.receiptNo, "" + customerInvoiceDO.customerSiteId, "" + customerInvoiceDO.siteName, customerInvoiceDO.vecPaymentDetailDOs.get(0).invoiceNumber,date, "" + deffAmt.format(StringUtils.getFloat("" + customerInvoiceDO.invoiceTotal))), 0, false);
				k++;
			}
			else
			{
				for(PaymentDetailDO paymentdetailDo : customerInvoiceDO.vecPaymentDetailDOs)
				{
					woosim.saveSpool(EUC_KR, String.format(format1,"",""+k,""+customerInvoiceDO.receiptNo,""+customerInvoiceDO.customerSiteId,""+customerInvoiceDO.siteName,paymentdetailDo.invoiceNumber,date,""+deffAmt.format(StringUtils.getFloat(""+paymentdetailDo.invoiceAmount))),0, false);
					k++;
				}
			}
		}
		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);
		woosim.saveSpool(EUC_KR, String.format(price,"","Total",deffAmt.format(totalPrice1)),0, false);
//		woosim.saveSpool(EUC_KR, String.format(LINE1,"",lines1),0, false);
	}

	private void DailySummaryfood()
	{
		try {
			byte[] init = {0x1b,'@'};
			woosim.controlCommand(init, init.length);
			String formatForAddress 	= "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
			String formatForDate		= "%1$1.0s %2$-44.44s %3$-20.20s %4$-22.22s\r\n";
			String LINE 				= "%1$1.1s %2$-89.89s \r\n";
			String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-34.34s %6$-12.12s %7$10.10s\n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-11.11s %7$3.3s %8$10.10s\n";
			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatItemreason	 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-26.26s\n";
			String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
			String formatFooter  		= "%1$1.1s %2$-22.22s %3$1.1s %4$15.15s       %5$-22.22s %6$1.1s %7$15.15s\r\n";
//			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$11.11s %7$3.3s %8$10.10s\n";
			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
			//printHeader();
			woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Daily Summary Food").toUpperCase(),""), 0x11, true);

			//ArrayList<DailySummaryDO> arr= new ArrayList<DailySummaryDO>();
			String Date= CalendarUtils.getCurrentDateAsStringInFormatPrint();
			String Time= CalendarUtils.getCurrentTime();
			String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
			woosim.saveSpool(EUC_KR, String.format(formatForDate,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+stratDate,"Day: "+objDailySumDO.dayOfWeek),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Vehicle No.: "+objDailySumDO.vehicleNo,"Helper Name: "+objDailySumDO.helperName),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Print Date: "+Date,"Print Time:"+Time),0, false);

			String lines = "=======================================================================================================";
			String linesSingle = "-------------------------------------------------------------------------------------------------------------";
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","DOCUMENT NO","CODE","CUST. NAME","TRX TYPE","QTY","GRSAMNT","NETAMNT"),0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			DailySummaryDO objDailySummary = null;
			float cash = 0, cheque = 0,sales = 0,grv = 0,foc = 0;
			int zeroSales = 0,unVisited = 0,qty=0,k=1;

			LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = objDailySumDO.hashDailySummary;
			for(int j = 1 ; j <=5 ; j++)
			{
				k=1;
				arrDailySummary = hashDailySummary.get(""+j);
				float amt=0;
				float amt1=0;
				if(arrDailySummary != null)
				{
					for(int i=0;i < arrDailySummary.size(); i++)
					{
						objDailySummary = arrDailySummary.get(i);

						if(objDailySummary != null && StringUtils.getInt(objDailySummary.Division)==1)
						{
							String strDocNo = "";
							if(objDailySummary.DocNo!=null && objDailySummary.DocNo.equalsIgnoreCase(""))
								strDocNo = "N/A";
							else
								strDocNo = ""+objDailySummary.DocNo;
							String strAmount = "";
							String GRSstrAmount = "";
							if((""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount))).equalsIgnoreCase(""))
								strAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount));
							else
								strAmount = "0.00";
							if((objDailySummary.type!=null&&objDailySummary.type.equals("Sales"))||(objDailySummary.type!=null&&objDailySummary.type.equalsIgnoreCase("FOC")))
								qty+=StringUtils.getInt(objDailySummary.Quantity);

							GRSstrAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.TotalAmnt));
							String type = "";
							if(objDailySummary.type != null)
								type += objDailySummary.type;
							if(StringUtils.getInt(objDailySummary.Priority) > 5)
								woosim.saveSpool(EUC_KR, String.format(formatItemreason,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type),0, false);
							else
								woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type,""+objDailySummary.Quantity,GRSstrAmount,strAmount),0, false);
							k++;
							amt += StringUtils.getFloat(""+objDailySummary.amount);
							amt1 += StringUtils.getFloat(""+objDailySummary.TotalAmnt);
						}
					}
				}
				if(j == StringUtils.getInt("1"))//SALES
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//	woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL SALES",":",""+deffAmt.format(objDailySumDO.total[0])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL SALES","",""+qty,""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					qty=0;
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("2"))//GRV
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL GRV",":",""+deffAmt.format(objDailySumDO.total[1])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL GRV","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("3"))//FOC
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL FOC","",""+qty,""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL FOC",":",""+deffAmt.format(objDailySumDO.total[2])),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					qty=0;
				}
				else if(j == StringUtils.getInt("4"))//CASH
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CASH",":",""+deffAmt.format(objDailySumDO.total[3])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CASH","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("5"))//CHEQUE
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CHEQUE",":",""+deffAmt.format(objDailySumDO.total[4])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CHEQUE","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private void DailySummaryTPB()
	{
		try {
			byte[] init = {0x1b,'@'};
			woosim.controlCommand(init, init.length);
			String formatForAddress 	= "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
			String formatForDate		= "%1$1.0s %2$-44.44s %3$-20.20s %4$-22.22s\r\n";
			String LINE 				= "%1$1.1s %2$-89.89s \r\n";
			String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-34.34s %6$-12.12s %7$10.10s\n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-11.11s %7$3.3s %8$10.10s\n";
			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatItemreason	 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-26.26s\n";
			String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
			String formatFooter  		= "%1$1.1s %2$-22.22s %3$1.1s %4$15.15s       %5$-22.22s %6$1.1s %7$15.15s\r\n";
//			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$11.11s %7$3.3s %8$10.10s\n";
			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
			//printHeader();
			woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Daily Summary TPB").toUpperCase(),""), 0x11, true);

			//ArrayList<DailySummaryDO> arr= new ArrayList<DailySummaryDO>();
			String Date= CalendarUtils.getCurrentDateAsStringInFormatPrint();
			String Time= CalendarUtils.getCurrentTime();
			String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
			woosim.saveSpool(EUC_KR, String.format(formatForDate,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+stratDate,"Day: "+objDailySumDO.dayOfWeek),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Vehicle No.: "+objDailySumDO.vehicleNo,"Helper Name: "+objDailySumDO.helperName),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Print Date: "+Date,"Print Time:"+Time),0, false);

			String lines = "=======================================================================================================";
			String linesSingle = "-------------------------------------------------------------------------------------------------------------";
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","DOCUMENT NO","CODE","CUST. NAME","TRX TYPE","QTY","GRSAMNT","NETAMNT"),0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			DailySummaryDO objDailySummary = null;
			float cash = 0, cheque = 0,sales = 0,grv = 0,foc = 0;
			int zeroSales = 0,unVisited = 0,qty=0,k=1;

			LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = objDailySumDO.hashDailySummary;
			for(int j = 1 ; j <=5 ; j++)
			{
				k=1;
				arrDailySummary = hashDailySummary.get(""+j);
				float amt=0;
				float amt1=0;
				if(arrDailySummary != null)
				{
					for(int i=0;i < arrDailySummary.size(); i++)
					{
						objDailySummary = arrDailySummary.get(i);

						if(objDailySummary != null && StringUtils.getInt(objDailySummary.Division)==2)
						{
							String strDocNo = "";
							if(objDailySummary.DocNo!=null && objDailySummary.DocNo.equalsIgnoreCase(""))
								strDocNo = "N/A";
							else
								strDocNo = ""+objDailySummary.DocNo;
							String strAmount = "";
							String GRSstrAmount = "";
							if((""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount))).equalsIgnoreCase(""))
								strAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount));
							else
								strAmount = "0.00";
							if((objDailySummary.type!=null&&objDailySummary.type.equals("Sales"))||(objDailySummary.type!=null&&objDailySummary.type.equalsIgnoreCase("FOC")))
								qty+=StringUtils.getInt(objDailySummary.Quantity);

							GRSstrAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.TotalAmnt));
							String type = "";
							if(objDailySummary.type != null)
								type += objDailySummary.type;
							if(StringUtils.getInt(objDailySummary.Priority) > 5)
								woosim.saveSpool(EUC_KR, String.format(formatItemreason,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type),0, false);
							else
								woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type,""+objDailySummary.Quantity,GRSstrAmount,strAmount),0, false);
							k++;
							amt += StringUtils.getFloat(""+objDailySummary.amount);
							amt1 += StringUtils.getFloat(""+objDailySummary.TotalAmnt);
						}
					}
				}
				if(j == StringUtils.getInt("1"))//SALES
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//	woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL SALES",":",""+deffAmt.format(objDailySumDO.total[0])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL SALES","",""+qty,""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					qty=0;
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("2"))//GRV
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL GRV",":",""+deffAmt.format(objDailySumDO.total[1])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL GRV","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("3"))//FOC
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL FOC","",""+qty,""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL FOC",":",""+deffAmt.format(objDailySumDO.total[2])),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					qty=0;
				}
				else if(j == StringUtils.getInt("4"))//CASH
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CASH",":",""+deffAmt.format(objDailySumDO.total[3])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CASH","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("5"))//CHEQUE
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CHEQUE",":",""+deffAmt.format(objDailySumDO.total[4])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CHEQUE","","",""+deffAmt.format(amt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void DailySummaryIcecream()
	{
		try {
			byte[] init = {0x1b,'@'};
			woosim.controlCommand(init, init.length);
			String formatForAddress 	= "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
			String formatForDate		= "%1$1.0s %2$-44.44s %3$-20.20s %4$-22.22s\r\n";
			String LINE 				= "%1$1.1s %2$-89.89s \r\n";
			String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-34.34s %6$-12.12s %7$10.10s\n";
//			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-11.11s %7$3.3s %8$10.10s\n";
			String formatItem		 	= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatItemreason	 	= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$-26.26s\n";
			String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
			String formatFooter  		= "%1$1.1s %2$-22.22s %3$1.1s %4$15.15s       %5$-22.22s %6$1.1s %7$15.15s\r\n";
//			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-15.15s %4$-9.9s %5$-30.30s %6$11.11s %7$3.3s %8$10.10s\n";
			String formatFooter1  		= "%1$1.1s %2$-3.3s %3$-12.12s %4$-7.7s %5$-30.30s %6$-9.9s %7$3.3s %8$10.10s %9$8.8s \n";
			String formatForSignature 	= "%1$1.1s %2$44.44s %3$44.44s\r\n";
			//printHeader();
			woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Daily Summary ICE CREAM").toUpperCase(),""), 0x11, true);

			String Date= CalendarUtils.getCurrentDateAsStringInFormatPrint();
			String Time= CalendarUtils.getCurrentTime();
			String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
			woosim.saveSpool(EUC_KR, String.format(formatForDate,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Date: "+stratDate,"Day: "+objDailySumDO.dayOfWeek),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Vehicle No.: "+objDailySumDO.vehicleNo,"Helper Name: "+objDailySumDO.helperName),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Print Date: "+Date,"Print Time:"+Time),0, false);

			String lines = "=======================================================================================================";
			String linesSingle = "-------------------------------------------------------------------------------------------------------------";
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","DOCUMENT NO","CODE","CUST. NAME","TRX TYPE","QTY","GRSAMNT","NETAMNT"),0, true);
			woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
			DailySummaryDO objDailySummary = null;
			//<editor-fold desc="ase">
			float cash = 0, cheque = 0,sales = 0,grv = 0,foc = 0;
			//</editor-fold>
			int zeroSales = 0,unVisited = 0,qty=0,k=1;

			arrDailySummary = objDailySumDO.arrDailySummary;
			LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = objDailySumDO.hashDailySummary;
			for(int j = 1 ; j <=5 ; j++)
			{
				k=1;
				arrDailySummary = hashDailySummary.get(""+j);
				float amt=0;
				float amnt1=0;
				if(arrDailySummary != null)
				{
					for(int  i =  0 ; i < arrDailySummary.size(); i++)
					{
						objDailySummary = arrDailySummary.get(i);
						if(objDailySummary != null && StringUtils.getInt(objDailySummary.Division)==0)
						{
							String strDocNo = "";
							if(objDailySummary.DocNo!=null && objDailySummary.DocNo.equalsIgnoreCase(""))
								strDocNo = "N/A";
							else
								strDocNo = ""+objDailySummary.DocNo;
							String strAmount = "";
							String GrsstrAmount = "";
							if((""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount))).equalsIgnoreCase(""))
								strAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.amount));
							else
								strAmount = "0.00";
							if((objDailySummary.type!=null&&objDailySummary.type.equals("Sales"))||(objDailySummary.type!=null&&objDailySummary.type.equalsIgnoreCase("FOC")))
								qty+=StringUtils.getInt(objDailySummary.Quantity);

							GrsstrAmount = ""+deffAmt.format(StringUtils.getFloat(""+objDailySummary.TotalAmnt));
							String type = "";
							if(objDailySummary.type != null)
								type += objDailySummary.type;
							if(StringUtils.getInt(objDailySummary.Priority) > 5)
								woosim.saveSpool(EUC_KR, String.format(formatItemreason,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type),0, false);
							else
								woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+k,strDocNo,""+objDailySummary.siteID,""+objDailySummary.siteName, type,""+objDailySummary.Quantity,GrsstrAmount,strAmount),0, false);
							k++;
							amt=amt+StringUtils.getFloat(""+objDailySummary.amount);
							amnt1=amnt1+StringUtils.getFloat(""+objDailySummary.TotalAmnt);
						}

					}
				}
				if(j == StringUtils.getInt("1"))//SALES
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//	woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL SALES",":",""+deffAmt.format(objDailySumDO.total[0])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL SALES","",""+qty,""+deffAmt.format(amnt1),""+deffAmt.format(amt)),0, true);
					qty=0;
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("2"))//GRV
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL GRV",":",""+deffAmt.format(objDailySumDO.total[1])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL GRV","","",""+deffAmt.format(amnt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("3"))//FOC
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL FOC","",""+qty,""+deffAmt.format(amnt1),""+deffAmt.format(amt)),0, true);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL FOC",":",""+deffAmt.format(objDailySumDO.total[2])),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					qty=0;
				}
				else if(j == StringUtils.getInt("4"))//CASH
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CASH",":",""+deffAmt.format(objDailySumDO.total[3])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CASH","","",""+deffAmt.format(amnt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
				else if(j == StringUtils.getInt("5"))//CHEQUE
				{
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
					//woosim.saveSpool(EUC_KR, String.format(formatFooter,"","","","","TOTAL CHEQUE",":",""+deffAmt.format(objDailySumDO.total[4])),0, true);
					woosim.saveSpool(EUC_KR, String.format(formatFooter1,"","","","","TOTAL CHEQUE","","",""+deffAmt.format(amnt1),""+deffAmt.format(amt)),0, true);
					woosim.saveSpool(EUC_KR, String.format(LINE,"",linesSingle),0, false);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	private void printHeader()
	{

		String formatForContact 	= "%1$-46.46s %2$-10.10s %3$-15.15s \r\n";
//		String formatInvoiceCopy 	= "%1$-34.34s %2$-20.20s  %3$-1.1s \n";
		try
		{
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try
		{
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, String.format(formatForContact,"","TRN : ","100073922500003"),0, false);
		if(salesmanContact != null && !TextUtils.isEmpty(salesmanContact))//salesmanContact
			woosim.saveSpool(EUC_KR, String.format(formatForContact,"","Contact : ",salesmanContact),0, false);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForContact,"","Contact : ",""),0, false);

	}
	private void printHeaderNew(boolean isAbudabhi)
	{

		String formatForContact 	= "%1$-46.46s %2$-10.10s %3$-12.12s \r\n";
//		String formatInvoiceCopy 	= "%1$-34.34s %2$-20.20s  %3$-1.1s \n";

//		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try
		{
			woosim.saveSpool(EUC_KR, "\r\n                ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
			woosim.saveSpool(EUC_KR,  "\r\n",0, true);
			if(!isAbudabhi) {
				try
				{
					woosim.printBitmap(AppConstants.KwalityLogoPath+"/leftpic.bmp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				woosim.saveSpool(EUC_KR, "", 1, false);
				try
				{
					woosim.printBitmap(AppConstants.KwalityLogoPath+"/rightpic.bmp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}else
			{
				try
				{
					woosim.printBitmap(AppConstants.KwalityLogoPath+"/leftpicabudahi.bmp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				woosim.saveSpool(EUC_KR, "", 1, false);
				try
				{
					woosim.printBitmap(AppConstants.KwalityLogoPath+"/rightpicabudabhi.bmp");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			woosim.saveSpool(EUC_KR, "\r\n", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
			String formatForContact2	= "%1$-1.1s %2$-44.44s %3$-45.45s";
			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","Registered Address","Correspondence Address" ),0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","PURE ICE CREAM CO. LLC.,","PURE ICE CREAM CO. LLC.," ),0, true);

			if(!isAbudabhi) {
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Al Wasit Street, Industrial Area-2", "Al Wasit Street, Industrial Area-2"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Sharjah, U.A.E. - P.O.Box 6172.", "Sharjah, U.A.E. - P.O.Box 6172. "), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Phone- +97165332153 Fax- +97165339707", "Phone- +97165332153 Fax- +97165339707"), 0, true);
			}else
			{
				woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","Mussafah, Abudhabi, U.A.E." ,"Al Wasit Street, Industrial Area-2"),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","P.O.Box -6172","Sharjah, U.A.E. - P.O.Box 6172. " ),0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","Phone- +97125547904 Fax- +97125547904","Phone- +97165332153 Fax- +97165339707" ),0, true);
			}

			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","Email: pureices@emirates.net.ae","Email: pureices@emirates.net.ae" ),0, true);
			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","","TRN - 100073922500003" ),0, true);

			if(salesmanContact != null && !TextUtils.isEmpty(salesmanContact))//salesmanContact
				woosim.saveSpool(EUC_KR, String.format(formatForContact,""," Contact : ",salesmanContact),0, false);
			else
				woosim.saveSpool(EUC_KR, String.format(formatForContact,""," Contact : ",""),0, false);

//			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"Registered Address","Correspondence Address" ),0, true);
//			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"PURE ICE CREAM CO. LLC.,","PURE ICE CREAM CO. LLC.," ),0, true);
//			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"Email: pureices@emirates.net.ae","Email: pureices@emirates.net.ae" ),0, true);
//			woosim.saveSpool(EUC_KR, String.format(formatForContact2,"","TRN - 100073922500003" ),0, true);


//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}


//		(exclude Abudhabi)



//
//		byte[] ff ={0x0c};
//		woosim.controlCommand(ff, 1);
//		byte[] lf = {0x0a};
//		woosim.controlCommand(lf, lf.length);
//		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
//		woosim.printSpool(true);
//
//		cardData = null;
//		isPrinted = true;

	}

	private void printLogReport()
	{
		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE 			= "%1$1.1s %2$-89.89s \r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-10.10s %4$-60.60s %5$11.11s\r\n";
//		String format 			= "%1$1.0s %2$-4.4s %3$-8.8s %4$-55.55s %5$5.5s %6$5.5s %7$5.5s\r\n";
//		String price 			= "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview  = "%1$-14.14s %2$-24.24s  %3$-1.1s \n";
		String formatItem		 	= "%1$1.1s %2$-3.3s %3$-19.19s %4$-9.9s %5$-10.10s %6$-18.18s %7$10.10s   %8$12.12s\n";
		String formatDetails    	= "%1$1.1s %2$-3.3s %3$-45.45s %4$-15.15s %5$8.8s   %6$12.12s\n";
		String formatFooter  		= "%1$1.1s %2$-25.25s %3$1.1s %4$16.16s        %5$-30.30s %6$1.1s %7$4.4s\r\n";
		printHeader();
		/*try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try {
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview,"",("Log Report").toUpperCase(),""), 0x11, true);

		String stratDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, fromDate);
		String endDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, toDate);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")+" ( "+preference.getStringFromPreference(Preference.USER_ID, "")+" )","Period: From "+stratDate+" to "+endDate),0, false);

		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatItem,"","S1#","Cust. Code/Name","As Per JP","Type","Doc No","Amount","Time"),0, true);
		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		for(int  i =  0 ; i < trxLogHeaders.vecTrxLogDetailsDO.size()  ; i++)
		{
			TrxLogDetailsDO trxLogDetailsDO = trxLogHeaders.vecTrxLogDetailsDO.get(i);
			if(trxLogDetailsDO != null)
			{
				String strDocNo = "";
				if(trxLogDetailsDO.DocumentNumber!=null && trxLogDetailsDO.DocumentNumber.equalsIgnoreCase(""))
					strDocNo = "N/A";
				else
					strDocNo = ""+trxLogDetailsDO.DocumentNumber;
				String strAmount = "";
				if((""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount)))!=null && !(""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount))).equalsIgnoreCase(""))
					strAmount = ""+deffAmt.format(StringUtils.getFloat(""+trxLogDetailsDO.Amount));
				else
					strAmount = "0.00";
				String strAsperJP = trxLogDetailsDO.IsJp.equalsIgnoreCase("True")?"Yes":"No";

				String timeStamp = CalendarUtils.parseDate(CalendarUtils.DATE_PATTERN, CalendarUtils.DATE_PATTERN_PRINT, trxLogDetailsDO.TimeStamp);

				String Date[] =CalendarUtils.getTimeStamp(timeStamp);
				String strDate = Date[0];
				String strTime = Date[1];
				woosim.saveSpool(EUC_KR, String.format(formatItem,"",""+(i+1),""+trxLogDetailsDO.CustomerCode,""+strAsperJP,""+trxLogDetailsDO.TrxType,strDocNo,strAmount,""+strDate),0, false);
				woosim.saveSpool(EUC_KR, String.format(formatDetails,"","",""+trxLogDetailsDO.CustomerName,"","",""+strTime),0, false);
			}
		}

		woosim.saveSpool(EUC_KR, String.format(LINE,"",lines),0, false);
		if(isMTD==0)
		{
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's SALES",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalSales)),"TODAY's SCHEDULED CALLS ",":",""+trxLogHeaders.TotalScheduledCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's CREDIT NOTE",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCreditNotes)),"TODAY's ACTUAL CALLS ",":",""+trxLogHeaders.TotalActualCalls),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","TODAY's COLLECTION",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCollections)),"TODAY's PRODUCTIVE CALLS",":",""+trxLogHeaders.TotalProductiveCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's ACTUAL CALLS (JP)",":"," "+StringUtils.getInt(""+trxLogHeaders.TotalActualCallsPlanned),"TODAY's PRODUCTIVE CALLS (JP)",":",""+trxLogHeaders.TotalProductiveCallsPlanned),0, false);

			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","MTD SALES",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalSales)),"MTD SCHEDULED CALLS",":",""+trxMonthDetails.TotalScheduledCalls),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","MTD CREDIT NOTE",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCreditNotes)),"MTD ACTUAL CALLS",":",""+trxMonthDetails.TotalActualCalls),0, false);
			woosim.saveSpool(EUC_KR, String.format(formatFooter,"","MTD COLLECTION",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCollections)),"MTD PRODUCTIVE CALLS",":",""+trxMonthDetails.TotalProductiveCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","MTD ACTUAL CALLS (JP)",":"," "+StringUtils.getInt(""+trxMonthDetails.TotalActualCallsPlanned),"MTD PRODUCTIVE CALLS (JP)",":",""+trxMonthDetails.TotalProductiveCallsPlanned),0, false);

		}
		else
		{
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's SALES",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalSales)),"TODAY's SCHEDULED CALLS ",":",""+trxMonthDetails.TotalScheduledCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's CREDIT NOTE",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCreditNotes)),"TODAY's ACTUAL CALLS ",":",""+trxMonthDetails.TotalActualCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's COLLECTION",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxMonthDetails.TotalCollections)),"TODAY's PRODUCTIVE CALLS",":",""+trxMonthDetails.TotalProductiveCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","TODAY's ACTUAL CALLS (JP)",":",""+StringUtils.getInt(""+trxMonthDetails.TotalActualCallsPlanned),"TODAY's PRODUCTIVE CALLS (JP)",":",""+trxMonthDetails.TotalProductiveCallsPlanned),0, false);

			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","MTD SALES",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalSales)),"MTD SCHEDULED CALLS",":",""+trxLogHeaders.TotalScheduledCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","MTD CREDIT NOTE",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCreditNotes)),"MTD ACTUAL CALLS",":",""+trxLogHeaders.TotalActualCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","MTD COLLECTION",":","AED "+deffAmt.format(StringUtils.getFloat(""+trxLogHeaders.TotalCollections)),"MTD PRODUCTIVE CALLS",":",""+trxLogHeaders.TotalProductiveCalls),0, false);
			woosim.saveSpool(EUC_KR,String.format(formatFooter,"","MTD ACTUAL CALLS (JP)",":"," "+StringUtils.getInt(""+trxLogHeaders.TotalActualCallsPlanned),"MTD PRODUCTIVE CALLS (JP)",":",""+trxLogHeaders.TotalProductiveCallsPlanned),0, false);

		}

		byte[] ff ={0x0c};
		woosim.controlCommand(ff, 1);
		byte[] lf = {0x0a};
		woosim.controlCommand(lf, lf.length);
		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
		woosim.printSpool(true);
		cardData = null;
		isPrinted = true;
	}

	/**
	 * Method to print the Payment Receipt in 4 inch mode
	 * @param isCustomerCopy
	 */
	private void printReceipt(int isCustomerCopy) {
		byte[] init = {0x1b, '@'};
		woosim.controlCommand(init, init.length);
		String formatForAddress = "%1$1.0s %2$-44.44s %3$-44.44s\r\n";
		String LINE = "%1$1.1s %2$-89.89s \r\n";
		String format = "%1$1.0s %2$-4.4s %3$-18.18s %4$-18.18s %5$-32.32s %6$11.11s\r\n";
		String price = "%1$1.0s %2$-44.44s %3$44.44s\r\n";
		String formatHeaderPreview = "%1$-16.16s %2$-20.20s  %3$-1.1s \n";
		String formatInvoiceCopy = "%1$-31.31s %2$-23.23s  %3$-1.1s \n";
		String formatInvoiceCopy2 = "%1$-31.31s %2$-32.32s  %3$-1.1s \n";
		String formatForSignature = "%1$1.1s %2$44.44s %3$44.44s\r\n";
		String formatRubbish = "%1$1.1s %2$-89.89s\r\n";
		String strPaymentCode = "";
		String strPaymentType = "";

//		printHeader();
//========================================newly added for food=========================================
		if (printTypeIce == 200) {
			printHeaderNewKwalityNew();
		} else {
			boolean isFromAbudabhii=mallsDetails!=null &&  mallsDetails.regionCode.toLowerCase().equalsIgnoreCase("abu dhabi")? true:false ;
			printHeaderNew(isFromAbudabhii);
		}

//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "          ", 1, false);
//		try {
//			woosim.printBitmap(AppConstants.KwalityLogoPath+"/address.bmp");
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
		if (paymode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE)) {
			strPaymentType = "CHEQUE";
			strPaymentCode = "Chq";
		} else {
			strPaymentType = "CASH";
			strPaymentCode = "";
		}
		String division = "";
		if (objPaymentDO.Division == 1)
			division = "Food";
		else if (objPaymentDO.Division == 2)
			division = "Third Party Brand";
		else
			division = "Ice Cream";
//changed
		woosim.saveSpool(EUC_KR, String.format(formatHeaderPreview, "", (strPaymentType + " RECEIPT").toUpperCase(), ""), 0x11, true);
		if (isCustomerCopy == 0) {
			if(division.equalsIgnoreCase("Third Party Brand"))
				woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy2, "", ( "     Customer Copy").toUpperCase(), ""), 0x8, true);
			else
				woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy, "", ( "     Customer Copy").toUpperCase(), ""), 0x8, true);
		} else {
			if(division.equalsIgnoreCase("Third Party Brand"))
				woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy2, "",("     Office Copy").toUpperCase(), ""), 0x8, true);
			else
				woosim.saveSpool(EUC_KR, String.format(formatInvoiceCopy, "", ("     Office Copy").toUpperCase(), ""), 0x8, true);
		}

//		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		/* commented by vinod


		String mydate = CalendarUtils.getDateToShow((objPaymentDO.paymentDate));
		String AM_PM = Calendar.getInstance().get(Calendar.AM_PM) == 1 ? "PM" : "AM";

		woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.siteName, "Cust No: " + mallsDetails.site), 0, false);
		//vinod
		if (mallsDetails != null && mallsDetails.addresss1 != null && !TextUtils.isEmpty(mallsDetails.addresss1)) {
			woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", mallsDetails.addresss1, ""), 0, false);
		}
		woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", "SM Name: " + preference.getStringFromPreference(Preference.USER_NAME, ""), "SM NO: " + preference.getStringFromPreference(Preference.USER_ID, "")), 0, false);

//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Receipt No: "+strReceiptNo,"Date: "+mydate*//*.substring(0, mydate.lastIndexOf(":")) +AM_PM*//*),0, false);
//vinod
//		woosim.saveSpool(EUC_KR,  " "+"ReceiptNo:" +strReceiptNo  ,1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-21.21s", " " + "Rec. No:" + strReceiptNo), 1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n", "     Date: " + mydate), 0, false);*/


//---------copied from sales--------------------------------------------
		String mydate = CalendarUtils.getDateToShow((objPaymentDO.paymentDate));
		String AM_PM =Calendar.getInstance().get(Calendar.AM_PM)== 1? "PM" : "AM" ;
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Bill To:","Ship To:"),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.siteName,""+mallsDetails.Attribute3),0, false);
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site,""),0, false);
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Cust Code: "+mallsDetails.site,"Cust Code: "+mallsDetails.site),0, false);
//		if (mallsDetails!=null && mallsDetails.addresss1 !=null && !TextUtils.isEmpty(mallsDetails.addresss1))
//		{
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"",mallsDetails.addresss1,mallsDetails.addresss1),0, false);
//		}
		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","TRN No: "+mallsDetails.VATNo, "Area:"+mallsDetails.regionCode),0, false);

		woosim.saveSpool(EUC_KR, String.format("%1$-21.21s", " " + "Rec. No:" + strReceiptNo), 1, false);
		woosim.saveSpool(EUC_KR, String.format("%1$-24.24s \n" , "     SM NO: "+preference.getStringFromPreference(Preference.USER_ID, "")  ),0,false);

		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"","Rept.Date: "+mydate ,"SM Name: "+preference.getStringFromPreference(Preference.USER_NAME, "")),0, false);

//		-------------------------------------------------------------------------


		String lines = "=======================================================================================================";
		woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, false);

		woosim.saveSpool(EUC_KR, String.format(format, "", "SR#", "INVOICE NO", "INVOICE DATE", "REMARK", "AMOUNT"), 0, true);

		woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, false);


//			int totalPrice  = 0;
		for (int i = 0; i < arrInvoiceNumbers.size(); i++) {
//				totalPrice += StringUtils.getFloat(arrInvoiceNumbers.get(i).payingAmount);
			String invoiceDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, arrInvoiceNumbers.get(i).invoiceDate);

			woosim.saveSpool(EUC_KR, String.format(format, "", "" + (i + 1), arrInvoiceNumbers.get(i).invoiceNo, invoiceDate, "", "" + deffAmt.format(StringUtils.getFloat(arrInvoiceNumbers.get(i).balance))), 0, false);
		}

		String Currency = "AED";
		if (mallsDetails.currencyCode != null && !mallsDetails.currencyCode.equalsIgnoreCase(""))
			Currency = "" + mallsDetails.currencyCode;

		woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, false);

		woosim.saveSpool(EUC_KR, String.format(price, "", "Total (" + Currency + ")", deffAmt.format(StringUtils.getFloat(objPaymentDO.amount))), 0, false);

		woosim.saveSpool(EUC_KR, String.format(price, "", "Amount in Words:", new NumberUtils().convertNumtoLetter(StringUtils.getFloat(objPaymentDO.amount))), 0, false);
		if (paymode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE)) {
			for (PaymentDetailDO paymentDetails : objPaymentDO.vecPaymentDetails) {
				String checkDate = CalendarUtils.parseDate(CalendarUtils.DATE_STD_PATTERN, CalendarUtils.DATE_STD_PATTERN_PRINT, paymentDetails.ChequeDate);
				woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", strPaymentCode + " Nr : " + paymentDetails.ChequeNo, strPaymentCode + " Amt : " + deffAmt.format(StringUtils.getFloat(paymentDetails.Amount))), 0, false);
				woosim.saveSpool(EUC_KR, String.format(formatForAddress, "", strPaymentCode + " Dt: " + checkDate, "Bank : " + paymentDetails.BankName), 0, false);
			}
		}
		woosim.saveSpool(EUC_KR, String.format(LINE, "", lines), 0, false);

//		woosim.saveSpool(EUC_KR, String.format(formatRubbish,"","* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."),0, false);


		if (printTypeIce == 200 && objPaymentDO.Division == TrxHeaderDO.get_DIVISION_FOOD()) {
			woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* Please issue cheque in favour of Kwality International Foodstuff L.L.C."), 0, false);
		}else
		if (printTypeIce == 200 && objPaymentDO.Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY()) {
			woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* Please issue cheque in favour of TASTY BITE FOODSTUFF TRADING LLC"), 0, false);
		} else {
			woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* Please issue cheque in favour of PURE ICE CREAM CO. L.L.C."), 0, false);
		}

		woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* E & O E"), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* Company will not be responsible for payment without an official receipt."), 0, false);
		woosim.saveSpool(EUC_KR, String.format(formatRubbish, "", "* Received complete invoice quantity in good condition. "), 0, false);

//		woosim.saveSpool(EUC_KR, String.format(formatForSignature,"","Customer Signature","For Pure Ice Cream Co. LLC.\r\n"),0, false);


			if(printTypeIce ==200){
				if((trxHeaderDO !=null && trxHeaderDO.Division==TrxHeaderDO.get_DIVISION_THIRD_PARTY()) || (objPaymentDO!=null  && objPaymentDO.Division ==TrxHeaderDO.get_DIVISION_THIRD_PARTY()))
					woosim.saveSpool(EUC_KR, String.format(formatForSignature, "", "Customer Signature", "For TASTY BITE FOODSTUFF TRADING LLC.\r\n"), 0, false);
				else
					woosim.saveSpool(EUC_KR, String.format(formatForSignature, "", "Customer Signature", "For Kwality International Foodstuff LLC.\r\n"), 0, false);

		}  else
			{

				woosim.saveSpool(EUC_KR, String.format(formatForSignature, "", "Customer Signature", "For Pure Ice Cream Co. LLC.\r\n"), 0, false);
			}

			woosim.saveSpool(EUC_KR, String.format(LINE, "", "-------------------------------------------- -------------------------------------------"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "|                                          | |                                         |"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "|                                          | |                                         |"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "|                                          | |                                         |"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "|                                          | |                                         |"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "|                                          | |                                         |"), 0, false);
			woosim.saveSpool(EUC_KR, String.format(LINE, "", "-------------------------------------------- -------------------------------------------"), 0, false);

			woosim.saveSpool(EUC_KR, String.format(LINE, "", "This is computer generated document & doesn't require signature."), 0, false);
			byte[] ff = {0x0c};
			woosim.controlCommand(ff, 1);
			byte[] lf = {0x0a};
			woosim.controlCommand(lf, lf.length);
			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
			woosim.printSpool(true);
			cardData = null;
			isPrinted = true;
		}

	public long dateDiffence(String beforeDate,String afterDate)
	{
		SimpleDateFormat beforeDateFormate = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat afterDateFormate = new SimpleDateFormat("dd/MM/yyyy");
		long day = 0;

		try
		{
			Date date1 = beforeDateFormate.parse(beforeDate);
			Date date2 = afterDateFormate.parse(afterDate);
			long diff = date2.getTime() - date1.getTime();
			day=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		}
		catch (java.text.ParseException e)
		{
			e.printStackTrace();
		}
		return day;
	}
	public void sortByDate(Vector<Customer_InvoiceDO> vec){
		Collections.sort(vec, new Comparator<Customer_InvoiceDO>() {
			@Override
			public int compare(Customer_InvoiceDO s1, Customer_InvoiceDO s2)
			{
				return s1.chequeDate.compareTo(s2.chequeDate);
			}
		});
	}


	private void closeForm()
	{
		woosim.BTDisConnection();
		Toast t = Toast.makeText(this, "CLOSE", Toast.LENGTH_SHORT);
		t.show();
	}




	/**
	 * Method to print the generated sales order in 3 inch mode
	 */
//	private void Print_3inch_Inventoty()
//	{
//		byte[] init = {0x1b,'@'};
//		woosim.controlCommand(init, init.length);
//		totalPrice = 0.0f;
//		String formatHeader = "%1$-8.8s %2$-15.15s  %3$-3.3s";
//		woosim.saveSpool(EUC_KR, String.format(formatHeader,"","STOCK INVENTORY",""), 0x11, true);
//
//		woosim.saveSpool(EUC_KR, "\r\n\n", 1, false);
//		try
//		{
//			woosim.printBitmap(AppConstants.masafiLogoPath+"/masafilogo1.bmp");
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "          ", 1, false);
//		try
//		{
//			woosim.printBitmap(AppConstants.masafiLogoPath+"/masafidetails.bmp");
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		woosim.saveSpool(EUC_KR, "\r\n", 0x11, true);
//
//		String formatForAddress = "%1$-32.32s   %2$-32.32s\r\n\r\n";
//
//		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//
//		woosim.saveSpool(EUC_KR, String.format(formatForAddress,"User Name : "+preference.getStringFromPreference(Preference.USER_NAME, ""),"Date: "+mydate.substring(0, mydate.lastIndexOf(":"))), 0, true);
//
//		if(vecInventoryItems != null && vecInventoryItems.size() > 0)
//		{
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"Inventory Quantity(Stock)",""), 0, true);
//			woosim.saveSpool(EUC_KR, "-------------------------------------------------------------------\r\n", 0, true);
//
//			String format = "%1$-3s %2$-8.8s %3$-18.18s %4$-4.4s %5$-9.9s %6$-10.10s %7$-9.9s \r\n";
//			woosim.saveSpool(EUC_KR,String.format(format,"SR#","ITEM CODE","DESCRIPTION","UOM","Total Qty","Delvrd Qty", "Avail Qty"), 0, true);
//
//			for( int i = 0 ; vecInventoryItems!=null && i < vecInventoryItems.size() ; i++ )
//			{
//				woosim.saveSpool(EUC_KR,String.format(format,""+(i+1),vecInventoryItems.get(i).itemCode,vecInventoryItems.get(i).itemDescription,vecInventoryItems.get(i).UOM,""+def.format(vecInventoryItems.get(i).PrimaryQuantity), ""+def.format(vecInventoryItems.get(i).deliveredCases), ""+def.format(vecInventoryItems.get(i).availCases)), 0, false);
//			}
//		}
//
//		if(vecGRVItems != null && vecGRVItems.size() > 0)
//		{
//			woosim.saveSpool(EUC_KR, "\r\n", 0, false);
//			woosim.saveSpool(EUC_KR, String.format(formatForAddress,"Collected Quantity(GRV)",""), 0, true);
//			woosim.saveSpool(EUC_KR, "-------------------------------------------------------------------\r\n", 0, true);
//
//			String format = "%1$-3s %2$-12.12s %3$-30.30s %4$-6.6s %5$-12.12s \r\n";
//			woosim.saveSpool(EUC_KR,String.format(format,"SR#","ITEM CODE","DESCRIPTION","UOM","Total Qty"), 0, true);
//
//			for( int i = 0 ; vecGRVItems!=null && i < vecGRVItems.size() ; i++ )
//			{
//				woosim.saveSpool(EUC_KR,String.format(format,""+(i+1),vecGRVItems.get(i).itemCode,vecGRVItems.get(i).itemDescription,vecGRVItems.get(i).UOM,""+def.format(vecGRVItems.get(i).PrimaryQuantity)), 0, false);
//			}
//		}
//
//		woosim.saveSpool(EUC_KR, "===================================================================\r\n", 0, true);
//		byte[] ff ={0x0c};
//		woosim.controlCommand(ff, 1);
//		byte[] lf = {0x0a};
//		woosim.controlCommand(lf, lf.length);
//		printFooter();
//		woosim.saveSpool(EUC_KR, "\r\n", 0, false);
//		woosim.printSpool(true);
//		cardData = null;
//		isPrinted = true;
//	}



	protected void onDestroy()
	{
		super.onDestroy();
		if (mBtAdapter != null)
		{
			mBtAdapter.cancelDiscovery();
		}
		if(woosim != null)
			woosim.BTDisConnection();
//		if(btnClose!=null)
//			btnClose.performClick();
		// Unregister broadcast listeners

		this.unregisterReceiver(mReceiver);
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
		{
			mBtAdapter.cancelDiscovery();
			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v).getText().toString();
			address 	= info.substring(info.length() - 17);
			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
			System.out.println("address" + address);
			setContentView(R.layout.woosim);
			createButton();
			btnOpen.performClick();
			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
		}
	};

	private void createButton()
	{
		btnOpen 		= (Button) findViewById(R.id.btn_open);

		btnClose 		= (Button) findViewById(R.id.btn_close);
		btnPrint2 		= (Button) findViewById(R.id.btn_print2inch);
		btnPrint3 		= (Button) findViewById(R.id.btn_print3inch);
		btnMSR23 		= (Button) findViewById(R.id.btn_msr23);
		btnMSR123 		= (Button) findViewById(R.id.btn_msr123);
		btnCardCancel 	= (Button) findViewById(R.id.btn_cardcancel);
		btnFinish 		= (Button) findViewById(R.id.btn_finish);
		btnPrintImg1 	= (Button) findViewById(R.id.btn_printimg1);
		btnPrintImg2 	= (Button) findViewById(R.id.btn_printimg2);
		cheProtocol 	= (CheckBox) findViewById(R.id.che_protocol);
		editTrack1 		= (EditText) findViewById(R.id.edit1);
		editTrack2 		= (EditText) findViewById(R.id.edit2);
		editTrack3 		= (EditText) findViewById(R.id.edit3);
		btnReprint 		= (Button) findViewById(R.id.btnReprint);
		btnFinishPrint 	= (Button) findViewById(R.id.btnFinishPrint);
		tvPrintHeader	= (TextView) findViewById(R.id.tvPrintHeader);

		btnClose.setOnClickListener(this);
		btnOpen.setOnClickListener(this);
		btnPrint2.setOnClickListener(this);
		btnPrint3.setOnClickListener(this);
		btnMSR23.setOnClickListener(this);
		btnMSR123.setOnClickListener(this);
		btnCardCancel.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		btnPrintImg1.setOnClickListener(this);
		btnPrintImg2.setOnClickListener(this);
		cheProtocol.setOnClickListener(this);
		editTrack1.setOnClickListener(this);
		editTrack2.setOnClickListener(this);
		editTrack3.setOnClickListener(this);
		btnReprint.setOnClickListener(this);
		btnFinishPrint.setOnClickListener(this);


//		btnReprint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		btnFinishPrint.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//		tvPrintHeader.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);

	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action))
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED)
				{
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// When discovery is finished, change the Activity title
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED	.equals(action))
			{
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0)
				{
					String noDevices = getResources().getText(R.string.none_found).toString();
					mNewDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery()
	{
		// Indicate scanning in the title
		if(mBtAdapter!=null)
		{
			if (mBtAdapter.getState() == BluetoothAdapter.STATE_OFF)
			{
				mBtAdapter.enable();
				showLoader("Please wait Turning on your bluetooth");
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						showDeviceLists(true);
					}
				},10000);
			}
			setProgressBarIndeterminateVisibility(true);
			setTitle(R.string.scanning);
			// Turn on sub-title for new devices
			findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
			// If we're already discovering, stop it
			if (mBtAdapter.isDiscovering())
			{
				mBtAdapter.cancelDiscovery();
			}
			// Request discover from BluetoothAdapter
			mBtAdapter.startDiscovery();
		}
	}


	public boolean onKeyDown(int keyConde, KeyEvent event)
	{
		if ((keyConde == KeyEvent.KEYCODE_BACK))
		{
			woosim.BTDisConnection();
//			 if(CALLFROM ==CONSTANTOBJ.PAYMENT_RECEIPT)
//			{
//				setResult(1000);
//				finish();
//			}
//			else
//			{
//				setResult(10000);
//				finish();
//			}

			hideLoader();
			setResult(Activity.RESULT_OK);
			finish();
		}
		return false;
	}

	private void printAsset()
	{
		int imgSize_1, imgSize_2, imgSize_3, imgSize_4, imgSize;

		byte[] init = {0x1b,'@'};
		woosim.controlCommand(init, init.length);
		try
		{
			AssetManager am = getApplicationContext().getAssets();
			InputStream is = am.open("masafilogo.bmp" ,	AssetManager.ACCESS_BUFFER);
			int t = is.available();
			byte[] bmpfile = new byte[t];
			for(int i = 0; i < t; i++)
				bmpfile[i] = (byte)is.read();

			imgSize_1 = bmpfile[5];
			imgSize_2 = bmpfile[4];
			imgSize_3 = bmpfile[3];
			imgSize_4 = bmpfile[2];
			if (imgSize_1 < 0)
				imgSize_1 += 256;
			else if (imgSize_2 < 0)
				imgSize_2 += 256;
			else if (imgSize_3 < 0)
				imgSize_3 += 256;
			else if (imgSize_4 < 0)
				imgSize_4 += 256;

			imgSize_1 = imgSize_1 << 24;
			imgSize_2 = imgSize_2 << 16;
			imgSize_3 = imgSize_3 << 8;
			imgSize = imgSize_1 | imgSize_2 | imgSize_3 | imgSize_4;

			woosim.printBitmap(bmpfile, imgSize);

		}
		catch (IOException e)
		{
			LogUtils.errorLog("PRINTBITMAP", "Error while printing bitmap");
			e.printStackTrace();
		}
	}

	private void printSavedImage()
	{
		byte[] ff = {0x0c};
		String sPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String imgPath = "sdcard/testfile.bmp";
		String makeFolder = sPath+"/woosim";
		Bitmap originalImg = BitmapFactory.decodeFile(imgPath);
		String outFilename = "test";
		int value = woosim.saveConvertedImage(2,originalImg,imgPath,makeFolder,outFilename);
		int s = 0;
		if(value == 1)
		{
			try
			{
				s = woosim.printBitmap(makeFolder+"/"+outFilename+".bmp");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if(s == 1)
			{
				woosim.controlCommand(ff, ff.length);
				int t = woosim.printSpool(true);
				LogUtils.errorLog("woosim","printSpool t: "+t);
			}
			else
			{
				LogUtils.errorLog("woosim","printSpool s: "+s );
			}

		}
		else
		{
			LogUtils.errorLog("woosim",""+value);
		}
	}

	private void printConvertedImage()
	{
		byte[] ff = {0x0c};
		String imgPath 		= 	"sdcard/testfile.bmp";
		Bitmap originalImg 	= 	BitmapFactory.decodeFile(imgPath);
		int value 			= 	woosim.printConvertedImage(2, originalImg, imgPath);
		if(value == 1)
		{
			woosim.controlCommand(ff, ff.length);
			int t = woosim.printSpool(true);
			LogUtils.errorLog("woosim","printSpool: "+t);
		}
		else
		{
			LogUtils.errorLog("woosim",""+value);
		}
	}

	public CustomDialog customDialog;
	//For showing Dialog message.
	class RunshowCustomDialogs implements Runnable
	{
		private String strTitle;//Title of the dialog
		private String strMessage;// Message to be shown in dialog
		private String firstBtnName;
		private String secondBtnName;
		private String from;
		private boolean isCancelable;

		public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable)
		{
			this.strTitle   	= strTitle;
			this.strMessage 	= strMessage;
			this.firstBtnName	= firstBtnName;
			this.secondBtnName	= secondBtnName;
			if(from != null)
				this.from			= from;
			else
				this.from= "";
			this.isCancelable = isCancelable;
		}

		@Override
		public void run()
		{

			View view = getLayoutInflater().inflate(R.layout.custom_common_popup, null);
			if(customDialog!=null && customDialog.isShowing())
				customDialog.dismiss();
			customDialog = new CustomDialog(WoosimPrinterActivity.this, view, new Preference(WoosimPrinterActivity.this).getIntFromPreference("DEVICE_DISPLAY_WIDTH",320) - 40, LayoutParams.WRAP_CONTENT, true);
			customDialog.setCancelable(isCancelable);
			TextView tvTitle 	  = (TextView)view.findViewById(R.id.tvTitlePopup);
			TextView tvMessage 	  = (TextView)view.findViewById(R.id.tvMessagePopup);
			Button btnYes 		  = (Button) view.findViewById(R.id.btnYesPopup);
			Button btnNo 		  = (Button) view.findViewById(R.id.btnNoPopup);

//			tvTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//			tvMessage.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//			btnYes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
//			btnNo.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);

			tvTitle.setText(""+strTitle);
			tvMessage.setText(""+strMessage);
			btnYes.setText(""+firstBtnName);

			if(secondBtnName != null && !secondBtnName.equalsIgnoreCase(""))
				btnNo.setText(""+secondBtnName);
			else
				btnNo.setVisibility(View.GONE);

			btnYes.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					customDialog.dismiss();
					if(from.equalsIgnoreCase("cardswipe"))
					{
						showLoader("Please swipe card...");
//						btnMSR23.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipeAgain"))
					{
						btnFinishPrint.setVisibility(View.GONE);
						btnReprint.setVisibility(View.GONE);
						btnMSR23.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipesuccessfully"))
					{
						woosim.BTDisConnection();
						Intent intent = new Intent();
						intent.putExtra("obj", postPaymentDO);
						setResult(10000,intent);
						finish();
					}
					else
					{
						showDeviceLists(false);
					}
				}
			});
			btnNo.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					customDialog.dismiss();
					if(from.equalsIgnoreCase("cardswipe"))
					{
						setResult(-10000);
						btnFinish.performClick();
					}
					else if(from.equalsIgnoreCase("cardswipeAgain"))
					{
						woosim.BTDisConnection();
						setResult(-10000);
						finish();
					}
					else
					{
						showDeviceLists(false);
					}
				}
			});
			if(!customDialog.isShowing())
				customDialog.show();
		}
	}

	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage, firstBtnName, secondBtnName, from, true));
	}
	/** Method to Show the alert dialog **/
	public void showCustomDialog(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable)
	{
		runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage, firstBtnName, secondBtnName, from, isCancelable));
	}

	// This is  to show the loading progress dialog when some other functionality is taking place.
	class RunShowLoader implements Runnable
	{
		private String strMsg;
		private String title;

		public RunShowLoader(String strMsg, String title)
		{
			this.strMsg = strMsg;
			this.title = title;
		}

		@Override
		public void run()
		{
			try
			{
				if(progressdialog == null ||(progressdialog != null && !progressdialog.isShowing()))
				{
					progressdialog = ProgressDialog.show(WoosimPrinterActivity.this, title, strMsg);
				}
			}
			catch(Exception e)
			{progressdialog = null;}
		}
	}
	/** For hiding progress dialog (Loader ). **/
	public void hideLoader()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					if(progressdialog != null && progressdialog.isShowing())
						progressdialog.dismiss();
					progressdialog = null;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	/** This method is  to show the loading progress dialog when some other functionality is taking place. **/
	public void showLoader(String msg)
	{
		runOnUiThread(new RunShowLoader(msg, ""));
	}



	/**
	 * Payment Receipt Delivery
	 */
	public void printPaymentReceiptDelivery(String strType)
	{}


	@Override
	protected void onResume()
	{
		super.onResume();

//		if(CALLFROM ==CONSTANTOBJ.CARD_SWIPE)
//		{
//		   btnReprint.setVisibility(View.GONE);
//		}
	}
	private int getUnitfromUOM(float availCases,int uomFactor)
	{
		int units = 0;
		if((int)uomFactor > 0)
			units = (int)availCases/(int)uomFactor;
		return units;
	}


	@Override
	public void onBackPressed()
	{
		woosim.BTDisConnection();
		hideLoader();
		setResult(Activity.RESULT_OK);
		finish();
	}

	//===========================================newly added for food print===============================================
	private void printHeaderNewKwalityNew() {


		if ((trxHeaderDO != null && trxHeaderDO.Division == 2) || (objPaymentDO != null && objPaymentDO.Division == 2)) {
			//For Third Pard Brand
//			woosim.saveSpool(EUC_KR, "                     ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);

//			String formatForContact2 = "%1$-1.1s %2$-45.45s %3$-44.44s";
			woosim.saveSpool(EUC_KR, "\r\n", 0, true);
			woosim.saveSpool(EUC_KR, "\r\n                ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/logo.bmp");
			} catch (Exception e) {
				e.printStackTrace();
			}
			woosim.saveSpool(EUC_KR, "\r\n", 0, true);

			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/lefttpt.bmp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			woosim.saveSpool(EUC_KR, "", 1, false);
			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/righttpt.bmp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//For Third Pard Brand
//			woosim.saveSpool(EUC_KR, "                     ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
			String formatForContact2 = "%1$-1.1s %2$-45.45s %3$-44.44s";
			try {
//				woosim.saveSpool(EUC_KR, "\r\n", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
//				woosim.printBitmap(AppConstants.KwalityLogoPath + "/logo.bmp");

				woosim.saveSpool(EUC_KR, "\r\n", 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Registered Address", "Correspondence Address"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "TASTY BITE FOODSTUFF TRADING LLC", "TASTY BITE FOODSTUFF TRADING LLC"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Dubai, U.A.E. - P.O.Box 93895,", "c/o PURE ICE CREAM CO. LLC.,"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Phone- +97165332153 Fax- +97165339707", "Al Wasit Street, Industrial Area-2"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Email: Tastybite2016@gmail.com", "Sharjah, U.A.E. - P.O.Box 6172,"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Phone- +97165332153 Fax- +97165339707"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Email: Tastybite2016@gmail.com"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "TRN - 100053610000003"), 0, true);
				if (salesmanContact != null && !TextUtils.isEmpty(salesmanContact))//salesmanContact
					woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Contact : " + salesmanContact), 0, false);
				else
					woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Contact : " + ""), 0, false);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else
			{
			woosim.saveSpool(EUC_KR, "\r\n                ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);

			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/logo.bmp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			woosim.saveSpool(EUC_KR, "\r\n", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);

			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/leftkwality.bmp");
			} catch (IOException e) {
				e.printStackTrace();
			}
			woosim.saveSpool(EUC_KR, "", 1, false);
			try {
				woosim.printBitmap(AppConstants.KwalityLogoPath + "/rightkwality.bmp");
			} catch (IOException e) {
				e.printStackTrace();
			}
//			String formatForContact2	= "%1$-1.1s %2$-49.49s %3$-40.40s";
			String formatForContact2 = "%1$-1.1s %2$-45.45s %3$-44.44s";


				//					woosim.saveSpool(EUC_KR, "\r\n                ", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);
//					woosim.printBitmap(AppConstants.KwalityLogoPath + "/logo.bmp");
//					woosim.saveSpool(EUC_KR, "\r\n", 1, false);//woosim.saveSpool(EUC_KR, "\n", 1, false);

//				String formatForContact2 = "%1$-1.1s %2$-49.49s %3$-40.40s";
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Registered Address", "Correspondence Address"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "KWALITY INTERNATIONAL FOODSTUFF LLC.,", "KWALITY INTERNATIONAL FOODSTUFF LLC."), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Office No.301,King Faisal Bin Khalid Al Qasimi,", "Al Wasit Street, Industrial Area-2"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Al Shuwaihaen, Sharjah, U.A.E. - P.O.Box 6172.", "Sharjah, U.A.E. - P.O.Box 6172."), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Phone- +97165332153 Fax- +97165339707", "Phone- +97165332153 Fax- +97165339707"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "Email: foods@kwality.ae", "Email: foods@kwality.ae"), 0, true);
				woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "TRN - 100039468200003"), 0, true);
				if (salesmanContact != null && !TextUtils.isEmpty(salesmanContact))//salesmanContact
                    woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Contact : " + salesmanContact), 0, false);
                else
                    woosim.saveSpool(EUC_KR, String.format(formatForContact2, "", "", "Contact : " + " "), 0, false);


			}


		}

	//===========================================newly added for food print===============================================
	private void printHeaderNewKwality()
	{

		String formatForContact 	= "%1$-46.46s %2$-10.10s %3$-12.12s \r\n";
//		String formatInvoiceCopy 	= "%1$-34.34s %2$-20.20s  %3$-1.1s \n";
		try
		{
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/logo.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		woosim.saveSpool(EUC_KR, "          ", 1, false);
		try
		{
			woosim.printBitmap(AppConstants.KwalityLogoPath+"/food.bmp");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if(salesmanContact != null && !TextUtils.isEmpty(salesmanContact))//salesmanContact
			woosim.saveSpool(EUC_KR, String.format(formatForContact,"","Contact : ",salesmanContact),0, false);
		else
			woosim.saveSpool(EUC_KR, String.format(formatForContact,"","Contact : "," "),0, false);

	}
}