package wmj.timemanager.weekViewFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by mj on 17-8-6.
 * 用于显示分隔的箭头
 */

public class ArrowView extends View {
    Paint paint = new Paint();
    public ArrowView(Context context) {
        super(context);
    }
    public ArrowView(Context context, AttributeSet attr) {super(context, attr); }
    public ArrowView(Context context, AttributeSet attr, int defStyle) {super(context, attr, defStyle); }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(3);
        Log.i("ArrowView", "画布宽度" + getWidth() + "; 画布高度" + getHeight());
        canvas.drawLine(0, 0, getWidth(), getHeight() / 2, paint);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight() / 2, paint);
        super.onDraw(canvas);
    }
}
