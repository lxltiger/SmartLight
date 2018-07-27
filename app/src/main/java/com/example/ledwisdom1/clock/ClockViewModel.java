package com.example.ledwisdom1.clock;

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

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.utils.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

public class ClockViewModel extends AndroidViewModel {
    private HomeRepository repository;

    public MutableLiveData<String> clockId = new MutableLiveData<>();
    public final MediatorLiveData<List<Lamp>> clockDevicesObserver=new MediatorLiveData<>();


    /**
     * 闹钟中添加或修改灯所有的数据
     * 选中的需要组灯 没有选中的从组中去除
     *
     */
    public final List<Lamp> allLamps=new ArrayList<>();
    // lamp列表请求
    public MutableLiveData<Integer> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<ApiResponse<LampList>> lampListObserver;

    // 创建修改闹钟请求
    public MutableLiveData<ClockRequest> clockRequest = new MutableLiveData<>();
    public final LiveData<ClockResult> clockObserver;


    public MutableLiveData<ClockRequest> updateClockRequest = new MutableLiveData<>();
    public final LiveData<RequestResult> updateClockObserver;

    public final ObservableBoolean isLoading=new ObservableBoolean(false);


    public final MutableLiveData<Integer> clockListRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<ClockList>> clockListObserver;

    public final MediatorLiveData<Clock> deleteClockObserver=new MediatorLiveData<>();

    public ClockViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::lampList);
        clockListObserver=Transformations.switchMap(clockListRequest, repository::getClockList);

        clockObserver=Transformations.switchMap(clockRequest, new Function<ClockRequest, LiveData<ClockResult>>() {
            @Override
            public LiveData<ClockResult> apply(ClockRequest input) {
                return repository.addOrUpdateClock(input);
            }
        });

        updateClockObserver=Transformations.switchMap(updateClockRequest, new Function<ClockRequest, LiveData<RequestResult>>() {
            @Override
            public LiveData<RequestResult> apply(ClockRequest input) {
                return repository.updateClock(input);
            }
        });

        clockDevicesObserver.addSource(clockId, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                LiveData<List<Lamp>> devicesInGroup = repository.getDevicesInClock(s);
                clockDevicesObserver.addSource(devicesInGroup, new Observer<List<Lamp>>() {
                    @Override
                    public void onChanged(@Nullable List<Lamp> lamps) {
                        clockDevicesObserver.removeSource(devicesInGroup);
                        clockDevicesObserver.setValue(lamps);
                    }
                });
            }
        });
    }


    public void deleteClick(Clock clock) {
        LiveData<ApiResponse<RequestResult>> responseLiveData =  repository.deleteClock(clock.getId());
        deleteClockObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteClockObserver.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    deleteClockObserver.setValue(clock);
                }else {
                    deleteClockObserver.setValue(null);
                }
            }
        });


    }
    public List<String> getSelectedLampIds() {
        List<String> deviceIds=new ArrayList<>();
        for (Lamp lamp : allLamps) {
            if (BindingAdapters.LIGHT_SELECTED==lamp.lampStatus.get()) {
                deviceIds.add(lamp.getId());
            }
        }

        return deviceIds;
    }
}
