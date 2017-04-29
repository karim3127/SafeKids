package com.yako.safekids.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.GeoPoint;
import com.safekids.android.R;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.model.Data;

public class GpsServices extends Service implements LocationListener, GpsStatus.Listener {
    private LocationManager mLocationManager;

    Location lastlocation = new Location("last");
    Data data;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    double currentLon=0 ;
    double currentLat=0 ;
    double lastLon = 0;
    double lastLat = 0;

    PendingIntent contentIntent;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;


    @Override
    public void onCreate() {

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            // make test
            // getting GPS status
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 1, 0, this);
            }else if (isGPSEnabled){
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60 * 1, 0, this);
            }

        }catch (Exception e){
            Log.e("nexmo", " " + e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }   
       
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }
   
    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {
        try {
            mLocationManager.removeUpdates(this);
            mLocationManager.removeGpsStatusListener(this);
        }catch (Exception e){

        }
        stopForeground(true);
    }



    @Override
    public void onGpsStatusChanged(int event) {}

    @Override
    public void onProviderDisabled(String provider) {}
   
    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onLocationChanged(Location location) {
        data = SpeedService.data;
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        if (data.isFirstTime()){
            lastLat = currentLat;
            lastLon = currentLon;
            data.setFirstTime(false);
        }

        lastlocation.setLatitude(lastLat);
        lastlocation.setLongitude(lastLon);
        double distance = lastlocation.distanceTo(location);

        if (location.getAccuracy() < distance){
            data.addDistance(distance);

            lastLat = currentLat;
            lastLon = currentLon;
        }

        if (location.hasSpeed()) {
            data.setCurSpeed(location.getSpeed() * 3.6);
            if(location.getSpeed() == 0){
                new isStillStopped().execute();
            }
        }
        data.setLatitude(location.getLatitude()+"");
        data.setLongtitude(location.getLongitude()+"");
        data.update();
        Log.e("nexmoo", "ouiouioui "+ (!prefs.getString("altitude", "").equals(location.getLatitude()+"") || !prefs.getString("longtitude", "").equals(location.getLongitude()+""))
                +prefs.getString("altitude", "")+" "+location.getLatitude()+"    "+prefs.getString("longtitude", "")+" "+location.getLongitude());


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    class isStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;
        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (data.getCurSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (InterruptedException t) {
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        @Override
        protected void onPostExecute(String message) {
            data.setTimeStopped(timer);
        }
    }
}
