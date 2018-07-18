package com.example.ledwisdom1.home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentDeviceBinding;
import com.example.ledwisdom1.databinding.ViewRecycleBinding;
import com.example.ledwisdom1.device.DeviceActivity;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.Hub;
import com.example.ledwisdom1.home.entity.HubList;
import com.example.ledwisdom1.utils.AutoClearValue;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备页面 包含灯具 网关 面板灯
 */
public class DeviceFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, CallBack {
    public static final String TAG = DeviceFragment.class.getSimpleName();

    private AutoClearValue<FragmentDeviceBinding> binding;
    private AutoClearValue<ViewRecycleBinding> bingingHub;
    private AutoClearValue<ViewRecycleBinding> bingingLamp;
    private HomeViewModel viewModel;
    private Handler handler = new Handler();
    //灯具
    private LampAdapter lampAdapter;
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

        List<View> viewList = new ArrayList<>();
        viewList.add(viewHubBinding.getRoot());
        viewList.add(viewLampBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        fragmentDeviceBinding.viewPager.setAdapter(pagerAdapter);

        hubAdapter=new HubAdapter(mHandleHubListener);
        viewHubBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewHubBinding.recyclerView.setAdapter(hubAdapter);

//        显示灯具列表
        lampAdapter = new LampAdapter(mHandleLampListener);
        viewLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewLampBinding.recyclerView.setAdapter(lampAdapter);

        binding = new AutoClearValue<>(this, fragmentDeviceBinding);
        bingingHub = new AutoClearValue<>(this, viewHubBinding);
        bingingLamp = new AutoClearValue<>(this, viewLampBinding);

        return fragmentDeviceBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        subscribeUI(viewModel);

    }



    private void subscribeUI(HomeViewModel viewModel) {
        viewModel.lampListObserver.observe(this, apiResponse -> {
            if (apiResponse == null) {
                Log.d(TAG, "get null");
                return;
            }
            if (apiResponse.isSuccessful()) {
                LampList body = apiResponse.body;
                List<Lamp> list = body.getList();
                lampAdapter.addLamps(list);
            }
        });

        viewModel.hubListObserver.observe(this, apiResponse -> {
            if (apiResponse == null) {
                Log.d(TAG, "get null");
                return;
            }
            if (apiResponse.isSuccessful()) {
                HubList body = apiResponse.body;
                List<Hub> list = body.getList();
                hubAdapter.addHubs(list);
            }
        });

        viewModel.deleteLampObserver.observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> resource) {
                if (resource.data!=null&&resource.data) {
                    lampAdapter.removeLamp(lamp);
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.addEventListener(NotificationEvent.ONLINE_STATUS, eventListener);
        smartLightApp.addEventListener(MeshEvent.OFFLINE, eventListener);
        //请求灯具列表第一页
        viewModel.lampListRequest.setValue(1);
        viewModel.hubListRequest.setValue(1);

    }

    @Override
    public void onStop() {
        super.onStop();
        SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
        smartLightApp.removeEventListener(eventListener);
    }

    private EventListener<String> eventListener = new EventListener<String>() {
        @Override
        public void performed(Event<String> event) {
            switch (event.getType()) {
                case NotificationEvent.ONLINE_STATUS:
                    onOnlineStatusNotify((NotificationEvent) event);
                    break;
                case MeshEvent.OFFLINE:
                    Log.d(TAG, "performed: on mesh off");
                    lampAdapter.meshOff();
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

        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int brightness = notificationInfo.brightness;
            Log.d(TAG, meshAddress+"meshAddress:" + brightness);
            //根据meshAddress查找
            Lamp lamp = lampAdapter.getLamp(meshAddress);
            if (lamp != null) {
                lamp.setBrightness(brightness);
                // 修改灯状态
                lamp.lampStatus.set(notificationInfo.connectStatus.getValue());
            }
//            light.mDescription=String.format(Locale.getDefault(),"%d\n%d",meshAddress,brightness);
        }
       /* handler.post(() -> lampAdapter.notifyDataSetChanged());*/
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
                break;
            case R.id.device_panel:
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
            Log.d(TAG, "onEditClick() called with: lamp = [" + hub + "]");
        }

        @Override
        public void onDeleteClick(Hub hub) {

        }
    };

    private Lamp lamp;
    private OnHandleLampListener mHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            Log.d(TAG, "lamp:" + lamp);
            Intent intent = new Intent(getActivity(), DeviceActivity.class);
            intent.putExtra("action", DeviceActivity.ACTION_LAMP_SETTING);
            // TODO: 2018/7/18 0018 统一使用deviceId
            intent.putExtra("meshAddress", lamp.getProductUuid());
            intent.putExtra("brightness", lamp.getBrightness());
            intent.putExtra("status", lamp.lampStatus.get());
            startActivity(intent);
        }

        @Override
        public void onEditClick(Lamp lamp) {
            /*Intent intent = new Intent(getActivity(), GeneralActivity.class);
            intent.putExtra(GeneralActivity.ACTION, GeneralActivity.ACTION_LAMB_DETAIL);
            intent.putExtra("lamp", lamp);
            startActivity(intent);*/
        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            DeviceFragment.this.lamp=lamp;
            viewModel.deleteLampRequest.setValue(lamp.getId());
//            deleteDeviceFromRemote(lamp);

        }
    };

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
                intent.putExtra("action", DeviceActivity.ACTION_ADD_DEVICE);
                startActivity(intent);
                break;
        }
    }


}
