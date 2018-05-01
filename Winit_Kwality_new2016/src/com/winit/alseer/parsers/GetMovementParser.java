package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;

public class GetMovementParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false, isDetail = false;
	private Vector<NameIDDo> vec;
	private NameIDDo nameIDDo;
	private String responseCode = "";
	public GetMovementParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			vec = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
		{
			nameIDDo = new NameIDDo();
			isDetail = true;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("MovementStatus"))
		{
			responseCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementCode") && isDetail)
		{
			nameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode") && isDetail)
		{
			nameIDDo.strName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ShippedQuantity") && isDetail)
		{
			nameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementHeaderDco"))
		{
			vec.add(nameIDDo);
		}
		else if(localName.equalsIgnoreCase("MovementHeaders"))
		{
			if(vec!=null && vec.size()>0 && responseCode.equalsIgnoreCase("100"))
				new InventoryDA().updateQty(vec);
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
		return responseCode.equalsIgnoreCase("100");
	}
	
	public Vector<NameIDDo> getData()
	{
		return vec;
	}
}
