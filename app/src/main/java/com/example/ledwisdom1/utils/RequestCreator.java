package com.example.ledwisdom1.utils;

import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.model.CommonRequest;
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
        DeviceRequest request = new DeviceRequest(id);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(request));
    }

    public static RequestBody requestAdLamp(Map<String, String> map) {
        JSONObject object = new JSONObject(map);
        JSONArray paramsArray = new JSONArray();
        paramsArray.put(object);
        String paramsStr = paramsArray.toString();

        return RequestBody.create(MEDIATYPE, paramsStr);
    }

    //

    /**
     * 为场景添加灯具
     *
     * @return
     */
    public static RequestBody requestAddLampToGroup(String groupId, String deviceIds) {
        AddDeviceToGroup addDeviceToGroup=new AddDeviceToGroup(groupId,deviceIds);
        return RequestBody.create(MEDIATYPE, new Gson().toJson(addDeviceToGroup));
    }

    public static RequestBody createAddHub(AddHubRequest addHubRequest) {
//        AddHubRequest request = new AddHubRequest(serialNum, ssid, psw, serialNum, "", "");
        return RequestBody.create(MEDIATYPE, new Gson().toJson(addHubRequest));
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
        private  String meshId;
        private  int pageNO;
        private  int pageSize;
        private  int typeId;
        private  String deviceId;

        public DeviceRequest(String meshId, int pageNO, int pageSize, int typeId) {
            this.meshId = meshId;
            this.pageNO = pageNO;
            this.pageSize = pageSize;
            this.typeId = typeId;
        }

        public DeviceRequest(String deviceId) {
            this.deviceId = deviceId;
        }
    }

    private static class AddDeviceToGroup {
        public final String groupId;
        public final String deviceId;

        public AddDeviceToGroup(String groupId, String deviceId) {
            this.groupId = groupId;
            this.deviceId = deviceId;
        }
    }



}
