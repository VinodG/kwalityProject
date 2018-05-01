package com.winit.sfa.salesman;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.alseer.pinch.SignatureView;
import com.winit.alseer.salesman.adapter.OrderPreviewAdapter;
import com.winit.alseer.salesman.adapter.ReturnReasonAdapter;
import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Base64;
import com.winit.alseer.salesman.common.CONSTANTOBJ;
import com.winit.alseer.salesman.common.CustomDialog;
import com.winit.alseer.salesman.common.CustomListView;
import com.winit.alseer.salesman.common.LocationUtility;
import com.winit.alseer.salesman.common.LocationUtility.LocationResult;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CaptureInventryDA;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDA;
import com.winit.alseer.salesman.dataaccesslayer.OrderDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.PaymentDetailDA;
import com.winit.alseer.salesman.dataaccesslayer.StatusDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.StatusDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.Tools;
import com.winit.alseer.salesman.webAccessLayer.ConnectionHelper.ConnectionExceptionListener;
import com.winit.kwalitysfa.salesman.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class SalesmanOrderPreview extends BaseActivity implements ConnectionExceptionListener, LocationResult {
    private LinearLayout llInvoicetyp, llcashcustomer, llcreditcustomer, llOrderPreview, llMiddleLayout, llOrderPreviewBottom, llEsignature, llSignatures, llStatementDiscValue,
            llHeaderLayout, llPresellerSignature, llCustomerSignature, llAdvanceOrderDate, llAmountLimit,
            llCrdAmountLimit, llNotes, llFOCTotalQty, llOrderValue,llNetValue, llDiscValue, llSplDiscValue,llVATValue;
    private TextView dot, tvHeaderPreview, tvItemName, tvOrderPreviewunits, tvLu, tvDiscount, tvInvoiceAmount, tvPrice, tvTotalPrice, tvItemUOM, tvToday_Date, tvDelivery_Date, tvOrderValue, tvDiscValue, tvStatementDiscValue, tvTotalValue,tvNetOrderValue, tvFOCTotalQty, tvOrderValueTitle,tvNetValueTitle,tvVATOrderValue,tvVATValueTitle,
            tvOrderQtyColon, tvOrderColon,tvNETColon, tvDiscountColon, tvTotalQty, llDiscount, tvSplDiscountPer, tvSplDiscValue, llStatementDiscount,
            tvSplDiscountColon, tvSpclDiscount, tvInvoiceAmountTitle, tvInvoicetype;
    private TextView tvMasafiLogoTitleCustomer, tvAddressCustomer;
    private Button btnFinalize, btnSave, btnPrintSalesOrder, btnContinue, /*btnPrintMerchant , */
            btnOrderPreviewContinue, btnPrintSalesOrderMerchant;
    private CustomListView lvPreviewOrder;
    private OrderPreviewAdapter orderPreviewAdapter;
    public JourneyPlanDO mallsDetailsOrderPrev;
    private SignatureView customerSignature, presellerSignature;
    private Paint mPaint;
    private boolean isFromPayment;
    private TrxHeaderDO trxHeaderDO;
    private String dateFormat = "", dateFormatforOrder = "";
    public int TRX_TYPE = 0, TRX_SUB_TYPE = 0;
    private LocationUtility locationUtility;
    private String orderID = "";
    private EditText etNotes;
    private String orderType = "Confirm Order";
    private float totalPaidAmount = 0.0f;
    private boolean isEditable = false, isfromsalesorder = false;
    private boolean isFromOrderSummary = false;
    private TextView tvAvailableLimitValue, tvCreditLimitValue;
    private CustomerCreditLimitDo creditLimit;
    private String Invoice_Type = "";
    private String select_Invoice_Type = "";
    private int focItemCount = 0;
    private LinearLayout llReasonForReturn;
    private int Division = 0;
    private ImageView ivCredit, ivCash;
    boolean cash = false;
    boolean credit = false;
    String code = "";
    String LPONo = "", Narration = "";
    private final int START_DATE_DIALOG_ID_FROM = 1;
    private int monthFrom, yearFrom, dayFrom;
    @Override
    public void initialize() {
        //inflate the preview_order_list layout
        llOrderPreview = (LinearLayout) getLayoutInflater().inflate(R.layout.preview_order_list, null);
        llBody.addView(llOrderPreview, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        if (getIntent().getExtras() != null) {
            mallsDetailsOrderPrev = (JourneyPlanDO) getIntent().getExtras().get("mallsDetails");
            trxHeaderDO = (TrxHeaderDO) getIntent().getExtras().get("trxHeaderDO");
            TRX_TYPE = trxHeaderDO.trxType;
            TRX_SUB_TYPE = trxHeaderDO.trxSubType;
            preTrxStatus = trxHeaderDO.trxStatus;
            trxHeaderDO.preTrxCode = trxHeaderDO.trxCode;

            if (getIntent().hasExtra("isFromPayment"))
                isFromPayment = getIntent().getExtras().getBoolean("isFromPayment");

            if (getIntent().hasExtra("isFromOrderSummary"))
                isFromOrderSummary = true;

            if (getIntent().hasExtra("SalesOrder"))
                isfromsalesorder = true;

            if (getIntent().hasExtra("Invoice_Type"))
                Invoice_Type = (String) getIntent().getExtras().get("Invoice_Type");

            if (getIntent().hasExtra("focItemCount"))
                focItemCount = (int) getIntent().getIntExtra("focItemCount", 0);

            if (getIntent().hasExtra(AppConstants.DIVISION))
                Division = getIntent().getExtras().getInt(AppConstants.DIVISION);
        }

        trxCodeOld = trxHeaderDO.trxCode;

        loadPricing();
        InitializeControls();
        setHeader();
        ivDivider.setVisibility(View.VISIBLE);
        setTypeFaceRobotoNormal(llOrderPreview);
        setTypeFaceRobotoBold(llEsignature);
        btnPrintSalesOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnFinalize.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnSave.setTypeface(AppConstants.Roboto_Condensed_Bold);


        if (mallsDetailsOrderPrev.customerPaymentType != null && mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
            btnSave.setVisibility(View.VISIBLE);
            btnFinalize.setVisibility(View.GONE);
            llNotes.setVisibility(View.GONE);
        }

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
            llNotes.setVisibility(View.VISIBLE);
        else
            llNotes.setVisibility(View.GONE);

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
            etNotes.setText(trxHeaderDO.reason);
            etNotes.setClickable(false);

            btnSave.setVisibility(View.GONE);
            tvFOCTotalQty.setText(focItemCount + "");
            tvInvoiceAmountTitle.setText("FOC Amount");

            llFOCTotalQty.setVisibility(View.VISIBLE);
            tvOrderQtyColon.setVisibility(View.VISIBLE);
            tvTotalQty.setVisibility(View.VISIBLE);

            tvOrderValueTitle.setVisibility(View.GONE);

            tvNetValueTitle.setVisibility(View.GONE);
            tvVATValueTitle.setVisibility(View.GONE);
            btnPrintSalesOrder.setVisibility(View.GONE);
            llDiscount.setVisibility(View.GONE);
            llStatementDiscount.setVisibility(View.GONE);
            dot.setVisibility(View.GONE);
            llStatementDiscValue.setVisibility(View.GONE);
            llOrderValue.setVisibility(View.GONE);
            llNetValue.setVisibility(View.GONE);
            llVATValue.setVisibility(View.GONE);
            llDiscValue.setVisibility(View.GONE);
            tvDiscountColon.setVisibility(View.GONE);
            tvOrderColon.setVisibility(View.GONE);
            tvNETColon.setVisibility(View.GONE);
            tvSpclDiscount.setVisibility(View.GONE);
            llSplDiscValue.setVisibility(View.GONE);
            tvSplDiscountColon.setVisibility(View.GONE);
        } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())//tblOfflineData
        {
            btnSave.setVisibility(View.GONE);
//			tvSpclDiscount.setVisibility(View.GONE);
//			llSplDiscValue.setVisibility(View.GONE);
            tvSplDiscountColon.setVisibility(View.VISIBLE);
            tvNETColon.setVisibility(View.VISIBLE);
            llFOCTotalQty.setVisibility(View.GONE);
            tvOrderQtyColon.setVisibility(View.GONE);
            tvTotalQty.setVisibility(View.GONE);
        } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())//tblOfflineData
        {
            llFOCTotalQty.setVisibility(View.GONE);
            tvOrderQtyColon.setVisibility(View.GONE);
            tvTotalQty.setVisibility(View.GONE);

            tvOrderValueTitle.setVisibility(View.VISIBLE);
            tvNetValueTitle.setVisibility(View.VISIBLE);
            tvVATValueTitle.setVisibility(View.VISIBLE);
            llDiscount.setVisibility(View.VISIBLE);
            //llStatementDiscount.setVisibility(View.VISIBLE);
            llOrderValue.setVisibility(View.VISIBLE);
            llNetValue.setVisibility(View.VISIBLE);
            llVATValue.setVisibility(View.VISIBLE);
            llDiscValue.setVisibility(View.VISIBLE);
            tvDiscountColon.setVisibility(View.VISIBLE);
            tvOrderColon.setVisibility(View.VISIBLE);
            tvNETColon.setVisibility(View.VISIBLE);
            tvSpclDiscount.setVisibility(View.VISIBLE);
            llSplDiscValue.setVisibility(View.VISIBLE);
            tvSplDiscountColon.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        } else {
            llFOCTotalQty.setVisibility(View.GONE);
            tvOrderQtyColon.setVisibility(View.GONE);
            tvTotalQty.setVisibility(View.GONE);

            tvOrderValueTitle.setVisibility(View.VISIBLE);
            tvNetValueTitle.setVisibility(View.VISIBLE);
            tvVATValueTitle.setVisibility(View.VISIBLE);
            llDiscount.setVisibility(View.VISIBLE);
//			llStatementDiscount.setVisibility(View.VISIBLE);
            llOrderValue.setVisibility(View.VISIBLE);
            llNetValue.setVisibility(View.VISIBLE);
            llVATValue.setVisibility(View.VISIBLE);
            llDiscValue.setVisibility(View.VISIBLE);
            tvDiscountColon.setVisibility(View.VISIBLE);
            tvOrderColon.setVisibility(View.VISIBLE);
            tvNETColon.setVisibility(View.VISIBLE);
            tvSpclDiscount.setVisibility(View.VISIBLE);
            llSplDiscValue.setVisibility(View.VISIBLE);
            tvSplDiscountColon.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
        }


        ((Button) llOrderPreview.findViewById(R.id.btnOrdersheetVerify)).setTypeface(Typeface.DEFAULT_BOLD);

        tvItemName.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvOrderPreviewunits.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvTotalPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvPrice.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvItemUOM.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvInvoiceAmount.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvDiscount.setTypeface(AppConstants.Roboto_Condensed_Bold);

        tvMasafiLogoTitleCustomer.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tvAddressCustomer.setTypeface(AppConstants.Roboto_Condensed);

        tvMasafiLogoTitleCustomer.setText(mallsDetailsOrderPrev.siteName + " [" + mallsDetailsOrderPrev.site + "]");
        tvAddressCustomer.setText(getAddress(mallsDetailsOrderPrev));
        lvPreviewOrder = new CustomListView(SalesmanOrderPreview.this);
        llMiddleLayout.addView(lvPreviewOrder, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        lvPreviewOrder.setCacheColorHint(0);
        lvPreviewOrder.setVerticalFadingEdgeEnabled(false);
        lvPreviewOrder.setVerticalScrollBarEnabled(false);
        lvPreviewOrder.setDivider(null);
        lvPreviewOrder.addHeaderView(llHeaderLayout, null, false);

        if (trxHeaderDO != null && trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
            isEditable = false;
        else if (isFromOrderSummary)
            isEditable = false;
        else
            isEditable = true;

        sortTrxDetail(trxHeaderDO.arrTrxDetailsDOs);

        lvPreviewOrder.setAdapter(orderPreviewAdapter = new OrderPreviewAdapter(SalesmanOrderPreview.this, trxHeaderDO.arrTrxDetailsDOs, isEditable, mallsDetailsOrderPrev, trxHeaderDO.trxType));

        llOrderPreviewBottom.addView(llEsignature, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        /*if (trxHeaderDO != null) {
            filterAvailableItesm(trxHeaderDO.arrTrxDetailsDOs);
        }*/

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDisableButton(btnSave);

                btnContinue.setVisibility(View.GONE);

                if (TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_SALES_ORDER())
                    trxHeaderDO.trxSubType = TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER();

                trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SAVED();
                LogUtils.debug("trxStatus", trxHeaderDO.trxStatus + "");
                postOrder();
            }
        });

        if ((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) &&
                mallsDetailsOrderPrev.customerPaymentType != null &&
                mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
            btnContinue.setText("Continue");
            btnContinue.setVisibility(View.GONE);
            btnFinalize.setVisibility(View.VISIBLE);
        } else if ((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) &&
                mallsDetailsOrderPrev.customerPaymentType != null &&
                mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) /*&&
                preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)*/) {
            btnContinue.setText("Continue");
            btnContinue.setVisibility(View.GONE);
            btnFinalize.setVisibility(View.VISIBLE);
        } else {
            btnContinue.setVisibility(View.GONE);
        }

        btnFinalize.setTag("Finalize");
        btnFinalize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFinalize.setClickable(false);
                btnFinalize.setEnabled(false);

//				if(trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED() || trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
//					trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SAVED_FINALIZED();
//				else if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
//					trxHeaderDO.trxStatus  = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
//				else
//					trxHeaderDO.trxStatus  = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED();
//				
                if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
                    if (validateSelectedDate(yearTo, monthOfYearTo, dayOfMonthTo)) ///change
                        finalizeOrder();
                } else
                    finalizeOrder();
            }
        });

        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trxHeaderDO != null && !TextUtils.isEmpty(trxHeaderDO.trxCode))
                    btnOrderPreviewContinue.performClick();
                else
                    finish();
            }
        });

        btnOrderPreviewContinue.setText("Continue ");
        btnOrderPreviewContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderCompletePopup(trxHeaderDO != null ? trxHeaderDO.trxStatus : 0);
            }
        });

        //btnPrintSalesOrder.setText(" Print ");
        btnPrintSalesOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SalesmanOrderPreview.this, WoosimPrinterActivity.class);
                intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES_PREVIEW);
                intent.putExtra("trxHeaderDO", trxHeaderDO);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                startActivity(intent);
            }
        });

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);


        tvDelivery_Date.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tvDelivery_Date);
            }
        });

        tvHeaderPreview.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnFinalize.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnPrintSalesOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnOrderPreviewContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnContinue.setTypeface(AppConstants.Roboto_Condensed_Bold);

        creditLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailsOrderPrev.site);
        if (creditLimit != null) {
            tvCreditLimitValue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.creditLimit)) + "");
            tvAvailableLimitValue.setText(amountFormate.format(StringUtils.getFloat(creditLimit.availbleLimit)) + "");
        }
    }

    private void finalizeOrder() {
        if ((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
                && trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED()) {

        } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() ||
                trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
            trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER();
        else
            trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
            showReturnReason();
        } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER()) {
//			showSignatureSalesFinalizeDialog();
            showSignatureDialog();
        } else
            showSignatureDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnFinalize.setClickable(true);
                btnFinalize.setEnabled(true);
            }
        }, 500);
    }

    private void sortTrxDetail(ArrayList<TrxDetailsDO> arrTrxDetailsDOs) {
        Collections.sort(arrTrxDetailsDOs, new Comparator<TrxDetailsDO>() {
            @Override
            public int compare(TrxDetailsDO s1, TrxDetailsDO s2) {
                return (s1.DisplayOrder) - (s2.DisplayOrder);
            }
        });
    }

    private void performCashCustomerPayment(final CustomDialog mCustomDialog) {
        showLoader("Please wait...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("performCashCustomerPayment", "1");
                float invoiceAmount = trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount;
//                float invoiceAmount = trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount;  before vat

//		//	newly added by Abhijit as Trx code is not updating in finalizing pre saels order if u select cashas per fayaz	
//				if(trxHeaderDO.trxCode!=null && !trxHeaderDO.trxCode.equalsIgnoreCase(""))
//				{
//					trxHeaderDO.status = 0;
//					ArrayList<TrxHeaderDO> arrayList = new ArrayList<TrxHeaderDO>();
//					arrayList.add(trxHeaderDO);
//					LogUtils.debug("offlinedata_query", "updateSavedOrder called");
//					
//					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
//					{
//						trxHeaderDO.trxCode = ""+new OrderDA().updatePresellerOrder(arrayList,preference.getStringFromPreference(preference.USER_ID, ""),
//						preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER));
//					}
//					orderID = trxHeaderDO.trxCode;
//					Division = trxHeaderDO.Division;
////					else
////						/*trxHeaderDO.trxCode = ""+*/new OrderDA().updateSavedOrder(arrayList);
//				}
//				//==============================================================================
//				 if(TextUtils.isEmpty(trxHeaderDO.trxCode) ) {
                if (TextUtils.isEmpty(orderID)) {
                    Log.e("performCashCustomerPayment", "2");
                    orderID = new OrderDA().getOrderId(Division);
                    Log.e("performCashCustomerPayment", "3" + orderID);
                }

                trxHeaderDO.trxCode = orderID;
                if (TextUtils.isEmpty(trxHeaderDO.trxCode)) {
                    trxHeaderDO.Division = Division;
                } else {
                    Division = trxHeaderDO.Division;
                }
                trxHeaderDO.LPONo = LPONo;
//				} else {
//					orderID = trxHeaderDO.trxCode;
//				}
                insertStmtAndInvoiceDiscountInTrxDetails(trxHeaderDO);

                if (orderID != null && orderID.length() > 0)
                    pendingInvoice = new CustomerDetailsDA().insertCurrentInvoice(mallsDetailsOrderPrev.site, (float) StringUtils.round(invoiceAmount, 2) + "", orderID, Division);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (orderID == null || orderID.length() <= 0 || !pendingInvoice)
                            showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "warning");
                        else {
                            if (mCustomDialog != null)
                                mCustomDialog.dismiss();

                            Log.e("performCashCustomerPayment", "100" + orderID + "---mallsDetails=" + mallsDetailsOrderPrev.site);

                            Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
                            intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                            intent.putExtra("isFromPayment", true);
                            intent.putExtra("AR", true);
                            intent.putExtra("isFromOrderPreview", true);
                            intent.putExtra("trxHeaderDO", trxHeaderDO);
                            intent.putExtra("isExceed", true);
                            intent.putExtra(AppConstants.DIVISION, Division);
                            startActivityForResult(intent, 5000);
                        }
                    }
                });

                hideLoader();
            }
        }).start();
    }

    boolean pendingInvoice = false;

    public void setHeader() {
        if (TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER())
            orderType = "Tele Order";
        else if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
            orderType = "Return Order";
        else if (TRX_SUB_TYPE == TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER())
            orderType = "Saved Order";
        else if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
            orderType = "Sales Order";
        else if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
            orderType = "Advance Order";
        else if (TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
            orderType = "FOC Order";
        tvHeaderPreview.setText("Confirm " + orderType);
    }
	
	
	/*
	 *  saves,saved_modified,saved_modified_finalized,setup (for differentiating the service call)
	 */

    public void postOrder() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        showLoader(getString(R.string.please_wait));

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mallsDetailsOrderPrev.currencyCode == null || mallsDetailsOrderPrev.currencyCode.length() <= 0)
                    mallsDetailsOrderPrev.currencyCode = curencyCode;

                //getting all the values for Order Table
                trxHeaderDO.trxDate = CalendarUtils.getCurrentDateTime();//dateFormat
                trxHeaderDO.freeNote = etNotes.getText().toString();

                trxHeaderDO.deliveryDate = dateFormatforOrder;

                trxHeaderDO.createdOn = trxHeaderDO.trxDate;

                if (mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
                    trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CASH();
                else
                    trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CREDIT();

                trxHeaderDO.LPONo = LPONo;
                trxHeaderDO.Narration = Narration;
                trxHeaderDO.returnReason = returnReason;

                if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER() || TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
                    trxHeaderDO.geoLatitude = preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 0.0);
                    trxHeaderDO.geoLongitude = preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 0.0);
                }

                Bitmap bitmap = getBitmap(presellerSignature);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    trxHeaderDO.salesmanSignature = Base64.encodeBytes(stream.toByteArray());
                    storeImage(bitmap, AppConstants.SALESMAN_SIGN);
                }

                //customer signature
                Bitmap image = getBitmap(customerSignature);
                ByteArrayOutputStream streams = new ByteArrayOutputStream();
                if (image != null) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
                    trxHeaderDO.clientSignature = Base64.encodeBytes(streams.toByteArray());
                    storeImage(image, AppConstants.CUSTOMER_SIGN);
                }
                trxHeaderDO.Division = Division;

                if (trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
                    showLoader(getString(R.string.please_wait_order_saving));
                else
                    showLoader(getString(R.string.please_wait_order_pushing));

                if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
                    for (int i = 0; i < trxHeaderDO.arrTrxDetailsDOs.size(); i++) {
//						trxHeaderDO.arrTrxDetailsDOs.get(i).calculatedDiscountAmount = 0;
//						trxHeaderDO.arrTrxDetailsDOs.get(i).totalDiscountAmount = 0;
//						trxHeaderDO.arrTrxDetailsDOs.get(i).CSPrice = 0;
                        trxHeaderDO.totalAmount = 0;
                        trxHeaderDO.totalDiscountAmount = 0;
                        trxHeaderDO.totalVATAmount=0;//VAT
                    }
                }
                insertStmtAndInvoiceDiscountInTrxDetails(trxHeaderDO);
                if (trxHeaderDO.trxCode != null && !trxHeaderDO.trxCode.equalsIgnoreCase("")) {
                    trxHeaderDO.status = 0;
                    ArrayList<TrxHeaderDO> arrayList = new ArrayList<TrxHeaderDO>();
                    arrayList.add(trxHeaderDO);
                    LogUtils.debug("offlinedata_query", "updateSavedOrder called");

                    if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
                        trxHeaderDO.trxCode = "" + new OrderDA().updatePresellerOrder(arrayList, preference.getStringFromPreference(preference.USER_ID, ""),
                                preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER));
                    } else
						/*trxHeaderDO.trxCode = ""+*/new OrderDA().updateSavedOrder(arrayList);
                } else {
                    trxHeaderDO.status = 0;
                    trxHeaderDO.trxCode = "" + new OrderDA().insertOrderDetails_Promo(trxHeaderDO);
                }

                if (!TextUtils.isEmpty(trxHeaderDO.trxCode)) {
                    if ((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() ||
                            trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() ||
                            trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) &&
                            trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()) {
                        new CustomerDA().updateCustomerVisit(trxHeaderDO);
                        //Need to update log also
                        new OrderDA().deleteIfExistsInCart(trxHeaderDO.clientCode);
                        float invoiceAmount = trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount;
//                        float invoiceAmount = trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount;  before vat

                        new OrderDA().updateInventory_WhileOrder(trxHeaderDO.arrTrxDetailsDOs, CalendarUtils.getOrderPostDate());
                        if (!isFromPayment)
                            new CustomerDetailsDA().insertCurrentInvoice(mallsDetailsOrderPrev.site, ((float) StringUtils.round(invoiceAmount, 2)) + "", trxHeaderDO.trxCode, Division);
                        else
                            new OrderDA().updateOrderStatus(trxHeaderDO.trxCode);
                    } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
                        new OrderDA().updateInventory_WhileReturnOrder(trxHeaderDO.arrTrxDetailsDOs, CalendarUtils.getOrderPostDate(), preference.getStringFromPreference(Preference.EMP_NO, ""));
                        float invoiceAmount = -(trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount);
//                        float invoiceAmount = -(trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount); before vat
                        new CustomerDetailsDA().insertCurrentInvoice(mallsDetailsOrderPrev.site, ((float) StringUtils.round(invoiceAmount, 2)) + "", trxHeaderDO.trxCode, Division);
                    }
                }

                updateOrderStatus();
                uploadOrder();

            }
        }).start();
    }

    private void insertStmtAndInvoiceDiscountInTrxDetails(TrxHeaderDO trxHeaderDO)
    {
//        this is suggested by digen sir
        for (int i = 0; i < trxHeaderDO.arrTrxDetailsDOs.size(); i++)
        {
            TrxDetailsDO trxDetailsDO= trxHeaderDO.arrTrxDetailsDOs.get(i);
            if(trxDetailsDO!=null)
            {
                trxDetailsDO.totalDiscountPercentage= (StringUtils.getFloat(trxHeaderDO.promotionalDiscount));
                trxDetailsDO.promotionalDiscountAmount= (StringUtils.getFloat(trxHeaderDO.promotionalDiscount)*(trxDetailsDO.CSPrice * trxDetailsDO.quantityBU))/100; //objItem.CSPrice * objItem.quantityBU
                trxDetailsDO.calculatedDiscountPercentage= StringUtils.getFloat(trxHeaderDO.statementDiscount);
                trxDetailsDO.calculatedDiscountAmount= (StringUtils.getFloat(trxHeaderDO.statementDiscount)*(trxDetailsDO.CSPrice * trxDetailsDO.quantityBU))/100;
            }
//            stmtInsertOrder.bindDouble(21, StringUtils.getDouble(trxHeaderDO.promotionalDiscount));
//
//							stmtInsertOrder.bindDouble(22,  (StringUtils.getDouble(trxHeaderDO.promotionalDiscount)*trxDetailsDO.basePrice)/100);
//
//							stmtInsertOrder.bindDouble(23,  StringUtils.getDouble(trxHeaderDO.statementDiscount));
//							stmtInsertOrder.bindDouble(24,  (StringUtils.getDouble(trxHeaderDO.statementDiscount)*trxDetailsDO.basePrice)/100);
        }

    }
	
	/*
	 *  saves,saved_modified,saved_modified_finalized,setup (for differentiating the service call)
	 */

    public void postCashOrder() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        showLoader(getString(R.string.please_wait));

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mallsDetailss.currencyCode == null || mallsDetailss.currencyCode.length() <= 0)
                    mallsDetailss.currencyCode = curencyCode;

                //getting all the values for Order Table
                trxHeaderDO.trxDate = CalendarUtils.getCurrentDateTime();//dateFormat

//				if(etNotes!=null)
//					trxHeaderDO.freeNote    = freeNotes;

                trxHeaderDO.deliveryDate = dateFormatforOrder;
                trxHeaderDO.createdOn = trxHeaderDO.trxDate;
//				if(mallsDetailss.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
//					trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CASH();
//				else
//					trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CREDIT();

                if (mallsDetailss.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) || AppConstants.customertype.equalsIgnoreCase("Cash"))
                    trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CASH();
                else
                    trxHeaderDO.paymentType = TrxHeaderDO.get_TRX_PAYMENT_TYPE_CREDIT();

                Bitmap bitmap = getBitmap(presellerSignature);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    trxHeaderDO.salesmanSignature = Base64.encodeBytes(stream.toByteArray());
                    storeImage(bitmap, AppConstants.SALESMAN_SIGN);
                }

                //customer signature
                Bitmap image = getBitmap(customerSignature);
                ByteArrayOutputStream streams = new ByteArrayOutputStream();
                if (image != null) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, streams);
                    trxHeaderDO.clientSignature = Base64.encodeBytes(streams.toByteArray());
                    storeImage(image, AppConstants.CUSTOMER_SIGN);
                }

                performCashCustomerPayment(null);
            }
        }).start();
    }

    private void updateOrderStatus() {

        if (trxHeaderDO.trxSubType != TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER()) {
            StatusDO statusDO = new StatusDO();
            statusDO.UUid = "";
            statusDO.Userid = preference.getStringFromPreference(Preference.SALESMANCODE, "");
            statusDO.Customersite = mallsDetailsOrderPrev.site;
            statusDO.Date = CalendarUtils.getCurrentDateAsStringforStoreCheck();
            statusDO.Visitcode = mallsDetailsOrderPrev.VisitCode;
            statusDO.JourneyCode = mallsDetailsOrderPrev.JourneyCode;
            statusDO.Status = "0";
            statusDO.Action = AppConstants.Action_CheckIn;

            if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER()) {
                statusDO.Type = AppConstants.Type_SalesOrder;
            } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()) {
                statusDO.Type = AppConstants.Type_PresalesOrder;
            } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()) {
                statusDO.Type = AppConstants.Type_AdvancedOrder;
            } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
                statusDO.Type = AppConstants.Type_ReturnOrder;
            } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
                statusDO.Type = AppConstants.Type_FOCOrder;
            }
            new StatusDA().insertOptionStatus(statusDO);
        }

        if (trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()) {
            TrxLogDetailsDO trxLogDetailsDO = new TrxLogDetailsDO();
            trxLogDetailsDO.Amount = (float) StringUtils.round(trxHeaderDO.totalAmount, 2);
            trxLogDetailsDO.CustomerCode = mallsDetailss.site;
            trxLogDetailsDO.CustomerName = mallsDetailss.siteName;
            trxLogDetailsDO.TrxType = AppConstants.INVOICES;
            trxLogDetailsDO.Date = CalendarUtils.getOrderPostDate();
            trxLogDetailsDO.IsJp = new CustomerDA().isCustomerIsInJourneyPlan(mallsDetailss.site, trxLogDetailsDO.Date) ? "True" : "False";
            String colName = TrxLogHeaders.COL_TOTAL_SALES;

            if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
                colName = TrxLogHeaders.COL_TOTAL_CREDIT_NOTES;
                trxLogDetailsDO.TrxType = AppConstants.CREDITNOTE;
            }

            trxLogDetailsDO.columnName = colName;
            trxLogDetailsDO.DocumentNumber = trxHeaderDO.trxCode;
            trxLogDetailsDO.TimeStamp = CalendarUtils.getCurrentDateAsStringForJourneyPlan();

            new TransactionsLogsDA().updateLogReport(trxLogDetailsDO);
        }
    }

    private void uploadOrder() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoader();

                if (!TextUtils.isEmpty(trxHeaderDO.trxCode)) {
//					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER() 
//							&& mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)
//							&&!isFromPayment)
//					{
//						Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
//						intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
//						intent.putExtra("isFromOrderPreview", true);
//						intent.putExtra("AR", false);
//						startActivity(intent);
//					}
//					else
                    {
                        btnContinue.setVisibility(View.GONE);
                        btnPrintSalesOrder.setVisibility(View.GONE);
                        btnOrderPreviewContinue.setVisibility(View.VISIBLE);
                        btnFinalize.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        showOrderCompletePopup(trxHeaderDO.trxStatus);
                    }
                    uploadData();
                } else
                    showCustomDialog(SalesmanOrderPreview.this, "Warning !", "Order sequence numbers are not synced properly from server. Please sync sequence numbers from Settings.", getString(R.string.OK), null, "warning");
            }
        });
    }

    public void filterAvailableItesm(ArrayList<TrxDetailsDO> arrItems) {
        Predicate<TrxDetailsDO> searchItem = new Predicate<TrxDetailsDO>() {
            public boolean apply(TrxDetailsDO trxDetailsDO) {
                return trxDetailsDO.quantityBU > 0 || trxDetailsDO.requestedBU > 0;
            }
        };
       /* vinod
        Collection<TrxDetailsDO> filteredResult = filter(new Vector<TrxDetailsDO>(arrItems), searchItem);

        orderPreviewAdapter.refresh((ArrayList<TrxDetailsDO>) filteredResult);
    */
    }

    private void showVisibleButton(final boolean isVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVisible) {
                    btnOrderPreviewContinue.setVisibility(View.VISIBLE);
                    btnFinalize.setVisibility(View.GONE);
                    btnContinue.setVisibility(View.GONE);
                } else {
                    btnOrderPreviewContinue.setVisibility(View.GONE);
                    btnFinalize.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void InitializeControls() {
        llMiddleLayout = (LinearLayout) llOrderPreview.findViewById(R.id.llOrderPreviewMidle);
        tvHeaderPreview = (TextView) llOrderPreview.findViewById(R.id.tvOrderPreviewHeader);
        btnFinalize = (Button) llOrderPreview.findViewById(R.id.btnOrderPreviewFinalize);
        btnSave = (Button) llOrderPreview.findViewById(R.id.btnSave);
        btnPrintSalesOrder = (Button) llOrderPreview.findViewById(R.id.btnOrdersheetVerify);
//		btnPrintMerchant			= 	(Button)llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
        btnContinue = (Button) llOrderPreview.findViewById(R.id.btnContinue);
        btnOrderPreviewContinue = (Button) llOrderPreview.findViewById(R.id.btnOrderPreviewContinue);
        btnPrintSalesOrderMerchant = (Button) llOrderPreview.findViewById(R.id.btnPrintSalesOrderMerchant);
//		tvlanguage					= 	(TextView)llOrderPreview.findViewById(R.id.tvlanguage);
        tvLu = (TextView) llOrderPreview.findViewById(R.id.tvLu);
        llOrderPreviewBottom = (LinearLayout) llOrderPreview.findViewById(R.id.llOrderPreviewBottom);
        //Masafi logo Layout as header of list view
        llHeaderLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.preview_header, null);
//		llHeaderLayout   			= (LinearLayout)getLayoutInflater().inflate(R.layout.preview_header_ar, null);

        tvMasafiLogoTitleCustomer = (TextView) llHeaderLayout.findViewById(R.id.tvMasafiLogoTitleCustomer);
        tvAddressCustomer = (TextView) llHeaderLayout.findViewById(R.id.tvAddressCustomer);

        tvItemName = (TextView) llHeaderLayout.findViewById(R.id.tvOrderPreviewItemName);
        tvOrderPreviewunits = (TextView) llHeaderLayout.findViewById(R.id.tvOrderPreviewunits);
        tvTotalPrice = (TextView) llHeaderLayout.findViewById(R.id.tvTotalPrice);
        tvPrice = (TextView) llHeaderLayout.findViewById(R.id.tvPrice);
        tvItemUOM = (TextView) llHeaderLayout.findViewById(R.id.tvItemUOM);
        tvInvoiceAmount = (TextView) llHeaderLayout.findViewById(R.id.tvInvoiceAmount);
        tvDiscount = (TextView) llHeaderLayout.findViewById(R.id.tvDiscount);

        //Signature Layout as footer of list view
        llEsignature = (LinearLayout) getLayoutInflater().inflate(R.layout.esignature_order_preview, null);
        llPresellerSignature = (LinearLayout) llEsignature.findViewById(R.id.llPresellerSignature);
        llCustomerSignature = (LinearLayout) llEsignature.findViewById(R.id.llCustomerSignature);
        llAdvanceOrderDate = (LinearLayout) llEsignature.findViewById(R.id.llAdvanceOrderDate);
        llNotes = (LinearLayout) llEsignature.findViewById(R.id.llNotes);
        tvToday_Date = (TextView) llEsignature.findViewById(R.id.tvToday_Date);
        tvDelivery_Date = (TextView) llEsignature.findViewById(R.id.tvDelivery_Date);
        tvTotalQty = (TextView) llEsignature.findViewById(R.id.tvTotalQty);
        etNotes = (EditText) llEsignature.findViewById(R.id.etNotes);
        llStatementDiscValue = (LinearLayout) llEsignature.findViewById(R.id.llStatementDiscValue);


        llFOCTotalQty = (LinearLayout) llEsignature.findViewById(R.id.llFOCTotalQty);
        tvFOCTotalQty = (TextView) llEsignature.findViewById(R.id.tvFOCTotalQty);
        tvOrderValueTitle = (TextView) llEsignature.findViewById(R.id.tvOrderValueTitle);
        tvNetValueTitle = (TextView) llEsignature.findViewById(R.id.tvNetValueTitle);
        tvVATValueTitle = (TextView) llEsignature.findViewById(R.id.tvVATValueTitle);
        tvOrderQtyColon = (TextView) llEsignature.findViewById(R.id.tvOrderQtyColon);
        tvOrderColon = (TextView) llEsignature.findViewById(R.id.tvOrderColon);
        tvNETColon = (TextView) llEsignature.findViewById(R.id.tvNETColon);
        tvDiscountColon = (TextView) llEsignature.findViewById(R.id.tvDiscountColon);
        tvSplDiscountPer = (TextView) llEsignature.findViewById(R.id.tvSplDiscountPer);
        tvSplDiscValue = (TextView) llEsignature.findViewById(R.id.tvSplDiscValue);
        tvSplDiscountColon = (TextView) llEsignature.findViewById(R.id.tvSplDiscountColon);
        tvSpclDiscount = (TextView) llEsignature.findViewById(R.id.llSpclDiscount);
        tvInvoiceAmountTitle = (TextView) llEsignature.findViewById(R.id.tvInvoiceAmountTitle);
        dot = (TextView) llEsignature.findViewById(R.id.dot);

        llSignatures = (LinearLayout) llEsignature.findViewById(R.id.llSignatures);
        llFOCTotalQty = (LinearLayout) llEsignature.findViewById(R.id.llFOCTotalQty);
        llDiscount = (TextView) llEsignature.findViewById(R.id.llDiscount);
        llStatementDiscount = (TextView) llEsignature.findViewById(R.id.llStatementDiscount);
        llOrderValue = (LinearLayout) llEsignature.findViewById(R.id.llOrderValue);
        llNetValue = (LinearLayout) llEsignature.findViewById(R.id.llNetValue);
        llVATValue = (LinearLayout) llEsignature.findViewById(R.id.llVATValue);
        llDiscValue = (LinearLayout) llEsignature.findViewById(R.id.llDiscValue);
        llSplDiscValue = (LinearLayout) llEsignature.findViewById(R.id.llSplDiscValue);

//		if(Division== TrxHeaderDO.get_DIVISION_FOOD())
//		{
//			llStatementDiscount.setVisibility(View.GONE);
//			llStatementDiscValue.setVisibility(View.GONE);
//			dot.setVisibility(View.GONE);
//			
//		}
        llSignatures.setVisibility(View.GONE);


        tvOrderValue = (TextView) llEsignature.findViewById(R.id.tvOrderValue);
        tvVATOrderValue = (TextView) llEsignature.findViewById(R.id.tvVATOrderValue);
        tvDiscValue = (TextView) llEsignature.findViewById(R.id.tvDiscValue);
        tvStatementDiscValue = (TextView) llEsignature.findViewById(R.id.tvStatementDiscValue);
        tvTotalValue = (TextView) llEsignature.findViewById(R.id.tvTotalValue);
        tvNetOrderValue = (TextView) llEsignature.findViewById(R.id.tvNetOrderValue);

        tvAvailableLimitValue = (TextView) llEsignature.findViewById(R.id.tvAvailableLimitValue);
        tvCreditLimitValue = (TextView) llEsignature.findViewById(R.id.tvCreditLimitValue);

        LinearLayout llCustomer_Passcode = (LinearLayout) llEsignature.findViewById(R.id.llCustomer_Passcode);
        LinearLayout llDeliveryDate = (LinearLayout) llEsignature.findViewById(R.id.llDeliveryDate);
        LinearLayout llDeliveryDeliveryTime = (LinearLayout) llEsignature.findViewById(R.id.llDeliveryDeliveryTime);

        TextView tvBottomDist = (TextView) llEsignature.findViewById(R.id.tvBottomDist);

        llAmountLimit = (LinearLayout) llEsignature.findViewById(R.id.llAmountLimit);
        llCrdAmountLimit = (LinearLayout) llEsignature.findViewById(R.id.llCrdAmountLimit);

        llDeliveryDate.setVisibility(View.GONE);
        llCustomer_Passcode.setVisibility(View.GONE);
        llDeliveryDeliveryTime.setVisibility(View.GONE);
        tvBottomDist.setVisibility(View.INVISIBLE);

        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()) {
            llAmountLimit.setVisibility(View.GONE);
            llPresellerSignature.setVisibility(View.VISIBLE);
            llCustomerSignature.setVisibility(View.VISIBLE);
            llAdvanceOrderDate.setVisibility(View.VISIBLE);

            if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
                tvHeaderPreview.setText(getResources().getString(R.string.Confirm_Order));
            else
                tvHeaderPreview.setText("Confirm Advance Order");

            locationUtility = new LocationUtility(SalesmanOrderPreview.this);
        }
//		else if(TRXTYPE_ORDER == TrxHeaderDO.get_TRXTYPE_TELE_ORDER())
//		{
//			llPresellerSignature.setVisibility(View.GONE);
//			llCustomerSignature.setVisibility(View.GONE);
//			llAdvanceOrderDate.setVisibility(View.VISIBLE);
//			
//			tvHeaderPreview.setText("Confirm Tele Order");
//		}
        else {
            llPresellerSignature.setVisibility(View.VISIBLE);
            llCustomerSignature.setVisibility(View.VISIBLE);

            tvHeaderPreview.setText("Confirm Order");

            if (preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
                llAdvanceOrderDate.setVisibility(View.VISIBLE);
            else
                llAdvanceOrderDate.setVisibility(View.GONE);

        }
        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
            llAmountLimit.setVisibility(View.GONE);
        } else if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER()) {
            llCrdAmountLimit.setVisibility(View.GONE);
        }


        String todayDate = getCurrentDate();
        tvToday_Date.setText(todayDate);
        dateFormatforOrder = CalendarUtils.getCurrentDateTime();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 0);        //Uncomment when sending build
        yearTo = c.get(Calendar.YEAR);
        monthOfYearTo = (c.get(Calendar.MONTH)) + 1;
        dayOfMonthTo = c.get(Calendar.DAY_OF_MONTH);
        todayDate = (c.get(Calendar.DAY_OF_MONTH)) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" + (c.get(Calendar.YEAR));

        todayDate = CalendarUtils.getMonthFormatedDate(todayDate);
        dateFormatforOrder = CalendarUtils.getSetDateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateFormat = todayDate;
        tvDelivery_Date.setText(todayDate);

//		calculateDiscount(trxHeaderDO);

//        String tot = decimalFormat.format(trxHeaderDO.totalDiscountAmount - trxHeaderDO.specialDiscount);
//        tvDiscValue.setText(amountFormate.format(StringUtils.getFloat(tot) - StringUtils.getFloat(decimalFormat.format(trxHeaderDO.statementdiscountvalue))));// need to bind discount
        tvDiscValue.setText(amountFormate.format(((StringUtils.getDouble(trxHeaderDO.promotionalDiscount) * trxHeaderDO.totalAmount) / 100)));// need to bind discount
        tvStatementDiscValue.setText(amountFormate.format(((StringUtils.getDouble(trxHeaderDO.statementDiscount) * trxHeaderDO.totalAmount) / 100)));// need to bind discount
        tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.specialDiscount));
        tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
        tvVATOrderValue.setText(amountFormate.format(trxHeaderDO.totalVATAmount));
        tvTotalValue.setText(amountFormate.format((trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount+trxHeaderDO.totalVATAmount)));
        tvNetOrderValue.setText(amountFormate.format((trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount)));

        showVisibleButton(false);

        btnCheckOut.setVisibility(View.GONE);
        ivLogOut.setVisibility(View.GONE);

        tvLu.setText(mallsDetailsOrderPrev.siteName + " (" + mallsDetailsOrderPrev.site + ")");
    }


    private void calculateDiscount(TrxHeaderDO trxHeaderDO) {
        float discount = (trxHeaderDO.totalAmount * StringUtils.getFloat(mallsDetailsOrderPrev.PromotionalDiscount)) / 100;
        trxHeaderDO.totalDiscountAmount = trxHeaderDO.totalDiscountAmount + discount;
    }

    @Override
    public void onButtonYesClick(String from) {
        super.onButtonYesClick(from);


        if (from.equalsIgnoreCase("scroll")) {
            if (lvPreviewOrder.isScrolled()) {
                lvPreviewOrder.setScrolled(false);
                lvPreviewOrder.setSelection(lvPreviewOrder.getChildAt(lvPreviewOrder.getChildCount() - 1).getTop());
            }
        } else if (from.equalsIgnoreCase("served")) {
            performCustomerServed();
        } else if (from.equalsIgnoreCase("Task")) {
            Intent intent = new Intent(SalesmanOrderPreview.this, TaskToDoActivity.class);
            intent.putExtra("object", mallsDetailsOrderPrev);
            startActivity(intent);
        } else if (from.equalsIgnoreCase("payment")) {
            Intent intent;
            if (TRX_TYPE != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
                intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                intent.putExtra("isFromOrderPreview", true);
                intent.putExtra("AR", false);
                startActivity(intent);
            } else {
                intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                intent.putExtra("isFromOrderPreview", true);
                intent.putExtra("AR", false);
                startActivity(intent);
            }
        } else if (from.equalsIgnoreCase("ReturnRequest")) {
            Intent intent = new Intent(SalesmanOrderPreview.this, SalesmanReturnOrder.class);
            intent.putExtra("name", "" + getString(R.string.Capture_Inventory));
            intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
            intent.putExtra("from", "checkINOption");

            if (trxHeaderDO != null)
                intent.putExtra("trxHeaderDO", trxHeaderDO);
            startActivity(intent);
        } else if (from.equalsIgnoreCase("Survey")) {
            Intent intent = new Intent(SalesmanOrderPreview.this, ConsumerBehaviourSurveyActivityNew.class);
            startActivity(intent);
        } else if (from.equalsIgnoreCase("exceed_limit")) {
            finish();
        } else if (from.equalsIgnoreCase("creditLimitPopup")) {
//			performCashCustomerPayment(null);
            postCashOrder();
        } else if (from.equalsIgnoreCase("modifyOrder")) {
            if (!isFromPayment && !TextUtils.isEmpty(orderID)) {
                deleteInsertedInvoice();
            } else {
                finish();
            }
        } else if (from.equalsIgnoreCase("continuecreditOrder")) {
            postOrder();
        }

        if (from.equalsIgnoreCase("creditLimitPopup") || from.equalsIgnoreCase("finalizePayment") || from.equalsIgnoreCase("warning")) {

        } else if (from.equalsIgnoreCase("return")) ;
        else if (from.equalsIgnoreCase("DATE_PICKER")) ;
        else if (from.equalsIgnoreCase("scroll")) ;
        else if (from.equalsIgnoreCase("nbu")) ;
        else if(!TextUtils.isEmpty(from)){
            btnOrderPreviewContinue.setText("Continue");
            btnOrderPreviewContinue.setVisibility(View.VISIBLE);
            btnFinalize.setVisibility(View.GONE);
        }
    }

    private void showPaymentModePopup(final Intent intent) {
        View view = inflater.inflate(R.layout.payment_mode_popup, null);
        final CustomDialog customDialog = new CustomDialog(SalesmanOrderPreview.this, view);
        customDialog.setCancelable(true);

        Button btn_CashPayment = (Button) view.findViewById(R.id.btn_CashPayment);
        Button btn_ChequePayment = (Button) view.findViewById(R.id.btn_ChequePayment);

        btn_CashPayment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CASH);
                startActivity(intent);
//				if(isExceed || isFromPayment)
//					startActivityForResult(intent, 5000);
//				else
//					startActivityForResult(intent, 1000);

                customDialog.dismiss();
            }
        });


        btn_ChequePayment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.putExtra("paymode", AppConstants.PAYMENT_NOTE_CHEQUE);
                startActivity(intent);
//				if(isExceed || isFromPayment)
//					startActivityForResult(intent, 5000);
//				else
//					startActivityForResult(intent, 1000);

                customDialog.dismiss();
            }
        });

        if (!customDialog.isShowing())
            customDialog.show();

    }

    @Override
    public void onButtonNoClick(String from) {
        if (from.equalsIgnoreCase("exceed_limit")) {
            Intent intent = new Intent(SalesmanOrderPreview.this, PendingInvoices.class);
            intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
            intent.putExtra("AR", false);
            intent.putExtra("isExceed", true);
            intent.putExtra("isFromOrderPreview", true);
            startActivityForResult(intent, 10001);
        }
    }

    @Override
    public void onBackPressed() {

        if (isFromPayment)
            showCustomDialog(SalesmanOrderPreview.this, getString(R.string.warning), getString(R.string.please_press_finalize_to_continue), getString(R.string.OK), null, "finalizePayment");
        else if (!TextUtils.isEmpty(orderID)) {
            deleteInsertedInvoice();
        } else if (trxHeaderDO != null && (trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED()
                || trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER()))
            finish();
        else if (trxHeaderDO != null && !TextUtils.isEmpty(trxHeaderDO.trxCode))
            btnOrderPreviewContinue.performClick();
        else
            finish();


    }

    private void deleteInsertedInvoice() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new CustomerDetailsDA().deletePendingInvoice(orderID);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orderID = null;
                            finish();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(SignatureView myView) {

        if (myView != null) {
            Bitmap bitmap = myView.getDrawingCache(true);
            return bitmap;
        }

        return null;
    }


    private void showOrderCompletePopup(int trxStatus) {
        View view = inflater.inflate(R.layout.custom_popup_order_complete, null);
        final CustomDialog mCustomDialog = new CustomDialog(SalesmanOrderPreview.this, view, preference
                .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 350) - 40,
                LayoutParams.WRAP_CONTENT, true);
        mCustomDialog.setCancelable(false);

        TextView tv_poptitle = (TextView) view.findViewById(R.id.tv_poptitle);
        TextView tv_poptitle1 = (TextView) view.findViewById(R.id.tv_poptitle1);

        Button btn_popup_print = (Button) view.findViewById(R.id.btn_popup_print);
        Button btn_popup_capture_inovice = (Button) view.findViewById(R.id.btn_popup_capture_inovice);

        Button btn_popup_collectpayment = (Button) view.findViewById(R.id.btn_popup_collectpayment);
        Button btn_popup_returnreq = (Button) view.findViewById(R.id.btn_popup_returnreq);
        Button btn_popup_task = (Button) view.findViewById(R.id.btn_popup_task);
        Button btn_popup_done = (Button) view.findViewById(R.id.btn_popup_done);
        Button btn_popup_survey = (Button) view.findViewById(R.id.btn_popup_survey);
        Button btnPlaceNewOrder = (Button) view.findViewById(R.id.btnPlaceNewOrder);

//===============================newly added for food===============================		

        Button btn_popup_print_Kwality = (Button) view.findViewById(R.id.btn_popup_print_Kwality);

        if (trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
            tv_poptitle.setText(orderType + " Saved");
        else
            tv_poptitle.setText(orderType + " Placed");

        btn_popup_returnreq.setVisibility(View.GONE);
        btn_popup_task.setVisibility(View.GONE);
        btn_popup_survey.setVisibility(View.GONE);
        btnPlaceNewOrder.setVisibility(View.GONE);


        if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && !isFromPayment)
            btn_popup_collectpayment.setVisibility(View.VISIBLE);
        else
            btn_popup_collectpayment.setVisibility(View.GONE);

//need to write the code for taking images...
        if (AppConstants.isServeyCompleted) {
            btn_popup_survey.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.order_order), null, getResources().getDrawable(R.drawable.check1_new), null);
            btn_popup_survey.setClickable(false);
            btn_popup_survey.setEnabled(false);
        }

        if (AppConstants.isTaskCompleted)
            btn_popup_task.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.taks_order), null, getResources().getDrawable(R.drawable.check1_new), null);

        tv_poptitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
        tv_poptitle1.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_print.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_print_Kwality.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_collectpayment.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_returnreq.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_done.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_task.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btn_popup_survey.setTypeface(AppConstants.Roboto_Condensed_Bold);
        btnPlaceNewOrder.setTypeface(AppConstants.Roboto_Condensed_Bold);
        //=========================newly added for food==========================
        if (trxHeaderDO.Division == trxHeaderDO.get_DIVISION_FOOD() || trxHeaderDO.Division == trxHeaderDO.get_DIVISION_THIRD_PARTY()) {

            btn_popup_print_Kwality.setVisibility(View.VISIBLE);
            if(Division==TrxHeaderDO.get_DIVISION_FOOD() )
            btn_popup_print_Kwality.setText("Print - Kwality");
            else
                btn_popup_print_Kwality.setText("Print - TPT");
            btn_popup_print.setText("Print-PIC");
        } else {
            btn_popup_print_Kwality.setVisibility(View.GONE);
            btn_popup_print.setText("Print");
        }

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED() && mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
            btn_popup_returnreq.setVisibility(View.GONE);
            btn_popup_done.setVisibility(View.VISIBLE);
            btn_popup_print.setVisibility(View.VISIBLE);

        } else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED()) {
            btn_popup_returnreq.setVisibility(View.GONE);
            btn_popup_print.setVisibility(View.GONE);

            btn_popup_done.setVisibility(View.VISIBLE);

            //==========================newly added for food=======================================================
            btn_popup_print_Kwality.setVisibility(View.GONE);

        } else {
            btn_popup_returnreq.setVisibility(View.GONE);
            btn_popup_collectpayment.setVisibility(View.GONE);
            if (Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Credit))
                btn_popup_done.setVisibility(View.VISIBLE);
            else if (Invoice_Type.equalsIgnoreCase(AppConstants.Invoice_Type_Cash))
                btn_popup_done.setVisibility(View.GONE);
            btn_popup_print.setVisibility(View.VISIBLE);
        }
//		btn_popup_collectpayment.setVisibility(View.GONE);
//        need to write the code for taking images...
        btn_popup_capture_inovice.setVisibility(View.GONE);
        btn_popup_capture_inovice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                orderPrintImageDO = new OrderPrintImageDO();
                orderPrintImageDO.TrxCode=trxHeaderDO.trxCode;
                orderPrintImageDO.status= 0;
                orderPrintImageDO.ImageType= OrderPrintImageDO.INVOICE_IMAGE_TYPE;
                orderPrintImageDO.UserCode= preference.getStringFromPreference(Preference.USER_ID,"");
                orderPrintImageDO.CaptureDate= CalendarUtils.getCurrentDateAsStringForJourneyPlan();
                captureImage(AppConstants.CAMERA_PIC_AFTER_INVOICE_PRINT);

            }
        });
        btn_popup_print.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                Intent intent = new Intent(SalesmanOrderPreview.this, WoosimPrinterActivity.class);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
                intent.putExtra("trxHeaderDO", trxHeaderDO);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);

                if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && trxHeaderDO.Division == trxHeaderDO.get_DIVISION_FOOD()) {
                    intent.putExtra("PrintTypeIce", 100);
                }
                startActivity(intent);
            }
        });
        btn_popup_print_Kwality.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                Intent intent = new Intent(SalesmanOrderPreview.this, WoosimPrinterActivity.class);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                intent.putExtra("CALLFROM", CONSTANTOBJ.PRINT_SALES);
                intent.putExtra("trxHeaderDO", trxHeaderDO);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                intent.putExtra("PrintTypeIce", 200);
                startActivity(intent);
            }
        });

        btnPlaceNewOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesmanOrderPreview.this, CaptureInventoryActivity.class);
                intent.putExtra("name", "" + getResources().getString(R.string.Capture_Inventory));
                intent.putExtra("mallsDetails", mallsDetailsOrderPrev);
                intent.putExtra("from", "checkin");
                startActivity(intent);
            }
        });

        btn_popup_collectpayment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                onButtonYesClick("payment");
            }
        });

        btn_popup_returnreq.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                onButtonYesClick("ReturnRequest");
            }
        });

        btn_popup_task.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                onButtonYesClick("Task");
            }
        });

        btn_popup_survey.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                Intent intent = new Intent(SalesmanOrderPreview.this, ServeyListActivity.class);
                startActivity(intent);
            }
        });

        btn_popup_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCustomDialog.dismiss();
                if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && mallsDetailsOrderPrev.customerPaymentType != null && mallsDetailsOrderPrev.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && trxHeaderDO.trxStatus != TrxHeaderDO.get_TRX_STATUS_SAVED()) {
                    if (!new PaymentDetailDA().isPaymentDone(trxHeaderDO.trxCode))
                        onButtonYesClick("payment");
                    else
                        onButtonYesClick("served");
                } else
                    onButtonYesClick("served");
            }
        });

        try {
            if (!mCustomDialog.isShowing())
                mCustomDialog.show();
        } catch (Exception e) {
        }
    }

    int yearTo, monthOfYearTo, dayOfMonthTo;

    @SuppressLint("NewApi")
    private void showDatePickerDialog(final TextView edtDate) {
        final Calendar c = Calendar.getInstance();

        c.add(Calendar.DATE, 0);

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePicker = new DatePickerDialog(SalesmanOrderPreview.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                yearTo = year;
                monthOfYearTo = monthOfYear;
                dayOfMonthTo = dayOfMonth;
                if (validateSelectedDate(year, monthOfYear, dayOfMonth)) {
                    dateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    dateFormat = CalendarUtils.getMonthFormatedDate(dateFormat);

                    dateFormatforOrder = CalendarUtils.getSetDateTime(year, monthOfYear, dayOfMonth);
                    edtDate.setText(dateFormat);
                }
            }
        }, mYear, mMonth, mDay);

        datePicker.show();
    }

    private boolean validateSelectedDate(int year, int monthOfYear, int dayOfMonth) {
        int dateDiff = CalendarUtils.getDiffBtwDatesInDays(CalendarUtils.getOrderPostDate(), CalendarUtils.getSetDate(year, monthOfYear, dayOfMonth));
        LogUtils.debug("date_check", "dateDiff: " + dateDiff);
        if (dateDiff < 0) {
            showCustomDialog(SalesmanOrderPreview.this, "Alert", getResources().getString(R.string.order_date_has_to_be_greater_than_current_date), getString(R.string.OK), null, "DATE_PICKER");
            return false;
        } else
            return true;
    }

    private String getCurrentDate() {
        String todaysDate = "";

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        todaysDate = mDay + "-" + (mMonth + 1) + "-" + mYear;
        todaysDate = CalendarUtils.getMonthFormatedDate(todaysDate);

        return todaysDate;
    }

    /* (non-Javadoc)
     * @see com.winit.alseer.salesman.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

//        cash = false;
//        credit = false;
//        select_Invoice_Type = "";

        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()) {
            if (locationUtility == null)
                locationUtility = new LocationUtility(SalesmanOrderPreview.this);
            //Live
            if (isNetworkConnectionAvailable(SalesmanOrderPreview.this) || isGPSEnable(SalesmanOrderPreview.this))
                locationUtility.getLocation(SalesmanOrderPreview.this);
        }
    }

    /* (non-Javadoc)
     * @see com.winit.alseer.salesman.common.LocationUtility.LocationResult#gotLocation(android.location.Location)
     */
    @Override
    public void gotLocation(Location loc) {
        if (loc != null) {
            locationUtility.stopGpsLocUpdation();
            preference.saveDoubleInPreference(Preference.CUREENT_LATTITUDE, "" + Double.parseDouble(decimalFormat.format(loc.getLatitude())));
            preference.saveDoubleInPreference(Preference.CUREENT_LONGITUDE, "" + Double.parseDouble(decimalFormat.format(loc.getLongitude())));
            preference.commitPreference();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000 && resultCode == 5000) {

            showLoader("Please wait...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new OrderDA().updateOrderNumber(orderID);
                    final CustomerCreditLimitDo customerLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailss.site);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoader();
                                    btnSave.setVisibility(View.GONE);
                                    btnContinue.setVisibility(View.GONE);
                                    btnOrderPreviewContinue.setVisibility(View.GONE);

                                    if (mallsDetailss.customerPaymentType != null && mallsDetailss.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)) {
                                        float availebleLimit = Tools.str2Float(customerLimit.availbleLimit);

                                        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && (trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount) > availebleLimit)
                                            isFromPayment = true;
                                    } else if (mallsDetailss.customerPaymentType != null && mallsDetailss.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
                                        isFromPayment = true;

                                    Bundle bundle = data.getExtras();
                                    if (bundle != null && bundle.containsKey("totalPaidAmount"))
                                        totalPaidAmount = totalPaidAmount + bundle.getFloat("totalPaidAmount");
                                    if (bundle != null && bundle.containsKey("trxcode"))
                                        code = bundle.getString("trxcode");

                                    if (!TextUtils.isEmpty(code) && code != null && !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)) {
                                        trxHeaderDO.trxCode = code;
                                    } else
                                        trxHeaderDO.trxCode = orderID;


                                    btnFinalize.setVisibility(View.VISIBLE);

                                    trxHeaderDO.preTrxCode = orderID;

                                    if (mallsDetailss.customerPaymentType != null && mallsDetailss.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
                                        btnContinue.setVisibility(View.GONE);
                                        btnPrintSalesOrder.setVisibility(View.GONE);
                                        btnOrderPreviewContinue.setVisibility(View.VISIBLE);
                                        btnFinalize.setVisibility(View.GONE);
                                        btnSave.setVisibility(View.GONE);
                                        showOrderCompletePopup(trxHeaderDO.trxStatus);
                                    } else {
                                        btnContinue.setVisibility(View.GONE);
                                        btnPrintSalesOrder.setVisibility(View.GONE);
                                        btnOrderPreviewContinue.setVisibility(View.VISIBLE);
                                        btnFinalize.setVisibility(View.GONE);
                                        btnSave.setVisibility(View.GONE);
                                        showOrderCompletePopup(trxHeaderDO.trxStatus);
//										checkCreditLimit();
                                    }
                                }
                            }, 300);
                        }
                    });
                }
            }).start();
        } else if (requestCode == 5000) {
            try {
//				if(TextUtils.isEmpty(trxHeaderDO.preTrxCode))
//					trxHeaderDO.trxCode = "";
//				else{
//					trxHeaderDO.trxStatus = preTrxStatus;
//				}
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new CustomerDetailsDA().deletePendingInvoiceFromPending();
                    }
                }).start();

                trxHeaderDO.trxCode = trxCodeOld;
                trxHeaderDO.trxStatus = preTrxStatus;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		/*
		showLoader("Please wait...");
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				new OrderDA().updateOrderNumber(orderID);
				final CustomerCreditLimitDo customerLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailsOrderPrev.site);

				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						new Handler().postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								hideLoader();
								btnSave.setVisibility(View.GONE);
								btnContinue.setVisibility(View.GONE);
								btnOrderPreviewContinue.setVisibility(View.GONE);
									
								if(mallsDetailsOrderPrev.customerPaymentType != null && mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
								{
									float availebleLimit = Tools.str2Float(customerLimit.availbleLimit);
									
									if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && (trxHeaderDO.totalAmount - trxHeaderDO.totalDiscountAmount) > availebleLimit)
										isFromPayment = true;
								}
								else if(mallsDetailsOrderPrev.customerPaymentType != null && mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
									isFromPayment = true;
								
								Bundle bundle = data.getExtras();
								if(bundle!=null && bundle.containsKey("totalPaidAmount"))
									totalPaidAmount = totalPaidAmount + bundle.getFloat("totalPaidAmount");
								btnFinalize.setVisibility(View.VISIBLE);
								trxHeaderDO.trxCode    = orderID;
								trxHeaderDO.preTrxCode = orderID;
							}
						}, 300);
					}
				});
			}
		}).start();
	*/
    }

    private int preTrxStatus;
    private String trxCodeOld = "";
    private String SYNCPRICECAL = "SYNCPRICECAL";

    public void calculatePrice(final View view) {
        LogUtils.debug("calculatePrice", "called");
        new Thread(new Runnable() {
            @Override
            public void run() {
                calculatePriceInSync(view);
            }
        }).start();
    }

    float orderTPrice, totalIPrice, totalDiscount;

    private void calculatePriceInSync(final View view) {
        try {
            synchronized (SYNCPRICECAL) {
                orderTPrice = 0.0f;
                totalIPrice = 0.0f;
                totalDiscount = 0.0f;

                int count = 1;
                for (TrxDetailsDO trxDetailsDO : trxHeaderDO.arrTrxDetailsDOs) {
                    LogUtils.debug("priceloop", "running");
//					trxDetailsDO.lineNo = (int) count++;
                    trxDetailsDO.itemType = "" + AppConstants.ITEM_TYPE_ORDER;
                    float price = trxDetailsDO.basePrice;
                    float price3 = 0;
                    float quanity = 0.0f;

                    if (hashMapPricing.containsKey(trxDetailsDO.itemCode)) {
                        price = hashMapPricing.get(trxDetailsDO.itemCode).get(
                                trxDetailsDO.UOM);// getting price of selected UOM
                        if (hashMapPricing.get(trxDetailsDO.itemCode).containsKey(TrxDetailsDO.getItemUomLevel3()))
                            price3 = hashMapPricing.get(trxDetailsDO.itemCode).get(TrxDetailsDO.getItemUomLevel3());
                    }

                    if (TRX_TYPE == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
                        trxDetailsDO.priceUsedLevel1 = 0;
                    else
                        trxDetailsDO.priceUsedLevel1 = price;

                    trxDetailsDO.priceUsedLevel3 = price3;
                    quanity = trxDetailsDO.quantityLevel1;

                    float discountAmount = (price * trxDetailsDO.calculatedDiscountPercentage) / 100.0f;
//					trxDetailsDO.calculatedDiscountAmount=StringUtils.getFloat(decimalFormat.format(discountAmount));
//					trxDetailsDO.totalDiscountAmount=StringUtils.getFloat(decimalFormat.format(trxDetailsDO.calculatedDiscountAmount*quanity));

                    trxDetailsDO.calculatedDiscountAmount = (float) StringUtils.round(discountAmount, 2);
                    trxDetailsDO.totalDiscountAmount = (float) StringUtils.round(trxDetailsDO.calculatedDiscountAmount * quanity, 2);

                    orderTPrice += (price * quanity);
                    totalDiscount += trxDetailsDO.totalDiscountAmount;
                    totalIPrice += (price * quanity);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        trxHeaderDO.totalDiscountAmount = totalDiscount;
                        trxHeaderDO.totalAmount = orderTPrice;

                        tvDiscValue.setText(amountFormate.format(trxHeaderDO.totalDiscountAmount - trxHeaderDO.specialDiscount));// need to bind discount
                        tvSplDiscValue.setText(amountFormate.format(trxHeaderDO.specialDiscount));
                        tvOrderValue.setText(amountFormate.format(trxHeaderDO.totalAmount));
                        tvVATOrderValue.setText(amountFormate.format(trxHeaderDO.totalVATAmount));
                        tvTotalValue.setText(amountFormate.format(totalIPrice - totalDiscount+trxHeaderDO.totalVATAmount));
                        tvNetOrderValue.setText(amountFormate.format(totalIPrice - totalDiscount));
                        if (orderPreviewAdapter != null)
                            orderPreviewAdapter.refreshListItems(view);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, HHInventryQTDO> hmInventory;
    private HashMap<String, HashMap<String, Float>> hashMapPricing = new HashMap<String, HashMap<String, Float>>();

    public void loadPricing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                hmInventory = new OrderDetailsDA().getAvailInventoryQtys();
                hashMapPricing = new CaptureInventryDA().getPricing(mallsDetailsOrderPrev.priceList, trxHeaderDO.trxType);
            }
        }).start();
    }

    public int isInventoryAvail(TrxDetailsDO objItem, int quantity) {
        int missedQTY = 0;
        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER()
                || TRX_TYPE == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
            return missedQTY;
        if (hmInventory != null && hmInventory.size() > 0 && hmInventory.containsKey(objItem.itemCode)) {
            HHInventryQTDO inventryDO = hmInventory.get(objItem.itemCode);
            objItem.expiryDate = inventryDO.expiryDate;
            objItem.batchCode = inventryDO.batchCode;

            float availQty = inventryDO.totalQt;
            if (quantity > availQty) {
                missedQTY = (int) ((quantity) - availQty);
                hmInventory.get(objItem.itemCode).tempTotalQt = 0;
            } else
                hmInventory.get(objItem.itemCode).tempTotalQt = quantity;
        } else
            missedQTY = quantity;

        return missedQTY;
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case START_DATE_DIALOG_ID_FROM:
                return new DatePickerDialog(this, fromDateListner,  yearFrom, monthFrom, dayFrom);

        }
        return null;
    }

    /**
     * date set listener for "From Date"
     */
    String fromDate;
    private DatePickerDialog.OnDateSetListener fromDateListner = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int yearSel, int monthOfYear, int dayOfMonth)
        {
            String selectedDate = CalendarUtils.getOrderSummaryDate(yearSel,monthOfYear,dayOfMonth);

            if(!selectedDate.equalsIgnoreCase(fromDate))
            {


                    yearFrom = yearSel;
                    monthFrom = monthOfYear;
                    dayFrom = dayOfMonth;


                    fromDate = selectedDate;
                    if(edtSRID!=null) {
                        edtSRID.setText(CalendarUtils.getFormatedDatefromString(fromDate));
                        edtSRID.setTag(fromDate);
                    }

            }
        }
    };




    Dialog dialog;

    private void showSignatureDialog() {
        btnFinalize.setClickable(false);
        btnFinalize.setEnabled(false);

        dialog = new Dialog(this, R.style.Dialog);
        LinearLayout llSignature = (LinearLayout) inflater.inflate(R.layout.signature_driver_supervsor_new, null);
        TextView tvLogisticsSignature = (TextView) llSignature.findViewById(R.id.tvLogisticsSignature);
        TextView tvSalesmanSignature = (TextView) llSignature.findViewById(R.id.tvSalesmanSignature);

        final LinearLayout llcashcustomer = (LinearLayout) llSignature.findViewById(R.id.llcashcustomer);
        final LinearLayout llInvoicetyp = (LinearLayout) llSignature.findViewById(R.id.llInvoicetyp);
        final LinearLayout llcreditcustomer = (LinearLayout) llSignature.findViewById(R.id.llcreditcustomer);
        final LinearLayout llSignSupervisor = (LinearLayout) llSignature.findViewById(R.id.llSignSupervisor);
        final LinearLayout llSignDriver = (LinearLayout) llSignature.findViewById(R.id.llSignDriver);
        final LinearLayout llLPO = (LinearLayout) llSignature.findViewById(R.id.llLPO);
        final LinearLayout llTRN = (LinearLayout) llSignature.findViewById(R.id.llTRN);
        final LinearLayout llSR = (LinearLayout) llSignature.findViewById(R.id.llSR);
        llTRN.setVisibility(View.VISIBLE);
        if(trxHeaderDO.trxType==TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
        llSR.setVisibility(View.VISIBLE);
        else
            llSR.setVisibility(View.GONE);
        tvInvoicetype = (TextView) llSignature.findViewById(R.id.tvInvoicetype);
        ivCash = (ImageView) llSignature.findViewById(R.id.ivCash);
        ivCredit = (ImageView) llSignature.findViewById(R.id.ivCredit);







        if (isfromsalesorder || (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() && !mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) && !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))) {
            llInvoicetyp.setVisibility(View.VISIBLE);
        }

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || mallsDetailsOrderPrev.customerPaymentType != null && mallsDetailsOrderPrev.customerPaymentType.trim().equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH)) {
            llInvoicetyp.setVisibility(View.GONE);
        }

        Button btnOK = (Button) llSignature.findViewById(R.id.btnOK);
        Button btnSKCear = (Button) llSignature.findViewById(R.id.btnSKCear);
        Button btnDriverCear = (Button) llSignature.findViewById(R.id.btnDriverCear);
        Button btnCancle = (Button) llSignature.findViewById(R.id.btnCancle);

        edtLPO = (EditText) llSignature.findViewById(R.id.edtLPO);
        edtTRNNo = (EditText) llSignature.findViewById(R.id.edtTRNNo);
        edtSRINO = (EditText) llSignature.findViewById(R.id.edtSRINO);
        edtSRID = (TextView) llSignature.findViewById(R.id.edtSRID);
        edtNarration = (EditText) llSignature.findViewById(R.id.edtNarration);
        Calendar c = Calendar.getInstance();
        monthFrom = c.get(Calendar.MONTH);
        yearFrom = c.get(Calendar.YEAR);
        dayFrom = c.get(Calendar.DAY_OF_MONTH);

        edtSRID.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialog(START_DATE_DIALOG_ID_FROM);
            }
        });
         if(!TextUtils.isEmpty(mallsDetailsOrderPrev.VATNo))
         {
             edtTRNNo.setText(mallsDetailsOrderPrev.VATNo);
         }
         else {
             edtTRNNo.setText(""); //trn no should be 15 digits
         }
        dialog.addContentView(llSignature, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        select_Invoice_Type = "";

        llcashcustomer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (cash == false) {
                    select_Invoice_Type = "Cash";
                    ivCash.setImageResource(R.drawable.rbtn);
                    ivCredit.setImageResource(R.drawable.rbtn_h);
                    cash = true;
                    credit = false;
                } else {
                    select_Invoice_Type = "Credit";
                    ivCredit.setImageResource(R.drawable.rbtn);
                    ivCash.setImageResource(R.drawable.rbtn_h);
                    credit = true;
                    cash = false;
                }

            }
        });
        llcreditcustomer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (credit == false) {
                    select_Invoice_Type = "Credit";
                    ivCredit.setImageResource(R.drawable.rbtn);
                    ivCash.setImageResource(R.drawable.rbtn_h);
                    credit = true;
                    cash = false;
                } else {
                    select_Invoice_Type = "cash";
                    ivCash.setImageResource(R.drawable.rbtn);
                    ivCredit.setImageResource(R.drawable.rbtn_h);
                    cash = true;
                    credit = false;
                }
            }
        });
//		tvcustomertype.setOnClickListener(new OnClickListener() { 	
//			
//			@Override
//			public void onClick(View v) {
//				
//				showtypeofcustomerpopup();
//				
//			}
//
//			
//		});

        if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() && !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER)))
            llLPO.setVisibility(View.VISIBLE);
        else
            llLPO.setVisibility(View.GONE);

        tvLogisticsSignature.setText("Customer Signature");
        tvSalesmanSignature.setText("Salesman Signature");

        presellerSignature = new SignatureView(this);
        presellerSignature.setDrawingCacheEnabled(true);
        presellerSignature.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (180 * px)));
        presellerSignature.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        customerSignature = new SignatureView(this);
        customerSignature.setDrawingCacheEnabled(true);
        customerSignature.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (180 * px)));
        customerSignature.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        llSignSupervisor.addView(customerSignature);
        llSignDriver.addView(presellerSignature);

        btnSKCear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerSignature != null)
                    customerSignature.resetSign();
            }
        });

        btnDriverCear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presellerSignature != null)
                    presellerSignature.resetSign();
            }
        });

        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                trxHeaderDO.trxStatus = preTrxStatus;

                btnFinalize.setClickable(true);
                btnFinalize.setEnabled(true);
            }
        });

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String trnNO=edtTRNNo.getText().toString();
                String oriINVNO=edtSRINO.getText().toString();
                String oriINVDate=edtSRID.getText().toString();
                if(trnNO!=null && (!trnNO.equalsIgnoreCase("")))
                {

                    if (((mallsDetailsOrderPrev.VATNo!=null && !TextUtils.isEmpty(mallsDetailsOrderPrev.VATNo ))  ||trnNO.length()>=15)  )
                    {
                        if(oriINVNO!=null && (!oriINVNO.equalsIgnoreCase(""))||trxHeaderDO.trxType != TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
                        {

                            if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER()) {
                                trxHeaderDO.oriSRINVNO=oriINVNO;
                                trxHeaderDO.oriSRINVDATE=oriINVDate;
                            }else{
                                trxHeaderDO.oriSRINVNO="";
                                trxHeaderDO.oriSRINVDATE="";
                            }
//                        mallsDetailsOrderPrev.VATNo = edtTRNNo.getText().toString();
                        if (edtLPO.getText().toString() != null && !edtLPO.getText().toString().equalsIgnoreCase(""))
                            LPONo = edtLPO.getText().toString();
                        if (edtNarration.getText().toString() != null && !edtNarration.getText().toString().equalsIgnoreCase(""))
                            Narration = edtNarration.getText().toString();
                        if (!TextUtils.isEmpty(select_Invoice_Type))
                            AppConstants.customertype = select_Invoice_Type;

                        if (isfromsalesorder && TextUtils.isEmpty(select_Invoice_Type)) {
                            btnFinalize.setEnabled(true);
                            btnFinalize.setClickable(true);
                            showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Please Select Invoice Type", getString(R.string.OK), null, "nbu");
                        } else if (!presellerSignature.isSigned()) {
                            btnFinalize.setEnabled(true);
                            btnFinalize.setClickable(true);
                            showCustomDialog(SalesmanOrderPreview.this, "Alert !", "Salesman's Signature is mandatory.", getString(R.string.OK), null, "scroll");

                        }
                        else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && mallsDetailss.customerPaymentType != null && mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT)) {
                            mallsDetailsOrderPrev.VATNo = edtTRNNo.getText().toString();
                            checkCreditLimit();
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                        } else {
                            mallsDetailsOrderPrev.VATNo = edtTRNNo.getText().toString();
//					if(trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
//						trxHeaderDO.trxStatus  = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED();
//					else
//						trxHeaderDO.trxStatus  = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
                            if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && mallsDetailss.customerPaymentType != null && mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH))
                                postCashOrder();
                            else if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() &&
                                    mallsDetailss.customerPaymentType != null &&
                                    (mallsDetailss.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CASH) || AppConstants.customertype.equalsIgnoreCase("Cash")) &&
                                    !preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(preference.PRESELLER))
                                postCashOrder();
                            else
                                postOrder();
                        }
                        }
                        else
                        {
                            btnFinalize.setEnabled(true);
                            btnFinalize.setClickable(true);
                            showCustomDialog(SalesmanOrderPreview.this,getString(R.string.alert),"Please Enter Original Invoice Number. ","OK",null,null);

                        }
                    }
                    else
                    {
                        btnFinalize.setEnabled(true);
                        btnFinalize.setClickable(true);
                        showCustomDialog(SalesmanOrderPreview.this,getString(R.string.alert),"TRN length for Customer be should greater than or equals 15.. please enter valid TRN no. ","OK",null,null);

                    }
                }else{
                    btnFinalize.setEnabled(true);
                    btnFinalize.setClickable(true);
                    showCustomDialog(SalesmanOrderPreview.this,getString(R.string.alert),"Please Enter TRN for Customer.","OK",null,null);
                }
//				else if((trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || trxHeaderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY()) && 
//						mallsDetailsOrderPrev.customerPaymentType!= null && mallsDetailsOrderPrev.customerPaymentType.equalsIgnoreCase(AppConstants.CUSTOMER_TYPE_CREDIT))
//				{
//					LPONo 		= edtLPO.getText().toString();
//					Narration 	= edtNarration.getText().toString();
//					
//					CustomerCreditLimitDo customerLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailsOrderPrev.site);
//					
//					float availebleLimit = Tools.str2Float(customerLimit.availbleLimit);
//					float pending =StringUtils.getFloat(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalAmount)) - (StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalDiscountAmount)) + StringUtils.getFloat(decimalFormat.format(totalPaidAmount))))));
//					if(TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && pending > availebleLimit)
//					{
//						orderID = trxHeaderDO.trxCode;
//						creditLimitPopup(mallsDetailsOrderPrev, 0,!isFromPayment);
//					}
//					else
//						postOrder();
//				}
//				else
//					postOrder();
            }
        });
    }

    private void checkCreditLimit() {
        CustomerCreditLimitDo customerLimit = new CustomerDA().getCustomerCreditLimit(mallsDetailss.site);

        float availebleLimit = StringUtils.getFloat(decimalFormat.format(StringUtils.getFloat(customerLimit.availbleLimit)));
        float pending = StringUtils.getFloat(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalAmount)) - (StringUtils.getFloat(decimalFormat.format(trxHeaderDO.totalDiscountAmount)) + StringUtils.getFloat(decimalFormat.format(totalPaidAmount))))));

        if (TRX_TYPE == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() &&
                pending > availebleLimit) {

            if (totalPaidAmount > 0 && availebleLimit >= 0) {
                if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
                    trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED();
                else
                    trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();

                if (AppConstants.customertype.equalsIgnoreCase("CASH"))//Abhijit
                    postCashOrder();
                else
                    postOrder();
            } else {
                if (!TextUtils.isEmpty(trxHeaderDO.trxCode))
                    orderID = trxHeaderDO.trxCode;
                creditLimitPopup(mallsDetailss, 0, !isFromPayment, trxHeaderDO);
            }
        } else {
            if (trxHeaderDO.trxType == TrxHeaderDO.get_TRXTYPE_ADVANCE_ORDER())
                trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED();
            else
                trxHeaderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();

            if (AppConstants.customertype.equalsIgnoreCase("CASH"))//Abhijit
                postCashOrder();
            else
                postOrder();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    EditText edtLPO, edtNarration,edtTRNNo,edtSRINO;
   TextView edtSRID;

    //	private void showSignaturePasswordDialog()
    private void showSignatureSalesFinalizeDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        LinearLayout llSignature = (LinearLayout) inflater.inflate(R.layout.lpo_narration, null);

        Button btnOK = (Button) llSignature.findViewById(R.id.btnOK);
        Button btnSKCear = (Button) llSignature.findViewById(R.id.btnSKCear);
        Button btnDriverCear = (Button) llSignature.findViewById(R.id.btnDriverCear);
        Button btnCancle = (Button) llSignature.findViewById(R.id.btnCancle);

        edtLPO = (EditText) llSignature.findViewById(R.id.edtLPO);
        edtNarration = (EditText) llSignature.findViewById(R.id.edtNarration);

        dialog.addContentView(llSignature, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        setTypeFaceRobotoNormal(llSignature);
        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LPONo = edtLPO.getText().toString();
                Narration = edtNarration.getText().toString();
                showSignatureDialog();
                dialog.dismiss();
            }
        });
        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                btnFinalize.setClickable(true);
                btnFinalize.setEnabled(true);
            }
        });
        btnDriverCear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtLPO != null)
                    edtLPO.setText("");
            }
        });

        btnSKCear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNarration != null)
                    edtNarration.setText("");
            }
        });
    }

    String returnReason = "";
    Vector<String> arrReason = new Vector<String>();

    private void showReturnReason() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                arrReason = new CommonDA().getReasonBasedOnType("Return Reason");
//				arrReason.add("Freezer Breakdown (Melted)");
//				arrReason.add("Power Failure (Melted)");
//				arrReason.add("Expiry");
//				arrReason.add("General");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View view = inflater.inflate(R.layout.custom_common_popup_checkout, null);

                        customDialog = new CustomDialog(SalesmanOrderPreview.this, view, preference
                                .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 40,
                                LayoutParams.WRAP_CONTENT, true);
                        customDialog.setCancelable(true);
                        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
                        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessagePopup);
                        Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
                        Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
                        ListView lvSalesOutlet = (ListView) view.findViewById(R.id.lvSalesOutlet);
                        final ReturnReasonAdapter adapterCheckout = new ReturnReasonAdapter(SalesmanOrderPreview.this, arrReason);
                        lvSalesOutlet.setAdapter(adapterCheckout);

                        tvTitle.setTypeface(AppConstants.Roboto_Condensed_Bold);
                        tvMessage.setTypeface(AppConstants.Roboto_Condensed_Bold);
                        btnYes.setTypeface(AppConstants.Roboto_Condensed_Bold);
                        btnNo.setTypeface(AppConstants.Roboto_Condensed_Bold);

                        tvTitle.setText("Select Reason for Return");
                        tvMessage.setText("Reasons :");
                        btnYes.setText("Submit");
                        btnNo.setText("Cancel");

                        btnFinalize.setClickable(true);
                        btnFinalize.setEnabled(true);

                        btnYes.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss();
                                returnReason = adapterCheckout.getSelecteSalesOutlet();
                                if (returnReason != "") {
                                    showSignatureDialog();
                                } else {
                                    showCustomDialog(SalesmanOrderPreview.this, getString(R.string.warning), "Please select atleast one Reason for return.", getString(R.string.Yes), null, "return");
                                }
                            }
                        });

                        btnNo.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.dismiss();
                            }
                        });
                        try {
                            if (!customDialog.isShowing())
                                customDialog.show();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }).start();

    }
}
