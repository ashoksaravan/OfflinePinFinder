package com.ashoksm.pinfinder;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashoksm.pinfinder.adapter.CursorRecyclerViewAdapter;
import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.ashoksm.pinfinder.sqlite.BankSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.PinSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.RTOSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import java.util.regex.Pattern;

public class AllCodeListActivity extends ActivityBase {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private EditText searchBar;
    private int menuId;
    private String queryTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_code_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);

        searchBar = (EditText) findViewById(R.id.search_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchBar.getBackground().mutate().setColorFilter(getResources().getColor(R.color.icons,
                    getTheme()), PorterDuff.Mode.SRC_ATOP);
        }

        Intent intent = getIntent();
        menuId = intent.getIntExtra(MainActivity.EXTRA_MENU_ID, 0);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.item_divider);
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        setupRecyclerView(recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        new AsyncTask<Void, Void, Void>() {
            LinearLayout progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
            PinSQLiteHelper sqLiteHelper = new PinSQLiteHelper(AllCodeListActivity.this);
            BankSQLiteHelper branchHelper = new BankSQLiteHelper(AllCodeListActivity.this);
            STDSQLiteHelper stdsqLiteHelper = new STDSQLiteHelper(AllCodeListActivity.this);
            RTOSQLiteHelper rtosqLiteHelper = new RTOSQLiteHelper(AllCodeListActivity.this);

            AllCodeViewAdapter adapter;

            @Override
            protected void onPreExecute() {
                progressLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (menuId == R.id.nav_pincode) {
                    adapter = new AllCodeViewAdapter(sqLiteHelper.getAllPinCodes(""));
                } else if (menuId == R.id.nav_office) {
                    adapter = new AllCodeViewAdapter(sqLiteHelper.getAllOfficeNames(""));
                } else if (menuId == R.id.nav_ifsc) {
                    adapter = new AllCodeViewAdapter(branchHelper.getIFSCCodes(""));
                } else if (menuId == R.id.nav_micr) {
                    adapter = new AllCodeViewAdapter(branchHelper.getMICRCodes(""));
                } else if (menuId == R.id.nav_branch_name) {
                    adapter = new AllCodeViewAdapter(branchHelper.getBranchNames(""));
                } else if (menuId == R.id.nav_std_city) {
                    adapter = new AllCodeViewAdapter(stdsqLiteHelper.getAllCityNames(""));
                } else if (menuId == R.id.nav_std_code) {
                    adapter = new AllCodeViewAdapter(stdsqLiteHelper.getAllSTDCodes(""));
                } else if (menuId == R.id.nav_rto_code) {
                    adapter = new AllCodeViewAdapter(rtosqLiteHelper.getAllRTOCodes(""));
                } else if (menuId == R.id.nav_rto_city) {
                    adapter = new AllCodeViewAdapter(rtosqLiteHelper.getAllCityNames(""));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                recyclerView.setAdapter(adapter);
                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        queryTxt = s.toString();
                        if (menuId == R.id.nav_pincode) {
                            adapter.changeCursor(sqLiteHelper.getAllPinCodes(queryTxt));
                        } else if (menuId == R.id.nav_office) {
                            adapter.changeCursor(sqLiteHelper.getAllOfficeNames(queryTxt));
                        } else if (menuId == R.id.nav_ifsc) {
                            adapter.changeCursor(branchHelper.getIFSCCodes(queryTxt));
                        } else if (menuId == R.id.nav_micr) {
                            adapter.changeCursor(branchHelper.getMICRCodes(queryTxt));
                        } else if (menuId == R.id.nav_branch_name) {
                            adapter.changeCursor(branchHelper.getBranchNames(queryTxt));
                        } else if (menuId == R.id.nav_std_city) {
                            adapter.changeCursor(stdsqLiteHelper.getAllCityNames(queryTxt));
                        } else if (menuId == R.id.nav_std_code) {
                            adapter.changeCursor(stdsqLiteHelper.getAllSTDCodes(queryTxt));
                        } else if (menuId == R.id.nav_rto_city) {
                            adapter.changeCursor(rtosqLiteHelper.getAllCityNames(queryTxt));
                        } else if (menuId == R.id.nav_rto_code) {
                            adapter.changeCursor(rtosqLiteHelper.getAllRTOCodes(queryTxt));
                        }
                    }
                });
                progressLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    public class AllCodeViewAdapter
            extends CursorRecyclerViewAdapter<AllCodeViewAdapter.ViewHolder> {

        public AllCodeViewAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_code_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, Cursor cursor, int position) {
            holder.mIdView
                    .setText(String.valueOf(position + 1));
            if (queryTxt != null && queryTxt.length() > 0) {
                String origString = cursor.getString(cursor.getColumnIndex(PinSQLiteHelper
                        .ID));
                origString = origString.replaceAll("(?i)" + Pattern.quote(queryTxt), "<font " +
                        "color='#ffc107'>" + queryTxt + "</font>");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.mContentView.setText(
                            Html.fromHtml(origString, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                } else {
                    holder.mContentView.setText(Html.fromHtml(origString));
                }
            } else {
                holder.mContentView.setText(cursor.getString(cursor.getColumnIndex
                        (PinSQLiteHelper.ID)));
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        callFragment(holder.mContentView.getText().toString().trim());
                    } else {
                        callActivity(holder.mContentView.getText().toString().trim());
                    }
                }
            });
        }

        private void callActivity(String name) {
            Intent intent = null;
            if (menuId == R.id.nav_pincode || menuId == R.id.nav_office) {
                intent = new Intent(getApplicationContext(), DisplayPinCodeResultActivity.class);
                intent.putExtra(PincodeFragment.EXTRA_STATE, "");
                intent.putExtra(PincodeFragment.EXTRA_DISTRICT, "");
                intent.putExtra(PincodeFragment.EXTRA_OFFICE, name);
            } else if (menuId == R.id.nav_ifsc || menuId == R.id.nav_micr || menuId
                    == R.id.nav_branch_name) {
                intent = new Intent(getApplicationContext(), DisplayBankBranchResultActivity.class);
                intent.putExtra(IFSCFragment.EXTRA_STATE, "");
                intent.putExtra(IFSCFragment.EXTRA_DISTRICT, "");
                intent.putExtra(IFSCFragment.EXTRA_BANK, "");
                intent.putExtra(IFSCFragment.EXTRA_BRANCH, name);
                if (menuId == R.id.nav_ifsc) {
                    intent.putExtra(IFSCFragment.EXTRA_ACTION, "IFSC");
                } else if (menuId == R.id.nav_micr) {
                    intent.putExtra(IFSCFragment.EXTRA_ACTION, "MICR");
                } else {
                    intent.putExtra(IFSCFragment.EXTRA_ACTION, "BRANCH");
                }
            } else if (menuId == R.id.nav_std_city || menuId == R.id.nav_std_code) {
                intent = new Intent(getApplicationContext(), DisplaySTDResultActivity.class);
                intent.putExtra(STDFragment.EXTRA_STATE, "");
                intent.putExtra(IFSCFragment.EXTRA_ACTION, "STD");
                intent.putExtra(STDFragment.EXTRA_CITY, name);
            } else if (menuId == R.id.nav_rto_city || menuId == R.id.nav_rto_code) {
                intent = new Intent(getApplicationContext(), DisplayRTOResultActivity.class);
                intent.putExtra(RTOFragment.EXTRA_STATE, "");
                intent.putExtra(IFSCFragment.EXTRA_ACTION, "RTO");
                intent.putExtra(RTOFragment.EXTRA_CITY, name);
            }
            if (intent != null) {
                intent.putExtra(MainActivity.EXTRA_SHOW_FAV, false);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, 0);
        }

        private void callFragment(String name) {
            Bundle arguments = new Bundle();
            if (menuId == R.id.nav_pincode || menuId == R.id.nav_office) {
                arguments.putString(IFSCFragment.EXTRA_ACTION, "");
                arguments.putString(PincodeFragment.EXTRA_OFFICE, name);
            } else if (menuId == R.id.nav_ifsc || menuId == R.id.nav_micr || menuId
                    == R.id.nav_branch_name) {
                if (menuId == R.id.nav_ifsc) {
                    arguments.putString(IFSCFragment.EXTRA_ACTION, "IFSC");
                } else if (menuId == R.id.nav_micr) {
                    arguments.putString(IFSCFragment.EXTRA_ACTION, "MICR");
                } else {
                    arguments.putString(IFSCFragment.EXTRA_ACTION, "BRANCH");
                }
                arguments.putString(IFSCFragment.EXTRA_BRANCH, name);
            } else if (menuId == R.id.nav_std_city || menuId == R.id.nav_std_code) {
                arguments.putString(IFSCFragment.EXTRA_ACTION, "STD");
                arguments.putString(STDFragment.EXTRA_CITY, name);
            } else if (menuId == R.id.nav_rto_city || menuId == R.id.nav_rto_code) {
                arguments.putString(IFSCFragment.EXTRA_ACTION, "RTO");
                arguments.putString(STDFragment.EXTRA_CITY, name);
            }
            AllCodeDetailFragment fragment = new AllCodeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;

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
    }
}
