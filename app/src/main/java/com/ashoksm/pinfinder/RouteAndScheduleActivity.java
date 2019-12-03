package com.ashoksm.pinfinder;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ashoksm.pinfinder.adapter.RouteAndScheduleAdapter;
import com.ashoksm.pinfinder.common.AdService;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RouteAndScheduleActivity extends ActivityBase {

    private static WeakReference<RailWaysSQLiteHelper> sqLiteHelperWeakReference;
    private static String trainNo;
    private static boolean xLargeScreen;

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
        Drawable dividerDrawable;
        xLargeScreen = isXLargeScreen();
        if (xLargeScreen) {
            dividerDrawable = ContextCompat.getDrawable(this, R.drawable.item_divider);
        } else {
            dividerDrawable = ContextCompat.getDrawable(this, R.drawable.item_divider_big);
        }
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
        trainNo = getIntent().getStringExtra(TrainsFragment.EXTRA_TRAIN);

        new MyAsyncTask(this).execute();
        if (!xLargeScreen) {
            Toast.makeText(this, "Use Landscape for better experience", Toast.LENGTH_LONG).show();
        }
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

    private boolean isXLargeScreen() {
        return (getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE ||
                getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE;
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private WeakReference<AppCompatActivity> activity;

        public MyAsyncTask(AppCompatActivity activityIn) {
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
                c = sqLiteHelper.getRouteAndSchedule(trainNo, xLargeScreen);
                sqLiteHelperWeakReference = new WeakReference<>(sqLiteHelper);
            } catch (Exception ex) {
                Log.e("RouteScheduleActivity", ex.getMessage(), ex);
            }
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            if (c != null && c.getCount() > 0) {
                ActionBar actionBar = activity.get().getSupportActionBar();
                if (actionBar != null) {
                    actionBar
                            .setTitle(activity.get().getIntent()
                                    .getStringExtra(TrainsFragment.EXTRA_STARTS)
                                    + " (" + trainNo + ")");
                }
                RouteAndScheduleAdapter adapter =
                        new RouteAndScheduleAdapter(c, xLargeScreen, activity.get());
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
