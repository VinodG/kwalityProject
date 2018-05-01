package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.DiscountDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.DiscountMasterDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetDiscountParser extends BaseHandler
{
	private StringBuilder currentValue;
	private String status = "";
	private boolean currentElement = false;
	private DiscountMasterDO objDiscountMasterDO;
	private Vector<DiscountMasterDO> vecDiscountMasterDOs;
	
	SynLogDO synLogDO = new SynLogDO();
	
	public GetDiscountParser(Context context) 
	{
		super(context); 
	}
	
	@Override 
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("discounts"))
		{
			vecDiscountMasterDOs = new Vector<DiscountMasterDO>();
		}
		else if(localName.equalsIgnoreCase("DiscountDco"))
		{
			objDiscountMasterDO = new DiscountMasterDO();
		}
//		else if (localName.equalsIgnoreCase("ServerTime"))
//		{
//			synLogDO = new SynLogDO();
//		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("ServerDateTime"))
		{
			synLogDO.TimeStamp  = currentValue.toString();;
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ  = currentValue.toString();;
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT  = currentValue.toString();;
		}
		else if(localName.equalsIgnoreCase("DiscountId"))
		{
			objDiscountMasterDO.DiscountId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteNumber"))
		{
			objDiscountMasterDO.Site_Number = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Level"))
		{
			objDiscountMasterDO.Level = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Code"))
		{
			objDiscountMasterDO.Code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DiscountType"))
		{
			objDiscountMasterDO.DiscountType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Discount"))
		{
			objDiscountMasterDO.Discount = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			objDiscountMasterDO.UOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MinQty"))
		{
			objDiscountMasterDO.MinQty = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MaxQty"))
		{
			objDiscountMasterDO.MaxQty = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DiscountDco"))
		{
			vecDiscountMasterDOs.add(objDiscountMasterDO);
		}
		else if(localName.equalsIgnoreCase("GetDiscountsResult"))
		{
			insertIntoDatebase(vecDiscountMasterDOs);
			synLogDO.entity = ServiceURLs.GET_DISCOUNTS;
			new SynLogDA().insertSynchLog(synLogDO);
		
		}
	}
	
	private boolean insertIntoDatebase(Vector<DiscountMasterDO> vec) 
	{
		if(vec != null && vec.size() > 0)
			return new DiscountDA().insertDiscounts(vec);
		
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
