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
    private String translation;
    private String fromCode;
    private String toCode;
    private String translate;

    @Nullable
    public String getTranslation() {
        return translation;
    }

    NetworkThreadTranslate(String fromCode, String toCode, String translate) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.translate = translate;
    }

    @Override
    protected URL getApiUrl() throws URISyntaxException, MalformedURLException {
        URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate");
        builder.addParameter("key", key);
        builder.addParameter("lang", String.format(Locale.getDefault(), "%s-%s", fromCode, toCode));
        builder.addParameter("text", translate);

        return builder.build().toURL();
    }

    // TODO: Нормальная обработка исключений
    @Override
    public void dispatchAPIResponse(@Nullable String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            int code = result.getInt("code");
            if (HttpURLConnection.HTTP_OK == code) {
                translation = result.getJSONArray("text").getString(0);
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