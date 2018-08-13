package com.example.dsm2016.memo_db;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MemoActivity extends AppCompatActivity {
    private Button save_button;
    private Button return_button;
    private EditText title_data;
    private EditText memo_data;
    private Realm mRealm;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd");
    String formatDate = sdfNow.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        mRealm.init(this);

        save_button = (Button) findViewById(R.id.save_memo);
        return_button = (Button) findViewById(R.id.return_memo);
        title_data = (EditText) findViewById(R.id.memo_title);
        memo_data = (EditText) findViewById(R.id.memo_data);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo_title = title_data.getText().toString();
                String memo_date = memo_data.getText().toString();
                try {
                    mRealm = Realm.getDefaultInstance();
                } catch (Exception e) {
                    RealmConfiguration config = new RealmConfiguration.Builder()
                            .deleteRealmIfMigrationNeeded()
                            .build();
                    mRealm = Realm.getInstance(config);
                }
                mRealm.beginTransaction();
                Save_DB save_db = mRealm.createObject(Save_DB.class);
                save_db.setTitle(memo_title);
                save_db.setMemo_Data(memo_date);
                save_db.setMemo_Day(formatDate);
                mRealm.commitTransaction();
                finish();
            }
        });

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
