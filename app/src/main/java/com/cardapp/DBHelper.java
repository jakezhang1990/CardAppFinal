package com.cardapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/2/7 17:49
 */
public class DBHelper extends SQLiteOpenHelper {

    // 数据库默认名字
    public static final String db_name = "test.db";

    public DBHelper(Context context, int version) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table table1 (" +
                " wallet_id text," +
                " personcardno text," +
                " serial text," +
                " money text," +
                " id text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
