package wmj.InnerLayer.Item;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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

    private Date startTime;
    private Date endTime;
    private String details;
    private int every;
    private String place;
    private int item_id;
    private int time_id;
    private int startWeek;
    private int endWeek;

    public Date getStartTime() {return (Date)startTime.clone();}
    public Date getEndTime() {return (Date)endTime.clone();}
    public String getDetails() {return details;}
    public String getPlace() {return place;}
    public int getEvery() {return every;}
    public int getTimeId() {return time_id;}
    public int getItemId() {return item_id;}
    public int getStartWeek() {return startWeek;}
    public int getEndWeek() {return endWeek;}

    public void setDetails(String s) {details = s;}
    public void setEvery(int e) {every = e;}
    public void setPlace(String s) {place = s;}
    public void setStartTime(Date t) {startTime = t;}
    public void setEndTime(Date t) {endTime = t;}

    public Time(Date startTime, Date endTime, String details,  int every, String place, int item_id, int time_id) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.details = details;
        this.every = every;
        this.place = place;
        if (item_id == -1) {
            try {
                throw new Exception("item id 不能为 -1");
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
        this.item_id = item_id;
        if (time_id == -1) {
            Log.d("计算时间id start", MyTools.weekTimeFormatter().format(startTime));
            Log.d("计算时间id end", MyTools.weekTimeFormatter().format(endTime));
            this.time_id = (String.valueOf(time_id) +
                    MyTools.weekTimeFormatter().format(startTime) +
                    MyTools.weekTimeFormatter().format(endTime)).hashCode();
        } else {
            this.time_id = time_id;
        }
        // 计算一共有多少周;
        Calendar c = Calendar.getInstance();
        c.setTime(this.startTime);
        this.startWeek = c.get(Calendar.WEEK_OF_YEAR);
        c.setTime(this.endTime);
        this.endWeek = c.get(Calendar.WEEK_OF_YEAR) + 1;
    }

    public Time(String startTime, String endTime, String details, int every, String place, int item_id, int time_id) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date st = MyTools.dateTimeFormatter().parse(startTime);
            Date et = MyTools.dateTimeFormatter().parse(endTime);
            this.startTime = st;
            this.endTime = et;
            this.details = details;
            this.every = every;
            this.place = place;
            this.item_id = item_id;
            if (time_id == -1) {
                this.time_id = (String.valueOf(time_id) + MyTools.weekTimeFormatter().format(startTime) + MyTools.weekTimeFormatter().format(endTime)).hashCode();
            } else {
                this.time_id = time_id;
            }

            // 计算一共有多少周;
            Calendar c = Calendar.getInstance();
            c.setTime(this.startTime);
            this.startWeek = c.get(Calendar.WEEK_OF_YEAR);
            c.setTime(this.endTime);
            this.endWeek = c.get(Calendar.WEEK_OF_YEAR) + 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getJson() {
        // 生成Json对象
        return getJsonObject().toString();
    }

    public JSONObject getJsonObject() {
        JSONObject ret = new JSONObject();
        try {
            ret.put("start_time", MyTools.dateTimeFormatter().format(startTime));
            ret.put("end_time", MyTools.dateTimeFormatter().format(endTime));
            ret.put("every", every);
            ret.put("place", place);
            ret.put("id", time_id);
            ret.put("item_id", item_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
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

    public static Time parseFromJson(String json) {
        try {
            return parseFromJson(new JSONObject(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Time parseFromJson(JSONObject json) {
        try {
            return new Time(
                    json.getString("start_date"),
                    json.getString("end_date"),
                    json.getString("details"),
                    json.getInt("every"),
                    json.getString("place"),
                    json.getInt("item_id"),
                    json.getInt("time_id")
            );
        } catch (JSONException e) {
            MyTools.showToast("内部错误", false);
            e.printStackTrace();
        }
        return null;
    }

    public Item getItem() {
        return Configure.itemList.getItemById(item_id);
    }

    // 返回由第几周和星期几确定的一个时间
    // e.g. weekOfYear = 1, dayOfWeek = 0, 返回2017年1月1日
    public static Calendar getDateByWeek(int weekOfYear, int dayOfWeek) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return c;
    }

    public static boolean timeEqual(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY) &&
                c1.get(Calendar.MINUTE) == c2.get(Calendar.MINUTE);
    }
}
