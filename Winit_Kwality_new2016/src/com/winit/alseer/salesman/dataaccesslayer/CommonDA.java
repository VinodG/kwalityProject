package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.databaseaccess.DictionaryEntry;
import com.winit.alseer.salesman.dataobject.AgencyDO;
import com.winit.alseer.salesman.dataobject.AllUsersDo;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.CashDenominationDetailDO;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.CheckInDemandInventoryDO;
import com.winit.alseer.salesman.dataobject.CurrencyDO;
import com.winit.alseer.salesman.dataobject.CustomerVisitDO;
import com.winit.alseer.salesman.dataobject.DamageImageDO;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.InventoryDO;
import com.winit.alseer.salesman.dataobject.InventoryDetailDO;
import com.winit.alseer.salesman.dataobject.InventoryGroupDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.NewCustomerDO;
import com.winit.alseer.salesman.dataobject.OrderDO;
import com.winit.alseer.salesman.dataobject.PaymentResponseDo;
import com.winit.alseer.salesman.dataobject.PostPaymentDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentDetailDONew;
import com.winit.alseer.salesman.dataobject.PostPaymentInviceDO;
import com.winit.alseer.salesman.dataobject.PostReasonDO;
import com.winit.alseer.salesman.dataobject.PricingDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.SalesTargetDO;
import com.winit.alseer.salesman.dataobject.SettingsDO;
import com.winit.alseer.salesman.dataobject.SkippingReasonDO;
import com.winit.alseer.salesman.dataobject.TrxDetailsDO;
import com.winit.alseer.salesman.dataobject.TrxHeaderDO;
import com.winit.alseer.salesman.dataobject.TrxPromotionDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.UOMFactorDO;
import com.winit.alseer.salesman.dataobject.UnUploadedDataDO;
import com.winit.alseer.salesman.dataobject.WareHouseDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.alseer.salesman.utilities.UploadTransactions.Transactions;
import com.winit.sfa.salesman.MyApplication;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
public class CommonDA extends BaseDA
{
	
	public boolean insertAllCategories(Vector<CategoryDO> vecCategories)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			
			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblCategory WHERE CategoryId =?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCategory (CategoryId, CategoryName, CategoryIcon) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblCategory set CategoryName=?, CategoryIcon=? WHERE CategoryId =?");
			
			for(CategoryDO objCategoryDO :  vecCategories)
			{
				stmtSelectRec.bindString(1, objCategoryDO.categoryId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{	
					if(objCategoryDO != null )
					{
						stmtUpdate.bindString(1, objCategoryDO.categoryName);
						stmtUpdate.bindString(2, objCategoryDO.categoryIcon);
						stmtUpdate.bindString(3, objCategoryDO.categoryId);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(objCategoryDO != null )
					{
						stmtInsert.bindString(1, objCategoryDO.categoryId);
						stmtInsert.bindString(2, objCategoryDO.categoryName);
						stmtInsert.bindString(3, objCategoryDO.categoryIcon);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtInsert.close();
			stmtUpdate.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}

	//vinod SkippingReasons
	public boolean insertSkippingReasons(Vector<SkippingReasonDO> vecSkippingReasonDO )
	{
		SQLiteDatabase objSqliteDB =null;
		try
		{
			objSqliteDB = DatabaseHelper.openDataBase();

			SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSkipReasons WHERE CustomerSiteId =?  AND PresellerId=?");
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSkipReasons (PresellerId, SkipDate, Reason,  ReasonId, ReasonType, CustomerSiteId) VALUES(?,?,?,?,?,? )");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set SkipDate=?, Reason=? , ReasonId=?,  ReasonType=? WHERE CustomerSiteId =?  AND PresellerId=?");
//			  tblSkipReasons(PresellerId  , SkipDate  , Reason  , ReasonId  , ReasonType  , CustomerSiteId  , "Status"  , "UploadStatus" VARCHAR)
			for(SkippingReasonDO objSkippingReasonDO :  vecSkippingReasonDO)
			{
				stmtSelectRec.bindString(1, objSkippingReasonDO.CustomerSiteId);
				stmtSelectRec.bindString(2, objSkippingReasonDO.PresellerId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{
					if(objSkippingReasonDO != null )
					{
						stmtUpdate.bindString(1, objSkippingReasonDO.PresellerId);
						stmtUpdate.bindString(2, objSkippingReasonDO.SkipDate);
						stmtUpdate.bindString(3, objSkippingReasonDO.Reason);
						stmtUpdate.bindString(4, objSkippingReasonDO.ReasonId);
						stmtUpdate.bindString(5, objSkippingReasonDO.ReasonType);
						stmtUpdate.bindString(6, objSkippingReasonDO.CustomerSiteId);
						stmtUpdate.execute();
					}
				}
				else
				{
					if(objSkippingReasonDO != null )
					{
						stmtInsert.bindString(1, objSkippingReasonDO.SkipDate);
						stmtInsert.bindString(2, objSkippingReasonDO.Reason);
						stmtInsert.bindString(3, objSkippingReasonDO.ReasonId);
						stmtInsert.bindString(4, objSkippingReasonDO.ReasonType);
						stmtInsert.bindString(5, objSkippingReasonDO.CustomerSiteId);
						stmtInsert.bindString(6, objSkippingReasonDO.PresellerId);
						stmtInsert.executeInsert();
					}
				}
			}

			stmtSelectRec.close();
			stmtInsert.close();
			stmtUpdate.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}

	public Vector<String> getReasonBasedOnType(String paymentType)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vecReason = new Vector<String>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase = DatabaseHelper.openDataBase();
					String query = "SELECT Name from tblReasons where Type = '"+paymentType+"'";
					cursor 	=  sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do 
						{
							vecReason.add(cursor.getString(0));
						} while (cursor.moveToNext());
						
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecReason;
		}
	}
	
	public HashMap<Integer,Vector<TrxHeaderDO>> getSavedOrder(String empNo,String customerSiteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			HashMap<Integer,Vector<TrxHeaderDO>> hmOrders   =  new HashMap<Integer, Vector<TrxHeaderDO>>();
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "WHERE UserCode ='"+empNo+"' " +
						       "AND TrxType !='"+AppConstants.CART_TYPE+"' AND TX.ClientCode = '"+customerSiteID+"' AND (TX.TRXStatus='"+TrxHeaderDO.get_TRX_STATUS_SAVED()+"' OR TX.TRXStatus='"+TrxHeaderDO.get_TRX_STATUS_SAVED_MODIFIED()+"')";
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							orderDO.lPOCode				= 	"";
						else
							orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 = 	cursor.getInt(33);
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						
						Predicate<TrxDetailsDO> salesSearchItem = new Predicate<TrxDetailsDO>() {
							public boolean apply(TrxDetailsDO trxDetailsDO) {
								return trxDetailsDO.quantityBU>0;
							}
						};
						Collection<TrxDetailsDO> salesfilteredResult =filter(new Vector<TrxDetailsDO>(
						        (ArrayList<TrxDetailsDO>)orderDO.arrTrxDetailsDOs),salesSearchItem);
						
						orderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) salesfilteredResult;
						
						float totalAmount = 0.0f;
						
						for(TrxDetailsDO trxDetailsDO:orderDO.arrTrxDetailsDOs)
							totalAmount	= totalAmount + (trxDetailsDO.quantityLevel1*trxDetailsDO.priceUsedLevel1);
						
						orderDO.totalAmount = totalAmount;
						
						if(!hmOrders.containsKey(orderDO.trxType))
							vecDetails = new Vector<TrxHeaderDO>();
						else
							vecDetails = hmOrders.get(orderDO.trxType);
						
						if(orderDO.arrTrxDetailsDOs!=null && orderDO.arrTrxDetailsDOs.size()>0)
						{
							vecDetails.add(orderDO);
							hmOrders.put(orderDO.trxType, vecDetails);
						}
						
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return hmOrders;
		}
	}
	
	public Vector<TrxHeaderDO> getSavedOrders(String empNo,String customerSiteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%'" +
						       "AND TrxType !='"+AppConstants.CART_TYPE+"' AND TX.ClientCode = '"+customerSiteID+"' AND TX.TRXStatus='"+TrxHeaderDO.get_TRX_STATUS_SAVED()+"' OR TX.TRXStatus='"+TrxHeaderDO.get_TRX_STATUS_SAVED_MODIFIED()+"'";
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							orderDO.lPOCode				= 	"";
						else
							orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 = 	cursor.getInt(33);
						
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						
						Predicate<TrxDetailsDO> salesSearchItem = new Predicate<TrxDetailsDO>() {
							public boolean apply(TrxDetailsDO trxDetailsDO) {
								return trxDetailsDO.quantityBU>0;
							}
						};
						Collection<TrxDetailsDO> salesfilteredResult =filter(new Vector<TrxDetailsDO>(
						        (ArrayList<TrxDetailsDO>)orderDO.arrTrxDetailsDOs),salesSearchItem);
						
						orderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) salesfilteredResult;
						
						float totalAmount = 0.0f;
						
						for(TrxDetailsDO trxDetailsDO:orderDO.arrTrxDetailsDOs)
							totalAmount	= totalAmount + (trxDetailsDO.quantityLevel1*trxDetailsDO.priceUsedLevel1);
						
						orderDO.totalAmount = totalAmount;
						vecDetails.add(orderDO);
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecDetails;
		}
	}
	
	public Vector<PostPaymentDONew> getAllPaymentsToPostNew(String strPresellerId, String date, String userID)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<PostPaymentDONew> vecPaymentDONews = new Vector<PostPaymentDONew>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery =  "SELECT DISTINCT PH.* FROM tblPaymentHeader PH INNER JOIN tblPaymentInvoice PI ON PI.ReceiptId = PH.ReceiptId where PH.Status = 0 "+ 
								   "AND PI.TrxCode NOT IN (SELECT TrxCode FROM tblTrxHeader WHERE Status <= 0)";
				cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						PostPaymentDONew postPaymentDONew 			= 	new PostPaymentDONew();
						
						postPaymentDONew.AppPaymentId 				= 	cursor.getString(0);
						postPaymentDONew.RowStatus 					= 	cursor.getString(1);
						postPaymentDONew.ReceiptId 					= 	cursor.getString(2);
						postPaymentDONew.PreReceiptId 				= 	cursor.getString(3);
						postPaymentDONew.PaymentDate 				= 	cursor.getString(4);
						postPaymentDONew.SiteId 					= 	cursor.getString(5);
						postPaymentDONew.EmpNo						= 	cursor.getString(6);
						postPaymentDONew.Amount 					= 	cursor.getString(7);
						postPaymentDONew.CurrencyCode				= 	cursor.getString(8);
						postPaymentDONew.Rate 						= 	cursor.getString(9);
						postPaymentDONew.VisitCode 					= 	cursor.getString(10);
						postPaymentDONew.JourneyCode 				= 	userID+CalendarUtils.getOrderPostDate();
						postPaymentDONew.PaymentStatus 				= 	cursor.getString(11);
						postPaymentDONew.CustomerSignature 			= 	cursor.getString(12);
						postPaymentDONew.Status 					= 	cursor.getString(13);
						postPaymentDONew.AppPayementHeaderId 		= 	cursor.getString(14);
						postPaymentDONew.PaymentType 				= 	cursor.getString(15);
						postPaymentDONew.vehicleNo 					= 	cursor.getString(16);
						postPaymentDONew.salesmanCode 				= 	cursor.getString(17);
						postPaymentDONew.salesOrgCode 				= 	cursor.getString(18);
						postPaymentDONew.Division	 				= 	StringUtils.getInt(cursor.getString(20));
						
						String strSubQuery =  "SELECT RowStatus,ReceiptNo,LineNo,PaymentTypeCode,BankCode,ChequeDate,ChequeNo,CCNo,CCExpiry,PaymentStatus," +
								"PaymentNote,UserDefinedBankName,Status,Amount,ChequeImagePath,ImageUploadStatus,Branch FROM tblPaymentDetail where ReceiptNo = '"+postPaymentDONew.ReceiptId+"'";
						
						Cursor subCursor 	=  sqLiteDatabase.rawQuery(strSubQuery, null);
						if(subCursor.moveToFirst())
						{
							do 
							{
								PostPaymentDetailDONew objPaymentDetailDO = new PostPaymentDetailDONew();
								
								objPaymentDetailDO.RowStatus 			= subCursor.getString(0);
								objPaymentDetailDO.ReceiptNo 			= subCursor.getString(1);
								objPaymentDetailDO.LineNo 				= subCursor.getString(2);
								objPaymentDetailDO.PaymentTypeCode 		= subCursor.getString(3);
								objPaymentDetailDO.BankCode				= subCursor.getString(4);
								objPaymentDetailDO.ChequeDate 			= subCursor.getString(5);
								objPaymentDetailDO.ChequeNo 			= subCursor.getString(6);
								objPaymentDetailDO.CCNo 				= subCursor.getString(7);
								objPaymentDetailDO.CCExpiry 			= subCursor.getString(8);
								objPaymentDetailDO.PaymentStatus 		= subCursor.getString(9);
								objPaymentDetailDO.PaymentNote			= subCursor.getString(10);
								objPaymentDetailDO.UserDefinedBankName  = subCursor.getString(11);
								objPaymentDetailDO.Status 				= subCursor.getString(12);
								objPaymentDetailDO.Amount				= subCursor.getString(13);
								objPaymentDetailDO.ChequeImagePath		= subCursor.getString(14);
								objPaymentDetailDO.ImageUploadStatus	= subCursor.getInt(15);
								objPaymentDetailDO.branchName			= subCursor.getString(16);
								if(TextUtils.isEmpty(objPaymentDetailDO.Amount))
									objPaymentDetailDO.Amount = "0"; 
								if(objPaymentDetailDO.Amount.contains(","))
									objPaymentDetailDO.Amount = objPaymentDetailDO.Amount.replace(",", "");
									
								postPaymentDONew.vecPaymentDetailDOs.add(objPaymentDetailDO);
							}
							while (subCursor.moveToNext());
						}
						if(subCursor!=null && !subCursor.isClosed())
							subCursor.close();
						
						String strSubQuery1 =  "SELECT * FROM tblPaymentInvoice where ReceiptId = '"+postPaymentDONew.ReceiptId+"'";
						Cursor subCursor1 	=  sqLiteDatabase.rawQuery(strSubQuery1, null);
						if(subCursor1.moveToFirst())
						{
							do 
							{
								PostPaymentInviceDO paymentInviceDO = new PostPaymentInviceDO();
								
								paymentInviceDO.RowStatus 		= subCursor1.getString(0);
								paymentInviceDO.ReceiptId 		= subCursor1.getString(1);
								paymentInviceDO.TrxCode 		= subCursor1.getString(2);
								paymentInviceDO.TrxType			= subCursor1.getString(3);
								paymentInviceDO.Amount			= StringUtils.getFloat(subCursor1.getString(4));
								paymentInviceDO.CurrencyCode  	= subCursor1.getString(5);
								paymentInviceDO.Rate 			= subCursor1.getString(6);
								paymentInviceDO.PaymentStatus 	= subCursor1.getString(7);
								paymentInviceDO.PaymentType 	= subCursor1.getString(8);
								paymentInviceDO.CashDiscount	= subCursor1.getString(9);
								postPaymentDONew.vecPostPaymentInviceDOs.add(paymentInviceDO);
							}
							while (subCursor1.moveToNext());
						}
						if(subCursor1!=null && !subCursor1.isClosed())
							subCursor1.close();
						
						String strSubQuery2 =  "SELECT PaymentCode,CashDenominationCode ,TotalAmount,CreatedBy FROM tblPaymentCashDenomination where PaymentCode = '"+postPaymentDONew.ReceiptId+"'";
						Cursor subCursor2 	=  sqLiteDatabase.rawQuery(strSubQuery2, null);
						if(subCursor2.moveToFirst())
						{
							do 
							{
								 CashDenominationDetailDO  cashDenominationDO = new CashDenominationDetailDO();
								 cashDenominationDO.PaymentCode           = subCursor2.getString(0);
								 cashDenominationDO.CashDenamationCode    = subCursor2.getString(1);
								 cashDenominationDO.TotalCount            = subCursor2.getInt(2);
								 cashDenominationDO.CreatedOn             = subCursor2.getString(3);
								 
								 postPaymentDONew.vecCashDenominationDOs.add(cashDenominationDO);
							}
							while (subCursor2.moveToNext());
						}
						
						if(subCursor2!=null && !subCursor2.isClosed())
							subCursor2.close();
						
						String strSubQuery3 =  "SELECT PaymentCode,CashDenominationCode ,CreatedBy ,SerialNumber FROM tblPaymentCashDenominationDetail where PaymentCode = '"+postPaymentDONew.ReceiptId+"'";
						Cursor subCursor3 	=  sqLiteDatabase.rawQuery(strSubQuery3, null);
						if(subCursor3.moveToFirst())
						{
							do 
							{
								 CashDenominationDetailDO  cashDenominationDO = new CashDenominationDetailDO();
								 cashDenominationDO.PaymentCode           = subCursor3.getString(0);
								 cashDenominationDO.CashDenamationCode    = subCursor3.getString(1);
								 cashDenominationDO.CreatedOn             = subCursor3.getString(2);
								 cashDenominationDO.SerialNumber		  = subCursor3.getString(3);
								 
								 postPaymentDONew.vecCashDenominationDetailDOs.add(cashDenominationDO);
							}
							while (subCursor3.moveToNext());
						}
						
						if(subCursor3!=null && !subCursor3.isClosed())
							subCursor3.close();
						
						vecPaymentDONews.add(postPaymentDONew);
					}
					while(cursor.moveToNext());
				}
				
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecPaymentDONews;
		}
	}
	
	
	public Vector<TrxHeaderDO> getMissedOrder(String empNo,String customerSiteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%'" +
						       "AND TrxType ='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' AND TX.ClientCode = '"+customerSiteID+"'";
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							orderDO.lPOCode				= 	"";
						else
							orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 = 	cursor.getInt(33);
						
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						
						
						if(orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER())
						{
							
						
							/*
							 * Missed Order
							 */
							
							
							Predicate<TrxDetailsDO> missedSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.missedBU>0;
								}
							};
							Collection<TrxDetailsDO> miisedfilteredResult =filter(orderDO.arrTrxDetailsDOs,missedSearchItem);
							
							if(miisedfilteredResult!=null && miisedfilteredResult.size()>0)
							{
								TrxHeaderDO trxHeaderDO		 = (TrxHeaderDO) orderDO.clone();
								
								trxHeaderDO.trxType			 = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
								trxHeaderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) miisedfilteredResult;
								
								float totalAmount = 0.0f;
								
								for(TrxDetailsDO trxDetailsDO:trxHeaderDO.arrTrxDetailsDOs)
									totalAmount	= totalAmount + (trxDetailsDO.missedBU*trxDetailsDO.priceUsedLevel1);
								
								trxHeaderDO.totalAmount = totalAmount;
								vecDetails.add(trxHeaderDO);
							}
							
							
						}
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecDetails;
		}
	}
	
	
	public Vector<TrxHeaderDO> getLastFiveOrder(String empNo,String customerSiteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null,cursorPromotion = null;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				
				
			
				
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
					       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
					       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
					       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , " +
					       "TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType,TX.SplDisc, TX.LPO, TX.Narrotion, TX.Reason, TX.RateDiff,TC.Attribute13,TX.VAT   " +
					       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
					       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getCurrentMonth()+"%'" +
					       "AND (TrxType ='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' or TrxType ='"+TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER()+"') AND TX.ClientCode = '"+customerSiteID+"' and TX.TRXStatus > 0  Order By TrxDate DESC";
				
				LogUtils.debug("veclastOrder", query);
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						if(TextUtils.isEmpty(cursor.getString(16)))
							orderDO.referenceCode		= "";
						else
							orderDO.referenceCode		= 	cursor.getString(16);
						
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						if(TextUtils.isEmpty(cursor.getString(30)))
							orderDO.userCreditAccountCode ="";
						else
							orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 =  cursor.getInt(33);
						orderDO.specialDiscPercent	 =  cursor.getFloat(34);
						
						orderDO.LPONo				 = 	cursor.getString(35);
						orderDO.Narration			 = 	cursor.getString(36);
						orderDO.returnReason		 = 	cursor.getString(37);
						orderDO.rateDiff			 = 	cursor.getFloat(38);
						orderDO.statementDiscount	 = 	cursor.getString(39);
						orderDO.totalVATAmount	 = 	cursor.getFloat(40);

//						String promotionQuery = "SELECT " +
//								"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
//								"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
//								"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
//								"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
//								"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
//								"WHERE C.Site = '"+orderDO.clientCode+"'";
						String promotionQuery="";
						if(AppConstants.isOldPromotion)
						{
							promotionQuery = "SELECT " +
									"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
									"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
									"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
									"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
									"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
									"WHERE C.Site = '"+orderDO.clientCode+"'";
						}
						else
							promotionQuery = "SELECT Attribute11 FROM tblCustomer WHERE Site = '"+orderDO.clientCode+"'";
						
						cursorPromotion = slDatabase.rawQuery(promotionQuery, null);
						if(cursorPromotion != null && cursorPromotion.moveToFirst())
							orderDO.promotionalDiscount = cursorPromotion.getString(0);
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						
						vecDetails.add(orderDO);
							
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecDetails;
		}
	}
//	public Vector<TrxHeaderDO> getLastFiveOrderAmountDo(String empNo,String customerSiteID)
//	{
//		synchronized(MyApplication.MyLock) 
//		{
//			SQLiteDatabase slDatabase = null;
//			Cursor cursor = null;
//			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
//			try
//			{
//				slDatabase 	= 	DatabaseHelper.openDataBase();
//				String query = "SELECT TX.TrxCode,TX.UserCode,TX.ClientCode, TX.TrxDate,TX.TrxType,TX.TotalAmount," +
//								"TX.TotalDiscountAmount,TX.TotalTAXAmount, TC.SiteName,TC.Address1 " +
//						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
//						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%'" +
//						       "AND TrxType ='"+TrxHeaderDO.get_TRXTYPE_SALES_ORDER()+"' AND TX.ClientCode = '"+customerSiteID+"' Order By TrxDate DESC LIMIT 5";
//				
//				cursor		=	slDatabase.rawQuery(query, null);
//			
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						TrxHeaderDO orderDO 		= new TrxHeaderDO();
//						
//						orderDO.trxCode    			= 	cursor.getString(0);
//						orderDO.userCode			= 	cursor.getString(1);
//						orderDO.clientCode			= 	cursor.getString(2);
//						orderDO.trxDate				= 	cursor.getString(3);
//						orderDO.trxType				= 	cursor.getInt(4);	
//						orderDO.totalAmount			= 	cursor.getFloat(5);
//						orderDO.totalDiscountAmount	= 	cursor.getFloat(6);
//						orderDO.totalTAXAmount		= 	cursor.getFloat(7);
//						orderDO.siteName			 = 	cursor.getString(8);
//						orderDO.address				 = 	cursor.getString(9);
//						
//						vecDetails.add(orderDO);
//							
//					}
//					while(cursor.moveToNext());
//				}
//				
//				if(cursor != null && !cursor.isClosed())
//					cursor.close();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(slDatabase != null)
//					slDatabase.close();
//			}
//			return vecDetails;
//		}
//	}
	
	public Vector<UnUploadedDataDO> getAllPaymentsUnload(String strPresellerId, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecUploadedDataDOs = new Vector<UnUploadedDataDO>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				String strQuery =  "SELECT ReceiptId, Status FROM tblPaymentHeader where PaymentDate like '"+date+"%'";
				
				cursor 	=  sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						UnUploadedDataDO uploadedDataDO	= 	new UnUploadedDataDO();
						uploadedDataDO.strId 		= 	cursor.getString(0);
						if(cursor.getString(1).equalsIgnoreCase("N"))
							uploadedDataDO.status = 0 ;
						else
							uploadedDataDO.status = 1 ;
						
						vecUploadedDataDOs.add(uploadedDataDO);
					}
					while(cursor.moveToNext());
				}
				
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecUploadedDataDOs;
		}
	}
	
	public Vector<String> getFreeDeliveryResion()
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vecReason = new Vector<String>();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor  = null;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase = DatabaseHelper.openDataBase();
					String query = "SELECT Name from tblReasons where Type = '"+AppConstants.FREE_DELIVERY_ORDER+"'";
					cursor 	=  sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do 
						{
							vecReason.add(cursor.getString(0));
						} while (cursor.moveToNext());
						
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecReason;
		}
	}
	
	public int getUnUploadedOrderStatus(String empNo)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			int totalCount = 0;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) " +
					       "FROM tblTrxHeader " +
					       "WHERE UserCode ='"+empNo+"' and status = 0 " +
				           "AND TrxType !='"+AppConstants.CART_TYPE+"' ORDER BY TrxCode ASC LIMIT "+AppConstants.ORDER_PUSH_LIMIT;
			
				cursor		=	sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					totalCount = cursor.getInt(0);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqliteDB != null && sqliteDB.isOpen())
					sqliteDB.close();
			}
			int loopCount = (totalCount / 10) + 1;
			return loopCount;
		}
	}
	
	public Vector<TrxHeaderDO> getAllSalesOrderToPost(String empNo/*, boolean isAll*/)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecOrderList = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TrxCode,AppTrxId,OrgCode,JourneyCode,VisitCode,UserCode,ClientCode,ClientBranchCode, " +
						       "TrxDate,TrxType,CurrencyCode,PaymentType,TotalAmount,TotalDiscountAmount,TotalTAXAmount ,TrxReasonCode, " +
						       "ReferenceCode ,ClientSignature ,SalesmanSignature ,Status ,FreeNote ,CreatedOn,PreTrxCode ,TRXStatus , " +
						       "BranchPlantCode , PrintingTimes ,ApproveByCode ,ApprovedDate ,LPOCode ,DeliveryDate , UserCreditAccountCode ," +
						       "GeoX , GeoY,TrxSubType,SplDisc, LPO, Narrotion, Reason, RateDiff, PresellerCode, Division,BranchPlantCode,VAT " +
						       "FROM tblTrxHeader " +
//						       "WHERE UserCode ='"+empNo+"' and status = 0 " +
						       "WHERE UserCode ='"+empNo+"' and status = 0 " +
					           "AND TrxType !='"+AppConstants.CART_TYPE+"' ORDER BY TrxCode ASC LIMIT "+AppConstants.ORDER_PUSH_LIMIT;
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 				= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						orderDO.geoLatitude			= 	StringUtils.getDouble((cursor.getString(31)));
						orderDO.geoLongitude		= 	StringUtils.getDouble(cursor.getString(32));
						orderDO.trxSubType			= 	cursor.getInt(33);
						orderDO.specialDiscPercent	= 	StringUtils.getFloat(cursor.getString(34));
						
						orderDO.LPONo				= 	cursor.getString(35);
						orderDO.PromotionReason			= 	cursor.getString(36);
						orderDO.returnReason		= 	cursor.getString(37);
						orderDO.rateDiff			= 	StringUtils.getFloat(cursor.getString(38));
     					orderDO.PreSellerUserCode	= 	cursor.getString(39);
     					orderDO.Division			= 	StringUtils.getInt(cursor.getString(40));
     					orderDO.statementDiscount	= 	cursor.getString(41);
     					orderDO.totalVATAmount	= 	cursor.getFloat(42);
						if(orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() || orderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrdernew(slDatabase, orderDO.trxCode,orderDO.totalAmount);
						else if(orderDO.trxType == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || orderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
							orderDO.arrTrxDetailsDOs	=   getProductsOfOrderFOC(slDatabase, orderDO.trxCode);
						else
							orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);

						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						if(orderDO.arrTrxDetailsDOs != null && orderDO.arrTrxDetailsDOs.size() > 0)
							vecOrderList.add(orderDO);
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecOrderList;
		}
	}
	
	
	public Vector<TrxHeaderDO> getAllModifiedOrderToPost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecOrderList = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TrxCode,AppTrxId,OrgCode,JourneyCode,VisitCode,UserCode,ClientCode,ClientBranchCode, " +
						       "TrxDate,TrxType,CurrencyCode,PaymentType,TotalAmount,TotalDiscountAmount,TotalTAXAmount ,TrxReasonCode, " +
						       "ReferenceCode ,ClientSignature ,SalesmanSignature ,Status ,FreeNote ,CreatedOn,PreTrxCode ,TRXStatus , " +
						       "BranchPlantCode , PrintingTimes ,ApproveByCode ,ApprovedDate ,LPOCode ,DeliveryDate , UserCreditAccountCode ," +
						       "GeoX , GeoY,TrxSubType " +
						       "FROM tblTrxHeader " +
						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%' and status = 0 " +
						       "AND TrxType !='"+AppConstants.CART_TYPE+"' AND (TRXStatus ='"+TrxHeaderDO.get_TRX_STATUS_SAVED_MODIFIED()+"' OR TRXStatus ='"+TrxHeaderDO.get_TRX_STATUS_SAVED_FINALIZED()+"' OR TRXStatus ='"+TrxHeaderDO.get_TRX_STATUS_CANCELLED()+"') ORDER BY TrxCode ASC LIMIT 10";
					
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		=   new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						if(orderDO.trxStatus==TrxHeaderDO.get_TRX_STATUS_SAVED_MODIFIED())
							orderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SAVED();
						else if(orderDO.trxStatus==TrxHeaderDO.get_TRX_STATUS_SAVED_FINALIZED())
							orderDO.trxStatus = TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						orderDO.geoLatitude			= 	StringUtils.getDouble(""+cursor.getString(31));
						orderDO.geoLongitude		= 	StringUtils.getDouble(""+cursor.getString(32));
						orderDO.trxSubType			= 	cursor.getInt(33);
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						if(orderDO.arrTrxDetailsDOs != null && orderDO.arrTrxDetailsDOs.size() > 0)
							vecOrderList.add(orderDO);
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecOrderList;
		}
	}
	
	
	public Vector<TrxHeaderDO> getAllCollectedReturnOrderToPost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecOrderList = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TrxCode,AppTrxId,OrgCode,JourneyCode,VisitCode,UserCode,ClientCode,ClientBranchCode, " +
						       "TrxDate,TrxType,CurrencyCode,PaymentType,TotalAmount,TotalDiscountAmount,TotalTAXAmount ,TrxReasonCode, " +
						       "ReferenceCode ,ClientSignature ,SalesmanSignature ,Status ,FreeNote ,CreatedOn,PreTrxCode ,TRXStatus , " +
						       "BranchPlantCode , PrintingTimes ,ApproveByCode ,ApprovedDate ,LPOCode ,DeliveryDate , UserCreditAccountCode ," +
						       "GeoX , GeoY,TrxSubType " +
						       "FROM tblTrxHeader " +
						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%' and status = 100 " +
						       "AND TrxType !='"+AppConstants.CART_TYPE+"' LIMIT 10";
					
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 				= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						orderDO.geoLatitude			= 	StringUtils.getDouble(cursor.getString(31));
						orderDO.geoLongitude		= 	StringUtils.getDouble(cursor.getString(32));
						orderDO.trxSubType			= 	cursor.getInt(33);
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);
						if(orderDO.arrTrxDetailsDOs != null && orderDO.arrTrxDetailsDOs.size() > 0)
							vecOrderList.add(orderDO);
						
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecOrderList;
		}
	}
	public HashMap<Integer,Vector<TrxHeaderDO>> getOrderSummary(String empNo,String fromDate,String toDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null,cursorPromotion = null;
			HashMap<Integer,Vector<TrxHeaderDO>> hmOrders   =  new HashMap<Integer, Vector<TrxHeaderDO>>();
			int trxType;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "";
				
				/*query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode," +
						       "TC.SiteName,TC.Address1,TX.TrxSubType,TX.SplDisc, TX.LPO, TX.Narrotion, TX.Reason, TX.RateDiff  " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "AND UserCode ='"+empNo+"' AND  Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') " +
						       "AND TRXStatus != '-100' ORDER BY TX.TrxDate DESC";*/
				
				query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode," +
						"TX.ClientBranchCode, TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount," +
						"TX.TotalTAXAmount ,TX.TrxReasonCode, TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ," +
						"TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , TX.BranchPlantCode ,TX.PrintingTimes ," +
						"TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName," +
						"TC.Address1,TX.TrxSubType,TX.SplDisc, TX.LPO, TX.Narrotion, TX.Reason, TX.RateDiff, TX.Division, TC.Attribute13,TX.VAT  " +
						"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site AND " +
						"CASE " +
							"WHEN UserCode ='"+empNo+"' " +
							"THEN Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') " +
						"ELSE Date(DeliveryDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') AND TRXStatus != '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' END " +
						"AND TRXStatus != '-100' " +
						"ORDER BY TX.TrxDate DESC";
				
				cursor		=	slDatabase.rawQuery(query, null);

				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 =  cursor.getInt(33);
						orderDO.specialDiscPercent	 =  cursor.getFloat(34);
						
						orderDO.LPONo				 = 	cursor.getString(35);
						orderDO.Narration			 = 	cursor.getString(36);
						orderDO.returnReason		 = 	cursor.getString(37);
						orderDO.rateDiff			 = 	cursor.getFloat(38);
						orderDO.Division			 = 	StringUtils.getInt(cursor.getString(39));
						orderDO.statementDiscount    = cursor.getString(40); // invoice discount and promotional discounts are taken from  value is taken from trxdetails
						orderDO.totalVATAmount    = cursor.getFloat(41); // invoice discount and promotional discounts are taken from  value is taken from trxdetails

						String promotionQuery = "";
						if(AppConstants.isOldPromotion)
						{
							promotionQuery = "SELECT " +
									"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
									"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
									"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
									"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
									"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
									"WHERE C.Site = '"+orderDO.clientCode+"'";
						}
						else
							promotionQuery = "SELECT Attribute11 FROM tblCustomer WHERE Site = '"+orderDO.clientCode+"'";
						
						cursorPromotion = slDatabase.rawQuery(promotionQuery, null);
						if(cursorPromotion != null && cursorPromotion.moveToFirst())
							orderDO.promotionalDiscount = cursorPromotion.getString(0);

						if(orderDO.Division==1)
							orderDO.statementDiscount=""+0;
						
						DecimalFormat decimalFormat = new DecimalFormat("##.##");
						decimalFormat.setMinimumFractionDigits(2);
						decimalFormat.setMaximumFractionDigits(2);
						
						float customerstatementdisc= (StringUtils.getFloat(orderDO.statementDiscount) * orderDO.totalAmount) / 100;
						
						float customerPromotion = (orderDO.specialDiscPercent * orderDO.totalAmount) / 100;
						
//						float customerPromoDiscount=Float.valueOf(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(orderDO.totalDiscountAmount)) - StringUtils.getFloat(decimalFormat.format(customerPromotion)) - StringUtils.getFloat(decimalFormat.format(customerstatementdisc)))));
						float customerPromoDiscount=Float.valueOf(decimalFormat.format((StringUtils.getFloat(decimalFormat.format(orderDO.totalDiscountAmount)) - StringUtils.getFloat(decimalFormat.format(customerPromotion)) - (Math.ceil(customerstatementdisc * 100) / 100))));
						
						
						
//						float customerPromoDiscount = Float.valueOf(decimalFormat.format(orderDO.totalDiscountAmount)) - StringUtils.getFloat(decimalFormat.format(customerPromotion))- StringUtils.getFloat(decimalFormat.format(customerstatementdisc));
						
						if(customerPromoDiscount > 0)
							orderDO.promotionalDiscount = StringUtils.getStringFromDouble((customerPromoDiscount * 100)/orderDO.totalAmount);
						else
							orderDO.promotionalDiscount = "0";
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrderSummary(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);

						if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
							orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage ) + "";
							orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage  ) + "";
						}	else {
							orderDO.statementDiscount = "0";
							orderDO.promotionalDiscount ="0";
						}


						if((orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && 
								orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()) || 
								orderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() ||
								orderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
						{
							Predicate<TrxDetailsDO> missedSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.missedBU>0;
								}
							};
							Collection<TrxDetailsDO> miisedfilteredResult =filter(orderDO.arrTrxDetailsDOs,missedSearchItem);
							
							if(miisedfilteredResult!=null && miisedfilteredResult.size()>0)
							{
								TrxHeaderDO trxHeaderDO		 = (TrxHeaderDO) orderDO.clone();
								
								trxHeaderDO.trxType			 = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
								trxHeaderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) miisedfilteredResult;

								if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
									orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage * orderDO.totalAmount / 100) + "";
									orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage * orderDO.totalAmount / 100) + "";
								}	else {
									orderDO.statementDiscount = "0";
									orderDO.promotionalDiscount ="0";
								}
								if(!hmOrders.containsKey(trxHeaderDO.trxType))
									vecDetails = new Vector<TrxHeaderDO>();
								else
									vecDetails = hmOrders.get(trxHeaderDO.trxType);
								
								vecDetails.add(trxHeaderDO);
								hmOrders.put(trxHeaderDO.trxType, vecDetails);
							}
							
							Predicate<TrxDetailsDO> salesSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.quantityBU>0;
								}
							};
							Collection<TrxDetailsDO> salesfilteredResult =filter(new Vector<TrxDetailsDO>(
							        (ArrayList<TrxDetailsDO>)orderDO.arrTrxDetailsDOs),salesSearchItem);
							
							orderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) salesfilteredResult;
						}
						
						if(orderDO.trxSubType== TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
						{
							trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
							if(!hmOrders.containsKey(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER()))
								vecDetails = new Vector<TrxHeaderDO>();
							else
								vecDetails = hmOrders.get(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER());
						}
						else if(orderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
							trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
						else
							trxType = orderDO.trxType;
						
						if(!hmOrders.containsKey(trxType))
							vecDetails = new Vector<TrxHeaderDO>();
						else
							vecDetails = hmOrders.get(trxType);

						if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
							orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage ) + "";
							orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage  ) + "";
						}	else {
							orderDO.statementDiscount = "0";
							orderDO.promotionalDiscount ="0";
						}
						if(orderDO.arrTrxDetailsDOs!=null && orderDO.arrTrxDetailsDOs.size()>0)
						{
							vecDetails.add(orderDO);
							hmOrders.put(trxType, vecDetails);
						}
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return hmOrders;
		}
	}
	
	public HashMap<Integer,Vector<TrxHeaderDO>> getCustomerOrderSummary(String empNo,String fromDate,String toDate,String customerSiteID)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null,cursorPromotion = null;
			HashMap<Integer,Vector<TrxHeaderDO>> hmOrders   =  new HashMap<Integer, Vector<TrxHeaderDO>>();
			int trxType = 0;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "";
				/*query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode," +
						       "TC.SiteName,TC.Address1,TX.TrxSubType,TX.SplDisc, TX.LPO, TX.Narrotion, TX.Reason, TX.RateDiff  " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "AND UserCode ='"+empNo+"' AND  Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') " +
						       "AND TRXStatus != '-100' AND TX.ClientCode = '"+customerSiteID+"' ORDER BY TX.TrxDate DESC";*/
				
				query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode," +
						"TX.ClientBranchCode, TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount," +
						"TX.TotalTAXAmount ,TX.TrxReasonCode, TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ," +
						"TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , TX.BranchPlantCode ,TX.PrintingTimes ," +
						"TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName," +
						"TC.Address1,TX.TrxSubType,TX.SplDisc, TX.LPO, TX.Narrotion, TX.Reason, TX.RateDiff,TX.Division,TC.Attribute13,TX.VAT  " +
						"FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site AND " +
						"CASE " +"WHEN UserCode ='"+empNo+"' " +
						"THEN Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') " +
						"ELSE Date(DeliveryDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') AND TRXStatus != '"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()+"' END " +
						"AND TRXStatus != '-100' AND TX.ClientCode = '"+customerSiteID+"' " +
						"ORDER BY TX.TrxDate DESC";
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 =  cursor.getInt(33);
						orderDO.specialDiscPercent	 =  cursor.getFloat(34);
						
						orderDO.LPONo				 = 	cursor.getString(35);
						orderDO.Narration			 = 	cursor.getString(36);
						orderDO.returnReason		 = 	cursor.getString(37);
						orderDO.rateDiff			 = 	cursor.getFloat(38);
						orderDO.Division			 = 	StringUtils.getInt(cursor.getString(39));
						orderDO.statementDiscount   = cursor.getString(40);
						orderDO.totalVATAmount   = cursor.getFloat(41);

						String promotionQuery = "";
						
						if(AppConstants.isOldPromotion)
						{
							promotionQuery = "SELECT " +
									"CASE WHEN SC.Discount > 0 THEN SC.Discount ELSE PBD.Discount END AS Discount " +
									"FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
									"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
									"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
									"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
									"WHERE C.Site = '"+orderDO.clientCode+"'";
						}
						else
							promotionQuery = "SELECT Attribute11 FROM tblCustomer WHERE Site = '"+orderDO.clientCode+"'";
						
						cursorPromotion = slDatabase.rawQuery(promotionQuery, null);
						if(cursorPromotion != null && cursorPromotion.moveToFirst())
							orderDO.promotionalDiscount = cursorPromotion.getString(0);
						
						DecimalFormat decimalFormat = new DecimalFormat("##.##");
						decimalFormat.setMinimumFractionDigits(2);
						decimalFormat.setMaximumFractionDigits(2);
						
						if(orderDO.Division==1)
							orderDO.statementDiscount=""+0;
						
						float customerPromotion = (orderDO.specialDiscPercent * orderDO.totalAmount) / 100;
						
						float customerstatementdisc= (StringUtils.getFloat(orderDO.statementDiscount) * orderDO.totalAmount) / 100;
						 DecimalFormat twoDForm = new DecimalFormat("#.##");
						//float customerPromoDiscount = Float.valueOf(twoDForm.format(orderDO.totalDiscountAmount)) - StringUtils.getFloat(decimalFormat.format(customerPromotion)) - StringUtils.getFloat(decimalFormat.format(customerstatementdisc));
						float customerPromoDiscount = Float.valueOf(twoDForm.format((StringUtils.getFloat(decimalFormat.format(orderDO.totalDiscountAmount)) - StringUtils.getFloat(decimalFormat.format(customerPromotion)) - StringUtils.getFloat(decimalFormat.format(customerstatementdisc)))));
						
						if(customerPromoDiscount==0.01f)
							customerPromoDiscount=0.00f;
						
						if(customerPromoDiscount > 0)
							orderDO.promotionalDiscount = StringUtils.getStringFromDouble((customerPromoDiscount * 100)/orderDO.totalAmount);
						else
							orderDO.promotionalDiscount = "0";
						
						orderDO.arrTrxDetailsDOs	=   getProductsOfOrderSummary(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	    =   getTRXPromotions(slDatabase, orderDO.trxCode);

						if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
							orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage ) + "";
							orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage  ) + "";
						}	else {
							orderDO.statementDiscount = "0";
							orderDO.promotionalDiscount ="0";
						}


						if(orderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
						{
							if(orderDO.arrTrxDetailsDOs != null && orderDO.arrTrxDetailsDOs.size() > 0)
							{
								orderDO.reason = orderDO.arrTrxDetailsDOs.get(0).reason;
							}
						}
						
						if((orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && 
								orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED()) || 
								orderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER() ||
								orderDO.trxType == TrxHeaderDO.get_TYPE_FREE_ORDER())
						{
							Predicate<TrxDetailsDO> missedSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.missedBU>0;
								}
							};
							Collection<TrxDetailsDO> miisedfilteredResult =filter(orderDO.arrTrxDetailsDOs,missedSearchItem);
							
							if(miisedfilteredResult!=null && miisedfilteredResult.size()>0)
							{
								TrxHeaderDO trxHeaderDO		 = (TrxHeaderDO) orderDO.clone();
								
								trxHeaderDO.trxType			 = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
								trxHeaderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) miisedfilteredResult;
								
								if(!hmOrders.containsKey(trxHeaderDO.trxType))
									vecDetails = new Vector<TrxHeaderDO>();
								else
									vecDetails = hmOrders.get(trxHeaderDO.trxType);
								if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
									orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage ) + "";
									orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage  ) + "";
								}	else {
									orderDO.statementDiscount = "0";
									orderDO.promotionalDiscount ="0";
								}


								vecDetails.add(trxHeaderDO);
								hmOrders.put(trxHeaderDO.trxType, vecDetails);
							}
							
							Predicate<TrxDetailsDO> salesSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.quantityBU>0;
								}
							};
							Collection<TrxDetailsDO> salesfilteredResult =filter(new Vector<TrxDetailsDO>(
							        (ArrayList<TrxDetailsDO>)orderDO.arrTrxDetailsDOs),salesSearchItem);
							
							orderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) salesfilteredResult;
							
							if(orderDO.trxSubType== TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
								trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
							else if(orderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
								trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
							else
								trxType = orderDO.trxType;
						}
//						else
//							trxType = orderDO.trxType;
						
						if(orderDO.trxSubType== TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
						{
							trxType = TrxHeaderDO.get_TRXTYPE_SAVED_ORDER();
							if(!hmOrders.containsKey(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER()))
								vecDetails = new Vector<TrxHeaderDO>();
							else
								vecDetails = hmOrders.get(TrxHeaderDO.get_TRXTYPE_SAVED_ORDER());
						}
						else if(orderDO.trxSubType == TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER() && orderDO.trxStatus == TrxHeaderDO.get_TRX_STATUS_SAVED())
							trxType = TrxHeaderDO.get_TRX_SUBTYPE_TELE_ORDER();
						else
							trxType = orderDO.trxType;
						
						if(!hmOrders.containsKey(trxType))
							vecDetails = new Vector<TrxHeaderDO>();
						else
							vecDetails = hmOrders.get(trxType);

						if(orderDO.arrTrxDetailsDOs!=null &&orderDO.arrTrxDetailsDOs.get(0)!=null ) {
							orderDO.statementDiscount = (orderDO.arrTrxDetailsDOs.get(0).calculatedDiscountPercentage ) + "";
							orderDO.promotionalDiscount = (orderDO.arrTrxDetailsDOs.get(0).totalDiscountPercentage  ) + "";
						}	else {
							orderDO.statementDiscount = "0";
							orderDO.promotionalDiscount ="0";
						}


						if(orderDO.arrTrxDetailsDOs!=null && orderDO.arrTrxDetailsDOs.size()>0)
						{
							vecDetails.add(orderDO);
							hmOrders.put(trxType, vecDetails);
						}
					}
					while(cursor.moveToNext());
				}
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return hmOrders;
		}
	}
	
	
	public Vector<TrxHeaderDO> getOrderDetailsBasedOnTrxType(String empNo,String customerSiteID,int trxType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			Vector<TrxHeaderDO> vecDetails = new Vector<TrxHeaderDO>();
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String query = "SELECT TX.TrxCode,TX.AppTrxId,TX.OrgCode,TX.JourneyCode,TX.VisitCode,TX.UserCode,TX.ClientCode,TX.ClientBranchCode, " +
						       "TX.TrxDate,TX.TrxType,TX.CurrencyCode,TX.PaymentType,TX.TotalAmount,TX.TotalDiscountAmount,TX.TotalTAXAmount ,TX.TrxReasonCode, " +
						       "TX.ReferenceCode ,TX.ClientSignature ,TX.SalesmanSignature ,TX.Status ,TX.FreeNote ,TX.CreatedOn,TX.PreTrxCode ,TX.TRXStatus , " +
						       "TX.BranchPlantCode ,TX.PrintingTimes ,TX.ApproveByCode ,TX.ApprovedDate ,TX.LPOCode ,TX.DeliveryDate , TX.UserCreditAccountCode,TC.SiteName,TC.Address1,TX.TrxSubType " +
						       "FROM tblTrxHeader TX INNER JOIN tblCustomer TC ON TX.ClientCode = TC.Site " +
						       "WHERE UserCode ='"+empNo+"' AND TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%'" +
						       "AND TrxType ='"+trxType+"' AND TX.ClientCode = '"+customerSiteID+"'";
				
				cursor		=	slDatabase.rawQuery(query, null);
			
				if(cursor.moveToFirst())
				{
					do
					{
						TrxHeaderDO orderDO 		= new TrxHeaderDO();
						orderDO.trxCode    			= 	cursor.getString(0);
						orderDO.appTrxId			= 	cursor.getString(1);
						orderDO.orgCode				= 	cursor.getString(2);
						orderDO.journeyCode			= 	cursor.getString(3);
						orderDO.visitCode			= 	cursor.getString(4);
						orderDO.userCode			= 	cursor.getString(5);
						orderDO.clientCode			= 	cursor.getString(6);
						orderDO.clientBranchCode	= 	cursor.getString(7);
						orderDO.trxDate				= 	cursor.getString(8);
						orderDO.trxType				= 	cursor.getInt(9);	
						orderDO.currencyCode		= 	cursor.getString(10);
						orderDO.paymentType			= 	cursor.getInt(11);
						orderDO.totalAmount			= 	cursor.getFloat(12);
						orderDO.totalDiscountAmount	= 	cursor.getFloat(13);
						orderDO.totalTAXAmount		= 	cursor.getFloat(14);
						orderDO.trxReasonCode		= 	cursor.getString(15);
						orderDO.referenceCode		= 	cursor.getString(16);
						orderDO.clientSignature		= 	cursor.getString(17);
						orderDO.salesmanSignature	= 	cursor.getString(18);
						orderDO.status				= 	cursor.getInt(19);
						orderDO.freeNote			= 	cursor.getString(20);
						orderDO.createdOn			= 	cursor.getString(21);
						orderDO.preTrxCode			= 	cursor.getString(22);
						orderDO.trxStatus			= 	cursor.getInt(23);
						orderDO.branchPlantCode		= 	cursor.getString(24);
						orderDO.printingTimes		= 	cursor.getInt(25);
						orderDO.approveByCode		= 	cursor.getString(26);
						orderDO.approvedDate		= 	cursor.getString(27);
						orderDO.lPOCode				= 	cursor.getString(28);
						orderDO.deliveryDate		= 	cursor.getString(29);
						orderDO.userCreditAccountCode= 	cursor.getString(30);
						orderDO.siteName			 = 	cursor.getString(31);
						orderDO.address				 = 	cursor.getString(32);
						orderDO.trxSubType			 =  cursor.getInt(33);
						orderDO.arrTrxDetailsDOs	 =   getProductsOfOrder(slDatabase, orderDO.trxCode);
						orderDO.arrPromotionDOs	     =   getTRXPromotions(slDatabase, orderDO.trxCode);
						
						
						if(orderDO.trxType == TrxHeaderDO.get_TRXTYPE_SALES_ORDER()||orderDO.trxType == TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER())
						{
							
						
							/*
							 * Missed Order
							 */
							
							
							Predicate<TrxDetailsDO> missedSearchItem = new Predicate<TrxDetailsDO>() {
								public boolean apply(TrxDetailsDO trxDetailsDO) {
									return trxDetailsDO.missedBU>0;
								}
							};
							Collection<TrxDetailsDO> miisedfilteredResult =filter(orderDO.arrTrxDetailsDOs,missedSearchItem);
							
							if(miisedfilteredResult!=null && miisedfilteredResult.size()>0)
							{
								TrxHeaderDO trxHeaderDO		 = (TrxHeaderDO) orderDO.clone();
								trxHeaderDO.trxType			 = TrxHeaderDO.get_TRXTYPE_MISSED_ORDER();
								trxHeaderDO.arrTrxDetailsDOs = (ArrayList<TrxDetailsDO>) miisedfilteredResult;
								vecDetails.add(trxHeaderDO);
							}
						}
						vecDetails.add(orderDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(slDatabase != null)
					slDatabase.close();
			}
			return vecDetails;
		}
	}
	
	public Object[] getSumupOfOrders(String empNo, String fromDate, String toDate, boolean isPresales) {
		synchronized (MyApplication.MyLock) {
			
			int TrxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
			String TrxStatus = ""+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			
			String savedQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxType + "' AND TrxSubType ='"+TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER()+"' AND TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_SAVED()+"' "
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			if(isPresales)
			{
				savedQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount + VAT) )),count(*) "
						+ "FROM tblTrxHeader "
						+ "WHERE TrxType ='" + TrxType + "' AND (TrxStatus ='"+TrxStatus+"') "
						+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
				
				TrxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				TrxStatus = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED()+"' OR TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER()+"' OR TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			}
			
			String salesQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxType + "' AND (TrxStatus ='"+TrxStatus+"') "
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			String returnQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxHeaderDO.get_TRXTYPE_RETURN_ORDER() + "' "
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			
			Cursor cursor = null;
			Object obj[] = new Object[6];
			SQLiteDatabase sqLiteDatabase = null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery(savedQuery, null);
				if (cursor.moveToFirst()) {
					obj[0] = cursor.getDouble(0);
					obj[1] = cursor.getLong(1);
				} else {
					obj[0] = 0.0;
					obj[1] = 0;
				}
				
				cursor = sqLiteDatabase.rawQuery(salesQuery, null);
				if (cursor.moveToFirst()) {
					obj[2] = cursor.getDouble(0);
					obj[3] = cursor.getLong(1);
				} else {
					obj[2] = 0.0;
					obj[3] = 0;
				}
				
				cursor = sqLiteDatabase.rawQuery(returnQuery, null);
				if (cursor.moveToFirst()) {
					obj[4] = cursor.getDouble(0);
					obj[5] = cursor.getLong(1);
				} else {
					obj[4] = 0.0;
					obj[5] = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			}
			return obj;
		}
	}
	
	
	public Object[] getSumupOfOrdersForOneClient(String empNo, String fromDate, String toDate, String site, boolean isPresales) {
		synchronized (MyApplication.MyLock) {
			
			int TrxType = TrxHeaderDO.get_TRXTYPE_SALES_ORDER();
			String TrxStatus = ""+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			
			
			String savedQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxType + "' AND TrxSubType ='"+TrxHeaderDO.get_TRX_SUBTYPE_SAVED_ORDER()+"' AND TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_SAVED()+"' AND ClientCode ='"+site+"' "
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			if(isPresales)
			{
				savedQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
						+ "FROM tblTrxHeader "
						+ "WHERE TrxType ='" + TrxType + "' AND (TrxStatus ='"+TrxStatus+"') AND ClientCode ='"+site+"' "
						+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";

				TrxType = TrxHeaderDO.get_TRXTYPE_PRESALES_ORDER();
				TrxStatus = TrxHeaderDO.get_TRX_STATUS_ADVANCE_ORDER_DELIVERED()+"' OR TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_PRESALES_ORDER()+"' OR TrxStatus ='"+TrxHeaderDO.get_TRX_STATUS_SALES_ORDER_DELIVERED();
			}
			
			String salesQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxType + "' AND (TrxStatus ='"+TrxStatus+"') AND ClientCode ='"+site+"' "
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			String returnQuery = "SELECT sum(((TotalAmount  - TotalDiscountAmount+VAT) )),count(*) "
					+ "FROM tblTrxHeader "
					+ "WHERE UserCode ='"
					+ empNo
					+ "' AND TrxType ='" + TrxHeaderDO.get_TRXTYPE_RETURN_ORDER() + "' AND ClientCode ='"+site+"'"
					+ "AND Date(TrxDate) BETWEEN  Date('"+fromDate+"') AND Date('"+toDate+"') ";
			
			Cursor cursor = null;
			Object obj[] = new Object[6];
			SQLiteDatabase sqLiteDatabase = null;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor = sqLiteDatabase.rawQuery(savedQuery, null);
				if (cursor.moveToFirst()) {
					obj[0] = cursor.getDouble(0);
					obj[1] = cursor.getLong(1);
				} else {
					obj[0] = 0.0;
					obj[1] = 0;
				}
				
				cursor = sqLiteDatabase.rawQuery(salesQuery, null);
				if (cursor.moveToFirst()) {
					obj[2] = cursor.getDouble(0);
					obj[3] = cursor.getLong(1);
				} else {
					obj[2] = 0.0;
					obj[3] = 0;
				}
				
				cursor = sqLiteDatabase.rawQuery(returnQuery, null);
				if (cursor.moveToFirst()) {
					obj[4] = cursor.getDouble(0);
					obj[5] = cursor.getLong(1);
				} else {
					obj[4] = 0.0;
					obj[5] = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			}
			return obj;
		}
	}
	
	//Need to change.
	public boolean isOrderPlace(String siteNo, String orderDate)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase slDatabase = null;
			Cursor cursor = null;
			boolean isOderPlace = false;
			try
			{
				slDatabase 	= 	DatabaseHelper.openDataBase();
				String QUERY = "Select * from tblTrxHeader where ClientCode = '"+siteNo+"' and TrxDate like '"+orderDate+"%'";
				
				cursor		=	slDatabase.rawQuery(QUERY, null);
				if(cursor.moveToFirst())
				{
					isOderPlace = true;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return isOderPlace;
		}
	}
	

	public Vector<UnUploadedDataDO> getAllSalesOrderUnupload(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecUnUploadedDataDOs = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct TrxCode, Status from tblTrxHeader where UserCode ='"+strPresellerId+"'  And TrxDate like '"+CalendarUtils.getOrderPostDate()+"%'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO unUploadedDataDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						unUploadedDataDO.strId 			= 	data[i][0].value.toString();
						unUploadedDataDO.status 		= 	StringUtils.getInt(data[i][1].value.toString());
						vecUnUploadedDataDOs.add(unUploadedDataDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
			return vecUnUploadedDataDOs;
		}
	}
	
	// Need to change based on the new services
//	public Vector<FinalOrderDO> getAllFreeDeliveryItemsOrderToPost(String strPresellerId)
//	{
//		synchronized(MyApplication.MyLock) 
//		{
//			Vector<FinalOrderDO> vecOrderList = new Vector<FinalOrderDO>();
//			try
//			{
//				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct o.orderid,o.PresellerId,o.PresellerId, o.PresellerId, o.DeliveryStatus, o.InvoiceDate, o.CustomerSiteId,o.CustomerSiteId,o.CustomerSiteId,o.PresellerSign,o.CustomerSign,o.DeliveryDate,o.TRANS_TYPE_NAME,o.LPONO,o.UUID,o.Message from tblOrderHeader o where o.presellerid ='"+strPresellerId+"'  And o.InvoiceDate like '%"+CalendarUtils.getOrderPostDate()+"%' and PushStatus == 1 and o.LPONO ='"+AppConstants.FREE_DELIVERY_ORDER+"'");
//				if(data != null && data.length > 0)
//				{
//					for(int i = 0 ; i < data.length ; i++)
//					{
//						FinalOrderDO finalOrderDO 			= 	new FinalOrderDO();
//						if(data[i][0].value!=null)
//						finalOrderDO.strOrderNo 			= 	data[i][0].value.toString();
//						finalOrderDO.strSalesmanCode		= 	data[i][1].value.toString();
//						finalOrderDO.strSalesmanName		=	data[i][2].value.toString();
//						finalOrderDO.strCustomerPriceClass 	= 	data[i][3].value.toString();
//						
//						if(finalOrderDO.strOrderStatus != null)
//							finalOrderDO.strOrderStatus		= 	data[i][4].value.toString();
//						else
//							finalOrderDO.strOrderStatus		= 	"E";
//						
//						finalOrderDO.strOrderDate			= 	data[i][5].value.toString();
//						finalOrderDO.strCustomerName 		= 	data[i][6].value.toString();
//						if(data[i][7].value!=null && data[i][7].value.toString()!=null)
//							finalOrderDO.strCustomerSiteId		= 	data[i][7].value.toString();
//						else
//							finalOrderDO.strCustomerSiteId = "";
//						finalOrderDO.strCustomerId			= 	data[i][8].value.toString();
//						finalOrderDO.strLineNumber			= 	"1";
//						finalOrderDO.strCurrencyCode		= 	"AED";
//						finalOrderDO.strPresellerSign		= 	data[i][9].value.toString();
//						finalOrderDO.strCustomerSign		= 	data[i][10].value.toString();
//						finalOrderDO.strDeliveryDate		= 	""+data[i][11].value.toString();
//						finalOrderDO.strOrder_Type			= 	""+data[i][12].value.toString();
//						finalOrderDO.orderSubType			= 	""+data[i][13].value.toString();
//						
//						if(data[i][14].value != null)
//							finalOrderDO.strUUID			= 	""+data[i][14].value.toString();
//						else
//							finalOrderDO.strUUID			= 	"";
//						
//						if(data[i][15].value != null)
//							finalOrderDO.strMessage			= 	""+data[i][15].value.toString();
//						else
//							finalOrderDO.strMessage			= 	"";
//						
//						finalOrderDO.vecProducts 			=   getProductsOfOrder(finalOrderDO.strOrderNo);
//						if(finalOrderDO.vecProducts != null && finalOrderDO.vecProducts.size() > 0)
//							vecOrderList.add(finalOrderDO);
//					}
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				DatabaseHelper.closedatabase();
//			}
//	
//			return vecOrderList;
//		}
//	}
	
	
	public Vector<UnUploadedDataDO> getAllFreeDeliveryUnuploadData(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecOrderList = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct OrderId, Status from tblOrderHeader where EmpNo ='"+strPresellerId+"'  And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' and SubType ='"+AppConstants.FREE_DELIVERY_ORDER+"'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO finalOrderDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						finalOrderDO.strId 			= 	data[i][0].value.toString();
						finalOrderDO.status			= 	StringUtils.getInt(data[i][1].value.toString());
						vecOrderList.add(finalOrderDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
	
			return vecOrderList;
		}
	}
	
	public Vector<UnUploadedDataDO> getAllAdvanceDeliveryUnuploadData(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> vecOrderList = new Vector<UnUploadedDataDO>();
			try
			{
				DictionaryEntry [][] data	=	DatabaseHelper.get("SELECT distinct OrderId, Status from tblOrderHeader where EmpNo ='"+strPresellerId+"'  And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' and SubType ='"+AppConstants.LPO_ORDER+"'");
				if(data != null && data.length > 0)
				{
					for(int i = 0 ; i < data.length ; i++)
					{
						UnUploadedDataDO finalOrderDO 			= 	new UnUploadedDataDO();
						if(data[i][0].value!=null)
						finalOrderDO.strId 			= 	data[i][0].value.toString();
						finalOrderDO.status			= 	StringUtils.getInt(data[i][1].value.toString());
						vecOrderList.add(finalOrderDO);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
	
			return vecOrderList;
		}
	}
	public ArrayList<TrxDetailsDO> getProductsOfOrder(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<TrxDetailsDO> vecItemList = new ArrayList<TrxDetailsDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT TD.LineNo, TD.TrxCode, TD.ItemCode, TD.OrgCode, TD.TrxReasonCode, TD.TrxDetailsNote," +
						"TD.ItemType, TD.BasePrice, TD.UOM, TD.QuantityLevel1, TD.QuantityLevel2,TD.QuantityLevel3, " +
						"TD.QuantityBU, TD.RequestedBU, TD.ApprovedBU, TD.CollectedBU,TD.FinalBU, TD.PriceUsedLevel1, " +
						"TD.PriceUsedLevel2, TD.PriceUsedLevel3,TD.TaxPercentage, TD.TotalDiscountPercentage, " +
						"TD.TotalDiscountAmount,TD.CalculatedDiscountPercentage, TD.CalculatedDiscountAmount," +
						"TD.UserDiscountPercentage, TD.UserDiscountAmount, TD.ItemDescription,TD.ItemAltDescription, " +
						"TD.DistributionCode, TD.AffectedStock, TD.Status,TD.PromoID, TD.PromoType, TD.CreatedOn, " +
						"TD.TRXStatus, TD.ExpiryDate,TD.RelatedLineID, TD.ItemGroupLevel5, TD.TaxType, TD.SuggestedBU, " +
						"TD.PushedOn,TD.ModifiedDate, TD.ModifiedTime, TD.Reason, TD.CancelledQuantity,TD.InProcessQuantity, " +
						"TD.ShippedQuantity, TD.MissedBU, TD.BatchNumber, UF.BarCode,  " +
						"TD.VAT,TD.TotalVATAmount " +
						"FROM tblTrxDetail TD " +
						"INNER JOIN tblProducts TP ON TP.ItemCode=TD.ItemCode " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode AND TD.UOM=UF.UOM " +
						"WHERE TrxCode='"+orderID+"' " +
						"Order by TP.DisplayOrder ASC";
				
				String uomQuery= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";
				
				cursor = sqLiteDatabase.rawQuery(query , null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO productDO 				= 	new TrxDetailsDO();
						
						productDO.lineNo					= cursor.getInt(0);
						productDO.trxCode					= cursor.getString(1);
						productDO.itemCode					= cursor.getString(2);
						productDO.orgCode					= cursor.getString(3);
						productDO.trxReasonCode				= cursor.getString(4);
						productDO.trxDetailsNote			= cursor.getString(5);
						productDO.itemType					= cursor.getString(6);
						productDO.basePrice					= cursor.getFloat(7);
						productDO.UOM						= cursor.getString(8);
						productDO.quantityLevel1			= cursor.getFloat(9);
						productDO.quantityLevel2			= cursor.getFloat(10);
						productDO.quantityLevel3			= cursor.getInt(11);
						
						productDO.quantityBU				= cursor.getInt(12);
						productDO.requestedBU				= cursor.getInt(13);
						
						productDO.approvedBU				= cursor.getInt(14);
						productDO.collectedBU				= cursor.getInt(15);
						
						productDO.finalBU					= cursor.getInt(16);
						productDO.priceUsedLevel1			= cursor.getFloat(17);
						productDO.priceUsedLevel2			= cursor.getFloat(18);
						productDO.priceUsedLevel3			= cursor.getFloat(19);
						
						
						productDO.taxPercentage				= cursor.getFloat(20);
						productDO.totalDiscountPercentage	= cursor.getFloat(21);
						productDO.totalDiscountAmount		= cursor.getFloat(22);
						productDO.calculatedDiscountPercentage= cursor.getFloat(23);
						productDO.calculatedDiscountAmount	= cursor.getFloat(24);
						
						
						productDO.userDiscountPercentage	= cursor.getFloat(25);
						productDO.userDiscountAmount		= cursor.getFloat(26);
						productDO.itemDescription			= cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							productDO.itemAltDescription			= "";
						else 
							productDO.itemAltDescription			= cursor.getString(28);
						
						if(TextUtils.isEmpty(cursor.getString(29)))
							productDO.distributionCode			= "";
						else 
							productDO.distributionCode			= cursor.getString(29);
						productDO.affectedStock				= cursor.getInt(30);
						productDO.status					= cursor.getInt(31);
						productDO.promoID					= cursor.getInt(32);
						productDO.promoType					= cursor.getString(33);
						productDO.createdOn					= cursor.getString(34);
						productDO.trxStatus					= cursor.getInt(35);
						productDO.expiryDate				= cursor.getString(36);
						if(!TextUtils.isEmpty(productDO.expiryDate))
							productDO.expiryDate=CalendarUtils.getOrderPostDate();
						productDO.relatedLineID				= cursor.getInt(37);
						productDO.itemGroupLevel5			= cursor.getString(38);
						productDO.taxType					= cursor.getInt(39);
						productDO.suggestedBU				= cursor.getInt(40);
						productDO.pushedOn					= cursor.getString(41);
						productDO.modifiedDate				= cursor.getString(42);
						productDO.modifiedTime				= cursor.getString(43);
						
						productDO.reason					= cursor.getString(44);
						productDO.cancelledQuantity			= cursor.getInt(45);
						productDO.inProcessQuantity			= cursor.getInt(46);
						productDO.shippedQuantity			= cursor.getInt(47);
						productDO.missedBU					= cursor.getInt(48);
						productDO.batchCode					= cursor.getString(49);
						productDO.barCode					= cursor.getString(50);
						productDO.vatPercentage					= cursor.getFloat(51);
						productDO.VATAmountNew					= cursor.getFloat(52);

						LogUtils.debug("missed_bu_itemCode", ""+productDO.itemCode);
						LogUtils.debug("missed_bu_missedBU", ""+productDO.missedBU);
						
						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);
						
						Cursor cursorsUoms=sqLiteDatabase.rawQuery(String.format(uomQuery,productDO.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
								
								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								productDO.hashArrUoms.put(key,conversionFactorDO);
								productDO.arrUoms.add(conversionFactorDO.UOM);
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();
						
						if(productDO.hashArrUoms.containsKey(productDO.itemCode+productDO.UOM) && productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion>0)
							productDO.requestedSalesBU			= (productDO.missedBU+productDO.quantityBU)/*/productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion*/;
						else
							productDO.requestedSalesBU = productDO.quantityLevel1;
						
						if(productDO.quantityBU>0)
							vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				vecItemList.clear();
			}
	
			return vecItemList;
		}
	}
	public ArrayList<TrxDetailsDO> getProductsOfOrderFOC(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock)
		{
			ArrayList<TrxDetailsDO> vecItemList = new ArrayList<TrxDetailsDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();

				String query = "SELECT TD.LineNo, TD.TrxCode, TD.ItemCode, TD.OrgCode, TD.TrxReasonCode, TD.TrxDetailsNote," +
						"TD.ItemType, TD.BasePrice, TD.UOM, TD.QuantityLevel1, TD.QuantityLevel2,TD.QuantityLevel3, " +
						"TD.QuantityBU, TD.RequestedBU, TD.ApprovedBU, TD.CollectedBU,TD.FinalBU, TD.PriceUsedLevel1, " +
						"TD.PriceUsedLevel2, TD.PriceUsedLevel3,TD.TaxPercentage, TD.TotalDiscountPercentage, " +
						"TD.TotalDiscountAmount,TD.CalculatedDiscountPercentage, TD.CalculatedDiscountAmount," +
						"TD.UserDiscountPercentage, TD.UserDiscountAmount, TD.ItemDescription,TD.ItemAltDescription, " +
						"TD.DistributionCode, TD.AffectedStock, TD.Status,TD.PromoID, TD.PromoType, TD.CreatedOn, " +
						"TD.TRXStatus, TD.ExpiryDate,TD.RelatedLineID, TD.ItemGroupLevel5, TD.TaxType, TD.SuggestedBU, " +
						"TD.PushedOn,TD.ModifiedDate, TD.ModifiedTime, TD.Reason, TD.CancelledQuantity,TD.InProcessQuantity, " +
						"TD.ShippedQuantity, TD.MissedBU, TD.BatchNumber, UF.BarCode  " +
//						", TD.VAT,TD.TotalVATAmount " +
						"FROM tblTrxDetail TD " +
						"INNER JOIN tblProducts TP ON TP.ItemCode=TD.ItemCode " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode AND TD.UOM=UF.UOM " +
						"WHERE TrxCode='"+orderID+"' " +
						"Order by TP.DisplayOrder ASC";

				String uomQuery= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";

				cursor = sqLiteDatabase.rawQuery(query , null);

				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO productDO 				= 	new TrxDetailsDO();

						productDO.lineNo					= cursor.getInt(0);
						productDO.trxCode					= cursor.getString(1);
						productDO.itemCode					= cursor.getString(2);
						productDO.orgCode					= cursor.getString(3);
						productDO.trxReasonCode				= cursor.getString(4);
						productDO.trxDetailsNote			= cursor.getString(5);
						productDO.itemType					= cursor.getString(6);
						productDO.basePrice					= cursor.getFloat(7);
						productDO.UOM						= cursor.getString(8);
						productDO.quantityLevel1			= cursor.getFloat(9);
						productDO.quantityLevel2			= cursor.getFloat(10);
						productDO.quantityLevel3			= cursor.getInt(11);

						productDO.quantityBU				= cursor.getInt(12);
						productDO.requestedBU				= cursor.getInt(13);

						productDO.approvedBU				= cursor.getInt(14);
						productDO.collectedBU				= cursor.getInt(15);

						productDO.finalBU					= cursor.getInt(16);
						productDO.priceUsedLevel1			= cursor.getFloat(17);
						productDO.priceUsedLevel2			= cursor.getFloat(18);
						productDO.priceUsedLevel3			= cursor.getFloat(19);


						productDO.taxPercentage				= cursor.getFloat(20);
						productDO.totalDiscountPercentage	= cursor.getFloat(21);
						productDO.totalDiscountAmount		= cursor.getFloat(22);
						productDO.calculatedDiscountPercentage= cursor.getFloat(23);
						productDO.calculatedDiscountAmount	= cursor.getFloat(24);


						productDO.userDiscountPercentage	= cursor.getFloat(25);
						productDO.userDiscountAmount		= cursor.getFloat(26);
						productDO.itemDescription			= cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							productDO.itemAltDescription			= "";
						else
							productDO.itemAltDescription			= cursor.getString(28);

						if(TextUtils.isEmpty(cursor.getString(29)))
							productDO.distributionCode			= "";
						else
							productDO.distributionCode			= cursor.getString(29);
						productDO.affectedStock				= cursor.getInt(30);
						productDO.status					= cursor.getInt(31);
						productDO.promoID					= cursor.getInt(32);
						productDO.promoType					= cursor.getString(33);
						productDO.createdOn					= cursor.getString(34);
						productDO.trxStatus					= cursor.getInt(35);
						productDO.expiryDate				= cursor.getString(36);
						if(!TextUtils.isEmpty(productDO.expiryDate))
							productDO.expiryDate=CalendarUtils.getOrderPostDate();
						productDO.relatedLineID				= cursor.getInt(37);
						productDO.itemGroupLevel5			= cursor.getString(38);
						productDO.taxType					= cursor.getInt(39);
						productDO.suggestedBU				= cursor.getInt(40);
						productDO.pushedOn					= cursor.getString(41);
						productDO.modifiedDate				= cursor.getString(42);
						productDO.modifiedTime				= cursor.getString(43);

						productDO.reason					= cursor.getString(44);
						productDO.cancelledQuantity			= cursor.getInt(45);
						productDO.inProcessQuantity			= cursor.getInt(46);
						productDO.shippedQuantity			= cursor.getInt(47);
						productDO.missedBU					= cursor.getInt(48);
						productDO.batchCode					= cursor.getString(49);
						productDO.barCode					= cursor.getString(50);
//						productDO.vatPercentage					= cursor.getFloat(51);
//						productDO.VATAmountNew					= cursor.getFloat(52);

						LogUtils.debug("missed_bu_itemCode", ""+productDO.itemCode);
						LogUtils.debug("missed_bu_missedBU", ""+productDO.missedBU);

						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);

						Cursor cursorsUoms=sqLiteDatabase.rawQuery(String.format(uomQuery,productDO.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);

								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								productDO.hashArrUoms.put(key,conversionFactorDO);
								productDO.arrUoms.add(conversionFactorDO.UOM);
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();

						if(productDO.hashArrUoms.containsKey(productDO.itemCode+productDO.UOM) && productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion>0)
							productDO.requestedSalesBU			= (productDO.missedBU+productDO.quantityBU)/*/productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion*/;
						else
							productDO.requestedSalesBU = productDO.quantityLevel1;

						if(productDO.quantityBU>0)
							vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}

				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
//				vecItemList.clear();
			}

			return vecItemList;
		}
	}
	public ArrayList<TrxDetailsDO> getProductsOfOrdernew(SQLiteDatabase sqLiteDatabase, String orderID, float totalAmount)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<TrxDetailsDO> vecItemList = new ArrayList<TrxDetailsDO>();
			Cursor cursor = null;
			float caclculateAmnt=0;
			
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT TD.LineNo, TD.TrxCode, TD.ItemCode, TD.OrgCode, TD.TrxReasonCode, TD.TrxDetailsNote," +
						"TD.ItemType, TD.BasePrice, TD.UOM, TD.QuantityLevel1, TD.QuantityLevel2,TD.QuantityLevel3, " +
						"TD.QuantityBU, TD.RequestedBU, TD.ApprovedBU, TD.CollectedBU,TD.FinalBU, TD.PriceUsedLevel1, " +
						"TD.PriceUsedLevel2, TD.PriceUsedLevel3,TD.TaxPercentage, TD.TotalDiscountPercentage, " +
						"TD.TotalDiscountAmount,TD.CalculatedDiscountPercentage, TD.CalculatedDiscountAmount," +
						"TD.UserDiscountPercentage, TD.UserDiscountAmount, TD.ItemDescription,TD.ItemAltDescription, " +
						"TD.DistributionCode, TD.AffectedStock, TD.Status,TD.PromoID, TD.PromoType, TD.CreatedOn, " +
						"TD.TRXStatus, TD.ExpiryDate,TD.RelatedLineID, TD.ItemGroupLevel5, TD.TaxType, TD.SuggestedBU, " +
						"TD.PushedOn,TD.ModifiedDate, TD.ModifiedTime, TD.Reason, TD.CancelledQuantity,TD.InProcessQuantity, " +
						"TD.ShippedQuantity, TD.MissedBU, TD.BatchNumber, UF.BarCode,TD.VAT,TD.TotalVATAmount  " +
						"FROM tblTrxDetail TD " +
						"INNER JOIN tblProducts TP ON TP.ItemCode=TD.ItemCode " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode AND TD.UOM=UF.UOM " +
						"WHERE TrxCode='"+orderID+"' " +
						"Order by TP.DisplayOrder ASC";
				
				String uomQuery= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";
				
				cursor = sqLiteDatabase.rawQuery(query , null);
			
				
				if(cursor.moveToFirst())
				{
					do
					{
						
						TrxDetailsDO productDO 				= 	new TrxDetailsDO();
						
						productDO.lineNo					= cursor.getInt(0);
						productDO.trxCode					= cursor.getString(1);
						productDO.itemCode					= cursor.getString(2);
						productDO.orgCode					= cursor.getString(3);
						productDO.trxReasonCode				= cursor.getString(4);
						productDO.trxDetailsNote			= cursor.getString(5);
						productDO.itemType					= cursor.getString(6);
						productDO.basePrice					= cursor.getFloat(7);
						productDO.UOM						= cursor.getString(8);
						productDO.quantityLevel1			= cursor.getFloat(9);
						productDO.quantityLevel2			= cursor.getFloat(10);
						productDO.quantityLevel3			= cursor.getInt(11);
						
						productDO.quantityBU				= cursor.getInt(12);
						productDO.requestedBU				= cursor.getInt(13);
						
						productDO.approvedBU				= cursor.getInt(14);
						productDO.collectedBU				= cursor.getInt(15);
						
						productDO.finalBU					= cursor.getInt(16);
						productDO.priceUsedLevel1			= cursor.getFloat(17);
						productDO.priceUsedLevel2			= cursor.getFloat(18);
						productDO.priceUsedLevel3			= cursor.getFloat(19);
						
//						caclculateAmnt += productDO.basePrice * productDO.quantityBU;
						
					
						
						
						productDO.taxPercentage				= cursor.getFloat(20);
						productDO.totalDiscountPercentage	= cursor.getFloat(21);
						productDO.totalDiscountAmount		= cursor.getFloat(22);
						productDO.calculatedDiscountPercentage= cursor.getFloat(23);
						productDO.calculatedDiscountAmount	= cursor.getFloat(24);
						
						
						productDO.userDiscountPercentage	= cursor.getFloat(25);
						productDO.userDiscountAmount		= cursor.getFloat(26);
						productDO.itemDescription			= cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							productDO.itemAltDescription			= "";
						else 
							productDO.itemAltDescription			= cursor.getString(28);
						
						if(TextUtils.isEmpty(cursor.getString(29)))
							productDO.distributionCode			= "";
						else 
							productDO.distributionCode			= cursor.getString(29);
						productDO.affectedStock				= cursor.getInt(30);
						productDO.status					= cursor.getInt(31);
						productDO.promoID					= cursor.getInt(32);
						productDO.promoType					= cursor.getString(33);
						productDO.createdOn					= cursor.getString(34);
						productDO.trxStatus					= cursor.getInt(35);
						productDO.expiryDate				= cursor.getString(36);
						if(!TextUtils.isEmpty(productDO.expiryDate))
							productDO.expiryDate=CalendarUtils.getOrderPostDate();
						productDO.relatedLineID				= cursor.getInt(37);
						productDO.itemGroupLevel5			= cursor.getString(38);
						productDO.taxType					= cursor.getInt(39);
						productDO.suggestedBU				= cursor.getInt(40);
						productDO.pushedOn					= cursor.getString(41);
						productDO.modifiedDate				= cursor.getString(42);
						productDO.modifiedTime				= cursor.getString(43);
						
						productDO.reason					= cursor.getString(44);
						productDO.cancelledQuantity			= cursor.getInt(45);
						productDO.inProcessQuantity			= cursor.getInt(46);
						productDO.shippedQuantity			= cursor.getInt(47);
						productDO.missedBU					= cursor.getInt(48);
						productDO.batchCode					= cursor.getString(49);
						productDO.barCode					= cursor.getString(50);
						productDO.vatPercentage					= cursor.getFloat(51);
						productDO.VATAmountNew					= cursor.getFloat(52);

						LogUtils.debug("missed_bu_itemCode", ""+productDO.itemCode);
						LogUtils.debug("missed_bu_missedBU", ""+productDO.missedBU);
						
						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);
						
						Cursor cursorsUoms=sqLiteDatabase.rawQuery(String.format(uomQuery,productDO.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
								
								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								productDO.hashArrUoms.put(key,conversionFactorDO);
								productDO.arrUoms.add(conversionFactorDO.UOM);
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();
						
						if(productDO.hashArrUoms.containsKey(productDO.itemCode+productDO.UOM) && productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion>0)
							productDO.requestedSalesBU			= (productDO.missedBU+productDO.quantityBU)/*/productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion*/;
						else
							productDO.requestedSalesBU = productDO.quantityLevel1;
						
						if(productDO.quantityBU>0 )
							vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				vecItemList.clear();
			}
//			if((int)totalAmount > (int)caclculateAmnt)
//				vecItemList.clear();
				
				return vecItemList;
		}
	}
	
	public ArrayList<TrxDetailsDO> getProductsOfOrderSummary(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<TrxDetailsDO> vecItemList = new ArrayList<TrxDetailsDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT TD.LineNo, TD.TrxCode, TD.ItemCode, TD.OrgCode, TD.TrxReasonCode, TD.TrxDetailsNote," +
						"TD.ItemType, TD.BasePrice, TD.UOM, TD.QuantityLevel1, TD.QuantityLevel2,TD.QuantityLevel3, " +
						"TD.QuantityBU, TD.RequestedBU, TD.ApprovedBU, TD.CollectedBU,TD.FinalBU, TD.PriceUsedLevel1, " +
						"TD.PriceUsedLevel2, TD.PriceUsedLevel3,TD.TaxPercentage, TD.TotalDiscountPercentage, " +
						"TD.TotalDiscountAmount,TD.CalculatedDiscountPercentage, TD.CalculatedDiscountAmount," +
						"TD.UserDiscountPercentage, TD.UserDiscountAmount, TD.ItemDescription,TD.ItemAltDescription, " +
						"TD.DistributionCode, TD.AffectedStock, TD.Status,TD.PromoID, TD.PromoType, TD.CreatedOn, " +
						"TD.TRXStatus, TD.ExpiryDate,TD.RelatedLineID, TD.ItemGroupLevel5, TD.TaxType, TD.SuggestedBU, " +
						"TD.PushedOn,TD.ModifiedDate, TD.ModifiedTime, TD.Reason, TD.CancelledQuantity,TD.InProcessQuantity, " +
						"TD.ShippedQuantity, TD.MissedBU, TD.BatchNumber, UF.BarCode, TP.DisplayOrder,TD.VAT,TD.TotalVATAmount " +
						"FROM tblTrxDetail TD " +
						"INNER JOIN tblProducts TP ON TP.ItemCode=TD.ItemCode " +
						"INNER JOIN tblUOMFactor UF ON TP.ItemCode=UF.ItemCode " +
						"WHERE TrxCode='"+orderID+"' " +
						"GROUP BY TP.ItemCode " +
						"Order by TP.DisplayOrder ASC";
				
				String uomQuery= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where ItemCode='%s'";
				
				cursor = sqLiteDatabase.rawQuery(query , null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						TrxDetailsDO productDO 				= 	new TrxDetailsDO();
						
						productDO.lineNo					= cursor.getInt(0);
						productDO.trxCode					= cursor.getString(1);
						productDO.itemCode					= cursor.getString(2);
						productDO.orgCode					= cursor.getString(3);
						productDO.trxReasonCode				= cursor.getString(4);
						productDO.trxDetailsNote			= cursor.getString(5);
						productDO.itemType					= cursor.getString(6);
						productDO.basePrice					= cursor.getFloat(7);
						productDO.UOM						= cursor.getString(8);
						productDO.quantityLevel1			= cursor.getFloat(9);
						productDO.quantityLevel2			= cursor.getFloat(10);
						productDO.quantityLevel3			= cursor.getInt(11);
						
						productDO.quantityBU				= cursor.getInt(12);
						productDO.requestedBU				= cursor.getInt(13);
						
						productDO.approvedBU				= cursor.getInt(14);
						productDO.collectedBU				= cursor.getInt(15);
						
						productDO.finalBU					= cursor.getInt(16);
						productDO.priceUsedLevel1			= cursor.getFloat(17);
						productDO.priceUsedLevel2			= cursor.getFloat(18);
						productDO.priceUsedLevel3			= cursor.getFloat(19);
						
						
						productDO.taxPercentage				= cursor.getFloat(20);
						productDO.totalDiscountPercentage	= cursor.getFloat(21);
						productDO.promotionalDiscountAmount	= cursor.getFloat(22);
						productDO.calculatedDiscountPercentage= cursor.getFloat(23);
						productDO.calculatedDiscountAmount	= cursor.getFloat(24);
						productDO.statementDiscountAmnt	= cursor.getFloat(24);

						
						productDO.userDiscountPercentage	= cursor.getFloat(25);
						productDO.userDiscountAmount		= cursor.getFloat(26);
						productDO.itemDescription			= cursor.getString(27);
						if(TextUtils.isEmpty(cursor.getString(28)))
							productDO.itemAltDescription			= "";
						else 
							productDO.itemAltDescription			= cursor.getString(28);
						
						if(TextUtils.isEmpty(cursor.getString(29)))
							productDO.distributionCode			= "";
						else 
							productDO.distributionCode			= cursor.getString(29);
						productDO.affectedStock				= cursor.getInt(30);
						productDO.status					= cursor.getInt(31);
						productDO.promoID					= cursor.getInt(32);
						productDO.promoType					= cursor.getString(33);
						productDO.createdOn					= cursor.getString(34);
						productDO.trxStatus					= cursor.getInt(35);
						productDO.expiryDate				= cursor.getString(36);
						if(!TextUtils.isEmpty(productDO.expiryDate))
							productDO.expiryDate=CalendarUtils.getOrderPostDate();
						productDO.relatedLineID				= cursor.getInt(37);
						productDO.itemGroupLevel5			= cursor.getString(38);
						productDO.taxType					= cursor.getInt(39);
						productDO.suggestedBU				= cursor.getInt(40);
						productDO.pushedOn					= cursor.getString(41);
						productDO.modifiedDate				= cursor.getString(42);
						productDO.modifiedTime				= cursor.getString(43);
						
						productDO.reason					= cursor.getString(44);
						productDO.cancelledQuantity			= cursor.getInt(45);
						productDO.inProcessQuantity			= cursor.getInt(46);
						productDO.shippedQuantity			= cursor.getInt(47);
						productDO.missedBU					= cursor.getInt(48);
						productDO.batchCode					= cursor.getString(49);
						productDO.barCode					= cursor.getString(50);
						productDO.DisplayOrder				= cursor.getInt(51);
						productDO.vatPercentage				= cursor.getFloat(52);
						productDO.VATAmountNew				= cursor.getFloat(53);

						LogUtils.debug("missed_bu_itemCode", ""+productDO.itemCode);
						LogUtils.debug("missed_bu_missedBU", ""+productDO.missedBU);
						
						productDO.vecDamageImages	 		= getDamageIMagePic(sqLiteDatabase, orderID, productDO.itemCode);
						
						Cursor cursorsUoms=sqLiteDatabase.rawQuery(String.format(uomQuery,productDO.itemCode), null);
						if(cursorsUoms.moveToFirst()){
							do {
								UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
								conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
								conversionFactorDO.UOM			=	cursorsUoms.getString(1);
								conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
								conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
								
								String key = conversionFactorDO.ItemCode+conversionFactorDO.UOM;
								productDO.hashArrUoms.put(key,conversionFactorDO);
								productDO.arrUoms.add(conversionFactorDO.UOM);
							} while (cursorsUoms.moveToNext());
						}
						if(cursorsUoms!=null && !cursorsUoms.isClosed())
							cursorsUoms.close();
						
						if(productDO.hashArrUoms.containsKey(productDO.itemCode+productDO.UOM) && productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion>0)
							productDO.requestedSalesBU			= (productDO.missedBU+productDO.quantityBU)/*/productDO.hashArrUoms.get(productDO.itemCode+productDO.UOM).eaConversion*/;
						else
							productDO.requestedSalesBU = productDO.quantityLevel1;
						

						if(productDO.quantityBU>0 || productDO.missedBU>0 )
							vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return vecItemList;
		}
	}
	
	public Vector<DamageImageDO> getDamageIMagePic(SQLiteDatabase sqLiteDatabase, String orderID, String itemCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<DamageImageDO> vecImage = new Vector<DamageImageDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
				cursor = sqLiteDatabase.rawQuery("SELECT *FROM tblOrderImage where OrderNo ='"+orderID+"' AND ItemCode ='"+itemCode+"'", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						DamageImageDO damageImageDO = new DamageImageDO();
						damageImageDO.OrderNo      = cursor.getString(0);
						damageImageDO.ItemCode     = cursor.getString(1);
						damageImageDO.LineNo       = cursor.getString(2);
						damageImageDO.ImagePath    = cursor.getString(3);
						damageImageDO.CapturedDate = cursor.getString(4);
						vecImage.add(damageImageDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return vecImage;
		}
	}
	
	public ArrayList<TrxPromotionDO> getTRXPromotions(SQLiteDatabase sqLiteDatabase, String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<TrxPromotionDO> vecItemList = new ArrayList<TrxPromotionDO>();
			Cursor cursor = null;
			try
			{
				if(sqLiteDatabase == null || !sqLiteDatabase.isOpen())
					sqLiteDatabase = DatabaseHelper.openDataBase();
				
			
				String query = "SELECT TrxCode, ItemCode, DiscountAmount, DiscountPercentage, OrgCode, PromotionID,FactSheetCode, "+
							   "Status, CreatedOn, TrxStatus, PromotionType, TrxDetailsLineNo, ItemType, IsStructural " +
							   "FROM tblTrxPromotion where TrxCode ='"+orderID+"'";
				
				cursor = sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						TrxPromotionDO productDO 		= 	new TrxPromotionDO();
						
						productDO.trxCode				= cursor.getString(1);
						productDO.itemCode				= cursor.getString(2);
						productDO.discountAmount		= cursor.getFloat(3);
						productDO.discountPercentage	= cursor.getFloat(4);
						productDO.orgCode				= cursor.getString(5);
						productDO.promotionID			= cursor.getInt(5);
						productDO.factSheetCode			= cursor.getString(6);
						productDO.status				= cursor.getInt(7);
						productDO.createdOn				= cursor.getString(8);
						productDO.trxStatus				= cursor.getInt(9);
						productDO.promotionType			= cursor.getInt(10);
						productDO.trxDetailsLineNo		= cursor.getInt(11);
						productDO.itemType				= cursor.getString(12);
						productDO.isStructural			= cursor.getInt(13);
						
						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	
			return vecItemList;
		}
	}
	public boolean updatePaymentStatus(PostPaymentDONew objPaymentDO)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentHeader set Status = ? where ReceiptId =?");
				
				stmtUpdateOrder.bindString(1, "1");
				stmtUpdateOrder.bindString(2, objPaymentDO.ReceiptId);
				stmtUpdateOrder.execute();
				
				stmtUpdateOrder.close();
				
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	public boolean updatePaymentStatus(Vector<PostPaymentDONew> vecPayments)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdateOrder = objSqliteDB.compileStatement("Update tblPaymentHeader set Status = ? where ReceiptId =?");
				for(PostPaymentDONew objPaymentDO : vecPayments)
				{
					
					stmtUpdateOrder.bindString(1, "1");
					stmtUpdateOrder.bindString(2, objPaymentDO.ReceiptId);
					stmtUpdateOrder.execute();
				}
				stmtUpdateOrder.close();
				
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updatePayments(Vector<PaymentResponseDo> vecPayments, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				//updating the table tblPaymentHeader
				SQLiteStatement stmtUpdatePayment 	    = objSqliteDB.compileStatement("UPDATE tblPaymentHeader SET ReceiptId =?, status =? where ReceiptId=?");
				//updating the table tblPaymentDetail
				SQLiteStatement stmtUpdatePaymentDetail = objSqliteDB.compileStatement("UPDATE tblPaymentDetail SET ReceiptNo =? where ReceiptNo=?");
				SQLiteStatement stmtUpdatePaymentDetail1 = objSqliteDB.compileStatement("UPDATE tblPaymentDetail SET ReceiptNo =?, status =? where ReceiptNo=?");

				SQLiteStatement stmtUpdateInvoiceDetail = objSqliteDB.compileStatement("UPDATE tblPaymentInvoice SET ReceiptId =? where ReceiptId=?");
				
				
				for(PaymentResponseDo objAllUsersDo : vecPayments)
				{
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.receiptNumber);
					LogUtils.errorLog("objAllUsersDo.New", ""+objAllUsersDo.receiptNumber);
					if(!(objAllUsersDo.Status == -1))
					{
						stmtUpdatePayment.bindString(1, objAllUsersDo.receiptNumber);
						stmtUpdatePayment.bindString(2, "1");
						stmtUpdatePayment.bindString(3, objAllUsersDo.receiptNumber);
						stmtUpdatePayment.execute();

						stmtUpdatePaymentDetail1.bindString(1,  objAllUsersDo.receiptNumber);
						stmtUpdatePaymentDetail1.bindString(2,  "1");
						stmtUpdatePaymentDetail1.bindString(3, objAllUsersDo.receiptNumber);
						stmtUpdatePaymentDetail1.execute();
					}
					else
					{
						String strReceiptNo = "";
						String type = "";
//						if(objAllUsersDo.Division <= 0)
//							type = AppConstants.Receipt;
//						else
//							type = AppConstants.Food_Receipt;
						if(objAllUsersDo.Division <= 0)
							type = AppConstants.Receipt;
						else if(objAllUsersDo.Division == 1)
							type = AppConstants.Food_Receipt;
						else
							type = AppConstants.TPT_Receipt;
						
						String query = "SELECT id from tblOfflineData where Type ='"+type+"' AND status = 0  AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
							strReceiptNo = cursor.getString(0);
						
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strReceiptNo+"'");
					
						stmtUpdatePayment.bindString(1, strReceiptNo);
						stmtUpdatePayment.bindString(2, "0");
						stmtUpdatePayment.bindString(3, objAllUsersDo.receiptNumber);
						
						stmtUpdatePaymentDetail.bindString(1, strReceiptNo);
						stmtUpdatePaymentDetail.bindString(2, objAllUsersDo.receiptNumber);
						
						
						stmtUpdateInvoiceDetail.bindString(1, strReceiptNo);
						stmtUpdateInvoiceDetail.bindString(2, objAllUsersDo.receiptNumber);
						
						stmtUpdatePayment.execute();
						stmtUpdatePaymentDetail.execute();
						stmtUpdateInvoiceDetail.execute();
					}
				}
				
				stmtUpdatePayment.close();
				stmtUpdatePaymentDetail.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	public boolean updateOrderNumbers(Vector<AllUsersDo> vecOrderlist, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteStatement stmtUpdateOrder = null , stmtUpdateDetail = null, 
					stmtUpdateInvoiceNo = null, stmtUpdateOrder_empty = null, stmtUpdatePromo = null,stmtUpdateModifiedOrder=null;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				//updating the table tblOrderHeader
				stmtUpdateOrder = objSqliteDB.compileStatement("UPDATE tblTrxHeader SET Status = ?,ClientSignature=?,SalesmanSignature=? where TrxCode=?");
				
				stmtUpdateModifiedOrder = objSqliteDB.compileStatement("UPDATE tblTrxHeader SET Status = ?,ClientSignature=?,SalesmanSignature=?,TRXStatus=? where TrxCode=?");
				
				stmtUpdateOrder_empty = objSqliteDB.compileStatement("UPDATE tblTrxHeader SET TrxCode =? where TrxCode=?");
				
				//updating the table tblOrderDetail
				stmtUpdateDetail = objSqliteDB.compileStatement("UPDATE tblTrxDetail SET TrxCode =? where TrxCode=?");
				
				//updating the table tblTrxPromotion
				stmtUpdatePromo = objSqliteDB.compileStatement("UPDATE tblTrxPromotion SET TrxCode =? where TrxCode=?");
				
				//updating the table tblPaymentInvoice
				stmtUpdateInvoiceNo = objSqliteDB.compileStatement("UPDATE tblPaymentInvoice SET TrxCode =? where TrxCode=?");
				
				for(AllUsersDo objAllUsersDo : vecOrderlist)
				{
					LogUtils.errorLog("objAllUsersDo.type", ""+objAllUsersDo.strOrderType);
					LogUtils.errorLog("objAllUsersDo.Old", ""+objAllUsersDo.strOldOrderNumber);
					
					if(objAllUsersDo.pushStatus != -1)
					{
						stmtUpdateOrder.bindString(1, ""+objAllUsersDo.pushStatus);
						stmtUpdateOrder.bindString(2, "");
						stmtUpdateOrder.bindString(3, "");
						stmtUpdateOrder.bindString(4, objAllUsersDo.strOldOrderNumber);
						stmtUpdateOrder.execute();
					}
					else
					{
						int[] TRX_TYPE = getTrxTypeById(objAllUsersDo.strOldOrderNumber, objSqliteDB);
						//////////////////////////////////
						String orderId = "", query = "", type = "";
						
						if(objSqliteDB == null || !objSqliteDB.isOpen())
							objSqliteDB = DatabaseHelper.openDataBase();
						
						if(TRX_TYPE[0] == TrxHeaderDO.get_TRXTYPE_RETURN_ORDER())
						{
							if(objAllUsersDo.Division <= 0)
								type = AppConstants.GRV;
							else if(objAllUsersDo.Division == 1)
								type = AppConstants.Food_GRV;
							else
								type = AppConstants.TPT_GRV;

						}
						else
						{
							if(TRX_TYPE[0] == TrxHeaderDO.get_TRXTYPE_SALES_ORDER() && TRX_TYPE[1] == TrxHeaderDO.get_TRX_STATUS_SAVED()) {
//								if(objAllUsersDo.Division <= 0)
//									type = AppConstants.SAVED;
//								else
//									type = AppConstants.Food_SAVED;
								if(objAllUsersDo.Division <= 0)
									type = AppConstants.SAVED;
								else if(objAllUsersDo.Division == 1)
									type = AppConstants.Food_SAVED;
								else
									type = AppConstants.TPT_SAVED;
							}
							else if(TRX_TYPE[0] == TrxHeaderDO.get_TYPE_FREE_DELIVERY() || TRX_TYPE[0] == TrxHeaderDO.get_TYPE_FREE_ORDER()) {
//								if(objAllUsersDo.Division <= 0)
//									type = AppConstants.FOC;
//								else
//									type = AppConstants.Food_FOC;
								if(objAllUsersDo.Division <= 0)
									type = AppConstants.FOC;
								else if(objAllUsersDo.Division == 1)
									type = AppConstants.Food_FOC;
								else
									type = AppConstants.TPT_FOC;

							}
							else {
//								if(objAllUsersDo.Division <= 0)
//									type = AppConstants.Order;
//								else
//									type = AppConstants.Food_Order;
								if(objAllUsersDo.Division <= 0)
									type = AppConstants.Order;
								else if(objAllUsersDo.Division == 1)
									type = AppConstants.Food_Order;
								else
									type = AppConstants.TPT_Order;
							}
						}
						
						query = "SELECT id from tblOfflineData where Type ='"+type+"' AND status = 0 AND id NOT IN(SELECT TrxCode FROM tblTrxHeader) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							orderId = cursor.getString(0);
						}
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+orderId+"'");
						
						Log.e("orderId", "orderId "+orderId);
						stmtUpdateOrder_empty.bindString(1, orderId);
						stmtUpdateOrder_empty.bindString(2, objAllUsersDo.strOldOrderNumber);
						
						stmtUpdateDetail.bindString(1, orderId);
						stmtUpdateDetail.bindString(2, objAllUsersDo.strOldOrderNumber);
						
						
						stmtUpdatePromo.bindString(1, orderId);
						stmtUpdatePromo.bindString(2, objAllUsersDo.strOldOrderNumber);
						
						stmtUpdateInvoiceNo.bindString(1, orderId);
						stmtUpdateInvoiceNo.bindString(2, objAllUsersDo.strOldOrderNumber);
						
						stmtUpdateOrder_empty.execute();
						stmtUpdateDetail.execute();
						stmtUpdatePromo.execute();
						stmtUpdateInvoiceNo.execute();
					}
				}
				
				stmtUpdateDetail.close();
				stmtUpdateOrder.close();
				stmtUpdateInvoiceNo.close();
				stmtUpdateOrder_empty.close();
				
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}
	
	private int[] getTrxTypeById(String strOldOrderNumber, SQLiteDatabase objSqliteDB) 
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			int[] trxType   = {0,0};
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				
				cursor = objSqliteDB.rawQuery("SELECT TrxType,TRXStatus FROM tblTrxHeader WHERE TrxCode = '"+strOldOrderNumber+"'", null);
				
				if(cursor.moveToNext())
				{
					trxType[0] = cursor.getInt(0);
					trxType[1] = cursor.getInt(1);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return trxType;
		}
	}
	
	private void updateAdvanceOrderInventory(String orderID, SQLiteDatabase objSqliteDB)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			String deliveryDate = "";
			String empNo = "";
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct;
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				cursor = objSqliteDB.rawQuery("SELECT DeliveryDate, EmpNo from tblOrderHeader where OrderId ='"+orderID+"' and SubType = 'Advance Order'", null);
				if(cursor.moveToNext())
				{
					do 
					{
						deliveryDate = cursor.getString(0);
						empNo		 = cursor.getString(1);		
						vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
						vecOrdProduct = getAddvanceOrderProductDetail(objSqliteDB, orderID);
						new CommonDA().insertCheckInDemandInventoryNew(objSqliteDB, empNo, deliveryDate, vecOrdProduct, 0);
					} while (cursor.moveToNext());
					if(deliveryDate != null && !deliveryDate.equals(""))
					{
						
					}
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	public Vector<NameIDDo> getReasonsByType(String strType)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<NameIDDo> vecReasons = new Vector<NameIDDo>();
			String strQuery = "Select ReasonId,Name,Type from tblReasons where Type like '%"+strType+"%'";
				
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 
				 cursor 				= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 NameIDDo objReasons 	=  new NameIDDo();
						 objReasons.strId 		=  cursor.getString(0);
						 objReasons.strName		=  cursor.getString(1);
						 objReasons.strType		=  cursor.getString(2);
						 vecReasons.add(objReasons);
					 }
					 while(cursor.moveToNext());
					 
					 
					 if(cursor!=null && !cursor.isClosed())
						 cursor.close();
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return vecReasons;
		}
	}
	public boolean insertItemPricing(Vector<PricingDO> vecPricingDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblPricing WHERE ITEMCODE =? and CUSTOMERPRICINGKEY = ? AND UOM=?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblPricing (ITEMCODE, CUSTOMERPRICINGKEY, PRICECASES,ENDDATE,STARTDATE,DISCOUNT,IsExpired,emptyCasePrice, TaxGroupCode,TaxPercentage,ModifiedDate, ModifiedTime, UOM) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblPricing set  PRICECASES=?,ENDDATE=?, STARTDATE=?, DISCOUNT=?,IsExpired=?,emptyCasePrice=?,TaxGroupCode=?,TaxPercentage=?,ModifiedDate=?, ModifiedTime=? WHERE ITEMCODE =? and CUSTOMERPRICINGKEY=? AND UOM=?");
				
				for(PricingDO pricingDO :  vecPricingDOs)
				{
					stmtSelectRec.bindString(1, pricingDO.itemCode);
					stmtSelectRec.bindString(2, pricingDO.customerPricingClass);
					stmtSelectRec.bindString(3, pricingDO.UOM);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, pricingDO.priceCases);
						stmtUpdate.bindString(2, pricingDO.endDate);
						stmtUpdate.bindString(3, pricingDO.startDate);
						stmtUpdate.bindString(4, pricingDO.dicount);
						stmtUpdate.bindString(5, pricingDO.IsExpired);
						stmtUpdate.bindString(6, pricingDO.emptyCasePrice);
						
						stmtUpdate.bindString(7, pricingDO.TaxGroupCode);
						stmtUpdate.bindString(8, pricingDO.TaxPercentage);
						stmtUpdate.bindString(9, pricingDO.ModifiedDate);
						stmtUpdate.bindString(10, pricingDO.ModifiedTime);
						
						stmtUpdate.bindString(11, pricingDO.itemCode);
						stmtUpdate.bindString(12, ""+pricingDO.customerPricingClass);
						stmtUpdate.bindString(13, ""+pricingDO.UOM);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, pricingDO.itemCode);
						stmtInsert.bindString(2, pricingDO.customerPricingClass);
						stmtInsert.bindString(3, pricingDO.priceCases);
						stmtInsert.bindString(4, pricingDO.endDate);
						stmtInsert.bindString(5, pricingDO.startDate);
						stmtInsert.bindString(6, pricingDO.dicount);
						stmtInsert.bindString(7, pricingDO.IsExpired);
						stmtInsert.bindString(8, ""+pricingDO.emptyCasePrice);
						
						stmtInsert.bindString(9, ""+pricingDO.TaxGroupCode);
						stmtInsert.bindString(10, ""+pricingDO.TaxPercentage);
						stmtInsert.bindString(11, ""+pricingDO.ModifiedDate);
						stmtInsert.bindString(12, ""+pricingDO.ModifiedTime);
						stmtInsert.bindString(13, ""+pricingDO.UOM);
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
				return false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			
			return true;
		}
	}
	
	public Vector<NameIDDo> getCategoryProductsAndImages(String categoryId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor  = null;
			
			Vector<NameIDDo> vecCategoryList = null;
			NameIDDo objCategory;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("select PI.ImagePath,P.Description from tblProductImages PI,tblProducts P where  PI.ItemCode = P.SKU and ItemCode in (Select SKU from tblProducts where CategoryId ='"+categoryId+"') ORDER BY P.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					vecCategoryList = new Vector<NameIDDo>();
					do
					{
						objCategory = new NameIDDo();
						objCategory.strId = cursor.getString(0);
						objCategory.strName = cursor.getString(1);
						vecCategoryList.add(objCategory);
						
					}while(cursor.moveToNext());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase!=null)
					mDatabase.close();
			}
			
			return vecCategoryList;
		}
	}
	
	public Vector<CategoryDO> getCategoryList()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor  = null;
		
		Vector<CategoryDO> vecCategoryList=null;

		try
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory order by CategoryName", null);
			if(cursor.moveToFirst())
			{
				vecCategoryList = new Vector<CategoryDO>();
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					objCategory.categoryIcon = cursor.getString(2);
					vecCategoryList.add(objCategory);
					
				}while(cursor.moveToNext());
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			if(cursor!=null)
				cursor.close();
			if(mDatabase!=null)
				mDatabase.close();
		}
		return vecCategoryList;
	}
	
	public Transactions isAnyTrxPending(){
		synchronized (MyApplication.MyLock) {
			Transactions transactions =Transactions.ALL;
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null;
			boolean isCheckNext=true;
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				if(isCheckNext){
					cursor =sqLiteDatabase.rawQuery("SELECT count(*) from tblTrxHeader o where TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%' and (Status != 1 AND Status != 2 OR Status = '')", null);
					if(cursor.moveToFirst()){
						transactions = Transactions.ORDERS;
						isCheckNext = false;
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				if(isCheckNext){
					cursor =sqLiteDatabase.rawQuery("SELECT count(*) from tblPaymentHeader o where PaymentDate like '%"+CalendarUtils.getOrderPostDate()+"%' and Status != 1 ", null);
					if(cursor.moveToFirst()){
						transactions = Transactions.PAYMENTS;
						isCheckNext = false;
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return transactions;
		}
	}
	public boolean isAllOrderPushed(String strPresellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isAllOrderPushed =false;
			SQLiteDatabase sqLiteDatabase =null;
			SQLiteStatement selectStament =null;
			try
			{
//				String query="SELECT count(*) from tblTrxHeader o where TrxDate like '%"+CalendarUtils.getOrderPostDate()+"%' and (Status != 1 AND Status != 2 OR Status = '')";
				String query="SELECT count(*) from tblTrxHeader o where   Status = 0 ";  //vinod
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				selectStament	=	sqLiteDatabase.compileStatement(query);
				long count		=	selectStament.simpleQueryForLong();
				if(count>0)
					isAllOrderPushed = false;
				else
					isAllOrderPushed = true;
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return isAllOrderPushed;
		}
	}
	
	public boolean isVanStockUnload()
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isTrue =false;
			SQLiteDatabase sqLiteDatabase =null;
			SQLiteStatement selectStament1 =null;
			SQLiteStatement selectStament2 =null;
			try
			{
				String query1 = "SELECT COUNT(SellableQuantity) FROM tblVanStock WHERE SellableQuantity > 0 AND IFNULL(IsSellableUnload ,0) != 1";
				String query2 = "SELECT COUNT(NonSellableQuantity) FROM tblVanStock WHERE NonSellableQuantity > 0 AND IFNULL(IsNonSellableUnload ,0) !=1";
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				selectStament1	=	sqLiteDatabase.compileStatement(query1);
				long count1		=	selectStament1.simpleQueryForLong();
				
				selectStament2	=	sqLiteDatabase.compileStatement(query2);
				long count2		=	selectStament2.simpleQueryForLong();
				if(count1>0 || count2 > 0)
					isTrue = false;
				else
					isTrue = true;
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return isTrue;
		}
	}
	
	
	public boolean isMovementPending()
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isTrue =false;
			SQLiteDatabase sqLiteDatabase =null;
			SQLiteStatement selectStament =null;
			try
			{
				String query = "SELECT COUNT(*) FROM tblMovementHeader WHERE MovementStatus = 'Pending'";
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				selectStament	=	sqLiteDatabase.compileStatement(query);
				long count		=	selectStament.simpleQueryForLong();
				
				
				if(count>0)
					isTrue = false;
				else
					isTrue = true;
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return isTrue;
		}
	}
	
	
	public boolean getLoadStatus(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isTrue = true;
			SQLiteDatabase sqLiteDatabase =null;
			SQLiteStatement selectStament =null;
			try
			{
				String query = "SELECT COUNT(*) FROM tblMovementHeader WHERE UserCode ='"+empNo+"' and MovementDate like '"+CalendarUtils.getTodaydate()+"'";
				sqLiteDatabase	=	DatabaseHelper.openDataBase();
				selectStament	=	sqLiteDatabase.compileStatement(query);
				long count		=	selectStament.simpleQueryForLong();
				if(count>0)
					isTrue = false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
	
			return isTrue;
		}
	}
	
	//method to get the reason vector by reason type
	public Vector<NameIDDo> getAllBanks()
	{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select *from tblBanks ORDER BY BankName";
			
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 NameIDDo objBanks 		=  new NameIDDo();
					 objBanks.strId 		=  cursor.getString(0);
					 objBanks.strName		=  cursor.getString(1);
					 objBanks.strType		=  cursor.getString(2);
					 vecBanks.add(objBanks);
				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
			 if(cursor != null)
				 cursor.close();
		 }
		 return vecBanks;
	}
	
	public Vector<NameIDDo> getAllBanksNew()
	{
		Vector<NameIDDo> vecBanks = new Vector<NameIDDo>();
		String strQuery = "Select *from tblBanks ORDER BY BankName";
			
		 SQLiteDatabase objSqliteDB =null;
		 Cursor cursor 				= null;
		 try
		 {
			 objSqliteDB = DatabaseHelper.openDataBase();
			 
			 cursor 				= objSqliteDB.rawQuery(strQuery, null);
			 if(cursor.moveToFirst())
			 {
				 do
				 {
					 NameIDDo objBanks 		=  new NameIDDo();
					 objBanks.strId 		=  cursor.getString(0);
					 objBanks.strName		=  cursor.getString(1);
					 objBanks.strType		=  cursor.getString(2);
					 vecBanks.add(objBanks);
				 }
				 while(cursor.moveToNext());
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
		 finally
		 {
			 if(objSqliteDB != null)
			 {
			 	objSqliteDB.close();
			 }
			 if(cursor != null)
				 cursor.close();
		 }
		 return vecBanks;
	}
	
		
	//method to insert or update the banks Detail
	public boolean insertSettings(Vector<SettingsDO> vecBankNames)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSettings WHERE SettingId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSettings (SettingId, SettingName, SettingValue, CountryId) VALUES(?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSettings SET SettingName = ?, SettingValue =?, CountryId=? WHERE SettingId = ?");
			
			for(SettingsDO objSettingsDO : vecBankNames)
			{
				stmtSelectRec.bindLong(1, objSettingsDO.SettingId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objSettingsDO != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindString(1, objSettingsDO.SettingName);
						stmtUpdate.bindString(2, objSettingsDO.SettingValue);
						stmtUpdate.bindLong(3, objSettingsDO.CountryId);
						stmtUpdate.bindLong(4, objSettingsDO.SettingId);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindLong(1,objSettingsDO.SettingId);
						stmtInsert.bindString(2, objSettingsDO.SettingName);
						stmtInsert.bindString(3, objSettingsDO.SettingValue);
						stmtInsert.bindLong(4, objSettingsDO.CountryId);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		finally
		{
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	
	
	public boolean insertBankDetails(Vector<NameIDDo> vecBankNames)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblBanks WHERE BankId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblBanks (BankId, BankName, BankCode) VALUES(?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblBanks SET BankName = ?, BankCode =? WHERE BankId = ?");
			
			for(NameIDDo objNameIDDo : vecBankNames)
			{
				stmtSelectRec.bindString(1, objNameIDDo.strId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objNameIDDo != null)
				{
					if (countRec != 0) 
					{
						stmtUpdate.bindString(1, objNameIDDo.strName);
						stmtUpdate.bindString(2, objNameIDDo.strType);
						stmtUpdate.bindString(3, objNameIDDo.strId);
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1,objNameIDDo.strId);
						stmtInsert.bindString(2, objNameIDDo.strName);
						stmtInsert.bindString(3, objNameIDDo.strType);
						stmtInsert.executeInsert();
					}
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		finally
		{
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public Vector<OrderDO> getDeliveryStatusOrderList(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<OrderDO> vectorOrderList = new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase   = null;
			String strQuery = "";
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase   = DatabaseHelper.openDataBase();
				strQuery = "SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, OT.SiteName, CST.ADDRESS2,OT.EmpNo,OT.OrderType,OT.SubType  " +
						"FROM tblOrderHeader OT  LEFT JOIN tblCustomerSites CST  ON OT.SiteNo=CST.CustomerSiteId " +
						"where OT.OrderId in(select OrderNo from tblOrderDetail) AND (OT.OrderDate LIKE '"+date+"%'  OR  OT.DeliveryDate LIKE '"+date+"%' )";
				cursor	=	sqLiteDatabase.rawQuery(strQuery,null);
				
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 		= 	new OrderDO();
						orderDO.OrderId  		= 	cursor.getString(0); 
						orderDO.DeliveryStatus 	= 	cursor.getString(1);
						orderDO.InvoiceDate 	= 	cursor.getString(2);
						orderDO.CustomerSiteId 	= 	cursor.getString(3);
						orderDO.strCustomerName = 	cursor.getString(5);
						orderDO.strAddress1 	= 	cursor.getString(4);
						orderDO.strAddress2 	= 	cursor.getString(6);
						orderDO.DeliveryAgentId = 	cursor.getString(7);
						orderDO.orderType 		= 	cursor.getString(8);
						orderDO.orderSubType 	= 	cursor.getString(9);
						
//						if(orderDO.strCustomerName == null || orderDO.strCustomerName.equalsIgnoreCase(""))
//							orderDO.strCustomerName = "Free Delivery";
						
						if(orderDO.strAddress1 == null || orderDO.strAddress1.equalsIgnoreCase(""))
							orderDO.strAddress1 = "";
						
						if(orderDO.strAddress2 == null || orderDO.strAddress2.equalsIgnoreCase(""))
							orderDO.strAddress2 = "";
							
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	}
	
	public ArrayList<ProductDO> getDeliveryStatusOrderProducts(String orderID)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<ProductDO> vecItemList = new ArrayList<ProductDO>();
			SQLiteDatabase sqLiteDatabase   = null;
			Cursor cursor	=	null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursor		   =	sqLiteDatabase.rawQuery("SELECT  OD.ItemCode, TP.UnitPerCase, OD.Units, OD.Cases,TP.Description,OD.QuantityBU,OD.PriceUsedLevel2," +
															"OD.unitSellingPrice,OD.UOM , OD.ReasonCode FROM tblOrderDetail OD,tblProducts TP " +
															"where OrderNo ='"+orderID+"' and OD.ItemCode=TP.ItemCode ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 		= 	new ProductDO();
						productDO.SKU 				= 	cursor.getString(0);
						productDO.UnitsPerCases 	= 	cursor.getInt(1);
						productDO.preUnits 			= 	cursor.getInt(2)+"";
						productDO.preCases 			= 	cursor.getString(3);
						productDO.Description 		= 	cursor.getString(4);
						productDO.totalCases 		= 	cursor.getFloat(5);
						productDO.invoiceAmount 	=	cursor.getFloat(6);
						productDO.unitSellingPrice 	=	cursor.getFloat(7);
						productDO.depositPrice	 	=	0;
						productDO.UOM 				= 	cursor.getString(8);
						productDO.itemPrice 		= 	0;
						productDO.Discount 			= 	0;
						productDO.TaxGroupCode		= 	"";
						productDO.TaxPercentage		= 	0;
						productDO.reason			= 	cursor.getString(9);
						
						if(productDO.preUnits == null || productDO.preUnits.equalsIgnoreCase(""))
							productDO.preUnits = "0";
						
						if(productDO.preCases == null || productDO.preCases.equalsIgnoreCase(""))
							productDO.preCases = "0";
						
						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecItemList;
		}
	}
	
	//method to insert or update the banks Detail
	public boolean saveCustomer(MallsDetails mallsDetails) 
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSites WHERE CustomerSiteId = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId, CustomerId , SiteName, ADDRESS1, ADDRESS2, CITY, GEO_CODE_X, GEO_CODE_Y, PresellerId, TYPE, PaymentType,email) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCustomerSites SET CustomerId=?, SiteName=?, ADDRESS1=?, ADDRESS2=?, CITY=?, GEO_CODE_X=?, GEO_CODE_Y=?, PresellerId=?, TYPE=?, PaymentType=?,email=? WHERE CustomerSiteId = ?");
			if(mallsDetails != null)
			{
				stmtSelectRec.bindString(1, mallsDetails.customerSiteId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				if (countRec != 0) 
				{
					stmtUpdate.bindString(1, mallsDetails.CustomerId);
					stmtUpdate.bindString(2, mallsDetails.SiteName);
					stmtUpdate.bindString(3, mallsDetails.Address1);
					stmtUpdate.bindString(4, mallsDetails.Address2);
					stmtUpdate.bindString(5, mallsDetails.City);
					stmtUpdate.bindString(6, ""+mallsDetails.Latitude);
					stmtUpdate.bindString(7, ""+mallsDetails.Longitude);
					stmtUpdate.bindString(8, mallsDetails.presellerId);
					stmtUpdate.bindString(9, mallsDetails.paymentType);
					stmtUpdate.bindString(10, mallsDetails.paymentCode);
					stmtUpdate.bindString(11, mallsDetails.email);
					stmtUpdate.bindString(12, mallsDetails.customerSiteId);
					stmtUpdate.execute();
				}
				else
				{
					stmtInsert.bindString(1,mallsDetails.customerSiteId);
					stmtInsert.bindString(2, mallsDetails.CustomerId);
					stmtInsert.bindString(3, mallsDetails.SiteName);
					stmtInsert.bindString(4,mallsDetails.Address1);
					stmtInsert.bindString(5, mallsDetails.Address2);
					stmtInsert.bindString(6, mallsDetails.City);
					stmtInsert.bindString(7, ""+mallsDetails.Latitude);
					stmtInsert.bindString(8, ""+mallsDetails.Longitude);
					stmtInsert.bindString(9, mallsDetails.presellerId);
					stmtInsert.bindString(10,mallsDetails.paymentType);
					stmtInsert.bindString(11, mallsDetails.paymentCode);
					stmtInsert.bindString(12, mallsDetails.email);
					stmtInsert.executeInsert();
				}
			}
			
			stmtSelectRec.close();
			stmtUpdate.close();
			stmtInsert.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		finally
		{
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public Vector<NewCustomerDO> getNewCustomerToUpload()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase = null;
			Vector<NewCustomerDO> vector = new Vector<NewCustomerDO>();
			Cursor cursor  = null;
			
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor    = mDatabase.rawQuery("select * from tblCustomerSites where Source == 0 AND BlaseCustId !=''", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						NewCustomerDO newCustomerDO = new NewCustomerDO();
						
						newCustomerDO.customerName = cursor.getString(cursor.getColumnIndex("SiteName")); 
						newCustomerDO.siteName = cursor.getString(cursor.getColumnIndex("SiteName")); 
						newCustomerDO.contactPerson = ""; 
						newCustomerDO.mobileNo = cursor.getString(cursor.getColumnIndex("MOBILENO1")); 
						newCustomerDO.region = cursor.getString(cursor.getColumnIndex("LandmarkId")); 
						newCustomerDO.landline = ""; 
						newCustomerDO.area = cursor.getString(cursor.getColumnIndex("LandmarkId")); 
						newCustomerDO.salesman = cursor.getString(cursor.getColumnIndex("PresellerId")); 
						newCustomerDO.customerType = cursor.getString(cursor.getColumnIndex("CustomerType")); 
						
						newCustomerDO.address1 = cursor.getString(cursor.getColumnIndex("ADDRESS1")); 
						newCustomerDO.address2 = cursor.getString(cursor.getColumnIndex("ADDRESS2")); 
						
						newCustomerDO.source = ""; 
						newCustomerDO.billTo = ""; 
						newCustomerDO.shipTo = ""; 
						newCustomerDO.countryName = cursor.getString(cursor.getColumnIndex("CountryId")); 
						newCustomerDO.email = ""; 
						newCustomerDO.nationality = cursor.getString(cursor.getColumnIndex("CountryId")); 
						newCustomerDO.longitude = cursor.getFloat(cursor.getColumnIndex("GEO_CODE_Y")); 
						newCustomerDO.latitude = cursor.getFloat(cursor.getColumnIndex("GEO_CODE_X")); 
						newCustomerDO.AppUUID = cursor.getString(cursor.getColumnIndex("BlaseCustId")); 
						newCustomerDO.CustomerSiteId = cursor.getString(cursor.getColumnIndex("CustomerSiteId"));
						newCustomerDO.CITY = cursor.getString(cursor.getColumnIndex("CITY"));
						newCustomerDO.countryId = cursor.getString(cursor.getColumnIndex("AnniversaryDate"));
						getCustomerSubData(mDatabase,newCustomerDO);
						vector.add(newCustomerDO);
					} 
					while (cursor.moveToNext());
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
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return vector;
		}
	}
	
	public Vector<UnUploadedDataDO> getNewCustomerUnUpload()
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase mDatabase = null;
			Vector<UnUploadedDataDO> uploadedDataDOs = new Vector<UnUploadedDataDO>();
			Cursor cursor  = null;
			
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select CustomerId, ISPushed from tblNewHouseHoldCustomer", null);
				if(cursor.moveToFirst())
				{
					do 
					{
						UnUploadedDataDO unUploadedDataDO = new UnUploadedDataDO();
						
						unUploadedDataDO.strId  = cursor.getString(0); 
						unUploadedDataDO.status = StringUtils.getInt(cursor.getString(1)); 
						uploadedDataDOs.add(unUploadedDataDO);
					} 
					while (cursor.moveToNext());
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
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return uploadedDataDOs;
		}
	}
	
	public String insertCustomerSite(MallsDetails mallsDetails, NameIDDo nameIDDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			String strCustomerId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
				String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
				Cursor cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strCustomerId = cursor.getString(0);
					mallsDetails.customerSiteId = strCustomerId;
					mallsDetails.CustomerId = strCustomerId;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
				
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
						"CountryId,DOB,AnniversaryDate,CustomerCategory,CustomerSubCategory) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtInsert2 = objSqliteDB.compileStatement("INSERT INTO tblCustomerSitesSub (CustomerSiteId," +
						" ContactPerson, Landline,OutLetType,OutLetTypeId,CompetitionBrand,CompetitionBrandId,SKU,Email,BuyerStatus) " +
						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(mallsDetails != null && mallsDetails.customerSiteId != null && !mallsDetails.customerSiteId.equalsIgnoreCase(""))
				{
					stmtInsert.bindString(1, mallsDetails.customerSiteId);
					stmtInsert.bindString(2, mallsDetails.CustomerId);
					stmtInsert.bindString(3, mallsDetails.SiteName);
					stmtInsert.bindString(4, mallsDetails.Address1);
					stmtInsert.bindString(5, mallsDetails.Address2);
					stmtInsert.bindString(6, mallsDetails.City);
					stmtInsert.bindString(7, ""+mallsDetails.Latitude);
					stmtInsert.bindString(8, ""+mallsDetails.Longitude);
					stmtInsert.bindString(9, ""+mallsDetails.CreditLimit);
					stmtInsert.bindString(10, mallsDetails.presellerId);
					stmtInsert.bindString(11, mallsDetails.paymentType);
					stmtInsert.bindString(12, mallsDetails.paymentCode);
					stmtInsert.bindString(13, "");
					stmtInsert.bindString(14, "0");
					stmtInsert.bindString(15, mallsDetails.subChannelCode);
					stmtInsert.bindString(16, mallsDetails.customerStatus);
					stmtInsert.bindString(17, mallsDetails.mobileNo);
					stmtInsert.bindString(18, mallsDetails.mobileNo);
					stmtInsert.bindString(19, "");
					stmtInsert.bindString(20, "");
					stmtInsert.bindString(21, mallsDetails.CustomerType);
					stmtInsert.bindString(22, mallsDetails.landId);
					stmtInsert.bindString(23, mallsDetails.landId);
					stmtInsert.bindString(24, "0");
					stmtInsert.bindString(25, mallsDetails.AppUUID);
					stmtInsert.bindString(26, mallsDetails.countryDesc);
					stmtInsert.bindString(27, "");
					stmtInsert.bindString(28, ""+nameIDDo.strId);
					stmtInsert.bindString(29, "ROUTE");
					stmtInsert.bindString(30, "OTHERS");
					stmtInsert.executeInsert();
					
					stmtInsert2.bindString(1, mallsDetails.customerSiteId);
					stmtInsert2.bindString(2, mallsDetails.contactPerson);
					stmtInsert2.bindString(3, mallsDetails.landline);
					stmtInsert2.bindString(4, mallsDetails.outLetType);
					stmtInsert2.bindString(5, mallsDetails.outLetTypeId);
					stmtInsert2.bindString(6, mallsDetails.competitionBrand);
					stmtInsert2.bindString(7, ""+mallsDetails.competitionBrandId);
					stmtInsert2.bindString(8, ""+mallsDetails.sku);
					stmtInsert2.bindString(9, ""+mallsDetails.email);
					stmtInsert2.bindString(10, mallsDetails.buyerStatus);
					stmtInsert2.executeInsert();
					
					
				}
				
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return strCustomerId;
		}
	}
	
	public String insertCustomerSiteInfo(JourneyPlanDO mallsDetails, NameIDDo nameIDDo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			String strCustomerId = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
				String query = "SELECT id from tblOfflineData where Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
				Cursor cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strCustomerId = cursor.getString(0);
					mallsDetails.customerId = strCustomerId;
					mallsDetails.site 		= strCustomerId;
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomer (id, Site, SiteName, CustomerId,"
						+ " CustomerStatus, CustAcctCreationDate, PartyName, ChannelCode, SubChannelCode, RegionCode, CountryCode, Category,"
						+ " Address1, Address2, Address3, Address4, PoNumber, City, PaymentType, PaymentTermCode, CreditLimit, GeoCodeX,"
						+ " GeoCodeY, PASSCODE, Email, ContactPersonName, PhoneNumber, AppCustomerId, MobileNumber1, MobileNumber2, Website, CustomerType,"
						+ " CreatedBy, ModifiedBy, Source, CustomerCategory, CustomerSubCategory, CustomerGroupCode, ModifiedDate, ModifiedTime,"
						+ " CurrencyCode, StoreGrowth, PriceList, SalesPerson, PaymentMode, SalesOrgCode, SalesOfficeCode, DivisionCode, Attribute1, Attribute2,"
						+ " Attribute3, Attribute4, Attribute5, Attribute6, Attribute7, Attribute8, Attribute9, Attribute10, CreatedDate, CreatedTime, BlockedStatus,"
						+ " BlockedByUserCode, BlockedReason, BlockedDate, BlockedTime)  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "	?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				
//				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
//						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
//						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
//						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
//						"CountryId,DOB,AnniversaryDate,CustomerCategory,CustomerSubCategory,PaymentMode) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
//				SQLiteStatement stmtInsert2 = objSqliteDB.compileStatement("INSERT INTO tblCustomerSitesSub (CustomerSiteId," +
//						" ContactPerson, Landline,OutLetType,OutLetTypeId,CompetitionBrand,CompetitionBrandId,SKU,Email,BuyerStatus) " +
//						"VALUES(?,?,?,?,?,?,?,?,?,?)");
				
				if(mallsDetails != null && mallsDetails.customerId != null && !mallsDetails.customerId.equalsIgnoreCase(""))
				{
					
					stmtInsert.bindString(1, ""+0);
					stmtInsert.bindString(2, ""+mallsDetails.site);
					stmtInsert.bindString(3, ""+mallsDetails.siteName);
					stmtInsert.bindString(4, ""+mallsDetails.customerId);
					stmtInsert.bindString(5, ""+true);
					stmtInsert.bindString(6, ""+CalendarUtils.getOrderPostDate());
					stmtInsert.bindString(7, ""+mallsDetails.partyName);
					stmtInsert.bindString(8, ""+mallsDetails.channelCode);
					stmtInsert.bindString(9, ""+mallsDetails.subChannelCode);
					stmtInsert.bindString(10, ""+mallsDetails.regionCode);
					stmtInsert.bindString(11, ""+mallsDetails.coutryCode);
					stmtInsert.bindString(12, ""+mallsDetails.category);
					stmtInsert.bindString(13, ""+mallsDetails.addresss1);
					stmtInsert.bindString(14, ""+mallsDetails.addresss2);
					stmtInsert.bindString(15, ""+mallsDetails.addresss3);
					stmtInsert.bindString(16, ""+mallsDetails.addresss4);
					stmtInsert.bindString(17, ""+mallsDetails.phoneNumber);
					stmtInsert.bindString(18, ""+mallsDetails.city);
					stmtInsert.bindString(19, ""+mallsDetails.customerPaymentType);
					stmtInsert.bindString(20, ""+mallsDetails.paymentTermCode);
					stmtInsert.bindString(21, "");
					stmtInsert.bindString(22, ""+mallsDetails.geoCodeX);
					stmtInsert.bindString(23, ""+mallsDetails.geoCodeY);
					stmtInsert.bindString(24, "");
					stmtInsert.bindString(25, ""+mallsDetails.email);
					stmtInsert.bindString(26, ""+mallsDetails.contectPersonName);
					stmtInsert.bindString(27, ""+mallsDetails.phoneNumber);
					stmtInsert.bindString(28, ""+mallsDetails.AppUUID);
					stmtInsert.bindString(29, ""+mallsDetails.phoneNumber);
					stmtInsert.bindString(30, ""+mallsDetails.phoneNumber);
					stmtInsert.bindString(31, "");
					stmtInsert.bindString(32, "");
					stmtInsert.bindString(33, "");
					stmtInsert.bindString(34, "");
					stmtInsert.bindString(35, "");
					stmtInsert.bindString(36, ""+mallsDetails.customerCategory);
					stmtInsert.bindString(37, ""+mallsDetails.customerSubCategory);
					stmtInsert.bindString(38, ""+mallsDetails.customerGroupCode);
					stmtInsert.bindString(39, "");
					stmtInsert.bindString(40, "");
					stmtInsert.bindString(41, ""+mallsDetails.currencyCode);
					stmtInsert.bindString(42, ""+mallsDetails.StoreGrowth);
					stmtInsert.bindString(43, "");//Prce List
					stmtInsert.bindString(44, ""+mallsDetails.userID);
					stmtInsert.bindString(45, ""+mallsDetails.customerPaymentMode);
					stmtInsert.bindString(46, "");
					stmtInsert.bindString(47, "");
					stmtInsert.bindString(48, "");
					stmtInsert.bindString(49, ""+mallsDetails.Attribute1);
					stmtInsert.bindString(50, ""+mallsDetails.Attribute2);
					stmtInsert.bindString(51, ""+mallsDetails.Attribute3);
					stmtInsert.bindString(52, ""+mallsDetails.Attribute4);
					stmtInsert.bindString(53, ""+mallsDetails.Attribute5);
					stmtInsert.bindString(54, ""+mallsDetails.Attribute6);
					stmtInsert.bindString(55, ""+mallsDetails.Attribute7);
					stmtInsert.bindString(56, ""+mallsDetails.Attribute8);
					stmtInsert.bindString(57, ""+mallsDetails.Attribute9);
					stmtInsert.bindString(58, ""+mallsDetails.Attribute10);
					stmtInsert.bindString(59, "");
					stmtInsert.bindString(60, "");
					stmtInsert.bindString(61, ""+0);
					stmtInsert.bindString(62, "");
					stmtInsert.bindString(63, "");
					stmtInsert.bindString(64, "");
					stmtInsert.bindString(65, "");
					stmtInsert.executeInsert();
					
//					stmtInsert.bindString(1, ""+mallsDetails.customerId);
//					stmtInsert.bindString(2, ""+mallsDetails.customerId);
//					stmtInsert.bindString(3, ""+mallsDetails.siteName);
//					stmtInsert.bindString(4, ""+mallsDetails.addresss1);
//					stmtInsert.bindString(5, ""+mallsDetails.addresss2);
//					stmtInsert.bindString(6, ""+mallsDetails.city);
//					stmtInsert.bindString(7, ""+mallsDetails.geoCodeX);
//					stmtInsert.bindString(8, ""+mallsDetails.geoCodeY);
//					stmtInsert.bindString(9, ""+mallsDetails.creditLimit);
//					stmtInsert.bindString(10, ""+mallsDetails.userID);
//					stmtInsert.bindString(11, ""+mallsDetails.customerPaymentType);
//					stmtInsert.bindString(12, ""+mallsDetails.paymentTermCode);
//					stmtInsert.bindString(13, "");
//					stmtInsert.bindString(14, "0");
//					stmtInsert.bindString(15, ""+mallsDetails.subChannelCode);
//					stmtInsert.bindString(16, ""+mallsDetails.custmerStatus);
//					stmtInsert.bindString(17, ""+mallsDetails.mobileNo1);
//					stmtInsert.bindString(18, ""+mallsDetails.mobileNo2);
//					stmtInsert.bindString(19, "");
//					stmtInsert.bindString(20, "");
//					stmtInsert.bindString(21, "");
//					stmtInsert.bindString(22, "");
//					stmtInsert.bindString(23, "");
//					stmtInsert.bindString(24, "0");
//					stmtInsert.bindString(25, ""+mallsDetails.AppUUID);
//					stmtInsert.bindString(26, ""+mallsDetails.coutryCode);
//					stmtInsert.bindString(27, "");
//					stmtInsert.bindString(28, ""+nameIDDo.strId);
//					stmtInsert.bindString(29, "ROUTE");
//					stmtInsert.bindString(30, "OTHERS");
//					stmtInsert.bindString(31, ""+mallsDetails.customerPaymentMode);
//					stmtInsert.executeInsert();
					
//					stmtInsert2.bindString(1, ""+mallsDetails.customerId);
//					stmtInsert2.bindString(2, ""+mallsDetails.contectPersonName);
//					stmtInsert2.bindString(3, ""+mallsDetails.mobileNo2);
//					stmtInsert2.bindString(4, ""+mallsDetails.outLetType);
//					stmtInsert2.bindString(5, ""+mallsDetails.outLetTypeId);
//					stmtInsert2.bindString(6, ""+mallsDetails.competitionBrand);
//					stmtInsert2.bindString(7, ""+mallsDetails.competitionBrandId);
//					stmtInsert2.bindString(8, ""+mallsDetails.sku);
//					stmtInsert2.bindString(9, ""+mallsDetails.email);
//					stmtInsert2.bindString(10,""+mallsDetails.buyerStatus);
//					stmtInsert2.executeInsert();
					
					
				}
				
				stmtInsert.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return "";
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return strCustomerId;
		}
	}
	
	
	public ArrayList<ProductDO> getStockInventory()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<ProductDO> vecItemList = new ArrayList<ProductDO>();
			SQLiteDatabase sqLiteDatabase   = null;
			Cursor cursor	=	null;
			try
			{
				sqLiteDatabase   = DatabaseHelper.openDataBase();
				
				cursor			 =	sqLiteDatabase.rawQuery("SELECT  STI.SKU, TP.UnitPerCase, STI.avilablequantity, TP.Description  FROM tblStockInventory STI, tblProducts TP where TP.SKU = STI.SKU ORDER BY TP.DisplayOrder ASC", null);
				if(cursor.moveToFirst())
				{
					do
					{
						ProductDO productDO 	= 	new ProductDO();
						productDO.SKU 			= 	cursor.getString(0);
						productDO.UnitsPerCases = 	StringUtils.getInt(cursor.getString(1));
						
						productDO.preCases 		= 	cursor.getString(2);
						productDO.preUnits 		= 	""+(int)Math.round(StringUtils.getFloat(productDO.preCases)*productDO.UnitsPerCases);
						productDO.Description 	= 	cursor.getString(3);
						
						vecItemList.add(productDO);
					}
					while(cursor.moveToNext());
				}
				
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vecItemList;
		}
	}
	
	public void deleteAllRecords()
	{
		synchronized(MyApplication.MyLock) 
		{
			LogUtils.errorLog("CustomerOrderDA", "Deleting all tables data");
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM BarcodeInfo");
				sqlDB.execSQL("DELETE FROM tblCustomerGroup");
				sqlDB.execSQL("DELETE FROM tblCustomerSites");
				sqlDB.execSQL("DELETE FROM tblCustomers");
				sqlDB.execSQL("DELETE FROM tblHouseHoldCustomers");
				sqlDB.execSQL("DELETE FROM tblNewHouseHoldCustomer");
				sqlDB.execSQL("DELETE FROM tblOrderDetail");
				sqlDB.execSQL("DELETE FROM tblOrderHeader");
				sqlDB.execSQL("DELETE FROM tblPaymentDetail");
				sqlDB.execSQL("DELETE FROM tblPaymentHeader");
				sqlDB.execSQL("DELETE FROM tblPendingInvoices");
				sqlDB.execSQL("DELETE FROM tblLandMark");
				sqlDB.execSQL("DELETE FROM tblCustomerHistory");
				sqlDB.execSQL("DELETE FROM tblOfflineData");
				sqlDB.execSQL("DELETE FROM tblPricing");
				sqlDB.execSQL("DELETE FROM tblVMSalesmanInventory");
				sqlDB.execSQL("DELETE FROM tblVehicle");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqlDB!=null)
					sqlDB.close();
			}
		}
	}

	public void deleteInventory()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				sqLiteDatabase.execSQL("DELETE FROM tblVMSalesmanInventory");
				sqLiteDatabase.execSQL("DELETE FROM tblVehicle");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public void deleteOfflineData()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				sqLiteDatabase.execSQL("DELETE FROM tblOfflineData");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
		}
	}
	
	public NameIDDo validatePassCode(String userCode, String passCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery   =  null; 
			strQuery 		  =  "SELECT PassCode,IsUsed from tblPassCode where Passcode ='"+passCode+"' and EmpId='"+userCode+"'";
			NameIDDo nameIDDo =  null;
			SQLiteDatabase sqLiteDatabase   = 	null;
			Cursor cursor					=	null;
			try
			{
				sqLiteDatabase   = 	DatabaseHelper.openDataBase();
				cursor			 =	sqLiteDatabase.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					nameIDDo 		 = new NameIDDo();
					nameIDDo.strName = cursor.getString(0);
					nameIDDo.strId   = cursor.getString(1);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return nameIDDo;
		}
	}
	
	public boolean deletePasscode(String passcode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				String strQuery = "DELETE FROM tblPassCode where PassCode = ?";
				objSqliteDB = DatabaseHelper.openDataBase();
				//updating the table tblPresellerPassCode
				SQLiteStatement stmtDeletePasscode = objSqliteDB.compileStatement(strQuery);
				
				stmtDeletePasscode.bindString(1, passcode);
				stmtDeletePasscode.execute();
				
				stmtDeletePasscode.close();
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public ArrayList<NameIDDo> getUnpostedCustomerId()
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery  	= 	"SELECT CT.CustomerId, OT.SiteNo FROM tblOrderHeader OT, tblNewHouseHoldCustomer CT  WHERE OT.SiteNo = CT.AppUUID";
			ArrayList<NameIDDo> arrayList 	= new ArrayList<NameIDDo>();
			SQLiteDatabase sqLiteDatabase   = 	null;
			Cursor cursor = null, cursor2	=	null;
			try
			{
				sqLiteDatabase   		= 	DatabaseHelper.openDataBase();
				cursor					=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo 	= 	new NameIDDo();
						nameIDDo.strId		=	cursor.getString(0);
						nameIDDo.strName	=	cursor.getString(1);
						nameIDDo.strType	=	"order";
						arrayList.add(nameIDDo);
					}
					while(cursor.moveToNext());
				}
				
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				strQuery  				= 	"SELECT CT.CustomerId, PN.SiteId FROM tblPaymentHeader PN, tblNewHouseHoldCustomer CT  WHERE PN.SiteId = CT.AppUUID";
				cursor2					=	sqLiteDatabase.rawQuery(strQuery, null);
				if(cursor2.moveToFirst())
				{
					do
					{
						NameIDDo nameIDDo 	= 	new NameIDDo();
						nameIDDo.strId		=	cursor2.getString(0);
						nameIDDo.strName	=	cursor2.getString(1);
						nameIDDo.strType	=	"payment";
						arrayList.add(nameIDDo);
					}
					while(cursor2.moveToNext());
				}
				
				if(cursor2!=null && !cursor2.isClosed())
					cursor2.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(cursor2!=null && !cursor2.isClosed())
					cursor2.close();
				
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return arrayList;
		}
	}
	
	public boolean updateCreatedCustomers(ArrayList<NameIDDo> arrList)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB 				= 	DatabaseHelper.openDataBase();
				SQLiteStatement order 		= 	objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments 	= 	objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				for(NameIDDo nameIDDo :arrList)
				{
					if(nameIDDo.strType.equalsIgnoreCase("order"))
					{
						order.bindString(1, nameIDDo.strId);
						order.bindString(2, nameIDDo.strName);
						order.execute();
					}
					else
					{
						payments.bindString(1, nameIDDo.strId);
						payments.bindString(2, nameIDDo.strName);
						payments.execute();
					}
				}
				
				order.close();
				payments.close();
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			
			return true;
		}
	}

	
	public String getReceiptNo(String SalesmanCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			String strReceiptNo = "";
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				String query = "SELECT id from tblOfflineData where SalesmanCode ='"+SalesmanCode+"' AND Type ='"+AppConstants.Receipt+"' AND status = 0 AND id NOT IN(SELECT ReceiptId FROM tblPaymentHeader) Order By id Limit 1";
				cursor = objSqliteDB.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					strReceiptNo = cursor.getString(0);
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return strReceiptNo;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return strReceiptNo;
		}
	}

	public boolean updateReceiptNo(String paymentId)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean isUpdated = false;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+paymentId+"'");
				isUpdated = true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				isUpdated = false;
			}
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return isUpdated;
		}
	}
	
	public boolean getAvailablityOfCustomerNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Customer' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}
	public boolean getGeoCodeStatusfromSettings()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			int isGeoCode = 1;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "select settingvalue from tblsettings where SettingName='"+AppConstants.CheckGEOCODE+"'";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isGeoCode =	cursor.getInt(0);
					
					if(isGeoCode== AppConstants.CheckGEOCODETrue)
					{
						isAvail=true;
					}else
						isAvail=false;
					
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}
	
	public boolean getAvailablityOfOrderNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Order' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}
	
	public boolean getAvailablityOfReceiptNo()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = false;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					String query = "SELECT Id FROM tblOfflineData WHERE Type='Receipt' AND status = 0";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return isAvail;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}

	public boolean checkInvoiceNumbers(String invoiceNumber, String amount) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			boolean isAvail = true;
			try
			{
				synchronized(MyApplication.MyLock) 
				{
					sqLiteDatabase =  DatabaseHelper.openDataBase();
					
					String query = "SELECT ReceiptId FROM tblPaymentInvoice WHERE TrxCode='"+invoiceNumber+"'";
					cursor = sqLiteDatabase.rawQuery(query, null);
					if(cursor.moveToFirst())
						isAvail = true;
					else
						isAvail = false;
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return true;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return isAvail;
		}
	}
	
	/**
	 * Method to save all the Reasons
	 * @param\ vecCategories
	 * @return boolean
	 */
	public boolean insertAllReasons(Vector<PostReasonDO> vecPostReasons)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblSkipReasons WHERE CustomerSiteId =? and SkipDate like ? ");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblSkipReasons (PresellerId, SkipDate, Reason, ReasonId, ReasonType, CustomerSiteId) VALUES(?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set PresellerId=?, SkipDate=?,Reason=?, ReasonId=?,ReasonType=? WHERE CustomerSiteId =? and SkipDate like ?");
				
				for(PostReasonDO objPostReasonDO :  vecPostReasons)
				{
					stmtSelectRec.bindString(1, objPostReasonDO.customerSiteID);
					stmtSelectRec.bindString(2, CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%");
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						stmtUpdate.bindString(1, objPostReasonDO.presellerId);
						stmtUpdate.bindString(2, objPostReasonDO.skippingDate);
						stmtUpdate.bindString(3, objPostReasonDO.reason);
						stmtUpdate.bindString(4, objPostReasonDO.reasonId);
						stmtUpdate.bindString(5, objPostReasonDO.reasonType);
						stmtUpdate.bindString(6, objPostReasonDO.customerSiteID);
						stmtUpdate.bindString(7, CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%");
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objPostReasonDO.presellerId);
						stmtInsert.bindString(2, objPostReasonDO.skippingDate);
						stmtInsert.bindString(3, objPostReasonDO.reason);
						stmtInsert.bindString(4, objPostReasonDO.reasonId);
						stmtInsert.bindString(5, objPostReasonDO.reasonType);
						stmtInsert.bindString(6, objPostReasonDO.customerSiteID);
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
				return false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	private void getCustomerSubData(SQLiteDatabase mDatabase, NewCustomerDO newCustomerDO)
	{
		Cursor cursor  = null;
		
		try
		{
			cursor  = mDatabase.rawQuery("select * from tblCustomerSitesSub where CustomerSiteId == '"+newCustomerDO.CustomerSiteId+"'", null);
			if(cursor.moveToFirst())
			{
				do 
				{
					newCustomerDO.contactPerson = cursor.getString(cursor.getColumnIndex("ContactPerson")); 
					newCustomerDO.landline = cursor.getString(cursor.getColumnIndex("Landline")); 
					newCustomerDO.outLetType = cursor.getString(cursor.getColumnIndex("OutLetType")); 
					newCustomerDO.outLetTypeId = cursor.getString(cursor.getColumnIndex("OutLetTypeId")); 
					newCustomerDO.competitionBrand = cursor.getString(cursor.getColumnIndex("CompetitionBrand")); 
					newCustomerDO.competitionBrandId = cursor.getString(cursor.getColumnIndex("CompetitionBrandId")); 
					newCustomerDO.sku = cursor.getString(cursor.getColumnIndex("SKU")); 
					newCustomerDO.email = cursor.getString(cursor.getColumnIndex("Email")); 
					newCustomerDO.buyerStatus = cursor.getString(cursor.getColumnIndex("BuyerStatus")); 
				} 
				while (cursor.moveToNext());
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
		}
	}
	
	public boolean insertCheckInDemandInventory(String empNo, String date, ArrayList<DeliveryAgentOrderDetailDco> arrayList, int pusStatus)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory (Date, EmpNo, ItemCode, PrimaryQuantity, SecondaryQuantity, pushStatus, AdvcCases, AdvcUnits) VALUES(?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtSelect 		= objSqliteDB.compileStatement("SELECT COUNT(*) FROM tblCheckINDemandStockInventory WHERE Date LIKE'%"+date+"%' AND EmpNo = ? AND ItemCode=?");
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCheckINDemandStockInventory SET PrimaryQuantity=?, SecondaryQuantity=?, pushStatus = 0 WHERE Date LIKE'%"+date+"%' AND EmpNo = ? AND ItemCode=?");
			
			if(arrayList != null && arrayList.size() > 0)
			{
				for(DeliveryAgentOrderDetailDco orderDetailDco :  arrayList)
				{
					stmtSelect.bindString(1, empNo);
					stmtSelect.bindString(2, orderDetailDco.itemCode);
					
					long count = stmtSelect.simpleQueryForLong();
					
					if(count <= 0)
					{
						
						stmtInsert.bindString(1, date);
						stmtInsert.bindString(2, empNo);
						stmtInsert.bindString(3, orderDetailDco.itemCode);
						stmtInsert.bindString(4, orderDetailDco.preCases+"");
						stmtInsert.bindString(5, orderDetailDco.preUnits+"");
						stmtInsert.bindString(6, pusStatus+"");
						stmtInsert.bindString(7, 0+"");
						stmtInsert.bindString(8, 0+"");
						stmtInsert.executeInsert();
					}
					else
					{
						stmtUpdate.bindString(1, orderDetailDco.preCases+"");
						stmtUpdate.bindString(2, orderDetailDco.preUnits+"");
						stmtUpdate.bindString(3, empNo);
						stmtUpdate.bindString(4, orderDetailDco.itemCode);
						stmtUpdate.execute();
					}
				}
			}
			stmtInsert.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public boolean insertCheckInDemandInventoryNew(SQLiteDatabase objSqliteDB, String empNo, String date, ArrayList<DeliveryAgentOrderDetailDco> arrayList, int pusStatus)
	{
		try 
		{
			if(objSqliteDB == null)
				objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblCheckINDemandStockInventory (Date, EmpNo, ItemCode, PrimaryQuantity, SecondaryQuantity, pushStatus) VALUES(?,?,?,?,?,?)");
			
			if(arrayList != null && arrayList.size() > 0)
			{
				for(DeliveryAgentOrderDetailDco orderDetailDco :  arrayList)
				{
					stmtInsert.bindString(1, date);
					stmtInsert.bindString(2, empNo);
					stmtInsert.bindString(3, orderDetailDco.itemCode);
					stmtInsert.bindString(4, orderDetailDco.preCases+"");
					stmtInsert.bindString(5, orderDetailDco.preUnits+"");
					stmtInsert.bindString(6, pusStatus+"");
					stmtInsert.executeInsert();
				}
			}
			stmtInsert.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private ArrayList<DeliveryAgentOrderDetailDco> getAddvanceOrderProductDetail(SQLiteDatabase objSqliteDB, String orderId)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = null;
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				cursor = objSqliteDB.rawQuery("SELECT ItemCode, Cases, Units from tblOrderDetail where OrderNO ='"+orderId+"'", null);
				if(cursor.moveToNext())
				{
					vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
					do 
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						orderDetailDco.itemCode = cursor.getString(0);
						orderDetailDco.preCases = StringUtils.getFloat(cursor.getString(1));
						orderDetailDco.preUnits = StringUtils.getInt(cursor.getString(2));
						
						vecOrdProduct.add(orderDetailDco);
					} while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return vecOrdProduct;
		}
	}
	
	public boolean UpdateDemandInventory(Vector<CheckInDemandInventoryDO> vector, int pusStatus)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCheckINDemandStockInventory SET pushStatus =? WHERE ItemCode =?");
			
			if(vector != null && vector.size() > 0)
			{
				for(CheckInDemandInventoryDO checkInDemandInventoryDO :  vector)
				{
					stmtUpdate.bindString(1, pusStatus+"");
					stmtUpdate.bindString(2, checkInDemandInventoryDO.ItemCode);
					stmtUpdate.executeInsert();
				}
			}
			stmtUpdate.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if(objSqliteDB!=null)
			{
				objSqliteDB.close();
			}
		}
		return true;
	}
	public Vector<CheckInDemandInventoryDO> getCheckINDemandInventory()
	{
		SQLiteDatabase mDatabase = null;
		Vector<CheckInDemandInventoryDO> vector = new Vector<CheckInDemandInventoryDO>();
		Cursor cursor  = null;
		
		try
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCheckINDemandStockInventory where pushStatus == '0'", null);
			if(cursor.moveToFirst())
			{
				do 
				{
					CheckInDemandInventoryDO checkInDemandInventoryDO = new CheckInDemandInventoryDO();
					checkInDemandInventoryDO.Date				= cursor.getString(0);
					checkInDemandInventoryDO.EmpNo				= cursor.getString(1);
					checkInDemandInventoryDO.ItemCode			= cursor.getString(2);
					checkInDemandInventoryDO.PrimaryQuantity	= cursor.getString(3);
					checkInDemandInventoryDO.SecondaryQuantity  = cursor.getString(4);
					checkInDemandInventoryDO.pushStatus			= cursor.getString(5);
					
					vector.add(checkInDemandInventoryDO);
				} 
				while (cursor.moveToNext());
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
			
			if(mDatabase != null)
				mDatabase.close();
		}
		
		return vector;
	}

	public ArrayList<DeliveryAgentOrderDetailDco> getCheckInDemandInventory(String empNo, String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase objSqliteDB = null;
			ArrayList<DeliveryAgentOrderDetailDco> vecOrdProduct = new ArrayList<DeliveryAgentOrderDetailDco>();
			try 
			{
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				cursor = objSqliteDB.rawQuery("SELECT TCK .ItemCode, TCK .PrimaryQuantity, TCK .SecondaryQuantity, TCK .AdvcCases, TCK .AdvcUnits, TP.Description, TP.ItemType FROM tblCheckINDemandStockInventory TCK INNER JOIN tblProducts TP ON TP.SKU = TCK.ItemCode WHERE TCK .Date LIKE '%"+date+"' AND TCK .EmpNo = '"+empNo+"' ORDER BY TP.DisplayOrder ASC", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						DeliveryAgentOrderDetailDco orderDetailDco = new DeliveryAgentOrderDetailDco();
						orderDetailDco.itemCode = cursor.getString(0);
						orderDetailDco.preCases = cursor.getFloat(1);
						orderDetailDco.preUnits = cursor.getInt(2);
						
						orderDetailDco.checkInCases = cursor.getFloat(1);
						orderDetailDco.checkInPcs = cursor.getInt(2);
						
//						orderDetailDco.advnCases = cursor.getFloat(5);
//						orderDetailDco.advnPcs = cursor.getInt(6);
						
						orderDetailDco.advnCases = cursor.getFloat(3);
						orderDetailDco.advnPcs = cursor.getInt(4);
						
						orderDetailDco.itemDescription = cursor.getString(5);
						orderDetailDco.itemType = cursor.getString(6);
						vecOrdProduct.add(orderDetailDco);
					} while (cursor.moveToNext());
				}
				
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return vecOrdProduct;
		}
	}
	public int getTotalProductCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			String strQuery  = 	"SELECT COUNT(*) from tblProducts";
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor 	= null;
			int count 		= 0;
			try
			{
				objSqliteDB 		= 	DatabaseHelper.openDataBase();
				cursor 	 		 	= 	objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					count = cursor.getInt(0);
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
			return count;
		}
	}
	
	public Vector<SalesTargetDO> getTargetCustomerCheckin()
	{
		synchronized(MyApplication.MyLock) 
		{
			DecimalFormat dfff = new DecimalFormat("##.##");
			dfff.setMaximumFractionDigits(2);
			dfff.setMinimumFractionDigits(2);
			
			String strQuery  = 	"SELECT * FROM tblPresellerTargetByCustCheckIN LIMIT 10";
			SQLiteDatabase objSqliteDB 	=	null;
			Cursor cursor = null;
			Vector<SalesTargetDO> vec = new Vector<SalesTargetDO>();
			try
			{
				objSqliteDB 		= 	DatabaseHelper.openDataBase();
				cursor 	 		 	= 	objSqliteDB.rawQuery(strQuery, null);
				if(cursor.moveToFirst())
				{
					do
					{
						SalesTargetDO nameIDDo 	= new SalesTargetDO();
						nameIDDo.TargetId 		= cursor.getString(0);
						nameIDDo.target 		= dfff.format(cursor.getFloat(1));
						nameIDDo.achived 		= dfff.format(cursor.getFloat(2));
						nameIDDo.cat 			= cursor.getString(3);
						nameIDDo.pending 		= dfff.format(StringUtils.getFloat(nameIDDo.target) - StringUtils.getFloat(nameIDDo.achived));
						vec.add(nameIDDo);
					}
					while(cursor.moveToNext());
					
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
			return vec;
		}
	}
	
	public ArrayList<PostReasonDO> getSkipReasonsToPost()
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<PostReasonDO> vecReasons = new ArrayList<PostReasonDO>();
			String strQuery = "Select * FROM tblSkipReasons where  IFNULL(Status, '') = ''";
				
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 cursor 				= objSqliteDB.rawQuery(strQuery, null);
				 if(cursor.moveToFirst())
				 {
					 do
					 {
						 PostReasonDO objReasons 	=  new PostReasonDO();
						 objReasons.presellerId 	=  cursor.getString(0);
						 objReasons.skippingDate	=  cursor.getString(1);
						 objReasons.reason 			=  cursor.getString(2);
						 objReasons.reasonId		=  cursor.getString(3);
						 objReasons.reasonType		=  cursor.getString(4);
						 objReasons.customerSiteID	=  cursor.getString(5);
						 vecReasons.add(objReasons);
					 }
					 while(cursor.moveToNext());
					 
					 
					 if(cursor!=null && !cursor.isClosed())
						 cursor.close();
				 }
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return vecReasons;
		}
	}

	public void deleteAllSkp(ArrayList<PostReasonDO> vecArrayList) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("DELETE FROM tblSkipReasons WHERE CustomerSiteId =? AND PresellerId =?");
				 
				 for(PostReasonDO postReasonDO : vecArrayList)
				 {
					 stmtUpdate.bindString(1, postReasonDO.customerSiteID);
					 stmtUpdate.bindString(2, postReasonDO.presellerId);
					 stmtUpdate.execute();
				 }
			 }
			 catch(Exception e)
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
	public void updateSkipReason(String siteIds, String date) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set Status = '1' WHERE CustomerSiteId in (?) AND SkipDate like ?");
				 
				 stmtUpdate.bindString(1, siteIds);
				 stmtUpdate.bindString(2, date+"%");
				 stmtUpdate.execute();
			 }
			 catch(Exception e)
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
	
	public void updateSkipReasonNew(ArrayList<PostReasonDO> vecArrayList, String date) 
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
//				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set Status = '1' WHERE CustomerSiteId = ? AND SkipDate like ?");
				 SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("Update tblSkipReasons set Status = '1' WHERE CustomerSiteId = ? AND strftime('%Y-%m-%d',SkipDate ) = strftime('%Y-%m-%d','"+date+"')");

				 for (PostReasonDO postReasonDO : vecArrayList)
				 {
					 stmtUpdate.bindString(1, postReasonDO.customerSiteID);
//					 stmtUpdate.bindString(2, date+"%");
					 stmtUpdate.execute();
				 }
			 }
			 catch(Exception e)
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
	

	public String getOrderStatus(String orderId)
	{
		synchronized(MyApplication.MyLock) 
		{
			 String query = "SELECT ReceiptId FROM tblPaymentInvoice WHERE TrxCode = '"+orderId+"'";
			 String strReceipts = "";
			 SQLiteDatabase objSqliteDB =null;
			 Cursor cursor 				= null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 cursor 				= objSqliteDB.rawQuery(query, null);
				 if(cursor.moveToFirst())
				 {
					strReceipts  = cursor.getString(0);
				 }
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB != null)
				 	objSqliteDB.close();
			 }
			 return strReceipts;
		}
	}
	
	public String getReceiptStatus(String receiptNo) 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			String status = "N";
			try
			{
				mDatabase = DatabaseHelper.openDataBase();
				
				cursor  = mDatabase.rawQuery("SELECT Status FROM tblPaymentHeader WHERE ReceiptId='"+receiptNo+"'", null);
				if(cursor.moveToFirst())
				{
					status = cursor.getString(0);
				} 
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return status;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			
			return status;
		}
	}
	
	public boolean updatePendingInvoices(String receiptNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			Cursor cursor = null, innerCursor = null;
			try
			{
				HashMap<String, Float> hashMap = new HashMap<String, Float>();
				String select 				=   "SELECT TrxCode,Amount FROM tblPaymentInvoice WHERE ReceiptId='"+receiptNo+"'";
				objSqliteDB 				= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate 	= 	objSqliteDB.compileStatement("UPDATE tblPendingInvoices SET BalanceAmount = ?, Status = 'N' WHERE InvoiceNumber =?");
				cursor = objSqliteDB.rawQuery(select, null);
				if(cursor.moveToFirst())
				{
					float amount = 0;
					do
					{
						String selectAmount =   "SELECT BalanceAmount FROM tblPendingInvoices WHERE InvoiceNumber= '"+cursor.getString(0)+"'";
						innerCursor = objSqliteDB.rawQuery(selectAmount, null);
						
						if(innerCursor.moveToFirst())
							amount = innerCursor.getFloat(0);
						
						if(innerCursor != null && !innerCursor.isClosed())
							innerCursor.close();
						
						amount = amount+cursor.getFloat(1);
						
						if(hashMap.size() > 0)
						{
							if(hashMap.containsKey(cursor.getString(0)))
							{
								float preAmount = hashMap.get(cursor.getString(0));
								amount = amount+preAmount;
								hashMap.put(cursor.getString(0), amount);
							}
							else
								hashMap.put(cursor.getString(0), amount);
						}
						else
							hashMap.put(cursor.getString(0), amount);
					}
					while(cursor.moveToNext());
				}
				
				if(hashMap != null && hashMap.size() > 0)
				{
					Set<String> keys = hashMap.keySet();
					for(String key : keys)	
					{
						stmtUpdate.bindString(1, ""+hashMap.get(key));
						stmtUpdate.bindString(2, key);
						stmtUpdate.execute();
					}
				}
				stmtUpdate.close();
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(innerCursor != null && !innerCursor.isClosed())
					innerCursor.close();
				
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	public boolean deleteReceipt(String receiptNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB 						= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtReceipts 		= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentHeader WHERE ReceiptId = ?");
				SQLiteStatement stmtReceiptDetail 	= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentDetail WHERE ReceiptNo = ?");
				SQLiteStatement stmtReceiptInvoice 	= 	objSqliteDB.compileStatement("DELETE FROM tblPaymentInvoice WHERE ReceiptId = ?");
				
				if(receiptNo != null && !receiptNo.equalsIgnoreCase(""))
				{
					stmtReceipts.bindString(1, receiptNo);
					stmtReceipts.execute();
					
					stmtReceiptDetail.bindString(1, receiptNo);
					stmtReceiptDetail.execute();
					
					stmtReceiptInvoice.bindString(1, receiptNo);
					stmtReceiptInvoice.execute();
				}
				
				stmtReceipts.close();
				stmtReceiptDetail.close();
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			
			return true;
		}
	}

	public void updatePasscodeStatus(String passcode) 
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB 			   = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("Update tblPassCode SET IsUsed = 1 WHERE PassCode =?");
				
				if(passcode != null && !passcode.equalsIgnoreCase(""))
				{
					stmtUpdate.bindString(1, passcode);
					stmtUpdate.execute();
				}
				stmtUpdate.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
		}
	}

	public String getUsedPasscode() 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			String passcode = null;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("SELECT PassCode from tblPassCode where IsUsed ='1' LIMIT 1", null);
				
				if(cursor.moveToFirst())
					passcode	 = 	cursor.getString(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			return passcode;
		}
	}
	
	public Vector<InventoryDO> getAllStoreCheck(String strEmpNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursor=null,cursorDetail=null,cursorGroup = null;
			Vector<InventoryDO> vecInventoryDOs= new Vector<InventoryDO>();
			try
			{
				sqLiteDatabase = DatabaseHelper.openDataBase();
				String inventoryQuery = "SELECT StoreCheckId,UserCode,JourneyCode,VisitCode,Date," +
						"Status,ClientCode, StoreCheckAppId,CustomerVisitAppId,TotalCount," +
						"FoodCount,NonFoodCount,SalesmanName,UnitManagerCode,UnitManagerName," +
						"ClientName,ChannelCode,Role,Region,RegionCode," +
						"NotApplicableItemCount,TotalApplicableCount,TotalStoreCorekuCount,StartTime,EndTime from tblStoreCheck where Status='0'";
				cursor = sqLiteDatabase.rawQuery(inventoryQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						InventoryDO inventoryDO 				= 	new InventoryDO();
						inventoryDO.inventoryId					=	cursor.getString(0);
						inventoryDO.createdBy					=	cursor.getString(1);
						inventoryDO.JourneyCode					=	cursor.getString(2);
						inventoryDO.VisitCode					=	cursor.getString(3);
						inventoryDO.date						=	cursor.getString(4);
						inventoryDO.Status						=	cursor.getString(5);
						inventoryDO.site						=	cursor.getString(6);
						inventoryDO.TotalCount					=	(int) cursor.getLong(9);
						inventoryDO.SalesmanName				=	cursor.getString(12);
						inventoryDO.ClientName					=	cursor.getString(15);
						inventoryDO.ChannelCode					=	cursor.getString(16);
						inventoryDO.Role						=	cursor.getString(17);
						inventoryDO.Region						=	cursor.getString(18);
						inventoryDO.RegionCode					=	cursor.getString(19);
						inventoryDO.NotApplicableItemCount		=	(int) cursor.getLong(20);
						inventoryDO.starTime		=	cursor.getString(23);
						inventoryDO.endTime		=	 cursor.getString(24);

						String itemQuery = "SELECT StoreCheckItemId, StoreCheckId,ItemCode,ItemDescription,CategoryCode,CategoryName,BrandCode,BrandName,AgencyCode,AgencyName,Status,StoreCheckAppId,StoreCheckItemAppId,UOM,Quantity from tblStoreCheckItem where StoreCheckId='"+inventoryDO.inventoryId+"'";
						cursorDetail = sqLiteDatabase.rawQuery(itemQuery, null);
						if(cursorDetail.moveToFirst())
						{
							do 
							{
								InventoryDetailDO inventoryDetailDO  = new InventoryDetailDO();
								inventoryDetailDO.inventoryDetailId	 =	cursorDetail.getString(0);
								inventoryDetailDO.inventoryId		 =	cursorDetail.getString(1);
								inventoryDetailDO.itemCode			 =	cursorDetail.getString(2);
								inventoryDetailDO.ItemDescription	 =	cursorDetail.getString(3);
								inventoryDetailDO.CategoryCode		 =	cursorDetail.getString(4);
								inventoryDetailDO.CategoryName		 =	cursorDetail.getString(5);
								inventoryDetailDO.BrandCode			 =	cursorDetail.getString(6);
								inventoryDetailDO.BrandName			 =	cursorDetail.getString(7);
								inventoryDetailDO.AgencyCode		 =	cursorDetail.getString(8);
								inventoryDetailDO.AgencyName		 =	cursorDetail.getString(9);
								inventoryDetailDO.status			 =	cursorDetail.getInt(10);
								inventoryDetailDO.UOM				 =	cursorDetail.getString(13);
								inventoryDetailDO.QTY				 =	cursorDetail.getInt(14);
								inventoryDO.vecInventoryDOs.add(inventoryDetailDO);
							} while (cursorDetail.moveToNext());
						}
						
						String groupQuery = "SELECT StorecheckGroupId, StoreCheckId, ItemGroupLevel, ItemGroupLevelName, TotalCount, TotalNotAvailableCount, ItemGroupCode from tblStoreCheckGroup where StoreCheckId='"+inventoryDO.inventoryId+"'";
						cursorGroup = sqLiteDatabase.rawQuery(groupQuery, null);
						if(cursorGroup.moveToFirst())
						{
							do 
							{
								InventoryGroupDO objinveInventoryGroupDO			=	new InventoryGroupDO();
								objinveInventoryGroupDO.StorecheckGroupId			=	cursorGroup.getString(0);
								objinveInventoryGroupDO.StoreCheckId				=	cursorGroup.getString(1);
								objinveInventoryGroupDO.ItemGroupLevel			 	=	cursorGroup.getString(2);
								objinveInventoryGroupDO.ItemGroupLevelName		 	=	cursorGroup.getString(3);
								objinveInventoryGroupDO.TotalCount			 		=	(int) cursorGroup.getLong(4);
								objinveInventoryGroupDO.TotalNotAvailableCount	 	=	(int) cursorGroup.getLong(5);
								objinveInventoryGroupDO.ItemGroupCode			 	=	cursorGroup.getString(6);
								inventoryDO.vecGroupDOs.add(objinveInventoryGroupDO);
							} while (cursorGroup.moveToNext());
						}
						
						if(cursorDetail!=null && !cursorDetail.isClosed())
							cursorDetail.close();
						if(cursorGroup!=null && !cursorGroup.isClosed())
							cursorGroup.close();
//						if(inventoryDO.vecInventoryDOs != null && inventoryDO.vecInventoryDOs.size() > 0)
							vecInventoryDOs.add(inventoryDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				if(cursorDetail!=null && !cursorDetail.isClosed())
					cursorDetail.close();
				
				if(cursorGroup!=null && !cursorGroup.isClosed())
					cursorGroup.close();
				DatabaseHelper.closedatabase();
			}
	
			return vecInventoryDOs;
		}
	}
	
	public boolean updateInventory(Vector<InventoryDO> vecOrderlist)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteStatement stmtUpdateInventory=null;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				stmtUpdateInventory = objSqliteDB.compileStatement("UPDATE tblStoreCheck SET Status = ? where StoreCheckId=?");
				for(InventoryDO objAllUsersDo : vecOrderlist)
				{
					stmtUpdateInventory.bindLong(1, 1);
					stmtUpdateInventory.bindString(2, objAllUsersDo.inventoryId);
					stmtUpdateInventory.execute();
				}
				return true;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return false;
			}
			finally
			{
				if(stmtUpdateInventory!=null)
					stmtUpdateInventory.close();
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
	}

	public int getPasscodeAvailibility() 
	{
		synchronized (MyApplication.MyLock) 
		{
			Cursor cursor = null;
			SQLiteDatabase mDatabase = null;
			int count = 0;
			try
			{
				mDatabase 	= 	DatabaseHelper.openDataBase();
				cursor  	= 	mDatabase.rawQuery("SELECT COUNT(*) FROM tblPassCode WHERE IsUsed = 0", null);
				
				if(cursor.moveToFirst())
					count  =  cursor.getInt(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				
				if(mDatabase != null)
					mDatabase.close();
			}
			return count;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public Vector<OrderDO> getNewCustomerDeliveryStatusOrderList(String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<OrderDO> vectorOrderList = new Vector<OrderDO>();
			SQLiteDatabase sqLiteDatabase   = null;
			String strQuery = "";
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase   = DatabaseHelper.openDataBase();
				strQuery = "SELECT DISTINCT OT.OrderId, OT.TRXStatus, OT.OrderDate, OT.SiteNo, CST.SiteName, CST.Billto, " +
						   "CST.country,OT.EmpNo FROM tblOrderHeader OT ,tblNewHouseHoldCustomer CST  where OT.SiteNo=CST.CustomerId " +
						   "and OT.OrderId in(select OrderId from tblOrderDetail) AND " +
						   "(OT.OrderDate LIKE '"+date+"%' OR OT.DeliveryDate LIKE '"+date+"%') and CST.CustomerId " +
						   "not in(select CustomerSiteId from tblCustomerSites) ORDER BY OT.OrderDate DESC";
				cursor	=	sqLiteDatabase.rawQuery(strQuery,null);
				
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						OrderDO orderDO 		= 	new OrderDO();
						orderDO.OrderId  		= 	cursor.getString(0); 
						orderDO.DeliveryStatus 	= 	cursor.getString(1);
						orderDO.InvoiceDate 	= 	cursor.getString(2);
						orderDO.CustomerSiteId 	= 	cursor.getString(3);
						orderDO.strCustomerName = 	cursor.getString(4);
						orderDO.strAddress1 	= 	cursor.getString(5);
						orderDO.strAddress2 	= 	cursor.getString(6);
						orderDO.DeliveryAgentId = 	cursor.getString(7);
						vectorOrderList.add(orderDO);
					}
					while(cursor.moveToNext());
					
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase != null)
					sqLiteDatabase.close();
			}
			return vectorOrderList;
		}
	}
	
	//02-11-2014 Added by venkat
		//method to insert or update the banks Detail
			public boolean insertCurrencyDetails(Vector<CurrencyDO> vecCurrencies)
			{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCurrency WHERE CurrencyId = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblCurrency (CurrencyId, Code, ArabicName,Rate, LevelId, Decimals,IsActive,Name) VALUES(?,?,?,?,?,?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblCurrency SET Code= ?,  ArabicName= ?, Rate= ?,  LevelId= ?,  Decimals= ?, IsActive= ?, Name=? WHERE CurrencyId = ?");
					
					for(CurrencyDO objCurrencyDO : vecCurrencies)
					{
						stmtSelectRec.bindLong(1, objCurrencyDO.CurrencyId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objCurrencyDO != null)
						{
							if (countRec != 0) 
							{
								stmtUpdate.bindString(1, objCurrencyDO.Code);
								stmtUpdate.bindString(2, objCurrencyDO.ArabicName);
								stmtUpdate.bindLong(3, (long) objCurrencyDO.Rate);
								stmtUpdate.bindLong(4, objCurrencyDO.LevelId);
								stmtUpdate.bindLong(5, objCurrencyDO.Decimals);
								stmtUpdate.bindString(6, ""+objCurrencyDO.IsActive);
								stmtUpdate.bindString(7, objCurrencyDO.Name);
								stmtUpdate.bindLong(8, objCurrencyDO.CurrencyId);
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindLong(1, objCurrencyDO.CurrencyId);
								stmtInsert.bindString(2, objCurrencyDO.Code);
								stmtInsert.bindString(3, objCurrencyDO.ArabicName);
								stmtInsert.bindLong(4, (long) objCurrencyDO.Rate);
								stmtInsert.bindLong(5, objCurrencyDO.LevelId);
								stmtInsert.bindLong(6, objCurrencyDO.Decimals);
								stmtInsert.bindString(7, ""+objCurrencyDO.IsActive);
								stmtInsert.bindString(8, objCurrencyDO.Name);
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				finally
				{
					if(objSqliteDB != null)
					{
						objSqliteDB.close();
					}
				}
				return true;
			}

			public boolean insertBrandDetails(Vector<BrandDO> vecBrands)
			{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblBrand WHERE BrandId = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblBrand (BrandId, BrandName, ParentCode,BrandImage) VALUES(?,?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblBrand SET BrandName = ?, ParentCode =?,BrandImage=? WHERE BrandId = ?");
					
					for(BrandDO objBrandDO : vecBrands)
					{
						stmtSelectRec.bindString(1, objBrandDO.brandId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objBrandDO != null)
						{
							if (countRec != 0) 
							{
								stmtUpdate.bindString(1, objBrandDO.brandName);
								stmtUpdate.bindString(2, objBrandDO.parentCode);
								stmtUpdate.bindString(3, objBrandDO.image);
								stmtUpdate.bindString(4, objBrandDO.brandId);
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,objBrandDO.brandId);
								stmtInsert.bindString(2, objBrandDO.brandName);
								stmtInsert.bindString(3, objBrandDO.parentCode);
								stmtInsert.bindString(4, objBrandDO.image);
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				finally
				{
					if(objSqliteDB != null)
					{
						
						objSqliteDB.close();
					}
				}
				return true;
			}
			public boolean insertAgenciesDetails(Vector<AgencyDO> vecAgencyDO)
			{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblAgency WHERE AgencyId = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblAgency (AgencyId, AgencyName, Priority) VALUES(?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblAgency SET AgencyName = ? WHERE AgencyId = ?");
					
					for(AgencyDO objBrandDO : vecAgencyDO)
					{
						stmtSelectRec.bindString(1, objBrandDO.AgencyId);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objBrandDO != null)
						{
							if (countRec != 0) 
							{
								stmtUpdate.bindString(1, objBrandDO.AgencyName);
								stmtUpdate.bindString(2, objBrandDO.AgencyId);
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,objBrandDO.AgencyId);
								stmtInsert.bindString(2, objBrandDO.AgencyName);
								stmtInsert.bindString(3, objBrandDO.Priority);
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				finally
				{
					if(objSqliteDB != null)
					{
						
						objSqliteDB.close();
					}
				}
				return true;
			}
			
			public boolean insertUOMFactorDetails(Vector<UOMFactorDO> vecUOMFactorDOs)
			{
				synchronized(MyApplication.MyLock) 
				{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblUOMFactor WHERE ItemCode = ? and UOM=?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUOMFactor (ItemCode, UOM, Factor,ModifiedDate,ModifiedTime,EAConversion) VALUES(?,?,?,?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblUOMFactor SET Factor =?,ModifiedDate=?,ModifiedTime=?,EAConversion=? WHERE ItemCode = ? and UOM = ?");
					
					for(UOMFactorDO objUOMFactorDO : vecUOMFactorDOs)
					{
						stmtSelectRec.bindString(1, objUOMFactorDO.ItemCode);
						stmtSelectRec.bindString(2, objUOMFactorDO.UOM);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objUOMFactorDO != null)
						{
							if (countRec != 0) 
							{
								stmtUpdate.bindLong(1, (long) objUOMFactorDO.Factor);
								stmtUpdate.bindLong(2, objUOMFactorDO.ModifiedDate);
								stmtUpdate.bindLong(3, objUOMFactorDO.ModifiedTime);
								stmtUpdate.bindLong(4, objUOMFactorDO.EAConversion);
								stmtUpdate.bindString(5, objUOMFactorDO.ItemCode);
								stmtUpdate.bindString(6, objUOMFactorDO.UOM);
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1, objUOMFactorDO.ItemCode);
								stmtInsert.bindString(2, objUOMFactorDO.UOM);
								stmtInsert.bindLong(3, (long) objUOMFactorDO.Factor);
								stmtInsert.bindLong(4, objUOMFactorDO.ModifiedDate);
								stmtInsert.bindLong(5, objUOMFactorDO.ModifiedTime);
								stmtInsert.bindLong(6, objUOMFactorDO.EAConversion);
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				finally
				{
					if(objSqliteDB != null)
						objSqliteDB.close();
				}
				return true;
				}
			}
			public boolean insertWareHouseDetails(Vector<WareHouseDO> vecAgencyDO)
			{
				SQLiteDatabase objSqliteDB = null;
				try
				{
					 objSqliteDB = DatabaseHelper.openDataBase();
					//Opening the database
						
					//Query to Insert the User information in to UserInfo Table
					SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblWareHouse WHERE Code = ?");
					SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblWareHouse (Code, Name,SalesOrgCode ) VALUES(?,?,?)");
					SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblWareHouse SET Name = ?,SalesOrgCode=? WHERE Code = ?");
					
					for(WareHouseDO objWareHouseDO : vecAgencyDO)
					{
						stmtSelectRec.bindString(1, objWareHouseDO.Code);
						long countRec = stmtSelectRec.simpleQueryForLong();
						
						if(objWareHouseDO != null)
						{
							if (countRec != 0) 
							{
								stmtUpdate.bindString(1, objWareHouseDO.Name);
								stmtUpdate.bindString(2, objWareHouseDO.SalesOrgCode);
								stmtUpdate.bindString(3, objWareHouseDO.Code);
								stmtUpdate.execute();
							}
							else
							{
								stmtInsert.bindString(1,objWareHouseDO.Code);
								stmtInsert.bindString(2, objWareHouseDO.Name);
								stmtInsert.bindString(2, objWareHouseDO.SalesOrgCode);
								stmtInsert.executeInsert();
							}
						}
					}
					
					stmtSelectRec.close();
					stmtUpdate.close();
					stmtInsert.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return false;
				}
				
				finally
				{
					if(objSqliteDB != null)
					{
						
						objSqliteDB.close();
					}
				}
				return true;
			}

			//////////////////////to update the Store check status///////////////////
	public boolean updateCustomerVisit_StoreCheck(CustomerVisitDO userJourneyPlan)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblCustomerVisit SET typeOfCall=? WHERE JourneyCode=? AND VisitCode=?");
				 
				stmtUpdate.bindString(1, userJourneyPlan.TypeOfCall);
				stmtUpdate.bindString(2, userJourneyPlan.JourneyCode);
				stmtUpdate.bindString(3, userJourneyPlan.VisitCode);
				stmtUpdate.execute();
				
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result =  false;
			}
			
			finally
			{
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return result;
		}
	}

	public int isBuildExpired(String installationDate) 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			int isExpired = 0;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "select SettingValue from tblSettings where SettingName = 'Expired'";
				cursor 	=  objSqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
					isExpired = cursor.getInt(0);
				/*if(isExpired==0){
					int days = CalendarUtils.getDiffBtwDatesInDays(installationDate, CalendarUtils.getOrderPostDate());
					if(days>7)
						isExpired=1;
				}*/
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return isExpired;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return isExpired;
		}
	}

	public boolean isAnyOrderPending()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			boolean isDatatoUpload = false;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			int statusCount = 0;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String queryTRXHeader = "SELECT COUNT(*) FROM tblTrxHeader WHERE Status='0'"; 
				cursor = objSqliteDB.rawQuery(queryTRXHeader, null);
				if(cursor != null && cursor.moveToFirst())
				{
					statusCount = cursor.getInt(0);
					if(statusCount <= 0)
					{
						if(cursor != null && !cursor.isClosed())
							cursor.close();
					}
				}
				
				if(statusCount > 0)
					isDatatoUpload = true;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				return isDatatoUpload;
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return isDatatoUpload;
		}
	}
}
