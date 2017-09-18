package wmj.InnerLayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mj on 17-9-18.
 * 项目数据库
 */

public class ItemDatabase extends SQLiteOpenHelper {
    public static final String ITEM_TABLE_NAME = "Items";
    public static final String TIME_TABLE_NAME = "Times";
    private static final String DB_NAME = "TIME_MANAGER.db";

    public ItemDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ItemDatabase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + ITEM_TABLE_NAME +
                "(id integer primary key, " +
                "name text," +
                "type integer," +
                "priority integer," +
                "color integer," +
                "details text," +
                "organization text)";
        db.execSQL(sql);
        String sql2 = "create table if not exists " + TIME_TABLE_NAME +
                "(time_id integer primary key," +
                "item_id integer," +
                "startTime text," +
                "endTime text," +
                "details text," +
                "every integer," +
                "place text)";
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + ITEM_TABLE_NAME;
        db.execSQL(sql);
        String sql2 = "DROP TABLE IF EXISTS " + TIME_TABLE_NAME;
        db.execSQL(sql2);
        onCreate(db);
    }
}
