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

class NetworkThreadMain extends Thread {
    private MainActivity callbackMainActivity;

    private HashMap<String, String> listLanguage;
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";


    NetworkThreadMain(MainActivity act){
        callbackMainActivity = act;
    }


    public HashMap<String, String> getListLanguage(){
        return listLanguage;
    }

    @Override
    public void run() {
        listLanguage = dispatchAPIResponse(getAPIResponse());
    }

    public HashMap<String, String> dispatchAPIResponse(String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            JSONObject text = result.getJSONObject("langs");

            //Log.e("Response code:", String.valueOf(text.getJSONObject("af")));
            Iterator<String> i = text.keys();
            HashMap<String, String> languages = new HashMap<>(text.length());
            for (; i.hasNext(); ) {
                String key = i.next();
                languages.put(text.getString(key), key);
            }
            Log.e(languages.get("Финский") + "  HashMap:", languages.toString());

//            Toast toast = Toast.makeText(getApplicationContext(), text.toString(), Toast.LENGTH_SHORT);
//            toast.show();
            //MainActivity.this.runOnUiThread(new TranslateActivity.OutputSetter(text));
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


    @Nullable
    public String getAPIResponse() {
        StringBuilder result = new StringBuilder();
        try {
            URIBuilder builder = new URIBuilder("https://translate.yandex.net/api/v1.5/tr.json/getLangs");
            builder.addParameter("key", key);
            builder.addParameter("ui", "ru");

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
