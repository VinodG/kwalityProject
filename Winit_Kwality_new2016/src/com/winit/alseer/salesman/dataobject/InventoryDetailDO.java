package com.winit.alseer.salesman.dataobject;

@SuppressWarnings("serial")
public class InventoryDetailDO extends BaseComparableDO
{
	public String inventoryDetailId = "";
	public String StoreCheckId = "";
	public String inventoryId = "";
	public String itemCode = "";
	
	public String ItemDescription = "";
	public String CategoryName = "";
	public String AgencyCode = "";
	public String AgencyName = "";
	
	public String CategoryCode = "";
	public String BrandCode = "";
	public String BrandName = "";
	public int status = 0;
	public float inventoryQty  = 0.0f ;
	public float recmQty = 0.0f;
	
	public String UOM = "";
	public int QTY = 0;
	
}
