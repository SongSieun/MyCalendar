package com.sesong.mycalendar.Todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.sesong.mycalendar.R;
import com.sesong.mycalendar.databinding.ActivityTodoBinding;

import java.util.Calendar;

public class TodoActivity extends AppCompatActivity {
    private ActivityTodoBinding binding;
    private String todoTitle;
    private String todoContent;
    private String todoDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(TodoActivity.this, dateSetListener, year, month, day).show();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTodo();
            }
        });
    }

    private void getTodo(){
        todoTitle = binding.layoutContent.titleEdit.getText().toString();
        todoContent = binding.layoutContent.contentEdit.getText().toString();
        if (todoTitle != null && todoContent != null && todoDate != null) {
            saveTodo();
        }else{
            Toast.makeText(TodoActivity.this, "제목, 내용, 날짜를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTodo() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("todoTitle",todoTitle);
        resultIntent.putExtra("todoContent",todoContent);
        resultIntent.putExtra("todoDate",todoDate);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            if ((dayOfMonth / 10) == 0)
                todoDate = String.valueOf(year) + String.valueOf(month+1) + "0" + String.valueOf(dayOfMonth);
            else
                todoDate = String.valueOf(year) + String.valueOf(month+1) + String.valueOf(dayOfMonth);
            Log.d("!@#todoDate ", String.valueOf(todoDate));
        }
    };
}
