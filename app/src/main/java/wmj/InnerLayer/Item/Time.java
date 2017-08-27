package wmj.InnerLayer.Item;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;

/**
 * Created by mj on 17-6-14.
 * 每个项目的时间
 */

public class Time implements Comparable<Time>, Cloneable {
    static public final int SUNDAY = 1;
    static public final int MONDAY = 2;
    static public final int TUESDAY = 4;
    static public final int WEDNESDAY = 8;
    static public final int THURSDAY = 16;
    static public final int FRIDAY = 32;
    static public final int SATURDAY = 64;

    public Date startTime;
    public Date endTime;
    public String details;
    public int every;
    public String place;
    public int item_id;
    public int time_id;
    public int startWeek;
    public int endWeek;


    Time(Date startTime, Date endTime, String details, int every, String place, int item_id, int time_id) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.every = every;
        this.place = place;
        this.item_id = item_id;
        this.time_id = time_id;
    }

    Time(String startTime, String endTime, String details, int every, String place, int item_id, int time_id)
            throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        this.startTime = df.parse(startTime);
        this.endTime = df.parse(endTime);
        this.details = details;
        this.every = every;
        this.place = place;
        this.item_id = item_id;
        this.time_id = time_id;

        // 计算一共有多少周;
        Calendar c = Calendar.getInstance();
        c.setTime(this.startTime);
        this.startWeek = c.get(Calendar.WEEK_OF_YEAR);
        c.setTime(this.endTime);
        this.endWeek = c.get(Calendar.WEEK_OF_YEAR) + 1;
    }

    String getJson() {
        // 生成Json对象
        return "{" + "\"start_time\": \"" + MyTools.dateTimeFormatter.format(startTime) +
                "\", \"end_time\": \"" + MyTools.dateTimeFormatter.format(endTime) +
                "\", \"details\": \"" + details +
                "\", \"every\": " + every +
                ", \"place\": \"" + place +
                "\", \"id\":" + time_id + "}";
    }

    @Override
    public int compareTo(@NonNull Time t) {
        if (item_id == t.item_id) {
            return startTime.compareTo(t.startTime);
        }
        else if (item_id < t.item_id)
            return -1;
        else
            return 1;
    }

    /**
     * returns time2 - time1, in minutes. if time2 is before than time1, return 0;
     * @param time1 time1
     * @param time2 time2
     * @return long
     */
    public static long minusTime(Date time1, Date time2) {

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);
        // Log.i("Time", "time1:  " + df.format(time1) +"  time2:  " + df.format(time2));
        String[] t1 = df.format(time1).split(":");
        String[] t2 = df.format(time2).split(":");
        return (Integer.valueOf(t2[0]) - Integer.valueOf(t1[0]))* 60 +
                (Integer.valueOf(t2[1]) - Integer.valueOf(t1[1]));
    }

    public static long minusTime(Date time2) {
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String[] t2 = df.format(time2).split(":");
        return (Integer.valueOf(t2[0]) - Configure.start_hour)* 60 +
                (Integer.valueOf(t2[1]) - Configure.start_minute);
    }

    @Override
    public Time clone() {
        try {
            return (Time) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
