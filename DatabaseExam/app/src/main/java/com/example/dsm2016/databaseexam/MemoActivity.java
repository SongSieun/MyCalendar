package com.example.dsm2016.databaseexam;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

public class MemoActivity extends AppCompatActivity {
    private EditText mContentsEditText;
    private long mMemoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        mContentsEditText = (EditText) findViewById(R.id.contents_edit);

        Intent intent = getIntent();
        if (intent != null) {
            mMemoId = intent.getLongExtra("id", -1);
            String contents = intent.getStringExtra("contents");
            mContentsEditText.setText(contents);
        }
    }

    @Override
    public void onBackPressed() {
        // DB에 저장하는 처리
        String contents = mContentsEditText.getText().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS, contents);
        SQLiteDatabase db = MemoDbHelper.getsInstance(this).getWritableDatabase();

        if (mMemoId == -1) {
            long newRowId = db.insert(MemoContract.MemoEntry.TABLE_NAME, null, contentValues);

            if (newRowId == -1) {
                Toast.makeText(this, "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "메모가 저장되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }else{
            // 기존 메모 내용을 업데이트 처리
            int count = db.update(MemoContract.MemoEntry.TABLE_NAME, contentValues, MemoContract.MemoEntry._ID + " = " + mMemoId, null);
            if (count == 0){
                Toast.makeText(this, "수정에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "메모가 수정되었습니다", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
            }
        }
        // 뒤로 가기 원래의 동작이 실행됨
        super.onBackPressed();
    }
}
