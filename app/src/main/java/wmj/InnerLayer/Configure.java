package wmj.InnerLayer;

import java.util.Calendar;

import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.control.MyHandler;

/**
 * Created by mj on 17-8-19.
 * 公开的全局变量类
 */

public class Configure {
    public static String url = "http://192.168.56.1:8000";
    public static MyHandler handler = null;
    public static ItemList itemList = null;
    public static User user = null;
    public static Calendar today;
    public static int DefaultCalenderView;
    public static int Current_week = 4;
    public static int start_hour = 7, start_minute = 30;
    public static int start_class_hour = 8, start_class_minute = 30;
    public static Calendar enrollDate;
}
