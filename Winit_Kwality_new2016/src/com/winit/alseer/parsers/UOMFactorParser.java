package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.UOMFactorDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class UOMFactorParser extends BaseHandler
{
	private Vector<UOMFactorDO> vecUOMFactorDOs ;
	private UOMFactorDO objUOMFactorDO;
	
	public UOMFactorParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("UOMFactors"))
		{
			vecUOMFactorDOs = new Vector<UOMFactorDO>();
		}
		else if(localName.equalsIgnoreCase("UOMFactorDco"))
		{
			objUOMFactorDO = new UOMFactorDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("ItemCode"))
		{
			objUOMFactorDO.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UOM"))
		{
			objUOMFactorDO.UOM = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Factor"))
		{
			objUOMFactorDO.Factor = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objUOMFactorDO.ModifiedDate =StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objUOMFactorDO.ModifiedTime =StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("EAConversion"))
		{
			objUOMFactorDO.EAConversion = (int)StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("UOMFactorDco"))
		{
			vecUOMFactorDOs.add(objUOMFactorDO);
		}
		else if(localName.equalsIgnoreCase("UOMFactors"))
		{
			if(vecUOMFactorDOs != null && vecUOMFactorDOs.size() > 0)
				insertUOMFactors(vecUOMFactorDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertUOMFactors(Vector<UOMFactorDO> vecUOMFactorDO) 
	{
		new CommonDA().insertUOMFactorDetails(vecUOMFactorDO);
	}
}

