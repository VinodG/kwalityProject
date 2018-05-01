package com.winit.alseer.parsers;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;

import com.winit.alseer.salesman.dataobject.BrandDO;

public class BrandsParser extends BaseHandler
{
	private Vector<BrandDO> vecBrands ;
	private BrandDO objBrand;
	
	public BrandsParser(Context context) 
	{
		super(context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
	{
		currentElement  = true;
		currentValue = new StringBuilder();
		if(localName.equalsIgnoreCase("Brands"))
		{
			vecBrands = new Vector<BrandDO>();
		}
		else if(localName.equalsIgnoreCase("BrandDco"))
		{
			objBrand = new BrandDO();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		currentElement  = false;
		
		if(localName.equalsIgnoreCase("BrandId"))
		{
			objBrand.brandId = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("CODE"))
		{
			objBrand.code = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("Description"))
		{
			objBrand.brandName = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("BrandImage"))
		{
			objBrand.image = currentValue.toString();
		}
		else if(localName.equalsIgnoreCase("ParentCode"))
		{
			objBrand.parentCode = currentValue.toString();
		}
		
		else if(localName.equalsIgnoreCase("BrandDco"))
		{
			vecBrands.add(objBrand);
		}
		else if(localName.equalsIgnoreCase("Brands"))
		{
			if(vecBrands != null && vecBrands.size() > 0)
				insertBrandsData(vecBrands);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)throws SAXException 
	{
		if (currentElement) 
			currentValue.append(new String(ch, start, length));
    }
	private void insertBrandsData(Vector<BrandDO> vecBrands) 
	{
//		new CommonDA().insertBrandDetails(vecBrands);
	}
}

