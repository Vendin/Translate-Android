package com.example.av.androidtranslate;

import android.support.annotation.Nullable;

import com.squareup.otto.Bus;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

class TranslateAsyncTask extends NetworkAsyncTask<String, Void, String> {
    private String fromCode;
    private String toCode;
    private String translate;

    private Bus publisher;

    public static void translate(Bus publisher, String fromCode, String toCode, String translate) {
        TranslateAsyncTask singleton = new TranslateAsyncTask(publisher);
        singleton.execute(fromCode, toCode, translate);
    }

    @Override
    protected String doInBackground(String... params) {
        fromCode = params[0];
        toCode = params[1];
        translate = params[2];
        String response = getAPIResponse();
        return dispatchAPIResponse(response);
    }

    @Override
    protected void onPostExecute(String result) {
        publisher.post(result);
    }

    private TranslateAsyncTask(Bus publisher) {
        this.publisher = publisher;
    }

    //TODO: нормальная обработка исключений
    @Override
    protected URL getApiUrl() {
        URL url = null;
        try {
            URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate");
            builder.addParameter("key", key);
            builder.addParameter("lang", String.format(Locale.getDefault(), "%s-%s", fromCode, toCode));
            builder.addParameter("text", translate);

            url = builder.build().toURL();
        } catch (URISyntaxException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // TODO: Нормальная обработка исключений
    @Nullable
    public String dispatchAPIResponse(@Nullable String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            int code = result.getInt("code");
            if (HttpURLConnection.HTTP_OK == code) {
                return result.getJSONArray("text").getString(0);
            } else {
                // TODO: failed
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}