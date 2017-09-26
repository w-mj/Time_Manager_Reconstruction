package wmj.timemanager.Spiders;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Item;
import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.Item.ItemType;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.timemanager.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SyncCalendar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SyncCalendar extends Fragment {
    private View v;
    private GetCaptcha gc;

    private EditText userId_edit;
    private EditText password_edit;
    private EditText captcha_edit;
    private ImageView captcha_pic;

    private final String TAG = "SyncCalendar";

    private int maxEndWeek = 0;

    public SyncCalendar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
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

        userId_edit = (EditText) v.findViewById(R.id.user_id);
        password_edit = (EditText) v.findViewById(R.id.password);
        captcha_edit = (EditText) v.findViewById(R.id.captcha_code);
        captcha_pic = (ImageView) v.findViewById(R.id.captcha_pic);
        Button submit = (Button) v.findViewById(R.id.submit);

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
        Button cancel = (Button) v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = MyTools.showFragmentMessage("Default view");
                Configure.handler.sendMessage(msg);
            }
        });
        return v;
    }

    private class GetCaptcha extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL("https://aao.qianhao.aiursoft.com/ACTIONVALIDATERANDOMPICTURE.APPPROCESS");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                Bitmap bm = BitmapFactory.decodeStream(con.getInputStream());

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
        protected String doInBackground(String... param) {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append("WebUserNO=").append(param[0])
                    .append("&Password=").append(param[1])
                    // .append("&applicant=ACTIONQUERYSTUDENTSCHEDULEBYSELF")
                    .append("&Agnomen=").append(param[2])
                    .append("&submit7=%B5%C7%C2%BC");
            Log.d("Login", "用户名:" + param[0] + " 密码:" + param[1] + " 验证码:" + param[2]);
            String cookie = CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com");
            if (cookie != null) Log.d("Login cookie", cookie);

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
                while (is.read(buffer) != -1) {
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
                // Log.d("Query syllabus cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));
                HttpURLConnection con = (HttpURLConnection)
                        (new URL("http://aao.qianhao.aiursoft.com/ACTIONQUERYSTUDENTSCHEDULEBYSELF.APPPROCESS?")).openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Cookie", CookieManager.getInstance().getCookie("http://aao.qianhao.aiursoft.com"));
                con.connect();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"), 32 * 1024);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                Log.d(TAG, "HTTP状态" + String.valueOf(con.getResponseCode()));
                con.disconnect();
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String result) {
            // Log.d(TAG, result);
            final String regexp_start = "东北大学(\\d{4})-\\d{4}学年第.学期学生课表";
            final int[] startClassHour = new int[]{8, 10, 14, 16, 18, 20};
            final int[] startClassMinute = new int[]{30, 30, 0, 0, 30, 30};
            Matcher matcher = Pattern.compile(regexp_start).matcher(result);
            if (!matcher.find()) {
                MyTools.showToast("获取课程表失败", false);
                return;
            }
            int baseYear = Integer.valueOf(matcher.group(1));  // 开始第一周的年份, 基于这个年份计算星期
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
                    int every = 0x01 << (j == 7 ? 0 : j);
                    String content = single_item.get(j).html();
                    String[] sp = content.split("<br style=\"mso-data-placement:same-cell\">");
                    for (int k = 0; k < sp.length / 4; k++) {
                        String name = sp[0 + k * 4];  // 课程名称
                        String teacher = sp[1 + k * 4];  // 教师名字
                        String room = sp[2 + k * 4];  // 教室
                        String time = sp[3 + k * 4];  // 时间  e.g. 2-4.6-12.14-16周 2节

                        Log.i("读取课程", name + teacher + room + time);

                        Item item = itemList.findItemByNameOrCreateCourse("NEU", name);  // 找到或创建item
                        item.setDetails(teacher);
                        itemList.addItem(item);

                        String[] timeSplit = time.split(" ");
                        String week = timeSplit[0].substring(0, timeSplit[0].length() - 1);  // 去掉后面的周
                        int hour = Integer.valueOf(timeSplit[1].substring(0, timeSplit[1].length() - 1));  // 去掉后面的节并转换成整数

                        String[] splitStr = week.split("\\.");  // 按.分割字符串
                        for (String aStr : splitStr) {
                            int startWeek, endWeek;
                            if (aStr.contains("-")) {
                                startWeek = Integer.valueOf(aStr.split("-")[0]);  // 获得起始周和结束周
                                endWeek = Integer.valueOf(aStr.split("-")[1]);
                            } else {
                                startWeek = endWeek = Integer.valueOf(aStr);
                            }
                            if (endWeek > maxEndWeek) maxEndWeek = endWeek;  // 记录最大结束周
                            Calendar startTime = Calendar.getInstance();
                            Calendar end = Calendar.getInstance();
                            startTime.set(Calendar.YEAR, baseYear);  // 设置年为第一周所在的年份
                            //Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            startTime.set(Calendar.WEEK_OF_YEAR, enroll_week + startWeek - 1);  // 设置开始周
                            Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            startTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);  // 设置为星期的第一天
                            //Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            startTime.set(Calendar.HOUR_OF_DAY, startClassHour[i - 3]);
                            //Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            startTime.set(Calendar.MINUTE, startClassMinute[i - 3]);
                            //Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            Date startDate = startTime.getTime();
                            end.set(Calendar.YEAR, baseYear);
                            end.set(Calendar.WEEK_OF_YEAR, enroll_week + endWeek - 1);  // 设置结束周, 对与跨年可正确的进行星期偏移
                            Log.d("syncCalendar 结束时间", MyTools.dateTimeFormatter().format(end.getTime()));
                            end.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);  // 设置为这个星期的最后一天
                            end.set(Calendar.HOUR_OF_DAY, startClassHour[i - 3] + hour); //  每节课的时间
                            end.set(Calendar.MINUTE, startClassMinute[i - 3]);
                            // Log.d("syncCalendar 开始时间", MyTools.dateTimeFormatter().format(startTime.getTime()));
                            // Log.d("syncCalendar 结束时间", MyTools.dateTimeFormatter().format(end.getTime()));
                            Time t = new Time(startTime.getTime(), end.getTime(), "", every, room, item.getId(), -1);
                            item.addTime(t);  // 添加时间, 并可以自动扩展every
                        }
                    }
                }
            }

            String json = itemList.getJson();
            if (json.length() > 1000) {
                for (int i = 0; i < json.length(); i += 1000) {
                    if (i + 1000 < json.length())
                        Log.i("rescounter" + i, json.substring(i, i + 1000));
                    else
                        Log.i("rescounter" + i, json.substring(i, json.length()));
                }
            } else
                Log.i("resinfo", json);
            MyTools.showToast("获取课程表成功", true);
            Configure.itemList.clearType(ItemType.Course);
            Configure.itemList.addItemAll(itemList);
            Configure.itemList.saveToDatabase(getContext());
            Configure.endWeek = maxEndWeek;
            Log.i(TAG, "最大周是:" + maxEndWeek);
            Message msg = MyTools.showFragmentMessage("Default view");
            Configure.handler.sendMessage(msg);
        }
    }

}
