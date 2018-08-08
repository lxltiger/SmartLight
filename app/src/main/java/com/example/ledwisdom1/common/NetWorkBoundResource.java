package com.example.ledwisdom1.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.AppExecutors;

import java.util.Objects;

/**
 *
 * @param <RequestType> 请求返回的原始数据类型
 * @param <ResultType> 加工后的数据类型
 */
public abstract class NetWorkBoundResource<RequestType, ResultType> {

    private     MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
    private final AppExecutors appExecutors;

    public NetWorkBoundResource(AppExecutors executors) {
        appExecutors = executors;
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromLocal();
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType resultType) {
                result.removeSource(dbSource);
                if (shouldLoadFromRemote(resultType)) {
                    loadFromRemote(dbSource);
                }else{
                    result.addSource(dbSource, newData -> setValue(Resource.success(newData, "")));
                }
            }
        });
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    protected  void loadFromRemote(LiveData<ResultType> dbSource){
        result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));
        LiveData<ApiResponse<RequestType>> apiResponse=createCall();
        result.addSource(apiResponse, new Observer<ApiResponse<RequestType>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestType> response) {
                result.removeSource(dbSource);
                result.removeSource(apiResponse);
                if (response.isSuccessful()) {
                    appExecutors.diskIO().execute(()->{
                        saveResult(processResponse(response));
                        appExecutors.mainThread().execute(()->{
                            LiveData<ResultType> local = loadFromLocal();
                            result.addSource(local,data->setValue(Resource.success(data,"")));
                        });
                    });
                }else{
                    onLoadFail();
                    result.addSource(dbSource, newData -> setValue(Resource.error(newData,"加载失败")));
                }
            }
        });
    }

    protected RequestType processResponse(ApiResponse<RequestType> response){
        return response.body;
    }

    private void setValue(Resource<ResultType> resource) {
        if (Objects.equals(result.getValue(), resource)) {
            return;
        }
        result.setValue(resource);
    }

    @WorkerThread
    protected abstract void saveResult(RequestType body);

    protected abstract void onLoadFail();

    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @MainThread
    protected abstract boolean shouldLoadFromRemote(ResultType resultType);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromLocal();
}
