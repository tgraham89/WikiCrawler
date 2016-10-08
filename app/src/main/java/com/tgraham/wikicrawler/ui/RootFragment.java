package com.tgraham.wikicrawler.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tgraham.wikicrawler.R;

public class RootFragment extends Fragment {

    private static final String TAG = "RootFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String linkName = getArguments().getString(ViewPagerFragment.KEY_WEB_PAGE);
        int index = getArguments().getInt(ViewPagerFragment.KEY_LINK_INDEX);
		/* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.root_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putInt(ViewPagerFragment.KEY_LINK_INDEX, index);
        args.putString(ViewPagerFragment.KEY_WEB_PAGE, linkName);
        webFragment.setArguments(args);

        transaction.replace(R.id.root_frame, webFragment);

        transaction.commit();

        return view;
    }
}