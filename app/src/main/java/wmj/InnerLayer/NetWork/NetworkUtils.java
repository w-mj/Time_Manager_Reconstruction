package wmj.InnerLayer.NetWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by mj on 17-9-8.
 * 网络相关的工具类
 */

public class NetworkUtils {
    public static String getContent(HttpURLConnection con) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String s;
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
