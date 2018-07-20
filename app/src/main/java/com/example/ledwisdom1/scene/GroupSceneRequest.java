package com.example.ledwisdom1.scene;

import java.io.File;

/**
 * 情景和场景的请求参数
 */
public class GroupSceneRequest {

    public int groupAddress;
    public String groupId = "";
    public String sceneId = "";
    public String deviceId = "";
    public String name = "";
    public File pic = null;
    //区分场景和情景
    public boolean isGroup;

    public GroupSceneRequest(boolean createGroup) {
        this.isGroup = createGroup;
    }

    public GroupSceneRequest() {
    }

    public GroupSceneRequest(String groupId, String sceneId) {
        this.groupId = groupId;
        this.sceneId = sceneId;
    }

    public GroupSceneRequest(String groupId, String sceneId, String deviceId) {
        this.groupId = groupId;
        this.sceneId = sceneId;
        this.deviceId = deviceId;
    }

    public GroupSceneRequest(String groupId, String name, File pic) {
        this.groupId = groupId;
        this.name = name;
        this.pic = pic;
    }

}