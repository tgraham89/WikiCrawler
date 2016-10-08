package com.tgraham.wikicrawler.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.tgraham.wikicrawler.ui.LinkFragment;
import com.tgraham.wikicrawler.ui.MainActivity;
import com.tgraham.wikicrawler.ui.SecondActivity;
import com.tgraham.wikicrawler.ui.ViewPagerFragment;
import com.tgraham.wikicrawler.ui.WebFragment;

import java.util.Map;

public class PageAdapter extends FragmentPagerAdapter {
    Activity mActivity;
    String mWebPage;

    private final FragmentManager mFragmentManager;

    public PageAdapter(FragmentManager fragmentManager, Activity activity, String webPage) {
        super(fragmentManager);
        mFragmentManager = fragmentManager;
        mActivity = activity;
        mWebPage = webPage;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ViewPagerFragment.KEY_LINK_INDEX, position + 1);
        bundle.putString(ViewPagerFragment.KEY_WEB_PAGE, mWebPage);
        String activityToString = mActivity.getLocalClassName();
        String[] string;
        if(activityToString.equals("ui.MainActivity")) {
            string = MainActivity.getLinkNames();
        } else {
            string = SecondActivity.getLinkNames();
        }
        bundle.putStringArray(ViewPagerFragment.KEY_LINK_STRING, string);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    public interface SecondPageFragmentListener
    {
        void onSwitchToNextFragment();


    }


}
