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

import com.google.gson.Gson;
import com.safekids.android.R;
import com.yako.safekids.adapter.KidsListItemAdapter;
import com.yako.safekids.model.Kids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class KidsList extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    com.getbase.floatingactionbutton.FloatingActionButton fab;

    @BindView(R.id.img_no_kids)
    ImageView img_no_kids;

    private KidsListItemAdapter kidsAdapter;
    List<Kids> allKids = new ArrayList<Kids>();
    private SharedPreferences prefs;
    Gson gson = new Gson();
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_kids_list);
        } else {
            setContentView(R.layout.activity_kids_list_en);
        }

        ButterKnife.bind(this);

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

        RecyclerView rv_kids = (RecyclerView) findViewById(R.id.rv_kids);
        rv_kids.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getListKids();

        kidsAdapter = new KidsListItemAdapter(this, allKids);
        rv_kids.setAdapter(kidsAdapter);

        if (allKids.size() == 0) {
            img_no_kids.setVisibility(View.VISIBLE);
        } else {
            img_no_kids.setVisibility(View.GONE);
        }
    }

    private void getListKids() {
        allKids.clear();
        String jsonKids = prefs.getString("MesKids", null);
        if (jsonKids != null) {
            Kids[] kidItems = gson.fromJson(jsonKids, Kids[].class);
            allKids = Arrays.asList(kidItems);
            allKids = new ArrayList(allKids);
        }
    }

    @OnClick(R.id.fab)
    public void addKids() {
        startActivityForResult(new Intent(this, NewKids.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean kidsUpdate = data.getBooleanExtra("KidsUpdate", false);
            boolean kidsDelete = data.getBooleanExtra("KidsDelete", false);
            boolean kidsNew = data.getBooleanExtra("KidsNew", false);

            if (kidsDelete) {
                getListKids();
                kidsAdapter.listItems.clear();
                kidsAdapter.listItems.addAll(allKids);

                kidsAdapter.notifyDataSetChanged();

                if (allKids.size() == 0) {
                    img_no_kids.setVisibility(View.VISIBLE);
                } else {
                    img_no_kids.setVisibility(View.GONE);
                }
            } else if (kidsUpdate) {
                getListKids();
                kidsAdapter.listItems.clear();
                kidsAdapter.listItems.addAll(allKids);

                kidsAdapter.notifyDataSetChanged();
            } else if (kidsNew) {
                getListKids();
                kidsAdapter.listItems.clear();
                kidsAdapter.listItems.addAll(allKids);

                kidsAdapter.notifyDataSetChanged();

                if (allKids.size() == 0) {
                    img_no_kids.setVisibility(View.VISIBLE);
                } else {
                    img_no_kids.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (kidsAdapter != null) {
            kidsAdapter.notifyDataSetChanged();
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
