package com.cardapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    private String TAG=DBManger.class.getSimpleName();

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

    //增
    public void add(String wallet_id, String personcardno, String serial, String money, String id) {
        /*
        * " wallet_id text," +
                " personcardno text," +
                " serial text," +
                " money text," +
                " id text" +
        * */

        ContentValues contentValues = new ContentValues();

//        String _text = "字符串";
//        contentValues.put("wallet_id", _text);
        contentValues.put("wallet_id", wallet_id);
        contentValues.put("personcardno", personcardno);
        contentValues.put("serial", serial);
        contentValues.put("money", money);
        contentValues.put("id", id);


        writableDatabase.insert("table1", null, contentValues);

    }

    //删
    public void del(String serial) {
        writableDatabase.delete("table1", "serial = ?", new String[]{serial});
    }


//查
    public List<CreateOrderBean.DataBean> select() {

        Cursor cursor = writableDatabase.query("table1", null, null, null, null, null, null, null);

        int position = cursor.getPosition();
        Log.e(TAG, "select: 游标默认位置：" + position);

//        String result = "";
        List<CreateOrderBean.DataBean> list=new ArrayList<>();
        do {
            cursor.moveToFirst();
            String wallet_id = cursor.getString(cursor.getColumnIndex("_text"));
            String personcardno = cursor.getString(cursor.getColumnIndex("_text"));
            String serial = cursor.getString(cursor.getColumnIndex("_text"));
            String money = cursor.getString(cursor.getColumnIndex("_text"));
            String id = cursor.getString(cursor.getColumnIndex("_text"));
            CreateOrderBean.DataBean bean=new CreateOrderBean.DataBean();
            bean.setId(id);
            bean.setMoney(money);
            bean.setSerial(serial);
            bean.setPersoncardno(personcardno);
            bean.setWallet_id(wallet_id);
            list.add(bean);
            Log.i(TAG, "select: "+bean.toString()+ "\n");
//            result += bean.toString()+ "\n";

        }while (cursor.moveToNext());

        /*while (cursor.moveToNext()) {


            String _text = cursor.getString(cursor.getColumnIndex("_text"));


            result += + "\n";

        }*/

        return list;

    }


}
