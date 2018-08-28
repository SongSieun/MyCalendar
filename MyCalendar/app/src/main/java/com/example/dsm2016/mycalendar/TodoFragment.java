package com.example.dsm2016.mycalendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class TodoFragment extends DialogFragment {
    private TodoAdapter madapter;

        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_todo, null);

            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            final LayoutInflater lInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

            final ListView listView = (ListView) view.findViewById(R.id.todo_list);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final View layout = lInflater != null ? lInflater.inflate(R.layout.dialogcustom, (ViewGroup) view.findViewById(R.id.layout_root)) : null;
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(getActivity());
                    alertDlg.setTitle("할 일 추가하기");
                    alertDlg.setMessage("할 일을 추가해주세요");
                    alertDlg.setView(layout);
                    alertDlg.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 쓰기 모드의 DB
                            SQLiteDatabase db = TodoDbHelper.getsInstance(getActivity()).getWritableDatabase();
                            // 칼럼명, 값의 쌍으로 삽입할 정보를 구성
                            ContentValues values = new ContentValues();
                            EditText text = (EditText) layout.findViewById(R.id.edit_text);
                            String string = text.getText().toString();
                            values.put(TodoContract.TodoEntry.COLUMN_NAME_CONTENTS, string);
                            // 새로운 행이 삽입되면, 삽입된 id가 반환. 에러 발생 시 -1 반환
                            long newRowId = db.insert(TodoContract.TodoEntry.TABLE_NAME, null, values);

                            if (newRowId == -1)
                                Toast.makeText(getActivity(), "저장에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getActivity(), "할 일이 추가되었습니다", Toast.LENGTH_SHORT).show();
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
            TodoDbHelper dbHelper = TodoDbHelper.getsInstance(getActivity());
            Cursor cursor = dbHelper.getReadableDatabase().query(TodoContract.TodoEntry.TABLE_NAME, null, null, null, null, null, TodoContract.TodoEntry._ID + " DESC");
            madapter = new TodoAdapter(getActivity(), cursor);
            listView.setAdapter(madapter);

            // 리스트 클릭 시 삭제 알림
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final long deletedId = id;
                    // 삭제 할 것인지 다이얼로그 표시
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("할 일 삭제하기");
                    builder.setMessage("할 일을 삭제하시겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase db = TodoDbHelper.getsInstance(getActivity()).getWritableDatabase();
                            int deletedCount = db.delete(TodoContract.TodoEntry.TABLE_NAME, TodoContract.TodoEntry._ID + " = " + deletedId, null);
                            if (deletedCount == 0)
                                Toast.makeText(getActivity(), "삭제에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                            else {
                                madapter.swapCursor(getTodoCursor());
                                Toast.makeText(getActivity(), "메모가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();
                    return true;
                }
            });
        return view;
    }
    private Cursor getTodoCursor(){
        TodoDbHelper dbHelper = TodoDbHelper.getsInstance(getActivity());
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
