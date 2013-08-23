package com.ashoksm.offlinepinfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PinFinderMainActivity extends Activity {

	public final static String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";

	public final static String EXTRA_DISTRICT = "com.ashoksm.offlinepinfinder.DISTRICT";

	public final static String EXTRA_OFFICE = "com.ashoksm.offlinepinfinder.OFFICE";

	/**
	 * states.
	 */
	private AutoCompleteTextView states;

	/**
	 * districts.
	 */
	private AutoCompleteTextView districts;

	/**
	 * text.
	 */
	private EditText text;

	/**
	 * statesAdapter.
	 */
	private ArrayAdapter<String> statesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_finder_main);

		states = (AutoCompleteTextView) findViewById(R.id.states);
		// Get the string array
		String[] statesArr = getResources().getStringArray(R.array.states_array);
		// Create the adapter and set it to the AutoCompleteTextView
		statesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statesArr);
		states.setAdapter(statesAdapter);
		addStateChangeListener();
		addListenerOnButton();
	}

	private void addStateChangeListener() {
		states.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				districts = (AutoCompleteTextView) findViewById(R.id.districts);
				if (states.getText().toString().equals("Puducherry")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_puducherry);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Tamil Nadu")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_tn);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Kerala")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_kl);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Andaman and Nicobar Islands")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_an);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Arunachal Pradesh")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_ap);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Chandigarh")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_ch);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Dadra and Nagar Haveli")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_dn);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Daman and Diu")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_dd);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Delhi")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_dl);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Goa")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_go);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Nagaland")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_na);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Mizoram")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_mi);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Lakshadweep")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_la);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Manipur")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_ma);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Meghalaya")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_me);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Sikkim")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_si);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else if (states.getText().toString().equals("Tripura")) {
					String[] districtPuducherry = getResources().getStringArray(R.array.district_tr);
					districts.setAdapter(new ArrayAdapter<String>(PinFinderMainActivity.this,
							android.R.layout.simple_list_item_1, districtPuducherry));
				} else {
					Toast.makeText(PinFinderMainActivity.this, "Not a valid state", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void addListenerOnButton() {
		states = (AutoCompleteTextView) findViewById(R.id.states);
		districts = (AutoCompleteTextView) findViewById(R.id.districts);
		text = (EditText) findViewById(R.id.text1);
		Button btnSubmit = (Button) findViewById(R.id.Search);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String stateName = states.getText().toString();
				String districtName = districts.getText().toString();
				String officeName = text.getText().toString();
				Intent intent = new Intent(PinFinderMainActivity.this, DisplayResultActivity.class);
				intent.putExtra(EXTRA_STATE, stateName.trim());
				intent.putExtra(EXTRA_DISTRICT, districtName.trim());
				intent.putExtra(EXTRA_OFFICE, officeName.trim());
				startActivity(intent);
			}

		});
	}
}
