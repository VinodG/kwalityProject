package com.winit.alseer.parsers;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.ReturnOrderDA;
import com.winit.alseer.salesman.dataobject.OrderDetailsDO;
import com.winit.alseer.salesman.dataobject.OrderPrintImageDO;
import com.winit.alseer.salesman.dataobject.ReturnOrderStatusDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Vector;

public class GetInsertInvoicePrintImageParser extends BaseHandler
{
//	private Vector<OrderPrintImageDO> vecOrderPrintImageDO = null;

	private OrderPrintImageDO orderPrintImageDO ;
	String status="";

	private TrxDetailsDO trxDetailsDO;

	public GetInsertInvoicePrintImageParser(Context context, OrderPrintImageDO  orderPrintImageDO  )
	{
		super(context);
		this.orderPrintImageDO = orderPrintImageDO;
	}
//	public  Vector<OrderPrintImageDO>  getdata()
//	{
//		return vecOrderPrintImageDO;
//	}
	
	
	 @Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		
//	 if(localName.equalsIgnoreCase("InsertInvoicePrintImageResult"))
//		{
//			vecOrderPrintImageDO = new Vector<OrderPrintImageDO>();
//		}
//		else if(localName.equalsIgnoreCase("TrxHeaderPrintDco"))
//		{
//			orderPrintImageDO = new OrderPrintImageDO();
//		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		//OrderDetailsDO
		if( localName.equalsIgnoreCase("Status"))
		{
			  status = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Message"))
		{
//			orderPrintImageDO.TrxCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CurrentTime"))
		{
//			orderPrintImageDO.UserCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerTime"))
		{
//			orderPrintImageDO.ImagePath = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ServerDateTime"))
		{
//			orderPrintImageDO.ImageType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Count"))
		{
//			orderPrintImageDO.CaptureDate = currentValue.toString();
		}

		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
//			vecOrderPrintImageDO.add(orderPrintImageDO);
		}
		else if(localName.equalsIgnoreCase("InsertInvoicePrintImageResult"))
		{
			//add inserting
			if(status.equalsIgnoreCase("Success"))
			{
				orderPrintImageDO.status = OrderPrintImageDO.STATUS_UPLOADED_WITH_IMAGE;
				new ReturnOrderDA().updateOrderPrintImagesStatus(orderPrintImageDO,orderPrintImageDO.ImagePath);
			}


		}


	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
	public boolean getStatus ()
	{
		if(status.equalsIgnoreCase("Success"))
		{
			return  true;
		}
		else
		{
			return  false;
		}
	}
}
