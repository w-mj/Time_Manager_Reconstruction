package wmj.InnerLayer.control;

/**
 * Created by mj on 17-8-19.
 * 消息类
 */

public class MyMessage {
    public enum Todo {
        Quit_loop,
        Net_work_request_finish,
        Callback
    }
    public Todo what;
    public String msg1;
    public String msg2;
    public Object obj;

    public MyMessage() {}

    public MyMessage(Todo what, String msg) {
        this.what = what;
        this.msg1 = msg;
    }
}
