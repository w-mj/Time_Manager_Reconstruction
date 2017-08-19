package wmj.InnerLayer.control;

import java.util.HashMap;

import wmj.InnerLayer.control.MyCallable;

/**
 * Created by mj on 17-8-19.
 * 用于回调内部类的类
 */

class MyCallback <T extends MyCallable>{
    private HashMap<String, T> instances = new HashMap<>();

    void addInstance(String name, T obj) {
        instances.put(name, obj);
    }

    void call(String instanceName, String cmd, Object parm) {
        instances.get(instanceName).listener(cmd, parm);
    }
}
