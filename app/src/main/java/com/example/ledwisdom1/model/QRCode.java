package com.example.ledwisdom1.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QRCode implements Parcelable {
//    private String id;
    private String netType;
    private String meshPassword;
    private String othersId;
    private String meshName="";
    private String netId;
    private String otherName;


    public QRCode() {
    }

    public QRCode(String meshPassword, String othersId, String meshName) {
        this.meshPassword = meshPassword;
        this.othersId = othersId;
        this.meshName = meshName;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getMeshPassword() {
        return meshPassword;
    }

    public void setMeshPassword(String meshPassword) {
        this.meshPassword = meshPassword;
    }

    public String getOthersId() {
        return othersId;
    }

    public void setOthersId(String othersId) {
        this.othersId = othersId;
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }

    public String getNetId() {
        return netId;
    }

    public void setNetId(String netId) {
        this.netId = netId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.netType);
        dest.writeString(this.meshPassword);
        dest.writeString(this.othersId);
        dest.writeString(this.meshName);
        dest.writeString(this.netId);
        dest.writeString(this.otherName);
    }

    protected QRCode(Parcel in) {
        this.netType = in.readString();
        this.meshPassword = in.readString();
        this.othersId = in.readString();
        this.meshName = in.readString();
        this.netId = in.readString();
        this.otherName = in.readString();
    }

    public static final Parcelable.Creator<QRCode> CREATOR = new Parcelable.Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel source) {
            return new QRCode(source);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };
}
