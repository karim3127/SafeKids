package com.yako.safekids.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.geo.BackendlessGeoQuery;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.yako.safekids.activity.AlertSpeed;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.activity.NormalAlert;
import com.yako.safekids.activity.RequestAlert;
import com.yako.safekids.model.Data;

import java.util.Calendar;

public class SpeedService extends Service {

    //  private LocationManager mLocationManager;
    public static Data data;
    public static Context context = null;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
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

                    if (!prefs.getString("altitude", "").equals(data.getLatitude() + "") || !prefs.getString("longtitude", "").equals(data.getLongtitude() + "")) {
                        UpdateLocationUser(data, Double.parseDouble(prefs.getString("altitude", "")), Double.parseDouble(prefs.getString("longtitude", "")));
                        prefs.edit().putString("altitude", data.getLatitude() + "").apply();
                        prefs.edit().putString("longtitude", data.getLongtitude() + "").apply();

                    }
                }
            }
        };
        data = new Data(onGpsServiceUpdate);
        startService(new Intent(context, GpsServices.class));
    }

    private void UpdateLocationUser(final Data location, final Double latitude, final Double longtitude) {

        String currentUserId = prefs.getString("UserId", "");
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause("objectId = '" + currentUserId + "'");
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated("locationn");
        dataQuery.setQueryOptions(queryOptions);

        Backendless.Data.of(BackendlessUser.class).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {

                final BackendlessUser backendlessUser = backendlessUserBackendlessCollection.getCurrentPage().get(0);

                if (backendlessUser != null) {
                    Log.e("nexmo", backendlessUser + "");
                    GeoPoint geoPoint = null;
                    try {
                        geoPoint = (GeoPoint) backendlessUser.getProperty("locationn");

                    } catch (Exception ignored) {
                    }
                    Log.e("55555", geoPoint + "   ");
                    if (geoPoint != null) {

                        if (geoPoint.getLatitude() != Double.parseDouble(location.getLatitude()) || geoPoint.getLongitude() != Double.parseDouble(location.getLongtitude())) {

                            Backendless.Geo.removePoint(geoPoint, new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void response) {
                                    // GeoPoint has been deleted
                                    saveData(backendlessUser, location);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    // an error has occurred, the error code can be retrieved with fault.getCode()
                                }
                            });
                        }
                    } else {

                        //geoPoint = new GeoPoint(latitude, longtitude);
                        BackendlessGeoQuery geoQuery = new BackendlessGeoQuery();
                        geoQuery.setLatitude(latitude);
                        geoQuery.setLongitude(longtitude);
                        Backendless.Geo.getPoints(geoQuery, new AsyncCallback<BackendlessCollection<GeoPoint>>() {
                            @Override
                            public void handleResponse(BackendlessCollection<GeoPoint> geoPointBackendlessCollection) {

                                if (geoPointBackendlessCollection.getData() != null) {
                                    if (geoPointBackendlessCollection.getData().size() > 0) {
                                        GeoPoint geoPoint = geoPointBackendlessCollection.getData().get(0);
                                        Backendless.Geo.removePoint(geoPoint, new AsyncCallback<Void>() {
                                            @Override
                                            public void handleResponse(Void response) {
                                                // GeoPoint has been deleted
                                                saveData(backendlessUser, location);
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                // an error has occurred, the error code can be retrieved with fault.getCode()
                                                Log.e("nexmo", fault + " " + latitude + "  " + longtitude);
                                                saveData(backendlessUser, location);
                                            }
                                        });
                                    } else {
                                        saveData(backendlessUser, location);
                                    }
                                } else {
                                    saveData(backendlessUser, location);
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                saveData(backendlessUser, location);
                            }
                        });

                    }


                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    private void saveData(BackendlessUser backendlessUser, Data location) {

        backendlessUser.setProperty("locationn", new GeoPoint(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongtitude())));
        Backendless.UserService.update(backendlessUser, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser user) {
                // user has been updated
                Log.e("nexmo", user + "  55555");
            }

            public void handleFault(BackendlessFault fault) {
                // user update failed, to get the error code call fault.getCode()
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
