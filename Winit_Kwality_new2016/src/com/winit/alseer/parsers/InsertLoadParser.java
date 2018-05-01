package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;

public class InsertLoadParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false, status = false;
	private Vector<NameIDDo> vec;
	private NameIDDo nameIDDo;
	public InsertLoadParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("MovementStatusList"))
		{
			vec = new Vector<NameIDDo>();
		}
		else if(localName.equalsIgnoreCase("MovementStatusDco"))
		{
			nameIDDo = new NameIDDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("MovementCode"))
		{
			nameIDDo.strId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AppMovementId"))
		{
			nameIDDo.strType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MovementStatusDco"))
		{
			vec.add(nameIDDo);
		}
		else if(localName.equalsIgnoreCase("MovementStatusList"))
		{
			if(vec!=null && vec.size()>0)
				new InventoryDA().updateStatus(vec);
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
}
