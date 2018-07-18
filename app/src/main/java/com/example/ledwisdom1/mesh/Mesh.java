package com.example.ledwisdom1.mesh;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(tableName = "mesh",primaryKeys = "id")
public class Mesh {
    /**
     * password : d706d3
     * isDefault : 1
     * name : 1a0a7891
     * creater : 1ec1664c7872468e9241887bd9f7babc
     * homeName : home name
     * homeIcon : /homeIcon/homeIcon_20180706150001.png
     * id : f1f156c445b543db81507ad8a70fa711
     */

    private String password;
    private int isDefault;
    private String name;
//    创建者ID
    private String creater;
    private String homeName;
    private String homeIcon;
    @NonNull
    private String id;
    @Ignore
    private boolean showDeleteIcon = false;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getHomeIcon() {
        return homeIcon;
    }

    public void setHomeIcon(String homeIcon) {
        this.homeIcon = homeIcon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isShowDeleteIcon() {
        return showDeleteIcon;
    }

    public void setShowDeleteIcon(boolean showDeleteIcon) {
        this.showDeleteIcon = showDeleteIcon;
    }

    public Mesh() {
        this.password = "";
        this.isDefault = 0;
        this.name = "";
        this.creater = "";
        this.homeName = "";
        this.homeIcon = "";
        this.id = "";
    }

    //    判断是否是自己的网络
    public  boolean isMyMesh(String owner) {
        return creater.equals(owner);
    }

    public boolean isDefault() {
        return isDefault == 1;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesh mesh = (Mesh) o;
        return Objects.equals(password, mesh.password) &&
                Objects.equals(name, mesh.name) &&
                Objects.equals(creater, mesh.creater) &&
                Objects.equals(homeName, mesh.homeName) &&
                Objects.equals(id, mesh.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(password, name, creater, homeName, id);
    }
}
