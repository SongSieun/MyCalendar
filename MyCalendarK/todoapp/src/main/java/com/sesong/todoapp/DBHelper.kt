package com.sesong.todoapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "tododb", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        val memoSQL = "create table tb_todo " +
                "(_id integer primary key autoincrement," +
                "title, " +
                "content, " +
                "date, " +
                "completed)" // 0: none, 1: completed
        db.execSQL(memoSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table tb_todo")
        onCreate(db)
    }

}