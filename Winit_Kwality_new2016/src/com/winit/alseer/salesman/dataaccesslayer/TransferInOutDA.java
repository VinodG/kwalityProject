package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.LoadRequestDO;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.TransferDetailDO;
import com.winit.alseer.salesman.dataobject.TransferInoutDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class TransferInOutDA
{
	public String insertTransferInOut(ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem, String salemanCode, String fromEmpNo, String toEmpNo, String transferType, String transferStatus, String sourceVNO, String destVno, String date, String transferID, String mDestOrderId)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			String orderId = "";
			Cursor cursor = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "SELECT id from tblOfflineData where SalesmanCode ='"+salemanCode+"' AND Type ='"+AppConstants.Order+"' AND status = 0 AND id NOT IN(SELECT OrderId FROM tblOrderHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					orderId = cursor.getString(0);
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(orderId != null && !orderId.equalsIgnoreCase(""))
				{
					objSqliteDB.execSQL("UPDATE tblOfflineData SET status = 1 WHERE Id='"+orderId+"'");
					
					String sourceOrderNo = "";
					String destOrderNo = "";
					
					if(transferType.equalsIgnoreCase(AppConstants.TRNS_TYPE_OUT))
					{
						sourceOrderNo = orderId+"";
						destOrderNo = orderId+"";
					}
					else
					{
						sourceOrderNo = orderId+"";
						destOrderNo = mDestOrderId+"";
					}
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferInOut (InventoryUID, fromEmpNo, toEmpNo, trnsferType, transferStatus, sourceVNO, destVNO, date, sourceOrdeNumber, destOrdeNumber) VALUES(?,?,?,?,?,?,?,?,?,?)");
					String inventoryId = transferID;
					
					stmtInsert.bindString(1,inventoryId);
					stmtInsert.bindString(2,fromEmpNo);
					stmtInsert.bindString(3,toEmpNo);
					stmtInsert.bindString(4,transferType);
					stmtInsert.bindString(5,transferStatus);
					stmtInsert.bindString(6,sourceVNO);
					stmtInsert.bindString(7,destVno);
					stmtInsert.bindString(8,date);
					stmtInsert.bindString(9,sourceOrderNo);
					stmtInsert.bindString(10,destOrderNo);
					stmtInsert.executeInsert();
					insertTransferInOutDetails(objSqliteDB, vecModifiedItem, sourceOrderNo);
					if(transferType.equalsIgnoreCase(AppConstants.TRNS_TYPE_IN))
					{
						updateInventoryInStatus(objSqliteDB, vecModifiedItem, fromEmpNo, inventoryId, transferType);
						deleteDetailsFromList(inventoryId);
						deleteFromList(inventoryId);
					}
					else
						updateInventoryInStatus(objSqliteDB, vecModifiedItem, toEmpNo, inventoryId, transferType);
					
					stmtInsert.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			
			return orderId;
		}
	}
	
	private void deleteFromList(String transferID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblTransferInOutNew Where InventoryUID = '"+transferID+"'");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
		}
	}
	
	private void deleteDetailsFromList(String transferID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblTransferedInventoryNew Where InventoryUID = '"+transferID+"'");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean insertTransferInOutNew(Vector<TransferInoutDO> transferInoutDOs)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferInOut (InventoryUID, fromEmpNo, toEmpNo, trnsferType, transferStatus, sourceVNO, destVNO, date, sourceOrdeNumber, destOrdeNumber,MovementType,MovementStatus,MovementCode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
				for (TransferInoutDO transferInoutDO : transferInoutDOs) 
				{
					String inventoryId = transferInoutDO.InventoryUID;
					stmtInsert.bindString(1,inventoryId);
					stmtInsert.bindString(2,transferInoutDO.fromEmpNo);
					stmtInsert.bindString(3,transferInoutDO.toEmpNo);
					stmtInsert.bindString(4,transferInoutDO.trnsferType);
					stmtInsert.bindString(5,transferInoutDO.transferStatus);
					stmtInsert.bindString(6,transferInoutDO.sourceVNO);
					stmtInsert.bindString(7,transferInoutDO.destVNO);
					stmtInsert.bindString(8,transferInoutDO.Date);
					stmtInsert.bindString(9,transferInoutDO.sourceOrderID);
					stmtInsert.bindString(10,transferInoutDO.destOrderID);
					
					stmtInsert.bindLong(11,transferInoutDO.MovementType);
					stmtInsert.bindLong(12,transferInoutDO.MovementStatus);
					stmtInsert.bindString(13,transferInoutDO.MovementCode);
					
					stmtInsert.executeInsert();
//					insertTransferInOutDetails(objSqliteDB, transferInoutDO.vecOrderDetailDcos, inventoryId);
				}				
				stmtInsert.close();
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
			
			return true;
		}
	}
	
	public boolean insertTransferInOutDetailsNew(Vector<TransferDetailDO> vecTransferDetailDOs)
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferedInventoryNew (InventoryUID, itemCode, itemDescription, cases,units,totalCases, requestedTotaCases, transferDetailId) VALUES(?,?,?,?,?,?,?,?)");
			
				for (TransferDetailDO transferDetailDO : vecTransferDetailDOs) 
				{
					stmtInsert.bindString(1,transferDetailDO.InventoryUID);
					stmtInsert.bindString(2, transferDetailDO.itemCode);
					stmtInsert.bindString(3, getPoroductNameByID(objSqliteDB, transferDetailDO.itemCode));
					stmtInsert.bindString(4, transferDetailDO.cases+"");
					stmtInsert.bindString(5, transferDetailDO.units+"");
					transferDetailDO.totalCases = (StringUtils.getFloat(transferDetailDO.cases) + StringUtils.getFloat(transferDetailDO.units)/getUniPerCasesBySKU(objSqliteDB, transferDetailDO.itemCode))+"";
					stmtInsert.bindString(6, transferDetailDO.totalCases+"");
					stmtInsert.bindString(7, "");
					stmtInsert.bindString(8, transferDetailDO.transferDetailID);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
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
			return true;
		}
	}
	
	
	public String getPoroductNameByID(SQLiteDatabase slDatabase, String SKU)
	{
		
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			String str = "";
			try
			{
				if(slDatabase == null)
					slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("Select Description from tblProducts where SKU = '"+SKU+"'", null);
				if(cursor.moveToFirst())
				{
					str = cursor.getString(0);
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return str;
		}
	}
	
	public void insertTransferInOutDetails(SQLiteDatabase objSqliteDB, ArrayList<DeliveryAgentOrderDetailDco> vecModifiedItem, String InventoryUID)
	{
		synchronized (MyApplication.MyLock)
		{
			try
			{
				//Opening the database
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblTransferedInventory (InventoryUID, itemCode, itemDescription, cases,units,totalCases, requestedTotaCases, transferDetailId) VALUES(?,?,?,?,?,?,?,?)");
				for (DeliveryAgentOrderDetailDco deliveryAgentOrderDetailDco : vecModifiedItem) 
				{
					stmtInsert.bindString(1,InventoryUID);
					stmtInsert.bindString(2, deliveryAgentOrderDetailDco.itemCode);
					stmtInsert.bindString(3, deliveryAgentOrderDetailDco.itemDescription);
					stmtInsert.bindString(4, deliveryAgentOrderDetailDco.preCases+"");
					stmtInsert.bindString(5, deliveryAgentOrderDetailDco.preUnits+"");
					stmtInsert.bindString(6, deliveryAgentOrderDetailDco.totalCases+"");
					stmtInsert.bindString(7, "");
					stmtInsert.bindString(8, deliveryAgentOrderDetailDco.transferDetailID);
					stmtInsert.executeInsert();
				}
				stmtInsert.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean updateInventoryInStatus(SQLiteDatabase objSqliteDB, ArrayList<DeliveryAgentOrderDetailDco> vecProductDO, String empNo, String inventoryId, String type)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor 			   = 	null;
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB 					= 	DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 		= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblVMSalesmanInventory WHERE ItemCode = ?");
//				String strUpdate 					= 	"Update tblVMSalesmanInventory  set availQty=?,PrimaryQuantity=? where ItemCode = ?";
				String strUpdate 					= 	"Update tblVMSalesmanInventory  set totalQty=?,availQty=?,PrimaryQuantity=?, SecondaryQuantity=? where ItemCode = ?";
				
				SQLiteStatement stmtUpdateQty		= 	objSqliteDB.compileStatement(strUpdate);
				SQLiteStatement stmtInsert 			= 	objSqliteDB.compileStatement("INSERT INTO tblVMSalesmanInventory (VMSalesmanInventoryId, Date, SalesmanCode, ItemCode, PrimaryQuantity, SecondaryQuantity,IsAllVerified, availQty,totalQty, uploadStatus) VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(vecProductDO != null && vecProductDO.size() > 0)
				{
					for(DeliveryAgentOrderDetailDco productDO : vecProductDO)
					{
						stmtSelectRec.bindString(1, productDO.itemCode);
						long count = stmtSelectRec.simpleQueryForLong();
						
						if(count > 0)
						{
							String strQuery 	= 	"SELECT totalQty,availQty, PrimaryQuantity, SecondaryQuantity From  tblVMSalesmanInventory where ItemCode = '"+productDO.itemCode+"'";
							cursor 	= objSqliteDB.rawQuery(strQuery, null);
							if(cursor.moveToFirst())
							{
								float totalQty 			= cursor.getFloat(0);
								float availQty 			= cursor.getFloat(1);
								float primaryQuantity 	= cursor.getFloat(2);
								float secondaryQuantity = cursor.getFloat(3);
								
								if(type.equalsIgnoreCase(AppConstants.TRNS_TYPE_IN))
								{
									totalQty		= totalQty + productDO.totalCases;
									availQty 		= availQty + productDO.totalCases;
									primaryQuantity = primaryQuantity + productDO.preCases;
									secondaryQuantity = secondaryQuantity + productDO.preUnits;
									
									stmtUpdateQty.bindString(1, ""+totalQty);
									
									stmtUpdateQty.bindString(2, ""+availQty);
									stmtUpdateQty.bindString(3, ""+primaryQuantity);
									stmtUpdateQty.bindString(4, ""+secondaryQuantity);
								}
								else
								{
									
									availQty 			= availQty - productDO.totalCases;
									
									stmtUpdateQty.bindString(1, ""+totalQty);
									
									if(availQty > 0 )
										stmtUpdateQty.bindString(2, ""+availQty);
									else
										stmtUpdateQty.bindString(2, "0");
									
									stmtUpdateQty.bindString(3, ""+primaryQuantity);
									stmtUpdateQty.bindString(4, ""+secondaryQuantity);
								}
								
								stmtUpdateQty.bindString(5, ""+productDO.itemCode);
								stmtUpdateQty.execute();
								
								if(cursor != null && !cursor.isClosed())
									cursor.close();
							}
						}
						else
						{
							stmtInsert.bindString(1, inventoryId);
							stmtInsert.bindString(2, CalendarUtils.getOrderPostDate());
							stmtInsert.bindString(3, empNo);
							stmtInsert.bindString(4, productDO.itemCode);
							stmtInsert.bindString(5, ""+productDO.preCases);
							stmtInsert.bindString(6, ""+productDO.preUnits);
							stmtInsert.bindString(7, "true");
							stmtInsert.bindString(8, ""+productDO.totalCases);
							stmtInsert.bindString(9, ""+productDO.totalCases);
							stmtInsert.bindString(10, "Y");
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdateQty.close();
				return true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
	}
	
	public ArrayList<LoadRequestDO> getAllVanTransferLoadRequestToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<LoadRequestDO> vecLoad = new ArrayList<LoadRequestDO>();
			SQLiteDatabase sLiteDatabase = null;
			Cursor cursor = null, cursorDetail = null;
			try
			{
				sLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String strQuery 	= 	"SELECT * FROM tblMovementHeader WHERE Status = 'N' AND UserLoadType = 4";
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
							
							LogUtils.debug("getVanTransferStock", "loadDA"+OrderDetail.MovementStatus);
							
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
										
										LogUtils.debug("getVanTransferStock", "loaddetailDA"+oRequestDetailDO.MovementStatus);
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
	
	public Vector<TransferInoutDO> getUnuploadedTransferData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferInOut where transferStatus ='N'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						TransferInoutDO transferInoutDO 	= new TransferInoutDO();
						transferInoutDO.InventoryUID   		= cursor.getString(0);
						transferInoutDO.fromEmpNo		 	= cursor.getString(1);
						transferInoutDO.toEmpNo 		 	= cursor.getString(2);
						transferInoutDO.trnsferType    		= cursor.getString(3);
						
						transferInoutDO.sourceVNO    		= cursor.getString(5);
						transferInoutDO.destVNO    			= cursor.getString(6);
						transferInoutDO.Date    			= cursor.getString(7);
						transferInoutDO.sourceOrderID		= cursor.getString(8);
						transferInoutDO.destOrderID  		= cursor.getString(9);
						
						transferInoutDO.MovementType    	= cursor.getInt(10);
						transferInoutDO.MovementStatus  	= cursor.getInt(11);
						transferInoutDO.MovementCode    	= cursor.getString(12);
						transferInoutDO.vecOrderDetailDcos	= getTransferedProduct(transferInoutDO.sourceOrderID);
						vecTransferInoutDOs.add(transferInoutDO);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecTransferInoutDOs;
		}
	}
	
	public Vector<TransferInoutDO> getTransferInOutList()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TransferInoutDO> vecTransferInoutDOs = new Vector<TransferInoutDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferInOutNew", null);
				if(cursor.moveToFirst())
				{
					do
					{
						TransferInoutDO transferInoutDO 	= new TransferInoutDO();
						transferInoutDO.InventoryUID   		= cursor.getString(0);
						transferInoutDO.fromEmpNo		 	= cursor.getString(1);
						transferInoutDO.toEmpNo 		 	= cursor.getString(2);
						transferInoutDO.trnsferType    		= cursor.getString(3);
						transferInoutDO.sourceVNO    		= cursor.getString(5);
						transferInoutDO.destVNO    			= cursor.getString(6);
						transferInoutDO.Date    			= cursor.getString(7);
						transferInoutDO.sourceOrderID    	= cursor.getString(8);
						transferInoutDO.destOrderID    		= cursor.getString(9);
						
						transferInoutDO.customerName = new UserInfoDA().getNameByEmpNO(transferInoutDO.fromEmpNo);
						vecTransferInoutDOs.add(transferInoutDO);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecTransferInoutDOs;
		}
	}
	
	
	public void updateTransferInOUTStatus(String UUID, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblTransferInOut SET transferStatus =? WHERE InventoryUID =?");
				stmtUpdate.bindString(1,status);
				stmtUpdate.bindString(2, UUID);
				stmtUpdate.executeInsert();
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
	
	public void updateTransferInOUTStatusNew(Vector<TransferInoutDO> transferInoutDOs, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				for (TransferInoutDO transferInoutDO : transferInoutDOs) {
					
					SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblTransferInOut SET transferStatus =? WHERE InventoryUID =?");
					stmtUpdate.bindString(1,status);
					stmtUpdate.bindString(2, transferInoutDO.InventoryUID);
					stmtUpdate.executeInsert();
				}
				//Query to Insert the User information in to UserInfo Table
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
	public void updateVanTransferStatus(ArrayList<LoadRequestDO> vecTransferInoutDOs, String status)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				
				for (LoadRequestDO objloaLoadRequestDO : vecTransferInoutDOs) {
					
					SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblMovementHeader SET Status =? WHERE MovementCode =?");
					stmtUpdate.bindString(1,status);
					stmtUpdate.bindString(2, objloaLoadRequestDO.MovementCode);
					stmtUpdate.executeInsert();
				}
				//Query to Insert the User information in to UserInfo Table
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
	public ArrayList<DeliveryAgentOrderDetailDco> getTransferedProduct(String orderNumber)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> arrOrderDetailDcos = new ArrayList<DeliveryAgentOrderDetailDco>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferedInventory where InventoryUID = '"+orderNumber+"'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						
						orderDetailDco.itemCode				= cursor.getString(1);
						orderDetailDco.itemDescription		= cursor.getString(2);
						orderDetailDco.preCases				= StringUtils.getFloat(cursor.getString(3));
						orderDetailDco.preUnits				= StringUtils.getInt(cursor.getString(4));
						orderDetailDco.totalCases			= StringUtils.getFloat(cursor.getString(5));
						orderDetailDco.transferDetailID		= cursor.getString(7);
						arrOrderDetailDcos.add(orderDetailDco);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return arrOrderDetailDcos;
		}
	}
	
	public ArrayList<DeliveryAgentOrderDetailDco> getTransferedProductNew(String uuID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> arrOrderDetailDcos = new ArrayList<DeliveryAgentOrderDetailDco>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				cursor		=	slDatabase.rawQuery("SELECT * from tblTransferedInventoryNew where InventoryUID = '"+uuID+"'", null);
				if(cursor.moveToFirst())
				{
					do
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						
						orderDetailDco.itemCode				= cursor.getString(1);
						orderDetailDco.itemDescription		= cursor.getString(2);
						orderDetailDco.preCases				= StringUtils.getFloat(cursor.getString(3));
						orderDetailDco.preUnits				= StringUtils.getInt(cursor.getString(4));
						orderDetailDco.totalCases			= StringUtils.getFloat(cursor.getString(5));
						orderDetailDco.transferDetailID		= cursor.getString(7);
						arrOrderDetailDcos.add(orderDetailDco);
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
				if(slDatabase != null)
					slDatabase.close();
			}
			return arrOrderDetailDcos;
		}
	}
	
	public int getUniPerCasesBySKU(SQLiteDatabase sqLiteDatabase, String SKU)
	{
		synchronized(MyApplication.MyLock) 
		{
			int unitPerCases = 0;
			Cursor cursor	 = null;
			try 
			{
				if(sqLiteDatabase == null)
					sqLiteDatabase  =  DatabaseHelper.openDataBase();
				String strQuery = "SELECT UnitPerCase FROM tblProducts where SKU ='"+SKU+"'";
				cursor	=	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
					unitPerCases = cursor.getInt(0);
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return unitPerCases;
		}
	}
	
	public Vector<LoadRequestDO> getUnuploadedTransferDOs()
	{
		synchronized (MyApplication.MyLock)
		{
			Vector<LoadRequestDO> vecloDos = null;
			LoadRequestDO objloaDo = null;
			SQLiteDatabase objsqlite = null;
			Cursor cursor = null;
			try
			{
				objsqlite = DatabaseHelper.openDataBase();
				String query = "SELECT * tblMovementHeader WHERE UserLoadType = 4 AND Status='N'";
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				
			}
			return vecloDos;
		}
	}
}
