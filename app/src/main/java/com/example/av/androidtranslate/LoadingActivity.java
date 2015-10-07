package com.example.av.androidtranslate;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
    public static final Bus bus = new Bus();
    public static HashMap<String, String> listLanguages = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ProgressBar diagProgress = (ProgressBar) findViewById(R.id.progressBar);
        diagProgress.setVisibility(View.VISIBLE);

        bus.register(this);
        LoadLanguagesTask.load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }


    @Subscribe
    public void resultAvailable(@Nullable HashMap<String, String> loadedResult) {
        listLanguages = loadedResult;

        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }

}
