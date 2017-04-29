package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.safekids.android.R;
import com.yako.safekids.adapter.AlertListItemAdapter;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.service.MonitorService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Notification extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_no_alert)
    ImageView img_no_alert;

    @BindView(R.id.rv_alert)
    RecyclerView rv_alert;

    private SharedPreferences prefs;
    Gson gson = new Gson();
    private String langage;
    private List<AlertModel> listAlert = new ArrayList<>();
    AlertListItemAdapter alertListItemAdapter;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            //Toast.makeText(this,langage+"  kkk",Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_notification);
        } else {
            //Toast.makeText(this,langage,Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_notification_en);
        }

        ButterKnife.bind(this);

        editor = prefs.edit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (langage.equals("ar") || langage.equals("he")) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getListAlerts();
        rv_alert.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        alertListItemAdapter = new AlertListItemAdapter(this, listAlert);
        rv_alert.setAdapter(alertListItemAdapter);

        if (listAlert.size() == 0) {
            img_no_alert.setVisibility(View.VISIBLE);
        } else {
            img_no_alert.setVisibility(View.GONE);
        }

    }

    void getListAlerts() {
        String jsonKidsStatus = prefs.getString("MesAlerts", null);
        if (jsonKidsStatus != null) {
            AlertModel[] items = gson.fromJson(jsonKidsStatus, AlertModel[].class);
            listAlert = Arrays.asList(items);
            listAlert = new ArrayList(listAlert);
        }
    }

    public void restartAlert(int position) {
        String alertJson = gson.toJson(listAlert.get(position));
        editor.putString("Alert", alertJson);
        editor.commit();

        MainActivity.listStatus.add(listAlert.get(position).getListKids().get(0).getObjectId());
        String jsonStatus = gson.toJson(MainActivity.listStatus);
        editor.putString("MesKidsStatus", jsonStatus);
        editor.commit();

        startService(new Intent(Notification.this, MonitorService.class));
        alertListItemAdapter.notifyItemChanged(position);
    }

    public void deleteAlert(int position) {
        listAlert.remove(position);
        alertListItemAdapter.notifyItemRemoved(position);

        String json = gson.toJson(listAlert);
        editor.putString("MesAlerts", json);
        editor.commit();

        if (listAlert.size() == 0) {
            img_no_alert.setVisibility(View.VISIBLE);
        } else {
            img_no_alert.setVisibility(View.GONE);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal_white, menu);
        if (langage.equals("en")) {
            menu.findItem(R.id.action_back).setVisible(false);
            menu.findItem(R.id.action_back).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (langage.equals("en")) {
                onBackPressed();
                return true;
            }
        }

        if (id == R.id.action_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
