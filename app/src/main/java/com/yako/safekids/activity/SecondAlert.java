package com.yako.safekids.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.yako.safekids.model.AlertModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SecondAlert extends AppCompatActivity {

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

    public static AlertModel alertModel;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    private Window wind;
    Ringtone ringtone;

    public static void start(Context context, AlertModel model) {
        if (model != null) {
            alertModel = model;

            Intent intent = new Intent(context, SecondAlert.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_alert);
        ButterKnife.bind(this);

        if (alertModel != null) {
            PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenLock.acquire();

            wind = this.getWindow();
            wind.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            wind.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            wind.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);

            ringtone = RingtoneManager.getRingtone(getApplicationContext(), sound);
            ringtone.play();


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
        alertModel = null;
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
