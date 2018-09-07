package com.example.ledwisdom1.device;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentGroupControlBinding;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BundleConstant;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.utils.MeshEventManager;
import com.example.ledwisdom1.view.RGBView;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.List;

/**
 * 场景下设备控制 开关 亮度  延时开关
 * 需要兼任蓝牙和网关控制
 */
public class GroupControlFragment extends Fragment /*implements EventListener<String>*/ {
    public static final String TAG = GroupControlFragment.class.getSimpleName();
    private FragmentGroupControlBinding binding;

    public GroupControlFragment() {
        // Required empty public constructor
    }

   /* public static GroupControlFragment newInstance(int meshAddress, int brightness, int status) {
        Bundle args = new Bundle();
        args.putInt("address", meshAddress);
        args.putInt("brightness", brightness);
        args.putInt("status", status);
        GroupControlFragment fragment = new GroupControlFragment();
        fragment.setArguments(args);
        return fragment;
    }*/


    public static Fragment newInstance(Bundle args) {
        GroupControlFragment fragment = new GroupControlFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_control, container, false);
        binding.titleBar.toolbar.setNavigationOnClickListener((view)->getActivity().finish());
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //使用工厂注入mesh address
        DeviceControlViewModel.Factory factory = new DeviceControlViewModel.Factory(getArguments().getInt(BundleConstant.ADDRESS));
        DeviceControlViewModel viewModel = ViewModelProviders.of(this, factory).get(DeviceControlViewModel.class);
//        使用Life observer监听页面的生命周期 并交给view model处理
        MeshEventManager.bindEventListener(this, viewModel.eventListener, SmartLightApp.INSTANCE());
//        逻辑都在view model
        binding.setViewModel(viewModel);
        binding.ivRgb.setOnColorChangedListenner(viewModel::onColorChanged);

    }

}
