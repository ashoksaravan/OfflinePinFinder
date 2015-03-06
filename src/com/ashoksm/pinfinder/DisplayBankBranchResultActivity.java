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
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.IFSCRecyclerViewAdapter;
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class DisplayBankBranchResultActivity extends ActionBarActivity {

	private BankBranchSQLiteHelper sqLiteHelper;

	private Cursor c;

	private String stateName;

	private String districtName;

	private String bankName;

	private String branchName;

	private static boolean scrollDown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
		setSupportActionBar(toolbar);

		final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.gridview);

		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);

		// use a linear layout manager
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		// add check to avoid toolbar animation for the devices before JELLY_BEAN
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					super.onScrollStateChanged(recyclerView, newState);
					if (!scrollDown) {
						toolbar.animate().translationY(0).alpha(1).setDuration(300)
						.setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {

							@Override
							public void run() {
								getSupportActionBar().show();
							}
						});
					} else {
						toolbar.animate().translationY(-toolbar.getBottom()).alpha(0).setDuration(300)
						.setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {

							@Override
							public void run() {
								getSupportActionBar().hide();
							}
						});
					}
				}

				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					super.onScrolled(recyclerView, dx, dy);
					if (dy > 1) {
						// scroll down
						scrollDown = true;

					} else if (dy < -1) {
						// scroll up
						scrollDown = false;
					}
				}
			});
		}
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
			IFSCRecyclerViewAdapter adapter;

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
					adapter = new IFSCRecyclerViewAdapter(DisplayBankBranchResultActivity.this, c,
							intent.getStringExtra(BankView.EXTRA_BANK));
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
		overridePendingTransition(R.anim.slide_in_left, 0);
	}

	@Override
	protected void onDestroy() {
		if (sqLiteHelper != null) {
			sqLiteHelper.closeDB();
		}
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_left, 0);
	}
}
