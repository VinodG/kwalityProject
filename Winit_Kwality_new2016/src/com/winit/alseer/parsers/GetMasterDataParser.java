package com.winit.alseer.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.EOTDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetMasterDataParser extends BaseHandler
{
	String strUrl = "";
	SynLogDO syncLogDO = new SynLogDO();
	public GetMasterDataParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("ServerTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_DISCOUNTS+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_GROUP+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_SITE+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GET_CUSTOMER_HISTORY_WITH_SYNC+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(Preference.OFFLINE_DATE, CalendarUtils.getOrderPostDate());
			
			preference.saveStringInPreference(ServiceURLs.GET_ALL_VEHICLES+preference.getStringFromPreference(Preference.EMP_NO,"")+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(ServiceURLs.GET_All_PRICE_WITH_SYNC+Preference.LAST_SYNC_TIME, currentValue.toString());
		
			preference.saveStringInPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, currentValue.toString());
		
			preference.saveStringInPreference(ServiceURLs.GET_JOURNEY_PLAN+preference.getStringFromPreference(Preference.EMP_NO,
			"")+Preference.LAST_SYNC_TIME, currentValue.toString().toString());
			
			
			preference.saveStringInPreference(ServiceURLs.GET_SPLASH_SCREEN_DATA_FOR_SYNC+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			
			preference.saveStringInPreference(ServiceURLs.GetHouseHoldMastersWithSync+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			preference.saveStringInPreference(Preference.GetAllPromotions , currentValue.toString());
		
			//Added by awaneesh bescause second time logi eot becomes false
			preference.saveStringInPreference(ServiceURLs.GET_PENDING_SALES_INVOICE+Preference.LAST_JOURNEY_DATE, CalendarUtils.getOrderPostDate());
			
			//Added by venkat for Syncing all data
			preference.saveStringInPreference(ServiceURLs.GetAllDataSync+Preference.LAST_SYNC_TIME, currentValue.toString());
			
			
			preference.commitPreference();
			
			
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			preference.saveStringInPreference(Preference.LSD , currentValue.toString().trim());
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			preference.saveStringInPreference(Preference.LST , currentValue.toString().trim());
		}
		else if(localName.equalsIgnoreCase("sqliteFileName"))
		{
			strUrl = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IsEOTDone"))
		{
			String status = "";
			if(currentValue.toString().equalsIgnoreCase("true"))
			{
				status = "True";
				preference.saveBooleanInPreference(Preference.IS_EOT_DONE, true);
			}
			else
			{
				status = "False";				
				preference.saveBooleanInPreference(Preference.IS_EOT_DONE, false);
			}
			new EOTDA().insertEOT(preference.getStringFromPreference(preference.USER_ID, ""), preference.getStringFromPreference(preference.USER_NAME, ""),status);
			
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("IsStockVerified"))
		{
			if(currentValue.toString().equalsIgnoreCase("true"))
			{
				preference.saveBooleanInPreference(Preference.IsStockVerified, true);
			}
			else
				preference.saveBooleanInPreference(Preference.IsStockVerified, false);
			
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("StartOdometerReading"))
		{
			preference.saveIntInPreference(Preference.STARTDAY_VALUE, StringUtils.getInt(currentValue.toString()));
			preference.commitPreference();
		}
		else if(localName.equalsIgnoreCase("JourneyStartTime"))
		{
			String strTime = "";
			try 
			{
				strTime = currentValue.toString().split(" ")[1].split(":")[0]+":"+currentValue.toString().split(" ")[1].split(":")[1]+" "+currentValue.toString().split(" ")[2];
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			preference.saveStringInPreference(Preference.STARTDAY_TIME, strTime);
			preference.saveStringInPreference(Preference.STARTDAY_TIME_ACTUAL, currentValue.toString());
			preference.commitPreference();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	public String getMasterDataURL()
	{
		return strUrl;
	}
}
