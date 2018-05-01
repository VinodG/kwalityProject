package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.SettingsDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class GetSettingsParser extends BaseHandler
{
	private Vector<SettingsDO> vecSettings ;
	private SettingsDO objSettingsDO;
	
	public GetSettingsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Settings"))
		{
			vecSettings = new Vector<SettingsDO>();
		}
		else if(localName.equalsIgnoreCase("SettingsDco"))
		{
			objSettingsDO = new SettingsDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("SettingId"))
		{
			objSettingsDO.SettingId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SettingName"))
		{
			objSettingsDO.SettingName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SettingValue"))
		{
			objSettingsDO.SettingValue = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CountryId"))
		{
			objSettingsDO.CountryId = StringUtils.getInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SettingsDco"))
		{
			vecSettings.add(objSettingsDO);
		}
		else if(localName.equalsIgnoreCase("Settings"))
		{
			if(vecSettings != null && vecSettings.size() > 0)
				insertSettings(vecSettings);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertSettings(Vector<SettingsDO> vecSettings) 
	{
		new CommonDA().insertSettings(vecSettings);
	}
}
