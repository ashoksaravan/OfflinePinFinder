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

public class BankView {

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";
    public final static String EXTRA_BANK = "com.ashoksm.offlinepinfinder.BANK";
    public final static String EXTRA_BRANCH = "com.ashoksm.offlinepinfinder.BRANCH";
    private static AutoCompleteTextView bankNameSpinner;
    private static AutoCompleteTextView stateNameTextView;
    private static AutoCompleteTextView districtNameTextView;
    private static EditText branchName;
    private static InterstitialAd mInterstitialAd;
    private static View bankView;
    private static Activity activity;

    public static void execute(final View rootView, final Resources resources, final Activity context) {
        bankView = rootView;
        activity = context;
        bankNameSpinner = (AutoCompleteTextView) rootView.findViewById(R.id.bankName);
        stateNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.stateName);
        districtNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.districtName);
        branchName = (EditText) rootView.findViewById(R.id.branchName);
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.bank_names,
                R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        bankNameSpinner.setAdapter(adapter);

        // add listener
        bankNameSpinner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateNameTextView.setText("");
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = parent.getItemAtPosition(position).toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_states";
                int bankId = resources.getIdentifier(resourceName, "array", context.getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, bankId,
                            R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    stateNameTextView.setAdapter(stateAdapter);
                }
            }
        });

        stateNameTextView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = bankNameSpinner.getText().toString();
                String stateName = stateNameTextView.getText().toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_"
                                + stateName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_") + "_districts";
                int bankId = resources.getIdentifier(resourceName, "array", context.getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(context, bankId,
                            R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districtNameTextView.setAdapter(districtAdapter);
                }
            }
        });

        Button btnSubmit = (Button) rootView.findViewById(R.id.ifscSearch);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }

        });

        branchName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        inputMethodManager.hideSoftInputFromWindow(bankView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String bankName = bankNameSpinner.getText().toString();
        if (bankName.trim().length() > 0) {
            String stateName = stateNameTextView.getText().toString();
            String districtName = districtNameTextView.getText().toString();
            String branch = branchName.getText().toString();
            Intent intent = new Intent(context, DisplayBankBranchResultActivity.class);
            intent.putExtra(EXTRA_STATE, stateName.trim());
            intent.putExtra(EXTRA_DISTRICT, districtName.trim());
            intent.putExtra(EXTRA_BANK, bankName.trim());
            intent.putExtra(EXTRA_BRANCH, branch.trim());
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_out_left, 0);
        } else {
            Toast.makeText(context, "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
        }
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
