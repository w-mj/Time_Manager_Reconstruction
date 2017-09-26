package wmj.timemanager.weekViewFragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.database.ConfigureDataBase;
import wmj.timemanager.R;

public class WeekView extends Fragment{

    private ArrayList<String> weeks = new ArrayList<>();

    private Spinner spinner = null;

    private CalendarView calendarView;


    public WeekView() {}

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
        spinner = (Spinner)view.findViewById(R.id.fns_head_week_list);
        Log.i("newSchedule", "共有 " + weeks.size() + "周");
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, weeks);
        spinner.setAdapter(adapter);
        calendarView = (CalendarView)view.findViewById(R.id.calendar);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // MyTools.showToast(String.valueOf(position + Configure.user.startWeek), true);
                calendarView.show(position + Configure.enrollWeek);
                spinner.setSelection(position); // 设置星期下拉菜单
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageView menu = (ImageView)view.findViewById(R.id.fns_head_burger);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new AddItemDialog();
                dialogFragment.show(getFragmentManager(), "addItem");
            }
        });

        calendarView.setStartTime(Configure.start_hour, Configure.start_minute);
        calendarView.setitemList(Configure.itemList);
        refresh();
        return view;
    }

    public void refresh() {
        if (weeks.size() != Configure.endWeek) {
            Log.i("WeekView", "修改总周数" + weeks.size() + "=>" + Configure.endWeek);
            weeks.clear();
            for (int i = 1; i <= Configure.endWeek; i++) {
                weeks.add("第" + MyTools.num2cn(i) + "周");
            }
//            String sql = "insert or replace into " + ConfigureDataBase.TABLE_NAME +
//                    "(name, value) values ((select name from " + ConfigureDataBase.TABLE_NAME +
//                    " where name = \"endWeek\")," + String.valueOf(Configure.endWeek) + ")";

            SQLiteDatabase db = (new ConfigureDataBase(getContext())).getWritableDatabase();
            //db.execSQL(sql);
            ContentValues values = new ContentValues();
            values.put("value", String.valueOf(Configure.endWeek));
//            long id = db.insertWithOnConflict(ConfigureDataBase.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
//            if (id == -1) {
                db.update(ConfigureDataBase.TABLE_NAME, values, "name=\"endWeek\"", null);
//            }
            db.close();
        }
        calendarView.show(Configure.Current_week);
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, weeks);
        spinner.setAdapter(adapter);
        spinner.setSelection(Configure.Current_week - Configure.enrollWeek); // 设置星期下拉菜单
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
