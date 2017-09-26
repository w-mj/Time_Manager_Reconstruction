package wmj.timemanager.weekViewFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.timemanager.R;

/**
 * Created by mj on 17-9-26.
 * 日历视图
 */

public class CalendarView extends LinearLayout implements
        TextView.OnClickListener, TextView.OnLongClickListener{

    private RelativeLayout[] weekdays;
    final private int weekRelativeLayoutId[] = {R.id.fns_sunday, R.id.fns_monday, R.id.fns_tuesday, R.id.fns_wednesday,
            R.id.fns_thursday, R.id.fns_friday, R.id.fns_saturday};
    final private String en2ch[] = {"日", "一", "二", "三", "四", "五", "六"};
    private ArrayList<Time> shownItem = new ArrayList<>();;

    private int startHour = 8, startMinute = 30, enrollWeek = 1;
    private ItemList itemList;

    private ChangeTimeDialog changeTimeDialog;
    private ConfirmDeleteDialog confirmDeleteDialog;

    public CalendarView(Context context, AttributeSet attributes) {
        super(context, attributes);
        init();
    }

    public void setStartTime(int h, int m) {
        startHour = h;
        startMinute = m;
    }
    public void setEnrollWeek(int week) {
        enrollWeek = week;
    }

    public void setitemList(ItemList list) {
        itemList = list;
    }

    void init() {
        inflate(getContext(), R.layout.calendar_view_fragment, this);
        float width = getResources().getDisplayMetrics().widthPixels;
        int aItemWidth = (int) (width * 0.13);
        int leftBarWidth = (int) (width * 0.09);
        // 此处获得的是整个屏幕的宽度
        weekdays = new RelativeLayout[7];
        Log.i("newSchedule", "屏幕的宽度是: " + String.valueOf(width));
        LinearLayout topWeekBar = (LinearLayout)findViewById(R.id.fns_top_week_bar);
        LinearLayout leftTimeBar = (LinearLayout)findViewById(R.id.fns_left_time_bar);
        // 设置左侧的时间栏
        int oneHourHeight = dip2px(getContext(), 40);
        for (int h = startHour; h < 24; h++) {
            TextView time = new TextView(getContext());
            time.setText(String.format(Locale.CHINA, "%2d:%2d", h, startMinute));
            time.setHeight(oneHourHeight);
            time.setWidth(leftBarWidth);
            leftTimeBar.addView(time);
        }


        // 设置顶部星期栏
        TextView blank = new TextView(getContext());
        blank.setWidth(leftBarWidth);
        topWeekBar.addView(blank);
        for (int i = 0; i < 7; i++) {
            TextView week = new TextView(getContext());
            week.setText("星期" + en2ch[i]);
            week.setWidth(aItemWidth);
            topWeekBar.addView(week);
        }

        // 设置事件列表
        for (int i = 0; i < 7; i++) {
            weekdays[i] = (RelativeLayout) findViewById(weekRelativeLayoutId[i]);
            LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams) weekdays[i].getLayoutParams();
            params.width = aItemWidth;
            weekdays[i].setLayoutParams(params);
        }
    }

    public void show(int week) {
        Log.i("newSchedule", "开始显示 第" + week + "周");

        for (int i = 0; i < 7; i++) {
            weekdays[i].removeAllViews(); // 清空现在有的views
        }
        shownItem.clear();
        HashMap<Integer, LinkedList<Time>> times = new HashMap<>();
        itemList.makeIndex();

        if (itemList.timeTable.get(week) == null)
            return;

        for (Time v: itemList.timeTable.get(week)) {
            for (int i = 0; i < 7; i++) {
                if ((v.getEvery() & (0x01 << i)) != 0) {
                    if (!times.containsKey(i)) {
                        times.put(i, new LinkedList<>());
                    }
                    times.get(i).add(v);
                }
            }
        }

        // 按开始时间排序
        for (int k : times.keySet()) {
            Collections.sort(times.get(k), new Comparator<Time>() {
                @Override
                public int compare(Time o1, Time o2) {
                    return o1.compareTo(o2);
                }
            });
        }

        Time lastTime;
        TextView lastTextView;
        for (int i = 0; i < 7; i++) {
            lastTime = null;
            lastTextView = null;
            if (times.get(i) == null) {
                continue;
            }
            for (Time t : times.get(i)) {
                TextView current = makeView(t);  // 按照时间创建新的TextView
                weekdays[i].addView(current);      // 把TextView添加到RelativeLayout中
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) current.getLayoutParams();
                if (lastTextView != null) {
                    params.topMargin = dip2px(getContext(),
                            Time.minusTime(lastTime.getEndTime(), t.getStartTime()) * 4 / 6);
                    Log.i("newSchedule, 设置布局关系", "current" + current.getId() + "  prev: " + lastTextView.getId());
                    params.addRule(RelativeLayout.BELOW, lastTextView.getId());
                } else {
                    Log.i("newSchedule, 设置布局关系", "current" + current.getId());
                    params.topMargin = dip2px(getContext(), Time.minusTime(t.getStartTime()) * 4 / 6);
                }
                Log.i("newSchedule, Margin top" , itemList.getItemById(t.getItemId()).getName() + String.valueOf(params.topMargin));
                current.setLayoutParams(params);

                lastTime = t;
                lastTextView = current;
            }
        }
    }

    private TextView makeView(Time now) {

        TextView result = new TextView(getContext());

        result.setHeight(dip2px(getContext(),
                Time.minusTime(now.getStartTime(), now.getEndTime()) * 4 / 6));
        result.setText(itemList.getItemById(now.getItemId()).getName() + "\n@" +
                now.getPlace());
        result.setBackgroundColor(itemList.getItemById(now.getItemId()).getColor());
        result.setBackgroundColor(0x7f040000);
        result.setId(shownItem.size() + 1);  // 设置递增id, id不能为０, 所以textView的id总比List中的item大1
        result.setOnClickListener(this); // 设置点击事件
        result.setOnLongClickListener(this);
        result.setBackground(getResources().getDrawable(R.drawable.item_text_view_bg, getContext().getTheme()));
        GradientDrawable myGrad = (GradientDrawable)result.getBackground();
        myGrad.setColor(now.getItem().getColor());

        shownItem.add(now);

//        Log.i("newSchedule", "创建textView\nheight:"+
//                String.valueOf(MyTools.dip2px(getContext(), Time.minusTime(now.startTime, now.endTime) * 4 / 6)) + "\n" +
//                "text:" + Configure.itemList.getItemById(now.item_id).getName() + "@" +
//                        now.place);

        return result;
    }

    public void onClick(View v) {
        if (changeTimeDialog == null)
            changeTimeDialog = new ChangeTimeDialog();

        Time t = shownItem.get(v.getId() - 1);
        Log.i("newSchedule", "点击TextView" + String.valueOf(v.getId()));

        FragmentManager fm = ((FragmentActivity)getContext()).getSupportFragmentManager();
        changeTimeDialog.setDialog(t);
        changeTimeDialog.show(fm, "changeTime");
    }

    @Override
    public boolean onLongClick(View v) {
        if (confirmDeleteDialog == null)
            confirmDeleteDialog = new ConfirmDeleteDialog();
        Time t = shownItem.get(v.getId() - 1);

        FragmentManager fm = ((FragmentActivity)getContext()).getSupportFragmentManager();
        confirmDeleteDialog.setTime(t);
        confirmDeleteDialog.show(fm, "confirmDeleteItem");
        return true;
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
