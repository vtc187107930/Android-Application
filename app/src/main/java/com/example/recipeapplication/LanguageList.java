package com.example.recipeapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class LanguageList extends AppCompatActivity {

    String currentLanguage;
    Locale myLocale;

    ImageView back;
    TextView eng, trad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        back = findViewById(R.id.language_back);
        eng = findViewById(R.id.english);
        trad = findViewById(R.id.traditional);

        currentLanguage = getIntent().getStringExtra("sendLanguage");
        Log.d("AAAAA", "onCreate: " + currentLanguage);
        if (currentLanguage == null) {
            currentLanguage = "en";
            Log.d("BBBBB", "onCreate: " + currentLanguage);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
            }
        });

        trad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("zh");

            }
        });
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration conf = getResources().getConfiguration();
            conf.locale = myLocale;
            getResources().updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            refresh.putExtra("getLanguage", localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(LanguageList.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
}
