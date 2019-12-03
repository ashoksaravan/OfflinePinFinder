package com.ashoksm.pinfinder;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashoksm.pinfinder.adapter.IPCRecyclerViewAdapter;
import com.ashoksm.pinfinder.sqlite.IPCSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IPCFragment extends Fragment {

    public static final String EXTRA_IPC = "ashoksm.ipc";
    private static DonutProgress progressBar;

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ipc_layout, container, false);
        final RecyclerView mRecyclerView = v.findViewById(R.id.ipc_grid_view);
        progressBar = v.findViewById(R.id.progress_bar);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // set item decorator
        Drawable dividerDrawable =
                ContextCompat.getDrawable(getActivity(), R.drawable.item_divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        new MyAsyncTask(this, mRecyclerView).execute();
        return v;
    }

    private static class MyAsyncTask extends AsyncTask<Void, Void, Cursor> {

        private WeakReference<Fragment> fragmentWeakReference;
        private WeakReference<RecyclerView> recyclerViewWeakReference;

        MyAsyncTask(Fragment fragmentIn, RecyclerView mRecyclerView) {
            fragmentWeakReference = new WeakReference<>(fragmentIn);
            recyclerViewWeakReference = new WeakReference<>(mRecyclerView);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerViewWeakReference.get().setVisibility(View.GONE);
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor c = null;
            try {
                IPCSQLiteHelper sqLiteHelper =
                        new IPCSQLiteHelper(fragmentWeakReference.get().getActivity(), progressBar);
                c = sqLiteHelper.findIPC();
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), ex.getLocalizedMessage(), ex);
            }
            return c;
        }

        @Override
        protected void onPostExecute(Cursor c) {
            IPCRecyclerViewAdapter adapter =
                    new IPCRecyclerViewAdapter(fragmentWeakReference.get().getActivity(), c);
            recyclerViewWeakReference.get().setAdapter(adapter);
            recyclerViewWeakReference.get().setVisibility(View.VISIBLE);
            progressBar.setProgress(100F);
            // HIDE THE SPINNER AFTER LOADING FEEDS
            progressBar.setVisibility(View.GONE);
        }

    }
}
