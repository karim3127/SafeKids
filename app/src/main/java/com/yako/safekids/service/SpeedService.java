package com.yako.safekids.service;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.yako.safekids.activity.AlertSpeed;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.activity.NormalAlert;
import com.yako.safekids.activity.RequestAlert;
import com.yako.safekids.model.Data;

import java.util.Calendar;

public class SpeedService extends Service {

  //  private LocationManager mLocationManager;
    public static Data data;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
    public static Context context = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // flag for GPS status
    //boolean isGPSEnabled = false;

    // flag for network status
    //boolean isNetworkEnabled = false;

    public SpeedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        context = this;

        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {

                if (prefs.getBoolean("SpeedActif", true) && prefs.getString("Login", "").equals("connecte")) {
                    int maxSpeed = prefs.getInt("SpeedMax", 130);
                    if (data.getCurSpeed() > maxSpeed) {
                        if (MainActivity.isOpen) {
                            MainActivity.showSpeedAlert();

                        } else if (AlertSpeed.context == null) {
                            long nextDateSpeed = prefs.getLong("NextDateSpeed", 0);
                            Calendar calendar = Calendar.getInstance();

                            if (calendar.getTime().getTime() > nextDateSpeed) {
                                Intent intent = new Intent(SpeedService.this, AlertSpeed.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                NormalAlert.finishActivity();
                            }

                        }
                    } else {
                        if (MainActivity.isOpen) {
                            MainActivity.deleteSpeedAlert();
                        }
                        AlertSpeed.finishActivity();
                    }

                    if (AlertSpeed.tv_speed != null) {
                        AlertSpeed.tv_speed.setText(String.format("%.0f", SpeedService.data.getCurSpeed()));
                    }

                    if (data.getCurSpeed() > 30) {
                        Calendar calendar = Calendar.getInstance();
                        int min = calendar.get(Calendar.MINUTE);
                        calendar.set(Calendar.MINUTE, min + 3);

                        editor.putLong("LastDateSpeed", calendar.getTime().getTime()).apply();

                    } else if (data.getCurSpeed() < 20 && data.getCurSpeed() >= 0) {
                        long lastDateSpeed = prefs.getLong("LastDateSpeed", 0);
                        Calendar calendar = Calendar.getInstance();

                        if (Math.abs(calendar.getTime().getTime() - lastDateSpeed) < 2000 && RequestAlert.context == null && !MainActivity.isOpen) {
                            Intent intent = new Intent(SpeedService.this, RequestAlert.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                   // Toast.makeText(context, data.getLatitude()+" "+data.getLongtitude(),Toast.LENGTH_SHORT).show();

                    if(!prefs.getString("altitude", "").equals(data.getLatitude()+"") || !prefs.getString("longtitude", "").equals(data.getLongtitude()+"") ){
                        prefs.edit().putString("altitude", data.getLatitude()+"").apply();
                        prefs.edit().putString("longtitude", data.getLongtitude()+"").apply();
                        UpdateLocationUser(data);
                    }
                }
            }
        };
        data = new Data(onGpsServiceUpdate);
        startService(new Intent(context, GpsServices.class));
    }

    private void UpdateLocationUser(final Data location) {

        String currentUserId = prefs.getString("UserId", "");
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(final BackendlessUser backendlessUser) {

                if(backendlessUser != null){
                    Log.e("nexmo", backendlessUser+"");
                    backendlessUser.setProperty("locationn", new GeoPoint(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongtitude())));
                    Backendless.UserService.update(backendlessUser, new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(BackendlessUser user) {
                            // user has been updated
                            Log.e("nexmo", user+"  55555");
                        }

                        public void handleFault(BackendlessFault fault) {
                            // user update failed, to get the error code call fault.getCode()
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });
    }

    @Override
    public void onDestroy() {
        context = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
