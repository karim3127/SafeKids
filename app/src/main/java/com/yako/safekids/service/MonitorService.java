package com.yako.safekids.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.safekids.android.R;
import com.yako.safekids.activity.FirstAlert;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.activity.NormalAlert;
import com.yako.safekids.activity.RedAlert;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.model.BackendSetting;
import com.yako.safekids.model.PhoneNumber;
import com.yako.safekids.util.WeatherUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MonitorService extends Service {

    AlertModel alertModel = null;

    public static boolean initialized = false;
    static boolean startService = false;
    private final IBinder mBinder = new LocalBinder();
    private ServiceCallback callback = null;

    public static int SERVICE_PERIOD = 1000;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    CountDownTimer countDownTimer, countDownTimerMessageAlert, countDownTimerAlertNearBy;
    List<PhoneNumber> listPhones = new ArrayList<>();

    public static String temperature = "";
    public static String humidity = "";

    public boolean isAlertShow = false;

    private LocalBroadcastManager broadcaster;

    private String _apiKey = "229df376";//"147bc04c";
    private String _apiSecret = "3233b1ddecbfc4b9";//"03974e37dc071d36";
    private String API_CALL = "https://rest.nexmo.com/sms/json?api_key=%s&api_secret=%s&from=%s&to=%s&text=%s&type=unicode";


    public interface ServiceCallback {
        void sendResults(int resultCode, Bundle b);
    }

    public class LocalBinder extends Binder {
        MonitorService getService() {
            // Return this instance of the service so clients can call public methods
            return MonitorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialized = true;

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();

        String json = prefs.getString("Alert", "");
        alertModel = gson.fromJson(json, AlertModel.class);

        BackendSetting.alertmodel = alertModel;
        //editor.putString("Alertelatitude",alertModel.getLatitude()).apply();
        //editor.putString("Alertelongtitude",alertModel.getLongitude()).apply();

        broadcaster = LocalBroadcastManager.getInstance(this);

        getListPhones();

        if (alertModel != null) {
            if (!startService) {
                startService = true;
                start();
            }



            String latitude = alertModel.getLatitude() + "";
            String longitude = alertModel.getLongitude() + "";

            WeatherUtil.placeIdTask asyncTask =new WeatherUtil.placeIdTask(new WeatherUtil.AsyncResponse() {
                public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

    //                cityField.setText(weather_city);
    //                updatedField.setText(weather_updatedOn);
    //                detailsField.setText(weather_description);
    //                currentTemperatureField.setText(weather_temperature);
    //                humidity_field.setText("Humidity: "+weather_humidity);
    //                pressure_field.setText("Pressure: "+weather_pressure);
    //                weatherIcon.setText(Html.fromHtml(weather_iconText));

                    temperature = weather_temperature;
                    humidity = weather_humidity;

                    if(!TextUtils.isEmpty(temperature) && temperature != null){
                        editor.putString("temperature", temperature);
                        editor.apply();
                    }else{
                        try{
                            temperature = prefs.getString("temperature","");
                            if(temperature ==null)
                                temperature = "";
                        }catch (Exception e){temperature = "";}
                    }
                    if(!TextUtils.isEmpty(humidity) && humidity != null){
                        editor.putString("humidity", humidity);
                        editor.apply();
                    }else{
                        try{
                            humidity = prefs.getString("humidity","");
                            if(humidity ==null)
                                humidity = "";
                        }catch (Exception e){humidity = "";}
                    }
                }
            });
            asyncTask.execute(latitude, longitude); //  asyncTask.execute("25.180000", "89.530000")
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

        editor.remove("MesKidsStatus").commit();

        editor.remove("Alert").commit();

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (countDownTimerMessageAlert != null) {
            countDownTimerMessageAlert.cancel();
            countDownTimerMessageAlert = null;
        }

        if(countDownTimerAlertNearBy != null){
            countDownTimerAlertNearBy.cancel();
            countDownTimerAlertNearBy = null;
        }

        super.onDestroy();
    }

    public void setCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    public void start() {

        Date now = new Date();
        final Date alertDate = alertModel.getAlertDate();

        long diff = now.getTime() - alertDate.getTime();

        long timeRest = (alertModel.getExtraTime() * 1000 * 60) - diff;

        countDownTimer = new CountDownTimer(timeRest, SERVICE_PERIOD) {

            public void onTick(long millisUntilFinished) {

                long timeout = millisUntilFinished / 1000;

                if (timeout <= 30 && !isAlertShow) {
                    Intent intent = new Intent(MonitorService.this, FirstAlert.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    isAlertShow = true;
                }
            }

            public void onFinish() {

                Log.e("azerty", "azertyyyy");

                isAlertShow = false;

                NormalAlert.finishActivity();

                try {
                    countDownTimerMessageAlert.start();
                } catch (Exception ignored) {

                }

            }
        };

        countDownTimer.start();

        countDownTimerMessageAlert = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                //Toast.makeText(getApplicationContext(),"first",Toast.LENGTH_LONG).show();
                Log.e("azerty", "azertyyyymmmmmmm");

                for (int i = 0; i < listPhones.size(); i++) {

                    String phoneNo = "";
                    try {
                        phoneNo = listPhones.get(i).getCodeCountry() + listPhones.get(i).getNumero();
                    } catch (Exception ignored) {

                    }

                    String name = "";
                    if (alertModel.getListKids().size() > 1) {
                        name = ""+getResources().getString(R.string.your_child);
                    } else {
                        name = ""+getResources().getString(R.string.your_child_one);
                    }
                    try {
                        name+=" ";
                        for (int j = 0; j < alertModel.getListKids().size(); j++) {
                            name += alertModel.getListKids().get(j).getName() + ", ";
                        }
                    } catch (Exception ignored) {

                    }

                    String carColor = "";
                    try {
                        carColor = " " + prefs.getString("carColor", "") + " ";
                    } catch (Exception ignored) {

                    }

                    String carType = "";
                    try {
                        carType = " " + prefs.getString("carType", "") + " ";
                    } catch (Exception ignored) {

                    }

                    String carNum = "" ;
                    try {
                        carNum = " " + prefs.getString("carNumber", "") + "." + System.getProperty("line.separator");
                    } catch (Exception ignored) {

                    }

                    String location = "";
                    try {
                        location = ""+getResources().getString(R.string.to_plvae_sms) + " " + alertModel.getLocationName() + "." + System.getProperty("line.separator");
                    } catch (Exception ignored) {

                    }

                    String geo = "";
                    try {
                        geo = ""+getResources().getString(R.string.geolocation_sms) + " " + alertModel.getLatitude() + "*" + alertModel.getLongitude() + System.getProperty("line.separator");
                    } catch (Exception ignored) {

                    }

                    String temp = "";
                    if (!temperature.equals("")) {
                        try {
                            temp = ""+getResources().getString(R.string.sous_temerature_sms) + " " + temperature + "," + System.getProperty("line.separator");
                        } catch (Exception ignored) {

                        }
                    }

                    String message = name + ""+getResources().getString(R.string.risque_sms) + carColor + ""+getResources().getString(R.string.type_voiture_sms) + carType + ""+getResources().getString(R.string.cars_number_sms) + carNum + location + geo + temp + ""+
                            getResources().getString(R.string.please_see_him_sms)+System.getProperty("line.separator")+"#Safe Kids";
                    //message = message.replaceAll(" ", "%20");
                    String text = null;
                    try {
                        text = Html.fromHtml(new String(message.getBytes("UTF-8"))).toString();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sendTextMessage("Safe kids", phoneNo, message);//messageForAlerte

                    String messagealerte = getResources().getString(R.string.carmessage)+"\n"+
                            getResources().getString(R.string.car_type)+": "+carType+"\n"+
                            getResources().getString(R.string.cart_color)+": "+carColor+"\n"+
                            getResources().getString(R.string.car_number)+": "+carNum;
                    //String messageAlerte = getResources().getString(R.string.thereare_child_sms)+" "+getResources().getString(R.string.risque_sms) + carColor + ""+getResources().getString(R.string.type_voiture_sms) + carType + ""+getResources().getString(R.string.cars_number_sms) + carNum + location + geo + temp + ""+
                            //getResources().getString(R.string.please_see_him_sms)+System.getProperty("line.separator")+"#Safe Kids";
                    editor.putString("messageForAlerte",messagealerte).apply();
                    editor.putString("messageForAlerteType",carType).apply();
                    editor.putString("messageForAlerteColor",carColor).apply();
                    editor.putString("messageForAlerteNumber",carNum).apply();

                }

                try {
                    countDownTimerAlertNearBy.start();
                } catch (Exception ignored) {

                }
                /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editor.remove("MesKidsStatus").commit();
                        editor.remove("Alert").commit();

                        sendResult();
                        stopSelf();

                    }
                }, 1000);*/
            }
        };

        countDownTimerAlertNearBy = new CountDownTimer(60 * 1000 * 2, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                //Toast.makeText(getApplicationContext(),"seond",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MonitorService.this, RedAlert.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Log.e("azerty", "azertyyyynnnnnnnnnnnnn");

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editor.remove("MesKidsStatus").commit();
                        editor.remove("Alert").commit();

                        sendResult();
                        stopSelf();

                    }
                }, 1000);
            }
        };
    }

    public boolean sendTextMessage(final String from, final String to, final String msg) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String uri = Uri.parse("https://rest.nexmo.com/sms/json?")
                        .buildUpon()
                        .appendQueryParameter("api_key", _apiKey)
                        .appendQueryParameter("api_secret", _apiSecret)
                        .appendQueryParameter("from", from)
                        .appendQueryParameter("to", to)
                        .appendQueryParameter("text", msg)
                        .appendQueryParameter("type", "unicode")
                        .build().toString();

                HttpPost httppost;
                HttpClient http = new DefaultHttpClient();

                httppost = new HttpPost(uri);

                try {
                    HttpResponse reponse = http.execute(httppost);

                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        }).start();
        return false;
    }

    private void getListPhones() {
        listPhones.clear();
        String jsonPhones = prefs.getString("MesPhones", null);
        if (jsonPhones != null) {
            PhoneNumber[] phoneItems = gson.fromJson(jsonPhones, PhoneNumber[].class);
            listPhones = Arrays.asList(phoneItems);
            listPhones = new ArrayList(listPhones);
        }
    }

    public void sendResult() {
        Intent intent = new Intent(MainActivity.COPA_RESULT);
        broadcaster.sendBroadcast(intent);
    }

}