package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RTOFragment extends Fragment {

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";
    private AutoCompleteTextView stateNameTextView;
    private EditText cityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.rto_layout, container, false);
        stateNameTextView = v.findViewById(R.id.rtoStates);

        ArrayAdapter<CharSequence> stateAdapter =
                ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()),
                        R.array.states_array,
                        R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        stateNameTextView.setAdapter(stateAdapter);
        cityName = v.findViewById(R.id.rtoCityName);
        Button btnSubmit = v.findViewById(R.id.rtoSearch);
        btnSubmit.setOnClickListener(v12 -> performSearch(getActivity()));

        cityName.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(getActivity());
                return true;
            }
            return false;
        });
        return v;
    }

    private void performSearch(Activity context) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context
                        .INPUT_METHOD_SERVICE);
        // hide keyboard
        if (getView() != null && inputMethodManager != null) {
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
