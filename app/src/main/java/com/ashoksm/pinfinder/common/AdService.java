package com.ashoksm.pinfinder.common;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.ashoksm.pinfinder.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * This class is used to load banner ads.
 */

public class AdService {

    public static void loadBannerAd(Activity activity) {
        final LinearLayout adParent = activity.findViewById(R.id.ad);
        final AdView ad = new AdView(activity);
        ad.setAdUnitId(activity.getString(R.string.admob_id));
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
}
