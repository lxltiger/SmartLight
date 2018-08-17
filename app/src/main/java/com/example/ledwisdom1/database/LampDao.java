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


    @Query("select * from lamp where meshId=:meshId and typeId=:typeId")
    LiveData<List<Lamp>> loadDevices(String meshId,int typeId);

    @Query("select * from lamp where meshId=:meshId")
    LiveData<List<Lamp>> loadDevices(String meshId);

    @Query("delete  from lamp where  meshId=:meshId")
    void deleteLampsUnderMesh(String meshId);

    @Query("delete  from lamp")
    void deleteLamps();


    @Query("delete  from lamp where  meshId=:meshId and typeId=:typeId")
    void deleteDeviceFromMesh(String meshId,int typeId);

    @Query("delete  from lamp where  meshId=:meshId")
    void deleteDeviceFromMesh(String meshId);

    @Query("delete  from lamp where  id=:id")
    void deleteLampById(String id);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLampsUnderMesh(List<Lamp> lamps);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDevices(List<Lamp> lamps);


    //更新制定设备的状态  需要 device id 和 mesh id  brightness为0就是关，-1 掉线 >0 就是开
    @Query("update lamp set brightness=:brightness where meshId=:meshId and device_id=:deviceId")
    void updateDeviceStatus(int brightness, String meshId, int deviceId);

    @Query("update lamp set brightness=:brightness where meshId=:meshId")
    void updateMeshStatus(int brightness, String meshId);

    @Query("select  * from lamp  where meshId=:meshId and device_id=:deviceId")
    LiveData<Lamp> loadLamp(String meshId,int deviceId);


}
