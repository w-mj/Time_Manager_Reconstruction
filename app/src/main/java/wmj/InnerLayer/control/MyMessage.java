package wmj.InnerLayer.control;

import android.support.annotation.NonNull;

/**
 * Created by mj on 17-8-19.
 * 消息类
 */

public class MyMessage {
    public enum Todo {
        Quit_loop,
        Net_work_request_finish,
        Show_toast_long,
        Show_toast_short,
        Callback;
        public String toString() {
            return name();
        }
    }
    public Todo what;
    public String msg1;
    public String msg2;
    public Object obj;

    public MyMessage() {}

    public MyMessage(@NonNull Todo what, String msg) {
        this.what = what;
        this.msg1 = msg;
    }
}
