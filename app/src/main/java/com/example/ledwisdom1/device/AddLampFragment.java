package com.example.ledwisdom1.device;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.common.BindingAdapters;
import com.example.ledwisdom1.databinding.FragmentAddLampBinding;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.model.Light;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.ToastUtil;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.ledwisdom1.common.BindingAdapters.ADD;
import static com.example.ledwisdom1.common.BindingAdapters.ADDED;

/**
 * 扫面添加灯具页面
 * 扫描之前需要开启auto connect
 * 通过配置LeScanParameters 的scanMode（true）来逐个扫描自动修改mesh 或scanMode（false）扫描当前mesh所有设备来手动修改 ，我们使用后一种
 * <p>
 * 现在只扫描设备来显示 限制15秒时间 超过这个时间停止扫描至用户手动重扫
 * 如果扫描过程中用户点击重扫 停止当前的扫描 清空列表 重新开始15秒扫描添加
 */
public class AddLampFragment extends Fragment implements EventListener<String>, CallBack {
    public static final String TAG = AddLampFragment.class.getSimpleName();

    /**
     * 每次扫描持续时间
     */
    private static final int SCANNING_SPAN = 15 * 1000;
    /**
     * 是否扫描 一进来就扫描 所以默认为true  也是停止扫描的标记
     */
    private boolean isScan = true;
    private FragmentAddLampBinding mBinding;

    /**
     * 新设备适配器
     */
    private AddLampAdapter mAdapter;
    /**
     * 添加状态
     */
    boolean isAdd = false;

    private Handler mHandler = new Handler();
    private DeviceViewModel viewModel;
//    private String meshName = "";
//    private String meshPsw = "";
    /**
     * 添加成功 通知deviceFragment刷新
     */
//    private boolean addSucceed=false;
    private SparseIntArray deviceType = new SparseIntArray();

    public static AddLampFragment newInstance() {
        Bundle args = new Bundle();
        AddLampFragment fragment = new AddLampFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceType.put(Config.LAMP_RGB, Config.LAMP_TYPE);
        deviceType.put(Config.LAMP, Config.LAMP_TYPE);
        deviceType.put(Config.SOCKET, Config.SOCKET_TYPE);
        deviceType.put(Config.PANEL, Config.PANEL_TYPE);
        addEventListener();

    }

    private void addEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();

        smartLightApp.addEventListener(LeScanEvent.LE_SCAN, this);
        smartLightApp.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
//        smartLightApp.addEventListener(LeScanEvent.LE_SCAN_COMPLETED, this);
        smartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        smartLightApp.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        smartLightApp.addEventListener(MeshEvent.OFFLINE, this);
        smartLightApp.addEventListener(MeshEvent.ERROR, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_lamp, container, false);
        mBinding.setHandler(this);
        List<Light> mLights = new ArrayList<>();
        mAdapter = new AddLampAdapter(mLights, mOnHandleNewLightListener);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.recyclerView.setAdapter(mAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isScan = true;
        startScan(100);
        mBinding.setIsScanning(true);
        mHandler.postDelayed(mStopScan, SCANNING_SPAN);
        viewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        subscribeUI(viewModel);
    }

    private void subscribeUI(DeviceViewModel viewModel) {
        viewModel.addLampObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    getActivity().setResult(Activity.RESULT_OK);
                } else {
                    ToastUtil.showToast("上报失败");
                }
            }
        });

        viewModel.lampMeshAddressObserver.observe(this, new Observer<Light>() {
            @Override
            public void onChanged(@Nullable Light light) {
                if (light != null) {
                    light.mAddStatus.set(BindingAdapters.ADDING);
                    LeUpdateParameters params = Parameters.createUpdateParameters();
                    params.setOldMeshName(Config.FACTORY_NAME);
                    params.setOldPassword(Config.FACTORY_PASSWORD);
                    DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
                    String meshName = "";
                    String meshPsw = "";
                    if (defaultMesh != null) {
                        meshName = defaultMesh.name;
                        meshPsw = defaultMesh.password;
                    }
//                    params.setOldMeshName("telink_mesh1");
//                    params.setOldPassword("123");
                    params.setNewMeshName(meshName);
                    params.setNewPassword(meshPsw);
                    params.setUpdateDeviceList(light.raw);
                    TelinkLightService.Instance().idleMode(true);
                    //加灯
                    TelinkLightService.Instance().updateMesh(params);
                } else {
                    ToastUtil.showToast("获取灯具地址失败");
                }
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 开始扫描
     * 名称为出厂名 kimascend
     *
     * @param delay
     */
    private void startScan(int delay) {
        Log.d(TAG, "startScan: " + isScan);
        if (isScan) {
            mHandler.postDelayed(() -> {
                //扫描参数
                LeScanParameters params = LeScanParameters.create();
                params.setMeshName(Config.FACTORY_NAME);
//                params.setMeshName("telink_mesh1");
                params.setOutOfMeshName("kick");
                params.setTimeoutSeconds(15);
                params.setScanMode(false);
                TelinkLightService.Instance().startScan(params);

            }, delay);
        }

    }


    /**
     * 停止扫描
     */
    Runnable mStopScan = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: stop scan");
            isScan = false;
            TelinkLightService.Instance().idleMode(true);
            mBinding.setIsScanning(false);
        }
    };


    @Override
    public void performed(Event<String> event) {
        Log.d(TAG, "performed() called with: event = [" + event.getType() + "]");
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                isScan = false;
                TelinkLightService.Instance().idleMode(true);
                mBinding.setIsScanning(false);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                onMeshOffline();
            case MeshEvent.ERROR:
                onMeshEvent((MeshEvent) event);
                break;
        }
    }


    /**
     * 设备离线
     */
    private void onMeshOffline() {
        showToast("设备离线");

    }

    private void onMeshEvent(MeshEvent event) {
        showToast(getString(R.string.start_bollue2));
//        new AlertDialog.Builder(getActivity()).setMessage(getResources().getString(R.string.start_bollue2)).show();
    }


    /**
     * 处理扫描事件
     *
     * @param event
     */
    private void onLeScan(LeScanEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.d(TAG, "DeviceInfo:" + deviceInfo);
        Light light = new Light(deviceInfo);
        light.mDescription = String.format("%s\n%s", deviceInfo.meshName, deviceInfo.macAddress);
        //用来区分设备类型
        light.type = deviceType.get(deviceInfo.productUUID);
        mAdapter.addLight(light);

    }


    /**
     * 设备状态变更  主要用来监听mesh网络修改的结果
     *
     * @param event
     */
    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        Log.d(TAG, " onDeviceStatusChanged: " + deviceInfo.toString());
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED: {
//                Light light = mAdapter.getLight(deviceInfo.meshAddress);
                Light light = mAdapter.getLightByMAC(deviceInfo.macAddress);
                if (light != null) {
                    light.mAddStatus.set(BindingAdapters.ADDED);
                    light.raw.meshAddress = deviceInfo.meshAddress;
                    light.raw.meshName = deviceInfo.meshName;
                    light.raw.deviceName = deviceInfo.deviceName;
                    //上传到网络
                    updateLight(light);
                }
                isAdd = false;
            }
            break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
            case LightAdapter.STATUS_LOGOUT: {
                Light light = mAdapter.getLightByMAC(deviceInfo.macAddress);
                //如果A灯添加成功 下一次添加B灯时会收到A灯登出 更新失败的回调 防止状态被修改 判断是否已经修改成功
                if (light != null && light.mAddStatus.get() != ADDED) {
                    light.mAddStatus.set(ADD);
                    isAdd = false;
                }
                //已添加的设备是登出状态，meshName已改变，也会扫描到，在此排除
                if (deviceInfo.meshName.equals(Config.FACTORY_NAME)) {
                    ToastUtil.showToast("添加失败");
                }
            }
            break;
        }
    }

    /**
     * 条目的点击事件回调
     * 点击条目 切换开关
     * 长按进入灯具设置页面
     * 点击右侧添加按钮 修改mesh名称和密码
     * <p>
     * 配合Databinding 在item_light_add.xml中使用
     * Light 的字段使用Observable 这样只要在这里修改值，相关UI会自动更新
     */
    private OnHandleNewLightListener mOnHandleNewLightListener = new OnHandleNewLightListener() {
        @Override
        public void onItemClick(Light light) {

        }

        @Override
        public void onAddClick(Light light) {
            Log.d(TAG, "onAddClick: " + light.toString());
            if (light.mAddStatus.get() == ADD) {
                if (!isAdd) {
                    isAdd = true;
                    viewModel.getDeviceMeshAddress(light);
                } else {
                    ToastUtil.showToast("正在添加");
                }
            }
        }

        @Override
        public boolean onItemLongClick(Light light) {
            return true;
        }
    };

    /**
     * 更新用户名和密码成功后上传到网络
     * 如果上传失败 是否需要恢复灯具配置
     *
     * @param light
     */
    private void updateLight(Light light) {
        String gatewayId = "d-" + java.util.UUID.randomUUID().toString();
        String meshAddress = String.valueOf(light.raw.meshAddress);
        String factoryId = String.valueOf(light.raw.meshUUID);
        String productUuid = String.valueOf(light.raw.productUUID);
        String typeId = String.valueOf(light.type);

        //参数存入集合
        Map<String, String> map = new ArrayMap<>();
        map.put("name", light.raw.deviceName);
        map.put("deviceId", meshAddress);
        map.put("mac", light.raw.macAddress);
        map.put("gatewayId", gatewayId);
        map.put("typeId", typeId);
        map.put("factoryId", factoryId);
        map.put("productUuid", productUuid);
        viewModel.addLampRequest.setValue(map);

    }


    /**
     * 刷新当前设备
     */
    private void refresh() {
        Log.d(TAG, "重新扫描");
//        暂停当前扫描 有可能还在扫描当中
        TelinkLightService.Instance().idleMode(true);
//        清空设备列表
        mAdapter.clear();
//        显示扫描等待框
        mBinding.setIsScanning(true);
        isScan = true;
//        开始扫描
        startScan(100);
//        30秒后停止
        mHandler.postDelayed(mStopScan, SCANNING_SPAN);
    }

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                refresh();
                break;
            case R.id.iv_back:
                getActivity().finish();
                break;
        }
    }

}