package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;


public class SceneViewModel extends AndroidViewModel {

    private final HomeRepository repository;
    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<ApiResponse<LampList>> lampListObserver;

    // 创建场景请求
    public MutableLiveData<AddGroup> addGroupRequest = new MutableLiveData<>();
    // 创建场景监听
    public final LiveData<ApiResponse<AddGroupResult>> addGroupObserver;

    //
    public MutableLiveData<Pair<String,String>> addDeviceToGroupRequest = new MutableLiveData<>();

    public final LiveData<ApiResponse<RequestResult>> addDeviceToGroupObserver;


    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::lampList);
        addGroupObserver=Transformations.switchMap(addGroupRequest, repository::createGroup);
        addDeviceToGroupObserver=Transformations.switchMap(addDeviceToGroupRequest, repository::addDeviceToGroup);


    }


}
