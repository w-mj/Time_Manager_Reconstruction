package wmj.timemanager.weekViewFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.Item.Item;
import wmj.InnerLayer.Item.ItemList;
import wmj.InnerLayer.Item.Time;
import wmj.InnerLayer.control.MyHandler;
import wmj.timemanager.R;

/**
 * Created by mj on 17-8-27.
 * 确认删除对话框
 */

public class ConfirmDeleteDialog extends DialogFragment {
    private static final String Tip = "你确定要删除%s吗\n\n注: 此操作将删除这个事件, 想删除单一时间请点击那个时间";
    Time time;
    public void setTime(Time t) {
        time = t;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        Log.i("Confirm dialog", "on create");

        Item item = Configure.itemList.getItemById(time.item_id);
        View view = View.inflate(getContext(), R.layout.new_schedule_confirm_delete_dialog, null);
        TextView tip = (TextView)view.findViewById(R.id.nscdd_tip);
        tip.setText(String.format(Tip, item.getName()));

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setView(view);
        dialog.setTitle("确定删除");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.removeTime(time);
                Configure.itemList.deletedItem.add(item.getId());
                Configure.itemList.saveChange(ItemList.ChangeType.DELETE_ITEM, item.getId());
                Message msg = new Message();
                msg.what = MyHandler.REFRESH_FRAGMENT;
                msg.obj = "Default view";
                Configure.handler.sendMessage(msg);
            }
        });
        dialog.setNegativeButton("取消", null);
        return dialog.create();
    }

}
