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
import android.util.Pair;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.utils.AbsentLiveData;

/**
 * 场景 情景的ViewModel
 */
public class SceneViewModel extends AndroidViewModel {

    private final HomeRepository repository;

    //场景
    public MutableLiveData<Group> group = new MutableLiveData<>();

    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<ApiResponse<LampList>> lampListObserver;

    // 创建场景请求
    public MutableLiveData<GroupRequest> addGroupRequest = new MutableLiveData<>();
    // 创建场景监听
    public final LiveData<ApiResponse<AddGroupResult>> addGroupObserver;

    // 修改场景请求
    public MutableLiveData<GroupRequest> updateGroupRequest = new MutableLiveData<>();
    // 修改场景监听
    public final LiveData<ApiResponse<AddGroupResult>> updateGroupObserver;

    //
    public MutableLiveData<Pair<String,String>> addDeviceToGroupRequest = new MutableLiveData<>();

    public final LiveData<ApiResponse<RequestResult>> addDeviceToGroupObserver;

//    public final LiveData<ApiResponse<Group>> groupDetailObserver;
    public final LiveData<ApiResponse<GroupDevice>> groupDevicesObserver;

    public final MediatorLiveData<ApiResponse<RequestResult>> deleteGroupObserver=new MediatorLiveData<>();

    public SceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::lampList);
        addGroupObserver=Transformations.switchMap(addGroupRequest, repository::createGroup);
        addDeviceToGroupObserver=Transformations.switchMap(addDeviceToGroupRequest, repository::addDeviceToGroup);
        updateGroupObserver =Transformations.switchMap(updateGroupRequest, repository::updateGroup);
        /*groupDetailObserver = Transformations.switchMap(group, new Function<Group, LiveData<ApiResponse<Group>>>() {
            @Override
            public LiveData<ApiResponse<Group>> apply(Group group) {
                if (null==group) {
                    return AbsentLiveData.create();
                }
                return repository.getGroupDetail(group.getId());
            }
        });*/

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



    }

    public void deleteGroup() {
        LiveData<ApiResponse<RequestResult>> responseLiveData = repository.deleteGroup(group.getValue().getId());

        deleteGroupObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteGroupObserver.removeSource(responseLiveData);
                deleteGroupObserver.setValue(apiResponse);
            }
        });
    }


}
