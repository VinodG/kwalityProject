package com.winit.alseer.salesman.dataobject;

import java.util.Vector;

public class StoreCheckBrandDO 
{
	public String categoryId   = "";
	public String categoryName = "";
	public String brandId   = "";
	public String brandName = "";
	public String image = "";
	public boolean isDone;
	
	public Vector<ProductDO> vecBrandProduct = new Vector<ProductDO>();
}
