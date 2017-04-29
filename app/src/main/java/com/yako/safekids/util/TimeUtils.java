package com.yako.safekids.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by igorkhomenko on 1/13/15.
 */
public class TimeUtils {
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    static SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private TimeUtils() {
    }

    /**
     *converts time (in milliseconds) to human-readable format
     *"<w> days, <x> hours, <y> minutes and (z) seconds"
     * @return String
     */
    public static String millisToLongDHMS( long durationfirst) {
        long duration = durationfirst;
        if(duration > 0) {
            duration = new Date().getTime() - duration;
        }
        if(duration < 0){
            duration = 0;
        }

        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= (ONE_SECOND * SECONDS)) {
            res.append(" منذ ");
            temp = duration / ONE_DAY;
            if(temp > 7){
                return ""+sdf2.format(durationfirst);
            }else if(temp > 2){
                return " منذ "+temp+" ايام ";
            } else if(temp > 1){
                return "منذ يومين";
            }else if (temp > 0 ) {
                return "منذ يوم";
            }else{
                temp = duration / ONE_HOUR;
                if (temp > 1) {
                    if(temp > 3) {
                        res.append(temp).append(" ساعة").append(temp > 1 ? "" : "");
                    }else if( temp > 2)
                        return "منذ ساعتين";
                    else
                        return "منذ ساعة";
                }else{
                    temp = duration / ONE_MINUTE;
                    if (temp > 0) {
                        // duration -= temp * ONE_MINUTE;
                        res.append(temp).append(" دقيقة").append(temp > 1 ? "" : "");
                    }else{
                        return "حاليا";
                    }
                }
            }

            //res.append(" ago");
            return res.toString();
        } else {
            return "حاليا";
        }
    }

    /**
     *
     * @return String
     */
    public static String dateToString(long durationfirst) {
        return ""+sdf2.format(durationfirst);
    }

}
