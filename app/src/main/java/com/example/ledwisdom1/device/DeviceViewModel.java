package com.example.ledwisdom1.device;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.AddHubRequest;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.Mesh;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.RequestCreator;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

public class DeviceViewModel extends AndroidViewModel {
    private static final String TAG = "DeviceViewModel";
    private HomeRepository repository;

    //    用户资料
    public final LiveData<Profile> profile;
    //    默认的Mesh
    public final LiveData<DefaultMesh> defaultMeshObserver;
    /**
     * Fragment 与Activity的通信
     */
    public final MutableLiveData<Integer> navigation = new MutableLiveData<>();

    //    添加lamp请求
    public final MutableLiveData<Map<String, String>> addLampRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> addLampObserver;


    //    添加Hub请求
    public final MutableLiveData<AddHubRequest> addHubRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> addHubObserver;
    //我的mesh列表
    public LiveData<List<Mesh>> myMeshList;
    public DeviceViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        profile = repository.profileObserver;
        defaultMeshObserver = repository.defaultMeshObserver;
        addLampObserver = Transformations.switchMap(addLampRequest, input -> {
            input.put("meshId", defaultMeshObserver.getValue().id);
            RequestBody requestBody = RequestCreator.requestAdLamp(input);
            return repository.reportDevice(requestBody);
        });

        addHubObserver = Transformations.switchMap(addHubRequest, addHubRequest -> repository.reportHub(addHubRequest));

        myMeshList = repository.loadMyMeshFromLocal();
    }

}
