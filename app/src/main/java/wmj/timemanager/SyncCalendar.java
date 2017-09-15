package wmj.timemanager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Pattern;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Item;
import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SyncCalendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SyncCalendar extends Fragment{
    private View v;
    private GetCaptcha gc;

    private EditText userId_edit;
    private EditText password_edit;
    private EditText captcha_edit;
    private ImageView captcha_pic;

    private final String TAG = "SyncCalendar";

    public SyncCalendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SyncCalendar.
     */
    public static SyncCalendar newInstance() {
        return new SyncCalendar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        java.net.CookieHandler.setDefault(new java.net.CookieManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sync_calendar, container, false);
        // 获取验证码
        gc = new GetCaptcha();
        gc.execute();

        userId_edit = (EditText)v.findViewById(R.id.user_id);
        password_edit = (EditText)v.findViewById(R.id.password);
        captcha_edit = (EditText)v.findViewById(R.id.captcha_code);
        captcha_pic = (ImageView)v.findViewById(R.id.captcha_pic);
        Button submit = (Button)v.findViewById(R.id.submit);

        userId_edit.setText("20164617");
        password_edit.setText("wangmingjian1");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userId_edit.getText().toString();
                String psd = password_edit.getText().toString();
                String cap = captcha_edit.getText().toString();
                if (user.isEmpty()) {
                    userId_edit.setError("用户名或学号不能为空");
                    return;
                }
                if (psd.isEmpty()) {
                    password_edit.setError("密码不能为空");
                    return;
                }
                if (cap.isEmpty()) {
                    captcha_edit.setError("验证码不能为空");
                    return;
                }
                Login login = new Login();
                login.execute(user, psd, cap);
            }
        });

        captcha_pic.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   gc = new GetCaptcha();
                   gc.execute();
               }
           }
        );
        Button cancel = (Button)v.findViewById(R.id.cancel);
        return v;
    }

    private class GetCaptcha extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL("https://aao.qianhao.aiursoft.com/ACTIONVALIDATERANDOMPICTURE.APPPROCESS");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                Bitmap bm = BitmapFactory.decodeStream(con.getInputStream());
                //                for (String t: con.getHeaderFields().keySet()){
//                    Log.d("Headers", t + "  " + con.getHeaderFields().get(t));
//                }
//                StringBuilder cookieBuilder = new StringBuilder();
//                for (String aCookie : con.getHeaderFields().get("Set-Cookie")){
//                    cookieBuilder.append(aCookie.split(";")[0]).append(";");
//                }
//                String cookie = cookieBuilder.toString();
//                CookieManager.getInstance().setCookie("https://aao.qianhao.aiursoft.com", cookie);
                Log.d(TAG, "Set-Cookie:" + CookieManager.getInstance().getCookie("https://aao.qianhao.aiursoft.com"));
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("SyncCalender", "获取验证码图片错误");
            return null;
        }
        @Override
        protected void onPostExecute(final Bitmap bm) {
            captcha_pic.setImageBitmap(bm);
        }
    }

    private class Login extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(String... param) {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("WebUserNO=").append(param[0])
                    .append("&Password=").append(param[1])
                    // .append("&applicant=ACTIONQUERYSTUDENTSCHEDULEBYSELF")
                    .append("&Agnomen=").append(param[2])
                    .append("&submit7=%B5%C7%C2%BC");
            Log.d("Login" , "用户名:" + param[0] + " 密码:" + param[1] + " 验证码:" + param[2]);
            Log.d("Login cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));

            try {

                HttpURLConnection con = (HttpURLConnection)
                        (new URL("http://aao.qianhao.aiursoft.com/ACTIONLOGON.APPPROCESS?mode=")).openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                //con.setRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));

                OutputStream os = con.getOutputStream();
                os.write(contentBuilder.toString().getBytes());
                os.flush();
                os.close();
                InputStream is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while(is.read(buffer) != -1) {
                    baos.write(buffer);
                }
                con.disconnect();
                return new String(baos.toByteArray(), "GBK");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Log.d("登录请求结果", result);

            final String regexp1 = "您的密码错误了\\d次，共3次错误机会！";
            final String regexp2 = "请输入正确的附加码";

            if (result.contains(regexp1)) {
                password_edit.setError("密码错误");
                password_edit.setText("");
                password_edit.requestFocus();
                return;
            }

            if (result.contains(regexp2)) {
                captcha_edit.setError("验证码错误");
                captcha_edit.setText("");
                captcha_edit.requestFocus();
                gc = new GetCaptcha();
                gc.execute();
                return;
            }

            querySchedule qs = new querySchedule();
            qs.execute();
        }
    }

    private class querySchedule extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... param) {
            try {
                Log.d("Query syllabus cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));
                HttpURLConnection con = (HttpURLConnection)
                        (new URL("http://aao.qianhao.aiursoft.com/ACTIONQUERYSTUDENTSCHEDULEBYSELF.APPPROCESS?")).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));
                con.connect();
                InputStream is = con.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while(is.read(buffer) != -1) {
                    baos.write(buffer);
                }
                Log.d(TAG, "HTTP状态" + String.valueOf(con.getResponseCode()));
                con.disconnect();
                return new String(baos.toByteArray(), "GBK");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);
            final String regexp_start = "东北大学\\d{4}-\\d{4}学年第.学期学生课表";
            if (!Pattern.matches(regexp_start, result)) {
                MyTools.showToast("获取课程表失败", false);
                return;
            }
            ItemList itemList = new ItemList();
            Document document = Jsoup.parse(result);
            Element start = document.select("colgroup").first(); // 表格正文前的一个标签, 在页面中仅有一个colgroup
            Element tbody = start.nextElementSibling();
            Elements items_by_time = tbody.children();

            int enroll_week = Configure.enrollDate.get(Calendar.WEEK_OF_YEAR);

            // 0 1 2 分别为标题, 个人信息和星期几, 从第4行开始是课程表, 最后一行也是星期
            for (int i = 3; i < items_by_time.size() - 1; i++) {
                Elements single_item = items_by_time.get(i).children();
                // 第一列是时间
                for (int j = 1; j < single_item.size(); j++) {
                    String content = single_item.get(j).html();
                    String[] sp = content.split("<br style=\"mso-data-placement:same-cell\">");
                    for (int k = 0; k < sp.length % 4; k++) {
                        String name = sp[0 + k * 4];
                        String teacher = sp[1 + k * 4];
                        String room = sp[2 + k * 4];
                        String weeks = sp[3 + k * 4];
                        int start_week = Integer.valueOf(weeks.split("-")[0]);
                        int end_week = Integer.valueOf(weeks.split("-")[0]);
                        Calendar startTime = Time.getDateByWeek(start_week + enroll_week, j); // 教务处显示课程表第一列为周一
                        startTime.set(Calendar.HOUR_OF_DAY, i - 3 + Configure.start_class_hour);
                        startTime.set(Calendar.MINUTE, i - 3 + Configure.start_minute);
                        Calendar endTime = Time.getDateByWeek(end_week + enroll_week, j); // 教务处显示课程表第一列为周一
                        endTime.set(Calendar.HOUR_OF_DAY, i - 3 + Configure.start_class_hour + 2); // 一节课两个小时
                        endTime.set(Calendar.MINUTE, i - 3 + Configure.start_minute);
                        int every = 0x01 << (j == 7?0:j);
                        Item item = itemList.findItemByNameOrCreateCourse(name);
                        item.setDetails(teacher);
                        item.setOrganization("NEU");
                        item.expendTime(new Time(startTime.getTime(), endTime.getTime(), teacher, every,room,  -1, -1));
                        itemList.addItem(item);
                    }
                }
            }

            Log.i("获取的课程表", itemList.getJson());
        }
    }
}
