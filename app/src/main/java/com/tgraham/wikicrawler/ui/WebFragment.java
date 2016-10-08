package com.tgraham.wikicrawler.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebFragment extends com.tgraham.wikicrawler.ui.WebViewFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String linkName = bundle.getString(ViewPagerFragment.KEY_WEB_PAGE);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        WebView webView = getWebView();

        webView.getSettings().setJavaScriptEnabled(true);

        final Activity activity = getActivity();

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        try {
            linkName = URLEncoder.encode(linkName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webView.loadUrl("https://en.wikipedia.org/wiki/" + linkName);
        webView.scrollTo(150,150);

        return view;
    }


    public static WebFragment newInstance(int index) {
        WebFragment webFragment = new WebFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        webFragment.setArguments(args);
        return webFragment;
    }

}