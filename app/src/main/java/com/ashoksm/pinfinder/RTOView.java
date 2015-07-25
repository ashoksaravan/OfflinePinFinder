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

public class RTOView {

    private static AutoCompleteTextView stateNameTextView;

    private static EditText cityName;

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

    public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";

    public static void execute(final View rootView, final Activity context) {
        stateNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.rtoStates);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states_array,
                R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateNameTextView.setAdapter(stateAdapter);
        cityName = (EditText) rootView.findViewById(R.id.rtoCityName);
        Button btnSubmit = (Button) rootView.findViewById(R.id.rtoSearch);
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(context, v);
            }

        });

        cityName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(context, v);
                    return true;
                }
                return false;
            }
        });
    }

    private static void performSearch(Activity context, View v) {
        // hide keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String stateName = stateNameTextView.getText().toString();
        String city = cityName.getText().toString();
        Intent intent = new Intent(context, DisplayRTOResultActivity.class);
        intent.putExtra(EXTRA_STATE, stateName.trim());
        intent.putExtra(EXTRA_CITY, city.trim());
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }
}
