package com.example.ledwisdom1.user;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.model.RequestResult;

/**
 * 用户个人资料
 */
@Entity(tableName = "profile")
public class Profile extends RequestResult {

    @PrimaryKey
    @NonNull
    public final String phone;
    public final String isValidatjeesiteLogin;
    public final String icon;
    public final String sessionid;
    public final String userId;
    public final String meshOwner;
    //   当前默认的mesh 为空说明没有mesh
    public String meshId;


    public Profile(@NonNull String phone, String isValidatjeesiteLogin, String icon, String sessionid, String userId, String meshOwner, String meshId) {
        this.phone = phone;
        this.isValidatjeesiteLogin = isValidatjeesiteLogin;
        this.icon = icon;
        this.sessionid = sessionid;
        this.userId = userId;
        this.meshOwner = meshOwner;
        this.meshId = meshId;
    }

    @Deprecated
    public String getSessionid() {
        return sessionid;
    }

    @Deprecated
    public String getUserId() {
        return userId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "phone='" + phone + '\'' +
                ", isValidatjeesiteLogin='" + isValidatjeesiteLogin + '\'' +
                ", icon='" + icon + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", userId='" + userId + '\'' +
                ", meshOwner='" + meshOwner + '\'' +
                ", meshId='" + meshId + '\'' +
                '}';
    }
}
