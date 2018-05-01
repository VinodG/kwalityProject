package com.winit.alseer.salesman.dataobject;


@SuppressWarnings("serial")
public class PromotionAssignmentsTypeDO extends BaseComparableDO
{
	public String AssignmentType	= "";
	public String Description		= "";
	public String CreatedOn			= "";
	public String ModifiedOn		= "";
	
	public static final int COUNTRY=0;
	public static final int REGION=1;
	public static final int CITY=2;
	public static final int SALESTEAM=3;
	public static final int SALESMAN=4;
	public static final int CHANNEL=5;
	public static final int SUBCHANNEL=6;
	public static final int CUSTOMERGROUP=7;
	public static final int CUSTOMER=8;
}
