package com.tgraham.wikicrawler.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tgraham.wikicrawler.R;
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

public class SplashScreen extends Activity {
    private Link[] mLink;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */
            String input = null;
            try {
                input = URLEncoder.encode("Main Page", "UTF-8");
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
                mLink = parseLinkDetails(allLinks);
                Log.i(TAG, "done getting data");
                Log.i(TAG, jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

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
            //System.out.println(links[i].getTitle());
        }
        return links;
    }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            Log.i(TAG, "Post Execute");
            String[] linkNames = new String[mLink.length];
            for (int i = 0; i < mLink.length; i++){
                linkNames[i] = mLink[i].getTitle();
            }

            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            i.putExtra("Links", linkNames);
            startActivity(i);

            // close this activity
            finish();
        }

    }
}
