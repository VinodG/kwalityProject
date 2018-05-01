package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.AllUsersDo;
import com.winit.alseer.salesman.utilities.StringUtils;

public class InsertOrdersParserAdvance extends BaseHandler
{
	private final int ENABLE = 100, DISABLE = 200;
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private AllUsersDo objOrders;
	private Vector<AllUsersDo> vecOrderNumbers;
	private Preference preference;
	private String newOrderId="";
	private int ENABLE_PARSING = DISABLE;
	private String strStatus = "";
	public InsertOrdersParserAdvance(Context context) 
	{
		super(context);
		preference  = new Preference(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("TrxStatusList"))
		{
			vecOrderNumbers = new Vector<AllUsersDo>();
		}
		else if(localName.equalsIgnoreCase("TrxStatusDco"))
		{
			ENABLE_PARSING = ENABLE;
			objOrders = new AllUsersDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		switch (ENABLE_PARSING) 
		{
		case ENABLE:
			if(localName.equalsIgnoreCase("OrderNumber"))
			{
				objOrders.strOldOrderNumber = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("AppId"))
			{
				objOrders.strUUID = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("Status"))
			{
				objOrders.pushStatus = StringUtils.getInt(currentValue.toString());
				if(objOrders.pushStatus == 1)
					objOrders.pushStatus = 2;
			}
			//need to verify with service
			else if(localName.equalsIgnoreCase("Message"))
			{
				objOrders.message = currentValue.toString();
			}
			else if(localName.equalsIgnoreCase("TrxStatusDco"))
			{
				vecOrderNumbers.add(objOrders);
			}
			else if(localName.equalsIgnoreCase("TrxStatusList"))
			{
				ENABLE_PARSING = DISABLE;
				updateOrders(vecOrderNumbers);
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
	
	public String getNewOrderId()
	{
		return newOrderId;
	}
	public boolean updateOrders(Vector<AllUsersDo> vecOrderNumbers)
	{
		boolean result = false;
		result = new CommonDA().updateOrderNumbers(vecOrderNumbers, preference.getStringFromPreference(Preference.SALESMANCODE, ""));
		return result;
	}
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}

	public boolean getStatus()
	{
		if(strStatus != null && strStatus.equalsIgnoreCase("Success"))
			return true;
		return false;
	}
}
