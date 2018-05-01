package com.winit.alseer.parsers;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

public class GetCustomersByUserIdParser extends BaseHandler
{
//	TaskDco 
	private boolean currentElement = false;
	private JourneyPlanDO objCustomer;
	private Vector<JourneyPlanDO> vecCustomerDOs;
	private SynLogDO synLogDO = new SynLogDO();
	public GetCustomersByUserIdParser(Context context, String strEmpNo) 
	{
		super(context); 
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue	= new StringBuilder();
		
		if(localName.equalsIgnoreCase("Customers"))
		{
			vecCustomerDOs = new Vector<JourneyPlanDO>();
		}
		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			objCustomer = new JourneyPlanDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerSiteId"))
		{
			objCustomer.site = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteName"))
		{
			objCustomer.siteName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerId"))
		{
			objCustomer.customerId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerName"))
		{
			objCustomer.partyName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address1"))
		{
			objCustomer.addresss1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address2"))
		{
			objCustomer.addresss2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address3"))
		{
			objCustomer.addresss3 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address4"))
		{
			objCustomer.addresss4 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("City"))
		{
			objCustomer.city = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Latitude"))
		{
			objCustomer.geoCodeX = StringUtils.getDouble(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Longitude"))
		{
			objCustomer.geoCodeY = StringUtils.getDouble(currentValue.toString());	
		}
		else if(localName.equalsIgnoreCase("CreditLimit"))
		{
			objCustomer.creditLimit = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("PaymentType"))
		{
			objCustomer.customerPaymentType = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("PaymentTermCode"))
		{
			objCustomer.paymentTermCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("SubChannelCode"))
		{
			objCustomer.subChannelCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("ChannelCode"))
		{
			objCustomer.channelCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("RegionCode"))
		{
			objCustomer.regionCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("StoreGrowth"))
		{
			objCustomer.StoreGrowth = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO1"))
		{
			objCustomer.mobileNo1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO2"))
		{
			objCustomer.mobileNo2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Website"))
		{
			objCustomer.website = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerGrade"))
		{
			objCustomer.CustomerGrade = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LandmarkId"))
		{
			objCustomer.LandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesmanlandmarkId"))
		{
			objCustomer.SalesmanlandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Source"))
		{
			objCustomer.source = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryId"))
		{
			objCustomer.coutryCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ParentGroup"))
		{
			objCustomer.ParentGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerPostingGroup"))
		{
			objCustomer.CustomerPostingGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerCategory"))
		{
			objCustomer.customerCategory = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CustomerSubCategory"))
		{
			objCustomer.customerSubCategory = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CustomerGroupCode"))
		{
			objCustomer.customerGroupCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("CurrencyCode"))
		{
			objCustomer.currencyCode = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("IsBlocked"))
		{
			objCustomer.blockedStatus = currentValue.toString();				
		}
		else if(localName.equalsIgnoreCase("IsSchemeApplicable"))
		{
			if(currentValue != null && currentValue.toString().equalsIgnoreCase("true"))
				objCustomer.isSchemeAplicable = 1;		
			else
				objCustomer.isSchemeAplicable = 0;
		}
		else if(localName.equalsIgnoreCase("PriceList"))
		{
			objCustomer.priceList = currentValue.toString();			
		}
		else if(localName.equalsIgnoreCase("CustomerType"))
		{
//			objCustomer.customerType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerStatus"))
		{
			objCustomer.custmerStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryCode"))
		{
			objCustomer.coutryCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Category"))
		{
			objCustomer.category = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PASSCODE"))
		{
			objCustomer.Passcode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Email"))
		{
			objCustomer.email = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ContactPersonName"))
		{
			objCustomer.contectPersonName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PhoneNumber"))
		{
			objCustomer.phoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppCustomerId"))
		{
			objCustomer.phoneNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PoNumber"))
		{
			objCustomer.poNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesPerson"))
		{
			objCustomer.salesmanCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentMode"))
		{
			objCustomer.customerPaymentMode = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("BlockedBy"))
		{
			objCustomer.blockedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BlockedReason"))
		{
			objCustomer.blockedReason = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BlockedDate"))
		{
			objCustomer.blockedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BlockedTime"))
		{
			objCustomer.blockedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute1"))
		{
			objCustomer.Attribute1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute2"))
		{
			objCustomer.Attribute2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute3"))
		{
			objCustomer.Attribute3 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute4"))
		{
			objCustomer.Attribute4 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute5"))
		{
			objCustomer.Attribute5 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute6"))
		{
			objCustomer.Attribute6 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute7"))
		{
			objCustomer.Attribute7 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute8"))
		{
			objCustomer.Attribute8 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute9"))
		{
			objCustomer.Attribute9 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute10"))
		{
			objCustomer.Attribute10 = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("Attribute11"))
		{
			objCustomer.Attribute11 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute12"))
		{
			objCustomer.Attribute12 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Attribute13"))
		{
			objCustomer.Attribute13 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ChannelName"))
		{
			objCustomer.ChannelName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SubChannelName"))
		{
			objCustomer.SubChannelName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentTermDays"))
		{
			objCustomer.PaymentTermDays = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GeoCodeFlag"))
		{
			objCustomer.GeoCodeFlag = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FInvDiscYH"))
		{
			objCustomer.FInvDiscYH = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FStatDiscYH"))
		{
			objCustomer.FStatDiscYH = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TInvDiscYH"))
		{
			objCustomer.TInvDiscYH = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TStatDiscYH"))
		{
			objCustomer.TStatDiscYH = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VATNo"))
		{
			objCustomer.VATNo = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IsVATApplicable"))
		{
			objCustomer.IsVATApplicableNew = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentTermDays"))
		{
			objCustomer.PaymentTermDays = currentValue.toString();
		}

		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			vecCustomerDOs.add(objCustomer);
		}
		else if(localName.equalsIgnoreCase("Customers"))
		{
			insertCustomer(vecCustomerDOs) ;
		}
//		"GeoCodeFlag" BOOL, "FInvDiscYH" VARCHAR, "FStatDiscYH" VARCHAR, "TInvDiscYH" VARCHAR, "TStatDiscYH" VARCHAR)
	}
	
	private boolean insertCustomer(Vector<JourneyPlanDO> vecCustomerDOs) 
	{
		synLogDO.entity = ServiceURLs.GET_CUSTOMER_SITE;
		if(vecCustomerDOs != null && vecCustomerDOs.size() > 0)
		{
			boolean isInserted = new CustomerDetailsDA().insertCustomerInforWithSync(vecCustomerDOs);
			if(isInserted)
				new SynLogDA().insertSynchLog(synLogDO);
			return isInserted;
		}
		else
			return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
