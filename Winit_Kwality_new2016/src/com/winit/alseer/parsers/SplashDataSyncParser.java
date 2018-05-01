package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class SplashDataSyncParser extends BaseHandler
{
	private Context context;
	private StringBuilder sBuffer;
	
	private final int PARSING_SCOPE_USERS = 1;
	private final int PARSING_SCOPE_REASONS = 2;
	private final int PARSING_SCOPE_LOCATIONS = 3;
	private final int PARSING_SCOPE_CATEGORIES = 4;
	private final int PARSING_SCOPE_ITEMS = 5;
	private final int PARSING_SCOPE_PRICE = 10;
	private final int PARSING_SCOPE_BANKS = 6;
	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;

	BaseHandler currentHandler;
	SynLogDO syncLogDO = new SynLogDO();
	
	public SplashDataSyncParser(Context context)
	{
		super(context);
		this.context = context;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		switch (parsingScope)
		{
			case (PARSING_SCOPE_INVALID):
			{
				if (localName.equalsIgnoreCase("ServerTime"))
				{
					parsingScope = PARSING_SCOPE_SERVERTIME;
				}
				else if (localName.equalsIgnoreCase("ModifiedDate"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("ModifiedTime"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				else if (localName.equalsIgnoreCase("BlaseUsers"))
				{
					parsingScope   = PARSING_SCOPE_USERS;
					currentHandler = new GetAllUserParser(context);
				}
				else if (localName.equalsIgnoreCase("Reasons"))
				{
					parsingScope   = PARSING_SCOPE_REASONS;
					currentHandler = new GetAllReasonsParser(context);
				}
				else if (localName.equalsIgnoreCase("Locations"))
				{
					parsingScope   = PARSING_SCOPE_LOCATIONS;
					currentHandler = new RegionListParser(context);
				}
				else if (localName.equalsIgnoreCase("Categories"))
				{
					parsingScope   = PARSING_SCOPE_CATEGORIES;
					currentHandler = new GetAllCategories(context);
				}
				else if (localName.equalsIgnoreCase("Items"))
				{
					parsingScope   = PARSING_SCOPE_ITEMS;
					currentHandler = new GetAllItemsParser(context);
				}
				else if (localName.equalsIgnoreCase("Banks"))
				{
					parsingScope   = PARSING_SCOPE_BANKS;
					currentHandler = new GetBanksParser(context);
				}
				else if (localName.equalsIgnoreCase("Prices"))
				{
					parsingScope   = PARSING_SCOPE_PRICE;
					currentHandler = new GetPricingParser(context);
				}
				if(currentHandler != null)
					currentHandler.startElement(uri, localName, qName, attributes);
			}
			break;
			
			case (PARSING_SCOPE_USERS):
			case (PARSING_SCOPE_REASONS):
			case (PARSING_SCOPE_LOCATIONS):
			case (PARSING_SCOPE_CATEGORIES):
			case (PARSING_SCOPE_ITEMS):
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_PRICE):
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
			case (PARSING_SCOPE_USERS):
			case (PARSING_SCOPE_REASONS):
			case (PARSING_SCOPE_LOCATIONS):
			case (PARSING_SCOPE_CATEGORIES):
			case (PARSING_SCOPE_ITEMS):
			case (PARSING_SCOPE_BANKS):
			case (PARSING_SCOPE_PRICE):
			{
				currentHandler.currentValue = sBuffer;
				currentHandler.endElement(uri, localName, qName);
				
				if(localName.equalsIgnoreCase("BlaseUsers") ||
				   localName.equalsIgnoreCase("Reasons") ||
				   localName.equalsIgnoreCase("Locations") ||
				   localName.equalsIgnoreCase("Categories") ||
				   localName.equalsIgnoreCase("Items") ||
				   localName.equalsIgnoreCase("Banks")||
				   localName.equalsIgnoreCase("Prices"))
				{
					parsingScope = PARSING_SCOPE_INVALID;
					currentHandler = null;
				}
			}
			break;
			
			case (PARSING_SCOPE_SERVERTIME):
			{
				if(localName.equalsIgnoreCase("ServerTime"))
				{
					syncLogDO.TimeStamp = sBuffer.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
				if(localName.equalsIgnoreCase("ModifiedDate"))
				{
					syncLogDO.UPMJ = sBuffer.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					syncLogDO.action =  currentValue.toString();
				}
				if(localName.equalsIgnoreCase("ModifiedTime"))
				{
					syncLogDO.UPMT = sBuffer.toString();
					preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
					syncLogDO.entity = ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC;
					new SynLogDA().insertSynchLog(syncLogDO);
				}
			}
			break;
			
			case (PARSING_SCOPE_INVALID):
			{
				if(localName.equalsIgnoreCase("SplashScreenData"))
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
