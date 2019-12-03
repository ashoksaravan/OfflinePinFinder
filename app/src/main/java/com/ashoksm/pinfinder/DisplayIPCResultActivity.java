package com.ashoksm.pinfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ashoksm.pinfinder.common.AdService;
import com.ashoksm.pinfinder.common.AppRater;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.IPCSQLiteHelper;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DisplayIPCResultActivity extends ActivityBase {

    private static DonutProgress progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ipc_result);

        // load action bar
        final Toolbar toolbar = findViewById(R.id.my_awesome_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);

        // load ad
        AdService.loadBannerAd(this);

        new MyAsyncTask(this).execute();
        AppRater.appLaunched(this);
    }

    @Override
    public void onBackPressed() {
        if (Float.floatToIntBits(progressBar.getProgress()) == Float.floatToIntBits(100F)) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, 0);
        }
    }

    @Override
    protected void onDestroy() {
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

    private static class MyAsyncTask extends AsyncTask<Void, Void, String[]> {

        private WeakReference<AppCompatActivity> activity;

        MyAsyncTask(AppCompatActivity activityIn) {
            activity = new WeakReference<>(activityIn);
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
            progressBar = activity.get().findViewById(R.id.pbHeaderProgress);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            IPCSQLiteHelper sqLiteHelper = null;
            try {
                Intent intent = activity.get().getIntent();
                sqLiteHelper = new IPCSQLiteHelper(activity.get(), progressBar);
                if (intent != null) {
                    return sqLiteHelper
                            .getDescription(intent.getStringExtra(IPCFragment.EXTRA_IPC));
                }
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), ex.getMessage());
            } finally {
                if (sqLiteHelper != null) {
                    sqLiteHelper.closeDB();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] data) {
            activity.get().findViewById(R.id.content_layout).setVisibility(View.VISIBLE);
            TextView content = activity.get().findViewById(R.id.content);
            TextView header = activity.get().findViewById(R.id.header);
            if (data != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    header.setText(Html.fromHtml(data[0], Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    content.setText(Html.fromHtml(data[1], Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                } else {
                    header.setText(Html.fromHtml(data[0]));
                    content.setText(Html.fromHtml(data[1]));
                }
            }
            progressBar.setProgress(100F);
            // HIDE THE SPINNER AFTER LOADING FEEDS
            activity.get().findViewById(R.id.progressLayout).setVisibility(View.GONE);
        }

    }
}
