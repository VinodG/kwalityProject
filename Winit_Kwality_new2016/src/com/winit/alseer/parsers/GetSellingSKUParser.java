/**
 * 
 */
package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SellingSKUDA;
import com.winit.alseer.salesman.dataobject.SellingSKU;

/**
 * @author Aritra.Pal
 *
 */
public class GetSellingSKUParser extends BaseHandler 
{

	Vector<SellingSKU> vecSellingSKU;
	SellingSKU objSellingSKU;
	/**
	 * @param context
	 */
	public GetSellingSKUParser(Context context) 
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
		if(localName.equalsIgnoreCase("SellingSKUs"))
		{
			vecSellingSKU = new Vector<SellingSKU>();
		}
		else if(localName.equalsIgnoreCase("SellingSKUDco"))
		{
			objSellingSKU = new SellingSKU();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("SellingSKUId"))
		{
			objSellingSKU.SellingSKUId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("GroupType"))
		{
			objSellingSKU.GroupType = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCode"))
		{
			objSellingSKU.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CreatedBy"))
		{
			objSellingSKU.CreatedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("GroupCode"))
		{
			objSellingSKU.GroupCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("IsCoreSKU"))
		{
			if(currentValue.toString().equalsIgnoreCase("true"))
				objSellingSKU.IsCoreSKU = true;
		}
		else if(localName.equalsIgnoreCase("Priority"))
		{
			objSellingSKU.Priority = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SellingSKUClassificationId"))
		{
			objSellingSKU.SellingSKUClassificationId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CreatedBy"))
		{
			objSellingSKU.CreatedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedBy"))
		{
			objSellingSKU.ModifiedBy = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ModifiedDate"))
		{
			objSellingSKU.ModifiedDate = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("ModifiedTime"))
		{
			objSellingSKU.ModifiedTime = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CreatedDate"))
		{
			objSellingSKU.CreatedDate = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("CreatedTime"))
		{
			objSellingSKU.CreatedTime = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsCoreSkuPriority"))
		{
			objSellingSKU.IsCoreSKUPriority = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SellingSKUDco"))
		{
			vecSellingSKU.add(objSellingSKU);
		}
		else if(localName.equalsIgnoreCase("SellingSKUs"))
		{
			new SellingSKUDA().insertSellingSKU(vecSellingSKU);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
