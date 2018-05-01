package com.winit.alseer.salesman.dataobject;

import java.util.Vector;

@SuppressWarnings("serial")
public class InventoryDO extends BaseComparableDO
{
	public String inventoryId = "";
	public String site = "";
	public String date = "";
	public int uplaodStatus = 0;
	public String createdBy="";
	public String BrandCode = "";
	public String Status = "0";
	public String VisitCode = "";
	public String JourneyCode = "";
	
	public int TotalCount = 0;
	public int NotApplicableItemCount = 0;
	public String SalesmanName = "";
	public String ClientName = "";
	public String ChannelCode = "";
	public String Role = "";
	public String Region = "";
	public String RegionCode = "";
	public String starTime = "";
	public String endTime = "";

	public Vector<InventoryDetailDO> vecInventoryDOs = new Vector<InventoryDetailDO>();
	public Vector<InventoryGroupDO> vecGroupDOs = new Vector<InventoryGroupDO>();
}
