package com.yjyc.zhoubian.Dao.Util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.yjyc.zhoubian.Dao.ChatMessageDao;
import com.yjyc.zhoubian.Dao.MySQLiteHelper;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DaoUtil {

    private static final String TAG = "DaoUtil";

    public static void closeDb(SQLiteDatabase db, Cursor cursor) {
        try {
            if(cursor != null) {
                cursor.close();
            }
            if(db != null) {
                db.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.i(e.toString());
        }
    }

    public static List<ECMessage> executeQueryMessage(String sql){
        SQLiteDatabase db = MySQLiteHelper.getInstance().getWritableDatabase();
        if(db == null) {
            return null;
        }
        List<ECMessage> entitys = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext()){
                ECMessage message = ChatMessageDao.CreateMessage(cursor);
                if(message != null){
                    entitys.add(message);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.i(e.toString());
            return null;
        }finally {
            closeDb(db, cursor);
        }
        return entitys;
    }

    public static int executeUpdate(String sql) {
        SQLiteDatabase db = MySQLiteHelper.getInstance().getWritableDatabase();
        if(db == null) {
            return -1;
        }
        try {
            db.execSQL(sql);
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtil.i(e.toString());
            return -1;
        }finally {
            closeDb(db, null);
        }
    }

}
