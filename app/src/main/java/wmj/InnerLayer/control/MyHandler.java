package wmj.InnerLayer.control;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by mj on 17-8-19.
 * 消息循环, 为InnerLayer各个模块提供消息服务
 */

public class MyHandler extends Thread{
    private LinkedList<MyMessage> queue;
    private MyCallback callback;

    public MyHandler() {
        queue = new LinkedList<>();
        callback = new MyCallback();
    }

    public <T extends MyCallable> void addCallbackInstance(String name, T obj) {
        callback.addInstance(name, obj);
    }

    @Override
    public void run() {
        MyMessage msg;
        boolean quit = false;
        while(!quit) {
            try {
                msg = queue.removeFirst();  // 相当于pop
            } catch (NoSuchElementException e) {
                continue;
            }
            switch(msg.what) {
                case Quit_loop:
                    quit = true;
                    break;
                case Callback:
                    callback.call(msg.msg1, msg.msg2, msg.obj);
                    break;
            }
        }
    }

    public void addMsg(MyMessage msg) {
        queue.add(msg);
    }
}
