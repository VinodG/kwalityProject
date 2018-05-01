package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.StoreCheckDA;
import com.winit.alseer.salesman.dataobject.ServiceCaptureDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class ServiceCaptureParser extends BaseHandler
{
	private ServiceCaptureDO objServiceCaptureDO;
	
	public ServiceCaptureParser(Context context, ServiceCaptureDO serviceCaptureDO) 
	{
		super(context);
		this.objServiceCaptureDO = serviceCaptureDO;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("InsertServiceCaptureResult"))
		{
			objServiceCaptureDO = new ServiceCaptureDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("UserCode"))
		{
			objServiceCaptureDO.UserCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerCode"))
		{
			objServiceCaptureDO.CustomerCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BeforeImage"))
		{
			objServiceCaptureDO.BeforeImage = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AfterImage"))
		{
			objServiceCaptureDO.AfterImage = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreatedDate"))
		{
			objServiceCaptureDO.CreatedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			objServiceCaptureDO.Status = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("InsertServiceCaptureResult"))
		{
			if(objServiceCaptureDO != null)
				new StoreCheckDA().updateServicecaptureDO(objServiceCaptureDO);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}

