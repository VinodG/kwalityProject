package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.AgencyNewDo;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class AgencyDA {

	private Vector<AgencyNewDo> ArrAgency= new Vector<AgencyNewDo>();	
	
	public Vector<AgencyNewDo> getAgencyDetail()
	{
		
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase= null;
			Cursor cursor=null;
			
			if(ArrAgency!=null && ArrAgency.size()>0)
			{
				ArrAgency.clear();
			}
			try
			{
				mDatabase= DatabaseHelper.openDataBase();
				String query= "SELECT * FROM tblAgency";
				
				cursor=mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						AgencyNewDo Agn= new AgencyNewDo();
						Agn.AgencyId= cursor.getString(0);
						Agn.AgencyName= cursor.getString(1);
						Agn.Priority= StringUtils.getInt(cursor.getString(2));
						ArrAgency.add(Agn);
					}while(cursor.moveToNext());
				}			
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(mDatabase!=null && mDatabase.isOpen())
					mDatabase.close();
			}	
		}	
		return ArrAgency;
	}
}
