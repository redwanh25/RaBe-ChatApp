package com.example.redwan.firebasechatapp;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private ViewPager mViewPager;
    private SectionsPagerAdapterNotification mSectionPagerAdapterNotification;
    private TabLayout mTabLayout;
    private View mView;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_notification, container, false);

        mViewPager = mView.findViewById(R.id.main_tabPager_notification);
        mSectionPagerAdapterNotification = new SectionsPagerAdapterNotification(getFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapterNotification);

        mTabLayout = mView.findViewById(R.id.main_tabs_notification);
        mTabLayout.setupWithViewPager(mViewPager);

        return mView;
    }

}
