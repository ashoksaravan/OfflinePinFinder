package com.ashoksm.offlinepinfinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;
import android.widget.TextView;

import com.ashoksm.offlinepinfinder.adapter.CustomOfficeAdapter;
import com.ashoksm.offlinepinfinder.logic.XMLParser;
import com.ashoksm.offlinepinfinder.to.Office;

public class DisplayResultActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);
		

		// Get the message from the intent
		Intent intent = getIntent();
		String stateName = intent.getStringExtra(PinFinderMainActivity.EXTRA_STATE).toLowerCase().replaceAll(" ", "");
		String districtName = intent.getStringExtra(PinFinderMainActivity.EXTRA_DISTRICT).toLowerCase().replaceAll(" ", "");
		String officeName = intent.getStringExtra(PinFinderMainActivity.EXTRA_OFFICE);
		String fileName = stateName+"_"+districtName+".xml";
		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			InputStream in_s = getApplicationContext().getAssets().open(fileName);
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);
			XMLParser xmlParser = new XMLParser();
			ArrayList<Office> offices = xmlParser.parseXML(parser);
			ArrayList<Office> matchingOffices = findMatchingOffices(offices,officeName);
			if(matchingOffices.size() > 0) {
			GridView gridview = (GridView) findViewById(R.id.gridview);

			gridview.setAdapter(new CustomOfficeAdapter(this, matchingOffices));
			} else {
				TextView textView = new TextView(this);
			    textView.setTextSize(10);
			    textView.setText("No matching records found");

			    // Set the text view as the activity layout
			    setContentView(textView);
			}
		} catch (XmlPullParserException e) {
			TextView textView = new TextView(this);
			 textView.setTextSize(10);
			 textView.setText("No matching records found");

		    // Set the text view as the activity layout
		    setContentView(textView);
		} catch (IOException e) {
			TextView textView = new TextView(this);
			 textView.setTextSize(10);
			 textView.setText("No matching records found");

		    // Set the text view as the activity layout
		    setContentView(textView);
		}
	}

	private ArrayList<Office> findMatchingOffices(ArrayList<Office> offices, String officeName) {
		ArrayList<Office> matchingOffices = new ArrayList<Office>();
		for (Office office : offices) {
			if(officeName.equalsIgnoreCase(office.getOfficeName())){
				matchingOffices.add(office);
			}
		}
		return matchingOffices;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_result, menu);
		return true;
	}

}
