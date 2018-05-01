package com.winit.alseer.salesman.dataaccesslayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.winit.alseer.salesman.common.AppConstants;
import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.BrandDO;
import com.winit.alseer.salesman.dataobject.CategoryDO;
import com.winit.alseer.salesman.dataobject.JourneyPlanDO;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.dataobject.ProductDO;
import com.winit.alseer.salesman.dataobject.SellingSKUClassification;
import com.winit.alseer.salesman.webAccessLayer.ServiceURLs;
import com.winit.sfa.salesman.MyApplication;

public class CategoriesDA 
{
	public HashMap<String, CategoryDO> getCategoryList()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		HashMap<String, CategoryDO> hmCategories = null;
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory where CategoryId in (select DISTINCT Category from tblProducts) order by CategoryName asc", null);
			
			if(cursor.moveToFirst())
			{
				hmCategories = new HashMap<String, CategoryDO>();
				AppConstants.vecCategories = new Vector<CategoryDO>();
				 
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					AppConstants.vecCategories.add(objCategory);
					hmCategories.put(objCategory.categoryId, objCategory);
					
				}while(cursor.moveToNext());
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return hmCategories;
	}
	
	public HashMap<String, CategoryDO> getCategoryListForReturn()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		HashMap<String, CategoryDO> hmCategories = null;
		try 
		{
			mDatabase = DatabaseHelper.openDataBase();
			cursor  = mDatabase.rawQuery("select * from tblCategory  where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0)) order by CategoryName asc", null);
			
			if(cursor.moveToFirst())
			{
				hmCategories = new HashMap<String, CategoryDO>();
				AppConstants.vecCategories = new Vector<CategoryDO>();
				 
				do
				{
					CategoryDO objCategory = new CategoryDO();
					objCategory.categoryId = cursor.getString(0);
					objCategory.categoryName = cursor.getString(1);
					AppConstants.vecCategories.add(objCategory);
					hmCategories.put(objCategory.categoryName, objCategory);
					
				}while(cursor.moveToNext());
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(mDatabase != null)
				mDatabase.close();
		}
		return hmCategories;
	}
	
	public Vector<CategoryDO> getAllCategory()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<CategoryDO> vector = new Vector<CategoryDO>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("select CategoryId,CategoryName from tblCategory order by CategoryName asc", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						CategoryDO objCategory = new CategoryDO();
						objCategory.categoryId = cursor.getString(0);
						objCategory.categoryName = cursor.getString(1);
						vector.add(objCategory);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public Vector<String> getAllCategoryName()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("Select * from tblCategory WHERE CategoryId IN (SELECT DISTINCT Category FROM tblProducts) Order By CategoryName", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public String getCategoryName(String CategoryId)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			String category = "";
			try 
			{
				if(mDatabase == null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("Select * from tblCategory WHERE CategoryId='"+CategoryId+"'", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						category = cursor.getString(1);
						break;
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
//				if(mDatabase != null)
//					mDatabase.close();
			}
			return category;
		}
	}
	
	public Vector<String> getAvailableCategory()
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<String> vector = new Vector<String>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				String query = "Select * from tblCategory where CategoryId in(select DISTINCT Category from tblProducts where ItemCode IN (select DISTINCT ItemCode from tblVanStock where SellableQuantity!= 0)) order by CategoryName asc";
				cursor  = mDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do
					{
						String category = cursor.getString(1);
						vector.add(category);
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	
	
//	public Vector<MainCategoryDO> getAllMainCategory(SQLiteDatabase mDatabase, String agencyID){
//		Cursor cursor = null;
//		synchronized(MyApplication.MyLock) 
//		{
//			Vector<MainCategoryDO> vector = new Vector<MainCategoryDO>();
//			try 
//			{
//				if(mDatabase == null || !mDatabase.isOpen())
//					mDatabase = DatabaseHelper.openDataBase();
//				
//				String QUERY =  "SELECT DISTINCT TP.Category, TC.CategoryName FROM tblProducts TP INNER JOIN tblCategory TC ON TP.Category = TC.CategoryId "+
//								"WHERE BRAND IN (SELECT BrandId FROM tblBrand WHERE ParentCode  ='"+agencyID+"')";
//				cursor  = mDatabase.rawQuery(QUERY, null);
//				
//				MainCategoryDO objCategory 	= new MainCategoryDO();
//				objCategory.categoryId 		= "New Launches";
//				objCategory.categoryName 	= "New Launches";
//				
//				objCategory.vecBrandDO 		= getBrandDetailByCategoryIDDefault(objCategory.categoryId); 
//				
//				if(objCategory.vecBrandDO != null && objCategory.vecBrandDO.size() > 0)
//					vector.add(objCategory);
//				
//				objCategory 	= new MainCategoryDO();
//				objCategory.categoryId 		= AppConstants.MUST_HAVE;
//				objCategory.categoryName 	= AppConstants.MUST_HAVE;
//				
//				objCategory.vecBrandDO 		= getBrandDetailByCategoryIDDefault(objCategory.categoryId); 
//				
//				if(objCategory.vecBrandDO != null && objCategory.vecBrandDO.size() > 0)
//					vector.add(objCategory);
//				
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						objCategory 				= new MainCategoryDO();
//						objCategory.categoryId 		= cursor.getString(0);
//						objCategory.categoryName 	= cursor.getString(1);
//						
//						objCategory.vecBrandDO 		= getBrandDetailByCategoryID(mDatabase, objCategory.categoryId, agencyID); 
//						
//						if(objCategory.vecBrandDO != null && objCategory.vecBrandDO.size() > 0)
//							vector.add(objCategory);
//						
//					}while(cursor.moveToNext());
//				}
//			} 
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(cursor != null)
//					cursor.close();
//			}
//			return vector;
//		}
//	}
	
	/**
	 * New StoreCheck Data fetching
	 * @param mallsDetails
	 * @return
	 */
	public HashMap<BrandDO, ArrayList<ProductDO>> getStoreCheckCategories(JourneyPlanDO mallsDetails,String categoryId)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase=null;
			HashMap<BrandDO, ArrayList<ProductDO>> hash=new HashMap<BrandDO, ArrayList<ProductDO>>();
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(mallsDetails, mDatabase);
				
				hash = getBrandDetailByClassification(sellingSKU,categoryId);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hash;
		}
	}
	
	/**
	 * New StoreCheck Data fetching
	 * @param mallsDetails
	 * @return
	 */
	public HashMap<String, ArrayList<ProductDO>> getStoreCheckByCategories(JourneyPlanDO mallsDetails,String categoryId)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase=null;
			HashMap<String, ArrayList<ProductDO>> hash=new HashMap<String, ArrayList<ProductDO>>();
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(mallsDetails, mDatabase);
				
				hash = getBrandDetailByClassificationBycategory(sellingSKU,categoryId);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hash;
		}
	}
	
	/**
	 * New StoreCheck Data fetching
	 * @param mallsDetails
	 * @param categoryId 
	 * @return
	 */
	public HashMap<String, ArrayList<ProductDO>> getStoreCheckNewSelling(JourneyPlanDO mallsDetails, String categoryId)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase=null;
			HashMap<String, ArrayList<ProductDO>> hash=new HashMap<String, ArrayList<ProductDO>>();
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerNewSellingSKUGroup(mallsDetails, mDatabase);
				
				hash = getBrandDetailByNewSelling(sellingSKU,categoryId);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hash;
		}
	}
	
	public LinkedHashMap<SellingSKUClassification, HashMap<BrandDO, ArrayList<ProductDO>>> getAllMainCategoryTemp(JourneyPlanDO mallsDetails)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			SQLiteDatabase mDatabase=null;
			LinkedHashMap<SellingSKUClassification, HashMap<BrandDO, ArrayList<ProductDO>>> hash=new LinkedHashMap<SellingSKUClassification, HashMap<BrandDO,ArrayList<ProductDO>>>();
			
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				SellingSKUClassification sellingSKU = new SellingSKUClassificationsDA().getCustomerSellingSKUGroup(mallsDetails, mDatabase);
				
				Vector<SellingSKUClassification> vecSellingSKU = new SellingSKUClassificationsDA().getAllSellingSKUClassifications();
				
				for (SellingSKUClassification objSellingSKU : vecSellingSKU) 
				{
					HashMap<BrandDO, ArrayList<ProductDO>> 	hashProductbyBrand= getBrandDetailByCategoryIDNew(objSellingSKU,mallsDetails, sellingSKU);
					if(hashProductbyBrand != null && hashProductbyBrand.size() > 0)
						hash.put(objSellingSKU, hashProductbyBrand);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hash;
		}
	}
	
	public Vector<BrandDO> getAllBrands(String agencyID)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<BrandDO> vector = new Vector<BrandDO>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				//"SELECT * FROM tblBrand WHERE ParentCode ='"+agencyID+"'"
				cursor  = mDatabase.rawQuery("SELECT * FROM tblBrand", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						BrandDO objCategory 		= new BrandDO();
						objCategory.brandId 		= cursor.getString(0);
						objCategory.brandName 		= cursor.getString(1);
						objCategory.categoryId 		= cursor.getString(2);
						objCategory.image			= cursor.getString(3);
						
						vector.add(objCategory);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	
//	public Vector<NameIDDo> getAllAgencies(){
//		SQLiteDatabase mDatabase = null;
//		Cursor cursor = null;
//		synchronized(MyApplication.MyLock) 
//		{
//			Vector<NameIDDo> vector = new Vector<NameIDDo>();
//			try 
//			{
//				mDatabase = DatabaseHelper.openDataBase();
//				cursor  = mDatabase.rawQuery("SELECT AgencyId,AgencyName FROM tblAgency", null);
//				
//				if(cursor.moveToFirst())
//				{
//					do
//					{
//						NameIDDo objNameID 	= new NameIDDo();
//						objNameID.strId 	= cursor.getString(0);
//						objNameID.strName 	= cursor.getString(1);
//						
//						objNameID.vecMainCategories = getAllMainCategory(mDatabase, objNameID.strId);
//						
//						if(objNameID.vecMainCategories != null)
//							vector.add(objNameID);
//						
//					}while(cursor.moveToNext());
//				}
//			} 
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
//			finally
//			{
//				if(cursor != null)
//					cursor.close();
//				if(mDatabase != null)
//					mDatabase.close();
//			}
//			return vector;
//		}
//	}
	
	
	public Vector<NameIDDo> getAllAgenciesTemp(JourneyPlanDO mallsDetails)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<NameIDDo> vector = new Vector<NameIDDo>();
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT AgencyId,AgencyName FROM tblAgency", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						NameIDDo objNameID 	= new NameIDDo();
						objNameID.strId 	= cursor.getString(0);
						objNameID.strName 	= cursor.getString(1);
						vector.add(objNameID);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	
	private Vector<BrandDO> getBrandDetail(SQLiteDatabase mDatabase,String categoryId)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<BrandDO> vector = new Vector<BrandDO>();
			try 
			{
				if(mDatabase == null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				
				cursor  = mDatabase.rawQuery("SELECT DISTINCT BRAND FROM tblProducts WHERE Category = '"+categoryId+"' ORDER BY DisplayOrder ASC", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						BrandDO objCategory 		= new BrandDO();
						objCategory.categoryId 		= categoryId;
						objCategory.brandId 		= cursor.getString(0);
						objCategory.categoryName 	= objCategory.brandId;
						objCategory.image		 	= objCategory.brandId;
						
						if(objCategory.image == null)
							objCategory.image = "axe";
						
						if(objCategory.image.contains("ANDAMAN"))
							objCategory.image = "daisy";
						else if(objCategory.image.contains("ICE MOU"))
							objCategory.image = "mou";
						else if(objCategory.image.contains("MAGNO"))
							objCategory.image = "mag";
						else if(objCategory.image.contains("Sparkling"))
							objCategory.image = "nutritea";
						else if(objCategory.image.contains("FRUIT"))
							objCategory.image = "fruit";
						else if(objCategory.image.contains("fn_seasons"))
							objCategory.image = "fruit";
						else if(objCategory.image.contains("MYANMAR DOUBLE"))
							objCategory.image = "fn";
						else if(objCategory.image.contains("100PLUS"))
							objCategory.image = "hun";
						else if(objCategory.image.contains("BEER"))
							objCategory.image = "beer";
						
						vector.add(objCategory);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	
	}
	private Vector<BrandDO> getBrandDetailByCategoryID(SQLiteDatabase mDatabase, String categoryId, String agencyID)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<BrandDO> vector = new Vector<BrandDO>();
			try 
			{
				if(mDatabase == null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				
				String query = "SELECT DISTINCT TP.BRAND, TB.BrandImage, TB.BrandName FROM tblProducts TP INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND" +
							   " WHERE TP.Category = '"+categoryId+"' AND TP.BRAND IN (SELECT BrandId FROM tblBrand WHERE ParentCode  ='"+agencyID+"') ORDER BY TP.DisplayOrder ASC";
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						BrandDO objBrand 	 = new BrandDO();
						objBrand.categoryId 	 = categoryId;
						objBrand.brandId 	 = cursor.getString(0);
						objBrand.image		 = cursor.getString(1);
						objBrand.brandName	 = cursor.getString(2);
						
						if(objBrand.image != null && objBrand.image.length() > 0 && objBrand.image.contains("../"))
						{
							objBrand.image = objBrand.image.replace("../", "");
							objBrand.image = ServiceURLs.IMAGE_GLOBAL_URL+objBrand.image;
						}
						else if(objBrand.image != null && objBrand.image.length() > 0)
							objBrand.image = ServiceURLs.IMAGE_GLOBAL_URL+objBrand.image;
						
						vector.add(objBrand);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return vector;
		}
	
	}
	
	private HashMap<BrandDO, ArrayList<ProductDO>> getBrandDetailByClassification(SellingSKUClassification sellingSKU,String category)
	{
		Cursor cursor = null;
		HashMap<BrandDO, ArrayList<ProductDO>> hashPro = new HashMap<BrandDO, ArrayList<ProductDO>>();
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				SQLiteDatabase mDatabase = DatabaseHelper.openDataBase();
				
				String query = "";
				if(category!=null)
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description from tblProducts TP " +
						"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND  " +
						"WHERE TP.Category ='"+category+"' " +
						"GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName";
				else
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description from tblProducts TP " +
							"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode  " +
							"WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND S.IsCoreSKU = 'True' " +
							"GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority";
				
				Log.d("productQuery", query);
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					String lastBrandId=null;
					BrandDO objBrand=null;
					do
					{
						if(lastBrandId==null || !lastBrandId.equalsIgnoreCase(cursor.getString(0))){
							lastBrandId = cursor.getString(0);
							objBrand = new BrandDO();
						}
						objBrand.brandId 	 	 =	lastBrandId;
						objBrand.brandName	 	 = cursor.getString(1);
						objBrand.image		 	 = cursor.getString(2);
						objBrand.agency		 	 = cursor.getString(3);
						ProductDO productDO      = new ProductDO();
						productDO.SKU			 = cursor.getString(4);
						productDO.Description	 = cursor.getString(5);
//						productDO.CategoryId	 = cursor.getString(5);
//						productDO.categoryName	 = cursor.getString(6);
						productDO.brand			 = objBrand.brandName;
						productDO.brandcode		 = objBrand.brandId;
						if(hashPro.containsKey(objBrand)){
							ArrayList<ProductDO> arrayList = hashPro.get(objBrand);
							arrayList.add(productDO);
						}else{
							ArrayList<ProductDO> arrayList=new ArrayList<ProductDO>();
							arrayList.add(productDO);
							hashPro.put(objBrand, arrayList);
						}
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hashPro;
		}
	}
	
	private HashMap<String, ArrayList<ProductDO>> getBrandDetailByClassificationBycategory(SellingSKUClassification sellingSKU,String category)
	{
		Cursor cursor = null;
		HashMap<String, ArrayList<ProductDO>> hashPro = new HashMap<String, ArrayList<ProductDO>>();
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				SQLiteDatabase mDatabase = DatabaseHelper.openDataBase();
				
				String query = "";
				/**If reverting back then change the Category to SubCategory**/
				if(category!=null)
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description, TP.CustomerGroupCodes,TP.Category, TP.Classifications from tblProducts TP " +
						"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND  " +
						"WHERE TP.Category ='"+category+"' " +
						"GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName";
				else
//					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description" +
//							", TP.CustomerGroupCodes,TP.Category, TP.Classifications from tblProducts TP " +
//							"INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode  " +
//							"AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND S.IsCoreSKU = 'True' " +
//							"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority";
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description" +
							", TP.CustomerGroupCodes,TP.Category , TP.Classifications from tblProducts TP " +
							"INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode  " +
							"AND S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND S.IsCoreSKU = 'True' AND TP.IsActive='True' " +
							"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority";
				
				Log.d("productQuery_OSA", query);
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					String lastBrandId=null;
					BrandDO objBrand=null;
					String CustomerGroupCodes = "";
					do
					{
						if(lastBrandId==null || !lastBrandId.equalsIgnoreCase(cursor.getString(0))){
							lastBrandId = cursor.getString(0);
							objBrand = new BrandDO();
						}
						objBrand.brandId 	 	 	=	lastBrandId;
						objBrand.brandName	 	 	= cursor.getString(1);
						objBrand.image		 	 	= cursor.getString(2);
						objBrand.agency		 	 	= cursor.getString(3);
						ProductDO productDO      	= new ProductDO();
						productDO.SKU			 	= cursor.getString(4);
						productDO.Description	 	= cursor.getString(5);
						CustomerGroupCodes		 	= cursor.getString(6);
						productDO.categoryName	 	= cursor.getString(7);
						productDO.subCategoryName	= cursor.getString(8);
						
						productDO.brand			 	= objBrand.brandName;
						productDO.brandcode		 	= objBrand.brandId;
						//If CustomerGroupCodes is placed in place of brandName then post service has to be handled
						if(hashPro.containsKey(objBrand.brandName)){
							ArrayList<ProductDO> arrayList = hashPro.get(objBrand.brandName);
							arrayList.add(productDO);
						}else{
							ArrayList<ProductDO> arrayList=new ArrayList<ProductDO>();
							arrayList.add(productDO);
							hashPro.put(objBrand.brandName, arrayList);
						}
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hashPro;
		}
	}
	
	private HashMap<String, ArrayList<ProductDO>> getBrandDetailByNewSelling(SellingSKUClassification sellingSKU, String categoryId)
	{
		Cursor cursor = null;
		HashMap<String, ArrayList<ProductDO>> hashPro = new HashMap<String, ArrayList<ProductDO>>();
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				SQLiteDatabase mDatabase = DatabaseHelper.openDataBase();
				
				String query = "";
				
				if(categoryId != null)
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description, TP.CustomerGroupCodes,TP.Category, TP.Classifications from tblProducts TP " +
							"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode  " +
							"WHERE TP.Category ='"+categoryId+"' " +
							"GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority limit 2";
				else
					query = "SELECT TB.BrandId , TB.BrandName, TB.BrandImage, TB.ParentCode, TP.ItemCode,TP.Description, TP.CustomerGroupCodes,TP.Category, TP.Classifications from tblProducts TP " +
							"INNER JOIN tblBrand TB ON TB.BrandId = TP.BRAND INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode  " +
							"GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandName, S.IsCoreSKUPriority limit 2";
				
				Log.d("productQuery_NewSelling", query);
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					String lastBrandId=null;
					BrandDO objBrand=null;
					String CustomerGroupCodes = "";
					do
					{
						if(lastBrandId==null || !lastBrandId.equalsIgnoreCase(cursor.getString(0))){
							lastBrandId = cursor.getString(0);
							objBrand = new BrandDO();
						}
						objBrand.brandId 	 	 	=	lastBrandId;
						objBrand.brandName	 	 	= cursor.getString(1);
						objBrand.image		 	 	= cursor.getString(2);
						objBrand.agency		 	 	= cursor.getString(3);
						ProductDO productDO      	= new ProductDO();
						productDO.SKU			 	= cursor.getString(4);
						productDO.Description	 	= cursor.getString(5);
						CustomerGroupCodes		 	= cursor.getString(6);
						productDO.categoryName	 	= cursor.getString(7);
						productDO.subCategoryName	= cursor.getString(8);
						
						productDO.brand			 	= objBrand.brandName;
						productDO.brandcode		 	= objBrand.brandId;
						if(hashPro.containsKey(CustomerGroupCodes)){
							ArrayList<ProductDO> arrayList = hashPro.get(CustomerGroupCodes);
							arrayList.add(productDO);
						}else{
							ArrayList<ProductDO> arrayList=new ArrayList<ProductDO>();
							arrayList.add(productDO);
							hashPro.put(CustomerGroupCodes, arrayList);
						}
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hashPro;
		}
	}
	
	private HashMap<BrandDO, ArrayList<ProductDO>> getBrandDetailByCategoryIDNew( SellingSKUClassification objSellingSKU,JourneyPlanDO mallsDetails, SellingSKUClassification sellingSKU)
	{
		Cursor cursor = null;
		HashMap<BrandDO, ArrayList<ProductDO>> hashPro = new HashMap<BrandDO, ArrayList<ProductDO>>();
		synchronized(MyApplication.MyLock) 
		{
			try 
			{
				SQLiteDatabase mDatabase = DatabaseHelper.openDataBase();
				
				String query = "";
//				if(objSellingSKU.Description.equalsIgnoreCase("Others"))
//						query = "SELECT TB.BrandId , TB.BrandName,TB.ParentCode, TP.ItemCode,TP.Description,TC.CategoryId,TC.CategoryName from tblProducts TP INNER JOIN tblBrand TB ON " +
//								"TB.BrandId = TP.BRAND INNER JOIN tblCategory TC on TP.Category = TC.CategoryId INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode " +
//								" WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' " +
//								"AND " +
//								"S.SellingSKUId NOT IN (SELECT SellingSKUId FROM tblGroupSellingSKUClassification) " +
//								" GROUP BY TP.ItemCode Order by TB.BrandId";
//				else
					query = "SELECT TB.BrandId , TB.BrandName,TB.ParentCode, TP.ItemCode,TP.Description,TC.CategoryId,TC.CategoryName from tblProducts TP INNER JOIN tblBrand TB ON " +
							"TB.BrandId = TP.BRAND INNER JOIN tblCategory TC on TP.Category = TC.CategoryId INNER JOIN tblSellingSKU S ON S.ItemCode = TP.ItemCode " +
							" WHERE S.GroupType = '"+sellingSKU.Name+"' AND S.GroupCode ='"+sellingSKU.Code+"' AND " +
							"S.SellingSKUId IN (SELECT SellingSKUId FROM tblGroupSellingSKUClassification WHERE SellingSKUClassificationId = "+objSellingSKU.SellingSkusClassificationId+") " +
							" GROUP BY TP.ItemCode Order by TP.DisplayOrder ASC,TB.BrandId";
				
				Log.d("productQuery", query);
				cursor  = mDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					String lastBrandId=null;
					BrandDO objBrand=null;
					do
					{
						if(lastBrandId==null || !lastBrandId.equalsIgnoreCase(cursor.getString(0))){
							lastBrandId = cursor.getString(0);
							objBrand = new BrandDO();
						}
						objBrand.brandId 	 	 =	lastBrandId;
						objBrand.brandName	 	 = cursor.getString(1);
						objBrand.agency		 	 = cursor.getString(2);
						ProductDO productDO      = new ProductDO();
						productDO.SKU			 = cursor.getString(3);
						productDO.Description	 = cursor.getString(4);
						productDO.CategoryId	 = cursor.getString(5);
						productDO.categoryName	 = cursor.getString(6);
						productDO.brand			 = objBrand.brandName;
						productDO.brandcode		 = objBrand.brandId;
						if(hashPro.containsKey(objBrand)){
							ArrayList<ProductDO> arrayList = hashPro.get(objBrand);
							arrayList.add(productDO);
						}else{
							ArrayList<ProductDO> arrayList=new ArrayList<ProductDO>();
							arrayList.add(productDO);
							hashPro.put(objBrand, arrayList);
						}
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
			}
			return hashPro;
		}
	
	}
	
	
	private Vector<BrandDO> getBrandDetailByBrandID(SQLiteDatabase mDatabase,String BrandId, String image)
	{
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			Vector<BrandDO> vector = new Vector<BrandDO>();
			try 
			{
				if(mDatabase == null || !mDatabase.isOpen())
					mDatabase = DatabaseHelper.openDataBase();
				
				cursor  = mDatabase.rawQuery("SELECT DISTINCT BRAND FROM tblProducts WHERE Brand = '"+BrandId+"' ORDER BY DisplayOrder ASC", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						BrandDO objCategory 		= new BrandDO();
						objCategory.categoryId 		= BrandId;
						objCategory.brandId 		= cursor.getString(0);
						objCategory.categoryName 	= objCategory.brandId;
						if(image.contains("../"))
							image = image.replace("../", "");
						
						objCategory.image		 	= ServiceURLs.IMAGE_GLOBAL_URL+image;
						
						vector.add(objCategory);
						
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return vector;
		}
	}
	
	public String getBrandCode(String itemcode)
	{
		SQLiteDatabase mDatabase = null;
		Cursor cursor = null;
		synchronized(MyApplication.MyLock) 
		{
			String brand = "";
			try 
			{
				mDatabase = DatabaseHelper.openDataBase();
				cursor  = mDatabase.rawQuery("SELECT Brand FROM tblProducts where itemcode ='"+itemcode+"' ORDER BY DisplayOrder ASC", null);
				
				if(cursor.moveToFirst())
				{
					do
					{
						brand = cursor.getString(0);
					}while(cursor.moveToNext());
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if(cursor != null)
					cursor.close();
				if(mDatabase != null)
					mDatabase.close();
			}
			return brand;
		}
	}
}
