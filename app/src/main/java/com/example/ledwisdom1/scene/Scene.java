package com.example.ledwisdom1.scene;

import android.os.Parcel;
import android.os.Parcelable;

public class Scene implements Parcelable{
    /**
     * createTime : {"date":19,"day":4,"hours":16,"minutes":54,"month":6,"seconds":40,"time":1531990480000,"timezoneOffset":-480,"year":118}
     * creater : 1ec1664c7872468e9241887bd9f7babc
     * icon : /sceneIcon/sceneIcon_20180719165439.png
     * id : 62aaaa397e994873b9e6ccbf876ce205
     * meshId : b14497ec94044a8c94a37bf075d69d02
     * meshName :
     * name : height
     * sceneId : 32800
     */

    private String creater;
    private String icon;
    private String id;
    private String meshId;
    private String meshName;
    private String name;
    private int sceneId;


    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creater);
        dest.writeString(this.icon);
        dest.writeString(this.id);
        dest.writeString(this.meshId);
        dest.writeString(this.meshName);
        dest.writeString(this.name);
        dest.writeInt(this.sceneId);
    }

    public Scene() {
    }

    protected Scene(Parcel in) {
        this.creater = in.readString();
        this.icon = in.readString();
        this.id = in.readString();
        this.meshId = in.readString();
        this.meshName = in.readString();
        this.name = in.readString();
        this.sceneId = in.readInt();
    }

    public static final Creator<Scene> CREATOR = new Creator<Scene>() {
        @Override
        public Scene createFromParcel(Parcel source) {
            return new Scene(source);
        }

        @Override
        public Scene[] newArray(int size) {
            return new Scene[size];
        }
    };
}