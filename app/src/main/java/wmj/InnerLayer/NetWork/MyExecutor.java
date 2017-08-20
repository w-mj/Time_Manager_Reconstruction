package wmj.InnerLayer.NetWork;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by mj on 17-8-20.
 * 带有超时的Executor
 */

public class MyExecutor <T extends Callable>{
    private T task;

    public MyExecutor(T callable) {
        this.task = callable;
    }

    public void start(int milliseconds) throws TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Log.e("监视进程", "被打断");
        }
    }
}
