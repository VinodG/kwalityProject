package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.MasterDA;
import com.winit.alseer.salesman.dataobject.LandmarkDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class LandmarkParser extends BaseHandler
{
	private final int PARSING_SCOPE_SERVERTIME = -2;
	private final int PARSING_SCOPE_INVALID = -1;
	private int parsingScope =  PARSING_SCOPE_INVALID;
	private final int PARSING_SCOPE_LANDMARK = 1;
	private StringBuilder sBuffer;
	private LandmarkDO landmarkDO;
	private Vector<LandmarkDO> vector;
	
	public LandmarkParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		switch (parsingScope) 
		{
			case PARSING_SCOPE_INVALID:
			{
				if(localName.equalsIgnoreCase("CurrentTime"))
					parsingScope = PARSING_SCOPE_SERVERTIME;
				
				else if(localName.equalsIgnoreCase("SalesmanLandmarkDco"))
				{
					vector = new Vector<LandmarkDO>();
					parsingScope = PARSING_SCOPE_LANDMARK;
				}
			}
			break;
			
			case PARSING_SCOPE_LANDMARK:
			{
				if(localName.equalsIgnoreCase("objLandmark"))
				{
					landmarkDO = new LandmarkDO();
				}
			}
			break;
		}
		
		if (parsingScope != PARSING_SCOPE_INVALID)
			sBuffer = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		switch (parsingScope) 
		{
			case PARSING_SCOPE_SERVERTIME:
			{
				if(localName.equalsIgnoreCase("CurrentTime"))
				{
					preference.saveStringInPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, sBuffer.toString());
					parsingScope = PARSING_SCOPE_INVALID;
				}
			}
			break;
			
			case PARSING_SCOPE_LANDMARK:
			{
				if(localName.equalsIgnoreCase("LandmarkId"))
				{
					landmarkDO.LandmarkId = Integer.parseInt(sBuffer.toString());
				}
				else if(localName.equalsIgnoreCase("LandmarkName"))
				{
					landmarkDO.LandmarkName = sBuffer.toString();
				}
				else if(localName.equalsIgnoreCase("LOCATIONID"))
				{
					landmarkDO.LOCATIONID = sBuffer.toString();
				}
				else if(localName.equalsIgnoreCase("objLandmark"))
				{
					vector.add(landmarkDO);
				}
				else if(localName.equalsIgnoreCase("SalesmanLandmarkDco"))
				{
					if(vector.size() > 0)
						saveIntoUserTable(vector);
					parsingScope = PARSING_SCOPE_INVALID;
				}
			}
			break;
			
			case PARSING_SCOPE_INVALID:
			{
				if(localName.equalsIgnoreCase("GetSalesmanLandmarkWithSyncResponse"))
				{
					preference.commitPreference();
				}
			}
			break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
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
	
	private boolean saveIntoUserTable(Vector<LandmarkDO> vector)
	{
		return new MasterDA().inserttblLandMark(vector);
	}

}
