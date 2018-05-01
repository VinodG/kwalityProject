package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.databaseaccess.DictionaryEntry;
import com.winit.alseer.salesman.dataobject.BlaseUserDco;
import com.winit.alseer.salesman.dataobject.EmployeeDo;
import com.winit.alseer.salesman.dataobject.LoginUserInfo;
import com.winit.alseer.salesman.dataobject.NameIDDo;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.sfa.salesman.MyApplication;

public class UserInfoDA
{
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertLoggedUserInfo(LoginUserInfo objUserInfo)
	{
		//Opening the database
		//Query to get all User's information from the UserInfo table
		 DictionaryEntry[][] data =	DatabaseHelper.get("select * from tblUsers where UserCode = '"+objUserInfo.strUserId+"'");
		 //checking here if already exist 
		 if(data != null)
			 updateUserInfo(objUserInfo);
		 else
			 insertUserInfo(objUserInfo);
	}
	
	/**
	 * Method to Insert the User information in to UserInfo Table
	 * @param LoginUserInfo
	 */
	public void insertUserInfo(LoginUserInfo objUserInfo)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblUsers (UserCode, RoleId, UserName, Description, ParentCode) VALUES(?,?,?,?,?)");
			
			if(objUserInfo!=null)
			{
				stmtInsert.bindString(1,objUserInfo.strUserId);
				stmtInsert.bindString(2, objUserInfo.strRole);
				stmtInsert.bindString(3, objUserInfo.strUserName);
				stmtInsert.bindString(4,objUserInfo.strUserName);
				stmtInsert.bindString(5,objUserInfo.strSalesmanCode);
				stmtInsert.executeInsert();
				
				stmtInsert.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
	}
	/**
	 * method to Update the user's information by UserId
	 * @param objUserInfo
	 */
	public void updateUserInfo(LoginUserInfo objUserInfo)
	{
		if(objUserInfo != null)
		{
			SQLiteDatabase objSqliteDB =null;
			SQLiteStatement stmtUpdate ;
			 try
			 {
				objSqliteDB = DatabaseHelper.openDataBase();
				stmtUpdate = objSqliteDB.compileStatement("Update tblUsers set RoleId=?, UserName=?, Description=?, ParentCode=? where UserCode = '"+objUserInfo.strUserId+"'");
					
			 	stmtUpdate.bindString(1, objUserInfo.strRole);
			 	stmtUpdate.bindString(2, objUserInfo.strUserName);
			 	stmtUpdate.bindString(3,objUserInfo.strUserName);
			 	stmtUpdate.bindString(4,objUserInfo.strSalesmanCode);
			 	
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
				{
					
					objSqliteDB.close();
				}
			 }
		}
	}
	

	/**
	 * Method to get all User's information from the UserInfo table
	 * @return Vector(LoginUserInfo)
	 */
	public Vector<LoginUserInfo> getAllUsersInfo()
	{
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			data 			=	DatabaseHelper.get("select * from tblUsers");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
					objUserInfo.strUserId		  	=	data[i][0].value.toString();
					objUserInfo.strRole				=	data[i][1].value.toString();
					objUserInfo.strUserName			=	data[i][2].value.toString();
					objUserInfo.strUserName			=	data[i][3].value.toString();
					objUserInfo.strSalesmanCode		=	data[i][4].value.toString();
					objUserInfo.strEmpNo			=	data[i][7].value.toString();
					vecUserInfo.add(objUserInfo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecUserInfo;
	}
	
	
	public String getNameByEmpNO(String empNO)
	{
		SQLiteDatabase slDatabase = null;
		Cursor cursor = null;
		String empNo =  "";
		try
		{
			slDatabase 	= 	DatabaseHelper.openDataBase();
			cursor		=	slDatabase.rawQuery("select UserId from tblUsers where EmpNo = '"+empNO+"'", null);
			if(cursor.moveToFirst())
			{
				empNo = cursor.getString(0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return empNo;
	}
	/**
	 * Method to get all User's information from the UserInfo table
	 * @return Vector(LoginUserInfo)
	 */
	
	public Vector<EmployeeDo> getEmployeesNew(String empNo)
	{
		Vector<EmployeeDo> vecEmployee =  new Vector<EmployeeDo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			data 			=	DatabaseHelper.get("select UserId, EmpNo from tblUsers where UserTypeId ='HOUSEHOLD' AND EmpNo != '"+empNo+"'");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					EmployeeDo employeeDo 			= 	new EmployeeDo();
					employeeDo.strEmpName		  	=	data[i][0].value.toString();
					employeeDo.strEmpId				=	data[i][1].value.toString();
					vecEmployee.add(employeeDo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecEmployee;
	}
	
	public EmployeeDo getEmployeesNewByEmpNo(String EmpNo)
	{
		EmployeeDo employeeDo =  new EmployeeDo();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			data 			=	DatabaseHelper.get("select UserName, EmpNo from tblUsers where UserTypeId ='HOUSEHOLD' and EmpNo = '"+EmpNo+",");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					employeeDo 			= 	new EmployeeDo();
					employeeDo.strEmpName		  	=	data[i][0].value.toString();
					employeeDo.strEmpId				=	data[i][1].value.toString();
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return employeeDo;
	}
	
	public String getEmployeesName(String EmpNo)
	{
		String strName = "";
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			data 			=	DatabaseHelper.get("select UserName from tblUsers where UserCode = '"+EmpNo+"'");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					strName	  	=	data[i][0].value.toString();
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return strName;
	}
	
	
	public Vector<EmployeeDo> getEmployees()
	{
		Vector<EmployeeDo> vecEmployee =  new Vector<EmployeeDo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			data 			=	DatabaseHelper.get("select UserName, EmpNo from tblUsers where UserTypeId ='DELIVERY AGENT'");
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					EmployeeDo employeeDo 			= 	new EmployeeDo();
					employeeDo.strEmpName		  	=	data[i][0].value.toString();
					employeeDo.strEmpId				=	data[i][1].value.toString();
					vecEmployee.add(employeeDo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecEmployee;
	}
	
	/**
	 * Method to get all User's information from the UserInfo table
	 * @return Vector(LoginUserInfo)
	 */
	public Vector<LoginUserInfo> getAllDeliveryAgents()
	{
		SQLiteDatabase objSqliteDB = null;
		Cursor cursor = null;
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		try
		{
			//Opening the database
			objSqliteDB = DatabaseHelper.openDataBase();
			
			//Query to get all User's information from the UserInfo table
			cursor	=	objSqliteDB.rawQuery("select UserId,UserTypeId, UserName,EmpNo  from tblUsers where UserTypeId = 'DELIVERY AGENT'", null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
					objUserInfo.strUserId		  	=	cursor.getString(0);
					objUserInfo.strRole				=	cursor.getString(1);
					objUserInfo.strUserName			=	cursor.getString(2);
					objUserInfo.strEmpNo			=	cursor.getString(3);
					vecUserInfo.add(objUserInfo);
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
			if(cursor!=null)
				cursor.close();
			if(objSqliteDB != null)
				objSqliteDB.close();
		}
		return vecUserInfo;
	}
	/**
	 * Method to get all User's information from the UserInfo table
	 * @return Vector(LoginUserInfo)
	 */
	public Vector<LoginUserInfo> getAllDeliveryAgentsUnload()
	{
		SQLiteDatabase objSqliteDB = null;
		Cursor cursor = null;
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		try
		{
			//Opening the database
			objSqliteDB = DatabaseHelper.openDataBase();
			
			//Query to get all User's information from the UserInfo table
			cursor	=	objSqliteDB.rawQuery("select UserId,UserTypeId, UserName,EmpNo  from tblUsers where UserTypeId = 'DELIVERY AGENT'", null);
			
			if(cursor.moveToFirst())
			{
				do
				{
					LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
					objUserInfo.strUserId		  	=	cursor.getString(0);
					objUserInfo.strRole				=	cursor.getString(1);
					objUserInfo.strUserName			=	cursor.getString(2);
					objUserInfo.strEmpNo			=	cursor.getString(3);
					vecUserInfo.add(objUserInfo);
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
			if(objSqliteDB != null)
				objSqliteDB.close();
			if(cursor!=null)
				cursor.close();
		}
		return vecUserInfo;
	}
	
	public LoginUserInfo getValidateUser(String strUserName,String strPassword)
	{
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			//Query to get all User's information from the UserInfo table
			data 			=	DatabaseHelper.get("select * from tblUsers where UserId='"+strUserName+"' AND PASSWORD ='"+strPassword+"'");
			
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					objUserInfo.strUserId		  	=	data[i][0].value.toString();
					objUserInfo.strRole				=	data[i][1].value.toString();
					objUserInfo.strUserName			=	data[i][2].value.toString();
					objUserInfo.strUserName			=	data[i][3].value.toString();
					objUserInfo.strSalesmanCode		=	data[i][4].value.toString();
					vecUserInfo.add(objUserInfo);
				}	
			}
			else
			{
				objUserInfo.strStatus = "Failure";
				objUserInfo.strMessage = "Please enter correct username";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return objUserInfo;
	}
	/**
	 * Method to get User information from the UserInfo table by UserId
	 * @return Vector<LoginUserInfo>
	 */
	public Vector<LoginUserInfo> getAllUsersInfoByUserId(String userID)
	{
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			//Query to get User information from the UserInfo table by UserId
			data 						=	DatabaseHelper.get("select * from tblUsers where UserId ='"+userID+"'");
			
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
					objUserInfo.strUserId		  	=	data[i][0].value.toString();
					objUserInfo.strRole				=	data[i][1].value.toString();
					objUserInfo.strUserName			=	data[i][2].value.toString();
					objUserInfo.strUserName			=	data[i][3].value.toString();
					objUserInfo.strSalesmanCode		=	data[i][4].value.toString();
					vecUserInfo.add(objUserInfo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecUserInfo;
	}
	
	/**
	 * Method to get User information from the UserInfo table by userName
	 * @return Vector<LoginUserInfo>
	 */
	public Vector<LoginUserInfo> getAllUsersInfoByUserUserName(String userName)
	{
		Vector<LoginUserInfo> vecUserInfo =  new Vector<LoginUserInfo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			//Query to get User information from the UserInfo table by userName
			data 						=	DatabaseHelper.get("select * from tblUsers where UserName ='"+userName+"'");
			
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					LoginUserInfo objUserInfo 		= 	new LoginUserInfo();
					objUserInfo.strUserId		  	=	data[i][0].value.toString();
					objUserInfo.strRole				=	data[i][1].value.toString();
					objUserInfo.strUserName			=	data[i][2].value.toString();
					objUserInfo.strUserName			=	data[i][3].value.toString();
					objUserInfo.strSalesmanCode		=	data[i][4].value.toString();
					vecUserInfo.add(objUserInfo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecUserInfo;
	}
	
	public float [] getUserTargetAndAcheive(String userCode)
	{
		synchronized(MyApplication.MyLock)
		{
			float [] target = new float[14];
			SQLiteDatabase objSqliteDB = null;
			Cursor c = null;
			try
			{

//				ThirdPartyTarget" Float DEFAULT (null) ,"AchiveThirdPartyTarget" FLOAT, "AdditionalThirdPartyTarget
				//Opening the database
				objSqliteDB = DatabaseHelper.openDataBase();
				String query = "select Target, AchievedTarget,WorkingDays,TotalWorkingDays,ReqSalesPerDay,FoodTarget,FoodAchivement,FoodReqSalesForDay,AdditionalTarget,AdditionalFoodTarget,ThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget from tblUsers where UserCode='"+userCode+"'";
				c = objSqliteDB.rawQuery(query, null);
				if(c.moveToNext())
				{
					target[0] = c.getFloat(0);//Target
					target[8] = c.getFloat(8);//Additional Ice-cream target
					target[0]=target[0]+target[8];//total Ice-Cream target
					
					target[1] = c.getFloat(1);//AchievedTarget
					
					
					
					
					if(target[0]==0)
						target[0]=target[1]*1.5f;//Target
					
//					if(target[1] > target[0])
//						target[1] = target[0];//AchievedTarget
					
					target[2] = c.getInt(2);//WorkingDays
					target[3] = c.getInt(3)==0?1:c.getInt(3);//TotalWorkingDays
					target[4] = c.getFloat(4);//ReqSalesPerDay
					
					/*************Food***************/
					target[5] = c.getFloat(5);//FoodTarget
					target[9] = c.getFloat(9);//Additional Food target					
					target[5]=target[5]+target[9];//total food target 
					
					
					target[6] = c.getFloat(6);//FoodAchivement
					
					
					if(target[5]==0)
						target[5]=target[6]*1.5f;//FoodTarget
					
//					if(target[6] > target[5])
//						target[6] = target[5];//FoodAchivement
					
					target[7] = c.getFloat(7);//FoodReqSalesForDay


					/*************ThirdPartyBrand***************/
					target[10] = c.getFloat(10);//ThirdPartyTarget
					target[11] = c.getFloat(11);//AchiveThirdPartyTarget
					target[12] = c.getFloat(12);//AdditionalThirdPartyTarget
					target[13] =target[12]+target[10]; //Total target for thirdparty

				}
				
				if(c != null && !c.isClosed())
					c.close();
			}
			catch(Exception e)
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
			return target;
		}
	}
	
	public void updateUserAchievedTarget1(float invoiceAmount,String empNumber){
		synchronized (MyApplication.MyLock) {
			SQLiteDatabase sqLiteDatabase=null;
			try {
//				vinod
				sqLiteDatabase = DatabaseHelper.openDataBase();
				SQLiteStatement stmtUpdate = sqLiteDatabase.compileStatement("update tblUsers set AchievedTarget=Cast((Cast(AchievedTarget as float)+?) as float) where UserCode=?");
				stmtUpdate.bindDouble(1, invoiceAmount);
				stmtUpdate.bindString(2, empNumber);
				stmtUpdate.execute();
				stmtUpdate.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(sqLiteDatabase!=null && sqLiteDatabase.isOpen()){
					sqLiteDatabase.close();
				}
			}
		}
	}
	public boolean insertUser(Vector<BlaseUserDco> vecBlaseUserDcos)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			//Opening the database
			objSqliteDB = DatabaseHelper.openDataBase();
//				vinod
//			ThirdPartyTarget" Float DEFAULT (null) ,"AchiveThirdPartyTarget" FLOAT, "AdditionalThirdPartyTarget
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblUsers WHERE UserCode = ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUsers (UserCode, Password, Description, UserName, Email" +
																		 ", Region,RoleId,ParentCode,IsActive, CreditLimit, PhoneNumber, UserType,Category, " +
																		 "Target, AchievedTarget, WorkingDays,intUserId,TotalWorkingDays,LocationCode, " +
																		 "LocationName , RouteNumber, FirstDiscount,SecondDiscount,ReqSalesPerDay,FoodTarget,FoodAchivement,FoodReqSalesForDay,AdditionalTarget,AdditionalFoodTarget,ThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblUsers SET Password=?, Description=?, UserName=?, Email=?" +
																		 ", Region=?,RoleId=?,ParentCode=?,IsActive=?, CreditLimit=?, PhoneNumber=?, UserType=?,Category=?, " +
																		 "Target=?, AchievedTarget=?, WorkingDays=?,intUserId=?,TotalWorkingDays=?,LocationCode=?,LocationName=?,RouteNumber=?,FirstDiscount=?,SecondDiscount=?,ReqSalesPerDay=?, FoodTarget=?, FoodAchivement=?, FoodReqSalesForDay=?,AdditionalTarget=?,AdditionalFoodTarget=? ,ThirdPartyTarget =? ,AchiveThirdPartyTarget =?,AdditionalThirdPartyTarget =? WHERE UserCode = ?");
			
			SQLiteStatement stmtUpdateReq    = objSqliteDB.compileStatement("UPDATE tblUsers SET Password=?, Description=?, UserName=?, Email=?" +
					 ", Region=?,RoleId=?,ParentCode=?,IsActive=?, CreditLimit=?, PhoneNumber=?, UserType=?,Category=?, " +
					 "Target=?, AchievedTarget=?, WorkingDays=?,intUserId=?,TotalWorkingDays=?,LocationCode=?,LocationName=?,RouteNumber=?,FirstDiscount=?,SecondDiscount=?,ReqSalesPerDay=?,FoodTarget=?,FoodAchivement=?,FoodReqSalesForDay=?,AdditionalTarget=?,AdditionalFoodTarget=?, ThirdPartyTarget=?,AchiveThirdPartyTarget=?,AdditionalThirdPartyTarget =? WHERE UserCode = ?");
			
			for(int i=0;i<vecBlaseUserDcos.size();i++)
			{
				BlaseUserDco objUserInfo = vecBlaseUserDcos.get(i);
				stmtSelectRec.bindString(1, objUserInfo.strUserid);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objUserInfo!=null)
				{
					if (countRec != 0) 
					{
						LogUtils.debug("ReqSalesPerDay", objUserInfo.strUserName);
						if(objUserInfo.ReqSalesPerDay > 0)
						{
							LogUtils.debug("ReqSalesPerDay", ""+objUserInfo.ReqSalesPerDay);
							
							stmtUpdateReq.bindString(1, "password");
							stmtUpdateReq.bindString(2, ""+objUserInfo.description);
							stmtUpdateReq.bindString(3, ""+objUserInfo.strUserName);
							stmtUpdateReq.bindString(4, ""+objUserInfo.email);
							stmtUpdateReq.bindString(5, ""+objUserInfo.strREGION);
							stmtUpdateReq.bindString(6, ""+objUserInfo.strRoleId);
							stmtUpdateReq.bindString(7, ""+objUserInfo.parentCode);
							stmtUpdateReq.bindString(8, ""+objUserInfo.isActive);
							stmtUpdateReq.bindString(9, ""+objUserInfo.creditLimit);
							stmtUpdateReq.bindString(10, ""+objUserInfo.phoneNumber);
							stmtUpdateReq.bindString(11, ""+objUserInfo.strUserType);
							stmtUpdateReq.bindString(12, ""+objUserInfo.category);
							stmtUpdateReq.bindString(13, ""+objUserInfo.Target);
							stmtUpdateReq.bindString(14, ""+objUserInfo.AchievedTarget);
							stmtUpdateReq.bindString(15, ""+objUserInfo.workingDays);
							stmtUpdateReq.bindString(16, ""+objUserInfo.intUserId);
							stmtUpdateReq.bindString(17, ""+objUserInfo.TotalWorkingDays);
							stmtUpdateReq.bindString(18, ""+objUserInfo.LocationCode );
							stmtUpdateReq.bindString(19, ""+objUserInfo.LocationName );
							stmtUpdateReq.bindString(20, ""+objUserInfo.RouteNumber );
							stmtUpdateReq.bindDouble(21, objUserInfo.FirstDiscount);
							stmtUpdateReq.bindDouble(22, objUserInfo.SecondDiscount);
							stmtUpdateReq.bindDouble(23, objUserInfo.ReqSalesPerDay);
							
							stmtUpdateReq.bindDouble(24, objUserInfo.FoodTarget);
							stmtUpdateReq.bindDouble(25, objUserInfo.FoodAchivement);
							stmtUpdateReq.bindDouble(26, objUserInfo.FoodReqSalesForDay);
							stmtUpdateReq.bindDouble(27, objUserInfo.AdditionalTarget);
							stmtUpdateReq.bindDouble(28, objUserInfo.AdditionalFoodTarget);

//							brands ThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget
							stmtUpdateReq.bindDouble(29, objUserInfo.ThirdPartyTarget);
							stmtUpdateReq.bindDouble(30, objUserInfo.AchiveThirdPartyTarget);
							stmtUpdateReq.bindDouble(31, objUserInfo.AdditionalThirdPartyTarget);

							stmtUpdateReq.bindString(32, ""+objUserInfo.strUserid);
							stmtUpdateReq.execute();
						}
						else
						{
							stmtUpdate.bindString(1, "password");
							stmtUpdate.bindString(2, ""+objUserInfo.description);
							stmtUpdate.bindString(3, ""+objUserInfo.strUserName);
							stmtUpdate.bindString(4, ""+objUserInfo.email);
							stmtUpdate.bindString(5, ""+objUserInfo.strREGION);
							stmtUpdate.bindString(6, ""+objUserInfo.strRoleId);
							stmtUpdate.bindString(7, ""+objUserInfo.parentCode);
							stmtUpdate.bindString(8, ""+objUserInfo.isActive);
							stmtUpdate.bindString(9, ""+objUserInfo.creditLimit);
							stmtUpdate.bindString(10, ""+objUserInfo.phoneNumber);
							stmtUpdate.bindString(11, ""+objUserInfo.strUserType);
							stmtUpdate.bindString(12, ""+objUserInfo.category);
							stmtUpdate.bindString(13, ""+objUserInfo.Target);
							stmtUpdate.bindString(14, ""+objUserInfo.AchievedTarget);
							stmtUpdate.bindString(15, ""+objUserInfo.workingDays);
							stmtUpdate.bindString(16, ""+objUserInfo.intUserId);
							stmtUpdate.bindString(17, ""+objUserInfo.TotalWorkingDays);
							stmtUpdate.bindString(18, ""+objUserInfo.LocationCode );
							stmtUpdate.bindString(19, ""+objUserInfo.LocationName );
							stmtUpdate.bindString(20, ""+objUserInfo.RouteNumber );
							stmtUpdate.bindDouble(21, objUserInfo.FirstDiscount);
							stmtUpdate.bindDouble(22, objUserInfo.SecondDiscount);
							
							stmtUpdate.bindDouble(23, objUserInfo.ReqSalesPerDay);
							stmtUpdate.bindDouble(24, objUserInfo.FoodTarget);
							stmtUpdate.bindDouble(25, objUserInfo.FoodAchivement);
							stmtUpdate.bindDouble(26, objUserInfo.FoodReqSalesForDay);
							stmtUpdate.bindDouble(27, objUserInfo.AdditionalTarget);
							stmtUpdate.bindDouble(28, objUserInfo.AdditionalFoodTarget);

//							ThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget
							stmtUpdate.bindDouble(29, objUserInfo.ThirdPartyTarget);
							stmtUpdate.bindDouble(30, objUserInfo.AchiveThirdPartyTarget);
							stmtUpdate.bindDouble(31, objUserInfo.AdditionalThirdPartyTarget);

							stmtUpdate.bindString(32, ""+objUserInfo.strUserid);
							stmtUpdate.execute();
						}
					}
					else
					{
						stmtInsert.bindString(1,""+objUserInfo.strUserid);
						stmtInsert.bindString(2, "password");
						stmtInsert.bindString(3, ""+objUserInfo.description);
						stmtInsert.bindString(4, ""+objUserInfo.strUserName);
						stmtInsert.bindString(5, ""+objUserInfo.email);
						stmtInsert.bindString(6, ""+objUserInfo.strREGION);
						stmtInsert.bindString(7, ""+objUserInfo.strRoleId);
						stmtInsert.bindString(8, ""+objUserInfo.parentCode);
						stmtInsert.bindString(9, ""+objUserInfo.isActive);
						stmtInsert.bindString(10, ""+objUserInfo.creditLimit);
						stmtInsert.bindString(11, ""+objUserInfo.phoneNumber);
						stmtInsert.bindString(12, ""+objUserInfo.strUserType);
						stmtInsert.bindString(13, ""+objUserInfo.category);
						stmtInsert.bindString(14, ""+objUserInfo.Target);
						stmtInsert.bindString(15, ""+objUserInfo.AchievedTarget);
						stmtInsert.bindString(16, ""+objUserInfo.workingDays);
						stmtInsert.bindString(17, ""+objUserInfo.intUserId);
						stmtInsert.bindString(18, ""+objUserInfo.TotalWorkingDays);
						stmtInsert.bindString(19, ""+objUserInfo.LocationCode);
						stmtInsert.bindString(20, ""+objUserInfo.LocationName );
						stmtInsert.bindString(21, ""+objUserInfo.RouteNumber );
						stmtInsert.bindDouble(22, objUserInfo.FirstDiscount);
						stmtInsert.bindDouble(23, objUserInfo.SecondDiscount);
						stmtInsert.bindDouble(24, objUserInfo.ReqSalesPerDay);
						
						stmtInsert.bindDouble(25, objUserInfo.FoodTarget);
						stmtInsert.bindDouble(26, objUserInfo.FoodAchivement);
						stmtInsert.bindDouble(27, objUserInfo.FoodReqSalesForDay);
						stmtInsert.bindDouble(28, objUserInfo.AdditionalTarget);
						stmtInsert.bindDouble(29, objUserInfo.AdditionalFoodTarget);

//						ThirdPartyTarget,AchiveThirdPartyTarget,AdditionalThirdPartyTarget
						stmtInsert.bindDouble(30, objUserInfo.ThirdPartyTarget);
						stmtInsert.bindDouble(31, objUserInfo.AchiveThirdPartyTarget);
						stmtInsert.bindDouble(32, objUserInfo.AdditionalThirdPartyTarget);

						stmtInsert.executeInsert();
					}
				}
			}
			
			if(stmtSelectRec!=null)
				stmtSelectRec.close();
			if(stmtUpdate!=null)
				stmtUpdate.close();
			if(stmtUpdateReq!=null)
				stmtUpdateReq.close();
			if(stmtInsert!=null)
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
	
	
	
	public boolean insertReasons(Vector<BlaseUserDco> vecBlaseUserDcos)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB =null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
					
				//Query to Insert the User information in to UserInfo Table tblSkipReasons
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblReasons WHERE ReasonId = ?");
				SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblReasons (ReasonId, Type, Name) VALUES(?,?,?)");
				SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblReasons SET " +"Type = ?, Name = ?" +" WHERE ReasonId = ?");
				
				for(int i=0;i<vecBlaseUserDcos.size();i++)
				{
					BlaseUserDco objUserInfo = vecBlaseUserDcos.get(i);
					stmtSelectRec.bindString(1, objUserInfo.strUserid);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(objUserInfo!=null)
					{
						if (countRec != 0) 
						{
							stmtUpdate.bindString(1, objUserInfo.strRole);
							stmtUpdate.bindString(2, objUserInfo.strUserName);
							stmtUpdate.bindString(3, objUserInfo.strUserid);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1,objUserInfo.strUserid);
							stmtInsert.bindString(2, objUserInfo.strRole);
							stmtInsert.bindString(3, objUserInfo.strUserName);
							stmtInsert.executeInsert();
						}
					}
				}
				
				if(stmtSelectRec!=null)
					stmtSelectRec.close();
				if(stmtUpdate!=null)
					stmtUpdate.close();
				if(stmtInsert!=null)
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
	}
	
	public boolean insertRegionList(Vector<NameIDDo> vecRegionList)
	{
		synchronized(MyApplication.MyLock)
		{
			SQLiteDatabase objSqliteDB = null;
			try
			{
				objSqliteDB = DatabaseHelper.openDataBase();
				//Opening the database
					
				//Query to Insert the User information in to UserInfo Table
				SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblRegions WHERE LocationID = ?");
				SQLiteStatement stmtInsert = objSqliteDB.compileStatement("INSERT INTO tblRegions (LocationID, LocationName) VALUES(?,?)");
				SQLiteStatement stmtUpdate = objSqliteDB.compileStatement("UPDATE tblRegions SET LocationName = ? WHERE LocationID = ?");
				
				for(int i=0;i<vecRegionList.size();i++)
				{
					NameIDDo nameIDDo = vecRegionList.get(i);
					stmtSelectRec.bindString(1, nameIDDo.strId);
					long countRec = stmtSelectRec.simpleQueryForLong();
					
					if(nameIDDo!=null)
					{
						if (countRec != 0) 
						{
							stmtUpdate.bindString(1, nameIDDo.strName);
							stmtUpdate.bindString(2, nameIDDo.strId);
							stmtUpdate.execute();
						}
						else
						{
							stmtInsert.bindString(1,nameIDDo.strId);
							stmtInsert.bindString(2, nameIDDo.strName);
							stmtInsert.executeInsert();
						}
					}
				}
				
				if(stmtSelectRec!=null)
					stmtSelectRec.close();
				if(stmtUpdate!=null)
					stmtUpdate.close();
				if(stmtInsert!=null)
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
					return true;
				}
			}
			return true;
		}
	}
	
	public Vector<NameIDDo> getRegionListAndCode()
	{
		Vector<NameIDDo> vecRegionList =  new Vector<NameIDDo>();
		try
		{
			//Opening the database
			DictionaryEntry[][] data	=	null;
			//Query to get User information from the UserInfo table by userName
			data 						=	DatabaseHelper.get("select * from tblRegions");
			
			if(data != null && data.length > 0)
			{
				for(int i=0;i<data.length;i++)
				{
					NameIDDo nameIDDo 			= 	new NameIDDo();
					nameIDDo.strId		  		=	data[i][0].value.toString();
					nameIDDo.strName			=	data[i][1].value.toString();
					vecRegionList.add(nameIDDo);
				}	
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return vecRegionList;
	}
	
	
	public String  getUserRole(String Usercode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			String userRole = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT UserType FROM tblUsers WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 userRole = cursor.getString(0);
					
					
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return userRole;
		}
	}
	public String  getUserId(String Usercode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			String userRole = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT intUserId FROM tblUsers WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 userRole = cursor.getString(0);
					
					
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
				 if(objSqliteDB!=null)
					 objSqliteDB.close();
			 }
			 return userRole;
		}
	}
	
	public String  getUserRoleID(String Usercode)
	{
		synchronized(MyApplication.MyLock) 
		{
			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 String userRole = null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT RoleId FROM tblUsers WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 userRole = cursor.getString(0);
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
				 if(objSqliteDB!=null && objSqliteDB.isOpen())
					 objSqliteDB.close();
			 }
			 return userRole;
		}
	}

	public String getUserMobileNumber(String Usercode) {
		synchronized (MyApplication.MyLock) {

			 SQLiteDatabase objSqliteDB = null;
			 Cursor  cursor = null;
			 String phoneNumber=null;
			 try
			 {
				 objSqliteDB = DatabaseHelper.openDataBase();
				 String suery = "SELECT PhoneNumber FROM tblUsers WHERE UserCode = '"+Usercode+"'";
				 
				 cursor =  objSqliteDB.rawQuery(suery, null);
				 while(cursor.moveToNext())
				 {
					 phoneNumber= cursor.getString(0);
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
				 if(objSqliteDB!=null && objSqliteDB.isOpen())
					 objSqliteDB.close();
			 }
			 return phoneNumber;
		}
	}
}
