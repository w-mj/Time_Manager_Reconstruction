package wmj.InnerLayer.Item;

import java.util.HashMap;
import java.util.LinkedList;

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

    private LinkedList<Time> time;
    private HashMap<Integer, LinkedList<Time>> index;

    public boolean indexed = false;


    private static final String TAG = "Item";

    Item(int id, String name, ItemType type, String details, int color, int priority) {
        this.id = id;
        this.type = type;
        this.details = details;
        this.name = name;
        this.color = color;
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

    public void setName(String name) {this.name = name;}
    public void setType(ItemType t) {type = t;}
    public void setType(String t) {type = ItemType.valueOf(t);}
    public void setDetails(String details) { this.details = details;}
    public void setColor(int color) {this.color = color;}
    public void setPriority(int priority) {this.priority = priority;}

    public void addTime(Time t) {
        time.add(t);
        indexed = false;
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
}
