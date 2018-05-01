package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.Vector;

public class SurveyQuestionNewDO implements Serializable
{
	public String SourceSurveyQuestionId = "";
	public String AnswerType="";
	public String ConditionType="";
	public String ConditionValue="";
	public String CurrentQuestionId="";
	public String IsMandatory="";
	public String ParentId="";
	public String QuestionName="";
	public String QuestionShortForm="";
	public String SequenceNumber="";
	public String isActive="";
	public String SurveyCode="";
	public String SurveyQuestionId="";
	public String SurveyQuestionOptionId="0";
	public String Answer="";
	public Vector<String> vecMediaAnswers=new Vector<String>();
	
	
	
	public Vector<OptionsDO> vecOptions=new Vector<OptionsDO>();
	public Vector<UserSurveyAnswerDO> vecUserSurveyAnswers = new Vector<UserSurveyAnswerDO>();
}
