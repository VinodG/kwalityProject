package com.winit.sfa.salesman;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomBuilder;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomScrollView;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PaymentDetailDO;
import com.winit.alseer.salesman.dataobject.PaymentHeaderDO;
import com.winit.alseer.salesman.dataobject.PaymentInvoiceDO;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.kwalitysfa.salesman.R;
public class ReceivePaymentBySalesman extends BaseActivity
{
	//declaration of variables
	private LinearLayout llReceivePayment, llCheque,llCustomerSignature, llPaymentLayout, llCustomer_Signature ,llCash, 
						 llAddPaymentLayout, llInvoiceNumbers, llRemainingAmount;
	
	private MyView viewAgent;
	
	private TextView tvRecieveHead, tvCheque, tvCash ,tvSelectBanks, tvDate, tvAmount, tvCurrencyType, tvTotalAmountValue
					 ,tvDateTitle, tvSelCurrencyType, tvSelAmount, tvCashDateTitle, tvCashSelect, tvCashSelAmount, 
					 tvCashSelCurrencyType, tvRemaining, tvRemainingAED, tvSignatureTitle, tvMinusCash, tvMinusCheck;
	
	private EditText  etCheque_Details , etTotalAmount, etChequeAmount, etCashAmount, etRemainingAmount;
	private Button btnConfirm_Payment, btnCancel , btnPrint, btnPaymentSignClear, btnContinue, btnPrintInvoice;
	private final int lastthickValue=2;
	private Paint  mPaint;
	private final int DATE_DIALOG_ID = 0;
	private String selectedAmount = "",strDate = "", strChequeDate = "";
	private CustomScrollView customScrollView;
	private Vector<NameIDDo> vecBankName;
	private boolean isSigned = false, isFromArCollection = false, isPaymentDone = false, isCouponPayment;
	private float totalAmount = 0;
	private String strInvoiceNo, strReceiptNo = "";
	private JourneyPlanDO mallsDetails;
	private ArrayList<PendingInvicesDO> arrInvoiceNumbers;
	private PaymentHeaderDO paymentHeaderDO;
	private String PAYMENT_TYPE = "CHEQUE", CASH = "CASH", CHEQUE = "CHEQUE", ENTERED_TYPE/*, SEL_TYPE = CHEQUE*/;
	private LinearLayout llBankName;
	private EditText etBankName;
	private int amountType = 1;
	private boolean isAdvance = false,isCheque=false,isCash=false,isExceed = false;
	
	@Override
	public void initialize() 
	{
		//inflate the receive_payment layout
		llReceivePayment		=	(LinearLayout)inflater.inflate(R.layout.receive_payment_main, null);
		llPaymentLayout			=	(LinearLayout)inflater.inflate(R.layout.preseller_receive_payment, null);
		
		isSigned 		= false;
		isPaymentDone 	= false;
	
		preference.removeFromPreference(Preference.RECIEPT_NO);
		preference.commitPreference();
		
		intialiseControls();
		if(getIntent().getExtras() != null)
		{
			isAdvance	= (boolean) getIntent().getExtras().getBoolean("isAdvance");
			isFromArCollection 	=	 getIntent().getExtras().getBoolean("arcollection");
			
			Log.e("isFromArCollection - ","isFromArCollection - " +isFromArCollection);
			selectedAmount	   	= 	""+getIntent().getExtras().getFloat("selectedAmount");
			float invoiceAmount	=	 getIntent().getExtras().getFloat("invoiceAmount");
			isExceed = getIntent().getExtras().getBoolean("isExceed");
			if(invoiceAmount < 0)
				amountType = -1;
			
			if(selectedAmount.trim().equalsIgnoreCase("NaN"))
				selectedAmount  = 	""+invoiceAmount;
			
			strInvoiceNo		= 	getIntent().getExtras().getString("invoiceNo");
			mallsDetails  		= 	(JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
			arrInvoiceNumbers  	= 	(ArrayList<PendingInvicesDO>) getIntent().getExtras().get("InvoiceNumbers");
			isCouponPayment		= 	getIntent().getExtras().getBoolean("isCouponPayment");
			strReceiptNo		= 	getIntent().getExtras().getString("strReceiptNo");
			
			if(mallsDetails.currencyCode != null && mallsDetails.currencyCode.length() > 0)
				curencyCode = mallsDetails.currencyCode;
			
			if(strInvoiceNo != null && arrInvoiceNumbers == null)
			{
				selectedAmount 						  =  ""+Math.round(StringUtils.getFloat(selectedAmount));
				LinearLayout llInvoiceLayout		  =	(LinearLayout)inflater.inflate(R.layout.preseller_invoice_list, null);
				TextView tvSelect_Pre_sold_OrderValue =	(TextView)llInvoiceLayout.findViewById(R.id.tvSelect_Pre_sold_OrderValue);
				TextView tvSelectAmount				  =	(TextView)llInvoiceLayout.findViewById(R.id.tvSelectAmount);
				tvSelectAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvSelect_Pre_sold_OrderValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
				tvSelectAmount.setText(""+amountFormate.format(selectedAmount));
				tvSelect_Pre_sold_OrderValue.setText(""+(strInvoiceNo));
				llInvoiceNumbers.addView(llInvoiceLayout,LayoutParams.MATCH_PARENT, (int)(25 * BaseActivity.px));
				
				if(selectedAmount != null && !selectedAmount.equalsIgnoreCase(""))
				{
					etTotalAmount.setText(amountFormate.format(StringUtils.getFloat(selectedAmount)));
					etRemainingAmount.setText(amountFormate.format(StringUtils.getFloat(selectedAmount)*amountType));
					tvTotalAmountValue.setText(curencyCode+" "+amountFormate.format(StringUtils.getFloat(selectedAmount)));
				}
				
				PendingInvicesDO obj = new PendingInvicesDO();
				obj.invoiceNo 	= strInvoiceNo;
				obj.balance 	= selectedAmount;
				obj.lastbalance	= selectedAmount;
				obj.orderId		= strInvoiceNo;
				arrInvoiceNumbers = new ArrayList<PendingInvicesDO>();
				arrInvoiceNumbers.add(obj);
			}
			else
			{
				for(PendingInvicesDO obj : arrInvoiceNumbers)
				{
					LinearLayout llInvoiceLayout			=	(LinearLayout)inflater.inflate(R.layout.preseller_invoice_list, null);
					TextView tvSelect_Pre_sold_OrderValue	=	(TextView)llInvoiceLayout.findViewById(R.id.tvSelect_Pre_sold_OrderValue);
					TextView tvSelectAmount					=	(TextView)llInvoiceLayout.findViewById(R.id.tvSelectAmount);
					final EditText etAmount					=	(EditText)llInvoiceLayout.findViewById(R.id.etAmount);
					final TextView tvInvoiceType			=	(TextView)llInvoiceLayout.findViewById(R.id.tvInvoiceType);
					
					etAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvSelectAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvSelect_Pre_sold_OrderValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
					tvSelect_Pre_sold_OrderValue.setText(""+obj.invoiceNo);
					
					if(StringUtils.getFloat(obj.balance) < 0)
						tvInvoiceType.setText("Return Invoice");
					else 
						tvInvoiceType.setText("Sales Invoice");
					
					tvSelectAmount.setText(""+amountFormate.format(Math.abs(StringUtils.getFloat(obj.balance))));
					etAmount.setText(""+amountFormate.format(Math.abs(StringUtils.getFloat(obj.balance))));
					
					etAmount.setTag(obj);
					etAmount.setVisibility(View.GONE);
					etAmount.addTextChangedListener(new TextWatcher()
					{
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) 
						{
							PendingInvicesDO obj = (PendingInvicesDO) etAmount.getTag();
							if(s != null && obj != null)
							{
								LogUtils.errorLog("s","s - "+StringUtils.getFloat(s.toString()));
								LogUtils.errorLog("balance","balance - "+StringUtils.getFloat(obj.balance));
								LogUtils.errorLog("lastbalance","lastbalance - "+StringUtils.getFloat(obj.lastbalance));
								if(StringUtils.getFloat(s.toString()) <= StringUtils.getFloat(obj.lastbalance))
								{
									obj.balance = s.toString();
									float totaleditedAmount = 0.0f;
									for(PendingInvicesDO objPendingInvicesDO :arrInvoiceNumbers)
									{
										totaleditedAmount+= StringUtils.getFloat(objPendingInvicesDO.balance);
										tvTotalAmountValue.setText(curencyCode+" "+amountFormate.format(totaleditedAmount));
										etTotalAmount.setText(""+decimalFormat.format(totaleditedAmount));
										etRemainingAmount.setText(""+decimalFormat.format(totaleditedAmount*amountType));
									}
								}
								else
								{
									tvTotalAmountValue.setText(curencyCode+" "+amountFormate.format(totalAmount));
									etTotalAmount.setText(""+decimalFormat.format(totalAmount));
									etRemainingAmount.setText(""+decimalFormat.format(totalAmount*amountType));
									etAmount.setText(""+obj.lastbalance);
								}
							}
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
					totalAmount+= StringUtils.getFloat(obj.balance);
					llInvoiceNumbers.addView(llInvoiceLayout,LayoutParams.MATCH_PARENT, (int)(35 * BaseActivity.px));
				}
				if(totalAmount != 0)
				{
					totalAmount = Math.round(totalAmount);
					tvTotalAmountValue.setText(curencyCode+" "+amountFormate.format(totalAmount));
					etTotalAmount.setText(""+decimalFormat.format(totalAmount));
					etRemainingAmount.setText(""+decimalFormat.format(totalAmount*amountType));
				}
			}
		}
		
		//setting current date in format 
		//getting current dateofJorney from Calendar
	    Calendar c 	= 	Calendar.getInstance();
	    int year 	= 	c.get(Calendar.YEAR);
	    int month  	= 	c.get(Calendar.MONTH);
	    int day 	=	c.get(Calendar.DAY_OF_MONTH);
	    strDate    	= 	CalendarUtils.getMonthFromNumber(month+1)+" "+day+CalendarUtils.getDateNotation(day)+", "+year;
	     
		viewAgent = new MyView(ReceivePaymentBySalesman.this);
		viewAgent.setDrawingCacheEnabled(true);
		viewAgent.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);

		if(viewAgent != null)
			llCustomerSignature.addView(viewAgent, new android.widget.FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lastthickValue);
       
        showLoader(getString(R.string.please_wait));
        new Thread(new Runnable()
        {
			@Override
			public void run() 
			{
				vecBankName = new CommonDA().getAllBanksNew();
				 runOnUiThread(new Runnable()
				 {
					@Override
					public void run()
					{
						hideLoader();
					}
				});
			}
		}).start();
        
		tvCheque.setOnClickListener(new OnClickListener()
		{
			@Override 
			public void onClick(View v) 
			{
				if(ENTERED_TYPE != null /*&& ENTERED_TYPE.equalsIgnoreCase(CASH)*/)//commented to display remaining amount in both the cases
					llRemainingAmount.setVisibility(View.VISIBLE);
				else
					llRemainingAmount.setVisibility(View.VISIBLE);
				if(!isCheque)
				{
					tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.checked1),null, null, null);
					llCheque.setVisibility(View.VISIBLE);
					isCheque = true;
				}
				else
				{
					tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked1),null, null, null);
					etCheque_Details.setText("");
					tvSelectBanks.setText("");
					tvSelectBanks.setTag(-1);
					etBankName.setText("");
					tvDate.setTag("");
					tvDate.setText("");
					etChequeAmount.setText("");
					llCheque.setVisibility(View.GONE);
					isCheque = false;
					
//					tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.checked1), null, null, null);
//					llCash.setVisibility(View.VISIBLE);
//					isCash = true;
				}
				
				tvDate.setBackgroundResource(R.drawable.spinner_disabled_holo_light);
				tvDate.setText(strChequeDate);
				
//				SEL_TYPE = CHEQUE;
			}
		});
		
		tvCash.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(ENTERED_TYPE != null /*&& ENTERED_TYPE.equalsIgnoreCase(CHEQUE)*/)//commented to display remaining amount in both the cases
					llRemainingAmount.setVisibility(View.VISIBLE);
				else
					llRemainingAmount.setVisibility(View.VISIBLE);
				
				
				if(!isCash)
				{
					tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.checked1), null, null, null);
					llCash.setVisibility(View.VISIBLE);
					isCash = true;
				}
				else
				{
					tvCash.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked1), null, null, null);
					etCashAmount.setText("");
					llCash.setVisibility(View.GONE);
					isCash = false;
					
//					tvCheque.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unchecked),null, null, null);
//					llCheque.setVisibility(View.VISIBLE);
//					isCheque = true;
				}
				tvCashSelect.setText(""+strDate);
				tvCashSelect.setTag(""+CalendarUtils.getOrderPostDate());
//				llCheque.setVisibility(View.GONE);
				tvCashSelect.setEnabled(false);
				tvCashSelect.setClickable(false);
//				SEL_TYPE = CASH;
			}
		});
		
		tvSelectBanks.setTag(-1);
		tvSelectBanks.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) 
			{
				CustomBuilder builder = new CustomBuilder(ReceivePaymentBySalesman.this, "Select Bank", true);
				builder.setSingleChoiceItems(vecBankName, v.getTag(), new CustomBuilder.OnClickListener() 
				{
					@Override
					public void onClick(CustomBuilder builder, Object selectedObject) 
					{
						NameIDDo ObjNameIDDo = (NameIDDo) selectedObject;
						tvSelectBanks.setText(""+ObjNameIDDo.strName);
						tvSelectBanks.setTag(ObjNameIDDo);
						
						if(ObjNameIDDo.strName.equalsIgnoreCase("Other"))
							llBankName.setVisibility(View.VISIBLE);
						else
							llBankName.setVisibility(View.GONE);
						
						builder.dismiss();
		    		}
			   }); 
				builder.show();
			}
		});
		
		btnConfirm_Payment.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				boolean isValid = false;
				if(StringUtils.getFloat(selectedAmount) > 0 )
					isValid = getValidateEnteredData();
				else
					isValid = getValidateReturnEnteredData();
				
				if(isValid)
				{
					btnConfirm_Payment.setEnabled(false);
					btnConfirm_Payment.setClickable(false);
					
					showLoader(getString(R.string.please_wait));
					
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							strReceiptNo = new PaymentDetailDA().getReceiptNo(preference.getStringFromPreference(Preference.SALESMANCODE, ""), preference,0);
							
							if(strReceiptNo != null && !strReceiptNo.equalsIgnoreCase(""))
							{
								paymentHeaderDO						= 	new PaymentHeaderDO();
								paymentHeaderDO.appPaymentId 		=  StringUtils.getUniqueUUID();
								paymentHeaderDO.rowStatus 			=  "1";
								paymentHeaderDO.receiptId 			=  strReceiptNo;
								paymentHeaderDO.preReceiptId 		=  strReceiptNo;
								paymentHeaderDO.paymentDate 		=  CalendarUtils.getCurrentDateTime();
								paymentHeaderDO.siteId 				=  mallsDetails.site;
								paymentHeaderDO.empNo 				=  preference.getStringFromPreference(Preference.EMP_NO, "");
								paymentHeaderDO.amount 				=  ""+StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", ""));
								paymentHeaderDO.currencyCode 		=  curencyCode+"";
								paymentHeaderDO.rate 				=  "1";
								paymentHeaderDO.visitCode 			=  ""+mallsDetails.VisitCode;
								paymentHeaderDO.paymentStatus 		=  "0";
								paymentHeaderDO.status = "0";
								paymentHeaderDO.appPayementHeaderId	= 	paymentHeaderDO.appPaymentId;
								paymentHeaderDO.paymentType 		=	"Collection";
								paymentHeaderDO.salesmanCode 		=	mallsDetails.salesmanCode;
								paymentHeaderDO.vehicleNo 			=	preference.getStringFromPreference(Preference.CURRENT_VEHICLE, "");
								
								//Customer Signature
								Bitmap bitmap 					= getBitmap(viewAgent);
								ByteArrayOutputStream stream 	= new ByteArrayOutputStream();
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
								paymentHeaderDO.customerSignature = Base64.encodeBytes(stream.toByteArray());
								storeImage(bitmap, AppConstants.CUSTOMER_SIGN);
								
								if(PAYMENT_TYPE.equalsIgnoreCase(CASH))
								{
									paymentHeaderDO.vecPaymentDetails.add(getCashDetail());
								}
								else if(PAYMENT_TYPE.equalsIgnoreCase(CHEQUE))
								{
									paymentHeaderDO.vecPaymentDetails.add(getChequeDetail());
								}
								else
								{
									paymentHeaderDO.vecPaymentDetails.add(getCashDetail());
									paymentHeaderDO.vecPaymentDetails.add(getChequeDetail());
								}
							
								for(PendingInvicesDO objInvicesDO : arrInvoiceNumbers)
								{
									PaymentInvoiceDO paymentInvoiceDO = new PaymentInvoiceDO();
									
									paymentInvoiceDO.RowStatus 		= 	"1";
									paymentInvoiceDO.ReceiptId		= 	strReceiptNo;
									paymentInvoiceDO.TrxCode		= 	objInvicesDO.invoiceNo;
									
									if(objInvicesDO.invoiceDate.contains(CalendarUtils.getOrderPostDate()))
									{
										if(objInvicesDO.TRX_TYPE.equalsIgnoreCase(AppConstants.RETURNORDER))
										{
											paymentInvoiceDO.TrxType		= 	"Return Invoice";
										}
										else
										{
											paymentInvoiceDO.TrxType		= 	"Normal Invoice";
										}
									}
									else
										paymentInvoiceDO.TrxType		= 	"+ Invoice";
									
									paymentInvoiceDO.Amount			= 	objInvicesDO.balance;
									paymentInvoiceDO.CurrencyCode	= 	curencyCode+"";
									paymentInvoiceDO.Rate			= 	"1";
									paymentInvoiceDO.PaymentStatus	= 	"0";
									paymentInvoiceDO.PaymentType	= 	PAYMENT_TYPE;
									paymentInvoiceDO.CashDiscount 	= 	"0";
									paymentHeaderDO.vecPaymentInvoices.add(paymentInvoiceDO);
								}
								
								boolean isInserted = new PaymentDetailDA().insertPaymentDetails(paymentHeaderDO , preference.getStringFromPreference(Preference.SALESMANCODE, ""));
								if(isInserted)
								{
									if(arrInvoiceNumbers != null && arrInvoiceNumbers.size() > 0)
										isPaymentDone = new PaymentDetailDA().updatePaymentStatus(arrInvoiceNumbers, "D");
									
									new CustomerDA().updateCustomerProductivity(mallsDetails.site, CalendarUtils.getOrderPostDate(), AppConstants.JOURNEY_CALL,"1");
									
									if(isNetworkConnectionAvailable(ReceivePaymentBySalesman.this))
									{
										if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
											uploadData();
									}
								}
							}
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									try 
									{
										if(tvSelectBanks.getTag() != null && tvSelectBanks.getTag() instanceof NameIDDo)  
										{
											NameIDDo nameIDDo = (NameIDDo) tvSelectBanks.getTag();
											if(nameIDDo != null && nameIDDo.strName.equalsIgnoreCase("Other"))
												loadSplashData();
										}
									}
									catch (Exception e) 
									{
										e.printStackTrace();
									}
									
									btnConfirm_Payment.setEnabled(true);
									btnConfirm_Payment.setClickable(true);
									hideLoader();
									
									if(isPaymentDone)
										resetLayoutVisibility();
									else
										showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Payment sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "");
								}
							});
						}
					}).start();
				}
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showOrderCompletePopup();
			}
		});
		
		if(mallsDetails.customerPaymentType != null && mallsDetails.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
			btnCancel.setVisibility(View.GONE);
		
		btnCancel.setVisibility(View.GONE);
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Are you sure you want to cancel the payment process?", getString(R.string.Yes), getString(R.string.No), "paymentprocess");
			}
		});
			
		tvDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		//Button for Print
		btnPrint.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
    				Intent intent = new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
    				intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
    				intent.putExtra("totalAmount", totalAmount);
    				intent.putExtra("strReceiptNo", strReceiptNo);
    				intent.putExtra("selectedAmount", selectedAmount);
    				intent.putExtra("paymentHeaderDO", paymentHeaderDO);
    				intent.putExtra("mallsDetails", mallsDetails);
    				intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
    				startActivityForResult(intent, 1000);
			}
		});
		
		btnPrintInvoice.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
    			intent.putExtra("mallsDetails", mallsDetails);
    			intent.putExtra("totalPrice", selectedAmount);
    			intent.putExtra("OrderId", strInvoiceNo);
    			startActivityForResult(intent, 1000);
			}
		});
		
		btnPaymentSignClear.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				isSigned = false;
				llCustomerSignature.removeAllViews();
				viewAgent = new MyView(ReceivePaymentBySalesman.this);
				viewAgent.setDrawingCacheEnabled(true);
				viewAgent.setDrawingCacheQuality(EditText.DRAWING_CACHE_QUALITY_HIGH);
				llCustomerSignature.addView(viewAgent, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		});
		
		//Adding the Layouts
		customScrollView  = new CustomScrollView(this, llPaymentLayout);
		llAddPaymentLayout.addView(customScrollView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		llBody.addView(llReceivePayment,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		
		super.btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(isPaymentDone)
				{
					btnContinue.performClick();
				}
				else
					showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Are you sure you want to cancel the payment process?", getString(R.string.Yes), getString(R.string.No), "paymentprocess");
			}
		});
		
		tvCash.performClick();
	
		etChequeAmount.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(ENTERED_TYPE == null)
					ENTERED_TYPE 	= 	CHEQUE;
				float amount  		= 	Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", "")));
				float cashamount  	= 	StringUtils.getFloat(etCashAmount.getText().toString().replace(",", ""));
				float mAmount	 	= 	StringUtils.getFloat(s.toString());
				
				if((mAmount + cashamount)> amount)
				{
					showToast("Entered amount should not be greater than total amount.");
					if(!s.toString().equals(""))
						etChequeAmount.setText("");
					
					etRemainingAmount.setText(""+decimalFormat.format((amount - cashamount )*amountType));
				}
				else
					etRemainingAmount.setText(""+decimalFormat.format((amount - (mAmount + cashamount))*amountType));
			}	
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		etCashAmount.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				if(ENTERED_TYPE == null)
					ENTERED_TYPE 	= 	CASH;
				float amount  		= 	Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", "")));
				float chequeamount  = 	StringUtils.getFloat(etChequeAmount.getText().toString().replace(",", ""));
				float mAmount	 	= 	StringUtils.getFloat(s.toString());
				
				if((mAmount + chequeamount)> amount)
				{
					showToast("Entered amount should not be greater than total amount.");
					if(!s.toString().equals(""))
						etCashAmount.setText("");
					etRemainingAmount.setText(""+decimalFormat.format((amount - chequeamount )*amountType));
				}
				else
					etRemainingAmount.setText(""+decimalFormat.format((amount - (mAmount + chequeamount))*amountType));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
			}
		});
		
		if(amountType == -1)
		{
			tvMinusCash.setVisibility(View.VISIBLE);
			tvMinusCheck.setVisibility(View.VISIBLE);
		}
		
		setTypeFaceRobotoNormal(llReceivePayment);
		
		if(mallsDetails.channelCode != null && mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			tvSignatureTitle.setText("Received By");
		else
			tvSignatureTitle.setText(getString(R.string.Customer_Signature));
		
		if(StringUtils.getFloat(selectedAmount) <= 0 )
		{
			tvCheque.setClickable(false);
			etCashAmount.setText("0");
			etCashAmount.setEnabled(false);
			etCashAmount.setFocusable(false);
			etCashAmount.setFocusableInTouchMode(false);
		}
	}
	
	private PaymentDetailDO getChequeDetail() 
	{
		PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
		NameIDDo nameIDDo 				= 	(NameIDDo) tvSelectBanks.getTag();
		paymentDetailDO.RowStatus 		= 	"1";
		paymentDetailDO.ReceiptNo		= 	strReceiptNo;
		paymentDetailDO.LineNo			= 	"2";
		paymentDetailDO.PaymentTypeCode	= 	CHEQUE;
		paymentDetailDO.BankCode		= 	nameIDDo.strId;
		
		if(!nameIDDo.strName.equalsIgnoreCase("Other"))
			paymentDetailDO.BankName		= 	nameIDDo.strName;
		else
			paymentDetailDO.BankName		= 	etBankName.getText().toString();
		
		paymentDetailDO.ChequeDate		= 	tvDate.getTag().toString();
		paymentDetailDO.ChequeNo		= 	etCheque_Details.getText().toString();
		paymentDetailDO.CCNo			= 	"";
		paymentDetailDO.CCExpiry		= 	"";
		paymentDetailDO.PaymentStatus	= 	"0";
		paymentDetailDO.PaymentNote		= 	CHEQUE;
		if(!nameIDDo.strName.equalsIgnoreCase("Other"))
			paymentDetailDO.UserDefinedBankName= nameIDDo.strName;
		else
			paymentDetailDO.UserDefinedBankName	= 	etBankName.getText().toString();
	
		paymentDetailDO.Status			= 	"0";
		paymentDetailDO.Amount			= 	""+StringUtils.getFloat(etChequeAmount.getText().toString())*amountType;
		return paymentDetailDO;
	}

	private PaymentDetailDO getCashDetail()
	{
		PaymentDetailDO paymentDetailDO = new PaymentDetailDO();
		paymentDetailDO.RowStatus 		= 	"1";
		paymentDetailDO.ReceiptNo		= 	strReceiptNo;
		paymentDetailDO.LineNo			= 	"1";
		paymentDetailDO.PaymentTypeCode	= 	CASH;
		paymentDetailDO.BankCode		= 	"";
		paymentDetailDO.ChequeDate		= 	"";
		paymentDetailDO.ChequeNo		= 	"";
		paymentDetailDO.CCNo			= 	"";
		paymentDetailDO.CCExpiry		= 	"";
		paymentDetailDO.PaymentStatus	= 	"0";
		paymentDetailDO.PaymentNote		= 	CASH;
		paymentDetailDO.UserDefinedBankName= "";
		paymentDetailDO.Status			= 	"0";
		paymentDetailDO.Amount			= 	""+StringUtils.getFloat(etCashAmount.getText().toString().replace(",", ""))*amountType;	
		return paymentDetailDO;
	}
	
	/** initializing all the Controls  of ReceivePaymentByPreseller class **/
	public void intialiseControls()
	{
		// all the fields from the receive_payment.xml is taken here
		llRemainingAmount				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llRemainingAmount);
		llCheque						=	(LinearLayout)llPaymentLayout.findViewById(R.id.llCheque);
		llBankName						=	(LinearLayout)llPaymentLayout.findViewById(R.id.llBankName);
		llInvoiceNumbers				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llInvoiceNumbers);
		tvCheque						=	(TextView)llPaymentLayout.findViewById(R.id.tvCheque);
		tvCash							=	(TextView)llPaymentLayout.findViewById(R.id.tvCash);
		tvSelectBanks					=	(TextView)llPaymentLayout.findViewById(R.id.tvSelectBanks);
		etCheque_Details				=	(EditText)llPaymentLayout.findViewById(R.id.etCheque_Details);
		etBankName						=	(EditText)llPaymentLayout.findViewById(R.id.etBankName);
		
		tvMinusCash						=	(TextView)llPaymentLayout.findViewById(R.id.tvMinusCash);
		tvMinusCheck					=	(TextView)llPaymentLayout.findViewById(R.id.tvMinusCheck);
		TextView tvChequeNumber			=	(TextView)llPaymentLayout.findViewById(R.id.tvChequeNumber);
		
		llCustomerSignature				=	(LinearLayout)llPaymentLayout.findViewById(R.id.llCustomerSignature);
		tvDate							=	(TextView)llPaymentLayout.findViewById(R.id.tvDateSelect);
		TextView tvPayment_Details		=	(TextView)llPaymentLayout.findViewById(R.id.tvPayment_Details);
		TextView tvMode					=	(TextView)llPaymentLayout.findViewById(R.id.tvModePayment);
		tvSignatureTitle				=	(TextView)llPaymentLayout.findViewById(R.id.tvSignatureTitle);
		TextView tvBankName				=	(TextView)llPaymentLayout.findViewById(R.id.tvBankName);
		TextView tvEnterBankName		=	(TextView)llPaymentLayout.findViewById(R.id.tvEnterBankName);
		
		tvDateTitle						=	(TextView)llPaymentLayout.findViewById(R.id.tvDateTitle);
		llCash 							= 	(LinearLayout)llPaymentLayout.findViewById(R.id.llCash);
		llCustomer_Signature 			= 	(LinearLayout)llPaymentLayout.findViewById(R.id.llCustomer_Signature);
		tvAmount						= 	(TextView)llPaymentLayout.findViewById(R.id.tvAmount);
		etTotalAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.ettvAmount);
		tvCurrencyType					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCurrencyType);
		btnPaymentSignClear				= 	(Button)llPaymentLayout.findViewById(R.id.btnPaymentSignClear);
		TextView tvUnPaidInvoice		= 	(TextView)llPaymentLayout.findViewById(R.id.tvUnPaidInvoice);
		TextView tvAmountDueInvoice		= 	(TextView)llPaymentLayout.findViewById(R.id.tvAmountDueInvoice);
		tvTotalAmountValue				= 	(TextView)llPaymentLayout.findViewById(R.id.tvTotalAmountValue);
		TextView tvTotalAmountText		= 	(TextView)llPaymentLayout.findViewById(R.id.tvTotalAmountText);
		
		tvCashDateTitle					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashDateTitle);
		tvCashSelect					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelect);
		tvCashSelAmount					= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelAmount);
		tvCashSelCurrencyType			= 	(TextView)llPaymentLayout.findViewById(R.id.tvCashSelCurrencyType);
		
		tvRemaining						= 	(TextView)llPaymentLayout.findViewById(R.id.tvRemaining);
		tvRemainingAED					= 	(TextView)llPaymentLayout.findViewById(R.id.tvRemainingAED);
		etRemainingAmount				= 	(EditText)llPaymentLayout.findViewById(R.id.etRemainingAmount);
		
		etCashAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.etCashAmount);
		etChequeAmount					= 	(EditText)llPaymentLayout.findViewById(R.id.etChequeAmount);
		tvSelCurrencyType				= 	(TextView)llPaymentLayout.findViewById(R.id.tvSelCurrencyType);
		tvSelAmount						= 	(TextView)llPaymentLayout.findViewById(R.id.tvSelAmount);
		
		//Main layout
		llAddPaymentLayout				= 	(LinearLayout)llReceivePayment.findViewById(R.id.llAddPaymentLayout);
		tvRecieveHead					=	(TextView)llReceivePayment.findViewById(R.id.tvRecieveHead);
		btnConfirm_Payment				=	(Button)llReceivePayment.findViewById(R.id.btnConfirm_Payment);
		btnCancel						=	(Button)llReceivePayment.findViewById(R.id.btnCancel);
		btnPrint						=	(Button)llReceivePayment.findViewById(R.id.btnPrint);
		
		btnContinue						=	(Button)llReceivePayment.findViewById(R.id.btnContinue);
		btnPrintInvoice					=	(Button)llReceivePayment.findViewById(R.id.btnPrintInvoice);
		
		//setting Type-faces here to all the fields
		/*etBankName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvUnPaidInvoice.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAmountDueInvoice.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalAmountValue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvTotalAmountText.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPaymentSignClear.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCurrencyType.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrint.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etTotalAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);	
		tvDateTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvBankName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvEnterBankName.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCheque.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCash.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etCheque_Details.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnConfirm_Payment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnCancel.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvPayment_Details.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvMode.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvSignatureTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvRecieveHead.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvDate.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvSelectBanks.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPrintInvoice.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etChequeAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvSelCurrencyType.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvSelAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvChequeNumber.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCashDateTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCashSelect.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCashSelAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvCashSelCurrencyType.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etCashAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		tvRemaining.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tvRemainingAED.setTypeface(AppConstants.Roboto_Condensed_Bold);
		etRemainingAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);*/
		
		btnPrint.setVisibility(View.GONE);
		btnPrintInvoice.setVisibility(View.GONE);
		
		if(ENTERED_TYPE == null)
			llRemainingAmount.setVisibility(View.GONE);
	}
	
	public class MyView extends View 
	{
		private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        float x,y;
        
        public MyView(Context c)
        {
            super(c);
            Display display = getWindowManager().getDefaultDisplay(); 
            int width = display.getWidth();
            int height = display.getHeight();
            
            if(mBitmap != null)
            {
            	mBitmap.recycle();
            	mBitmap = null;
            	System.gc();
            }
            
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
           
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setDither(true);
            mBitmapPaint.setColor(Color.BLACK);
            mBitmapPaint.setStyle(Paint.Style.STROKE);
            mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
            mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
            mBitmapPaint.setStrokeWidth(lastthickValue);
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
        	customScrollView.setScrollable(false);
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
	      customScrollView.setScrollable(true);
	      isSigned = true;
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
	@Override
	protected Dialog onCreateDialog(int id) 
    {
		//getting current dateofJorney from Calendar
	     Calendar c = 	Calendar.getInstance();
	     int cyear 	= 	c.get(Calendar.YEAR);
	     int cmonth = 	c.get(Calendar.MONTH);
	     int cday 	=	c.get(Calendar.DAY_OF_MONTH);
	     
	     switch (id) 
	     {
		     case DATE_DIALOG_ID:
		      	return new DatePickerDialog(this, DateListener,  cyear, cmonth, cday);
	     }
		 return null;
	  }
		
	/** method for dateofJorney picker **/
	private DatePickerDialog.OnDateSetListener DateListener = new DatePickerDialog.OnDateSetListener()
    {
	    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	    {
	    	//getting current date from Calendar
		     Calendar currentCal = 	Calendar.getInstance();
		     int cyear 			 = 	currentCal.get(Calendar.YEAR);
		     int cmonth 		 = 	currentCal.get(Calendar.MONTH);
		     int cday 			 =	currentCal.get(Calendar.DAY_OF_MONTH);
		     currentCal.set(cyear, cmonth, cday);
		     //selected date 
		     Calendar selectedCal = Calendar.getInstance();
		     selectedCal.set(year, monthOfYear, dayOfMonth);
//		     if(currentCal.after(selectedCal))
//		     {
//		    	 showCustomDialog(ReceivePaymentByPreseller.this, getString(R.string.warning), "Date should not be before current date.", getString(R.string.OK), null, "");
//		     }
//		     else
//		     {
	    	 tvDate.setTag(year+"-"+((monthOfYear+1)< 10?"0"+(monthOfYear+1):(monthOfYear+1))+"-"+((dayOfMonth)<10?"0"+(dayOfMonth):(dayOfMonth)));
	    	 strChequeDate = CalendarUtils.getMonthFromNumber(monthOfYear+1)+" "+dayOfMonth+CalendarUtils.getDateNotation(dayOfMonth)+", "+year;
	    	 tvDate.setText(strChequeDate);
//		     }
	    }
    };
    
  
    @Override
    public void onButtonYesClick(String from)
    {
    	super.onButtonYesClick(from);
    	if(from.equalsIgnoreCase("confirm"))
    	{
    		if(!isFromArCollection)
    			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served");
    		else
    		{
    			Intent intentBrObj = new Intent();
    			intentBrObj.setAction(AppConstants.ACTION_GOTO_AR);
    			sendBroadcast(intentBrObj);
    		}
    	}
    	else if(from.equalsIgnoreCase("served"))
		{
    		if(isExceed)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    		else
    		{
				Intent intentBrObj = new Intent();
				intentBrObj.setAction(AppConstants.ACTION_HOUSE_LIST_NEW);
				sendBroadcast(intentBrObj);
    		}
		}
    	else if(from.equalsIgnoreCase("isFromArCollectionfalse"))
		{
    		setResult(5000);
    		finish();
		}
    	else if(from.equalsIgnoreCase("simplepayment"))
		{
    		showCustomDialog(ReceivePaymentBySalesman.this, "Alert !", "Do you want to create a Return Request ?", "Yes", "No", "ReturnRequest", false);
		}
    	else if(from.equalsIgnoreCase("scroll"))
		{
			if(customScrollView.isScrolled())
			{
				customScrollView.setScrollable(false);
				customScrollView.scrollBy(0, llCustomer_Signature.getTop());
			}
		}
    	else if(from.equalsIgnoreCase("paymentprocess"))
		{
    		if(!isFromArCollection)
    		{
    			showCustomDialog(ReceivePaymentBySalesman.this, "Alert !", "Do you want to create a Return Request?", "Yes", "No", "ReturnRequest", false);
    		}
    		else
    		{
    			finish();
    		}
		}
    	else if(from.equalsIgnoreCase("ReturnRequest"))
		{
    		Intent intent =	new Intent(ReceivePaymentBySalesman.this,  SalesManTakeReturnOrder.class);
			intent.putExtra("name",""+getString(R.string.Capture_Inventory) );
			intent.putExtra("mallsDetails", mallsDetails);
			intent.putExtra("isAdvance", isAdvance);
			startActivity(intent);
		}
    }
    
    @Override
    public void onButtonNoClick(String from)
    {
    	super.onButtonNoClick(from);
		if(from.equalsIgnoreCase("isFromArCollectiontrue"))
		{
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), getString(R.string.you_served_this_customer_successfully_another_order), getString(R.string.Yes), getString(R.string.No), "served");
		}
		else if(from.equalsIgnoreCase("served"))
		{
			performCustomerServed();
		}
		else if(from.equalsIgnoreCase("askCredit"))
		{
			if(mallsDetails.customerPaymentMode.equalsIgnoreCase("CASH"))
				tvCash.performClick();
			else
				tvCheque.performClick();
		}
		else if(from.equalsIgnoreCase("ReturnRequest"))
		{
			showCustomDialog(ReceivePaymentBySalesman.this,getString(R.string.successful), "You have successfully served this customer.", "Ok", null, "served",false);
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(resultCode == 2222)
    	{
    		AppConstants.isServeyCompleted = true;
    	}
    	else if(resultCode == 20000)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.successful), getString(R.string.Your_payment_detail_has_been_prited_successfully), getString(R.string.OK), null , "");
    	else if(resultCode == -10000)
    	{
    		if(mallsDetails.customerPaymentMode.equalsIgnoreCase("CASH"))
    		{
    			tvCheque.setVisibility(View.GONE);
    			tvCash.performClick();
    		}
    		else
    			tvCheque.performClick();
    	}
    }

	@Override
	public void onConnectionException(Object msg) 
	{
	}
	
	@Override
	public void onBackPressed()
	{
		if(llDashBoard != null && llDashBoard.isShown())
			TopBarMenuClick();
		else if(isPaymentDone)
			showOrderCompletePopup();
		else
			finish();
	}

	private void resetLayoutVisibility()
	{
		btnConfirm_Payment.setEnabled(true);
		btnConfirm_Payment.setClickable(true);
		
		btnContinue.setVisibility(View.VISIBLE);
		btnConfirm_Payment.setVisibility(View.GONE);
//		btnPrint.setVisibility(View.VISIBLE);
		
//		if(strInvoiceNo != null)
//			btnPrintInvoice.setVisibility(View.VISIBLE);
		
		btnCancel.setVisibility(View.GONE);
		
		etCashAmount.setEnabled(false);
		etCashAmount.setClickable(false);
		etCashAmount.setFocusableInTouchMode(false);
		
		etCheque_Details.setEnabled(false);
		etCheque_Details.setClickable(false);
		etCheque_Details.setFocusableInTouchMode(false);
		
		etChequeAmount.setEnabled(false);
		etChequeAmount.setClickable(false);
		etChequeAmount.setFocusableInTouchMode(false);
		
		tvSelectBanks.setEnabled(false);
		tvSelectBanks.setClickable(false);
		
		tvDate.setEnabled(false);
		tvDate.setClickable(false);
		
		btnPaymentSignClear.setEnabled(false);
		btnPaymentSignClear.setClickable(false);
		
		showOrderCompletePopup();
	}
	
	private boolean getValidateEnteredData()
	{
		boolean isValid = false;
		float cashAmount, chequeAmount, totalAmount, remAmount;
		
		totalAmount 	= 	Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", "")));
		cashAmount 		= 	StringUtils.getFloat(etCashAmount.getText().toString().replace(",", ""));
		chequeAmount 	= 	StringUtils.getFloat(etChequeAmount.getText().toString().replace(",", ""));
		remAmount		=	totalAmount - (cashAmount + chequeAmount);
		
		if(!isCash && !isCheque)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select atleast one payment mode.", getString(R.string.OK), null, "");
		
		else if(isCash && cashAmount <=0)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter amount.", getString(R.string.OK), null, "");
		
		else if((isCheque || chequeAmount > 0)  && etCheque_Details.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter cheque number.", getString(R.string.OK), null, "");
		
		else if((isCheque || chequeAmount > 0)  && tvSelectBanks.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select bank name.", getString(R.string.OK), null, "");
		
		else if((isCheque || chequeAmount > 0)  && tvSelectBanks.getText().toString().equalsIgnoreCase("Other") && etBankName.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter bank name.", getString(R.string.OK), null, "");
		
		else if((isCheque || chequeAmount > 0) && tvDate.getText().toString().equalsIgnoreCase(""))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please select cheque date.", getString(R.string.OK), null, "");
		
		else if((isCash  && cashAmount <=0 && remAmount > 0) || (isCheque  && chequeAmount <=0 && remAmount > 0))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please enter amount.", getString(R.string.OK), null, "");
		
		else if(totalAmount > (chequeAmount + cashAmount))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Entered amount is less than invoice amount. Please enter correct amount.", getString(R.string.OK), null, "");
		
		else if(totalAmount < (chequeAmount + cashAmount))
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Entered amount is greater than invoice amount. Please enter correct amount.", getString(R.string.OK), null, "");
		
		else if(!isSigned)
		{
			if(mallsDetails.channelCode.equalsIgnoreCase(AppConstants.CUSTOMER_CHANNEL_PARLOUR))
			{
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please take Receiver's signature.", getString(R.string.OK), null, "");
			}
			else
			{
				showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please take Customer's signature.", getString(R.string.OK), null, "");
			}
		}
		else 
		{
			isValid = true; 
			if(chequeAmount > 0 && cashAmount > 0)
				PAYMENT_TYPE = CASH + CHEQUE;
			else if(cashAmount > 0)
				PAYMENT_TYPE = CASH;
			else if(chequeAmount > 0)
				PAYMENT_TYPE = CHEQUE;
		}
		
		return isValid;
	}
	
	private boolean getValidateReturnEnteredData()
	{
		boolean isValid = false;
		totalAmount 	= 	Math.abs(StringUtils.getFloat(etTotalAmount.getText().toString().replace(",", "")));
		
		if(!isSigned)
			showCustomDialog(ReceivePaymentBySalesman.this, getString(R.string.warning), "Please take Customer's signature.", getString(R.string.OK), null, "");
		else 
		{
			isValid = true; 
			PAYMENT_TYPE = CASH;
		}
		return isValid;
	}
	
	private Bitmap getBitmap(MyView myView)
	{
		Bitmap bitmap = myView.getDrawingCache(true);
		return bitmap;
	}
	
	private void showOrderCompletePopup()
	{
		View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
		final CustomDialog mCustomDialog = new CustomDialog(ReceivePaymentBySalesman.this, view, preference
				.getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
				LayoutParams.WRAP_CONTENT, true);
		mCustomDialog.setCancelable(true);
		
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
		
		tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
		tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
		btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
		
		if(isFromArCollection)
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
//				Intent intent = new Intent(ReceivePaymentBySalesman.this, TaskToDoActivity.class);
//				Intent intent = new Intent(ReceivePaymentBySalesman.this, ConsumerBehaviourSurveyActivityNew.class);
//				intent.putExtra("object", mallsDetails);
//				startActivityForResult(intent, 2222);
				
				Intent intent = new Intent(ReceivePaymentBySalesman.this, ServeyListActivity.class);
				startActivity(intent);
				
			}
		});
		
		btnPlaceNewOrder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
//				Intent intent = new Intent(ReceivePaymentBySalesman.this, CaptureInventoryCategory.class);
//				Intent intent = new Intent(ReceivePaymentBySalesman.this, StoreCheckActivity.class);
				Intent intent = new Intent(ReceivePaymentBySalesman.this, StoreCheckCatagoryActivity.class);
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
				Intent intent = new Intent(ReceivePaymentBySalesman.this, WoosimPrinterActivity.class);
				intent.putExtra("CALLFROM", CONSTANTOBJ.PAYMENT_RECEIPT);
				intent.putExtra("totalAmount", totalAmount);
				intent.putExtra("strReceiptNo", strReceiptNo);
				intent.putExtra("selectedAmount", selectedAmount);
				intent.putExtra("paymentHeaderDO", paymentHeaderDO);
				intent.putExtra("mallsDetails", mallsDetails);
				intent.putExtra("arrInvoiceNumbers", arrInvoiceNumbers);
				startActivityForResult(intent, 1000);
			}
		});
		
		btn_popup_collectpayment.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.print_order), null, getResources().getDrawable(R.drawable.check1_new), null);
		btn_popup_collectpayment.setClickable(false);
		btn_popup_collectpayment.setEnabled(false);
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
				Intent intent = new Intent(ReceivePaymentBySalesman.this, TaskToDoActivity.class);
				intent.putExtra("object", mallsDetails);
				startActivity(intent);
			}
		});
		btn_popup_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				mCustomDialog.dismiss();
				onButtonYesClick("served");
				
//				if(isReturnOrder)
//					onButtonYesClick("confirm");
//				else if(!isFromArCollection)
//					onButtonYesClick("simplepayment");
//				else
//					onButtonYesClick("confirm");
			}
		});
		
		if(isFromArCollection)
		{
			btn_popup_survey.setVisibility(View.GONE);
			btn_popup_collectpayment.setVisibility(View.GONE);
			btn_popup_returnreq.setVisibility(View.GONE);
			btn_popup_task.setVisibility(View.GONE);
		}
		
		try{
		if (!mCustomDialog.isShowing())
			mCustomDialog.show();
		}catch(Exception e){}
	}
}
