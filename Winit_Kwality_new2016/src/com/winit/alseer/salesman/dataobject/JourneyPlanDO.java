package com.winit.alseer.salesman.dataobject;

import com.winit.alseer.salesman.utilities.CalendarUtils;


@SuppressWarnings("serial")
public class JourneyPlanDO extends BaseComparableDO
{
	public int rowid = 0;
	public int stop;//sequence;
	public String lifeCycle="";
	public String id="";
	public String site="";
	public String siteName="";
	public String customerId="";
	public String custmerStatus = "1";
	public String custAccCreationDate = CalendarUtils.getOrderPostDate();
	public String partyName="";
	public String channelCode="";
	public String channelName="";
	public String subChannelCode="";
	public String regionCode="";
	public String coutryCode="";
	public String category="";
	public String addresss1="";
	public String addresss2="";
	public String addresss3="";
	public String addresss4="";
	public String poNumber="";
	public String city="";
//	public String paymentType="";
	public String paymentTermCode="";
	public String creditLimit;
	public double geoCodeX;
	public double geoCodeY;
	public String Passcode;
	public String email;
	public String contectPersonName;
	public String phoneNumber;
	public String appCustomerId;
	public String mobileNo1;
	public String mobileNo2;
	public String website;
//	public String customerType;
	public String createdby;
	public String modifiedBy;
	public String source;
	public String customerCategory;
	public String customerSubCategory;
	public String customerGroupCode;
	public String modifiedDate;
	public String modifiedTime;
	public String currencyCode;
	public String timeOut;
	public String timeIn;
	public String clientCode;
	public String routeId;
	
	public String CustomerPostingGroup;
	public String ParentGroup;
	public String SalesmanlandmarkId;
	public String LandmarkId;
	public String CustomerGrade;
	public String StoreGrowth;
	public String customerPaymentType;
	public String customerPaymentMode;
	
	public int mPosition;
	public String ActualArrivalTime;
	public String reasonForSkip = "";
	public String dateOfJourny;
	
	public String Distance, TravelTime, SeviceTime, isServed;
	public int isSchemeAplicable = 1;
	
	public String balanceAmount;
	public String freeDeliveryResion = "";
	public String AppUUID = "";
	public String outLetType = "";
	public String outLetTypeId = "";
	public String competitionBrand = "";
	public String competitionBrandId = "";
	public String sku = "";
	public String buyerStatus = "";
	public String countryDesc = "";
	
	public String JourneyCode = "0";
	public String VisitCode = "0";
	public String priceList = "";
	
	public String salesmanName = "";
	public String salesmanCode = "";
	public String userID= "";
	
	public String Attribute1 = "";
	public String Attribute2 = "";
	public String Attribute3 = "";  //"Ship To " --code which is coming from ERP
	public String Attribute4 = "";
	public String Attribute5 = "";
	public String Attribute6 = "";
	public String Attribute7 = "";
	public String Attribute8 = "";
	public String Attribute9 = "";
	public String Attribute10 = "";
	public String Attribute11 = "";
	public String Attribute12 = "";
	public String Attribute13 = "";
	
	
	public String blockedBy		 	 = "";
	public String blockedReason		 = "";
	public String blockedDate		 = "";
	public String blockedTime		 = "";
	public String ChannelName		 = "";
	public String SubChannelName	 = "";
	public String PaymentTermDays	 = "";
	
	public int cartItemCount;
	public String blockedStatus = "";
	
	/****************Promotion detail***********************/
	public String PromotionID = "";
	public String PromotionalDiscount = "";
	
	public String reasonType = "";
	public String JourneyDate = "";
	public String statementdiscount="";
//newlwy added for food and tpt brands discounts
	public String GeoCodeFlag = "False";
	public String FInvDiscYH = "0";
	public String FStatDiscYH = "0";
	public String TInvDiscYH = "0";
	public String TStatDiscYH = "0";
	//For VAT
	public String VATNo = "";
	public Boolean IsVATApplicable = true;
	public String IsVATApplicableNew = "True";


	
}
