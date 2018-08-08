package com.example.ledwisdom1.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.mesh.Mesh;
import com.example.ledwisdom1.scene.Scene;
import com.example.ledwisdom1.user.Profile;

/**
 * 如果修改了entity的字段 一定要升级版本号
 * 提高Migration
 * 如果自已实体类被标记了entity 一定要在此处注册登记 否则编译不过
 */
@Database(entities = {Lamp.class, Profile.class,Mesh.class, Scene.class}, version = 5, exportSchema = false)
public abstract class SmartLightDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "SmartLight.db";
    private static SmartLightDataBase sDataBase;

    public abstract LampDao lamp();
    public abstract UserDao user();

    public synchronized static SmartLightDataBase INSTANCE(Context context) {
        if (sDataBase == null) {
            sDataBase = Room.databaseBuilder(context.getApplicationContext(), SmartLightDataBase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_3_4,MIGRATION_4_5)
//                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sDataBase;
    }

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE mesh ADD COLUMN userId text");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            加了一个表scene
            database.execSQL("CREATE TABLE IF NOT EXISTS `scene` (`id` TEXT NOT NULL, `creater` TEXT, `icon` TEXT, `meshId` TEXT, `meshName` TEXT, `name` TEXT, `sceneId` INTEGER NOT NULL, PRIMARY KEY(`id`))");

        }
    };
}
