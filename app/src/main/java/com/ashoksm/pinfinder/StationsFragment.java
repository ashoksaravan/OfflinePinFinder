package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ashoksm.pinfinder.common.AdCounter;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class StationsFragment extends Fragment {

    private AutoCompleteTextView station;
    private AutoCompleteTextView state;
    private AutoCompleteTextView city;
    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";
    public final static String EXTRA_STATION = "com.ashoksm.offlinepinfinder.STATION";
    private InterstitialAd mInterstitialAd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AdCounter.getInstance().incrementCount();
        final View v = inflater.inflate(R.layout.stations_layout, container, false);

        //load ad
        if(AdCounter.getInstance().getCount() % 5 == 0 || AdCounter.getInstance().isShowAd()) {
            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
            AdCounter.getInstance().incrementCount();
        }

        station = (AutoCompleteTextView) v.findViewById(R.id.station);
        state = (AutoCompleteTextView) v.findViewById(R.id.stations_state);
        city = (AutoCompleteTextView) v.findViewById(R.id.stations_city);
        Button btnSubmit = (Button) v.findViewById(R.id.station_search);

        new AsyncTask<Void, Void, Void>() {

            RailWaysSQLiteHelper sqLiteHelper = new RailWaysSQLiteHelper(getActivity());
            String[] stationCodes;
            String[] states;
            String[] cities;

            @Override
            protected Void doInBackground(Void... params) {
                stationCodes = sqLiteHelper.getStationCodes();
                states = sqLiteHelper.getStates();
                cities = sqLiteHelper.getCities();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (getContext() != null) {
                    ArrayAdapter<String> stationAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, stationCodes);
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, states);
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, cities);
                    station.setAdapter(stationAdapter);
                    state.setAdapter(stateAdapter);
                    city.setAdapter(cityAdapter);
                }
            }
        }.execute();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });

        station.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return editorAction(actionId);
            }
        });

        state.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return editorAction(actionId);
            }
        });

        city.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return editorAction(actionId);
            }
        });
        return v;
    }

    private boolean editorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            showInterstitial();
            return true;
        }
        return false;
    }

    private void performSearch(Activity context) {
        // hide keyboard
        if (getView() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getContext().getSystemService(Context
                            .INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        Intent intent = new Intent(context, DisplayStationResultActivity.class);
        intent.putExtra(EXTRA_STATE, state.getText().toString().trim());
        intent.putExtra(EXTRA_CITY, city.getText().toString().trim());
        String station = this.station.getText().toString();

        if (station.contains(" - ")) {
            intent.putExtra(EXTRA_STATION, station
                    .substring(0, station.indexOf(" - ")).trim());
        } else {
            intent.putExtra(EXTRA_STATION, station);
        }
        intent.putExtra(IFSCFragment.EXTRA_ACTION, "");
        intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(getActivity().getString(R.string.admob_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
                performSearch(getActivity());
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            AdCounter.getInstance().setShowAd(false);
        } else {
            performSearch(getActivity());
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
