package wmj.timemanager.weekViewFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Item;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.NetWork.SendPost;
import wmj.timemanager.R;

/**
 * Created by mj on 17-8-28.
 * 添加事件对话框
 */

public class AddItemDialog extends DialogFragment {

    private Item item;
    private LinkedList<AddItemDialogTimeView> timeViewList = new LinkedList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saveInstance) {
        View backView = View.inflate(getActivity(), R.layout.new_schedule_add_item_dialog, null);
        LinearLayout timeViewLayout = (LinearLayout)backView.findViewById(R.id.nsaid_time_list);

        if (timeViewList.size() == 0)
            addTimeView();

        // timeViewList.forEach(timeViewLayout::addView);  // 把创建的view全部添加到layout里
        for (View v: timeViewList) {
            timeViewLayout.addView(v);
        }

        View addTime = backView.findViewById(R.id.nsaid_add_time);
        TextView name = (TextView)backView.findViewById(R.id.nsaid_name);
        TextView detail = (TextView)backView.findViewById(R.id.nsaid_detail);
        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemDialogTimeView vvv = generateTimeView();
                timeViewLayout.addView(vvv);
                timeViewList.add(vvv);
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setView(backView);
        dialog.setTitle("添加日程");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject data = new JSONObject();
                try {
                    data.put("name", name.getText());
                    data.put("details", detail.getText());
                    data.put("type", "6");
                    data.put("priority", "0");

                    JSONArray times = new JSONArray();
                    for (AddItemDialogTimeView time : timeViewList) {
                        // 跳过已经被删除的时间
                        if (time.time != null)
                            times.put(time.time.getJsonObject());
                    }
                    data.put("time", times);

                    SendPost post = new SendPost("affair/upload", null);
                    post.data.put("type", "add_item");
                    post.data.put("user_id", String.valueOf(Configure.user.userId));
                    post.data.put("data", data.toString());

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    String result = executorService.submit(post).get(2000, TimeUnit.MILLISECONDS);

                    Configure.itemList.addItem(Item.parseFromJson(result));

                } catch (JSONException e) {
                    e.printStackTrace();
                    MyTools.showToast("内部错误", false);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    MyTools.showToast("网络错误", false);
                    e.printStackTrace();
                }
            }
        });
        return dialog.create();
    }


    private AddItemDialogTimeView generateTimeView(Calendar c) {
        Date startTime  = c.getTime();
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + 2);
        Date endTime = c.getTime();
        Time t = new Time(startTime, endTime, null, 0, null, -1, -1);
        AddItemDialogTimeView v = new AddItemDialogTimeView(getContext());
        v.setTime(t);
        return v;
    }

    private AddItemDialogTimeView generateTimeView() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        return generateTimeView(c);
    }

    public void addTimeView(Calendar c) {
        timeViewList.add(generateTimeView(c));
    }

    public void addTimeView() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        timeViewList.add(generateTimeView(c));
    }
}
