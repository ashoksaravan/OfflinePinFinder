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
import com.ashoksm.pinfinder.sqlite.BankBranchSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

public class AllCodeDetailFragment extends Fragment {

    private PinFinderSQLiteHelper sqLiteHelper;
    private BankBranchSQLiteHelper branchSQLiteHelper;
    private SharedPreferences sharedPreferences;
    private String officeName;
    private String action;
    private String branchName;
    private Cursor c;
    public static final String EXTRA_ACTION = "com.ashoksm.pinfinder.AllCodeDetailFragment.ACTION";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllCodeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("AllCodeFinder", Context
                .MODE_PRIVATE);
        sqLiteHelper = new PinFinderSQLiteHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getPincodeView(inflater, container);
    }

    @NonNull
    private View getPincodeView(LayoutInflater vi, ViewGroup container) {
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
        action = getArguments().getString(EXTRA_ACTION);
        if (action != null && action.length() == 0) {
            String officeName = getArguments().getString(PincodeFragment.EXTRA_OFFICE);
            if (officeName != null) {
                this.officeName =
                        officeName.toLowerCase().replaceAll(" ", "").replaceAll("'", "''");
            }
        } else {
            branchName = getArguments().getString(IFSCFragment.EXTRA_BRANCH);
        }

        new AsyncTask<Void, Void, Void>() {
            LinearLayout progressLayout = (LinearLayout) v.findViewById(R.id.progressLayout);
            CursorRecyclerViewAdapter adapter;

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (action != null && action.length() == 0) {
                        sqLiteHelper = new PinFinderSQLiteHelper(getActivity());
                        c = sqLiteHelper.findMatchingOffices("", "",
                                AllCodeDetailFragment.this.officeName);
                    } else {
                        branchSQLiteHelper = new BankBranchSQLiteHelper(getActivity());
                        c = branchSQLiteHelper
                                .findIfscCodes("", "", "", branchName.toLowerCase(), action);
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
                        adapter = new PinCodeRecyclerViewAdapter(getActivity(), c,
                                sharedPreferences, false);
                    } else {
                        adapter = new IFSCRecyclerViewAdapter(getActivity(), c, "",
                                sharedPreferences, false);
                    }
                    mRecyclerView.setAdapter(adapter);
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
}
