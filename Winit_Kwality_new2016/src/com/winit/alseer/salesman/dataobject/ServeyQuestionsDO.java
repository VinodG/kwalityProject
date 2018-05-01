package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class ServeyQuestionsDO implements Serializable
{
	public String SurveyQuestionId = "";
	public String SurveyId = "";
	public String Question = "";
	public String AnswerType = "";
	
	public String strAnswer  = "";
	public String strImageUrs  = "";
	public String strVideoUrl  = "";
	
	public ArrayList<ServeyOptionsDO> vecOptionsDOs;
}
