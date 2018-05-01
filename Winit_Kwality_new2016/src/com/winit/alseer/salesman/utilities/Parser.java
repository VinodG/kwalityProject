package com.winit.alseer.salesman.utilities;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class Parser {

	public static boolean parse(int fileToParse, Context context, DefaultHandler handler) 
	{
		SAXParser parser = null;
		boolean iscompleted = false;
		try
		{
			parser = SAXParserFactory.newInstance().newSAXParser();
			InputStream is = context.getResources().openRawResource(fileToParse);
			parser.parse(is, handler);
			iscompleted = true;
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
			iscompleted = false;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return iscompleted;

	}
	

}
