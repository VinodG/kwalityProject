package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
public class TrxLogResponseParser extends BaseHandler
{
	private Context context;
	private StringBuilder sBuffer;
	private final int PARSING_SCOPE_TRX_LOG_HEADERS=95;
	private final int PARSING_SCOPE_tRX_LOG_DETAILS=96;
	
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	private String empNo;
	BaseHandler currentHandler;
	SynLogDO syncLogDO = new SynLogDO();
	public Preference preference;
	//TRXStatus
	public TrxLogResponseParser(Context context)
	{
		super(context);
		this.context = context;
		preference = new Preference(context);
	}
	public TrxLogResponseParser(Context context, String empNo) 
	{
		super(context);
		this.context = context;
		this.empNo = empNo;
		preference = new Preference(context);
	}
	//tblSellingSKUClassification
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_INVALID):
			{
				 if (localName.equalsIgnoreCase("TrxLogHeaderDcos"))
				{
					parsingScope   = PARSING_SCOPE_TRX_LOG_HEADERS;
					currentHandler = new TrxLogHeaderParser(context);
				}
				
				else if (localName.equalsIgnoreCase("TrxLogDetailsDcos"))
				{
					parsingScope   = PARSING_SCOPE_tRX_LOG_DETAILS;
					currentHandler = new TrxLogDetailsParser(context);
				}
				
				
				
				
				LogUtils.debug("localName_parser", localName);
				if(currentHandler != null)
					currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
			
			
			case (PARSING_SCOPE_TRX_LOG_HEADERS): 
			case (PARSING_SCOPE_tRX_LOG_DETAILS): 
			{
				currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			sBuffer = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		switch (parsingScope)
		{
			
			case (PARSING_SCOPE_TRX_LOG_HEADERS): 
			case (PARSING_SCOPE_tRX_LOG_DETAILS): 
			{
				currentHandler.currentValue = sBuffer;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("TrxLogHeaderDcos")||
				   localName.equalsIgnoreCase("TrxLogDetailsDcos"))
				{
					LogUtils.errorLog("localName", localName+" Completed");
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
				}
			}
			break;
			
			
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("TrxLogResponse"))
					preference.commitPreference();
			}
			break;
		}
	}

	public void characters(char[] ch, int start, int length) 
	{
		if (parsingScope == PARSING_SCOPE_INVALID || ch == null || length == 0 || sBuffer == null)
			return;

		try
		{
			sBuffer.append(ch, start, length);
		}
		catch (Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "XML ch[] appending exception:"+e.getMessage() );
		}
	}
}
