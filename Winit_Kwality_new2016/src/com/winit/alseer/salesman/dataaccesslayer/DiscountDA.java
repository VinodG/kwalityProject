package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.DiscountMasterDO;
import com.winit.sfa.salesman.MyApplication;

public class DiscountDA 
{
	public boolean insertDiscounts(Vector<DiscountMasterDO> vecDiscountMasterDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB 	=	null;
			boolean result				= 	true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT COUNT(*) from tblDiscountMaster WHERE DiscountId = ?");
				SQLiteStatement stmtInsert 		= 	objSqliteDB.compileStatement("INSERT INTO tblDiscountMaster (DiscountId, SiteNumber, Level, Code, DiscountType, Discount, UOM, MinQty, MaxQty) VALUES(?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= 	objSqliteDB.compileStatement("UPDATE tblDiscountMaster SET SiteNumber=?, Level=?, Code=?, DiscountType=?, Discount=?, UOM=?, MinQty=?, MaxQty=? WHERE DiscountId = ?");
				 //Need to change the Column values.
				for(DiscountMasterDO  discountMasterDO : vecDiscountMasterDOs)
				{
					stmtSelectRec.bindString(1, discountMasterDO.DiscountId);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, discountMasterDO.Site_Number);
						stmtUpdate.bindString(2, discountMasterDO.Level);
						stmtUpdate.bindString(3, discountMasterDO.Code);
						stmtUpdate.bindString(4, discountMasterDO.DiscountType);
						stmtUpdate.bindString(5, discountMasterDO.Discount);
						stmtUpdate.bindString(6, discountMasterDO.UOM);
						stmtUpdate.bindString(7, discountMasterDO.MinQty);
						stmtUpdate.bindString(8, discountMasterDO.MaxQty);
						stmtUpdate.bindString(9, discountMasterDO.DiscountId);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, discountMasterDO.DiscountId);
						stmtInsert.bindString(2, discountMasterDO.Site_Number);
						stmtInsert.bindString(3, discountMasterDO.Level);
						stmtInsert.bindString(4, discountMasterDO.Code);
						stmtInsert.bindString(5, discountMasterDO.DiscountType);
						stmtInsert.bindString(6, discountMasterDO.Discount);
						stmtInsert.bindString(7, discountMasterDO.UOM);
						stmtInsert.bindString(8, discountMasterDO.MinQty);
						stmtInsert.bindString(9, discountMasterDO.MaxQty);
						stmtInsert.executeInsert();
					}
				}
				
				
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
				
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
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
}
