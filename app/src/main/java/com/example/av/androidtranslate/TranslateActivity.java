package com.example.av.androidtranslate;

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
    private final String key = "trnsl.1.1.20150920T100138Z.be418cc4b6842e02.b4f643f222a54b8ff78e7a738fe291d8a76f511a";

    private String langFrom;
    private String langTo;
    private String langFromCode;
    private String langToCode;

    private TextView langFromView, langToView;
    private EditText inputEdit, outputEdit;
    private Button exchangeButton, translateButton;

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

        translateButton.setOnClickListener(new TranslateListener());
        exchangeButton.setOnClickListener(new ExchangeListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: get parameters from intent and saved instance state
        setLangFrom("английский", "en");
        setLangTo("русский", "ru");

        inputEdit.setText("Shall so come in like manner as ye have seen him");

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

    class NetworkThread extends Thread {
        private TranslateActivity callback;

        NetworkThread(TranslateActivity parentActivity) {
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
                builder.addParameter("lang", String.format(Locale.getDefault(), "%s-%s", langFromCode, langToCode));
                builder.addParameter("text", inputEdit.getText().toString());

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

    private void translate() {
        NetworkThread thread = new NetworkThread(this);
        thread.start();
    }

    // TODO: Нормальная обработка исключений
    public void dispatchAPIResponse(String apiResponse) {
        try {
            JSONObject result = new JSONObject(apiResponse);
            int code = result.getInt("code");
            if (HttpURLConnection.HTTP_OK == code) {
                String text = result.getJSONArray("text").getString(0);
                TranslateActivity.this.runOnUiThread(new TranslateActivity.OutputSetter(text));
            } else {
                // TODO: failed
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    class OutputSetter implements Runnable {
        private String out;
        OutputSetter(String output) {
            out = output;
        }

        public void run() {
            TranslateActivity.this.setOutput(out);
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

    class ExchangeListener implements Button.OnClickListener {
        public void onClick(View v) {
            String fromBuffer = TranslateActivity.this.langFrom;
            String fromCodeBuffer = TranslateActivity.this.langFromCode;

            TranslateActivity.this.setLangFrom(TranslateActivity.this.langTo,
                    TranslateActivity.this.langToCode);
            TranslateActivity.this.setLangTo(fromBuffer, fromCodeBuffer);
            TranslateActivity.this.translate();
        }
    }
}
