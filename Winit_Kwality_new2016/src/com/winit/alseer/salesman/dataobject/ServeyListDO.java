package com.winit.alseer.salesman.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class ServeyListDO implements Serializable
{
	public String SurveyId = "";
	public String SurveyTitle = "";
	public String Guidelines = "";
	public String AssignedCount = "";
	public String ImageUrl = "";
	public String VideoUrl = "";
	public String UserAnswerCount = "";
	
	public ArrayList<ServeyQuestionsDO> vecQuestionsDOs;
}
