package com.yako.safekids.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Switch;
import com.safekids.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Setting extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.switch_speed)
    Switch switch_speed;

    @BindView(R.id.switch_alarme)
    Switch switch_alarme;

    @BindView(R.id.switch_notification)
    Switch switch_notification;

    @BindView(R.id.switch_exit_alert)
    Switch switch_exit_alert;

    @BindView(R.id.tv_speed)
    TextView tv_speed;

    @BindView(R.id.tv_temp)
    TextView tv_temp;

    @BindView(R.id.et_speed)
    EditText et_speed;

    @BindView(R.id.et_temp)
    EditText et_temp;

    @BindView(R.id.ll_speed)
    LinearLayout ll_speed;

    @BindView(R.id.ll_temp)
    LinearLayout ll_temp;


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_setting);
        } else {
            setContentView(R.layout.activity_setting_en);
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

        switch_speed.setChecked(prefs.getBoolean("SpeedActif", true));
        switch_speed.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                editor.putBoolean("SpeedActif", checked);
                editor.apply();
            }
        });

        switch_alarme.setChecked(prefs.getBoolean("AlarmeActif", true));
        switch_alarme.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                editor.putBoolean("AlarmeActif", checked);
                editor.apply();
            }
        });

        switch_notification.setChecked(prefs.getBoolean("NotifActif", true));
        switch_notification.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                editor.putBoolean("NotifActif", checked);
                editor.apply();
            }
        });

        switch_exit_alert.setChecked(prefs.getBoolean("AlarmeExitActif", false));
        switch_exit_alert.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                editor.putBoolean("AlarmeExitActif", checked);
                editor.apply();
            }
        });

        tv_speed.setText(prefs.getInt("SpeedMax", 130) + "");
        tv_temp.setText(prefs.getInt("TempMax", 40) + "");

        et_speed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            valideSpeed();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        et_temp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            valideTemp();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @OnClick(R.id.tv_speed)
    public void changheSpeed() {
        tv_speed.setVisibility(View.GONE);
        ll_speed.setVisibility(View.VISIBLE);
        et_speed.requestFocus();
        et_speed.setText(tv_speed.getText().toString());
    }

    @OnClick(R.id.tv_temp)
    public void changheTemp() {
        tv_temp.setVisibility(View.GONE);
        ll_temp.setVisibility(View.VISIBLE);
        et_temp.requestFocus();
        et_temp.setText(tv_temp.getText().toString());
    }

    @OnClick(R.id.img_speed)
    public void valideSpeed() {
        if (et_speed.getText().toString().equals("") || Integer.parseInt(et_speed.getText().toString()) < 50) {
            Toast.makeText(Setting.this, getResources().getString(R.string.max_speed), Toast.LENGTH_SHORT).show();
        } else {
            tv_speed.setVisibility(View.VISIBLE);
            ll_speed.setVisibility(View.GONE);

            tv_speed.setText(et_speed.getText().toString());
            editor.putInt("SpeedMax", Integer.parseInt(tv_speed.getText().toString())).commit();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_speed.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.img_temp)
    public void valideTemp() {
        if (et_temp.getText().toString().equals("")) {
            Toast.makeText(Setting.this, getResources().getString(R.string.enter_max_temp), Toast.LENGTH_SHORT).show();
        } else {
            tv_temp.setVisibility(View.VISIBLE);
            ll_temp.setVisibility(View.GONE);

            tv_temp.setText(et_temp.getText().toString());
            editor.putInt("TempMax", Integer.parseInt(tv_temp.getText().toString())).commit();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_temp.getWindowToken(), 0);
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
