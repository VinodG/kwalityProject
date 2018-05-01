package com.winit.alseer.salesman.dataaccesslayer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.databaseaccess.DictionaryEntry;
import com.winit.alseer.salesman.dataobject.DeliveryAgentOrderDetailDco;
import com.winit.alseer.salesman.dataobject.LoadRequestDetailDO;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.ProductsDO;
import com.winit.alseer.salesman.dataobject.UOMConversionFactorDO;
import com.winit.alseer.salesman.dataobject.VanLoadDO;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class ProductsDA 
{
	//Need to change the method
	public boolean insertProducts(Vector<ProductsDO> vecProductsDOs)
	{
		SQLiteDatabase objSqliteDB =null;
		try 
		{
			objSqliteDB = DatabaseHelper.openDataBase();
				
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblProducts WHERE ItemCode = ?");
			SQLiteStatement stmtSelectProductImage = objSqliteDB.compileStatement("SELECT COUNT(*) from tblProductImages WHERE ImageGalleryId = ?");
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblProducts (ProductId, ItemCode, Category, Description,UnitPerCase,ItemBatchCode,UOM,CaseBarCode,UnitBarCode,ItemType,PricingKey,Brand, SecondaryUOM, TaxGroupCode,TaxPercentage, CustomerGroupCodes,CategoryLevel0,IsActive,Division,VAT) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtInsertInProductImages = objSqliteDB.compileStatement("INSERT INTO tblProductImages (ImageGalleryId, ItemCode, ImagePath) VALUES(?,?,?)");
			
			
			SQLiteStatement stmtUpdateProductImages = objSqliteDB.compileStatement("UPDATE tblProductImages SET ItemCode =? , ImagePath =? WHERE ImageGalleryId = ?");
			
			SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblProducts SET " +
					"ProductId =? , ItemCode =?, Category =?, Description = ?, UnitPerCase= ?, ItemBatchCode = ?," +
					" UOM = ? , CaseBarCode = ?, UnitBarCode = ? , ItemType = ? , PricingKey = ?, Brand = ?, SecondaryUOM =?,TaxGroupCode=?,TaxPercentage=?,IsActive=?,VAT=? WHERE ItemCode = ?");
			 for(int i=0;i<vecProductsDOs.size();i++)
			 {
				ProductsDO productsDO = vecProductsDOs.get(i);
				stmtSelectRec.bindString(1, productsDO.SKU);
					
				long countRec = stmtSelectRec.simpleQueryForLong();
				if(countRec != 0)
				{					
					if(productsDO != null )
					{
						stmtUpdate.bindString(1, productsDO.ProductId);
						stmtUpdate.bindString(2, productsDO.SKU);
						stmtUpdate.bindString(3, productsDO.CategoryId);
						stmtUpdate.bindString(4, productsDO.ItemDesc);
						stmtUpdate.bindLong  (5, productsDO.UnitsPerCases);
						stmtUpdate.bindString(6, productsDO.BatchCode);
						stmtUpdate.bindString(7, productsDO.UOM);
						stmtUpdate.bindString(8, productsDO.CaseBarCode);
						stmtUpdate.bindString(9, productsDO.UnitBarCode);
						stmtUpdate.bindString(10, productsDO.ItemType);
						stmtUpdate.bindString(11, productsDO.PricingKey);
						stmtUpdate.bindString(12, productsDO.Brand);
						stmtUpdate.bindString(13, productsDO.secondryUOM);
						stmtUpdate.bindString(14, productsDO.TaxGroupCode);
						stmtUpdate.bindString(15, productsDO.TaxPercentage);
						stmtUpdate.bindString(16, productsDO.IsActive);
						stmtUpdate.bindDouble(17, productsDO.VATPer);
						stmtUpdate.bindString(18, productsDO.SKU);
						
						for(int index = 0;productsDO.vecProductImages!=null && index<productsDO.vecProductImages.size();index++)
						{
							long countRecord = stmtSelectProductImage.simpleQueryForLong();
							stmtSelectProductImage.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
							if(countRecord!=0)
							{
								stmtUpdateProductImages.bindString(1, productsDO.SKU);
								stmtUpdateProductImages.bindString(2, productsDO.vecProductImages.get(index).strName);
								stmtUpdateProductImages.bindDouble(3, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
								stmtUpdateProductImages.execute();
							}
							else
							{
								stmtInsertInProductImages.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
								stmtInsertInProductImages.bindString(2, productsDO.SKU);
								stmtInsertInProductImages.bindString(3, productsDO.vecProductImages.get(index).strName);
								stmtInsertInProductImages.executeInsert();
							}
						}
						stmtUpdate.execute();
					}
				}
				else
				{
					if(productsDO != null )
					{
						stmtInsert.bindString(1, productsDO.ProductId);
						stmtInsert.bindString(2, productsDO.SKU);
						stmtInsert.bindString(3, productsDO.CategoryId);
						stmtInsert.bindString(4, productsDO.ItemDesc);
						stmtInsert.bindLong  (5, productsDO.UnitsPerCases);
						stmtInsert.bindString(6, productsDO.BatchCode);
						stmtInsert.bindString(7, productsDO.UOM);
						stmtInsert.bindString(8, productsDO.CaseBarCode);
						stmtInsert.bindString(9, productsDO.UnitBarCode);
						stmtInsert.bindString(10, productsDO.ItemType);
						stmtInsert.bindString(11, productsDO.PricingKey);
						stmtInsert.bindString(12, productsDO.Brand);
						stmtInsert.bindString(13, productsDO.secondryUOM);
						stmtInsert.bindString(14, productsDO.TaxGroupCode);
						stmtInsert.bindString(15, productsDO.TaxPercentage);
						stmtInsert.bindString(16, productsDO.CustomerGroupCodes);
						stmtInsert.bindString(17, productsDO.CategoryLevel0);
						stmtInsert.bindString(18, productsDO.IsActive);
						stmtInsert.bindLong(19, productsDO.Division);
						stmtInsert.bindDouble(20, productsDO.VATPer);
						for(int index = 0;productsDO.vecProductImages!=null && index<productsDO.vecProductImages.size();index++)
						{
							stmtInsertInProductImages.bindDouble(1, StringUtils.getInt(productsDO.vecProductImages.get(index).strId));
							stmtInsertInProductImages.bindString(2, productsDO.SKU);
							stmtInsertInProductImages.bindString(3, productsDO.vecProductImages.get(index).strName);
							stmtInsertInProductImages.executeInsert();
						}
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
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
				return true;
			}
		}
		return true;
	}
	public Vector<ProductsDO> getProductsDetails()
	{
		ProductsDO productsDO;
		Vector<ProductsDO> vector = new Vector<ProductsDO>();
		try
		{
			DictionaryEntry [][] data = null;
			data	=	DatabaseHelper.get("SELECT *FROM tblProducts ORDER BY DisplayOrder ASC");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductsDO();
					productsDO.ProductId  = data[i][0].value.toString(); 
					productsDO.SKU = data[i][1].value.toString(); 
					productsDO.CategoryId = data[i][2].value.toString(); 
					productsDO.Description =  data[i][3].value.toString(); 
					productsDO.UnitsPerCases = (Integer) data[i][4].value; 
					productsDO.BatchCode = data[i][5].value.toString(); 
					productsDO.UOM =  data[i][6].value.toString();
					productsDO.CaseBarCode = data[i][7].value.toString();
					productsDO.UnitBarCode =data[i][8].value.toString();
					productsDO.ItemType =  data[i][9].value.toString();
					productsDO.PricingKey = data[i][10].value.toString();
					productsDO.ItemId = (Integer) data[i][11].value;
					productsDO.Brand = data[i][12].value.toString();
					productsDO.Category = data[i][13].value.toString();
					productsDO.CompanyId = data[i][14].value.toString();
					productsDO.GroupId = data[i][15].value.toString();
					productsDO.ItemDesc = data[i][16].value.toString();
					productsDO.ItemCode = data[i][17].value.toString();
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<ProductsDO> getProductsDetails(String productID)
	{
		ProductsDO productsDO;
		Vector<ProductsDO> vector = new Vector<ProductsDO>();
		try
		{
			DictionaryEntry [][] data = null;
			data	=	DatabaseHelper.get("SELECT *FROM tblProducts where ProductId ='"+productID+"' ORDER BY DisplayOrder ASC");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductsDO();
					productsDO.ProductId  = data[i][0].value.toString();
					productsDO.SKU  = data[i][1].value.toString();
					productsDO.CategoryId  = data[i][2].value.toString();
					productsDO.Description  = data[i][3].value.toString();
					productsDO.UnitsPerCases  = (Integer) data[i][4].value;
					productsDO.BatchCode  = data[i][5].value.toString();
					productsDO.UOM  = data[i][6].value.toString();
					productsDO.CaseBarCode  = data[i][7].value.toString();
					productsDO.UnitBarCode  = data[i][8].value.toString();
					productsDO.ItemType  = data[i][9].value.toString();
					productsDO.PricingKey  = data[i][10].value.toString();
					productsDO.ItemId = (Integer) data[i][11].value;
					productsDO.Brand = data[i][12].value.toString();
					productsDO.Category = data[i][13].value.toString();
					productsDO.CompanyId = data[i][14].value.toString();
					productsDO.GroupId = data[i][15].value.toString();
					productsDO.ItemDesc = data[i][16].value.toString();
					productsDO.ItemCode = data[i][17].value.toString();
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	
	public Vector<ProductsDO> getItemsPriceDetails()
	{
		ProductsDO productsDO;
		Vector<ProductsDO> vector = new Vector<ProductsDO>();
		SQLiteDatabase objSQLdb = null;
		Cursor cursor = null;
		try
		{
			objSQLdb = DatabaseHelper.openDataBase();
			String query = "select distinct P.ItemCode ,TP.Category,group_concat(distinct P.UOM||'='||PRICECASES),Description,'1 CS = '||EAConversion||' EA'  from tblPricing P inner join tblProducts TP on TP.ItemCode=P.ItemCode left outer join tblUOMFactor TUF on P.ItemCode=TUF. ItemCode where TUF.UOM='CS' group by P.ItemCode ORDER BY DisplayOrder ASC";
			cursor = objSQLdb.rawQuery(query, null);
			if(cursor.moveToFirst())
			{
				do
				{
//					EA=10.46108,CS=251.066
					productsDO = new ProductsDO();
					productsDO.ItemCode  	 = cursor.getString(0);
					productsDO.Category  	 = cursor.getString(1);
					productsDO.Pricecases	 = cursor.getString(2);
					if(productsDO.Pricecases.contains(","))
					{
						String[] pricecases = productsDO.Pricecases.split(",");
						productsDO.ItemPriceEA = pricecases[0].substring(3, pricecases[0].length());
						productsDO.ItemPriceCS = pricecases[1].substring(3, pricecases[1].length());
					}
					else if(productsDO.Pricecases.toLowerCase().contains("ea"))
					{
						productsDO.ItemPriceEA = productsDO.Pricecases.substring(3, productsDO.Pricecases.length());
					}
					else if(productsDO.Pricecases.toLowerCase().contains("cs"))
					{
						productsDO.ItemPriceCS = productsDO.Pricecases.substring(3, productsDO.Pricecases.length());
					} 
					productsDO.Description   = cursor.getString(3);
					productsDO.EAconversion  = cursor.getString(4);
					vector.add(productsDO);
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
			if(objSQLdb != null && objSQLdb.isOpen())
				objSQLdb.close();
		}
		
		return vector;
	}
	
	public ProductsDO getProductsDetailsByItemCode(String itemCode)
	{
		synchronized (MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase = null;
			Cursor cursor = null;
			ProductsDO obj = null;
			
			try {
				
				mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT *FROM tblProducts where ItemCode ='"+itemCode+"' ORDER BY DisplayOrder ASC";
				cursor = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						obj = new ProductsDO();
						obj.ProductId = cursor.getString(0);
						obj.ItemCode = cursor.getString(1);
						obj.Description = cursor.getString(2);
						obj.Brand = cursor.getString(10);
						obj.UnitsPerCases = cursor.getInt(16);
					}
					while(cursor.moveToNext());
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
	
	public Vector<ProductDO> getProductsDetailsByCategoryId(String catgId, String sku, String desc,String orderedItemsList, String strPricingClass, int orderType, boolean fromVanstock)
	{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery ="";
			
			if(fromVanstock)
				strQuery = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId where tp.Category like '"+catgId+"%' and tp.ItemCode like '"+sku+"%' and tp.Description like '"+desc+"%' and tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')  AND tp.ItemCode IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0)";
			else
				strQuery = "SELECT * FROM tblProducts tp Inner Join tblCategory tc on tp.Category=tc.CAtegoryId  where tp.Category like '"+catgId+"%' and tp.ItemCode like '"+sku+"%' and tp.Description like '"+desc+"%' and tp.ItemCode NOT IN ("+orderedItemsList+") and tp.ItemCode IN(select ITEMCODE from tblPricing where CUSTOMERPRICINGKEY='"+strPricingClass+"' and IsExpired='False')";
			
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.Description = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.Description1 =  data[i][3].value.toString(); 
					if(data[i][30].value != null)
						productsDO.CategoryId =data[i][30].value.toString(); 
					if(data[i][18].value != null)
						productsDO.BatchCode = data[i][18].value.toString();
					if(data[i][11].value != null)
						productsDO.UOM =  data[i][11].value.toString();
					
					productsDO.primaryUOM = productsDO.UOM;
					
					if(data[i][13].value != null)
						productsDO.CaseBarCode = data[i][13].value.toString();
					if(data[i][14].value != null)
						productsDO.UnitBarCode =data[i][14].value.toString();
					if(data[i][15].value != null)
						productsDO.ItemType =  data[i][15].value.toString();
				
					if(data[i][16].value != null)
						productsDO.UnitsPerCases =  StringUtils.getInt(data[i][16].value.toString());
					
					if(data[i][17].value != null)
						productsDO.PricingKey = data[i][17].value.toString();
					if(data[i][10].value != null)
						productsDO.brand = data[i][10].value.toString();
					
					productsDO.preCases = "";
					productsDO.preUnits = "";
					
					productsDO.vecUOM = getPricingUOM(productsDO.SKU, strPricingClass);
					
					vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<String> getPricingUOM(String itemcode, String pricing)
	{
		Vector<String> vector = new Vector<String>();
		try
		{
			SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
			
			String strQuery = "SELECT UOM FROM tblPricing WHERE  ITEMCODE = '"+itemcode+"' AND CUSTOMERPRICINGKEY ='"+pricing+"'";
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c.moveToFirst())
			{
				do
				{
					vector.add(c.getString(0));
				}
				while(c.moveToNext());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<DeliveryAgentOrderDetailDco> getProductsByCategoryId(String catgId, String item)
	{
		DeliveryAgentOrderDetailDco productsDO;
		Vector<DeliveryAgentOrderDetailDco> vector = null;
		try
		{
			if(item != null && item.endsWith(","))
				item = item.substring(0, item.lastIndexOf(","));
			
			DictionaryEntry [][] data = null;
			String strQuery = "SELECT * FROM tblProducts where CategoryId like '"+catgId+"%' and SKU NOT IN ("+item+") ORDER BY DisplayOrder ASC";
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<DeliveryAgentOrderDetailDco>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new DeliveryAgentOrderDetailDco();
					if(data[i][1].value != null)
						productsDO.itemCode = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.CategoryId = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.itemDescription =  data[i][3].value.toString(); 
					if(data[i][4].value != null)
						productsDO.unitPerCase = StringUtils.getInt(data[i][4].value.toString()); 
					if(data[i][6].value != null)
						productsDO.strUOM =  data[i][6].value.toString();
					if(data[i][10].value != null)
						productsDO.customerPriceClass = data[i][10].value.toString();
					
					if(data[i][12].value != null)
						productsDO.secondaryUOM = data[i][12].value.toString();
					
					productsDO.preCases = 1;
					productsDO.preUnits = productsDO.unitPerCase;
					
					vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<DeliveryAgentOrderDetailDco> getProductsByCategoryId_(String catgId, String item)
	{
		DeliveryAgentOrderDetailDco productsDO;
		Vector<DeliveryAgentOrderDetailDco> vector = null;
		try
		{
			if(item != null && item.endsWith(","))
				item = item.substring(0, item.lastIndexOf(","));
			
			DictionaryEntry [][] data = null;
			String strQuery = "SELECT * FROM tblProducts where CategoryId like '"+catgId+"%' ORDER BY DisplayOrder ASC";
			LogUtils.errorLog("Awa--strQuery :", strQuery);
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<DeliveryAgentOrderDetailDco>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new DeliveryAgentOrderDetailDco();
					if(data[i][1].value != null)
						productsDO.itemCode = data[i][1].value.toString(); 
					if(item != null && item.contains(productsDO.itemCode));
					else
					{
						if(data[i][2].value != null)
							productsDO.CategoryId = data[i][2].value.toString(); 
						if(data[i][3].value != null)
							productsDO.itemDescription =  data[i][3].value.toString(); 
						if(data[i][4].value != null)
							productsDO.unitPerCase = StringUtils.getInt(data[i][4].value.toString()); 
						if(data[i][6].value != null)
							productsDO.strUOM =  data[i][6].value.toString();
						if(data[i][10].value != null)
							productsDO.customerPriceClass = data[i][10].value.toString();
						
						if(data[i][9].value != null)
							productsDO.itemType = data[i][9].value.toString();
						
						if(data[i][12].value != null)
							productsDO.secondaryUOM = data[i][12].value.toString();
						
						productsDO.preCases = 0;
						productsDO.preUnits = 0;
						productsDO.totalCases = 0;
						vector.add(productsDO);
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<VanLoadDO> getProductsVanByCategoryId(String catgId, String item, int load_type, Preference preference, int division)
	{
		synchronized (MyApplication.MyLock)
		{
			VanLoadDO productsDO;
			Vector<VanLoadDO> vector = null;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(item != null && item.endsWith(","))
					item = item.substring(0, item.lastIndexOf(","));
				
				String strQuery = "";
				if(catgId == null)
				{
					if(load_type == AppConstants.UNLOAD_STOCK)
					{
//						strQuery = "SELECT distinct TPO.*, TVS.SellableQuantity FROM tblProducts TPO INNER JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode where TVS.SellableQuantity > 0 ORDER BY TPO.DisplayOrder ASC";
						strQuery = "SELECT distinct TPO.*, TVS.SellableQuantity,TRQ.LoadQuantity FROM tblProducts TPO INNER JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode LEFT OUTER JOIN tblRecommendedQuantity TRQ ON TRQ.ItemCode=TPO.ItemCode where  TVS.SellableQuantity > 0 AND TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
					}
					else
						strQuery = "SELECT distinct TPO.*,TVS.SellableQuantity FROM tblProducts TPO left outer JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode and TVS.SellableQuantity >0 WHERE TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
				}
				else
				{
					if(load_type == AppConstants.UNLOAD_STOCK)
					{
						strQuery = "SELECT distinct TPO.*, TVS.SellableQuantity FROM tblProducts TPO INNER JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode where TPO.Category like '"+catgId+"%' AND TVS.SellableQuantity > 0 AND TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
					}
					else
						strQuery = "SELECT distinct * FROM tblProducts where Category like '"+catgId+"%' AND IsActive='True' AND TPO.Division = '"+division+"' ORDER BY DisplayOrder ASC";
				}
				
				LogUtils.errorLog("Awa--strQuery :", strQuery);
				cursor = objSqliteDB.rawQuery(strQuery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					vector = new Vector<VanLoadDO>();
					do
					{
						productsDO = new VanLoadDO();
						if(cursor.getString(1) != null)
							productsDO.ItemCode = cursor.getString(1); 
						if(item != null && item.contains(productsDO.ItemCode));
						else
						{
							if(cursor.getString(9) != null)
								productsDO.CategoryId = cursor.getString(9); 
							if(cursor.getString(2) != null)
								productsDO.Description =  cursor.getString(2); 
							if(cursor.getString(16) != null)
								productsDO.UnitsPerCases = cursor.getInt(16); 
							if(cursor.getString(6) != null)
								productsDO.UOM =  "PCS";//for van load UOM always be in EA only - Anil
							if(cursor.getString(10) != null)
								productsDO.customerPriceClass = cursor.getString(10);
							
							if(cursor.getString(9) != null)
								productsDO.itemType = cursor.getString(15);
							if(cursor.getString(29) != null)
								productsDO.subCategoryId = cursor.getString(29);
							
							productsDO.SellableQuantity = 1;
							productsDO.TotalQuantity = 1;
							
							productsDO.HighlightItem = ""+cursor.getString(35);//cursor.getString(35);
							productsDO.IsActive = ""+cursor.getString(37);
							
							if(load_type == AppConstants.UNLOAD_STOCK)
							{
								if(cursor.getString(37) != null)
									productsDO.TotalQuantityToUnload = cursor.getInt(40);
//									productsDO.TotalQuantityToUnload = cursor.getInt(39);
								productsDO.SellableQuantity = productsDO.TotalQuantityToUnload;
							}
							else
							{
								if(cursor.getString(37) != null)
									productsDO.inventoryQty = cursor.getInt(40);
//									productsDO.inventoryQty = cursor.getInt(39);
								productsDO.SellableQuantity 	 = preference.getIntFromPreference(Preference.DefaultLoad_Quantity, 20);
							}
							productsDO.RecomendedLoadQuantity = cursor.getString(41);//cursor.getString(37);
//							productsDO.RecomendedLoadQuantity = cursor.getString(40);//cursor.getString(37);

							productsDO.eaConversion = new VehicleDA().getUOMofProducts(objSqliteDB, productsDO.ItemCode);
							if(productsDO.eaConversion > 0 && 
									((int)productsDO.TotalQuantityToUnload/(int)productsDO.eaConversion) > 0 && 
									productsDO.IsActive.equalsIgnoreCase("True"))
								vector.add(productsDO);
						}
					} while(cursor.moveToNext());
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
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return vector;
		}
		
	}
	
	public ArrayList<VanLoadDO> getVanProductsByCategoryId(String catgId, String item, int load_type, Preference preference, int division)
	{
		
		synchronized (MyApplication.MyLock)
		{
			VanLoadDO vanLoadDO = null;
			ArrayList<VanLoadDO> arrVanload = null;
			SQLiteDatabase objSqliteDB = null;
			Cursor cursor = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				if(item != null && item.endsWith(","))
					item = item.substring(0, item.lastIndexOf(","));
				
				String strQuery = "";
				if(catgId == null)
				{
					if(load_type == AppConstants.UNLOAD_STOCK)
					{
						strQuery = "SELECT distinct TPO.*, TVS.SellableQuantity,TRQ.LoadQuantity FROM tblProducts TPO INNER JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode  LEFT OUTER JOIN tblRecommendedQuantity TRQ ON TRQ.ItemCode=TPO.ItemCode  where TVS.SellableQuantity > 0 AND TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
					}
					else
						strQuery = "SELECT distinct TPO.*,TVS.SellableQuantity,TRQ.LoadQuantity FROM tblProducts TPO left outer JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode and TVS.SellableQuantity >0 LEFT OUTER JOIN tblRecommendedQuantity TRQ ON TRQ.ItemCode=TPO.ItemCode WHERE TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
				}
				else
				{
					if(load_type == AppConstants.UNLOAD_STOCK)
					{
						strQuery = "SELECT distinct TPO.*, TVS.SellableQuantity FROM tblProducts TPO INNER JOIN tblVanStock TVS ON TPO.ItemCode = TVS.ItemCode where TPO.Category like '"+catgId+"%' AND TVS.SellableQuantity > 0 AND TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY TPO.DisplayOrder ASC";
					}
					else
						strQuery = "SELECT distinct * FROM tblProducts where Category like '"+catgId+"%' AND TPO.IsActive='True' AND TPO.Division = '"+division+"' ORDER BY DisplayOrder ASC";
				}
				
				LogUtils.errorLog("Awa--strQuery :", strQuery);
				cursor	=	objSqliteDB.rawQuery(strQuery, null);
				if(cursor != null && cursor.moveToFirst())
				{
					arrVanload = new ArrayList<VanLoadDO>();
					do
					{
						vanLoadDO	=	new VanLoadDO();
						
						if(cursor.getString(1) != null)
							vanLoadDO.ItemCode = cursor.getString(1);
						if(item != null && item.contains(vanLoadDO.ItemCode));
						else
						{
							if(cursor.getString(9) != null)
								vanLoadDO.CategoryId = cursor.getString(9);
							if(cursor.getString(2) != null)
								vanLoadDO.Description = cursor.getString(2);
							
							if(cursor.getString(16) != null)
								vanLoadDO.UnitsPerCases = cursor.getInt(16); 
							if(cursor.getString(6) != null)
								vanLoadDO.UOM =  "UNIT";//for van load UOM always be in EA only - Anil
							if(cursor.getString(10) != null)
								vanLoadDO.customerPriceClass = cursor.getString(10);
							
							if(cursor.getString(9) != null)
								vanLoadDO.itemType = cursor.getString(15);
							if(cursor.getString(29) != null)
								vanLoadDO.subCategoryId = cursor.getString(29);
							
							vanLoadDO.SellableQuantity = 1;
							vanLoadDO.TotalQuantity = 1;
							
							vanLoadDO.HighlightItem = ""+cursor.getString(35);
							
							vanLoadDO.IsActive = ""+cursor.getString(37);
							
							if(load_type == AppConstants.UNLOAD_STOCK)
							{
								if(cursor.getString(37) != null)
//									vanLoadDO.TotalQuantityToUnload = cursor.getInt(39);
								vanLoadDO.TotalQuantityToUnload = cursor.getInt(40);
								vanLoadDO.SellableQuantity 	 = vanLoadDO.TotalQuantityToUnload;
							}
							else
							{
								if(cursor.getString(37) != null)
									vanLoadDO.inventoryQty = cursor.getInt(40);
								vanLoadDO.SellableQuantity 	 = preference.getIntFromPreference(Preference.DefaultLoad_Quantity, 20);
							}
							
							vanLoadDO.RecomendedLoadQuantity = cursor.getString(41);
							
							vanLoadDO.eaConversion = new VehicleDA().getUOMofProducts(objSqliteDB, vanLoadDO.ItemCode);
							
							if(vanLoadDO.IsActive.equalsIgnoreCase("True"))
								arrVanload.add(vanLoadDO);
						}
					} while(cursor.moveToNext());
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
				if(objSqliteDB != null && objSqliteDB.isOpen())
					objSqliteDB.close();
			}
			return arrVanload;
		}
		
	}
	
	private int getSellableQty(SQLiteDatabase objSqliteDB, String itemCode)
	{
		
		synchronized (MyApplication.MyLock)
		{
			
			int qty = 0;
			try
			{
				String strQuery = "";
				Cursor cursor = null;
				
				if(objSqliteDB == null)
					objSqliteDB = DatabaseHelper.openDataBase();
				
				strQuery = "SELECT  SellableQuantity FROM tblVanStock WHERE ItemCode = '"+itemCode+"'";
				
				cursor = objSqliteDB.rawQuery(strQuery, null);
				
				if(cursor.moveToFirst())
				{
					qty = cursor.getInt(0);
				}
				cursor.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return qty;
		}
	}
	
	public Vector<VanLoadDO> getProductsUnload(boolean isSalable)
	{
		VanLoadDO productsDO;
		Vector<VanLoadDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			
			String strQuery = "";
			
			if(isSalable)
				strQuery = "SELECT  tvm.SellableQuantity, tvm.NonSellableQuantity, TP.* FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0 AND IFNULL(IsSellableUnload ,0)=0) ORDER BY TP.DisplayOrder ASC";
			else
				strQuery = "SELECT  tvm.SellableQuantity, tvm.NonSellableQuantity, TP.* FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE NonSellableQuantity > 0 AND IFNULL(IsNonSellableUnload ,0)=0) ORDER BY TP.DisplayOrder ASC";
				
			
			LogUtils.errorLog("Awa--strQuery :", strQuery);
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<VanLoadDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new VanLoadDO();
					if(data[i][3].value != null)
						productsDO.ItemCode = data[i][3].value.toString(); 
					if(data[i][11].value != null)
						productsDO.CategoryId = data[i][11].value.toString(); 
					if(data[i][4].value != null)
						productsDO.Description =  data[i][4].value.toString(); 
					if(data[i][18].value != null)
						productsDO.UnitsPerCases = StringUtils.getInt(data[i][18].value.toString()); 
					if(data[i][13].value != null)
						productsDO.UOM =  data[i][13].value.toString();
					if(data[i][19].value != null)
						productsDO.customerPriceClass = data[i][19].value.toString();
					
					if(data[i][17].value != null)
						productsDO.itemType = data[i][17].value.toString();
						
					if(isSalable)
						productsDO.SellableQuantity = StringUtils.getInt(data[i][0].value.toString());
					else
						productsDO.SellableQuantity = StringUtils.getInt(data[i][1].value.toString());
					
					productsDO.TotalQuantity = 0;
					vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public boolean checkUnload(boolean isSalable)
	{
		boolean isAvailable = false;
		SQLiteDatabase objSqliteDB =null;
		try
		{
			String strQuery = "";
			Cursor cursor = null;
			
			objSqliteDB = DatabaseHelper.openDataBase();
			
			if(isSalable)
				strQuery = "SELECT  COUNT(*) FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE IFNULL(IsSellableUnload ,0)=1)  ORDER BY TP.DisplayOrder ASC";
			else
				strQuery = "SELECT  COUNT(*) FROM tblProducts TP inner join tblVanStock tvm on tvm.ItemCode = TP.ItemCode where  tp.ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE IFNULL(IsNonSellableUnload ,0)=1) ORDER BY TP.DisplayOrder ASC";
				
			cursor = objSqliteDB.rawQuery(strQuery, null);
			
			if(cursor.moveToFirst())
			{
				if(cursor.getInt(0) >  0)
					isAvailable = true;
				else
					isAvailable = false;
			}
			cursor.close();
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
		return isAvailable;
	}
	
	public void updateUnlod(boolean isSalable,ArrayList<LoadRequestDetailDO> loadRequestDetailDOs)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate;
				 if(isSalable)
					 stmtUpdate = objSqliteDB.compileStatement("Update tblVanStock set IsSellableUnload = '1' WHERE ItemCode = ?");
				 else
					 stmtUpdate = objSqliteDB.compileStatement("Update tblVanStock set IsNonSellableUnload = '1' WHERE ItemCode = ?");
				 
				 if(loadRequestDetailDOs != null && loadRequestDetailDOs.size() > 0)
				 {
					 for (LoadRequestDetailDO loadRequestDetailDO : loadRequestDetailDOs)
					 {
						 stmtUpdate.bindString(1, loadRequestDetailDO.ItemCode);
						 stmtUpdate.execute();
					 }
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
	
	public void updateUnlodStatus(ArrayList<VanLoadDO> vecOrdProduct)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB =null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 SQLiteStatement stmtUpdate;
				 stmtUpdate = objSqliteDB.compileStatement("Update tblVanStock set IsNonSellableUnload = '0', IsSellableUnload = '0' WHERE ItemCode = ?");
				 
				 for (VanLoadDO vanLoadDO : vecOrdProduct) 
				 {
					 stmtUpdate.bindString(1, vanLoadDO.ItemCode);
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
	
//	public ArrayList<LoadRequestDetailDO> getProductsVanUnload(String item, boolean isSealable, String strMovemetCode)
//	{
//		LoadRequestDetailDO productsDO;
//		Vector<LoadRequestDetailDO> vector = null;
//		try
//		{
//			if(item != null && item.endsWith(","))
//				item = item.substring(0, item.lastIndexOf(","));
//			
//			DictionaryEntry [][] data = null;
//			
//			String strQuery = "";
//			
//			if(isSealable)
//				strQuery = "SELECT * FROM tblProducts where  ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE SellableQuantity > 0)";
//			else
//				strQuery = "SELECT * FROM tblProducts where  ItemCode  IN(SELECT ItemCode FROM tblVanStock WHERE ReturnedQuantity > 0)";
//			
//			LogUtils.errorLog("Awa--strQuery :", strQuery);
//			data	=	DatabaseHelper.get(strQuery);
//			if(data != null && data.length > 0)
//			{
//				vector = new Vector<LoadRequestDetailDO>();
//				for(int i=0;i<data.length;i++)
//				{
//					LoadRequestDetailDO oRequestDetailDO		= new LoadRequestDetailDO();
//					oRequestDetailDO.LineNo			=	i+"";
//					oRequestDetailDO.MovementCode	=	strMovemetCode;
//					oRequestDetailDO.ItemCode		=	data[i][1].value.toString();
//					oRequestDetailDO.OrgCode		=	"DUB";
//					oRequestDetailDO.ItemDescription=	data[i][2].value.toString();
//					oRequestDetailDO.ItemAltDescription=	"";
//					oRequestDetailDO.MovementStatus	=	"Pending";
//					oRequestDetailDO.UOM			=	data[i][1].value.toString();
//					oRequestDetailDO.QuantityLevel1	=	data[i][1].value.toString();
//					oRequestDetailDO.QuantityLevel2	=	data[i][1].value.toString();
//					oRequestDetailDO.QuantityLevel3	=	data[i][1].value.toString();
//					oRequestDetailDO.QuantityBU		=	data[i][1].value.toString();
//					oRequestDetailDO.QuantitySU		=	data[i][1].value.toString();
//					oRequestDetailDO.NonSellableQty	=	data[i][1].value.toString();
//					oRequestDetailDO.CurrencyCode	=	data[i][1].value.toString();
//					oRequestDetailDO.PriceLevel1	=	data[i][1].value.toString();
//					oRequestDetailDO.PriceLevel2	=	data[i][1].value.toString();
//					oRequestDetailDO.PriceLevel3	=	data[i][1].value.toString();
//					oRequestDetailDO.MovementReasonCode=	data[i][1].value.toString();
//					oRequestDetailDO.ExpiryDate		=	data[i][1].value.toString();
//					oRequestDetailDO.Note			=	data[i][1].value.toString();
//					oRequestDetailDO.AffectedStock	=	data[i][1].value.toString();
//					oRequestDetailDO.Status			=	data[i][1].value.toString();
//					oRequestDetailDO.DistributionCode=	data[i][1].value.toString();
//					oRequestDetailDO.CreatedOn		=	data[i][1].value.toString();
//					oRequestDetailDO.ModifiedDate	=	data[i][1].value.toString();
//					oRequestDetailDO.ModifiedTime	=	data[i][1].value.toString();
//					oRequestDetailDO.PushedOn		=	data[i][1].value.toString();
//					oRequestDetailDO.CancelledQuantity=	0;
//					oRequestDetailDO.InProcessQuantity=	0;
//					oRequestDetailDO.ShippedQuantity=	0;
//					oRequestDetailDO.ModifiedOn		=	"";
//					
//					
//					vector.add(oRequestDetailDO);
//				}
//			}
//		}
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//		
//		return vector;
//	}
	
	public Vector<ProductDO> getProductsByCategoryId_New(String catgId, String item)
	{
		ProductDO productsDO;
		Vector<ProductDO> vector = null;
		try
		{
			DictionaryEntry [][] data = null;
			String strQuery = "SELECT * FROM tblProducts where CategoryId like '"+catgId+"%' and SKU NOT IN ("+item+") ORDER BY  ORDER BY DisplayOrder ASC";
			data	=	DatabaseHelper.get(strQuery);
			if(data != null && data.length > 0)
			{
				vector = new Vector<ProductDO>();
				for(int i=0;i<data.length;i++)
				{
					productsDO = new ProductDO();
					if(data[i][0].value != null)
						productsDO.ProductId  = data[i][0].value.toString();
					if(data[i][1].value != null)
						productsDO.SKU = data[i][1].value.toString(); 
					if(data[i][2].value != null)
						productsDO.CategoryId = data[i][2].value.toString(); 
					if(data[i][3].value != null)
						productsDO.Description =  data[i][3].value.toString(); 
					if(data[i][4].value != null)
						productsDO.UnitsPerCases = StringUtils.getInt(data[i][4].value.toString()); 
					if(data[i][5].value != null)
						productsDO.BatchCode = data[i][5].value.toString();
					if(data[i][6].value != null)
						productsDO.UOM =  data[i][6].value.toString();
					if(data[i][7].value != null)
						productsDO.CaseBarCode = data[i][7].value.toString();
					if(data[i][8].value != null)
						productsDO.UnitBarCode =data[i][8].value.toString();
					if(data[i][9].value != null)
						productsDO.ItemType =  data[i][9].value.toString();
					if(data[i][10].value != null)
						productsDO.PricingKey = data[i][10].value.toString();
					if(data[i][11].value != null)
						productsDO.brand = data[i][11].value.toString();
					
					if(data[i][12].value != null)
						productsDO.secondaryUOM = data[i][12].value.toString();
					
					productsDO.preCases = "1";
					productsDO.preUnits = ""+productsDO.UnitsPerCases;
					
					vector.add(productsDO);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<ProductDO> getProductsDetailsByCategoryId(String catgId, String sku, String desc,String orderedItemsList, boolean isCapture)
	{
		Vector<ProductDO> vector = null;
		try
		{
			SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
			String strQuery = "SELECT p.*, c.CategoryName FROM tblProducts p inner join tblCategory c on c.CategoryId = p.Category where p.Category like '"+catgId+"%' and p.ItemCode like '"+sku+"%' and p.Description like '"+desc+"%' and p.ItemCode NOT IN ("+orderedItemsList+") and p.ItemCode IN(select DISTINCT ITEMCODE from tblVanStock where SellableQuantity > 0) ORDER BY p.DisplayOrder ASC";
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c!=null)
			{
				vector = new Vector<ProductDO>();
				while(c.moveToNext())
				{
					ProductDO p  = new ProductDO();
					p.promotionId = c.getString(0);
					p.SKU = c.getString(1);
					p.Description= c.getString(2);
					p.CategoryId = c.getString(9);
					p.brand = c.getString(10);
					p.UOM = c.getString(11);
					p.secondaryUOM= c.getString(12);
					p.CaseBarCode = c.getString(13);
					p.UnitBarCode = c.getString(14);
					p.ItemType = c.getString(15);
					p.UnitsPerCases = c.getInt(16);
					p.PricingKey = c.getString(17);
					p.BatchCode = c.getString(18);
					p.TaxGroupCode = c.getString(21);
					p.TaxPercentage = c.getFloat(22);
					p.CategoryId = c.getString(29);
					vector.add(p);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public Vector<ProductDO> getProductsDetailsByBrandId(String brandID)
	{
		Vector<ProductDO> vector = null;
		try
		{
			SQLiteDatabase sqlLite = DatabaseHelper.openDataBase();
			String strQuery = "SELECT * FROM tblProducts where Brand = "+"'"+brandID+"' ORDER BY DisplayOrder ASC";
			Cursor c=sqlLite.rawQuery(strQuery, null); 
			if(c!=null)
			{
				vector = new Vector<ProductDO>();
				while(c.moveToNext())
				{
					ProductDO p  = new ProductDO();
					p.promotionId = c.getString(0);
					p.SKU = c.getString(1);
					p.Description= c.getString(2);
					p.CategoryId = c.getString(9);
					p.brand = c.getString(10);
					p.UOM = c.getString(11);
					p.secondaryUOM= c.getString(12);
					p.CaseBarCode = c.getString(13);
					p.UnitBarCode = c.getString(14);
					p.ItemType = c.getString(15);
					p.UnitsPerCases = c.getInt(16);
					p.PricingKey = c.getString(17);
					p.BatchCode = c.getString(18);
					p.TaxGroupCode = c.getString(21);
					p.TaxPercentage = c.getFloat(22);
					p.CategoryId = c.getString(29);
					vector.add(p);
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return vector;
	}
	
	public HashMap<String,UOMConversionFactorDO> getUOMConversion(String uom){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			Cursor cursorsUoms=null;
			String query= "Select distinct ItemCode,UOM,Factor,EAConversion from tblUOMFactor where UOM='"+uom+"'";
			HashMap<String,UOMConversionFactorDO> hashArrUoms = new HashMap<String, UOMConversionFactorDO>();
			try {
				sqLiteDatabase = DatabaseHelper.openDataBase();
				cursorsUoms=sqLiteDatabase.rawQuery(query, null);
				if(cursorsUoms.moveToFirst()){
					do {
						UOMConversionFactorDO conversionFactorDO = new UOMConversionFactorDO();
						conversionFactorDO.ItemCode		= 	cursorsUoms.getString(0);
						conversionFactorDO.UOM			=	cursorsUoms.getString(1);
						conversionFactorDO.factor		=	cursorsUoms.getFloat(2);
						conversionFactorDO.eaConversion	=	cursorsUoms.getFloat(3);
						
						String key = conversionFactorDO.ItemCode;
						hashArrUoms.put(key,conversionFactorDO);
					} while (cursorsUoms.moveToNext());
				}
				if(cursorsUoms!=null && !cursorsUoms.isClosed())
					cursorsUoms.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(cursorsUoms!=null && !cursorsUoms.isClosed())
					cursorsUoms.close();
				if(sqLiteDatabase!=null && !sqLiteDatabase.isOpen())
					sqLiteDatabase.close();
			}
			return hashArrUoms;
		}
	}
}
