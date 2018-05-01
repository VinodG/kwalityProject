package com.winit.alseer.salesman.dataobject;

import java.util.Vector;


@SuppressWarnings("serial")
public class PaymentHeaderDO extends BaseComparableDO
{
	public String appPaymentId = "";
	public String rowStatus = "";
	public String receiptId = "";
	public String preReceiptId = "";
	public String paymentDate = "";
	public String siteId = "";
	public String empNo = "";
	public String amount = "";
	public String currencyCode = "";
	public String rate = "1";
	public String visitCode = "";
	public String paymentStatus = "";
	public String customerSignature = "";
	public String status = "";
	public String appPayementHeaderId = "";
	public String paymentType ="";
	public String vehicleNo ="";
	public String salesmanCode = "";
	public String salesOrgCode = "";
	
	public Vector<PaymentDetailDO> vecPaymentDetails   = new Vector<PaymentDetailDO>();
	public Vector<PaymentInvoiceDO> vecPaymentInvoices = new Vector<PaymentInvoiceDO>();
	public int Division = 0;
	
	
	
}
