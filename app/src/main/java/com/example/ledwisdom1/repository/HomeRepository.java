package com.example.ledwisdom1.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.KimAscendService;
import com.example.ledwisdom1.api.NetWork;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.AppExecutors;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.clock.Clock;
import com.example.ledwisdom1.clock.ClockList;
import com.example.ledwisdom1.clock.ClockRequest;
import com.example.ledwisdom1.clock.ClockResult;
import com.example.ledwisdom1.database.SmartLightDataBase;
import com.example.ledwisdom1.database.UserDao;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.home.entity.HubList;
import com.example.ledwisdom1.mesh.AddMeshResult;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.Mesh;
import com.example.ledwisdom1.mesh.MeshList;
import com.example.ledwisdom1.mesh.ReportMesh;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.scene.AddGroupSceneResult;
import com.example.ledwisdom1.scene.GroupDevice;
import com.example.ledwisdom1.scene.GroupRequest;
import com.example.ledwisdom1.scene.GroupSceneRequest;
import com.example.ledwisdom1.scene.SceneList;
import com.example.ledwisdom1.scene.SceneRequest;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.example.ledwisdom1.utils.RequestCreator;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 负责网络请求 查询数据库数据 为ViewModel提供数据
 * <p>
 * 数据中心 单列模式
 * 一旦实例化，首先从数据库表profile加载个人数据，没有使用SharePreference防止缓存被清空
 * 如果profile为空 1.进入登录页面
 * 2.不为空说明已经登录进入首页
 * 2.1 meshId为空 新建空mesh
 * 2.3 meshId不为空 从后台加载最新的mesh信息
 * <p>
 * Profile采用LiveData封装，当其中meshId被修改就会根据meshId重新加载最新的Mesh，头像其他个人资料暂未实行
 */
public class HomeRepository {
    private static final String TAG = HomeRepository.class.getSimpleName();
    private static HomeRepository sDataRepository;
    private final SmartLightDataBase mDataBase;
    private final KimAscendService kimService;
    private final UserDao userDao;
    private final AppExecutors executors;

    //个人资料 是否需要观察 TODO
    public final LiveData<Profile> profileObserver;
    //    默认mesh
    public final MediatorLiveData<DefaultMesh> defaultMeshObserver = new MediatorLiveData<>();

    private String sessionId = "";


    private HomeRepository(Context context) {
        mDataBase = SmartLightDataBase.INSTANCE(context);
        userDao = mDataBase.user();
        kimService = NetWork.kimService();
        executors = SmartLightApp.INSTANCE().appExecutors();
        profileObserver = userDao.loadProfile();
        loadDefaultMesh();
    }

    public static HomeRepository INSTANCE(Context context) {
        if (sDataRepository == null) {
            synchronized (HomeRepository.class) {
                if (sDataRepository == null) {
                    sDataRepository = new HomeRepository(context);
                }
            }
        }
        return sDataRepository;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 监听本地个人资料 当有变动时根据meshId获取默认的Mesh
     *
     * @return
     */
    public void loadDefaultMesh() {
        defaultMeshObserver.addSource(profileObserver, profile -> {
            if (profile != null && !TextUtils.isEmpty(profile.meshId)) {
                Log.d(TAG, "onChanged: " + profile);
                sessionId = profile.sessionid;
                RequestBody requestBody = RequestCreator.createMeshDetail(profile.meshId);
                LiveData<ApiResponse<DefaultMesh>> apiResponseLiveData = kimService.meshDetail(requestBody);
                defaultMeshObserver.addSource(apiResponseLiveData, defaultMeshApiResponse -> {
                    defaultMeshObserver.removeSource(apiResponseLiveData);
                    if (defaultMeshApiResponse.isSuccessful()) {
                        defaultMeshObserver.setValue(defaultMeshApiResponse.body);
                    } else {
                        defaultMeshObserver.setValue(new DefaultMesh());
                    }
                });
            } else {
                Log.d(TAG, "onChanged: " + null);
                defaultMeshObserver.setValue(new DefaultMesh());
            }
        });


    }


    public LiveData<ApiResponse<RequestResult>> modifyMesh(ReportMesh reportMesh) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("homeName", reportMesh.homeName);
        map.put("meshId", reportMesh.meshName);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, reportMesh.homeIcon);
        // MultipartBody.Part用来发送真实的文件名
        MultipartBody.Part icon =
                MultipartBody.Part.createFormData("homeIcon", reportMesh.homeIcon.getName(), requestFile);
        return kimService.upateMesh(icon, map);

    }


    /**
     * @param request 创建场景或情景的参数
     * @return
     */
    @Deprecated
    public LiveData<ApiResponse<AddGroupSceneResult>> createGroup(GroupSceneRequest request) {
        String id = defaultMeshObserver.getValue().id;
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", request.name);
        map.put("meshId", id);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, request.pic);
        // MultipartBody.Part用来发送真实的文件名
        MultipartBody.Part icon =
                MultipartBody.Part.createFormData("pic", request.pic.getName(), requestFile);
        return request.isGroup ? kimService.createGroup(icon, map) : kimService.createScene(icon, map);

    }

    /**
     * 分两步
     * 1.创建场景
     * 2.添加设备到场景
     *
     * @param groupRequest 添加场景的参数 含上传的device ids，上传之前就要判断是否存在
     * @return
     */
    public LiveData<AddGroupSceneResult> addGroup(GroupRequest groupRequest) {
        MediatorLiveData<AddGroupSceneResult> addGroupResult = new MediatorLiveData<>();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", groupRequest.name);
        map.put("meshId", defaultMeshObserver.getValue().id);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, groupRequest.pic);
        // MultipartBody.Part用来发送真实的文件名
        MultipartBody.Part icon = MultipartBody.Part.createFormData("pic", groupRequest.pic.getName(), requestFile);
        //先创建场景
        LiveData<ApiResponse<AddGroupSceneResult>> createGroupResponse = kimService.createGroup(icon, map);
        addGroupResult.addSource(createGroupResponse, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                addGroupResult.removeSource(createGroupResponse);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    AddGroupSceneResult addDeviceResult = apiResponse.body;
                    //这个是后台的数据库的标识
                    groupRequest.groupId = addDeviceResult.id;
//                    仅需要groupid和deviceids
                    RequestBody requestBody = RequestCreator.requestAddLampToGroup(groupRequest);
                    //开始添加设备到场景
                    LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToGroup(requestBody);
                    addGroupResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
                        @Override
                        public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                            addGroupResult.removeSource(responseLiveData);
                            if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
//                                将请求的参数返回 其包含所需要的参数
                                addGroupResult.setValue(addDeviceResult);
                            } else {
                                addGroupResult.setValue(null);
                            }
                        }
                    });
                } else {
                    addGroupResult.setValue(null);
                }
            }
        });
        return addGroupResult;

    }

    /**
     * 分两步
     * 1.创建情景
     * 2.添加设备到情景或添加场景到情景
     *
     * @param sceneRequest 添加情景的参数 含上传的device ids，上传之前就要判断是否存在
     * @return
     */
    public LiveData<SceneRequest> addScene(SceneRequest sceneRequest) {
        MediatorLiveData<SceneRequest> addSceneResult = new MediatorLiveData<>();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", sceneRequest.name);
        map.put("meshId", defaultMeshObserver.getValue().id);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, sceneRequest.pic);
        MultipartBody.Part icon = MultipartBody.Part.createFormData("pic", sceneRequest.pic.getName(), requestFile);
        //先创建情景
        LiveData<ApiResponse<AddGroupSceneResult>> createGroupResponse = kimService.createScene(icon, map);
        addSceneResult.addSource(createGroupResponse, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                addSceneResult.removeSource(createGroupResponse);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    AddGroupSceneResult addDeviceResult = apiResponse.body;
                    sceneRequest.sceneId = addDeviceResult.id;
                    sceneRequest.sceneAddress=addDeviceResult.sceneId;
                    //判断是添加设备还是添加场景
                    if (sceneRequest.isGroupSetting) {
                        addGroupToScene(addSceneResult, sceneRequest);
                    }  else {
                        addLampsToScene(addSceneResult, sceneRequest);
                    }
                } else {
                    addSceneResult.setValue(null);
                }
            }
        });
        return addSceneResult;
    }

    public LiveData<ApiResponse<RequestResult>> createDeviceSetting(String request) {
        RequestBody requestBody = RequestCreator.createDeviceSetting(request);
        return kimService.createDeviceSetting(requestBody);
    }

    //    添加场景到情景,成功后到设置页面
    private void addGroupToScene(MediatorLiveData<SceneRequest> addSceneResult, SceneRequest sceneRequest) {
        Log.d(TAG, "addGroupToScene: ");
        RequestBody requestBody = RequestCreator.requestAddGroupToScene(sceneRequest);
        //开始添加场景到情景
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addGroupToScene(requestBody);
        addSceneResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                addSceneResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
//                    getGroupFromScene(addSceneResult, sceneRequest);
                    addSceneResult.setValue(sceneRequest);
                } else {
                    addSceneResult.setValue(null);
                }
            }
        });
    }

    private void getGroupFromScene(MediatorLiveData<SceneRequest> addSceneResult, SceneRequest sceneRequest) {
        Log.d(TAG, "getGroupFromScene: ");
        RequestBody requestBody = RequestCreator.requestGroupFromScene(sceneRequest.sceneId);
        //开始添加场景到情景
        LiveData<ApiResponse<GroupList>> responseLiveData = kimService.getGroupsBySceneId(requestBody);
        addSceneResult.addSource(responseLiveData, new Observer<ApiResponse<GroupList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupList> apiResponse) {
                addSceneResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful()) {
                    //  将请求的参数返回 其包含所需要的参数
                    addSceneResult.setValue(sceneRequest);
                } else {
                    addSceneResult.setValue(null);
                }
            }
        });
    }

    //    添加灯具到情景
    private void addLampsToScene(MediatorLiveData<SceneRequest> addSceneResult, SceneRequest sceneRequest) {
        Log.d(TAG, "addLampsToScene: ");
        RequestBody requestBody = RequestCreator.requestAddLampToScene(sceneRequest);
        //开始添加设备到情景
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToScene(requestBody);
        addSceneResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                addSceneResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    //  将请求的参数返回 其包含所需要的参数
                    addSceneResult.setValue(sceneRequest);
                } else {
                    addSceneResult.setValue(null);
                }
            }
        });
    }

    private void getDeviceSetting(MediatorLiveData<SceneRequest> addSceneResult, SceneRequest sceneRequest) {
        Log.d(TAG, "getDeviceSetting: ");
        RequestBody requestBody = RequestCreator.requestDeviceSetting(sceneRequest.sceneId);
        LiveData<ApiResponse<GroupDevice>> apiResponseLiveData = kimService.getDeviceSettingByObjectId(requestBody);
        addSceneResult.addSource(apiResponseLiveData, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> groupDeviceApiResponse) {
                addSceneResult.removeSource(apiResponseLiveData);
                if (groupDeviceApiResponse.isSuccessful()) {
                    addSceneResult.setValue(sceneRequest);
                } else {
                    addSceneResult.setValue(null);
                }
            }
        });
    }

    /**
     * @param clockRequest 添加闹钟的参数 含上传的device ids，上传之前就要判断是否存在
     * @return
     */
    public LiveData<ClockResult> addClock(ClockRequest clockRequest) {
        MediatorLiveData<ClockResult> clockResult = new MediatorLiveData<>();
        Map<String, String> map = new ArrayMap<>();
        map.put("name", clockRequest.name);
        map.put("type", clockRequest.type);
        map.put("meshId", defaultMeshObserver.getValue().id);
        map.put("cycle", clockRequest.cycle);
//        RequestBody requestBody = RequestCreator.requestClock(clockRequest);
        LiveData<ApiResponse<ClockResult>> clock = kimService.createClock(map);
        clockResult.addSource(clock, new Observer<ApiResponse<ClockResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<ClockResult> apiResponse) {
                clockResult.removeSource(clock);
                //创建闹钟
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    ClockResult body = apiResponse.body;
//                    map.put("clockId", body.id);
                    clockRequest.clockId = body.id;
                    //开始添加设备到闹钟
                    RequestBody requestBody = RequestCreator.requestClock(clockRequest);
                    LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToClock(requestBody);
                    clockResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
                        @Override
                        public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                            clockResult.removeSource(responseLiveData);
                            if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                                clockResult.setValue(body);
                            } else {
                                clockResult.setValue(null);
                            }
                        }
                    });
                } else {
                    clockResult.setValue(null);
                }
            }
        });
        return clockResult;

    }

    //更新闹钟
    @Deprecated
    public LiveData<RequestResult> updateClock(ClockRequest clockRequest) {
        MediatorLiveData<RequestResult> updateResult = new MediatorLiveData<>();
        Map<String, String> map = new ArrayMap<>();
        map.put("clockId", clockRequest.clockId);
        map.put("name", clockRequest.name);
        map.put("type", clockRequest.type);
        map.put("meshId", defaultMeshObserver.getValue().id);
        map.put("cycle", clockRequest.cycle);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.updateClock(map);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    updateResult.setValue(apiResponse.body);
                } else {
                    updateResult.setValue(new RequestResult());
                }
            }
        });
        return updateResult;
    }

    /**
     * 更新闹钟和设备
     * 1.第一步更新
     * 2. 删除旧设备
     * 3.添加新设备
     * todo 优化 一步解决
     *
     * @param clockRequest
     * @return
     */
    public LiveData<ClockRequest> updateClockAndDevice(ClockRequest clockRequest) {
        Log.d(TAG, "updateClockAndDevice: ");
        MediatorLiveData<ClockRequest> updateResult = new MediatorLiveData<>();
        Map<String, String> map = new ArrayMap<>();
        map.put("clockId", clockRequest.clockId);
        map.put("name", clockRequest.name);
        map.put("type", clockRequest.type);
        map.put("meshId", defaultMeshObserver.getValue().id);
        map.put("cycle", clockRequest.cycle);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.updateClock(map);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    deleteClockDevice(updateResult, clockRequest);

                } else {
                    updateResult.setValue(null);
                }
            }
        });
        return updateResult;
    }


    private void deleteClockDevice(MediatorLiveData<ClockRequest> updateResult, ClockRequest clockRequest) {
        //使用全部灯具id 旧的不好记录
       /* Log.d(TAG, "onChanged: " + lamps.toString());
        List<String> idsToDelete = new ArrayList<>();
        List<String> idsToAdd = new ArrayList<>();
        for (Lamp lamp : lamps) {
            idsToDelete.add(lamp.getId());
            if (lamp.isSelected()) {
                idsToAdd.add(lamp.getId());
            }
        }*/
        clockRequest.deviceId = clockRequest.oldDeviceId;
        Log.d(TAG, "delete  " + clockRequest.deviceId);
        RequestBody requestDeleteClockDevice = RequestCreator.requestClock(clockRequest);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.deleteDeviceFromClock(requestDeleteClockDevice);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    clockRequest.deviceId = clockRequest.newDeviceId;
                    Log.d(TAG, "add " + clockRequest.deviceId);
                    addNewDeviceToClock(updateResult, clockRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }

    private void addNewDeviceToClock(MediatorLiveData<ClockRequest> updateResult, ClockRequest clockRequest) {
        RequestBody requestBody = RequestCreator.requestClock(clockRequest);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToClock(requestBody);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    updateResult.setValue(clockRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }


    //    更新场景 情景

    public LiveData<ApiResponse<AddGroupSceneResult>> updateGroupScene(GroupSceneRequest addGroup) {
        String id = defaultMeshObserver.getValue().id;
        boolean isGroup = addGroup.isGroup;
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", addGroup.name);
        map.put(isGroup ? "groupId" : "sceneId", isGroup ? addGroup.groupId : addGroup.sceneId);
        map.put("meshId", id);
        if (null != addGroup.pic) {
            RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, addGroup.pic);
            MultipartBody.Part icon = MultipartBody.Part.createFormData("pic", addGroup.pic.getName(), requestFile);
            return isGroup ? kimService.updateGroup(icon, map) : kimService.updateScene(icon, map);
        } else {
            return isGroup ? kimService.updateGroup(map) : kimService.updateScene(map);
        }
    }


    /**
     * 更新场景
     * 1.更新参数
     * 2.删除旧设备
     * 3.添加新设备
     *
     * @param groupRequest
     * @return
     */
    public LiveData<GroupRequest> updateGroupAndDevices(GroupRequest groupRequest) {
        MediatorLiveData<GroupRequest> updateResult = new MediatorLiveData<>();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", groupRequest.name);
        map.put("groupId", groupRequest.groupId);
        map.put("meshId", defaultMeshObserver.getValue().id);
        LiveData<ApiResponse<AddGroupSceneResult>> responseLiveData;
        if (null != groupRequest.pic) {
            RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, groupRequest.pic);
            MultipartBody.Part icon = MultipartBody.Part.createFormData("pic", groupRequest.pic.getName(), requestFile);
            responseLiveData = kimService.updateGroup(icon, map);
        } else {
            responseLiveData = kimService.updateGroup(map);
        }
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    //如果相等说明没有更新
                    if (groupRequest.oldDeviceId.equals(groupRequest.newDeviceId)) {
                        updateResult.setValue(groupRequest);
                    } else {
                        groupRequest.deviceId = groupRequest.oldDeviceId;
                        deleteGroupDevice(updateResult, groupRequest);
                    }
                } else {
                    updateResult.setValue(null);
                }
            }
        });
        return updateResult;
    }


    /**
     * 更新情景
     * 1.更新参数
     * 2.删除旧设备
     * 3.添加新设备
     *
     * @param sceneRequest
     * @return
     */
    public LiveData<SceneRequest> updateSceneAndDevices(SceneRequest sceneRequest) {
        MediatorLiveData<SceneRequest> updateResult = new MediatorLiveData<>();
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", sceneRequest.name);
        map.put("sceneId", sceneRequest.sceneId);
        map.put("meshId", defaultMeshObserver.getValue().id);
        LiveData<ApiResponse<AddGroupSceneResult>> responseLiveData;
        if (null != sceneRequest.pic) {
            RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, sceneRequest.pic);
            MultipartBody.Part icon = MultipartBody.Part.createFormData("pic", sceneRequest.pic.getName(), requestFile);
            responseLiveData = kimService.updateScene(icon, map);
        } else {
            responseLiveData = kimService.updateScene(map);
        }
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    //如果相等说明没有更新
                    if (sceneRequest.oldDeviceId.equals(sceneRequest.newDeviceId)) {
                        //灯具设置页面
                        updateResult.setValue(sceneRequest);
                    } else {
                        sceneRequest.deviceId = sceneRequest.oldDeviceId;
                        deleteSceneDevice(updateResult, sceneRequest);
                    }
                } else {
                    updateResult.setValue(null);
                }
            }
        });
        return updateResult;
    }

    private void deleteGroupDevice(MediatorLiveData<GroupRequest> updateResult, GroupRequest groupRequest) {
        Log.d(TAG, "deleteGroupDevice:" + groupRequest);
        RequestBody requestBody = RequestCreator.requestDeleteDeviceInGroup(groupRequest.groupId, groupRequest.deviceId);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.deleteDeviceFromGroup(requestBody);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    groupRequest.deviceId = groupRequest.newDeviceId;
                    Log.d(TAG, "add " + groupRequest.deviceId);
                    addNewDeviceToGroup(updateResult, groupRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }

    private void deleteSceneDevice(MediatorLiveData<SceneRequest> updateResult, SceneRequest sceneRequest) {
        Log.d(TAG, "deleteSceneDevice:" + sceneRequest);
        RequestBody requestBody = RequestCreator.requestDeleteDeviceInScene(sceneRequest);
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.deleteDeviceFromScene(requestBody);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    sceneRequest.deviceId = sceneRequest.newDeviceId;
                    Log.d(TAG, "add SceneDevice" + sceneRequest.deviceId);
                    addNewDeviceToScene(updateResult, sceneRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }

    private void addNewDeviceToScene(MediatorLiveData<SceneRequest> updateResult, SceneRequest sceneRequest) {
        Log.d(TAG, "addNewDeviceToScene: ");
        RequestBody requestBody = RequestCreator.requestAddLampToScene(sceneRequest);
        //开始添加设备到场景
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToScene(requestBody);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    //     将请求的参数返回 其包含所需要的参数
                    updateResult.setValue(sceneRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }

    private void addNewDeviceToGroup(MediatorLiveData<GroupRequest> updateResult, GroupRequest groupRequest) {
        RequestBody requestBody = RequestCreator.requestAddLampToGroup(groupRequest);
        //开始添加设备到场景
        LiveData<ApiResponse<RequestResult>> responseLiveData = kimService.addDeviceToGroup(requestBody);
        updateResult.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                updateResult.removeSource(responseLiveData);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    //     将请求的参数返回 其包含所需要的参数
                    updateResult.setValue(groupRequest);
                } else {
                    updateResult.setValue(null);
                }
            }
        });
    }


    public LiveData<List<Lamp>> getDevicesInGroup(boolean group, String id) {
        MediatorLiveData<List<Lamp>> lamps = new MediatorLiveData<>();
        RequestBody requestBody = group ? RequestCreator.requestGroupDevices(id) : RequestCreator.requestSceneDevices(id);
        LiveData<ApiResponse<GroupDevice>> devicesByGroupId = group ? kimService.getDevicesByGroupId(requestBody) : kimService.getDevicesBySceneId(requestBody);
        lamps.addSource(devicesByGroupId, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                lamps.removeSource(devicesByGroupId);
                if (apiResponse != null && apiResponse.isSuccessful()) {
                    GroupDevice body = apiResponse.body;
                    List<Lamp> list = body.getList();
                    lamps.setValue(list);
                }
            }
        });
        return lamps;
    }


    public LiveData<List<Lamp>> getDevicesInClock(String id) {
        MediatorLiveData<List<Lamp>> lamps = new MediatorLiveData<>();
        RequestBody requestBody = RequestCreator.requestClockDevices(id);
        LiveData<ApiResponse<GroupDevice>> devices = kimService.getDevicesByClockId(requestBody);
        lamps.addSource(devices, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                lamps.removeSource(devices);
                if (apiResponse != null && apiResponse.isSuccessful()) {
                    GroupDevice body = apiResponse.body;
                    List<Lamp> list = body.getList();
                    lamps.setValue(list);
                }
            }
        });
        return lamps;
    }

    /*获取情景景下的设备*/
    public LiveData<ApiResponse<GroupDevice>> getDevicesInScene(String sceneId) {
        RequestBody requestBody = RequestCreator.requestSceneDevices(sceneId);
        return kimService.getDevicesBySceneId(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> deleteGroup(String groupId) {
        RequestBody requestBody = RequestCreator.requestDeleteGroup(groupId);
        return kimService.deleteGroup(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> deleteScene(String sceneId) {
        RequestBody requestBody = RequestCreator.requestDeleteScene(sceneId);
        return kimService.deleteScene(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> deleteClock(String id) {
        RequestBody requestBody = RequestCreator.requestDeleteClock(id);
        return kimService.deleteClock(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> switchClock(Clock clock) {
        //1 开 0-关  使用1-当前状态 来切换
        RequestBody requestBody = RequestCreator.requestSwitchClock(clock.getId(), 1 - clock.getIsOpen());
        return kimService.switchClock(requestBody);
    }


    /**
     * 添加蓝牙网络 成功后返回蓝牙列表
     *
     * @param reportMesh
     * @return
     */
    public LiveData<Resource<AddMeshResult>> addMesh(ReportMesh reportMesh) {
        MediatorLiveData<Resource<AddMeshResult>> result = new MediatorLiveData<>();
//        设置为加载状态
        result.setValue(Resource.loading(null));
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("homeName", reportMesh.homeName);
        map.put("meshName", reportMesh.meshName);
        map.put("meshPassword", reportMesh.meshPassword);
        map.put("othersId", profileObserver.getValue().userId);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, reportMesh.homeIcon);
        // MultipartBody.Part用来发送真实的文件名
        MultipartBody.Part icon =
                MultipartBody.Part.createFormData("homeIcon", reportMesh.homeIcon.getName(), requestFile);
        LiveData<ApiResponse<AddMeshResult>> apiResponseLiveData = kimService.reportBleMesh(icon, map);
        result.addSource(apiResponseLiveData, requestResultApiResponse -> {
//                监听是永久的，不会自动移除，一定要手动移除 防止重复监听
            result.removeSource(apiResponseLiveData);
//                Http请求是否成功
            if (requestResultApiResponse.isSuccessful()) {
//                    返回数据是否正确
                if (requestResultApiResponse.body.succeed()) {
                    //新加的mesh成为默认的 所以需要 更新数据库 获取详细的mesh
                    updateMeshId(requestResultApiResponse.body.meshId);
                    result.setValue(Resource.success(requestResultApiResponse.body, requestResultApiResponse.body.resultMsg));
                } else {
                    result.setValue(Resource.error(null, requestResultApiResponse.body.resultMsg));
                }
            } else {
                result.setValue(Resource.error(null, requestResultApiResponse.errorMsg));
            }
        });

        return result;

    }


    /**
     * 设置默认的mesh
     *
     * @param meshId
     * @return
     */
    public LiveData<Resource<Boolean>> setDefaultMesh(String meshId) {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        RequestBody requestBody = RequestCreator.createMeshDetail(meshId);
        result.setValue(Resource.loading(false));
        LiveData<ApiResponse<RequestResult>> response = kimService.setDefaultMesh(requestBody);
        result.addSource(response, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                result.removeSource(response);
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {
                        result.setValue(Resource.success(true, apiResponse.body.resultMsg));
                        updateMeshId(meshId);
                    } else {
                        result.setValue(Resource.error(false, apiResponse.body.resultMsg));
                    }
                } else {
                    result.setValue(Resource.error(false, apiResponse.errorMsg));
                }
            }
        });


        return result;
    }

    /**
     * 删除mesh  成功后需要删除本地数据
     *
     * @param meshId
     * @return
     */
    public LiveData<Resource<Boolean>> deleteMesh(String meshId) {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        String userId = profileObserver.getValue().userId;
        RequestBody requestBody = RequestCreator.createDeleteMesh(meshId, userId);
        LiveData<ApiResponse<RequestResult>> response = kimService.deleteMesh(requestBody);
        result.addSource(response, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                result.removeSource(response);
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {
                        result.setValue(Resource.success(true, apiResponse.body.resultMsg));
                        userDao.deleteMeshById(meshId);
                    } else {
                        result.setValue(Resource.error(false, apiResponse.body.resultMsg));
                    }
                } else {
                    result.setValue(Resource.error(false, apiResponse.errorMsg));
                }
            }
        });

        return result;
    }


    public LiveData<ApiResponse<RequestResult>> shareMesh(RequestBody requestBody) {
        return kimService.shareMesh(requestBody);
    }

    /**
     * 更新数据库的当前的meshId 会导致 loadDefaultMesh方法的调用
     *
     * @param meshId
     */
    private void updateMeshId(String meshId) {
        userDao.updateMeshId(profileObserver.getValue().phone, meshId);
    }

    //    获取灯具列表 todo 优化
    public LiveData<ApiResponse<LampList>> getLampList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, pageNo);
        return kimService.deviceList(requestBody);
    }


    public LiveData<ApiResponse<LampList>> lampList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, pageNo);
        return kimService.deviceList(requestBody);
    }

    //加载场景下的设备 如果是修改标记已选择设备
    public LiveData<List<Lamp>> loadLampForGroup(String groupId) {
        MediatorLiveData<List<Lamp>> data = new MediatorLiveData<>();
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, 1);
        LiveData<ApiResponse<LampList>> apiResponseLiveData = kimService.deviceList(requestBody);
        data.addSource(apiResponseLiveData, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> lampListApiResponse) {
                data.removeSource(apiResponseLiveData);
                if (lampListApiResponse != null && lampListApiResponse.isSuccessful() && lampListApiResponse.body != null) {
                    List<Lamp> list = lampListApiResponse.body.getList();
                    //为空 说明是添加
                    if (TextUtils.isEmpty(groupId)) {
                        if (list != null) {
                            for (Lamp lamp : list) {
                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                            }
                            data.setValue(list);
                        } else {
                            data.setValue(null);
                        }
                    } else {
                        RequestBody requestBody = RequestCreator.requestGroupDevices(groupId);
                        LiveData<ApiResponse<GroupDevice>> devices = kimService.getDevicesByGroupId(requestBody);
                        data.addSource(devices, new Observer<ApiResponse<GroupDevice>>() {
                            @Override
                            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                                data.removeSource(devices);
                                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body != null) {
                                    List<Lamp> selectedList = apiResponse.body.getList();
                                    //标记已选择数据
                                    if (selectedList != null) {
                                        for (Lamp lamp : list) {
                                            if (selectedList.contains(lamp)) {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                                            } else {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                                            }
                                        }
                                    }
                                }
                                data.setValue(list);
                            }
                        });
                    }
                }

            }
        });
        return data;

    } //加载场景下的设备 如果是修改标记已选择设备


    //加载情景景下的设备 如果是修改标记已选择设备
    public LiveData<List<Lamp>> loadLampForScene(String sceneId) {
        MediatorLiveData<List<Lamp>> data = new MediatorLiveData<>();
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, 1);
        LiveData<ApiResponse<LampList>> apiResponseLiveData = kimService.deviceList(requestBody);
        data.addSource(apiResponseLiveData, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> lampListApiResponse) {
                data.removeSource(apiResponseLiveData);
                if (lampListApiResponse != null && lampListApiResponse.isSuccessful() && lampListApiResponse.body != null) {
                    List<Lamp> list = lampListApiResponse.body.getList();
                    //为空 说明是添加
                    if (TextUtils.isEmpty(sceneId)) {
                        if (list != null) {
                            for (Lamp lamp : list) {
                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                            }
                            data.setValue(list);
                        } else {
                            data.setValue(null);
                        }
                    } else {
                        RequestBody requestBody = RequestCreator.requestSceneDevices(sceneId);
                        LiveData<ApiResponse<GroupDevice>> devices = kimService.getDevicesBySceneId(requestBody);
                        data.addSource(devices, new Observer<ApiResponse<GroupDevice>>() {
                            @Override
                            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                                data.removeSource(devices);
                                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body != null) {
                                    List<Lamp> selectedList = apiResponse.body.getList();
                                    //标记已选择数据
                                    if (selectedList != null) {
                                        for (Lamp lamp : list) {
                                            if (selectedList.contains(lamp)) {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                                            } else {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                                            }
                                        }
                                    }
                                }
                                data.setValue(list);
                            }
                        });
                    }
                }

            }
        });
        return data;

    }

    /**
     * 为闹钟模块加载灯具
     *
     * @param clockId 如果为空 怎加载当前mesh下的灯具，不为空还要加载此clock下的灯具 供用户在两个页面选择
     * @return
     */
    public LiveData<List<Lamp>> loadLampsForClock(String clockId) {
        Log.d(TAG, "loadLampsForClock() called with: clockId = [" + clockId + "]");
        MediatorLiveData<List<Lamp>> data = new MediatorLiveData<>();
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, 1);
        LiveData<ApiResponse<LampList>> apiResponseLiveData = kimService.deviceList(requestBody);
        data.addSource(apiResponseLiveData, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> lampListApiResponse) {
                data.removeSource(apiResponseLiveData);
                if (lampListApiResponse != null && lampListApiResponse.isSuccessful() && lampListApiResponse.body != null) {
                    List<Lamp> list = lampListApiResponse.body.getList();
                    //为空 说明是添加
                    if (TextUtils.isEmpty(clockId)) {
                        if (list != null) {
                            for (Lamp lamp : list) {
                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                            }
                            data.setValue(list);
                        } else {
                            data.setValue(null);
                        }
                    } else {
                        RequestBody requestBody = RequestCreator.requestClockDevices(clockId);
                        LiveData<ApiResponse<GroupDevice>> devices = kimService.getDevicesByClockId(requestBody);
                        data.addSource(devices, new Observer<ApiResponse<GroupDevice>>() {
                            @Override
                            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                                data.removeSource(devices);
                                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body != null) {
                                    List<Lamp> selectedList = apiResponse.body.getList();
                                    //标记已选择数据
                                    if (selectedList != null) {
                                        for (Lamp lamp : list) {
                                            if (selectedList.contains(lamp)) {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                                            } else {
                                                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
                                            }
                                        }
                                    }
                                }
                                data.setValue(list);
                            }
                        });
                    }
                }

            }
        });
        return data;

    }


    //获取hub列表
    public LiveData<ApiResponse<HubList>> getHubList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestHubList(meshId, pageNo);
        return kimService.hubList(requestBody);
    }

    //获取场景列表
    public LiveData<ApiResponse<GroupList>> getGroupList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestHubList(meshId, pageNo);
        return kimService.groupList(requestBody);
    }


    public LiveData<ApiResponse<SceneList>> getSceneList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestHubList(meshId, pageNo);
        return kimService.getSceneList(requestBody);
    }

    public LiveData<ApiResponse<ClockList>> getClockList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestHubList(meshId, pageNo);
        return kimService.getClockList(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> reportDevice(Map<String, String> map) {
        map.put("meshId", defaultMeshObserver.getValue().id);
        RequestBody requestBody = RequestCreator.requestAdLamp(map);
        return kimService.reportDevice(requestBody);
    }


    //删除灯具
    public LiveData<Resource<Boolean>> deleteDevice(String deviceId) {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        RequestBody requestBody = RequestCreator.requestDeleteLamp(deviceId);
        LiveData<ApiResponse<RequestResult>> response = kimService.deleteDevice(requestBody);
        result.addSource(response, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                result.removeSource(response);
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {
                        result.setValue(Resource.success(true, apiResponse.body.resultMsg));
//                        userDao.deleteMeshById(meshId);
                    } else {
                        result.setValue(Resource.error(false, apiResponse.body.resultMsg));
                    }
                } else {
                    result.setValue(Resource.error(false, apiResponse.errorMsg));
                }
            }
        });

        return result;
    }

    public LiveData<ApiResponse<RequestResult>> reportHub(AddHubRequest addHubRequest) {
        DefaultMesh defaultMesh = defaultMeshObserver.getValue();
        addHubRequest.meshId = defaultMesh.id;
        addHubRequest.meshName = defaultMesh.aijiaName;
        addHubRequest.password = defaultMesh.password;
        RequestBody requestBody = RequestCreator.createAddHub(addHubRequest);
        return kimService.reportHub(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> getDeviceId() {
        DefaultMesh defaultMesh = defaultMeshObserver.getValue();
        RequestBody requestBody = RequestCreator.requestDeviceId(defaultMesh.id);
        Log.d(TAG, "getDeviceId: " + defaultMesh.id);
        return kimService.getDeviceId(requestBody);

    }


    /**
     * 先从本地获取，再判断是否需要从网络获取 ，从网络获取失败使用本地数据，否则使用网络数据
     * <p>
     * 一次性请求足够多的 不再分页 没有必要
     *
     * @return mesh列表  找到默认的 需要显示在首页
     */
    public LiveData<Resource<List<Mesh>>> loadMeshList() {
        boolean needLoadFromLocal = true;
        MediatorLiveData<Resource<List<Mesh>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
//        加载本地
        LiveData<List<Mesh>> dbSource = userDao.loadAllMesh();
//        监听本地加载结果
        result.addSource(dbSource, meshes -> {
            result.removeSource(dbSource);
//                如果需要从网络获取
            if (needLoadFromLocal) {
                //先显示本地内容和加载状态
                result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));

                RequestBody requestBody = RequestCreator.createPage(1, 40);
                LiveData<ApiResponse<MeshList>> apiResponse = kimService.meshList(requestBody);
//                监听网络请求
                result.addSource(apiResponse, response -> {
                    result.removeSource(dbSource);
                    result.removeSource(apiResponse);
                    //加载成功
                    if (response.isSuccessful()) {
//                            在子线程中保存到本地
                        executors.diskIO().execute(() -> {
                            List<Mesh> list = response.body.getList();
                            if (list != null && !list.isEmpty()) {
                                userDao.insertMeshes(list);
                            } else {
                                userDao.deleteAllMeshes();
                            }
//                            在主线程中从数据库获取
                            executors.mainThread().execute(() -> {
                                LiveData<List<Mesh>> local = userDao.loadAllMesh();
                                result.addSource(local, newData -> {
                                    //如果还想监听数据库变化 可以不用移除
                                    result.removeSource(local);
                                    result.setValue(Resource.success(newData, ""));
                                });
                            });
                        });

                    } else {
                        result.addSource(dbSource, newData -> result.setValue(Resource.error(newData, response.errorMsg)));
                    }
                });
            } else {
                result.addSource(dbSource, newData -> result.setValue(Resource.success(newData, "")));
            }
        });

        return result;
    }

    public LiveData<List<Mesh>> loadMyMeshFromLocal() {
        MediatorLiveData<List<Mesh>> result = new MediatorLiveData<>();
        RequestBody requestBody = RequestCreator.createPage(1, 40);
        LiveData<ApiResponse<MeshList>> apiResponse = kimService.meshList(requestBody);
        result.addSource(apiResponse, new Observer<ApiResponse<MeshList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<MeshList> response) {
                result.removeSource(apiResponse);
                if (response.isSuccessful()) {
                    List<Mesh> list = response.body.getList();
                    if (list != null && !list.isEmpty()) {
                        userDao.insertMeshes(list);
                    } else {
                        userDao.deleteAllMeshes();
                    }
//                            在主线程中从数据库获取
                    executors.mainThread().execute(() -> {
                        String userId = profileObserver.getValue().userId;
                        LiveData<List<Mesh>> local = userDao.loadMyMesh(userId);
                        result.addSource(local, newData -> {
                            //如果还想监听数据库变化 可以不用移除
                            result.removeSource(local);
                            result.setValue(newData);
                        });
                    });

                }

            }
        });
        return result;

    }


}
