package com.example.av.androidtranslate;

import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

class NetworkThreadTranslate extends NetworkThread {
    private TranslateActivity callback;
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";


    NetworkThreadTranslate(TranslateActivity parentActivity) {
        callback = parentActivity;
    }

    @Override
    protected URL getApiUrl() throws URISyntaxException, MalformedURLException {
        URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate");
        builder.addParameter("key", key);
        builder.addParameter("lang", String.format(Locale.getDefault(), "%s-%s",
                callback.getLangFromCode(), callback.getLangToCode()));
        builder.addParameter("text", callback.getInputEdit().getText().toString());

        return builder.build().toURL();
    }

    // TODO: Нормальная обработка исключений
    @Override
    public void dispatchAPIResponse(@Nullable String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            int code = result.getInt("code");
            if (HttpURLConnection.HTTP_OK == code) {
                String text = result.getJSONArray("text").getString(0);
                callback.runOnUiThread(new TranslateActivity.OutputSetter(text, callback));
            } else {
                // TODO: failed
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}