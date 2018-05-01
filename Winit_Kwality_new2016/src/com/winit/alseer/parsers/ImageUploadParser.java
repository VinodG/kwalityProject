package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

public class ImageUploadParser extends BaseHandler
{
	public ImageUploadParser(Context context) 
	{
		super(context);
	}

	private boolean executionStatus;
	
	private boolean currentElement = false;
	private StringBuffer sBuffer;
	private String filPath = "";
	
	private String errorMessage = "";
	
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement = true;
		sBuffer = new StringBuffer();
	}
	
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement = false;
		
		if(localName.equalsIgnoreCase("FileName"))
		{
			filPath = sb2String(sBuffer);
		}
		else if(localName.equalsIgnoreCase("Message"))
		{
			if(sb2String(sBuffer).equalsIgnoreCase("successful"))
			{
				executionStatus = true;
			}
			else
			{
				errorMessage = sb2String(sBuffer);
				executionStatus = false;
			}
		}
	}
	
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement)
		{
			sBuffer.append(ch, start, length);
			currentElement = false;
		}
	}
	
	public String getUploadedFileName()
	{
		return filPath;
	}
}
