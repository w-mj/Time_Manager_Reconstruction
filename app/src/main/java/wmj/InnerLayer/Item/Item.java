package wmj.InnerLayer.Item;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PrimitiveIterator;
import java.util.Random;

import wmj.InnerLayer.Configure;
import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.control.MyHandler;
import wmj.timemanager.R;

/**
 * Created by mj on 17-5-9.
 * 每个项目
 */

public class Item {
    private String name;
    public int id;
    private ItemType type;
    private int priority;
    private int color;
    private String details;
    private String organization;

    private LinkedList<Time> time;
    private HashMap<Integer, LinkedList<Time>> index;

    public boolean indexed = false;


    private static final String TAG = "Item";

    public Item(int id, String name, ItemType type, String details, int color, int priority, String organization) {
        int[] colors = Configure.handler.getActivity().getResources().getIntArray(R.array.rainbow);
        this.id = id;
        this.type = type;
        this.details = details;
        this.name = name;
        // this.color = color;
        this.color = (colors[new Random().nextInt(colors.length)] & 0x00ffffff) | 0x99000000; // 随机颜色并设置透明
        this.priority = priority;
        time = new LinkedList<>();
    }

    public String getName() {return name;}
    public int getId() {return id;}
    public ItemType getType() {return type;}
    public int getPriority() {return priority;}
    public String getDetails() {return details;}
    public int getColor() {return color;}
    public LinkedList getTime() { return time; }
    public String getOrganization() {return organization;}

    public void setName(String name) {this.name = name;}
    public void setType(ItemType t) {type = t;}
    public void setType(String t) {type = ItemType.valueOf(t);}
    public void setDetails(String details) { this.details = details;}
    public void setColor(int color) {this.color = color;}
    public void setPriority(int priority) {this.priority = priority;}
    public void setOrganization(String organization) {this.organization = organization;}

    public void addTime(Time t) {
        time.add(t);
        indexed = false;
    }

    public void addTimes(LinkedList<Time> t) {
        time.addAll(t);
        indexed = false;
    }

    /**
     * 扩展时间, 如果新时间的开始正好是某一个时间的结尾, 则将原时间的结束修改为新时间的结束
     * 如果没找到可扩展的时间, 那么调用addTime
     */
    public void expendTime(Time newTime) {
        for (Time t : time) {
            if (t.every != newTime.every || newTime.every == 0)
                continue;
            if (Time.timeEqual(t.endTime, newTime.startTime)) {
                t.endTime = newTime.endTime;
                return;
            }
            if (Time.timeEqual(newTime.endTime, t.startTime)) {
                t.startTime = newTime.startTime;
                return;
            }
        }
        addTime(newTime);
    }

    /**
     * 为一个Item删除一个时间
     * @param t t一定是time列表里的某一个元素
     */
    public void removeTime(Time t) {
        for (int k: index.keySet()) {
            index.get(k).remove(t);
        }
        if (!time.remove(t))
            throw new RuntimeException("没有这个时间");
        indexed = false;
    }


    String getJson() {
        String data = "{" + "\"name\":\"" + name + "\", \"id\":" + id +
                ", \"type\":" + type.toInt() + ", \"priority\":" + priority +
                ", \"details\": \"" + details + "\"";
        StringBuilder timedata = new StringBuilder();
        for (Time t: time) {
            timedata.append(t.getJson());
            if (t != time.getLast())
                timedata.append(',');
        }

        return data + ", \"time\": [" + timedata.toString() + "]}";
    }


    public HashMap<Integer, LinkedList<Time>>getIndex() {
        if (!indexed) {

            if (index == null) index = new HashMap<>();

            for (Time k : time) {
                for (int i = k.startWeek; i < k.endWeek; i++) {
                    if (!index.containsKey(i))
                        index.put(i, new LinkedList<Time>());
                    index.get(i).add(k);
                }
            }
            indexed = true;
        }
        return index;
    }


    static public Item parseFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Item item = new Item(
                    jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    ItemType.Normal,
                    jsonObject.getString("details"),
                    jsonObject.getInt("color"),
                    jsonObject.getInt("priority"),
                    jsonObject.getString("organization")
            );
            try {
                JSONArray time = jsonObject.getJSONArray("time");
                for (int i = 0; i < time.length(); i++) {
                    JSONObject t = (JSONObject)time.get(i);
                    item.time.add(Time.parseFromJson(t));
                }
            } catch (JSONException e) {
                Log.e("item", e.getMessage());
            }
        } catch (JSONException e) {
            MyTools.showToast("内部错误", false);
            e.printStackTrace();
        }


        return null;
    }
}
