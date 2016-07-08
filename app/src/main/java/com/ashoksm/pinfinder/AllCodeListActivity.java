package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ashoksm.pinfinder.common.AllCodeItem;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.PinFinderSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AllCodeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AllCodeListActivity extends ActivityBase {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_code_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        PinFinderSQLiteHelper pinFinderSQLiteHelper = new PinFinderSQLiteHelper(this);
        final AllCodeListRecyclerViewAdapter adapter =
                new AllCodeListRecyclerViewAdapter(pinFinderSQLiteHelper.getAllPinCodes());
        recyclerView.setAdapter(
                adapter);
        EditText searchBar = (EditText) findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    adapter.getFilter().filter(s.toString());
                }
            });
        }
    }

    public class AllCodeListRecyclerViewAdapter
            extends RecyclerView.Adapter<AllCodeListRecyclerViewAdapter.ViewHolder> implements
            Filterable {

        private final List<AllCodeItem> mValues;
        private final List<AllCodeItem> filteredValues = new ArrayList<>();

        public AllCodeListRecyclerViewAdapter(List<AllCodeItem> items) {
            mValues = items;
            filteredValues.addAll(items);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_code_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = filteredValues.get(position);
            holder.mIdView.setText(filteredValues.get(position).getPincode());
            holder.mContentView.setText(filteredValues.get(position).getOfficeName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(AllCodeDetailFragment.ARG_ITEM_ID,
                                holder.mItem.getPincode());
                        arguments.putString(AllCodeDetailFragment.ARG_ITEM_NAME,
                                holder.mItem.getOfficeName());
                        AllCodeDetailFragment fragment = new AllCodeDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, AllCodeDetailActivity.class);
                        intent.putExtra(AllCodeDetailFragment.ARG_ITEM_ID,
                                holder.mItem.getPincode());
                        intent.putExtra(AllCodeDetailFragment.ARG_ITEM_NAME,
                                holder.mItem.getOfficeName());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredValues.size();
        }

        @Override
        public Filter getFilter() {
            return new AllCodeFilter(this, mValues);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public AllCodeItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        private class AllCodeFilter extends Filter {

            private final AllCodeListRecyclerViewAdapter adapter;

            private final List<AllCodeItem> originalList;

            private final List<AllCodeItem> filteredList;

            public AllCodeFilter(AllCodeListRecyclerViewAdapter adapter,
                                 List<AllCodeItem> originalList) {
                this.adapter = adapter;
                this.originalList = originalList;
                this.filteredList = new ArrayList<>();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();

                    for (final AllCodeItem item : originalList) {
                        if (item.getPincode().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                adapter.filteredValues.clear();
                adapter.filteredValues.addAll((ArrayList<AllCodeItem>) filterResults.values);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
