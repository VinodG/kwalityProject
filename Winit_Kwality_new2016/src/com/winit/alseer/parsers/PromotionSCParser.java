package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.PromotionSCDA;
import com.winit.alseer.salesman.dataobject.PromotionSCDO;

public class PromotionSCParser extends BaseHandler
{
	private Vector<PromotionSCDO> vecPromotionSCDOs;
	private PromotionSCDO promotionSCDO;
	
	public PromotionSCParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("PromotionSCs"))
		{
			vecPromotionSCDOs = new Vector<PromotionSCDO>();
		}
		else if(localName.equalsIgnoreCase("PromotionSCDco"))
		{
			promotionSCDO = new PromotionSCDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("PromotionId"))
		{
			promotionSCDO.PromotionId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Description"))
		{
			promotionSCDO.Description = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PromoItemCode"))
		{
			promotionSCDO.PromoItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PromoItemQuantity"))
		{
			promotionSCDO.PromoItemQuantity = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("PromoType"))
		{
			promotionSCDO.PromoType = currentValue.toString();
		}
	    else if(localName.equalsIgnoreCase("FOCItemCode"))
		{
	    	promotionSCDO.FOCItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("FOCItemQuantity"))
		{
			promotionSCDO.FOCItemQuantity = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Discount"))
		{
			promotionSCDO.Discount = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsActive"))
		{
			promotionSCDO.IsActive = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreatedBy"))
		{
			promotionSCDO.CreatedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			promotionSCDO.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			promotionSCDO.ModifiedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("PromotionSCDco"))
		{
			vecPromotionSCDOs.add(promotionSCDO);
		}
		else if(localName.equalsIgnoreCase("PromotionSCs"))
		{
			new PromotionSCDA().insertPromotionSC(vecPromotionSCDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}

