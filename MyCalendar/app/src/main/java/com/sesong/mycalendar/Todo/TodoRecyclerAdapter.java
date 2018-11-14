package com.sesong.mycalendar.Todo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sesong.mycalendar.R;
import com.sesong.mycalendar.databinding.TodoItemBinding;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {
    TodoItemBinding binding;
    private Realm realm;
    private List<TodoItem> mDataList;

    public TodoRecyclerAdapter(List<TodoItem> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        binding = TodoItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        TodoItem todoItem = mDataList.get(i);
        viewHolder.binding.titleText.setText(todoItem.getTitle());
        viewHolder.binding.contentText.setText(todoItem.getContent());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TodoItemBinding binding;

        public ViewHolder(TodoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void removeItemView(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
    }

    // 데이터 삭제
    private void removeMemo(String text) {
        realm = Realm.getDefaultInstance();
        final RealmResults<TodoRealmObject> results = realm.where(TodoRealmObject.class).equalTo("text", text).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteFromRealm(0);
            }
        });
    }
}
