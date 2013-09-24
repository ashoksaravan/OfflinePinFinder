package com.ashoksm.pinfinder.logic;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.ashoksm.pinfinder.to.Office;

public class SAXXMLParser {

	public static List<Office> parse(InputStream is, String stateName) {
		List<Office> offices = null;
		try {
			// create a XMLReader from SAXParser
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			// create a SAXXMLHandler
			SAXXMLHandler saxHandler = new SAXXMLHandler();
			saxHandler.setStateName(getStateName(stateName));
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

	private static String getStateName(String xmlName) {
		String stateName = null;
		if(xmlName.contains("andamanandnicobarislands")) {
			stateName = "Andaman and Nicobar Islands";
		} else if(xmlName.contains("andhrapradesh")) {
			stateName = "Andhra Pradesh";
		} else if(xmlName.contains("arunachalpradesh")) {
			stateName = "Arunachal Pradesh";
		} else if(xmlName.contains("chandigarh")) {
			stateName = "Chandigarh";
		} else if(xmlName.contains("dadraandnagarhaveli")) {
			stateName = "Dadra and Nagar Haveli";
		} else if(xmlName.contains("damananddiu")) {
			stateName = "Daman and Diu";
		} else if(xmlName.contains("delhi")) {
			stateName = "Delhi";
		} else if(xmlName.contains("goa")) {
			stateName = "Goa";
		} else if(xmlName.contains("karnataka")) {
			stateName = "Karnataka";
		}  else if(xmlName.contains("kerala")) {
			stateName = "Kerala";
		} else if(xmlName.contains("lakshadweep")) {
			stateName = "Lakshadweep";
		} else if(xmlName.contains("manipur")) {
			stateName = "Manipur";
		} else if(xmlName.contains("meghalaya")) {
			stateName = "Meghalaya";
		}  else if(xmlName.contains("mizoram")) {
			stateName = "Mizoram";
		} else if(xmlName.contains("nagaland")) {
			stateName = "Nagaland";
		}  else if(xmlName.contains("puducherry")) {
			stateName = "Puducherry";
		} else if(xmlName.contains("sikkim")) {
			stateName = "Sikkim";
		} else if(xmlName.contains("tamilnadu")) {
			stateName = "Tamil Nadu";
		} else if(xmlName.contains("tripura")) {
			stateName = "Tripura";
		} 
		return stateName;
	}
}
