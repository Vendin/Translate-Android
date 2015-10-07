package com.example.av.androidtranslate;

import android.support.annotation.Nullable;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mihanik on 28.09.15.
 */
class LoadLanguagesTask extends NetworkAsyncTask<Void, String, HashMap<String, String>> {
    final private static URL apiURL;
    private static volatile LoadLanguagesTask singleton;
    private static Object synchronizer = new Object();

    public static void load() {
        if (singleton == null) {
            synchronized (synchronizer) {
                if (singleton == null) {
                    singleton = new LoadLanguagesTask();
                    singleton.execute((Void) null);
                }
            }
        }
    }

    private LoadLanguagesTask() {
    }

    static {
        URL url = null;
        try {
            URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/getLangs");
            builder.addParameter("key", key);
            builder.addParameter("ui", "ru");
            url = builder.build().toURL();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            apiURL = url;
        }
    }


    @Override
    protected URL getApiUrl() {
        return apiURL;
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
        publishProgress("Loading started");
        String response = getAPIResponse();
        publishProgress("Server info dispatch");
        return dispatchAPIResponse(response);
    }

    @Override
    protected void onProgressUpdate(String... params) {
        LoadingActivity.bus.post(params[0]);
    }

    @Override
    protected void onPostExecute(@Nullable HashMap<String, String> result) {
        LoadingActivity.bus.post(result);
    }

    @Nullable
    protected HashMap<String, String> dispatchAPIResponse(@Nullable String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            JSONObject text = result.getJSONObject("langs");

            Iterator<String> i = text.keys();
            HashMap<String, String> languages = new HashMap<>(text.length());
            for (; i.hasNext(); ) {
                String key = i.next();
                languages.put(text.getString(key), key);
            }

            return languages;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
