package com.winit.alseer.salesman.dataaccesslayer;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.winit.alseer.salesman.databaseaccess.DatabaseHelper;
import com.winit.alseer.salesman.dataobject.OptionsDO;
import com.winit.alseer.salesman.dataobject.SurveyListDO;
import com.winit.alseer.salesman.dataobject.SurveyQuestionNewDO;
import com.winit.alseer.salesman.dataobject.UserSurveyAnswerDO;
import com.winit.alseer.salesman.dataobject.UserSurveyDO;
import com.winit.alseer.salesman.utilities.StringUtils;
import com.winit.sfa.salesman.MyApplication;

public class StaticSurveyDL extends BaseDL{

	private static final String REORDER_ITEMS_TABLE = "tblReorderItems";
	
	public StaticSurveyDL()
	{
		super(REORDER_ITEMS_TABLE);
	}

	
		
	public Vector<SurveyListDO> getAllSurveyListByUserId(String userId)
	{
		Vector<SurveyListDO> vecSurveyListDOs = new Vector<SurveyListDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM tblSurvey SU inner join tblUserSurvey US on  SU.SurveyId=US.SurveyId and US.UserId='"+userId+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						SurveyListDO surveyListDO = new SurveyListDO();
						
						surveyListDO.surveyId =cursor.getString(cursor.getColumnIndex("SurveyId"));
						surveyListDO.surveyTypeCode =cursor.getString(cursor.getColumnIndex("SurveyTypeCode"));
						surveyListDO.surveyName =cursor.getString(cursor.getColumnIndex("SurveyName"));
						surveyListDO.surveyCode =cursor.getString(cursor.getColumnIndex("SurveyCode"));
						surveyListDO.startDate =cursor.getString(cursor.getColumnIndex("StartDate"));
						surveyListDO.endDate =cursor.getString(cursor.getColumnIndex("EndDate"));
						surveyListDO.isConfidential =cursor.getString(cursor.getColumnIndex("IsConfidential"));
						surveyListDO.isActive =cursor.getString(cursor.getColumnIndex("IsActive"));
						surveyListDO.statusCode =cursor.getString(cursor.getColumnIndex("StatusCode"));
						surveyListDO.AssignType =cursor.getString(cursor.getColumnIndex("AssignType"));
						surveyListDO.AssignId =cursor.getString(cursor.getColumnIndex("AssignId"));
						surveyListDO.IsCompleted = cursor.getString(cursor.getColumnIndex("IsCompleted"));
						surveyListDO.description =cursor.getString(cursor.getColumnIndex("Description"));
						surveyListDO.userSurveyCount  = ""+getUserSurveyCount(userId,surveyListDO.surveyId);
						
						
						vecSurveyListDOs.add(surveyListDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return vecSurveyListDOs;
	}
	
	public int getUserSurveyCount(String UserId, String SurveyId)
	{
		int count = 0;
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			
			try 
			{ 
				cursor = getCursor("SELECT count(*) from tblUserSurveyAnswer where UserId='"+UserId+"' and SurveyId='"+SurveyId+"'", null);
				if(cursor.moveToFirst())
				{
					count = StringUtils.getInt(cursor.getString(0));
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return count;
	  }

	//
	public Vector<SurveyQuestionNewDO> getQuestionsBySurveyCode(String surveyCode, String userId, String surveyId,String userSurveyAnswerId, boolean isAnswer)
	{
		Vector<SurveyQuestionNewDO> vecQuestionsDO = new Vector<SurveyQuestionNewDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM tblSurveyQuestion where SurveyCode='"+surveyCode+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						SurveyQuestionNewDO questionsDO = new SurveyQuestionNewDO();
						
						questionsDO.SurveyQuestionId =cursor.getString(cursor.getColumnIndex("SurveyQuestionId"));
						questionsDO.SurveyCode =cursor.getString(cursor.getColumnIndex("SurveyCode"));
						questionsDO.QuestionName =cursor.getString(cursor.getColumnIndex("Question"));
						questionsDO.AnswerType =cursor.getString(cursor.getColumnIndex("AnswerTypeCode"));
						questionsDO.IsMandatory =cursor.getString(cursor.getColumnIndex("IsMandatory"));
						questionsDO.SequenceNumber =cursor.getString(cursor.getColumnIndex("SequenceNumber"));
						questionsDO.ConditionType =cursor.getString(cursor.getColumnIndex("ConditionType"));
						questionsDO.ConditionValue =cursor.getString(cursor.getColumnIndex("ConditionValue"));
						questionsDO.ParentId =cursor.getString(cursor.getColumnIndex("ParentId"));
						questionsDO.QuestionShortForm =cursor.getString(cursor.getColumnIndex("QuestionShortForm"));
						questionsDO.isActive = cursor.getString(cursor.getColumnIndex("isActive"));
						questionsDO.vecOptions = getOptionsByQuestionId(questionsDO.SurveyQuestionId,questionsDO.AnswerType);
						
						if(isAnswer)
						{
							questionsDO.vecUserSurveyAnswers = getUserSurveyAnswerDetails(userId, surveyId,questionsDO.SurveyQuestionId,userSurveyAnswerId);
						}
						
						
						vecQuestionsDO.add(questionsDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return vecQuestionsDO;
	}
	
	//
	public Vector<OptionsDO> getOptionsByQuestionId(String questionId, String answerType)
	{
		Vector<OptionsDO> vecOptions = new Vector<OptionsDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			Cursor innerCursor = null;
			
			try 
			{ 
				cursor = getCursor("SELECT * FROM tblSurveyQuestionOption where SurveyQuestionId='"+questionId+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						OptionsDO optionsDO = new OptionsDO();
						
						optionsDO.SurveyQuestionId =cursor.getString(cursor.getColumnIndex("SurveyQuestionId"));
						optionsDO.SurveyQuestionOptionId =cursor.getString(cursor.getColumnIndex("SurveyQuestionOptionId"));
						optionsDO.OptionName =cursor.getString(cursor.getColumnIndex("OptionName"));
						optionsDO.SequenceNumber =cursor.getString(cursor.getColumnIndex("SequenceNumber"));
						optionsDO.isActive =cursor.getString(cursor.getColumnIndex("isActive"));
						optionsDO.IsEmotion =cursor.getString(cursor.getColumnIndex("IsEmotion"));
						
						if(answerType.equalsIgnoreCase("EMOTION"))
						{
							try 
							{ 
								innerCursor = getCursor("SELECT * FROM tblSurveyQuestionOption SQO inner join tblEmotionImage EI on  SQO .OptionName=EI .EmotionImageId and SQO .SurveyQuestionId='"+questionId+"' and SQO.SurveyQuestionOptionId='"+optionsDO.SurveyQuestionOptionId+"' ", null);
								if(innerCursor.moveToFirst())
								{
									do 
									{
										optionsDO.EmotionName =innerCursor.getString(innerCursor.getColumnIndex("EmotionName"));
										optionsDO.ImagePath =innerCursor.getString(innerCursor.getColumnIndex("ImagePath"));
									}while(innerCursor.moveToNext());
									
								}
								
							}
							catch (Exception e) 
							{
								e.getLocalizedMessage();
							}
							finally
							{
								if(innerCursor != null && !innerCursor.isClosed())
									innerCursor.close();
							}
						}
						vecOptions.add(optionsDO);
					} while (cursor.moveToNext());
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return vecOptions;
	  }
	public String getEmotionName(String questionId, String surveyQuestionOptionId)
	{
		String emotionName="";
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor innerCursor = null;
			
			
			try 
			{ 
				innerCursor = getCursor("SELECT EI .EmotionName FROM tblSurveyQuestionOption SQO inner join tblEmotionImage EI on  SQO .OptionName=EI .EmotionImageId and SQO .SurveyQuestionId='"+questionId+"'  and SQO.SurveyQuestionOptionId='"+surveyQuestionOptionId+"' ", null);
				if(innerCursor.moveToFirst())
				{
					do 
					{
						emotionName =innerCursor.getString(innerCursor.getColumnIndex("EmotionName"));
					}while(innerCursor.moveToNext());
					
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(innerCursor != null && !innerCursor.isClosed())
					innerCursor.close();
			}
		}
		
		return emotionName;
	  }
	
	
	//UserSurveyAnswers
	public Vector<UserSurveyAnswerDO> getUserSurveyAnswers(String userId, String surveyId)
	{
		Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO = new Vector<UserSurveyAnswerDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM tblUserSurveyAnswer where UserId='"+userId+"' and SurveyId='"+surveyId+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						UserSurveyAnswerDO userSurveyAnswerDO = new UserSurveyAnswerDO();
						
						userSurveyAnswerDO.UserSurveyAnswerId =cursor.getString(cursor.getColumnIndex("UserSurveyAnswerId"));
						userSurveyAnswerDO.UserId =cursor.getString(cursor.getColumnIndex("UserId"));
						userSurveyAnswerDO.SurveyId =cursor.getString(cursor.getColumnIndex("SurveyId"));
						userSurveyAnswerDO.IsActive =cursor.getString(cursor.getColumnIndex("IsActive"));
						userSurveyAnswerDO.CreatedBy =cursor.getString(cursor.getColumnIndex("CreatedBy"));
						userSurveyAnswerDO.CreatedOn =cursor.getString(cursor.getColumnIndex("CreatedOn"));
						userSurveyAnswerDO.ModifiedBy =cursor.getString(cursor.getColumnIndex("ModifiedBy"));
						userSurveyAnswerDO.ModifiedOn =cursor.getString(cursor.getColumnIndex("ModifiedOn"));
						userSurveyAnswerDO.FirstName =cursor.getString(cursor.getColumnIndex("FirstName"));
						userSurveyAnswerDO.LastName =cursor.getString(cursor.getColumnIndex("LastName"));
						userSurveyAnswerDO.UploadStatus = cursor.getString(cursor.getColumnIndex("PNR"));
						userSurveyAnswerDO.From = cursor.getString(cursor.getColumnIndex("From"));
						userSurveyAnswerDO.To = cursor.getString(cursor.getColumnIndex("To"));
						userSurveyAnswerDO.Ex1 = cursor.getString(cursor.getColumnIndex("Ex1"));
						userSurveyAnswerDO.Remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
						
						vecUserSurveyAnswerDO.add(userSurveyAnswerDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return vecUserSurveyAnswerDO;
	}
	
	public Vector<UserSurveyAnswerDO> getOfflineUserSurveyAnswers(String userId,String status)
	{
		Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO = new Vector<UserSurveyAnswerDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			try 
			{ 
				openDataBase();
				cursor = getCursor("SELECT * FROM tblUserSurveyAnswer where UserId='"+userId+"' ", null);//		cursor = getCursor("SELECT * FROM tblUserSurveyAnswer where UserId='"+userId+"' and PNR='"+status+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						UserSurveyAnswerDO userSurveyAnswerDO = new UserSurveyAnswerDO();
						
						userSurveyAnswerDO.UserSurveyAnswerId =cursor.getString(cursor.getColumnIndex("UserSurveyAnswerId"));
						userSurveyAnswerDO.UserId =cursor.getString(cursor.getColumnIndex("UserId"));
						userSurveyAnswerDO.SurveyId =cursor.getString(cursor.getColumnIndex("SurveyId"));
						userSurveyAnswerDO.IsActive =cursor.getString(cursor.getColumnIndex("IsActive"));
						userSurveyAnswerDO.CreatedBy =cursor.getString(cursor.getColumnIndex("CreatedBy"));
						userSurveyAnswerDO.CreatedOn =cursor.getString(cursor.getColumnIndex("CreatedOn"));
						userSurveyAnswerDO.ModifiedBy =cursor.getString(cursor.getColumnIndex("ModifiedBy"));
						userSurveyAnswerDO.ModifiedOn =cursor.getString(cursor.getColumnIndex("ModifiedOn"));
						userSurveyAnswerDO.FirstName =cursor.getString(cursor.getColumnIndex("FirstName"));
						userSurveyAnswerDO.LastName =cursor.getString(cursor.getColumnIndex("LastName"));
						userSurveyAnswerDO.UploadStatus = cursor.getString(cursor.getColumnIndex("PNR"));
						userSurveyAnswerDO.From = cursor.getString(cursor.getColumnIndex("From"));
						userSurveyAnswerDO.To = cursor.getString(cursor.getColumnIndex("To"));
						userSurveyAnswerDO.Ex1 = cursor.getString(cursor.getColumnIndex("Ex1"));
						userSurveyAnswerDO.Remarks = cursor.getString(cursor.getColumnIndex("Remarks"));
						userSurveyAnswerDO.SurveyCode = getSurveyCodeBySurveryId(userSurveyAnswerDO.SurveyId);
						
						vecUserSurveyAnswerDO.add(userSurveyAnswerDO);
					} while (cursor.moveToNext());
				}
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
				closeDatabase();
			}
		}
		
		return vecUserSurveyAnswerDO;
	}
	
	public Vector<UserSurveyAnswerDO> getUserSurveyAnswerDetails(String userId, String surveyId,String surveyQuestionId,String userSurveyAnswerId)
	{
		Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO = new Vector<UserSurveyAnswerDO>();
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			Cursor innerCursor = null;
			
			try 
			{ 
				cursor = getCursor("SELECT * FROM tblUserSurveyAnswerDetails SUA inner join tblUserSurveyAnswer  USA on  SUA.UserSurveyAnswerId=USA.UserSurveyAnswerId  AND USA.UserId='"+userId+"' AND USA.SurveyId='"+surveyId+"' AND  SUA.SurveyQuestionId='"+surveyQuestionId+"' AND SUA.UserSurveyAnswerId='"+userSurveyAnswerId+"'  ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						UserSurveyAnswerDO optionsDO = new UserSurveyAnswerDO();
						
						optionsDO.UserSurveyAnswerId =cursor.getString(cursor.getColumnIndex("UserSurveyAnswerId"));
						optionsDO.SurveyQuestionId =cursor.getString(cursor.getColumnIndex("SurveyQuestionId"));
						optionsDO.AnswerType =cursor.getString(cursor.getColumnIndex("AnswerTypeCode"));
						optionsDO.SurveyOptionId =cursor.getString(cursor.getColumnIndex("SurveyOptionId"));
						optionsDO.Answer =cursor.getString(cursor.getColumnIndex("SurveyAnswer"));
						optionsDO.IsActive =cursor.getString(cursor.getColumnIndex("isActive"));
						optionsDO.EmotionName = getEmotionName(surveyQuestionId, optionsDO.SurveyOptionId);
						
						
						vecUserSurveyAnswerDO.add(optionsDO);
					} while (cursor.moveToNext());
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return vecUserSurveyAnswerDO;
	  }
	
	//08-11-2014 Added by the Venkatrao choppa.
	//Add Survey
	public boolean  insertSurvey(Vector<SurveyListDO> vecSurveyListDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurvey WHERE SurveyId= ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSurvey (SurveyId,SurveyTypeCode,SurveyName,SurveyCode,StartDate,EndDate,IsConfidential,IsActive,StatusCode,AssignType,AssignId,IsCompleted,Description) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSurvey SET SurveyTypeCode=?, SurveyName=?, SurveyCode=?, StartDate=?, EndDate=?, IsConfidential=?, IsActive=?, StatusCode=?, AssignType=?, AssignId=?, IsCompleted=?, Description=? WHERE SurveyId = ?");
			
			for(SurveyListDO objSurveyListDO : vecSurveyListDO)
			{
				stmtSelectRec.bindString(1, objSurveyListDO.surveyId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objSurveyListDO != null)
				{
					if (countRec != 0) 
					{
						
						stmtUpdate.bindString(1, objSurveyListDO.surveyTypeCode);
						stmtUpdate.bindString(2, objSurveyListDO.surveyName);
						stmtUpdate.bindString(3, objSurveyListDO.surveyCode);
						stmtUpdate.bindString(4, objSurveyListDO.startDate);
						stmtUpdate.bindString(5, objSurveyListDO.endDate);
						stmtUpdate.bindString(6, objSurveyListDO.isConfidential);
						stmtUpdate.bindString(7, objSurveyListDO.isActive);
						stmtUpdate.bindString(8, objSurveyListDO.statusCode);
						stmtUpdate.bindString(9, objSurveyListDO.AssignType);
						stmtUpdate.bindString(10, objSurveyListDO.AssignId);
						stmtUpdate.bindString(11, objSurveyListDO.IsCompleted);
						stmtUpdate.bindString(12, objSurveyListDO.description);
						stmtUpdate.bindString(13, objSurveyListDO.surveyId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objSurveyListDO.surveyId);
						stmtInsert.bindString(2, objSurveyListDO.surveyTypeCode);
						stmtInsert.bindString(3, objSurveyListDO.surveyName);
						stmtInsert.bindString(4, objSurveyListDO.surveyCode);
						stmtInsert.bindString(5, objSurveyListDO.startDate);
						stmtInsert.bindString(6, objSurveyListDO.endDate);
						stmtInsert.bindString(7, objSurveyListDO.isConfidential);
						stmtInsert.bindString(8, objSurveyListDO.isActive);
						stmtInsert.bindString(9, objSurveyListDO.statusCode);
						stmtInsert.bindString(10, objSurveyListDO.AssignType);
						stmtInsert.bindString(11, objSurveyListDO.AssignId);
						stmtInsert.bindString(12, objSurveyListDO.IsCompleted);
						stmtInsert.bindString(13, objSurveyListDO.description);
						
						
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
	public boolean insertSurveyQuestions(Vector<SurveyQuestionNewDO> vecSurveyQuestionNewDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyQuestion WHERE SurveyQuestionId= ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSurveyQuestion (SurveyQuestionId,SurveyCode,Question,AnswerTypeCode,IsMandatory,SequenceNumber,ConditionType,ConditionValue,ParentId,QuestionShortForm,SourceSurveyQuestionId,isActive) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSurveyQuestion SET SurveyCode=?, Question=?, AnswerTypeCode=?, IsMandatory=?, SequenceNumber=?, ConditionType=?, ConditionValue=?, ParentId=?, QuestionShortForm=?, SourceSurveyQuestionId=?, isActive=? WHERE SurveyQuestionId = ?");
			
			for(SurveyQuestionNewDO objSurveyQuestionNewDO : vecSurveyQuestionNewDO)
			{
				stmtSelectRec.bindString(1, objSurveyQuestionNewDO.SurveyQuestionId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objSurveyQuestionNewDO != null)
				{
					if (countRec != 0) 
						
					{
						
						stmtUpdate.bindString(1, objSurveyQuestionNewDO.SurveyCode);
						stmtUpdate.bindString(2, objSurveyQuestionNewDO.QuestionName);
						stmtUpdate.bindString(3, objSurveyQuestionNewDO.AnswerType);
						stmtUpdate.bindString(4, objSurveyQuestionNewDO.IsMandatory);
						stmtUpdate.bindString(5, objSurveyQuestionNewDO.SequenceNumber);
						stmtUpdate.bindString(6, objSurveyQuestionNewDO.ConditionType);
						stmtUpdate.bindString(7, objSurveyQuestionNewDO.ConditionValue);
						stmtUpdate.bindString(8, objSurveyQuestionNewDO.ParentId);
						stmtUpdate.bindString(9, objSurveyQuestionNewDO.QuestionShortForm);
						stmtUpdate.bindString(10, objSurveyQuestionNewDO.SourceSurveyQuestionId);
						stmtUpdate.bindString(11, objSurveyQuestionNewDO.isActive);
						stmtUpdate.bindString(12, objSurveyQuestionNewDO.SurveyQuestionId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objSurveyQuestionNewDO.SurveyQuestionId);
						stmtInsert.bindString(2, objSurveyQuestionNewDO.SurveyCode);
						stmtInsert.bindString(3, objSurveyQuestionNewDO.QuestionName);
						stmtInsert.bindString(4, objSurveyQuestionNewDO.AnswerType);
						stmtInsert.bindString(5, objSurveyQuestionNewDO.IsMandatory);
						stmtInsert.bindString(6, objSurveyQuestionNewDO.SequenceNumber);
						stmtInsert.bindString(7, objSurveyQuestionNewDO.ConditionType);
						stmtInsert.bindString(8, objSurveyQuestionNewDO.ConditionValue);
						stmtInsert.bindString(9, objSurveyQuestionNewDO.ParentId);
						stmtInsert.bindString(10, objSurveyQuestionNewDO.QuestionShortForm);
						stmtInsert.bindString(11, objSurveyQuestionNewDO.SourceSurveyQuestionId);
						stmtInsert.bindString(12, objSurveyQuestionNewDO.isActive);
						
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
	
//	
	
	
	public boolean insertSurveyQuestionsOptions(Vector<OptionsDO> vecOptionsDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblSurveyQuestionOption WHERE SurveyQuestionOptionId= ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblSurveyQuestionOption (SurveyQuestionOptionId,SurveyQuestionId,OptionName,SequenceNumber,isActive,IsEmotion) VALUES(?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblSurveyQuestionOption SET SurveyQuestionId=?,OptionName=?,SequenceNumber=?,isActive=?,IsEmotion=? WHERE SurveyQuestionOptionId = ?");
			
			for(OptionsDO objOptionsDO : vecOptionsDO)
			{
				stmtSelectRec.bindString(1, objOptionsDO.SurveyQuestionOptionId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objOptionsDO != null)
				{
					if (countRec != 0) 
					{
						
						stmtUpdate.bindString(1, objOptionsDO.SurveyQuestionId);
						stmtUpdate.bindString(2, objOptionsDO.OptionName);
						stmtUpdate.bindString(3, objOptionsDO.SequenceNumber);
						stmtUpdate.bindString(4, objOptionsDO.isActive);
						stmtUpdate.bindString(5, objOptionsDO.IsEmotion);
						stmtUpdate.bindString(6, objOptionsDO.SurveyQuestionOptionId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objOptionsDO.SurveyQuestionOptionId);
						stmtInsert.bindString(2, objOptionsDO.SurveyQuestionId);
						stmtInsert.bindString(3, objOptionsDO.OptionName);
						stmtInsert.bindString(4, objOptionsDO.SequenceNumber);
						stmtInsert.bindString(5, objOptionsDO.isActive);
						stmtInsert.bindString(6, objOptionsDO.IsEmotion);
						
						
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
	
	public boolean insertUserSurvey(Vector<UserSurveyDO> vecUserSurveyDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblUserSurvey WHERE SurveyId= ? and UserId=?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUserSurvey (UserSurveyId,UserId,SurveyId,isActive) VALUES(?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblUserSurvey SET UserSurveyId = ? ,isActive=? WHERE SurveyId=? and UserId=? ");
			
			for(UserSurveyDO objUserSurveyDO : vecUserSurveyDO)
			{
				stmtSelectRec.bindString(1, objUserSurveyDO.SurveyId);
				stmtSelectRec.bindString(2, objUserSurveyDO.UserId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objUserSurveyDO != null)
				{
					if (countRec != 0) 
					{
						
						stmtUpdate.bindString(1, objUserSurveyDO.UserSurveyId);
						stmtUpdate.bindString(2, objUserSurveyDO.isActive);
						stmtUpdate.bindString(3, objUserSurveyDO.SurveyId);
						stmtUpdate.bindString(4, objUserSurveyDO.UserId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objUserSurveyDO.UserSurveyId);
						stmtInsert.bindString(2, objUserSurveyDO.UserId);
						stmtInsert.bindString(3, objUserSurveyDO.SurveyId);
						stmtInsert.bindString(4, objUserSurveyDO.isActive);
						
						
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
	
	
	public boolean insertUserSurveyAnswer(Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtSelectRec = objSqliteDB.compileStatement("SELECT COUNT(*) from tblUserSurveyAnswer WHERE UserSurveyAnswerId= ?");
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUserSurveyAnswer (UserSurveyAnswerId,UserId,SurveyId,IsActive,CreatedBy,CreatedOn,ModifiedBy,ModifiedOn,FirstName,LastName,PNR,Remarks) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement stmtUpdate    = objSqliteDB.compileStatement("UPDATE tblUserSurveyAnswer SET UserId=?,SurveyId=?,IsActive=?,CreatedBy=?,CreatedOn=?,ModifiedBy=?,ModifiedOn=?,FirstName=?,LastName=?,PNR=?,Remarks=? WHERE UserSurveyAnswerId = ?");
			
			for(UserSurveyAnswerDO objUserSurveyAnswerDO : vecUserSurveyAnswerDO)
			{
				stmtSelectRec.bindString(1, objUserSurveyAnswerDO.SurveyQuestionId);
				long countRec = stmtSelectRec.simpleQueryForLong();
				
				if(objUserSurveyAnswerDO != null)
				{
					if (countRec != 0) 
					{
						
						stmtUpdate.bindString(1, objUserSurveyAnswerDO.UserId);
						stmtUpdate.bindString(2, objUserSurveyAnswerDO.SurveyId);
						stmtUpdate.bindString(3, objUserSurveyAnswerDO.IsActive);
						stmtUpdate.bindString(4, objUserSurveyAnswerDO.CreatedBy);
						stmtUpdate.bindString(5, objUserSurveyAnswerDO.CreatedOn);
						stmtUpdate.bindString(6, objUserSurveyAnswerDO.ModifiedBy);
						stmtUpdate.bindString(7, objUserSurveyAnswerDO.ModifiedOn);
						stmtUpdate.bindString(8, objUserSurveyAnswerDO.FirstName);
						stmtUpdate.bindString(9, objUserSurveyAnswerDO.LastName);
						stmtUpdate.bindString(10, objUserSurveyAnswerDO.UploadStatus);
						stmtUpdate.bindString(11, objUserSurveyAnswerDO.Remarks);
						stmtUpdate.bindString(12, objUserSurveyAnswerDO.UserSurveyAnswerId);
						
						stmtUpdate.execute();
					}
					else
					{
						stmtInsert.bindString(1, objUserSurveyAnswerDO.UserSurveyAnswerId);
						stmtInsert.bindString(2, objUserSurveyAnswerDO.UserId);
						stmtInsert.bindString(3, objUserSurveyAnswerDO.SurveyId);
						stmtInsert.bindString(4, objUserSurveyAnswerDO.IsActive);
						stmtInsert.bindString(5, objUserSurveyAnswerDO.CreatedBy);
						stmtInsert.bindString(6, objUserSurveyAnswerDO.CreatedOn);
						stmtInsert.bindString(7, objUserSurveyAnswerDO.ModifiedBy);
						stmtInsert.bindString(8, objUserSurveyAnswerDO.ModifiedOn);
						stmtInsert.bindString(9, objUserSurveyAnswerDO.FirstName);
						stmtInsert.bindString(10, objUserSurveyAnswerDO.LastName);
						stmtInsert.bindString(11, objUserSurveyAnswerDO.UploadStatus);
						stmtInsert.bindString(12, objUserSurveyAnswerDO.Remarks);
						
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
	
	
	public boolean insertUserSurveyAnswerDetails(Vector<UserSurveyAnswerDO> vecUserSurveyAnswerDO)
	{
		SQLiteDatabase objSqliteDB = null;
		try
		{
			 objSqliteDB = DatabaseHelper.openDataBase();
			//Opening the database
				
			//Query to Insert the User information in to UserInfo Table
			SQLiteStatement stmtInsert 	  = objSqliteDB.compileStatement("INSERT INTO tblUserSurveyAnswerDetails (UserSurveyAnswerDetailsId,UserSurveyAnswerId,SurveyQuestionId,AnswerTypeCode,SurveyOptionId,SurveyAnswer,isActive) VALUES(?,?,?,?,?,?,?)");
			
			for(UserSurveyAnswerDO objUserSurveyAnswerDO : vecUserSurveyAnswerDO)
			{
				
						stmtInsert.bindString(1, objUserSurveyAnswerDO.UserSurveyAnswerDetailsDco);
						stmtInsert.bindString(2, objUserSurveyAnswerDO.UserSurveyAnswerId);
						stmtInsert.bindString(3, objUserSurveyAnswerDO.SurveyQuestionId);
						stmtInsert.bindString(4, objUserSurveyAnswerDO.AnswerType);
						stmtInsert.bindString(5, objUserSurveyAnswerDO.SurveyOptionId);
						stmtInsert.bindString(6, objUserSurveyAnswerDO.Answer);
				stmtInsert.bindString(7, "True");//stmtInsert.bindString(7, objUserSurveyAnswerDO.IsActive);
						stmtInsert.executeInsert();
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
			if(objSqliteDB != null)
			{
				
				objSqliteDB.close();
			}
		}
		return true;
	}
	
	public int generateOfflineUserSurveyAnswerId()
	{
		int count = 0;
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			
			try 
			{ 
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query 	= 	"SELECT  UserSurveyAnswerId   FROM tblUserSurveyAnswer    ORDER BY Cast(UserSurveyAnswerId as Int) DESC  ";//"SELECT UserSurveyAnswerId FROM tblUserSurveyAnswer ORDER BY CreatedOn DESC LIMIT 1 ";
				cursor 			= 	sqLiteDatabase.rawQuery(query, null);
				
				if(cursor.moveToFirst())
				{
					count = StringUtils.getInt(cursor.getString(0));
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return count+1;
	  }
	
	public String getSurveyCodeBySurveryId(String surveryId)
	{
		String surveyCode="";
		synchronized (MyApplication.APP_DB_LOCK)
		{
			Cursor cursor = null;
			
			try 
			{ 
				cursor = getCursor("SELECT SurveyCode FROM tblSurvey where  SurveyId='"+surveryId+"' ", null);
				
				if(cursor.moveToFirst())
				{
					do 
					{
						surveyCode =cursor.getString(cursor.getColumnIndex("SurveyCode"));
					} while (cursor.moveToNext());
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return surveyCode;
	  }
	public int getCompletedSurveyCount(String UserId)
	{
		int surveyCount=0;
		synchronized (MyApplication.APP_DB_LOCK)
		{
			SQLiteDatabase sqLiteDatabase 	= null;
			Cursor cursor = null;
			
			try 
			{ 
				
				
				sqLiteDatabase 	= 	DatabaseHelper.openDataBase();
				String query 	= 	"SELECT count(*) from tblSurvey S Inner Join tblUserSurveyAnswer US on S.SurveyId=US.SurveyId and US.UserId='"+UserId+"' ";
				cursor 			= 	sqLiteDatabase.rawQuery(query, null);
				if(cursor.moveToFirst())
				{
					do 
					{
						surveyCount =cursor.getInt(0);
					} while (cursor.moveToNext());
				}
				
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				if(cursor != null && !cursor.isClosed())
					cursor.close();
			}
		}
		
		return surveyCount;
	  }
//	SELECT count(*) from tblSurvey S Inner Join tblUserSurveyAnswer US on S.SurveyId=US.SurveyId
	
	//Update the UserSurveyAnswer with status true
	public void updateUserSurveyAnswer(String status, String userSurveyAnswerId, String userId,String surveyId)
	{
		String TblUserSurveyAnswer = "tblUserSurveyAnswer";
		synchronized (MyApplication.APP_DB_LOCK)
		{
				SQLiteStatement stmtUpdate = null;
				try
				{
					openTransaction();
					String updateQuery 	= "UPDATE " + TblUserSurveyAnswer + " SET PNR = '"+status+"' WHERE UserSurveyAnswerId = '"+userSurveyAnswerId+"' AND UserId = '"+userId+"' AND SurveyId = '"+surveyId+"' ";
					
					stmtUpdate 	= getSqlStatement(updateQuery);
					stmtUpdate.execute();
					setTransaction();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					closeTransaction();
					stmtUpdate.close();
				}
		}
	}
	
	public void deletePickedItemsByid(String id)
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			try 
			{
				openDataBase();
				delete("item_id = "+id, null);
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				closeDatabase();
			}
		}
	}
	
	public void deletePlanogram()
	{
		synchronized (MyApplication.APP_DB_LOCK)
		{
			try 
			{
				openDataBase();
				delete(null, null);
			}
			catch (Exception e) 
			{
				e.getLocalizedMessage();
			}
			finally
			{
				closeDatabase();
			}
		}
	}
}