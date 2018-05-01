/**
 * 
 */
package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SellingSKUDA;
import com.winit.alseer.salesman.dataobject.CustomerSellingSKUGroup;

/**
 * @author Aritra.Pal
 *
 */
public class CustomerSellingSKUGroupParser extends BaseHandler 
{

	Vector<CustomerSellingSKUGroup> vecCustomerSellingSKUGroup;
	CustomerSellingSKUGroup objCustomerSellingSKUGroup;
	/**
	 * @param context
	 */
	public CustomerSellingSKUGroupParser(Context context) 
	{
		super(context);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("CustomerSellingSKUGroups"))
		{
			vecCustomerSellingSKUGroup = new Vector<CustomerSellingSKUGroup>();
		}
		else if(localName.equalsIgnoreCase("CustomerSellingSKUGroupDco"))
		{
			objCustomerSellingSKUGroup = new CustomerSellingSKUGroup();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CustomerSellingSKUGroupId"))
		{
			objCustomerSellingSKUGroup.CustomerSellingSKUGroupId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Code"))
		{
			objCustomerSellingSKUGroup.Code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Name"))
		{
			objCustomerSellingSKUGroup.Name = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FieldName"))
		{
			objCustomerSellingSKUGroup.FieldName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Priority"))
		{
			objCustomerSellingSKUGroup.Priority = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CustomerSellingSKUGroupDco"))
		{
			vecCustomerSellingSKUGroup.add(objCustomerSellingSKUGroup);
		}
		else if(localName.equalsIgnoreCase("CustomerSellingSKUGroups"))
		{
			new SellingSKUDA().insertCustomerSellingSKUGroup(vecCustomerSellingSKUGroup);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
