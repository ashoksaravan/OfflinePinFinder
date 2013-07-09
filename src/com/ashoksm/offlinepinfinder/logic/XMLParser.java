package com.ashoksm.offlinepinfinder.logic;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ashoksm.offlinepinfinder.to.Office;

public class XMLParser {

	public ArrayList<Office> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<Office> offices = null;
		int eventType = parser.getEventType();
		Office office = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				offices = new ArrayList<Office>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("office")) {
					office = new Office();
				} else if (office != null) {
					if (name.equalsIgnoreCase("name")) {
						office.setOfficeName(parser.nextText());
					} else if (name.equalsIgnoreCase("pincode")) {
						office.setPinCode(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("office") && office != null) {
					offices.add(office);
				}
			}
			eventType = parser.next();
		}
		return offices;
	}
}
