package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.dataaccesslayer.CommonDA;
import com.winit.alseer.salesman.dataaccesslayer.SynLogDA;
import com.winit.alseer.salesman.dataobject.PricingDO;
import com.winit.alseer.salesman.dataobject.SynLogDO;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;

public class GetPricingParser extends BaseHandler
{
	private Vector<PricingDO> vecItemPricing;
	private PricingDO pricingDO;
	private SynLogDO synLogDO = new SynLogDO();
	private int completedCount;
	public GetPricingParser(Context context)
	{
		super(context);
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Prices")||localName.equalsIgnoreCase("Price"))
		{
			vecItemPricing = new Vector<PricingDO>();
		}
		else if(localName.equalsIgnoreCase("PriceDco"))
		{
			pricingDO = new PricingDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		currentElement = false;
		if(localName.equalsIgnoreCase("CurrentTime"))
		{
			synLogDO.TimeStamp = currentValue.toString();
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
		else if(localName.equalsIgnoreCase("ITEMCODE"))
			pricingDO.itemCode = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("CUSTOMERPRICINGKEY"))
			pricingDO.customerPricingClass = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("PRICECASES"))
			pricingDO.priceCases = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ENDDATE"))
			pricingDO.endDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("STARTDATE"))
			pricingDO.startDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("DISCOUNT"))
			pricingDO.dicount = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("IsExpired"))
		{
			if(currentValue.toString().equalsIgnoreCase("false"))
				pricingDO.IsExpired = "False";
			else
				pricingDO.IsExpired = "True";
		}
		
		else if(localName.equalsIgnoreCase("DepositPrice"))
			pricingDO.emptyCasePrice = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("TaxGroupCode"))
			pricingDO.TaxGroupCode = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("TaxPercentage"))
			pricingDO.TaxPercentage = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedDate"))
			pricingDO.ModifiedDate = currentValue.toString();
		
		else if(localName.equalsIgnoreCase("ModifiedTime"))
			pricingDO.ModifiedTime = currentValue.toString();
	
		else if(localName.equalsIgnoreCase("UOM"))
			pricingDO.UOM = currentValue.toString();


		else if(localName.equalsIgnoreCase("PriceDco") || localName.equalsIgnoreCase("SyncPriceDco")) {
			vecItemPricing.add(pricingDO);
			completedCount++;
			if (vecItemPricing.size() > AppConstants.SYNC_COUNT) {
				new CommonDA().insertItemPricing(vecItemPricing);
				vecItemPricing.clear();
				Log.e("completedCount", "" + completedCount);
			}
		}
		
//		else if(localName.equalsIgnoreCase("PriceDco"))
//			vecItemPricing.add(pricingDO);
		
		else if((localName.equalsIgnoreCase("Prices") ||localName.equalsIgnoreCase("Price"))&& vecItemPricing != null)
		{
			if(new CommonDA().insertItemPricing(vecItemPricing))
			{
				synLogDO.entity = ServiceURLs.GET_All_PRICE_WITH_SYNC;
				new SynLogDA().insertSynchLog(synLogDO);
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
}
