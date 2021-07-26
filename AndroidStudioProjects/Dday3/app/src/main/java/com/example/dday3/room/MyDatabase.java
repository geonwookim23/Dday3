package com.example.dday3.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(version = 1, entities = {TodoItem.class})
public abstract class MyDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();

    private static MyDatabase myDatabase;

    public static MyDatabase getInstance(Context context){
        if(myDatabase == null){
            myDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    MyDatabase.class, "myDatabase.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return myDatabase;
    }
}