package wmj.InnerLayer.Item;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import wmj.InnerLayer.MyTools;

/**
 * Created by mj on 17-5-9.
 * 每个项目
 */

public class Item {
    private String name;
    private int id;
    private ItemType type;
    private int priority;
    private int color;
    private String details;
    private String organization;

    private LinkedList<Time> time;
    private HashMap<Integer, LinkedList<Time>> index;

    public boolean indexed = false;

    private int[] colors = new int[]{0xFF007F, 0xFF0000, 0xFF7F00, 0xFFFF00, 0x7FFF00,
            0x00FF00, 0x00FF7F, 0x00FFFF, 0x007FFF, 0x0000FF, 0x7F00FF, 0xFF00FF};


    private static final String TAG = "Item";

    // 构造函数, 如果id=-1则通过机构和名称生成hash id
    public Item(int id, String name, ItemType type, String details, int color, int priority, String organization) {
        // int[] colors = Configure.handler.getActivity().getResources().getIntArray(R.array.rainbow);
        this.type = type;
        this.details = details;
        this.name = name;
        if (color != -1) {
            this.color = color;
        } else {
            this.color = (colors[new Random().nextInt(colors.length)] & 0x00ffffff) | 0x99000000; // 随机颜色并设置透明
        }
        this.priority = priority;
        this.organization = organization;
        if (id == -1) {
            this.id = (organization + name).hashCode();
        } else {
            this.id = id;
        }
        time = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public int getPriority() {
        return priority;
    }

    public String getDetails() {
        return details;
    }

    public int getColor() {
        return color;
    }

    public LinkedList<Time> getTime() {
        return time;
    }

    public String getOrganization() {
        return organization;
    }

    public String getFullName() {
        return organization + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ItemType t) {
        type = t;
    }

    public void setType(String t) {
        type = ItemType.valueOf(t);
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void addTime(Time t) {
        for (Time x : time) {
            if (t.getTimeId() == x.getTimeId()) {
                x.setEvery(x.getEvery() | t.getEvery());
                indexed = false;
                return;
            }
        }
        time.add(t);
        indexed = false;
    }

    public void addTimes(LinkedList<Time> t) {
        time.addAll(t);
        indexed = false;
    }


    /**
     * 为一个Item删除一个时间
     *
     * @param t t一定是time列表里的某一个元素
     */
    public void removeTime(Time t) {
        for (int k : index.keySet()) {
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
        for (Time t : time) {
            timedata.append(t.getJson());
            if (t != time.getLast())
                timedata.append(',');
        }

        return data + ", \"time\": [" + timedata.toString() + "]}";
    }


    public HashMap<Integer, LinkedList<Time>> getIndex() {
        if (!indexed) {

            if (index == null) index = new HashMap<>();

            for (Time k : time) {
                for (int i = k.getStartWeek(); i < k.getEndWeek(); i++) {
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
                    JSONObject t = (JSONObject) time.get(i);
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
