package com.sesong.mycalendar.Todo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sesong.mycalendar.databinding.TodoItemBinding
import io.realm.Realm
import io.realm.RealmResults

class TodoRecyclerAdapter(private val mDataList: List<TodoItem>) : RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder>() {
    var binding: TodoItemBinding? = null
    private var realm: Realm? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        binding = TodoItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val todoItem = mDataList[i]
        viewHolder.binding.titleText.text = todoItem.title
        viewHolder.binding.contentText.text = todoItem.content
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ViewHolder(var binding: TodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    private fun removeItemView(position: Int) {
        mDataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mDataList.size)
    }

    // 데이터 삭제
    private fun removeMemo(text: String) {
        realm = Realm.getDefaultInstance()
        val results: RealmResults<TodoRealmObject> = realm.where(TodoRealmObject::class.java).equalTo("text", text).findAll()
        realm.executeTransaction(object : Transaction() {
            fun execute(realm: Realm?) {
                results.deleteFromRealm(0)
            }
        })
    }

}