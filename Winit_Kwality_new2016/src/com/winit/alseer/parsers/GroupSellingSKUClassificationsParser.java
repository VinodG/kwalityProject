/**
 * 
 */
package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataaccesslayer.SellingSKUDA;
import com.winit.alseer.salesman.dataobject.GroupSellingSKUClassification;

/**
 * @author Aritra.Pal
 *
 */
public class GroupSellingSKUClassificationsParser extends BaseHandler 
{

	Vector<GroupSellingSKUClassification> vecGroupSellingSKUClassification;
	GroupSellingSKUClassification objGroupSellingSKUClassification;
	/**
	 * @param context
	 */
	public GroupSellingSKUClassificationsParser(Context context) 
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
		if(localName.equalsIgnoreCase("GroupSellingSKUClassifications"))
		{
			vecGroupSellingSKUClassification = new Vector<GroupSellingSKUClassification>();
		}
		else if(localName.equalsIgnoreCase("GroupSellingSKUClassificationDco"))
		{
			objGroupSellingSKUClassification = new GroupSellingSKUClassification();
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
			objGroupSellingSKUClassification.SellingSKUId = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("SellingSKUClassificationId"))
		{
			objGroupSellingSKUClassification.SellingSKUClassificationId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Priority"))
		{
			objGroupSellingSKUClassification.Priority = Integer.parseInt(currentValue.toString());
		}
		else if(localName.equalsIgnoreCase("IsDeleted"))
		{
			if(currentValue.toString().equalsIgnoreCase("true"))
				objGroupSellingSKUClassification.IsDeleted = true;
		}
		else if(localName.equalsIgnoreCase("GroupSellingSKUClassificationDco"))
		{
			vecGroupSellingSKUClassification.add(objGroupSellingSKUClassification);
		}
		else if(localName.equalsIgnoreCase("GroupSellingSKUClassifications"))
		{
			new SellingSKUDA().insertGroupSellingSKUClassification(vecGroupSellingSKUClassification);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if(currentElement)
			currentValue.append(new String(ch, start, length));
	}
}
