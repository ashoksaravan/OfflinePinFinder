/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashoksm.pinfinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends ActivityBase {

    private InterstitialAd mInterstitialAd;
    private Class clazz;
    public static final String EXTRA_SHOW_FAV = "EXTRA_SHOW_FAV";
    public static final String EXTRA_MENU_ID = "EXTRA_MENU_ID";
    private DrawerLayout mDrawerLayout;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private boolean gpsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load ad
        loadAd();

        /*
         Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = findViewById(R.id.drawerLayout);
        NavigationView mNavigationView = findViewById(R.id.shitstuff);

        /*
          Lets inflate the very first fragment
          Here , we are inflating the TabFragment as the first Fragment
         */
        if (!isFinishing()) {
            FragmentManager mFragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new TabFragment())
                    .commitAllowingStateLoss();
        }

        if (ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        //load floating button
        addFloatingButton();

        /*
          Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        Intent intent = null;
                        if (menuItem.getItemId() != R.id.nav_near_by_post_office && menuItem
                                .getItemId() != R.id.nav_near_by_bank && menuItem.getItemId() !=
                                R.id.nav_near_by_atm && menuItem.getItemId() != R.id
                                .nav_near_by_railway_station) {
                            intent = new Intent(getApplicationContext(), AllCodeListActivity.class);
                        } else {
                            locationManager =
                                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            gpsStatus = locationManager.isProviderEnabled(LocationManager
                                    .GPS_PROVIDER);
                            if (gpsStatus) {
                                intent = new Intent(getApplicationContext(),
                                        NearByPlacesActivity.class);
                            } else {
                                Toast.makeText(MainActivity.this, "Please turn on the GPS!!!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        if (intent != null) {
                            intent.putExtra(EXTRA_MENU_ID, menuItem.getItemId());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_out_left, 0);
                        }
                        return false;
                    }
                });

        /*
          Setup Drawer Toggle of the Toolbar
         */

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                        R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    private void addFloatingButton() {
        final FloatingActionMenu actionMenu =
                findViewById(R.id.floatingActionMenu);

        FloatingActionButton pincodeButton =
                findViewById(R.id.floating_pincode);
        pincodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                clazz = DisplayPinCodeResultActivity.class;
                performSearch();
            }
        });

        FloatingActionButton ifscButton =
                findViewById(R.id.floating_ifsc);
        ifscButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                clazz = DisplayBankResultActivity.class;
                performSearch();
            }
        });

        FloatingActionButton stdButton =
                findViewById(R.id.floating_std);
        stdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                clazz = DisplaySTDResultActivity.class;
                performSearch();
            }
        });

        FloatingActionButton rtoButton =
                findViewById(R.id.floating_rto);
        rtoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(true);
                clazz = DisplayRTOResultActivity.class;
                performSearch();
            }
        });
        actionMenu.setClosedOnTouchOutside(true);
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

        //load ad
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_left, 0);
        //load ad
        if (!isFinishing()) {
            mInterstitialAd = newInterstitialAd();
            loadInterstitial();
        }
    }

    private InterstitialAd newInterstitialAd() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.admob_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdClosed() {
                finish();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            super.onBackPressed();
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void performSearch() {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(EXTRA_SHOW_FAV, true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, 0);
    }

    @Override
    public void onBackPressed() {
        showInterstitial();
    }
}
