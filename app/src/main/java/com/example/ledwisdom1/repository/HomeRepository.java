package com.example.ledwisdom1.repository;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.KimAscendService;
import com.example.ledwisdom1.api.NetWork;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.AppExecutors;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.database.SmartLightDataBase;
import com.example.ledwisdom1.database.UserDao;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.Group;
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
import com.example.ledwisdom1.scene.GroupSceneRequest;
import com.example.ledwisdom1.scene.SceneList;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.RequestCreator;

import java.util.List;

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
    ;

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

    /**
     * 监听本地个人资料 当有变动时根据meshId获取默认的Mesh
     *
     * @return
     */
    public void loadDefaultMesh() {
        defaultMeshObserver.addSource(profileObserver, profile -> {
            if (profile != null && !TextUtils.isEmpty(profile.meshId)) {
                Log.d(TAG, "onChanged: " + profile);
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
     *
     * @param request  创建场景或情景的参数
     * @return
     */
    public LiveData<ApiResponse<AddGroupSceneResult>> createGroup(GroupSceneRequest request) {
        String id = defaultMeshObserver.getValue().id;
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", request.name);
        map.put("meshId", id);
        RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, request.pic);
        // MultipartBody.Part用来发送真实的文件名
        MultipartBody.Part icon =
                MultipartBody.Part.createFormData("pic", request.pic.getName(), requestFile);
        return request.isGroup ?kimService.createGroup(icon, map):kimService.createScene(icon,map);

    }


//    更新场景 情景
    public LiveData<ApiResponse<AddGroupSceneResult>> updateGroupScene(GroupSceneRequest addGroup) {
        String id = defaultMeshObserver.getValue().id;
        boolean isGroup=addGroup.isGroup;
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("name", addGroup.name);
        map.put(isGroup?"groupId":"sceneId", isGroup?addGroup.groupId:addGroup.sceneId);
        map.put("meshId", id);
        if (null != addGroup.pic) {
            RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, addGroup.pic);
            MultipartBody.Part icon =MultipartBody.Part.createFormData("pic", addGroup.pic.getName(), requestFile);
            return isGroup?kimService.updateGroup(icon, map):kimService.updateScene(icon,map);
        }else{
            return isGroup?kimService.updateGroup(map):kimService.updateScene(map);
        }
    }




    /*获取场景详情*/
    public LiveData<ApiResponse<Group>> getGroupDetail(String groupId) {
        RequestBody requestBody = RequestCreator.requestGroupDetail(groupId);
        return kimService.getGroupById(requestBody);
    }

    public LiveData<List<Lamp>> getDevicesInGroup(boolean group,String id) {
        MediatorLiveData<List<Lamp>> lamps=new MediatorLiveData<>();
        RequestBody requestBody =group? RequestCreator.requestGroupDevices(id):RequestCreator.requestSceneDevices(id);
        LiveData<ApiResponse<GroupDevice>> devicesByGroupId =group? kimService.getDevicesByGroupId(requestBody): kimService.getDevicesBySceneId(requestBody);
        lamps.addSource(devicesByGroupId, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                lamps.removeSource(devicesByGroupId);
                if (apiResponse!=null&&apiResponse.isSuccessful()) {
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


    @Deprecated
    public LiveData<ApiResponse<RequestResult>> addDeviceToGroup(Pair<String, String> pair) {
        RequestBody requestBody = RequestCreator.requestAddLampToGroup(pair.first, pair.second);
        return kimService.addDeviceToGroup(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> addDeviceToGroupScene(GroupSceneRequest request) {
        RequestBody requestBody = RequestCreator.requestAddLampToGroupScene(request);
        return request.isGroup ?kimService.addDeviceToGroup(requestBody):kimService.addDeviceToScene(requestBody);
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
    public LiveData<ApiResponse<LampList>> getLampList(RequestBody requestBody) {
        return kimService.deviceList(requestBody);
    }


    public LiveData<ApiResponse<LampList>> lampList(int pageNo) {
        String meshId = profileObserver.getValue().meshId;
        RequestBody requestBody = RequestCreator.requestLampList(meshId, pageNo);
        return kimService.deviceList(requestBody);
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



    public LiveData<ApiResponse<RequestResult>> reportDevice(RequestBody requestBody) {
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
        addHubRequest.meshId=defaultMesh.id;
        addHubRequest.meshName=defaultMesh.aijiaName;
        addHubRequest.password=defaultMesh.password;
        RequestBody requestBody = RequestCreator.createAddHub(addHubRequest);
        return kimService.reportHub(requestBody);
    }

    //    加载mesh列表
    public LiveData<Resource<List<Mesh>>> loadMeshListFromRemote() {
        MediatorLiveData<Resource<List<Mesh>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        boolean needLoadFromLocal = true;
//        从数据库加载
        LiveData<List<Mesh>> local = userDao.loadAllMesh();
        result.addSource(local, meshes -> {
            result.removeSource(local);
//            如果需要从网络加载
            if (needLoadFromLocal) {
                RequestBody requestBody = RequestCreator.createPage(1, 40);
                LiveData<ApiResponse<MeshList>> remote = kimService.meshList(requestBody);
                result.addSource(local, newData -> {
                    result.setValue(Resource.loading(newData));
                });
                result.addSource(remote, newData -> {
                    result.removeSource(local);
                    result.removeSource(remote);
                    if (newData.isSuccessful()) {
                        MeshList body = newData.body;

                    }
                });

            } else {
                result.addSource(local, newData -> {
                    result.setValue(Resource.success(newData, ""));
                });
            }

        });


        return result;
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
