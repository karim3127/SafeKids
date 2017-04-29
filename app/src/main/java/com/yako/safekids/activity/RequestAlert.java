package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.safekids.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RequestAlert extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    @BindView(R.id.tv_valid)
    TextView tv_valid;

    private SharedPreferences prefs;
    private String langage;
    public static Context context = null;
    private Window wind;
    private MediaPlayer ringtone, thePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_requet_alert);
        } else {
            setContentView(R.layout.activity_requet_alert_en);
        }
        ButterKnife.bind(this);

        context = this;

        PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Uri sound;
        if (langage.equals("ar")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stop_ar);
        } else if (langage.equals("he")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stop_he);
        } else {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stop_en);
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        ringtone = MediaPlayer.create(getApplicationContext(), sound);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        thePlayer = MediaPlayer.create(getApplicationContext(),notification);
        //thePlayer = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hola_notification));
        if(thePlayer != null)
            thePlayer.start();

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

        tv_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestAlert.this, MainActivity.class));
                finish();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        context = null;
        if (ringtone != null)
            ringtone.stop();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
