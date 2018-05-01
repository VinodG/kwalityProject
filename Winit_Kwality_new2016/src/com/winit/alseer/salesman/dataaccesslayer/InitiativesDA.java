package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.InitiativeDO;
import com.winit.alseer.salesman.dataobject.InitiativeProductsDO;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanDO;
import com.winit.alseer.salesman.dataobject.InitiativeTradePlanImageDO;
import com.winit.sfa.salesman.MyApplication;

public class InitiativesDA
{

	private String TABLE_NAME = "tblInitiative";
	/**
	 * Method to Insert the Initiative information in to Initiative Table
	 * @param Assetinfo
	 */
	
	public boolean insertInitiative(Vector<InitiativeDO> vecInitiativeDOs)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try 
			{
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from "+TABLE_NAME+" WHERE InitiativeId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInitiative(InitiativeId, Title, Guidline, Brand, StartDate, " +
						"Image , Status, Month, InitiativeMonth, InitiativeYear,Planogram," +
						"ModifiedDate,ModifiedTime,POSM,CustomerVisitAppId,IsChannelSpecified,ModifiedBy,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblInitiative SET Title = ?, Guidline = ?, Brand = ?, " +
						"StartDate = ?, Image = ?, Status = ?, Month = ?, InitiativeMonth = ?, " +
						"InitiativeYear = ? , Planogram = ?, ModifiedDate = ?, ModifiedTime = ?, POSM = ?, CustomerVisitAppId = ?, " +
						" IsChannelSpecified = ?, ModifiedBy = ?, ModifiedOn = ? where InitiativeId= ?");
				
				if(vecInitiativeDOs!=null && vecInitiativeDOs.size()>0)
				{
					
					for(InitiativeDO initiativeDO : vecInitiativeDOs)
					{
						stmtSelectRec.bindDouble(1, initiativeDO.InitiativeId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, initiativeDO.Title);
							stmtUpdate.bindString(2, initiativeDO.Guidline);
							stmtUpdate.bindString(3, initiativeDO.Brand);
							stmtUpdate.bindString(4, initiativeDO.StartDate);
							stmtUpdate.bindString(5, initiativeDO.Image);
							stmtUpdate.bindString(6, initiativeDO.Status);
							stmtUpdate.bindString(7, initiativeDO.Month);
							stmtUpdate.bindString(8, initiativeDO.InitiativeMonth);
							stmtUpdate.bindString(9, initiativeDO.InitiativeYear);
							stmtUpdate.bindString(10, initiativeDO.Planogram);
							stmtUpdate.bindDouble(11, initiativeDO.ModifiedDate);
							stmtUpdate.bindDouble(12, initiativeDO.ModifiedTime);
							stmtUpdate.bindString(13, initiativeDO.POSM);
							stmtUpdate.bindString(14, initiativeDO.CustomerVisitAppId);
							stmtUpdate.bindString(15, initiativeDO.IsChannelSpecified);
							stmtUpdate.bindString(16, initiativeDO.ModifiedBy);
							stmtUpdate.bindString(17, initiativeDO.ModifiedOn);
							stmtUpdate.bindDouble(18, initiativeDO.InitiativeId);
							
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindDouble(1, initiativeDO.InitiativeId);
							stmtInsert.bindString(2, initiativeDO.Title);
							stmtInsert.bindString(3, initiativeDO.Guidline);
							stmtInsert.bindString(4, initiativeDO.Brand);
							stmtInsert.bindString(5, initiativeDO.StartDate);
							stmtInsert.bindString(6, initiativeDO.Image);
							stmtInsert.bindString(7, initiativeDO.Status);
							stmtInsert.bindString(8, initiativeDO.Month);
							stmtInsert.bindString(9, initiativeDO.InitiativeMonth);
							stmtInsert.bindString(10, initiativeDO.InitiativeYear);
							stmtInsert.bindString(11, initiativeDO.Planogram);
							stmtInsert.bindDouble(12, initiativeDO.ModifiedDate);
							stmtInsert.bindDouble(13, initiativeDO.ModifiedTime);
							stmtInsert.bindString(14, initiativeDO.POSM);
							stmtInsert.bindString(15, initiativeDO.CustomerVisitAppId);
							stmtInsert.bindString(16, initiativeDO.IsChannelSpecified);
							stmtInsert.bindString(17, initiativeDO.ModifiedBy);
							stmtInsert.bindString(18, initiativeDO.ModifiedOn);
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
	public boolean insertInitiativeTradePlanImages(Vector<InitiativeTradePlanImageDO> vecInitiativeTradePlanImageDO)
	{

		synchronized (MyApplication.MyLock)
		{

			SQLiteDatabase objSqliteDB = null;

			try
			{

				objSqliteDB = DatabaseHelper.openDataBase();


				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblInitiativeTradePlanImage  WHERE InitiativeTradePlanImageId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInitiativeTradePlanImage( InitiativeTradePlanImageId, InitiativeTradePlanId, " +
						" Type, ExecutionPicture, ExecutionStatus,  ImplementedBy , ImplementedOn, VerifiedBy, VerifiedOn, Reason, JourneyCode, " +
						" VisitCode, ModifiedDate, ModifiedTime, RowStatus) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");

				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblInitiativeTradePlanImage SET InitiativeTradePlanImageId = ?, InitiativeTradePlanId = ?, " +
						" Type = ?, ExecutionPicture = ?, ExecutionStatus = ?,  ImplementedBy = ? , ImplementedOn = ?, VerifiedBy = ?, VerifiedOn = ?, Reason = ?, JourneyCode = ?, " +
						" VisitCode = ?, ModifiedDate = ?, ModifiedTime = ?, RowStatus= ? where InitiativeTradePlanImageId = ?");

//				 [InitiativeTradePlanImageId] [int] , [InitiativeTradePlanId] [int]  , [Type] [varchar] NULL,
//				[ExecutionPicture] [varchar]( , [ExecutionStatus] [int] , [ImplementedBy] [varchar] , [ImplementedOn] [varchar] ,
//				[VerifiedBy] [varchar](50) , [VerifiedOn] [varchar]  , [Reason] [varchar](50)  , [JourneyCode] [varchar]  ,
//				[VisitCode] [varchar]  , [ModifiedDate] [int]  , [ModifiedTime] [int]   , "RowStatus" VARCHAR)

				if(vecInitiativeTradePlanImageDO!=null && vecInitiativeTradePlanImageDO.size()>0)
				{

					for( InitiativeTradePlanImageDO initiativeTradePlanImageDO : vecInitiativeTradePlanImageDO)
					{
						stmtSelectRec.bindDouble(1, initiativeTradePlanImageDO.InitiativeTradePlanImageId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindLong(1, initiativeTradePlanImageDO.InitiativeTradePlanImageId);
							stmtUpdate.bindLong(2, initiativeTradePlanImageDO.InitiativeTradePlanId);
							stmtUpdate.bindString(3, initiativeTradePlanImageDO.Type);
							stmtUpdate.bindString(4, initiativeTradePlanImageDO.ExecutionPicture);
							stmtUpdate.bindLong(5, initiativeTradePlanImageDO.ExecutionStatus);
							stmtUpdate.bindString(6, initiativeTradePlanImageDO.ImplementedBy);
							stmtUpdate.bindString(7, initiativeTradePlanImageDO.ImplementedOn);
							stmtUpdate.bindString(8, initiativeTradePlanImageDO.VerifiedBy);
							stmtUpdate.bindString(9, initiativeTradePlanImageDO.VerifiedOn);
							stmtUpdate.bindString(10, initiativeTradePlanImageDO.Reason);
							stmtUpdate.bindString(11, initiativeTradePlanImageDO.JourneyCode);
							stmtUpdate.bindString(12, initiativeTradePlanImageDO.VisitCode);
							stmtUpdate.bindLong(13, initiativeTradePlanImageDO.ModifiedDate);
							stmtUpdate.bindLong(14, initiativeTradePlanImageDO.ModifiedTime);
							stmtUpdate.bindString(15, initiativeTradePlanImageDO.RowStatus);
							stmtUpdate.bindLong(16, initiativeTradePlanImageDO.InitiativeTradePlanImageId);
							stmtUpdate.execute();
						}
						else
						{

							stmtInsert.bindLong(1, initiativeTradePlanImageDO.InitiativeTradePlanImageId);
							stmtInsert.bindLong(2, initiativeTradePlanImageDO.InitiativeTradePlanId);
							stmtInsert.bindString(3, initiativeTradePlanImageDO.Type);
							stmtInsert.bindString(4, initiativeTradePlanImageDO.ExecutionPicture);
							stmtInsert.bindLong(5, initiativeTradePlanImageDO.ExecutionStatus);
							stmtInsert.bindString(6, initiativeTradePlanImageDO.ImplementedBy);
							stmtInsert.bindString(7, initiativeTradePlanImageDO.ImplementedOn);
							stmtInsert.bindString(8, initiativeTradePlanImageDO.VerifiedBy);
							stmtInsert.bindString(9, initiativeTradePlanImageDO.VerifiedOn);
							stmtInsert.bindString(10, initiativeTradePlanImageDO.Reason);
							stmtInsert.bindString(11, initiativeTradePlanImageDO.JourneyCode);
							stmtInsert.bindString(12, initiativeTradePlanImageDO.VisitCode);
							stmtInsert.bindLong(13, initiativeTradePlanImageDO.ModifiedDate);
							stmtInsert.bindLong(14, initiativeTradePlanImageDO.ModifiedTime);
							stmtInsert.bindString(15, initiativeTradePlanImageDO.RowStatus);
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
	public boolean insertInitiativeProducts(Vector<InitiativeProductsDO> vecInitiativeProductsDO)
	{

		synchronized (MyApplication.MyLock)
		{

			SQLiteDatabase objSqliteDB = null;

			try
			{

				objSqliteDB = DatabaseHelper.openDataBase();



				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblInitiativeProduct WHERE InitiativeId =?");

//				CREATE TABLE [tblInitiativeProduct]( [InitiativeProductId] [int] IDENTITY(1,1) NOT NULL, [InitiativeId] [int] NOT NULL,
// [ItemCode] [varchar](50) NULL, [ModifiedDate] [int] NULL, [ModifiedTime] [int] NULL )
				String insertStmt="INSERT INTO tblInitiativeProduct(InitiativeProductId, InitiativeId, ItemCode, ModifiedDate, ModifiedTime)  " +
						 "   VALUES(?,?,?,?,? )";
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement(insertStmt);
				String updateStmt="UPDATE tblInitiativeProduct SET InitiativeProductId =?, InitiativeId=?, ItemCode=?, ModifiedDate=?, ModifiedTime=?   " +
						"   where InitiativeProductId=?";
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement(updateStmt );

				if(vecInitiativeProductsDO!=null && vecInitiativeProductsDO.size()>0)
				{

					for(InitiativeProductsDO initiativeProductsDO : vecInitiativeProductsDO)
					{
						stmtSelectRec.bindDouble(1, initiativeProductsDO.InitiativeId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindLong(1, initiativeProductsDO.InitiativeProductId);
							stmtUpdate.bindLong(2, initiativeProductsDO.InitiativeId);
							stmtUpdate.bindString(3, initiativeProductsDO.ItemCode);
							stmtUpdate.bindLong(4, initiativeProductsDO.ModifiedDate);
							stmtUpdate.bindLong(5, initiativeProductsDO.ModifiedTime);
							stmtUpdate.bindLong(6, initiativeProductsDO.InitiativeProductId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindLong(1, initiativeProductsDO.InitiativeProductId);
							stmtInsert.bindLong(2, initiativeProductsDO.InitiativeId);
							stmtInsert.bindString(3, initiativeProductsDO.ItemCode);
							stmtInsert.bindLong(4, initiativeProductsDO.ModifiedDate);
							stmtInsert.bindLong(5, initiativeProductsDO.ModifiedTime);
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
	public boolean insertInitiativeTradePlans(Vector<InitiativeTradePlanDO> vecInitiativeTradePlanDO)
	{

		synchronized (MyApplication.MyLock)
		{

			SQLiteDatabase objSqliteDB = null;

			try
			{

				objSqliteDB = DatabaseHelper.openDataBase();

				 //[InitiativeTradePlanId] [int]   [InitiativeId] [int]   [OutletId] [varchar](20)  [OutletName] [varchar](250)  ,
				// [KBDGE] [varchar] , [FD] [varchar] , [FDCost] [float] , [GE] [varchar] , [GECost] [float]  , [FSU] [varchar]( )
				//  , [FSUCost] [float]  , [StartDate] [datetime]  , [EndDate] [datetime]  , [Status] [int]  , [ImageUrl] [varchar]
				//  , [ImplementedOn] [datetime]  , [ImplementedBy] [varchar](20)  , [ModifiedDate] [int]  , [ModifiedTime] [int]
				//  [CustomerVisitAppId]   , [ApprovedBy] [varchar] , [ApprovedOn] [datetime]  , [Branding] [varchar] ,
				// [BrandingCost] [float]  , [RejectionReason] [varchar] )

				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblInitiativeTradePlan WHERE InitiativeTradePlanId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInitiativeTradePlan (InitiativeTradePlanId, InitiativeId, " +
						" OutletId, OutletName, KBDGE, FD, FDCost, GE, GECost, FSU , FSUCost, StartDate, EndDate, Status, ImageUrl, ImplementedOn, ImplementedBy, " +
						" ModifiedDate, ModifiedTime, CustomerVisitAppId, ApprovedBy, ApprovedOn, Branding, BrandingCost, RejectionReason) " +
						" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblInitiativeTradePlan SET InitiativeTradePlanId= ? , InitiativeId= ? , " +
						" OutletId= ? ,OutletName= ? , KBDGE= ? , FD= ? , FDCost= ? , GE= ? , GECost= ? , FSU = ? , FSUCost= ? , StartDate= ? , EndDate= ? ," +
						" Status= ? , ImageUrl= ? , ImplementedOn= ? , ImplementedBy= ? , ModifiedDate= ? , ModifiedTime= ? , CustomerVisitAppId= ? ," +
						" ApprovedBy= ? , ApprovedOn= ? , Branding= ? , BrandingCost= ? , RejectionReason= ? where InitiativeTradePlanId= ?");

				if(vecInitiativeTradePlanDO!=null && vecInitiativeTradePlanDO.size()>0)
				{

					for(InitiativeTradePlanDO initiativeTradePlanDO : vecInitiativeTradePlanDO)
					{
						stmtSelectRec.bindDouble(1, initiativeTradePlanDO .InitiativeTradePlanId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindLong(1, initiativeTradePlanDO.InitiativeTradePlanId);
							stmtUpdate.bindLong(2, initiativeTradePlanDO.InitiativeId);
							stmtUpdate.bindString(3, initiativeTradePlanDO.OutletId);
							stmtUpdate.bindString(4, initiativeTradePlanDO.OutletName);
							stmtUpdate.bindString(5, initiativeTradePlanDO.KBDGE);
							stmtUpdate.bindString(6, initiativeTradePlanDO.FD);
							stmtUpdate.bindDouble(7, Float.parseFloat(initiativeTradePlanDO.FDCost));
							stmtUpdate.bindString(8, initiativeTradePlanDO.GE);
							stmtUpdate.bindDouble(9, Float.parseFloat(initiativeTradePlanDO.GECost) );
							stmtUpdate.bindString(10, initiativeTradePlanDO.FSU);
							stmtUpdate.bindDouble(11, Float.parseFloat(initiativeTradePlanDO.FSUCost));
							stmtUpdate.bindString(12, initiativeTradePlanDO.StartDate);
							stmtUpdate.bindString(13, initiativeTradePlanDO.EndDate);
							stmtUpdate.bindLong(14, Integer.parseInt(initiativeTradePlanDO.Status));
							stmtUpdate.bindString(15, initiativeTradePlanDO.ImageUrl);
							stmtUpdate.bindString(16, initiativeTradePlanDO.ImplementedOn);
							stmtUpdate.bindString(17, initiativeTradePlanDO.ImplementedBy);
							stmtUpdate.bindDouble(18, initiativeTradePlanDO.ModifiedDate);
							stmtUpdate.bindDouble(19, initiativeTradePlanDO.ModifiedTime);
							stmtUpdate.bindString(20, initiativeTradePlanDO.CustomerVisitAppId);
							stmtUpdate.bindString(21, initiativeTradePlanDO.ApprovedBy);
							stmtUpdate.bindString(22, initiativeTradePlanDO.ApprovedOn);
							stmtUpdate.bindString(23, initiativeTradePlanDO.Branding);
							stmtUpdate.bindDouble(24, Float.parseFloat(initiativeTradePlanDO.BrandingCost));
							stmtUpdate.bindString(25, initiativeTradePlanDO.RejectionReason);
							stmtUpdate.bindLong(26, initiativeTradePlanDO.InitiativeTradePlanId);

							stmtUpdate.execute();
						}
						else
						{

							stmtInsert.bindLong(1, initiativeTradePlanDO.InitiativeTradePlanId);
							stmtInsert.bindLong(2, initiativeTradePlanDO.InitiativeId);
							stmtInsert.bindString(3, initiativeTradePlanDO.OutletId);
							stmtInsert.bindString(4, initiativeTradePlanDO.OutletName);
							stmtInsert.bindString(5, initiativeTradePlanDO.KBDGE);
							stmtInsert.bindString(6, initiativeTradePlanDO.FD);
							stmtInsert.bindDouble(7, Float.parseFloat(initiativeTradePlanDO.FDCost));
							stmtInsert.bindString(8, initiativeTradePlanDO.GE);
							stmtInsert.bindDouble(9, Float.parseFloat(initiativeTradePlanDO.GECost) );
							stmtInsert.bindString(10, initiativeTradePlanDO.FSU);
							stmtInsert.bindDouble(11, Float.parseFloat(initiativeTradePlanDO.FSUCost));
							stmtInsert.bindString(12, initiativeTradePlanDO.StartDate);
							stmtInsert.bindString(13, initiativeTradePlanDO.EndDate);
							stmtInsert.bindLong(14, Integer.parseInt(initiativeTradePlanDO.Status));
							stmtInsert.bindString(15, initiativeTradePlanDO.ImageUrl);
							stmtInsert.bindString(16, initiativeTradePlanDO.ImplementedOn);
							stmtInsert.bindString(17, initiativeTradePlanDO.ImplementedBy);
							stmtInsert.bindDouble(18, initiativeTradePlanDO.ModifiedDate);
							stmtInsert.bindDouble(19, initiativeTradePlanDO.ModifiedTime);
							stmtInsert.bindString(20, initiativeTradePlanDO.CustomerVisitAppId);
							stmtInsert.bindString(21, initiativeTradePlanDO.ApprovedBy);
							stmtInsert.bindString(22, initiativeTradePlanDO.ApprovedOn);
							stmtInsert.bindString(23, initiativeTradePlanDO.Branding);
							stmtInsert.bindDouble(24, Float.parseFloat(initiativeTradePlanDO.BrandingCost));
							stmtInsert.bindString(25, initiativeTradePlanDO.RejectionReason);

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
	public boolean insertInitiative2(InitiativeDO executionDO)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try 
			{
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInitiative(InitiativeId, Title, Guidline, Brand, StartDate, " +
						"Image , Status, Month, InitiativeMonth, InitiativeYear,Planogram," +
						"ModifiedDate,ModifiedTime,POSM,CustomerVisitAppId,IsChannelSpecified,ModifiedBy,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				
				if(executionDO !=null)
				{
					stmtInsert.bindDouble(1, executionDO.InitiativeId);
					stmtInsert.bindString(2, executionDO.Title);
					stmtInsert.bindString(3, executionDO.Guidline);
					stmtInsert.bindString(4, executionDO.Brand);
					stmtInsert.bindString(5, executionDO.StartDate);
					stmtInsert.bindString(6, executionDO.Planogram);
					stmtInsert.bindString(7, "");
					stmtInsert.bindString(8, "");
					stmtInsert.bindString(9, "");
					stmtInsert.bindString(10, "");
					stmtInsert.bindString(11, "");
					stmtInsert.bindDouble(12, 0);
					stmtInsert.bindDouble(13, 0);
					stmtInsert.bindString(14, "");
					stmtInsert.bindString(15, "");
					stmtInsert.bindString(16, "");
					stmtInsert.bindString(17, "");
					stmtInsert.bindString(18, "");
					stmtInsert.executeInsert();
					
				}
				
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
	
	public boolean updateInitiative(String initiativeId,String outletId)
	{
		SQLiteDatabase objSqliteDB = null;
		objSqliteDB = DatabaseHelper.openDataBase();
		SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("update tblInitiativeTradePlan set Status = 1 where InitiativeId = "+"'"+initiativeId+"'"+" and OutletId ="+"'"+outletId+"'");
		stmtSelectRec.execute();
		return true;
	}
	
	public int getCount()
	{
		int count=0;
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeDO obj = null;
			Vector<InitiativeDO> vecInitiativeDOs = new Vector<InitiativeDO>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT count(*) FROM  tblInitiative";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						count = cursor.getInt(0);
						
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
			
			return count;
		}
		
	
	}
	
	/* For Inserting Single Record */
	
	public boolean insertInitiative(InitiativeDO initiativeDO)
	{
		
		synchronized (MyApplication.MyLock) 
		{
			
			SQLiteDatabase objSqliteDB = null;
			
			try 
			{
				
				objSqliteDB = DatabaseHelper.openDataBase();
				
				//Query to Insert the Asset information in to Asset Table
				
				//Query to Insert the Asset information in to Asset Table
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from "+TABLE_NAME+" WHERE InitiativeId =?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblInitiative(InitiativeId, Title, Guidline, Brand, StartDate, " +
						"Image , Status, Month, InitiativeMonth, InitiativeYear,Planogram," +
						"ModifiedDate,ModifiedTime,POSM,CustomerVisitAppId,IsChannelSpecified,ModifiedBy,ModifiedOn) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblInitiative SET Title = ?, Guidline = ?, Brand = ?, " +
						"StartDate = ?, Image = ?, Status = ?, Month = ?, InitiativeMonth = ?, " +
						"InitiativeYear = ? , Planogram = ?, ModifiedDate = ?, ModifiedTime = ?, POSM = ?, CustomerVisitAppId = ?, " +
						" IsChannelSpecified = ?, ModifiedBy = ?, ModifiedOn = ? where InitiativeId= ?");
				
					if(initiativeDO !=null)
					{
						stmtSelectRec.bindDouble(1, initiativeDO.InitiativeId);
						long countRec = stmtSelectRec.simpleQueryForLong();


						if(countRec >0)
						{
							stmtUpdate.bindString(1, initiativeDO.Title);
							stmtUpdate.bindString(2, initiativeDO.Guidline);
							stmtUpdate.bindString(3, initiativeDO.Brand);
							stmtUpdate.bindString(4, initiativeDO.StartDate);
							stmtUpdate.bindString(5, initiativeDO.Image);
							stmtUpdate.bindString(6, initiativeDO.Status);
							stmtUpdate.bindString(7, initiativeDO.Month);
							stmtUpdate.bindString(8, initiativeDO.InitiativeMonth);
							stmtUpdate.bindString(9, initiativeDO.InitiativeYear);
							stmtUpdate.bindString(10, initiativeDO.Planogram);
							stmtUpdate.bindDouble(11, initiativeDO.ModifiedDate);
							stmtUpdate.bindDouble(12, initiativeDO.ModifiedTime);
							stmtUpdate.bindString(13, initiativeDO.POSM);
							stmtUpdate.bindString(14, initiativeDO.CustomerVisitAppId);
							stmtUpdate.bindString(15, initiativeDO.IsChannelSpecified);
							stmtUpdate.bindString(16, initiativeDO.ModifiedBy);
							stmtUpdate.bindString(17, initiativeDO.ModifiedOn);
							stmtUpdate.bindDouble(18, initiativeDO.InitiativeId);
							
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindDouble(1, initiativeDO.InitiativeId);
							stmtInsert.bindString(2, initiativeDO.Title);
							stmtInsert.bindString(3, initiativeDO.Guidline);
							stmtInsert.bindString(4, initiativeDO.Brand);
							stmtInsert.bindString(5, initiativeDO.StartDate);
							stmtInsert.bindString(6, initiativeDO.Image);
							stmtInsert.bindString(7, initiativeDO.Status);
							stmtInsert.bindString(8, initiativeDO.Month);
							stmtInsert.bindString(9, initiativeDO.InitiativeMonth);
							stmtInsert.bindString(10, initiativeDO.InitiativeYear);
							stmtInsert.bindString(11, initiativeDO.Planogram);
							stmtInsert.bindDouble(12, initiativeDO.ModifiedDate);
							stmtInsert.bindDouble(13, initiativeDO.ModifiedTime);
							stmtInsert.bindString(14, initiativeDO.POSM);
							stmtInsert.bindString(15, initiativeDO.CustomerVisitAppId);
							stmtInsert.bindString(16, initiativeDO.IsChannelSpecified);
							stmtInsert.bindString(17, initiativeDO.ModifiedBy);
							stmtInsert.bindString(18, initiativeDO.ModifiedOn);
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
	
	public Vector<InitiativeDO> getAllInitiatives()
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeDO obj = null;
			Vector<InitiativeDO> vecInitiativeDOs = new Vector<InitiativeDO>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT DISTINCT * FROM "+ TABLE_NAME +" group by InitiativeId";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new InitiativeDO();
						obj.InitiativeId = cursor.getInt(0);
						obj.Title		 = cursor.getString(1);
						obj.Guidline	 = cursor.getString(2);
						obj.Brand		 = cursor.getString(3);
						obj.StartDate = cursor.getString(4);
						obj.Image = cursor.getString(5);
						obj.Status = cursor.getString(6);
						obj.Month = cursor.getString(7);
						obj.InitiativeMonth = cursor.getString(8);
						obj.InitiativeYear = cursor.getString(9);
						obj.Planogram = cursor.getString(10);
						obj.ModifiedDate = cursor.getInt(11);
						obj.ModifiedTime = cursor.getInt(12);
						obj.POSM = cursor.getString(13);
						obj.CustomerVisitAppId = cursor.getString(14);
						obj.IsChannelSpecified = cursor.getString(15);
						obj.ModifiedBy = cursor.getString(16);
						obj.ModifiedOn = cursor.getString(17);
						
						vecInitiativeDOs.add(obj);
						
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
			
			return vecInitiativeDOs;
		}
		
	}
	
	public Vector<String> getAllInitiativesBrands()
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<String> vecInitiativeBrands = new Vector<String>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT Brand FROM tblInitiative";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						
						String brandID = cursor.getString(0);
						
						vecInitiativeBrands.add(brandID);
						
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
			
			return vecInitiativeBrands;
		}
		
	}
	
	public Vector<String> getAllInitiativesIdsForCustomer(String customerId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			Vector<String> vecInitiativeBrands = new Vector<String>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT InitiativeId FROM tblInitiativeTradePlan where OutletId = "+"'"+customerId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						
						String brandID = cursor.getString(0);
						
						vecInitiativeBrands.add(brandID);
						
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
			
			return vecInitiativeBrands;
		}
		
	}
	
	public String getInitiativesStatus(String customerId,int initiativeId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			String status = "0";
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT Status FROM tblInitiativeTradePlan where OutletId = "+"'"+customerId+"'"+" and InitiativeId = "+"'"+initiativeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						status = cursor.getString(0);
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
			
			return status;
		}
		
	}
	
	public Vector<InitiativeDO> getAllInitiativesById(String initiativeid)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeDO obj = null;
			Vector<InitiativeDO> vecInitiativeDOs = new Vector<InitiativeDO>();
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM "+ TABLE_NAME +" where InitiativeId = "+"'"+initiativeid+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new InitiativeDO();
						obj.InitiativeId = cursor.getInt(0);
						obj.Title		 = cursor.getString(1);
						obj.Guidline	 = cursor.getString(2);
						obj.Brand		 = cursor.getString(3);
						obj.StartDate = cursor.getString(4);
						obj.Image = cursor.getString(5);
						obj.Status = cursor.getString(6);
						obj.Month = cursor.getString(7);
						obj.InitiativeMonth = cursor.getString(8);
						obj.InitiativeYear = cursor.getString(9);
						obj.Planogram = cursor.getString(10);
						obj.ModifiedDate = cursor.getInt(11);
						obj.ModifiedTime = cursor.getInt(12);
						obj.POSM = cursor.getString(13);
						obj.CustomerVisitAppId = cursor.getString(14);
						obj.IsChannelSpecified = cursor.getString(15);
						obj.ModifiedBy = cursor.getString(16);
						obj.ModifiedOn = cursor.getString(17);
						
						vecInitiativeDOs.add(obj);
						
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
			
			return vecInitiativeDOs;
		}
		
	}
	
	public InitiativeDO getInitiativesById(String initiativeid)
	{
		InitiativeDO obj = new InitiativeDO();;
		
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM "+ TABLE_NAME +" where InitiativeId = "+"'"+initiativeid+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						
						obj.InitiativeId = cursor.getInt(0);
						obj.Title		 = cursor.getString(1);
						obj.Guidline	 = cursor.getString(2);
						obj.Brand		 = cursor.getString(3);
						obj.StartDate = cursor.getString(4);
						obj.Image = cursor.getString(5);
						obj.Status = cursor.getString(6);
						obj.Month = cursor.getString(7);
						obj.InitiativeMonth = cursor.getString(8);
						obj.InitiativeYear = cursor.getString(9);
						obj.Planogram = cursor.getString(10);
						obj.ModifiedDate = cursor.getInt(11);
						obj.ModifiedTime = cursor.getInt(12);
						obj.POSM = cursor.getString(13);
						obj.CustomerVisitAppId = cursor.getString(14);
						obj.IsChannelSpecified = cursor.getString(15);
						obj.ModifiedBy = cursor.getString(16);
						obj.ModifiedOn = cursor.getString(17);
						
						
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
			
			return obj;
		}
		
	}
	
	public Vector<InitiativeDO> getAllInitiatives(String InitiativeId)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeDO obj = null;
			Vector<InitiativeDO> vecInitiativeDOs = new Vector<InitiativeDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM "+TABLE_NAME+" where assetId = '"+InitiativeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new InitiativeDO();
						obj.InitiativeId = cursor.getInt(0);
						obj.Title		 = cursor.getString(1);
						obj.Guidline	 = cursor.getString(2);
						obj.Brand		 = cursor.getString(3);
						obj.StartDate = cursor.getString(4);
						obj.Image = cursor.getString(5);
						obj.Status = cursor.getString(6);
						obj.Month = cursor.getString(7);
						obj.InitiativeMonth = cursor.getString(8);
						obj.InitiativeYear = cursor.getString(9);
						obj.Planogram = cursor.getString(10);
						obj.ModifiedDate = cursor.getInt(11);
						obj.ModifiedTime = cursor.getInt(12);
						obj.POSM = cursor.getString(13);
						obj.CustomerVisitAppId = cursor.getString(14);
						obj.IsChannelSpecified = cursor.getString(15);
						obj.ModifiedBy = cursor.getString(16);
						obj.ModifiedOn = cursor.getString(17);
						
						vecInitiativeDOs.add(obj);
						
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
			
			return vecInitiativeDOs;
		}
		
	}
	
	public Vector<InitiativeProductsDO> getAllInitiativeProducts(String InitiativeId)
	{
		synchronized (MyApplication.MyLock) {
			
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			InitiativeProductsDO obj = null;
			Vector<InitiativeProductsDO> vecInitiativeProductsDOs = new Vector<InitiativeProductsDO>();
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT * FROM tblInitiativeProduct where InitiativeId = '"+InitiativeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new InitiativeProductsDO();
						obj.InitiativeProductId = cursor.getInt(0);
						obj.InitiativeId = cursor.getInt(1);
						obj.ItemCode = cursor.getString(2);
						obj.ModifiedDate = cursor.getInt(3);
						obj.ModifiedTime = cursor.getInt(4);
						
						vecInitiativeProductsDOs.add(obj);
						
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
			
			return vecInitiativeProductsDOs;
		}
		
	}

	public String getInitiativesTradePlanId(String customerId,int initiativeId)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			String status = "0";
			
			try 
			{
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT InitiativeTradePlanId FROM tblInitiativeTradePlan where OutletId = "+"'"+customerId+"'"+" and InitiativeId = "+"'"+initiativeId+"'";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						status = cursor.getString(0);
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
			
			return status;
		}
		
	}



}
