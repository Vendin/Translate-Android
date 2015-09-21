package com.example.av.androidtranslate;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    HashMap<Integer, String> hashMapLanguage = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkThread networkThread = new NetworkThread(this);

        networkThread.start();

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
                Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);

                String fromAt =  hashMapLanguage.get(spinner1.getSelectedItemPosition()) + "-" + hashMapLanguage.get(spinner2.getSelectedItemPosition());

                Toast toast = Toast.makeText(getApplicationContext(), fromAt, Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(MainActivity.this , TranslateActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Нормальная обработка исключений
    public void dispatchAPIResponse(String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            JSONObject text = result.getJSONObject("langs");
            Log.e("Response code:", String.valueOf(text.get("af")));
            Toast toast = Toast.makeText(getApplicationContext(), text.toString(), Toast.LENGTH_SHORT);
            toast.show();
            //MainActivity.this.runOnUiThread(new TranslateActivity.OutputSetter(text));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class NetworkThread extends Thread {
    private MainActivity callback;
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";

    NetworkThread(MainActivity act){
        callback = act;
    }

    @Override
    public void run() {
        callback.dispatchAPIResponse(getAPIResponse());
    }

    @Nullable
    private String getAPIResponse() {
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