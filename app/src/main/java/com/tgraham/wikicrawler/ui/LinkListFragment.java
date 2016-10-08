package com.tgraham.wikicrawler.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tgraham.wikicrawler.R;
import com.tgraham.wikicrawler.adapters.PageAdapter;

public abstract class LinkListFragment extends Fragment {
    private static final String KEY_CHECKED_BOXES = "key_checked_boxes";
    private CheckBox[] mCheckBoxes;
    private int mCounter;
    PageAdapter mPageAdapter;
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int index = getArguments().getInt(ViewPagerFragment.KEY_LINK_INDEX);
        String[] contents = getArguments().getStringArray(ViewPagerFragment.KEY_LINK_STRING);
        View view = inflater.inflate(R.layout.fragment_checkboxes, container,false);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.checkBoxesLayout);
        //String[] contents = getContents(index);

        mCheckBoxes = new CheckBox[contents.length];
        boolean[] checkedBoxes = new boolean[mCheckBoxes.length];
        if (savedInstanceState != null && savedInstanceState.getBooleanArray(KEY_CHECKED_BOXES) != null) {
            checkedBoxes = savedInstanceState.getBooleanArray(KEY_CHECKED_BOXES);
        }

        setUpCheckBoxes(contents, linearLayout, checkedBoxes);


        return view;
    }

    public abstract String[] getContents(int index);

    private void setUpCheckBoxes(String[] contents, ViewGroup container, final boolean[] checkedBoxes) {
        int i = 0;
        for (String content : contents) {
            mCheckBoxes[i] = new CheckBox(getActivity());
            mCheckBoxes[i].setPadding(8, 16, 8, 16);
            mCheckBoxes[i].setTextSize(20f);
            mCheckBoxes[i].setText(content);
            container.addView(mCheckBoxes[i]);
            if (checkedBoxes[i]) {
                mCheckBoxes[i].toggle();
            }
            final int a = i;
            mCheckBoxes[a].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCheckBoxes[a].isChecked()) {
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        WebFragment webFragment = new WebFragment();
                        Bundle args = new Bundle();
                        args.putString(ViewPagerFragment.KEY_WEB_PAGE, mCheckBoxes[a].getText().toString());
                        webFragment.setArguments(args);
                        fragmentTransaction.replace(R.id.root_frame, webFragment);
                        fragmentTransaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        final ViewPager viewPager = ViewPagerFragment.getFirstViewPager();
                        //viewPager.setCurrentItem(1, true);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(1, true);
                            }

                        });
                    }
                }
            });
            i++;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        boolean[] stateOfCheckBoxes = new boolean[mCheckBoxes.length];
        int i = 0;
        for (CheckBox checkBox : mCheckBoxes) {
            stateOfCheckBoxes[i] = checkBox.isChecked();
            i++;
        }
        outState.putBooleanArray(KEY_CHECKED_BOXES, stateOfCheckBoxes);
        super.onSaveInstanceState(outState);
    }


}
