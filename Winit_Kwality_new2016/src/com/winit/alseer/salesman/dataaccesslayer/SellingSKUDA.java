package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CustomerSellingSKUGroup;
import com.winit.alseer.salesman.dataobject.GroupSellingSKUClassification;
import com.winit.alseer.salesman.dataobject.SellingSKU;

public class SellingSKUDA 
{
	/**
	 * Insert and Update tblSellingSKU
	 * @param vecSellingSKU
	 * @return boolean
	 */
	public boolean insertSellingSKU(Vector<SellingSKU> vecSellingSKU)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			 SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSellingSKU WHERE GroupType =? AND GroupCode = ? AND ItemCode=?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSellingSKU (SellingSKUId, GroupType, GroupCode, IsCoreSKU, Priority, SellingSKUClassificationId, ModifiedDate, ModifiedTime, CreatedDate, CreatedTime, IsCoreSkuPriority, ItemCode, CreatedBy) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSellingSKU SET IsCoreSKU=?, Priority=?, SellingSKUClassificationId=?, ModifiedDate=?, ModifiedTime=?, IsCoreSkuPriority=?, CreatedBy=?, SellingSKUId=? WHERE GroupType =? AND GroupCode = ? AND ItemCode=?");
			
			for(SellingSKU objSellingSKUDO : vecSellingSKU)
			{
				stmtSelectRec.bindString(1, objSellingSKUDO.GroupType);
				stmtSelectRec.bindString(2, objSellingSKUDO.GroupCode);
				stmtSelectRec.bindString(3, objSellingSKUDO.ItemCode);
				
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objSellingSKUDO != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindString(1, objSellingSKUDO.IsCoreSKU == true? "True" : "False");
						stmtUpdate.bindString(2, ""+objSellingSKUDO.Priority);
						stmtUpdate.bindString(3, ""+objSellingSKUDO.SellingSKUClassificationId);
						stmtUpdate.bindString(4, ""+objSellingSKUDO.ModifiedDate);
						stmtUpdate.bindString(5, ""+objSellingSKUDO.ModifiedTime);
						stmtUpdate.bindString(6, ""+objSellingSKUDO.IsCoreSKUPriority);
						stmtUpdate.bindString(7, ""+objSellingSKUDO.CreatedBy);
						stmtUpdate.bindString(8, ""+objSellingSKUDO.SellingSKUId);
						stmtUpdate.bindString(9, objSellingSKUDO.GroupType);
						stmtUpdate.bindString(10, objSellingSKUDO.GroupCode);
						stmtUpdate.bindString(11, objSellingSKUDO.ItemCode);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindLong(1, objSellingSKUDO.SellingSKUId);
						stmtInsert.bindString(2, objSellingSKUDO.GroupType);
						stmtInsert.bindString(3, objSellingSKUDO.GroupCode);
						stmtInsert.bindString(4, objSellingSKUDO.IsCoreSKU == true? "True" : "False");
						stmtInsert.bindLong(5, objSellingSKUDO.Priority);
						stmtInsert.bindLong(6, objSellingSKUDO.SellingSKUClassificationId);
						stmtInsert.bindLong(7, objSellingSKUDO.ModifiedDate);
						stmtInsert.bindLong(8, objSellingSKUDO.ModifiedTime);
						stmtInsert.bindLong(9, objSellingSKUDO.CreatedDate);
						stmtInsert.bindLong(10, objSellingSKUDO.CreatedTime);
						stmtInsert.bindLong(11, objSellingSKUDO.IsCoreSKUPriority);
						stmtInsert.bindString(12, objSellingSKUDO.ItemCode);
						stmtInsert.bindString(13, ""+objSellingSKUDO.CreatedBy);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null  && objSqliteDB.isOpen())
				objSqliteDB.close();
		}
		return true;
	}
	
	/**
	 * Insert and Update tblCustomerSellingSKUGroup
	 * @param vecCustomerSellingSKUGroup
	 * @return boolean
	 */
	public boolean insertCustomerSellingSKUGroup(Vector<CustomerSellingSKUGroup> vecCustomerSellingSKUGroup)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSellingSKUGroup WHERE CustomerSellingSKUGroupId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCustomerSellingSKUGroup (CustomerSellingSKUGroupId, Code, Name, FieldName, Priority) VALUES(?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCustomerSellingSKUGroup SET Code=?, Name=?, FieldName=?, Priority=? WHERE CustomerSellingSKUGroupId = ?");
			
			for(CustomerSellingSKUGroup objCustomerSellingSKUGroup : vecCustomerSellingSKUGroup)
			{
				stmtSelectRec.bindLong(1, objCustomerSellingSKUGroup.CustomerSellingSKUGroupId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objCustomerSellingSKUGroup != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindString(1, objCustomerSellingSKUGroup.Code);
						stmtUpdate.bindString(2, objCustomerSellingSKUGroup.Name);
						stmtUpdate.bindString(3,objCustomerSellingSKUGroup.FieldName);
						stmtUpdate.bindLong(4, objCustomerSellingSKUGroup.Priority);
						stmtUpdate.bindLong(5, objCustomerSellingSKUGroup.CustomerSellingSKUGroupId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindLong(1, objCustomerSellingSKUGroup.CustomerSellingSKUGroupId);
						stmtInsert.bindString(2, objCustomerSellingSKUGroup.Code);
						stmtInsert.bindString(3, objCustomerSellingSKUGroup.Name);
						stmtInsert.bindString(4,objCustomerSellingSKUGroup.FieldName);
						stmtInsert.bindLong(5, objCustomerSellingSKUGroup.Priority);
						
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null  && objSqliteDB.isOpen())
				objSqliteDB.close();
		}
		return true;
	}
	
	/**
	 * Insert and Update tblGroupSellingSKUClassification
	 * @param vecGroupSellingSKUClassification
	 * @return boolean
	 */
	public boolean insertGroupSellingSKUClassification(Vector<GroupSellingSKUClassification> vecGroupSellingSKUClassification)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblGroupSellingSKUClassification WHERE SellingSKUId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblGroupSellingSKUClassification (SellingSKUId, SellingSKUClassificationId, Priority) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblGroupSellingSKUClassification SET SellingSKUClassificationId=?, Priority=? WHERE SellingSKUId = ?");
			SQLiteStatement stmtDelete    = objSqliteDB.compileStatement("DELETE FROM tblGroupSellingSKUClassification WHERE SellingSKUId =?");
			
			for(GroupSellingSKUClassification objGroupSellingSKUClassification : vecGroupSellingSKUClassification)
			{
				if(objGroupSellingSKUClassification != null)
				{
					
					stmtSelectRec.bindLong(1, objGroupSellingSKUClassification.SellingSKUId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if (countRec != 0) 
					{
						if(objGroupSellingSKUClassification.IsDeleted)
							stmtDelete.execute();
						else
						{
							stmtUpdate.bindString(1, objGroupSellingSKUClassification.SellingSKUClassificationId);
							stmtUpdate.bindLong(2, objGroupSellingSKUClassification.Priority);
							stmtUpdate.bindLong(3, objGroupSellingSKUClassification.SellingSKUId);
							
							stmtUpdate.execute();
						}
					}
					else
					{
						stmtInsert.bindLong(1, objGroupSellingSKUClassification.SellingSKUId);
						stmtInsert.bindString(2, objGroupSellingSKUClassification.SellingSKUClassificationId);
						stmtInsert.bindLong(3, objGroupSellingSKUClassification.Priority);
						
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB != null  && objSqliteDB.isOpen())
				objSqliteDB.close();
		}
		return true;
	}
}
