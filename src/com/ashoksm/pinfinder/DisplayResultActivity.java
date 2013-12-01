package com.ashoksm.pinfinder;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.ashoksm.pinfinder.adapter.CustomOfficeAdapter;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;
import com.ashoksm.pinfinder.to.Office;

public class DisplayResultActivity extends SherlockActivity {

	private PinFinderSQLiteHelper sqLiteHelper;

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
				String stateName = intent.getStringExtra(PinFinderMainActivity.EXTRA_STATE).toLowerCase().replaceAll(" ", "");
				String districtName = intent.getStringExtra(PinFinderMainActivity.EXTRA_DISTRICT).toLowerCase().replaceAll(" ", "");;
				String officeName = intent.getStringExtra(PinFinderMainActivity.EXTRA_OFFICE).toLowerCase().replaceAll(" ", "");;
				try {
					sqLiteHelper = new PinFinderSQLiteHelper(getApplicationContext());
					List<Office> matchingOffices = sqLiteHelper.findMatchingOffices(stateName, districtName, officeName);
					sqLiteHelper.closeDB();
					if (matchingOffices.size() > 0) {
						adapter = new CustomOfficeAdapter(DisplayResultActivity.this, matchingOffices);
					}
				} catch (Exception ex) {
					Log.e("Failed to parse the office : ", ex.getMessage());
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
}
