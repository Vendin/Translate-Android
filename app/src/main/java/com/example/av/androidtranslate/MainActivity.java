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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        List<String> list = new ArrayList<String>();
        mapLanguage = LoadingActivity.listLanguages;

        list.add("Выберете язык");
        for(Map.Entry<String, String> entity : mapLanguage.entrySet()){
            list.add(entity.getKey());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(dataAdapter);
        spinner2.setAdapter(dataAdapter);

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

            Toast toast = Toast.makeText(getApplicationContext(), fromAt, Toast.LENGTH_SHORT);
            toast.show();

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

}
