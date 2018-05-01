package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataobject.WareHouseDO;

public class WareHouseParser extends BaseHandler
{
	private Vector<WareHouseDO> vecWareHouseDO ;
	private WareHouseDO wareHouseDO;
	
	public WareHouseParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("WareHouses"))
		{
			vecWareHouseDO = new Vector<WareHouseDO>();
		}
		else if(localName.equalsIgnoreCase("WareHouseDco"))
		{
			wareHouseDO = new WareHouseDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Code"))
		{
			wareHouseDO.Code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Description"))
		{
			wareHouseDO.Name = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesOrgCode"))
		{
			wareHouseDO.SalesOrgCode = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("WareHouseDco"))
		{
			vecWareHouseDO.add(wareHouseDO);
		}
		else if(localName.equalsIgnoreCase("WareHouses"))
		{
			if(vecWareHouseDO != null && vecWareHouseDO.size() > 0)
				insertBrandsData(vecWareHouseDO);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertBrandsData(Vector<WareHouseDO> vecWareHouses) 
	{
//		new CommonDA().insertWareHouseDetails(vecWareHouses);
	}
}

