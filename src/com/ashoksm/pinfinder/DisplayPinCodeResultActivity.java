package com.ashoksm.pinfinder;

import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.PinCodeRecyclerViewAdapter;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class DisplayPinCodeResultActivity extends ActionBarActivity {

	private PinFinderSQLiteHelper sqLiteHelper;

	private Cursor c;

	private String stateName;

	private String districtName;

	private String officeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);
		Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
		setSupportActionBar(toolbar);
		
		final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.gridview);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		Locale l = Locale.getDefault();
		// Get the message from the intent
		Intent intent = getIntent();
		stateName = intent.getStringExtra(PinCodeView.EXTRA_STATE).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");
		districtName = intent.getStringExtra(PinCodeView.EXTRA_DISTRICT).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");
		officeName = intent.getStringExtra(PinCodeView.EXTRA_OFFICE).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");

		// load ad
		final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.ad_1);
		final AdView ad = new AdView(this);
		ad.setAdUnitId(getString(R.string.admob_id));
		ad.setAdSize(AdSize.SMART_BANNER);

		final AdListener listener = new AdListener() {
			@Override
			public void onAdLoaded() {
				adParent.setVisibility(View.VISIBLE);
				super.onAdLoaded();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				adParent.setVisibility(View.GONE);
				super.onAdFailedToLoad(errorCode);
			}
		};

		ad.setAdListener(listener);

		adParent.addView(ad);
		AdRequest adRequest = new AdRequest.Builder().build();
		ad.loadAd(adRequest);

		new AsyncTask<Void, Void, Void>() {
			LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
			PinCodeRecyclerViewAdapter adapter;

			@Override
			protected void onPreExecute() {
				// SHOW THE SPINNER WHILE LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sqLiteHelper = new PinFinderSQLiteHelper(DisplayPinCodeResultActivity.this);
					c = sqLiteHelper.findMatchingOffices(stateName, districtName, officeName);
					// sqLiteHelper.closeDB();
				} catch (Exception ex) {
					Log.e("Failed to parse the office : ", ex.getMessage());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (c.getCount() > 0) {
					adapter = new PinCodeRecyclerViewAdapter(DisplayPinCodeResultActivity.this, c);
					getSupportActionBar().setTitle(c.getCount() + " Results found");
					mRecyclerView.setAdapter(adapter);
					mRecyclerView.setVisibility(View.VISIBLE);
				} else {
					LinearLayout noMatchingLayout = (LinearLayout) findViewById(R.id.noMatchingLayout);
					noMatchingLayout.setVisibility(View.VISIBLE);
				}
				// HIDE THE SPINNER AFTER LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.GONE);
			}

		}.execute();
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
