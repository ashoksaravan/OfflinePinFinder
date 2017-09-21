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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RTOFragment extends Fragment {

    private AutoCompleteTextView stateNameTextView;
    private EditText cityName;
    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rto_layout, container, false);
        stateNameTextView = v.findViewById(R.id.rtoStates);

        ArrayAdapter<CharSequence> stateAdapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.states_array,
                        R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateNameTextView.setAdapter(stateAdapter);
        cityName = v.findViewById(R.id.rtoCityName);
        Button btnSubmit = v.findViewById(R.id.rtoSearch);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(getActivity());
            }

        });

        cityName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(getActivity());
                    return true;
                }
                return false;
            }
        });
        return v;
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

        String stateName = stateNameTextView.getText().toString();
        String city = cityName.getText().toString();
        Intent intent = new Intent(context, DisplayRTOResultActivity.class);
        intent.putExtra(EXTRA_STATE, stateName.trim());
        intent.putExtra(EXTRA_CITY, city.trim());
        intent.putExtra(IFSCFragment.EXTRA_ACTION, "");
        intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }

}
