/**
 * 
 */
package com.winit.sfa.salesman;

import httpimage.HttpImageManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.winit.alseer.pinch.SignatureView;
import com.winit.alseer.salesman.adapter.ChequePaymentAdapter;
import com.winit.alseer.salesman.adapter.CurrencyNoteAdapter;
import com.winit.alseer.salesman.adapter.InvoiceAmountAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CashDenominationDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;
import com.winit.alseer.salesman.dataobject.ChequePaymentDetailDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PaymentCashDenominationDO;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PaymentHeaderDO;
import com.winit.alseer.salesman.dataobject.PaymentInvoiceDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.utilities.BitmapUtilsLatLang;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.kwalitysfa.salesman.MyApplicationNew;
import com.winit.kwalitysfa.salesman.R;

public class ReceivePaymentActivity extends BaseActivity implements LocationResult
{
	private LinearLayout llRecievePayment,llPagerTab,llSelectCheque,llSelectCash,llPaymentCash,llPaymentCheque;
	private Button btnConfirm, btnContinue;
	private ListView lvInvoiceAmount;
	private TextView tvTotalInvoiceAmt,  tvRemainingAmt, 
			tvPageIndication,tvClearSignature,tvScreenName;
	private ViewPager chequesPager;
	private ImageView tvAddAnotherCheque;
	private LinearLayout llCustomerSignature;
	private EditText edtAmount;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers;
	private Vector<ChequePaymentDetailDO> vecChequePayment;
	private ChequePaymentAdapter chequePaymentAdapter;
	
	private ImageView ivCurrencyNote, ivCurrencyAdd,ivDeleteCheque;
	private GridView gvCurrencyNotes;
	private TextView tvCancel, tvSave;
	private PopupWindow popwindow;
	private CurrencyNoteAdapter currencyNoteAdapter;
    private MyView myViewDriver;
    private Paint mPaint;
    private JourneyPlanDO mallsDetails;
	private float totalInvoiceAmount = 0.0f;
	private float totalAmountPaid = 0.0f;
	private float remainingInvoiceAmount= 0.0f;
	private float paidByCheque = 0.0f;
	private float paidAsCash= 0.0f;
	
	private Vector<CashDenominationDO> vecCashDenomination;
	private int currentPage = 1;
	private int totalPages =  1;
	private Vector<NameIDDo> vecBankDetails;
	private String  strReceiptNo = "",selectedAmount;
	private PaymentHeaderDO paymentHeaderDO;
	private boolean isPaymentDone,isFromArCollection = false,isFromOrderPreview=false;
	private String methodUsedToPay="";//this variable contains method by which invoice is paid so either cash, cheque or both
	private HashMap<CashDenominationDO, PaymentCashDenominationDO> hashUsedCashDenominations = new HashMap<CashDenominationDO, PaymentCashDenominationDO>();
	private boolean isSigned=false,isFromPayment;
	
	// Capture Image
	private ImageView ivCaptureImage,ivChequeImage;
	private String capturedImageFilePath;
	private Bitmap mBtBitmap = null;
	private Bitmap bitmapProcessed;
	private String lat = "", lang = "";
	private  Uri mCapturedImageURI;
	private static final int CAMERA_PIC_REQUEST = 2500;
	private LocationUtility locationUtility;
	private ListView lvCollectedAmt;
//	private PayMentAdapter payMentAdapter;
	private String payMode = "";
	private TextView tvCollectedAmt;
	private ImageView tvCashPaymode, tvChequePaymode;
	private LinearLayout llCash, llCheque;
	private String Invoice_Type = "";
	private TrxHeaderDO trxHeaderDO;
	private final String SYNC_PAYMENT = "SYNC_PAYMENT";
	private String uuid;
	private int Division = 0;
	private boolean frommenu;
	private String Receiptno;
	
	
	@Override
	public void initialize() 
	{
		llRecievePayment 		= (LinearLayout) inflater.inflate(R.layout.receive_payment_new2, null);
		llBody.addView(llRecievePayment,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		setTypeFaceRobotoNormal(llRecievePayment);
		isSigned=false;
		uuid = StringUtils.getUniqueUUID();
		locationUtility  = new LocationUtility(ReceivePaymentActivity.this);
		initialiseScreenControls();	
		if(getIntent().getExtras()!= null)
		{
			mallsDetails		= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			arrInvoiceNumbers  	= 	(ArrayList<PendingInvicesDO>) getIntent().getExtras().get("InvoiceNumbers");
			isFromArCollection 	=	 getIntent().getExtras().getBoolean("arcollection");
			isFromOrderPreview 	=	 getIntent().getExtras().getBoolean("isFromOrderPreview");
			float invoiceAmount	=	 getIntent().getExtras().getFloat("invoiceAmount");
			selectedAmount	   	= 	""+getIntent().getExtras().getFloat("selectedAmount");
			
			trxHeaderDO	   		= 	(TrxHeaderDO) getIntent().getExtras().get("trxHeaderDO");
			
			if(getIntent().hasExtra("paymode"))
				payMode		=	getIntent().getExtras().getString("paymode");
			
			
			if(selectedAmount.trim().equalsIgnoreCase("NaN"))
				selectedAmount  = 	""+invoiceAmount;
			
			if(getIntent().hasExtra("isFromPayment"))
				isFromPayment = getIntent().getExtras().getBoolean("isFromPayment");
			
			if(getIntent().hasExtra("Invoice_Type"))
				Invoice_Type = getIntent().getExtras().getString("Invoice_Type");
			
			if(getIntent().hasExtra(AppConstants.DIVISION))
				Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
			
			if(getIntent().hasExtra("fromMenu"))
				frommenu=getIntent().getExtras().getBoolean("fromMenu");
		}
		if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() > 0)
			sortInvoiceDOs();
			
		loadData();
		initializeInvoiceDetails();
		initializeChequePaymentDetails();
		initializeCashPaymentDetails();
		bindControls();
	}
	
	private void sortInvoiceDOs() 
	{
		Collections.sort(arrInvoiceNumbers, new Comparator<PendingInvicesDO>() {
		    @Override
		   public int compare(PendingInvicesDO s1, PendingInvicesDO s2) {
		    	return (s1.invoiceDate).compareToIgnoreCase(s2.invoiceDate);
		    }
		});
	}

	/**
	 * Initialise Screen Controls
	 */
	private void initialiseScreenControls() 
	{
		btnConfirm					= (Button) llRecievePayment.findViewById(R.id.btnConfirm);
		btnContinue					= (Button) llRecievePayment.findViewById(R.id.btnContinue);
		
		lvInvoiceAmount				= (ListView) llRecievePayment.findViewById(R.id.lvInvoiceAmount);
		tvScreenName		 		= (TextView) llRecievePayment.findViewById(R.id.tvScreenName);
		tvTotalInvoiceAmt			= (TextView) llRecievePayment.findViewById(R.id.tvTotalInvoiceAmt);
		tvAddAnotherCheque			= (ImageView) llRecievePayment.findViewById(R.id.tvAddAnotherCheque);
		tvRemainingAmt				= (TextView) llRecievePayment.findViewById(R.id.tvRemainingAmt);
		tvClearSignature			= (TextView) llRecievePayment.findViewById(R.id.tvClearSignature);
		edtAmount					= (EditText) llRecievePayment.findViewById(R.id.edtAmount);
		
		tvPageIndication			= (TextView) llRecievePayment.findViewById(R.id.tvPageIndication);
	    chequesPager				= (ViewPager) llRecievePayment.findViewById(R.id.pagerPayement);
		llCustomerSignature			= (LinearLayout) llRecievePayment.findViewById(R.id.llCustonerSignature);
		
		ivCaptureImage				= (ImageView) llRecievePayment.findViewById(R.id.ivCaptureImage);
		ivDeleteCheque				= (ImageView) llRecievePayment.findViewById(R.id.ivDeleteCheque);
		ivChequeImage				= (ImageView) llRecievePayment.findViewById(R.id.ivChequeImage);
		llPagerTab 					=   (LinearLayout) llRecievePayment.findViewById(R.id.llPagerTab);
		
		lvCollectedAmt				= (ListView) llRecievePayment.findViewById(R.id.lvCollectedAmt);
		
		llSelectCheque				= (LinearLayout) llRecievePayment.findViewById(R.id.llSelectCheque);
		llSelectCash				= (LinearLayout) llRecievePayment.findViewById(R.id.llSelectCash);	
		llPaymentCash				= (LinearLayout) llRecievePayment.findViewById(R.id.llPaymentCash);
		llPaymentCheque				= (LinearLayout) llRecievePayment.findViewById(R.id.llPaymentCheque);	
		tvCollectedAmt				= (TextView) llRecievePayment.findViewById(R.id.tvCollectedAmt);
		tvCashPaymode				= (ImageView) llRecievePayment.findViewById(R.id.tvCashPaymode);
		tvChequePaymode				= (ImageView) llRecievePayment.findViewById(R.id.tvChequePaymode);
		
		llCash						= (LinearLayout) llRecievePayment.findViewById(R.id.llCash);
		llCheque					= (LinearLayout) llRecievePayment.findViewById(R.id.llCheque);
		
	
		setTypeFaceRobotoNormal(llRecievePayment);
		tvScreenName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnConfirm.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvAddAnotherCheque.setVisibility(View.GONE);
		tvAddAnotherCheque.setEnabled(false);
		tvAddAnotherCheque.setClickable(false);
	}
	
	
	private void initializeInvoiceDetails()
	{
		InvoiceAmountAdapter listAdapter = new InvoiceAmountAdapter(ReceivePaymentActivity.this,arrInvoiceNumbers);
		lvInvoiceAmount.setAdapter(listAdapter);	
		for(int i = 0;i < arrInvoiceNumbers.size(); i++)
		{
			float amount = StringUtils.getFloat(arrInvoiceNumbers.get(i).balance);
			totalInvoiceAmount += amount;
			
			if(amount > 0)
				totalAmountPaid +=amount;
		}
		
		totalInvoiceAmount  = StringUtils.getFloat(decimalFormat.format(totalInvoiceAmount));
		totalAmountPaid		= StringUtils.getFloat(decimalFormat.format(totalAmountPaid));
		tvTotalInvoiceAmt.setText(amountFormate.format(totalInvoiceAmount));
//		tvCollectedAmt.setText(amountFormate.format(paidAsCash)+"");
		tvCollectedAmt.setText(amountFormate.format(paidAsCash + paidByCheque)+"");
		calculateRemainingAmount(paidByCheque, paidAsCash);
	}
	
	private void initializeChequePaymentDetails()
	{
		vecChequePayment = new Vector<ChequePaymentDetailDO>();
		ChequePaymentDetailDO objchePaymentDetailDO = new ChequePaymentDetailDO();
		vecChequePayment.add(objchePaymentDetailDO);
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				 vecBankDetails = new CommonDA().getAllBanksNew();
				 runOnUiThread(new Runnable() {
					
					@Override
					public void run() 
					{
						chequePaymentAdapter = new ChequePaymentAdapter(ReceivePaymentActivity.this,vecChequePayment, vecBankDetails, false);
						chequesPager.setAdapter(chequePaymentAdapter);
						
						if(!payMode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE))
						{
							chequePaymentAdapter.setEnabled(false);
							chequesPager.setEnabled(false);
							chequesPager.setClickable(false);
						}
//						payMentAdapter = new PayMentAdapter(ReceivePaymentActivity.this, vecChequePayment);
//						lvCollectedAmt.setAdapter(payMentAdapter);
						refreshPageController();
						//This method is written for paymode options
						disableOrEnableControls();
					}
				});
			}
		}).start();
		
	}
	
	public void disableTouchTheft(View view) {
	    view.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View view, MotionEvent motionEvent) {
	            view.getParent().requestDisallowInterceptTouchEvent(true);
	            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
	            case MotionEvent.ACTION_UP:
	                view.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }
	            return false;
	        }
	    });
	    
	}
	
	private void initializeCashPaymentDetails()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				vecCashDenomination = CashDenominationDA.getCashDenominations();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						downloadFullImage();
						//This method is written for paymode options
						disableOrEnableControls();
					}
				});
			}
		}).start();
	}
	
	
	/**
	 * 
	 */
	private void loadData() 
	{
		tvPageIndication.setText(""+currentPage+"/"+totalPages);
		String actualDateFormat  = CalendarUtils.getCurrentDateAsStringInFormat();
		String actualDate[]      = actualDateFormat.split("-");
		String reqDateFormat     = actualDate[1]+" "+actualDate[0]+CalendarUtils.getDateNotation(StringUtils.getInt(actualDate[0]))+" "+actualDate[2];
	}
	
	private void bindControls()
	{
		
		edtAmount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) 
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) 
			{
				
			}
			
			@Override
			public void afterTextChanged(Editable s) 
			{
				String cashPaymentAmount = edtAmount.getText().toString();
				paidAsCash  = StringUtils.getFloat(cashPaymentAmount);
				float totalAmount = StringUtils.getFloat(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(paidAsCash))+StringUtils.getFloat(decimalFormat.format(paidByCheque)))));
//				if( totalAmount > totalInvoiceAmount)
//				if((totalAmount - totalInvoiceAmount) > 1)
//				{
//					showToast("Total Amount Exceeded the Invoice Amount");
//					paidAsCash = totalInvoiceAmount-(remainingInvoiceAmount+paidByCheque);
//					tvCollectedAmt.setText(paidAsCash+"");
//					edtAmount.setText(paidAsCash+"");
//				}
//				else
				{
					remainingInvoiceAmount  = totalInvoiceAmount - totalAmount;
					
					tvRemainingAmt.setText(amountFormate.format(remainingInvoiceAmount)+"");
					tvCollectedAmt.setText(amountFormate.format(paidAsCash+paidByCheque)+"");
				}
				
				remainingInvoiceAmount  = StringUtils.getFloat(decimalFormat.format(remainingInvoiceAmount));
				
				LogUtils.debug("TotalCashPayment", ""+remainingInvoiceAmount);
			}
		});
	
		
		
		myViewDriver  = new MyView(this, true);
		myViewDriver.setDrawingCacheEnabled(true);
		myViewDriver.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , (int)(180 * px)));
		myViewDriver.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		llCustomerSignature.addView(myViewDriver);
		
		mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
		
		
		//OnClicks 
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SetDisableButton(btnConfirm);
				
				isSigned = true;
				showLoader(getString(R.string.Validating_payment));
				validDatePayment(new PaymentValidationListner() {
					@Override
					public void valid(int errorCode) {
						hideLoader();
						switch (errorCode) {
						case ERROR_ENTERED_MORE_SERIAL_NUMBER:
						case ERROR_NOT_ENTERED_ALL_SERIAL_NUMBER:
							showCustomDialog(ReceivePaymentActivity.this,"Alert","Please check the denomination entered ",getString(R.string.OK),null,"");
							break;
						case ERROR_TRYINGFOR_LESSAMOUNT:
//						case ERROR_TRYINGFOR_MOREAMOUNT:
							showCustomDialog(ReceivePaymentActivity.this,"Alert","Please adjust invoice amount",getString(R.string.OK),null,"");
							break;
						case ERROR_CHEQUE_DETAIL_NOT_ENTERED:
							showCustomDialog(ReceivePaymentActivity.this,"Alert","Please enter complete cheque details",getString(R.string.OK),null,"");
							break;
						case ERROR_NOT_SIGNED:
							showCustomDialog(ReceivePaymentActivity.this, getString(R.string.warning), "Customer's signature is mandatory.", getString(R.string.OK), null, "");
							break;
						case ERROR_UNKNOWN:
							showCustomDialog(ReceivePaymentActivity.this,"Alert","Please check payment details ",getString(R.string.OK),null,"");
							break;
						case VALID_PAYMNET:
							showSignatureDialog();
							
							break;
						default:
							break;
						}
					}
				});
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
//				showCustomDialog(ReceivePaymentActivity.this, "Successful !", "Payment\n Successful !", "OK", null, "ReceivePayment");
				showPaymentCompletePopup();
			}
		});
		
	chequesPager.setOnPageChangeListener(new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) 
		{
			currentPage = position + 1;
			tvPageIndication.setText("- "+currentPage+"/"+totalPages);
			
			refreshPageController();
			
			if(position < vecChequePayment.size() && !vecChequePayment.get(position).filePath.equalsIgnoreCase(""))
			{
				ivChequeImage.setVisibility(View.VISIBLE);
				final Uri uri = Uri.parse(vecChequePayment.get(position).filePath);
				if (uri != null) 
				{
					Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivChequeImage,vecChequePayment.get(position).filePath));
					if (bitmap != null) 
					{
						ivChequeImage.setImageBitmap(bitmap);
					}
				}
			}
			else
				ivChequeImage.setVisibility(View.GONE);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) 
		{
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) 
		{
			
		}
	  });
		
		
		tvAddAnotherCheque.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(validateChequePayment())
				{
					ChequePaymentDetailDO objchePaymentDetailDO = new ChequePaymentDetailDO();
					vecChequePayment.add(objchePaymentDetailDO);
					chequePaymentAdapter.refresh(vecChequePayment);
//					payMentAdapter.refresh(vecChequePayment);
					
					totalPages ++;
					chequesPager.setCurrentItem(chequesPager.getCurrentItem() + 1);
					tvPageIndication.setText("- "+(chequesPager.getCurrentItem() + 1)+"/"+totalPages);
					
					refreshPageController();
				}
				else
					showCustomDialog(ReceivePaymentActivity.this, "Alert", "Please enter complete cheque details", "ok", "", "Add Cheque");
			}
		});
		
		tvClearSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				isSigned=false;
				if(myViewDriver != null)
					myViewDriver.clearCanvas();
			}
		});
		
		ivCaptureImage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				startCamera();
			}
		});
		
		ivDeleteCheque.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v) 
			{
				deleteCheque();
			}
		});
		
//		llSelectCash.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				if(payMode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE))
//				{
//					payMode = AppConstants.PAYMENT_NOTE_CASH;
//					
//					tvCashPaymode.setBackgroundResource(R.drawable.paymode_checked);
//					tvChequePaymode.setBackgroundResource(R.drawable.paymode_unchecked);
//					lvCollectedAmt.setVisibility(View.INVISIBLE);
//					ivChequeImage.setImageBitmap(null);
//					ivChequeImage.setVisibility(View.INVISIBLE);
//					edtAmount.setEnabled(true);
//					tvAddAnotherCheque.setEnabled(false);
//					ivCaptureImage.setEnabled(false);
//					
////					vecChequePayment.clear();
//					
////					vecChequePayment = new Vector<ChequePaymentDetailDO>();
////					ChequePaymentDetailDO chequePaymentDetailDO = new ChequePaymentDetailDO();
////					vecChequePayment.add(chequePaymentDetailDO);
////					chequePaymentAdapter.refresh(vecChequePayment);
////					payMentAdapter.refresh(vecChequePayment);
////					tvCollectedAmt.setText("");
//					
////					paidAsCash = 0.0f;
////					paidByCheque = 0.0f;
//					
//					calculateRemainingAmount(paidByCheque, paidAsCash);
//					
////					tvRemainingAmt.setText(decimalFormat.format(totalInvoiceAmount)+"");
//					tvCollectedAmt.setText(amountFormate.format(paidAsCash + paidByCheque) +"");
//					
////					chequePaymentAdapter.setEnabled(false);
////					chequesPager.setEnabled(false);
////					chequesPager.setClickable(false);
//					
//					llCash.setAlpha(1);
//					llCheque.setAlpha(0.5f);
//				}
//			}
//		});
		
//		llSelectCheque.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) 
//			{
//				if(payMode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CASH))
//				{
//					payMode = AppConstants.PAYMENT_NOTE_CHEQUE;
//					
//					tvCashPaymode.setBackgroundResource(R.drawable.paymode_unchecked);
//					tvChequePaymode.setBackgroundResource(R.drawable.paymode_checked);
//					lvCollectedAmt.setVisibility(View.VISIBLE);
//					ivChequeImage.setVisibility(View.VISIBLE);
//					edtAmount.setEnabled(false);
//					tvAddAnotherCheque.setEnabled(true);
//					ivCaptureImage.setEnabled(true);
//					
////					chequesPager.setEnabled(true);
////					chequePaymentAdapter.setEnabled(true);
//					
////					edtAmount.setText("");
////					tvCollectedAmt.setText("");
////					vecCashDenomination.clear();
////					
////					vecCashDenomination = new Vector<CashDenominationDO>();
////					vecCashDenomination = CashDenominationDA.getCashDenominations();
////					
////					paidAsCash = 0.0f;
////					paidByCheque = 0.0f;
//					calculateRemainingAmount(paidByCheque, paidAsCash);
//					
//
//					llCash.setAlpha(0.5f);
//					llCheque.setAlpha(1);
//					
////					tvRemainingAmt.setText(decimalFormat.format(totalInvoiceAmount)+"");
//					tvCollectedAmt.setText(amountFormate.format(paidAsCash + paidByCheque)+"");
//				}
//			}
//		});
	}
	public void insertPayment(){
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				double totalPaid = (paidAsCash + paidByCheque)/* + new PaymentDetailDA().getCustomerExcess(mallsDetails.site)*/;
				strReceiptNo = new PaymentDetailDA().getReceiptNo(preference.getStringFromPreference(Preference.SALESMANCODE, ""), preference,Division);
				if(paymentHeaderDO==null && strReceiptNo != null && !strReceiptNo.equalsIgnoreCase(""))
				{
					paymentHeaderDO						= 	new PaymentHeaderDO();
					paymentHeaderDO.appPaymentId 		=  uuid;
					paymentHeaderDO.rowStatus 			=  "1";
					paymentHeaderDO.receiptId 			=  strReceiptNo;
					paymentHeaderDO.preReceiptId 		=  strReceiptNo;
					paymentHeaderDO.paymentDate 		=  CalendarUtils.getCurrentDateTime();
					paymentHeaderDO.siteId 				=  mallsDetails.site;
					paymentHeaderDO.empNo 				=  preference.getStringFromPreference(Preference.EMP_NO, "");
					paymentHeaderDO.amount 				=  ""+(paidAsCash+paidByCheque);/*totalInvoiceAmount;*/
					paymentHeaderDO.currencyCode 		=  curencyCode+"";
					paymentHeaderDO.rate 				=  "1";
					paymentHeaderDO.visitCode 			=  ""+mallsDetails.VisitCode;
					paymentHeaderDO.paymentStatus 		=  "0";
					paymentHeaderDO.status 				=  "0";
					paymentHeaderDO.salesOrgCode		=   preference.getStringFromPreference(Preference.ORG_CODE,"");
					paymentHeaderDO.appPayementHeaderId	= 	paymentHeaderDO.appPaymentId;
					paymentHeaderDO.Division			= 	Division;
					if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() == 1 && arrInvoiceNumbers.get(0).isNewleyAdded)
						paymentHeaderDO.paymentType 		=	"OnAccount";
					else
						paymentHeaderDO.paymentType 		=	"Collection";
					paymentHeaderDO.salesmanCode 		=	mallsDetails.salesmanCode;
					paymentHeaderDO.vehicleNo 			=	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
					
					if(paymentHeaderDO.vehicleNo == null || paymentHeaderDO.vehicleNo.equalsIgnoreCase(""))
						paymentHeaderDO.vehicleNo = new VehicleDA().getVechicleNO(paymentHeaderDO.empNo);
					
					//Customer Signature
					Bitmap bitmap 					= getBitmap(customerSignature);
					ByteArrayOutputStream stream 	= new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					paymentHeaderDO.customerSignature = Base64.encodeBytes(stream.toByteArray());
					storeImage(bitmap, AppConstants.CUSTOMER_SIGN);
					
					if(paidAsCash>0)
						paymentHeaderDO.vecPaymentDetails.add(getCashDetail());
					if(paidByCheque>0)
						paymentHeaderDO.vecPaymentDetails.addAll(getChequeDetail());
				
					sortAllInvoiceByAsc();
					for(PendingInvicesDO objInvicesDO : arrInvoiceNumbers)
					{
						PaymentInvoiceDO paymentInvoiceDO = new PaymentInvoiceDO();
						
						paymentInvoiceDO.RowStatus 		= 	"1";
						paymentInvoiceDO.ReceiptId		= 	strReceiptNo;
						Receiptno                        = strReceiptNo;
						paymentInvoiceDO.TrxCode		= 	/*strReceiptNo*/objInvicesDO.invoiceNo;
						
						if(objInvicesDO.invoiceDate.contains(CalendarUtils.getOrderPostDate()))
						{
							if(objInvicesDO.TRX_TYPE.equalsIgnoreCase(String.valueOf(TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())/*AppConstants.RETURNORDER*/))
								paymentInvoiceDO.TrxType		= 	"Return Invoice";
							else
								paymentInvoiceDO.TrxType		= 	"Normal Invoice";
						}
						else
							paymentInvoiceDO.TrxType		= 	"+ Invoice";
							
						paymentInvoiceDO.CurrencyCode	= 	curencyCode+"";
						paymentInvoiceDO.Rate			= 	"1";
						paymentInvoiceDO.PaymentStatus	= 	"0";
						paymentInvoiceDO.PaymentType	= 	methodUsedToPay;
						paymentInvoiceDO.CashDiscount 	= 	"0";
						
						if(objInvicesDO.isNewleyAdded)
						{
//							totalPaid += Math.abs(StringUtils.getFloat(objInvicesDO.balance));
							paymentInvoiceDO.Amount = ""+(-totalPaid);
						}
						else
						{
							if (objInvicesDO.TRX_TYPE.equalsIgnoreCase(String.valueOf(TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())))
							{
								totalPaid += Math.abs(StringUtils.getFloat(objInvicesDO.balance));
								paymentInvoiceDO.Amount			= 	objInvicesDO.balance;
							}
							else
							{
								if(totalPaid > StringUtils.getFloat(objInvicesDO.balance))
								{
									totalPaid -= StringUtils.getFloat(objInvicesDO.balance);
									paymentInvoiceDO.Amount			= 	objInvicesDO.balance;
								}
								else if(totalPaid <= StringUtils.getFloat(objInvicesDO.balance))
								{
									paymentInvoiceDO.Amount			=	""+totalPaid;
									objInvicesDO.balance			=	""+totalPaid;
									totalPaid = 0;
//								paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
								}
							}
						}
						paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
					}
					
//					new PaymentDetailDA().updateCustomerLimit(mallsDetails.site, totalPaid);
					if(totalPaid > 0)
					{
						new CustomerDetailsDA().insertCurrentInvoice(mallsDetails.site,decimalFormat.format(-totalPaid), paymentHeaderDO.receiptId,Division);
					}
					boolean isInserted = new PaymentDetailDA().insertPaymentDetails(paymentHeaderDO , preference.getStringFromPreference(Preference.SALESMANCODE, ""));
					if(isInserted)
					{
						if(trxHeaderDO != null)
							postOrder(trxHeaderDO);
						
						if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() > 0)
							isPaymentDone = new PaymentDetailDA().updatePaymentStatus(arrInvoiceNumbers, "D");
						
						new CustomerDA().updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");   //vinod need to commented
						TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
						trxLogDetailsDO.Amount = StringUtils.getFloat(decimalFormat.format(totalInvoiceAmount));
						trxLogDetailsDO.CustomerCode = mallsDetails.site;
						trxLogDetailsDO.CustomerName = mallsDetails.siteName;
						trxLogDetailsDO.TrxType = AppConstants.COLLECTIONS;
						
						trxLogDetailsDO.columnName =  TrxLogHeaders.COL_TOTAL_COLLECTIONS;
						trxLogDetailsDO.DocumentNumber = paymentHeaderDO.receiptId;
						
						trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
						trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
						trxLogDetailsDO.IsJp  = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetails.site,trxLogDetailsDO.Date)?"True":"False";
						new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);
						
						updatePaymentStatus();
						if(isNetworkConnectionAvailable(ReceivePaymentActivity.this))
						{
							if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)){
								if(!isFromOrderPreview)
									uploadData();
							}
						}
					}
				}
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						hideLoader();
						if(isPaymentDone){
							btnConfirm.setClickable(false);
							btnConfirm.setEnabled(false);
							btnConfirm.setVisibility(View.GONE);
							btnContinue.setVisibility(View.VISIBLE);
							showPaymentCompletePopup();
//							showCustomDialog(ReceivePaymentActivity.this, "Successful !", "Payment\n Successful !", "OK", null, "ReceivePayment");
							
						}
						else
							showCustomDialog(ReceivePaymentActivity.this, getString(R.string.warning), "Payment sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
					}
				});
			}
		}).start();
	}
	
	private void postOrder(TrxHeaderDO trxHeaderDO)
	{
		int TRX_TYPE = trxHeaderDO.trxType;
		trxHeaderDO.status = 0;
		trxHeaderDO.trxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
		trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
		
		ArrayList<TrxHeaderDO> arrayList = new ArrayList<TrxHeaderDO>();
		arrayList.add(trxHeaderDO);
		/*
		 * It should call updatesavedorder suppose if order is saved aso in both cases
		 * 
		 * the last boolean value to know whether it is from orderpreview or payment screen 
		 */
		if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER())
		{
			trxHeaderDO.trxCode = ""+new OrderDA().updatePresellerOrder(arrayList,preference.getStringFromPreference(preference.USER_ID, ""),
					preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER));
		//Added by Abhijit for Preseller finalize =================================================	
//			arrInvoiceNumbers.get(0).invoiceNo=trxHeaderDO.trxCode;
//			if(!TextUtils.isEmpty(Receiptno) && !TextUtils.isEmpty(trxHeaderDO.trxCode))
//			new PaymentDetailDA().updateInVoiceNumberforPresellerFinalize(Receiptno , trxHeaderDO.trxCode);
		//===========================================================================================
			
		}
		else
			/*trxHeaderDO.trxCode = ""+*/new OrderDA().updateSavedOrder(arrayList);
		
		if(!TextUtils.isEmpty(trxHeaderDO.trxCode))
		{
			new CustomerDA().updateCustomerVisit(trxHeaderDO);
			//Need to update log also
			new OrderDA().deleteIfExistsInCart(trxHeaderDO.clientCode);
		
			new OrderDA().updateInventory_WhileOrder(trxHeaderDO.arrTrxDetailsDOs, 
					CalendarUtils.getOrderPostDate());
			new OrderDA().updateOrderStatus(trxHeaderDO.trxCode);
		}
		
		updateOrderStatus(trxHeaderDO);
	}
	
	private void updateOrderStatus(TrxHeaderDO trxHeaderDO)
	{
		StatusDO statusDO = new StatusDO();
		statusDO.UUid = "";
		statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
		statusDO.Customersite = mallsDetails.site;
		statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
		statusDO.Visitcode = mallsDetails.VisitCode;
		statusDO.JourneyCode = mallsDetails.JourneyCode;
		statusDO.Status = "0";
		statusDO.Action = AppConstants.Action_CheckIn;
		
		if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
		{
			statusDO.Type = AppConstants.Type_SalesOrder;
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
		{
			statusDO.Type = AppConstants.Type_AdvancedOrder;
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
		{
			statusDO.Type = AppConstants.Type_PresalesOrder;
		}
		else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
		{
			statusDO.Type = AppConstants.Type_ReturnOrder;
		}
		new StatusDA().insertOptionStatus(statusDO);
		
		TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
		trxLogDetailsDO.Amount = StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalAmount))-StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalDiscountAmount));
		trxLogDetailsDO.CustomerCode = mallsDetails.site;
		trxLogDetailsDO.CustomerName = mallsDetails.siteName;
		trxLogDetailsDO.TrxType = AppConstants.INVOICES;
		trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
		trxLogDetailsDO.IsJp  = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetails.site,trxLogDetailsDO.Date)?"True":"False";
		String colName = TrxLogHeaders.COL_TOTAL_SALES;
		trxLogDetailsDO.columnName = colName;
		trxLogDetailsDO.DocumentNumber = trxHeaderDO.trxCode;
		trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();
		
		new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);
	}
	
	private void sortAllInvoiceByAsc() 
	{
		for (int j = 0; j < arrInvoiceNumbers.size(); j++) 
		{
			if(arrInvoiceNumbers.get(j).isNewleyAdded)
			{
				arrInvoiceNumbers.get(j).balance = ""+(-StringUtils.getFloat(arrInvoiceNumbers.get(j).balance));				
			}
		}
		
		Collections.sort(arrInvoiceNumbers, new Comparator<PendingInvicesDO>() {
		    @Override
		   public int compare(PendingInvicesDO s1, PendingInvicesDO s2) {
		    	return (StringUtils.getFloat(s1.balance)<StringUtils.getFloat(s2.balance)) ? -1
		    			: ((StringUtils.getFloat(s1.balance)>StringUtils.getFloat(s2.balance)) ? 1 : 0);
		    }
		});
	}
	private void showCurrencyNoteDetailPopup(final CashDenominationDO cashDenominationDO) 
	{
		View popupview = LayoutInflater.from(ReceivePaymentActivity.this).inflate(R.layout.currency_note_detail, null);
		initialisePopUpControls(popupview,cashDenominationDO.Name);
		initialisePopUpView(cashDenominationDO);
		
		popwindow = new PopupWindow(popupview, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
		popwindow.setOutsideTouchable(false);
		popwindow.setFocusable(true);
		popwindow.setBackgroundDrawable(new BitmapDrawable());
		popwindow.showAtLocation(llRecievePayment, Gravity.CENTER, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		popwindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if(cashDenominationDO.count==0)
					hashUsedCashDenominations.remove(cashDenominationDO);
			}
		});
	}
	/**
	 * 
	 */
	PaymentCashDenominationDO paymentCashDenominationDO =null;
	private void initialisePopUpView(final CashDenominationDO cashDenominationDO) 
	{
		paymentCashDenominationDO =null;
		if(hashUsedCashDenominations.containsKey(cashDenominationDO))
			paymentCashDenominationDO = hashUsedCashDenominations.get(cashDenominationDO);
		else{
			paymentCashDenominationDO = new PaymentCashDenominationDO();
			hashUsedCashDenominations.put(cashDenominationDO, paymentCashDenominationDO);
		}
		
		currencyNoteAdapter = new CurrencyNoteAdapter(ReceivePaymentActivity.this,paymentCashDenominationDO.arrDenominationDetail);
		gvCurrencyNotes.setAdapter(currencyNoteAdapter);
		
		ivCurrencyAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				for (int i = 0; i < 2; i++) 
					paymentCashDenominationDO.arrDenominationDetail.add("");
				currencyNoteAdapter.refresh(paymentCashDenominationDO.arrDenominationDetail);
			}
		});
		
		tvCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				popwindow.dismiss();
				if(cashDenominationDO.count==0)
					hashUsedCashDenominations.remove(cashDenominationDO);
			}
		});
		
		
		tvSave.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					paymentCashDenominationDO.arrDenominationDetail = filterSerialNumber(currencyNoteAdapter.getDetails());
					cashDenominationDO.count = paymentCashDenominationDO.arrDenominationDetail.size();
					if(cashDenominationDO.count==0)
						hashUsedCashDenominations.remove(cashDenominationDO);
					isDuplicateSrialNumbersExist(new PaymentValidationListner() {
						@Override
						public void valid(int errorCode) {
							if(errorCode==ERROR_ENTERED_DUPLICATE_SERIAL_NUMBER){
								showCustomDialog(ReceivePaymentActivity.this,"Alert","Entered serial number already exists",getString(R.string.OK),null,"");
							}else{
								popwindow.dismiss();
							}
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			
			}
		});
		
	}
	
	/**
	 * @param popupview
	 */
	private void initialisePopUpControls(View popupview,String path) 
	{
		ivCurrencyNote = (ImageView) popupview.findViewById(R.id.ivCurrencyNote);
		gvCurrencyNotes = (GridView) popupview.findViewById(R.id.gvCurrencyNotes);
		ivCurrencyAdd = (ImageView) popupview.findViewById(R.id.ivCurrencyAdd);
		tvCancel = (TextView) popupview.findViewById(R.id.tvCancel);
		tvSave = (TextView) popupview.findViewById(R.id.tvSave);
		
		
		 String imageURL = "cashfullimages/"+"full_"+path+".png";
		 
		//String imageURL = path.replace("../", ServiceURLs.IMAGE_GLOBAL_URL);
		
		final Uri uri = Uri.parse(imageURL);
//		ivCurrencyNote.setImageResource(defaultImage);
		if (uri != null) {
			Bitmap bitmap = getHttpImageManager().loadImage(
					new HttpImageManager.LoadRequest(uri, ivCurrencyNote,imageURL));
			if (bitmap != null) {
				ivCurrencyNote.setImageBitmap(bitmap);
			}
		}	
		
	}

	private void calculateRemainingAmount(float TotalChequePayment ,float TotalPaidCashPayment)
	{
		remainingInvoiceAmount  = totalInvoiceAmount - (TotalChequePayment + TotalPaidCashPayment);
		remainingInvoiceAmount  = StringUtils.getFloat(decimalFormat.format(remainingInvoiceAmount));
		tvRemainingAmt.setText(amountFormate.format(remainingInvoiceAmount)+"");
	}
	
	float recentCheckAmount = 0.0f;
	public void getChequeAmount(EditText edtAmount) 
	{
		paidByCheque = 0;
		for(int i =0;i<vecChequePayment.size();i++)
		{
			paidByCheque += StringUtils.getFloat(vecChequePayment.get(i).Amount);
		}
		paidByCheque = StringUtils.getFloat(decimalFormat.format(paidByCheque));
		float totalAmount = StringUtils.getFloat(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(paidAsCash))+StringUtils.getFloat(decimalFormat.format(paidByCheque)))));
//		if( totalAmount > totalInvoiceAmount)
//		{
//			showToast("Total Amount Exceeded the Invoice Amount");
//			paidByCheque = totalInvoiceAmount-(remainingInvoiceAmount+paidAsCash);
//			edtAmount.setText("");
//		}
//		else
		{
			recentCheckAmount = StringUtils.getFloat(edtAmount.getText().toString());
			remainingInvoiceAmount  = totalInvoiceAmount - totalAmount;
			tvRemainingAmt.setText(amountFormate.format(remainingInvoiceAmount)+"");
			tvCollectedAmt.setText(amountFormate.format(totalAmount)+"");
		}
		remainingInvoiceAmount  = StringUtils.getFloat(decimalFormat.format(remainingInvoiceAmount));
	}
	
	public class MyView extends View 
	{
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        int width = 480, height = 800;
        private boolean isDriver = false;
        
        @SuppressWarnings("deprecation")
		public MyView(Context c, boolean isDriver)
        {
            super(c);
            Display display = 	getWindowManager().getDefaultDisplay(); 
            width 			= 	display.getWidth();
            height 			= 	display.getHeight();
            this.isDriver = isDriver; 
            if(mBitmap != null)
            	mBitmap.recycle();
            
            mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas 		= 	new Canvas(mBitmap);
            mPath 			= 	new Path();
            mBitmapPaint	= 	new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(2);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y)
        {
            mPath.reset();
            mPath.moveTo(x, y);
            mPath.addCircle(x, y,(.3f),Direction.CW);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) 
        {
          float dx = Math.abs(x - mX);
          float dy = Math.abs(y - mY);
          if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
          {
           mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
           mX = x;
           mY = y;
          }
	    }
	    private void touch_up()
	    {
	         mPath.lineTo(mX, mY);
	         mCanvas.drawPath(mPath, mPaint);
	         mPath.reset();
	         isSigned = true;
	     }
        
	    public void clearCanvas()
	    {
	    	mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
	        mCanvas 		= 	new Canvas(mBitmap);
	    	invalidate();
	    }
	    
        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
             x = event.getX();
             y = event.getY();
            
            switch (event.getAction()) 
            {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                    
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        	return true;
       }
	}
	
	private PaymentDetailDO getCashDetail()
	{
		PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
		paymentDetailDO.RowStatus 		= 	"1";
		paymentDetailDO.ReceiptNo		= 	strReceiptNo;
		paymentDetailDO.LineNo			= 	"1";
		paymentDetailDO.PaymentTypeCode	= 	PaymentDetailDO.getPaymentTypeCash();
		paymentDetailDO.BankCode		= 	"";
		paymentDetailDO.ChequeDate		= 	"";
		paymentDetailDO.ChequeNo		= 	"";
		paymentDetailDO.CCNo			= 	"";
		paymentDetailDO.CCExpiry		= 	"";
		paymentDetailDO.PaymentStatus	= 	"0";
		paymentDetailDO.PaymentNote		= 	PaymentDetailDO.getPaymentTypeCash();
		paymentDetailDO.UserDefinedBankName= "";
		paymentDetailDO.Status			= 	"0";
//		if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() == 1 && arrInvoiceNumbers.get(0).isNewleyAdded)
//			paymentDetailDO.Amount			= 	"0";
//		else
			paymentDetailDO.Amount			= 	""+paidAsCash;
			paymentDetailDO.invoiceAmount	= 	""+paidAsCash;
		paymentDetailDO.arrayList		=	new ArrayList<PaymentCashDenominationDO>();
		Set<CashDenominationDO> setUsedForCash 		=	hashUsedCashDenominations.keySet();
		for(CashDenominationDO cashPaymentDenominationUsed :setUsedForCash){
			PaymentCashDenominationDO paymentCashDenominationDO = hashUsedCashDenominations.get(cashPaymentDenominationUsed);
			paymentCashDenominationDO.cashDenominationCode = cashPaymentDenominationUsed.CashDenamationCode;
			paymentCashDenominationDO.totalAmount = cashPaymentDenominationUsed.count;
			paymentDetailDO.arrayList.add(paymentCashDenominationDO);
		}
		methodUsedToPay					=   PaymentDetailDO.getPaymentTypeCash();
		
		return paymentDetailDO;
	}
	
	private Vector<PaymentDetailDO> getChequeDetail() 
	{
		Vector<PaymentDetailDO> vecDetails = new Vector<PaymentDetailDO>();
		int lineNo = 2;
		vecChequePayment = chequePaymentAdapter.getCheckPaymentDetails();
		
		if(vecChequePayment!=null)
		{
			for(ChequePaymentDetailDO chequePaymentDetailDO:vecChequePayment)
			{
				PaymentDetailDO paymentDetailDO =   new PaymentDetailDO();
				paymentDetailDO.RowStatus 		= 	"1";
				paymentDetailDO.ReceiptNo		= 	strReceiptNo;
				paymentDetailDO.LineNo			= 	lineNo+"";
				paymentDetailDO.PaymentTypeCode	= 	PaymentDetailDO.getPaymentTypeCheque();
				paymentDetailDO.BankCode		= 	chequePaymentDetailDO.BankID;
				paymentDetailDO.ChequeImagePath	= 	chequePaymentDetailDO.filePath;
				
				if(!chequePaymentDetailDO.BankName.equalsIgnoreCase("Other"))
					paymentDetailDO.BankName	= 	chequePaymentDetailDO.BankName;
				else
					paymentDetailDO.BankName	= 	chequePaymentDetailDO.BankName;
				
				paymentDetailDO.branchName	    = 	""+chequePaymentDetailDO.BranchName;
				
				paymentDetailDO.ChequeDate		= 	chequePaymentDetailDO.Date;
				paymentDetailDO.ChequeNo		= 	chequePaymentDetailDO.ChequeNumber;
				paymentDetailDO.CCNo			= 	"";
				paymentDetailDO.CCExpiry		= 	"";
				paymentDetailDO.PaymentStatus	= 	"0";
				paymentDetailDO.PaymentNote		= 	PaymentDetailDO.getPaymentTypeCheque();
				if(!chequePaymentDetailDO.BankName.equalsIgnoreCase("Other"))
					paymentDetailDO.UserDefinedBankName = chequePaymentDetailDO.BankName;
				else
					paymentDetailDO.UserDefinedBankName	= 	chequePaymentDetailDO.BankName;
				
				paymentDetailDO.Status			= 	"0";
//				if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() == 1 && arrInvoiceNumbers.get(0).isNewleyAdded)
//				{
//					paymentDetailDO.Amount			= 	"0";
//					vecDetails.add(paymentDetailDO);					
//				}
//				else
				{
					paymentDetailDO.Amount			= 	chequePaymentDetailDO.Amount;
					paymentDetailDO.invoiceAmount	= 	""+paidByCheque;
					if(StringUtils.getFloat(paymentDetailDO.Amount.trim())>0.0f)	
						vecDetails.add(paymentDetailDO);
				}
				
				lineNo++;
			}
			if(methodUsedToPay.length()>0)
				methodUsedToPay =","+PaymentDetailDO.getPaymentTypeCheque();
			else
				methodUsedToPay =PaymentDetailDO.getPaymentTypeCheque();
		}
		return vecDetails;
	}
	
	private void showPaymentCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(ReceivePaymentActivity.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(false);
		TextView tv_poptitle	  = (TextView) view.findViewById(R.id.tv_poptitle);
		TextView tv_poptitle1	  = (TextView) view.findViewById(R.id.tv_poptitle1);
		
		tv_poptitle.setText("Payment");
		tv_poptitle1.setText("Successful");
		Button btn_popup_print		  = (Button) view.findViewById(R.id.btn_popup_print);
		
		Button btn_popup_collectpayment= (Button) view.findViewById(R.id.btn_popup_collectpayment);
		Button btn_popup_returnreq	  = (Button) view.findViewById(R.id.btn_popup_returnreq);
		Button btn_popup_done		  = (Button) view.findViewById(R.id.btn_popup_done);
		Button btn_popup_task		  = (Button) view.findViewById(R.id.btn_popup_task);   
		Button btn_popup_survey		  = (Button) view.findViewById(R.id.btn_popup_survey);   
		Button btnPlaceNewOrder		  = (Button) view.findViewById(R.id.btnPlaceNewOrder);   

//===============================newly aded for food========================================================================
	    Button btn_popup_print_kwality		  = (Button) view.findViewById(R.id.btn_popup_print_Kwality);		
		
		
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
//===============================newly added for food========================================================================		
		
		btn_popup_print_kwality.setTypeface(AppConstants.Roboto_Condensed_Bold);

if(Division==TrxHeaderDO.get_DIVISION_FOOD() || Division == TrxHeaderDO.get_DIVISION_THIRD_PARTY ()){
	
			btn_popup_print_kwality.setVisibility(View.VISIBLE);
	if(Division==TrxHeaderDO.get_DIVISION_FOOD() )
			btn_popup_print_kwality.setText("Print Payment -Kwality"); //vinod
	else
		btn_popup_print_kwality.setText("Print Payment -TPT"); //vinod

			btn_popup_print.setText("Print Payment - PIC");
		}else{
	     		btn_popup_print_kwality.setVisibility(View.GONE);
		        btn_popup_print.setText("Print Payment");
		}
		if(isFromArCollection || isFromPayment)
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btn_popup_returnreq.setVisibility(View.GONE);
			btn_popup_task.setVisibility(View.GONE);
			btn_popup_survey.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.GONE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
		{
			btn_popup_collectpayment.setVisibility(View.GONE);
			btnPlaceNewOrder.setVisibility(View.GONE);
		}
		else if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_MODERN))
			btnPlaceNewOrder.setVisibility(View.GONE);
		
		if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			btn_popup_returnreq.setVisibility(View.GONE);
		
		btn_popup_returnreq.setText("Replacement");
		
		if(AppConstants.isServeyCompleted)
		{
			btn_popup_survey.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.order_order), null, getResources().getDrawable(R.drawable.check1_new), null);
			btn_popup_survey.setClickable(false);
			btn_popup_survey.setEnabled(false);
		}
		
		
		if(AppConstants.isTaskCompleted)
		{
			btn_popup_task.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.taks_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		}
		btn_popup_survey.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mCustomDialog.dismiss();
				Intent intent = new Intent(ReceivePaymentActivity.this, ServeyListActivity.class);
				startActivity(intent);
				
			}
		});
		
		btnPlaceNewOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(ReceivePaymentActivity.this, CaptureInventoryCategory.class);
//				Intent intent = new Intent(ReceivePaymentActivity.this, StoreCheckActivity.class);
				Intent intent = new Intent(ReceivePaymentActivity.this, StoreCheckCatagoryActivity.class);
				intent.putExtra("name",""+getResources().getString(R.string.Capture_Inventory) );
				intent.putExtra("mallsDetails",mallsDetails);
				intent.putExtra("from", "checkin");
				startActivity(intent);
			}
		});
		
		btn_popup_print.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();
				
				Intent intent = new Intent(ReceivePaymentActivity.this, WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
				intent.putExtra("totalAmount", totalInvoiceAmount);
				intent.putExtra("strReceiptNo", strReceiptNo);
				intent.putExtra("selectedAmount", selectedAmount);
				intent.putExtra("paymentHeaderDO", paymentHeaderDO);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
				intent.putExtra("paymode", payMode);
				intent.putExtra("PrintTypeIce", 100);
				startActivityForResult(intent, 1000);
				
				
			}
		});
		
		
//===================================newly added for food ====================================		
		
		
	btn_popup_print_kwality.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();
				
				Intent intent = new Intent(ReceivePaymentActivity.this, WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
				intent.putExtra("totalAmount", totalInvoiceAmount);
				intent.putExtra("strReceiptNo", strReceiptNo);
				intent.putExtra("selectedAmount", selectedAmount);
				intent.putExtra("paymentHeaderDO", paymentHeaderDO);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
				intent.putExtra("paymode", payMode);
				intent.putExtra("PrintTypeIce", 200);
				startActivityForResult(intent, 1000);
				
				
			}
		});
		
		btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("payment");
			}
		});
//		btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
//		btn_popup_collectpayment.setClickable(false);
//		btn_popup_collectpayment.setEnabled(false);
		btn_popup_collectpayment.setVisibility(View.GONE);
		btn_popup_returnreq.setVisibility(View.VISIBLE);
		
		btn_popup_returnreq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("ReturnRequest");
			}
		});

		btn_popup_task.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				Intent intent = new Intent(ReceivePaymentActivity.this, TaskToDoActivity.class);
				intent.putExtra("object", mallsDetails);
				startActivity(intent);
			}
		});
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				
				if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)){
					if(!isFromOrderPreview)
						uploadData();
				}
				
				if(isFromPayment)
				{
					Intent intent = new Intent();
					intent.putExtra("totalInvoiceAmount",totalAmountPaid);
					intent.putExtra("TrxCode", trxHeaderDO.trxCode);
					setResult(5000,intent);
					finish();
				}
				else
				{
					Intent intentBrObj = new Intent();
					intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
					sendBroadcast(intentBrObj);
				}
				
			}
		});
		
		if(isFromArCollection || isFromPayment)
		{
			btn_popup_survey.setVisibility(View.GONE);
			btn_popup_collectpayment.setVisibility(View.GONE);
			btn_popup_returnreq.setVisibility(View.GONE);
			btn_popup_task.setVisibility(View.GONE);
		}
		
		btn_popup_survey.setVisibility(View.GONE);
		btn_popup_returnreq.setVisibility(View.GONE);
		btn_popup_task.setVisibility(View.GONE);
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		
		if(dialog!=null && dialog.isShowing())
			dialog.dismiss();
		
			btnConfirm.setClickable(false);
			btnConfirm.setEnabled(false);
			btnConfirm.setVisibility(View.GONE);
			btnContinue.setVisibility(View.VISIBLE);
		}catch(Exception e){e.printStackTrace();}
	}
	
	private void updatePaymentStatus() 
	{
		StatusDO statusDO = new StatusDO();
		statusDO.UUid ="";
		statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
		statusDO.Customersite =mallsDetails.site;
		statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
		statusDO.Visitcode=mallsDetails.VisitCode;
		statusDO.JourneyCode = mallsDetails.JourneyCode;
		statusDO.Status = "0";
		statusDO.Action = AppConstants.Action_CheckIn;
		statusDO.Type =AppConstants.Type_Collections;
		new StatusDA().insertOptionStatus(statusDO);
	}
	
	public ArrayList<String> filterSerialNumber(ArrayList<String> arrSerialNumbers){
		if(arrSerialNumbers!=null){
			Predicate<String> filteredItems = new Predicate<String>() {
				public boolean apply(String serialNumber) {
					return !TextUtils.isEmpty(serialNumber.trim());
				}
			};
			Collection<String> filteredResult = filter(arrSerialNumbers,
					filteredItems);
			return (ArrayList<String>) filteredResult;
		}else{
			return new ArrayList<String>();
		}
	}
	//
	private static final int VALID_PAYMNET=1;
	private static final int ERROR_TRYINGFOR_LESSAMOUNT=2;
	private static final int ERROR_TRYINGFOR_MOREAMOUNT=3;
	private static final int ERROR_NOT_ENTERED_ALL_SERIAL_NUMBER=4;
	private static final int ERROR_ENTERED_MORE_SERIAL_NUMBER=5;
	private static final int ERROR_ENTERED_DUPLICATE_SERIAL_NUMBER=6;
	private static final int ERROR_CHEQUE_DETAIL_NOT_ENTERED=7;
	private static final int ERROR_NOT_SIGNED=8;
	private static final int ERROR_UNKNOWN=9;
	
	
	public void validDatePayment(final PaymentValidationListner paymentValidationListner){
		new Thread(new Runnable() {
			int valid=VALID_PAYMNET;
			@Override
			public void run() {
				float byCheque=0;
				try {
					for (int i = 0; i < vecChequePayment.size(); i++) {
							ChequePaymentDetailDO tmpObj=vecChequePayment.get(i);
							float tmp=StringUtils.getFloat(tmpObj.Amount);
							if((tmp>0.0f && (TextUtils.isEmpty(tmpObj.ChequeNumber.trim())
									||TextUtils.isEmpty(tmpObj.BankName.trim())
									||TextUtils.isEmpty(tmpObj.BankID.trim())
									||TextUtils.isEmpty(tmpObj.Date.trim())
									||(tmpObj.ChequeNumber.length() < 6))))
							{
								valid = ERROR_CHEQUE_DETAIL_NOT_ENTERED;
								break;
							}
							byCheque += StringUtils.getFloat(vecChequePayment.get(i).Amount);
					}
					if(valid!=ERROR_CHEQUE_DETAIL_NOT_ENTERED){
						paidByCheque=byCheque;
						paidAsCash = StringUtils.getFloat(edtAmount.getText().toString());
						float paidAmount = paidAsCash + paidByCheque;
						paidAmount = StringUtils.getFloat(decimalFormat.format(paidAmount));
						remainingInvoiceAmount = totalInvoiceAmount - paidAmount;
//						if (Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Cash) && remainingInvoiceAmount > 0)
						if (mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)
								&& remainingInvoiceAmount > 0)
							valid = ERROR_TRYINGFOR_LESSAMOUNT;
						else
							if (mallsDetails.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)	&& remainingInvoiceAmount > 0 && AppConstants.customertype.equalsIgnoreCase("CASH") && !frommenu)
								valid = ERROR_TRYINGFOR_LESSAMOUNT;
						else if(paidAmount < 0.5)
							valid = ERROR_TRYINGFOR_LESSAMOUNT;
						/*else if (remainingInvoiceAmount < -1) {
							valid = ERROR_TRYINGFOR_MOREAMOUNT;
						} else*/ if(valid==VALID_PAYMNET && !isSigned)
								valid = ERROR_NOT_SIGNED;
							/*float cashAmountWithDetail = 0;// amountForWhichDenominationDetailEntered
							Set<CashDenominationDO> cashDenominationDOs = hashUsedCashDenominations
									.keySet();
							for (CashDenominationDO cashDenominationDO : cashDenominationDOs) {
								PaymentCashDenominationDO paymentCashDenominationDO = hashUsedCashDenominations
										.get(cashDenominationDO);
								cashAmountWithDetail = cashAmountWithDetail+(paymentCashDenominationDO.arrDenominationDetail
										.size()
										* StringUtils.getFloat(cashDenominationDO.Amount));
								if(isDuplicateSrialNumbersExist(paymentCashDenominationDO.arrDenominationDetail)){
									valid = ERROR_ENTERED_DUPLICATE_SERIAL_NUMBER;
									break;
								}
							}
							if(valid != ERROR_ENTERED_DUPLICATE_SERIAL_NUMBER){
								if (cashAmountWithDetail < paidAsCash) {
									valid = ERROR_NOT_ENTERED_ALL_SERIAL_NUMBER;
								} else if (cashAmountWithDetail > paidAsCash) {
									valid = ERROR_ENTERED_MORE_SERIAL_NUMBER;
								}
							}*/
//						}
						
					} 
					}catch (Exception e) {
						e.printStackTrace();
						valid=ERROR_UNKNOWN;
					}
					
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						paymentValidationListner.valid(valid);
					}
				});
			}
		}).start();
	}


	private boolean isDuplicateSrialNumbersExist(
			ArrayList<String> arrSerialNumbers) {
		ArrayList<String> filterDistinctSerialNumbers=new ArrayList<String>();
		for(String str :arrSerialNumbers){
			if(!filterDistinctSerialNumbers.contains(str))
				filterDistinctSerialNumbers.add(str);
			else 
				break;
		}
		return arrSerialNumbers.size()!=filterDistinctSerialNumbers.size();
	}
	private void isDuplicateSrialNumbersExist(final PaymentValidationListner paymentValidationListner) {
		new Thread(new Runnable() {
			ArrayList<String> filterDistinctSerialNumbers=new ArrayList<String>();
			int valid=VALID_PAYMNET;
			@Override
			public void run() {
				
				Set<CashDenominationDO> cashDenominationDOs = hashUsedCashDenominations
						.keySet();
				for (CashDenominationDO cashDenominationDO : cashDenominationDOs) {
					PaymentCashDenominationDO paymentCashDenominationDO = hashUsedCashDenominations
							.get(cashDenominationDO);
					for(String str :paymentCashDenominationDO.arrDenominationDetail){
						if(!filterDistinctSerialNumbers.contains(str)){
							filterDistinctSerialNumbers.add(str);
						}
						else{ 
							valid=ERROR_ENTERED_DUPLICATE_SERIAL_NUMBER;
							break;
						}
					}
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						paymentValidationListner.valid(valid);						
					}
				});
			}
		}).start();
		
	}
	private interface PaymentValidationListner{
		public void valid(int errorCode);
	}
	private Bitmap getBitmap(SignatureView customerSignature)
	{
		Bitmap bitmap = customerSignature.getDrawingCache(true);
		return bitmap;
	}
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPaymentDone)
		{
//			showCustomDialog(ReceivePaymentActivity.this, "Successful !", "Payment\n Successful !", "OK", null, "ReceivePayment");
			showPaymentCompletePopup();
		}
		else {
			showCustomDialog(ReceivePaymentActivity.this,"Alert","Do you want to cancel this payment ?",getString(R.string.Yes),getString(R.string.No),"CancelPayment");
		}
	}
	@Override
	public void onButtonYesClick(String from) {
		if(from.equalsIgnoreCase("CancelPayment")){
			finish();
		}
		if(from.equalsIgnoreCase("ReceivePayment"))
		{
			Intent intent = new Intent(ReceivePaymentActivity.this, WoosimPrinterActivity.class);
			intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
			intent.putExtra("totalAmount", totalInvoiceAmount);
			intent.putExtra("strReceiptNo", strReceiptNo);
			intent.putExtra("selectedAmount", selectedAmount);
			intent.putExtra("paymentHeaderDO", paymentHeaderDO);
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
			startActivityForResult(intent, 1000);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == CAMERA_PIC_REQUEST) && mCapturedImageURI != null && resultCode ==RESULT_OK) 
    	{
    		try
    		{
    			showLoader("Please wait...");
            	new Thread(new Runnable()
            	{
    				@Override
    				public void run()
    				{
    					
    					try
    					{
    						System.gc();
        		        	String[] projection = { MediaStore.Images.Media.DATA}; 
        		        	
        		            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null); 
        		            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
        		            if(cursor.moveToFirst())
        		            {
        		            	capturedImageFilePath = cursor.getString(column_index_data);
        		            	AppConstants.iconpaths=capturedImageFilePath;
        		            	
        		            	File f = new File(capturedImageFilePath);
        		            	Bitmap bmp = BitmapUtilsLatLang.decodeSampledBitmapFromResource(f, 720,1280);
        		            	Bitmap bitmapProcessed = getBitMap(bmp,capturedImageFilePath);
        		            	mBtBitmap = bitmapProcessed;
        		            	
        		            	Cursor cursor1 = managedQuery(getImageUri(ReceivePaymentActivity.this,mBtBitmap), projection, null, null, null); 
        		            	int column_index_data1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
        		            	if(cursor1.moveToFirst())
        		            	{
        		            		
        		            		capturedImageFilePath = cursor1.getString(column_index_data1);
    		            		
    		            		
	    		            		runOnUiThread(new Runnable() {
	    		            			@Override
	    		            			public void run() {
	    		            				if(chequesPager!=null && chequePaymentAdapter!=null)
	    		            				{
	    		            					vecChequePayment.get(chequesPager.getCurrentItem()).filePath = capturedImageFilePath;
	    		            					chequePaymentAdapter.refreshImage(chequesPager.getCurrentItem(),capturedImageFilePath);
	    		            				}
	    		            				ivChequeImage.setVisibility(View.VISIBLE);
	    		            				final Uri uri = Uri.parse(capturedImageFilePath);
	    		            				if (uri != null) 
	    		            				{
	    		            					Bitmap bitmap = getHttpImageManager().loadImage(new HttpImageManager.LoadRequest(uri,ivChequeImage,capturedImageFilePath));
	    		            					if (bitmap != null) 
	    		            					{
	    		            						ivChequeImage.setImageBitmap(bitmap);
	    		            					}
	    		            				}
	    		            				
	    		            			}
	    		            		});
        		            	}
    		            	}
    		            }
    					catch(Exception e)
    					{
    						e.printStackTrace();
    					}
    				}
    			}).start();
			}
    		catch (OutOfMemoryError e)
    		{
    			hideLoader();
    			showCustomDialog(ReceivePaymentActivity.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    		catch (Exception e)
    		{
    			hideLoader();
    			showCustomDialog(ReceivePaymentActivity.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    		
    		hideLoader();
    	} 
		if ((requestCode == 1000) && resultCode ==RESULT_OK) 
		{
			if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)){
				if(!isFromOrderPreview)
					uploadData();
			}
			
			if(isFromPayment)
			{
				Intent intent = new Intent();
				intent.putExtra("totalInvoiceAmount",totalAmountPaid);
				setResult(5000,intent);
				finish();
			}
			else
			{
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
				sendBroadcast(intentBrObj);
			}
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) 
	{
	   String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
	   return Uri.parse(path);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		if(locationUtility != null)
			locationUtility.stopGpsLocUpdation();
	}
	
	private Bitmap getBitMap(Bitmap bmp, String camera_imagepath)
	{
		Bitmap mBtBitmap = null;
		if(bmp != null)
		{
			bitmapProcessed = BitmapUtilsLatLang.processBitmap2(bmp, lat, lang, "");
			if(bmp!=null && !bmp.isRecycled())
				bmp.recycle();

			mBtBitmap = bitmapProcessed;
			return mBtBitmap;
		}
		return mBtBitmap;
	}
	public HttpImageManager getHttpImageManager() {
		return ((MyApplicationNew) ((Activity) ReceivePaymentActivity.this)
				.getApplication()).getHttpImageManager();
	}
	
	public void startCamera()
	{
		String fileName = "temp.jpg";  
		ContentValues values = new ContentValues();  
		values.put(MediaStore.Images.Media.TITLE, fileName);  
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);  
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
		
		if(locationUtility != null)
			   locationUtility.getLocation(ReceivePaymentActivity.this);
	}
	
	private void deleteCheque()
	{
		if(chequesPager!=null && chequePaymentAdapter!=null && vecChequePayment!=null && chequesPager.getCurrentItem() < vecChequePayment.size())
		{
			float chequeAmount = StringUtils.getFloat(vecChequePayment.get(chequesPager.getCurrentItem()).Amount);
			remainingInvoiceAmount = remainingInvoiceAmount + chequeAmount;
			paidByCheque = paidByCheque -  chequeAmount;
			vecChequePayment.remove(chequesPager.getCurrentItem());
			chequePaymentAdapter.refresh(vecChequePayment);
			
			totalPages --;
			tvPageIndication.setText("- "+(1)+"/"+totalPages);
			tvRemainingAmt.setText(amountFormate.format(remainingInvoiceAmount)+"");
			tvCollectedAmt.setText(amountFormate.format(paidAsCash + paidByCheque)+"");
			refreshPageController();
		}
	}
	
	private void refreshPageController() 
	{
		int pagerPosition = 0;
		llPagerTab.removeAllViews();
		for (int i = 0; i <= (chequePaymentAdapter.getCount()-1); i++)
		{
			final ImageView imgvPagerController = new ImageView(ReceivePaymentActivity.this);
			imgvPagerController.setPadding(2,2,2,2);
			
			imgvPagerController.setImageResource(R.drawable.pager_dot);
			
			
			llPagerTab.addView(imgvPagerController);
		}		
		
		pagerPosition = chequesPager.getCurrentItem();
		
		if(((ImageView)llPagerTab.getChildAt(pagerPosition)) != null)
			((ImageView)llPagerTab.getChildAt(pagerPosition)).setImageResource(R.drawable.pager_dot_h);
		
		if(chequePaymentAdapter.getCount() > 1)
		{
			ivDeleteCheque.setVisibility(View.VISIBLE);
			llPagerTab.setVisibility(View.VISIBLE);
		}
		else
		{
			ivDeleteCheque.setVisibility(View.INVISIBLE);
			llPagerTab.setVisibility(View.INVISIBLE);
		}
			
	}
	
	private boolean validateChequePayment()
	{
		if(vecChequePayment!=null && vecChequePayment.size()>0 && !vecChequePayment.get(chequePaymentAdapter.getCount()-1).ChequeNumber.equalsIgnoreCase("") && 
				!vecChequePayment.get(chequePaymentAdapter.getCount()-1).BankName.equalsIgnoreCase("") && 
				!vecChequePayment.get(chequePaymentAdapter.getCount()-1).Date.equalsIgnoreCase("") && 
				!vecChequePayment.get(chequePaymentAdapter.getCount()-1).Amount.equalsIgnoreCase(""))
		{
			return true;
		}
		else
			return false;
	}
	private void downloadFullImage(){
		if(vecCashDenomination!=null){
			for(CashDenominationDO cashDenominationDO:vecCashDenomination)
			{
				String imageURL = cashDenominationDO.Picture.replace("../", ServiceURLs.IMAGE_GLOBAL_URL);
				final Uri uri = Uri.parse(imageURL);
				if (uri != null) {
					ImageView imageView=null;
					getHttpImageManager().loadImage(
							new HttpImageManager.LoadRequest(uri, imageView,imageURL));
				}	
			}
		}
	}

	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			lat	 = loc.getLatitude()+"";
			lang = loc.getLongitude()+"";
		}
	}
	
	public void disableOrEnableControls()
	{
		if(payMode.equalsIgnoreCase(AppConstants.PAYMENT_NOTE_CHEQUE))
		{
			tvCashPaymode.setBackgroundResource(R.drawable.paymode_unchecked);
			tvChequePaymode.setBackgroundResource(R.drawable.paymode_checked);
			lvCollectedAmt.setVisibility(View.VISIBLE);
			edtAmount.setEnabled(false);
			tvAddAnotherCheque.setEnabled(true);
			ivCaptureImage.setEnabled(true);
			
			llCash.setAlpha(0.5f);
			llPaymentCash.setVisibility(View.GONE);
			llCheque.setAlpha(1);
		}
		else
		{
			tvCashPaymode.setBackgroundResource(R.drawable.paymode_checked);
			tvChequePaymode.setBackgroundResource(R.drawable.paymode_unchecked);
			lvCollectedAmt.setVisibility(View.INVISIBLE);
			edtAmount.setEnabled(true);
			tvAddAnotherCheque.setEnabled(false);
			ivCaptureImage.setEnabled(false);
			
			llCash.setAlpha(1);
			llCheque.setAlpha(0.5f);
			llPaymentCheque.setVisibility(View.GONE);
		}
	}
	
	Dialog dialog;
	private SignatureView customerSignature, presellerSignature;
	private void showSignatureDialog()
	{
		
		dialog		= new Dialog(this,R.style.Dialog);
		LinearLayout llSignature 	  	= (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
		TextView tvLogisticsSignature	= (TextView)llSignature.findViewById(R.id.tvLogisticsSignature);
		TextView tvSalesmanSignature	= (TextView)llSignature.findViewById(R.id.tvSalesmanSignature);
		
		final LinearLayout llSignSupervisor = (LinearLayout)llSignature.findViewById(R.id.llSignSupervisor);
		final LinearLayout llSignDriver = (LinearLayout)llSignature.findViewById(R.id.llSignDriver);
		final LinearLayout llLPO		= (LinearLayout)llSignature.findViewById(R.id.llLPO);
	
		final Button btnOK 					= (Button)llSignature.findViewById(R.id.btnOK);
		Button btnSKCear 				= (Button)llSignature.findViewById(R.id.btnSKCear);
		Button btnDriverCear 			= (Button)llSignature.findViewById(R.id.btnDriverCear);
		Button btnCancle 				= (Button)llSignature.findViewById(R.id.btnCancle);
		
		dialog.addContentView(llSignature,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		dialog.show();
		
		llLPO.setVisibility(View.GONE);
		
		tvLogisticsSignature.setText("Customer Signature");
		tvSalesmanSignature.setText("Salesman Signature");
		
		tvSalesmanSignature.setVisibility(View.GONE);
		llSignDriver.setVisibility(View.GONE);
		btnDriverCear.setVisibility(View.GONE);
		
		customerSignature  = new SignatureView(this);
		customerSignature.setDrawingCacheEnabled(true);
		customerSignature.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT , (int)(180 * px)));
		customerSignature.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

		llSignSupervisor.addView(customerSignature);
	
		btnSKCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				if(customerSignature != null)
					customerSignature.resetSign();
			}
		});
		
		btnDriverCear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				isSigned=false;
				if(presellerSignature != null)
					presellerSignature.resetSign();
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if(dialog!=null && dialog.isShowing())
					dialog.dismiss();
			}
		});
		
		btnOK.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(final View v) 
			{
				synchronized (SYNC_PAYMENT) 
				{
					SetDisableButton(btnOK);
					
					LogUtils.debug("paymentclick", "signature clicked");
					if(!customerSignature.isSigned()){
						showCustomDialog(ReceivePaymentActivity.this, "Alert !", "Please take Customer Signature.", getString(R.string.OK), null, "scroll");
					}
					else{
						insertPayment();
					}
				}
			}
		});
	}
}
