package wmj.timemanager.weekViewFragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.control.MyCallable;
import wmj.timemanager.R;

public class WeekView extends Fragment implements TextView.OnClickListener{

    private RelativeLayout[] weekdays;
    final private int weekRelativeLayoutId[] = {R.id.fns_sunday, R.id.fns_monday, R.id.fns_tuesday, R.id.fns_wednesday,
            R.id.fns_thursday, R.id.fns_friday, R.id.fns_saturday};
    final private String en2ch[] = {"日", "一", "二", "三", "四", "五", "六"};
    private ArrayList<String> weeks = new ArrayList<>();

    private ArrayList<Time> shownItem;

    private ChangeTimeDialog changeTimeDialog;


    public WeekView() {
        shownItem = new ArrayList<>();
        for(int i = Configure.user.startWeek; i <= Configure.user.endWeek; i++) {
            weeks.add( "第" + MyTools.num2cn(i) + "周");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("newSchedule", "进入OnCreateView方法");
        View view = inflater.inflate(R.layout.fragment_new_schedule, container, false);
        Spinner spinner = (Spinner)view.findViewById(R.id.fns_head_week_list);
        Log.i("newSchedule", "共有 " + weeks.size() + "周");
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, weeks);
        spinner.setAdapter(adapter);
        initLineaLayout(view);
        show(Configure.Current_week);
        return view;
    }

    private void initLineaLayout(View view) {
        float width = getResources().getDisplayMetrics().widthPixels;
        int aItemWidth = (int) (width * 0.13);
        int leftBarWidth = (int) (width * 0.09);
        // 此处获得的是整个屏幕的宽度
        weekdays = new RelativeLayout[7];
        Log.i("newSchedule", "屏幕的宽度是: " + String.valueOf(width));
        LinearLayout topWeekBar = (LinearLayout) view.findViewById(R.id.fns_top_week_bar);
        LinearLayout leftTimeBar = (LinearLayout)view.findViewById(R.id.fns_left_time_bar);
        // 设置左侧的时间栏
        int oneHourHeight = MyTools.dip2px(getContext(), 40);
        for (int h = Configure.start_hour; h < 24; h++) {
            TextView time = new TextView(getContext());
            time.setText(String.format(Locale.CHINA, "%2d:%2d", h, Configure.start_minute));
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
            weekdays[i] = (RelativeLayout) view.findViewById(weekRelativeLayoutId[i]);
            LinearLayout.LayoutParams params =  (LinearLayout.LayoutParams) weekdays[i].getLayoutParams();
            params.width = aItemWidth;
            weekdays[i].setLayoutParams(params);
        }
    }

    public void show(int week) {
        Log.i("newSchedule", "开始显示");
        
        for (int i = 0; i < 7; i++) {
            weekdays[i].removeAllViews(); // 清空现在有的views
        }
        shownItem.clear();
        HashMap<Integer, LinkedList<Time>> times = new HashMap<>();
        Configure.itemList.makeIndex();
//        Configure.itemList.timeTable.get(week).forEach(v -> {
//            // 把这一周的所有时间按照星期几展开
//            for (int i = 0; i < 7; i++) {
//                if ((v.every & (0x01 << i)) != 0) {
//                    if (!times.containsKey(i)) {
//                        times.put(i, new LinkedList<>());
//                    }
//                    times.get(i).add(v);
//                }
//            }
//        });
        // TODO:使用lambda代替循环

        for (Time v: Configure.itemList.timeTable.get(week)) {
            for (int i = 0; i < 7; i++) {
                if ((v.every & (0x01 << i)) != 0) {
                    if (!times.containsKey(i)) {
                        times.put(i, new LinkedList<>());
                    }
                    times.get(i).add(v);
                }
            }
        }

        // 按开始时间排序
        // times.forEach((k, v) -> v.sort((o1, o2) -> o1.startTime.compareTo(o2.startTime)));
        // TODO:使用lambda代替循环
        for (int k : times.keySet()) {
            Collections.sort(times.get(k), new Comparator<Time>() {
                @Override
                public int compare(Time o1, Time o2) {
                    return o1.startTime.compareTo(o2.startTime);
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
                    params.topMargin = MyTools.dip2px(getContext(),
                            Time.minusTime(lastTime.endTime, t.startTime) * 4 / 6);
                    Log.i("newSchedule, 设置布局关系", "current" + current.getId() + "  prev: " + lastTextView.getId());
                    params.addRule(RelativeLayout.BELOW, lastTextView.getId());
                } else {
                    Log.i("newSchedule, 设置布局关系", "current" + current.getId());
                    params.topMargin = MyTools.dip2px(getContext(), Time.minusTime(t.startTime) * 4 / 6);
                }
                Log.i("newSchedule, Margin top" , Configure.itemList.getItemById(t.item_id).getName() + String.valueOf(params.topMargin));
                current.setLayoutParams(params);

                lastTime = t;
                lastTextView = current;
            }
        }
    }


    private TextView makeView(Time now) {

        TextView result = new TextView(getContext());

        result.setHeight(MyTools.dip2px(getContext(),
                Time.minusTime(now.startTime, now.endTime) * 4 / 6));
        result.setText(Configure.itemList.getItemById(now.item_id).getName() + "@" +
                now.place);
        result.setBackgroundColor(Configure.itemList.getItemById(now.item_id).getColor());
        result.setBackgroundColor(0x7f040000);
        result.setId(shownItem.size() + 1);  // 设置递增id, id不能为０, 所以textView的id总比List中的item大1
        result.setOnClickListener(this); // 设置点击事
        shownItem.add(now);

//        Log.i("newSchedule", "创建textView\nheight:"+
//                String.valueOf(MyTools.dip2px(getContext(), Time.minusTime(now.startTime, now.endTime) * 4 / 6)) + "\n" +
//                "text:" + Configure.itemList.getItemById(now.item_id).getName() + "@" +
//                        now.place);

        return result;
    }

    @Override
    public void onClick(View v) {
        if (changeTimeDialog == null)
            changeTimeDialog = new ChangeTimeDialog();

        Time t = shownItem.get(v.getId() - 1);
        Log.i("newSchedule", "点击TextView" + String.valueOf(v.getId()));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        changeTimeDialog.setDialog(t);
        changeTimeDialog.show(fm, "changeTime");
    }
}