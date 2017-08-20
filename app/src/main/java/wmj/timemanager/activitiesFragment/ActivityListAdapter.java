package wmj.timemanager.activitiesFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import wmj.timemanager.R;

/**
 * Created by mj on 17-4-22.
 * 活动列表的适配器
 */

public class ActivityListAdapter extends BaseAdapter {
    private List<ActivityViewItem> list; // 数据源
    private LayoutInflater inflater; // 布局装载器

    public ActivityListAdapter(Context context, List<ActivityViewItem> l) {
        list = l;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ActivityViewItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // 第一次运行, 分配空间
            convertView = inflater.inflate(R.layout.activities_item, null); // 创建一个新的Item

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.ai_activityName);
            viewHolder.data = (TextView)convertView.findViewById(R.id.ai_activityTime);

            // 缓存view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ActivityViewItem item = list.get(position);
        viewHolder.name.setText(item.name);
        viewHolder.data.setText(item.date.toString());
        // Log.i("时间", item.date.toString());

        return convertView;
    }

    /**
     * 用于缓存view,findViewByID是线性查找,在数据经常修改时效率低
     */
    class ViewHolder {
        TextView name;
        TextView data;
    }
}
