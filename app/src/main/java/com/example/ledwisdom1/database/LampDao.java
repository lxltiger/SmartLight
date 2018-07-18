package com.example.ledwisdom1.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ledwisdom1.device.entity.Lamp;

import java.util.List;

/**
 * 所有mesh下的灯具都存在一个表中
 */
@Dao
public interface LampDao {

    @Query("select * from lamp where meshId=:meshId")
    LiveData<List<Lamp>> loadLampsUnderMesh(String meshId);

    @Query("delete  from lamp where  meshId=:meshId")
    void clearLampsUnderMesh(String meshId);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLampsUnderMesh(List<Lamp> lamps);

    //设备离线 状态设为2
    /*@Query("update lamp set deviceState=2 where meshName=:meshName")
    void onMeshOffLine(String meshName);
*/
}
