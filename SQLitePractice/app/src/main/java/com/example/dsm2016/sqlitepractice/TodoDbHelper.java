package com.example.dsm2016.sqlitepractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDbHelper extends SQLiteOpenHelper {
    private static TodoDbHelper sInstance;
    // DB의 버전으로 1부터 시작하고 스키마가 변경될 때 숫자를 올린다
    private static final int DB_VERSION = 1;
    // DB 파일명
    private static final String DB_NAME = "Todo.db";
    // 테이블 생성 SQL 문
    private static final String SQL_CREATE_ENTRIES = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT)"
            , TodoContract.TodoEntry.TABLE_NAME
            , TodoContract.TodoEntry._ID
            , TodoContract.TodoEntry.COLUMN_NAME_CONTENTS);
    // 테이블 삭제 SQL 문
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TodoContract.TodoEntry.TABLE_NAME;

    // 팩토리 메서드
    public static synchronized TodoDbHelper getsInstance(Context context) {
        // 액티비티의 context가 메모리 릭(leak)을 발생할 수 있으므로
        // application context를 사용하는 것이 좋다
        if (sInstance == null) {
            sInstance = new TodoDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // 생성자를 private 로 직접 인스턴스화를 방지하고
    // getInstance() 를 통해 인스턴스를 얻어야 함
    private TodoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // DB 스키마가 변경될 때 여기서 데이터를 백업하고
        // 테이블을 삭제 후 재생성 및 데이터 복원 등을 한다
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
