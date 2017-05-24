package com.yako.safekids.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

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
import com.yako.safekids.activity.RedAlert;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.model.BackendSetting;

/**
 * Created by hp on 24/04/2017.
 */
public class NotiNearbyService extends Service {

    public static boolean initialized = false;
    static boolean startService = false;
    private final IBinder mBinder = new LocalBinder();

    String toSend = "";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    CountDownTimer countDownTimerMessageAlert;

    public boolean isAlertShow = false;

    public class LocalBinder extends Binder {

        NotiNearbyService getService() {
            // Return this instance of the service so clients can call public methods
            return NotiNearbyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialized = true;

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        toSend = prefs.getString("tosendpush", "oui");


        if ( toSend.equals("oui")) {

            BackendSetting.alertmodel = new AlertModel();
            if (!startService) {
                BackendSetting.alertmodel.setLatitude(Double.parseDouble(prefs.getString("altitude", "")));
                BackendSetting.alertmodel.setLongitude(Double.parseDouble(prefs.getString("longtitude", "")));
                startService = true;
                start();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (initialized) {
            return mBinder;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        initialized = false;
        startService = false;
        isAlertShow = false;

        if (countDownTimerMessageAlert != null) {
            countDownTimerMessageAlert.cancel();
            countDownTimerMessageAlert = null;
        }

        super.onDestroy();
    }

    public void start() {

        countDownTimerMessageAlert = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                Log.e("ici", ((BackendSetting.alertmodel.getLatitude() != 0) && (BackendSetting.alertmodel.getLongitude() != 0) && toSend.equals("oui"))+"  "+
                        (BackendSetting.alertmodel.getLatitude() != 0));

                if((BackendSetting.alertmodel.getLatitude() != 0) && (BackendSetting.alertmodel.getLongitude() != 0) && toSend.equals("oui"))
                    sendNotification();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editor.putString("tosendpush","non").commit();
                        try {
                            RedAlert.activity.finish();
                        }catch (Exception ignored){}
                        try {
                            countDownTimerMessageAlert = null;
                        }catch (Exception ignored){}
                        stopSelf();

                    }
                }, 1000);
            }
        };

        countDownTimerMessageAlert.start();
    }


    private void sendNotification() {

        double myLatitude = BackendSetting.alertmodel.getLatitude();
        double myLongitude = BackendSetting.alertmodel.getLongitude();
        String query = "distance( "+myLatitude+","+ myLongitude+", locationn.latitude, locationn.longitude ) < mi(0.5)";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery( query );
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth( 1 );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Data.of( BackendlessUser.class ).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> cars) {

                if (cars.getCurrentPage().size() == 0)
                    Log.e("ici","Did not find any user");

                for (BackendlessUser user : cars.getCurrentPage()) {
                    Log.e("ici",String.format("Found car: %s %s", user.getProperty("codeCountry"), user.getProperty("phoneNumber")));
                    if (!user.getObjectId().equals(prefs.getString("UserId", null))) {
                        String textMessage = prefs.getString("messageForAlerte", "");
                        sendNotiftoUser(user, textMessage);
                    }

                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e("ici","Server reported an error - " + backendlessFault.getMessage());
            }
        });

    }

    private void sendNotiftoUser(BackendlessUser user, String textMessage) {

        String DeviceToken = (String) user.getProperty("deviceRegistration");
        if (!TextUtils.isEmpty(DeviceToken)) {
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions.addPushSinglecast(DeviceToken);
            PublishOptions publishOptions = new PublishOptions();
            publishOptions.putHeader("android-ticker-text", getResources().getString(R.string.app_name));
            publishOptions.putHeader("android-content-title", getResources().getString(R.string.app_name));
            publishOptions.putHeader("android-content-text", textMessage);
            publishOptions.putHeader("ios-alert", textMessage);
            publishOptions.putHeader("ios-badge", "1");
            publishOptions.putHeader("TypeCar", prefs.getString("messageForAlerteType", ""));
            publishOptions.putHeader("ColorCar", prefs.getString("messageForAlerteColor", ""));
            publishOptions.putHeader("NumberCar", prefs.getString("messageForAlerteNumber", ""));
            publishOptions.putHeader("latitude", BackendSetting.alertmodel.getLatitude()+"");
            publishOptions.putHeader("longtitude", BackendSetting.alertmodel.getLongitude()+"");
            publishOptions.putHeader("ios-sound", "default");
            Backendless.Messaging.publish("", publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus messageStatus) {
                    Log.e("ici",""+messageStatus);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Log.e("ici",""+backendlessFault);
                }
            });
        }else{
            Log.e("ici",DeviceToken);
        }

    }
}