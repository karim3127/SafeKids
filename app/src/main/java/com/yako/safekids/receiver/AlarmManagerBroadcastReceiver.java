package com.yako.safekids.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yako.safekids.activity.Alarme;
import com.yako.safekids.model.KidsPlaningModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    private SharedPreferences prefs;
    Gson gson = new Gson();
    private List<KidsPlaningModel> allPlaning = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        prefs = context.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);

        if (prefs.getBoolean("AlarmeActif", true)) {
            getListPlaning();

            KidsPlaningModel kids = null;
            for (int i = 0; i < allPlaning.size(); i++) {
                int heure = allPlaning.get(i).getHoure();
                int minute = allPlaning.get(i).getMinute();
                int timeMode = (allPlaning.get(i).getTimeMode().equals("pm")) ? 1 : 0;
                List<Integer> days = allPlaning.get(i).getDays();
                KidsPlaningModel currentKid = allPlaning.get(i);

                Calendar cal = Calendar.getInstance();

                int minutes = cal.get(Calendar.MINUTE);
                int hours = cal.get(Calendar.HOUR);
                int AM_orPM = cal.get(Calendar.AM_PM);

                boolean isThisDay = false;
                for (int j = 0; j < days.size(); j++) {
                    if (days.get(j) == cal.get(Calendar.DAY_OF_WEEK)) {
                        isThisDay = true;
                    }
                }

                if (isThisDay) {
                    if (timeMode == AM_orPM && heure == hours && minute == minutes) {
                        kids = currentKid;
                    }
                }
            }

            if (kids != null) {
                Alarme.start(context, kids);
            }
        }
    }

    private void getListPlaning() {
        allPlaning.clear();

        String json = prefs.getString("KidsPlaning", null);
        if (json != null) {
            KidsPlaningModel[] items = gson.fromJson(json, KidsPlaningModel[].class);
            allPlaning = Arrays.asList(items);
            allPlaning = new ArrayList(allPlaning);
        }
    }

}