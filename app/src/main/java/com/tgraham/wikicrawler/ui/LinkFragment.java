package com.tgraham.wikicrawler.ui;

import android.os.Bundle;

public class LinkFragment extends LinkListFragment {
    public static final String ARG_LINK_NAME = "link_name";
    private String[] mLinkNames;



    @Override
    public String[] getContents(int index) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mLinkNames = bundle.getStringArray(ARG_LINK_NAME);
        }

        return mLinkNames;
    }
}
