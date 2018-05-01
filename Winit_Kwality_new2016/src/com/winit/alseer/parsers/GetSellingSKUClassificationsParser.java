/**
 * 
 */
package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SellingSKUClassificationsDA;
import com.winit.alseer.salesman.dataobject.SellingSKUClassification;

/**
 * @author Aritra.Pal
 *
 */
public class GetSellingSKUClassificationsParser extends BaseHandler 
{

	Vector<SellingSKUClassification> vecSellingSKU;
	SellingSKUClassification objSellingSKU;
	/**
	 * @param context
	 */
	public GetSellingSKUClassificationsParser(Context context) 
	{
		super(context);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("SellingSkusClassifications"))
		{
			vecSellingSKU = new Vector<SellingSKUClassification>();
		}
		else if(localName.equalsIgnoreCase("SellingSkusClassificationsDco"))
		{
			objSellingSKU = new SellingSKUClassification();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("SellingSkusClassificationId"))
		{
			objSellingSKU.SellingSkusClassificationId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("Code"))
		{
			objSellingSKU.Code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Description"))
		{
			objSellingSKU.Description = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("SalesOrgCode"))
		{
			objSellingSKU.SalesOrgCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Sequence"))
		{
			objSellingSKU.Sequence = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsActive"))
		{
			if(currentValue.toString().equalsIgnoreCase("true"))
				objSellingSKU.IsActive = true;
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objSellingSKU.ModifiedDate = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objSellingSKU.ModifiedTime = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SellingSkusClassificationsDco"))
		{
			vecSellingSKU.add(objSellingSKU);
		}
		else if(localName.equalsIgnoreCase("SellingSkusClassifications"))
		{
			new SellingSKUClassificationsDA().insertSellingSKU(vecSellingSKU);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
