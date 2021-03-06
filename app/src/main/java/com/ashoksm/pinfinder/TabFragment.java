package com.ashoksm.pinfinder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class TabFragment extends Fragment {

    public static int int_items = 7;
    public TabLayout tabLayout;
    public ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, container, false);
        tabLayout = x.findViewById(R.id.tabs);
        viewPager = x.findViewById(R.id.viewpager);

        /*
         Set an Adapter for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /*
          Now , this is a workaround ,
          The setupWithViewPager dose't works without the runnable .
          Maybe a Support Library Bug .
         */

        tabLayout.post(() -> tabLayout.setupWithViewPager(viewPager));

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
                    return new IPCFragment();
                case 3:
                    return new StationsFragment();
                case 4:
                    return new TrainsFragment();
                case 5:
                    return new STDFragment();
                case 6:
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
                    return "IPC";
                case 3:
                    return "Stations";
                case 4:
                    return "Trains";
                case 5:
                    return "STD";
                case 6:
                    return "RTO";
            }
            return null;
        }
    }

}
