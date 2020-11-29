package com.example.zorana.cats.database;

import android.content.Context;

import com.example.zorana.cats.database.dao.CarPartsDao;
import com.example.zorana.cats.database.dao.PresentDao;
import com.example.zorana.cats.database.dao.UserDao;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.database.entity.Present;
import com.example.zorana.cats.database.entity.User;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {User.class, CarParts.class, Present.class},
        version = 1,
        exportSchema = false)
public abstract class MyRoomDatabase extends RoomDatabase {

    private static MyRoomDatabase singleton;

    public abstract UserDao userDao();
    public abstract CarPartsDao carPartsDao();
    public abstract PresentDao presentDao();

    public static MyRoomDatabase getSingleton(final Context context){
        if(singleton==null){
            synchronized (MyRoomDatabase.class){
                if(singleton==null){
                    singleton= Room.databaseBuilder(context,
                            MyRoomDatabase.class, "my_database").
                            fallbackToDestructiveMigration().addCallback(callback).build();
                }
            }
        }

        return singleton;
    }

    private static RoomDatabase.Callback callback=new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new Thread(new Runnable() {
                @Override
                public void run() {
//                    long id=singleton.studentDao().insert(new Student("Zorana","Jankovic","0143","2016"));
//                    singleton.studentDao().insert(new Student("Milos","Matijasevic","0440","2015"));
//                    singleton.studentDao().insert(new Student("Ljiljana","Jankovic","0010","2016"));
//
//                    singleton.predmetDao().insert(new Predmet("11ss2","SS",6));
//                    singleton.predmetDao().insert(new Predmet("11ss3","MIPS",6));
//                    singleton.predmetDao().insert(new Predmet("11ss5","MUPS",6));
//                    long pr=singleton.predmetDao().insert(new Predmet("11ss2","PROBA",6));
//
//                    singleton.studentPredmetDao().insert(new StudentPredmet(id,pr));

                    //singleton.userDao().deleteAllUsers();
                }
            }).start();
        }
    };


}
