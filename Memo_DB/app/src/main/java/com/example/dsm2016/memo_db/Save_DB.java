package com.example.dsm2016.memo_db;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Save_DB extends RealmObject {

    @Required
    private String Title;
    private String Memo_Data;
    private String Memo_Day;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMemo_Data() {
        return Memo_Data;
    }

    public void setMemo_Data(String memo_Data) {
        Memo_Data = memo_Data;
    }

    public String getMemo_Day() {
        return Memo_Day;
    }

    public void setMemo_Day(String memo_Day) {
        Memo_Day = memo_Day;
    }
}
