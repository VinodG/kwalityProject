package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataobject.CurrencyDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class GetCurrenciesParser extends BaseHandler
{
	private Vector<CurrencyDO> vecCurrencyDOs ;
	private CurrencyDO objCurrencyDO;
	
	public GetCurrenciesParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Currencies"))
		{
			vecCurrencyDOs = new Vector<CurrencyDO>();
		}
		else if(localName.equalsIgnoreCase("CurrencyDco"))
		{
			objCurrencyDO = new CurrencyDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("CurrencyId"))
		{
			objCurrencyDO.CurrencyId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Code"))
		{
			objCurrencyDO.Code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Name"))
		{
			objCurrencyDO.Name = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("Rate"))
		{
			objCurrencyDO.Rate = StringUtils.getFloat(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("LevelId"))
		{
			objCurrencyDO.LevelId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Decimals"))
		{
			objCurrencyDO.Decimals = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsActive"))
		{
			objCurrencyDO.IsActive = StringUtils.getBoolean(currentValue.toString());
		}
		
		
		else if(localName.equalsIgnoreCase("CurrencyDco"))
		{
			vecCurrencyDOs.add(objCurrencyDO);
		}
		else if(localName.equalsIgnoreCase("Currencies"))
		{
			if(vecCurrencyDOs != null && vecCurrencyDOs.size() > 0)
				insertBankData(vecCurrencyDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertBankData(Vector<CurrencyDO> vecCurrencies) 
	{
//		new CommonDA().insertCurrencyDetails(vecCurrencies);
	}
}
