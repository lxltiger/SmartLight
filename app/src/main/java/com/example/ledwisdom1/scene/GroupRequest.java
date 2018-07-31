package com.example.ledwisdom1.scene;

import java.io.File;

/**
 * 情景添加和修改请求参数
 */
public class GroupRequest {

    public int groupAddress;
    public String groupId = "";
    //修改时使用 存储旧设备
    public String oldDeviceId = "";
//    新设备
    public String newDeviceId = "";
    public String deviceId = "";
    public String name = "";
    public File pic = null;
    //是否是添加
    public boolean isAdd=true;
    public String imageUrl = "";


    public GroupRequest() {
    }

    public GroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public GroupRequest(String groupId,  String deviceId) {
        this.groupId = groupId;
        this.deviceId = deviceId;
    }


    @Override
    public String toString() {
        return "GroupRequest{" +
                "groupAddress=" + groupAddress +
                ", groupId='" + groupId + '\'' +
                ", oldDeviceId='" + oldDeviceId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", name='" + name + '\'' +
                ", pic=" + pic +
                ", isAdd=" + isAdd +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}