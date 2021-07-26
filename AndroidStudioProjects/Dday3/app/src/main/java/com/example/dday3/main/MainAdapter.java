package com.example.dday3.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dday3.R;
import com.example.dday3.addEdit.AddEditActivity;
import com.example.dday3.room.MyDatabase;
import com.example.dday3.room.TodoItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainVeiwHolder> {

    private List<TodoItem> itemList = new ArrayList<>();
    public void submitAll(List<TodoItem> list){ //submit All (전체 저장)
        itemList.clear();
        itemList.addAll(list);
        Collections.sort(itemList);
        notifyDataSetChanged();
    }
    public void removeAll(){    //remove All (전체 삭제)
        itemList.clear();
        notifyDataSetChanged();
    }
    public void removeAllItem(List<TodoItem> newList){ //기존의 것 전부 삭제하고 받아온 list로 바꿔주기
        itemList.clear();
        itemList = newList;
        notifyDataSetChanged();
    }

    public boolean checkItem(TodoItem item){
        return item.isChecked();
    }

    @NonNull
    @Override
    public MainVeiwHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //두 가지 경우가 있겠지? 1.그냥 눌렀을 때 2.길게 눌렀을 때
        final MainVeiwHolder viewHolder = new MainVeiwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TodoItem temp = itemList.get(viewHolder.getAdapterPosition());
                temp.setChecked(!temp.isChecked());
                MyDatabase myDatabase = MyDatabase.getInstance(parent.getContext());
                myDatabase.todoDao().editTodo(temp);
                notifyItemChanged(viewHolder.getAdapterPosition()); //눌렀을 때 checkbox 표시
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { //길게 누르면
                final TodoItem temp = itemList.get(viewHolder.getAdapterPosition());    //클릭한 아이템 지정
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(temp.getTitle());
                final String[] items = {"수정", "삭제", "취소"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(items[which]){
                            case "수정":
                                //intent 사용. mode, item_id 보내기
                                Intent intent = new Intent(parent.getContext(), AddEditActivity.class);
                                intent.putExtra("mode", 1);
                                intent.putExtra("item_id", temp.getId());
                                parent.getContext().startActivity(intent);
                                break;
                            case "삭제":
                                //remove, 데이터베이스, 다오
                                itemList.remove(temp);
                                MyDatabase myDatabase = MyDatabase.getInstance(parent.getContext());
                                myDatabase.todoDao().deleteTodo(temp);
                                notifyItemRemoved(viewHolder.getAdapterPosition()); //이게 뭘까?
                                break;
                            case "취소":
                                //cancel
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainVeiwHolder holder, int position) {
        //묶어주기
        try{
            holder.onBind(itemList.get(position));
        }catch(ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }



}
