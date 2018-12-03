package com.yjyc.zhoubian.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static MySQLiteHelper mySQLiteHelper;
    private static String DATABASE_NAME = "zhoubian.db";
    private static int DATABASE_VERSION = 3;

    private static final String CREATE_PIC_TABLE = "create table chat_message("
            + "id integer not null primary key AUTOINCREMENT, " // message '图片id', "
            + "from_id varchar(30) not null, "
            + "to_id varchar(30) not null, "
            + "type integer not null, "
            + "time_milli integer not null, "
            + "text varchar(1000) , "
            + "thumb_url varchar(300) , "
            + "hd_url varchar(300) , "
            + "local_url varchar(300) , "
            + "msg_status integer not null, "
            + "is_delete int(4) not null default 1)"; //COMMENT '是否已删除：1: 未被删除; 2: 已被删除')";

    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }
    public static MySQLiteHelper Instance(Context context) {
        if (mySQLiteHelper == null) {
            synchronized (MySQLiteHelper.class) {
                if (mySQLiteHelper == null) {
                    mySQLiteHelper = new MySQLiteHelper(context);
                }
            }
        }
        return mySQLiteHelper;
    }

    public static MySQLiteHelper getInstance() {
        return mySQLiteHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PIC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
