package com.qf.teawork.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by my on 2016/11/15.
 */

public class MySQLite extends SQLiteOpenHelper {
    public MySQLite(Context context) {
        super(context,"base.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //建一张数据表
        db.execSQL("create table shoucang(_id integer primary key autoincrement,title varchar,laiyuan varchar, name varchar,time varchar,id varchar)");
        db.execSQL("create table lishi(_id integer primary key autoincrement,title varchar,laiyuan varchar, name varchar,time varchar,id varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
