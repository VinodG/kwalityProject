package com.winit.alseer.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.utilities.StringUtils;

public class UploadChequeImagesParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private ArrayList<String> arrList;
	private String strStatus = "";
	private final int ENABLE = 100, DISABLE = 200;
	private int ENABLE_PARSING = DISABLE;
	private String trxCode = "";
	
	public UploadChequeImagesParser(Context context) 
	{
		super(context);
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue  = new StringBuilder();
		if(localName.equalsIgnoreCase("ReceiptNumbers"))
		{
			arrList = new ArrayList<String>();
		}
		else if(localName.equalsIgnoreCase("ReceiptNumberDco"))
		{
			ENABLE_PARSING = ENABLE;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		switch (ENABLE_PARSING) 
		{
		case ENABLE:
			if(localName.equalsIgnoreCase("Status"))
			{
				int status = StringUtils.getInt(currentValue.toString());
				if(status == 1)
					arrList.add(trxCode);
			}
			else if(localName.equalsIgnoreCase("NewNumber"))
			{
				trxCode = currentValue.toString();
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
	
	
	public boolean getStatus()
	{
		if(strStatus.equalsIgnoreCase("Success"))
			return true;
		
		return false; 
	}
	
	public ArrayList<String> getData()
	{
		return arrList;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
