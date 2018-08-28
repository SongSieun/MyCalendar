package com.example.dsm2016.databaseexam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_INSERT = 1000;
    private MemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, MemoActivity.class), REQUEST_CODE_INSERT);
            }
        });
        ListView listView = (ListView) findViewById(R.id.memo_list);

        MemoDbHelper dbHelper = MemoDbHelper.getsInstance(this);
        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, null);
        //MemoAdapter adapter = new MemoAdapter(this, cursor);
        //listView.setAdapter(adapter);
        mAdapter = new MemoAdapter(this, cursor);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                // 클릭한 시점의 아이템을 얻음
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                // 커서에서 제목과 내용을 얻음
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));
                // 인텐트에 id와 함께 저장
                intent.putExtra("id", id);
                intent.putExtra("contents", contents);
                // MemoActivity 시작
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });
        // 아이템 롱 클릭 이벤트 정의
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deleteId = id;
                // 삭제할 것인지 다이얼로그 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",         new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = MemoDbHelper.getsInstance(MainActivity.this).getWritableDatabase();
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME, MemoContract.MemoEntry._ID + " = " + deleteId, null);
                        if (deletedCount == 0) {
                            Toast.makeText(MainActivity.this, "삭제에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            mAdapter.swapCursor(getMemoCursor());
                            Toast.makeText(MainActivity.this, "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });
    }

    private Cursor getMemoCursor() {
        MemoDbHelper dbHelper = MemoDbHelper.getsInstance(this);
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, MemoContract.MemoEntry._ID + " DESC");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 메모가 정상적으로 삽입되었다면, 메모 목록을 갱신
        if (requestCode == REQUEST_CODE_INSERT && resultCode == RESULT_OK) {
            mAdapter.swapCursor(getMemoCursor());
        }
    }

    private class MemoAdapter extends CursorAdapter {
        public MemoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView contentsText = (TextView) view.findViewById(android.R.id.text1);
            contentsText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS)));
        }
    }
}
