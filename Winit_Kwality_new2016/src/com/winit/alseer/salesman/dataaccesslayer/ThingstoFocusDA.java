package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.AssetDO;
import com.winit.alseer.salesman.dataobject.ThingstofocusDO;
import com.winit.sfa.salesman.MyApplication;

public class ThingstoFocusDA
{

	/**
	 * Method to Insert the Asset information in to Asset Table
	 * @param Assetinfo
	 */
	
	public boolean insertThingstofocus(Vector<ThingstofocusDO> vecThingstofocusDO)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblThingstoFocus WHERE FocusId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblThingstoFocus(FocusId, Type, Code, Title, SubTitle, Content , ImagePath, VAlign, HAlign, DisplayTime) VALUES(?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblThingstoFocus SET Type = ?, Code = ?, Title = ?, SubTitle = ?, Content = ?, ImagePath = ?, VAlign = ?, HAlign = ?, DisplayTime = ? where FocusId= ?");
				
				if(vecThingstofocusDO!=null && vecThingstofocusDO.size()>0)
				{
					
					for(ThingstofocusDO thingstofocusDO : vecThingstofocusDO)
					{
						stmtSelectRec.bindLong(1, thingstofocusDO.FocusId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, thingstofocusDO.Type);
							stmtUpdate.bindString(2, thingstofocusDO.Code);
							stmtUpdate.bindString(3, thingstofocusDO.Title);
							stmtUpdate.bindString(4, thingstofocusDO.SubTitle);
							stmtUpdate.bindString(5, thingstofocusDO.Content);
							stmtUpdate.bindString(6, thingstofocusDO.ImagePath);
							stmtUpdate.bindString(7, thingstofocusDO.VAlign);
							stmtUpdate.bindString(8, thingstofocusDO.HAlign);
							stmtUpdate.bindString(9, thingstofocusDO.DisplayAt);
							stmtUpdate.bindLong(10, thingstofocusDO.FocusId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindLong(1, thingstofocusDO.FocusId);
							stmtInsert.bindString(2, thingstofocusDO.Type);
							stmtInsert.bindString(3, thingstofocusDO.Code);
							stmtInsert.bindString(4, thingstofocusDO.Title);
							stmtInsert.bindString(5, thingstofocusDO.SubTitle);
							stmtInsert.bindString(6, thingstofocusDO.Content);
							stmtInsert.bindString(7, thingstofocusDO.ImagePath);
							stmtInsert.bindString(8, thingstofocusDO.VAlign);
							stmtInsert.bindString(9, thingstofocusDO.HAlign);
							stmtInsert.bindString(10, thingstofocusDO.DisplayAt);
							stmtInsert.executeInsert();
						}
						
					}
				}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
			
		}
		
	}
	
	/* For Inserting Single Record */
	
	public boolean insertAsset(AssetDO assestdo)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try {
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblAsset WHERE assetId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblAsset(assetId, barCode, assetType, name, modifiedDate, modifiedTime , Capacity, ImagePath, InstallationDate, LastServiceDate ) VALUES(?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAsset SET barCode = ?, assetType = ?, name = ?, modifiedDate = ?, modifiedTime = ?, Capacity = ?, ImagePath = ?, InstallationDate = ?, LastServiceDate = ? where assetId= ?");
				
					if(assestdo !=null)
					{
						stmtSelectRec.bindString(1, assestdo.assetId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, assestdo.barCode);
							stmtUpdate.bindString(2, assestdo.assetType);
							stmtUpdate.bindString(3, assestdo.name);
							stmtUpdate.bindString(4, assestdo.modifiedDate);
							stmtUpdate.bindString(5, assestdo.modifiedTime);
							stmtUpdate.bindString(6, assestdo.capacity);
							stmtUpdate.bindString(7, assestdo.imagePath);
							stmtUpdate.bindString(8, assestdo.installationDate);
							stmtUpdate.bindString(9, assestdo.lastServiceDate);
							stmtUpdate.bindString(10, assestdo.assetId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, assestdo.assetId);
							stmtInsert.bindString(2, assestdo.barCode);
							stmtInsert.bindString(3, assestdo.assetType);
							stmtInsert.bindString(4, assestdo.name);
							stmtInsert.bindString(5, assestdo.modifiedDate);
							stmtInsert.bindString(6, assestdo.modifiedTime);
							stmtInsert.bindString(7, assestdo.capacity);
							stmtInsert.bindString(8, assestdo.imagePath);
							stmtInsert.bindString(9, assestdo.installationDate);
							stmtInsert.bindString(10, assestdo.lastServiceDate);
							stmtInsert.executeInsert();
						}
						
					}
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
				
			} catch (Exception e){
				e.printStackTrace();
				return false;
			}finally{
				
				if(objSqliteDB!= null)
					objSqliteDB.close();
			}
			return true;
			
		}
		
	}
	
	public Vector<AssetDO> getAllAsset()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetDO obj = null;
			Vector<AssetDO> vecAsset = new Vector<AssetDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblAsset";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new AssetDO();
						obj.assetId 	 = cursor.getString(0);
						obj.barCode		 = cursor.getString(1);
						obj.assetType	 = cursor.getString(2);
						obj.name		 = cursor.getString(3);
						obj.modifiedDate = cursor.getString(4);
						obj.modifiedTime = cursor.getString(5);
						obj.capacity = cursor.getString(6);
						obj.imagePath = cursor.getString(7);
						obj.installationDate = cursor.getString(8);
						obj.lastServiceDate = cursor.getString(9);
						vecAsset.add(obj);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return vecAsset;
		}
		
	}
	
	public Vector<ThingstofocusDO> getAllThingstofocusofuser(String usercode)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			ThingstofocusDO obj = null;
			Vector<ThingstofocusDO> vecThingstofocusDO = new Vector<ThingstofocusDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblThingstoFocus where Code = '"+usercode+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new ThingstofocusDO();
						
						
						obj.FocusId	 	 = cursor.getInt(0);
						obj.Type		 = cursor.getString(1);
						obj.Code	 	 = cursor.getString(2);
						obj.Title		 = cursor.getString(3);
						obj.SubTitle 	 = cursor.getString(4);
						obj.Content 	 = cursor.getString(5);
						obj.ImagePath    = cursor.getString(6);
						obj.VAlign       = cursor.getString(7);
						obj.HAlign       = cursor.getString(8);
						obj.DisplayAt    = cursor.getString(9);
						vecThingstofocusDO.add(obj);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return vecThingstofocusDO;
		}
		
	}
	
	public Vector<ThingstofocusDO> getAllThingstofocus()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			ThingstofocusDO obj = null;
			Vector<ThingstofocusDO> vecThingstofocusDO = new Vector<ThingstofocusDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblThingstoFocus";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new ThingstofocusDO();
						
						
						obj.FocusId	 	 = cursor.getInt(0);
						obj.Type		 = cursor.getString(1);
						obj.Code	 	 = cursor.getString(2);
						obj.Title		 = cursor.getString(3);
						obj.SubTitle 	 = cursor.getString(4);
						obj.Content 	 = cursor.getString(5);
						obj.ImagePath    = cursor.getString(6);
						obj.VAlign       = cursor.getString(7);
						obj.HAlign       = cursor.getString(8);
						obj.DisplayAt    = cursor.getString(9);
						vecThingstofocusDO.add(obj);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return vecThingstofocusDO;
		}
		
	}
	public AssetDO getAssetByAssetId(String assetId)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			AssetDO obj = null;
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblAsset where assetId = '"+assetId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
						obj = new AssetDO();
						obj.assetId 	 = cursor.getString(0);
						obj.barCode		 = cursor.getString(1);
						obj.assetType	 = cursor.getString(2);
						obj.name		 = cursor.getString(3);
						obj.modifiedDate = cursor.getString(4);
						obj.modifiedTime = cursor.getString(5);
						obj.capacity     = cursor.getString(6);
						obj.imagePath    = cursor.getString(7);
						obj.installationDate = cursor.getString(8);
						obj.lastServiceDate = cursor.getString(9);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();

				if(mDatabase !=null)
					mDatabase.close();

			}
			
			return obj;
		}
		
	}
	
	public void updateAssetImage(String assetId, String imagePath)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = null;
				stmtUpdate = objSqliteDB.compileStatement("UPDATE tblAsset SET ImagePath = ? WHERE assetId = ?");
				
				stmtUpdate.bindString(1, imagePath);
				stmtUpdate.bindString(2, assetId);
				stmtUpdate.execute();
				
				stmtUpdate.close();
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
