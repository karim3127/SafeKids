package com.yako.safekids.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.safekids.android.R;
import com.yako.safekids.adapter.PagerNormalAlertAdapter;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.view.tabs.SlidingTabLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NormalAlert extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    SlidingTabLayout mTabs;

    @BindView(R.id.img_close)
    ImageView img_close;

    @BindView(R.id.imgKids)
    CircleImageView imgKids;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.img_back)
    ImageView img_back;

    @BindView(R.id.txtAge)
    TextView txtAge;

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
    public AlertModel alertModel = null;

    public static Context context = null;
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_normal_alert);
        } else {
            setContentView(R.layout.activity_normal_alert_en);
        }

        ButterKnife.bind(this);

        context = this;

        editor = prefs.edit();
        gson = new Gson();

        String json = prefs.getString("Alert", null);
        if (json != null)
            alertModel = gson.fromJson(json, AlertModel.class);

        if (alertModel != null) {

            Date now = new Date();
            Date alertDate = alertModel.getAlertDate();

            long diff = now.getTime() - alertDate.getTime();
            long timeRest = (alertModel.getExtraTime() * 1000 * 60) - diff;
            if (timeRest <= 0) {
                finish();
            }

            mPager.setOffscreenPageLimit(2);
            mTabs.setDistributeEvenly(true);

            mPager.setAdapter(new PagerNormalAlertAdapter(getSupportFragmentManager(), this));
            if (langage.equals("ar")) {
                mPager.setCurrentItem(1);
            } else {
                mPager.setCurrentItem(0);
            }

            mTabs.setViewPager(mPager);

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
                        imgKids.setImageBitmap(loadedImage);
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
            txtName.setText(alertModel.getListKids().get(0).getName());

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
                txtAge.setText("العمر: " + age);

            } else {
                String age;
                if (alertModel.getListKids().get(0).getAge() == 1) {
                    age = "1 "+getResources().getString(R.string.one_year_normal_alert);
                } else  {
                    age = alertModel.getListKids().get(0).getAge() + " "+getResources().getString(R.string.nombre_years_normal_alert);
                }
                txtAge.setText(getResources().getString(R.string.age_normal_alerte)+" " + age);

            }

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @OnClick(R.id.img_close)
    public void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        context = null;
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
