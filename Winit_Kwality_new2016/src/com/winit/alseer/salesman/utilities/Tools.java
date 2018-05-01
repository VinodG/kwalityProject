package com.winit.alseer.salesman.utilities;

public class Tools 
{
	public static int str2Int(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static double str2Double(String s)
	{
		try
		{
			return Double.parseDouble(s);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static float str2Float(String s)
	{
		try
		{
			return Float.parseFloat(s);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	
	
}
