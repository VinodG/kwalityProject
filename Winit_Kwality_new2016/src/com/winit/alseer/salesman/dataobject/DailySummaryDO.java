package com.winit.alseer.salesman.dataobject;

public class DailySummaryDO extends BaseObject
{
	public String siteID 		= "";
	public String siteName 		= ""; 
	public String amount		= "";
	public String type			= "";
	public String DocNo			= "";
	public String Priority		= "";
	public String Quantity		= "";
	public String Division       = "";
	public String TotalAmnt       = "";
	public String TotalVatAmnt       = "";

	private static int DAILY_SUMMARY_ICECREAM = 0;
	private static int DAILY_SUMMARY_FOOD = 1;
	private static int DAILY_THIRD_PARTY_BRAND = 2;

	public static int get_DAILY_SUMMARY_ICECREAM(){
		return DAILY_SUMMARY_ICECREAM;
	}
	public static int get_DAILY_SUMMARY_FOOD(){
		return DAILY_SUMMARY_FOOD;
	}
	public static int get_DAILY_THIRD_PARTY_BRAND(){
		return DAILY_THIRD_PARTY_BRAND;
	}
}
