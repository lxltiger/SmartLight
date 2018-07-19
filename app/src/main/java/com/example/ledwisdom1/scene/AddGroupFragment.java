
package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
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
import com.example.ledwisdom1.databinding.FragmentAddGroupBinding;
import com.example.ledwisdom1.databinding.LayoutAddGroupBinding;
import com.example.ledwisdom1.databinding.LayoutSelectLampBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.home.LampAdapter;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;


/**
 * A simple {@link Fragment} subclass.
 * 添加场景页面
 * 图片、场景名称、设备都不能为空
 * 先创建场景获取到id，使用id绑定设备 ，如果绑定设备失败需要删除场景 告知创建失败。
 * 以后完成结束此页面
 *
 * 修改场景
 * 图片可以为空 但请求接口不同
 */

public class AddGroupFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {

    public static final String TAG = AddGroupFragment.class.getSimpleName();
    private FragmentAddGroupBinding mBinding;
    private LayoutAddGroupBinding addGroupBinding;
    private LayoutSelectLampBinding selectLampBinding;
    private LampAdapter mLampAdapter;
    private CommonItemAdapter commonItemAdapter;
    private GroupRequest groupRequest;
    private SceneViewModel viewModel;

    public static AddGroupFragment newInstance() {
        Bundle args = new Bundle();
        AddGroupFragment fragment = new AddGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_group, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupRequest = new GroupRequest();
        populateViewPager();

    }

    private void populateViewPager() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        //添加场景UI
        addGroupBinding = DataBindingUtil.inflate(inflater, R.layout.layout_add_group, null, false);
        addGroupBinding.setHandler(this);
        addGroupBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<CommonItem> commonItems = new ArrayList<>();
        CommonItem pic = new CommonItem(0, "图片", false, R.drawable.btn_addpic, true, "");
        CommonItem name = new CommonItem(1, "名称", true, -1, true, "请输入");
        CommonItem device = new CommonItem(2, "设备", true, -1, true, "请添加");
        commonItems.add(pic);
        commonItems.add(name);
        commonItems.add(device);
        commonItemAdapter = new CommonItemAdapter(commonItemClickListener, commonItems);
        addGroupBinding.recyclerView.setAdapter(commonItemAdapter);

        //灯具选择UI
        selectLampBinding = DataBindingUtil.inflate(inflater, R.layout.layout_select_lamp, null, false);
        selectLampBinding.setHandler(this);
        //灯具列表
        selectLampBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLampAdapter = new LampAdapter(mOnHandleLampListener);
        //显示是否选中图片
        mLampAdapter.setShowSelectIcon(true);
        selectLampBinding.recyclerView.setAdapter(mLampAdapter);

        List<View> views = new ArrayList<>();
        views.add(addGroupBinding.getRoot());
        views.add(selectLampBinding.getRoot());

        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(views);
        mBinding.addScene.setAdapter(pagerAdapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SceneViewModel.class);
        viewModel.lampListRequest.setValue(1);
        subscribeUI(viewModel);
    }

    private int groupId = -1;
    List<Lamp> lampsSelected;

    private void subscribeUI(SceneViewModel viewModel) {
//        如果是修改 会受到场景详情
        /*viewModel.groupDetailObserver.observe(this, new Observer<ApiResponse<Group>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<Group> groupApiResponse) {

            }
        });*/
        viewModel.group.observe(this, new Observer<Group>() {
            @Override
            public void onChanged(@Nullable Group group) {
                if (group != null) {
                    addGroupBinding.setTitle("修改场景");
                    addGroupBinding.setAdd(false);
                    groupRequest.groupId = group.getId();
                    CommonItem pic = commonItemAdapter.getItem(0);
                    //显示图片
                    pic.observableValue.set(Config.IMG_PREFIX.concat(group.getIcon()));
//                    显示名称
                    CommonItem name = commonItemAdapter.getItem(1);
                    name.observableValue.set(group.getName());

                } else {
                    addGroupBinding.setTitle("新建场景");
                    addGroupBinding.setAdd(true);
                }
            }
        });

        viewModel.groupDevicesObserver.observe(this, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.isSuccessful()) {
                        GroupDevice body = apiResponse.body;
                        lampsSelected = body.getList();
                        CommonItem device = commonItemAdapter.getItem(2);
                        device.observableValue.set(String.valueOf(lampsSelected.size()));
//
                    }
                }
            }
        });

//        删除场景
        viewModel.deleteGroupObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    getActivity().finish();
                } else {
                    showToast("删除失败");
                }
            }
        });


        viewModel.lampListObserver.observe(this, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    LampList body = apiResponse.body;
                    mLampAdapter.addLampsForSelection(body.getList());
                }
            }
        });

        viewModel.addGroupObserver.observe(this, new Observer<ApiResponse<AddGroupResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupResult> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    AddGroupResult body = apiResponse.body;
                    if (body.succeed()) {
                        String id = body.id;
                        groupId = body.groupId;
                        List<String> ids = mLampAdapter.getSelectedNum();
                        String deviceIds = new Gson().toJson(ids);
                        Log.d(TAG, "onChanged: " + deviceIds);
                        viewModel.addDeviceToGroupRequest.setValue(new Pair<>(id, deviceIds));
                    }
                }
            }
        });

        viewModel.addDeviceToGroupObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    List<Lamp> lampList = mLampAdapter.getLampList();
                    for (Lamp lamp : lampList) {
                        if (BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get()) {
                            allocDeviceGroup(groupId, lamp.getProductUuid(), true);
                        }
                    }
                    getActivity().finish();
                }
            }
        });

        viewModel.updateGroupObserver.observe(this, new Observer<ApiResponse<AddGroupResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    getActivity().finish();
                }else{
                    showToast("更新失败");
                }
            }
        });
    }

    private CommonItemClickListener commonItemClickListener = commonItem -> {
        Log.d(TAG, "onClick() called with: commonItem = [" + commonItem + "]");
        switch (commonItem.pos) {
            case 0:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case 1:
                SceneDialog.newInstance(commonItem.observableValue).show(getActivity().getSupportFragmentManager(), SceneDialog.TAG);
                break;
            case 2:
                mBinding.addScene.setCurrentItem(1);
//                        如果已存在 设为选择状态
                if (lampsSelected != null) {
                    // 假设灯具已经加载
                    List<Lamp> lampList = mLampAdapter.getLampList();
                    for (Lamp lamp : lampList) {
                        Log.d(TAG, "lamp" + lamp.getId());
                        if (lampsSelected.contains(lamp)) {
                            lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                        }
                    }
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

        }
    };


    /**
     * 添加灯具到场景
     *
     * @param groupAddress
     * @param dstAddress
     * @param add
     */

    private void allocDeviceGroup(int groupAddress, int dstAddress, boolean add) {

        byte opcode = (byte) 0xD7;
        byte[] params = new byte[]{0x01, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};

        params[0] = (byte) (add ? 0x01 : 0x00);

        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);

    }


    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                int currentItem = mBinding.addScene.getCurrentItem();
                if (currentItem > 0) {
                    setLampsNum();
                } else {
                    getActivity().finish();
                }
                break;
            case R.id.ok:
                setLampsNum();
                break;
//                创建场景
            case R.id.confirm:
//                不为空说明是修改
                Group value = viewModel.group.getValue();
                if (value != null) {
                    updateGroup();
                } else {
                    addGroup();
                }
                break;
            case R.id.delete:
                viewModel.deleteGroup();
                break;
        }

    }

    //    添加场景
    private void addGroup() {
        CommonItem itemName = commonItemAdapter.getItem(1);
        String name = itemName.observableValue.get();
        Log.d(TAG, "name " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        groupRequest.name = name;
        if (null == groupRequest.pic) {
            Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> ids = mLampAdapter.getSelectedNum();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.addGroupRequest.setValue(groupRequest);
    }

//    更新场景
    private void updateGroup() {
        CommonItem itemName = commonItemAdapter.getItem(1);
        String name = itemName.observableValue.get();
        Log.d(TAG, "name " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        groupRequest.name = name;

        List<String> ids = mLampAdapter.getSelectedNum();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.updateGroupRequest.setValue(groupRequest);
    }

    //选择灯具返回添加页面 统计选择的灯数
    private void setLampsNum() {
        mBinding.addScene.setCurrentItem(0);
        List<String> ids = mLampAdapter.getSelectedNum();
        CommonItem item = commonItemAdapter.getItem(2);
        item.observableValue.set(String.valueOf(ids.size()));
    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = commonItemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
//        item.value = file.getAbsolutePath();
        groupRequest.pic = file;
//        commonItemAdapter.notifyItemChanged(0);

    }
}

