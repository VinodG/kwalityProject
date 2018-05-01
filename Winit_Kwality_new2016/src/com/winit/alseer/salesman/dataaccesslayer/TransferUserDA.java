/**
 * 
 */
package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.WareHouseDetailDO;
import com.winit.sfa.salesman.MyApplication;

/**
 * @author Aritra.Pal
 *
 */
public class TransferUserDA 
{

	/**
	 * @param userID 
	 * @return 
	 * 
	 */
	public Vector<WareHouseDetailDO> getAllUser(String userID) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			Vector<WareHouseDetailDO> vecTransferUserDO = new Vector<WareHouseDetailDO>();
			WareHouseDetailDO objtransferUserDO;
			try 
			{
				String query= "SELECT UserCode, UserName FROM tblUsers";
				
				objSqliteDB = DatabaseHelper.openDataBase();
				cursor 		   = objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						if(!cursor.getString(0).equalsIgnoreCase(userID))
						{
							objtransferUserDO = new WareHouseDetailDO();
							objtransferUserDO.WareHouseCode = cursor.getString(0);
							objtransferUserDO.WareHouseName = cursor.getString(1);
							vecTransferUserDO.add(objtransferUserDO);
						}
					}while(cursor.moveToNext());
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
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return vecTransferUserDO;
		}
	}

}
