package com.ashoksm.pinfinder;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class BankView {

	private static Spinner bankNameSpinner;

	private static Spinner stateNameSpinner;

	private static Spinner districtNameSpinner;

	private static EditText branchName;

	public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

	public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";

	public final static String EXTRA_BANK = "com.ashoksm.offlinepinfinder.BANK";

	public final static String EXTRA_BRANCH = "com.ashoksm.offlinepinfinder.BRANCH";

	public static void execute(final View rootView, final Resources resources, final Context context) {
		AdView adView = (AdView) rootView.findViewById(R.id.adIfsc);
		adView.loadAd(new AdRequest());

		bankNameSpinner = (Spinner) rootView.findViewById(R.id.bankName);
		stateNameSpinner = (Spinner) rootView.findViewById(R.id.stateName);
		districtNameSpinner = (Spinner) rootView.findViewById(R.id.districtName);
		branchName = (EditText) rootView.findViewById(R.id.branchName);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		int id = resources.getIdentifier("bank_names", "array", context.getPackageName());
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, id, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// Apply the adapter to the spinner
		bankNameSpinner.setAdapter(adapter);

		// add listener
		bankNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Locale l = Locale.getDefault();
				String bankName = parent.getItemAtPosition(position).toString();
				String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
						.replaceAll(" ", "").replaceAll("-", "_")
						+ "_states";
				int bankId = resources.getIdentifier(resourceName, "array", context.getPackageName());
				if (bankId != 0) {
					ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, bankId,
							R.layout.spinner_item);
					// Specify the layout to use when the list of choices
					// appears;
					stateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					// Apply the adapter to the spinner
					stateNameSpinner.setAdapter(stateAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		stateNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Locale l = Locale.getDefault();
				String bankName = bankNameSpinner.getSelectedItem().toString();
				String stateName = parent.getItemAtPosition(position).toString();
				String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
						.replaceAll(" ", "").replaceAll("-", "_")
						+ "_"
						+ stateName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
						.replaceAll(" ", "").replaceAll("-", "_") + "_districts";
				int bankId = resources.getIdentifier(resourceName, "array", context.getPackageName());
				if (bankId != 0) {
					ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(context, bankId,
							R.layout.spinner_item);
					// Specify the layout to use when the list of choices
					// appears
					districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					// Apply the adapter to the spinner
					districtNameSpinner.setAdapter(districtAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		Button btnSubmit = (Button) rootView.findViewById(R.id.ifscSearch);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performSearch(resources, context);
			}

		});
	}

	private static void performSearch(Resources resources, Context context) {
		String bankName = bankNameSpinner.getSelectedItem().toString();
		String stateName = stateNameSpinner.getSelectedItem().toString();
		String districtName = districtNameSpinner.getSelectedItem().toString();
		String branch = branchName.getText().toString();
		Intent intent = new Intent(context, DisplayBankBranchResultActivity.class);
		intent.putExtra(EXTRA_STATE, stateName.trim());
		intent.putExtra(EXTRA_DISTRICT, districtName.trim());
		intent.putExtra(EXTRA_BANK, bankName.trim());
		intent.putExtra(EXTRA_BRANCH, branch.trim());
		context.startActivity(intent);
	}
}
