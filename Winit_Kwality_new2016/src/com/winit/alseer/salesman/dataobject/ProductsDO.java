package com.winit.alseer.salesman.dataobject;

import java.util.Vector;

public class ProductsDO {

	public String ProductId 	= "";
	public String SKU 			= "";
	public String CategoryId	= "";
	public String Description	= "";
	public int UnitsPerCases;
	public String BatchCode		= "";
	public String UOM			= "";
	public String CaseBarCode	= "";
	public String UnitBarCode	= "";
	public String ItemType		= "";
	public String PricingKey	= "";
	public int ItemId;
	public String Brand			= "";
	public String Category		= "";
	public String CompanyId		= "";
	public String GroupId		= "";
	public String ItemDesc		= "";
	public String ItemCode		= "";
	public String secondryUOM	= "";
	public String TaxPercentage	= "";
	public String ItemPrice     = "";
	public String TaxGroupCode	= "";
	public String ItemPriceEA     = "";
	public String ItemPriceCS	= "";
	public String EAconversion	= "";
	public String Pricecases	= "";
	public int Division 		= 0;
	public Double VATPer 		=0.0;

	public Vector<NameIDDo> vecProductImages;
	public String GroupCodeLevel1 = "";
	public String CategoryLevel0 = "";
	public String CustomerGroupCodes = "";
	public String IsActive = "";
}
