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
import android.widget.Switch;

import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;

import java.lang.ref.WeakReference;

public class TrainsFragment extends Fragment {

    public static final String EXTRA_STARTS = "com.ashoksm.offlinepinfinder.STARTS";
    public static final String EXTRA_ENDS = "com.ashoksm.offlinepinfinder.ENDS";
    public static final String EXTRA_TRAIN = "com.ashoksm.offlinepinfinder.TRAIN";
    private AutoCompleteTextView starts;
    private AutoCompleteTextView ends;
    private AutoCompleteTextView trainName;
    private Switch aSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.trains_layout, container, false);

        starts = v.findViewById(R.id.starts);
        ends = v.findViewById(R.id.ends);
        trainName = v.findViewById(R.id.train_name);
        Button btnSubmit = v.findViewById(R.id.train_search);
        aSwitch = v.findViewById(R.id.train_switch);

        new MyAsyncTask(this).execute();

        btnSubmit.setOnClickListener(v13 -> performSearch(getActivity()));

        ends.setOnEditorActionListener((v12, actionId, event) -> editorAction(actionId));

        trainName.setOnEditorActionListener((v1, actionId, event) -> editorAction(actionId));

        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                trainName.setVisibility(View.VISIBLE);
                starts.setVisibility(View.GONE);
                ends.setVisibility(View.GONE);
            } else {
                trainName.setVisibility(View.GONE);
                starts.setVisibility(View.VISIBLE);
                ends.setVisibility(View.VISIBLE);
            }
        });

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
        if (getView() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getContext().getSystemService(Context
                            .INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        Intent intent = new Intent(context, DisplayTrainResultActivity.class);
        if (aSwitch.isChecked()) {
            intent.putExtra(EXTRA_STARTS, starts.getText().toString().trim());
            intent.putExtra(EXTRA_ENDS, ends.getText().toString().trim());
            intent.putExtra(EXTRA_TRAIN, "");
        } else {
            intent.putExtra(EXTRA_TRAIN, trainName.getText().toString().trim());
            intent.putExtra(EXTRA_STARTS, "");
            intent.putExtra(EXTRA_ENDS, "");
        }
        intent.putExtra(IFSCFragment.EXTRA_ACTION, "");
        intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_out_left, 0);
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private String[] stationCodes;
        private String[] trainNos;
        private WeakReference<Fragment> fragment;


        MyAsyncTask(Fragment trainsFragment) {
            this.fragment = new WeakReference<>(trainsFragment);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RailWaysSQLiteHelper sqLiteHelper =
                    new RailWaysSQLiteHelper(fragment.get().getActivity());
            stationCodes = sqLiteHelper.getStationCodes();
            trainNos = sqLiteHelper.getTrainNos();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (fragment.get().getContext() != null) {
                AutoCompleteTextView starts =
                        fragment.get().getActivity().findViewById(R.id.starts);
                AutoCompleteTextView ends = fragment.get().getActivity().findViewById(R.id.ends);
                AutoCompleteTextView trainName =
                        fragment.get().getActivity().findViewById(R.id.train_name);

                ArrayAdapter<String> startAdapter = new ArrayAdapter<>(fragment.get().getContext(),
                        R.layout.spinner_dropdown_item, stationCodes);
                ArrayAdapter<String> endAdapter = new ArrayAdapter<>(fragment.get().getContext(),
                        R.layout.spinner_dropdown_item, stationCodes);
                ArrayAdapter<String> trainAdapter = new ArrayAdapter<>(fragment.get().getContext(),
                        R.layout.spinner_dropdown_item, trainNos);

                starts.setAdapter(startAdapter);
                ends.setAdapter(endAdapter);
                trainName.setAdapter(trainAdapter);
            }
        }
    }

}
