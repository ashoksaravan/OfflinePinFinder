package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ifsc_layout, container, false);

        bankNameSpinner = v.findViewById(R.id.bankName);
        stateNameTextView = v.findViewById(R.id.stateName);
        districtNameTextView = v.findViewById(R.id.districtName);
        branchName = v.findViewById(R.id.branchName);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.bank_names,
                        R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        bankNameSpinner.setAdapter(adapter);

        // add listener
        bankNameSpinner.setOnItemClickListener((parent, view, position, id) -> {
            stateNameTextView.setText("");
            districtNameTextView.setText("");
            branchName.setText("");
            Locale l = Locale.getDefault();
            String bankName = parent.getItemAtPosition(position).toString();
            String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ')
                    .replace(')', ' ').replace('&', ' ').replaceAll(" ", "")
                    .replaceAll("-", "_") + "_states";
            int bankId = getActivity().getResources()
                    .getIdentifier(resourceName, "array", getActivity().getPackageName());
            if (bankId != 0) {
                ArrayAdapter<CharSequence> stateAdapter =
                        ArrayAdapter.createFromResource(getActivity(), bankId,
                                R.layout.spinner_dropdown_item);
                // Apply the adapter to the spinner
                stateNameTextView.setAdapter(stateAdapter);
            }
        });

        stateNameTextView.setOnItemClickListener((parent, view, position, id) -> {
            districtNameTextView.setText("");
            branchName.setText("");
            Locale l = Locale.getDefault();
            String bankName = bankNameSpinner.getText().toString();
            String stateName = stateNameTextView.getText().toString();
            String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ')
                    .replace(')', ' ').replace('&', ' ').replaceAll(" ", "")
                    .replaceAll("-", "_") + "_" + stateName.toLowerCase(l)
                    .replace('.', ' ').replace('(', ' ').replace(')', ' ')
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
        });

        Button btnSubmit = v.findViewById(R.id.ifscSearch);
        btnSubmit.setOnClickListener(v12 -> performSearch(getActivity()));

        branchName.setOnEditorActionListener((v1, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(getActivity());
                return true;
            }
            return false;
        });
        return v;
    }

    private void performSearch(Activity context) {

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

}
