

package com.example.ledwisdom1.home;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentDeviceBinding;
import com.example.ledwisdom1.databinding.ViewRecycleBinding;
import com.example.ledwisdom1.device.DeviceActivity;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.entity.Hub;
import com.example.ledwisdom1.home.entity.HubList;
import com.example.ledwisdom1.common.AutoClearValue;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.utils.ToastUtil;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.GetAlarmNotificationParser;
import com.telink.bluetooth.light.NotificationInfo;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * 设备页面 包含灯具 网关 面板灯
 */

public class DeviceFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, CallBack {
    public static final String TAG = DeviceFragment.class.getSimpleName();

    private AutoClearValue<FragmentDeviceBinding> binding;
    private HomeViewModel viewModel;
    //灯具
    private LampAdapter2 lampAdapter;
    private LampAdapter2 socketAdapter;
    private LampAdapter2 panelAdapter;
    private HubAdapter hubAdapter;

    public DeviceFragment() {
        // Required empty public constructor
    }

    public static DeviceFragment newInstance() {
        Bundle args = new Bundle();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDeviceBinding fragmentDeviceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device, container, false);
        fragmentDeviceBinding.device.setOnCheckedChangeListener(this);
        fragmentDeviceBinding.setHandler(this);

        ViewRecycleBinding viewHubBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);
        ViewRecycleBinding viewLampBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);
        ViewRecycleBinding viewSocketBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);
        ViewRecycleBinding viewPanelBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycle, container, false);

        List<View> viewList = new ArrayList<>();
        viewList.add(viewHubBinding.getRoot());
        viewList.add(viewLampBinding.getRoot());
        viewList.add(viewSocketBinding.getRoot());
        viewList.add(viewPanelBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        fragmentDeviceBinding.viewPager.setAdapter(pagerAdapter);

        hubAdapter = new HubAdapter(mHandleHubListener);
        viewHubBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewHubBinding.recyclerView.setAdapter(hubAdapter);

//        显示灯具列表
        lampAdapter = new LampAdapter2(mHandleLampListener);
        viewLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewLampBinding.recyclerView.setAdapter(lampAdapter);

        socketAdapter = new LampAdapter2(mHandleLampListener);
        viewSocketBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewSocketBinding.recyclerView.setAdapter(socketAdapter);

        panelAdapter = new LampAdapter2(mHandleLampListener);
        viewPanelBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewPanelBinding.recyclerView.setAdapter(panelAdapter);

        binding = new AutoClearValue<>(this, fragmentDeviceBinding);

        return fragmentDeviceBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        addEventListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        subscribeUI(viewModel);
        viewModel.deviceListRequest.setValue(1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        removeEventListener();
    }

    private void subscribeUI(HomeViewModel viewModel) {
        viewModel.deviceListObserver.observe(this, new Observer<Resource<List<Lamp>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Lamp>> resource) {
                binding.get().setResource(resource);
                List<Lamp> data = resource.data;
                if (data != null) {
                    List<Lamp> lamps = new ArrayList<>();
                    List<Lamp> sockets = new ArrayList<>();
                    List<Lamp> panels = new ArrayList<>();
                    for (Lamp lamp : data) {
                        switch (lamp.getTypeId()) {
                            case Config.LAMP_TYPE:
                                lamps.add(lamp);
                                break;
                            case Config.SOCKET_TYPE:
                                sockets.add(lamp);
                                break;
                            case Config.PANEL_TYPE:
                                panels.add(lamp);
                                break;
                        }
                    }
                    lampAdapter.replaceLamps(lamps);
                    socketAdapter.replaceLamps(sockets);
                    panelAdapter.replaceLamps(panels);
                } else {
                    lampAdapter.replaceLamps(data);
                    socketAdapter.replaceLamps(data);
                    panelAdapter.replaceLamps(data);
                }
            }
        });

        viewModel.hubListObserver.observe(this, apiResponse -> {
            if (apiResponse == null) {
                Log.d(TAG, "get null");
                return;
            }
            if (apiResponse.isSuccessful() && apiResponse.body != null) {
                HubList body = apiResponse.body;
                List<Hub> list = body.getList();
                hubAdapter.addHubs(list);
            }
        });

        viewModel.deleteLampObserver.observe(this, new Observer<Resource<Lamp>>() {
            @Override
            public void onChanged(@Nullable Resource<Lamp> resource) {
                if (Status.ERROR == resource.status) {
                    ToastUtil.showToast(resource.message);
                }
            }
        });

        viewModel.deleteHubObserver.observe(this, new Observer<Resource<Hub>>() {
            @Override
            public void onChanged(@Nullable Resource<Hub> hubResource) {
                if (Status.SUCCESS == hubResource.status) {
                    hubAdapter.removeLamp(hubResource.data);
                } else if (Status.ERROR == hubResource.status) {
                    ToastUtil.showToast(hubResource.message);
                }
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        viewModel.hubListRequest.setValue(1);
    }

    private void addEventListener() {
//        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
//        smartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, eventListener);
//        smartLightApp.addEventListener(NotificationEvent.GET_ALARM, eventListener);
//        smartLightApp.addEventListener(NotificationEvent.GET_TIME, eventListener);
//        smartLightApp.addEventListener(MeshEvent.OFFLINE, eventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取灯具的时间
//        LightCommandUtils.getLampTime();
//        LightCommandUtils.getAlarm();

    }

    private void removeEventListener() {
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private EventListener<String> eventListener = new EventListener<String>() {
        @Override
        public void performed(Event<String> event) {
            Log.d(TAG, "event type" + event.getType());
            switch (event.getType()) {
                case NotificationEvent.ONLINE_STATUS:
                    onOnlineStatusNotify((NotificationEvent) event);
                    break;
                case NotificationEvent.GET_TIME: {
                    NotificationEvent notificationEvent = (NotificationEvent) event;
                    NotificationInfo args = notificationEvent.getArgs();
                    byte[] params = args.params;
                    String s = Arrays.toString(params);
                    Log.d(TAG, "param " + s);
                    Calendar calendar = (Calendar) notificationEvent.parse();
                    String format = DateFormat.getDateTimeInstance().format(calendar.getTimeInMillis());
                    Log.d(TAG, format);
                    break;
                }
                case NotificationEvent.GET_ALARM: {
                    NotificationEvent notificationEvent = (NotificationEvent) event;
                    NotificationInfo args = notificationEvent.getArgs();
                    byte[] params = args.params;
                    String s = Arrays.toString(params);
                    Log.d(TAG, "param " + s);
                    GetAlarmNotificationParser.AlarmInfo alarmInfo = (GetAlarmNotificationParser.AlarmInfo) notificationEvent.parse();
                    if (alarmInfo != null) {
                        Log.d(TAG, "alarm info" + alarmInfo.toString());
                    }
                }
                break;
                case MeshEvent.OFFLINE:
                    viewModel.onMeshOff();
                    break;
            }
        }
    };

    @WorkerThread
    protected void onOnlineStatusNotify(NotificationEvent event) {

        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList
                = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;
       /* for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress + "meshAddress:" + brightness);
            viewModel.updateDeviceStatus(brightness, meshAddress);
        }*/
        viewModel.updateDeviceStatus(notificationInfoList);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.device_hub:
                binding.get().viewPager.setCurrentItem(0);
                break;
            case R.id.device_lamp:
                binding.get().viewPager.setCurrentItem(1);
                break;
            case R.id.device_socket:
                binding.get().viewPager.setCurrentItem(2);
                break;
            case R.id.device_panel:
                binding.get().viewPager.setCurrentItem(3);
                break;
        }

    }

    private OnHandleHubListener mHandleHubListener = new OnHandleHubListener() {
        @Override
        public void onItemClick(Hub hub) {
            Log.d(TAG, "onItemClick() called with: lamp = [" + hub + "]");
        }

        @Override
        public void onEditClick(Hub hub) {
        }

        @Override
        public void onDeleteClick(Hub hub) {
            DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
            if (defaultMesh.isMine) {
                viewModel.deleteHubRequest.setValue(hub);
            }else{
                ToastUtil.showToast("不是自己的蓝牙网络");
            }
        }
    };

    private OnHandleLampListener mHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            if (lamp.getBrightness()<0) {
                ToastUtil.showToast("设备已离线");
            }else{
                DeviceActivity.start(getActivity(), DeviceActivity.ACTION_LAMP_SETTING, lamp);
            }
        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            DefaultMesh defaultMesh = SmartLightApp.INSTANCE().getDefaultMesh();
            if (defaultMesh.isMine) {
                viewModel.deleteLampRequest.setValue(lamp);
            }else{
                ToastUtil.showToast("不是自己的蓝牙网络");
            }
        }
    };

    boolean toggle = true;

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
                intent.putExtra("action", DeviceActivity.ACTION_ADD_DEVICE);
                //添加类型
                intent.putExtra("type", binding.get().viewPager.getCurrentItem());
                //如果添加成功会设置成功信号
                startActivityForResult(intent, 1);
                break;
            case R.id.temp:
                if (toggle) {
                    LightCommandUtils.addAlarm();
                } else {
                    LightCommandUtils.getAlarm();
                }
                toggle = !toggle;
                break;
            case R.id.iv_search:
                ToastUtil.showToast("暂未实现");
                break;
        }
    }

    //    接到成功信息 更新灯具列表
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.deviceListRequest.setValue(1);
        }

    }
}

