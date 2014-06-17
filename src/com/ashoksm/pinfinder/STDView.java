package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class STDView {

	private static AutoCompleteTextView stateNameTextView;

	private static EditText cityName;

	public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

	public final static String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";

	public static void execute(final View rootView, final Resources resources, final Context context) {
		AdView adView = (AdView) rootView.findViewById(R.id.stdAd);
		adView.loadAd(new AdRequest());

		stateNameTextView = (AutoCompleteTextView) rootView.findViewById(R.id.stdStates);
		ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context, R.array.states_array,
				R.layout.spinner_dropdown_item);
		// Apply the adapter to the spinner
		stateNameTextView.setAdapter(stateAdapter);
		cityName = (EditText) rootView.findViewById(R.id.cityName);
		Button btnSubmit = (Button) rootView.findViewById(R.id.stdSearch);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				performSearch(resources, context);
			}

		});

		cityName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					performSearch(resources, context);
					return true;
				}
				return false;
			}
		});
	}

	private static void performSearch(Resources resources, Context context) {
		String stateName = stateNameTextView.getText().toString();
		String city = cityName.getText().toString();
		Intent intent = new Intent(context, DisplaySTDResultActivity.class);
		intent.putExtra(EXTRA_STATE, stateName.trim());
		intent.putExtra(EXTRA_CITY, city.trim());
		context.startActivity(intent);
	}
}
