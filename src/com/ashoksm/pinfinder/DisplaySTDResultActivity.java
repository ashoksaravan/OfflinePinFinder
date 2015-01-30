package com.ashoksm.pinfinder;

import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.STDAdapter;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class DisplaySTDResultActivity extends ActivityBase {

	private STDSQLiteHelper sqLiteHelper;

	private Cursor c;

	private String stateName;

	private String cityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);
		Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
		setSupportActionBar(toolbar);

		Locale l = Locale.getDefault();
		// Get the message from the intent
		final Intent intent = getIntent();
		stateName = intent.getStringExtra(STDView.EXTRA_STATE).toLowerCase(l).replaceAll(" ", "").replaceAll("'", "''");
		cityName = intent.getStringExtra(STDView.EXTRA_CITY).toLowerCase(l).replaceAll(" ", "").replaceAll("'", "''");

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
			STDAdapter adapter;

			@Override
			protected void onPreExecute() {
				// SHOW THE SPINNER WHILE LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sqLiteHelper = new STDSQLiteHelper(DisplaySTDResultActivity.this);
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
					adapter = new STDAdapter(DisplaySTDResultActivity.this, c, false);
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
