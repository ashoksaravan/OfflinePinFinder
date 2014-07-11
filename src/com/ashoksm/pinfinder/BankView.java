package com.ashoksm.pinfinder;

import java.util.Locale;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BankView {

	private static Spinner bankNameSpinner;

	private static AutoCompleteTextView stateNameTextView;

	private static AutoCompleteTextView districtNameTextView;

	private static EditText branchName;

	public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

	public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";

	public final static String EXTRA_BANK = "com.ashoksm.offlinepinfinder.BANK";

	public final static String EXTRA_BRANCH = "com.ashoksm.offlinepinfinder.BRANCH";

	public static void execute(final View rootView, final Resources resources, final Context context) {
		bankNameSpinner = (Spinner) rootView.findViewById(R.id.bankName);
		stateNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.stateName);
		districtNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.districtName);
		branchName = (EditText) rootView.findViewById(R.id.branchName);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.bank_names,
				R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		// Apply the adapter to the spinner
		bankNameSpinner.setAdapter(adapter);

		// add listener
		bankNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				stateNameTextView.setText("");
				districtNameTextView.setText("");
				branchName.setText("");
				Locale l = Locale.getDefault();
				String bankName = parent.getItemAtPosition(position).toString();
				String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
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

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		stateNameTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				districtNameTextView.setText("");
				branchName.setText("");
				Locale l = Locale.getDefault();
				String bankName = bankNameSpinner.getSelectedItem().toString();
				String stateName = stateNameTextView.getText().toString();
				String resourceName = bankName.toLowerCase(l).replace('.', ' ').replace('(', ' ').replace(')', ' ')
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
				performSearch(resources, context, v);
			}

		});

		branchName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch(resources, context, v);
					return true;
				}
				return false;
			}
		});
	}

	private static void performSearch(Resources resources, Context context, View v) {
		// hide keyboard
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		String bankName = bankNameSpinner.getSelectedItem().toString();
		if (!"Please Select a Bank".equals(bankName)) {
			String stateName = stateNameTextView.getText().toString();
			String districtName = districtNameTextView.getText().toString();
			String branch = branchName.getText().toString();
			Intent intent = new Intent(context, DisplayBankBranchResultActivity.class);
			intent.putExtra(EXTRA_STATE, stateName.trim());
			intent.putExtra(EXTRA_DISTRICT, districtName.trim());
			intent.putExtra(EXTRA_BANK, bankName.trim());
			intent.putExtra(EXTRA_BRANCH, branch.trim());
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "Please Select a Bank!!!", Toast.LENGTH_LONG).show();
		}
	}
}
