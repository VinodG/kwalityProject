package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataaccesslayer.ThingstoFocusDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.ThingstofocusDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetThingstofocusParser extends BaseHandler
{
	private ThingstofocusDO thingstofocusDO;
	private Vector<ThingstofocusDO> vecThingstofocusDO;
	SynLogDO synLogDO = new SynLogDO();
	public GetThingstofocusParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("ThingsToResponses"))
		{
			vecThingstofocusDO = new Vector<ThingstofocusDO>();
		}
		else if(localName.equalsIgnoreCase("ThingsToFocusDco"))
		{
			thingstofocusDO = new ThingstofocusDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FocusId"))
		{
			thingstofocusDO.FocusId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Type"))
		{
			thingstofocusDO.Type = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Title"))
		{
			thingstofocusDO.Title = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SubTitle"))
		{
			thingstofocusDO.SubTitle = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Content"))
		{
			thingstofocusDO.Content = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ImagePath"))
		{
			thingstofocusDO.ImagePath = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VAlign"))
		{
			thingstofocusDO.VAlign = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("HAlign"))
		{
			thingstofocusDO.HAlign = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DisplayAt"))
		{
			thingstofocusDO.DisplayAt = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("MappingId"))
		{
			thingstofocusDO.MappingId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Code"))
		{
			thingstofocusDO.Code = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("ThingsToFocusDco"))
		{
			vecThingstofocusDO.add(thingstofocusDO);
		}
		else if(localName.equalsIgnoreCase("ThingsToResponses"))
		{
			if( vecThingstofocusDO != null && vecThingstofocusDO.size() > 0)
			{
				if(saveIntointoThingstofocustable(vecThingstofocusDO))
				{
					preference.commitPreference();
					synLogDO.entity		= ServiceURLs.GetThingsToFocusesByUserId;
					new SynLogDA().insertSynchLog(synLogDO);
				}
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveIntointoThingstofocustable(Vector<ThingstofocusDO> vecThingstofocusDO2)
	{
		return new ThingstoFocusDA().insertThingstofocus(vecThingstofocusDO2);
	}
}
