

package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.CommonItemClickListener;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonItemAdapter;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentAddSceneBinding;
import com.example.ledwisdom1.databinding.LayoutEditBinding;
import com.example.ledwisdom1.databinding.LayoutGroupSceneLampBinding;
import com.example.ledwisdom1.databinding.LayoutSelectLampBinding;
import com.example.ledwisdom1.databinding.SceneLayoutAddBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.home.LampAdapter;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.example.ledwisdom1.utils.ToastUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 同场景，不同之处在于设备选择，情景即可以选择设备也可以选择场景来控制
 * 添加或修改场景
 */


public class AddSceneFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {

    public static final String TAG = AddSceneFragment.class.getSimpleName();
    private FragmentAddSceneBinding binding;
    private SceneLayoutAddBinding sceneLayoutAddBinding;
    private LayoutEditBinding editBinding;

    private CommonItemAdapter commonItemAdapter;

    private GroupSceneViewModel viewModel;
    //用来编辑使用景名称
    public ObservableField<String> sceneName = new ObservableField<>("");
    //区分添加还是更新
    private boolean MODE_ADD = true;
    private LayoutSelectLampBinding selectLampBinding;
    private LampAdapter lampAdapter;
    private LampAdapter groupSceneLampAdapter;

    public static AddSceneFragment newInstance() {
        Bundle args = new Bundle();
        AddSceneFragment fragment = new AddSceneFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_scene, container, false);
        populateViewPager();
        return binding.getRoot();
    }


    private void populateViewPager() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        //添加场景UI
        sceneLayoutAddBinding = DataBindingUtil.inflate(inflater, R.layout.scene_layout_add, null, false);
        sceneLayoutAddBinding.setHandler(this);
        sceneLayoutAddBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, "");
        CommonItem name = new CommonItem(1, "名称", true, -1, true, "请输入");
        CommonItem device = new CommonItem(2, "设备", true, -1, true, "请添加");
        CommonItem group = new CommonItem(2, "场景", true, -1, true, "请添加");
        commonItems.add(pic);
        commonItems.add(name);
        commonItems.add(device);
        commonItems.add(group);
        commonItemAdapter = new CommonItemAdapter(commonItemClickListener, commonItems);
        sceneLayoutAddBinding.recyclerView.setAdapter(commonItemAdapter);

        //名称编辑页面
        editBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit, null, false);
        editBinding.setHandler(this);
        editBinding.setContent(sceneName);

        //灯具选择页面
        selectLampBinding = DataBindingUtil.inflate(inflater, R.layout.layout_select_lamp, null, false);
        selectLampBinding.setHandler(this);
        selectLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampAdapter = new LampAdapter(mOnHandleLampListener);
        //显示是否选中图片
        lampAdapter.setShowSelectIcon(true);
        selectLampBinding.recyclerView.setAdapter(lampAdapter);

        //场景或情景已有的灯具
        LayoutGroupSceneLampBinding layoutGroupSceneLampBinding = DataBindingUtil.inflate(inflater, R.layout.layout_group_scene_lamp, null, false);
        layoutGroupSceneLampBinding.setHandler(this);
        layoutGroupSceneLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupSceneLampAdapter = new LampAdapter(null);
        groupSceneLampAdapter.setShowSelectIcon(true);
        layoutGroupSceneLampBinding.recyclerView.setAdapter(groupSceneLampAdapter);


        List<View> views = new ArrayList<>();
        views.add(sceneLayoutAddBinding.getRoot());
        views.add(editBinding.getRoot());
        views.add(layoutGroupSceneLampBinding.getRoot());
        views.add(selectLampBinding.getRoot());

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(views);
        binding.viewPager.setAdapter(pagerAdapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        viewModel.groupSceneRequest.isGroup = false;
        subscribeUI(viewModel);
    }


    private void subscribeUI(GroupSceneViewModel viewModel) {
        viewModel.scene.observe(this, new Observer<Scene>() {
            @Override
            public void onChanged(@Nullable Scene scene) {
                MODE_ADD = null == scene;
                sceneLayoutAddBinding.setTitle(MODE_ADD ? "新建情景" : "修改情景");
                sceneLayoutAddBinding.setAdd(MODE_ADD);
                if (scene != null) {
                    viewModel.groupSceneRequest.sceneId = scene.getId();
                    CommonItem pic = commonItemAdapter.getItem(0);
                    //显示图片
                    pic.observableValue.set(Config.IMG_PREFIX.concat(scene.getIcon()));
                    // 显示名称
                    CommonItem name = commonItemAdapter.getItem(1);
                    name.observableValue.set(scene.getName());
                }
            }
        });


        viewModel.lampListObserver.observe(this, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    LampList body = apiResponse.body;
                    lampAdapter.addLampsForSelection(body.getList());
                }
            }
        });

        //        情景已选中灯具
        viewModel.sceneDevicesObserver.observe(this, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.isSuccessful()) {
                        GroupDevice groupDevice = apiResponse.body;
                        List<Lamp> lampsSelected = groupDevice.getList();
                        CommonItem device = commonItemAdapter.getItem(2);
                        device.observableValue.set(String.valueOf(groupDevice.getList().size()));
                        groupSceneLampAdapter.addLampsForSelection(lampsSelected);

                    }
                }
            }
        });

    }

    private CommonItemClickListener commonItemClickListener = commonItem -> {
        switch (commonItem.pos) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                CommonItem name = commonItemAdapter.getItem(1);
                sceneName.set(name.observableValue.get());
                binding.viewPager.setCurrentItem(1);
                break;
            case 2:
                if (MODE_ADD) {
                    binding.viewPager.setCurrentItem(3);
                } else {
                    binding.viewPager.setCurrentItem(2);
                }
                break;

        }
    };

    private OnHandleLampListener mOnHandleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
//            切换选择状态
            int status = lamp.lampStatus.get();
            if (BindingAdapters.LIGHT_HIDE == status) {
                List<Lamp> lampList = groupSceneLampAdapter.getLampList();
                if (!lampList.contains(lamp)) {
                    lampList.add(lamp);
                    lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                } else {
                    ToastUtil.showToast("已选择");
                }
            } else {
                groupSceneLampAdapter.removeLamp(lamp);
                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
            }
        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {

        }
    };


    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_SCENE);
                break;
            case R.id.cancel:
                binding.viewPager.setCurrentItem(0);
                break;
//                放弃灯具的选择
            case R.id.iv_back_select:
                binding.viewPager.setCurrentItem(2);
                break;
            // 选好灯具回退
            case R.id.iv_select:
                binding.viewPager.setCurrentItem(2);
                groupSceneLampAdapter.notifyDataSetChanged();
                break;
            //已有灯具的选择回退
            case R.id.iv_back_lamp:
                setLampsNum();
                break;
            case R.id.modify:
                setName();
                break;
//                添加灯具
            case R.id.add:
                binding.viewPager.setCurrentItem(3);
                break;
            case R.id.confirm:
                if (MODE_ADD) {
                    addGroup();
                } else {
                    updateGroup();
                }
                break;
            case R.id.delete:
                viewModel.deleteGroup(false);
                break;
            case R.id.clear:
                sceneName.set("");
                break;

        }
    }

    private void setLampsNum() {
        binding.viewPager.setCurrentItem(0);
        CommonItem item = commonItemAdapter.getItem(2);
        item.observableValue.set(String.valueOf(groupSceneLampAdapter.getItemCount()));
    }


    /**
     */

    private void setName() {
        String name = sceneName.get();
        if (TextUtils.isEmpty(name)) {
            editBinding.content.setError("名称不能为空");
            editBinding.content.requestFocus();
            return;
        }
        //  todo 关闭输入法
        binding.viewPager.setCurrentItem(0);
        // 显示名称
        CommonItem nameItem = commonItemAdapter.getItem(1);
        nameItem.observableValue.set(name);
        viewModel.groupSceneRequest.name = name;
    }

    //    添加场景
    private void addGroup() {
        if (TextUtils.isEmpty(viewModel.groupSceneRequest.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (null == viewModel.groupSceneRequest.pic) {
            Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (groupSceneLampAdapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
       /* if (TextUtils.isEmpty(viewModel.groupSceneRequest.deviceId) || "[]".equals(viewModel.groupSceneRequest.deviceId)) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }*/

        List<String> selectedNum = groupSceneLampAdapter.getIds();
        viewModel.groupSceneRequest.deviceId = new Gson().toJson(selectedNum);
        Log.d(TAG, "ids " + viewModel.groupSceneRequest.deviceId);
        viewModel.groupSceneLamps.clear();
        viewModel.groupSceneLamps.addAll(groupSceneLampAdapter.getLampList());
        viewModel.addGroupRequest.setValue(viewModel.groupSceneRequest);

    }

    //    更新场景
    private void updateGroup() {
        CommonItem itemName = commonItemAdapter.getItem(1);
        String name = itemName.observableValue.get();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.groupSceneRequest.name = name;

        if (TextUtils.isEmpty(viewModel.groupSceneRequest.deviceId) || "[]".equals(viewModel.groupSceneRequest.deviceId)) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.updateGroupRequest.setValue(viewModel.groupSceneRequest);

    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = commonItemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
        viewModel.groupSceneRequest.pic = file;
    }
}

