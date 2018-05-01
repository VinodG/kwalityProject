package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CustomerDetailsDA;
import com.winit.alseer.salesman.dataobject.SyncLogDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetAllDeletedCustomers extends BaseHandler{

	private StringBuilder currentValue ;
	private boolean currentElement;
	private Vector<SyncLogDO> vecSyncLogDOs;
	private SyncLogDO objSyncLogDO;
	
	public GetAllDeletedCustomers(Context context) {
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		currentElement = true;
		currentValue=new StringBuilder();  
		
		if(localName.equalsIgnoreCase("SyncLogs"))
		{
			vecSyncLogDOs = new Vector<SyncLogDO>();
		}
		else if(localName.equalsIgnoreCase("SyncLogDco"))
		{
			objSyncLogDO = new SyncLogDO();
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentElement = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			preference.saveStringInPreference(ServiceURLs.GET_HH_DELETED_CUSTOMERS+Preference.LAST_SYNC_TIME, currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SyncLogId"))
		{
			objSyncLogDO.syncLogId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Module"))
		{
			objSyncLogDO.module = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EntityId"))
		{
			objSyncLogDO.entityId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SyncLogDco"))
		{
			vecSyncLogDOs.add(objSyncLogDO);
		}
		else if(localName.equalsIgnoreCase("SyncLogs") && vecSyncLogDOs != null)
		{
			if(vecSyncLogDOs!=null && vecSyncLogDOs.size()>0)
			{
				if(new CustomerDetailsDA().deleteCustomers(vecSyncLogDOs))
					preference.commitPreference();
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
