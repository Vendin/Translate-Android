package com.example.av.androidtranslate;

import android.support.annotation.Nullable;
import android.util.Log;


import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

class NetworkThreadLoading extends NetworkThread {

    private HashMap<String, String> listLanguage;
    public HashMap<String, String> getListLanguage(){
        return listLanguage;
    }

    @Override
    protected URL getApiUrl() throws URISyntaxException, MalformedURLException {
        URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/getLangs");
        builder.addParameter("key", key);
        builder.addParameter("ui", "ru");
        return builder.build().toURL();
    }

    @Override
    protected void dispatchAPIResponse(@Nullable String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            JSONObject text = result.getJSONObject("langs");

            Iterator<String> i = text.keys();
            HashMap<String, String> languages = new HashMap<>(text.length());
            for (; i.hasNext(); ) {
                String key = i.next();
                languages.put(text.getString(key), key);
            }

            listLanguage = languages;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
