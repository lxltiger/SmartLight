package com.example.ledwisdom1.clock;

import android.app.Application;
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
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;
import com.example.ledwisdom1.utils.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

public class ClockViewModel extends AndroidViewModel {
    private HomeRepository repository;

    /**
     * lamp列表请求 如果Clockid不为空从中标记已选择的灯具，此方法暂不支持分页
     */
    public MutableLiveData<String> lampListRequest = new MutableLiveData<>();
    // lamp列表监听
    public final LiveData<List<Lamp>> lampListObserver;

    // 添加改闹钟请求
    public MutableLiveData<ClockRequest> clockRequest = new MutableLiveData<>();
    public final LiveData<ClockResult> clockObserver;

    // 创建修改闹钟请求
    public MutableLiveData<ClockRequest> updateClockRequest = new MutableLiveData<>();
    public final LiveData<ClockRequest> updateClockObserver;

    public final ObservableBoolean isLoading = new ObservableBoolean(false);


    public final MutableLiveData<Integer> clockListRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<ClockList>> clockListObserver;

    public final MediatorLiveData<Clock> deleteClockObserver = new MediatorLiveData<>();
    public final MediatorLiveData<Clock> switchClockObserver = new MediatorLiveData<>();

    public ClockViewModel(@NonNull Application application) {
        super(application);
        repository = HomeRepository.INSTANCE(application);
        lampListObserver = Transformations.switchMap(lampListRequest, repository::loadLampsForClock);

        clockListObserver = Transformations.switchMap(clockListRequest, repository::getClockList);

        clockObserver = Transformations.switchMap(clockRequest, repository::addClock);

        updateClockObserver = Transformations.switchMap(updateClockRequest, repository::updateClockAndDevice);

    }


    public void deleteClick(Clock clock) {
        LiveData<ApiResponse<RequestResult>> responseLiveData = repository.deleteClock(clock.getId());
        deleteClockObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                deleteClockObserver.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    deleteClockObserver.setValue(clock);
                } else {
                    deleteClockObserver.setValue(null);
                }
            }
        });
    }

    public void switchClock(Clock clock) {
        LiveData<ApiResponse<RequestResult>> responseLiveData = repository.switchClock(clock);
        switchClockObserver.addSource(responseLiveData, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                switchClockObserver.removeSource(responseLiveData);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    clock.setIsOpen(1 - clock.getIsOpen());
                    switchClockObserver.setValue(clock);
                } else {
                    switchClockObserver.setValue(null);
                }
            }
        });
    }

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
}
