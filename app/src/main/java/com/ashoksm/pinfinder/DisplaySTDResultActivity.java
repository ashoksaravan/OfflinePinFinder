package com.ashoksm.pinfinder;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.STDRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

public class DisplaySTDResultActivity extends ActivityBase {

    private STDSQLiteHelper sqLiteHelper;

    private Cursor c;

    private String stateName;

    private String cityName;

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
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!scrollDown) {
                        toolbar.animate().translationY(0).alpha(1).setDuration(300)
                                .setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {

                            @Override
                            public void run() {
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().show();
                                }
                            }
                        });
                    } else {
                        toolbar.animate().translationY(-toolbar.getBottom()).alpha(0).setDuration(300)
                                .setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {

                            @Override
                            public void run() {
                                if (getSupportActionBar() != null) {
                                    getSupportActionBar().hide();
                                }
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
            STDRecyclerViewAdapter adapter;

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                linlaHeaderProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sqLiteHelper = new STDSQLiteHelper(DisplaySTDResultActivity.this);
                    c = sqLiteHelper.findRTOCodes(stateName, cityName);
                    // sqLiteHelper.closeDB();
                } catch (Exception ex) {
                    Log.e(this.getClass().getName(), ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (c != null && c.getCount() > 0) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(c.getCount() + " Results found");
                    }
                    adapter = new STDRecyclerViewAdapter(DisplaySTDResultActivity.this, c);
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
