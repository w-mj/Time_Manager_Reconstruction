package wmj.timemanager.dayViewFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mj on 17-9-5.
 * 用于显示日视图的扇形VIew
 */

public class SectorView extends View {
    private Canvas mCanvas;

    public SectorView(Context context) {
        super(context);
    }
    public SectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onDraw(Canvas canvas) {
        mCanvas = canvas;
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO: 日视图
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Rect rect = new Rect(0, 0, width, height);
        Paint sectorPaint = new Paint();
        // canvas.drawArc();

        super.draw(canvas);
    }
}
