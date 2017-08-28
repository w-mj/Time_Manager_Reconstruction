package wmj.timemanager.weekViewFragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import wmj.InnerLayer.Item.Item;
import wmj.timemanager.R;

/**
 * Created by mj on 17-8-28.
 * 添加事件对话框
 */

public class AddItemDialog extends DialogFragment {

    private Item item;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saveInstance) {
        View backView = View.inflate(getActivity(), R.layout.new_schedule_add_item_dialog, null);
        LinearLayout timeList = (LinearLayout)backView.findViewById(R.id.nsaid_time_list);

        View time1 = View.inflate(getActivity(), R.layout.new_schedule_add_item_dialog_time, null);
        View time2 = View.inflate(getActivity(), R.layout.new_schedule_add_item_dialog_time, null);

        TextView t1 = (TextView)time1.findViewById(R.id.nsaidt_start_date);
        TextView t2 = (TextView)time2.findViewById(R.id.nsaidt_start_date);

        t1.setText("1111");
        t2.setText("22222");

        timeList.addView(time1);
        timeList.addView(time2);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setView(backView);
        dialog.setTitle("添加日程");
        return dialog.create();
    }
}
