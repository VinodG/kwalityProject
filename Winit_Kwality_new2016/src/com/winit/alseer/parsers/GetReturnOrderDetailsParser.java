package com.winit.alseer.parsers;

import java.util.ArrayList;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataobject.ReturnOrderStatusDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.StringUtils;

public class GetReturnOrderDetailsParser extends BaseHandler
{
	private ReturnOrderStatusDetailsDO returnOrderStatusDetailsDO;
	
	private TrxHeaderDO trxHeaderDO ;
	private Vector<TrxHeaderDO> vecTrxHeaderDOs;
	
	private TrxDetailsDO trxDetailsDO;
	
	public GetReturnOrderDetailsParser(Context context) 
	{
		super(context);
	}
	public ReturnOrderStatusDetailsDO getdata()
	{
		return returnOrderStatusDetailsDO;
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
		if(localName.equalsIgnoreCase("GetReturnOrderStatusDetailsResult"))
		{
			returnOrderStatusDetailsDO = new ReturnOrderStatusDetailsDO();
		}
		else if(localName.equalsIgnoreCase("TrxHeaders"))
		{
			vecTrxHeaderDOs = new Vector<TrxHeaderDO>();
		}
		else if(localName.equalsIgnoreCase("TrxHeaderDco"))
		{
			trxHeaderDO = new TrxHeaderDO();
		}
		else if(localName.equalsIgnoreCase("objTrxDetails"))//objTrxDetails
		{
			trxHeaderDO.arrTrxDetailsDOs = new ArrayList<TrxDetailsDO>();
		}
		else if(localName.equalsIgnoreCase("TrxDetailDco"))//TrxDetailDco
		{
			trxDetailsDO = new TrxDetailsDO();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		//OrderDetailsDO
		if(trxHeaderDO == null && trxDetailsDO == null && localName.equalsIgnoreCase("Status"))
		{
			returnOrderStatusDetailsDO.Status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerDateTime"))
		{
			returnOrderStatusDetailsDO.ServerDateTime = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Count"))
		{
			returnOrderStatusDetailsDO.Count = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			returnOrderStatusDetailsDO.ModifiedDate = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			returnOrderStatusDetailsDO.ModifiedTime = currentValue.toString();
		}
		
		//TrxHeaders
		else if(trxDetailsDO == null && localName.equalsIgnoreCase("OrderNumber"))
		{
			trxHeaderDO.trxCode = currentValue.toString();
		}
		else if(trxDetailsDO == null && localName.equalsIgnoreCase("Status"))
		{
			trxHeaderDO.status = StringUtils.getInt(currentValue.toString());
		}
		
		//TrxDetails
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("OrderNumber"))
		{
			trxDetailsDO.trxCode = currentValue.toString();
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("ItemCode"))
		{
			trxDetailsDO.itemCode = currentValue.toString();
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("ApprovedQuantity"))
		{
			trxDetailsDO.approvedBU = StringUtils.getInt(currentValue.toString());
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("CancelledQuantity"))
		{
			trxDetailsDO.cancelledQuantity = StringUtils.getInt(currentValue.toString());
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("ApprovedCases"))
		{
			//trxDetailsDO.ApprovedCases = currentValue.toString();
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("ApprovedUnits"))
		{
			trxDetailsDO.approvedBU = StringUtils.getInt(currentValue.toString());
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("CancelledCases"))
		{
//			trxDetailsDO.CancelledCases  = currentValue.toString();
		}
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("CancelledUnits"))
		{
			trxDetailsDO.cancelledQuantity = StringUtils.getInt(currentValue.toString());
		}
		
		else if(trxDetailsDO != null && localName.equalsIgnoreCase("TrxDetailDco"))
		{
			trxHeaderDO.arrTrxDetailsDOs.add(trxDetailsDO);
		}
		else if(localName.equalsIgnoreCase("TrxHeaderDco"))
		{
			vecTrxHeaderDOs.add(trxHeaderDO);
		}
		else if(localName.equalsIgnoreCase("TrxHeaders"))
		{
			returnOrderStatusDetailsDO.vecTrxHeadderDOs = (Vector<TrxHeaderDO>) vecTrxHeaderDOs.clone();
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
