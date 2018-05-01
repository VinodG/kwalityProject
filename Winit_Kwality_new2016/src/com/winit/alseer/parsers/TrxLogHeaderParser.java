package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.TransactionsLogsDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.TrxLogHeaders;

public class TrxLogHeaderParser extends BaseHandler
{
	private Vector<TrxLogHeaders> vecTrxLogHeaders ;
	private TrxLogHeaders objTrxLogHeaders;
	
	public TrxLogHeaderParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("TrxLogHeaderDcos"))
		{
			vecTrxLogHeaders = new Vector<TrxLogHeaders>();
		}
		else if(localName.equalsIgnoreCase("TrxLogHeaderDco"))
		{
			objTrxLogHeaders = new TrxLogHeaders();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Date"))
		{
			objTrxLogHeaders.TrxDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("TotalScheduledCalls"))
		{
			objTrxLogHeaders.TotalScheduledCalls = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalActualCalls"))
		{
			objTrxLogHeaders.TotalActualCalls = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalProductiveCalls"))
		{
			objTrxLogHeaders.TotalProductiveCalls = string2Int(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalSales"))
		{
			objTrxLogHeaders.TotalSales = string2Double(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalMonthSales"))
		{
			objTrxLogHeaders.CurrentMonthlySales = string2Double(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalCreditNotes"))
		{
			objTrxLogHeaders.TotalCreditNotes = string2Double(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("TotalCollections"))
		{
			objTrxLogHeaders.TotalCollections = string2Double(currentValue.toString());
		}
		
		else if(localName.equalsIgnoreCase("TrxLogHeaderDco"))
		{
			vecTrxLogHeaders.add(objTrxLogHeaders);
		}
		else if(localName.equalsIgnoreCase("TrxLogHeaderDcos"))
		{
		/*	if(vecTrxLogHeaders != null && vecTrxLogHeaders.size() > 0)
				new TransactionsLogsDA().insertTrxLogHeaders(vecTrxLogHeaders);*/
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertBankData(Vector<NameIDDo> vecBankNames) 
	{
		new CommonDA().insertBankDetails(vecBankNames);
	}
}
