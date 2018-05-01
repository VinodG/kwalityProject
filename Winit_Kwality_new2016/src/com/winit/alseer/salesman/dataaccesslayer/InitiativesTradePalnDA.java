package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanImageDO;
import com.winit.sfa.salesman.MyApplication;

public class InitiativesTradePalnDA 
{
	private String TABLE_NAME = "tblInitiativeTradePlanImage";

	/* For Inserting Single Record */

	public boolean insertInitiative(InitiativeTradePlanImageDO initiativeDO) 
	{

		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;

			try {
				objSqliteDB = DatabaseHelper.openDataBase();

				SQLiteStatement stmtInsert = objSqliteDB
						.compileStatement("INSERT INTO tblInitiativeTradePlanImage(InitiativeTradePlanImageId, InitiativeTradePlanId, Type, ExecutionPicture, ExecutionStatus, "
								+ "ImplementedBy , ImplementedOn, VerifiedBy, VerifiedOn, Reason,JourneyCode,"
								+ "VisitCode,ModifiedDate,ModifiedTime) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				if (initiativeDO != null) {
					stmtInsert.bindDouble(1,initiativeDO.InitiativeTradePlanImageId);
					stmtInsert.bindDouble(2, initiativeDO.InitiativeTradePlanId);
					stmtInsert.bindString(3, initiativeDO.Type);
					stmtInsert.bindString(4, initiativeDO.ExecutionPicture);
					stmtInsert.bindDouble(5, initiativeDO.ExecutionStatus);
					stmtInsert.bindString(6, initiativeDO.ImplementedBy);
					stmtInsert.bindString(7, initiativeDO.ImplementedOn);
					stmtInsert.bindString(8, initiativeDO.VerifiedBy);
					stmtInsert.bindString(9, initiativeDO.VerifiedOn);
					stmtInsert.bindString(10, initiativeDO.Reason);
					stmtInsert.bindString(11, initiativeDO.JourneyCode);
					stmtInsert.bindString(12, initiativeDO.VisitCode);
					stmtInsert.bindDouble(13, initiativeDO.ModifiedDate);
					stmtInsert.bindDouble(14, initiativeDO.ModifiedTime);
					stmtInsert.executeInsert();
				}

				stmtInsert.close();

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {

				if (objSqliteDB != null)
					objSqliteDB.close();
			}
			return true;

		}

	}

	public Vector<InitiativeTradePlanImageDO> getAllInitiatives() {
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeTradePlanImageDO obj = null;
			Vector<InitiativeTradePlanImageDO> vecInitiativeDOs = new Vector<InitiativeTradePlanImageDO>();

			try {
				mDatabase = DatabaseHelper.openDataBase();

				String query = "SELECT DISTINCT * FROM " + TABLE_NAME;
				cursor = mDatabase.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						obj = new InitiativeTradePlanImageDO();
						obj.InitiativeTradePlanImageId = cursor.getInt(0);
						obj.InitiativeTradePlanId = cursor.getInt(1);
						obj.Type = cursor.getString(2);
						obj.ExecutionPicture = cursor.getString(3);
						obj.ExecutionStatus = cursor.getInt(4);
						obj.ImplementedBy = cursor.getString(5);
						obj.ImplementedOn = cursor.getString(6);
						obj.VerifiedBy = cursor.getString(7);
						obj.VerifiedOn = cursor.getString(8);
						obj.Reason = cursor.getString(9);
						obj.JourneyCode = cursor.getString(10);
						obj.VisitCode = cursor.getString(11);
						obj.ModifiedDate = cursor.getInt(12);
						obj.ModifiedTime = cursor.getInt(13);

						vecInitiativeDOs.add(obj);

					} while (cursor.moveToNext());
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();

				if (mDatabase != null)
					mDatabase.close();

			}

			return vecInitiativeDOs;
		}

	}

	public Vector<InitiativeTradePlanImageDO> getAllInitiativesById(String initiativeid,String visitcode) {
		synchronized (MyApplication.MyLock) {

			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeTradePlanImageDO obj = null;
			Vector<InitiativeTradePlanImageDO> vecInitiativeDOs = new Vector<InitiativeTradePlanImageDO>();

			try {

				mDatabase = DatabaseHelper.openDataBase();

				String query = "SELECT * FROM " + TABLE_NAME+ " where InitiativeTradePlanId = " + "'" + initiativeid + "'"+" and VisitCode = "+"'"+visitcode+"'";
				cursor = mDatabase.rawQuery(query, null);

				if (cursor.moveToFirst()) {
					do {
						obj = new InitiativeTradePlanImageDO();
						obj.InitiativeTradePlanImageId = cursor.getInt(0);
						obj.InitiativeTradePlanId = cursor.getInt(1);
						obj.Type = cursor.getString(2);
						obj.ExecutionPicture = cursor.getString(3);
						obj.ExecutionStatus = cursor.getInt(4);
						obj.ImplementedBy = cursor.getString(5);
						obj.ImplementedOn = cursor.getString(6);
						obj.VerifiedBy = cursor.getString(7);
						obj.VerifiedOn = cursor.getString(8);
						obj.Reason = cursor.getString(9);
						obj.JourneyCode = cursor.getString(10);
						obj.VisitCode = cursor.getString(11);
						obj.ModifiedDate = cursor.getInt(12);
						obj.ModifiedTime = cursor.getInt(13);

						vecInitiativeDOs.add(obj);

					} while (cursor.moveToNext());
					if (cursor != null && !cursor.isClosed())
						cursor.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();

				if (mDatabase != null)
					mDatabase.close();

			}

			return vecInitiativeDOs;
		}

	}

}
