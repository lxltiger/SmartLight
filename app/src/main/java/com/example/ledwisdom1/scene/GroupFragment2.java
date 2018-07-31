package com.example.ledwisdom1.scene;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
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
import com.example.ledwisdom1.databinding.FragmentGroup2Binding;
import com.example.ledwisdom1.databinding.LayoutEditBinding;
import com.example.ledwisdom1.databinding.LayoutGroupBinding;
import com.example.ledwisdom1.databinding.LayoutLampSelectedBinding;
import com.example.ledwisdom1.databinding.LayoutLampUnselectedBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.example.ledwisdom1.utils.LightCommandUtils;
import com.example.ledwisdom1.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;


/**
 * 添加场景
 * 图片、场景名称、设备都不能为空
 * 先创建场景获取到id，使用id绑定设备 ，如果绑定设备失败需要删除场景 告知创建失败。
 * 以后完成结束此页面
 * <p>
 * 修改场景
 * 图片可以为空 但请求接口不同
 */


public class GroupFragment2 extends Fragment implements CallBack, ProduceAvatarFragment.Listener {
    public static final String TAG = GroupFragment2.class.getSimpleName();
    private FragmentGroup2Binding binding;
    private LayoutEditBinding editBinding;

    private CommonItemAdapter itemAdapter;
    //未选灯具列表
    private UnSelectedLampAdapter unSelectedLampAdapter;
    //已选灯具列表 修改的时候才使用
    private SelectedLampAdapter selectedLampAdapter;
    private GroupViewModel viewModel;
    //添加或修改场景的参数
    private GroupRequest groupRequest = new GroupRequest();
    /**
     * 已有场景详情 添加操作为null
     */
    private Group group;

    public static GroupFragment2 newInstance(Group group) {
        Bundle args = new Bundle();
        args.putParcelable("group", group);
        GroupFragment2 fragment = new GroupFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = getArguments().getParcelable("group");
        if (group != null && !TextUtils.isEmpty(group.getId())) {
            groupRequest.isAdd = false;
            groupRequest.name = group.getName();
            groupRequest.imageUrl = Config.IMG_PREFIX.concat(group.getIcon());
            groupRequest.groupId = group.getId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group2, container, false);

        LayoutGroupBinding groupBinding = DataBindingUtil.inflate(inflater, R.layout.layout_group, container, false);
        groupBinding.setHandler(this);
        groupBinding.setAdd(groupRequest.isAdd);
        groupBinding.setTitle(groupRequest.isAdd ? "新建场景" : "修改场景");
        itemAdapter = new CommonItemAdapter(itemClickListener, generateItems(groupRequest));
        groupBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupBinding.recyclerView.setAdapter(itemAdapter);

        editBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit, container, false);
        editBinding.setHandler(this);
        editBinding.setName(groupRequest.name);

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


        List<View> viewList = new ArrayList<>();
        viewList.add(editBinding.getRoot());
        viewList.add(groupBinding.getRoot());
        viewList.add(lampSelectedBinding.getRoot());
        viewList.add(lampUnselectedBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        binding.viewPager.setAdapter(pagerAdapter);
        //显示第二页
        binding.viewPager.setCurrentItem(1);
        return binding.getRoot();
    }

    public List<CommonItem> generateItems(GroupRequest groupRequest) {
        List<CommonItem> items = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, groupRequest.imageUrl);
        CommonItem name = new CommonItem(1, "名称", true, -1, true, TextUtils.isEmpty(groupRequest.name) ? "请输入" : groupRequest.name);
        CommonItem device = new CommonItem(2, "设备", true, -1, true, "请添加");
        items.add(pic);
        items.add(name);
        items.add(device);
        return items;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(GroupViewModel.class);
        subscribeUI(viewModel);
        //获取灯具 如果是修改，被选中的数据会被标记为selected
        viewModel.lampListRequest.setValue(null != group ? group.getId() : "");
    }


    private void subscribeUI(GroupViewModel viewModel) {
        viewModel.lampListObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    selectedLampAdapter.addLamps(lamps);
                    unSelectedLampAdapter.addLamps(lamps);
                    //记录旧的设备
                    groupRequest.oldDeviceId = selectedLampAdapter.getIds();
                    updateSelectDeviceNum();
                } else {
                    ToastUtil.showToast("没有数据");
                }
            }
        });

        viewModel.addGroupObserver.observe(this, new Observer<AddGroupSceneResult>() {
            @Override
            public void onChanged(@Nullable AddGroupSceneResult addGroupSceneResult) {
                binding.setIsLoading(false);
                if (addGroupSceneResult != null) {
                    List<Lamp> lamps = viewModel.lampListObserver.getValue();
                    for (Lamp lamp : lamps) {
                        LightCommandUtils.allocDeviceGroup(addGroupSceneResult.groupId, lamp.getDevice_id(), BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get());
                    }
                    getActivity().finish();
                    showToast("场景创建成功");
                } else {
                    showToast("场景创建失败");
                }
            }
        });

        viewModel.deleteGroupObserver.observe(this, new Observer<RequestResult>() {
            @Override
            public void onChanged(@Nullable RequestResult apiResponse) {
                if (apiResponse != null) {
                    showToast("删除成功");
                    getActivity().finish();
                } else {
                    showToast("删除失败");
                }
            }
        });

        viewModel.updateGroupObserver.observe(this, new Observer<GroupRequest>() {
            @Override
            public void onChanged(@Nullable GroupRequest groupRequest) {
                binding.setIsLoading(false);
                if (groupRequest!=null) {
                    List<Lamp> lamps = viewModel.lampListObserver.getValue();
                    for (Lamp lamp : lamps) {
                        LightCommandUtils.allocDeviceGroup(groupRequest.groupAddress, lamp.getDevice_id(), BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get());
                    }
                    showToast("更新成功");
                    getActivity().finish();
                } else {
                    showToast("更新失败");
                }
            }
        });

    }

    private CommonItemClickListener itemClickListener = commonItem -> {
        switch (commonItem.pos) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                binding.viewPager.setCurrentItem(0);
                break;
            case 2:
                if (groupRequest.isAdd) {
                    binding.viewPager.setCurrentItem(3);
                } else {
                    binding.viewPager.setCurrentItem(2);
                }
                break;

        }
    };

    //回退的处理
    private void handleBackPressed() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 3:
                unSelectedLampAdapter.resetLampStatus();
                binding.viewPager.setCurrentItem(2);
                break;
            case 2:
                binding.viewPager.setCurrentItem(1);
                updateSelectDeviceNum();
                break;
            case 1:
                getActivity().finish();
                break;
            case 0:
                binding.viewPager.setCurrentItem(1);
                break;
        }

    }

    private void handleConfirm() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 3:
                List<Lamp> lamps = unSelectedLampAdapter.removeSelectLamps();
                selectedLampAdapter.addSelectedLamp(lamps);
                binding.viewPager.setCurrentItem(2);
                break;
            case 1:
                if (groupRequest.isAdd) {
                    addGroup();
                } else {
                    updateGroup();
                }
                break;
            case 0:
                String name = editBinding.getName();
                if (TextUtils.isEmpty(name) || name.length() > 10) {
                    editBinding.content.setError("名称在1到10个字符之间");
                    editBinding.content.requestFocus();
                    return;
                }
                groupRequest.name = name;
                //更新UI
                CommonItem item = itemAdapter.getItem(1);
                item.observableValue.set(name);
                binding.viewPager.setCurrentItem(1);
                break;
        }

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

    private void updateSelectDeviceNum() {
        //已选设备数量
        int itemCount = selectedLampAdapter.getItemCount();
        CommonItem item = itemAdapter.getItem(2);
        item.observableValue.set(itemCount == 0 ? "请添加" : String.valueOf(itemCount));
    }

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                handleBackPressed();
                break;
//                创建场景
            case R.id.confirm:
                handleConfirm();
                break;
            case R.id.add:
                binding.viewPager.setCurrentItem(3);
                break;
            case R.id.clear:
                editBinding.setName("");
                break;
            case R.id.delete:
                viewModel.deleteGroup(group.getId());
                break;
        }

    }

    //    添加场景
    private void addGroup() {
        if (TextUtils.isEmpty(groupRequest.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(groupRequest.imageUrl)) {
            Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
            return;
        }

        groupRequest.deviceId  = selectedLampAdapter.getIds();
        if (TextUtils.isEmpty(groupRequest.deviceId )) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.setIsLoading(true);
        viewModel.addGroupRequest.setValue(groupRequest);
    }

    //    更新场景 只有名称需要判断
    private void updateGroup() {
        if (TextUtils.isEmpty(groupRequest.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        groupRequest.newDeviceId  = selectedLampAdapter.getIds();
        if (TextUtils.isEmpty(groupRequest.newDeviceId )) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.setIsLoading(true);
        viewModel.updateGroupRequest.setValue(groupRequest);

    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = itemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
        groupRequest.imageUrl = file.getAbsolutePath();
        groupRequest.pic = file;

    }
}


