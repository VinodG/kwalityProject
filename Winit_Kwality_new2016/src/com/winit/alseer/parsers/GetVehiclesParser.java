package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataaccesslayer.VehicleDA;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.dataobject.VehicleDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetVehiclesParser extends BaseHandler
{
	private VehicleDO vehicleDO;
	private Vector<VehicleDO> vecVehicleDO;
	SynLogDO synLogDO = new SynLogDO();
	public GetVehiclesParser(Context context)
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Vehicles"))
		{
			vecVehicleDO = new Vector<VehicleDO>();
		}
		else if(localName.equalsIgnoreCase("VehicleDco"))
		{
			vehicleDO = new VehicleDO();
		}
//		DAPassCodeDco
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp =  currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Status"))
		{
			synLogDO.action = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			synLogDO.UPMJ = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			synLogDO.UPMT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VEHICLE_NO"))
		{
			vehicleDO.VEHICLE_NO = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VEHICLE_MODEL"))
		{
			vehicleDO.VEHICLE_MODEL = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VEHICLE_TYPE"))
		{
			vehicleDO.VEHICLE_TYPE = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("DEPT"))
		{
			vehicleDO.DEPT = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("EMPNO"))
		{
			vehicleDO.EMPNO = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("AGENT_NAME"))
		{
			vehicleDO.AGENT_NAME = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LOCATION"))
		{
			vehicleDO.LOCATION = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ROUTE"))
		{
			vehicleDO.ROUTE = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("VehicleDco"))
		{
			vecVehicleDO.add(vehicleDO);
		}
		else if(localName.equalsIgnoreCase("Vehicles"))
		{
			if( vecVehicleDO != null && vecVehicleDO.size() > 0)
			{
				if(saveIntoUserTable(vecVehicleDO))
				{
					preference.commitPreference();
					synLogDO.entity		= ServiceURLs.GET_ALL_VEHICLES;
					new SynLogDA().insertSynchLog(synLogDO);
				}
			}
		}
	}
//BlaseUserDco
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	
	private boolean saveIntoUserTable(Vector<VehicleDO> vector)
	{
		return new VehicleDA().insertVehicles(vector);
	}
}
