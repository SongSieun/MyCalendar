package com.example.dsm2016.memo_db;

import android.content.Intent;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm.init(this);
        try {
            mRealm = Realm.getDefaultInstance();
        } catch (Exception e) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            mRealm = Realm.getInstance(config);
        }

        RealmResults<Save_DB> results = mRealm.where(Save_DB.class).findAll();
        if (results.size() == 0) {

        } else {
            for (int i = 0; i < results.size(); i++) {
                Save_DB save_db = results.get(i);
                Log.d(" \n메모 제목 :", save_db.getTitle() + " / 메모 내용 : " + save_db.getMemo_Data() + " / 메모한 날짜 : " + save_db.getMemo_Day());
            }
        }

        Button createMemo = (Button) findViewById(R.id.new_memo);

        createMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                startActivity(intent);
            }
        });
    }
}
