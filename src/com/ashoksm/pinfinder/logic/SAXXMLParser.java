package com.ashoksm.pinfinder.logic;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.ashoksm.pinfinder.to.Office;

public class SAXXMLParser {

	public static List<Office> parse(InputStream is) {
		List<Office> offices = null;
		try {
			// create a XMLReader from SAXParser
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			// create a SAXXMLHandler
			SAXXMLHandler saxHandler = new SAXXMLHandler();
			// store handler in XMLReader
			xmlReader.setContentHandler(saxHandler);
			// the process starts
			xmlReader.parse(new InputSource(is));
			// get the `Employee list`
			offices = saxHandler.getOffices();

		} catch (Exception ex) {
			Log.e("SAXXMLParser: parse() failed", ex.getMessage());
		}

		// return Employee list
		return offices;
	}
}
