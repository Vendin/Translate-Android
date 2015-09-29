package com.example.av.androidtranslate;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static final int GET_LANGS = 0;

    private HashMap<String, String> mapLanguage;
    private String langFrom;
    private String langTo;
    private String langFromCode;
    private String langToCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> listFrom = new ArrayList<String>();
        mapLanguage = LoadingActivity.listLanguages;


        for(Map.Entry<String, String> entity : mapLanguage.entrySet()){
            listFrom.add(entity.getKey());
        }
        Collections.sort(listFrom);
        listFrom.add(0, "Выберете язык из которого перевести");

        ArrayAdapter<String> dataAdapterFrom = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listFrom);
        dataAdapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapterFrom);

        List<String> listTo = new ArrayList<>(listFrom);
        listTo.set(0, "Выберете язык в который перевести");
        ArrayAdapter<String> dataAdapterTo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTo);
        dataAdapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapterTo);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

    }

    private void sendData() {
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        langFrom = spinner1.getSelectedItem().toString();
        langTo = spinner2.getSelectedItem().toString();
        langFromCode =  mapLanguage.get(langFrom);
        langToCode = mapLanguage.get(langTo);

        if (langToCode != null && langFromCode != null) {

            String fromAt = langFrom + " " + langFromCode + " : " + langTo + " " + langToCode;

            Intent intent;
            boolean isCalledFromOutside = (getIntent().hasExtra("requestCode") &&
                    getIntent().getIntExtra("requestCode", 0) == GET_LANGS);
            if (isCalledFromOutside) {
                intent = new Intent();
            } else {
                intent = new Intent(MainActivity.this, TranslateActivity.class);
            }

            intent.putExtra("langForm", langFrom);
            intent.putExtra("langFormCode", langFromCode);
            intent.putExtra("langTo", langTo);
            intent.putExtra("langToCode", langToCode);

            if (isCalledFromOutside) {
                setResult(RESULT_OK, intent);
                finish();
            } else {
                startActivity(intent);
            }
        } else {
            String message = "Выберите язык!";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
