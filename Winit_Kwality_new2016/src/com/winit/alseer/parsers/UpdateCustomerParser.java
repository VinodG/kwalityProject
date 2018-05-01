package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.winit.alseer.salesman.dataobject.InsertCustoDo;

public class UpdateCustomerParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false, status = false;
	private InsertCustoDo objInsertCustomer;
	private Vector<InsertCustoDo> vecCustomerSiteIdDco;
	private String customerSiteId="";
	public UpdateCustomerParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("CustomerSites"))
		{
			vecCustomerSiteIdDco = new Vector<InsertCustoDo>();
		}
		else if(localName.equalsIgnoreCase("CustomerSiteIdDco"))
		{
			objInsertCustomer = new InsertCustoDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("status"))
		{
			Log.e("status", "status - "+currentValue.toString());
			if(currentValue.toString().equalsIgnoreCase("Success"))
				status = true;
			else
				status = false;
		}
		else if(localName.equalsIgnoreCase("OldSiteId"))
		{
			objInsertCustomer.strOldSiteId = currentValue.toString();
			Log.e("strOldSiteId", "strOldSiteId - "+objInsertCustomer.strOldSiteId);
		}
		else if(localName.equalsIgnoreCase("NewSiteId"))
		{
			objInsertCustomer.strNewSiteId = currentValue.toString();
			customerSiteId=objInsertCustomer.strNewSiteId;
			Log.e("strNewSiteId", "strNewSiteId - "+objInsertCustomer.strNewSiteId);
		}
		else if(localName.equalsIgnoreCase("CustomerSiteIdDco"))
		{
			vecCustomerSiteIdDco.add(objInsertCustomer);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getStatus()
	{
		return status;
	}
	public String getCustomerSiteId()
	{
		return customerSiteId;
	}
}
