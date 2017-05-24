package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.safekids.android.R;
import com.yako.safekids.model.BackendSetting;
import com.yako.safekids.service.NotiNearbyService;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hp on 23/04/2017.
 */

public class RedAlert extends AppCompatActivity {

    public static AppCompatActivity activity;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    @BindView(R.id.tv_valid)
    TextView tv_valid;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    MediaPlayer ringtone;
    private String langage;
    MediaPlayer thePlayer;

    private Window wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");
        editor = prefs.edit();
        editor.putString("tosendpush", "oui");
        editor.apply();

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_red_alert);
        } else {
            setContentView(R.layout.activity_red_alert_en);
        }

        ButterKnife.bind(this);

        PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        wind = this.getWindow();
        wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        wind.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        startService(new Intent(this, NotiNearbyService.class));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

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

        ringtone = MediaPlayer.create(getApplicationContext(), sound);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        thePlayer = MediaPlayer.create(getApplicationContext(),notification);
        if(thePlayer != null)
            thePlayer.start();

        final Handler hFirst = new Handler();
        final Runnable rFirst = new Runnable() {
            @Override
            public void run() {
                try{
                    ringtone.stop();
                }catch (Exception ignored){}
            }
        };

        final Handler hSecond = new Handler();
        final Runnable rSecond = new Runnable() {
            @Override
            public void run() {
                try{
                    ringtone.stop();
                }catch (Exception ignored){}

                if(ringtone != null)
                    ringtone.start();
                hFirst.postDelayed(rFirst, 1000 * 10);
            }
        };

        final Handler hThird = new Handler();
        final Runnable rThird = new Runnable() {
            @Override
            public void run() {
                try{
                    ringtone.stop();
                }catch (Exception ignored){}

                if(ringtone != null)
                    ringtone.start();
                hSecond.postDelayed(rSecond, 1000 * 10);
            }
        };

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    thePlayer.stop();
                }catch (Exception ignored){}

                if(ringtone != null)
                    ringtone.start();
                hThird.postDelayed(rThird, 1000 * 10);
            }
        };
        handler.postDelayed(r, 1000 * 10);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        tv_valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept();
            }
        });
    }

    private void cancel() {
        if (ringtone != null)
            ringtone.stop();
        NormalAlert.finishActivity();
        finish();
    }

    private void accept() {
        NormalAlert.finishActivity();
        editor.putString("tosendpush", "non");
        editor.apply();
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
