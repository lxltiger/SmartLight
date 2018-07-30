package com.example.ledwisdom1.user;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.KimAscendService;
import com.example.ledwisdom1.api.NetWork;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.AppExecutors;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.database.SmartLightDataBase;
import com.example.ledwisdom1.database.UserDao;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.repository.HomeRepository;

import okhttp3.RequestBody;

/**
 * 用户的数据仓库，负责从local db和remote 获取数据，保存数据到本地 对UI透明
 * UI只负责交互不负责数据处理
 */
public class UserRepository {


    private SmartLightDataBase db;
    private UserDao userDao;
    private KimAscendService kimService;
    private final AppExecutors executors;


    public UserRepository(Context context) {
        db = SmartLightDataBase.INSTANCE(context);
        userDao = db.user();
        kimService = NetWork.kimService();
        executors = SmartLightApp.INSTANCE().appExecutors();
    }


//    登录，成功后保存用户数据
    public LiveData<Resource<Boolean>> login(RequestBody requestBody) {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Profile>> loginResponse = kimService.login(requestBody);
        result.addSource(loginResponse, apiResponse -> {
            result.removeSource(loginResponse);
            if (apiResponse.isSuccessful()) {
                Profile profile = apiResponse.body;
                if (profile.succeed()) {
                    executors.diskIO().execute(()->{
                        HomeRepository.INSTANCE(SmartLightApp.INSTANCE()).setSessionId(profile.sessionid);
                        userDao.insert(profile);
                        result.postValue(Resource.success(true, profile.resultMsg));
                    });

                } else {
                    result.setValue(Resource.error(false, profile.resultMsg));
                }
            } else {
                result.setValue(Resource.error(false, apiResponse.errorMsg));
            }
        });
        return result;
    }

    public LiveData<ApiResponse<RequestResult>> getAuthCode(RequestBody requestBody) {
        return kimService.getAuthCode(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> smsValidate(RequestBody requestBody) {
        return kimService.SMSvalidate(requestBody);
    }


    public LiveData<ApiResponse<RequestResult>> register(RequestBody requestBody) {
        return kimService.regist(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> resetPsw(RequestBody requestBody) {
        return kimService.resetpassword(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> modifyPsw(RequestBody requestBody) {
        return kimService.setPassword(requestBody);
    }

    public LiveData<ApiResponse<RequestResult>> feedback(RequestBody requestBody) {
        return kimService.feedBack(requestBody);
    }

    //    登出，成功后清除本地数据
    public LiveData<Resource<Boolean>> logout() {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<RequestResult>> logoutResponse = kimService.logout();
        result.addSource(logoutResponse, apiResponse -> {
            result.removeSource(logoutResponse);
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    clearLocalData();
                    result.setValue(Resource.success(true,apiResponse.body.resultMsg));
                }else{
                    result.setValue(Resource.error(true,apiResponse.body.resultMsg));
                }
            } else {
                result.setValue(Resource.error(true,apiResponse.errorMsg));
            }
        });

        return result;
    }

    public LiveData<ApiResponse<RequestResult>> checkAccount(RequestBody requestBody) {
        return kimService.checkAccount(requestBody);
    }


    public void clearLocalData() {
        //清空session
        HomeRepository.INSTANCE(SmartLightApp.INSTANCE()).setSessionId("");
        db.runInTransaction(()->{
            userDao.deleteProfile();
            userDao.deleteAllMeshes();
        });

    }


    public LiveData<Profile> loadProfile() {
        return userDao.loadProfile();
    }


}
