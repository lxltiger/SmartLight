package com.example.ledwisdom1.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ledwisdom1.mesh.Mesh;
import com.example.ledwisdom1.scene.Scene;
import com.example.ledwisdom1.user.Profile;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);


    @Query("select * from profile")
    Profile getProfile();

    @Query("select * from profile")
    LiveData<Profile> loadProfile();

    @Query("delete from profile")
    void deleteProfile();

    @Query("update profile set meshId=:meshId where phone=:phone")
    void updateMeshId(String phone, String meshId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeshes(List<Mesh> meshes);

    @Query("select * from mesh")
    LiveData<List<Mesh>> loadAllMesh();

    //    在家自己的mesh
    @Query("select * from mesh where creater=:userId")
    LiveData<List<Mesh>> loadMyMesh(String userId);


    @Query("delete from mesh where id=:meshId")
    void deleteMeshById(String meshId);

    @Query("delete from mesh")
    void deleteAllMeshes();

    @Query("select * from scene where meshId=:meshId")
    LiveData<List<Scene>> loadScene(String meshId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScene(List<Scene> sceneList);

    @Query("delete  from scene where meshId=:meshId")
    void deleteScenes(String meshId);

    @Query("delete  from scene where id=:id")
    void deleteSceneById(String id);

}
