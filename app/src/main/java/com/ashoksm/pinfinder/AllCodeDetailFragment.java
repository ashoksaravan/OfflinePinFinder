package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.ashoksm.pinfinder.common.ContentAdLayoutContext;
import com.ashoksm.pinfinder.sqlite.BankSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.PinSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.clockbyte.admobadapter.AdmobRecyclerAdapterWrapper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

public class AllCodeDetailFragment extends Fragment {

    private PinSQLiteHelper sqLiteHelper;
    private BankSQLiteHelper bSQLiteHelper;
    private STDSQLiteHelper stdsqLiteHelper;
    private RTOSQLiteHelper rtosqLiteHelper;
    private SharedPreferences sharedPref;
    private String officeName;
    private String action;
    private String branchName;
    private String cityName;
    private Cursor c;
    private AdmobRecyclerAdapterWrapper adAdapterWrapper;
    private CursorRecyclerViewAdapter adapter;

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
        MobileAds.initialize(getActivity(), getString(R.string.admob_small_native_ad_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getView(inflater, container);
    }

    @NonNull
    private View getView(LayoutInflater vi, ViewGroup container) {
        final View v = vi.inflate(R.layout.all_code_fragment_content, container, false);

        final RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.gridView);

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
        action = getArguments().getString(IFSCFragment.EXTRA_ACTION);
        if (action != null && action.length() == 0) {
            String offName = getArguments().getString(PincodeFragment.EXTRA_OFFICE);
            if (offName != null) {
                officeName = offName.toLowerCase().replaceAll(" ", "").replaceAll("'", "''");
            }
        } else if ("STD".equalsIgnoreCase(action) || "RTO".equalsIgnoreCase(action)) {
            cityName = getArguments().getString(STDFragment.EXTRA_CITY);
        } else {
            branchName = getArguments().getString(IFSCFragment.EXTRA_BRANCH);
        }

        new AsyncTask<Void, Void, Void>() {
            LinearLayout progressLayout = (LinearLayout) v.findViewById(R.id.progressLayout);

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (action != null && action.length() == 0) {
                        sqLiteHelper = new PinSQLiteHelper(getActivity());
                        c = sqLiteHelper.findMatchingOffices("", "",
                                AllCodeDetailFragment.this.officeName);
                    } else if ("STD".equalsIgnoreCase(action)) {
                        stdsqLiteHelper = new STDSQLiteHelper(getActivity());
                        c = stdsqLiteHelper.findSTDCodes("", cityName.toLowerCase(), action);
                    } else if ("RTO".equalsIgnoreCase(action)) {
                        rtosqLiteHelper = new RTOSQLiteHelper(getActivity());
                        c = rtosqLiteHelper.findRTOCodes("", cityName.toLowerCase(), action);
                    } else {
                        bSQLiteHelper = new BankSQLiteHelper(getActivity());
                        c = bSQLiteHelper.findIfscCodes("", "", "", branchName.toLowerCase(),
                                action);
                    }
                } catch (Exception ex) {
                    Log.e(this.getClass().getName(), ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (c != null && c.getCount() > 0) {
                    if (action != null && action.length() == 0) {
                        adapter = new PinCodeRecyclerViewAdapter(getActivity(), c, sharedPref,
                                false);
                    } else if ("STD".equalsIgnoreCase(action)) {
                        adapter = new STDRecyclerViewAdapter(getActivity(), c, sharedPref, false);
                    } else if ("RTO".equalsIgnoreCase(action)) {
                        adapter = new RTORecyclerViewAdapter(getActivity(), c, sharedPref, false);
                    } else {
                        adapter = new IFSCRecyclerViewAdapter(getActivity(), c, "", sharedPref,
                                false);
                    }
                    initNativeAd();
                    mRecyclerView.setAdapter(adAdapterWrapper);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout noMatchingLayout =
                            (LinearLayout) v.findViewById(R.id.noMatchingLayout);
                    noMatchingLayout.setVisibility(View.VISIBLE);
                }
                // HIDE THE SPINNER AFTER LOADING FEEDS
                progressLayout.setVisibility(View.GONE);
            }

        }.execute();
        return v;
    }

    @SuppressWarnings("unchecked")
    private void initNativeAd() {
        String[] testDevicesIds = new String[]{AdRequest.DEVICE_ID_EMULATOR};
        adAdapterWrapper = new AdmobRecyclerAdapterWrapper(getActivity(), testDevicesIds);
        adAdapterWrapper.setContentAdsLayoutContext(new ContentAdLayoutContext(R.layout
                .ad_content));
        adAdapterWrapper.setAdapter(adapter);
        adAdapterWrapper.setLimitOfAds(3);
        adAdapterWrapper.setNoOfDataBetweenAds(10);
        adAdapterWrapper.setFirstAdIndex(2);
    }
}
