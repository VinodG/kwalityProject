package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.RecommendedQuantityDO;
import com.winit.sfa.salesman.MyApplication;

public class RecommendedQuantityDA extends BaseDA
{

	public void insertUpdate(Vector<RecommendedQuantityDO> vecRecQuntDOs) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblRecommendedQuantity WHERE ItemCode=? AND UserCode=?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblRecommendedQuantity(ItemCode, UserCode, LoadQuantity) VALUES(?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblRecommendedQuantity set LoadQuantity=? WHERE ItemCode=? AND UserCode=?");
				
				for(RecommendedQuantityDO objRecommendedQuantityDO :  vecRecQuntDOs)
				{
					stmtSelectRec.bindString(1, objRecommendedQuantityDO.ItemCode);
					stmtSelectRec.bindString(2, objRecommendedQuantityDO.UserCode);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objRecommendedQuantityDO != null )
						{
							stmtUpdate.bindString(1, objRecommendedQuantityDO.LoadQuantity);
							stmtUpdate.bindString(2, objRecommendedQuantityDO.ItemCode);
							stmtUpdate.bindString(3, objRecommendedQuantityDO.UserCode);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objRecommendedQuantityDO != null )
						{
							stmtInsert.bindString(1, objRecommendedQuantityDO.ItemCode);
							stmtInsert.bindString(2, objRecommendedQuantityDO.UserCode);
							stmtInsert.bindString(3, objRecommendedQuantityDO.LoadQuantity);
							stmtInsert.executeInsert();
						}
					}
				}
				stmtSelectRec.close();
				stmtInsert.close();
				stmtUpdate.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null && objSqliteDB.isOpen())
				{
					objSqliteDB.close();
				}
			}
		}
	}

}
