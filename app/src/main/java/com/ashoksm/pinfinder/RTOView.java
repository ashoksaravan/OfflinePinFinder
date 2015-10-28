package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class RTOView {

    private static AutoCompleteTextView stateNameTextView;

    private static EditText cityName;

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

    public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";

    private static InterstitialAd mInterstitialAd;
    private static View rtoCodeView;
    private static Activity activity;


    public static void execute(final View rootView, final Activity context) {
        rtoCodeView = rootView;
        activity = context;
        stateNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.rtoStates);

        mInterstitialAd = newInterstitialAd();
        loadInterstitial();

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states_array,
                R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateNameTextView.setAdapter(stateAdapter);
        cityName = (EditText) rootView.findViewById(R.id.rtoCityName);
        Button btnSubmit = (Button) rootView.findViewById(R.id.rtoSearch);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }

        });

        cityName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showInterstitial();
                    return true;
                }
                return false;
            }
        });
    }

    private static void performSearch(Activity context) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rtoCodeView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String stateName = stateNameTextView.getText().toString();
        String city = cityName.getText().toString();
        Intent intent = new Intent(context, DisplayRTOResultActivity.class);
        intent.putExtra(EXTRA_STATE, stateName.trim());
        intent.putExtra(EXTRA_CITY, city.trim());
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }

    private static InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(activity.getString(R.string.admob_id));
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
                performSearch(activity);
            }
        });
        return interstitialAd;
    }

    private static void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            performSearch(activity);
        }
    }

    private static void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
