package wmj.InnerLayer;

import wmj.InnerLayer.control.MyHandler;

/**
 * Created by mj on 17-8-19.
 * 通用工具类
 */

public class MyTools {
    static public void initInnerLayer() {
        MyHandler handler = new MyHandler();
        // handler.addCallbackInstance();
        handler.start();
        Configure.handler = handler;
    }
}
