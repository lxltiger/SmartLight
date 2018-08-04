package com.example.ledwisdom1.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.ledwisdom1.mesh.Mesh;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.user.Profile;

/**
 * 如果修改了entity的字段 一定要升级版本号
 */
@Database(entities = {Lamp.class, Profile.class,Mesh.class}, version = 3, exportSchema = false)
public abstract class SmartLightDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "SmartLight.db";
    private static SmartLightDataBase sDataBase;

    public abstract LampDao lamp();
    public abstract UserDao user();

    public synchronized static SmartLightDataBase INSTANCE(Context context) {
        if (sDataBase == null) {
            sDataBase = Room.databaseBuilder(context.getApplicationContext(), SmartLightDataBase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sDataBase;
    }
}
