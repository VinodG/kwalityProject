package com.winit.alseer.salesman.dataaccesslayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.HHInventryQTDO;
import com.winit.alseer.salesman.dataobject.InventoryObject;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.OrderDetailsDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class OrderDetailsDA 
{
	//format get up 2 decimal points
	public Vector<OrderDetailsDO> getOrderDetails(String OrdersId)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery = "";
			SQLiteDatabase sqLiteDatabase 			= 	null;
			Cursor cursor 	 						=	null;
			Vector<OrderDetailsDO> vectorOrderList 	= 	new Vector<OrderDetailsDO>();
			try
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				strQuery 			= 	"SELECT OD.OrderNo, OD.ItemCode, OD.Cases, OD.Units, OD.UOM, TP.Description,TP.UnitPerCase  FROM tblOrderDetail OD , tblProducts TP where OD.OrderNo='"+OrdersId+"' and OD.ItemCode = TP.ItemCode ORDER BY TP.DisplayOrder ASC";
				cursor 				= 	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						OrderDetailsDO orderDO 	= 	new OrderDetailsDO();
						orderDO.OrderId  		= 	cursor.getString(0);
						orderDO.SKU 			= 	cursor.getString(1);
						orderDO.Cases 			=	StringUtils.getFloat(cursor.getString(2));
						orderDO.Units 			= 	cursor.getInt(3);
						orderDO.UOM		 		= 	cursor.getString(4);
						orderDO.UnitsPerCase	= 	StringUtils.getInt(cursor.getString(6));
						orderDO.description		= 	cursor.getString(5);
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
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
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	}
	public String getPaymentDueDate(String invoiceNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strPamentDueDate = "";
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			String query ="Select NextPaymentDate from tblDeliveryOrderDetail where LineNumber ='"+invoiceNo+"'  and typeof(NextPaymentDate)!='null'  Limit 1";
			try
			{
				sqLiteDatabase 	=	DatabaseHelper.openDataBase();
				cursor			=	sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strPamentDueDate = cursor.getString(0);
					strPamentDueDate =CalendarUtils.getFormatedDatefromString(strPamentDueDate);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null )
					sqLiteDatabase.close();
			}
			return strPamentDueDate;
		}
	}
	
	
	public Vector<ProductDO> getOrderProductsDetails(String orderID, String strSKUs)
	{
		synchronized(MyApplication.MyLock) 
		{
			ProductDO objProducts;
			Vector<ProductDO> vecProductList = new Vector<ProductDO>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			
			try
			{
				_database = DatabaseHelper.openDataBase();
				cursor	=	_database.rawQuery("SELECT DISTINCT OD.SKU,OD.Cases,OD.Units, P.UnitPerCase, P.Description ,P.UOM, P.SECONDARY_UOM, P.PricingKey from tblInvoiceOrderDetails OD,tblProducts P where  P.SKU = OD.SKU and  OD.OrderId='"+orderID+"' and OD.SKU not in("+strSKUs+") and OD.Units > 0 ORDER BY P.DisplayOrder ASC", null);
				if(cursor != null)
				{
					cursor.moveToFirst();
					do
					{
						objProducts 			= new ProductDO();
						objProducts.SKU 		= cursor.getString(0);
						if(cursor.getString(2) != null && !cursor.getString(2).equalsIgnoreCase(""))
							objProducts.orderedUnits 	= StringUtils.getInt(cursor.getString(2));
						else
							objProducts.orderedUnits = 0;
						objProducts.UnitsPerCases 		= StringUtils.getInt(cursor.getString(3));
						
						if(objProducts.orderedUnits != 0 && objProducts.UnitsPerCases != 0)
							objProducts.orderedCases 	= (float)objProducts.orderedUnits/objProducts.UnitsPerCases;
						else
							objProducts.orderedCases = 0;
						
						objProducts.Description  = cursor.getString(4);
						objProducts.itemPrice  	 = 0;
						objProducts.UOM	 		 = cursor.getString(5);
						objProducts.secondaryUOM = cursor.getString(6);
						objProducts.PricingKey	 = cursor.getString(7);
						vecProductList.add(objProducts); 
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
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
				if(_database != null)
					_database.close();
			}
			return vecProductList;
		}
	}
	
	public Vector<ProductDO> getAllProductsDetails( String strSKUs)
	{
		synchronized(MyApplication.MyLock) 
		{
			ProductDO objProducts;
			Vector<ProductDO> vecProductList = new Vector<ProductDO>();
			SQLiteDatabase _database = null;
			Cursor cursor = null;
			
			try
			{
				_database = DatabaseHelper.openDataBase();
				cursor	=	_database.rawQuery("SELECT DISTINCT  P.SKU, P.UnitPerCase, P.Description ,P.UOM, P.SECONDARY_UOM, P.PricingKey from  tblProducts P where  P.SKU not in("+strSKUs+") ORDER BY P.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objProducts 				= 	new ProductDO();
						objProducts.SKU 			= 	cursor.getString(0);
						objProducts.orderedUnits 	= 	0;
						objProducts.UnitsPerCases 	= 	StringUtils.getInt(cursor.getString(1));
						objProducts.orderedCases 	= 	0;
						objProducts.Description  	= 	cursor.getString(2);
						objProducts.itemPrice 		=	0;
						objProducts.UOM	 		 	= 	cursor.getString(3);
						objProducts.secondaryUOM 	= 	cursor.getString(4);
						objProducts.PricingKey	 	= 	cursor.getString(5);
						vecProductList.add(objProducts); 
					}
					while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				 }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecProductList;
		}
	}
	
	public boolean updateDeliveryOrder(Vector<DeliveryAgentOrderDetailDco>  vecDeliveryAgentOrderDetail,String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 SQLiteStatement stmtUpdate = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 	
				 stmtUpdate = objSqliteDB.compileStatement("Update tblDeliveryOrderDetail set OrderStatus=?, DeliveredQty=? where ItemCode=? and OrderId = ?");
				 for(int i =0 ; vecDeliveryAgentOrderDetail != null && i < vecDeliveryAgentOrderDetail.size() ; i++)
				 {
					 stmtUpdate.bindString(1, status);
					 stmtUpdate.bindString(2, vecDeliveryAgentOrderDetail.get(i).etCases.getText().toString());
					 stmtUpdate.bindString(3, vecDeliveryAgentOrderDetail.get(i).itemCode);
					 stmtUpdate.bindString(4, vecDeliveryAgentOrderDetail.get(i).blaseOrderNumber);
					 stmtUpdate.execute();
				 }
				 
				 
				 return true;
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
				 return false;
			 }
			 finally
			 {
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
				 stmtUpdate.close();
				
			 }
		 }
	}
	
	public Vector<InventoryObject> getInventoryQty(String strDate)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				
				String query = "";
				
//				query = "SELECT DISTINCT VI.ItemCode, TP.Description, VI.TotalQuantity,  VI.SellableQuantity, " +
//						"CASE " +
//						"WHEN D.DeliveredQuantity>0 THEN D.DeliveredQuantity " +
////						"ELSE 0 END AS Delivered,TP.UOM,TP.UnitPerCase, UF.EAConversion, TS.OpeningQty  FROM tblVanStock VI "
//						"ELSE 0 END AS Delivered,TP.UOM,TP.UnitPerCase, UF.EAConversion, (Case When TS.OpeningQty Is Null then '0' ELSE TS.OpeningQty END) AS OpeningQTY FROM tblVanStock VI "
//						+ "LEFT JOIN tblDetailedStock TS ON TS.ItemCode=VI.Itemcode  " +
//						"LEFT JOIN " +
//							"(SELECT TD.ItemCode, SUM(TD.QuantityLevel1)*UF.EAConversion AS DeliveredQuantity " +
//							"FROM tblTrxHeader TH " +
//							"INNER JOIN tblTrxDetail TD ON TH.TrxCode=TD.TrxCode " +
//									"AND TH.TrxDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' " +
//									"AND TH.TrxType IN(1,6) " +
//									"AND TH.TrxStatus=200 " +
//							"INNER JOIN tblUOMFactor UF ON TD.ItemCode=UF.ITEMCODE AND UF.UOM='UNIT' " +
//							"GROUP BY TD.ItemCode) AS D ON D.ItemCode=VI.ItemCode " +
//						"LEFT JOIN  tblProducts TP ON VI.ItemCode = TP.ItemCode " +
//						"LEFT JOIN tblUOMFactor UF ON VI.ItemCode = UF.ItemCode " +
//						"WHERE UF.UOM='UNIT' AND TP.IsActive='True' " +
//						"ORDER BY TP.DisplayOrder ASC";
				
				
				
				query = "SELECT DISTINCT VI.ItemCode, TP.Description, VI.TotalQuantity,  VI.SellableQuantity,"+ 
						"CASE WHEN D.DeliveredQuantity>0 THEN D.DeliveredQuantity ELSE 0 END AS Delivered,TP.UOM,TP.UnitPerCase,"+ 
						"UF.EAConversion, (Case When TS.OpeningQty Is Null then '0' ELSE TS.OpeningQty END) AS OpeningQTY, "+
						"(Case When V.UnloadedQuantity Is Null then '0' ELSE V.UnloadedQuantity END) AS UnloadQuantity "+
						"FROM tblVanStock VI LEFT JOIN tblDetailedStock TS ON TS.ItemCode=VI.Itemcode "+ 
						"LEFT JOIN (SELECT TD.ItemCode, SUM(TD.QuantityLevel1)*UF.EAConversion AS DeliveredQuantity FROM tblTrxHeader TH "+ 
						"INNER JOIN tblTrxDetail TD ON TH.TrxCode=TD.TrxCode AND TH.TrxDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' AND TH.TrxType IN(1,6) AND TH.TrxStatus=200 "+ 
						"INNER JOIN tblUOMFactor UF ON TD.ItemCode=UF.ITEMCODE AND UF.UOM='UNIT' GROUP BY TD.ItemCode) AS D ON D.ItemCode=VI.ItemCode "+
						"LEFT JOIN (SELECT MD.ItemCode, SUM(MD.ShippedQuantityLevel1)*UF.EAConversion AS UnloadedQuantity FROM tblMovementHeader MH "+
						"INNER JOIN tblMovementDetail MD ON MH.MovementCode=MD.MovementCode AND MH.MovementDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' AND MH.MovementType IN("+AppConstants.UNLOAD_STOCK+") "+ 
						"AND MH.MovementStatus= "+LoadRequestDO.MOVEMENT_STATUS_APPROVED_VERIFY+
						" INNER JOIN tblUOMFactor UF ON MD.ItemCode=UF.ITEMCODE AND UF.UOM='UNIT' GROUP BY MD.ItemCode) AS V ON V.ItemCode=VI.ItemCode "+ 
						"LEFT JOIN  tblProducts TP ON VI.ItemCode = TP.ItemCode "+
						"LEFT JOIN tblUOMFactor UF ON VI.ItemCode = UF.ItemCode WHERE UF.UOM='UNIT' AND TP.IsActive='True' "+ 
						"ORDER BY TP.DisplayOrder ASC";
				
				cursor		=	_database.rawQuery(query,null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.availCases		=	cursor.getFloat(2);
						objInventoryObject.availQty			=	cursor.getFloat(3);
						objInventoryObject.deliveredCases	=	cursor.getFloat(4);
						objInventoryObject.UOM				=	cursor.getString(5);
						objInventoryObject.unitPerCases		=	(int) cursor.getFloat(6);
						objInventoryObject.uomFactor		=	(int) cursor.getInt(7);
						objInventoryObject.openingQTY		=	cursor.getString(8);
						objInventoryObject.UnloadedQty		=	cursor.getFloat(9);
						
						int orderQty = (int)(getUnitfromUOM(objInventoryObject.deliveredCases,objInventoryObject.uomFactor) >= 0 ? getUnitfromUOM(objInventoryObject.deliveredCases,objInventoryObject.uomFactor) : 0);
						int availQty = (int)(getUnitfromUOM(objInventoryObject.availQty,objInventoryObject.uomFactor) >= 0 ? getUnitfromUOM(objInventoryObject.availQty,objInventoryObject.uomFactor) : 0);
						int vanQty = orderQty + availQty;
						if(vanQty > 0)
							vecInventoryItems.add(objInventoryObject); 
					}
					while(cursor.moveToNext());
				 }
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	private int getUnitfromUOM(float availCases,int uomFactor)
	{
		int units = 0;
		if((int)uomFactor > 0)
			units = (int)availCases/(int)uomFactor;
		return units;
	}
	
	public Object[] getInventorySumUp() {
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			Object obj[] = new Object[3];
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();

//				String query = "SELECT DISTINCT VI.ItemCode, VI.TotalQuantity,  VI.SellableQuantity ,CASE WHEN " +
//						"(VI.TotalQuantity- VI.SellableQuantity) > 0 THEN (VI.TotalQuantity- VI.SellableQuantity) " +
//						"ELSE 0 END AS Delivered from tblVanStock VI LEFT JOIN  tblProducts TP ON VI.ItemCode = TP.ItemCode ORDER BY TP.DisplayOrder ASC";
				
				String query = "SELECT DISTINCT VI.ItemCode, CAST(VI.TotalQuantity/UF.EAConversion as INT),  " +
						"CAST(VI.SellableQuantity/UF.EAConversion  as INT), " +
						"CASE WHEN CAST((VI.TotalQuantity- VI.SellableQuantity)/UF.EAConversion as INT) > 0 " +
						"THEN CAST((VI.TotalQuantity- VI.SellableQuantity)/UF.EAConversion  as INT) " +
						"ELSE 0 END AS Delivered,UF.EAConversion FROM tblVanStock VI " +
						"LEFT JOIN  tblProducts TP ON VI.ItemCode = TP.ItemCode " +
						"LEFT JOIN tblUOMFactor UF ON VI.ItemCode = UF.ItemCode " +
						"WHERE UF.UOM='UNIT' " +
						"ORDER BY TP.DisplayOrder ASC";
				
				cursor = sqLiteDatabase.rawQuery(query,null);

				if (cursor.moveToFirst()) 
				{
					long totalqty = 0l, available = 0l, delivered = 0l;
					do {
						Log.d("totalqty", ""+cursor.getLong(1));
						totalqty = totalqty + (cursor.getLong(1)>0?cursor.getLong(1):0l);
						available = available + (cursor.getLong(2)>0?cursor.getLong(2):0l);
						delivered = delivered + (cursor.getLong(3)>0?cursor.getLong(3):0l);
					} while (cursor.moveToNext());
					obj[0] = totalqty;
					obj[1] = available;
					obj[2] = delivered;
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
				if (sqLiteDatabase != null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return obj;
		}
	}
	public Vector<InventoryObject> getInventoryQty_New(String strDate, String orderType)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT VI.ItemCode, TP.Description, VI.PrimaryQuantity, (SELECT SUM(QuantityBU) FROM tblOrderDetail" +
													" WHERE OrderNo IN (SELECT OrderNo FROM tblOrderHeader WHERE ((DeliveryDate like '"+strDate+"%' AND TRXStatus ='D')" +
													" OR " +
													"OrderDate LIKE '"+strDate+"%') AND OrderType='"+orderType+"' ) AND VI.ItemCode = ItemCode) AS Delivered" +
													",TP.UOM,TP.UnitPerCase,VI.availQty from tblVMSalesmanInventory VI LEFT JOIN  tblProducts TP "+
													"ON VI.ItemCode = TP.SKU WHERE VI.Date like '"+strDate+"%' ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	cursor.getFloat(2);
						objInventoryObject.deliveredCases	=	cursor.getFloat(3) +(objInventoryObject.PrimaryQuantity - cursor.getFloat(6));
						objInventoryObject.UOM				=	cursor.getString(4);
						objInventoryObject.unitPerCases		=	StringUtils.getInt(cursor.getString(5));
						objInventoryObject.availCases		=	objInventoryObject.PrimaryQuantity - objInventoryObject.deliveredCases;
						
						if(objInventoryObject.availCases < 0)
							objInventoryObject.availCases = 0;
						
						vecInventoryItems.add(objInventoryObject); 
					}
					while(cursor.moveToNext());
				 }
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public ArrayList<InventoryObject> getReturnInventoryQtyNew(String strDate, String orderType)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			ArrayList<InventoryObject> vecInventoryItems = new ArrayList<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				String where = "";
				if(orderType != null && orderType.equalsIgnoreCase(AppConstants.REPLACEMETORDER))
					where = "OrderType = '"+AppConstants.REPLACEMETORDER+"' AND CAST(TotalAmount AS REAL) <= 0";
				else
					where = "OrderType = '"+AppConstants.RETURNORDER+"' AND CAST(TotalAmount AS REAL) > 0";
				
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT OD.ItemCode, TP.Description,SUM(OD.Units),TP.UOM, TP.UnitPerCase FROM tblOrderDetail OD " +
												   "INNER JOIN tblProducts TP ON TP.ItemCode  = OD.ItemCode " +
												   "WHERE OrderNo IN (SELECT OrderId FROM tblOrderHeader WHERE OrderDate LIKE '"+strDate+"%' AND "+where+") " +
												   "GROUP BY OD.ItemCode, OD.UOM ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	StringUtils.getFloat(decimalFormat.format(cursor.getFloat(2)));
						objInventoryObject.UOM				=	cursor.getString(3);
						objInventoryObject.unitPerCases		=	cursor.getInt(4);
						
						vecInventoryItems.add(objInventoryObject); 
					}
					while(cursor.moveToNext());
				 }
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public Vector<InventoryObject> getReturnInventoryQty(String strDate, String orderType)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setMaximumFractionDigits(2);
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT OD.ItemCode, TP.Description,SUM(OD.QuantityBU),TP.UOM, TP.UnitPerCase FROM tblOrderDetail OD " +
												   "INNER JOIN tblProducts TP ON TP.SKU = OD.ItemCode " +
												   "WHERE OrderNo IN (SELECT OrderId FROM tblOrderHeader WHERE OrderDate LIKE '"+strDate+"%' AND OrderType = '"+orderType+"' ) " +
												   "GROUP BY OD.ItemCode, OD.UOM ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	StringUtils.getFloat(decimalFormat.format(cursor.getFloat(2)));
						objInventoryObject.UOM				=	cursor.getString(3);
						objInventoryObject.unitPerCases		=	cursor.getInt(4);
						
						vecInventoryItems.add(objInventoryObject); 
					}
					while(cursor.moveToNext());
				 }
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public Vector<InventoryObject> getAvailaInventoryQty(String strDate)
	{
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);
		
		synchronized(MyApplication.MyLock) 
		{
			InventoryObject objInventoryObject;
			Vector<InventoryObject> vecInventoryItems = new Vector<InventoryObject>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT VI.ItemCode, TP.Description, VI.PrimaryQuantity,  VI.availQty," +
													"CASE WHEN (VI.PrimaryQuantity - VI.availQty) > 0 " +
													"THEN (VI.PrimaryQuantity - VI.availQty) " +
													"ELSE 0 END AS Delivered,TP.UOM,TP.UnitPerCase " +
													"from tblVMSalesmanInventory VI LEFT JOIN  tblProducts TP " +
													"ON VI.ItemCode = TP.SKU WHERE VI.Date like '"+strDate+"%' and VI.availQty >0 ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						objInventoryObject 					= 	new InventoryObject();
						objInventoryObject.itemCode			=	cursor.getString(0);
						objInventoryObject.itemDescription	=	cursor.getString(1);
						objInventoryObject.PrimaryQuantity	=	cursor.getInt(2);
						objInventoryObject.availCases		=	cursor.getFloat(3);
						objInventoryObject.deliveredCases	=	cursor.getFloat(4);
						objInventoryObject.UOM				=	cursor.getString(5);
						objInventoryObject.unitPerCases		=	StringUtils.getInt(cursor.getString(6));
						vecInventoryItems.add(objInventoryObject); 
					}
					while(cursor.moveToNext());
				 }
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return vecInventoryItems;
		}
	}
	
	public HashMap<String, HHInventryQTDO> getAvailInventoryQtys()
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, HHInventryQTDO> hmInventory = new HashMap<String, HHInventryQTDO>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				
				String query = "SELECT SI.ItemCode, SellableQuantity, TP.UnitPerCase, SI.BatchNumber,SI.ExpiryDate,UF.EAConversion " +
						"FROM tblVanStock SI INNER JOIN tblProducts TP ON TP.ItemCode = SI.ItemCode AND SI.SellableQuantity > 0 " +
						"INNER JOIN tblUOMFactor UF ON UF.ItemCode = SI.ItemCode AND UF.UOM='UNIT' " +
						"ORDER BY TP.DisplayOrder ASC";
				cursor		=	_database.rawQuery(query,null);
				
//				cursor		=	_database.rawQuery("SELECT SI.ItemCode, SellableQuantity, TP.UnitPerCase, SI.BatchNumber,SI.ExpiryDate from tblVanStock SI " +
//                        						   "INNER JOIN tblProducts TP ON TP.ItemCode = SI.ItemCode AND SI.SellableQuantity > 0 ORDER BY TP.DisplayOrder ASC", null);
				int eaConversion = 1;
				int totalqty 	 = 0;
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						HHInventryQTDO hhInventryQTDO = new HHInventryQTDO();
						hhInventryQTDO.totalQt 		  = cursor.getInt(1);
						hhInventryQTDO.batchCode 	  = cursor.getString(3);
						hhInventryQTDO.expiryDate	  = cursor.getString(4);
						eaConversion				  = cursor.getInt(5);
						totalqty					  = hhInventryQTDO.totalQt/eaConversion;
						if(hhInventryQTDO.expiryDate.equalsIgnoreCase("0001-01-01"))
							hhInventryQTDO.expiryDate=CalendarUtils.getOrderPostDate();
						
						if(totalqty > 0)
							hmInventory.put(cursor.getString(0), hhInventryQTDO);
					}
					while(cursor.moveToNext());
				 }
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
	public HashMap<String, Float> getUOMFactor()
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, Float> hmInventory = new HashMap<String, Float>();
			SQLiteDatabase _database =null;
			Cursor cursor = null;
			try
			{
				_database 	= 	DatabaseHelper.openDataBase();
				cursor		=	_database.rawQuery("SELECT ItemCode||UOM, Factor FROM tblUOMFactor", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						hmInventory.put(cursor.getString(0), cursor.getFloat(1));
					}
					while(cursor.moveToNext());
				 }
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(_database != null)
					_database.close();
			}
			return hmInventory;
		}
	}
	
}
