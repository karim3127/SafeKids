package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.safekids.android.R;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.service.MonitorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FirstAlert extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_age)
    TextView tv_age;

    @BindView(R.id.tv_oxygen)
    TextView tv_oxygen;

    @BindView(R.id.tv_temperature)
    TextView tv_temperature;

    @BindView(R.id.tv_timer)
    TextView tv_timer;

    @BindView(R.id.tv_geo)
    TextView tv_geo;

    @BindView(R.id.tv_location)
    TextView tv_location;

    @BindView(R.id.img_kids)
    ImageView img_kids;

    @BindView(R.id.img_cancel)
    ImageView img_cancel;

    @BindView(R.id.img_accept)
    ImageView img_accept;

    int timeout = 30;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private AlertModel alertModel = null;
    private Window wind;
    MediaPlayer ringtone;
    boolean finish = false;
    private String langage;
    MediaPlayer thePlayer;//, thePlayer1, thePlayer2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_first_alert);
        } else {
            setContentView(R.layout.activity_first_alert_en);
        }

        ButterKnife.bind(this);

        editor = prefs.edit();
        gson = new Gson();

        finish = getIntent().getBooleanExtra("Finish", false);

        String json = prefs.getString("Alert", null);
        if (json != null)
            alertModel = gson.fromJson(json, AlertModel.class);

        if (alertModel != null) {
            PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenLock.acquire();

            wind = this.getWindow();
            wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            Uri sound;
            switch (langage) {
                case "ar":
                    sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.go_car_ar);
                    break;
                case "he":
                    sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.go_car_he);
                    break;
                default:
                    sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.go_car_en);
                    break;
            }
            //ringtone = RingtoneManager.getRingtone(getApplicationContext(), sound);
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
            ringtone = MediaPlayer.create(getApplicationContext(), sound);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            thePlayer = MediaPlayer.create(getApplicationContext(),notification);
            //thePlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hola_notification));
            //thePlayer1 = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hola_notification));
            //thePlayer2 = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hola_notification));
            try {
                thePlayer.start();
            }catch (Exception ignored){}
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try{
                        thePlayer.stop();
                    }catch (Exception e){}
                    ringtone.start();
                }
            };
            handler.postDelayed(r, 1000 * 10);
            /*final Handler handler2 = new Handler();
            final Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    try{
                        thePlayer2.stop();
                    }catch (Exception ignored){}

                    ringtone.start();
                }
            };

            final Handler handlerretardsecond = new Handler();
            final  Runnable rretardsecond = new Runnable() {
                @Override
                public void run() {
                    try {
                        thePlayer2.start();
                    }catch (Exception ignored){}

                    handler2.postDelayed(r2,500);
                }
            };

            final Handler handler1 = new Handler();
            final Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    try{
                        thePlayer1.stop();
                    }catch (Exception ignored){}

                    handlerretardsecond.postDelayed(rretardsecond,500);

                }
            };

            final Handler handlerretard = new Handler();
           final  Runnable rretard = new Runnable() {
                @Override
                public void run() {
                    try {
                        thePlayer1.start();
                    }catch (Exception ignored){}

                    handler1.postDelayed(r1,500);
                }
            };

            final Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try{
                        thePlayer.stop();
                    }catch (Exception ignored){}

                   handlerretard.postDelayed(rretard,500);
                }
            };
            handler.postDelayed(r,500);*/

            if (alertModel.getListKids().get(0).getPhoto() != null && !alertModel.getListKids().get(0).equals("")) {
                ImageLoader.getInstance().loadImage(alertModel.getListKids().get(0).getPhoto(), options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        img_kids.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                img_kids.setImageResource(R.drawable.ic_avatar_kids);
            }
            tv_name.setText(alertModel.getListKids().get(0).getName());

            if (langage.equals("ar")) {
                String age;
                if (alertModel.getListKids().get(0).getAge() == 1) {
                    age = "سنة";
                } else if (alertModel.getListKids().get(0).getAge() == 2) {
                    age = "سنتان";
                } else if (alertModel.getListKids().get(0).getAge() < 11) {
                    age = alertModel.getListKids().get(0).getAge() +  " سنوات" ;
                } else {
                    age = alertModel.getListKids().get(0).getAge() +  " سنة" ;
                }
                tv_age.setText(age);
            } else {
                String age;
                if (alertModel.getListKids().get(0).getAge() == 1) {
                    age = "1 "+getResources().getString(R.string.one_year_normal_alert);
                } else {
                    age = alertModel.getListKids().get(0).getAge() +  " "+getResources().getString(R.string.nombre_years_normal_alert) ;
                }
                tv_age.setText(age);
            }

            tv_location.setText(alertModel.getLocationName());
            tv_geo.setText(String.format("%.2f", alertModel.getLatitude()) + " ° " + String.format("%.2f", alertModel.getLongitude()));

            tv_oxygen.setText(MonitorService.humidity);
            tv_temperature.setText(MonitorService.temperature);

            if (finish) {
                tv_timer.setText("00:00");
            } else {
                CountDownTimer countDownTimer = new CountDownTimer(1000 * 30, 1000) {
                    public void onTick(long millisUntilFinished) {
                        timeout--;
                        tv_timer.setText("00:" + ((timeout > 9) ? timeout : ("0" + timeout)));
                    }

                    public void onFinish() {
                        finish();
                    }
                };

                countDownTimer.start();
            }

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @OnClick(R.id.img_cancel)
    public void cancel() {
        if (ringtone != null)
            ringtone.stop();

        Uri sound;
        if (langage.equals("ar")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_ar);
        } else if (langage.equals("he")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_he);
        } else {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alert_cancel_en);
        }
        MediaPlayer ringtoneNew = MediaPlayer.create(getApplicationContext(), sound);
        ringtoneNew.start();

        editor.remove("MesKidsStatus").commit();
        editor.remove("Alert").commit();

        stopService(new Intent(this, MonitorService.class));
        NormalAlert.finishActivity();
        finish();
    }

    @OnClick(R.id.img_accept)
    public void accept() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null)
            ringtone.stop();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
