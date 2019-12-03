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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ashoksm.pinfinder.adapter.IFSCRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.AdService;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.sqlite.BankSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.lang.ref.WeakReference;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisplayBankResultActivity extends AppCompatActivity {

    private static String stateName;
    private static String districtName;
    private static String bankName;
    private static String branchName;
    private static String action;
    private static boolean showFav;
    private static SharedPreferences sharedPreferences;
    private static DonutProgress progressBar;
    private static WeakReference<BankSQLiteHelper> sqLiteHelperWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);

        final Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);

        // load ad
        AdService.loadBannerAd(this);

        sharedPreferences = getSharedPreferences("AllCodeFinder", Context.MODE_PRIVATE);

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
        showFav = intent.getBooleanExtra(MainActivity.EXTRA_SHOW_FAV, false);
        if (!showFav) {
            stateName = intent.getStringExtra(IFSCFragment.EXTRA_STATE).toLowerCase(l)
                    .replaceAll(" ", "").replaceAll("'", "''");
            districtName = intent.getStringExtra(IFSCFragment.EXTRA_DISTRICT).toLowerCase(l)
                    .replaceAll(" ", "").replaceAll("'", "''");
            bankName = intent.getStringExtra(IFSCFragment.EXTRA_BANK).toLowerCase(l)
                    .replaceAll(" ", "").replaceAll("'", "''");
            action = intent.getStringExtra(IFSCFragment.EXTRA_ACTION);
            if (action != null && action.length() > 0) {
                branchName = intent.getStringExtra(IFSCFragment.EXTRA_BRANCH).toLowerCase(l)
                        .replaceAll("'", "''");
            } else {
                branchName = intent.getStringExtra(IFSCFragment.EXTRA_BRANCH).toLowerCase(l)
                        .replaceAll(" ", "").replaceAll("'", "''");
            }

        }

        new MyAsyncTask(this).execute();
        AppRater.appLaunched(this);
    }

    @Override
    public void onBackPressed() {
        if (Float.floatToIntBits(progressBar.getProgress()) == Float.floatToIntBits(100F)) {
            if (sqLiteHelperWeakReference.get() != null) {
                sqLiteHelperWeakReference.get().closeDB();
            }
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, 0);
        }
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
                if (Float.floatToIntBits(progressBar.getProgress()) == Float.floatToIntBits(100F)) {
                    super.onBackPressed();
                    overridePendingTransition(R.anim.slide_in_left, 0);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private WeakReference<AppCompatActivity> activity;

        MyAsyncTask(AppCompatActivity activityIn) {
            this.activity = new WeakReference<>(activityIn);
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
            progressBar = activity.get().findViewById(R.id.pbHeaderProgress);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor c = null;
            try {
                BankSQLiteHelper sqLiteHelper = new BankSQLiteHelper(activity.get(), progressBar);
                sqLiteHelperWeakReference = new WeakReference<>(sqLiteHelper);
                if (showFav) {
                    c = sqLiteHelper
                            .findFavIfscCodes(sharedPreferences.getString("ifscs", null));
                } else {
                    c = sqLiteHelper
                            .findIfscCodes(stateName, districtName, bankName, branchName,
                                    action);
                }
            } catch (Exception ex) {
                Log.e(DisplayBankResultActivity.class.getName(), ex.getMessage());
            }
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            ActionBar actionBar = activity.get().getSupportActionBar();
            String bankName = activity.get().getIntent().getStringExtra(IFSCFragment.EXTRA_BANK);
            if ((showFav || action.length() > 0) && actionBar != null) {
                actionBar.setTitle(c.getCount() + " Results Found");
            } else if (actionBar != null) {
                actionBar.setTitle(bankName);
            }
            if (c != null && c.getCount() > 0) {
                IFSCRecyclerViewAdapter adapter =
                        new IFSCRecyclerViewAdapter(activity.get(), c, bankName, sharedPreferences,
                                showFav);
                RecyclerView mRecyclerView = activity.get().findViewById(R.id.gridView);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                activity.get().findViewById(R.id.noMatchingLayout).setVisibility(View.VISIBLE);
            }
            progressBar.setProgress(100);
            // HIDE THE SPINNER AFTER LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.GONE);
        }

    }

}
