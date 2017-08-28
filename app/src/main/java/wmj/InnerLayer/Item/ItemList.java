package wmj.InnerLayer.Item;

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
import wmj.InnerLayer.NetWork.SendGet;
import wmj.InnerLayer.NetWork.SendPost;
import wmj.InnerLayer.Configure;
import wmj.InnerLayer.control.MyCallable;
import wmj.InnerLayer.control.MyHandler;

/**
 * Created by mj on 17-5-9.
 * 每一个日历项目
 */

public class ItemList implements MyCallable {

    public final static int READ = 0;
    public final static int UPLOAD = 1;

    private HashMap<Integer, Item> itemList;
    public HashMap<Integer, LinkedList<Time>> timeTable;

    public ArrayList<Integer> modified = new ArrayList<>();
    public ArrayList<Integer> added = new ArrayList<>();
    public ArrayList<Integer> deletedTime = new ArrayList<>();
    public ArrayList<Integer> deletedItem = new ArrayList<>();

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
            String result = future.get(2000, TimeUnit.MILLISECONDS);
            parseXML(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            MyTools.showToast("未知错误", false);
        } catch (TimeoutException e) {
            MyTools.showToast("获取日程表超时, 请检查网络连接", false);
        }
    }

    public enum ChangeType {ADD_TIME, DELETE_TIME, CHANGE_TIME, ADD_ITEM, DELETE_ITEM, CHANGE_ITEM}

    public void saveChange(ChangeType type, int content) {
        saveChange(type, String.valueOf(content));
    }
    public void saveChange(ChangeType type, String content) {
        SendPost post = new SendPost("affair/upload/", null);
        post.data.put("user_id", String.valueOf(Configure.user.userId));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String jsonData;
        switch (type) {
            case ADD_TIME:
                post.data.put("type", "add_time");
                break;
            case DELETE_TIME:
                post.data.put("type", "delete_time");
                content = "{\"id\":" + content + "}";
                break;
            case CHANGE_TIME:
                post.data.put("type", "change_time");
                break;
            case ADD_ITEM:
                post.data.put("type", "add_item");
                break;
            case DELETE_ITEM:
                post.data.put("type", "delete_item");
                content = "{\"id\":" + content + "}";
                break;
            case CHANGE_ITEM:
                post.data.put("type", "change_item");
                break;
            default:
                throw new RuntimeException("save change 未知命令" + String.valueOf(type));
        }
        post.data.put("data", content);
        executor.submit(post);
    }

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
                    default:
                        throw new Exception("unknown course type " + type_s);
                }

                int id = Integer.valueOf(item.getAttribute("id"));
                String name = item.getAttribute("name");
                int priority = Integer.valueOf(item.getAttribute("priority"));
                String details = item.getAttribute("details");

                Log.i("创建新项目", id + name + type + details);
                Item it = new Item(id, name, type, details, 0xFF0000, priority);

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

                itemList.put(it.id, it);
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
                if (v.item_id == itemId)
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
                removeIndex(v.id);
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
        String data = "{\"userId\": " + Configure.user.userId + "\"type\":all, \"data\":[";
        // data += itemList.entrySet().stream().map(k -> k.getValue().getJson()).collect(Collectors.joining(","));
        for (Map.Entry k : itemList.entrySet()) {
            data += ((Item) k.getValue()).getJson();
            data += ',';
        }
        data = data.substring(0, data.length() - 2);  // 去掉最后的逗号
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
}
