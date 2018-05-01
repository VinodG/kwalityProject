package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.Vector;


@SuppressWarnings("serial")
public class OrderDO extends BaseComparableDO implements Serializable
{
	public String OrderId = "";
	public String orderType = "";
	public String orderSubType = "";
	public String PresellerId = "";
	public String empNo = "";
	public String CustomerSiteId = "";
	public float Discount;
	public float TotalAmount;
	public String DeliveryStatus = "";
	public String DeliveryDate = "";
	public float BalanceAmount;
	public String InvoiceNumber = "";
	public String InvoiceDate = "";
	public String DeliveryAgentId = "";
	public String strCustomerName = "";
	public String strAddress1 = "";
	public String strAddress2 = "";
	public String strCustomerPriceKey = "";
	//added recently required to save the signature images
	public String strPresellerSign = "";
	public String strCustomerSign  = "";
	public String strUUID  = "";
	
	public int pushStatus;
	public int returnOrderStatus;
	public String message="";
	public String lpoNo ="";
	public String isDiscountApplied = "";
	public String strCustomerRefCode = "";
	public String freeDeliveryResion = "";
	
	public String JourneyCode = "";
	public String VisitCode = "";
	public String StampDate = "";
	public String StampImage = "";
	public String TRXStatus = "";
	public String TotalTaxAmt = "";
	public String TrxReasonCode = "";
	public String CurrencyCode = "";
	public String PaymentType = "";
	public String PaymentCode = "";
	public String LPOCode = "";
	public String salesmanCode = "";
	public String vehicleNo = "";
	
	public Vector<ProductDO> vecProductDO = new Vector<ProductDO>();
	public Vector<ProductDO> vecProductDOPromotions = new Vector<ProductDO>();
} 
