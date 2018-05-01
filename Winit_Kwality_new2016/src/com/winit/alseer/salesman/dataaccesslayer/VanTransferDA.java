package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.VanTransferLoadRequestDO;
import com.winit.sfa.salesman.MyApplication;

public class VanTransferDA 
{

	public void insertVanTransferDO(ArrayList<VanTransferLoadRequestDO> arrLoadRequestDO) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement insertVanstock = sqLiteDatabase.compileStatement("INSERT ");//tblMovementHeader
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				
			}
		}
	}
	
}
