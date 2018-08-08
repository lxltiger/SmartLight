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

import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.repository.HomeRepository;

import java.util.ArrayList;
import java.util.List;

public class MeshViewModel extends AndroidViewModel {
    private static final String TAG = MeshViewModel.class.getSimpleName();

    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> account = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableInt type = new ObservableInt();

    //    用户资料
//    public final LiveData<Profile> profile;
    //    默认的Mesh
//    public final LiveData<DefaultMesh> defaultMeshObserver;

    public List<String> meshDetailName = new ArrayList<>();
    /**
     * 添加mesh网络的请求监听
     */
    public final MutableLiveData<ReportMesh> meshObserver = new MutableLiveData<>();
    /**
     * 添加Mesh的结果监听
     */
    public final LiveData<Resource<Boolean>> addMeshObserver;
    /**
     * 修改mesh网络的请求监听
     */
    public final MutableLiveData<ReportMesh> modifymeshRequest = new MutableLiveData<>();
    /**
     * 修改Mesh的结果监听
     */
    public final LiveData<Resource<Boolean>> modifyMeshObserver;

    public MutableLiveData<Integer> meshListRequest = new MutableLiveData<>();

    //    蓝牙网路列表
    public final LiveData<Resource<List<Mesh>>> meshListObserver;

    public MutableLiveData<Mesh> setDefaultMeshRequest = new MutableLiveData<>();
    //    mesh detail
    public final LiveData<Resource<Boolean>> setDefaultMeshObserver;

    public MutableLiveData<Mesh> deleteMeshRequest = new MutableLiveData<>();
    //    mesh
    public final LiveData<Resource<Mesh>> deleteMeshObserver;

    private final HomeRepository repository;

    public MeshViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        populateMesh();
        addMeshObserver = Transformations.switchMap(meshObserver, repository::addMesh);

        modifyMeshObserver = Transformations.switchMap(modifymeshRequest, repository::modifyMesh);

        meshListObserver = Transformations.switchMap(meshListRequest, pageNo -> repository.loadMeshList());


        setDefaultMeshObserver = Transformations.switchMap(setDefaultMeshRequest, repository::setDefaultMesh);

        deleteMeshObserver = Transformations.switchMap(deleteMeshRequest, new Function<Mesh, LiveData<Resource<Mesh>>>() {
            @Override
            public LiveData<Resource<Mesh>> apply(Mesh Mesh) {
                return repository.deleteMesh(Mesh);
            }
        });


    }

    private void populateMesh() {
//        name.set("home name");
//        account.set(UUID.randomUUID().toString().substring(0, 6));
//        password.set(UUID.randomUUID().toString().substring(0, 6));
        meshDetailName.add("图片");
        meshDetailName.add("名称");
        meshDetailName.add("网络");
//        meshDetailName.add("网关");
        meshDetailName.add("设备总数量");
    }


}
