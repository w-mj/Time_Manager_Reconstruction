package wmj.timemanager.weekViewFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.timemanager.R;

/**
 * Created by mj on 17-8-30.
 * 添加日程对话框里的每一个时间
 */

public class AddItemDialogTimeView extends LinearLayout{

    Time time;

    public AddItemDialogTimeView(Context context) {
        super(context);
    }
    // 不允许在xml中直接定义
//    public AddItemDialogTimeView(Context context, AttributeSet attributeSet) {
//        super(context, attributeSet);
//        init();
//    }

    public void setTime(Time time) {
        this.time = time;
        init();
    }

    public Time getTime() {return time;}

    private DatePickerDialog generateDatePickerDialog(Date container, TextView v) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(container);
        return new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance(Locale.CHINA);
                c.setTime(container);
                c.set(year, month, dayOfMonth);
                container.setTime(c.getTime().getTime());
                v.setText(String.format(Locale.CHINA, "%4d-%2d-%2d", year, month + 1, dayOfMonth));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog generateTimePickerDialog(Date container, TextView v) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(container);
        Log.i("Add item dialog time", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        return new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance(Locale.CHINA);
                c.setTime(container);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                container.setTime(c.getTime().getTime());
                v.setText(String.format(Locale.CHINA, "%02d:%02d", hourOfDay, minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
    }

    private void init() {
        inflate(getContext(), R.layout.new_schedule_add_item_dialog_time, this);
        setId(View.generateViewId());
        TextView startDateView = (TextView)findViewById(R.id.nsaidt_start_date);
        TextView startTimeView = (TextView)findViewById(R.id.nsaidt_start_time);
        TextView endDateView = (TextView)findViewById(R.id.nsaidt_end_date);
        TextView endTimeView = (TextView)findViewById(R.id.nsaidt_end_time);

        startDateView.setText(MyTools.dateFormatter().format(time.getStartTime()));
        startTimeView.setText(MyTools.timeFormatter().format(time.getStartTime()));
        endDateView.setText(MyTools.dateFormatter().format(time.getEndTime()));
        endTimeView.setText(MyTools.timeFormatter().format(time.getEndTime()));

        startDateView.setOnClickListener(v -> generateDatePickerDialog(time.getStartTime(), startDateView).show());
        startTimeView.setOnClickListener(v -> generateTimePickerDialog(time.getStartTime(), startTimeView).show());
        endDateView.setOnClickListener(v -> generateDatePickerDialog(time.getEndTime(), endDateView).show());
        endTimeView.setOnClickListener(v -> generateTimePickerDialog(time.getEndTime(), endTimeView).show());

        WeekPicker weekPicker = (WeekPicker)findViewById(R.id.nsaidt_repeat);
        Log.i("add item ", String.valueOf(time.getEvery()));
        weekPicker.set(time.getEvery());
        // 回调方法, 当weekPicker的状态改变时修改time
        weekPicker.setOnStatusChangeListener(status -> time.setEvery(status));

        Switch isRepeatSwitch = (Switch)findViewById(R.id.nsaidt_switch);
        isRepeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    weekPicker.setVisibility(View.VISIBLE);
                else
                    weekPicker.setVisibility(View.GONE);
            }
        });

        ImageView close = (ImageView)findViewById(R.id.nsaidt_close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                time = null;
            }
        });
    }
}
