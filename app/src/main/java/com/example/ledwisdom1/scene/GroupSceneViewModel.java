package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.utils.AbsentLiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景 情景的ViewModel
 */
public class GroupSceneViewModel extends AndroidViewModel {
    private static final String TAG = "GroupSceneViewModel";
    private final HomeRepository repository;

    //场景  更新使用
    public MutableLiveData<Group> group = new MutableLiveData<>();
    //情景 更新使用
    public MutableLiveData<Scene> scene = new MutableLiveData<>();

    //情景请求参数的封装
    public GroupSceneRequest groupSceneRequest = new GroupSceneRequest();

    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<ApiResponse<LampList>> lampListObserver;

    // 创建场景请求
    public MutableLiveData<GroupSceneRequest> addGroupRequest = new MutableLiveData<>();
    // 创建场景监听
    public final LiveData<ApiResponse<AddGroupSceneResult>> addGroupObserver;

    // 修改场景请求
    public MutableLiveData<GroupSceneRequest> updateGroupRequest = new MutableLiveData<>();
    // 修改场景监听
    public final LiveData<ApiResponse<AddGroupSceneResult>> updateGroupObserver;

    @Deprecated
    public MutableLiveData<Pair<String,String>> addDeviceToGroupRequest = new MutableLiveData<>();

    public final LiveData<ApiResponse<RequestResult>> addDeviceToGroupObserver;
    /**
     * 添加设备到场景或情景的请求，封装了所有参数 和区分场景和情景的标识
     */
    public MutableLiveData<GroupSceneRequest> addDeviceToGroupSceneRequest = new MutableLiveData<>();

    public final LiveData<ApiResponse<RequestResult>> addDeviceToGroupSceneObserver;

//    public final LiveData<ApiResponse<Group>> groupDetailObserver;
    public final LiveData<ApiResponse<GroupDevice>> groupDevicesObserver;
    public final LiveData<ApiResponse<GroupDevice>> sceneDevicesObserver;

    public final MediatorLiveData<ApiResponse<RequestResult>> deleteGroupObserver=new MediatorLiveData<>();
    /**
     * 场景或情景中添加或修改灯所有的数据
     * 选中的需要组灯 没有选中的从组中去除
     *
     */
    public final List<Lamp> groupSceneLamps=new ArrayList<>();


    public final MutableLiveData<Integer> sceneListRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<SceneList>> sceneListObserver;


    public GroupSceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::lampList);
        addGroupObserver=Transformations.switchMap(addGroupRequest, new Function<GroupSceneRequest, LiveData<ApiResponse<AddGroupSceneResult>>>() {
            @Override
            public LiveData<ApiResponse<AddGroupSceneResult>> apply(GroupSceneRequest request) {
                if (request == null) {
                    Log.d(TAG, "apply: GroupSceneRequest null");
                    return AbsentLiveData.create();
                }
                return repository.createGroup(request);
            }
        });
        addDeviceToGroupObserver=Transformations.switchMap(addDeviceToGroupRequest, repository::addDeviceToGroup);

        addDeviceToGroupSceneObserver=Transformations.switchMap(addDeviceToGroupSceneRequest, new Function<GroupSceneRequest, LiveData<ApiResponse<RequestResult>>>() {
            @Override
            public LiveData<ApiResponse<RequestResult>> apply(GroupSceneRequest input) {
                if (input == null) {
                    Log.d(TAG, "apply: addDeviceToGroupSceneObserver null");
                    return AbsentLiveData.create();
                }
                return repository.addDeviceToGroupScene(input);
            }
        });
        updateGroupObserver =Transformations.switchMap(updateGroupRequest, repository::updateGroupScene);


//        场景已添加的设备
        groupDevicesObserver = Transformations.switchMap(group, new Function<Group, LiveData<ApiResponse<GroupDevice>>>() {
            @Override
            public LiveData<ApiResponse<GroupDevice>> apply(Group group) {
                if (null==group) {
                    return AbsentLiveData.create();
                }
                return repository.getDevicesInGroup(group.getId());
            }
        });

        //情景已添加的设备
        sceneDevicesObserver = Transformations.switchMap(scene, new Function<Scene, LiveData<ApiResponse<GroupDevice>>>() {
            @Override
            public LiveData<ApiResponse<GroupDevice>> apply(Scene scene) {
                if (null==scene) {
                    return AbsentLiveData.create();
                }
                return repository.getDevicesInScene(scene.getId());
            }
        });

        sceneListObserver=Transformations.switchMap(sceneListRequest, repository::getSceneList);

    }

    /**
     * 删除场景或情景
     * @param isGroup 是否是场景
     */
    public void deleteGroup(boolean isGroup) {
        LiveData<ApiResponse<RequestResult>> responseLiveData = isGroup ? repository.deleteGroup(group.getValue().getId()) : repository.deleteScene(scene.getValue().getId());
        deleteGroupObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteGroupObserver.removeSource(responseLiveData);
                deleteGroupObserver.setValue(apiResponse);
            }
        });


    }




}
