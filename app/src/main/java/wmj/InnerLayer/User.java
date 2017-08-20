package wmj.InnerLayer;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import wmj.InnerLayer.Group.GroupList;
import wmj.InnerLayer.NetWork.SendGet;
import wmj.InnerLayer.control.MyCallable;
import wmj.InnerLayer.control.MyMessage;


/**
 * Created by mj on 17-4-16.
 * 用户类
 */

public class User implements MyCallable {

    private enum Sex {male, female}

    public Date updateTime;
    public int userId;
    public String nickName;
    public String realName;
    public String location;
    public Date birthday;
    public short sex;
    public String studentId;
    public String phone;
    public String email;
    public String schoolId;
    public String schoolName;
    public String major;
    public int grade;
    private Date startDate;
    private Date endDate;
    public int startWeek;
    public int endWeek;
    public GroupList groups;

    // 空的构造函数
    public User(int userId) {this.userId = userId;}

    // 保存数据
    public boolean save() {
        return false;
    }
    // 读取个人信息
    public void loadInformationFromFile() {
        // TODO: 在数据库中暂存数据
    }

    public void setUserId(int id) throws Exception{
        if (userId!= -1) {
            throw new Exception("Already login");
        }
        userId = id;
    }

    public void loadInformationFromNet() {
        MyMessage msg = new MyMessage(MyMessage.Todo.Callback, "User");
        msg.msg2 = "load finish";
        SendGet get = new SendGet("resource/query/user/?user_id=" + String.valueOf(userId), msg);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(get).get(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            MyTools.showToast("未知错误", false);
        } catch (TimeoutException e) {
            MyTools.showToast("获取个人信息超时, 请检查网络连接", false);
        }
    }

    /**
     * 把xml中的内容读到user类的属性中
     */
    private void setValue(Document userXML) throws Exception {
        Element rootElement = userXML.getDocumentElement();
        DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        updateTime = dateTimeFormat.parse(rootElement.getAttribute("updateTime"));
        if( userId != Integer.valueOf(rootElement.getAttribute("id"))) {
            throw new Exception("不是这个用户");
        }
        nickName = rootElement.getAttribute("nickname");
        realName = rootElement.getAttribute("realname");
        sex = Short.valueOf(rootElement.getAttribute("sex")); // 厉害了,我的枚举
        phone = rootElement.getAttribute("mobile");
        email = rootElement.getAttribute("email");
        schoolId = rootElement.getAttribute("schoolID");
        schoolName = rootElement.getAttribute("schoolName");
        location = rootElement.getAttribute("location");
        major = rootElement.getAttribute("major");
        grade = Integer.valueOf(rootElement.getAttribute("grade"));
        startDate = dateFormat.parse(rootElement.getAttribute("startDate"));
        endDate = dateFormat.parse(rootElement.getAttribute("endDate"));
        studentId = rootElement.getAttribute("studentID");
        birthday = dateFormat.parse(rootElement.getAttribute("birthday"));

        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        startWeek = c.get(Calendar.WEEK_OF_YEAR) + 1;
        c.setTime(endDate);
        endWeek = c.get(Calendar.WEEK_OF_YEAR) + 1;
        Log.i("User", "开始星期:" + startWeek);
        Log.i("User", "结束星期:" + endWeek);
    }

    public void printClass() {
        Log.i("PRINTUSER",
                "nickname=" + nickName + "\n" +
                "realName" + realName + "\n" +
                // "age" + age + "\n" +
                //"sex" + sex.toString() + "\n" +
                "phone" + phone + "\n" +
                "email" + email + "\n" +
                "schoolID" + schoolId + "\n" +
                "schoolName" + schoolName + "\n" +
                "major" + major + "\n" +
                "grade" + grade + "\n");
    }

    @Override
    public void listener(String message, Object data) {
        if(message.equals("load finish")) {
            try {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                File tempFile = File.createTempFile("user_result", null);
                FileOutputStream tempFileOutput = new FileOutputStream(tempFile);
                tempFileOutput.write(((String) data).getBytes());

                Document doc = builder.parse(tempFile);
                setValue(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new Error("未知命令" + message);
        }
    }
}
