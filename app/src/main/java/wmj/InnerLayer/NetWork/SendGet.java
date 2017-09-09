package wmj.InnerLayer.NetWork;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import wmj.InnerLayer.Configure;

/**
 * Created by mj on 17-8-19.
 * 发送Get请求
 */

public class SendGet implements Callable<String>{
    private Message msg;
    private String url;
    public HashMap<String, String> data;
    public HashMap<String, String> requestProperty;


    private String makeData() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry x : data.entrySet()) {
            sb.append(x.getKey()).append("=").append(x.getValue());
        }
        return sb.toString();
    }

    public SendGet(String url, Message msg) {
        this.msg = msg;
        data = new HashMap<>();
        requestProperty = new HashMap<>();
        this.url = Configure.url + '/' + url;
    }

    public String call() throws IOException {
        try {
            String data = makeData();
            URL url;
            if (data.isEmpty())
                url = new URL(this.url);
            else
                url = new URL(this.url + "?" + data);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            for (String k : requestProperty.keySet())
                con.setRequestProperty(k, requestProperty.get(k));

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
                Log.i("GET", "添加消息至队列");
                msg.obj = con;
                Configure.handler.sendMessage(msg);
            }
            return result;

        } catch (IOException e) {
            Log.e("SendGET", e.getMessage());
            throw e;
        }

    }
}
