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

import com.ashoksm.pinfinder.adapter.BankBranchAdapter;
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DisplayBankBranchResultActivity extends ActionBarActivity {

	private BankBranchSQLiteHelper sqLiteHelper;

	private Cursor c;

	private String stateName;

	private String districtName;

	private String bankName;

	private String branchName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.activity_display_result);

		Locale l = Locale.getDefault();
		// Get the message from the intent
		final Intent intent = getIntent();
		stateName = intent.getStringExtra(BankView.EXTRA_STATE).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");
		districtName = intent.getStringExtra(BankView.EXTRA_DISTRICT).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");
		bankName = intent.getStringExtra(BankView.EXTRA_BANK).toLowerCase(l).replaceAll(" ", "").replaceAll("'", "''");
		branchName = intent.getStringExtra(BankView.EXTRA_BRANCH).toLowerCase(l).replaceAll(" ", "")
				.replaceAll("'", "''");

		// load ad
		AdView adView = (AdView) this.findViewById(R.id.ad_1);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		new AsyncTask<Void, Void, Void>() {
			LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
			BankBranchAdapter adapter;

			@Override
			protected void onPreExecute() {
				// SHOW THE SPINNER WHILE LOADING FEEDS
				linlaHeaderProgress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sqLiteHelper = new BankBranchSQLiteHelper(DisplayBankBranchResultActivity.this);
					c = sqLiteHelper.findIfscCodes(stateName, districtName, bankName, branchName);
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
					adapter = new BankBranchAdapter(DisplayBankBranchResultActivity.this, c, false,
							intent.getStringExtra(BankView.EXTRA_BANK));
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
