package com.example.ledwisdom1.fragment;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.sevice.TelinkLightService;
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

/**
 * A simple {@link Fragment} subclass.
 * 处理蓝牙网络连接的基类
 * 获取某个mesh的灯具的信息、控制灯具都需要先连接
 * 默认连接出厂未修改mesh名称和密码的设备
 */
public abstract class BaseFragment extends Fragment implements EventListener<String> {
    private static final String TAG = BaseFragment.class.getSimpleName();
    public static String sFactoryName = "kimascend";
    public static String sFactoryPassword = "123456";


    private SmartLightApp mSmartLightApp;
    Handler mHandler = new Handler();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBlueToothStatusReceiver();
        mSmartLightApp = (SmartLightApp) getActivity().getApplication();

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        addEventListener();
//        autoConnect();
    }


    @Override
    public void onResume() {
        super.onResume();
        requireBlueTooth();

    }

    @Override
    public void onStop() {
        super.onStop();
        removeEventListener();
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBlueToothStatusReceiver);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void requireBlueTooth() {
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(getActivity())) {
            Toast.makeText(getActivity(), "ble not support", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.setNegativeButton("enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(getActivity());
                }
            });
            builder.show();
        }

    }

    private void addBlueToothStatusReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        getActivity().registerReceiver(mBlueToothStatusReceiver, filter);

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

    private void addEventListener() {
        mSmartLightApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        mSmartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        mSmartLightApp.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        mSmartLightApp.addEventListener(MeshEvent.OFFLINE, this);
        mSmartLightApp.addEventListener(MeshEvent.ERROR, this);
    }

    private void removeEventListener() {
        mSmartLightApp.removeEventListener(this);
    }

    /**
     * 自动重连
     * 使用蓝牙控制灯具之前需要连接mesh
     *
     */
    protected void autoConnect() {
        Log.d(TAG, "autoConnect() called");
        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {
                //自动重连参数
                Log.d(TAG, "connect");
                String meshName = "";
                String psw = "";
                /*if (mSmartLightApp.isEmptyMesh()) {
                    meshName = sFactoryName;
                    psw=sFactoryName;

                }else{
                    meshName = mSmartLightApp.getMesh().getMeshName();
                    psw = mSmartLightApp.getMesh().getMeshPassword();
                }*/
                Log.d(TAG, meshName);
                Log.d(TAG, psw);
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

    @Override
    public void performed(Event<String> event) {
        Log.d(TAG, "performed " + event.getType());

        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
//               onMeshError((MeshEvent) event);
                Toast.makeText(mSmartLightApp, "蓝牙出了问题 重启试试", Toast.LENGTH_SHORT).show();

                break;
            case ServiceEvent.SERVICE_CONNECTED:
                autoConnect();
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
//                onServiceDisconnected((ServiceEvent) event);
                break;
        }
    }

    abstract protected void onMeshOffline(MeshEvent event);


    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        String meshName = deviceInfo.meshName;
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                Log.d(TAG, "connecting success");
                Toast.makeText(mSmartLightApp, "连接成功 "+meshName, Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{});
                    }
                }, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                Toast.makeText(mSmartLightApp, "正在连接 "+meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connecting");
                break;
            case LightAdapter.STATUS_LOGOUT:
                Toast.makeText(mSmartLightApp, "失去连接 "+meshName, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "disconnect");
                break;
            default:
                break;
        }
    }

    protected abstract void onOnlineStatusNotify(NotificationEvent event);

}
