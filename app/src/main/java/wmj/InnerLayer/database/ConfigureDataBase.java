package wmj.InnerLayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mj on 17-9-26.
 * 设置数据库
 */

public class ConfigureDataBase extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "Configure";
    private static final String DB_NAME = "TIME_MANAGER_Configure.db";

    public ConfigureDataBase(Context context) {
        super(context, DB_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME + " (name string, value string)";
        db.execSQL(sql);
        ContentValues initValues = new ContentValues();
        initValues.put("endWeek", "10");
        db.insert(TABLE_NAME, null, initValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
