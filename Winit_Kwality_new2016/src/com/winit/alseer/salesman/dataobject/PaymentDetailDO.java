package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class PaymentDetailDO implements Serializable
{
	public String RowStatus = "";
	public String ReceiptNo= "";
	public String LineNo= "";
	public String PaymentTypeCode= "";
	public String BankCode= "";
	public String BankName= "";
	public String branchName= "";
	public String ChequeImagePath= "";
	public String ChequeDate= "";
	public String ChequeNo= "";
	public String CCNo= "";
	public String CCExpiry= "";
	public String PaymentStatus= "";
	public String PaymentNote;
	public String UserDefinedBankName= "";
	public String Status= "";
	public String Amount= "";
	public int paymentId;
	public int imageUploadStatus;
	public String invoiceNumber;
	public String invoiceAmount;
	public String invoiceDate = "";
	
	public ArrayList<PaymentCashDenominationDO> arrayList =null;//this will be used only when payment mad by cash initialize before using
	private static final String PAYMENT_TYPE_CASH = "CASH";
	private static final String PAYMENT_TYPE_CHEQUE = "CHEQUE";
	
	public static String getPaymentTypeCash(){
		return PAYMENT_TYPE_CASH;
	}
	public static String getPaymentTypeCheque(){
		return PAYMENT_TYPE_CHEQUE;
	}
	
}
