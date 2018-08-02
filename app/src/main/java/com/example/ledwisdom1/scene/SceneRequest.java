package com.example.ledwisdom1.scene;

import java.io.File;

/**
 * 情景添加和修改请求参数
 */
public class SceneRequest {

    public int sceneAddress;
    public String sceneId = "";
    //场景id 目前只能选一个
    public String groupIds = "";
    //修改时使用 存储旧设备
    public String oldDeviceId = "";
//    新设备
    public String newDeviceId = "";
    public String deviceId = "";
    public String name = "";
    public File pic = null;
    //是否是添加
    public boolean isAdd=true;
//    默认设置场景
    public boolean isGroupSetting =true;
    public String imageUrl = "";


    public SceneRequest() {
    }

    public SceneRequest(String sceneId) {
        this.sceneId = sceneId;
    }

    public SceneRequest(String sceneId, String deviceId) {
        this.sceneId = sceneId;
        this.deviceId = deviceId;
    }


    @Override
    public String toString() {
        return "SceneRequest{" +
                "sceneAddress=" + sceneAddress +
                ", sceneId='" + sceneId + '\'' +
                ", oldDeviceId='" + oldDeviceId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", name='" + name + '\'' +
                ", pic=" + pic +
                ", isAdd=" + isAdd +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}