package com.tgraham.wikicrawler.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tgraham.wikicrawler.R;

public class ViewPagerFragment extends Fragment {
    public static final String KEY_LINK_INDEX = "link_index";
    public static final String KEY_LINK_STRING = "link_string";
    public static final String KEY_WEB_PAGE = "web_page";

    public static ViewPager mViewPager;
    public static ViewPager mFirstViewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int index = getArguments().getInt(KEY_LINK_INDEX);
        String[] linkNames = getArguments().getStringArray(KEY_LINK_STRING);
        String webPage = getArguments().getString(KEY_WEB_PAGE);

        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        final LinkFragment linkFragment = new LinkFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LINK_INDEX, index);
        args.putStringArray(KEY_LINK_STRING, linkNames);
        linkFragment.setArguments(args);

        final RootFragment rootFragment = new RootFragment();
        args = new Bundle();
        args.putInt(KEY_LINK_INDEX, index);
        args.putString(KEY_WEB_PAGE, webPage);
        rootFragment.setArguments(args);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? linkFragment : rootFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Links" : "Page";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        if (index == 2 && getActivity().getLocalClassName().equals("ui.MainActivity")) {
            mFirstViewPager = mViewPager;
        } else if (index == 1 && getActivity().getLocalClassName().equals("ui.SecondActivity")) {
            mFirstViewPager = mViewPager;
        }

        return view;
    }


    public static String getFragmentTag(int pos) {
        return "android:switcher:" + R.id.viewPager + ":" + pos;
    }

    public static ViewPager getViewPager() {
        return mViewPager;
    }

    public static ViewPager getFirstViewPager() {
        return mFirstViewPager;
    }
}
