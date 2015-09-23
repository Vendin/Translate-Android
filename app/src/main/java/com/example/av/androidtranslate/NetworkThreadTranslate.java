package com.example.av.androidtranslate;

import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

class NetworkThreadTranslate extends Thread {
    private TranslateActivity callback;
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";


    NetworkThreadTranslate(TranslateActivity parentActivity) {
        callback = parentActivity;
    }

    @Override
    public void run() {
        callback.dispatchAPIResponse(getAPIResponse());
    }

    // TODO: Нормальная обработка Exception-ов
    @Nullable
    private String getAPIResponse() {
        StringBuilder result = new StringBuilder();
        try {
            URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/translate");
            builder.addParameter("key", key);
            builder.addParameter("lang", String.format(Locale.getDefault(), "%s-%s", callback.getLangFromCode(), callback.getLangToCode()));
            builder.addParameter("text", callback.getInputEdit().getText().toString());

            URL url = builder.build().toURL();
            Log.v("formed url", url.toString());

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            Log.v("Response code:", String.valueOf(connection.getResponseCode()));

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}