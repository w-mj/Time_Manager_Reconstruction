package wmj.InnerLayer.control;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import wmj.timemanager.MainActivity;

/**
 * Created by mj on 17-8-19.
 * 消息循环, 为InnerLayer各个模块提供消息服务
 */

public class MyHandler <T extends MyCallable> extends Handler {
    public static final int SHOW_FRAGMENT = 1;
    public static final int SHOW_TOAST_SHORT = 2;
    public static final int SHOW_TOAST_LONG = 3;
    public static final int CALL_BACK = 4;
    public static final int SHOW_ACTIVITY = 5;
    public static final int REFRESH_FRAGMENT = 6;
    public static final int CALL_BACK_DIRECTLY = 7;

    private MyCallback<T> callback;
    private WeakReference<MainActivity> mActivity;



    public MyHandler(MainActivity mainActivity) {
        mActivity = new WeakReference<MainActivity>(mainActivity);
        callback = new MyCallback<>();
    }

    public  void addCallbackInstance(String name, T obj) {
        callback.addInstance(name, obj);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity activity = mActivity.get();
        switch (msg.what) {
            case SHOW_FRAGMENT:
                Log.i("MainActivityHandler", "ShowFragment");
                if (msg.obj == null) {
                    Log.e("MainActivityHandler", "必须指定一个Fragment");
                    return;
                }
                activity.onFragmentInteraction("ShowFragment", (String)msg.obj);
                break;
            case SHOW_TOAST_SHORT:
                Log.i("MainActivityHandler", "Show toast short");
                Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case SHOW_TOAST_LONG:
                Log.i("MainActivityHandler", "Show toast long");
                Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_LONG).show();
                break;
            case CALL_BACK:
                callback.call(msg.getData().getString("name"), msg.getData().getInt("action"), msg.obj);
                break;
            case SHOW_ACTIVITY:
                activity.onFragmentInteraction("ShowActivity", (String)msg.obj);
                break;
            case REFRESH_FRAGMENT:
                activity.onFragmentInteraction("RefreshFragment", (String)msg.obj);
                break;
            default:
                Log.e("MainActivityHandler", "未知的命令" + msg.what);
        }
    }

    public MainActivity getActivity() {
        return mActivity.get();
    }
}
