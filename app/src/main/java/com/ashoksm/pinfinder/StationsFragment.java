package com.ashoksm.pinfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

import java.lang.ref.WeakReference;

public class StationsFragment extends Fragment {

    public static final String EXTRA_STATE = "com.ashoksm.offlinepinfinder.STATE";
    public static final String EXTRA_CITY = "com.ashoksm.offlinepinfinder.CITY";
    public static final String EXTRA_STATION = "com.ashoksm.offlinepinfinder.STATION";
    private AutoCompleteTextView station;
    private AutoCompleteTextView state;
    private AutoCompleteTextView city;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.stations_layout, container, false);

        station = v.findViewById(R.id.station);
        state = v.findViewById(R.id.stations_state);
        city = v.findViewById(R.id.stations_city);
        Button btnSubmit = v.findViewById(R.id.station_search);

        new MyAsyncTask(this).execute();

        btnSubmit.setOnClickListener(v14 -> performSearch(getActivity()));

        station.setOnEditorActionListener((v13, actionId, event) -> editorAction(actionId));

        state.setOnEditorActionListener((v12, actionId, event) -> editorAction(actionId));

        city.setOnEditorActionListener((v1, actionId, event) -> editorAction(actionId));
        return v;
    }

    private boolean editorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch(getActivity());
            return true;
        }
        return false;
    }

    private void performSearch(Activity context) {
        // hide keyboard
        InputMethodManager inputMethodManager =
                (InputMethodManager) getContext().getSystemService(Context
                        .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getView() != null) {
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        Intent intent = new Intent(context, DisplayStationResultActivity.class);
        intent.putExtra(EXTRA_STATE, state.getText().toString().trim());
        intent.putExtra(EXTRA_CITY, city.getText().toString().trim());
        String station = this.station.getText().toString();

        if (station.contains(" - ")) {
            intent.putExtra(EXTRA_STATION, station
                    .substring(0, station.indexOf(" - ")).trim());
        } else {
            intent.putExtra(EXTRA_STATION, station);
        }
        intent.putExtra(IFSCFragment.EXTRA_ACTION, "");
        intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        String[] stationCodes;
        String[] states;
        String[] cities;

        private WeakReference<Fragment> fragment;

        public MyAsyncTask(Fragment fragmentIn) {
            fragment = new WeakReference<>(fragmentIn);
        }

        @Override
        protected Void doInBackground(Void... params) {
            RailWaysSQLiteHelper sqLiteHelper =
                    new RailWaysSQLiteHelper(fragment.get().getActivity());
            stationCodes = sqLiteHelper.getStationCodes();
            states = sqLiteHelper.getStates();
            cities = sqLiteHelper.getCities();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (fragment.get().getContext() != null) {
                AutoCompleteTextView station =
                        fragment.get().getActivity().findViewById(R.id.station);
                AutoCompleteTextView state =
                        fragment.get().getActivity().findViewById(R.id.stations_state);
                AutoCompleteTextView city =
                        fragment.get().getActivity().findViewById(R.id.stations_city);

                ArrayAdapter<String> stationAdapter =
                        new ArrayAdapter<>(fragment.get().getContext(),
                                R.layout.spinner_dropdown_item, stationCodes);
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(fragment.get().getContext(),
                        R.layout.spinner_dropdown_item, states);
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(fragment.get().getContext(),
                        R.layout.spinner_dropdown_item, cities);
                station.setAdapter(stationAdapter);
                state.setAdapter(stateAdapter);
                city.setAdapter(cityAdapter);
            }
        }
    }
}
