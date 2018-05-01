package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataobject.PaymentResponseDo;
import com.winit.alseer.salesman.utilities.StringUtils;

public class InsertPaymentParser extends BaseHandler
{
	private StringBuilder currentValue ;
	private boolean currentElement = false;
	private PaymentResponseDo objPayments;
	private Vector<PaymentResponseDo> vecPaymentsNumbers;
	private String strStatus = "";
	private final int ENABLE = 100, DISABLE = 200;
	private int ENABLE_PARSING = DISABLE;
	
	public InsertPaymentParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("PaymentStatus"))
		{
			ENABLE_PARSING = ENABLE;
			vecPaymentsNumbers = new Vector<PaymentResponseDo>();
		}
		else if(localName.equalsIgnoreCase("PaymentStatusDco"))
		{
			objPayments = new PaymentResponseDo();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		switch (ENABLE_PARSING) 
		{
			case ENABLE:
				if(localName.equalsIgnoreCase("Receipt_Number"))
				{
					objPayments.receiptNumber = currentValue.toString();
				}
				else if(localName.equalsIgnoreCase("Status"))
				{
					objPayments.Status = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("Division"))
				{
					objPayments.Division = StringUtils.getInt(currentValue.toString());
				}
				else if(localName.equalsIgnoreCase("Message"))
				{
					objPayments.message = currentValue.toString();
					vecPaymentsNumbers.add(objPayments);
				}
				if(localName.equalsIgnoreCase("PaymentStatus"))
				{
					updatePayments( vecPaymentsNumbers);
				}
				break;
			case DISABLE:
				if(localName.equalsIgnoreCase("Status"))
					strStatus = currentValue.toString();
				break;
		}
		
	}
	
	
	public boolean getStatus()
	{
		if(strStatus.equalsIgnoreCase("Success"))
			return true;
		
		return false; 
	}
	public boolean updatePayments(Vector<PaymentResponseDo> vecPaymentsNumbers)
	{
		boolean result = false;
		result = new CommonDA().updatePayments(vecPaymentsNumbers, preference.getStringFromPreference(Preference.SALESMANCODE, ""));
		return result;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
