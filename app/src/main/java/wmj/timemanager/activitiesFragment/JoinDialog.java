package wesayallright.timemanager.surface.activitiesFragment;


import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import wesayallright.timemanager.R;

/**
 * Created by mj on 17-4-24.
 * 当点击加入一个活动的时候的对话框
 */

public class JoinDialog{

    private static String authorities = "电话\n学号\n真实姓名\n";


     static public AlertDialog.Builder show(Activity a, ActivityViewItem item) {
         AlertDialog.Builder builder = new AlertDialog.Builder(a);
         View v = View.inflate(a, R.layout.join_dialog, null);
         builder.setTitle(item.name);

         TextView date = (TextView)v.findViewById(R.id.jd_date);
         TextView free = (TextView)v.findViewById(R.id.jd_free);
         TextView detail = (TextView)v.findViewById(R.id.jd_detail);
         TextView authority = (TextView) v.findViewById(R.id.jd_authority);

         date.setText(item.date.toString());
         free.setText("Free");
         free.setTextColor(0xFF00ff2A);
         detail.setText("签订契约吧!");
         authority.setText(authorities);

         builder.setView(v);

         builder.setPositiveButton("加入", null);
         builder.setNegativeButton("取消", null);

         builder.show();
         return builder;
    }
}