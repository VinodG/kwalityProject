package com.winit.alseer.salesman.dataobject;

public class CashDenomDO extends BaseObject
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String UserCode			= "";
	public String CollectionDate 	= "";
	public String HelperUserCode 	= "";
	public String RouteNo 			= "";
	public String Division 			= "";
	
	public Denominations objIceCreamDenom = new Denominations();
	public Denominations objFoodDenom = new Denominations();
	
}
