package com.winit.alseer.salesman.dataobject;

import java.util.ArrayList;
import java.util.Vector;

public class SurveyListDO extends BaseObject 
{
	public String createdBy				= "";
	public String createdOn				= "";
	public String description			= "";
	public String endDate				= "";
	public String isActive				= "";
	public String isConfidential		= "";
	public String modifiedBy			= "";
	public String modifiedOn			= "";
	public String startDate				= "";
	public String statusCode			= "";
	public String surveyCode			= "";
	public String surveyId				= "";
	public String surveyName			= "";
	public String surveyTypeCode		= "";
	public String userSurveyCount		= "";
	
	public String AssignType 			= "";
	public String AssignId 			= "";
	public String IsCompleted 			= "";
	public String Status 			= "";
	public Vector<SurveyQuestionNewDO> vecQuestions=new Vector<SurveyQuestionNewDO>();
	public ArrayList<String> surveyQuestionList	= new ArrayList<String>();
}
