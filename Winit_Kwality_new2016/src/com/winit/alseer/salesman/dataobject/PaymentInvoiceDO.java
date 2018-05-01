package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

public class PaymentInvoiceDO implements Serializable
{
	public String RowStatus = "";
	public String ReceiptId= "";
	public String TrxCode= "";
	public String TrxType= "";
	public String Amount= "";
	public String CurrencyCode= "";
	public String Rate= "";
	public String PaymentStatus= "";
	public String PaymentType= "";
	public String CashDiscount;
}
