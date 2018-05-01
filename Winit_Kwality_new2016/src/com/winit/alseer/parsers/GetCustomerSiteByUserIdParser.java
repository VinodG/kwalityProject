package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.CustomerSite_NewDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetCustomerSiteByUserIdParser extends BaseHandler
{
	private StringBuffer currentValue;
	private String status = "";
	private boolean currentElement = false;
	private CustomerSite_NewDO objCustomerSiteNewDO;
	private CustomerDetailsDA customerDetailsBL;	
	private Vector<CustomerSite_NewDO> vecCustomerSiteNewDOs;
	private String salesmanCode = "";
	
	public GetCustomerSiteByUserIdParser(Context context) 
	{
		super(context); 
		salesmanCode = preference.getStringFromPreference(Preference.SALESMANCODE, "");
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuffer();
		if(localName.equalsIgnoreCase("Customers"))
		{
			vecCustomerSiteNewDOs = new Vector<CustomerSite_NewDO>();
			customerDetailsBL = new CustomerDetailsDA();
		}
		if(localName.equalsIgnoreCase("CustomerDco"))
		{
			objCustomerSiteNewDO = new CustomerSite_NewDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_SITE+preference.getStringFromPreference(Preference.EMP_NO,
									"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			LogUtils.errorLog("CurrentTime", "CustomerSiteByUserId - "+currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerSiteId"))
		{
			objCustomerSiteNewDO.CustomerSiteId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteName"))
		{
			objCustomerSiteNewDO.SiteName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerId"))
		{
			objCustomerSiteNewDO.CustomerId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address1"))
		{
			objCustomerSiteNewDO.Address1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Address2"))
		{
			objCustomerSiteNewDO.Address2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("City"))
		{
			objCustomerSiteNewDO.City = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Latitude"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.Latitude = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Longitude"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.Longitude = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Salesman"))
		{
			objCustomerSiteNewDO.Salesman = salesmanCode;
		}
		else if(localName.equalsIgnoreCase("PaymentType"))
		{
			objCustomerSiteNewDO.PaymentType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentTermCode"))
		{
			objCustomerSiteNewDO.PaymentTermCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PaymentTermDescription"))
		{
			objCustomerSiteNewDO.PaymentTermDescription = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TotalOutstandingBalance"))
		{
			objCustomerSiteNewDO.TotalOutstandingBalance = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ROUTE_CODE"))
		{
			objCustomerSiteNewDO.SubChannel = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreditLimit"))
		{
			objCustomerSiteNewDO.CreditLimit = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CUSTOMER_STATUS"))
		{
			objCustomerSiteNewDO.CustomerStatus = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO1"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.MOBILENO1 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MOBILENO2"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.MOBILENO2 = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Website"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.Website = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerGrade"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.CustomerGrade = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerType"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.CustomerType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LandmarkId"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.LandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesmanlandmarkId"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.SalesmanlandmarkId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Source"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.Source = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BlaseCustId"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.BlaseCustId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryId"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.CountryId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DOB"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.DOB = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AnniversaryDate"))
		{
			if(currentValue.toString()!=null)
				objCustomerSiteNewDO.AnniversaryDate = currentValue.toString();
		}
		
		/*************************************************************************/
		else if(localName.equalsIgnoreCase("ParentGroup"))
		{
			objCustomerSiteNewDO.ParentGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerPostingGroup"))
		{
			objCustomerSiteNewDO.CustomerPostingGroup = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerCategory"))
		{
			objCustomerSiteNewDO.CustomerCategory = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerSubCategory"))
		{
			objCustomerSiteNewDO.CustomerSubCategory = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerGroupCode"))
		{
			objCustomerSiteNewDO.CustomerGroupCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IsSchemeApplicable"))
		{
			if(currentValue.toString().equalsIgnoreCase("false"))
				objCustomerSiteNewDO.isSchemeApplicable = 0+"";
			else
				objCustomerSiteNewDO.isSchemeApplicable = 1+"";
		}
		/*************************************************************************/
		
		else if(localName.equalsIgnoreCase("CustomerDco"))
		{
			vecCustomerSiteNewDOs.add(objCustomerSiteNewDO);
		}
		else if(localName.equalsIgnoreCase("Customers"))
		{
			if(insertIntoDatebase(vecCustomerSiteNewDOs))
				preference.commitPreference();
		}
		
	}
	
	private boolean insertIntoDatebase(Vector<CustomerSite_NewDO> vecCustomerSiteNewDOs) 
	{
		if(vecCustomerSiteNewDOs!=null && vecCustomerSiteNewDOs.size()>0)
			return customerDetailsBL.insertCustomerSite(vecCustomerSiteNewDOs);
		else
			return false;
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getStatus()
	{
		if(!status.equalsIgnoreCase("Success"))
			return false;
		
		return true;
	}
}
