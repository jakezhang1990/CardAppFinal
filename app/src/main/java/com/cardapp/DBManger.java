package com.cardapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/2/7 18:00
 * https://blog.csdn.net/IT_XF/article/details/82591770
 */
public class DBManger {
    private Context context;
    private static DBManger instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;

    private DBManger(Context context) {
        this.context = context;
        DBHelper dbHelper = new DBHelper(context, 1);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    public static DBManger getInstance(Context context) {
        if (instance == null) {
            synchronized (DBManger.class) {
                if (instance == null) {
                    instance = new DBManger(context);
                }
            }
        }
        return instance;
    }
}
