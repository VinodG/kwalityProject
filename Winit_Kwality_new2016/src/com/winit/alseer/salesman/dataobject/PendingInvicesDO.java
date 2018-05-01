package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PendingInvicesDO implements Serializable
{
	public String orderId ="";
	public String invoiceNo ="";
	public String balance="";
	public String lastbalance="";
	public String invoiceDate="";
	public String invoiceDateToShow="";
	public String IsOutStanding = "false";
	public boolean isNewleyAdded = false;
	public String totalAmount="";
	public String detailUUID = "";
	public String TRX_TYPE = "";
	public String payingAmount = "";
	public String invoiceDueDate="";
}
