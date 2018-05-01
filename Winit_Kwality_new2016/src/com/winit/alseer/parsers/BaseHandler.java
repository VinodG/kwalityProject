package com.winit.alseer.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.winit.alseer.salesman.common.Preference;
import com.winit.alseer.salesman.utilities.LogUtils;
import com.winit.alseer.salesman.utilities.StringUtils;

public class BaseHandler extends DefaultHandler
{
	public StringBuilder currentValue;
	public boolean currentElement = false;
	
	public final static String apostrophe = "'";
	public final static String sep = ",";
	public Context context;
	public Preference preference;
	
	public BaseHandler(Context context)
	{
		this.context = context;
		preference = new Preference(context);
	}
	public BaseHandler(InputStream is)
	{
	}
	/**
	 * Method to convert StringBuffer to String.
	 * @param sb
	 * @return String
	 */
	public String sb2String(StringBuffer sb)
	{
		if(sb == null)
			return "";
		try
		{
			return sb.toString();
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2String exception:"+e.getMessage() );
		}
		return null;
	}
	
	/**
	 * Method to convert StringBuffer to Long.
	 * @param sb
	 * @return long
	 */
	public long sb2Long(StringBuffer sb)
	{
		if(sb == null)
			return 0;
		try
		{
			return Long.parseLong(sb.toString());
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2Long exception:"+e.getMessage() );
		}
		return 0;
	}
	
	/**
	 * Method to convert StringBuffer to double.
	 * @param sb
	 * @return double
	 */
	public double sb2Double(StringBuffer sb)
	{
		if(sb == null)
			return 0;
		try
		{
			return Double.parseDouble(sb.toString());
		}
		catch(Exception e)
		{
	   		LogUtils.errorLog(this.getClass().getName(), "sb2Long exception:"+e.getMessage() );
		}
		return 0;
	}
	
	/**
	 * Method to convert StringBuffer to boolean.
	 * @param sb
	 * @return boolean
	 */
	public boolean sb2Boolean(StringBuffer sb)
	{
		boolean result = false;
		
		if(sb == null)
			return result;
		
		if (sb.length() > 0)
		{
			try
			{
				result = sb.toString().equalsIgnoreCase("true");
			}
			catch(Exception e)
			{
		   		LogUtils.errorLog(this.getClass().getName(), "sb2Boolean exception:"+e.getMessage() );
			}
			
		}
		return result;
	}
	
	/**
	 * Method to convert StringBuffer to int.
	 * @param sb
	 * @return int
	 */
	public int sb2Int(StringBuffer sb)
	{
		if (sb==null) 
			return 0;
		
		return string2Int(sb.toString());
	}
	
	/**
	 * Method to convert String to Double.
	 * @param string
	 * @return Double
	 */
	public double string2Double(String string)
	{
		double result = 0;
		if (string != null && string.length() > 0)
		{
			try
			{
				result = StringUtils.getDouble(string);
			}
			catch(Exception e)
			{
		   		LogUtils.errorLog(this.getClass().getName(), "string2Dobule exception:"+e.getMessage() );
			}
		}
		return result;
	}
	
	/**
	 * Method to convert String to int.
	 * @param string
	 * @return int
	 */
	public int string2Int(String string)
	{
		int result = 0;
		if (string != null && string.length() > 0)
		{
			try
			{
				result = StringUtils.getInt(string);
			}
			catch(Exception e)
			{
		   		LogUtils.errorLog(this.getClass().getName(), "string2Int exception:"+e.getMessage() );
			}
		}
		return result;
	}
	
	protected String getStringFromInputStream(InputStream inputStream)
	{
		if(inputStream != null)
		{
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(inputStream));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();
		}
		else
		{
			return "";
		}
	}
}
