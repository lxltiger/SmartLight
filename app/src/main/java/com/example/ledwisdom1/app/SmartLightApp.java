package com.example.ledwisdom1.app;

import android.util.Log;

import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.SharePrefencesUtil;
import com.google.gson.Gson;
import com.telink.TelinkApplication;
import com.telink.bluetooth.TelinkLog;
import com.telink.crypto.AES;

/**
 * xiaolin.li@kimascend.cn
 * OPPO和VIVO手机在电量低时不能启动service
 */
public final class SmartLightApp extends TelinkApplication {
    private static final String TAG = SmartLightApp.class.getSimpleName();
    private AppExecutors mAppExecutors;
    //是否使用蓝牙
    private boolean isBlueTooth = true;
    private Profile profile;
    private DefaultMesh defaultMesh;
    private int meshStatus;

    private static SmartLightApp sLightApp;


    public static SmartLightApp INSTANCE() {
        return sLightApp;
    }

    public AppExecutors appExecutors() {
        return mAppExecutors;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sLightApp = this;
        retrieveLocalData();
//        doInit();
        AdvanceStrategy.setDefault(new MySampleAdvanceStrategy());
        mAppExecutors = new AppExecutors();
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        SharePrefencesUtil.saveUserProfile(new Gson().toJson(profile));
    }

    public void updateProfile(String meshId) {
        profile.meshId = meshId;
        SharePrefencesUtil.saveUserProfile(new Gson().toJson(profile));

    }

    public Profile getProfile() {
        return profile;
    }


    public void setDefaultMesh(DefaultMesh defaultMesh) {
        this.defaultMesh = defaultMesh;
        SharePrefencesUtil.saveDefaultMesh(new Gson().toJson(defaultMesh));

    }

    public DefaultMesh getDefaultMesh() {
        return defaultMesh;
    }

    public boolean isBlueTooth() {
        return isBlueTooth;
    }

    public void setBlueTooth(boolean blueTooth) {
        isBlueTooth = blueTooth;
    }

    public int getMeshStatus() {
        return meshStatus;
    }

    public void setMeshStatus(int meshStatus) {
        this.meshStatus = meshStatus;
    }

    @Override
    public void doInit() {
        String fileName = "telink-";
        fileName += System.currentTimeMillis();
        fileName += ".log";
        TelinkLog.LOG2FILE_ENABLE = false;
        TelinkLog.onCreate(fileName);
        super.doInit();
        AES.Security = true;
        //启动LightService
        this.startLightService(TelinkLightService.class);
    }

    @Override
    public void doDestroy() {
        TelinkLog.onDestroy();
        super.doDestroy();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: ");
    }


    private void retrieveLocalData() {
        Gson gson = new Gson();
        String userProfile = SharePrefencesUtil.getUserProfile();
        profile = gson.fromJson(userProfile, Profile.class);
        String mesh = SharePrefencesUtil.getDefaultMesh();
        defaultMesh = gson.fromJson(mesh, DefaultMesh.class);

    }


}
