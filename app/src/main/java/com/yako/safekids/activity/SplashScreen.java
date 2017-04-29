package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        prefs = getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        String defaultLangage = Locale.getDefault().getLanguage();
        SharedPreferences prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        if (prefs.getString("Langage", "").equals("")) {
            SharedPreferences.Editor editor = prefs.edit();

            if (defaultLangage.equals("ar") ) {
                editor.putString("Langage", "ar");
            } else if (defaultLangage.equals("iw") ) {
                editor.putString("Langage", "he");
            }else {
                editor.putString("Langage", "en");
            }
            editor.apply();
        }

        Locale locale = new Locale(prefs.getString("Langage", "en"));
        Locale.setDefault(locale);
        Configuration configLanguage = new Configuration();
        configLanguage.locale = locale;
        getBaseContext().getResources().updateConfiguration(configLanguage, getBaseContext().getResources().getDisplayMetrics());


        if (prefs.getString("Login", "").equals("connecte")) {
            startActivity(new Intent(this, Connexion.class));
        } else {
            startActivity(new Intent(this, IntroSlider.class));
        }
        finish();
    }

}
