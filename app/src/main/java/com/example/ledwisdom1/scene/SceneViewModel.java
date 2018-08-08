package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.common.BindingAdapters;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 情景ViewModel
 */
public class SceneViewModel extends AndroidViewModel {
    private static final String TAG = SceneViewModel.class.getSimpleName();
    private final HomeRepository repository;

    //    情景列表请求
    public final MutableLiveData<Integer> sceneListRequest = new MutableLiveData<>();

    public final LiveData<Resource<List<Scene>>> sceneListObserver;

    // 场景列表请求
    public MutableLiveData<Integer> groupListRequest = new MutableLiveData<>();
    //场景列表监听
    public final LiveData<ApiResponse<GroupList>> groupListObserver;


    // lamp列表请求
    public MutableLiveData<String> lampListRequest = new MutableLiveData<>();

    // lamp列表监听
    public final LiveData<List<Lamp>> lampListObserver;

    // 创建情景请求
    public MutableLiveData<SceneRequest> addSceneRequest = new MutableLiveData<>();
    // 创建情景结果监听
    public final LiveData<SceneRequest> addSceneObserver;

    // 设备参数
    public MutableLiveData<String> deviceSettingRequest = new MutableLiveData<>();
    // 设置设备参数结果监听
    public final LiveData<ApiResponse<RequestResult>> deviceSettingObserver;


    // 修改情景请求
    public MutableLiveData<SceneRequest> updateSceneRequest = new MutableLiveData<>();
    // 修改情景监听
    public final LiveData<SceneRequest> updateSceneObserver;

    public final LiveData<SceneRequest> deleteSceneObserver ;


    public MutableLiveData<SceneRequest> deleteSceneRequest = new MutableLiveData<>();



    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        sceneListObserver=Transformations.switchMap(sceneListRequest, repository::getSceneList);
        groupListObserver= Transformations.switchMap(groupListRequest, repository::getGroupList);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::loadLampForScene);
        updateSceneObserver =Transformations.switchMap(updateSceneRequest, repository::updateSceneAndDevices);
        addSceneObserver=Transformations.switchMap(addSceneRequest, repository::addScene);
        deviceSettingObserver = Transformations.switchMap(deviceSettingRequest, repository::createDeviceSetting);
        deleteSceneObserver = Transformations.switchMap(deleteSceneRequest, repository::deleteScene);
    }




    //获取选中设备的id
    public List<String> getSelectedLampIds() {
        List<String> deviceIds = new ArrayList<>();
        List<Lamp> allLamps = lampListObserver.getValue();
        if (allLamps != null) {
            for (Lamp lamp : allLamps) {
                if (BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get()) {
                    deviceIds.add(lamp.getId());
                }
            }
        }
        return deviceIds;
    }


    /**
     * 删除情景
     */
   /* public void deleteScene(SceneRequest sceneRequest) {
        LiveData<ApiResponse<RequestResult>> responseLiveData =repository.deleteScene(sceneRequest.sceneId);
        deleteSceneObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteSceneObserver.removeSource(responseLiveData);
                if (null != apiResponse && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    repository.deleteSceneById(sceneRequest.sceneId);
                    LightCommandUtils.deleteAllDevicesFromScene(sceneRequest.sceneAddress);
                    deleteSceneObserver.setValue(apiResponse.body);
                } else {
                    deleteSceneObserver.setValue(null);
                }
            }
        });


    }
*/



}
