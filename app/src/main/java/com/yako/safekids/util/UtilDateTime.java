package com.yako.safekids.util;

import android.content.Context;

import com.safekids.android.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilDateTime {
    private static final int TIME_HOURS_24 = 24 * 60 * 60 * 1000;
    private static final SimpleDateFormat DAY_OF_WEEK = new SimpleDateFormat("EEE, dd LLL", Locale.ENGLISH);


    public static String formatTime(Context context, Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        final SimpleDateFormat format = new SimpleDateFormat("dd MMMM", Locale.ENGLISH);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeText;
        if (date.getTime() > todayMidnight) {
            timeText = timeFormat.format(date.getTime());
        } else if (date.getTime() > yesterMidnight) {
            timeText = context.getString(R.string.time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeText = context.getResources().getStringArray(R.array.time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else if (date.getYear() == cal.getTime().getYear()) {
            timeText = format.format(date);
        } else {
            timeText = dateFormat.format(date);
        }
        return timeText;
    }

    public static String formatChatStatus(Context context, Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        final SimpleDateFormat format = new SimpleDateFormat("dd MMMM", Locale.ENGLISH);

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeText;
        if (date.getMinutes() > currentDate.getMinutes() - 3) {
            timeText = "دقيقتين";
        } else if (date.getMinutes() > currentDate.getMinutes() - 30) {
            timeText = "نصف ساعة";
        } else if (date.getMinutes() > currentDate.getMinutes() - 60) {
            timeText = "ساعة";
        } else if (date.getTime() > todayMidnight) {
            timeText = timeFormat.format(date.getTime());
        } else if (date.getTime() > yesterMidnight) {
            timeText = context.getString(R.string.time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeText = context.getResources().getStringArray(R.array.time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else if (date.getYear() == cal.getTime().getYear()) {
            timeText = format.format(date);
        } else {
            timeText = dateFormat.format(date);
        }
        return timeText;
    }

    public static String formatTimeDay(Context context, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long todayMidnight = cal.getTimeInMillis();
        long yesterdayMidnight = todayMidnight - TIME_HOURS_24;
        long weekAgoMidnight = todayMidnight - TIME_HOURS_24 * 7;

        String timeBarDayText;
        if (date.getTime() > todayMidnight) {
            timeBarDayText = context.getString(R.string.time_today);
        } else if (date.getTime() > yesterdayMidnight) {
            timeBarDayText = context.getString(R.string.time_yesterday);
        } else if (date.getTime() > weekAgoMidnight) {
            cal.setTime(date);
            timeBarDayText = context.getResources().getStringArray(R.array.time_days_of_week)[cal.get(Calendar.DAY_OF_WEEK) - 1];
        } else {
            timeBarDayText = DAY_OF_WEEK.format(date);
        }
        return timeBarDayText;
    }

}