package com.ashoksm.pinfinder;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ashoksm.pinfinder.adapter.StationRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.AdService;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class DisplayStationResultActivity extends ActivityBase {

    private static WeakReference<RailWaysSQLiteHelper> sqLiteHelperWeakReference;
    private static String station;
    private static String stateName;
    private static String cityName;
    private static String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        final Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);

        // load ad
        AdService.loadBannerAd(this);

        final RecyclerView mRecyclerView = findViewById(R.id.gridView);

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
        action = intent.getStringExtra(IFSCFragment.EXTRA_ACTION);
        stateName = intent.getStringExtra(StationsFragment.EXTRA_STATE).toLowerCase(l)
                .replaceAll(" ", "").replaceAll("'", "''");
        cityName = intent.getStringExtra(StationsFragment.EXTRA_CITY).toLowerCase(l)
                .replaceAll(" ", "").replaceAll("'", "''");
        if (action != null) {
            station = intent.getStringExtra(StationsFragment.EXTRA_STATION).toLowerCase();
        } else {
            station = intent.getStringExtra(StationsFragment.EXTRA_STATION).toLowerCase(l)
                    .replaceAll(" ", "").replaceAll("'", "''");
        }

        new MyAsyncTask(this).execute();
        AppRater.appLaunched(this);
    }

    @Override
    public void onBackPressed() {
        if (sqLiteHelperWeakReference.get() != null) {
            sqLiteHelperWeakReference.get().closeDB();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, 0);
    }

    @Override
    protected void onDestroy() {
        if (sqLiteHelperWeakReference.get() != null) {
            sqLiteHelperWeakReference.get().closeDB();
        }
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_left, 0);
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

    private static class MyAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private WeakReference<AppCompatActivity> activity;

        MyAsyncTask(AppCompatActivity activityIn) {
            activity = new WeakReference<>(activityIn);
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor c = null;
            try {
                RailWaysSQLiteHelper sqLiteHelper = new RailWaysSQLiteHelper(activity.get());
                c = sqLiteHelper.findStations(station, stateName, cityName, action);
                sqLiteHelperWeakReference = new WeakReference<>(sqLiteHelper);
            } catch (Exception ex) {
                Log.e("DisplayStationActivity", ex.getMessage(), ex);
            }
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            ActionBar actionBar = activity.get().getSupportActionBar();
            if (c != null && c.getCount() > 0) {
                if (actionBar != null) {
                    actionBar.setTitle(c.getCount() + " Results found");
                }
                StationRecyclerViewAdapter adapter =
                        new StationRecyclerViewAdapter(activity.get(), c);
                RecyclerView mRecyclerView = activity.get().findViewById(R.id.gridView);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                activity.get().findViewById(R.id.noMatchingLayout).setVisibility(View.VISIBLE);
            }
            // HIDE THE SPINNER AFTER LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.GONE);
        }

    }
}
