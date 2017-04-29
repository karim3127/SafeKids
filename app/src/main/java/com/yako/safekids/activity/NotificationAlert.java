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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.safekids.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hp on 23/04/2017.
 */
public class NotificationAlert extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    @BindView(R.id.tv_valid)
    TextView tv_valid;

    @BindView(R.id.tv_top)
    TextView tv_top;

    private SharedPreferences prefs;
    MediaPlayer thePlayer;
    private String langage;

    String latitude="", longtitude="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_notif_alert);
        } else {
            setContentView(R.layout.activity_notif_alert_en);
        }

        ButterKnife.bind(this);//messageForAlerte
        tv_top.setText(getIntent().getExtras().getString("messageForAlerte"));

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        thePlayer = MediaPlayer.create(getApplicationContext(),notification);
        if(thePlayer != null)
            thePlayer.start();
        /*
        final Handler handlersecond = new Handler();
        final Runnable rsecond = new Runnable() {
            @Override
            public void run() {
                try{
                    thePlayer.stop();
                }catch (Exception ignored){}
            }
        };
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    if(thePlayer != null)
                        thePlayer.start();
                }catch (Exception ignored){}

                handlersecond.postDelayed(rsecond, 1000 * 10);
            }
        };
        handler.postDelayed(r, 1000 * 10);
         */

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    thePlayer.stop();
                }catch (Exception e){}
            }
        };
        handler.postDelayed(r, 1000 * 10);

        latitude = getIntent().getExtras().getString("latitude");
        longtitude = getIntent().getExtras().getString("longtitude");

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
        finish();
    }

    private void accept() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longtitude+"");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thePlayer != null)
            thePlayer.stop();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
