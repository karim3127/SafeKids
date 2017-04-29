package com.yako.safekids;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.backendless.Backendless;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.safekids.android.BuildConfig;
import com.safekids.android.R;
import com.yako.safekids.model.BackendSetting;
import com.yako.safekids.model.Kids;
import com.yako.safekids.model.PhoneNumber;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.util.Locale;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by karizmaltd1 on 29/03/2016.
 */

@ReportsCrashes(
        formUri = "",
        mailTo = "safekids.kids@gmail.com"
)

public class SafeKidsApplication extends Application {

    private static SafeKidsApplication instance;

    public static SafeKidsApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        ButterKnife.setDebug(BuildConfig.DEBUG);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ElMessiri-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplicationContext());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        //config.diskCacheSize(250 * 1024 * 1024); // 250 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());

        String defaultLangage = Locale.getDefault().getLanguage();
        SharedPreferences prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        if (prefs.getString("Langage", "").equals("")) {
            SharedPreferences.Editor editor = prefs.edit();

            if (defaultLangage.equals("ar") ) {
                editor.putString("Langage", "ar");
            } else if (defaultLangage.equals("iw") ) {
                editor.putString("Langage", "he");
            }else {
                editor.putString("Langage", "en");
            }
            editor.apply();
        }

        Locale locale = new Locale(prefs.getString("Langage", "en"));
        Locale.setDefault(locale);
        Configuration configLanguage = new Configuration();
        configLanguage.locale = locale;
        getBaseContext().getResources().updateConfiguration(configLanguage, getBaseContext().getResources().getDisplayMetrics());

        Backendless.initApp(this, BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);

        Backendless.Data.mapTableToClass("Kids", Kids.class);
        Backendless.Data.mapTableToClass("PhoneNumber", PhoneNumber.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }
}
