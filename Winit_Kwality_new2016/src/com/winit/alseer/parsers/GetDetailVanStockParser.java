package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.winit.alseer.salesman.dataaccesslayer.InventoryDA;
import com.winit.alseer.salesman.dataaccesslayer.ProductsDA;
import com.winit.alseer.salesman.dataaccesslayer.RecommendedQuantityDA;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.alseer.salesman.dataobject.RecommendedQuantityDO;
import com.winit.alseer.salesman.dataobject.VanStockDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;

import android.content.Context;

public class GetDetailVanStockParser extends BaseHandler 
{
	private Vector<VanStockDO> vecVanStock=null;
	VanStockDO objVanstock;
	
	public GetDetailVanStockParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("DetailedStocksDco"))
		{
			vecVanStock= new Vector<VanStockDO>();
			
		}
		else if(localName.equalsIgnoreCase("DetailedStockDco"))
		{
			objVanstock= new VanStockDO();
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
			objVanstock.ItemCode = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("OpeningQty"))
		{
			objVanstock.OpeningQTY = StringUtils.getInt(currentValue.toString());
		}		
		else if(localName.equalsIgnoreCase("LoadedQty"))
		{
			objVanstock.LoadedQty = StringUtils.getInt(currentValue.toString());
		}		
		else if(localName.equalsIgnoreCase("DetailedStockDco"))
		{
			vecVanStock.add(objVanstock);
		}
		else if(localName.equalsIgnoreCase("DetailedStocksDco"))
		{
			new InventoryDA().insertUpdatetblDetailStock(vecVanStock);			
		}
	}

}
