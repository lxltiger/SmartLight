package com.example.ledwisdom1.device;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentGroupControlBinding;
import com.example.ledwisdom1.utils.BundleConstant;
import com.example.ledwisdom1.utils.MeshEventManager;

/**
 * 场景下设备控制 开关 亮度  延时开关
 * 需要兼任蓝牙和网关控制
 */
@Deprecated
public class GroupControlFragment extends Fragment /*implements EventListener<String>*/ {
    public static final String TAG = GroupControlFragment.class.getSimpleName();
    private FragmentGroupControlBinding binding;

    public GroupControlFragment() {
        // Required empty public constructor
    }

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
        DeviceControlViewModel.Factory factory = new DeviceControlViewModel.Factory(getArguments().getInt(BundleConstant.ADDRESS),80);
        DeviceControlViewModel viewModel = ViewModelProviders.of(this, factory).get(DeviceControlViewModel.class);
//        使用Life observer监听页面的生命周期 并交给view model处理
        MeshEventManager.bindEventListener(this, viewModel.eventListener, SmartLightApp.INSTANCE());
//        逻辑都在view model
        binding.setViewModel(viewModel);
        binding.ivRgb.setOnColorChangedListenner(viewModel::onColorChanged);

    }

}
