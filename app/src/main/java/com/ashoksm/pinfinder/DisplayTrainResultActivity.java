package com.ashoksm.pinfinder;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.TrainRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.clockbyte.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class DisplayTrainResultActivity extends ActivityBase {

    private RailWaysSQLiteHelper sqLiteHelper;
    private Cursor c;
    private String trainNo;
    private String start;
    private String ends;
    private AdmobExpressRecyclerAdapterWrapper adAdapterWrapper;
    private TrainRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_small_native_ad_id));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // load ad
        loadAd();

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.gridView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // set item decorator
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.item_divider_big);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // add check to avoid toolbar animation for the devices before JELLY_BEAN
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mRecyclerView.addOnScrollListener(new HidingScrollListener(this) {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onMoved(int distance) {
                    toolbar.setTranslationY(-distance);
                }
            });
        }
        // Get the message from the intent
        final Intent intent = getIntent();
        start = intent.getStringExtra(TrainsFragment.EXTRA_STARTS).replaceAll("'", "''");
        ends = intent.getStringExtra(TrainsFragment.EXTRA_ENDS).replaceAll("'", "''");
        trainNo = intent.getStringExtra(TrainsFragment.EXTRA_TRAIN).replaceAll("'", "''");

        new AsyncTask<Void, Void, Void>() {
            LinearLayout progressLayout = (LinearLayout) findViewById(R.id.progressLayout);

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sqLiteHelper = new RailWaysSQLiteHelper(DisplayTrainResultActivity.this);
                    if (trainNo.trim().length() > 0) {
                        c = sqLiteHelper.findTrainsByNoOrName(trainNo);
                    } else {
                        c = sqLiteHelper.findTrainsByStation(start, ends);
                    }
                } catch (Exception ex) {
                    Log.e("TrainResultActivity", ex.getMessage(), ex);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (c != null && c.getCount() > 0) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(c.getCount() + " Results found");
                    }
                    adapter = new TrainRecyclerViewAdapter(DisplayTrainResultActivity.this, c);
                    initNativeAd();
                    mRecyclerView.setAdapter(adAdapterWrapper);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout noMatchingLt = (LinearLayout) findViewById(R.id.noMatchingLayout);
                    noMatchingLt.setVisibility(View.VISIBLE);
                }
                // HIDE THE SPINNER AFTER LOADING FEEDS
                progressLayout.setVisibility(View.GONE);
            }

        }.execute();
        AppRater.appLaunched(this);
    }

    private void loadAd() {
        final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.adLayout);
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

    @SuppressWarnings("unchecked")
    private void initNativeAd() {
        String[] testDevicesIds = new String[]{AdRequest.DEVICE_ID_EMULATOR};
        adAdapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, testDevicesIds);
        adAdapterWrapper.setAdapter((RecyclerView.Adapter) adapter);
        adAdapterWrapper.setLimitOfAds(3);
        adAdapterWrapper.setNoOfDataBetweenAds(10);
        adAdapterWrapper.setFirstAdIndex(2);
    }
}
