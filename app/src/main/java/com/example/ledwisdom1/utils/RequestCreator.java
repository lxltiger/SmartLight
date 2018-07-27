package com.example.ledwisdom1.utils;

import com.example.ledwisdom1.clock.ClockRequest;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.model.CommonRequest;
import com.example.ledwisdom1.scene.GroupSceneRequest;
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
        DeviceRequest request = new DeviceRequest(meshId, pageNo, 10, 8);
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


    public static RequestBody requestAddLampToGroupScene(GroupSceneRequest request) {
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }


    /*删除场景*/
    public static RequestBody requestDeleteGroup(String groupId) {
        GroupSceneRequest request = new GroupSceneRequest(groupId, null);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestDeleteScene(String sceneId) {
        GroupSceneRequest request = new GroupSceneRequest(null, sceneId);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    /*删除场景下的设备*/
    public static RequestBody requestDeleteDeviceInGroup(String groupId, String deviceIds) {
        GroupSceneRequest request = new GroupSceneRequest(groupId, null, deviceIds);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    /*获取场景详情*/
    public static RequestBody requestGroupDetail(String groupId) {
        GroupSceneRequest groupRequest = new GroupSceneRequest(groupId, null);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(groupRequest));
    }

    /*获取场景下的设备*/
    public static RequestBody requestGroupDevices(String groupId) {
        GroupSceneRequest groupRequest = new GroupSceneRequest(groupId, null);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(groupRequest));
    }

    /*获取情景景下的设备*/
    public static RequestBody requestSceneDevices(String sceneId) {
        GroupSceneRequest groupRequest = new GroupSceneRequest(null, sceneId);
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
