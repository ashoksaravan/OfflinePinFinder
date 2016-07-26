package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.adapter.PinCodeRecyclerViewAdapter;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;

public class AllCodeDetailFragment extends Fragment {

    private PinFinderSQLiteHelper sqLiteHelper;
    private SharedPreferences sharedPreferences;
    private String officeName;
    private Cursor c;

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

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        String string = getArguments().getString(PincodeFragment.EXTRA_OFFICE);
        if (string != null) {
            officeName = string.toLowerCase().replaceAll(" ", "").replaceAll("'", "''");
        }

        new AsyncTask<Void, Void, Void>() {
            LinearLayout progressLayout = (LinearLayout) v.findViewById(R.id.progressLayout);
            PinCodeRecyclerViewAdapter adapter;

            @Override
            protected void onPreExecute() {
                // SHOW THE SPINNER WHILE LOADING FEEDS
                progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sqLiteHelper = new PinFinderSQLiteHelper(getActivity());
                    c = sqLiteHelper.findMatchingOffices("", "", officeName);
                } catch (Exception ex) {
                    Log.e(this.getClass().getName(), ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (c != null && c.getCount() > 0) {
                    adapter = new PinCodeRecyclerViewAdapter(getActivity(), c,
                            sharedPreferences,
                            false);
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
