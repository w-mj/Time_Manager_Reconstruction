package wmj.timemanager.weekViewFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.NetWork.SendPost;
import wmj.InnerLayer.control.MyHandler;
import wmj.timemanager.R;

public class ChangeTimeDialog extends DialogFragment implements
        TextView.OnClickListener,
        DialogInterface.OnClickListener{

    private Calendar chosen_start; // 选择的时间和日期
    private Calendar chosen_end; // 选择的时间和日期
    private int chosen_every;  // 选择的星期

    private TextView startTime;
    private TextView endTime;
    private TextView startDate;
    private TextView endDate;
    private TextView startWeek;
    private TextView during;
    private TextView[] weekdays;

    private Time t; // 旧时间

    @IdRes private final int START_TIME = 8;
    @IdRes private final int END_TIME = 9;
    @IdRes private final int START_DATE = 10;
    @IdRes private final int END_DATE = 11;

    public ChangeTimeDialog() {
        chosen_start = Calendar.getInstance();
        chosen_end = Calendar.getInstance();
        weekdays = new TextView[7];
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        Log.i("ChangeTimeDialog", "调用onCreateDialog");

        View v = View.inflate(getActivity(), R.layout.new_schedule_change_time_dialog, null);
        findViews(v);

        chosen_start.setTime(t.startTime);
        chosen_end.setTime(t.endTime);
        chosen_every = t.every;
        // 显示日期和时间
        startTime.setText(MyTools.timeFormatter.format(t.startTime));
        endTime.setText(MyTools.timeFormatter.format(t.endTime));
        startDate.setText(MyTools.dateFormatter.format(t.startTime));
        endDate.setText(MyTools.dateFormatter.format(t.endTime));
        startWeek.setText("第" + t.startWeek + "周");
        during.setText("共" + (t.endWeek - t.startWeek) + "周");

        //显示星期
        for (int i = 0; i < 7; i++ ) {
            if ((t.every & (0x01 << i)) != 0) {
                weekdays[i].setTextColor(Color.RED);
            } else {
                weekdays[i].setTextColor(Color.BLACK);
            }
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(Configure.itemList.getItemById(t.item_id).getName());
        dialog.setView(v);
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", this);
        return dialog.create();
    }

    private void findViews(View v) {
        startTime = (TextView) v.findViewById(R.id.nsctd_start_time);
        endTime = (TextView) v.findViewById(R.id.nsctd_end_time);
        startDate = (TextView) v.findViewById(R.id.nsctd_start_date);
        endDate = (TextView) v.findViewById(R.id.nsctd_end_date);
        startWeek = (TextView) v.findViewById(R.id.nsctd_start_week);
        during = (TextView) v.findViewById(R.id.nsctd_during);

        weekdays[0] = (TextView) v.findViewById(R.id.nsctd_every_sun);
        weekdays[1] = (TextView) v.findViewById(R.id.nsctd_every_mon);
        weekdays[2] = (TextView) v.findViewById(R.id.nsctd_every_tue);
        weekdays[3] = (TextView) v.findViewById(R.id.nsctd_every_wed);
        weekdays[4] = (TextView) v.findViewById(R.id.nsctd_every_thur);
        weekdays[5] = (TextView) v.findViewById(R.id.nsctd_every_fri);
        weekdays[6] = (TextView) v.findViewById(R.id.nsctd_every_sat);

        startTime.setId(START_TIME);
        endTime.setId(END_TIME);
        startDate.setId(START_DATE);
        endDate.setId(END_DATE);

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        for (int i = 0; i < 7; i++) {
            weekdays[i].setId(i);
            weekdays[i].setOnClickListener(v1 -> {
                if ((chosen_every & (0x01 << v1.getId())) != 0) {
                    // 这个星期已经被选
                    ((TextView)v1).setTextColor(Color.BLACK);
                    chosen_every = chosen_every & (~(0x01 << v1.getId()));
                } else {
                    ((TextView)v1).setTextColor(Color.RED);
                    chosen_every = chosen_every | (0x01 << v1.getId());
                }
            });
        }

    }

    public void setDialog(Time t) {
        this.t = t;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case START_TIME: showTimePicker(chosen_start, startTime); break;
            case END_TIME: showTimePicker(chosen_end, endTime); break;
            case START_DATE: showDatePicker(chosen_start, startDate); break;
            case END_DATE: showDatePicker(chosen_end, endDate); break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (chosen_start.getTime().compareTo(t.startTime) != 0 ||
                chosen_end.getTime().compareTo(t.endTime) != 0 ||
                t.every != chosen_every) {
            // 如果选择了新的时间, 则创建一个新的Time对象来替换原来的Time.
            Time newTime = t.clone();
            newTime.startTime = chosen_start.getTime();
            newTime.endTime = chosen_end.getTime();
            newTime.every = chosen_every;
            SendPost post = new SendPost("affair/upload/", null);
            post.data.put("type", "change_time");
            post.data.put("data", newTime.getJson());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(post);
            try {
                String result = future.get(2000, TimeUnit.MILLISECONDS);
                JSONObject j = new JSONObject(result);
                int new_time_id = j.getInt("new_time_id");
                newTime.time_id = new_time_id;
                Log.i("ChangeTime", "设置新的Time id为" + String.valueOf(new_time_id));
                Configure.itemList.getItemById(t.item_id).removeTime(t);
                Configure.itemList.getItemById(t.item_id).addTime(newTime);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                MyTools.showToast("网络错误, 你的修改不会被保存", false);
                e.printStackTrace();
            } catch (JSONException e) {
                MyTools.showToast("内部错误", false);
                e.printStackTrace();
            }

            Message msg = new Message();
            msg.what = MyHandler.REFRESH_FRAGMENT;
            msg.obj = "Default view";
            Configure.handler.sendMessage(msg);
        }
    }

    private void showTimePicker(Calendar chosen, TextView text) {
        TimePickerDialog picker = new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
            chosen.set(Calendar.HOUR_OF_DAY, hourOfDay);
            chosen.set(Calendar.MINUTE, minute);
            text.setText(MyTools.timeFormatter.format(chosen.getTime()));
        }, chosen.get(Calendar.HOUR_OF_DAY), chosen.get(Calendar.MINUTE), true);
        picker.show();
    }

    private void showDatePicker(Calendar chosen, TextView text) {
        DatePickerDialog picker = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            chosen.set(Calendar.YEAR, year);
            chosen.set(Calendar.MONTH, month);
            chosen.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            text.setText(MyTools.dateFormatter.format(chosen.getTime()));
        } , chosen.get(Calendar.YEAR), chosen.get(Calendar.MONTH), chosen.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }
}
