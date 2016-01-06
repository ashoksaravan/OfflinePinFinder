package com.ashoksm.pinfinder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.IFSCRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

public class DisplayBankBranchResultActivity extends AppCompatActivity {

    private BankBranchSQLiteHelper sqLiteHelper;

    private Cursor c;

    private String stateName;

    private String districtName;

    private String bankName;

    private String branchName;

    private boolean showFav;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_result);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("AllCodeFinder", Context.MODE_PRIVATE);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.gridView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

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
            stateName =
                    intent.getStringExtra(BankView.EXTRA_STATE).toLowerCase(l).replaceAll(" ", "")
                            .replaceAll("'", "''");
            districtName =
                    intent.getStringExtra(BankView.EXTRA_DISTRICT).toLowerCase(l)
                            .replaceAll(" ", "")
                            .replaceAll("'", "''");
            bankName = intent.getStringExtra(BankView.EXTRA_BANK).toLowerCase(l).replaceAll(" ", "")
                    .replaceAll("'", "''");
            branchName =
                    intent.getStringExtra(BankView.EXTRA_BRANCH).toLowerCase(l).replaceAll(" ", "")
                            .replaceAll("'", "''");
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
            IFSCRecyclerViewAdapter adapter;

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sqLiteHelper = new BankBranchSQLiteHelper(DisplayBankBranchResultActivity.this);
                    if (showFav) {
                        c = sqLiteHelper
                                .findFavIfscCodes(sharedPreferences.getString("ifscs", null));
                    } else {
                        c = sqLiteHelper
                                .findIfscCodes(stateName, districtName, bankName, branchName);
                    }
                } catch (Exception ex) {
                    Log.e(this.getClass().getName(), ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (showFav && getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(c.getCount() + " Results Found");
                } else if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(intent.getStringExtra(BankView.EXTRA_BANK));
                }
                if (c != null && c.getCount() > 0) {
                    adapter = new IFSCRecyclerViewAdapter(DisplayBankBranchResultActivity.this, c,
                            intent.getStringExtra(BankView.EXTRA_BANK), sharedPreferences, showFav);
                    mRecyclerView.setAdapter(adapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout noMatchingLayout =
                            (LinearLayout) findViewById(R.id.noMatchingLayout);
                    noMatchingLayout.setVisibility(View.VISIBLE);
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
    }
}
