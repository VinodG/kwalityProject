package com.winit.alseer.parsers;

import java.io.InputStream;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;

import com.winit.alseer.salesman.dataaccesslayer.CustomerDA;
import com.winit.alseer.salesman.dataobject.InsertCustoDo;

public class AddNewCustomerParser extends BaseHandler
{
	private final int ENABLE = 100, DISABLE = 200;
	private StringBuilder currentValue ;
	private boolean currentElement = false, status = false;
	private InsertCustoDo objInsertCustomer;
	private Vector<InsertCustoDo> vecCustomerSiteIdDco;
	private String customerSiteId="";
	private int ENABLE_PARSING = DISABLE;
	private String strStatus = "";
	private String Msg;
	
	public AddNewCustomerParser(InputStream is) 
	{
		super(is);
		// TODO Auto-generated constructor stub
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
		switch (ENABLE_PARSING) 
		{
			case ENABLE:
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
				else if(localName.equalsIgnoreCase("SiteId"))
				{
					objInsertCustomer.strNewSiteId = currentValue.toString();
					customerSiteId=objInsertCustomer.strNewSiteId;
					Log.e("strNewSiteId", "strNewSiteId - "+objInsertCustomer.strNewSiteId);
				}
				else if(localName.equalsIgnoreCase("CustomerSiteIdDco"))
				{
					vecCustomerSiteIdDco.add(objInsertCustomer);
				}
				else if(localName.equalsIgnoreCase("Msg"))
				{
					Msg = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("CustomerSites"))
				{
					if(vecCustomerSiteIdDco!=null && vecCustomerSiteIdDco.size()>0)
					{
						status=new CustomerDA().insertCustomerSync(vecCustomerSiteIdDco.get(0).strNewSiteId,1);
					}
				}
			break;
			case DISABLE:
				if(localName.equalsIgnoreCase("Status"))
					strStatus = currentValue.toString();
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public boolean getResponseStatus()
	{
		if(strStatus != null && strStatus.equalsIgnoreCase("Success"))
			return true;
		return false;
	}
	
	public boolean getStatus()
	{
		if(Msg!=null && Msg.equalsIgnoreCase("SiteId Already exists."))
			return true;
		return status;
	}
	
	public String getCustomerSiteId()
	{
		return customerSiteId;
	}
}
