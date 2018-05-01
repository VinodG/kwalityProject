package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

public class SaveServeyAnswerParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private String Status = "";
	
	public SaveServeyAnswerParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("PostSurveyAnswersResult"))
		{
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		 
		if(localName.equalsIgnoreCase("Status"))
		{
			Status  = currentValue.toString();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	public String getStatus()
	{
		return Status;
	}
}
