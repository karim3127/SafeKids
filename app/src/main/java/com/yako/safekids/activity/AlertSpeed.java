package com.yako.safekids.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.safekids.android.R;
import com.yako.safekids.service.SpeedService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AlertSpeed extends AppCompatActivity {

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.imgKids)
    CircleImageView imgKids;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtNextTime)
    TextView txtNextTime;

    @BindView(R.id.img_back)
    ImageView img_back;

    public static TextView tv_speed;

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
    private Window wind;
    public static Context context = null;
    private String langage;
    MediaPlayer ringtone, thePlayer;
    String name, photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_alert_speed);
        } else {
            setContentView(R.layout.activity_alert_speed_en);
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
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.speed_ar);
        } else if (langage.equals("he")) {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.speed_he);
        } else {
            sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.speed_en);
        }
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        ringtone = MediaPlayer.create(getApplicationContext(), sound);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        thePlayer = MediaPlayer.create(getApplicationContext(),notification);
        if(thePlayer != null)
            thePlayer.start();

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    thePlayer.stop();
                }catch (Exception ignored){}
                ringtone.start();
            }
        };
        handler.postDelayed(r, 1000 * 10);

        editor = prefs.edit();
        gson = new Gson();

        name = prefs.getString("name", "");
        photo = prefs.getString("UserPhoto", "");

        tv_speed = (TextView) findViewById(R.id.tv_speed);

        if (!photo.equals("")) {
            ImageLoader.getInstance().displayImage(photo,imgKids, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //imgKids.setImageBitmap(loadedImage);
                    Bitmap bm = NativeStackBlur.process(loadedImage, getResources().getInteger(R.integer.blur_radius));
                    img_back.setImageBitmap(bm);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            imgKids.setImageResource(R.drawable.ic_avatar_kids);
            Bitmap bm = NativeStackBlur.process(BitmapFactory.decodeResource(getResources(), R.drawable.bg_profil_setting), getResources().getInteger(R.integer.blur_radius));
            img_back.setImageBitmap(bm);
        }
        txtName.setText(name);

        try {
            SpannableString s = new SpannableString(String.format("%.0f", SpeedService.data.getCurSpeed()) + "km/h");
            s.setSpan(new RelativeSizeSpan(0.25f), s.length()-4, s.length(), 0);
            tv_speed.setText(String.format("%.0f", SpeedService.data.getCurSpeed()));

        } catch (Exception e) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @OnClick(R.id.img_close)
    public void close() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.txtNextTime)
    public void nextAlert() {
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, min + 15);

        editor.putLong("NextDateSpeed", calendar.getTime().getTime()).commit();
        finish();
    }

    @Override
    protected void onDestroy() {
        context = null;
        tv_speed = null;
        if (ringtone != null)
            ringtone.stop();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static void finishActivity() {
        if (context != null)
            ((Activity) context).finish();
    }

}
