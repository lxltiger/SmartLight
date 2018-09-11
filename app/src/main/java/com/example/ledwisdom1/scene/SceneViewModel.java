package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.repository.HomeRepository;

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

    // 修改情景请求
    public MutableLiveData<SceneRequest> updateSceneRequest = new MutableLiveData<>();
    // 修改情景监听
    public final LiveData<SceneRequest> updateSceneObserver;

    public final LiveData<SceneRequest> deleteSceneObserver ;


    public MutableLiveData<SceneRequest> deleteSceneRequest = new MutableLiveData<>();

    //已设置的灯具
    public MutableLiveData<String> settingLampRequest = new MutableLiveData<>();

    public LiveData<List<DeviceSetting>> settingObserver;
    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        sceneListObserver=Transformations.switchMap(sceneListRequest, repository::getSceneList);
        groupListObserver= Transformations.switchMap(groupListRequest, repository::getGroupList);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::loadLampForScene);
        updateSceneObserver =Transformations.switchMap(updateSceneRequest, repository::updateSceneAndDevices);
        addSceneObserver=Transformations.switchMap(addSceneRequest, repository::addScene);
        deleteSceneObserver = Transformations.switchMap(deleteSceneRequest, repository::deleteScene);
        settingObserver = Transformations.switchMap(settingLampRequest, repository::getDeviceSetting);

    }



}
