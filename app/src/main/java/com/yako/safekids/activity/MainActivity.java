package com.yako.safekids.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.RadioButton;
import com.safekids.android.R;
import com.yako.safekids.adapter.KidsChooseItemAdapter;
import com.yako.safekids.fragment.NavigationDrawerFragment;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.model.Kids;
import com.yako.safekids.model.PhoneNumber;
import com.yako.safekids.receiver.AlarmManagerBroadcastReceiver;
import com.yako.safekids.service.GPSTracker;
import com.yako.safekids.service.GpsServices;
import com.yako.safekids.service.MonitorService;
import com.yako.safekids.service.NotiNearbyService;
import com.yako.safekids.service.SpeedService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.imgDrawer)
    ImageView imgDrawer;

    @BindView(R.id.imgHelp)
    ImageView imgHelp;

    @BindView(R.id.imgTranslate)
    ImageView imgTranslate;

    @BindView(R.id.tv_add_kids)
    TextView tv_add_kids;

    @BindView(R.id.tv_phones)
    TextView tv_phones;

    @BindView(R.id.tv_safe_kids)
    TextView tv_safe_kids;

    @BindView(R.id.tv_delete_safe)
    TextView tv_delete_safe;

    @BindView(R.id.tv_no_oxygen)
    TextView tv_no_oxygen;

    static TextView tv_over_speed = null;

    @BindView(R.id.tv_top_hot)
    TextView tv_top_hot;

    static ImageView img_center = null;

    @BindView(R.id.img_fb)
    ImageView img_fb;

    @BindView(R.id.img_tw)
    ImageView img_tw;

    @BindView(R.id.img_g)
    ImageView img_g;

    @BindView(R.id.img_in)
    ImageView img_in;

    @BindView(R.id.img_y)
    ImageView img_y;

    @BindView(R.id.img_p)
    ImageView img_p;

    @BindView(R.id.img_s)
    ImageView img_s;

    private static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 23;
    private static int PERMISSIONS_REQUEST_SEND_SMS = 24;
    private static int PLACE_PICKER_REQUEST = 25;

    private NavigationDrawerFragment drawerLayout;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    private List<Kids> allKids = new ArrayList<>();
    private List<PhoneNumber> allPhones = new ArrayList<>();
    List<AlertModel> listAlert = new ArrayList<>();

    public static List<String> listStatus = new ArrayList<>();
    public static boolean chooseAllKids = false;

    public List<Kids> alertKidsList = new ArrayList<>();
    public int extraTime;
    double longitude;
    double latitude;

    Place place;

    AlertDialog alertChooseKids;
    AlertDialog alertTimer;
    RelativeLayout rlBlock;
    AlertModel newAlertModel;
    private String langage;

    boolean speedPermission = false;
    public static boolean isOpen = false;

    static int img_center_res = -1;

    public static final String COPA_RESULT = "MESSAGE.REQUEST_PROCESSED";
    private BroadcastReceiver receiver;


    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(COPA_RESULT)
        );
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOpen = true;

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();


        String defaultLangage = Locale.getDefault().getLanguage();
        SharedPreferences prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        if (prefs.getString("Langage", "").equals("")) {

            if (defaultLangage.equals("ar")) {
                editor.putString("Langage", "ar");
            } else if (defaultLangage.equals("he")) {
                editor.putString("Langage", "he");
            } else {
                editor.putString("Langage", "en");
            }
            editor.commit();
        }

        langage = prefs.getString("Langage", "ar");

        Locale locale = new Locale(langage);
        Locale.setDefault(locale);
        Configuration configLanguage = new Configuration();
        configLanguage.locale = locale;
        getBaseContext().getResources().updateConfiguration(configLanguage, getBaseContext().getResources().getDisplayMetrics());

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_en);
        }
        ButterKnife.bind(this);
        //Toast.makeText(this,langage,Toast.LENGTH_LONG).show();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                if (img_center != null) {

                    editor.remove("MesKidsStatus").commit();
                    listStatus.clear();

                    editor.remove("Alert").commit();

                    img_center_res = 0;
                    try {
                        img_center.setImageResource(R.drawable.ic_no_safe);
                    } catch (Exception ex) {
                        img_center = (ImageView) findViewById(R.id.img_center);
                        img_center.setImageResource(R.drawable.ic_no_safe);
                    }
                    tv_delete_safe.setVisibility(View.GONE);
                    tv_safe_kids.setVisibility(View.VISIBLE);
                }
            }
        };

        img_center = (ImageView) findViewById(R.id.img_center);
        tv_over_speed = (TextView) findViewById(R.id.tv_over_speed);

        drawerLayout = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerLayout.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), null);
        mDrawerLayout.closeDrawer(findViewById(R.id.fragment_navigation_drawer));

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (SpeedService.context == null) {
                startService(new Intent(this, SpeedService.class));
            }

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            speedPermission = true;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        registerAlarm(this);

        //Intent intent = new Intent(MainActivity.this, RedAlert.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        //startService(new Intent(this,NotiNearbyService.class));
    }



    @OnClick(R.id.tv_add_kids)
    public void addKids() {
        startActivityForResult(new Intent(this, KidsList.class), 1);
    }

    @OnClick(R.id.tv_phones)
    public void addPhoneNumber() {
        startActivity(new Intent(this, PhoneNumberList.class));
    }

    @OnClick(R.id.tv_safe_kids)
    public void safeKids() {
        stopService(new Intent(this, MonitorService.class));
        showPopupChooseKids();
        //startActivity(new Intent(this, RedAlert.class));
    }

    @OnClick(R.id.tv_delete_safe)
    public void deleteSafe() {

        stopService(new Intent(this, MonitorService.class));

        editor.remove("MesKidsStatus").commit();
        listStatus.clear();

        editor.remove("Alert").commit();

        img_center_res = 0;
        try {
            img_center.setImageResource(R.drawable.ic_no_safe);
        } catch (Exception ex) {
            img_center = (ImageView) findViewById(R.id.img_center);
            img_center.setImageResource(R.drawable.ic_no_safe);
        }

        tv_delete_safe.setVisibility(View.GONE);
        tv_safe_kids.setVisibility(View.VISIBLE);

        Uri sound;
        if (langage.equals("ar")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_ar);
        } else if (langage.equals("he")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_he);
        } else {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_en);
        }
        /*Ringtone ringtoneNew = RingtoneManager.getRingtone(getApplicationContext(), sound);
        ringtoneNew.play();*/
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        MediaPlayer ringtoneNew = MediaPlayer.create(getApplicationContext(), sound);
        ringtoneNew.start();

    }

    @OnClick(R.id.imgHelp)
    public void showHelp() {
        startActivity(new Intent(this, Notice.class));
    }

    @OnClick(R.id.imgTranslate)
    public void translate() {

        // make alerte
        showPopupChooseLanguage();
    }

    private void showPopupChooseLanguage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);

        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_language_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_language_popup_en, null);
        }
        dialog.setView(dialogView);

        final AlertDialog alerteLanguage = dialog.create();
        alerteLanguage.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alerteLanguage.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.rb3);

        if (langage.equals("ar"))
            rb1.setChecked(true);
        if (langage.equals("en"))
            rb2.setChecked(true);
        if (langage.equals("he"))
            rb3.setChecked(true);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                }
            }
        });

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rb1.isChecked() && !langage.equals("ar")) {
                    editor.putString("Langage", "ar");
                    editor.commit();
                    Locale myLocale = new Locale("ar");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (rb2.isChecked() && !langage.equals("en")) {
                    editor.putString("Langage", "en");
                    editor.commit();
                    Locale myLocale = new Locale("en");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else if (rb3.isChecked() && !langage.equals("he")) {
                    editor.putString("Langage", "he");
                    editor.commit();
                    /**/
                    Locale myLocale = new Locale("he");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                alerteLanguage.dismiss();
            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerteLanguage.dismiss();
            }
        });
    }

    @OnClick(R.id.imgDrawer)
    public void toggelDrawer() {
        if (mDrawerLayout.isDrawerOpen(findViewById(R.id.fragment_navigation_drawer))) {
            mDrawerLayout.closeDrawer(findViewById(R.id.fragment_navigation_drawer));
        } else {
            mDrawerLayout.openDrawer(findViewById(R.id.fragment_navigation_drawer));
        }
    }

    public void showPopupChooseKids() {

        alertKidsList.clear();
        chooseAllKids = false;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);
        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_kid_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_kid_popup_en, null);
        }
        dialog.setView(dialogView);

        alertChooseKids = dialog.create();
        alertChooseKids.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alertChooseKids.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertKidsList.size() == 0) {
                    Toast.makeText(MainActivity.this, "" + getResources().getString(R.string.choose_kids), Toast.LENGTH_SHORT).show();
                } else {
                    alertChooseKids.dismiss();
                    showPopupChooseTime();
                }
            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertChooseKids.dismiss();
            }
        });

        RecyclerView rvCat = (RecyclerView) dialogView.findViewById(R.id.rvListKids);
        final KidsChooseItemAdapter kidsChooseItemAdapter = new KidsChooseItemAdapter(this, allKids);

        rvCat.setLayoutManager(new LinearLayoutManager(this));
        rvCat.setAdapter(kidsChooseItemAdapter);
        rvCat.setItemViewCacheSize(50);

        final CheckBox checkAll = (CheckBox) dialogView.findViewById(R.id.checkAll);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chooseAllKids = b;
                kidsChooseItemAdapter.notifyDataSetChanged();
            }
        });

        TextView txtCheckAll = (TextView) dialogView.findViewById(R.id.txtCheckAll);
        txtCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll.setChecked(!checkAll.isChecked());
            }
        });
    }

    public void showPopupChooseTime() {

        extraTime = 5;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);

        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_time_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_time_popup_en, null);
        }
        dialog.setView(dialogView);

        alertTimer = dialog.create();
        alertTimer.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alertTimer.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        rlBlock = (RelativeLayout) dialogView.findViewById(R.id.rlBlock);
        rlBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date alertDate = new Date();

                newAlertModel = new AlertModel(alertKidsList, alertDate, extraTime);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    GPSTracker tracker = new GPSTracker(MainActivity.this);
                    if (!tracker.canGetLocation()) {
                        tracker.showSettingsAlert();
                        alertTimer.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.repeat_after_setting), Toast.LENGTH_LONG).show();
                    } else {
                        rlBlock.setVisibility(View.VISIBLE);
                        latitude = tracker.getLatitude();
                        longitude = tracker.getLongitude();

                        if (latitude != 0.0) {
                            editor.putString("Latitude", latitude + "");
                            editor.commit();
                        } else {
                            try {
                                latitude = Double.parseDouble(prefs.getString("Latitude", "0.0"));
                            } catch (Exception e) {
                                latitude = 0.0;
                            }
                        }
                        if (longitude != 0.0) {
                            editor.putString("Longitude", longitude + "");
                            editor.commit();
                        } else {
                            try {
                                longitude = Double.parseDouble(prefs.getString("Longitude", "0.0"));
                            } catch (Exception e) {
                                longitude = 0.0;
                            }
                        }

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String cityName = "";
                        String stateName = "";
                        String countryName = "";
                        if (addresses != null && addresses.size() > 0) {

                            cityName = addresses.get(0).getAddressLine(0);
                            if (cityName == null)
                                cityName = "";

                            try {
                                stateName = ", " + addresses.get(0).getAddressLine(1);

                            } catch (Exception e) {
                                stateName = "";
                            }

                            try {
                                countryName = ", " + addresses.get(0).getAddressLine(2);
                            } catch (Exception e) {
                                countryName = "";
                            }

                            if (!TextUtils.isEmpty(cityName) && cityName != null) {
                                editor.putString("CityName", cityName);
                                editor.commit();
                            } else {
                                try {
                                    cityName = prefs.getString("CityName", "");
                                    if (cityName == null)
                                        cityName = "";
                                } catch (Exception e) {
                                    cityName = "";
                                }
                            }

                            if (!TextUtils.isEmpty(stateName) && stateName != null) {
                                editor.putString("StateName", stateName);
                                editor.commit();
                            } else {
                                try {
                                    stateName = prefs.getString("StateName", "");
                                    if (stateName == null)
                                        stateName = "";
                                } catch (Exception e) {
                                    stateName = "";
                                }
                            }

                            if (!TextUtils.isEmpty(countryName) && countryName != null) {
                                editor.putString("CountryName", countryName);
                                editor.commit();
                            } else {
                                try {
                                    countryName = prefs.getString("CountryName", "");
                                    if (countryName == null)
                                        countryName = "";
                                } catch (Exception e) {
                                    countryName = "";
                                }
                            }

                        } else {

                            try {
                                countryName = prefs.getString("CountryName", "");
                                if (countryName == null)
                                    countryName = "";
                            } catch (Exception e) {
                                countryName = "";
                            }

                            try {
                                stateName = prefs.getString("StateName", "");
                                if (stateName == null)
                                    stateName = "";
                            } catch (Exception e) {
                                stateName = "";
                            }

                            try {
                                cityName = prefs.getString("CityName", "");
                                if (cityName == null)
                                    cityName = "";
                            } catch (Exception e) {
                                cityName = "";
                            }

                        }

                        newAlertModel.setLocationName(cityName + stateName + countryName);
                        newAlertModel.setLatitude(latitude);
                        newAlertModel.setLongitude(longitude);

                        // save all information altitude longitude and adresse

                        String alertJson = gson.toJson(newAlertModel);
                        editor.putString("Alert", alertJson);
                        editor.commit();

                        listAlert.add(newAlertModel);
                        String json = gson.toJson(listAlert);
                        editor.putString("MesAlerts", json);
                        editor.commit();

                        startService(new Intent(MainActivity.this, MonitorService.class));

                        Uri sound;
                        if (langage.equals("ar")) {
                            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.safe_ar);
                        } else if (langage.equals("he")) {
                            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.safe_he);
                        } else {
                            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.safe_en);
                        }
                        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);


                        MediaPlayer ringtoneNew = MediaPlayer.create(getApplicationContext(), sound);
                        ringtoneNew.start();


                        alertTimer.dismiss();

                        img_center_res = 1;
                        try {
                            img_center.setImageResource(R.drawable.ic_delete_safe);
                        } catch (Exception e) {
                            img_center = (ImageView) findViewById(R.id.img_center);
                            img_center.setImageResource(R.drawable.ic_delete_safe);
                        }

                        tv_delete_safe.setVisibility(View.VISIBLE);
                        tv_safe_kids.setVisibility(View.GONE);

                        for (int i = 0; i < alertKidsList.size(); i++) {
                            listStatus.add(alertKidsList.get(i).getObjectId());
                        }

                        String jsonStatus = gson.toJson(listStatus);
                        editor.putString("MesKidsStatus", jsonStatus);
                        editor.commit();
                    }


                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertTimer.dismiss();
            }
        });

        final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.rb3);
        final RadioButton rb4 = (RadioButton) dialogView.findViewById(R.id.rb4);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                    rb4.setChecked(false);

                    extraTime = 5;

                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb3.setChecked(false);
                    rb4.setChecked(false);

                    extraTime = 10;

                }
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb4.setChecked(false);

                    extraTime = 15;

                }
            }
        });

        rb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    rb3.setChecked(false);

                    extraTime = 20;

                }
            }
        });

    }

    void getListKids() {
        allKids.clear();
        String jsonKids = prefs.getString("MesKids", null);
        if (jsonKids != null) {
            Kids[] kidItems = gson.fromJson(jsonKids, Kids[].class);
            allKids = Arrays.asList(kidItems);
            allKids = new ArrayList(allKids);
        }
    }

    void getListPhones() {
        allPhones.clear();
        String jsonPhone = prefs.getString("MesPhones", null);
        if (jsonPhone != null) {
            PhoneNumber[] phoneItems = gson.fromJson(jsonPhone, PhoneNumber[].class);
            allPhones = Arrays.asList(phoneItems);
            allPhones = new ArrayList(allPhones);
        }
    }

    void getListKidsStatus() {
        listStatus.clear();
        String jsonKidsStatus = prefs.getString("MesKidsStatus", null);
        if (jsonKidsStatus != null) {
            String[] statusItems = gson.fromJson(jsonKidsStatus, String[].class);
            listStatus = Arrays.asList(statusItems);
            listStatus = new ArrayList(listStatus);
        }
    }

    void getListAlerts() {
        listAlert.clear();
        String jsonKidsStatus = prefs.getString("MesAlerts", null);
        if (jsonKidsStatus != null) {
            try {
                AlertModel[] items = gson.fromJson(jsonKidsStatus, AlertModel[].class);
                listAlert = Arrays.asList(items);
                listAlert = new ArrayList(listAlert);
            } catch (Exception e) {

            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getListKids();
        getListAlerts();
        getListKidsStatus();

        try {
            getListPhones();
        } catch (Exception ex) {

        }

        try {
            if (allKids == null || allKids.size() == 0) {
                tv_add_kids.setVisibility(View.VISIBLE);
                tv_phones.setVisibility(View.GONE);
                tv_safe_kids.setVisibility(View.GONE);
                tv_delete_safe.setVisibility(View.GONE);

                img_center_res = 0;
                try {
                    img_center.setImageResource(R.drawable.ic_no_safe);
                } catch (Exception ex) {
                    img_center = (ImageView) findViewById(R.id.img_center);
                    img_center.setImageResource(R.drawable.ic_no_safe);
                }
            } else if (allPhones == null || allPhones.size() == 0) {
                tv_phones.setVisibility(View.VISIBLE);
                tv_add_kids.setVisibility(View.GONE);
                tv_safe_kids.setVisibility(View.GONE);
                tv_delete_safe.setVisibility(View.GONE);

                img_center_res = 0;
                try {
                    img_center.setImageResource(R.drawable.ic_no_safe);
                } catch (Exception ex) {
                    img_center = (ImageView) findViewById(R.id.img_center);
                    img_center.setImageResource(R.drawable.ic_no_safe);
                }

            } else if (!prefs.getString("Alert", "").equals("")) {
                float temp = 0;
                try {
                    if (!MonitorService.temperature.equals("")) {
                        temp = Float.parseFloat(MonitorService.temperature.substring(0, MonitorService.temperature.length() - 1).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3")
                                .replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7")
                                .replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0").replaceAll(",", "."));
                    }
                } catch (Exception e) {
                    if (!MonitorService.temperature.equals("")) {
                        temp = Float.parseFloat(MonitorService.temperature.substring(0, 1).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3")
                                .replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7")
                                .replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0").replaceAll(",", "."));
                    }
                }
                int tempMax = prefs.getInt("TempMax", 40);
                if (temp > tempMax) {
                    img_center_res = 2;
                    try {
                        img_center.setImageResource(R.drawable.ic_delete_safe_4);
                    } catch (Exception ex) {
                        img_center = (ImageView) findViewById(R.id.img_center);
                        img_center.setImageResource(R.drawable.ic_delete_safe_4);
                    }
                    tv_top_hot.setVisibility(View.VISIBLE);
                } else {
                    img_center_res = 1;
                    try {
                        img_center.setImageResource(R.drawable.ic_delete_safe);
                    } catch (Exception ex) {
                        img_center = (ImageView) findViewById(R.id.img_center);
                        img_center.setImageResource(R.drawable.ic_delete_safe);
                    }
                    tv_top_hot.setVisibility(View.GONE);
                }
                tv_add_kids.setVisibility(View.GONE);
                tv_phones.setVisibility(View.GONE);
                tv_safe_kids.setVisibility(View.GONE);
                tv_delete_safe.setVisibility(View.VISIBLE);

            } else {
                tv_add_kids.setVisibility(View.GONE);
                tv_phones.setVisibility(View.GONE);
                tv_safe_kids.setVisibility(View.VISIBLE);
                tv_delete_safe.setVisibility(View.GONE);

                img_center_res = 0;
                try {
                    img_center.setImageResource(R.drawable.ic_no_safe);
                } catch (Exception ex) {
                    img_center = (ImageView) findViewById(R.id.img_center);
                    img_center.setImageResource(R.drawable.ic_no_safe);
                }
            }
        } catch (Exception e) {
            /// Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);

                alertChooseKids.dismiss();
                showPopupChooseTime();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (speedPermission) {

                    if (SpeedService.context == null) {
                        startService(new Intent(this, SpeedService.class));
                    }

                    speedPermission = false;
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    rlBlock.setVisibility(View.VISIBLE);
                    GPSTracker tracker = new GPSTracker(MainActivity.this);
                    if (!tracker.canGetLocation()) {
                        tracker.showSettingsAlert();
                    } else {
                        latitude = tracker.getLatitude();
                        longitude = tracker.getLongitude();

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses != null && addresses.size() > 0) {
                            String cityName = addresses.get(0).getAddressLine(0);
                            String stateName = addresses.get(0).getAddressLine(1);
                            String countryName = addresses.get(0).getAddressLine(2);

                            newAlertModel.setLocationName(cityName);
                        }

                        newAlertModel.setLatitude(latitude);
                        newAlertModel.setLongitude(longitude);

                        String alertJson = gson.toJson(newAlertModel);
                        editor.putString("Alert", alertJson);
                        editor.commit();

                        startService(new Intent(MainActivity.this, MonitorService.class));

                        alertTimer.dismiss();

                        img_center_res = 1;
                        try {
                            img_center.setImageResource(R.drawable.ic_delete_safe);
                        } catch (Exception ex) {
                            img_center = (ImageView) findViewById(R.id.img_center);
                            img_center.setImageResource(R.drawable.ic_delete_safe);
                        }
                        tv_delete_safe.setVisibility(View.VISIBLE);
                        tv_safe_kids.setVisibility(View.GONE);
                    }

                }


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
        }
    }

    @OnClick(R.id.img_fb)
    public void shareFb() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/559668990902498")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/safekidss")));
        }

    }

    @OnClick(R.id.img_tw)
    public void shareTw() {
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://page?screen_name=783982968276676608")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/safekidss")));
        }

    }

    @OnClick(R.id.img_g)
    public void shareG() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/114318675797509026254")));
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.img_in)
    public void shareIn() {
        try {
            getPackageManager().getPackageInfo("com.instagram.android", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/safekidss")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/safekidss")));
        }

    }

    @OnClick(R.id.img_y)
    public void shareY() {
        String url = "https://www.youtube.com/channel/UCccvFrsy0l8QU6RdCMLcYdQ ";
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    @OnClick(R.id.img_p)
    public void shareP() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/safekidss/")));
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.img_s)
    public void shareS() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/safekidss")));
        } catch (Exception e) {

        }
    }

    public void registerAlarm(Context context) {

        Intent myIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 0); // first time
        long frequency = 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);

    }

    public static void showSpeedAlert() {
        if (tv_over_speed != null && img_center != null) {
            tv_over_speed.setVisibility(View.VISIBLE);
            img_center_res = 3;
            img_center.setImageResource(R.drawable.ic_delete_safe_3);
        }
    }

    public static void deleteSpeedAlert() {
        if (tv_over_speed != null && img_center != null) {
            tv_over_speed.setVisibility(View.GONE);
            if (img_center_res == 0) {
                img_center.setImageResource(R.drawable.ic_no_safe);
            } else if (img_center_res == 1) {
                img_center.setImageResource(R.drawable.ic_delete_safe);
            } else if (img_center_res == 2) {
                img_center.setImageResource(R.drawable.ic_delete_safe_4);
            } else {
                img_center.setImageResource(R.drawable.ic_no_safe);
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        boolean alarmeExit = prefs.getBoolean("AlarmeExitActif", false);

        if (mDrawerLayout.isDrawerOpen(findViewById(R.id.fragment_navigation_drawer))) {
            mDrawerLayout.closeDrawer(findViewById(R.id.fragment_navigation_drawer));
        } else if (!prefs.getString("Alert", "").equals("") && alarmeExit) {
            startActivity(new Intent(this, NormalAlert.class));
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpen = false;
        tv_over_speed = null;
        img_center = null;
        img_center_res = -1;
    }
}
