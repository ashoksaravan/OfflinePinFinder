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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.ashoksm.pinfinder.common.AdCounter;
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class TrainsFragment extends Fragment {

    private AutoCompleteTextView starts;
    private AutoCompleteTextView ends;
    private AutoCompleteTextView trainName;
    private Switch aSwitch;
    public final static String EXTRA_STARTS = "com.ashoksm.offlinepinfinder.STARTS";
    public final static String EXTRA_ENDS = "com.ashoksm.offlinepinfinder.ENDS";
    public final static String EXTRA_TRAIN = "com.ashoksm.offlinepinfinder.TRAIN";
    private InterstitialAd mInterstitialAd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.trains_layout, container, false);

        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        starts = (AutoCompleteTextView) v.findViewById(R.id.starts);
        ends = (AutoCompleteTextView) v.findViewById(R.id.ends);
        trainName = (AutoCompleteTextView) v.findViewById(R.id.train_name);
        Button btnSubmit = (Button) v.findViewById(R.id.train_search);
        aSwitch = (Switch) v.findViewById(R.id.train_switch);

        new AsyncTask<Void, Void, Void>() {

            RailWaysSQLiteHelper sqLiteHelper = new RailWaysSQLiteHelper(getActivity());
            String[] stationCodes;
            String[] trainNos;

            @Override
            protected Void doInBackground(Void... voids) {
                stationCodes = sqLiteHelper.getStationCodes();
                trainNos = sqLiteHelper.getTrainNos();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (getContext() != null) {
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, stationCodes);
                    ArrayAdapter<String> endAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, stationCodes);
                    ArrayAdapter<String> trainAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_dropdown_item, trainNos);
                    starts.setAdapter(startAdapter);
                    ends.setAdapter(endAdapter);
                    trainName.setAdapter(trainAdapter);
                }
            }
        }.execute();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });

        ends.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return editorAction(actionId);
            }
        });

        trainName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return editorAction(actionId);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    trainName.setVisibility(View.VISIBLE);
                    starts.setVisibility(View.GONE);
                    ends.setVisibility(View.GONE);
                } else {
                    trainName.setVisibility(View.GONE);
                    starts.setVisibility(View.VISIBLE);
                    ends.setVisibility(View.VISIBLE);
                }
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
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        Intent intent = new Intent(context, DisplayTrainResultActivity.class);
        if (aSwitch.isChecked()) {
            intent.putExtra(EXTRA_STARTS, starts.getText().toString().trim());
            intent.putExtra(EXTRA_ENDS, ends.getText().toString().trim());
            intent.putExtra(EXTRA_TRAIN, "");
        } else {
            intent.putExtra(EXTRA_TRAIN, trainName.getText().toString().trim());
            intent.putExtra(EXTRA_STARTS, "");
            intent.putExtra(EXTRA_ENDS, "");
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
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded() && AdCounter.getInstance()
                .getCount() % 5 == 0) {
            mInterstitialAd.show();
        } else {
            performSearch(getActivity());
        }
        AdCounter.getInstance().incrementCount();
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
