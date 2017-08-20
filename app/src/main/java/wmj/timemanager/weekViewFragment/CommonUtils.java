package wesayallright.timemanager.surface.newScheduleFragment;

import android.content.Context;
import android.util.Log;

/**
 * Created by mj on 17-7-20.
 * 通用工具类
 */

public class CommonUtils {
    private static String[] cnList = {"零","一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        // Log.i("屏幕密度", String.valueOf(scale));
        return (int)(dipValue * scale + 0.5f);
    }

    public static String num2cn(int num) {
        if (num < 10) {
            return cnList[num];
        } else if (num < 20) {
            return cnList[10] + cnList[num - 10];
        } else {
            return cnList[num / 10] + cnList[10] + cnList[num % 10];
        }
    }
}
