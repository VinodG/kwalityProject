package com.winit.alseer.salesman.dataobject;

import java.util.Vector;


public class TrxLogHeaders extends BaseObject implements Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String TrxLogHeaderId = "";
	public String TrxDate = "";
	public int TotalScheduledCalls= 0;
	public int TotalActualCalls = 0;
	public int TotalProductiveCalls =0;
	public double TotalSales = 0.0;
	public double TotalCreditNotes = 0.0;
	public double TotalCollections = 0.0;
	public double CurrentMonthlySales = 0.0;
	public int TotalActualCallsPlanned = 0;
	public int TotalProductiveCallsPlanned = 0;
	public Vector<TrxLogDetailsDO> vecTrxLogDetailsDO=new Vector<TrxLogDetailsDO>();
	
	public static final String COL_TOTAL_SALES 		  = "TotalSales";
	public static final String COL_TOTAL_CREDIT_NOTES = "TotalCreditNotes";
	public static final String COL_TOTAL_COLLECTIONS  = "TotalCollections";
	public static final String COL_STORE_CHECK  		= "StoreCheck";
	public static final String COL_TOTAL_ACTUAL_CALLS  = "TotalActualCalls";
}
