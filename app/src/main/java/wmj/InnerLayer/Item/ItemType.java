package wmj.InnerLayer.Item;

/**
 * Created by mj on 17-5-9.
 * 每个项目的类型, 有课程, 活动, 目标
 */

public enum ItemType {
    Course, Activity, Target, Normal;
    public int toInt() {
        if (this == Course)
            return 1;
        else if (this == Target)
            return 2;
        else if (this == Activity)
            return 3;
        else
            return 0;
    }
}
