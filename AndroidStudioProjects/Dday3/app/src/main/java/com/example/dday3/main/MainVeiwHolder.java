package com.example.dday3.main;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dday3.R;
import com.example.dday3.room.TodoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainVeiwHolder extends RecyclerView.ViewHolder {
    private TextView todo_tv_title;
    private CheckBox todo_cb;
    private TextView todo_tv_due;
    private TextView todo_tv_left;

    public  MainVeiwHolder(View itemView){
        super(itemView);

        todo_tv_title = itemView.findViewById(R.id.todo_title);
        todo_cb = itemView.findViewById(R.id.todo_cb);
        todo_tv_due = itemView.findViewById(R.id.todo_due_date);
        todo_tv_left = itemView.findViewById(R.id.todo_left);


    }

    public void onBind(TodoItem item) throws ParseException {
        todo_tv_title.setText(item.getTitle());
        todo_cb.setChecked(item.isChecked());
        todo_tv_due.setText(item.getDue());


        if (item.isChecked()){
            SpannableString contentSp = new SpannableString(item.getTitle());
            contentSp.setSpan(new StrikethroughSpan(), 0,
                item.getTitle().length(),0);
            todo_tv_title.setText(contentSp);
        }


        //left
        Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH) + 1;
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy / mm / dd");

        Date dDate = simpleDateFormat.parse(item.getDue());
        Date today = simpleDateFormat.parse(mYear+" / "+mMonth + "/"+mDay);
        Long left = -(dDate.getTime() - today.getTime()) / (26*60*60*1000);

        if (left.intValue() < 0){
            todo_tv_left.setText("D"+left);
        }
        else if (left.intValue() == 0){
            todo_tv_left.setText("D-Day");
            todo_tv_left.setTextColor(Color.RED);
        }
        else{
            todo_tv_left.setText("D"+left);
            todo_tv_left.setTextColor(Color.RED);
        }

    }


}
