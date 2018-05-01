package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.Customer_GroupDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetCustomerGroupByUserIdParser extends BaseHandler
{
	private StringBuilder currentValue;
	private String status = "";
	private boolean currentElement = false;
	private Customer_GroupDO objCustomerGroupDO;
	private CustomerDetailsDA customerDetailsBL;
	private Vector<Customer_GroupDO> vecCustomer_GroupDO;
	public GetCustomerGroupByUserIdParser(Context context) 
	{
		super(context); 
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("CustomerGroups"))
		{
			vecCustomer_GroupDO = new Vector<Customer_GroupDO>();
			customerDetailsBL = new CustomerDetailsDA();
		}
		if(localName.equalsIgnoreCase("CustomerGroupDco"))
		{
			objCustomerGroupDO = new Customer_GroupDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_GROUP+preference.getStringFromPreference(Preference.EMP_NO,
									"")+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CompanyId"))
		{
			objCustomerGroupDO.CompanyId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustpriceClass"))
		{
			objCustomerGroupDO.CustpriceClass = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteNumber"))
		{
			objCustomerGroupDO.SiteNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerGroupDco"))
		{
			vecCustomer_GroupDO.add(objCustomerGroupDO);
		}
		else if(localName.equalsIgnoreCase("CustomerGroups"))
		{
			if(insertIntoDatebase(vecCustomer_GroupDO))
				preference.commitPreference();
		}
	}
	
	private boolean insertIntoDatebase(Vector<Customer_GroupDO> vec) 
	{
		return customerDetailsBL.insertCustomerGroup(vec);
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
