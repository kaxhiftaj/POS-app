package com.techease.posapp.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techease.posapp.R;
import com.techease.posapp.utils.Configuration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HomeFragment extends Fragment {


    @BindView(R.id.missions_tab)
    TabLayout tabLayout;
    @BindView(R.id.missions_pager)
    ViewPager viewPager;
    Unbinder unbinder;
    android.support.v7.app.AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, v);

        sharedPreferences = getActivity().getSharedPreferences(Configuration.MY_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor = sharedPreferences.edit();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        tabLayout.addTab(tabLayout.newTab().setText("JOBS"));
        tabLayout.addTab(tabLayout.newTab().setText("MAP"));

        viewPager.setAdapter(new PagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        reduceMarginsInTabs(tabLayout, 20);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    public static void reduceMarginsInTabs(TabLayout tabLayout, int marginOffset) {

        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            for (int i = 0; i < ((ViewGroup) tabStrip).getChildCount(); i++) {
                View tabView = tabStripGroup.getChildAt(i);
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).leftMargin = marginOffset;
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).rightMargin = marginOffset;
                }
            }

            tabLayout.requestLayout();
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(android.support.v4.app.FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    MissionsFragment frag = new MissionsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", "1");
                    frag.setArguments(bundle);
                    return frag;

                case 1:
                    MapFragment frag1 = new MapFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("club_id", "1");
                    frag1.setArguments(bundle1);
                    return frag1;


                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}

