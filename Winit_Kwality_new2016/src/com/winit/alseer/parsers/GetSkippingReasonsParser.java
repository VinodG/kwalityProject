package com.winit.alseer.parsers;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.SkippingReasonDO;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Vector;

public class GetSkippingReasonsParser extends BaseHandler
{
	private Vector<SkippingReasonDO> vec ;
	private SkippingReasonDO skippingReasonDO;

	public GetSkippingReasonsParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("SkippingReasons"))
		{
			vec = new Vector<SkippingReasonDO>();
		}
		else if(localName.equalsIgnoreCase("SkippingReasonDco"))
		{
			skippingReasonDO = new SkippingReasonDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("PresellerId"))
		{
			skippingReasonDO.PresellerId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SkippingDate"))
		{
			skippingReasonDO.SkipDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Reason"))
		{
			skippingReasonDO.Reason = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SiteId"))
		{
			skippingReasonDO.CustomerSiteId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Type"))
		{
			skippingReasonDO.ReasonType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ReasonId"))
		{
			skippingReasonDO.ReasonId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("status"))
		{
			skippingReasonDO.Status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SkippingReasonDco"))
		{
			vec.add(skippingReasonDO);
		}
		else if(localName.equalsIgnoreCase("SkippingReasons"))
		{
			if(vec != null && vec.size() > 0)
				insertSkippingReasons(vec);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertSkippingReasons(Vector<SkippingReasonDO> vec )
	{
		new CommonDA().insertSkippingReasons(vec);
	}
}
