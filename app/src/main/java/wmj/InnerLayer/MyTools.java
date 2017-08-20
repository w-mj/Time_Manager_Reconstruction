package wmj.InnerLayer;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.control.MyHandler;
import wmj.InnerLayer.control.MyMessage;

/**
 * Created by mj on 17-8-19.
 * 通用工具类
 */

public class MyTools {
    public static DateFormat dateFormatter;
    public static DateFormat timeFormatter;
    public static DateFormat dateTimeFormatter;

    static public void initInnerLayer() {
        MyHandler handler = new MyHandler();
        ItemList items = new ItemList();
        User user = new User(-1); // TODO: 读取设置后取得保存的User
        // 添加回调实例
        handler.addCallbackInstance("ItemList", items);
        handler.addCallbackInstance("User", user);

        handler.start();
        Configure.itemList = items;
        Configure.handler = handler;
        Configure.user = user;

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        timeFormatter = new SimpleDateFormat("HH-mm-dd", Locale.CHINA);
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-dd", Locale.CHINA);
    }

    public static void showToast(String message, boolean isShort) {
        // TODO: 用原生Handler代替
        MyMessage msg = new MyMessage();
        if(isShort)
            msg.what = MyMessage.Todo.Show_toast_short;
        else
            msg.what = MyMessage.Todo.Show_toast_long;
        msg.obj = message;
        Configure.handler.addMsg(msg);
    }
}
