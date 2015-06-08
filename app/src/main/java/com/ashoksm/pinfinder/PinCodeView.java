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

public class PinCodeView {

    /**
     * states.
     */
    private static AutoCompleteTextView states;

    /**
     * districts.
     */
    private static AutoCompleteTextView districts;

    /**
     * text.
     */
    private static AutoCompleteTextView text;

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

    public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";

    public final static String EXTRA_OFFICE = "com.ashoksm.offlinepinfinder.OFFICE";

    public static void execute(final View rootView, final Resources resources, final Activity context) {
        states = (AutoCompleteTextView) rootView.findViewById(R.id.states);
        // Get the string array
        String[] statesArr = resources.getStringArray(R.array.states_array);
        // Create the adapter and set it to the AutoCompleteTextView

        ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, statesArr);
        states.setAdapter(statesAdapter);
        // populate all districts
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        String[] allDistricts = resources.getStringArray(R.array.district_all);
        districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item, allDistricts));
        addStateChangeListener(rootView, resources, context);
        addListenerOnButton(rootView, context);
    }

    private static void addStateChangeListener(View rootView, final Resources resources, final Activity context) {
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        states.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                districts.setText("");
                if (states.getText().toString().equals("Puducherry")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_puducherry);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Tamil Nadu")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_tn);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Kerala")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_kl);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Andaman and Nicobar Islands")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_an);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Arunachal Pradesh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ar);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Chandigarh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ch);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Dadra and Nagar Haveli")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_dn);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Daman and Diu")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_dd);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Delhi")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_dl);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Goa")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_go);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Nagaland")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_na);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Mizoram")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_mi);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Lakshadweep")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_la);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Manipur")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ma);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Meghalaya")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_me);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Sikkim")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_si);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Tripura")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_tr);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Karnataka")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ka);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Andhra Pradesh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ap);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Maharashtra")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_mh);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Jammu and Kashmir")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_jk);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("West Bengal")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_wb);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Haryana")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_hr);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Assam")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_as);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Himachal Pradesh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_hp);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Chhattisgarh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ct);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Jharkhand")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_jh);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Punjab")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_pu);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Uttarakhand")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_uk);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Bihar")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_bh);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Gujarat")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_gu);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Madhya Pradesh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_mp);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Odisha")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_od);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Rajasthan")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_ra);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Uttar Pradesh")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_up);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else if (states.getText().toString().equals("Telangana")) {
                    String[] districtPuducherry = resources.getStringArray(R.array.district_te);
                    districts.setAdapter(new ArrayAdapter<>(context, R.layout.spinner_dropdown_item,
                            districtPuducherry));
                } else {
                    // not supported
                    Toast.makeText(context, "State not supported yet!!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void addListenerOnButton(View rootView, final Activity context) {
        states = (AutoCompleteTextView) rootView.findViewById(R.id.states);
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        text = (AutoCompleteTextView) rootView.findViewById(R.id.text1);
        Button btnSubmit = (Button) rootView.findViewById(R.id.Search);

        text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(context, v);
                    return true;
                }
                return false;
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(context, v);
            }

        });
    }

    private static void performSearch(Activity context, View v) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        String stateName = states.getText().toString();
        String districtName = districts.getText().toString();
        String officeName = text.getText().toString();
        if (stateName.trim().length() == 0 && districtName.trim().length() == 0 && officeName.trim().length() == 0) {
            Toast.makeText(context, "All search fields can't be empty!!!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(context, DisplayPinCodeResultActivity.class);
            intent.putExtra(EXTRA_STATE, stateName.trim());
            intent.putExtra(EXTRA_DISTRICT, districtName.trim());
            intent.putExtra(EXTRA_OFFICE, officeName.trim());
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_out_left, 0);
        }
    }
}
