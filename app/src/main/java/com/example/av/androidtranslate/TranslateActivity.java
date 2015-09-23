package com.example.av.androidtranslate;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
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

/*
Переключение тредов идёт примерно так:
TranslateActivity        NetworkThread              UIThread (OutputSetter)
    |                           |                       |
    |                           |                       |
translate -------------->      run ___________          |
    |                                        |          |
    |                                        |          |
    |                    getAPIResponse  <---|          |
    |                           |                       |
    |                           |-----------------> setOutput
    |                                                   |
    |                                                   |
 */


public class TranslateActivity extends AppCompatActivity {
    NetworkThreadTranslate networkThreadTranslate;

    private String langFrom;
    private String langTo;
    private String langFromCode;
    private String langToCode;

    private TextView langFromView, langToView;
    private EditText inputEdit, outputEdit;
    private Button exchangeButton, translateButton, chooseLangButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        langFromView = (TextView) findViewById(R.id.fromLanguage);
        langToView = (TextView) findViewById(R.id.toLanguage);

        inputEdit = (EditText) findViewById(R.id.input);
        outputEdit = (EditText) findViewById(R.id.output);

        exchangeButton = (Button) findViewById(R.id.exchange);
        translateButton = (Button) findViewById(R.id.translate);
        chooseLangButton = (Button) findViewById(R.id.choose_lang);

        translateButton.setOnClickListener(new TranslateListener());
        exchangeButton.setOnClickListener(new ExchangeListener());
        chooseLangButton.setOnClickListener(new ChooseLangListener());

        fetchDataFromIntent(getIntent());
    }

    public String getLangFromCode(){
        return  langFromCode;
    }

    public String getLangToCode(){
        return  langToCode;
    }

    public EditText getInputEdit(){
        return  inputEdit;
    }

    public EditText getOutputEdit() {
        return outputEdit;
    }

    private void fetchDataFromIntent(Intent intent) {
        setLangFrom(intent.getStringExtra("langForm"), intent.getStringExtra("langFormCode"));
        setLangTo(intent.getStringExtra("langTo"), intent.getStringExtra("langToCode"));

        Log.v("r", intent.getStringExtra("langForm"));

        translate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setLangFrom(String langFrom, String langFromCode) {
        this.langFrom = langFrom;
        this.langFromCode = langFromCode;
        langFromView.setText(langFrom);
    }

    public void setLangTo(String langTo, String langToCode) {
        this.langTo = langTo;
        langToView.setText(langTo);
        this.langToCode = langToCode;
    }

    private void translate() {
        networkThreadTranslate = new NetworkThreadTranslate(langFromCode, langToCode,
                inputEdit.getText().toString());
        networkThreadTranslate.start();

        try {
            networkThreadTranslate.join();

            String tranlsation = networkThreadTranslate.getTranslation();
            if (tranlsation != null){
                setOutput(tranlsation);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setOutput(String output) {
        outputEdit.setText(output);
    }

    class TranslateListener implements Button.OnClickListener {
        public void onClick(View v) {
            TranslateActivity.this.translate();
        }
    }

    class ChooseLangListener implements Button.OnClickListener {
        public void onClick(View v) {
            Intent i = new Intent(TranslateActivity.this, MainActivity.class);
            i.putExtra("requestCode", MainActivity.GET_LANGS);
            startActivityForResult(i, MainActivity.GET_LANGS);
        }
    }

    class ExchangeListener implements Button.OnClickListener {
        public void onClick(View v) {
            String fromBuffer = TranslateActivity.this.langFrom;
            String fromCodeBuffer = TranslateActivity.this.langFromCode;

            TranslateActivity.this.setLangFrom(TranslateActivity.this.langTo,
                    TranslateActivity.this.langToCode);

            EditText input = TranslateActivity.this.getInputEdit();
            String inputBuffer = input.getText().toString();

            EditText output = TranslateActivity.this.getOutputEdit();
            String outputBuffer = output.getText().toString();

            input.setText(outputBuffer);
            output.setText(inputBuffer);

            TranslateActivity.this.setLangTo(fromBuffer, fromCodeBuffer);
            TranslateActivity.this.translate();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.GET_LANGS && resultCode == RESULT_OK) {
            fetchDataFromIntent(data);
        }
    }
}
