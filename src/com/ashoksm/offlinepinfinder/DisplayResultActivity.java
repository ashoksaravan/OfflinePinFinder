package com.ashoksm.offlinepinfinder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ashoksm.offlinepinfinder.adapter.CustomOfficeAdapter;
import com.ashoksm.offlinepinfinder.logic.XMLParser;
import com.ashoksm.offlinepinfinder.to.Office;

public class DisplayResultActivity extends Activity {

	private static final Map<String, ArrayList<Office>> OFFICEHOLDER = new HashMap<String, ArrayList<Office>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.activity_display_result);

		new AsyncTask<Void, Void, Void>() {
			LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
			CustomOfficeAdapter adapter;

			@Override
			protected void onPreExecute() {
				// SHOW THE SPINNER WHILE LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				// Get the message from the intent
				Intent intent = getIntent();
				String stateName = intent.getStringExtra(PinFinderMainActivity.EXTRA_STATE).toLowerCase()
						.replaceAll(" ", "");
				String districtName = intent.getStringExtra(PinFinderMainActivity.EXTRA_DISTRICT).toLowerCase();
				String officeName = intent.getStringExtra(PinFinderMainActivity.EXTRA_OFFICE).toLowerCase();
				String fileName = stateName + ".xml";
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
					ArrayList<Office> matchingOffices = findMatchingOffices(offices, officeName, districtName);
					if (matchingOffices.size() > 0) {
						adapter = new CustomOfficeAdapter(DisplayResultActivity.this, matchingOffices);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (adapter != null) {
					GridView gridview = (GridView) findViewById(R.id.gridview);
					gridview.setAdapter(adapter);
				} else {
					LinearLayout noMatchingLayout = (LinearLayout) findViewById(R.id.noMatchingLayout);
					noMatchingLayout.setVisibility(View.VISIBLE);
				}
				// HIDE THE SPINNER AFTER LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.GONE);
			}

		}.execute();

		setProgressBarIndeterminateVisibility(false);
	}

	private ArrayList<Office> findMatchingOffices(ArrayList<Office> offices, String officeName, String districtName) {
		ArrayList<Office> matchingOffices = new ArrayList<Office>();
		for (Office office : offices) {
			if (office.getDistrict().indexOf(districtName) >= 0
					&& (office.getOfficeName().toLowerCase().indexOf(officeName) >= 0 || officeName.equals(office
							.getPinCode()))) {
				matchingOffices.add(office);
			}
		}
		return matchingOffices;
	}
}
