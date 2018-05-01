package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.TrxLogDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;

public class TrxLogDetailsParser extends BaseHandler
{
	private Vector<TrxLogDetailsDO> vecTrxLogDetailsDO ;
	private TrxLogDetailsDO objTrxLogDetailsDO;
	
	public TrxLogDetailsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("TrxLogDetailsDcos"))
		{
			vecTrxLogDetailsDO = new Vector<TrxLogDetailsDO>();
		}
		else if(localName.equalsIgnoreCase("TrxLogDetailsDco"))
		{
			objTrxLogDetailsDO = new TrxLogDetailsDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Date"))
		{
			objTrxLogDetailsDO.Date = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerCode"))
		{
			objTrxLogDetailsDO.CustomerCode =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CustomerName"))
		{
			objTrxLogDetailsDO.CustomerName  =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TrxType"))
		{
			objTrxLogDetailsDO.TrxType =currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IsJP"))
		{
			objTrxLogDetailsDO.IsJp = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DocumentNumber"))
		{
			objTrxLogDetailsDO.DocumentNumber = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Amount"))
		{
			objTrxLogDetailsDO.Amount = string2Double(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TimeStamp"))
		{
			objTrxLogDetailsDO.TimeStamp = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("TrxLogDetailsDco"))
		{
			vecTrxLogDetailsDO.add(objTrxLogDetailsDO);
		}
		else if(localName.equalsIgnoreCase("TrxLogDetailsDcos"))
		{
			//no need to insert
			/*if(vecTrxLogDetailsDO != null && vecTrxLogDetailsDO.size() > 0)
				new TransactionsLogsDA().insertTrxLogDetails(vecTrxLogDetailsDO);*/
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	
}
