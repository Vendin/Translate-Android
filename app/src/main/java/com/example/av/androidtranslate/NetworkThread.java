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

/**
 * Created by mihanik on 23.09.15.
 */
public abstract class NetworkThread extends Thread {
    protected final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";

    @Override
    final public void run() {
        getAPIResponse();
    }

    protected abstract void dispatchAPIResponse(@Nullable String apiResponse);
    protected abstract URL getApiUrl() throws URISyntaxException, MalformedURLException;


    private void getAPIResponse() {
        String result = null;
        try {
            URL url = getApiUrl();

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder responseBuilder = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            rd.close();
            result = responseBuilder.toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispatchAPIResponse(result);
    }
}
