package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.ashoksm.pinfinder.sqlite.RailWaysSQLiteHelper;
import com.ashoksm.pinfinder.sqlite.STDSQLiteHelper;
import com.clockbyte.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.clockbyte.admobadapter.expressads.NativeExpressAdViewHolder;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

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
    private AdmobExpressRecyclerAdapterWrapper adAdapterWrapper;
    private AllCodeViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_code_list);
        if (isXLargeScreen(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_small_native_ad_id));
        loadAd();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        setSupportActionBar(toolbar);

        searchBar = findViewById(R.id.search_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchBar.getBackground().mutate().setColorFilter(getResources().getColor(R.color.icons,
                    getTheme()), PorterDuff.Mode.SRC_ATOP);
        }

        Intent intent = getIntent();
        menuId = intent.getIntExtra(MainActivity.EXTRA_MENU_ID, 0);

        RecyclerView recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(false);
        NpaLinearLayoutManager layoutManager = new NpaLinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
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
            RailWaysSQLiteHelper railSQLiteHelper = new RailWaysSQLiteHelper(AllCodeListActivity
                    .this);

            @Override
            protected void onPreExecute() {
                progressLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                switch (menuId) {
                    case R.id.nav_pincode:
                        adapter = new AllCodeViewAdapter(sqLiteHelper.getAllPinCodes(""));
                        break;
                    case R.id.nav_office:
                        adapter = new AllCodeViewAdapter(sqLiteHelper.getAllOfficeNames(""));
                        break;
                    case R.id.nav_ifsc:
                        adapter = new AllCodeViewAdapter(branchHelper.getIFSCCodes(""));
                        break;
                    case R.id.nav_micr:
                        adapter = new AllCodeViewAdapter(branchHelper.getMICRCodes(""));
                        break;
                    case R.id.nav_branch_name:
                        adapter = new AllCodeViewAdapter(branchHelper.getBranchNames(""));
                        break;
                    case R.id.nav_std_city:
                        adapter = new AllCodeViewAdapter(stdsqLiteHelper.getAllCityNames(""));
                        break;
                    case R.id.nav_std_code:
                        adapter = new AllCodeViewAdapter(stdsqLiteHelper.getAllSTDCodes(""));
                        break;
                    case R.id.nav_rto_code:
                        adapter = new AllCodeViewAdapter(rtosqLiteHelper.getAllRTOCodes(""));
                        break;
                    case R.id.nav_rto_city:
                        adapter = new AllCodeViewAdapter(rtosqLiteHelper.getAllCityNames(""));
                        break;
                    case R.id.nav_station_code:
                        adapter = new AllCodeViewAdapter(railSQLiteHelper.getStationCodes(""));
                        break;
                    case R.id.nav_station_name:
                        adapter = new AllCodeViewAdapter(railSQLiteHelper.getStationNames(""));
                        break;
                    case R.id.nav_train_name_or_no:
                        adapter = new AllCodeViewAdapter(railSQLiteHelper.getTrains(""));
                        break;
                    case R.id.nav_train_via_station:
                        adapter = new AllCodeViewAdapter(railSQLiteHelper.getStns(""));
                        break;
                    default:
                        break;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                initNativeAd();
                recyclerView.setAdapter(adAdapterWrapper);
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
                        recyclerView.stopScroll();
                        switch (menuId) {
                            case R.id.nav_pincode:
                                adapter.changeCursor(sqLiteHelper.getAllPinCodes(queryTxt));
                                break;
                            case R.id.nav_office:
                                adapter.changeCursor(sqLiteHelper.getAllOfficeNames(queryTxt));
                                break;
                            case R.id.nav_ifsc:
                                adapter.changeCursor(branchHelper.getIFSCCodes(queryTxt));
                                break;
                            case R.id.nav_micr:
                                adapter.changeCursor(branchHelper.getMICRCodes(queryTxt));
                                break;
                            case R.id.nav_branch_name:
                                adapter.changeCursor(branchHelper.getBranchNames(queryTxt));
                                break;
                            case R.id.nav_std_city:
                                adapter.changeCursor(stdsqLiteHelper.getAllCityNames(queryTxt));
                                break;
                            case R.id.nav_std_code:
                                adapter.changeCursor(stdsqLiteHelper.getAllSTDCodes(queryTxt));
                                break;
                            case R.id.nav_rto_code:
                                adapter.changeCursor(rtosqLiteHelper.getAllCityNames(queryTxt));
                                break;
                            case R.id.nav_rto_city:
                                adapter.changeCursor(rtosqLiteHelper.getAllRTOCodes(queryTxt));
                                break;
                            case R.id.nav_station_code:
                                adapter.changeCursor(railSQLiteHelper.getStationCodes(queryTxt));
                                break;
                            case R.id.nav_station_name:
                                adapter.changeCursor(railSQLiteHelper.getStationNames(queryTxt));
                                break;
                            case R.id.nav_train_name_or_no:
                                adapter.changeCursor(railSQLiteHelper.getTrains(queryTxt));
                                break;
                            case R.id.nav_train_via_station:
                                adapter.changeCursor(railSQLiteHelper.getStns(queryTxt));
                                break;
                            default:
                                break;
                        }
                    }
                });
                progressLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    class AllCodeViewAdapter
            extends CursorRecyclerViewAdapter<AllCodeViewAdapter.ViewHolder> {

        AllCodeViewAdapter(Cursor cursor) {
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
                intent = new Intent(getApplicationContext(), DisplayBankResultActivity.class);
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
            } else if (menuId == R.id.nav_station_code || menuId == R.id.nav_station_name) {
                intent = new Intent(getApplicationContext(), DisplayStationResultActivity.class);
                intent.putExtra(StationsFragment.EXTRA_STATE, "");
                intent.putExtra(StationsFragment.EXTRA_CITY, "");
                intent.putExtra(IFSCFragment.EXTRA_ACTION, "RAIL");
                intent.putExtra(StationsFragment.EXTRA_STATION, name);
            } else if (menuId == R.id.nav_train_name_or_no) {
                intent = new Intent(getApplicationContext(), DisplayTrainResultActivity.class);
                intent.putExtra(TrainsFragment.EXTRA_TRAIN, name);
                intent.putExtra(TrainsFragment.EXTRA_STARTS, "");
                intent.putExtra(TrainsFragment.EXTRA_ENDS, "");
            } else if (menuId == R.id.nav_train_via_station) {
                intent = new Intent(getApplicationContext(), DisplayTrainResultActivity.class);
                intent.putExtra(TrainsFragment.EXTRA_TRAIN, "");
                intent.putExtra(TrainsFragment.EXTRA_STARTS, name);
                intent.putExtra(TrainsFragment.EXTRA_ENDS, "");
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
            } else if (menuId == R.id.nav_station_code || menuId == R.id.nav_station_name) {
                arguments.putString(IFSCFragment.EXTRA_ACTION, "RAIL");
                arguments.putString(StationsFragment.EXTRA_STATION, name);
            } else if (menuId == R.id.nav_train_name_or_no) {
                arguments.putString(TrainsFragment.EXTRA_TRAIN, name);
                arguments.putString(TrainsFragment.EXTRA_STARTS, "");
                arguments.putString(TrainsFragment.EXTRA_ENDS, "");
                arguments.putString(IFSCFragment.EXTRA_ACTION, "TRAIN");
            } else if (menuId == R.id.nav_train_via_station) {
                arguments.putString(TrainsFragment.EXTRA_TRAIN, "");
                arguments.putString(TrainsFragment.EXTRA_STARTS, name);
                arguments.putString(TrainsFragment.EXTRA_ENDS, "");
                arguments.putString(IFSCFragment.EXTRA_ACTION, "TRAIN");
            }
            AllCodeDetailFragment fragment = new AllCodeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIdView;
            final TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = view.findViewById(R.id.id);
                mContentView = view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private boolean isXLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void loadAd() {
        final LinearLayout adParent = this.findViewById(R.id.ad);
        final AdView ad = new AdView(this);
        ad.setAdUnitId(getString(R.string.admob_id));
        ad.setAdSize(AdSize.SMART_BANNER);

        final AdListener listener = new AdListener() {
            @Override
            public void onAdLoaded() {
                adParent.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adParent.setVisibility(View.GONE);
                super.onAdFailedToLoad(errorCode);
            }
        };

        ad.setAdListener(listener);

        adParent.addView(ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }


    @SuppressWarnings("unchecked")
    private void initNativeAd() {
        String[] testDevicesIds = new String[]{AdRequest.DEVICE_ID_EMULATOR};
        adAdapterWrapper = new AdmobExpressRecyclerAdapterWrapper(this, getString(R.string
                .admob_small_native_ad_id), testDevicesIds) {
            @Override
            protected ViewGroup wrapAdView(NativeExpressAdViewHolder adViewHolder, ViewGroup parent,
                                           int viewType) {

                //get ad view
                NativeExpressAdView adView = adViewHolder.getAdView();

                RecyclerView.LayoutParams lp =
                        new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                                RecyclerView.LayoutParams.WRAP_CONTENT);
                CardView cardView = new CardView(AllCodeListActivity.this);
                cardView.setLayoutParams(lp);

                TextView textView = new TextView(AllCodeListActivity.this);
                textView.setLayoutParams(lp);
                textView.setText(R.string.ad_loading);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(getResources().getColor(R.color.accent, getTheme()));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.accent));
                }

                cardView.addView(textView);
                //wrapping
                cardView.addView(adView);
                //return wrapper view
                return cardView;
            }
        };
        adAdapterWrapper.setAdapter((RecyclerView.Adapter) adapter);
        adAdapterWrapper.setLimitOfAds(3);
        adAdapterWrapper.setNoOfDataBetweenAds(10);
        adAdapterWrapper.setFirstAdIndex(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adAdapterWrapper != null) {
            adAdapterWrapper.release();
        }
    }

    /**
     * No Predictive Animations LinearLayoutManager
     */
    private static class NpaLinearLayoutManager extends LinearLayoutManager {


        NpaLinearLayoutManager(Context context) {
            super(context);
        }

        /**
         * Disable predictive animations. There is a bug in RecyclerView which causes views that
         * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
         * adapter size has decreased since the ViewHolder was recycled.
         */
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }


    }
}
