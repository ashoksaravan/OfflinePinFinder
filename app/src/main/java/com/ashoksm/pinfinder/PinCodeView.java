package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Locale;

public class PinCodeView {

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";
    public final static String EXTRA_OFFICE = "com.ashoksm.offlinepinfinder.OFFICE";
    private static AutoCompleteTextView states;
    private static AutoCompleteTextView districts;
    private static EditText text;
    private static InterstitialAd mInterstitialAd;
    private static View pinCodeView;
    private static Activity activity;

    public static void execute(final View rootView, final Resources resources,
                               final Activity context) {
        pinCodeView = rootView;
        activity = context;
        states = (AutoCompleteTextView) rootView.findViewById(R.id.states);
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
        // Get the string array
        String[] statesArr = resources.getStringArray(R.array.states_array);
        // Create the adapter and set it to the AutoCompleteTextView

        ArrayAdapter<String> statesAdapter =
                new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, statesArr);
        states.setAdapter(statesAdapter);
        // populate all districts
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        String[] allDistricts = resources.getStringArray(R.array.district_all);
        districts.setAdapter(
                new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, allDistricts));
        addStateChangeListener(rootView, resources, context);
        addListenerOnButton(rootView);
    }

    private static void addStateChangeListener(View rootView, final Resources resources,
                                               final Activity context) {
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        states.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                districts.setText("");
                String resourceName =
                        "district_" + states.getText().toString().toLowerCase(Locale.getDefault())
                                .replace('&', ' ')
                                .replaceAll(" ", "");
                int resourceId =
                        resources.getIdentifier(resourceName, "array", context.getPackageName());
                if (resourceId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter =
                            ArrayAdapter.createFromResource(context, resourceId,
                                    R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districts.setAdapter(districtAdapter);
                }
            }
        });
    }

    public static void addListenerOnButton(View rootView) {
        states = (AutoCompleteTextView) rootView.findViewById(R.id.states);
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        text = (EditText) rootView.findViewById(R.id.text1);
        Button btnSubmit = (Button) rootView.findViewById(R.id.Search);

        text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showInterstitial();
                    return true;
                }
                return false;
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }

        });
    }

    private static void performSearch(Activity context) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(pinCodeView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        String stateName = states.getText().toString();
        String districtName = districts.getText().toString();
        String officeName = text.getText().toString();
        Intent intent = new Intent(context, DisplayPinCodeResultActivity.class);
        intent.putExtra(EXTRA_STATE, stateName.trim());
        intent.putExtra(EXTRA_DISTRICT, districtName.trim());
        intent.putExtra(EXTRA_OFFICE, officeName.trim());
        intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
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
        String stateName = states.getText().toString();
        String districtName = districts.getText().toString();
        String officeName = text.getText().toString();
        if (stateName.trim().length() == 0 && districtName.trim().length() == 0 &&
                officeName.trim().length() == 0) {
            Toast.makeText(activity, "All search fields can't be empty!!!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // Show the ad if it's ready. Otherwise toast and reload the ad.
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                performSearch(activity);
            }
        }
    }

    private static void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
