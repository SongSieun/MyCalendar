package com.sesong.todoapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var list: MutableList<ItemV0> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val date = Date()
        val sdFormat = SimpleDateFormat("yyyy-MM-dd")
        addDateView.text = sdFormat.format(date)

        addDateView.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dateDialog = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    addDateView.text = "$year-${monthOfYear + 1}-$dayOfMonth"
                }
            }, year, month, day).show()
        }
        selectDB()

        fab.setOnClickListener {
            val intent = Intent(this, AddTodoActivity::class.java)
            startActivityForResult(intent, 10)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_add) {
            if (addTitleEditView.text.toString() != null && addContentEditView.text.toString() != null) {

            }
        }
    }

    private fun selectDB() {
        list = mutableListOf()
        val helper = DBHelper(this)
        val db = helper.readableDatabase
        val cursor = db.rawQuery("select * from tb_todo order by date desc", null)

        var preDate: Calendar? = null
        while (cursor.moveToNext()) {
            val dbdate = cursor.getString(3)
            val date = SimpleDateFormat("yyyy-MM-dd").parse(dbdate)
            val currentDate = GregorianCalendar()
            currentDate.time = date

            if (!currentDate.equals(preDate)) {
                val headerItem = HeaderItem(dbdate)
                list.add(headerItem)
                preDate = currentDate
            }

            val completed = cursor.getInt(4) != 0
            val dateItem = DataItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), completed)
            list.add(dateItem)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(list)
        recyclerView.addItemDecoration(MyDecoration())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            selectDB()
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerView = view.itemHeaderView
    }

    class DataViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val completedIconView = view.completedIconView
        val itemTitleView = view.itemTitleView
        val itemContentView = view.itemContentView
    }

    inner class MyAdapter(val list: MutableList<ItemV0>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemViewType(position: Int): Int {
            return list.get(position).type
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == ItemV0.TYPE_HEADER) {
                val layoutInflater = LayoutInflater.from(parent?.context)
                return HeaderViewHolder(layoutInflater.inflate(R.layout.item_header, parent, false))
            } else {
                val layoutInflater = LayoutInflater.from(parent?.context)
                return DataViewHolder(layoutInflater.inflate(R.layout.item_main, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            val itemV0 = list.get(position)

            if (itemV0.type == ItemV0.TYPE_HEADER) {
                val viewHolder = holder as HeaderViewHolder
                val headerItem = itemV0 as HeaderItem
                viewHolder.headerView.setText(headerItem.date)
            } else {
                val viewHolder = holder as DataViewHolder
                val dataItem = itemV0 as DataItem
                viewHolder.itemTitleView.setText(dataItem.title)
                viewHolder.itemContentView.setText(dataItem.content)
                if (dataItem.completed) {
                    viewHolder.completedIconView.setImageResource(R.drawable.ic_check_box_black_24dp)
                } else {
                    viewHolder.completedIconView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp)
                }
                viewHolder.completedIconView.setOnClickListener {
                    val helper = DBHelper(this@MainActivity)
                    val db = helper.writableDatabase

                    if (dataItem.completed) {
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(0, dataItem.id))
                        viewHolder.completedIconView.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp)
                    } else {
                        db.execSQL("update tb_todo set completed=? where _id=?", arrayOf(1, dataItem.id))
                        viewHolder.completedIconView.setImageResource(R.drawable.ic_check_box_black_24dp)
                    }
                    dataItem.completed = !dataItem.completed
                    db.close()
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    inner class MyDecoration() : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            val index = parent!!.getChildAdapterPosition(view)
            val itemV0 = list.get(index)
            if (itemV0.type == ItemV0.TYPE_DATA) {
                view!!.setBackgroundColor(0xFFFFFFFF.toInt())
                ViewCompat.setElevation(view, 10.0f)
            }
            outRect!!.set(20, 10, 20, 10)
        }
    }
}

abstract class ItemV0 {
    abstract val type: Int

    companion object {
        val TYPE_HEADER = 0
        val TYPE_DATA = 1
    }
}

class HeaderItem(var date: String) : ItemV0() {
    override val type: Int
        get() = ItemV0.TYPE_HEADER
}

internal class DataItem(var id: Int, var title: String, var content: String, var completed: Boolean = false) : ItemV0() {
    override val type: Int
        get() = ItemV0.TYPE_DATA
}
