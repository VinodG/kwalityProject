package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;


public class  PaymentCashDenominationDO implements Serializable
{
	public int paymentCashDenominationId    = 0;
	public String paymentCode  = "";
	public String cashDenominationCode                = "";
	public int totalAmount              = 0;
	public String  createdBy=""; 
	public ArrayList<String> arrDenominationDetail = new ArrayList<String>();
}
