package com.example.ledwisdom1.user;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.KimAscendService;
import com.example.ledwisdom1.api.NetWork;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.AppExecutors;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.common.RequestCreator;
import com.example.ledwisdom1.database.SmartLightDataBase;
import com.example.ledwisdom1.database.UserDao;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.model.User;
import com.example.ledwisdom1.utils.SharePrefencesUtil;
import com.google.gson.Gson;

import okhttp3.MultipartBody;
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



    /**
     * 登录成功首先在内存保存Profile然后在本地保存
     * 判断profile中的meshId是否为空 ，为空是第一次登录，直接返回 ，不为空请求缺省的Mesh数据
     * @param requestBody
     * @return
     */
    public LiveData<Resource<Boolean>> login(RequestBody requestBody) {
        MediatorLiveData<Resource<Boolean>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Profile>> loginResponse = kimService.login(requestBody);
        result.addSource(loginResponse, apiResponse -> {
            result.removeSource(loginResponse);
            if (apiResponse.isSuccessful()&&apiResponse.body!=null) {
                Profile profile = apiResponse.body;
                if (profile.succeed()) {
                    SmartLightApp.INSTANCE().setProfile(profile);
                    SharePrefencesUtil.saveUserProfile(new Gson().toJson(profile));
//                    没有默认的meshId
                    if (TextUtils.isEmpty(profile.meshId)) {
                        result.setValue(Resource.success(true, profile.resultMsg));
                    }else{
                        //登录成功获取默认mesh详情 并保存到本地和内存中 其实应该由后台直接返回
                        RequestBody meshDetailRequest = RequestCreator.createMeshDetail(profile.meshId);
                        LiveData<ApiResponse<DefaultMesh>> apiResponseLiveData = kimService.meshDetail(meshDetailRequest);
                        result.addSource(apiResponseLiveData, defaultMeshApiResponse -> {
                            result.removeSource(apiResponseLiveData);
                            if (defaultMeshApiResponse.isSuccessful()) {
                                DefaultMesh defaultMesh = defaultMeshApiResponse.body;
                                //判斷是否是自己的
                                defaultMesh.isMine = defaultMesh.creater.equals(profile.userId);
                                SmartLightApp.INSTANCE().setDefaultMesh(defaultMesh);
                            }
                            result.setValue(Resource.success(true, profile.resultMsg));
                        });
                    }
                   /* executors.diskIO().execute(()->{
                        HomeRepository.INSTANCE(SmartLightApp.INSTANCE()).setSessionId(profile.sessionid);
                        userDao.insert(profile);
                        result.postValue(Resource.success(true, profile.resultMsg));
                    });*/
                } else {
                    result.setValue(Resource.error(false, profile.resultMsg));
                }
            } else {
                result.setValue(Resource.error(false, apiResponse.errorMsg));
            }
        });
        return result;
    }

    public LiveData<ApiResponse<RequestResult>> updateUser(UserRequest request) {
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("userName", request.userName);
        LiveData<ApiResponse<RequestResult>> responseLiveData;
        if (null !=  request.userIcon) {
            RequestBody requestFile = RequestBody.create(RequestCreator.MEDIATYPE, request.userIcon);
            MultipartBody.Part icon =MultipartBody.Part.createFormData("userIcon", request.userIcon.getName(), requestFile);
            responseLiveData = kimService.updateUser(icon, map);
        } else {
//            RequestBody requestBody = RequestCreator.createDeviceSetting(new Gson().toJson(map));
            responseLiveData = kimService.updateUser(map);
        }
        return responseLiveData;

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
//        HomeRepository.INSTANCE(SmartLightApp.INSTANCE()).setSessionId("");
        SharePrefencesUtil.clear();
        db.runInTransaction(()->{
            userDao.deleteProfile();
            userDao.deleteAllMeshes();
        });

    }


    public LiveData<Profile> loadProfile() {
        return userDao.loadProfile();
    }

    public LiveData<User> getUserInfo() {
        MediatorLiveData<User> result=new MediatorLiveData<>();
        LiveData<ApiResponse<User>> userInfo = kimService.getUserInfo();
        result.addSource(userInfo, new Observer<ApiResponse<User>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<User> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    result.setValue(apiResponse.body);
                }
            }
        });
        return result;

    }

}
