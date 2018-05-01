package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.AgencyDO;

public class AgenciesParser extends BaseHandler
{
	private Vector<AgencyDO> vecAgencyDOs ;
	private AgencyDO objAgencyDO;
	
	public AgenciesParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Agencies"))
		{
			vecAgencyDOs = new Vector<AgencyDO>();
		}
		else if(localName.equalsIgnoreCase("AgencyDco"))
		{
			objAgencyDO = new AgencyDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("Code"))
		{
			objAgencyDO.AgencyId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Name"))
		{
			objAgencyDO.AgencyName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Priority"))
		{
			objAgencyDO.Priority = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("AgencyDco"))
		{
			vecAgencyDOs.add(objAgencyDO);
		}
		else if(localName.equalsIgnoreCase("Agencies"))
		{
			if(vecAgencyDOs != null && vecAgencyDOs.size() > 0)
				insertAgenciesData(vecAgencyDOs);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertAgenciesData(Vector<AgencyDO> vecAgencies) 
	{
		new CommonDA().insertAgenciesDetails(vecAgencies);
	}
}

