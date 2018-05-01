package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.PromotionAssignmentsTypeDO;
import com.winit.alseer.salesman.dataobject.PromotionSCDO;
import com.winit.alseer.salesman.dataobject.PromotionTypesDO;
import com.winit.sfa.salesman.MyApplication;

public class PromotionSCDA {
	
	
	public boolean insertPromotionSC(Vector<PromotionSCDO> vecPromotionSCDOs)
	{
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase mDatabase = null;
			
			try {
				mDatabase = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = mDatabase.compileStatement("SELECT COUNT(*) from tblPromotionSC where PromotionId =?");
				SQLiteStatement stmtInsert = mDatabase.compileStatement("INSERT INTO tblPromotionSC(PromotionId, Description, PromoItemCode, PromoItemQuantity, PromoType, FOCItemCode, FOCItemQuantity, Discount, IsActive, CreatedBy, ModifiedDate, ModifiedTime)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = mDatabase.compileStatement("UPDATE tblPromotionSC SET Description =?, PromoItemCode =?, PromoItemQuantity =?, PromoType =?, FOCItemCode =?, FOCItemQuantity =?, Discount =?, IsActive =?, CreatedBy =?, ModifiedDate =?, ModifiedTime =? where  PromotionId =?");
				
				if(vecPromotionSCDOs!=null && vecPromotionSCDOs.size()>0)
				{
					
					for(PromotionSCDO promotionSCDO : vecPromotionSCDOs)
					{
						
						stmtSelectRec.bindString(1, promotionSCDO.PromotionId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(countRec>0)
						{
							stmtUpdate.bindString(1, promotionSCDO.Description);
							stmtUpdate.bindString(2, promotionSCDO.PromoItemCode);
							stmtUpdate.bindString(3, promotionSCDO.PromoItemQuantity+"");
							stmtUpdate.bindString(4, promotionSCDO.PromoType);
							stmtUpdate.bindString(5, promotionSCDO.FOCItemCode);
							stmtUpdate.bindString(6, promotionSCDO.FOCItemQuantity+"");
							stmtUpdate.bindString(7, promotionSCDO.Discount+"");
							stmtUpdate.bindString(8, promotionSCDO.IsActive);
							stmtUpdate.bindString(9, promotionSCDO.CreatedBy);
							stmtUpdate.bindString(10, promotionSCDO.ModifiedDate);
							stmtUpdate.bindString(11, promotionSCDO.ModifiedTime);
							stmtUpdate.bindString(12, promotionSCDO.PromotionId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1, promotionSCDO.PromotionId);
							stmtInsert.bindString(2, promotionSCDO.Description);
							stmtInsert.bindString(3, promotionSCDO.PromoItemCode);
							stmtInsert.bindString(4, promotionSCDO.PromoItemQuantity+"");
							stmtInsert.bindString(5, promotionSCDO.PromoType);
							stmtInsert.bindString(6, promotionSCDO.FOCItemCode);
							stmtInsert.bindString(7, promotionSCDO.FOCItemQuantity+"");
							stmtInsert.bindString(8, promotionSCDO.Discount+"");
							stmtInsert.bindString(9, promotionSCDO.IsActive);
							stmtInsert.bindString(10, promotionSCDO.CreatedBy);
							stmtInsert.bindString(11, promotionSCDO.ModifiedDate);
							stmtInsert.bindString(12, promotionSCDO.ModifiedTime);
							stmtInsert.executeInsert();
						}
					}
				}
				
				stmtSelectRec.close();
				stmtUpdate.close();
				stmtInsert.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally{
				if(mDatabase!=null)
					mDatabase.close();
			}
		}
		return true;
		
	}
	
	private boolean isFocApplicable(int promotionType,
			String countryCode,
			String cityode,
			String regionCode,
			String channelCode,
			String subChannelCode,
			String customerCode,
			String customerGroupCode,
			String salesmanCode,
			String salesmanTeam){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			boolean isApplicable=false;
			SQLiteStatement sqLiteStatement=null;
			Cursor cursorAssignmentType=null;
			String queryFocAssignmentType="select Code,AssignmentType from tblFOCAssignment where IsDeleted='False' collate nocase";
			String selectCount="select count(distinct AssignmentType) from tblFOCAssignment where IsDeleted='False' collate nocase";
			try {
				sqLiteDatabase =DatabaseHelper.openDataBase();
				sqLiteStatement=sqLiteDatabase.compileStatement(selectCount);
				long count=sqLiteStatement.simpleQueryForLong();
				int matched=0;
				cursorAssignmentType=sqLiteDatabase.rawQuery(queryFocAssignmentType, null);
				if(cursorAssignmentType.moveToFirst()){
					do {
						String assignMentCode=cursorAssignmentType.getString(0);
						int assignmentType=cursorAssignmentType.getInt(1);
						
						switch (assignmentType) {
						case PromotionAssignmentsTypeDO.COUNTRY:
							if(!TextUtils.isEmpty(countryCode) && assignMentCode.equalsIgnoreCase(countryCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.CITY:
							if(!TextUtils.isEmpty(cityode) && assignMentCode.equalsIgnoreCase(cityode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.CHANNEL:
							if(!TextUtils.isEmpty(channelCode) && assignMentCode.equalsIgnoreCase(channelCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.CUSTOMER:
							if(!TextUtils.isEmpty(customerCode) && assignMentCode.equalsIgnoreCase(customerCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.CUSTOMERGROUP:
							if(!TextUtils.isEmpty(customerGroupCode) && assignMentCode.equalsIgnoreCase(customerGroupCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.REGION:
							if(!TextUtils.isEmpty(regionCode) && assignMentCode.equalsIgnoreCase(regionCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.SUBCHANNEL:
							if(!TextUtils.isEmpty(subChannelCode) && assignMentCode.equalsIgnoreCase(subChannelCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.SALESMAN:
							if(!TextUtils.isEmpty(salesmanCode) && assignMentCode.equalsIgnoreCase(salesmanCode))
								matched++;
							break;
						case PromotionAssignmentsTypeDO.SALESTEAM:
							if(!TextUtils.isEmpty(salesmanTeam) && assignMentCode.equalsIgnoreCase(salesmanTeam));
								matched++;
							break;
						default:
							break;
						}
					} while (cursorAssignmentType.moveToNext());
				}
				if(count ==matched){
					isApplicable=true;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorAssignmentType!=null && !cursorAssignmentType.isClosed())
					cursorAssignmentType.close();
			}
			return isApplicable;
		}
	}
	public Vector<PromotionSCDO> getPromotionSCDOs(String countryCode,
			String cityode,
			String regionCode,
			String channelCode,
			String subChannelCode,
			String customerCode,
			String customerGroupCode,
			String salesmanCode,
			String salesmanTeam)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor=null;
		PromotionSCDO promotionSCDO = null;
		Vector<PromotionSCDO> vecPromotionSCDOs = new Vector<PromotionSCDO>();
		try {
			
			mDatabase = DatabaseHelper.openDataBase();
			boolean isFocApplicable=isFocApplicable(PromotionTypesDO.get_TYPE_FOC_PROMOTION(), countryCode, cityode, regionCode, channelCode, subChannelCode, customerCode, customerGroupCode, salesmanCode, salesmanTeam);
			if(mDatabase==null || !mDatabase.isOpen())
				mDatabase = DatabaseHelper.openDataBase();
			if(isFocApplicable){
				String query = "SELECT SC.*,TP.Description  FROM tblPromotionSC SC INNER JOIN tblProducts TP ON TP.ItemCode = SC.FOCItemCode Where SC.IsActive = 'True' COLLATE NOCASE";
				cursor = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						promotionSCDO = new PromotionSCDO();
						promotionSCDO.PromotionId		= cursor.getString(0);
//						promotionSCDO.Description		= cursor.getString(1);
						promotionSCDO.PromoItemCode		= cursor.getString(2);
						promotionSCDO.PromoItemQuantity	= cursor.getInt(3);
						promotionSCDO.PromoType			= cursor.getString(4);
						promotionSCDO.FOCItemCode		= cursor.getString(5);
						promotionSCDO.FOCItemQuantity	= cursor.getInt(6);
						promotionSCDO.Discount			= cursor.getInt(7);
						promotionSCDO.IsActive			= cursor.getString(8);
						promotionSCDO.CreatedBy			= cursor.getString(9);
						promotionSCDO.ModifiedDate		= cursor.getString(10);
						promotionSCDO.ModifiedTime		= cursor.getString(11);
						promotionSCDO.Description		= cursor.getString(12);
						vecPromotionSCDOs.add(promotionSCDO);
						
					}while(cursor.moveToNext());
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		finally{
			if(cursor!=null && !cursor.isClosed())
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		
		return vecPromotionSCDOs;
		
	}
}
