package wmj.timemanager.activitiesFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;
import wmj.timemanager.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mj on 17-4-29.
 * 添加新活动
 */


abstract public class NewActivityDialog{
    static public void NewActivity(final Activity a) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(a);
        View v = View.inflate(a, R.layout.new_activity_dialog, null);
        TextView title = (TextView)v.findViewById(R.id.nad_top);
        final TextView date = (TextView)v.findViewById(R.id.nad_date);
        final TextView time = (TextView)v.findViewById(R.id.nad_time);
        TextView limit = (TextView)v.findViewById(R.id.nad_limit);
        EditText place = (EditText)v.findViewById(R.id.nad_placeBox);
        EditText detail = (EditText)v.findViewById(R.id.nad_detail);

        dialog.setTitle("发起一个活动");
        // title.setText("添加新活动");
        final Date today = (Calendar.getInstance()).getTime();
        date.setText(MyTools.dateFormatter().format(today));
        time.setText(MyTools.timeFormatter().format(today));
        limit.setText("无限");
        final int[] chosenTime = new int[5]; // 神TM指针 厉害了我的Java
        final Calendar c = Configure.today;// 获取今天的日期
        chosenTime[0] = c.get(Calendar.YEAR);
        chosenTime[1] = c.get(Calendar.MONTH);
        chosenTime[2] = c.get(Calendar.DAY_OF_MONTH);
        chosenTime[3] = c.get(Calendar.HOUR_OF_DAY);
        chosenTime[4] = c.get(Calendar.MINUTE);

        date.setOnClickListener(v1 -> {

            // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
            DatePickerDialog datePicker = new DatePickerDialog(a,
                    // 绑定监听器
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // 选择的日期
                        chosenTime[0] = year;
                        chosenTime[1] = monthOfYear + 1;
                        chosenTime[2] = dayOfMonth;
                        date.setText(String.format(Locale.CHINA, "%04d-%02d-%02d",
                                chosenTime[0], chosenTime[1], chosenTime[2]));
                    }
                    // 设置初始日期
                    , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                    .get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
    });

        time.setOnClickListener(v12 -> {
            TimePickerDialog timePicker = new TimePickerDialog(a,
                    (view, hourOfDay, minute) -> {
                        chosenTime[3] = hourOfDay;
                        chosenTime[4] = minute;
                        time.setText(String.format(Locale.CHINA, "%02d:%02d",
                                chosenTime[3], chosenTime[4]));
                    }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true);
            timePicker.show();
        });

        dialog.setView(v);
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", null);

        dialog.show();

    }
}
