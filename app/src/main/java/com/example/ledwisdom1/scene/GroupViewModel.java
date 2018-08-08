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
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.common.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景ViewModel
 */
public class GroupViewModel extends AndroidViewModel {
    private static final String TAG = GroupViewModel.class.getSimpleName();
    private final HomeRepository repository;

    // lamp列表请求
    public MutableLiveData<String> lampListRequest = new MutableLiveData<>();

    // lamp列表监听
    public final LiveData<List<Lamp>> lampListObserver;

    // 创建场景请求
    public MutableLiveData<GroupRequest> addGroupRequest = new MutableLiveData<>();
    // 创建场景情景结果监听
    public final LiveData<AddGroupSceneResult> addGroupObserver;


    // 修改场景请求
    public MutableLiveData<GroupRequest> updateGroupRequest = new MutableLiveData<>();
    // 修改场景监听
    public final LiveData<GroupRequest> updateGroupObserver;


    public final MediatorLiveData<RequestResult> deleteGroupObserver=new MediatorLiveData<>();



    public GroupViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::loadLampForGroup);
        updateGroupObserver =Transformations.switchMap(updateGroupRequest, repository::updateGroupAndDevices);
        addGroupObserver=Transformations.switchMap(addGroupRequest, repository::addGroup);

    }

    //场景页面的条目 使用代码生成 列表显示 这样可以多处通用
   /* public List<CommonItem> generateItems() {
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


    /**
     */
    public void deleteGroup(String groupId) {
        LiveData<ApiResponse<RequestResult>> responseLiveData =repository.deleteGroup(groupId);
        deleteGroupObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteGroupObserver.removeSource(responseLiveData);
                if (null != apiResponse && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    deleteGroupObserver.setValue(apiResponse.body);
                } else {
                    deleteGroupObserver.setValue(null);
                }
            }
        });


    }




}
