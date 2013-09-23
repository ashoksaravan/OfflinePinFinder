package com.ashoksm.pinfinder.logic;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Build;
import android.util.Log;

import com.ashoksm.pinfinder.to.Office;

public class SAXXMLHandler extends DefaultHandler {

	private List<Office> offices;
	private StringBuilder sb;
	private Office office;

	public SAXXMLHandler() {
		offices = new ArrayList<Office>();
	}

	public List<Office> getOffices() {
		return offices;
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// reset
		sb = new StringBuilder();
		String corretcName = getCorrectName(qName, localName);
		if (corretcName.equalsIgnoreCase("office")) {
			// create a new instance of employee
			office = new Office();
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (sb!=null) {
	        for (int i=start; i<start+length; i++) {
	        	sb.append(ch[i]);
	        }
	    }
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		String corretcName = getCorrectName(qName, localName);
		String value = sb.toString();
		if (corretcName.equalsIgnoreCase("office")) {
			// add it to the list
			offices.add(office);
		} else if (corretcName.equalsIgnoreCase("name")) {
			office.setOfficeName(value);
		} else if (corretcName.equalsIgnoreCase("pincode")) {
			office.setPinCode(value);
		} else if (corretcName.equalsIgnoreCase("location")) {
			office.setLocation(value);
			try {
				String district = value.substring(value.toLowerCase().indexOf("taluk of ") + 9, value.toLowerCase()
						.indexOf("district"));
				office.setDistrict(district.toLowerCase());
			} catch (Exception ex) {
				Log.e("Failed for the location : ", value);
				Log.e("Unable to fetch the districts : ", office.getOfficeName());
			}
		} else if (corretcName.equalsIgnoreCase("status")) {
			office.setStatus(value);
		} else if (corretcName.equalsIgnoreCase("suboffice")) {
			office.setSuboffice(value);
		} else if (corretcName.equalsIgnoreCase("headoffice")) {
			office.setHeadoffice(value);
		} else if (corretcName.equalsIgnoreCase("telephone")) {
			office.setTelephone(value);
		}
	}

	private static String getCorrectName(String qName, String name) {
		String retValue = null;
		if (Build.VERSION.SDK_INT <= 7) {
			retValue = name;
		} else {
			retValue = qName;
		}
		return retValue;
	}
}
