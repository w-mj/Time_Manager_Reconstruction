package wmj.InnerLayer.Item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import wmj.InnerLayer.MyTools;
import wmj.InnerLayer.NetWork.NetworkUtils;
import wmj.InnerLayer.NetWork.SendGet;
import wmj.InnerLayer.NetWork.SendPost;
import wmj.InnerLayer.Configure;
import wmj.InnerLayer.control.MyCallable;
import wmj.InnerLayer.control.MyHandler;
import wmj.InnerLayer.database.ItemDatabase;

/**
 * Created by mj on 17-5-9.
 * 每一个日历项目
 */

public class ItemList implements MyCallable {

    public final static int READ = 0;
    public final static int UPLOAD = 1;

    private HashMap<Integer, Item> itemList;
    public HashMap<Integer, LinkedList<Time>> timeTable;

    private boolean indexed = false;

    public ItemList() {
        timeTable = new HashMap<>();
        itemList = new HashMap<>();
    }


    public void loadInformationFromNet() {
        SendGet get = new SendGet("affair/get/?query_type=1&query_type=2&query_type=3&user_id=" + Configure.user.userId, null);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(get);
        try {
            String result = future.get(20000, TimeUnit.MILLISECONDS);
            parseXML(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            MyTools.showToast("未知错误", false);
        } catch (TimeoutException e) {
            MyTools.showToast("获取日程表超时, 请检查网络连接", false);
        }
    }

    public enum ChangeType {ADD_TIME, DELETE_TIME, CHANGE_TIME, ADD_ITEM, DELETE_ITEM, CHANGE_ITEM}

    @Override
    public void listener(int message, Object data) {
        // GET方法用于获得日程, POST方法用于把日程上传保存至服务器
        if (message == READ) {
            parseXML((String) data);
        } else if (message == UPLOAD) {
            if (data.equals("OK")) {
                MyTools.showToast("保存成功", true);
                Log.i("ItemList", "保存成功");
            } else {
                Log.e("ItemList", "保存失败" + data);
            }
        }
    }

    private void parseXML(String xml) {
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document d = documentBuilder.parse(is);
            Element rootElement = d.getDocumentElement();

            int schoolWeek = Integer.valueOf(rootElement.getAttribute("schoolWeek"));
            NodeList items = rootElement.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String type_s = item.getAttribute("type");
                ItemType type;
                switch (type_s) {
                    case "1":
                        type = ItemType.Course;
                        break;
                    case "2":
                        type = ItemType.Target;
                        break;
                    case "3":
                        type = ItemType.Activity;
                        break;
                    case "0":
                        type = ItemType.Normal;
                        break;
                    default:
                        throw new Exception("unknown course type " + type_s);
                }

                int id = Integer.valueOf(item.getAttribute("id"));
                String name = item.getAttribute("name");
                int priority = Integer.valueOf(item.getAttribute("priority"));
                String details = item.getAttribute("details");
                String organization = item.getAttribute("organization");

                Log.i("创建新项目", id + name + type + details);
                Item it = new Item(id, name, type, details, 0xFF0000, priority, organization);

                NodeList timeList = item.getElementsByTagName("time");
                for (int j = 0; j < timeList.getLength(); j++) {
                    Element time = (Element) timeList.item(j);

                    String startTime = time.getAttribute("startTime");
                    String endTime = time.getAttribute("endTime");
                    String time_details = time.getAttribute("details");
                    String place = time.getAttribute("place");
                    int every = Integer.valueOf(time.getAttribute("every"));
                    int time_id = Integer.valueOf(time.getAttribute("time_id"));

                    it.addTime(new Time(startTime, endTime, time_details, every, place, id, time_id));
                }

                itemList.put(it.getId(), it);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("parseItemList", "解析xml成功");
        Message msg = MyTools.showFragmentMessage("Default view");
        Configure.handler.sendMessage(msg);
    }


    /**
     * show 把x中给定的元素从timeTable中删除
     * <p>此方法会将x中的元素一并删除</p>
     *
     * @param x
     */
    public void removeIndex(HashMap<Integer, LinkedList<Time>> x) {
        for (int k : x.keySet()) {
            LinkedList<Time> v = x.get(k);
            for (Time e : v) {
                if (e.compareTo(v.getFirst()) >= 0) {
                    if (e.compareTo(v.getFirst()) == 0) {
                        timeTable.get(k).remove(e);
                    }
                    v.removeFirst();
                }
            }
        }
    }

    /**
     * 在索引列表中删除某个item所有的时间索引
     *
     * @param itemId itemid
     */
    public void removeIndex(int itemId) {
        HashMap<Integer, Time> deleteList = new HashMap<>();
        for (int k : timeTable.keySet()) {
            for (Time v : timeTable.get(k)) {
                if (v.getTimeId() == itemId)
                    deleteList.put(k, v);
            }
        }
        for (int k : deleteList.keySet()) {
            timeTable.get(k).remove(deleteList.get(k));
        }
    }

    public void joinIndex(HashMap<Integer, LinkedList<Time>> x) {
        for (int k : x.keySet()) {
            LinkedList<Time> v = x.get(k);
            if (!timeTable.containsKey(k)) {
                timeTable.put(k, new LinkedList<>());
            }
            Collections.sort(v);
            timeTable.get(k).addAll(v);
        }
    }

    public void makeIndex() {
        for (int k : itemList.keySet()) {
            Item v = itemList.get(k);
            if (!v.indexed) {
                removeIndex(v.getId());
                joinIndex(v.getIndex());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void makeIndex(int id) {
        joinIndex(itemList.get(id).getIndex());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getJson() {
        String data = "{\"userId\": " + Configure.user.userId + "\",type\":\"all\", \"data\":[";
        // data += itemList.entrySet().stream().map(k -> k.getValue().getJson()).collect(Collectors.joining(","));
        for (Map.Entry k : itemList.entrySet()) {
            data += ((Item) k.getValue()).getJson();
            data += ',';
        }
        data = data.substring(0, data.length() - 1);  // 去掉最后的逗号
        data += "]}";
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getJson(int id) {
        return itemList.get(id).getJson();
    }

    public Item getItemById(int id) {
        return itemList.get(id);
    }

    public HashMap<Integer, Item> getItemList() {
        return itemList;
    }

    public void addItem(Item i) {
        if (itemList.containsKey(i.getId()))
            return;
        itemList.put(i.getId(), i);
        makeIndex();
    }

    public void addItemAll(ItemList list) {
        itemList.putAll(list.itemList);
    }

    public void clearType(ItemType type) {
        ArrayList<Integer> deleteList = new ArrayList<>();
        for (int k : itemList.keySet()) {
            if (itemList.get(k).getType() == type) {
                deleteList.add(k);
            }
        }
        for(int k :deleteList) {
            itemList.remove(k);
        }
    }


    public void addTempItem(Item i) {
        itemList.put(i.getId(), i);
    }

    public Item findItemByNameOrCreateCourse(String organization, String name) {
        if (itemList.containsKey((organization + name).hashCode())) {
            return itemList.get((organization + name).hashCode());
        } else {
            return new Item(-1, name, ItemType.Course, "", -1, 0, organization);
        }
    }

    public void saveToDatabase(Context context) {
        int count = 0;
        ItemDatabase itemDatabaseHelper = new ItemDatabase(context);
        SQLiteDatabase data = itemDatabaseHelper.getWritableDatabase();
        data.beginTransaction();
        data.delete(ItemDatabase.ITEM_TABLE_NAME, null, null);
        data.delete(ItemDatabase.TIME_TABLE_NAME, null, null); // 删除全部数据
        for (int k : itemList.keySet()) {
            Item t = itemList.get(k);
            ContentValues values = new ContentValues();
            values.put("name", t.getName());
            values.put("id", t.getId());
            values.put("type", t.getType().toInt());
            values.put("priority", t.getPriority());
            values.put("color", t.getColor());
            values.put("details", t.getDetails());
            values.put("organization", t.getOrganization());
            data.insert(ItemDatabase.ITEM_TABLE_NAME, null, values);
            for (Time ti: t.getTime()) {
                ContentValues tv = new ContentValues();
                tv.put("startTime", MyTools.dateTimeFormatter().format(ti.getStartTime()));
                tv.put("endTime", MyTools.dateTimeFormatter().format(ti.getEndTime()));
                tv.put("details", ti.getDetails());
                tv.put("every", ti.getEvery());
                tv.put("place", ti.getPlace());
                tv.put("item_id", ti.getItemId());
                tv.put("time_id", ti.getTimeId());
                data.insert(ItemDatabase.TIME_TABLE_NAME, null, tv);
            }
            count += 1;
        }
        data.setTransactionSuccessful();
        data.endTransaction();
        Log.i("itemList", "保存到数据库成功: " + count);
    }

    public void readFromDatabase(Context context) {
        int count = 0;
        itemList.clear();
        ItemDatabase itemDatabase = new ItemDatabase(context);
        SQLiteDatabase db = itemDatabase.getReadableDatabase();
        Cursor itemSet = db.query(ItemDatabase.ITEM_TABLE_NAME, null ,null, null, null, null, null);
        while(itemSet.moveToNext()) {
            Item it = new Item(
                    itemSet.getInt(itemSet.getColumnIndex("id")),
                    itemSet.getString(itemSet.getColumnIndex("name")),
                    ItemType.parseInt(itemSet.getInt(itemSet.getColumnIndex("id"))),
                    itemSet.getString(itemSet.getColumnIndex("details")),
                    itemSet.getInt(itemSet.getColumnIndex("color")),
                    itemSet.getInt(itemSet.getColumnIndex("priority")),
                    itemSet.getString(itemSet.getColumnIndex("organization")));
            Cursor timeSet = db.query(ItemDatabase.TIME_TABLE_NAME, null, "item_id=?",
                    new String[]{String.valueOf(it.getId())}, null, null, null);
            while(timeSet.moveToNext()) {
                try {
                    Time ti = new Time(
                            MyTools.dateTimeFormatter().parse(timeSet.getString(timeSet.getColumnIndex("startTime"))),
                            MyTools.dateTimeFormatter().parse(timeSet.getString(timeSet.getColumnIndex("endTime"))),
                            timeSet.getString(timeSet.getColumnIndex("details")),
                            timeSet.getInt(timeSet.getColumnIndex("every")),
                            timeSet.getString(timeSet.getColumnIndex("place")),
                            timeSet.getInt(timeSet.getColumnIndex("item_id")),
                            timeSet.getInt(timeSet.getColumnIndex("time_id")));
                    it.addTime(ti);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            timeSet.close();
            itemList.put(it.getId(), it);
            count += 1;
        }
        itemSet.close();
        Log.i("ItemList", "从数据库读取数据成功:" + count);
    }
}
