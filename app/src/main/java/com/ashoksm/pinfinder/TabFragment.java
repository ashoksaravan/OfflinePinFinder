package com.ashoksm.pinfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TabFragment extends Fragment {

    public TabLayout tabLayout;
    public ViewPager viewPager;
    public static int int_items = 6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, container, false);
        tabLayout = x.findViewById(R.id.tabs);
        viewPager = x.findViewById(R.id.viewpager);

        /**
         *Set an Adapter for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PincodeFragment();
                case 1:
                    return new IFSCFragment();
                case 2:
                    return new StationsFragment();
                case 3:
                    return new TrainsFragment();
                case 4:
                    return new STDFragment();
                case 5:
                    return new RTOFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Pincode";
                case 1:
                    return "IFSC";
                case 2:
                    return "Stations";
                case 3:
                    return "Trains";
                case 4:
                    return "STD";
                case 5:
                    return "RTO";
            }
            return null;
        }
    }

}
