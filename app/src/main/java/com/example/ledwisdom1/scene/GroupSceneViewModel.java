package com.example.ledwisdom1.scene;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.common.AbsentLiveData;
import com.example.ledwisdom1.common.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景 情景的ViewModel
 */
@Deprecated
public class GroupSceneViewModel extends AndroidViewModel {
    private static final String TAG = "GroupSceneViewModel";
    private final HomeRepository repository;
    //场景 情景名称
    public String name = "";
    public String imagePath = "";
    /**
     * 场景或情景中添加或修改灯所有的数据
     * 选中的需要组灯 没有选中的从组中去除
     *
     */
    @Deprecated
    public final List<Lamp> groupSceneLamps=new ArrayList<>();
    //添加还是修改的标记
    public boolean MODE_ADD=true;
    //场景  更新使用
    public MutableLiveData<String> groupSceneId = new MutableLiveData<>();

    //场景 情景请求参数的封装
    public GroupSceneRequest groupSceneRequest = new GroupSceneRequest();

    // lamp列表请求
    public MutableLiveData<String> lampListRequest = new MutableLiveData<>();

    // lamp列表监听
    public final LiveData<List<Lamp>> lampListObserver;

    // 创建场景请求
    public MutableLiveData<GroupSceneRequest> addGroupRequest = new MutableLiveData<>();
    // 创建场景情景结果监听
    public final LiveData<ApiResponse<AddGroupSceneResult>> addGroupObserver;

    // 修改场景请求
    public MutableLiveData<GroupSceneRequest> updateGroupRequest = new MutableLiveData<>();
    // 修改场景监听
    public final LiveData<ApiResponse<AddGroupSceneResult>> updateGroupObserver;

    /**
     * 添加设备到场景或情景的请求，封装了所有参数 和区分场景和情景的标识
     */
    public MutableLiveData<GroupSceneRequest> addDeviceToGroupSceneRequest = new MutableLiveData<>();

    public final LiveData<ApiResponse<RequestResult>> addDeviceToGroupSceneObserver;

    //场景 情景已有设备
    public final MediatorLiveData<List<Lamp>> groupDevicesObserver=new MediatorLiveData<>();

    public final MediatorLiveData<ApiResponse<RequestResult>> deleteGroupObserver=new MediatorLiveData<>();
//    情景列表请求
    public final MutableLiveData<Integer> sceneListRequest = new MutableLiveData<>();

//    public final LiveData<ApiResponse<SceneList>> sceneListObserver;

    public final ObservableBoolean isLoading=new ObservableBoolean(false);

    public GroupSceneViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, new Function<String, LiveData<List<Lamp>>>() {
            @Override
            public LiveData<List<Lamp>> apply(String input) {
                return repository.loadLampForGroup(input);
            }
        });
        updateGroupObserver =Transformations.switchMap(updateGroupRequest, repository::updateGroupScene);
//        sceneListObserver=Transformations.switchMap(sceneListRequest, repository::getSceneList);

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

        addDeviceToGroupSceneObserver=Transformations.switchMap(addDeviceToGroupSceneRequest, new Function<GroupSceneRequest, LiveData<ApiResponse<RequestResult>>>() {
            @Override
            public LiveData<ApiResponse<RequestResult>> apply(GroupSceneRequest input) {
                if (input == null) {
                    Log.d(TAG, "apply: addDeviceToGroupSceneObserver null");
                    return AbsentLiveData.create();
                }
                return null;
//                return repository.addDeviceToGroupScene(input);
            }
        });



//        场景 情景已添加的设备
        groupDevicesObserver.addSource(groupSceneId, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "onChanged: "+s);
                LiveData<List<Lamp>> devicesInGroup = repository.getDevicesInGroup(groupSceneRequest.isGroup,s);
                groupDevicesObserver.addSource(devicesInGroup, new Observer<List<Lamp>>() {
                    @Override
                    public void onChanged(@Nullable List<Lamp> lamps) {
                        groupDevicesObserver.removeSource(devicesInGroup);
                        groupDevicesObserver.setValue(lamps);
                    }
                });
            }
        });


    }

    //场景页面的条目 使用代码生成 列表显示 这样可以多处通用
    public List<CommonItem> generateItems() {
        int num=getLampNum();
        List<CommonItem> items = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, imagePath);
        CommonItem name = new CommonItem(1, "名称", true, -1, true, TextUtils.isEmpty(this.name)?"请输入":this.name);
        CommonItem device = new CommonItem(2, "设备", true, -1, true, num==0?"请添加":String.valueOf(num));
        items.add(pic);
        items.add(name);
        items.add(device);
        if (!groupSceneRequest.isGroup) {
            CommonItem group = new CommonItem(3, "场景", true, -1, true, "请添加");
            items.add(group);
        }
        return items;
    }

   /* public List<String> getSelectedLampIds() {
        List<String> deviceIds=new ArrayList<>();
        for (Lamp lamp : groupSceneLamps) {
            if (BindingAdapters.LIGHT_SELECTED==lamp.lampStatus.get()) {
                deviceIds.add(lamp.getId());
            }
        }

        return deviceIds;
    }*/

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

    private int getLampNum() {
        int num=0;
        for (Lamp lamp : groupSceneLamps) {
            if (BindingAdapters.LIGHT_SELECTED==lamp.lampStatus.get()) {
                num++;
            }
        }
        return num;
    }

    /**
     * 删除场景或情景
     * @param isGroup 是否是场景
     */
/*
    public void deleteGroup(boolean isGroup) {
        LiveData<ApiResponse<RequestResult>> responseLiveData = isGroup ? repository.deleteGroup(groupSceneId.getValue()) : repository.deleteScene(groupSceneId.getValue());
        deleteGroupObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteGroupObserver.removeSource(responseLiveData);
                deleteGroupObserver.setValue(apiResponse);
            }
        });


    }
*/




}
