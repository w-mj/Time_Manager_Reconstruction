package wmj.InnerLayer.NetWork;

import android.os.Message;
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

/**
 * Created by mj on 17-8-19.
 * 发送POST请求
 */

public class SendPost implements Callable<String>{
    private Message msg;
    private String url;
    public HashMap<String, String> data;


    private String makeData() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry x : data.entrySet()) {
            sb.append(x.getKey()).append("=").append(x.getValue());
        }
        return sb.toString();
    }

    public SendPost(String url, Message msg) {
        this.msg = msg;
        data = new HashMap<>();
        this.url = Configure.url + '/' + url;
    }

    public String call() throws IOException {
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            Log.i("POST开始", "url:" + url.toString());

            OutputStream o = con.getOutputStream();
            String data = makeData();
            o.write(data.getBytes("UTF-8"));
            o.flush();
            o.close();
            Log.i("POST发送数据", data);

            int responseCode = con.getResponseCode();
            Log.i("POST状态码", String.valueOf(responseCode));

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = in.readLine()) != null) {
                response.append(line);
            }
            String result = response.toString();
            Log.i("POST响应", result);

            if(msg != null) {
                Log.i("POST", "添加消息至队列");
                msg.obj = result;
                Configure.handler.sendMessage(msg);
            }
            return result;

        } catch (IOException e) {
            Log.e("SendPost", e.getMessage());
            throw e;
        }

    }
}
