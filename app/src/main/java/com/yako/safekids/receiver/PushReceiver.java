package com.yako.safekids.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessBroadcastReceiver;
import com.safekids.android.R;
import com.yako.safekids.activity.NotificationAlert;
import com.yako.safekids.activity.RedAlert;

/**
 * Created by hp on 03/05/2016.
 */
public class PushReceiver extends BackendlessBroadcastReceiver {

    private SharedPreferences prefs;

    @Override
    public boolean onMessage(Context context, Intent intent) {

        prefs = context.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);

        CharSequence contentTitle;
        contentTitle = context.getResources().getString(R.string.app_name);
        CharSequence contentText = intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TEXT_TAG);
        try {
            if (prefs.getBoolean("NotifActif", true)) {

                int appIcon = R.mipmap.ic_launch;
                //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, null, PendingIntent.FLAG_CANCEL_CURRENT);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setSmallIcon(appIcon);
                notificationBuilder.setTicker(contentTitle);
                notificationBuilder.setWhen(System.currentTimeMillis());
                notificationBuilder.setContentTitle(contentTitle);//
                notificationBuilder.setContentText(contentText);
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setContentIntent(null);
                try {
                    //Vibration
                    notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

                    //LED
                    notificationBuilder.setLights(Color.RED, 3000, 3000);

                    //SOUND
                    notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                } catch (Exception e) {

                }

                Notification notification = notificationBuilder.build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification);

                Intent intentNotification = new Intent(context, NotificationAlert.class);
                intentNotification.putExtra("latitude",intent.getExtras().getString("latitude"));
                intentNotification.putExtra("longtitude",intent.getExtras().getString("longtitude"));
                intentNotification.putExtra("messageForAlerte",contentText);
                intentNotification.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentNotification);

            } else {
                //  Toast.makeText(context,"isnotactivated",Toast.LENGTH_SHORT).show();
                Log.e("pushhh", "oui");
            }
        } catch (Exception e) {
            //Toast.makeText(context,""+e,Toast.LENGTH_SHORT).show();
             Log.e("pushhh","oui"+e);
        }
        return false;
    }


}
