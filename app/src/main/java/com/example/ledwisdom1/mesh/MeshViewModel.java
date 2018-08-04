package com.example.ledwisdom1.mesh;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.user.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MeshViewModel extends AndroidViewModel {
    private static final String TAG = MeshViewModel.class.getSimpleName();

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> account = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableInt type = new ObservableInt();

    //    用户资料
    public final LiveData<Profile> profile;
    //    默认的Mesh
    public final LiveData<DefaultMesh> defaultMeshObserver;

    public List<String> meshDetailName = new ArrayList<>();
    /**
     * 添加mesh网络的请求监听
     */
    public final MutableLiveData<ReportMesh> meshObserver = new MutableLiveData<>();
    /**
     * 添加Mesh的结果监听
     */
    public final LiveData<Resource<AddMeshResult>> addMeshObserver;
 /**
     * 修改mesh网络的请求监听
     */
    public final MutableLiveData<ReportMesh> modifymeshRequest = new MutableLiveData<>();
    /**
     * 修改Mesh的结果监听
     */
    public final LiveData<ApiResponse<RequestResult>> modifyMeshObserver;

    public MutableLiveData<Integer> pagerNo = new MutableLiveData<>();

    //    蓝牙网路列表
    public final LiveData<Resource<List<Mesh>>> meshList;

    public MutableLiveData<String> setDefaultMeshRequest = new MutableLiveData<>();
    //    mesh detail
    public final LiveData<Resource<Boolean>> setDefaultMeshObserver;

    public MutableLiveData<Mesh> deleteMeshRequest = new MutableLiveData<>();
    //    mesh
    public final LiveData<Resource<Boolean>> deleteMeshObserver;


    private final HomeRepository repository;

    public MeshViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        profile = repository.profileObserver;
        defaultMeshObserver = repository.defaultMeshObserver;
        populateMesh();
        addMeshObserver = Transformations.switchMap(meshObserver, reportMesh -> {
            if (reportMesh == null) {
                return null;
            }
            reportMesh.homeName = name.get();
            reportMesh.meshName = account.get();
            reportMesh.meshPassword = password.get();
            Log.d(TAG, reportMesh.toString());
            return repository.addMesh(reportMesh);
        });

        modifyMeshObserver = Transformations.switchMap(modifymeshRequest, reportMesh -> {
            if (reportMesh == null) {
                return null;
            }
//            reportMesh.homeName = reportMesh.homeName;
            Log.d(TAG, reportMesh.toString());
            return repository.modifyMesh(reportMesh);
        });

        meshList = Transformations.switchMap(pagerNo, pageNo -> {
//            RequestBody requestBody = RequestCreator.createPage(1, 40);
            return repository.loadMeshList();
        });


        setDefaultMeshObserver = Transformations.switchMap(setDefaultMeshRequest, repository::setDefaultMesh);

        deleteMeshObserver = Transformations.switchMap(deleteMeshRequest, new Function<Mesh, LiveData<Resource<Boolean>>>() {
            @Override
            public LiveData<Resource<Boolean>> apply(Mesh Mesh) {
                return repository.deleteMesh(Mesh);
            }
        });


    }

    private void populateMesh() {
        name.set("home name");
        account.set(UUID.randomUUID().toString().substring(0, 6));
        password.set(UUID.randomUUID().toString().substring(0, 6));
        meshDetailName.add("图片");
        meshDetailName.add("名称");
        meshDetailName.add("网络");
        meshDetailName.add("网关");
        meshDetailName.add("设备总数量");
    }



}
