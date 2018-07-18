package com.example.ledwisdom1.home;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.HubList;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.RequestCreator;

import okhttp3.RequestBody;

/**
 * 负责首页各页面的网络请求 并提供数据返回接口供UI监听
 */
public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();
    private HomeRepository repository;

    //    用户资料
    public final LiveData<Profile> profile;
//    默认的Mesh
    public final LiveData<DefaultMesh> defaultMeshObserver;

    //    分享mesh
    public MutableLiveData<String> shareMeshRequest = new MutableLiveData<>();
    // 分享mesh监听
    public final LiveData<ApiResponse<RequestResult>> shareMeshObserver;


    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<ApiResponse<LampList>> lampListObserver;


    // hub列表请求
    public MutableLiveData<Integer> hubListRequest = new MutableLiveData<>();
    // hub列表监听
    public final LiveData<ApiResponse<HubList>> hubListObserver;

    //    删除lamp请求
    public final MutableLiveData<String> deleteLampRequest = new MutableLiveData<>();
    public final LiveData<Resource<Boolean>> deleteLampObserver;

    // 场景列表请求
    public MutableLiveData<Integer> groupListRequest = new MutableLiveData<>();
    //场景列表监听
    public final LiveData<ApiResponse<GroupList>> groupListObserver;



    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        profile = repository.profileObserver;
        defaultMeshObserver = repository.defaultMeshObserver;

        shareMeshObserver = Transformations.switchMap(shareMeshRequest, input -> {
            RequestBody requestBody = RequestCreator.createShareMesh(input);
            return repository.shareMesh(requestBody);
        });

        lampListObserver = Transformations.switchMap(lampListRequest, input -> {
            if (profile.getValue() != null) {
                String meshId = profile.getValue().meshId;
                RequestBody requestBody = RequestCreator.requestLampList(meshId, input);
                return repository.getLampList(requestBody);
            }else{
                Log.d(TAG, "profile is null");
                return null;
            }
        });

        hubListObserver = Transformations.switchMap(hubListRequest, input -> repository.getHubList(input));

        groupListObserver= Transformations.switchMap(groupListRequest, input -> repository.getGroupList(input));

        deleteLampObserver=Transformations.switchMap(deleteLampRequest, new Function<String, LiveData<Resource<Boolean>>>() {
            @Override
            public LiveData<Resource<Boolean>> apply(String input) {
                return repository.deleteDevice(input);
            }
        });
    }


}
