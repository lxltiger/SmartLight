package com.example.ledwisdom1.scene;

import java.io.File;

public class GroupRequest {

    public  String groupId;
    public  String deviceId;
    public String meshId;
    public String name;
    public File pic;

    public GroupRequest() {
    }

    public GroupRequest(String groupId, String deviceId) {
        this.groupId = groupId;
        this.deviceId = deviceId;
    }

    public GroupRequest(String groupId, String name, File pic) {
        this.groupId = groupId;
        this.name = name;
        this.pic = pic;
    }

}