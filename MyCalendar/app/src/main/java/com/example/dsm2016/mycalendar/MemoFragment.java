package com.example.dsm2016.mycalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class MemoFragment extends Fragment {
    private static final int REQUEST_CODE_INSERT = 1000;
    private MemoAdapter mAdapter;
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memo, null);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), MemoActivity.class), REQUEST_CODE_INSERT);
            }
        });
        ListView listView = (ListView) view.findViewById(R.id.memo_list);
        MemoDbHelper dbHelper = MemoDbHelper.getsInstance(getActivity());
        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, MemoContract.MemoEntry._ID + " DESC");
        //MemoAdapter adapter = new MemoAdapter(getActivity(), cursor);
        //listView.setAdapter(adapter);
        mAdapter = new MemoAdapter(getActivity(), cursor);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MemoActivity.class);
                //클릭한 시점의 아이템을 얻음
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                //커서에서 제목과 내용을 얻음
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));
                //인텐트와 id와 함께 저장
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("contents", contents);
                //MemoActivity 시작
                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long deletedId = id;
                // 삭제할 것인지 다이얼로그 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("메모 삭제");
                builder.setMessage("메모를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = MemoDbHelper.getsInstance(getActivity()).getWritableDatabase();
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME, MemoContract.MemoEntry._ID + " = " + deletedId, null);
                        if (deletedCount == 0){
                            Toast.makeText(getActivity(), "삭제에 문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        }else{
                            mAdapter.swapCursor(getMemoCursor());
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
    private Cursor getMemoCursor(){
        MemoDbHelper dbHelper = MemoDbHelper.getsInstance(getActivity());
        return dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, null, null, null, null, MemoContract.MemoEntry._ID + " DESC");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //메모가 정상적으로 삽입되었다면, 메모목록을 갱신
        if (requestCode == REQUEST_CODE_INSERT && resultCode == RESULT_OK){
            mAdapter.swapCursor(getMemoCursor());
        }
    }

    private class MemoAdapter extends CursorAdapter {
        public MemoAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView titleText = (TextView) view.findViewById(android.R.id.text1);
            titleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }
    }
}