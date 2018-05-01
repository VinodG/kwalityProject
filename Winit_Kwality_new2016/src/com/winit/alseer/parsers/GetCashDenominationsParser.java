package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CashDenominationDA;
import com.winit.alseer.salesman.dataobject.CashDenominationDO;

public class GetCashDenominationsParser extends BaseHandler
{
	private Vector<CashDenominationDO> vecCashDenominationDOs ;
	private CashDenominationDO cashDenominationDO;
	
	public GetCashDenominationsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("CashDenominations"))
		{
			vecCashDenominationDOs = new Vector<CashDenominationDO>();
		}
		else if(localName.equalsIgnoreCase("CashDenominationDco"))
		{
			cashDenominationDO = new CashDenominationDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CashDenominationId"))
		{
			cashDenominationDO.CashDenamationId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CashDenominationCode"))
		{
			cashDenominationDO.CashDenamationCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Name"))
		{
			cashDenominationDO.Name = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Amount"))
		{
			cashDenominationDO.Amount = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Thumb"))
		{
			cashDenominationDO.Thumb = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Picture"))
		{
			cashDenominationDO.Thumb = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("CreatedBy"))
		{
			cashDenominationDO.CreatedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedBy"))
		{
			cashDenominationDO.ModifiedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			cashDenominationDO.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			cashDenominationDO.ModifiedTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CashDenominationDco"))
		{
			vecCashDenominationDOs.add(cashDenominationDO);
		}
		else if(localName.equalsIgnoreCase("CashDenominations"))
		{
			if(vecCashDenominationDOs != null && vecCashDenominationDOs.size() > 0)
				insertCashDenomination(vecCashDenominationDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertCashDenomination(Vector<CashDenominationDO> vecCashDenominationDOs) 
	{
		new CashDenominationDA().insertCashDenominations(vecCashDenominationDOs);
	}
}
