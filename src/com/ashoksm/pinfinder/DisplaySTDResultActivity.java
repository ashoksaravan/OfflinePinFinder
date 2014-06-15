package com.ashoksm.pinfinder;

import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.STDAdapter;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;

public class DisplaySTDResultActivity extends ActionBarActivity {

	private STDSQLiteHelper sqLiteHelper;

	private Cursor c;

	private String stateName;

	private String cityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.activity_display_result);

		Locale l = Locale.getDefault();
		// Get the message from the intent
		final Intent intent = getIntent();
		stateName = intent.getStringExtra(STDView.EXTRA_STATE).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");
		cityName = intent.getStringExtra(STDView.EXTRA_CITY).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");

		new AsyncTask<Void, Void, Void>() {
			LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
			STDAdapter adapter;

			@Override
			protected void onPreExecute() {
				// SHOW THE SPINNER WHILE LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sqLiteHelper = new STDSQLiteHelper(getApplicationContext());
					c = sqLiteHelper.findIfscCodes(stateName, cityName);
					// sqLiteHelper.closeDB();
				} catch (Exception ex) {
					Log.e("Failed to parse the office : ", ex.getMessage());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				getSupportActionBar().setTitle(intent.getStringExtra(BankView.EXTRA_BANK));
				if (c != null && c.getCount() > 0) {
					getSupportActionBar().setTitle(c.getCount() + " Results found");
					adapter = new STDAdapter(getApplicationContext(), c, false);
					GridView gridview = (GridView) findViewById(R.id.gridview);
					gridview.setVisibility(View.VISIBLE);
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

	@Override
	public void onBackPressed() {
		if (sqLiteHelper != null) {
			sqLiteHelper.closeDB();
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		if (sqLiteHelper != null) {
			sqLiteHelper.closeDB();
		}
		super.onDestroy();
	}
}
