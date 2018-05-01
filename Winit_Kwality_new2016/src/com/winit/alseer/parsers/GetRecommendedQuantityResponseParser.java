package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.RecommendedQuantityDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.alseer.salesman.dataobject.RecommendedQuantityDO;
import com.winit.alseer.salesman.utilities.LogUtils;

import android.content.Context;

public class GetRecommendedQuantityResponseParser extends BaseHandler 
{
	private Vector<RecommendedQuantityDO> vecRecQuntDOs = null;
	private RecommendedQuantityDO objRecommendedQuantityDO = null;
	private RecommendedQuantityDA recommendedQtyDA = null;
	
	public GetRecommendedQuantityResponseParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("RecommendedQuantityList"))
		{
			vecRecQuntDOs = new Vector<RecommendedQuantityDO>();
			recommendedQtyDA = new RecommendedQuantityDA();
		}
		else if(localName.equalsIgnoreCase("RecommendedQuantityDco"))
		{
			objRecommendedQuantityDO = new RecommendedQuantityDO();
		}
		LogUtils.errorLog("localName_parser", "GetRecommendedQuantityResponseParser:"+localName);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		LogUtils.errorLog("localName_parser", "GetRecommendedQuantityResponseParser:"+localName);
		currentElement  = false;
		if(localName.equalsIgnoreCase("ItemCode"))
		{
			objRecommendedQuantityDO.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("UserCode"))
		{
			objRecommendedQuantityDO.UserCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("LoadQuantity"))
		{
			objRecommendedQuantityDO.LoadQuantity = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ItemCount"))
		{
			objRecommendedQuantityDO.ItemCount = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("RecommendedQuantityValue"))
		{
			objRecommendedQuantityDO.RecommendedQuantityValue = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("RecommendedQuantityDco"))
		{
			vecRecQuntDOs.add(objRecommendedQuantityDO);
		}
		else if(localName.equalsIgnoreCase("RecommendedQuantityList"))
		{
			recommendedQtyDA.insertUpdate(vecRecQuntDOs);
		}
	}

}
