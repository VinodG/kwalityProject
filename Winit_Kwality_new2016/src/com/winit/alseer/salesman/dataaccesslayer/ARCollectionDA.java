package com.winit.alseer.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.PendingInvicesDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class ARCollectionDA 
{
	private ArrayList<PendingInvicesDO> vecPendingInvoices = new ArrayList<PendingInvicesDO>();
	public  ArrayList<PendingInvicesDO> getPendingInvoices(String customerSiteId, int division)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor=null;
			if(vecPendingInvoices!=null && vecPendingInvoices.size()>0)
				vecPendingInvoices.clear();
			
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "SELECT TPI.InvoiceNumber, TPI.BalanceAmount, TPI.InvoiceDate ,TPI.OrderId,TPI.IsOutStanding, " +
						"TOH.TrxType, TPI.TotalAmount,TPI.DueDate " +
						"FROM tblPendingInvoices TPI " +
						"LEFT JOIN tblTrxHeader TOH " +
						"ON TPI.InvoiceNumber= TOH.TrxCode " +
						"WHERE TPI.CustomerSiteId ='"+ customerSiteId+"' AND TPI.BalanceAmount !=0 AND TPI.Status  ='N' AND TPI.Division = '"+division+"' " +
						"ORDER BY TPI.InvoiceDate  ASC";
				cursor = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= cursor.getString(0);
						pendingInvicesDO.balance 		= String.valueOf(cursor.getFloat(1));
						pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
						pendingInvicesDO.invoiceDate 	= cursor.getString(2);
						if(cursor.getString(3)!=null)
							pendingInvicesDO.orderId 		= cursor.getString(3);
						if(cursor.getString(4)!=null)
							pendingInvicesDO.IsOutStanding	= ""+cursor.getString(4);
						pendingInvicesDO.isNewleyAdded	=	false;
						pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
						
						if(cursor.getString(5)!=null)
							pendingInvicesDO.TRX_TYPE = ""+cursor.getString(5);
						else if(StringUtils.getFloat(pendingInvicesDO.balance)<0)
							pendingInvicesDO.TRX_TYPE = String.valueOf(TrxHeaderDO.get_TRXTYPE_RETURN_ORDER());
						else
							pendingInvicesDO.TRX_TYPE = AppConstants.HHOrder;
						
						if(cursor.getString(6)!=null)
							pendingInvicesDO.totalAmount = String.valueOf(cursor.getFloat(6));
						pendingInvicesDO.invoiceDueDate = cursor.getString(7);
						vecPendingInvoices.add(pendingInvicesDO);
						
					}
				while(cursor.moveToNext());
				} 
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}
			return vecPendingInvoices;
		}
	}
	public  ArrayList<PendingInvicesDO> getListPendingInvoices(String customerSiteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor=null;
			if(vecPendingInvoices!=null && vecPendingInvoices.size()>0)
				vecPendingInvoices.clear();
//			DictionaryEntry [][] data = null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "Select InvoiceNumber, InvoiceDate, TotalAmount, BalanceAmount, IsOutStanding from tblPendingInvoices where CustomerSiteId ='"+customerSiteId+"' and BalanceAmount != 0 AND Status ='N'";
				cursor = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						DecimalFormat decimalFormat = new DecimalFormat("##.##");
						PendingInvicesDO pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= cursor.getString(0);
						pendingInvicesDO.invoiceDate	= cursor.getString(1);
						pendingInvicesDO.totalAmount 	= String.valueOf(decimalFormat.format(cursor.getFloat(2)));
						pendingInvicesDO.balance 		= String.valueOf(decimalFormat.format(cursor.getFloat(3)));
						float pedndingAmnt = StringUtils.getFloat(pendingInvicesDO.balance);
						if(cursor.getString(4)!=null)
							pendingInvicesDO.IsOutStanding 	= ""+cursor.getString(4);
						if(pedndingAmnt>0)
						vecPendingInvoices.add(pendingInvicesDO);
					}
					while (cursor.moveToNext());
				} 
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}
			return vecPendingInvoices;
		}
	}

	public int getPendingInvoicesCountBySite(String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor=null;
			int count = 0;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "Select count(*) from tblPendingInvoices where CustomerSiteId ='"+siteId+"' and BalanceAmount > 0 AND Status ='N'";
//				DictionaryEntry [][] data = DatabaseHelper.get(strPeningInvoices);
				cursor = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
					count = StringUtils.getInt(cursor.getString(0));
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}
			return count;
		}
	}

	public PendingInvicesDO getCashPendingInvoices(String customerSiteId,String invoiceNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			PendingInvicesDO pendingInvicesDO = null;
			Cursor cursor=null;
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "SELECT TPI.InvoiceNumber, TPI.BalanceAmount, TPI.InvoiceDate ,TPI.OrderId,TPI.IsOutStanding, " +
						"TOH.TrxType, TPI.TotalAmount,TPI.DueDate " +
						"FROM tblPendingInvoices TPI " +
						"LEFT JOIN tblTrxHeader TOH " +
						"ON TPI.InvoiceNumber= TOH.TrxCode " +
						"WHERE TPI.CustomerSiteId ='"+ customerSiteId+"' AND TPI.BalanceAmount !=0 AND TPI.Status  ='N' " +
						"AND InvoiceNumber='"+invoiceNo+"'" +
						"ORDER BY TPI.InvoiceDate  ASC";
				cursor = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						pendingInvicesDO = new PendingInvicesDO();
						pendingInvicesDO.invoiceNo 		= cursor.getString(0);
						pendingInvicesDO.balance 		= String.valueOf(cursor.getFloat(1));
						pendingInvicesDO.lastbalance	= pendingInvicesDO.balance;
						pendingInvicesDO.invoiceDate 	= cursor.getString(2);
						if(cursor.getString(3)!=null)
							pendingInvicesDO.orderId 		= cursor.getString(3);
						if(cursor.getString(4)!=null)
							pendingInvicesDO.IsOutStanding	= ""+cursor.getString(4);
						pendingInvicesDO.isNewleyAdded	=	false;
						pendingInvicesDO.invoiceDateToShow = CalendarUtils.getFormatedDatefromString(pendingInvicesDO.invoiceDate);
						
						if(cursor.getString(5)!=null)
							pendingInvicesDO.TRX_TYPE = ""+cursor.getString(5);
						else if(StringUtils.getFloat(pendingInvicesDO.balance)<0)
							pendingInvicesDO.TRX_TYPE = String.valueOf(TrxHeaderDO.get_TRXTYPE_RETURN_ORDER());
						else
							pendingInvicesDO.TRX_TYPE = AppConstants.HHOrder;
						
						if(cursor.getString(6)!=null)
							pendingInvicesDO.totalAmount = String.valueOf(cursor.getFloat(6));
						pendingInvicesDO.invoiceDueDate = cursor.getString(7);
					}
				while(cursor.moveToNext());
				} 
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}
			return pendingInvicesDO;
		}
	}
}
