package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.CursorRecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.IFSCRecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.PinCodeRecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.RTORecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.STDRecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.StationRecyclerViewAdapter;
import com.ashoksm.pinfinder.adapter.TrainRecyclerViewAdapter;
import com.ashoksm.pinfinder.sqlite.BankSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.PinSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.lang.ref.WeakReference;

public class AllCodeDetailFragment extends Fragment {

    private static SharedPreferences sharedPref;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllCodeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences("AllCodeFinder", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getView(inflater, container);
    }

    @NonNull
    private View getView(LayoutInflater vi, ViewGroup container) {
        final View v = vi.inflate(R.layout.all_code_fragment_content, container, false);

        final RecyclerView mRecyclerView = v.findViewById(R.id.gridView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // set item decorator
        Drawable dividerDrawable =
                ContextCompat.getDrawable(v.getContext(), R.drawable.item_divider_big);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        new MyAsyncTask(getActivity(), getArguments()).execute();
        return v;
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Cursor> {
        private WeakReference<View> progressLayout;
        private WeakReference<View> progressBar;
        private WeakReference<FragmentActivity> activity;
        private String officeName;
        private String action;
        private String branchName;
        private String cityName;
        private String station;
        private String trainNo;

        MyAsyncTask(FragmentActivity activityIn, Bundle argumentsIn) {
            progressLayout = new WeakReference<>(activityIn.findViewById(R.id.progressLayout));
            activity = new WeakReference<>(activityIn);
            action = argumentsIn.getString(IFSCFragment.EXTRA_ACTION);
            if (action != null && action.length() == 0) {
                String offName = argumentsIn.getString(PincodeFragment.EXTRA_OFFICE);
                if (offName != null) {
                    officeName = offName.toLowerCase().replaceAll(" ", "").replaceAll("'", "''");
                }
            } else if ("STD".equalsIgnoreCase(action) || "RTO".equalsIgnoreCase(action)) {
                cityName = argumentsIn.getString(STDFragment.EXTRA_CITY);
            } else if ("RAIL".equalsIgnoreCase(action)) {
                String stn = argumentsIn.getString(StationsFragment.EXTRA_STATION);
                if (stn != null) {
                    station = stn.replaceAll("'", "''").toLowerCase();
                }
            } else if ("TRAIN".equalsIgnoreCase(action)) {
                trainNo = argumentsIn.getString(TrainsFragment.EXTRA_TRAIN);
                station = argumentsIn.getString(TrainsFragment.EXTRA_STARTS);
            } else {
                branchName = argumentsIn.getString(IFSCFragment.EXTRA_BRANCH);
            }
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            progressLayout.get().setVisibility(View.VISIBLE);
            progressBar =
                    new WeakReference<>(progressLayout.get().findViewById(R.id.pbHeaderProgress));
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor c = null;
            try {
                RailWaysSQLiteHelper railSQLiteHelper;
                if (action != null && action.length() == 0) {
                    PinSQLiteHelper sqLiteHelper =
                            new PinSQLiteHelper(activity.get(), (DonutProgress) progressBar.get());
                    c = sqLiteHelper.findMatchingOffices("", "", officeName);
                } else if ("STD".equalsIgnoreCase(action)) {
                    STDSQLiteHelper stdsqLiteHelper =
                            new STDSQLiteHelper(activity.get(), (DonutProgress) progressBar.get());
                    c = stdsqLiteHelper.findSTDCodes("", cityName.toLowerCase(), action);
                } else if ("RTO".equalsIgnoreCase(action)) {
                    RTOSQLiteHelper rtosqLiteHelper =
                            new RTOSQLiteHelper(activity.get(), (DonutProgress) progressBar.get());
                    c = rtosqLiteHelper.findRTOCodes("", cityName.toLowerCase(), action);
                } else if ("RAIL".equalsIgnoreCase(action)) {
                    railSQLiteHelper = new RailWaysSQLiteHelper(activity.get());
                    c = railSQLiteHelper.findStations(station, "", "", action);
                } else if ("TRAIN".equalsIgnoreCase(action)) {
                    railSQLiteHelper = new RailWaysSQLiteHelper(activity.get());
                    if (trainNo.length() > 0) {
                        c = railSQLiteHelper.findTrainsByNoOrName(trainNo.replaceAll("'",
                                "''"));
                    } else {
                        c = railSQLiteHelper.findTrainsByStation(station.replaceAll("'",
                                "''"), "");
                    }
                } else {
                    BankSQLiteHelper bSQLiteHelper =
                            new BankSQLiteHelper(activity.get(), (DonutProgress) progressBar.get());
                    c = bSQLiteHelper.findIfscCodes("", "", "", branchName.toLowerCase(),
                            action);
                }
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), ex.getMessage());
            }
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            if (c != null && c.getCount() > 0) {
                CursorRecyclerViewAdapter adapter;
                if (action != null && action.length() == 0) {
                    adapter = new PinCodeRecyclerViewAdapter(activity.get(), c, sharedPref,
                            false);
                } else if ("STD".equalsIgnoreCase(action)) {
                    adapter = new STDRecyclerViewAdapter(activity.get(), c, sharedPref, false);
                } else if ("RTO".equalsIgnoreCase(action)) {
                    adapter = new RTORecyclerViewAdapter(activity.get(), c, sharedPref, false);
                } else if ("RAIL".equalsIgnoreCase(action)) {
                    adapter = new StationRecyclerViewAdapter(activity.get(), c);
                } else if ("TRAIN".equalsIgnoreCase(action)) {
                    adapter = new TrainRecyclerViewAdapter(activity.get(), c);
                } else {
                    adapter = new IFSCRecyclerViewAdapter(activity.get(), c, "", sharedPref,
                            false);
                }
                RecyclerView mRecyclerView = activity.get().findViewById(R.id.gridView);
                mRecyclerView.setAdapter(adapter);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                LinearLayout noMatchingLayout = activity.get().findViewById(R.id.noMatchingLayout);
                noMatchingLayout.setVisibility(View.VISIBLE);
            }
            // HIDE THE SPINNER AFTER LOADING FEEDS
            progressLayout.get().setVisibility(View.GONE);
        }

    }
}
