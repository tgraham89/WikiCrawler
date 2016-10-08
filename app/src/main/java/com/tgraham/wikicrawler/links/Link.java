package com.tgraham.wikicrawler.links;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.tgraham.wikicrawler.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Link {
    public static final String TAG = MainActivity.class.getSimpleName();

    private String mTitle;
    private int mSubLinkNumber;
    private Link[] mLinks;
    private String[] mLinkNames;
    public static String name = "Barack Obama";

    public Link[] getLinks() {
        return mLinks;
    }

    public void setLinks(Link[] links) {
        mLinks = links;
    }

    public String[] getLinkNames() {
        return mLinkNames;
    }

    public void setLinkNames(String[] linkNames) {
        mLinkNames = linkNames;
    }

    public int getSubLinkNumber() {
        return mSubLinkNumber;
    }

    public void setSubLinkNumber(int subLinkNumber) {
        this.mSubLinkNumber = subLinkNumber;
    }

    public Link() {

    }

    public Link(String title, int subLinkNumber) {
        mTitle = title;
        mSubLinkNumber = subLinkNumber;

    }



    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public void getData(String input) throws UnsupportedEncodingException {

        try {
            input = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=links&format=json&formatversion=2&pllimit=max&titles=";
        String urlToGet = baseUrl + input;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlToGet)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!response.isSuccessful()) try {
            throw new IOException("Unexpected code " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonData = null;
        try {
            jsonData = response.body().string();
            JSONArray allLinks = parseLinkArray(jsonData);
            mLinks = parseLinkDetails(allLinks);
            Log.i(TAG, "done getting data");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private JSONArray parseLinkArray(String jsonData) throws JSONException {
        JSONObject raw = new JSONObject(jsonData);
        JSONObject query = raw.getJSONObject("query");
        JSONArray pages = query.getJSONArray("pages");
        JSONObject title = pages.getJSONObject(0);
        JSONArray data = title.getJSONArray("links");
        return data;
    }

    private Link[] parseLinkDetails(JSONArray data) throws JSONException, UnsupportedEncodingException {

        Link[] links = new Link[data.length()];

        for(int i = 0; i < data.length(); i++) {
            JSONObject jsonLinkName = data.getJSONObject(i);
            Link link = new Link();

            link.setTitle(jsonLinkName.getString("title"));
            //link.setSubLinkNumber(generateSubLinkNumber(link));


            links[i] = link;
            System.out.println(links[i].getTitle());
        }
        return links;
    }


}
