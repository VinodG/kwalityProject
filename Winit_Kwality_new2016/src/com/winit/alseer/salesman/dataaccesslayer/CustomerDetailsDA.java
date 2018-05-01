package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.databaseaccess.DictionaryEntry;
import com.winit.alseer.salesman.dataobject.CustomerCreditLimitDo;
import com.winit.alseer.salesman.dataobject.CustomerDO;
import com.winit.alseer.salesman.dataobject.CustomerOrdersDO;
import com.winit.alseer.salesman.dataobject.CustomerSite_NewDO;
import com.winit.alseer.salesman.dataobject.Customer_GroupDO;
import com.winit.alseer.salesman.dataobject.InsertCustoDo;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.MallsDetails;
import com.winit.alseer.salesman.dataobject.SyncLogDO;
import com.winit.alseer.salesman.dataobject.UnUploadedDataDO;
import com.winit.alseer.salesman.dataobject.UserJourneyPlanDO;
import com.winit.alseer.salesman.utilities.CalendarUtils;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class CustomerDetailsDA
{
	public ArrayList<JourneyPlanDO> getJourneyPlanForTeleOrder(String strUserId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				c 			= objSqliteDB.rawQuery("SELECT C.*,UT.UserName FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson ORDER BY C.SiteName", null);
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.site =c.getString(1);//customer code
						j.siteName =c.getString(2);
						j.customerId =c.getString(3);
						j.custmerStatus =c.getString(4);
						j.custAccCreationDate =c.getString(5); /** 0001-01-01T12:00:00 !*/
						j.partyName =c.getString(6);
						j.channelCode =c.getString(7);//channel code
						j.subChannelCode =c.getString(8);
						j.regionCode =c.getString(9);
						j.coutryCode =c.getString(10);
						j.category = c.getString(11);
						j.addresss1 =c.getString(12);
						j.addresss2 =c.getString(13);
						j.addresss3 =c.getString(14);
						j.addresss4 =c.getString(15);
					
						j.poNumber =c.getString(16);
						j.city =c.getString(17);
						j.customerPaymentType =c.getString(18);
						j.paymentTermCode =c.getString(19);
						j.creditLimit =c.getString(20);
						j.geoCodeX =c.getDouble(21);
						j.geoCodeY =c.getDouble(22);
						j.Passcode =c.getString(23);
						j.email=c.getString(24);
						j.contectPersonName=c.getString(25);
						j.phoneNumber =c.getString(26);
						j.appCustomerId =c.getString(27);
						j.mobileNo1 =c.getString(28);
						j.mobileNo2 =c.getString(29);
						j.website =c.getString(30);
//						j.customerType = c.getString(31);
						j.createdby = c.getString(32);
						j.modifiedBy = c.getString(33);
						j.source = c.getString(34);
						j.customerCategory = c.getString(35);
						j.customerSubCategory = c.getString(36);
						j.customerGroupCode = c.getString(37);//customer group
						j.modifiedDate = c.getString(38);
						j.modifiedTime = c.getString(39);
						j.currencyCode = c.getString(40);
						j.StoreGrowth = c.getString(41);
						j.priceList = c.getString(42);
						j.salesmanCode = c.getString(43);
						j.customerPaymentMode =c.getString(44);
						
						j.Attribute1 =c.getString(48)+"";
						j.Attribute2 =c.getString(49)+"";
						j.Attribute3 =c.getString(50)+"";
						j.Attribute4 =c.getString(51)+"";
						j.Attribute5 =c.getString(52)+"";
						j.Attribute6 =c.getString(53)+"";
						j.Attribute7 =c.getString(54)+"";
						j.Attribute8 =c.getString(55)+"";
						j.Attribute9 =c.getString(56)+"";
						j.Attribute10 =c.getString(57)+"";
						j.Attribute10 =c.getString(58)+"";
						if(c.getString(60) != null)
							j.blockedStatus =c.getString(60);
						else
							j.blockedStatus ="";
						j.salesmanName = c.getString(65);
						
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return arJourneyPlan;
		}
	}
	
	/**
	 * Customer List with Promotional Discount
	 * @param strUserId
	 * @return
	 */
	public ArrayList<JourneyPlanDO> getJourneyPlanForTeleOrderWithDiscount(String strUserId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
//				String query = "SELECT C.*,UT.UserName,PBD.PromotionId,PBD.Discount FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId ORDER BY C.SiteName";
				String query;
				if(AppConstants.isOldPromotion)
				{
					query = "SELECT C.*,UT.UserName,PBD.PromotionId, " +
							"CASE WHEN SC.Discount > 0 THEN SC.Discount " +
							"ELSE PBD.Discount END AS Discount FROM tblCustomer C " +
							"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
							"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
							"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
							"WHERE C.BlockedStatus='False' COLLATE NOCASE " +
							"ORDER BY C.SiteName ";
				}
				else
				{
					query = "SELECT C.*,UT.UserName " +
							"FROM tblCustomer C " +
							"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"WHERE C.BlockedStatus='False' COLLATE NOCASE " +
							"ORDER BY C.SiteName ";
				}
				LogUtils.debug("query_custoemrs", query);
				c 			= objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.site =c.getString(1);//customer code
						j.siteName =c.getString(2);
						j.customerId =c.getString(3);
						j.custmerStatus =c.getString(4);
						j.custAccCreationDate =c.getString(5); /** 0001-01-01T12:00:00 !*/
						j.partyName =c.getString(6);
						j.channelCode =c.getString(7);//channel code
						j.subChannelCode =c.getString(8);
						j.regionCode =c.getString(9);
						j.coutryCode =c.getString(10);
						j.category = c.getString(11);
						j.addresss1 =c.getString(12);
						j.addresss2 =c.getString(13);
						j.addresss3 =c.getString(14);
						j.addresss4 =c.getString(15);
					
						j.poNumber =c.getString(16);
						j.city =c.getString(17);
						j.customerPaymentType =c.getString(18);
						j.paymentTermCode =c.getString(19);
						j.creditLimit =c.getString(20);
						j.geoCodeX =c.getDouble(21);
						j.geoCodeY =c.getDouble(22);
						j.Passcode =c.getString(23);
						j.email=c.getString(24);
						j.contectPersonName=c.getString(25);
						j.phoneNumber =c.getString(26);
						j.appCustomerId =c.getString(27);
						j.mobileNo1 =c.getString(28);
						j.mobileNo2 =c.getString(29);
						j.website =c.getString(30);
//						j.customerType = c.getString(31);
						j.createdby = c.getString(32);
						j.modifiedBy = c.getString(33);
						j.source = c.getString(34);
						j.customerCategory = c.getString(35);
						j.customerSubCategory = c.getString(36);
						j.customerGroupCode = c.getString(37);//customer group
						j.modifiedDate = c.getString(38);
						j.modifiedTime = c.getString(39);
						j.currencyCode = c.getString(40);
						j.StoreGrowth = c.getString(41);
						j.priceList = c.getString(42);
						j.salesmanCode = c.getString(43);
						j.customerPaymentMode =c.getString(44);
						
						j.Attribute1 =c.getString(48)+"";
						j.Attribute2 =c.getString(49)+"";
						j.Attribute3 =c.getString(50)+"";
						j.Attribute4 =c.getString(51)+"";
						j.Attribute5 =c.getString(52)+"";
						j.Attribute6 =c.getString(53)+"";        
						j.Attribute7 =c.getString(54)+"";
						j.Attribute8 =c.getString(55)+"";
						j.Attribute9 =c.getString(56)+"";
						j.Attribute10 =c.getString(57)+"";
						j.Attribute10 =c.getString(58)+"";
						
						j.Attribute11 =c.getString(58)+"";
						if(c.getString(60) != null)
							j.blockedStatus =c.getString(60);
						else
							j.blockedStatus ="";
						
						j.PromotionalDiscount 	= c.getString(65); //this is for icecream
						j.statementdiscount = c.getString(67); // this is icecream

						j.GeoCodeFlag = c.getString(68); //decides to show force check popup
						//newly added for food and tpt discounts
//						"GeoCodeFlag" BOOL, "FInvDiscYH" VARCHAR, "FStatDiscYH" VARCHAR, "TInvDiscYH" VARCHAR, "TStatDiscYH"
						j.FInvDiscYH = c.getString(69);
						j.FStatDiscYH = c.getString(70);
						j.TInvDiscYH = c.getString(71);
						j.TStatDiscYH = c.getString(72);
						j.VATNo = c.getString(73);
						j.IsVATApplicable = c.getString(74).equalsIgnoreCase("True")?true:false;

						j.salesmanName = c.getString(75);
						
						if(AppConstants.isOldPromotion)
						{
							j.PromotionID 			= c.getString(76);
							j.PromotionalDiscount 	= c.getString(77);
						}
//						j.salesmanName = c.getString(68);
//
//						if(AppConstants.isOldPromotion)
//						{
//							j.PromotionID 			= c.getString(68);
//							j.PromotionalDiscount 	= c.getString(69);
//						}

						CustomerCreditLimitDo obj = new CustomerDA().getCustomerCreditLimit(j.site);
						j.balanceAmount = obj.availbleLimit;
						if(!j.blockedStatus.equalsIgnoreCase("True"))
							arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return arJourneyPlan;
		}
	}
	
	public JourneyPlanDO getCustometBySiteId(String siteId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			JourneyPlanDO j = null;
			Cursor c = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT C.*,UT.UserName,PBD.PromotionId,PBD.Discount " +
						"FROM tblCustomer C " +
						"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
						"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
						"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON  PBD.PromotionId = PA.PromotionId " +
						"WHERE C.Site = '"+siteId+"'";
//				c 			= objSqliteDB.rawQuery("SELECT C.*,UT.UserName FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson WHERE C.Site = '"+siteId+"'", null);
				c 			= objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
				{
					j = new JourneyPlanDO();
					j.site =c.getString(1);//customer code
					j.siteName =c.getString(2);
					j.customerId =c.getString(3);
					j.custmerStatus =c.getString(4);
					j.custAccCreationDate =c.getString(5);
					j.partyName =c.getString(6);
					j.channelCode =c.getString(7);//channel code
					j.subChannelCode =c.getString(8);
					j.regionCode =c.getString(9);
					j.coutryCode =c.getString(10);
					j.category = c.getString(11);
					j.addresss1 =c.getString(12);
					j.addresss2 =c.getString(13);
					j.addresss3 =c.getString(14);
					j.addresss4 =c.getString(15);
				
					j.poNumber =c.getString(16);
					j.city =c.getString(17);
					j.customerPaymentType =c.getString(18);
					j.paymentTermCode =c.getString(19);
					j.creditLimit =c.getString(20);
					j.geoCodeX =c.getDouble(21);
					j.geoCodeY =c.getDouble(22);
					j.Passcode =c.getString(23);
					j.email=c.getString(24);
					j.contectPersonName=c.getString(25);
					j.phoneNumber =c.getString(26);
					j.appCustomerId =c.getString(27);
					j.mobileNo1 =c.getString(28);
					j.mobileNo2 =c.getString(29);
					j.website =c.getString(30);
//						j.customerType = c.getString(31);
					j.createdby = c.getString(32);
					j.modifiedBy = c.getString(33);
					j.source = c.getString(34);
					j.customerCategory = c.getString(35);
					j.customerSubCategory = c.getString(36);
					j.customerGroupCode = c.getString(37);//customer group
					j.modifiedDate = c.getString(38);
					j.modifiedTime = c.getString(39);
					j.currencyCode = c.getString(40);
					j.StoreGrowth = c.getString(41);
					j.priceList = c.getString(42);
					j.salesmanCode = c.getString(43);
					j.customerPaymentMode =c.getString(44);
					
					j.Attribute1 =c.getString(48)+"";
					j.Attribute2 =c.getString(49)+"";
					j.Attribute3 =c.getString(50)+"";
					j.Attribute4 =c.getString(51)+"";
					j.Attribute5 =c.getString(52)+"";
					j.Attribute6 =c.getString(53)+"";
					j.Attribute7 =c.getString(54)+"";
					j.Attribute8 =c.getString(55)+"";
					j.Attribute9 =c.getString(56)+"";
					j.Attribute10 =c.getString(57)+"";
					//j.Attribute10 =c.getString(58)+"";
					if(c.getString(60) != null)
						j.blockedStatus =c.getString(60);
					else
						j.blockedStatus ="";

					j.statementdiscount =c.getString(67);
					
					j.VATNo 			= c.getString(73);
					j.IsVATApplicable 	= c.getString(74).equalsIgnoreCase("true")?true:false;
					j.salesmanName = c.getString(75);
					j.PromotionID 			= c.getString(76);
					j.PromotionalDiscount 	= c.getString(77);
						
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return j;
		}
	}
	
	public Vector<JourneyPlanDO> getCustomerDetailsForJourney(String strUserId)
	{
		synchronized(MyApplication.MyLock) 
		{
			JourneyPlanDO objCustomer;
			Vector<JourneyPlanDO>vectCustomer;
			DictionaryEntry[][] data=null;
			vectCustomer = new Vector<JourneyPlanDO>();
			String query="SELECT rowid, *FROM tblCustomer ORDER BY Site";
			
			try
			{
				data = DatabaseHelper.get(query);
				if(data !=null && data.length>0)
				{
					for(int i=0;i<data.length;i++)
					{
						 objCustomer 			    	= 	new JourneyPlanDO();
						 objCustomer.rowid				=	StringUtils.getInt(data[i][0].value.toString());
						 objCustomer.site		=	data[i][2].value.toString();
						 objCustomer.siteName			=	data[i][3].value.toString();
						 objCustomer.customerId			=	data[i][4].value.toString();
						 objCustomer.addresss1			=	""+data[i][13].value.toString();
						 
						 if(data[i][5].value != null)
							 objCustomer.addresss2		=	""+data[i][14].value.toString();
						 else
							 objCustomer.addresss2		=	"N/A";
						 
						 objCustomer.city				=	data[i][18].value.toString();
						 
						 if(data[0][7].value!=null)
							 objCustomer.geoCodeX		=	StringUtils.getDouble(data[0][22].value.toString());
						 if(data[0][8].value!=null)
							 objCustomer.geoCodeY		=	StringUtils.getDouble(data[0][23].value.toString());
						 
						 objCustomer.customerPaymentMode=	data[i][45].value.toString();
						 objCustomer.paymentTermCode	=	data[i][20].value.toString();
						 objCustomer.email				=	data[i][25].value.toString();
//						 objCustomer.isSchemeAplicable	=	StringUtils.getInt(data[i][34].value.toString());
						 vectCustomer.add(objCustomer);
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
			
			 return vectCustomer;
		}
	}
	
	public boolean updateCreatedCustomers(Vector<InsertCustoDo> vecInsertCustoDos, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement updateCustomerRecord = objSqliteDB.compileStatement("update tblNewHouseHoldCustomer set isPushed ='1', CustomerId = ? where CustomerId =?");
				SQLiteStatement updateCustomerRecord_empty = objSqliteDB.compileStatement("update tblNewHouseHoldCustomer set CustomerId = ? where CustomerId =?");
				
				SQLiteStatement order = objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments = objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				for(InsertCustoDo insertCustoDo :vecInsertCustoDos)
				{
					if(!insertCustoDo.strNewSiteId.equalsIgnoreCase("-1"))
					{
						order.bindString(1, insertCustoDo.strNewSiteId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, insertCustoDo.strNewSiteId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord.bindString(1, insertCustoDo.strNewSiteId);
						updateCustomerRecord.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord.execute();
					}
					else
					{
						
						String strCustomerId = "";
						//Opening the database
						String query = "SELECT id from tblOfflineData where SalesmanCode ='"+strSalesCode+"' AND Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerId FROM tblNewHouseHoldCustomer) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							strCustomerId = cursor.getString(0);
						}
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
						
						order.bindString(1, strCustomerId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, strCustomerId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord_empty.bindString(1, strCustomerId);
						updateCustomerRecord_empty.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord_empty.execute();
					}
				}
				
				order.close();
				payments.close();
				updateCustomerRecord.close();
				
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
	
	public boolean updateCreatedCustomersNew(Vector<InsertCustoDo> vecInsertCustoDos, String strSalesCode)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement updateCustomerRecord = objSqliteDB.compileStatement("update tblCustomerSites set source ='1', CustomerSiteId = ? where CustomerSiteId =?");
				SQLiteStatement updateCustomerRecord_empty = objSqliteDB.compileStatement("update tblCustomerSites set CustomerSiteId = ? where CustomerSiteId =?");
				
				SQLiteStatement order = objSqliteDB.compileStatement("update tblOrderHeader set SiteNo =? where SiteNo =?");
				SQLiteStatement payments = objSqliteDB.compileStatement("update tblPaymentHeader set SiteId =? where SiteId =?");
				
				SQLiteStatement updateCustomerSub = objSqliteDB.compileStatement("update tblCustomerSitesSub set CustomerSiteId =? where CustomerSiteId =?");
				
				
				for(InsertCustoDo insertCustoDo :vecInsertCustoDos)
				{
					if(!insertCustoDo.strNewSiteId.equalsIgnoreCase("-1"))
					{
						order.bindString(1, insertCustoDo.strNewSiteId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, insertCustoDo.strNewSiteId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerRecord.bindString(1, insertCustoDo.strNewSiteId);
						updateCustomerRecord.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord.execute();
					}
					else
					{
						
						String strCustomerId = "";
						//Opening the database
						String query = "SELECT id from tblOfflineData where SalesmanCode ='"+strSalesCode+"' AND Type ='"+AppConstants.Customer+"' AND status = 0 AND id NOT IN(SELECT CustomerSiteId FROM tblCustomerSites) Order By id Limit 1";
						Cursor cursor = objSqliteDB.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							strCustomerId = cursor.getString(0);
						}
						if(cursor!=null && !cursor.isClosed())
							cursor.close();
						
						Log.e("strCustomerId", "strCustomerId - "+strCustomerId);
						objSqliteDB.execSQL("UPDATE tblOfflineData SET status=1 WHERE Id='"+strCustomerId+"'");
						
						order.bindString(1, strCustomerId);
						order.bindString(2, insertCustoDo.strOldSiteId);
						order.execute();
						
						payments.bindString(1, strCustomerId);
						payments.bindString(2, insertCustoDo.strOldSiteId);
						payments.execute();
						
						updateCustomerSub.bindString(1, strCustomerId);
						updateCustomerSub.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerSub.execute();
						
						updateCustomerRecord_empty.bindString(1, strCustomerId);
						updateCustomerRecord_empty.bindString(2, insertCustoDo.strOldSiteId);
						updateCustomerRecord_empty.execute();
					}
				}
				
				order.close();
				payments.close();
				updateCustomerRecord.close();
				updateCustomerSub.close();
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
	
	public boolean insertAllCustomerPendingInvoices(Vector<CustomerOrdersDO> vecCustomerPendingInvoices)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result =true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
				
				SQLiteStatement stmtSelectPendingIn		= objSqliteDB.compileStatement("SELECT COUNT(*) from tblPendingInvoices WHERE InvoiceNumber = ?");
				SQLiteStatement stmtInsertPendingIn 	= objSqliteDB.compileStatement("INSERT INTO tblPendingInvoices(OrderId , PresellerId,CustomerSiteId,TotalAmount, DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,IsOutStanding,DocType,DueDate,ReferenceDocument,Division) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdatePendingIn 	= objSqliteDB.compileStatement("UPDATE tblPendingInvoices SET PresellerId =?,CustomerSiteId=?,TotalAmount=?,DeliveryDate=?,BalanceAmount=?,OrderId=?,InvoiceDate=?,IsOutStanding =?,DocType=?,DueDate=?,ReferenceDocument=? WHERE InvoiceNumber = ?");
				 
	
				if(vecCustomerPendingInvoices != null && vecCustomerPendingInvoices.size() > 0)
				{
					objSqliteDB.execSQL("Delete from tblPendingInvoices");//added by anil, suggested by venky sir
					for(int index = 0 ; index < vecCustomerPendingInvoices.size() ; index++)
					{
						CustomerOrdersDO objCustomerOrders = vecCustomerPendingInvoices.get(index);
						stmtSelectPendingIn.bindString(1, objCustomerOrders.invoiceNumber);
						/*long countRecord = stmtSelectPendingIn.simpleQueryForLong();
						if(countRecord != 0)
						{
							stmtUpdatePendingIn.bindString(1, objCustomerOrders.salesManCode);
							stmtUpdatePendingIn.bindString(2, objCustomerOrders.siteNumber);
							stmtUpdatePendingIn.bindString(3, objCustomerOrders.invoiceAmount);
							stmtUpdatePendingIn.bindString(4, objCustomerOrders.invoiceDate);
							stmtUpdatePendingIn.bindString(5, objCustomerOrders.balanceAmount);
							stmtUpdatePendingIn.bindString(6, objCustomerOrders.orderId);
							stmtUpdatePendingIn.bindString(7, objCustomerOrders.invoiceDate);
							stmtUpdatePendingIn.bindString(8, objCustomerOrders.IsOutStanding);
							stmtUpdatePendingIn.bindString(9, objCustomerOrders.Doc_Type);
							stmtUpdatePendingIn.bindString(10, objCustomerOrders.Due_Date);
							stmtUpdatePendingIn.bindString(11, objCustomerOrders.Reference_Document);
							stmtUpdatePendingIn.bindString(12, objCustomerOrders.invoiceNumber);
							stmtUpdatePendingIn.execute();
						}
						else*/
						{
							stmtInsertPendingIn.bindString(1,  objCustomerOrders.orderId);
							stmtInsertPendingIn.bindString(2,  objCustomerOrders.salesManCode);
							stmtInsertPendingIn.bindString(3,  objCustomerOrders.siteNumber);
							stmtInsertPendingIn.bindString(4,  objCustomerOrders.invoiceAmount);
							stmtInsertPendingIn.bindString(5,  objCustomerOrders.invoiceDate);
							stmtInsertPendingIn.bindString(6,  objCustomerOrders.balanceAmount);
							stmtInsertPendingIn.bindString(7,  objCustomerOrders.invoiceNumber);
							stmtInsertPendingIn.bindString(8,  objCustomerOrders.invoiceDate);
							stmtInsertPendingIn.bindString(9,  objCustomerOrders.IsOutStanding);
							stmtInsertPendingIn.bindString(10, objCustomerOrders.Doc_Type);
							stmtInsertPendingIn.bindString(11, objCustomerOrders.Due_Date);
							stmtInsertPendingIn.bindString(12, objCustomerOrders.Reference_Document);
							stmtInsertPendingIn.bindLong(13, objCustomerOrders.Division);
							stmtInsertPendingIn.executeInsert();
						}
					}
				}
				
				stmtSelectPendingIn.close();
				stmtInsertPendingIn.close();
				stmtUpdatePendingIn.close();
				
				result = true;
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
			return result;
		}
	}
	
	public void deletePendingInvoices()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			LogUtils.errorLog("deleting","tblPendingInvoices");
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				objSqliteDB.execSQL("Delete from tblPendingInvoices");
				
				LogUtils.errorLog("deleted","tblPendingInvoices");
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
	
	public boolean deleteCustomers(Vector<SyncLogDO> vecSyncLogDO)
	{
		boolean isDeleted = false;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				SQLiteStatement delete = objSqliteDB.compileStatement("DELETE  FROM tblCustomerSites WHERE CustomerSiteId = ?");
				if(vecSyncLogDO != null && vecSyncLogDO.size() > 0)
				{
					for(SyncLogDO syncLogDO : vecSyncLogDO)
					{
						LogUtils.errorLog("syncLogDO - ", syncLogDO.entityId);
						delete.bindString(1, syncLogDO.entityId);
						delete.execute();
					}
				}
				isDeleted = true;
				delete.close();
			}
			catch (Exception e) 
			{
				isDeleted = false;
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return isDeleted;
		}
	}
	
	public ArrayList<JourneyPlanDO> getAllCreditCustomerList(String strPaymentType)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				c 			= objSqliteDB.rawQuery("SELECT C.*,UT.UserName,(SELECT Outstanding FROM vw_CustomerCreditLimit WHERE vw_CustomerCreditLimit.Site=C.Site) AS Outstanding " +
												   "FROM tblCustomer C LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson ORDER BY C.SiteName", null);
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.site =c.getString(1);
						j.siteName =c.getString(2);
						j.customerId =c.getString(3);
						j.custmerStatus =c.getString(4);
						j.custAccCreationDate =c.getString(5);
						j.partyName =c.getString(6);
						j.channelCode =c.getString(7);
						j.subChannelCode =c.getString(8);
						j.regionCode =c.getString(9);
						j.coutryCode =c.getString(10);
						j.category = c.getString(11);
						j.addresss1 =c.getString(12);
						j.addresss2 =c.getString(13);
						j.addresss3 =c.getString(14);
						j.addresss4 =c.getString(15);
					
						j.poNumber =c.getString(16);
						j.city =c.getString(17);
						j.customerPaymentType =c.getString(18);
						j.paymentTermCode =c.getString(19);
						j.creditLimit =c.getString(20);
						j.geoCodeX =c.getDouble(21);
						j.geoCodeY =c.getDouble(22);
						j.Passcode =c.getString(23);
						j.email=c.getString(24);
						j.contectPersonName=c.getString(25);
						j.phoneNumber =c.getString(26);
						j.appCustomerId =c.getString(27);
						j.mobileNo1 =c.getString(28);
						j.mobileNo2 =c.getString(29);
						j.website =c.getString(30);
//						j.customerType = c.getString(31);
						j.createdby = c.getString(32);
						j.modifiedBy = c.getString(33);
						j.source = c.getString(34);
						j.customerCategory = c.getString(35);
						j.customerSubCategory = c.getString(36);
						j.customerGroupCode = c.getString(37);
						j.modifiedDate = c.getString(38);
						j.modifiedTime = c.getString(39);
						j.currencyCode = c.getString(40);
						j.StoreGrowth = c.getString(41);
						j.priceList = c.getString(42);
						j.salesmanCode = c.getString(43);
						j.customerPaymentMode =c.getString(44);
						j.salesmanName = c.getString(45);
						j.balanceAmount = c.getString(46);
						 
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
			return arJourneyPlan;
		}
	}
	
	public int getTotalStock(String strUserId,String DateOfJourney)
	{
		synchronized(MyApplication.MyLock) 
		{
			int stock=0;
			DictionaryEntry[][] data=null;
			String query = "SELECT sum(Cases) FROM tblJourneyPlan where PresellerId='"+strUserId+"' AND (DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan(DateOfJourney)+"%' or DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan2(DateOfJourney)+"%') and CustomerSiteId in (select CustomerSiteId from tblInvoiceOrders ) ORDER BY Stop";
			try
			{
				data = DatabaseHelper.get(query);
				if(data !=null && data.length>0)
					stock =	(int)StringUtils.getFloat(data[0][0].value.toString());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closedatabase();
			}
			return stock;
		}
	}
	
	public void updateLastJourneyLog()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor nCursor = null, cursor = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT CustomerSiteId FROM tblJourneyLog WHERE OutTime =''";
				 nCursor = objSqliteDB.rawQuery(suery, null);
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set IsServed=? ,OutTime = ?,TotalTimeAtOutLet =? where CustomerSiteId =? and DateOfJourney like ? ");
				 
				 if(nCursor.moveToFirst())
				 {
					 do
					 {
						 cursor	 =	objSqliteDB.rawQuery("select ArrivalTime from tblJourneyLog where CustomerSiteId ='"+nCursor.getString(0)+"'", null);
						 if(cursor.moveToFirst())
						 {
							 String outDate = CalendarUtils.getCurrentDateAsString()+"T"+CalendarUtils.getRetrunTime()+":00";
							 stmtUpdate.bindString(1, "true");
							 stmtUpdate.bindString(2, outDate);
							 long minutes = CalendarUtils.getDateDifferenceInMinutes(cursor.getString(0), outDate);
							 stmtUpdate.bindString(3, ""+minutes);
							 
							 stmtUpdate.bindString(4, nCursor.getString(0));
							 stmtUpdate.bindString(5, "%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%");
							 stmtUpdate.execute();
						 }
					 }
					 while(nCursor.moveToNext());
				 }
				 
				 if(nCursor!=null && !nCursor.isClosed())
					 nCursor.close();
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 
				 if(cursor!=null && !cursor.isClosed())
					 cursor.close();
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
	public void insertIntoJourneyLog(JourneyPlanDO objMallsDetails)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase database =null;
			 try
			 {
				 DictionaryEntry[][] data=null;
				 data = DatabaseHelper.get("SELECT * FROM tblJourneyLog where CustomerSiteId='"+objMallsDetails.site+"' and DateOfJourney like '%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%'");
				 if(data ==null || data.length==0)
				 {
					 database = DatabaseHelper.openDataBase();
					 if(database!=null)
					 {
						 SQLiteStatement stmt = database.compileStatement("INSERT INTO tblJourneyLog (DateOfJourney, PresellerId, CustomerSiteId,CustomerPasscode,Stop,Distance,TravelTime,ArrivalTime,SeviceTime,ReasonForSkip,isServed,isPosted,OutTime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
						 stmt.bindString(1,  ""+objMallsDetails.dateOfJourny);
						 stmt.bindString(2,  ""+objMallsDetails.userID);
						 stmt.bindString(3,  ""+objMallsDetails.site);
						 stmt.bindString(4,  ""+objMallsDetails.Passcode);
						 stmt.bindString(5,  ""+objMallsDetails.stop);
						 stmt.bindString(6,  ""+objMallsDetails.Distance);
						 stmt.bindString(7,  ""+objMallsDetails.TravelTime);
						 stmt.bindString(8 , ""+objMallsDetails.ActualArrivalTime);
						 stmt.bindString(9,  ""+objMallsDetails.SeviceTime);
						 stmt.bindString(10, ""+objMallsDetails.reasonForSkip);
						 stmt.bindString(11, ""+objMallsDetails.isServed);
						 stmt.bindString(12, "N");
						 stmt.bindString(13, "");
						 stmt.executeInsert();
						 stmt.close();
					 }
				 }
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(database != null)
					 database.close();
			 }
		}
		 
	}
	
	public boolean insertCustomerGroup(Vector<Customer_GroupDO> vectCustomerGroupDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result= true;
			SQLiteDatabase objSqliteDB =null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerGroup WHERE CUSTOMER_SITE_ID =?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerGroup (COMPANYID, CUSTPRICECLASS, CUSTOMER_SITE_ID) VALUES(?,?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomerGroup SET " +
						"COMPANYID =? , CUSTPRICECLASS =? WHERE CUSTOMER_SITE_ID =?");
				for(int i=0;i<vectCustomerGroupDOs.size();i++)
				{
					Customer_GroupDO objCustomerGroupDO = vectCustomerGroupDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerGroupDO.SiteNumber);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerGroupDO != null )
						{
							stmtUpdate.bindString(1, objCustomerGroupDO.CompanyId);
							stmtUpdate.bindString(2, objCustomerGroupDO.CustpriceClass);
							stmtUpdate.bindString(3, objCustomerGroupDO.SiteNumber);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerGroupDO != null )
						{
							stmtInsert.bindString(1, objCustomerGroupDO.CompanyId);
							stmtInsert.bindString(2, objCustomerGroupDO.CustpriceClass);
							stmtInsert.bindString(3, objCustomerGroupDO.SiteNumber);
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
				result =  false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public boolean insertCustomer(Vector<CustomerDO> vecCustomerDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomers WHERE CustomerId = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomers(CustomerId, Name) VALUES(?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomers SET " +
						"CustomerId =? , Name =? WHERE CustomerId = ?");
				for(int i= 0 ;i<vecCustomerDOs.size();i++)
				{
					CustomerDO objCustomerDO =  vecCustomerDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerDO.Customer_Id);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerDO != null )
						{
							stmtUpdate.bindString(1, objCustomerDO.Customer_Id);
							stmtUpdate.bindString(2, objCustomerDO.customerName);
							stmtUpdate.bindString(3, objCustomerDO.Customer_Id);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerDO != null )
						{
							stmtInsert.bindString(1, objCustomerDO.Customer_Id);
							stmtInsert.bindString(2, objCustomerDO.customerName);
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
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	
	public boolean insertCustomerInforWithSync(Vector<JourneyPlanDO> vecCustomerDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomer WHERE Site = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomer(" +
						"Site,SiteName,CustomerId,CustomerStatus,CustAcctCreationDate,PartyName," +
						"ChannelCode,SubChannelCode,RegionCode,CountryCode,Category,Address1," +
						"Address2,Address3,	Address4,PoNumber,City,	PaymentType,PaymentTermCode,CreditLimit," +
						" GeoCodeX,	GeoCodeY, PASSCODE,	Email, ContactPersonName, PhoneNumber," +
						" AppCustomerId, MobileNumber1,MobileNumber2, Website, PaymentMode, CreatedBy," +
						" ModifiedBy, Source, CustomerCategory,	CustomerSubCategory, CustomerGroupCode," +
						" ModifiedDate,	ModifiedTime, CurrencyCode, StoreGrowth, PriceList, id,SalesPerson,BlockedStatus,"////		"GeoCodeFlag" BOOL, "FInvDiscYH" VARCHAR, "FStatDiscYH" VARCHAR, "TInvDiscYH" VARCHAR, "TStatDiscYH" VARCHAR)
//						+ "Attribute1,Attribute2,Attribute3,Attribute4,Attribute5,Attribute6,Attribute7,Attribute8,Attribute9,Attribute10,Attribute11,Attribute12,Attribute13) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						+ "Attribute1,Attribute2,Attribute3,Attribute4,Attribute5,Attribute6,Attribute7,Attribute8,Attribute9,Attribute10,Attribute11,Attribute12,Attribute13,GeoCodeFlag,FInvDiscYH,FStatDiscYH,TInvDiscYH,TStatDiscYH,VATNo,IsVATApplicable) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomer SET " +
						"CustomerId = ?,SiteName = ?,CustomerStatus = ?,CustAcctCreationDate = ?,PartyName = ?," +
						"ChannelCode = ?,SubChannelCode = ?,RegionCode = ?,CountryCode = ?,Category = ?,Address1 = ?," +
						"Address2 = ?,Address3 = ?,	Address4 = ?,PoNumber = ?,City = ?,	PaymentType = ?,PaymentTermCode = ?,CreditLimit = ?," +
						" GeoCodeX = ?,	GeoCodeY = ?,	PASSCODE = ?,	Email = ?,	ContactPersonName = ?,	PhoneNumber = ?," +
						" AppCustomerId = ?,	MobileNumber1 = ?,MobileNumber2 = ?,	Website = ?,	PaymentMode = ?,	CreatedBy = ?," +
						" ModifiedBy = ?,	Source = ?,	CustomerCategory = ?,	CustomerSubCategory = ?,	CustomerGroupCode = ?," +
						" ModifiedDate = ?,	ModifiedTime = ?, CurrencyCode = ?, StoreGrowth = ?,PriceList=?, SalesPerson=?, BlockedStatus=?,Attribute1=?,Attribute2=?,Attribute3=?,Attribute4=?,Attribute5=?,Attribute6=?,Attribute7=?,Attribute8=?,Attribute9=?,Attribute10=?,Attribute11=?,Attribute12=?,Attribute13=? " +
						",GeoCodeFlag=?,FInvDiscYH=?,FStatDiscYH=?,TInvDiscYH=?,TStatDiscYH=?,VATNo=?,IsVATApplicable=?"+
						" WHERE Site = ?");
				
				for(int i= 0 ;i<vecCustomerDOs.size();i++)
				{
					JourneyPlanDO objCustomerDO =  vecCustomerDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerDO.site);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerDO != null )
						{
							if(objCustomerDO.customerId.equalsIgnoreCase("DXA341"))
								Log.d("APP",""+objCustomerDO.blockedStatus);
								
								
							stmtUpdate.bindString(1, ""+objCustomerDO.customerId);
							stmtUpdate.bindString(2, ""+objCustomerDO.siteName);
							stmtUpdate.bindString(3, ""+objCustomerDO.custmerStatus);
							stmtUpdate.bindString(4, ""+objCustomerDO.custAccCreationDate);
							stmtUpdate.bindString(5, ""+objCustomerDO.partyName);
							stmtUpdate.bindString(6, ""+objCustomerDO.channelCode);
							stmtUpdate.bindString(7, ""+objCustomerDO.subChannelCode);
							stmtUpdate.bindString(8, ""+objCustomerDO.regionCode);
							stmtUpdate.bindString(9, ""+objCustomerDO.coutryCode);
							stmtUpdate.bindString(10, ""+objCustomerDO.category);
							stmtUpdate.bindString(11, ""+objCustomerDO.addresss1);
							stmtUpdate.bindString(12, ""+objCustomerDO.addresss2);
							stmtUpdate.bindString(13, ""+objCustomerDO.addresss3);
							stmtUpdate.bindString(14, ""+objCustomerDO.addresss4);
							stmtUpdate.bindString(15, "");
							stmtUpdate.bindString(16, ""+objCustomerDO.city);
							stmtUpdate.bindString(17, ""+objCustomerDO.customerPaymentType);
							stmtUpdate.bindString(18, ""+objCustomerDO.paymentTermCode);
							stmtUpdate.bindString(19, ""+objCustomerDO.creditLimit);
							stmtUpdate.bindString(20, ""+objCustomerDO.geoCodeX);
							stmtUpdate.bindString(21, ""+objCustomerDO.geoCodeY);
							stmtUpdate.bindString(22, ""+objCustomerDO.Passcode);
							stmtUpdate.bindString(23, ""+objCustomerDO.email);
							stmtUpdate.bindString(24, ""+objCustomerDO.contectPersonName);
							stmtUpdate.bindString(25, ""+objCustomerDO.phoneNumber);
							stmtUpdate.bindString(26, ""+objCustomerDO.appCustomerId);
							stmtUpdate.bindString(27, ""+objCustomerDO.mobileNo1);
							stmtUpdate.bindString(28, ""+objCustomerDO.mobileNo2);
							stmtUpdate.bindString(29, ""+objCustomerDO.website);
							stmtUpdate.bindString(30, ""+objCustomerDO.customerPaymentMode);
							stmtUpdate.bindString(31, ""+objCustomerDO.createdby);
							stmtUpdate.bindString(32, ""+objCustomerDO.modifiedBy);
							stmtUpdate.bindString(33, ""+objCustomerDO.source);
							stmtUpdate.bindString(34, ""+objCustomerDO.customerCategory);
							stmtUpdate.bindString(35, ""+objCustomerDO.customerSubCategory);
							stmtUpdate.bindString(36, ""+objCustomerDO.customerGroupCode);
							stmtUpdate.bindString(37, ""+objCustomerDO.modifiedDate);
							stmtUpdate.bindString(38, ""+objCustomerDO.modifiedTime);
							stmtUpdate.bindString(39, ""+objCustomerDO.currencyCode);
							stmtUpdate.bindString(40, ""+objCustomerDO.StoreGrowth);
							stmtUpdate.bindString(41, ""+objCustomerDO.priceList);
							stmtUpdate.bindString(42, ""+objCustomerDO.salesmanCode);
							stmtUpdate.bindString(43, ""+objCustomerDO.blockedStatus);
							stmtUpdate.bindString(44, ""+objCustomerDO.Attribute1);
							stmtUpdate.bindString(45, ""+objCustomerDO.Attribute2);
							stmtUpdate.bindString(46, ""+objCustomerDO.Attribute3);
							stmtUpdate.bindString(47, ""+objCustomerDO.Attribute4);
							stmtUpdate.bindString(48, ""+objCustomerDO.Attribute5);
							stmtUpdate.bindString(49, ""+objCustomerDO.Attribute6);
							stmtUpdate.bindString(50, ""+objCustomerDO.Attribute7);
							stmtUpdate.bindString(51, ""+objCustomerDO.Attribute8);
							stmtUpdate.bindString(52, ""+objCustomerDO.Attribute9);
							stmtUpdate.bindString(53, ""+objCustomerDO.Attribute10);
							stmtUpdate.bindString(54, ""+objCustomerDO.Attribute11);
							stmtUpdate.bindString(55, ""+objCustomerDO.Attribute12);
							stmtUpdate.bindString(56, ""+objCustomerDO.Attribute13);

							stmtUpdate.bindString(57, ""+objCustomerDO.GeoCodeFlag);
							stmtUpdate.bindString(58, ""+objCustomerDO.FInvDiscYH);
							stmtUpdate.bindString(59, ""+objCustomerDO.FStatDiscYH);
							stmtUpdate.bindString(60, ""+objCustomerDO.TInvDiscYH);
							stmtUpdate.bindString(61, ""+objCustomerDO.TStatDiscYH);
							stmtUpdate.bindString(62, ""+objCustomerDO.VATNo);
							stmtUpdate.bindString(63, ""+objCustomerDO.IsVATApplicableNew);

							stmtUpdate.bindString(64, ""+objCustomerDO.site);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerDO != null )
						{
							stmtInsert.bindString(1, ""+objCustomerDO.site);
							stmtInsert.bindString(2, ""+objCustomerDO.siteName);
							stmtInsert.bindString(3, ""+objCustomerDO.customerId);
							stmtInsert.bindString(4, ""+objCustomerDO.custmerStatus);
							stmtInsert.bindString(5, ""+objCustomerDO.custAccCreationDate);
							stmtInsert.bindString(6, ""+objCustomerDO.partyName);
							stmtInsert.bindString(7, ""+objCustomerDO.channelCode);
							stmtInsert.bindString(8, ""+objCustomerDO.subChannelCode);
							stmtInsert.bindString(9, ""+objCustomerDO.regionCode);
							stmtInsert.bindString(10, ""+objCustomerDO.coutryCode);
							stmtInsert.bindString(11, ""+objCustomerDO.category);
							stmtInsert.bindString(12, ""+objCustomerDO.addresss1);
							stmtInsert.bindString(13, ""+objCustomerDO.addresss2);
							stmtInsert.bindString(14, ""+objCustomerDO.addresss3);
							stmtInsert.bindString(15, ""+objCustomerDO.addresss4);
							stmtInsert.bindString(16, "");
							stmtInsert.bindString(17, ""+objCustomerDO.city);
							stmtInsert.bindString(18, ""+objCustomerDO.customerPaymentType);
							stmtInsert.bindString(19, ""+objCustomerDO.paymentTermCode);
							stmtInsert.bindString(20, ""+objCustomerDO.creditLimit);
							stmtInsert.bindString(21, ""+objCustomerDO.geoCodeX);
							stmtInsert.bindString(22, ""+objCustomerDO.geoCodeY);
							stmtInsert.bindString(23, ""+objCustomerDO.Passcode);
							stmtInsert.bindString(24, ""+objCustomerDO.email);
							stmtInsert.bindString(25, ""+objCustomerDO.contectPersonName);
							stmtInsert.bindString(26, ""+objCustomerDO.phoneNumber);
							stmtInsert.bindString(27, ""+objCustomerDO.appCustomerId);
							stmtInsert.bindString(28, ""+objCustomerDO.mobileNo1);
							stmtInsert.bindString(29, ""+objCustomerDO.mobileNo2);
							stmtInsert.bindString(30, ""+objCustomerDO.website);
							stmtInsert.bindString(31, ""+objCustomerDO.customerPaymentMode);
							stmtInsert.bindString(32, ""+objCustomerDO.createdby);
							stmtInsert.bindString(33, ""+objCustomerDO.modifiedBy);
							stmtInsert.bindString(34, ""+objCustomerDO.source);
							stmtInsert.bindString(35, ""+objCustomerDO.customerCategory);
							stmtInsert.bindString(36, ""+objCustomerDO.customerSubCategory);
							stmtInsert.bindString(37, ""+objCustomerDO.customerGroupCode);
							stmtInsert.bindString(38, ""+objCustomerDO.modifiedDate);
							stmtInsert.bindString(39, ""+objCustomerDO.modifiedTime);
							stmtInsert.bindString(40, ""+objCustomerDO.currencyCode);
							stmtInsert.bindString(41, ""+objCustomerDO.StoreGrowth);
							stmtInsert.bindString(42, ""+objCustomerDO.priceList);
							stmtInsert.bindString(43, ""+objCustomerDO.site);
							stmtInsert.bindString(44, ""+objCustomerDO.salesmanCode);
							stmtInsert.bindString(45, ""+objCustomerDO.blockedStatus);
							stmtInsert.bindString(46, ""+objCustomerDO.Attribute1);
							stmtInsert.bindString(47, ""+objCustomerDO.Attribute2);
							stmtInsert.bindString(48, ""+objCustomerDO.Attribute3);
							stmtInsert.bindString(49, ""+objCustomerDO.Attribute4);
							stmtInsert.bindString(50, ""+objCustomerDO.Attribute5);
							stmtInsert.bindString(51, ""+objCustomerDO.Attribute6);
							stmtInsert.bindString(52, ""+objCustomerDO.Attribute7);
							stmtInsert.bindString(53, ""+objCustomerDO.Attribute8);
							stmtInsert.bindString(54, ""+objCustomerDO.Attribute9);
							stmtInsert.bindString(55, ""+objCustomerDO.Attribute10);
							stmtInsert.bindString(56, ""+objCustomerDO.Attribute11);
							stmtInsert.bindString(57, ""+objCustomerDO.Attribute12);
							stmtInsert.bindString(58, ""+objCustomerDO.Attribute13);

							stmtInsert.bindString(59, ""+objCustomerDO.GeoCodeFlag);
							stmtInsert.bindString(60, ""+objCustomerDO.FInvDiscYH);
							stmtInsert.bindString(61, ""+objCustomerDO.FStatDiscYH);
							stmtInsert.bindString(62, ""+objCustomerDO.TInvDiscYH);
							stmtInsert.bindString(63, ""+objCustomerDO.TStatDiscYH);
							stmtInsert.bindString(64, ""+objCustomerDO.VATNo);
							stmtInsert.bindString(65, ""+objCustomerDO.IsVATApplicableNew);

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
					objSqliteDB.close();
			}
			return true;
		}
	}
	
	public boolean insertCustomerSite(Vector<CustomerSite_NewDO> vecCustomerSiteNewDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			boolean result= true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
					
			
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblCustomerSites WHERE CustomerSiteId = ?");
				
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblCustomerSites (CustomerSiteId," +
						" CustomerId, SiteName,ADDRESS1,ADDRESS2,CITY,GEO_CODE_X,GEO_CODE_Y,CREDIT_LIMIT,PresellerId," +
						"PAYMENT_TYPE,PAYMENT_TERM_CODE,PAYMENT_TERM_DESCRIPTION,TotalOutstandingBalance,SubChannelCode," +
						"CustomerStatus,MOBILENO1,MOBILENO2,Website,CustomerGrade,CustomerType,LandmarkId,SalesmanlandmarkId,Source,BlaseCustId," +
						"CountryId,DOB,AnniversaryDate,ParentGroup,CustomerPostingGroup,CustomerCategory,CustomerSubCategory," +
						"CustomerGroupCode, IsSchemeApplicable) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomerSites SET " +
						"CustomerId =?, SiteName =?,ADDRESS1 =?, ADDRESS2 =?,CITY =?,GEO_CODE_X =?, GEO_CODE_Y=?,CREDIT_LIMIT =?," +
						"PresellerId =?, PAYMENT_TYPE =?,PAYMENT_TERM_CODE =?,PAYMENT_TERM_DESCRIPTION =?,TotalOutstandingBalance=? ," +
						"SubChannelCode =?,CustomerStatus=? ,MOBILENO1=?,MOBILENO2=?,Website=?,CustomerGrade=?,CustomerType=?," +
						"LandmarkId=?,SalesmanlandmarkId=?,Source=?,BlaseCustId=?,CountryId=?,DOB=?,AnniversaryDate=?," +
						"ParentGroup=?,CustomerPostingGroup=?,CustomerCategory=?,CustomerSubCategory=?,CustomerGroupCode=?, IsSchemeApplicable = ? WHERE CustomerSiteId = ?");
				 
				for(int i=0;i<vecCustomerSiteNewDOs.size();i++)
				{
					CustomerSite_NewDO objCustomerSite_NewDO=vecCustomerSiteNewDOs.get(i);
					stmtSelectRec.bindString(1, objCustomerSite_NewDO.CustomerSiteId);
					
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(objCustomerSite_NewDO != null )
						{
							
							stmtUpdate.bindString(1, objCustomerSite_NewDO.CustomerId);
							stmtUpdate.bindString(2, objCustomerSite_NewDO.SiteName);
							stmtUpdate.bindString(3, objCustomerSite_NewDO.Address1);
							stmtUpdate.bindString(4, objCustomerSite_NewDO.Address2);
							stmtUpdate.bindString(5, objCustomerSite_NewDO.City);
							stmtUpdate.bindString(6, objCustomerSite_NewDO.Latitude);
							stmtUpdate.bindString(7, objCustomerSite_NewDO.Longitude);
							stmtUpdate.bindString(8, objCustomerSite_NewDO.CreditLimit);
							stmtUpdate.bindString(9, objCustomerSite_NewDO.Salesman);
							stmtUpdate.bindString(10, objCustomerSite_NewDO.PaymentType);
							stmtUpdate.bindString(11, objCustomerSite_NewDO.PaymentTermCode);
							stmtUpdate.bindString(12, objCustomerSite_NewDO.PaymentTermDescription);
							stmtUpdate.bindString(13, objCustomerSite_NewDO.TotalOutstandingBalance);
							stmtUpdate.bindString(14, objCustomerSite_NewDO.SubChannel);
							stmtUpdate.bindString(15, objCustomerSite_NewDO.CustomerStatus);
							stmtUpdate.bindString(16, objCustomerSite_NewDO.MOBILENO1);
							stmtUpdate.bindString(17, objCustomerSite_NewDO.MOBILENO2);
							stmtUpdate.bindString(18, objCustomerSite_NewDO.Website);
							stmtUpdate.bindString(19, objCustomerSite_NewDO.CustomerGrade);
							stmtUpdate.bindString(20, objCustomerSite_NewDO.CustomerType);
							stmtUpdate.bindString(21, objCustomerSite_NewDO.LandmarkId);
							stmtUpdate.bindString(22, objCustomerSite_NewDO.SalesmanlandmarkId);
							stmtUpdate.bindString(23, objCustomerSite_NewDO.Source);
							stmtUpdate.bindString(24, objCustomerSite_NewDO.BlaseCustId);
							stmtUpdate.bindString(25, objCustomerSite_NewDO.CountryId);
							stmtUpdate.bindString(26, objCustomerSite_NewDO.DOB);
							stmtUpdate.bindString(27, objCustomerSite_NewDO.AnniversaryDate);
							
							stmtUpdate.bindString(28, objCustomerSite_NewDO.ParentGroup);
							stmtUpdate.bindString(29, objCustomerSite_NewDO.CustomerPostingGroup);
							stmtUpdate.bindString(30, objCustomerSite_NewDO.CustomerCategory);
							stmtUpdate.bindString(31, objCustomerSite_NewDO.CustomerSubCategory);
							stmtUpdate.bindString(32, objCustomerSite_NewDO.CustomerGroupCode);
							stmtUpdate.bindString(33, objCustomerSite_NewDO.isSchemeApplicable);
							
							stmtUpdate.bindString(34, objCustomerSite_NewDO.CustomerSiteId);
							
							stmtUpdate.execute();
						}
					}
					else
					{
						if(objCustomerSite_NewDO != null )
						{
							stmtInsert.bindString(1, objCustomerSite_NewDO.CustomerSiteId);
							stmtInsert.bindString(2, objCustomerSite_NewDO.CustomerId);
							stmtInsert.bindString(3, objCustomerSite_NewDO.SiteName);
							stmtInsert.bindString(4, objCustomerSite_NewDO.Address1);
							stmtInsert.bindString(5, objCustomerSite_NewDO.Address2);
							stmtInsert.bindString(6, objCustomerSite_NewDO.City);
							stmtInsert.bindString(7, objCustomerSite_NewDO.Latitude);
							stmtInsert.bindString(8, objCustomerSite_NewDO.Longitude);
							stmtInsert.bindString(9, objCustomerSite_NewDO.CreditLimit);
							stmtInsert.bindString(10, objCustomerSite_NewDO.Salesman);
							stmtInsert.bindString(11, objCustomerSite_NewDO.PaymentType);
							stmtInsert.bindString(12, objCustomerSite_NewDO.PaymentTermCode);
							stmtInsert.bindString(13, objCustomerSite_NewDO.PaymentTermDescription);
							stmtInsert.bindString(14, objCustomerSite_NewDO.TotalOutstandingBalance !=null ? objCustomerSite_NewDO.TotalOutstandingBalance : "0");
							stmtInsert.bindString(15, objCustomerSite_NewDO.SubChannel);
							stmtInsert.bindString(16, objCustomerSite_NewDO.CustomerStatus);
							stmtInsert.bindString(17, objCustomerSite_NewDO.MOBILENO1);
							stmtInsert.bindString(18, objCustomerSite_NewDO.MOBILENO2);
							stmtInsert.bindString(19, objCustomerSite_NewDO.Website);
							stmtInsert.bindString(20, objCustomerSite_NewDO.CustomerGrade);
							stmtInsert.bindString(21, objCustomerSite_NewDO.CustomerType);
							stmtInsert.bindString(22, objCustomerSite_NewDO.LandmarkId);
							stmtInsert.bindString(23, objCustomerSite_NewDO.SalesmanlandmarkId);
							stmtInsert.bindString(24, objCustomerSite_NewDO.Source);
							stmtInsert.bindString(25, objCustomerSite_NewDO.BlaseCustId);
							stmtInsert.bindString(26, objCustomerSite_NewDO.CountryId );
							stmtInsert.bindString(27, objCustomerSite_NewDO.DOB);
							stmtInsert.bindString(28, objCustomerSite_NewDO.AnniversaryDate);
							
							
							stmtInsert.bindString(29, objCustomerSite_NewDO.ParentGroup);
							stmtInsert.bindString(30, objCustomerSite_NewDO.CustomerPostingGroup);
							stmtInsert.bindString(31, objCustomerSite_NewDO.CustomerCategory);
							stmtInsert.bindString(32, objCustomerSite_NewDO.CustomerSubCategory);
							stmtInsert.bindString(33, objCustomerSite_NewDO.CustomerGroupCode);
							stmtInsert.bindString(34, objCustomerSite_NewDO.isSchemeApplicable);
							
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
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
				{
					objSqliteDB.close();
				}
			}
			return result;
		}
	}
	
	public boolean updateCustomerSiteGEOLocation(JourneyPlanDO mallsDetails)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB =null;
			boolean result= true;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblCustomer SET " +
						"GeoCodeX =?, GeoCodeY=? WHERE Site = ?");
				 
				if(mallsDetails!=null)
				{
					stmtUpdate.bindString(1, ""+mallsDetails.geoCodeX);
					stmtUpdate.bindString(2, ""+mallsDetails.geoCodeY);
					stmtUpdate.bindString(3, mallsDetails.site);
					stmtUpdate.execute();
				}
				stmtUpdate.close();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				result = false;
			}
			finally
			{
				if(objSqliteDB!=null)
					objSqliteDB.close();
			}
			return result;
		}
	}
	
	public int getTotalCustomerListCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			int totalCustomerCount = 0;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query		=	"SELECT COUNT(*) FROM tblCustomer C  INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson WHERE IsDeleted !='true' Order by Sequence";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					totalCustomerCount = cursor.getInt(0);
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
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return totalCustomerCount;
		}
	}
	
	public int getVisitedCustomerListCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			int visitedCustomerCount = 0;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
				String query		=	"SELECT COUNT(*) FROM tblCustomerVisit WHERE (Date LIKE '%"+CalendarUtils.getDateAsString(new Date())+"%') GROUP BY ClientCode";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					visitedCustomerCount = cursor.getInt(0);
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
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return visitedCustomerCount;
		}
	}
	
	/**
	 * This method get the list of Served Customer List,
	 * Which can then be used to show the additional 
	 * flyers like 'Perfect Store' and 'Visited'
	 * @param salesmanCode
	 * @param day
	 * @param preference
	 * @return
	 */
	public HashMap<String, Integer> getServedCustomerList(String salesmanCode, String day, Preference preference)
	{
		synchronized(MyApplication.MyLock) 
		{
			HashMap<String, Integer> hmVisits		= 	new HashMap<String, Integer>();
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase 		= 	DatabaseHelper.openDataBase();
//				String query		=	"SELECT DISTINCT ClientCode, TypeOfCall FROM tblCustomerVisit WHERE (Date LIKE '%"+CalendarUtils.getDateAsString(new Date())+"%') GROUP BY ClientCode";
				String query 		=	"SELECT DISTINCT ClientCode, TypeOfCall " +
										"FROM tblCustomerVisit " +
										"INNER JOIN tblServiceCapture SC ON ClientCode = CustomerCode " +
										"WHERE (Date LIKE '%"+CalendarUtils.getDateAsString(new Date())+"%') AND (CreatedDate LIKE '%"+CalendarUtils.getDateAsString(new Date())+"%') " +
										"GROUP BY ClientCode";
				cursor 				= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						hmVisits.put(cursor.getString(0), cursor.getInt(1));
					}
					while(cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				/*if(preference.getStringFromPreference(Preference.SALESMAN_TYPE, "").equalsIgnoreCase(AppConstants.SALESMAN_GT))
				{
					query				=	"SELECT distinct SiteNo from tblOrderHeader where OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%'";
					cursor 				= 	sqLiteDatabase .rawQuery(query, null);
					if(cursor.moveToFirst())
					{
						do
						{
							if(!vectCustomer.contains(cursor.getString(0)))
								vectCustomer.add(cursor.getString(0));
						}
						while(cursor.moveToNext());
					}
					if(cursor!=null && !cursor.isClosed())
						cursor.close();
				}*/
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return hmVisits;
		}
	}
	
	
	
	public ArrayList<String> getOrderTobePost(String empNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<String>vectCustomer 		= 	new ArrayList<String>();
			SQLiteDatabase sqLiteDatabase 	= 	null;
			Cursor cursor = null;
			try 
			{
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query					=	"SELECT distinct SiteNo from tblOrderHeader where  EmpNo ='"+empNo+"'" +
													" And OrderDate like '%"+CalendarUtils.getOrderPostDate()+"%' AND status<=0";
				cursor 						    = 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						if(!vectCustomer.contains(cursor.getString(0)))
							vectCustomer.add(cursor.getString(0));
					}
					while(cursor.moveToNext());
				}
				if(cursor!=null && !cursor.isClosed())
					cursor.close();
				
				query					=	"SELECT SiteId FROM tblPaymentHeader where status='0' and PaymentDate like '"+CalendarUtils.getOrderPostDate()+"%'";
				cursor 					= 	sqLiteDatabase .rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						if(!vectCustomer.contains(cursor.getString(0)))
							vectCustomer.add(cursor.getString(0));
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
				if(sqLiteDatabase!=null)
					sqLiteDatabase.close();
			}
			return vectCustomer;
		}
	}
	
	public boolean insertJourneyPlan(Vector<UserJourneyPlanDO> vecJourneyPlanDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			boolean result = true;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
			
				SQLiteStatement stmtSelectRec 	= objSqliteDB.compileStatement("SELECT COUNT(*) from tblJourneyPlan WHERE PresellerId = ? and DateOfJourney = ? and CustomerSiteId = ?");
				SQLiteStatement stmtInsert 		= objSqliteDB.compileStatement("INSERT INTO tblJourneyPlan(DateOfJourney,PresellerId,CustomerSiteId,CustomerPasscode,Stop,Distance,TravelTime,ArrivalTime,ServiceTime,Cases,KG) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				
				SQLiteStatement stmtUpdate 		= objSqliteDB.compileStatement("UPDATE tblJourneyPlan SET CustomerPasscode = ?,Stop = ?,Distance = ?,TravelTime = ?,ArrivalTime = ?,ServiceTime = ?,Cases = ?,KG = ? WHERE CustomerSiteId = ?  and DateOfJourney = ? and PresellerId = ?");
				 
				for(int i=0;i<vecJourneyPlanDOs.size();i++)
				{
					UserJourneyPlanDO userJourneyPlan = vecJourneyPlanDOs.get(i);
					stmtSelectRec.bindString(1, userJourneyPlan.strSalesmancode);
					stmtSelectRec.bindString(2, userJourneyPlan.strRoutePlanDetails);
					stmtSelectRec.bindString(3, userJourneyPlan.strSiteNumber);
					long countRec = stmtSelectRec.simpleQueryForLong();
					if(countRec != 0)
					{	
						if(userJourneyPlan != null )
						{
							stmtUpdate.bindString(1, userJourneyPlan.strPassCode);
							stmtUpdate.bindString(2, userJourneyPlan.strStop);
							stmtUpdate.bindString(3, userJourneyPlan.strDistance);
							stmtUpdate.bindString(4, userJourneyPlan.strTravelTime);
							stmtUpdate.bindString(5, userJourneyPlan.strArrivalTime);
							stmtUpdate.bindString(6, userJourneyPlan.strServiceTime);
							stmtUpdate.bindString(7, userJourneyPlan.strCases);
							stmtUpdate.bindString(8, userJourneyPlan.strKG);
							stmtUpdate.bindString(9, userJourneyPlan.strSiteNumber);
							stmtUpdate.bindString(10, userJourneyPlan.strRoutePlanDetails);
							stmtUpdate.bindString(11, userJourneyPlan.strSalesmancode);
							stmtUpdate.execute();
						}
					}
					else
					{
						if(userJourneyPlan != null )
						{
							stmtInsert.bindString(1, userJourneyPlan.strRoutePlanDetails);
							stmtInsert.bindString(2, userJourneyPlan.strSalesmancode);
							stmtInsert.bindString(3, userJourneyPlan.strSiteNumber);
							stmtInsert.bindString(4, userJourneyPlan.strPassCode);
							stmtInsert.bindString(5, userJourneyPlan.strStop);
							stmtInsert.bindString(6, userJourneyPlan.strDistance);
							stmtInsert.bindString(7, userJourneyPlan.strTravelTime);
							stmtInsert.bindString(8, userJourneyPlan.strArrivalTime);
							stmtInsert.bindString(9, userJourneyPlan.strServiceTime);
							stmtInsert.bindString(10, userJourneyPlan.strCases);
							stmtInsert.bindString(11, userJourneyPlan.strKG);
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
	
	public ArrayList<JourneyPlanDO> getJourneyPlan(long todayTimeStamp, String date, String day, String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT DISTINCT RC.Sequence as Sequence,'0' as lifeCycle, C.*, RC.EndTime as TimeOut,RC.StartTime as TimeIn,C.Site as ClientCode,UT.UserName FROM tblCustomer C " +
		                 		"INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson WHERE IsDeleted !='true' AND RC.JourneyDate LIKE '%"+date+"%' Order by Sequence";
				c = objSqliteDB.rawQuery(query, null);
				ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.stop =c.getInt(0);
						j.lifeCycle =c.getString(1);
						j.id =c.getString(2);
						j.site =c.getString(3);//coustomer code
						j.siteName =c.getString(4);
						j.customerId =c.getString(5);
						j.custmerStatus =c.getString(6);
						j.custAccCreationDate =c.getString(7);
						j.partyName =c.getString(8);
						j.channelCode =c.getString(9);//channel code
						j.subChannelCode =c.getString(10);
						j.regionCode =c.getString(11);
						j.coutryCode =c.getString(12);
						j.category = c.getString(13);
						j.addresss1 =c.getString(14);
						j.addresss2 =c.getString(15);
						j.addresss3 =c.getString(16);
						j.addresss4 =c.getString(17);
						
						j.poNumber =c.getString(18);
						j.city =c.getString(19);
						j.customerPaymentType =c.getString(20);
						j.paymentTermCode =c.getString(21);
						j.creditLimit =c.getString(22);
						j.geoCodeX =c.getDouble(23);
						j.geoCodeY =c.getDouble(24);
						j.Passcode =c.getString(25);
						j.email=c.getString(26);
						j.contectPersonName=c.getString(27);
						j.phoneNumber =c.getString(28);
						j.appCustomerId =c.getString(29);
						j.mobileNo1 =c.getString(30);
						j.mobileNo2 =c.getString(31);
						j.website =c.getString(32);
//						j.customerType = c.getString(33);
						j.createdby = c.getString(34);
						j.modifiedBy = c.getString(35);
						j.source = c.getString(36);
						j.customerCategory = c.getString(37);
						j.customerSubCategory = c.getString(38);
						j.customerGroupCode = c.getString(39);//customer group
						j.modifiedDate = c.getString(40);
						j.modifiedTime = c.getString(41);
						j.currencyCode = c.getString(42);
						j.StoreGrowth = c.getString(43);
						j.priceList = c.getString(44);
						j.salesmanCode = c.getString(45);
						j.customerPaymentMode =c.getString(46);
						
						
						j.Attribute1 =c.getString(50)+"";
						j.Attribute2 =c.getString(51)+"";
						j.Attribute3 =c.getString(52)+"";
						j.Attribute4 =c.getString(53)+"";
						j.Attribute5 =c.getString(54)+"";
						j.Attribute6 =c.getString(55)+"";
						j.Attribute7 =c.getString(56)+"";
						j.Attribute8 =c.getString(57)+"";
						j.Attribute9 =c.getString(58)+"";
						j.Attribute10 =c.getString(59)+"";
						
						if(c.getString(62) != null)
							j.blockedStatus =c.getString(62);
						else
							j.blockedStatus ="";
						
						j.timeOut = c.getString(67);
						j.timeIn = c.getString(68);
						j.clientCode = c.getString(69);
						j.salesmanName = c.getString(70);
						
						j.userID = presellerId;
						j.dateOfJourny = day;
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
				return arJourneyPlan;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		return null;
	}
	
	/**
	 * get Journeyplan with discount
	 * @param todayTimeStamp
	 * @param date
	 * @param day
	 * @param presellerId
	 * @return
	 */
	public ArrayList<JourneyPlanDO> getJourneyPlanWithDiscount(long todayTimeStamp, String date, String day, String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "";
				
				if(AppConstants.isOldPromotion)
				{
					query = "SELECT DISTINCT RC.Sequence as Sequence,'0' as lifeCycle, C.*, RC.EndTime as TimeOut," +
							"RC.StartTime as TimeIn,C.Site as ClientCode,UT.UserName,PBD.PromotionId, " +
							"CASE WHEN SC.Discount > 0 THEN SC.Discount " +
							"ELSE PBD.Discount END AS Discount " +
							"FROM tblCustomer C " +
							"INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode " +
							"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"LEFT OUTER JOIN tblPromotionAssignment PA ON PA.Code=C.Site " +
							"LEFT OUTER JOIN tblPromotionSC SC ON SC.PromotionId =PA.PromotionId " +
							"LEFT OUTER JOIN tblPromotionBrandDiscount PBD ON PBD.PromotionId = PA.PromotionId " +
							"WHERE IsDeleted !='true' AND RC.JourneyDate LIKE '%"+date+"%' AND C.BlockedStatus='False' COLLATE NOCASE " +
							"Order by Sequence";
				}
				else
				{
					
					query = "SELECT DISTINCT RC.Sequence as Sequence,'0' as lifeCycle, C.*, RC.EndTime as TimeOut," +
							"RC.StartTime as TimeIn,C.Site as ClientCode,UT.UserName " +
							"FROM tblCustomer C " +
							"INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode " +
							"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
							"WHERE IsDeleted !='true' AND RC.JourneyDate LIKE '%"+date+"%' AND C.BlockedStatus='False' COLLATE NOCASE " +
							"Order by Sequence";
					
//					query = "SELECT C.*,UT.UserName " +
//							"FROM tblCustomer C " +
//							"LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson " +
//							"WHERE C.BlockedStatus='False' COLLATE NOCASE " +
//							"ORDER BY C.SiteName ";
				}
				
				c = objSqliteDB.rawQuery(query, null);
				ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.stop =c.getInt(0);
						j.lifeCycle =c.getString(1);
						j.id =c.getString(2);
						j.site =c.getString(3);//coustomer code
						j.siteName =c.getString(4);
						j.customerId =c.getString(5);
						j.custmerStatus =c.getString(6);
						j.custAccCreationDate =c.getString(7);
						j.partyName =c.getString(8);
						j.channelCode =c.getString(9);//channel code
						j.subChannelCode =c.getString(10);
						j.regionCode =c.getString(11);
						j.coutryCode =c.getString(12);
						j.category = c.getString(13);
						j.addresss1 =c.getString(14);
						j.addresss2 =c.getString(15);
						j.addresss3 =c.getString(16);
						j.addresss4 =c.getString(17);
						
						j.poNumber =c.getString(18);
						j.city =c.getString(19);
						j.customerPaymentType =c.getString(20);
						j.paymentTermCode =c.getString(21);
						j.creditLimit =c.getString(22);
						j.geoCodeX =c.getDouble(23);
						j.geoCodeY =c.getDouble(24);
						j.Passcode =c.getString(25);
						j.email=c.getString(26);
						j.contectPersonName=c.getString(27);
						j.phoneNumber =c.getString(28);
						j.appCustomerId =c.getString(29);
						j.mobileNo1 =c.getString(30);
						j.mobileNo2 =c.getString(31);
						j.website =c.getString(32);
//						j.customerType = c.getString(33);
						j.createdby = c.getString(34);
						j.modifiedBy = c.getString(35);
						j.source = c.getString(36);
						j.customerCategory = c.getString(37);
						j.customerSubCategory = c.getString(38);
						j.customerGroupCode = c.getString(39);//customer group
						j.modifiedDate = c.getString(40);
						j.modifiedTime = c.getString(41);
						j.currencyCode = c.getString(42);
						j.StoreGrowth = c.getString(43);
						j.priceList = c.getString(44);
						j.salesmanCode = c.getString(45);
						j.customerPaymentMode =c.getString(46);
						
						
						j.Attribute1 =c.getString(50)+"";
						j.Attribute2 =c.getString(51)+"";
						j.Attribute3 =c.getString(52)+"";
						j.Attribute4 =c.getString(53)+"";
						j.Attribute5 =c.getString(54)+"";
						j.Attribute6 =c.getString(55)+"";
						j.Attribute7 =c.getString(56)+"";
						j.Attribute8 =c.getString(57)+"";
						j.Attribute9 =c.getString(58)+"";
						j.Attribute10 =c.getString(59)+"";
						
						if(c.getString(62) != null)
							j.blockedStatus =c.getString(62);
						else
							j.blockedStatus ="";
						
						j.PromotionalDiscount 	= c.getString(67);
						j.statementdiscount=c.getString(69);
						j.GeoCodeFlag=c.getString(70); // decide whether to show forcecheck in popup

						//newly added for food and tpt discounts
//						"GeoCodeFlag" BOOL, "FInvDiscYH" VARCHAR, "FStatDiscYH" VARCHAR, "TInvDiscYH" VARCHAR, "TStatDiscYH"
						j.FInvDiscYH = c.getString(71);
						j.FStatDiscYH = c.getString(72);
						j.TInvDiscYH = c.getString(73);
						j.TStatDiscYH = c.getString(74);
						//-------------------------------
						j.VATNo = c.getString(75);
						j.IsVATApplicable = c.getString(76).equalsIgnoreCase("True")?true:false;
						j.timeOut = c.getString(77);
						j.timeIn = c.getString(78);
						j.clientCode = c.getString(79);
						j.salesmanName = c.getString(80);
						j.userID = presellerId;
						j.dateOfJourny = day;

						CustomerCreditLimitDo obj = new CustomerDA().getCustomerCreditLimit(j.site);
						j.balanceAmount = obj.availbleLimit;
						if(!j.blockedStatus.equalsIgnoreCase("True"))
							arJourneyPlan.add(j);

//						j.timeOut = c.getString(70);
//						j.timeIn = c.getString(71);
//						j.clientCode = c.getString(72);
//						j.salesmanName = c.getString(73);
////						j.PromotionID 			= c.getString(73);
////						j.PromotionalDiscount 	= c.getString(74);
//						j.userID = presellerId;
//						j.dateOfJourny = day;
//						if(!j.blockedStatus.equalsIgnoreCase("True"))
//							arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
				return arJourneyPlan;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		return null;
	}
	
	public ArrayList<JourneyPlanDO> getNextDayJourneyPlan(long todayTimeStamp, String date, String day, String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT DISTINCT RC.Sequence as Sequence,'0' as lifeCycle, C.*, RC.EndTime as TimeOut,RC.StartTime as TimeIn,C.Site as ClientCode,UT.UserName FROM tblCustomer C " +
		                 		"INNER JOIN  tblDailyJourneyPlan RC ON C.Site = RC.ClientCode LEFT JOIN tblUsers UT ON UT.UserCode = C.SalesPerson WHERE IsDeleted !='true' AND RC.JourneyDate LIKE '%"+date+"%' Order by Sequence";
				c = objSqliteDB.rawQuery(query, null);
				ArrayList<JourneyPlanDO> arJourneyPlan = new ArrayList<JourneyPlanDO>();
				if(c.moveToFirst())
				{
					do
					{
						JourneyPlanDO j = new JourneyPlanDO();
						j.stop =c.getInt(0);
						j.lifeCycle =c.getString(1);
						j.id =c.getString(2);
						j.site =c.getString(3);//coustomer code
						j.siteName =c.getString(4);
						j.customerId =c.getString(5);
						j.custmerStatus =c.getString(6);
						j.custAccCreationDate =c.getString(7);
						j.partyName =c.getString(8);
						j.channelCode =c.getString(9);//channel code
						j.subChannelCode =c.getString(10);
						j.regionCode =c.getString(11);
						j.coutryCode =c.getString(12);
						j.category = c.getString(13);
						j.addresss1 =c.getString(14);
						j.addresss2 =c.getString(15);
						j.addresss3 =c.getString(16);
						j.addresss4 =c.getString(17);
						
						j.poNumber =c.getString(18);
						j.city =c.getString(19);
						j.customerPaymentType =c.getString(20);
						j.paymentTermCode =c.getString(21);
						j.creditLimit =c.getString(22);
						j.geoCodeX =c.getDouble(23);
						j.geoCodeY =c.getDouble(24);
						j.Passcode =c.getString(25);
						j.email=c.getString(26);
						j.contectPersonName=c.getString(27);
						j.phoneNumber =c.getString(28);
						j.appCustomerId =c.getString(29);
						j.mobileNo1 =c.getString(30);
						j.mobileNo2 =c.getString(31);
						j.website =c.getString(32);
//						j.customerType = c.getString(33);
						j.createdby = c.getString(34);
						j.modifiedBy = c.getString(35);
						j.source = c.getString(36);
						j.customerCategory = c.getString(37);
						j.customerSubCategory = c.getString(38);
						j.customerGroupCode = c.getString(39);//customer group
						j.modifiedDate = c.getString(40);
						j.modifiedTime = c.getString(41);
						j.currencyCode = c.getString(42);
						j.StoreGrowth = c.getString(43);
						j.priceList = c.getString(44);
						j.salesmanCode = c.getString(45);
						j.customerPaymentMode =c.getString(46);
						
						
						j.Attribute1 =c.getString(50)+"";
						j.Attribute2 =c.getString(51)+"";
						j.Attribute3 =c.getString(52)+"";
						j.Attribute4 =c.getString(53)+"";
						j.Attribute5 =c.getString(54)+"";
						j.Attribute6 =c.getString(55)+"";
						j.Attribute7 =c.getString(56)+"";
						j.Attribute8 =c.getString(57)+"";
						j.Attribute9 =c.getString(58)+"";
						j.Attribute10 =c.getString(59)+"";
						
						if(c.getString(62) != null)
							j.blockedStatus =c.getString(62);
						else
							j.blockedStatus ="";
						
						j.timeOut = c.getString(67);
						j.timeIn = c.getString(68);
						j.clientCode = c.getString(69);
						j.salesmanName = c.getString(70);
						
						j.userID = presellerId;
						j.dateOfJourny = day;
						arJourneyPlan.add(j);
					}
					while(c.moveToNext());
				}
				return arJourneyPlan;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(c != null && !c.isClosed())
					c.close();
				
				if(objSqliteDB != null)
					objSqliteDB.close();
			}
		}
		return null;
	}
	
	public int getJourneyPlanCount()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			Cursor c=null;
			int count = 0;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblDailyJourneyPlan WHERE IsDeleted !='true' " +
						"AND JourneyDate like '%"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				
				c = objSqliteDB.rawQuery(query, null);
				if(c.moveToFirst())
					count = c.getInt(0);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}finally{
				if(c!=null && !c.isClosed())
					c.close();
				if(objSqliteDB!=null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return count;
		}
	}
	
	public int getJourneyPlanCountForMonth()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase objSqliteDB = null;
			int count = 0;
			try 
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "SELECT COUNT(*) FROM tblDailyJourneyPlan WHERE IsDeleted !='true' " +
						"AND JourneyDate like '%"+CalendarUtils.getCurrentMonth()+"%'";
				
				Cursor c = objSqliteDB.rawQuery(query, null);
				
				if(c.moveToFirst())
				{
					count = c.getInt(0);
				}

				if(c != null && !c.isClosed())
					c.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return count;
		}
	}
	
	public int getServedCustomerCount(String strSelectedDate,String presellerId)
	{
		synchronized(MyApplication.MyLock) 
		{
			long countRec =0;
			SQLiteDatabase objSqliteDB = null;
			try 
			{
				objSqliteDB 					= 	DatabaseHelper.openDataBase();
				SQLiteStatement stmtSelectRec 	= 	objSqliteDB.compileStatement("SELECT count(*) FROM tblCustomerVisit where Date Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(strSelectedDate)+"%'");
				countRec 						= 	stmtSelectRec.simpleQueryForLong();
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
			return (int) countRec;
		}
	}
	
	//need to change
		public int getServedCustomerCount(int stopNumber ,String Day,String PresellerId, int Date, long DateInMS, String currentDate)
		{
			synchronized(MyApplication.MyLock) 
			{
				int stopNo=stopNumber;
				SQLiteDatabase mDatabase = null;
				String query = "SELECT count(DISTINCT ClientCode) FROM tblCustomerVisit where Date like '%"+CalendarUtils.getCurrentDateAsString()+"%'";
				Cursor cursor  = null,cursor2 = null;
				try
				{
					synchronized(MyApplication.MyLock) 
			    	{
						mDatabase = DatabaseHelper.openDataBase();
						cursor  = mDatabase.rawQuery(query, null);
						if(cursor.moveToFirst())
						{
							stopNo =stopNo-StringUtils.getInt(cursor.getString(0));
							
							if(stopNo>1)
							{
								String queryCustomers = "SELECT C.SiteName, C.Site as ClientCode FROM tblCustomer C INNER JOIN  tblDailyJourneyPlan RC " +
				                "ON C.Site = RC.ClientCode WHERE RC.JourneyDate like '%"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%' AND VisitStatus = 0 AND Sequence < "+stopNumber+" AND IsDeleted !='true' AND C.Site NOT IN(SELECT  CustomerSiteId FROM tblSkipReasons) Order by Sequence";
								
								cursor2  = mDatabase.rawQuery(queryCustomers, null);
								
								if(cursor2.moveToFirst())
								{
									int i=1;
									AppConstants.skippedCustomerSitIds = new ArrayList<String>();
									do
									{
										AppConstants.SKIPPED_CUSTOMERS =AppConstants.SKIPPED_CUSTOMERS+ i+") "+cursor2.getString(0)+"\n";
										AppConstants.skippedCustomerSitIds.add(cursor2.getString(1));
										i++;
									}while(cursor2.moveToNext());
									if(cursor2!=null && !cursor2.isClosed())
										cursor2.close();
								}
							}
							if(cursor!=null && !cursor.isClosed())
								cursor.close();	
						}
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
					if(cursor2!=null && !cursor2.isClosed())
						cursor2.close();
					if(mDatabase!=null)
						mDatabase.close();
				}
				return stopNo;
			}
		}

	public void updateCheckOutTimeByService(String empId,String date, String siteId, String checkOut)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase database =null;
			 try
			 {
				 database 					= DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = database.compileStatement("Update tblCheckinout set checkout=? WHERE empNo = ? AND  siteId =? AND date like '"+date+"%' AND checkout=?");
				 
				 stmtUpdate.bindString(1, checkOut);
				 stmtUpdate.bindString(2, empId);
				 stmtUpdate.bindString(3, siteId);
				 stmtUpdate.bindString(4, "");
				 stmtUpdate.execute();
				 stmtUpdate.close();
			 }
			 catch (Exception e) 
			 {
				e.printStackTrace();
			 }
			 finally
			 {
				 if(database != null)
					 database.close();
			 }
		}
	}
	public void updateJourneyLog(String strCustomerSiteId,long totaltime)
	{
		synchronized(MyApplication.MyLock) 
		{
			 LogUtils.errorLog("totaltime","totaltime" +totaltime);
			 SQLiteDatabase objSqliteDB = null;
			 Cursor cursor = null;
			 int timeInMinute = 0;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set IsServed=? ,OutTime = ?,TotalTimeAtOutLet =? where CustomerSiteId =? and DateOfJourney like ? ");
				 cursor						=	objSqliteDB.rawQuery("select TotalTimeAtOutLet from tblJourneyLog where CustomerSiteId ='"+strCustomerSiteId+"'", null);
				 stmtUpdate.bindString(1, "true");
				 stmtUpdate.bindString(2, CalendarUtils.getCurrentDateAsString()+"T"+CalendarUtils.getRetrunTime()+":00");
				 if(cursor.moveToFirst())
				 {
					 if(totaltime>0)
						 timeInMinute = (int)Math.ceil(StringUtils.getFloat(cursor.getString(0))+(float)((CalendarUtils.getCurrentTimeInMilli()-totaltime)/(float)(60000)));
					 else
						 timeInMinute = (int)Math.ceil(StringUtils.getFloat(cursor.getString(0)));
					 if(timeInMinute>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else if((int)Math.ceil(StringUtils.getFloat(cursor.getString(0)))>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else
						 stmtUpdate.bindString(3, "0");

				 }
				 else
				 {
					 timeInMinute = (int)Math.ceil(((CalendarUtils.getCurrentTimeInMilli()-totaltime)/(60*1000)));
					 if(timeInMinute>0)
						 stmtUpdate.bindString(3, ""+timeInMinute);
					 else
						 stmtUpdate.bindString(3, "0"); 

				 }
					 
				 stmtUpdate.bindString(4, strCustomerSiteId);
				 stmtUpdate.bindString(5, "%"+CalendarUtils.getCurrentDateForJourneyPlan(CalendarUtils.getCurrentDateAsString())+"%");
				 stmtUpdate.execute();
					 
				 stmtUpdate.close();
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
		}
	}
	
	
	public void updateJourneyLogStatus()
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate = 	objSqliteDB.compileStatement("Update tblJourneyLog set isPosted=?");
				 stmtUpdate.bindString(1, "Y");
				 stmtUpdate.execute();
				 
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
	
	public ArrayList<MallsDetails> getJournyLog(String presellerId,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			ArrayList<MallsDetails> arrayList = new ArrayList<MallsDetails>();
			SQLiteDatabase database = null;
			String strQuery  = "";
			strQuery 		= "SELECT CustomerSiteId,ArrivalTime,OutTime,TotalTimeAtOutLet FROM tblJourneyLog where DateOfJourney Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(date)+"%' And PresellerId ='"+presellerId+"' AND OutTime !='' AND isPosted ='N'";
			Cursor cursor 			= null;
			try 
			{
				database = DatabaseHelper.openDataBase();
				cursor = database.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						MallsDetails objDetails 	=  	new MallsDetails();
						objDetails.customerSiteId 	= 	cursor.getString(0);
						objDetails.ActualArrivalTime= 	cursor.getString(1);
						objDetails.ActualOutTime 	= 	cursor.getString(2);
						objDetails.TotalTime	 	= 	cursor.getString(3);
						arrayList.add(objDetails);
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
				
				if(database != null)
					database.close();
			}
			return arrayList;
		}
	}
	
	public Vector<UnUploadedDataDO> getJournyLogUnUpload(String presellerId,String date)
	{
		synchronized(MyApplication.MyLock) 
		{
			Vector<UnUploadedDataDO> uploadedDataDOs = new Vector<UnUploadedDataDO>();
			SQLiteDatabase database = null;
			String strQuery  = "";
			strQuery 		= "SELECT CustomerSiteId, isPosted FROM tblJourneyLog where DateOfJourney Like '%"+CalendarUtils.getCurrentDateForJourneyPlan(date)+"%' And PresellerId ='"+presellerId+"' AND OutTime !=''";
			Cursor cursor 			= null;
			try 
			{
				database = DatabaseHelper.openDataBase();
				cursor = database.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						UnUploadedDataDO unUploadedDataDO 	=  	new UnUploadedDataDO();
						unUploadedDataDO.strId 	= 	cursor.getString(0);
						if(cursor.getString(1).equalsIgnoreCase("N"))
							unUploadedDataDO.status = 0;
						else
							unUploadedDataDO.status = 1;
						
						uploadedDataDOs.add(unUploadedDataDO);
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
				
				if(database != null)
					database.close();
			}
			return uploadedDataDOs;
		}
	}
	
	public boolean insertCurrentInvoice(String strCustomerSIteID,String BalanceAmount,String InvoiceNumber, int division)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqLiteDatabase = null;
			SQLiteStatement sqlStatement = null;
			SQLiteStatement sqlSelectStatement = null;
			SQLiteStatement sqlUpdateStatement = null;
			
			boolean result = false;
			try
			{
				sqLiteDatabase 	=  DatabaseHelper.openDataBase();
				sqLiteDatabase.beginTransaction();
				
				String query 		= "INSERT INTO tblPendingInvoices(CustomerSiteId,DeliveryDate,BalanceAmount,InvoiceNumber,InvoiceDate,OrderId,TotalAmount,Division) VALUES(?,?,?,?,?,?,?,?)";
				String querySelect 	= "Select count(*) from tblPendingInvoices where InvoiceNumber =?";
				String queryUpdate 	= "Update tblPendingInvoices set BalanceAmount =?,TotalAmount=?  where InvoiceNumber =?";
				
				sqlSelectStatement = sqLiteDatabase.compileStatement(querySelect);
				sqlSelectStatement.bindString(1,InvoiceNumber);
				long countRecTruck = sqlSelectStatement.simpleQueryForLong();
			
				Log.e("performCashCustomerPayment","4");
				
				if(countRecTruck ==0)
				{
					Log.e("performCashCustomerPayment","5");
					sqlStatement 			=	sqLiteDatabase.compileStatement(query);
					sqlStatement.bindString(1, strCustomerSIteID);
					sqlStatement.bindString(2,	CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00");
					sqlStatement.bindString(3, BalanceAmount);
					sqlStatement.bindString(4, InvoiceNumber);
					sqlStatement.bindString(5, CalendarUtils.getOrderPostDate()+"T"+CalendarUtils.getRetrunTime()+":00");
					sqlStatement.bindString(6, InvoiceNumber);
					sqlStatement.bindString(7, BalanceAmount);
					sqlStatement.bindLong(8, division);
					sqlStatement.executeInsert();
				}
				else
				{
					Log.e("performCashCustomerPayment","6");
					sqlUpdateStatement 	=	sqLiteDatabase.compileStatement(queryUpdate);
					sqlUpdateStatement.bindString(1, BalanceAmount);
					sqlUpdateStatement.bindString(2, BalanceAmount);
					sqlUpdateStatement.bindString(3, InvoiceNumber);
					sqlUpdateStatement.execute();
				}
				sqLiteDatabase.setTransactionSuccessful();
				result =  true;
			}
			catch (Exception e) 
			{
				Log.e("performCashCustomerPayment","20");
				e.printStackTrace();
			}
			finally
			{
				if(sqlStatement!=null)
					sqlStatement.close();
				if(sqlUpdateStatement!=null)
					sqlUpdateStatement.close();
				if(sqlSelectStatement!=null)
					sqlSelectStatement.close();
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen()) {
					sqLiteDatabase.endTransaction();
					sqLiteDatabase.close();
				}
				Log.e("performCashCustomerPayment","final");
			}
			return result;
		}
	}
	
	public void deletePendingInvoice(String orderNo)
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("DELETE FROM tblPendingInvoices WHERE InvoiceNumber='"+orderNo+"'");
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

	public ArrayList<JourneyPlanDO> getUnvisitedCustomers() 
	{
		synchronized (MyApplication.MyLock)
		{
			SQLiteDatabase sqliteDB = null;
			Cursor cursor = null;
			ArrayList<JourneyPlanDO> arrJP = new ArrayList<JourneyPlanDO>();
			JourneyPlanDO objJourneyPlanDO = null;
			try
			{
				sqliteDB = DatabaseHelper.openDataBase();
//				String query = "SELECT  JP.ClientCode,C.SiteName FROM tblDailyJourneyPlan JP " +
//						"INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
//						"WHERE ClientCode NOT IN(SELECT ClientCode " +
//						"FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') " +
//						"AND JourneyDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				
				String query = "SELECT  JP.ClientCode,C.SiteName FROM tblDailyJourneyPlan JP " +
						"INNER JOIN tblCustomer C ON JP.ClientCode = C.Site " +
						"WHERE ClientCode NOT IN(SELECT ClientCode FROM tblCustomerVisit WHERE Date LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') " +
						"AND ClientCode NOT IN(SELECT ClientCode FROM tblUnvisitedCustomers WHERE JourneyDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%') " +
						"AND JourneyDate LIKE '"+CalendarUtils.getCurrentDateAsStringforStoreCheck()+"%'";
				
				cursor = sqliteDB.rawQuery(query, null);
				if(cursor != null && cursor.moveToFirst())
				{
					do
					{
						objJourneyPlanDO = new JourneyPlanDO();
						objJourneyPlanDO.site = cursor.getString(0);
						objJourneyPlanDO.siteName = cursor.getString(1);
						arrJP.add(objJourneyPlanDO);
					}while(cursor.moveToNext());
				}
			}
			catch(Exception ex)
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
			return arrJP;
		}
	}

	public void deletePendingInvoiceFromPending()
	{
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase sqlDB = null;
			
			try
			{
				sqlDB = DatabaseHelper.openDataBase();
				sqlDB.execSQL("delete from tblPendingInvoices where  InvoiceNumber  in(select id from tblOfflineData where Type='Order' and status=0 and id in (Select InvoiceNumber from tblPendingInvoices))");
				sqlDB.execSQL("delete from tblPendingInvoices where  InvoiceNumber  in(Select InvoiceNumber from tblPendingInvoices where InvoiceNumber in (select TrxCode from tblTrxHeader where TRXStatus=0))");
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
}
