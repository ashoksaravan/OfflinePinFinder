package com.ashoksm.pinfinder.common;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashoksm.pinfinder.R;
import com.clockbyte.admobadapter.NativeAdLayoutContext;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

public class ContentAdLayoutContext extends NativeAdLayoutContext {

    public ContentAdLayoutContext(int mAdLayoutId){
        setAdLayoutId(mAdLayoutId);
    }

    @Override
    public void bind(NativeAdView nativeAdView, NativeAd nativeAd) {
        if (nativeAdView == null || nativeAd == null) return;
        if(!(nativeAd instanceof NativeContentAd) || !(nativeAdView instanceof NativeContentAdView))
            throw new ClassCastException();

        NativeContentAd ad = (NativeContentAd) nativeAd;
        NativeContentAdView adView = (NativeContentAdView) nativeAdView;

        // Locate the view that will hold the headline, set its text, and call the
        // NativeContentAdView's setHeadlineView method to register it.
        TextView tvHeader = (TextView) nativeAdView.findViewById(R.id.contentad_headline);
        tvHeader.setText(ad.getHeadline());
        adView.setHeadlineView(tvHeader);

        TextView tvDescription = (TextView) nativeAdView.findViewById(R.id.contentad_body);
        tvDescription.setText(ad.getBody());
        adView.setBodyView(tvDescription);

        ImageView ivLogo = (ImageView) nativeAdView.findViewById(R.id.contentad_image);
        NativeAd.Image logoImage = ad.getLogo();
        if(logoImage != null) {
            ivLogo.setImageDrawable(logoImage.getDrawable());
        } else {
            ivLogo.setImageDrawable(ad.getImages().get(0).getDrawable());
        }
        adView.setLogoView(ivLogo);

        TextView tvAdvertiser = (TextView) nativeAdView.findViewById(R.id.contentad_advertiser);
        tvAdvertiser.setText(ad.getAdvertiser());
        adView.setAdvertiserView(tvAdvertiser);

        // Call the NativeContentAdView's setNativeAd method to register the
        // NativeAdObject.
        nativeAdView.setNativeAd(nativeAd);
    }
}
