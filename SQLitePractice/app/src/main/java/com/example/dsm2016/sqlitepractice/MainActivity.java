package com.example.dsm2016.sqlitepractice;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsm2016.sqlitepractice.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TodoAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final ListView listView = (ListView) findViewById(R.id.todo_list);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final View layout = inflater.inflate(R.layout.dialogcustom, (ViewGroup) findViewById(R.id.layout_root));
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                alertDlg.setTitle("할 일 추가하기");
                alertDlg.setMessage("할 일을 추가해주세요");
                alertDlg.setView(layout);
                alertDlg.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 쓰기 모드의 DB
                        SQLiteDatabase db = TodoDbHelper.getsInstance(MainActivity.this).getWritableDatabase();
                        // 칼럼명, 값의 쌍으로 삽입할 정보를 구성
                        ContentValues values = new ContentValues();
                        EditText text = (EditText) layout.findViewById(R.id.edit_text);
                        String string = text.getText().toString();
                        values.put(TodoContract.TodoEntry.COLUMN_NAME_CONTENTS, string);
                        // 새로운 행이 삽입되면, 삽입된 id가 반환. 에러 발생 시 -1 반환
                        long newRowId = db.insert(TodoContract.TodoEntry.TABLE_NAME, null, values);

                        if (newRowId == -1)
                            Toast.makeText(MainActivity.this, "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(MainActivity.this, "할 일이 추가되었습니다", Toast.LENGTH_SHORT).show();
                            /*Intent intent = getIntent();
                            finish();
                            startActivity(intent);*/
//                            listView.setAdapter(madapter);
                            madapter.swapCursor(getTodoCursor());
                        }
                    }
                });
                alertDlg.setNegativeButton("취소", null);
                alertDlg.create().show();
            }
        });
        TodoDbHelper dbHelper = TodoDbHelper.getsInstance(this);
        Cursor cursor = dbHelper.getReadableDatabase().query(TodoContract.TodoEntry.TABLE_NAME, null, null, null, null, null, TodoContract.TodoEntry._ID + " DESC");
        madapter = new TodoAdapter(this, cursor);
        listView.setAdapter(madapter);

        // 리스트 클릭 시 삭제 알림
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deletedId = id;
                // 삭제 할 것인지 다이얼로그 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("할 일 삭제하기");
                builder.setMessage("할 일을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = TodoDbHelper.getsInstance(MainActivity.this).getWritableDatabase();
                        int deletedCount = db.delete(TodoContract.TodoEntry.TABLE_NAME, TodoContract.TodoEntry._ID + " = " + deletedId, null);
                        if (deletedCount == 0) Toast.makeText(MainActivity.this, "삭제에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        else {
                            madapter.swapCursor(getTodoCursor());
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

    private Cursor getTodoCursor(){
        TodoDbHelper dbHelper = TodoDbHelper.getsInstance(this);
        return dbHelper.getReadableDatabase().query(TodoContract.TodoEntry.TABLE_NAME, null, null, null, null, null, TodoContract.TodoEntry._ID + " DESC");
    }

    private static class TodoAdapter extends CursorAdapter {
        public TodoAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView contentsText = (TextView) view.findViewById(android.R.id.text1);
            contentsText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_CONTENTS)));
        }
    }
}
