package com.winit.alseer.salesman.dataobject;

import java.util.Vector;



@SuppressWarnings("serial")
public class ProductCatalogDO extends BaseComparableDO
{
	public String CatagoryId   = ""; 
	public String CatagoryName  = ""; 
	public Vector<ProductCatalogItemDO> CatagoryList  = new Vector<ProductCatalogItemDO>();
}
