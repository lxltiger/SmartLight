package com.example.ledwisdom1.home;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.mqtt.MQTTClient;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.NavigatorController;
import com.example.ledwisdom1.utils.ToastUtil;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.io.IOException;

/**
 * 主页含4个UI
 * fixme 蓝牙连接bug；添加灯具后断开了连接，必须选择mesh来激活
 */
public class HomeActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener, EventListener<String> {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Handler handler = new Handler();
    private NavigatorController navigatorController;
    private HomeViewModel viewModel;

    private boolean isEmptyMesh = true;
    private DefaultMesh mesh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        addBlueToothStatusReceiver();
        //底部选项按钮
        RadioGroup rgMainRadioGroup = (RadioGroup) findViewById(R.id.rg_main_group);
        rgMainRadioGroup.setOnCheckedChangeListener(this);
        navigatorController = new NavigatorController(this, R.id.fl_main_container);
        if (savedInstanceState == null) {
            navigatorController.navigateToHome();
        }
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        subscribeUI(viewModel);

        try {
            MQTTClient.INSTANCE().startConnect();
        } catch (IOException e) {
            Log.d(TAG, "fail to start mqtt");
            e.printStackTrace();
        }

    }

    private void subscribeUI(HomeViewModel viewModel) {
        viewModel.profile.observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                if (profile != null) {
                    isEmptyMesh = TextUtils.isEmpty(profile.meshId);
                }
            }
        });

        viewModel.defaultMeshObserver.observe(this, defaultMesh -> {
            if (defaultMesh != null) {
                Log.d(TAG, "mesh " + defaultMesh.toString());
                mesh = defaultMesh;
                TelinkLightService.Instance().idleMode(true);
                autoConnect();
            }
        });

//        todo  切换mesh
        viewModel.shareMeshObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeActivity.this, response.body.resultMsg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void addBlueToothStatusReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mBlueToothStatusReceiver, filter);

    }

    private BroadcastReceiver mBlueToothStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: 蓝牙开启");
                        TelinkLightService.Instance().idleMode(true);
//                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: 蓝牙关闭");
                        break;
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        addEventListener();
        autoConnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requireBlueTooth();

    }

    @Override
    protected void onStop() {
        super.onStop();
        removeEventListener();
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBlueToothStatusReceiver);
        handler.removeCallbacks(null);
        MQTTClient.INSTANCE().exit();
    }

    private void requireBlueTooth() {
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(this)) {
            Toast.makeText(this, "ble not support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(HomeActivity.this);
                }
            });
            builder.show();
        }
    }

    private void addEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
//        smartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        smartLightApp.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        smartLightApp.addEventListener(MeshEvent.OFFLINE, this);
        smartLightApp.addEventListener(MeshEvent.ERROR, this);
    }

    private void removeEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                navigatorController.navigateToHome();
                break;
            case R.id.rb_device:
                if (isEmptyMesh) {
                    Toast.makeText(this, "请先添加一个家", Toast.LENGTH_SHORT).show();
                } else {
                    navigatorController.navigateToDevice();
                }
                break;
            case R.id.rb_scene:
                navigatorController.navigateToGroup();
                break;
            case R.id.rb_more:
                navigatorController.navigateToMore();
                break;
        }
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
//                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
//                onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
//               onMeshError((MeshEvent) event);
                ToastUtil.showToast("蓝牙出了问题 重启试试");
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                autoConnect();
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
//                onServiceDisconnected((ServiceEvent) event);
                break;
        }
    }

    /**
     * 自动重连
     * 使用蓝牙控制灯具之前需要连接mesh
     */
    protected void autoConnect() {
        Log.d(TAG, "autoConnect() called");
        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                //自动重连参数
                Log.d(TAG, "connect");
                if (null == mesh) {
                    return;
                }
                String meshName = mesh.name;
                String psw = mesh.password;
                Log.d(TAG, meshName + "--" + psw);
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(meshName);
                connectParams.setPassword(psw);
                connectParams.autoEnableNotification(true);
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams);
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(5000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        String meshName;
        if (mesh != null) {
            meshName = mesh.aijiaName;
        } else {
            meshName = deviceInfo.meshName;
        }
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                Log.d(TAG, "connecting success");
                Toast.makeText(this, "连接成功 " + meshName, Toast.LENGTH_SHORT).show();
                handler.postDelayed(() -> TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{}), 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                Toast.makeText(this, "正在连接 " + meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connecting");
                break;
            case LightAdapter.STATUS_LOGOUT:
                Toast.makeText(this, "失去连接 " + meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "disconnect");
                break;
            default:
                break;
        }
    }

    //    加载制定页面，采用隐藏策略，不移处已经初始化的Fragment
   /* private void loadFragment(@Nullable Fragment toFragment) {
        if (toFragment == null) return;

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (toFragment != mCurrentFragment) {
            if (!toFragment.isAdded()) {
                supportFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.fl_main_container, toFragment).commit();
            } else {
                supportFragmentManager.beginTransaction().hide(mCurrentFragment).show(toFragment).commit();
            }
//            记录当前页面
            mCurrentFragment = toFragment;
        }

    }*/
    //记录当前系统时间
    private long mExitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
