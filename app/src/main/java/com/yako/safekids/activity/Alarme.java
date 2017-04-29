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
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.safekids.android.R;
import com.yako.safekids.model.Kids;
import com.yako.safekids.model.KidsPlaningModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Alarme extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_age)
    TextView tv_age;

    @BindView(R.id.img_kids)
    ImageView img_kids;

    @BindView(R.id.img_cancel)
    ImageView img_cancel;

    @BindView(R.id.img_accept)
    ImageView img_accept;

    @BindView(R.id.tv_texto)
    TextView tv_texto;

    public static KidsPlaningModel kidsPlaningModel;
    Kids curentKids;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Window wind;
    MediaPlayer ringtone;
    private SharedPreferences prefs;
    private String langage;

    public static void start(Context context, KidsPlaningModel kids) {
        kidsPlaningModel = kids;

        Intent intent = new Intent(context, Alarme.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_alarme);
        } else {
            setContentView(R.layout.activity_alarme_en);
        }

        ButterKnife.bind(this);


        if (kidsPlaningModel != null) {

            curentKids = kidsPlaningModel.getKids();

            PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenLock.acquire();

            wind = this.getWindow();
            wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
            //ringtone = MediaPlayer.create(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hola_notification));
            if(ringtone != null)
                ringtone.start();

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try{
                        ringtone.stop();
                    }catch (Exception e){}
                }
            };
            handler.postDelayed(r, 1000 * 10);

            if (curentKids.getPhoto() != null && !curentKids.equals("")) {
                ImageLoader.getInstance().loadImage(curentKids.getPhoto(), options, new ImageLoadingListener() {
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
            tv_name.setText(curentKids.getName());

            if (langage.equals("ar")) {
                String age;
                if (curentKids.getAge() == 1) {
                    age = "سنة";
                } else if (curentKids.getAge() == 2) {
                    age = "سنتان";
                } else if (curentKids.getAge() < 11) {
                    age = curentKids.getAge() + " سنوات";
                } else {
                    age = curentKids.getAge() + " سنة";
                }
                tv_age.setText(age);
            } else {
                String age;
                if (curentKids.getAge() == 1) {
                    age = "1 "+getResources().getString(R.string.one_year_normal_alert);
                } else {
                    age = curentKids.getAge() + " "+getResources().getString(R.string.nombre_years_normal_alert);
                }
                tv_age.setText(age);
            }

            if (!kidsPlaningModel.getTexto().equals("")) {
                tv_texto.setText(kidsPlaningModel.getTexto());
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.img_cancel)
    public void cancel() {
        finish();
    }

    @OnClick(R.id.img_accept)
    public void accept() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (ringtone != null)
            ringtone.stop();
        curentKids = null;
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
