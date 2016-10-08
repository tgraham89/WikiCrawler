package com.tgraham.wikicrawler.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.tgraham.wikicrawler.R;
import com.tgraham.wikicrawler.adapters.PageAdapter;
import com.tgraham.wikicrawler.links.AlertDialogFragment;
import com.tgraham.wikicrawler.links.Link;

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

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Link[] mLink;

    private int mLinkNumbers;
    private Link[] mTempLink;
    private Link[] mSubLink;
    PageAdapter mPageAdapter;
    static ViewPager mViewPager;
    private static String[] mLinkNames;


    public static String[] getLinkNames() {
        return mLinkNames;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        mLinkNames = intent.getStringArrayExtra("Links");

        mPageAdapter = new PageAdapter(getSupportFragmentManager(), this, "Main Page");
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPageAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Link[] getData(String input) throws UnsupportedEncodingException {

        input = URLEncoder.encode(input, "UTF-8");
        String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=links&format=json&formatversion=2&pllimit=max&titles=";
        String urlToGet = baseUrl + input;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlToGet)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        //Log.v(TAG, jsonData);
                        if(response.isSuccessful()) {
                            JSONArray allLinks = parseLinkArray(jsonData);
                            mLink = parseLinkDetails(allLinks);
                            System.out.println(jsonData);
                            System.out.println(mLink.length);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }

        return mLink;

    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private JSONArray parseLinkArray(String jsonData) throws JSONException {
        JSONObject raw = new JSONObject(jsonData);
        JSONObject query = raw.getJSONObject("query");
        JSONArray pages = query.getJSONArray("pages");
        JSONObject title = pages.getJSONObject(0);
        JSONArray data = title.getJSONArray("links");
        return data;
    }

    private int getNumberOfLinks(JSONArray jsonArray) {
        return jsonArray.length();
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

    private int generateSubLinkNumber(final Link link) throws UnsupportedEncodingException {
        String oneLevelDownLink = URLEncoder.encode(link.getTitle(), "UTF-8");
        String baseUrl = "https://en.wikipedia.org/w/api.php?action=query&prop=links&format=json&formatversion=2&pllimit=max&titles=";
        String urlToGet = baseUrl + oneLevelDownLink;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlToGet)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if(response.isSuccessful()) {
                            JSONArray allLinks = parseLinkArray(jsonData);
                            mLinkNumbers = allLinks.length();
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
        return mLinkNumbers;
    }

    //public void setCurrentItem (int item, boolean smoothScroll) {
    //    mViewPager.setCurrentItem(item, smoothScroll);
    //}

    public static ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(1, true);
    }
}
