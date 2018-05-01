package com.winit.alseer.salesman.dataobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DailySumDO extends BaseObject
{
	public String vehicleNo 			= "";
	public String helperName 			= "";
	public String dayOfWeek 			= "";
	public int totalVisit				= 0;
	
	public String startTime 			= "";
	public String endTime 				= "";
	public String startKM	 			= "";
	public String endKM		 			= "";
	public String totalScheduledCall	= "";
	public String totalActualCall		= "";
	
	public ArrayList<DailySummaryDO> arrDailySummary = new ArrayList<DailySummaryDO>();
	public LinkedHashMap<String, ArrayList<DailySummaryDO>> hashDailySummary = new LinkedHashMap<String, ArrayList<DailySummaryDO>>();
	public CashDenomDO objCashDenom = new CashDenomDO();
	public double[] total = {0,0,0,0,0,0,0,0,0,0,0};
//	public double[] total = {0,0,0,0,0,0,0,0,0,0};
//	public double[] grstotal = {0,0,0,0,0,0,0,0,0,0};
	public double[] grstotal = {0,0,0,0,0,0,0,0,0,0,0};
	public float [] targetAcheivement;
}
