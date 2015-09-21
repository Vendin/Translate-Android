package com.example.av.androidtranslate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HashMap<Integer, String> hashMapLanguage = new HashMap<>();
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02\n" +
            ".b4f643f222a54b8ff78e7a738fe291d8a76f511a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHashMapLanguage();

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
                Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);

                String fromAt =  hashMapLanguage.get(spinner1.getSelectedItemPosition()) + "-" + hashMapLanguage.get(spinner2.getSelectedItemPosition());

                Toast toast = Toast.makeText(getApplicationContext(), fromAt, Toast.LENGTH_SHORT);
                toast.show();

//                Intent intent = new Intent(MainActivitya.this , Activity0.class);
//                startActivity(intent);

            }
        });

    }




    public void initHashMapLanguage(){
        this.hashMapLanguage.put(1, "en");
        this.hashMapLanguage.put(2, "ar");
        this.hashMapLanguage.put(3, "nl");
        this.hashMapLanguage.put(4, "el");
        this.hashMapLanguage.put(5, "da");
        this.hashMapLanguage.put(6, "he");
        this.hashMapLanguage.put(7, "ga");
        this.hashMapLanguage.put(8, "it");
        this.hashMapLanguage.put(9, "zh");
        this.hashMapLanguage.put(10, "ko");
        this.hashMapLanguage.put(11, "la");
        this.hashMapLanguage.put(12, "de");
        this.hashMapLanguage.put(13, "no");
        this.hashMapLanguage.put(14, "ru");
        this.hashMapLanguage.put(15, "fr");
        this.hashMapLanguage.put(16, "ja");

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
