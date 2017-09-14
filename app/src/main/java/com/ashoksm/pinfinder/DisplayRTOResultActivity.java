package com.ashoksm.pinfinder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashoksm.pinfinder.adapter.RTORecyclerViewAdapter;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;
import com.clockbyte.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.clockbyte.admobadapter.expressads.NativeExpressAdViewHolder;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.Locale;

public class DisplayRTOResultActivity extends ActivityBase {

    private RTOSQLiteHelper sqLiteHelper;
    private Cursor c;
    private String stateName;
    private String cityName;
    private String action;
    private boolean showFav;
    private SharedPreferences sharedPref;
    private AdmobExpressRecyclerAdapterWrapper adAdapterWrapper;
    private RTORecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_small_native_ad_id));
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);
        sharedPref = getSharedPreferences("AllCodeFinder", Context.MODE_PRIVATE);

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
        Locale l = Locale.getDefault();
        // Get the message from the intent
        final Intent intent = getIntent();
        showFav = intent.getBooleanExtra(MainActivity.EXTRA_SHOW_FAV, false);
        if (!showFav) {
            stateName = intent.getStringExtra(RTOFragment.EXTRA_STATE).toLowerCase(l)
                    .replaceAll(" ", "").replaceAll("'", "''");
            action = intent.getStringExtra(IFSCFragment.EXTRA_ACTION);
            if (action.length() == 0) {
                cityName = intent.getStringExtra(RTOFragment.EXTRA_CITY).toLowerCase(l)
                        .replaceAll(" ", "").replaceAll("'", "''");
            } else {
                cityName = intent.getStringExtra(RTOFragment.EXTRA_CITY).toLowerCase(l)
                        .replaceAll("'", "''");
            }
        }
        // load ad
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
                    sqLiteHelper = new RTOSQLiteHelper(DisplayRTOResultActivity.this);
                    if (showFav) {
                        c = sqLiteHelper.findFavRTOCodes(sharedPref.getString("RTOCodes", null));
                    } else {
                        c = sqLiteHelper.findRTOCodes(stateName, cityName, action);
                    }
                } catch (Exception ex) {
                    Log.e("DisplayRTOResult", ex.getMessage(), ex);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (c != null && c.getCount() > 0) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(c.getCount() + " Results found");
                    }
                    adapter = new RTORecyclerViewAdapter(DisplayRTOResultActivity.this, c,
                            sharedPref, showFav);
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
        if (adAdapterWrapper != null) {
            adAdapterWrapper.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    private void initNativeAd() {
        String[] testDevicesIds = new String[]{AdRequest.DEVICE_ID_EMULATOR};
        adAdapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, getString(R.string
                .admob_small_native_ad_id), testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent,
                                           int viewType) {

                //get ad view
                NativeExpressAdView adView = adViewHolder.getAdView();

                RecyclerView.LayoutParams lp =
                        new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                                RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(DisplayRTOResultActivity.this);
                cardView.setLayoutParams(lp);

                TextView textView = new TextView(DisplayRTOResultActivity.this);
                textView.setLayoutParams(lp);
                textView.setText(R.string.ad_loading);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(getResources().getColor(R.color.accent, getTheme()));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.accent));
                }

                cardView.addView(textView);
                //wrapping
                cardView.addView(adView);
                //return wrapper view
                return cardView;
            }
        };
        adAdapterWrapper.setAdapter((RecyclerView.Adapter) adapter);
        adAdapterWrapper.setLimitOfAds(3);
        adAdapterWrapper.setNoOfDataBetweenAds(10);
        adAdapterWrapper.setFirstAdIndex(2);
    }
}
