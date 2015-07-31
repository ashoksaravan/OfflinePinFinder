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

import java.util.Locale;

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
    private static EditText text;

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
                String resourceName = "district_" +states.getText().toString().toLowerCase(Locale.getDefault()).replace('&', ' ')
                        .replaceAll(" ", "");
                int resourceId = resources.getIdentifier(resourceName, "array", context.getPackageName());
                if (resourceId != 0) {
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(context, resourceId,
                            R.layout.spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    districts.setAdapter(districtAdapter);
                }
            }
        });
    }

    public static void addListenerOnButton(View rootView, final Activity context) {
        states = (AutoCompleteTextView) rootView.findViewById(R.id.states);
        districts = (AutoCompleteTextView) rootView.findViewById(R.id.districts);
        text = (EditText) rootView.findViewById(R.id.text1);
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
