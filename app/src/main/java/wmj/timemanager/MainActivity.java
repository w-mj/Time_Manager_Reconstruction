package wmj.timemanager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;
import wmj.timemanager.activitiesFragment.Activities;
import wmj.timemanager.weekViewFragment.WeekView;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener{

    private static final int WEEKVIEW = 1;
    private static final int DAYVIEW = 2;
    private static final int ACTIVITIES = 3;
    private static final int GROUPS = 4;
    private static final int ME = 5;
    private static final int SignUp = 6;
    private static final int Login = 7;

    // 保存每个fragment的实例对象，防止重复初始化
    private Activities activities_fragment_instance;
    private WeekView calendar_fragment_instance;
    private Me me_fragment_instance;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment(Configure.DefaultCalenderView);
                    return true;
                case R.id.navigation_dashboard:
                    showFragment(ACTIVITIES);
                    return true;
                case R.id.navigation_notifications:
                    showFragment(ME);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        try {
            MyTools.initialization(this);
        } catch (Exception e) {
            Log.e("MainActivity", "初始化程序失败");
            e.printStackTrace();
        }
    }

    private void hideFragments(FragmentTransaction ft) {
        if (calendar_fragment_instance != null) {
            ft.hide( calendar_fragment_instance);
        }
        if (activities_fragment_instance != null) {
            ft.hide(activities_fragment_instance);
        }
        if (me_fragment_instance != null) {
            ft.hide(me_fragment_instance);
        }
    }

    private void showFragment(int fragmentIndex) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        hideFragments(ft);

        switch (fragmentIndex) {
            case WEEKVIEW:
                if (calendar_fragment_instance == null) {
                    calendar_fragment_instance = new WeekView();
                    ft.add(R.id.content, calendar_fragment_instance);
                } else {
                    calendar_fragment_instance.show(3);
                    ft.show(calendar_fragment_instance);
                }
                break;
            case ACTIVITIES:
                if (activities_fragment_instance == null) {
                    activities_fragment_instance = Activities.newInstance();
                    ft.add(R.id.content, activities_fragment_instance);
                } else {
                    ft.show(activities_fragment_instance);
                }
                break;
            case ME:
                if (me_fragment_instance == null) {
                    me_fragment_instance = Me.newInstance(this);
                    ft.add(R.id.content, me_fragment_instance);
                } else {
                    ft.show(me_fragment_instance);
                }
                break;
        }
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(String cmd, String msg) {
        Log.i("回调MainActivity", msg);
        if (cmd.equals("ShowFragment")) {
            switch (msg) {
                case "Calendar":
                    showFragment(WEEKVIEW);
                    break;
                case "Activities":
                    showFragment(ACTIVITIES);
                    break;
                case "Groups":
                    showFragment(GROUPS);
                    break;
                case "Me":
                    showFragment(ME);
                    break;
                default:
                    throw new RuntimeException("未知消息: " + msg);
            }
        } else {
            throw new RuntimeException("未知命令: " + cmd);
        }
    }
}
