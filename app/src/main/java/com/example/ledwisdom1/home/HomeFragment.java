package com.example.ledwisdom1.home;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.common.AutoClearValue;
import com.example.ledwisdom1.databinding.FragmentHomeBinding;
import com.example.ledwisdom1.databinding.HomeLayoutDetailBinding;
import com.example.ledwisdom1.databinding.HomeLayoutEmptyBinding;
import com.example.ledwisdom1.databinding.HomePopMoreBinding;
import com.example.ledwisdom1.device.entity.LampCmd;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.HomeAdapter;
import com.example.ledwisdom1.mesh.MeshActivity2;
import com.example.ledwisdom1.mqtt.MQTTClient;
import com.example.ledwisdom1.scene.OnHandleSceneListener;
import com.example.ledwisdom1.scene.Scene;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.utils.ToastUtil;
import com.google.gson.Gson;
import com.telink.bluetooth.light.LightAdapter;

import java.util.ArrayList;
import java.util.List;

import ledwisdom1.example.com.zxinglib.camera.CaptureActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private AutoClearValue<FragmentHomeBinding> binding;
    private AutoClearValue<HomeLayoutDetailBinding> bindingDetail;
    private AutoClearValue<HomeLayoutEmptyBinding> bindingEmpty;
    private AutoClearValue<HomePopMoreBinding> bindingPop;
    private HomeViewModel viewModel;
    private PopupWindow popupWindow;
    private HomeSceneAdapter sceneAdapter;


    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        HomeLayoutDetailBinding layoutDetailBinding = DataBindingUtil.inflate(inflater, R.layout.home_layout_detail, container, false);
        HomeLayoutEmptyBinding homeLayoutEmptyBinding = DataBindingUtil.inflate(inflater, R.layout.home_layout_empty, container, false);
        HomePopMoreBinding homePopMoreBinding = DataBindingUtil.inflate(inflater, R.layout.home_pop_more, container, false);
        homeBinding.setHandler(this);
        layoutDetailBinding.setHandler(this);
        homeLayoutEmptyBinding.setHandler(this);
        homePopMoreBinding.setHandler(this);

        List<View> viewList = new ArrayList<>();
        viewList.add(layoutDetailBinding.getRoot());
        viewList.add(homeLayoutEmptyBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        homeBinding.viewPager.setAdapter(pagerAdapter);

        layoutDetailBinding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        layoutDetailBinding.recyclerView.addItemDecoration(new InsetDecoration(getActivity()));
        HomeAdapter adapter = new HomeAdapter();
        layoutDetailBinding.recyclerView.setAdapter(adapter);

        layoutDetailBinding.recyclerViewScene.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        sceneAdapter = new HomeSceneAdapter(mHandleSceneListener);
        layoutDetailBinding.recyclerViewScene.setAdapter(sceneAdapter);

        boolean blueTooth = SmartLightApp.INSTANCE().isBlueTooth();
        layoutDetailBinding.setBle(blueTooth);

        layoutDetailBinding.meshSwitch.setOnCheckedChangeListener(checkedChangeListener);

        binding = new AutoClearValue<>(this, homeBinding);
//        默认界面 蓝牙网路
        bindingDetail = new AutoClearValue<>(this, layoutDetailBinding);
//        没有蓝牙网路的界面
        bindingEmpty = new AutoClearValue<>(this, homeLayoutEmptyBinding);
//        弹出框
        bindingPop = new AutoClearValue<>(this, homePopMoreBinding);
        return homeBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        binding.get().setViewModel(viewModel);
        bindingDetail.get().setViewModel(viewModel);
        bindingEmpty.get().setViewModel(viewModel);
        subscribeUI(viewModel);

    }

    @Override
    public void onStart() {
        super.onStart();
        handleMesh();
    }

    public void handleMesh() {
        Profile profile = SmartLightApp.INSTANCE().getProfile();
        if (TextUtils.isEmpty(profile.meshId)) {
            binding.get().viewPager.setCurrentItem(1);
        } else {
            viewModel.sceneListRequest.setValue(1);
            binding.get().viewPager.setCurrentItem(0);
        }

        DefaultMesh mesh = SmartLightApp.INSTANCE().getDefaultMesh();
        bindingDetail.get().setMesh(mesh);
    }


    private void subscribeUI(HomeViewModel homeViewModel) {
        homeViewModel.sceneListObserver.observe(this, new Observer<Resource<List<Scene>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Scene>> listResource) {
                if (null != listResource.data) {
                    int size = listResource.data.size();
                    bindingDetail.get().setShowScene(size > 0);
                    if (size > 0) {
                        //最多显示三个
                        sceneAdapter.addScenes(listResource.data.subList(0, Math.min(size, 3)));
                    }
                } else {
                    bindingDetail.get().setShowScene(false);

                }
            }
        });




    }


    private OnHandleSceneListener mHandleSceneListener = new OnHandleSceneListener() {
        @Override
        public void onItemClick(Scene scene) {
            LightCommandUtils.loadScene(scene.getSceneId());
        }

        @Override
        public void onEditClick(Scene scene) {
        }
    };


    private CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
        SmartLightApp.INSTANCE().setBlueTooth(isChecked);
        bindingDetail.get().setBle(isChecked);
    };


    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.add_mesh: {
                MeshActivity2.start(getActivity(), MeshActivity2.ACTION_ADD_MESH);
            }
            break;
            case R.id.scan_mesh:
            case R.id.pop_scan_mesh:
                startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 1);
                popupWindow.dismiss();
                break;
            case R.id.more:
                if (popupWindow == null) {
                    popupWindow = new PopupWindow(bindingPop.get().getRoot(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                    popupWindow.setFocusable(true);
                }
                popupWindow.showAsDropDown(bindingDetail.get().more);
                break;
            case R.id.pop_mesh_list: {
                MeshActivity2.start(getActivity(), MeshActivity2.ACTION_MESH_LIST);
                popupWindow.dismiss();
            }
            break;
            case R.id.avatar: {
                MeshActivity2.start(getActivity(), MeshActivity2.ACTION_MESH_DETAIL, bindingDetail.get().getMesh());
            }
            break;
            case R.id.open_all:
                LightCommandUtils.toggleLamp(0xffff, true);
//                handleCommand(true);
                break;
            case R.id.close_all:
                LightCommandUtils.toggleLamp(0xffff, false);
//                handleCommand(false);
                break;
        }
    }

    private void handleCommand(boolean status) {
        if (handleMeshStatus()) {
            LightCommandUtils.toggleLamp(0xffff, status);
        }
        /*Integer value = viewModel.meshStatus().getValue();
        if (value != null) {
            switch (value) {
                case LightAdapter.STATUS_LOGIN:
                    LightCommandUtils.toggleLamp(0xffff, status);
                    break;
                case LightAdapter.STATUS_LOGOUT:
                    ToastUtil.showToast("失去连接");
                    break;
                case LightAdapter.STATUS_CONNECTING:
                    ToastUtil.showToast("正在连接");
                    break;
                case -1:
                    ToastUtil.showToast("蓝牙网络离线");
                    break;
                case -2:
                    ToastUtil.showToast("蓝牙出了问题 重启试试");
                    break;
            }
        }*/
    }

    private boolean handleMeshStatus() {
//        Integer value = viewModel.meshStatus().getValue();
        int value = SmartLightApp.INSTANCE().getMeshStatus();
        switch (value) {
            case LightAdapter.STATUS_LOGIN:
                return true;
            case LightAdapter.STATUS_LOGOUT:
                ToastUtil.showToast("失去连接");
                return false;
            case LightAdapter.STATUS_CONNECTING:
                ToastUtil.showToast("正在连接");
                return false;
            case -1:
                ToastUtil.showToast("蓝牙网络离线");
                return false;
            case -2:
                ToastUtil.showToast("蓝牙出了问题 重启试试");
                return false;
        }

        return false;

    }

    /**
     * 切换灯具的开关
     *
     * @param open
     */
    public void toggle(boolean open) {
        LightCommandUtils.toggleLamp(0xffff, true);
        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;
        byte[] params;
        if (open) {
            params = new byte[]{0x01, 0x00, 0x00};

        } else {
            params = new byte[]{0x00, 0x00, 0x00};
        }
        boolean blueTooth = SmartLightApp.INSTANCE().isBlueTooth();
        if (blueTooth) {
            TelinkLightService.Instance().sendCommandNoResponse(opcode, address, params);
        } else {
            LampCmd lampCmd = new LampCmd(5, 255, 1, "0", open ? 100 : 0);
            String message = new Gson().toJson(lampCmd);
            MQTTClient.INSTANCE().publishLampControlMessage("1102F483CD9E6123", message);
        }
    }

    /*扫描mesh二维码的返回结果*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            Log.d(TAG, "result " + result);
            viewModel.shareMeshRequest.setValue(result);
        }

    }

}
