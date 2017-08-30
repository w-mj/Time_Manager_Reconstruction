package wmj.timemanager.weekViewFragment;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import wmj.timemanager.R;

/**
 * Created by mj on 17-8-30.
 * 星期选择器
 */

public class WeekPicker extends LinearLayout {

    private TextView[] day = new TextView[7];
    private int status = 0;

    private OnStatusChangeListener mOnStatusChangeListener;

    public WeekPicker(Context context) {
        super(context);
        init();
    }
    public WeekPicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.week_picker_layout, this);
        day[0] = (TextView)findViewById(R.id.wpl_sun);
        day[1] = (TextView)findViewById(R.id.wpl_mon);
        day[2] = (TextView)findViewById(R.id.wpl_tue);
        day[3] = (TextView)findViewById(R.id.wpl_web);
        day[4] = (TextView)findViewById(R.id.wpl_thur);
        day[5] = (TextView)findViewById(R.id.wpl_fri);
        day[6] = (TextView)findViewById(R.id.wpl_sat);

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            day[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((status & (0x01 << finalI)) != 0) {
                        // 这个星期已经被选
                        ((TextView)v).setTextColor(Color.BLACK);
                        status = status & (~(0x01 << finalI));
                    } else {
                        ((TextView)v).setTextColor(Color.RED);
                        status = status | (0x01 << finalI);
                    }

                    if (mOnStatusChangeListener != null) {
                        mOnStatusChangeListener.onStatusChange(status);
                    }
                }
            });
        }
    }

    public int check() {return status;}

    public void set(final int status) {
        this.status = status;
        for (int i = 0; i < 7; i++) {
            if ((status & (0x01 << i)) != 0) {
                day[i].setTextColor(Color.RED);
            } else {
                day[i].setTextColor(Color.BLACK);
            }
        }
    }

    public void setOnStatusChangeListener(OnStatusChangeListener onStatusChangeListener) {
        mOnStatusChangeListener = onStatusChangeListener;
    }

    public interface OnStatusChangeListener {
        void onStatusChange(int status);
    }
}
