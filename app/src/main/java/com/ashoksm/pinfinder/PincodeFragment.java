package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class PincodeFragment extends Fragment {

    public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";
    public final static String EXTRA_OFFICE = "com.ashoksm.offlinepinfinder.OFFICE";
    private AutoCompleteTextView states;
    private AutoCompleteTextView districts;
    private EditText text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pincode_layout, container, false);
        states = v.findViewById(R.id.states);

        // Get the string array
        String[] statesArr = getActivity().getResources().getStringArray(R.array.states_array);
        // Create the adapter and set it to the AutoCompleteTextView

        ArrayAdapter<String> statesAdapter =
                new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, statesArr);
        states.setAdapter(statesAdapter);
        // populate all districts
        districts = v.findViewById(R.id.districts);
        String[] allDistricts = getActivity().getResources().getStringArray(R.array.district_all);
        districts.setAdapter(
                new ArrayAdapter<>(getActivity(), R.layout.spinner_dropdown_item, allDistricts));
        addStateChangeListener(v, getActivity().getResources(), getActivity());
        addListenerOnButton(v);
        return v;
    }

    private void addStateChangeListener(View rootView, final Resources resources,
                                        final Activity context) {
        districts = rootView.findViewById(R.id.districts);
        states.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void addListenerOnButton(View rootView) {
        states = rootView.findViewById(R.id.states);
        districts = rootView.findViewById(R.id.districts);
        text = rootView.findViewById(R.id.text1);
        Button btnSubmit = rootView.findViewById(R.id.Search);

        text.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(getActivity());
                    return true;
                }
                return false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(getActivity());
            }

        });
    }

    private void performSearch(Activity context) {
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
}
