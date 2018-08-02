package com.example.ledwisdom1.utils;

import android.util.ArrayMap;

import com.example.ledwisdom1.clock.ClockRequest;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.model.CommonRequest;
import com.example.ledwisdom1.scene.GroupRequest;
import com.example.ledwisdom1.scene.SceneRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestCreator {

    public static final MediaType MEDIATYPE = MediaType.parse("application/json; charset=utf-8");


    public static RequestBody createPage(int pageNO, int pageSize) {
        Page page = new Page(pageNO, pageSize);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(page));
    }

    public static RequestBody createModifyPsw(String account, String psw1, String psw2) {
        CommonRequest commonRequest = new CommonRequest(psw1, psw2, account);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(commonRequest));
    }


    public static RequestBody createFeedBack(String content, String phone) {
        CommonRequest commonRequest = new CommonRequest(0, "", content, phone);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(commonRequest));
    }

    public static RequestBody createMeshDetail(String meshId) {
        MeshRequest request = new MeshRequest(meshId, "", "");
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody createDeleteMesh(String meshId, String userId) {
        MeshRequest request = new MeshRequest(meshId, userId, "");
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }


    public static String createShareMeshCode(String meshId, String userId, String owner) {
        MeshRequest request = new MeshRequest(meshId, userId, owner);
        return new Gson().toJson(request);
    }

    public static RequestBody createShareMesh(String josn) {
        return RequestBody.create(MEDIATYPE, josn);
    }

    public static RequestBody requestLampList(String meshId, int pageNo) {
        DeviceRequest request = new DeviceRequest(meshId, pageNo, 30, 256);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestHubList(String meshId, int pageNo) {
        DeviceRequest request = new DeviceRequest(meshId, pageNo, 10, 0);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestDeleteLamp(String id) {
        DeviceRequest request = new DeviceRequest(null, id);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestAdLamp(Map<String, String> map) {
        JSONObject object = new JSONObject(map);
        JSONArray paramsArray = new JSONArray();
        paramsArray.put(object);
        String paramsStr = paramsArray.toString();

        return RequestBody.create(MEDIATYPE, paramsStr);
    }

    /**
     * 为场景添加灯具
     *
     * @return
     */


    public static RequestBody requestAddLampToGroup(GroupRequest request) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestAddLampToScene(SceneRequest request) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestAddGroupToScene(SceneRequest request) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }


    public static RequestBody requestDeviceSetting(String sceneId) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("objectId", sceneId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(map));
    }

    public static RequestBody createDeviceSetting(String params) {
        return RequestBody.create(MEDIATYPE, params);
    }

    public static RequestBody requestGroupFromScene(String sceneId) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("sceneId", sceneId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(map));
    }


    /*删除场景*/
    public static RequestBody requestDeleteGroup(String groupId) {
        GroupRequest request = new GroupRequest(groupId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestDeleteScene(String sceneId) {
        SceneRequest request = new SceneRequest(sceneId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    /*删除场景下的设备*/
    public static RequestBody requestDeleteDeviceInGroup(String groupId, String deviceIds) {
        GroupRequest request = new GroupRequest(groupId, deviceIds);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }


    /*删除情景下的设备*/
    public static RequestBody requestDeleteDeviceInScene(SceneRequest sceneRequest) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(sceneRequest));
    }


    /*获取场景下的设备*/
    public static RequestBody requestGroupDevices(String groupId) {
        GroupRequest groupRequest = new GroupRequest(groupId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(groupRequest));
    }

    /*获取情景景下的设备*/
    public static RequestBody requestSceneDevices(String sceneId) {
        SceneRequest groupRequest = new SceneRequest(sceneId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(groupRequest));
    }

    public static RequestBody requestClockDevices(String clockId) {
        ClockRequest clockRequest = new ClockRequest(clockId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(clockRequest));
    }

    public static RequestBody requestDeleteClock(String clockId) {
        ClockRequest clockRequest = new ClockRequest(clockId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(clockRequest));
    }

    public static RequestBody requestSwitchClock(String clockId, int isOpen) {
        ClockRequest clockRequest = new ClockRequest(clockId, isOpen);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(clockRequest));
    }


    public static RequestBody createAddHub(AddHubRequest addHubRequest) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(addHubRequest));
    }

    public static RequestBody requestClock(ClockRequest clockRequest) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(clockRequest));
    }

    public static RequestBody requestDeviceId(String meshId) {
        DeviceRequest deviceRequest = new DeviceRequest(meshId, null);
        String s = new Gson().toJson(deviceRequest);
        return RequestBody.create(MEDIATYPE, s);
    }


    private static class Page {
        private final int pageNO;
        private final int pageSize;


        public Page(int pageNO, int pageSize) {
            this.pageNO = pageNO;
            this.pageSize = pageSize;
        }

        public Page(int pageNO) {
            this.pageNO = pageNO;
            this.pageSize = 10;
        }

    }

    private static class MeshRequest {
        private final String meshId;
        private final String othersId;
        private final String owner;

        public MeshRequest(String meshId, String othersId, String owner) {
            this.meshId = meshId;
            this.othersId = othersId;
            this.owner = owner;
        }


    }

    /**
     * pageSize=10
     * typeId=8
     * 灯具列表请求
     */
    private static class DeviceRequest {
        private String meshId;
        private String deviceId;
        private int pageNO;
        private int pageSize;
        private int typeId;

        public DeviceRequest(String meshId, int pageNO, int pageSize, int typeId) {
            this.meshId = meshId;
            this.pageNO = pageNO;
            this.pageSize = pageSize;
            this.typeId = typeId;
        }

        public DeviceRequest(String meshId, String deviceId) {
            this.meshId = meshId;
            this.deviceId = deviceId;
        }

    }


}
