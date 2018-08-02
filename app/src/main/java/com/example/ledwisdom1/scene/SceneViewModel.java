package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.utils.BindingAdapters;

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

    public final LiveData<ApiResponse<SceneList>> sceneListObserver;

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

    public final MediatorLiveData<RequestResult> deleteSceneObserver =new MediatorLiveData<>();



    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        sceneListObserver=Transformations.switchMap(sceneListRequest, repository::getSceneList);
        groupListObserver= Transformations.switchMap(groupListRequest, repository::getGroupList);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::loadLampForScene);
        updateSceneObserver =Transformations.switchMap(updateSceneRequest, repository::updateSceneAndDevices);
        addSceneObserver=Transformations.switchMap(addSceneRequest, repository::addScene);
        deviceSettingObserver = Transformations.switchMap(deviceSettingRequest, repository::createDeviceSetting);

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
     */
    public void deleteScene(String sceneId) {
        LiveData<ApiResponse<RequestResult>> responseLiveData =repository.deleteScene(sceneId);
        deleteSceneObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteSceneObserver.removeSource(responseLiveData);
                if (null != apiResponse && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    deleteSceneObserver.setValue(apiResponse.body);
                } else {
                    deleteSceneObserver.setValue(null);
                }
            }
        });


    }




}
