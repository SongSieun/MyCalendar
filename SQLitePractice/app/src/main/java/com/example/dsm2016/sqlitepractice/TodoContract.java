package com.example.dsm2016.sqlitepractice;

import android.provider.BaseColumns;

public class TodoContract {
    // 인스턴스화 금지
    private TodoContract() {

    }

    // 테이블 정보를 내부 클래스로 정의
    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";
        public static final String COLUMN_NAME_CONTENTS = "contents";
    }
}
