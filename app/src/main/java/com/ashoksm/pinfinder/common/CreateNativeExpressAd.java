package com.ashoksm.pinfinder.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashoksm.pinfinder.R;
import com.clockbyte.admobadapter.expressads.AdViewWrappingStrategyBase;
import com.clockbyte.admobadapter.expressads.AdmobExpressRecyclerAdapterWrapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * This class is used to create Ad.
 * Created by Ashok on 27/9/17.
 */

public class CreateNativeExpressAd {

    public static AdmobExpressRecyclerAdapterWrapper initNativeAd(Context context,
                                                                  RecyclerView.Adapter adapter) {
        String[] testDevicesIds = new String[]{AdRequest.DEVICE_ID_EMULATOR};
        return AdmobExpressRecyclerAdapterWrapper.builder(context)
                .setLimitOfAds(10)
                .setFirstAdIndex(2)
                .setNoOfDataBetweenAds(10)
                .setTestDeviceIds(testDevicesIds)
                .setSingleAdUnitId(context.getString(R.string.admob_small_native_ad_id))
                .setAdapter(adapter)
                .setAdViewWrappingStrategy(new AdViewWrappingStrategyBase() {
                    @NonNull
                    @Override
                    protected ViewGroup getAdViewWrapper(ViewGroup parent) {
                        return (ViewGroup) LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.native_express_ad_container,
                                        parent, false);
                    }

                    @Override
                    protected void recycleAdViewWrapper(@NonNull ViewGroup wrapper,
                                                        @NonNull NativeExpressAdView ad) {
                        //get the view which directly will contain ad
                        ViewGroup container = wrapper.findViewById(R.id.ad_container);
                        //iterating through all children of the container view and remove the first
                        // occurred {@link NativeExpressAdView}. It could be different with {@param ad}!!!*//*
                        for (int i = 0; i < container.getChildCount(); i++) {
                            View v = container.getChildAt(i);
                            if (v instanceof NativeExpressAdView) {
                                container.removeViewAt(i);
                                break;
                            }
                        }
                    }

                    @Override
                    protected void addAdViewToWrapper(@NonNull ViewGroup wrapper, @NonNull
                            NativeExpressAdView ad) {
                        //get the view which directly will contain ad
                        ViewGroup container = wrapper.findViewById(R.id.ad_container);
                        //add the {@param ad} directly to the end of container*//*
                        container.addView(ad);
                        container.findViewById(R.id.text).setVisibility(View.GONE);
                    }
                })
                .build();
    }
}
