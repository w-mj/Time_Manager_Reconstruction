package wmj.InnerLayer;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.control.MyHandler;
import wmj.timemanager.MainActivity;

/**
 * Created by mj on 17-8-19.
 * 通用工具类
 */

public class MyTools {
    public static DateFormat dateFormatter;
    public static DateFormat timeFormatter;
    public static DateFormat dateTimeFormatter;

    static public void initialization(MainActivity mainActivity) throws Exception {
        MyHandler handler = new MyHandler(mainActivity);
        ItemList items = new ItemList();
        User user = new User(-1); // TODO: 读取设置后取得保存的User
        // 添加回调实例
        handler.addCallbackInstance("ItemList", items);
        handler.addCallbackInstance("User", user);

        Configure.itemList = items;
        Configure.handler = handler;
        Configure.user = user;
        Configure.today = Calendar.getInstance();
        Configure.DefaultCalenderView = 1;  // 只能取1和2
        if (Configure.DefaultCalenderView != 1 && Configure.DefaultCalenderView != 2)
            throw new Exception("CalenderView只能取1或2");

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        timeFormatter = new SimpleDateFormat("HH-mm-dd", Locale.CHINA);
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-dd", Locale.CHINA);

    }

    public static void showToast(String message, boolean isShort) {
        Message msg = new Message();
        if(isShort)
            msg.what = MyHandler.SHOW_TOAST_SHORT;
        else
            msg.what = MyHandler.SHOW_TOAST_LONG;
        msg.obj = message;
        Configure.handler.sendMessage(msg);
    }

    private static String[] cnList = {"零","一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        // Log.i("屏幕密度", String.valueOf(scale));
        return (int)(dipValue * scale + 0.5f);
    }

    public static String num2cn(int num) {
        if (num < 10) {
            return cnList[num];
        } else if (num < 20) {
            return cnList[10] + cnList[num - 10];
        } else {
            return cnList[num / 10] + cnList[10] + cnList[num % 10];
        }
    }

    public static Message callbackMessage(String name, int action) {
        Message msg = new Message();
        msg.what = MyHandler.CALL_BACK;
        Bundle data = new Bundle();
        data.putString("name", name);
        data.putInt("action", action);
        msg.setData(data);
        return msg;
    }

    public static Message showFragmentMessage(String name) {
        Message msg = new Message();
        msg.what = MyHandler.SHOW_FRAGMENT;
        msg.obj = name;
        return msg;
    }
}
