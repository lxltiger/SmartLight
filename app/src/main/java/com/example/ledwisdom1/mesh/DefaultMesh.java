package com.example.ledwisdom1.mesh;

import android.os.Parcel;
import android.os.Parcelable;

public class DefaultMesh implements Parcelable{

    /**
     * aijiaIcon : /homeIcon/homeIcon_20180709115433.png
     * aijiaName : home name
     * createTime : {"date":9,"day":1,"hours":11,"minutes":54,"month":6,"seconds":34,"time":1531108474000,"timezoneOffset":-480,"year":118}
     * creater : 1ec1664c7872468e9241887bd9f7babc
     * id : 6e855981180e420c8fb1cf3d9acd82ed
     * name : ec1a9d9e
     * password : 38c205
     * shareCounts : 0
     */

    public String aijiaIcon;
    public String aijiaName;
    public String creater;
    public String id;
    public String name;
    public String password;
    public String shareCounts;
    public int deviceCount;
    public String gatewayId;
    public boolean isMine;

    public DefaultMesh() {
        this.password = "";
        this.name = "";
        this.creater = "";
        this.aijiaName = "";
        this.aijiaIcon = "";
        this.id = "";
        shareCounts = "0";
        deviceCount=0;
        gatewayId = "";
    }



    @Override
    public String toString() {
        return "DefaultMesh{" +
                "aijiaIcon='" + aijiaIcon + '\'' +
                ", aijiaName='" + aijiaName + '\'' +
                ", creater='" + creater + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", shareCounts='" + shareCounts + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.aijiaIcon);
        dest.writeString(this.aijiaName);
        dest.writeString(this.creater);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeString(this.shareCounts);
        dest.writeInt(this.deviceCount);
        dest.writeString(this.gatewayId);
        dest.writeByte(this.isMine ? (byte) 1 : (byte) 0);
    }

    protected DefaultMesh(Parcel in) {
        this.aijiaIcon = in.readString();
        this.aijiaName = in.readString();
        this.creater = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.password = in.readString();
        this.shareCounts = in.readString();
        this.deviceCount = in.readInt();
        this.gatewayId = in.readString();
        this.isMine = in.readByte() != 0;
    }

    public static final Creator<DefaultMesh> CREATOR = new Creator<DefaultMesh>() {
        @Override
        public DefaultMesh createFromParcel(Parcel source) {
            return new DefaultMesh(source);
        }

        @Override
        public DefaultMesh[] newArray(int size) {
            return new DefaultMesh[size];
        }
    };
}
