package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

public class IFSCFragment extends Fragment {

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";
    public final static String EXTRA_BANK = "com.ashoksm.offlinepinfinder.BANK";
    public final static String EXTRA_BRANCH = "com.ashoksm.offlinepinfinder.BRANCH";
    public final static String EXTRA_ACTION = "com.ashoksm.offlinepinfinder.ACTION";
    private AutoCompleteTextView bankNameSpinner;
    private AutoCompleteTextView stateNameTextView;
    private AutoCompleteTextView districtNameTextView;
    private EditText branchName;
    private static InterstitialAd mInterstitialAd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ifsc_layout, null);
        bankNameSpinner = (AutoCompleteTextView) v.findViewById(R.id.bankName);
        stateNameTextView = (AutoCompleteTextView) v.findViewById(R.id.stateName);
        districtNameTextView = (AutoCompleteTextView) v.findViewById(R.id.districtName);
        branchName = (EditText) v.findViewById(R.id.branchName);
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.bank_names,
                        R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        bankNameSpinner.setAdapter(adapter);

        // add listener
        bankNameSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateNameTextView.setText("");
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = parent.getItemAtPosition(position).toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ')
                                .replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_states";
                int bankId = getActivity().getResources()
                        .getIdentifier(resourceName, "array", getActivity().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> stateAdapter =
                            ArrayAdapter.createFromResource(getActivity(), bankId,
                                    R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    stateNameTextView.setAdapter(stateAdapter);
                }
            }
        });

        stateNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                districtNameTextView.setText("");
                branchName.setText("");
                Locale l = Locale.getDefault();
                String bankName = bankNameSpinner.getText().toString();
                String stateName = stateNameTextView.getText().toString();
                String resourceName =
                        bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ')
                                .replace(')', ' ').replace('&', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_")
                                + "_"
                                + stateName.toLowerCase(l).replace('.', ' ').replace('(', ' ')
                                .replace(')', ' ')
                                .replaceAll(" ", "").replaceAll("-", "_") + "_districts";
                int bankId = getActivity().getResources()
                        .getIdentifier(resourceName, "array", getActivity().getPackageName());
                if (bankId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter =
                            ArrayAdapter.createFromResource(getActivity(), bankId,
                                    R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districtNameTextView.setAdapter(districtAdapter);
                }
            }
        });

        Button btnSubmit = (Button) v.findViewById(R.id.ifscSearch);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
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
        return v;
    }

    private void performSearch(Activity context) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getView() != null) {
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String bankName = bankNameSpinner.getText().toString();
        if (bankName.trim().length() > 0) {
            String stateName = stateNameTextView.getText().toString();
            String districtName = districtNameTextView.getText().toString();
            String branch = branchName.getText().toString();
            Intent intent = new Intent(context, DisplayBankResultActivity.class);
            intent.putExtra(EXTRA_STATE, stateName.trim());
            intent.putExtra(EXTRA_DISTRICT, districtName.trim());
            intent.putExtra(EXTRA_BANK, bankName.trim());
            intent.putExtra(EXTRA_BRANCH, branch.trim());
            intent.putExtra(EXTRA_ACTION, "");
            intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_out_left, 0);
        } else {
            Toast.makeText(context, "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
        }
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
        if (bankNameSpinner.getText().toString().trim().length() > 0) {
            // Show the ad if it's ready. Otherwise toast and reload the ad.
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                performSearch(getActivity());
            }
        } else {
            Toast.makeText(getActivity(), "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
        }
    }

    private static void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }
}
