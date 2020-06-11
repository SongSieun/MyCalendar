package com.sesong.mycalendar.todo

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.sesong.mycalendar.R
import com.sesong.mycalendar.databinding.ActivityTodoBinding
import java.util.*

class TodoActivity : AppCompatActivity() {
    private var binding: ActivityTodoBinding? = null
    private var todoTitle: String? = null
    private var todoContent: String? = null
    private var todoDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo)
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        DatePickerDialog(this@TodoActivity, dateSetListener, year, month, day).show()
        binding.fab.setOnClickListener { todo }
    }

    private val todo: Unit
        private get() {
            todoTitle = binding!!.layoutContent.titleEdit.text.toString()
            todoContent = binding!!.layoutContent.contentEdit.text.toString()
            if (todoTitle != null && todoContent != null && todoDate != null) {
                saveTodo()
            } else {
                Toast.makeText(this@TodoActivity, "제목, 내용, 날짜를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun saveTodo() {
        val resultIntent = Intent()
        resultIntent.putExtra("todoTitle", todoTitle)
        resultIntent.putExtra("todoContent", todoContent)
        resultIntent.putExtra("todoDate", todoDate)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private val dateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
        todoDate = if (dayOfMonth / 10 == 0) year.toString() + (month + 1).toString() + "0" + dayOfMonth.toString() else year.toString() + (month + 1).toString() + dayOfMonth.toString()
        Log.d("!@#todoDate ", todoDate.toString())
    }
}