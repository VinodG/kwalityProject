package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.dataobject.VanLogDO;
import com.winit.alseer.salesman.dataobject.VanStockDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;


public class InventoryDA 
{
	/*public boolean insertVMInventory(LoadRequestDO loadRequestDO, int loadType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblLoadRequest WHERE id = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblLoadRequest (id,type,date,status,quantity,uploadStatus) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblLoadRequest SET date = ?, quantity = ?,status = ? WHERE id = ?");


				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty, totalQty ) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,SecondaryQuantity = ?, totalQty = ? WHERE ItemCode = ?");


//				SQLiteStatement stmtSelectU		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
//				SQLiteStatement stmtUpdateU 	= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,SecondaryQuantity = ? WHERE ItemCode = ?");


				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblLoadDetail WHERE ItemCode = ? AND id =?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblLoadDetail (id,Date,SalesmanCode,ItemCode,PrimaryQuantity,UOM) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblLoadDetail SET PrimaryQuantity = ? WHERE ItemCode = ? AND id=?");

				if(loadRequestDO != null)
				{

					stmtSelectR.bindString(1, loadRequestDO.requestId);
					long count = stmtSelectR.simpleQueryForLong();

					if(count > 0)
					{	
						stmtUpdateR.bindString(1, ""+loadRequestDO.requestDate);
						stmtUpdateR.bindString(2, ""+loadRequestDO.requestQty);
						stmtUpdateR.bindString(3, ""+loadRequestDO.requestStatus);
						stmtUpdateR.bindString(4, ""+loadRequestDO.requestId);
						stmtUpdateR.execute();
					}
					else
					{
						stmtInsertR.bindString(1, loadRequestDO.requestId);
						stmtInsertR.bindString(2, loadRequestDO.requestType);
						stmtInsertR.bindString(3, loadRequestDO.requestDate);
						stmtInsertR.bindString(4, loadRequestDO.requestStatus);
						stmtInsertR.bindString(5, ""+loadRequestDO.requestQty);
						stmtInsertR.bindString(6, ""+loadRequestDO.requestUploadStatus);
						stmtInsertR.executeInsert();
					}

					for(DeliveryAgentOrderDetailDco inventoryObject : loadRequestDO.vecItems)
					{
						stmtSelectD.bindString(1, inventoryObject.itemCode);
						stmtSelectD.bindString(2, loadRequestDO.requestId);
						long countRec = stmtSelectD.simpleQueryForLong();

						if(countRec != 0)
						{	
							float lastQty = 0;
							Cursor cursor = objSqliteDB.rawQuery("SELECT PrimaryQuantity FROM tblLoadDetail WHERE ItemCode = '"+inventoryObject.itemCode+"' AND id = '"+loadRequestDO.requestId+"'", null);
							if(cursor.moveToFirst())
								lastQty  	= 	StringUtils.getFloat(cursor.getString(0));

							if(cursor != null && !cursor.isClosed())
								cursor.close();

							insertUpdateInventory(objSqliteDB, loadRequestDO, inventoryObject, stmtSelectRec, stmtUpdate, stmtInsert, lastQty, loadType);

							stmtUpdateD.bindString(1, ""+inventoryObject.preUnits);
							stmtUpdateD.bindString(2, inventoryObject.itemCode);
							stmtUpdateD.bindString(3, loadRequestDO.requestId);
							stmtUpdateD.execute();
						}
						else
						{
							stmtInsertD.bindString(1, loadRequestDO.requestId);
							stmtInsertD.bindString(2, loadRequestDO.requestDate);
							stmtInsertD.bindString(3, loadRequestDO.empNo);
							stmtInsertD.bindString(4, inventoryObject.itemCode);
							stmtInsertD.bindString(5, ""+inventoryObject.preUnits);
							stmtInsertD.bindString(6, ""+inventoryObject.strUOM);
							stmtInsertD.executeInsert();

							insertUpdateInventory(objSqliteDB, loadRequestDO, inventoryObject, stmtSelectRec, stmtUpdate, stmtInsert, 0, loadType);
						}
					}
				}

				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();

				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}*/

	private void insertUpdateInventory(SQLiteDatabase objSqliteDB ,LoadRequestDO loadRequestDO,DeliveryAgentOrderDetailDco inventoryObject, SQLiteStatement stmtSelectRec, SQLiteStatement stmtUpdate, SQLiteStatement stmtInsert, float lastSKUQty, int load_type)
	{
		stmtSelectRec.bindString(1, inventoryObject.itemCode);
		long countRec = stmtSelectRec.simpleQueryForLong();

		if(countRec != 0)
		{	
			float lastUnits = 0, totalUnits = 0, availUnits = 0;
			Cursor cursor   = objSqliteDB.rawQuery("SELECT SI.SecondaryQuantity,SI.totalQty, SI.availQty " +
					"FROM tblVMSalesmanInventory SI WHERE SI.ItemCode = '"+inventoryObject.itemCode+"'", null);

			if(cursor.moveToFirst())
			{
				lastUnits  	= 	cursor.getFloat(0);
				totalUnits 	= 	cursor.getFloat(1);
				availUnits 	= 	cursor.getFloat(2);

				if(load_type == AppConstants.LOAD_STOCK)
				{
					lastUnits  = 	lastUnits  + inventoryObject.preUnits - lastSKUQty;
					totalUnits = 	totalUnits + inventoryObject.preUnits - lastSKUQty;
					availUnits = 	availUnits + inventoryObject.preUnits - lastSKUQty;
				}
				else
				{
					lastUnits  = 	lastUnits  + lastSKUQty - inventoryObject.preUnits;
					totalUnits = 	totalUnits + lastSKUQty - inventoryObject.preUnits;
					availUnits = 	availUnits + lastSKUQty - inventoryObject.preUnits;
				}

				lastUnits   = lastUnits  < 0 ? 0 : lastUnits;
				totalUnits  = totalUnits < 0 ? 0 : totalUnits;
				availUnits  = availUnits < 0 ? 0 : availUnits;

				stmtUpdate.bindString(1, ""+lastUnits);
				stmtUpdate.bindString(2, ""+availUnits);
				stmtUpdate.bindString(3, ""+lastUnits);
				stmtUpdate.bindString(4, ""+totalUnits);
				stmtUpdate.bindString(5, inventoryObject.itemCode);

				stmtUpdate.execute();
			}
			if(cursor != null && !cursor.isClosed())
				cursor.close();
		}
		else if(load_type == AppConstants.LOAD_STOCK)
		{
			stmtInsert.bindString(1, loadRequestDO.MovementCode);
			stmtInsert.bindString(2, loadRequestDO.MovementDate);
			stmtInsert.bindString(3, loadRequestDO.UserCode);
			stmtInsert.bindString(4, inventoryObject.itemCode);
			stmtInsert.bindString(5, ""+inventoryObject.preUnits);
			stmtInsert.bindString(6, ""+inventoryObject.preUnits);
			stmtInsert.bindString(7, "false");
			stmtInsert.bindString(8, ""+inventoryObject.preUnits);
			stmtInsert.bindString(9, ""+(StringUtils.getInt(inventoryObject.totalQtyShiped)-inventoryObject.preUnits));
			stmtInsert.executeInsert();
		}
	}

	//	public boolean getPendingStatus(String moveMentId)
	//	{
	//		synchronized(MyApplication.MyLock) 
	//		{
	//			SQLiteDatabase objSqliteDB 	=	null;
	//			Cursor cursor = null;
	//			boolean isAprovwed = false;
	//			String query= "SELECT MovementStatus FROM tblMovementHeader WHERE MovementCode ='"+moveMentId+"'";
	//			try
	//			{
	//				objSqliteDB = DatabaseHelper.openDataBase();
	//				cursor 		   = objSqliteDB.rawQuery(query, null);
	//				if(cursor.moveToFirst())
	//				{
	//					if(cursor.getString(0).equalsIgnoreCase("Pending"))
	//						isAprovwed = false;
	//					else
	//						isAprovwed = true;
	//				}
	//				if(cursor != null && !cursor.isClosed())
	//					cursor.close();
	//			}
	//			catch (Exception e)
	//			{
	//				e.printStackTrace();
	//			}
	//			finally
	//			{
	//				if(cursor != null && !cursor.isClosed())
	//					cursor.close();
	//				
	//				if(objSqliteDB != null)
	//					objSqliteDB.close();
	//			}
	//			
	//			return isAprovwed;
	//		}
	//	}

	public int getPendingStatus(String moveMentId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			int status = LoadRequestDO.MOVEMENT_STATUS_PENDING;
			String query= "SELECT MovementStatus FROM tblMovementHeader WHERE MovementCode ='"+moveMentId+"'";
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				cursor 		   = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
					status = StringUtils.getInt(cursor.getString(0));
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();

				if(objSqliteDB != null)
					objSqliteDB.close();
			}

			return status;
		}
	}

	public void insertUpdateInventory(LoadRequestDO loadRequestDO , int load_type, Vector<NameIDDo> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty, totalQty ) VALUES(?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtSelectU		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
				SQLiteStatement stmtUpdateU 	= 	objSqliteDB.compileStatement("UPDATE tblVMSalesmanInventory SET PrimaryQuantity = ?, availQty = ?,SecondaryQuantity = ?, totalQty = ? WHERE ItemCode = ?");

				for(NameIDDo object : vec)
				{
					stmtSelectU.bindString(1, object.strName);
					long countRec = stmtSelectU.simpleQueryForLong();

					if(countRec != 0)
					{	
						float lastUnits = 0, totalUnits = 0, availUnits = 0;
						Cursor cursor   = objSqliteDB.rawQuery("SELECT SI.SecondaryQuantity,SI.totalQty, SI.availQty " +
								"FROM tblVMSalesmanInventory SI WHERE SI.ItemCode = '"+object.strName+"'", null);

						if(cursor.moveToFirst())
						{
							lastUnits  	= 	cursor.getFloat(0);
							totalUnits 	= 	cursor.getFloat(1);
							availUnits 	= 	cursor.getFloat(2);

							if(load_type == AppConstants.LOAD_STOCK)
							{
								lastUnits  = 	lastUnits  + StringUtils.getFloat(object.strType);
								totalUnits = 	totalUnits + StringUtils.getFloat(object.strType);
								availUnits = 	availUnits + StringUtils.getFloat(object.strType);
							}
							else
							{
								lastUnits  = 	lastUnits  - StringUtils.getFloat(object.strType);
								totalUnits = 	totalUnits - StringUtils.getFloat(object.strType);
								availUnits = 	availUnits - StringUtils.getFloat(object.strType);
							}

							lastUnits   = lastUnits  < 0 ? 0 : lastUnits;
							totalUnits  = totalUnits < 0 ? 0 : totalUnits;
							availUnits  = availUnits < 0 ? 0 : availUnits;

							stmtUpdateU.bindString(1, ""+lastUnits);
							stmtUpdateU.bindString(2, ""+availUnits);
							stmtUpdateU.bindString(3, ""+lastUnits);
							stmtUpdateU.bindString(4, ""+totalUnits);
							stmtUpdateU.bindString(5, object.strName);

							stmtUpdateU.execute();
						}
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
					else if(load_type == AppConstants.LOAD_STOCK)
					{
						stmtInsert.bindString(1, loadRequestDO.MovementCode);
						stmtInsert.bindString(2, loadRequestDO.MovementDate);
						stmtInsert.bindString(3, loadRequestDO.UserCode);
						stmtInsert.bindString(4, object.strName);
						stmtInsert.bindString(5, ""+object.strType);
						stmtInsert.bindString(6, ""+object.strType);
						stmtInsert.bindString(7, "false");
						stmtInsert.bindString(8, ""+object.strType);
						stmtInsert.bindString(9, ""+(object.strType));
						stmtInsert.executeInsert();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public ArrayList<LoadRequestDO> getAllRequestByType(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			LoadRequestDO loadRequestDO = null;
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query = "";
			if(type.equalsIgnoreCase(""+AppConstants.UNLOAD_VAN_TRANSFER_STOCK))
				query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId FROM tblMovementHeader WHERE MovementType ='"+AppConstants.LOAD_VAN_TRANSFER_STOCK+"' AND UserLoadType ='"+type+"' ORDER BY MovementDate DESC";
			else
				query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId FROM tblMovementHeader WHERE MovementType ='"+type+"' ORDER BY MovementDate DESC";
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						loadRequestDO 				= 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus =	cursor.getString(3);
						loadRequestDO.AppMovementId =	cursor.getString(4);
						vectLoadRequestDO.add(loadRequestDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();

				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectLoadRequestDO;
		}
	}
	public ArrayList<LoadRequestDO> getAllVanstockRequestByType(int type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			LoadRequestDO loadRequestDO = null;
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query = "";
			query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId FROM tblMovementHeader WHERE MovementType ='"+AppConstants.LOAD_VAN_TRANSFER_STOCK+"' AND UserLoadType ='"+type+"' ORDER BY MovementDate DESC";

			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						loadRequestDO 				= 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus =	cursor.getString(3);
						loadRequestDO.AppMovementId =	cursor.getString(4);
						vectLoadRequestDO.add(loadRequestDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();

				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectLoadRequestDO;
		}
	}
	public ArrayList<LoadRequestDO> getAllRequestApprovedByType(String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList <LoadRequestDO> vectLoadRequestDO = new ArrayList<LoadRequestDO>();
			Cursor cursor = null;
			SQLiteDatabase sqLiteDatabase = null;
			String query= "SELECT MovementCode,MovementType,MovementDate,MovementStatus,AppMovementId FROM tblMovementHeader WHERE MovementType ='"+type+"' and MovementStatus='"+LoadRequestDO.MOVEMENT_STATUS_APPROVED+"'";
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor 		   = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						LoadRequestDO loadRequestDO = 	new LoadRequestDO();
						loadRequestDO.MovementCode	=	cursor.getString(0);
						loadRequestDO.MovementType	=	cursor.getString(1);
						loadRequestDO.MovementDate	=	cursor.getString(2);
						loadRequestDO.MovementStatus =	cursor.getString(3);
						loadRequestDO.AppMovementId =	cursor.getString(4);
						vectLoadRequestDO.add(loadRequestDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();

				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectLoadRequestDO;
		}
	}
	public ArrayList<DeliveryAgentOrderDetailDco> getAllItemToVerify(String date, String id, int load_type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;

			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT SUM(INV.SecondaryQuantity), SUM(INV.PrimaryQuantity),INV.ItemCode,TP.Description,TP.UnitPerCase, TP.UOM, PT.PRICECASES from  tblProducts TP,tblVMSalesmanInventory INV,tblPricing PT where INV.Date like '%"+date+"%' and PT.ITEMCODE =INV.ItemCode AND TP.SKU = INV.ItemCode AND INV.VMSalesmanInventoryId = '"+id+"' group by INV.ItemCode ORDER BY TP.DisplayOrder ASC";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preCases			= 	StringUtils.getInt(cursor.getString(0)); 
							OrderDetail.preUnits			= 	StringUtils.getInt(cursor.getString(1));
							OrderDetail.itemCode 			= 	cursor.getString(2);
							OrderDetail.itemDescription		= 	cursor.getString(3);
							OrderDetail.unitPerCase			= 	cursor.getInt(4);
							OrderDetail.strUOM				= 	cursor.getString(5);
							OrderDetail.unitSellingPrice	= 	cursor.getString(6);
							OrderDetail.invoiceAmount 		=   StringUtils.getFloat(OrderDetail.unitSellingPrice) * OrderDetail.preUnits;

							if(load_type == AppConstants.UNLOAD_STOCK)
								OrderDetail.preUnits = (int) OrderDetail.preCases;

							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}

			return vectorOrderList;
		}
	}

	public ArrayList<DeliveryAgentOrderDetailDco> getAllItemToVerifyById(String date, String id, int load_type)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<DeliveryAgentOrderDetailDco> vectorOrderList = new ArrayList<DeliveryAgentOrderDetailDco>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			/*INV.Date like '%"+date+"%' AND */
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT SUM(INV.PrimaryQuantity),INV.ItemCode,TP.Description,TP.UnitPerCase, TP.UOM from tblProducts TP,tblLoadDetail INV where TP.SKU = INV.ItemCode AND INV.id = '"+id+"' group by INV.ItemCode ORDER BY TP.DisplayOrder ASC";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							DeliveryAgentOrderDetailDco OrderDetail	= new DeliveryAgentOrderDetailDco();
							OrderDetail.preCases			= 	StringUtils.getInt(cursor.getString(0)); 
							OrderDetail.preUnits			= 	StringUtils.getInt(cursor.getString(0));
							OrderDetail.itemCode 			= 	cursor.getString(1);
							OrderDetail.itemDescription		= 	cursor.getString(2);
							OrderDetail.unitPerCase			= 	cursor.getInt(3);
							OrderDetail.strUOM				= 	cursor.getString(4);
							OrderDetail.totalQtyShiped		= 	1000+"";
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}

			return vectorOrderList;
		}
	}

	public ArrayList<VanLoadDO> getAllItemToVerifyByLoadId(String loadId) 
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<VanLoadDO> vectorOrderList = new ArrayList<VanLoadDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null;
			/*INV.Date like '%"+date+"%' AND */
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				//				String strQuery 	= 	"SELECT ItemCode,ItemDescription, UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,InProcessQuantityLevel1 ,InProcessQuantityLevel2 ,InProcessQuantityLevel3 , InProcessQuantity,ShippedQuantityLevel1 ,ShippedQuantityLevel2 ,ShippedQuantityLevel3 ,ShippedQuantity FROM tblMovementDetail WHERE MovementCode = '"+loadId+"' ORDER BY ItemCode AND DisplayOrder ASC";
				String strQuery		=	"SELECT MD.ItemCode,MD.ItemDescription, MD.UOM, MD.QuantityLevel1,MD.QuantityLevel2," +
						"MD.QuantityLevel3,MD.QuantityBU,MD.InProcessQuantityLevel1 ,MD.InProcessQuantityLevel2 ," +
						"MD.InProcessQuantityLevel3 , MD.InProcessQuantity,MD.ShippedQuantityLevel1 ,MD.ShippedQuantityLevel2 ," +
						"MD.ShippedQuantityLevel3 ,MD.ShippedQuantity " +
						"FROM tblMovementDetail MD " +
						"INNER JOIN tblProducts P ON P.ItemCode=MD.ItemCode AND P.IsActive='True' " +
						"WHERE MD.MovementCode = '"+loadId+"' " +
						"ORDER BY P.DisplayOrder ASC";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							VanLoadDO OrderDetail			= 	new VanLoadDO();
							OrderDetail.ItemCode 			= 	cursor.getString(0);
							OrderDetail.Description			= 	cursor.getString(1);
							OrderDetail.UOM					= 	cursor.getString(2);
							OrderDetail.quantityLevel1	= 	cursor.getInt(3);
							OrderDetail.quantityLevel2	= 	cursor.getInt(4);
							OrderDetail.quantityLevel3	= 	cursor.getInt(5);
							OrderDetail.SellableQuantity= 	cursor.getInt(6);

							OrderDetail.inProcessQuantityLevel1	= 	cursor.getInt(7);
							OrderDetail.inProcessQuantityLevel2	= 	cursor.getInt(8);
							OrderDetail.inProcessQuantityLevel3	= 	cursor.getInt(9);
							OrderDetail.inProccessQty= 	cursor.getInt(10);

							OrderDetail.shippedQuantityLevel1	= 	cursor.getInt(11);
							OrderDetail.shippedQuantityLevel2	= 	cursor.getInt(12);
							OrderDetail.shippedQuantityLevel3	= 	cursor.getInt(13);
							OrderDetail.ShippedQuantity= 	cursor.getInt(14);
							vectorOrderList.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}

			return vectorOrderList;
		}
	}


	public boolean insertLoadRequest(LoadRequestDO loadRequestDO, int loadType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementHeader WHERE MovementCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						"(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						"MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						"IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn, ProductType, WHCode, UserLoadType,Division) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						"MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						"IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?, ProductType= ?, WHCode=?, UserLoadType=?, Division=? WHERE MovementCode=?");


				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
						"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
						"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
						"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
						"ShippedQuantity,ModifiedOn,InProcessQuantityLevel1,InProcessQuantityLevel2,InProcessQuantityLevel3,"
						+ " ShippedQuantityLevel1,ShippedQuantityLevel2,ShippedQuantityLevel3) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
						"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
						"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
						"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
						"ShippedQuantity=?,ModifiedOn=?,InProcessQuantityLevel1=?,InProcessQuantityLevel2=?,InProcessQuantityLevel3=?, "
						+ " ShippedQuantityLevel1=?, ShippedQuantityLevel2=?,ShippedQuantityLevel3=? WHERE MovementCode=? AND ItemCode=?");

				if(loadRequestDO != null)
				{
					stmtSelectR.bindString(1, loadRequestDO.MovementCode);
					long count = stmtSelectR.simpleQueryForLong();

					if(count > 0)
					{	
						stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
						stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
						stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
						stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
						stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
						stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
						stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
						stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
						stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
						stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
						stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
						stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
						stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
						stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);

						stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
						stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
						stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
						stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
						stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
						stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
						stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
						stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);

						stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
						stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
						stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
						stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
						stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
						stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
						stmtUpdateR.bindString(29, ""+loadRequestDO.ProductType);
						stmtUpdateR.bindString(30, ""+loadRequestDO.WHCode);
						stmtUpdateR.bindString(31, ""+loadRequestDO.UserLoadType);
						stmtUpdateR.bindString(32, ""+loadRequestDO.Division);
						stmtUpdateR.bindString(33, ""+loadRequestDO.MovementCode);
						stmtUpdateR.execute();
					}
					else
					{
						stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
						stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
						stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
						stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
						stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
						stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
						stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
						stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
						stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
						stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
						stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
						stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
						stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
						stmtInsertR.bindString(14, ""+loadRequestDO.Status);
						stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);

						stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
						stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
						stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
						stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
						stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
						stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
						stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
						stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);

						stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
						stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
						stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
						stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
						stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
						stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
						stmtInsertR.bindString(30, ""+loadRequestDO.ProductType);
						stmtInsertR.bindString(31, ""+loadRequestDO.WHCode);
						stmtInsertR.bindString(32, ""+loadRequestDO.UserLoadType);
						stmtInsertR.bindLong(33, loadRequestDO.Division);
						stmtInsertR.executeInsert();
					}

					for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
					{
						stmtSelectD.bindString(1, inventoryObject.MovementCode);
						stmtSelectD.bindString(2, inventoryObject.ItemCode);
						long countRec = stmtSelectD.simpleQueryForLong();

						if(countRec != 0)
						{	
							stmtUpdateD.bindString(1, ""+inventoryObject.LineNo);
							stmtUpdateD.bindString(2, inventoryObject.OrgCode);
							stmtUpdateD.bindString(3, inventoryObject.ItemDescription);
							stmtUpdateD.bindString(4, ""+inventoryObject.ItemAltDescription);
							stmtUpdateD.bindString(5, inventoryObject.MovementStatus);
							stmtUpdateD.bindString(6, inventoryObject.UOM);
							stmtUpdateD.bindString(7, ""+inventoryObject.QuantityLevel1);
							stmtUpdateD.bindString(8, ""+inventoryObject.QuantityLevel2);
							stmtUpdateD.bindString(9, ""+inventoryObject.QuantityLevel3);
							stmtUpdateD.bindString(10, ""+inventoryObject.QuantityBU);
							stmtUpdateD.bindString(11, ""+inventoryObject.QuantitySU);
							stmtUpdateD.bindString(12, ""+inventoryObject.NonSellableQty);

							stmtUpdateD.bindString(13, ""+inventoryObject.CurrencyCode);
							stmtUpdateD.bindString(14, ""+inventoryObject.PriceLevel1);
							stmtUpdateD.bindString(15, ""+inventoryObject.PriceLevel2);
							stmtUpdateD.bindString(16, ""+inventoryObject.PriceLevel3);
							stmtUpdateD.bindString(17, inventoryObject.MovementReasonCode);
							stmtUpdateD.bindString(18, inventoryObject.ExpiryDate);
							stmtUpdateD.bindString(19, ""+inventoryObject.Note);
							stmtUpdateD.bindString(20, inventoryObject.AffectedStock);
							stmtUpdateD.bindString(21, loadRequestDO.Status);

							stmtUpdateD.bindString(22, ""+inventoryObject.DistributionCode);
							stmtUpdateD.bindString(23, inventoryObject.CreatedOn);
							stmtUpdateD.bindString(24, loadRequestDO.ModifiedDate);
							stmtUpdateD.bindString(25, ""+inventoryObject.ModifiedTime);
							stmtUpdateD.bindString(26, inventoryObject.PushedOn);
							stmtUpdateD.bindString(27, ""+inventoryObject.CancelledQuantity);

							stmtUpdateD.bindString(28, ""+inventoryObject.InProcessQuantity);
							stmtUpdateD.bindString(29, ""+inventoryObject.ShippedQuantity);
							stmtUpdateD.bindString(30, loadRequestDO.ModifiedOn);
							stmtUpdateD.bindLong(31, inventoryObject.inProcessQuantityLevel1);
							stmtUpdateD.bindLong(32, inventoryObject.inProcessQuantityLevel2);
							stmtUpdateD.bindLong(33, inventoryObject.inProcessQuantityLevel3);
							stmtUpdateD.bindLong(34, inventoryObject.shippedQuantityLevel1);
							stmtUpdateD.bindLong(35, inventoryObject.shippedQuantityLevel2);
							stmtUpdateD.bindLong(36, inventoryObject.shippedQuantityLevel3);

							stmtUpdateD.bindString(37, loadRequestDO.MovementCode);
							stmtUpdateD.bindString(38, inventoryObject.ItemCode);
							stmtUpdateD.execute();
						}
						else
						{
							stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
							stmtInsertD.bindString(2, loadRequestDO.MovementCode);
							stmtInsertD.bindString(3, inventoryObject.ItemCode);
							stmtInsertD.bindString(4, inventoryObject.OrgCode);
							stmtInsertD.bindString(5, inventoryObject.ItemDescription);
							stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
							stmtInsertD.bindString(7, inventoryObject.MovementStatus);
							stmtInsertD.bindString(8, inventoryObject.UOM);
							stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
							stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
							stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
							stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
							stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
							stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
							stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
							stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
							stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
							stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
							stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
							stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
							stmtInsertD.bindString(21, ""+inventoryObject.Note);
							stmtInsertD.bindString(22, inventoryObject.AffectedStock);
							stmtInsertD.bindString(23, loadRequestDO.Status);

							stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
							stmtInsertD.bindString(25, inventoryObject.CreatedOn);
							stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
							stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
							stmtInsertD.bindString(28, inventoryObject.PushedOn);
							stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);

							stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
							stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
							stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);

							stmtInsertD.bindLong(33, inventoryObject.inProcessQuantityLevel1);
							stmtInsertD.bindLong(34, inventoryObject.inProcessQuantityLevel2);
							stmtInsertD.bindLong(35, inventoryObject.inProcessQuantityLevel3);
							stmtInsertD.bindLong(36, inventoryObject.shippedQuantityLevel1);
							stmtInsertD.bindLong(37, inventoryObject.shippedQuantityLevel2);
							stmtInsertD.bindLong(38, inventoryObject.shippedQuantityLevel3);

							stmtInsertD.executeInsert();
						}
					}
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();

				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean updateLoadRequestVantransfer(LoadRequestDO loadRequestDO, int loadType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementHeader WHERE MovementCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						"(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						"MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						"IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn, ProductType, WHCode, UserLoadType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						"MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						"IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?, ProductType= ?, WHCode=?, UserLoadType=? WHERE MovementCode=?");


				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
						"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
						"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
						"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
						"ShippedQuantity,ModifiedOn,InProcessQuantityLevel1,InProcessQuantityLevel2,InProcessQuantityLevel3,"
						+ " ShippedQuantityLevel1,ShippedQuantityLevel2,ShippedQuantityLevel3) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET InProcessQuantityLevel1=? WHERE MovementCode=? AND ItemCode=?");

				if(loadRequestDO != null)
				{
					stmtSelectR.bindString(1, loadRequestDO.MovementCode);
					long count = stmtSelectR.simpleQueryForLong();

					if(count > 0)
					{	
						stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
						stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
						stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
						stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
						stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
						stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
						stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
						stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
						stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
						stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
						stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
						stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
						stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
						stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);

						stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
						stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
						stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
						stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
						stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
						stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
						stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
						stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);

						stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
						stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
						stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
						stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
						stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
						stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
						stmtUpdateR.bindString(29, ""+loadRequestDO.ProductType);
						stmtUpdateR.bindString(30, ""+loadRequestDO.WHCode);
						stmtUpdateR.bindString(31, ""+loadRequestDO.UserLoadType);
						stmtUpdateR.bindString(32, ""+loadRequestDO.MovementCode);
						stmtUpdateR.execute();
					}
					else
					{
						stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
						stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
						stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
						stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
						stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
						stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
						stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
						stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
						stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
						stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
						stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
						stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
						stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
						stmtInsertR.bindString(14, ""+loadRequestDO.Status);
						stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);

						stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
						stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
						stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
						stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
						stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
						stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
						stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
						stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);

						stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
						stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
						stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
						stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
						stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
						stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
						stmtInsertR.bindString(30, ""+loadRequestDO.ProductType);
						stmtInsertR.bindString(31, ""+loadRequestDO.WHCode);
						stmtInsertR.bindString(32, ""+loadRequestDO.UserLoadType);
						stmtInsertR.executeInsert();
					}

					for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
					{
						stmtSelectD.bindString(1, inventoryObject.MovementCode);
						stmtSelectD.bindString(2, inventoryObject.ItemCode);
						long countRec = stmtSelectD.simpleQueryForLong();

						if(countRec != 0)
						{	
							stmtUpdateD.bindLong(1, inventoryObject.inProcessQuantityLevel1);

							stmtUpdateD.bindString(2, loadRequestDO.MovementCode);
							stmtUpdateD.bindString(3, inventoryObject.ItemCode);
							stmtUpdateD.execute();
						}
						else
						{
							stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
							stmtInsertD.bindString(2, loadRequestDO.MovementCode);
							stmtInsertD.bindString(3, inventoryObject.ItemCode);
							stmtInsertD.bindString(4, inventoryObject.OrgCode);
							stmtInsertD.bindString(5, inventoryObject.ItemDescription);
							stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
							stmtInsertD.bindString(7, inventoryObject.MovementStatus);
							stmtInsertD.bindString(8, inventoryObject.UOM);
							stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
							stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
							stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
							stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
							stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
							stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
							stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
							stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
							stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
							stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
							stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
							stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
							stmtInsertD.bindString(21, ""+inventoryObject.Note);
							stmtInsertD.bindString(22, inventoryObject.AffectedStock);
							stmtInsertD.bindString(23, loadRequestDO.Status);

							stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
							stmtInsertD.bindString(25, inventoryObject.CreatedOn);
							stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
							stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
							stmtInsertD.bindString(28, inventoryObject.PushedOn);
							stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);

							stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
							stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
							stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);

							stmtInsertD.bindLong(33, inventoryObject.inProcessQuantityLevel1);
							stmtInsertD.bindLong(34, inventoryObject.inProcessQuantityLevel2);
							stmtInsertD.bindLong(35, inventoryObject.inProcessQuantityLevel3);
							stmtInsertD.bindLong(36, inventoryObject.shippedQuantityLevel1);
							stmtInsertD.bindLong(37, inventoryObject.shippedQuantityLevel2);
							stmtInsertD.bindLong(38, inventoryObject.shippedQuantityLevel3);

							stmtInsertD.executeInsert();
						}
					}
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();

				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean insertLoadRequests(ArrayList<LoadRequestDO> arrloadRequestDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementHeader WHERE MovementCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						"(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						"MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						"IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn, ProductType, WHCode,UserLoadType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						"MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						"IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?, ProductType= ?, WHCode=?, UserLoadType=? WHERE MovementCode=?");


				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
						"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
						"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
						"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
						"ShippedQuantity,ModifiedOn,InProcessQuantityLevel1,InProcessQuantityLevel2,InProcessQuantityLevel3,"
						+ " ShippedQuantityLevel1,ShippedQuantityLevel2,ShippedQuantityLevel3) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
						"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
						"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
						"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
						"ShippedQuantity=?,ModifiedOn=?,InProcessQuantityLevel1=?,InProcessQuantityLevel2=?,InProcessQuantityLevel3=?, "
						+ " ShippedQuantityLevel1=?, ShippedQuantityLevel2=?,ShippedQuantityLevel3=? WHERE MovementCode=? AND ItemCode=?");

				if(arrloadRequestDO != null && arrloadRequestDO.size() > 0)
				{
					for (int i = 0; i < arrloadRequestDO.size(); i++) 
					{
						LoadRequestDO loadRequestDO= arrloadRequestDO.get(i);

						if(loadRequestDO != null)
						{
							stmtSelectR.bindString(1, loadRequestDO.MovementCode);
							long count = stmtSelectR.simpleQueryForLong();

							if(count > 0)
							{	
								stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
								stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
								stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
								stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
								stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
								stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
								stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
								stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
								stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
								stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
								stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
								stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
								stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
								stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);

								stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
								stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
								stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
								stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
								stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
								stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
								stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
								stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);

								stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
								stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
								stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
								stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
								stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
								stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
								stmtUpdateR.bindString(29, ""+loadRequestDO.ProductType);
								stmtUpdateR.bindString(30, ""+loadRequestDO.WHCode);
								stmtUpdateR.bindString(31, ""+loadRequestDO.UserLoadType);
								stmtUpdateR.bindString(32, ""+loadRequestDO.MovementCode);
								stmtUpdateR.execute();
							}
							else
							{
								stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
								stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
								stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
								stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
								stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
								stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
								stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
								stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
								stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
								stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
								stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
								stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
								stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
								stmtInsertR.bindString(14, ""+loadRequestDO.Status);
								stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);

								stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
								stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
								stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
								stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
								stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
								stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
								stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
								stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);

								stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
								stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
								stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
								stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
								stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
								stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
								stmtInsertR.bindString(30, ""+loadRequestDO.ProductType);
								stmtInsertR.bindString(31, ""+loadRequestDO.WHCode);
								stmtInsertR.bindString(32, ""+loadRequestDO.UserLoadType);
								stmtInsertR.executeInsert();
							}

							for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
							{
								stmtSelectD.bindString(1, inventoryObject.MovementCode);
								stmtSelectD.bindString(2, inventoryObject.ItemCode);
								long countRec = stmtSelectD.simpleQueryForLong();

								if(countRec != 0)
								{	
									stmtUpdateD.bindString(1, ""+inventoryObject.LineNo);
									stmtUpdateD.bindString(2, inventoryObject.OrgCode);
									stmtUpdateD.bindString(3, inventoryObject.ItemDescription);
									stmtUpdateD.bindString(4, ""+inventoryObject.ItemAltDescription);
									stmtUpdateD.bindString(5, inventoryObject.MovementStatus);
									stmtUpdateD.bindString(6, inventoryObject.UOM);
									stmtUpdateD.bindString(7, ""+inventoryObject.QuantityLevel1);
									stmtUpdateD.bindString(8, ""+inventoryObject.QuantityLevel2);
									stmtUpdateD.bindString(9, ""+inventoryObject.QuantityLevel3);
									stmtUpdateD.bindString(10, ""+inventoryObject.QuantityBU);
									stmtUpdateD.bindString(11, ""+inventoryObject.QuantitySU);
									stmtUpdateD.bindString(12, ""+inventoryObject.NonSellableQty);

									stmtUpdateD.bindString(13, ""+inventoryObject.CurrencyCode);
									stmtUpdateD.bindString(14, ""+inventoryObject.PriceLevel1);
									stmtUpdateD.bindString(15, ""+inventoryObject.PriceLevel2);
									stmtUpdateD.bindString(16, ""+inventoryObject.PriceLevel3);
									stmtUpdateD.bindString(17, inventoryObject.MovementReasonCode);
									stmtUpdateD.bindString(18, inventoryObject.ExpiryDate);
									stmtUpdateD.bindString(19, ""+inventoryObject.Note);
									stmtUpdateD.bindString(20, inventoryObject.AffectedStock);
									stmtUpdateD.bindString(21, loadRequestDO.Status);

									stmtUpdateD.bindString(22, ""+inventoryObject.DistributionCode);
									stmtUpdateD.bindString(23, inventoryObject.CreatedOn);
									stmtUpdateD.bindString(24, loadRequestDO.ModifiedDate);
									stmtUpdateD.bindString(25, ""+inventoryObject.ModifiedTime);
									stmtUpdateD.bindString(26, inventoryObject.PushedOn);
									stmtUpdateD.bindString(27, ""+inventoryObject.CancelledQuantity);

									stmtUpdateD.bindString(28, ""+inventoryObject.InProcessQuantity);
									stmtUpdateD.bindString(29, ""+inventoryObject.ShippedQuantity);
									stmtUpdateD.bindString(30, loadRequestDO.ModifiedOn);
									stmtUpdateD.bindLong(31, inventoryObject.inProcessQuantityLevel1);
									stmtUpdateD.bindLong(32, inventoryObject.inProcessQuantityLevel2);
									stmtUpdateD.bindLong(33, inventoryObject.inProcessQuantityLevel3);
									stmtUpdateD.bindLong(34, inventoryObject.shippedQuantityLevel1);
									stmtUpdateD.bindLong(35, inventoryObject.shippedQuantityLevel2);
									stmtUpdateD.bindLong(36, inventoryObject.shippedQuantityLevel3);

									stmtUpdateD.bindString(37, loadRequestDO.MovementCode);
									stmtUpdateD.bindString(38, inventoryObject.ItemCode);
									stmtUpdateD.execute();
								}
								else
								{
									stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
									stmtInsertD.bindString(2, loadRequestDO.MovementCode);
									stmtInsertD.bindString(3, inventoryObject.ItemCode);
									stmtInsertD.bindString(4, inventoryObject.OrgCode);
									stmtInsertD.bindString(5, inventoryObject.ItemDescription);
									stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
									stmtInsertD.bindString(7, inventoryObject.MovementStatus);
									stmtInsertD.bindString(8, inventoryObject.UOM);
									stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
									stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
									stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
									stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
									stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
									stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
									stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
									stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
									stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
									stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
									stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
									stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
									stmtInsertD.bindString(21, ""+inventoryObject.Note);
									stmtInsertD.bindString(22, inventoryObject.AffectedStock);
									stmtInsertD.bindString(23, loadRequestDO.Status);

									stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
									stmtInsertD.bindString(25, inventoryObject.CreatedOn);
									stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
									stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
									stmtInsertD.bindString(28, inventoryObject.PushedOn);
									stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);

									stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
									stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
									stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);

									stmtInsertD.bindLong(33, inventoryObject.inProcessQuantityLevel1);
									stmtInsertD.bindLong(34, inventoryObject.inProcessQuantityLevel2);
									stmtInsertD.bindLong(35, inventoryObject.inProcessQuantityLevel3);
									stmtInsertD.bindLong(36, inventoryObject.shippedQuantityLevel1);
									stmtInsertD.bindLong(37, inventoryObject.shippedQuantityLevel2);
									stmtInsertD.bindLong(38, inventoryObject.shippedQuantityLevel3);

									stmtInsertD.executeInsert();
								}
							}
						}
					}
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();

				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean insertLoadRequests(HashMap<String, LoadRequestDO> hashMap)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementHeader WHERE MovementCode = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementHeader " +
						"(MovementCode,PreMovementCode,AppMovementId,OrgCode,UserCode,WHKeeperCode,CurrencyCode,JourneyCode,"+
						"MovementDate,MovementNote,MovementType,SourceVehicleCode,DestinationVehicleCode,Status,VisitID"+
						",MovementStatus,CreatedOn,ApproveByCode,ApprovedDate,JDETRXNumber,ISStampDate,ISFromPC,OperatorCode,"+
						"IsDummyCount,Amount,ModifiedDate,ModifiedTime,PushedOn,ModifiedOn,WHCode,UserLoadType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblMovementHeader SET PreMovementCode=?,AppMovementId=?,OrgCode=?,UserCode=?,WHKeeperCode=?,CurrencyCode=?,JourneyCode=?,"+
						"MovementDate=?,MovementNote=?,MovementType=?,SourceVehicleCode=?,DestinationVehicleCode=?,Status=?,VisitID=?"+
						",MovementStatus=?,CreatedOn=?,ApproveByCode=?,ApprovedDate=?,JDETRXNumber=?,ISStampDate=?,ISFromPC=?,OperatorCode=?,"+
						"IsDummyCount=?,Amount=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,ModifiedOn=?,WHCode=?,UserLoadType=? WHERE MovementCode=?");


				SQLiteStatement stmtSelectD 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblMovementDetail WHERE MovementCode=? AND ItemCode=?");
				SQLiteStatement stmtInsertD 	= 	objSqliteDB.compileStatement("INSERT INTO tblMovementDetail (LineNo,MovementCode,ItemCode,OrgCode,ItemDescription,ItemAltDescription," +
						"MovementStatus,UOM, QuantityLevel1,QuantityLevel2,QuantityLevel3,QuantityBU,QuantitySU,NonSellableQty," +
						"CurrencyCode, PriceLevel1,PriceLevel2,PriceLevel3,MovementReasonCode,ExpiryDate,Note,AffectedStock,Status," +
						"DistributionCode, CreatedOn,ModifiedDate,ModifiedTime,PushedOn,CancelledQuantity,InProcessQuantity," +
						"ShippedQuantity,ModifiedOn,InProcessQuantityLevel1,InProcessQuantityLevel2 ,InProcessQuantityLevel3,ShippedQuantityLevel1,ShippedQuantityLevel2 ,ShippedQuantityLevel3) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateD 	= 	objSqliteDB.compileStatement("UPDATE tblMovementDetail SET LineNo=?,OrgCode=?,ItemDescription=?,ItemAltDescription=?," +
						"MovementStatus=?,UOM=?, QuantityLevel1=?,QuantityLevel2=?,QuantityLevel3=?,QuantityBU=?,QuantitySU=?,NonSellableQty=?," +
						"CurrencyCode=?, PriceLevel1=?,PriceLevel2=?,PriceLevel3=?,MovementReasonCode=?,ExpiryDate=?,Note=?,AffectedStock=?,Status=?," +
						"DistributionCode=?, CreatedOn=?,ModifiedDate=?,ModifiedTime=?,PushedOn=?,CancelledQuantity=?,InProcessQuantity=?," +
						"ShippedQuantity=?,ModifiedOn=?,InProcessQuantityLevel1=?,InProcessQuantityLevel2=? ,InProcessQuantityLevel3=?,ShippedQuantityLevel1=?,ShippedQuantityLevel2=?,ShippedQuantityLevel3=? WHERE MovementCode=? AND ItemCode=?");

				objSqliteDB.beginTransaction();
				if(hashMap != null && hashMap.size() >0)
				{
					Set<String> keys = hashMap.keySet();
					
					for (String string : keys)
					{
						LoadRequestDO loadRequestDO = hashMap.get(string);
						if(loadRequestDO != null)
						{
							stmtSelectR.bindString(1, loadRequestDO.MovementCode);
							long count = stmtSelectR.simpleQueryForLong();

							if(count > 0)
							{	
								stmtUpdateR.bindString(1, ""+loadRequestDO.PreMovementCode);
								stmtUpdateR.bindString(2, ""+loadRequestDO.AppMovementId);
								stmtUpdateR.bindString(3, ""+loadRequestDO.OrgCode);
								stmtUpdateR.bindString(4, ""+loadRequestDO.UserCode);
								stmtUpdateR.bindString(5, ""+loadRequestDO.WHKeeperCode);
								stmtUpdateR.bindString(6, ""+loadRequestDO.CurrencyCode);
								stmtUpdateR.bindString(7, ""+loadRequestDO.JourneyCode);
								stmtUpdateR.bindString(8, ""+loadRequestDO.MovementDate);
								stmtUpdateR.bindString(9, ""+loadRequestDO.MovementNote);
								stmtUpdateR.bindString(10, ""+loadRequestDO.MovementType);
								stmtUpdateR.bindString(11, ""+loadRequestDO.SourceVehicleCode);
								stmtUpdateR.bindString(12, ""+loadRequestDO.DestinationVehicleCode);
								stmtUpdateR.bindString(13, ""+loadRequestDO.Status);
								stmtUpdateR.bindString(14, ""+loadRequestDO.VisitID);

								stmtUpdateR.bindString(15, ""+loadRequestDO.MovementStatus);
								stmtUpdateR.bindString(16, ""+loadRequestDO.CreatedOn);
								stmtUpdateR.bindString(17, ""+loadRequestDO.ApproveByCode);
								stmtUpdateR.bindString(18, ""+loadRequestDO.ApprovedDate);
								stmtUpdateR.bindString(19, ""+loadRequestDO.JDETRXNumber);
								stmtUpdateR.bindString(20, ""+loadRequestDO.ISStampDate);
								stmtUpdateR.bindString(21, ""+loadRequestDO.ISFromPC);
								stmtUpdateR.bindString(22, ""+loadRequestDO.OperatorCode);

								stmtUpdateR.bindString(23, ""+loadRequestDO.IsDummyCount);
								stmtUpdateR.bindString(24, ""+loadRequestDO.Amount);
								stmtUpdateR.bindString(25, ""+loadRequestDO.ModifiedDate);
								stmtUpdateR.bindString(26, ""+loadRequestDO.ModifiedTime);
								stmtUpdateR.bindString(27, ""+loadRequestDO.PushedOn);
								stmtUpdateR.bindString(28, ""+loadRequestDO.ModifiedOn);
								stmtUpdateR.bindString(29, ""+loadRequestDO.WHCode);
								stmtUpdateR.bindString(30, ""+loadRequestDO.UserLoadType );
								stmtUpdateR.bindString(31, ""+loadRequestDO.MovementCode);
								stmtUpdateR.execute();
							}
							else
							{
								stmtInsertR.bindString(1, ""+loadRequestDO.MovementCode);
								stmtInsertR.bindString(2, ""+loadRequestDO.PreMovementCode);
								stmtInsertR.bindString(3, ""+loadRequestDO.AppMovementId);
								stmtInsertR.bindString(4, ""+loadRequestDO.OrgCode);
								stmtInsertR.bindString(5, ""+loadRequestDO.UserCode);
								stmtInsertR.bindString(6, ""+loadRequestDO.WHKeeperCode);
								stmtInsertR.bindString(7, ""+loadRequestDO.CurrencyCode);
								stmtInsertR.bindString(8, ""+loadRequestDO.JourneyCode);
								stmtInsertR.bindString(9, ""+loadRequestDO.MovementDate);
								stmtInsertR.bindString(10, ""+loadRequestDO.MovementNote);
								stmtInsertR.bindString(11, ""+loadRequestDO.MovementType);
								stmtInsertR.bindString(12, ""+loadRequestDO.SourceVehicleCode);
								stmtInsertR.bindString(13, ""+loadRequestDO.DestinationVehicleCode);
								stmtInsertR.bindString(14, ""+loadRequestDO.Status);
								stmtInsertR.bindString(15, ""+loadRequestDO.VisitID);

								stmtInsertR.bindString(16, ""+loadRequestDO.MovementStatus);
								stmtInsertR.bindString(17, ""+loadRequestDO.CreatedOn);
								stmtInsertR.bindString(18, ""+loadRequestDO.ApproveByCode);
								stmtInsertR.bindString(19, ""+loadRequestDO.ApprovedDate);
								stmtInsertR.bindString(20, ""+loadRequestDO.JDETRXNumber);
								stmtInsertR.bindString(21, ""+loadRequestDO.ISStampDate);
								stmtInsertR.bindString(22, ""+loadRequestDO.ISFromPC);
								stmtInsertR.bindString(23, ""+loadRequestDO.OperatorCode);

								stmtInsertR.bindString(24, ""+loadRequestDO.IsDummyCount);
								stmtInsertR.bindString(25, ""+loadRequestDO.Amount);
								stmtInsertR.bindString(26, ""+loadRequestDO.ModifiedDate);
								stmtInsertR.bindString(27, ""+loadRequestDO.ModifiedTime);
								stmtInsertR.bindString(28, ""+loadRequestDO.PushedOn);
								stmtInsertR.bindString(29, ""+loadRequestDO.ModifiedOn);
								stmtInsertR.bindString(30, ""+loadRequestDO.WHCode);
								stmtInsertR.bindString(31, ""+loadRequestDO.UserLoadType);
								stmtInsertR.executeInsert();
							}

							for(LoadRequestDetailDO inventoryObject : loadRequestDO.vecItems)
							{
								stmtSelectD.bindString(1, inventoryObject.MovementCode);
								stmtSelectD.bindString(2, inventoryObject.ItemCode);
								long countRec = stmtSelectD.simpleQueryForLong();

								if(countRec != 0)
								{	
									stmtUpdateD.bindString(1, ""+inventoryObject.LineNo);
									stmtUpdateD.bindString(2, inventoryObject.OrgCode);
									stmtUpdateD.bindString(3, inventoryObject.ItemDescription);
									stmtUpdateD.bindString(4, ""+inventoryObject.ItemAltDescription);
									stmtUpdateD.bindString(5, inventoryObject.MovementStatus);
									stmtUpdateD.bindString(6, inventoryObject.UOM);
									stmtUpdateD.bindString(7, ""+inventoryObject.QuantityLevel1);
									stmtUpdateD.bindString(8, ""+inventoryObject.QuantityLevel2);
									stmtUpdateD.bindString(9, ""+inventoryObject.QuantityLevel3);
									stmtUpdateD.bindString(10, ""+inventoryObject.QuantityBU);
									stmtUpdateD.bindString(11, ""+inventoryObject.QuantitySU);
									stmtUpdateD.bindString(12, ""+inventoryObject.NonSellableQty);

									stmtUpdateD.bindString(13, ""+inventoryObject.CurrencyCode);
									stmtUpdateD.bindString(14, ""+inventoryObject.PriceLevel1);
									stmtUpdateD.bindString(15, ""+inventoryObject.PriceLevel2);
									stmtUpdateD.bindString(16, ""+inventoryObject.PriceLevel3);
									stmtUpdateD.bindString(17, inventoryObject.MovementReasonCode);
									stmtUpdateD.bindString(18, inventoryObject.ExpiryDate);
									stmtUpdateD.bindString(19, ""+inventoryObject.Note);
									stmtUpdateD.bindString(20, inventoryObject.AffectedStock);
									stmtUpdateD.bindString(21, loadRequestDO.Status);

									stmtUpdateD.bindString(22, ""+inventoryObject.DistributionCode);
									stmtUpdateD.bindString(23, inventoryObject.CreatedOn);
									stmtUpdateD.bindString(24, loadRequestDO.ModifiedDate);
									stmtUpdateD.bindString(25, ""+inventoryObject.ModifiedTime);
									stmtUpdateD.bindString(26, inventoryObject.PushedOn);
									stmtUpdateD.bindString(27, ""+inventoryObject.CancelledQuantity);

									stmtUpdateD.bindString(28, ""+inventoryObject.InProcessQuantity);
									stmtUpdateD.bindString(29, ""+inventoryObject.ShippedQuantity);
									stmtUpdateD.bindString(30, loadRequestDO.ModifiedOn);

									stmtUpdateD.bindLong(31, inventoryObject.inProcessQuantityLevel1);
									stmtUpdateD.bindLong(32, inventoryObject.inProcessQuantityLevel2);
									stmtUpdateD.bindLong(33, inventoryObject.inProcessQuantityLevel3);

									stmtUpdateD.bindLong(34, inventoryObject.shippedQuantityLevel1);
									stmtUpdateD.bindLong(35, inventoryObject.shippedQuantityLevel2);
									stmtUpdateD.bindLong(36, inventoryObject.shippedQuantityLevel3);


									stmtUpdateD.bindString(37, loadRequestDO.MovementCode);
									stmtUpdateD.bindString(38, inventoryObject.ItemCode);
									stmtUpdateD.execute();
								}
								else
								{
									stmtInsertD.bindString(1, ""+inventoryObject.LineNo);
									stmtInsertD.bindString(2, loadRequestDO.MovementCode);
									stmtInsertD.bindString(3, inventoryObject.ItemCode);
									stmtInsertD.bindString(4, inventoryObject.OrgCode);
									stmtInsertD.bindString(5, inventoryObject.ItemDescription);
									stmtInsertD.bindString(6, ""+inventoryObject.ItemAltDescription);
									stmtInsertD.bindString(7, inventoryObject.MovementStatus);
									stmtInsertD.bindString(8, inventoryObject.UOM);
									stmtInsertD.bindString(9, ""+inventoryObject.QuantityLevel1);
									stmtInsertD.bindString(10, ""+inventoryObject.QuantityLevel2);
									stmtInsertD.bindString(11, ""+inventoryObject.QuantityLevel3);
									stmtInsertD.bindString(12, ""+inventoryObject.QuantityBU);
									stmtInsertD.bindString(13, ""+inventoryObject.QuantitySU);
									stmtInsertD.bindString(14, ""+inventoryObject.NonSellableQty);
									stmtInsertD.bindString(15, ""+inventoryObject.CurrencyCode);
									stmtInsertD.bindString(16, ""+inventoryObject.PriceLevel1);
									stmtInsertD.bindString(17, ""+inventoryObject.PriceLevel2);
									stmtInsertD.bindString(18, ""+inventoryObject.PriceLevel3);
									stmtInsertD.bindString(19, inventoryObject.MovementReasonCode);
									stmtInsertD.bindString(20, inventoryObject.ExpiryDate);
									stmtInsertD.bindString(21, ""+inventoryObject.Note);
									stmtInsertD.bindString(22, inventoryObject.AffectedStock);
									stmtInsertD.bindString(23, loadRequestDO.Status);

									stmtInsertD.bindString(24, ""+inventoryObject.DistributionCode);
									stmtInsertD.bindString(25, inventoryObject.CreatedOn);
									stmtInsertD.bindString(26, loadRequestDO.ModifiedDate);
									stmtInsertD.bindString(27, ""+inventoryObject.ModifiedTime);
									stmtInsertD.bindString(28, inventoryObject.PushedOn);
									stmtInsertD.bindString(29, ""+inventoryObject.CancelledQuantity);

									stmtInsertD.bindString(30, ""+inventoryObject.InProcessQuantity);
									stmtInsertD.bindString(31, ""+inventoryObject.ShippedQuantity);
									stmtInsertD.bindString(32, loadRequestDO.ModifiedOn);

									stmtInsertD.bindLong(33, inventoryObject.inProcessQuantityLevel1);
									stmtInsertD.bindLong(34, inventoryObject.inProcessQuantityLevel2);
									stmtInsertD.bindLong(35, inventoryObject.inProcessQuantityLevel3);

									stmtInsertD.bindLong(36, inventoryObject.shippedQuantityLevel1);
									stmtInsertD.bindLong(37, inventoryObject.shippedQuantityLevel2);
									stmtInsertD.bindLong(38, inventoryObject.shippedQuantityLevel3);

									stmtInsertD.executeInsert();
								}
							}
						}
					}
					objSqliteDB.setTransactionSuccessful();
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();

				stmtSelectD.close();
				stmtInsertD.close();
				stmtUpdateD.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
				
			}
			finally
			{
				if(objSqliteDB!=null)
				{					
					objSqliteDB.endTransaction();
					objSqliteDB.close();
				}
			}
			return result;
		}
	}

	public boolean insertVanLog(Vector<VanLogDO> vecVanLODos)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{

				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVanStockLog WHERE VanStockLogId = ?");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblVanStockLog " +
						"(VanStockLogId, OrgCode, TrxCode, TrxType, ItemCode, UserCode, JDETrxCode, QuantityEach, DistributionCode, ModifiedDate, ModifiedTime, BatchNumber, ExpiryDate, VehicleCode) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblVanStockLog SET OrgCode =?, TrxCode =?, TrxType =?, ItemCode =?, UserCode =?, JDETrxCode =?, QuantityEach =?, DistributionCode =?, ModifiedDate =?, ModifiedTime =?, BatchNumber =?, ExpiryDate =?, VehicleCode =? WHERE VanStockLogId = ?");

				if(vecVanLODos != null && vecVanLODos.size() >0)
				{
					for (VanLogDO vanLogDO : vecVanLODos)
					{
						stmtSelectR.bindString(1, vanLogDO.VanStockLogId);
						long count = stmtSelectR.simpleQueryForLong();

						if(count > 0)
						{	
							stmtUpdateR.bindString(1, ""+vanLogDO.OrgCode);
							stmtUpdateR.bindString(2, ""+vanLogDO.TrxCode);
							stmtUpdateR.bindString(3, ""+vanLogDO.TrxType);
							stmtUpdateR.bindString(4, ""+vanLogDO.ItemCode);
							stmtUpdateR.bindString(5, ""+vanLogDO.UserCode);
							stmtUpdateR.bindString(6, ""+vanLogDO.JDETrxCode);
							stmtUpdateR.bindString(7, ""+vanLogDO.QuantityEach);
							stmtUpdateR.bindString(8, ""+vanLogDO.DistributionCode);
							stmtUpdateR.bindString(9, ""+vanLogDO.ModifiedDate);
							stmtUpdateR.bindString(10, ""+vanLogDO.ModifiedTime);
							stmtUpdateR.bindString(11, ""+vanLogDO.BatchNumber);
							stmtUpdateR.bindString(12, ""+vanLogDO.ExpiryDate);
							stmtUpdateR.bindString(13, ""+vanLogDO.VehicleCode);
							stmtUpdateR.bindString(14, ""+vanLogDO.VanStockLogId);
							stmtUpdateR.execute();
						}
						else
						{
							stmtInsertR.bindString(1, ""+vanLogDO.VanStockLogId);
							stmtInsertR.bindString(2, ""+vanLogDO.OrgCode);
							stmtInsertR.bindString(3, ""+vanLogDO.TrxCode);
							stmtInsertR.bindString(4, ""+vanLogDO.TrxType);
							stmtInsertR.bindString(5, ""+vanLogDO.ItemCode);
							stmtInsertR.bindString(6, ""+vanLogDO.UserCode);
							stmtInsertR.bindString(7, ""+vanLogDO.JDETrxCode);
							stmtInsertR.bindString(8, ""+vanLogDO.QuantityEach);
							stmtInsertR.bindString(9, ""+vanLogDO.DistributionCode);
							stmtInsertR.bindString(10, ""+vanLogDO.ModifiedDate);
							stmtInsertR.bindString(11, ""+vanLogDO.ModifiedTime);
							stmtInsertR.bindString(12, ""+vanLogDO.BatchNumber);
							stmtInsertR.bindString(13, ""+vanLogDO.ExpiryDate);
							stmtInsertR.bindString(14, ""+vanLogDO.VehicleCode);
							stmtInsertR.executeInsert();
						}
					}
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean insertVanLoad(Vector<VanStockDO> vecVanStockDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				Cursor cursor 					= 	null;
				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtSelectVanStockLog 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVanStockLog WHERE VanStockLogId = ?  COLLATE NOCASE");
				SQLiteStatement stmtSelectR 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVanStock WHERE ItemCode = ?  COLLATE NOCASE");
				SQLiteStatement stmtInsertR 	= 	objSqliteDB.compileStatement("INSERT INTO tblVanStock " +
						"(VanStockId, OrgCode, UserCode, ItemCode, SellableQuantity, ReturnedQuantity, TotalQuantity, BatchNumber, ExpiryDate) " +
						"VALUES(?,?,?,?,?,?,?,?,?)");

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblVanStock SET SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?, ExpiryDate=? WHERE ItemCode = ?  COLLATE NOCASE");

				SQLiteStatement stmtTrxDetail		= 	objSqliteDB.compileStatement("UPDATE tblTrxDetail SET ItemType = ? WHERE ItemCode = ? and TrxCode = ?  COLLATE NOCASE");


				SQLiteStatement stmtInsertRLog 	= 	objSqliteDB.compileStatement("INSERT INTO tblVanStockLog " +
						"(VanStockLogId, OrgCode,TrxCode,TrxType,ItemCode,UserCode,QuantityEach,BatchNumber,ExpiryDate,TrxDate) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");



				if(vecVanStockDOs != null && vecVanStockDOs.size() >0)
				{
					for (VanStockDO vanStockDO : vecVanStockDOs)
					{
						stmtSelectVanStockLog.bindString(1, vanStockDO.VanStockId);
						long vanstocLogkCount = stmtSelectVanStockLog.simpleQueryForLong();
						if(vanstocLogkCount==0){
							LogUtils.debug("vanstocLogkCountInsert", ""+vanstocLogkCount+" "+vanStockDO.VanStockId);
							stmtSelectR.bindString(1, vanStockDO.ItemCode);

							long count = stmtSelectR.simpleQueryForLong();

							if(count > 0)
							{	
								String strQuery 	= 	"SELECT SellableQuantity, ReturnedQuantity, TotalQuantity From tblVanStock where ItemCode = '"+vanStockDO.ItemCode+"' COLLATE NOCASE";
								cursor 	= objSqliteDB.rawQuery(strQuery, null);
								if(cursor.moveToFirst())
								{
									int sellebaleQty  = cursor.getInt(0);
									int returnQty 	  = cursor.getInt(1);
									int totalQty      = cursor.getInt(2);

									if(vanStockDO.TrxType.equalsIgnoreCase(AppConstants.UNLOAD_RVU))
									{
										returnQty = returnQty+vanStockDO.QuantityEach;
										if(returnQty<0){
											sellebaleQty = sellebaleQty + returnQty;
											returnQty=0;
										}

										totalQty	 = totalQty 	+ vanStockDO.QuantityEach;
									}
									else if(vanStockDO.TrxType.equalsIgnoreCase(AppConstants.GRVD))
									{
										returnQty = returnQty+vanStockDO.QuantityEach;
										totalQty	 = totalQty 	+ vanStockDO.QuantityEach;
									}
									else if(vanStockDO.TrxType.equalsIgnoreCase(AppConstants.GRVG))
									{
										sellebaleQty = sellebaleQty + vanStockDO.QuantityEach;
										totalQty	 = totalQty 	+ vanStockDO.QuantityEach;
										stmtTrxDetail.bindString(1, TrxDetailsDO.get_TRX_ITEM_SELLABLE());
										stmtTrxDetail.bindString(2, vanStockDO.ItemCode);
										stmtTrxDetail.bindString(3, vanStockDO.TrxCode);
										stmtTrxDetail.execute();
									}
									else
									{
										sellebaleQty = sellebaleQty + vanStockDO.QuantityEach;
										totalQty	 = totalQty 	+ vanStockDO.QuantityEach;
									}

									stmtUpdateR.bindLong(1, sellebaleQty);
									stmtUpdateR.bindLong(2, returnQty);
									stmtUpdateR.bindLong(3, totalQty);

								}
								if(cursor != null && !cursor.isClosed())
									cursor.close();

								stmtUpdateR.bindString(4, vanStockDO.ExpiryDate);
								stmtUpdateR.bindString(5, vanStockDO.ItemCode);
								stmtUpdateR.execute();
							}
							else
							{
								stmtInsertR.bindString(1, ""+vanStockDO.VanStockId);
								stmtInsertR.bindString(2, ""+vanStockDO.OrgCode);
								stmtInsertR.bindString(3, ""+vanStockDO.UserCode);
								stmtInsertR.bindString(4, ""+vanStockDO.ItemCode);
								stmtInsertR.bindString(5, ""+vanStockDO.QuantityEach);
								stmtInsertR.bindString(6, "0");
								stmtInsertR.bindString(7, ""+vanStockDO.QuantityEach);
								stmtInsertR.bindString(8, ""+vanStockDO.BatchNumber);
								stmtInsertR.bindString(9, ""+vanStockDO.ExpiryDate);
								stmtInsertR.executeInsert();
							}

							stmtInsertRLog.bindString(1, ""+vanStockDO.VanStockId);
							stmtInsertRLog.bindString(2, ""+vanStockDO.OrgCode);
							stmtInsertRLog.bindString(3, ""+vanStockDO.TrxCode);
							stmtInsertRLog.bindString(4, ""+vanStockDO.TrxType);
							stmtInsertRLog.bindString(5, ""+vanStockDO.ItemCode);
							stmtInsertRLog.bindString(6, ""+vanStockDO.UserCode);
							stmtInsertRLog.bindString(7, ""+vanStockDO.QuantityEach);
							stmtInsertRLog.bindString(8, ""+vanStockDO.BatchNumber);
							stmtInsertRLog.bindString(9, ""+vanStockDO.ExpiryDate);
							stmtInsertRLog.bindString(10, ""+vanStockDO.TrxDate);

							stmtInsertRLog.executeInsert();
						}

					}
				}

				stmtSelectR.close();
				stmtInsertR.close();
				stmtUpdateR.close();
				stmtInsertRLog.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public boolean updateVanLoad(Vector<VanStockDO> vecVanStockDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				Cursor cursor 					= 	null;
				Cursor cursorUOM				= 	null;
				Cursor cursorMovement			= 	null;
				objSqliteDB 					= 	DatabaseHelper.openDataBase();

				SQLiteStatement stmtUpdateR		= 	objSqliteDB.compileStatement("UPDATE tblVanStock SET SellableQuantity = ?, ReturnedQuantity = ?, TotalQuantity = ?, ExpiryDate=? WHERE ItemCode = ?  COLLATE NOCASE");

				if(vecVanStockDOs != null && vecVanStockDOs.size() >0)
				{
					for (VanStockDO vanStockDO : vecVanStockDOs)
					{
						String queryMovementcoll = "SELECT COUNT(*) FROM tblMovementHeader WHERE MovementStatus != '100' " +
								"AND MovementCode ='"+vanStockDO.TrxCode+"'";
						cursorMovement = objSqliteDB.rawQuery(queryMovementcoll, null);
						if(cursorMovement != null && cursorMovement.moveToFirst())
						{
							if(cursorMovement.getInt(0) > 0)
							{
								String strQuery 	= 	"SELECT SellableQuantity, ReturnedQuantity, TotalQuantity From tblVanStock where ItemCode = '"+vanStockDO.ItemCode+"' COLLATE NOCASE";
								cursor 	= objSqliteDB.rawQuery(strQuery, null);
								if(cursor.moveToFirst())
								{
									int sellebaleQty  = cursor.getInt(0);
									int returnQty 	  = cursor.getInt(1);
									int totalQty      = cursor.getInt(2);

									String queryUOM = "SELECT EAConversion FROM tblUOMFactor WHERE ItemCode = '"+vanStockDO.ItemCode+"' AND UOM = 'UNIT'";
									int eaConversion = 1;
									cursorUOM = objSqliteDB.rawQuery(queryUOM, null);
									if(cursorUOM != null && cursorUOM.moveToFirst())
									{
										eaConversion = cursorUOM.getInt(0);
									}
									sellebaleQty = sellebaleQty - (vanStockDO.QuantityEach*eaConversion);
									totalQty	 = totalQty 	- (vanStockDO.QuantityEach*eaConversion);

									stmtUpdateR.bindLong(1, sellebaleQty);
									stmtUpdateR.bindLong(2, returnQty);
									stmtUpdateR.bindLong(3, totalQty);

									stmtUpdateR.bindString(4, vanStockDO.ExpiryDate);
									stmtUpdateR.bindString(5, vanStockDO.ItemCode);
									stmtUpdateR.execute();
								}
							}
						}
						if(cursorUOM != null && !cursorUOM.isClosed())
							cursorUOM.close();
						if(cursorMovement != null && !cursorMovement.isClosed())
							cursorMovement.close();
						if(cursor != null && !cursor.isClosed())
							cursor.close();

					}
				}

				stmtUpdateR.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public String getMovementId(String salesmanCode, int division)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase objSqliteDB = null;
			String movementId = "";
			Cursor cursor = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String type 	=	"";
				if(division <= 0)
					type = AppConstants.Movement;
				else if(division ==1)
					type = AppConstants.Food_Movement;
				else
					type = AppConstants.TPT_Movement;

				String query = "SELECT id from tblOfflineData where Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT MovementCode FROM tblMovementHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					movementId = cursor.getString(0);
					if(movementId != null && !movementId.equals(""))
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+movementId+"'");
				}

				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				return movementId;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(objSqliteDB != null)
					objSqliteDB.close();
			}	
		}

	}
	public ArrayList<LoadRequestDO> getAllLoadRequestToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<LoadRequestDO> vecLoad = new ArrayList<LoadRequestDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = 'N' AND UserLoadType != 4";
				cursor = sLiteDatabase.rawQuery(strQuery, null);
				if(cursor != null)
				{
					if(cursor.moveToFirst())
					{
						do
						{
							LoadRequestDO OrderDetail		= new LoadRequestDO();
							OrderDetail.MovementCode		= 	cursor.getString(0);
							OrderDetail.PreMovementCode		= 	cursor.getString(1);
							OrderDetail.AppMovementId 		= 	cursor.getString(2);
							OrderDetail.OrgCode				= 	cursor.getString(3);
							OrderDetail.UserCode			= 	cursor.getString(4);
							OrderDetail.WHKeeperCode		= 	cursor.getString(5);
							OrderDetail.CurrencyCode		= 	cursor.getString(6);
							OrderDetail.JourneyCode 		=   cursor.getString(7);

							OrderDetail.MovementDate		= 	cursor.getString(8);
							OrderDetail.MovementNote		= 	cursor.getString(9);
							OrderDetail.MovementType 		= 	cursor.getString(10);
							OrderDetail.SourceVehicleCode	= 	cursor.getString(11);
							OrderDetail.DestinationVehicleCode= 	cursor.getString(12);
							OrderDetail.Status				= 	cursor.getString(13);
							OrderDetail.VisitID				= 	cursor.getString(14);
							OrderDetail.MovementStatus		= 	cursor.getString(15);
							OrderDetail.CreatedOn 			=   cursor.getString(16);

							OrderDetail.ApproveByCode		= 	cursor.getString(17);
							OrderDetail.ApprovedDate		= 	cursor.getString(18);
							OrderDetail.JDETRXNumber 		= 	cursor.getString(19);
							OrderDetail.ISStampDate			= 	cursor.getString(20);
							OrderDetail.ISFromPC			= 	cursor.getString(21);
							OrderDetail.OperatorCode		= 	cursor.getString(22);
							OrderDetail.IsDummyCount		= 	cursor.getString(23);
							OrderDetail.Amount 				=   cursor.getFloat(24);

							OrderDetail.ModifiedDate		= 	cursor.getString(25);
							OrderDetail.ModifiedTime		= 	cursor.getString(26);
							OrderDetail.PushedOn 			=   cursor.getString(27);
							OrderDetail.ModifiedOn 			=   cursor.getString(28);
							OrderDetail.ProductType			=   cursor.getString(29);
							OrderDetail.WHCode				=   cursor.getString(31);
							OrderDetail.Division			=   StringUtils.getInt(cursor.getString(33));

							String strDetailQuery 			=   "SELECT * FROM tblMovementDetail WHERE MovementCode = '"+OrderDetail.MovementCode+"'";

							cursorDetail = sLiteDatabase.rawQuery(strDetailQuery, null);
							if(cursorDetail != null)
							{
								if(cursorDetail.moveToFirst())
								{
									do
									{
										LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
										oRequestDetailDO.LineNo			=	cursorDetail.getString(0);
										oRequestDetailDO.MovementCode	=	cursorDetail.getString(1);
										oRequestDetailDO.ItemCode		=	cursorDetail.getString(2);
										oRequestDetailDO.OrgCode		=	cursorDetail.getString(3);
										oRequestDetailDO.ItemDescription=	cursorDetail.getString(4);
										oRequestDetailDO.ItemAltDescription=	cursorDetail.getString(5);
										oRequestDetailDO.MovementStatus	=	cursorDetail.getString(6);
										oRequestDetailDO.UOM			=	cursorDetail.getString(7);
										oRequestDetailDO.QuantityLevel1	=	(int) cursorDetail.getFloat(8);
										oRequestDetailDO.QuantityLevel2	=	(int) cursorDetail.getFloat(9);
										oRequestDetailDO.QuantityLevel3	=	(int) cursorDetail.getFloat(10);
										oRequestDetailDO.QuantityBU		=	(int) cursorDetail.getFloat(11);
										oRequestDetailDO.QuantitySU		=	(int) cursorDetail.getFloat(12);
										oRequestDetailDO.NonSellableQty	=	(int) cursorDetail.getFloat(13);
										oRequestDetailDO.CurrencyCode	=	cursorDetail.getString(14);
										oRequestDetailDO.PriceLevel1	=	cursorDetail.getFloat(15);
										oRequestDetailDO.PriceLevel2	=	cursorDetail.getFloat(16);
										oRequestDetailDO.PriceLevel3	=	cursorDetail.getFloat(17);
										oRequestDetailDO.MovementReasonCode=	cursorDetail.getString(18);
										oRequestDetailDO.ExpiryDate		=	cursorDetail.getString(19);
										oRequestDetailDO.Note			=	cursorDetail.getString(20);
										oRequestDetailDO.AffectedStock	=	cursorDetail.getString(21);
										oRequestDetailDO.Status			=	cursorDetail.getString(22);
										oRequestDetailDO.DistributionCode=	cursorDetail.getString(23);
										oRequestDetailDO.CreatedOn		=	cursorDetail.getString(24);
										oRequestDetailDO.ModifiedDate	=	cursorDetail.getString(25);
										oRequestDetailDO.ModifiedTime	=	cursorDetail.getString(26);
										oRequestDetailDO.PushedOn		=	cursorDetail.getString(27);
										oRequestDetailDO.CancelledQuantity=	(int) cursorDetail.getFloat(28);
										oRequestDetailDO.InProcessQuantity=	(int) cursorDetail.getFloat(29);
										oRequestDetailDO.ShippedQuantity=	(int) cursorDetail.getFloat(30);
										oRequestDetailDO.ModifiedOn		=	cursorDetail.getString(31);

										oRequestDetailDO.inProcessQuantityLevel1	=	 cursorDetail.getInt(33);
										oRequestDetailDO.inProcessQuantityLevel2	=	cursorDetail.getInt(34);
										oRequestDetailDO.inProcessQuantityLevel3	=	cursorDetail.getInt(35);

										oRequestDetailDO.shippedQuantityLevel1	=	cursorDetail.getInt(36);
										oRequestDetailDO.shippedQuantityLevel2	=	cursorDetail.getInt(37);
										oRequestDetailDO.shippedQuantityLevel3	=	cursorDetail.getInt(38);

										OrderDetail.vecItems.add(oRequestDetailDO);
									}
									while(cursorDetail.moveToNext());
								}
								if(cursorDetail!=null && !cursorDetail.isClosed())
									cursorDetail.close();
							}
							if(OrderDetail.vecItems != null && OrderDetail.vecItems.size() > 0)
								vecLoad.add(OrderDetail);
						}
						while(cursor.moveToNext());
					}
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
				if(sLiteDatabase!=null)
					sLiteDatabase.close();
			}

			return vecLoad;
		}
	}

	public boolean updateStatus(Vector<NameIDDo> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				String strUpdate = "UPDATE tblMovementHeader SET Status = ? WHERE MovementCode=?";
				SQLiteStatement stmtUpdateCustomer		= objSqliteDB.compileStatement(strUpdate);

				for (NameIDDo object : vec)
				{
					stmtUpdateCustomer.bindString(1, "1");
					stmtUpdateCustomer.bindString(2, object.strId);
					stmtUpdateCustomer.execute();
				}
				stmtUpdateCustomer.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public boolean updateMovemetStatuStatus(String movementId, int Status, ArrayList<VanLoadDO> vecVanLodDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				String strUpdate = "UPDATE tblMovementHeader SET MovementStatus = ? WHERE MovementCode=?";
				String strUpdateMovementDetail = "UPDATE tblMovementDetail SET ShippedQuantity = ?,ShippedQuantityLevel1 =?,ShippedQuantityLevel3=? WHERE MovementCode=? and ItemCode=?";
				SQLiteStatement stmtUpdateMovement		= objSqliteDB.compileStatement(strUpdate);
				stmtUpdateMovement.bindString(1, Status+"");
				stmtUpdateMovement.bindString(2, movementId);
				stmtUpdateMovement.execute();
				SQLiteStatement stmtUpdateMovementDetal	= objSqliteDB.compileStatement(strUpdateMovementDetail);
				for(VanLoadDO vanLoadDO:vecVanLodDOs){
					stmtUpdateMovementDetal.bindDouble(1, vanLoadDO.SellableQuantity);
					stmtUpdateMovementDetal.bindLong(2, vanLoadDO.shippedQuantityLevel1);
					stmtUpdateMovementDetal.bindLong(3, vanLoadDO.shippedQuantityLevel3);
					stmtUpdateMovementDetal.bindString(4, movementId);
					stmtUpdateMovementDetal.bindString(5, vanLoadDO.ItemCode);
					stmtUpdateMovementDetal.execute();
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
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public boolean updateQty(Vector<NameIDDo> vec)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();

				String strUpdateHeader = "UPDATE tblMovementHeader SET MovementStatus=? WHERE MovementCode=?";
				String strUpdate = "UPDATE tblMovementDetail SET QuantityLevel1 = ?, ShippedQuantity=?,MovementStatus=? WHERE MovementCode=? AND ItemCode=?";
				SQLiteStatement stmtUpdateDetail		= objSqliteDB.compileStatement(strUpdate);
				SQLiteStatement stmtUpdateHeader		= objSqliteDB.compileStatement(strUpdateHeader);

				if(vec != null && vec.size() > 0)
				{
					stmtUpdateHeader.bindString(1, "Approved");
					stmtUpdateHeader.bindString(2, vec.get(0).strId);
					stmtUpdateHeader.execute();
				}

				for (NameIDDo object : vec)
				{
					stmtUpdateDetail.bindString(1, object.strType);
					stmtUpdateDetail.bindString(2, object.strType);
					stmtUpdateDetail.bindString(3, "Approved");
					stmtUpdateDetail.bindString(4, object.strId);
					stmtUpdateDetail.bindString(5, object.strName);
					stmtUpdateDetail.execute();
				}
				stmtUpdateDetail.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}	
	public void insertUpdatetblDetailStock(Vector<VanStockDO> vecStockDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblDetailedStock (ItemCode, OpeningQty, LoadedQty) VALUES(?,?,?)");
				SQLiteStatement stmtUpdateU 	= 	objSqliteDB.compileStatement("UPDATE tblDetailedStock SET OpeningQty = ?, LoadedQty=?  WHERE ItemCode = ?");

				for(VanStockDO objVanStockDO : vecStockDo)
				{

					stmtUpdateU.bindLong(1, objVanStockDO.OpeningQTY);	
					stmtUpdateU.bindLong(2, objVanStockDO.LoadedQty);	
					stmtUpdateU.bindString(3, objVanStockDO.ItemCode);	

					if(stmtUpdateU.executeUpdateDelete()<=0){
						stmtInsert.bindString(1, objVanStockDO.ItemCode);
						stmtInsert.bindLong(2, objVanStockDO.OpeningQTY);
						stmtInsert.bindLong(3, objVanStockDO.LoadedQty);
						stmtInsert.executeInsert();
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
}
