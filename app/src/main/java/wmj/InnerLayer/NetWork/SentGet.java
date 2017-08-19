package wmj.InnerLayer.NetWork;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.control.MyMessage;

/**
 * Created by mj on 17-8-19.
 * 发送Get请求
 */

public class SentGet implements Callable{
    private MyMessage msg;
    private String url;
    public HashMap<String, String> data;


    private String makeData() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry x : data.entrySet()) {
            sb.append(x.getKey()).append("=").append(x.getValue());
        }
        return sb.toString();
    }

    SentGet(String url, MyMessage msg) {
        this.msg = msg;
        data = new HashMap<>();
        if (url == null) {
            this.url = Configure.url;
        }
    }

    public String call() throws IOException {
        try {
            String data = makeData();
            URL url = new URL(this.url + "?" + data);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);

            Log.i("GET开始", "url:" + url.toString());

            int responseCode = con.getResponseCode();
            Log.i("GET状态码", String.valueOf(responseCode));

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = in.readLine()) != null) {
                response.append(line);
            }
            String result = response.toString();
            Log.i("GET响应", result);

            if(msg != null) {
                Log.i("GET添加消息至队列", msg.what.name()+ " " + msg.msg1 + " " + msg.msg2);
                msg.obj = result;
                Configure.handler.addMsg(msg);
            }
            return result;

        } catch (IOException e) {
            Log.e("SendGET", e.getMessage());
            throw e;
        }

    }
}
