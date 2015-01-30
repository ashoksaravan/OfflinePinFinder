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

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.common.activities.ActivityBase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends ActivityBase {

	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		setSupportActionBar(toolbar);

		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getString(R.string.admob_id));

		// load ad
		final LinearLayout adParent = (LinearLayout) this.findViewById(R.id.ad);
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
		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);

		if (savedInstanceState == null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
			transaction.replace(R.id.pinfinder_content_fragment, fragment);
			transaction.commit();
		}
	}

	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	@Override
	public void onBackPressed() {
		displayInterstitial();
		super.onBackPressed();
	}
}
