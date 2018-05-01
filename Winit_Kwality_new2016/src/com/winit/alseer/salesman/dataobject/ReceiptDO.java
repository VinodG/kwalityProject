package com.winit.alseer.salesman.dataobject;

import java.util.Vector;

public class ReceiptDO 
{
	public String ReceiptNo="";
	public String ReceiptDate="";
	public String ReceiptAmount="";
	public Vector<InvoiceAmountDO> vecInvoiceAmountDOs=new Vector<InvoiceAmountDO>();
	public Vector<PaymentInvoiceDO> vecPaymentInvoiceDOs = new Vector<PaymentInvoiceDO>();
}
