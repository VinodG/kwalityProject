package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.PresellerPassCodeDO;
import com.winit.sfa.salesman.MyApplication;

public class PasscodesDA
{
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserPresellerPasscode(Vector<PresellerPassCodeDO> vecPresellerPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblVMPassCode (VMPasscodeId, PresellerId, Passcode, IsUsed) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblVMPassCode");
				for(int i = 0; vecPresellerPassCodeDO !=null && i < vecPresellerPassCodeDO.size(); i++)
				{
					PresellerPassCodeDO objPresellerPassCodeDO = vecPresellerPassCodeDO.get(i);
					
					sqLiteStatement.bindLong(1, objPresellerPassCodeDO.presellerPasscodeId);
					sqLiteStatement.bindString(2, objPresellerPassCodeDO.presellerId);
					sqLiteStatement.bindLong(3, objPresellerPassCodeDO.passCode);
					sqLiteStatement.bindString(4, String.valueOf(objPresellerPassCodeDO.isUsed));
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
	
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
	
	/**
	 * Insert PresellerPasscode Table in database
	 * @param objPresellerPassCodeDO
	 * @return
	 */
	public boolean inserDAPasscode(Vector<NameIDDo> vecPassCodeDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqLiteDatabase = null;
			try
			{
				objSqLiteDatabase = DatabaseHelper.openDataBase();
						 
				SQLiteStatement sqLiteStatement = objSqLiteDatabase.compileStatement("INSERT INTO tblPassCode (EmpId,PassCode,IsUsed,UsedDate) VALUES(?,?,?,?)");
				
				objSqLiteDatabase.execSQL("delete from tblPassCode");
				for(int i = 0; vecPassCodeDO !=null && i < vecPassCodeDO.size(); i++)
				{
					NameIDDo objNameIDDo = vecPassCodeDO.get(i);
					
					sqLiteStatement.bindString(1, objNameIDDo.strName);
					sqLiteStatement.bindString(2, objNameIDDo.strType);
					sqLiteStatement.bindString(3, "0");
					sqLiteStatement.bindString(4, "");
					sqLiteStatement.executeInsert();
				}
				sqLiteStatement.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqLiteDatabase!=null)
				{
					objSqLiteDatabase.close();
				}
			}
		}
	}
}
