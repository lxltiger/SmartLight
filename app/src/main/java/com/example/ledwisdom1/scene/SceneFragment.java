

package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
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
import com.example.ledwisdom1.adapter.SelectedLampAdapter;
import com.example.ledwisdom1.adapter.UnSelectedLampAdapter;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentSceneBinding;
import com.example.ledwisdom1.databinding.LayoutDeviceSettingBinding;
import com.example.ledwisdom1.databinding.LayoutEditBinding;
import com.example.ledwisdom1.databinding.LayoutGroupListBinding;
import com.example.ledwisdom1.databinding.LayoutGroupToSettingBinding;
import com.example.ledwisdom1.databinding.LayoutLampSelectedBinding;
import com.example.ledwisdom1.databinding.LayoutLampToSettingBinding;
import com.example.ledwisdom1.databinding.LayoutLampUnselectedBinding;
import com.example.ledwisdom1.databinding.LayoutSceneBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.utils.ToastUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;


/**
 * 添加或修改场景
 * UI同场景
 * 情景即可以选择设备也可以选择场景来控制
 * 情景是对选择的灯具设置
 */


public class SceneFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {

    public static final String TAG = SceneFragment.class.getSimpleName();
    private FragmentSceneBinding binding;
    private LayoutEditBinding editBinding;

    private CommonItemAdapter itemAdapter;
    //未选灯具列表
    private UnSelectedLampAdapter unSelectedLampAdapter;
    //已选灯具列表 修改的时候才使用
    private SelectedLampAdapter selectedLampAdapter;
    private GroupForSceneAdapter groupForSceneAdapter;

    private SceneViewModel viewModel;
    //添加或修改场景的参数
    private SceneRequest sceneRequest = new SceneRequest();
    private LayoutGroupToSettingBinding groupToSettingBinding;
    private LayoutDeviceSettingBinding deviceSettingBinding;
    private LampForSceneAdapter lampForSceneAdapter;

    public static SceneFragment newInstance(Scene scene) {
        Bundle args = new Bundle();
        args.putParcelable("scene", scene);
        SceneFragment fragment = new SceneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Scene scene = getArguments().getParcelable("scene");
        if (scene != null && !TextUtils.isEmpty(scene.getId())) {
            sceneRequest.isAdd = false;
            sceneRequest.name = scene.getName();
            sceneRequest.imageUrl = Config.IMG_PREFIX.concat(scene.getIcon());
            sceneRequest.sceneId = scene.getId();
            sceneRequest.sceneAddress = scene.getSceneId();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene, container, false);

        LayoutSceneBinding sceneBinding = DataBindingUtil.inflate(inflater, R.layout.layout_scene, container, false);
        sceneBinding.setHandler(this);
        sceneBinding.setAdd(sceneRequest.isAdd);
        sceneBinding.setTitle(sceneRequest.isAdd ? "新建情景" : "修改情景");
        itemAdapter = new CommonItemAdapter(itemClickListener, generateItems(sceneRequest));
        sceneBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sceneBinding.recyclerView.setAdapter(itemAdapter);

        editBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit, container, false);
        editBinding.setHandler(this);
        editBinding.setName(sceneRequest.name);

        LayoutLampSelectedBinding lampSelectedBinding = DataBindingUtil.inflate(inflater, R.layout.layout_lamp_selected, container, false);
        lampSelectedBinding.setHandler(this);
        selectedLampAdapter = new SelectedLampAdapter(handleLampListener);
        lampSelectedBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampSelectedBinding.recyclerView.setAdapter(selectedLampAdapter);


        LayoutLampUnselectedBinding lampUnselectedBinding = DataBindingUtil.inflate(inflater, R.layout.layout_lamp_unselected, container, false);
        lampUnselectedBinding.setHandler(this);
        unSelectedLampAdapter = new UnSelectedLampAdapter(handleLampListener);
        lampUnselectedBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampUnselectedBinding.recyclerView.setAdapter(unSelectedLampAdapter);

        LayoutGroupListBinding groupListBinding = DataBindingUtil.inflate(inflater, R.layout.layout_group_list, container, false);
        groupListBinding.setHandler(this);
        groupForSceneAdapter = new GroupForSceneAdapter();
        groupListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupListBinding.recyclerView.setAdapter(groupForSceneAdapter);

        //要设置的场景
        groupToSettingBinding = DataBindingUtil.inflate(inflater, R.layout.layout_group_to_setting, container, false);
        groupToSettingBinding.setHandler(this);
        //要设置的灯具
        LayoutLampToSettingBinding lampToSettingBinding = DataBindingUtil.inflate(inflater, R.layout.layout_lamp_to_setting, container, false);
        lampForSceneAdapter = new LampForSceneAdapter(handleLampSettingListener);
        lampToSettingBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampToSettingBinding.recyclerView.setAdapter(lampForSceneAdapter);

        lampToSettingBinding.setHandler(this);
//        设置页面
        deviceSettingBinding = DataBindingUtil.inflate(inflater, R.layout.layout_device_setting, container, false);
        deviceSettingBinding.setHandler(this);


        //五个页面分别是场景列表 名称编辑  情景详情  已选灯具 未选灯具
        List<View> viewList = new ArrayList<>();
        viewList.add(groupListBinding.getRoot());
        viewList.add(editBinding.getRoot());
        viewList.add(sceneBinding.getRoot());
        viewList.add(lampSelectedBinding.getRoot());
        viewList.add(lampUnselectedBinding.getRoot());
        viewList.add(groupToSettingBinding.getRoot());
        viewList.add(lampToSettingBinding.getRoot());
        viewList.add(deviceSettingBinding.getRoot());

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        binding.viewPager.setAdapter(pagerAdapter);
        //显示第三页
        binding.viewPager.setCurrentItem(2);
        return binding.getRoot();
    }

    public List<CommonItem> generateItems(SceneRequest sceneRequest) {
        List<CommonItem> items = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, sceneRequest.imageUrl);
        CommonItem name = new CommonItem(1, "名称", true, -1, true, TextUtils.isEmpty(sceneRequest.name) ? "请输入" : sceneRequest.name);
        CommonItem device = new CommonItem(2, "设备", true, -1, true, "请添加");
//        CommonItem group = new CommonItem(3, "场景", true, -1, true, "请添加");
        items.add(pic);
        items.add(name);
        items.add(device);
//        items.add(group);
        return items;
    }

    //两个adapter 共用一个
    private OnHandleLampListener handleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
//            对未选的设备进行选择标记 确定的时候将移动lamp
            int status = lamp.lampStatus.get();
            if (BindingAdapters.LIGHT_HIDE == status) {
                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
            } else {
                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
            }
        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            selectedLampAdapter.removeLamp(lamp);
            unSelectedLampAdapter.addLamp(lamp);
        }
    };


    private OnHandleLampListener handleLampSettingListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
            deviceSettingBinding.setLamp(lamp);
            binding.viewPager.setCurrentItem(7);
        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {

        }
    };

    private void updateSelectDeviceNum() {
        //已选设备数量
        int itemCount = selectedLampAdapter.getItemCount();
        CommonItem item = itemAdapter.getItem(2);
        item.observableValue.set(itemCount == 0 ? "请添加" : String.valueOf(itemCount));
    }


    private CommonItemClickListener itemClickListener = commonItem -> {
        switch (commonItem.pos) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                binding.viewPager.setCurrentItem(1);
                break;
            case 2:
                //添加灯具首先判断是否已选择场景
                String selectedGroup = groupForSceneAdapter.getSelectedGroupId();
                if (!TextUtils.isEmpty(selectedGroup)) {
                    showToast("已选择场景，不能再选择灯具");
                    return;
                }
                if (sceneRequest.isAdd) {
                    binding.viewPager.setCurrentItem(4);
                } else {
                    binding.viewPager.setCurrentItem(3);
                }
                break;
            case 3:
                if (selectedLampAdapter.getItemCount() != 0) {
                    showToast("已选择灯具，不能再选择场景");
                    return;
                }
                binding.viewPager.setCurrentItem(0);
                if (groupForSceneAdapter.getItemCount() == 0) {
                    binding.setIsLoading(false);
                    viewModel.groupListRequest.setValue(1);
                }
                break;
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SceneViewModel.class);
        subscribeUI(viewModel);
        //获取灯具 如果是修改，sceneId不为空 在全部灯具中会将已选的灯具会被标记为selected
        viewModel.lampListRequest.setValue(sceneRequest.sceneId);
    }


    private void subscribeUI(SceneViewModel viewModel) {
        viewModel.lampListObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    selectedLampAdapter.addLamps(lamps);
                    unSelectedLampAdapter.addLamps(lamps);
                    //记录旧的设备
                    sceneRequest.oldDeviceId = selectedLampAdapter.getIds();
                    updateSelectDeviceNum();
                } else {
                    ToastUtil.showToast("没有数据");
                }
            }
        });

        viewModel.groupListObserver.observe(this, new Observer<ApiResponse<GroupList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupList> apiResponse) {
                binding.setIsLoading(false);
                if (apiResponse.isSuccessful() && apiResponse.body != null) {
                    List<Group> list = apiResponse.body.getList();
                    if (list != null) {
                        groupForSceneAdapter.addGroup(list);
                    } else {
                        showToast("还没有添加场景");
                    }
                } else {
                    showToast("获取场景列表失败");
                }
            }
        });

        viewModel.addSceneObserver.observe(this, new Observer<SceneRequest>() {
            @Override
            public void onChanged(@Nullable SceneRequest sceneRequest) {
                binding.setIsLoading(false);
                if (sceneRequest != null) {
                    if (sceneRequest.isGroupSetting) {
                        Group selectedGroup = groupForSceneAdapter.getSelectedGroup();
                        if (selectedGroup != null) {
                            binding.viewPager.setCurrentItem(5);
                            groupToSettingBinding.setGroup(selectedGroup);
                        } else {
                            showToast("场景设置失败");
                        }
                    } else {
                        List<Lamp> lamps = selectedLampAdapter.getmLampList();
                        lampForSceneAdapter.addLamps(lamps);
                        binding.viewPager.setCurrentItem(6);

                    }
                    showToast("情景景创建成功");
                } else {
                    showToast("情景景创建失败");
                }
            }
        });

        viewModel.deleteSceneObserver.observe(this, new Observer<RequestResult>() {
            @Override
            public void onChanged(@Nullable RequestResult apiResponse) {
                if (apiResponse != null) {
                    showToast("删除成功");

                    getActivity().onBackPressed();
                } else {
                    showToast("删除失败");
                }
            }
        });

        viewModel.deviceSettingObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                binding.setIsLoading(false);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    if (sceneRequest.isGroupSetting) {
                        binding.viewPager.setCurrentItem(5);
                        groupToSettingBinding.setIsSetting(true);
                    }else{
                        binding.viewPager.setCurrentItem(6);
                        Lamp lamp = deviceSettingBinding.getLamp();
                        lamp.isSetting = true;
                        lampForSceneAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast("设置失败");
                }
            }
        });

        viewModel.updateSceneObserver.observe(this, new Observer<SceneRequest>() {
            @Override
            public void onChanged(@Nullable SceneRequest groupRequest) {
                binding.setIsLoading(false);
                if (groupRequest != null) {
                    showToast("更新成功");
                    List<Lamp> lamps = selectedLampAdapter.getmLampList();
                    lampForSceneAdapter.addLamps(lamps);
                    binding.viewPager.setCurrentItem(6);
                } else {
                    showToast("更新失败");
                }
            }
        });
    }


    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                handleBackPressed();
                break;
            case R.id.confirm:
                handleConfirm();
                break;
            case R.id.add:
                binding.viewPager.setCurrentItem(4);
                break;
            case R.id.clear:
                editBinding.setName("");
                break;
            case R.id.delete:
                viewModel.deleteScene(sceneRequest);
                break;
            case R.id.rl_group:
                binding.viewPager.setCurrentItem(7);
                break;
        }
    }

    //回退的处理
    private void handleBackPressed() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 7:
                if (sceneRequest.isGroupSetting) {
                    binding.viewPager.setCurrentItem(5);
                }else{
                    binding.viewPager.setCurrentItem(6);
                }
                break;
            case 5:
            case 6:
//                binding.viewPager.setCurrentItem(4);
                getActivity().onBackPressed();
                break;
            case 4:
                unSelectedLampAdapter.resetLampStatus();
                binding.viewPager.setCurrentItem(3);
                break;
            case 3:
                binding.viewPager.setCurrentItem(2);
                updateSelectDeviceNum();
                break;
            case 2:
                getActivity().onBackPressed();
                break;
            case 1:
                binding.viewPager.setCurrentItem(2);
                break;
            case 0:
                //放弃场景选择
                groupForSceneAdapter.resetGroupStatus();
                binding.viewPager.setCurrentItem(2);
                break;
        }

    }

    private void handleConfirm() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            //灯具设置页面
            case 7:
                handleLightSetting();

                break;
            //场景完成设置
            case 5:
            case 6:
                getActivity().onBackPressed();
                break;
            case 4:
                List<Lamp> lamps = unSelectedLampAdapter.removeSelectLamps();
                selectedLampAdapter.addSelectedLamp(lamps);
                binding.viewPager.setCurrentItem(3);
                break;
            case 2:
                if (sceneRequest.isAdd) {
                    addScene();
                } else {
                    updateScene();
                }
                break;
            case 1:
                String name = editBinding.getName();
                if (TextUtils.isEmpty(name) || name.length() > 10) {
                    editBinding.content.setError("名称在1到10个字符之间");
                    editBinding.content.requestFocus();
                    return;
                }
                sceneRequest.name = name;
                //更新UI
                CommonItem item = itemAdapter.getItem(1);
                item.observableValue.set(name);
                binding.viewPager.setCurrentItem(2);
                break;
            case 0:
                CommonItem group = itemAdapter.getItem(3);
                Group selectedGroup = groupForSceneAdapter.getSelectedGroup();
                group.observableValue.set(null == selectedGroup ? "请添加" : selectedGroup.getName());
                binding.viewPager.setCurrentItem(2);
                break;
        }

    }

    private void handleLightSetting() {
        ArrayMap<String, Integer> map = new ArrayMap<>();
        int progress = deviceSettingBinding.sbBrightness.getProgress();
        int colorText = deviceSettingBinding.ivRgb.ColorText;
        int blue = Color.blue(colorText);
        int red = Color.red(colorText);
        int green = Color.green(colorText);
        map.put("light", progress);
        map.put("red", red);
        map.put("green", green);
        map.put("blue", blue);
        Gson gson = new Gson();
        String param = gson.toJson(map);
        ArrayMap<String, String> map2 = new ArrayMap<>();
        map2.put("objectId", sceneRequest.sceneId);
        map2.put("setting", param);
        if (sceneRequest.isGroupSetting) {
            map2.put("sonId", sceneRequest.groupIds);
        } else {
            Lamp lamp = deviceSettingBinding.getLamp();
            map2.put("sonId", lamp.getId());
            //添加到情景
            LightCommandUtils.addDeviceToScene(sceneRequest.sceneAddress, lamp.getDevice_id(), progress,red,green,blue);
        }
        String request = gson.toJson(map2);
        binding.setIsLoading(true);
        viewModel.deviceSettingRequest.setValue(request);
    }

    private void addScene() {
        if (TextUtils.isEmpty(sceneRequest.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sceneRequest.imageUrl)) {
            Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
            return;
        }
        sceneRequest.deviceId = selectedLampAdapter.getIds();
        sceneRequest.groupIds = groupForSceneAdapter.getSelectedGroupId();
        if (TextUtils.isEmpty(sceneRequest.deviceId) && TextUtils.isEmpty(sceneRequest.groupIds)) {
            Toast.makeText(getContext(), "灯具和场景至少选择一个", Toast.LENGTH_SHORT).show();
            return;
        }
        sceneRequest.isGroupSetting = TextUtils.isEmpty(sceneRequest.deviceId);
        binding.setIsLoading(true);
        viewModel.addSceneRequest.setValue(sceneRequest);

    }

    private void updateScene() {
        if (TextUtils.isEmpty(sceneRequest.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }

        sceneRequest.newDeviceId = selectedLampAdapter.getIds();
        if (TextUtils.isEmpty(sceneRequest.newDeviceId)) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        //很重要 影响方法调用和id传参
        sceneRequest.isGroupSetting = TextUtils.isEmpty(sceneRequest.newDeviceId);
        binding.setIsLoading(true);
        viewModel.updateSceneRequest.setValue(sceneRequest);
    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = itemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
        sceneRequest.imageUrl = file.getAbsolutePath();
        sceneRequest.pic = file;
    }
}

