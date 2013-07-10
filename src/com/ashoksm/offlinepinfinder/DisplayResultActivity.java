package com.ashoksm.offlinepinfinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.ashoksm.offlinepinfinder.adapter.CustomOfficeAdapter;
import com.ashoksm.offlinepinfinder.logic.XMLParser;
import com.ashoksm.offlinepinfinder.to.Office;

public class DisplayResultActivity extends Activity {

	private static final Map<String, ArrayList<Office>> OFFICEHOLDER = new HashMap<String, ArrayList<Office>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);

		// Get the message from the intent
		Intent intent = getIntent();
		String stateName = intent.getStringExtra(PinFinderMainActivity.EXTRA_STATE).toLowerCase().replaceAll(" ", "");
		String districtName = intent.getStringExtra(PinFinderMainActivity.EXTRA_DISTRICT).toLowerCase()
				.replaceAll(" ", "");
		String officeName = intent.getStringExtra(PinFinderMainActivity.EXTRA_OFFICE).toLowerCase();
		String fileName = stateName + "_" + districtName + ".xml";
		try {
			ArrayList<Office> offices = null;
			if (!OFFICEHOLDER.containsKey(fileName)) {
				XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				InputStream in_s = getApplicationContext().getAssets().open(fileName);
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in_s, null);
				XMLParser xmlParser = new XMLParser();
				offices = xmlParser.parseXML(parser);
				OFFICEHOLDER.put(fileName, offices);
			} else {
				offices = OFFICEHOLDER.get(fileName);
			}
			ArrayList<Office> matchingOffices = findMatchingOffices(offices, officeName);
			if (matchingOffices.size() > 0) {
				GridView gridview = (GridView) findViewById(R.id.gridview);

				gridview.setAdapter(new CustomOfficeAdapter(this, matchingOffices));
			} else {
				TextView textView = new TextView(this);
				textView.setTextSize(20);
				textView.setText("No matching records found");

				// Set the text view as the activity layout
				setContentView(textView);
			}
		} catch (XmlPullParserException e) {
			TextView textView = new TextView(this);
			textView.setTextSize(20);
			textView.setText("No matching records found");

			// Set the text view as the activity layout
			setContentView(textView);
		} catch (IOException e) {
			TextView textView = new TextView(this);
			textView.setTextSize(20);
			textView.setText("No matching records found");

			// Set the text view as the activity layout
			setContentView(textView);
		}
	}

	private ArrayList<Office> findMatchingOffices(ArrayList<Office> offices, String officeName) {
		ArrayList<Office> matchingOffices = new ArrayList<Office>();
		for (Office office : offices) {
			String name = office.getOfficeName().toLowerCase();
			if(name.indexOf(officeName) >= 0 || officeName.equals(office.getPinCode())){
				matchingOffices.add(office);
			}
		}
		return matchingOffices;
	}
}
