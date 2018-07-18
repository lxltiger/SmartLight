
package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
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
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.Url;
import com.example.ledwisdom1.adapter.CommonItemAdapter;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.databinding.FragmentAddGroupBinding;
import com.example.ledwisdom1.databinding.LayoutAddGroupBinding;
import com.example.ledwisdom1.databinding.LayoutSelectLampBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.home.LampAdapter;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.model.CommonItem;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;


/**
 * A simple {@link Fragment} subclass.
 * 添加场景页面
 * 图片、场景名称、设备都不能为空
 * 先创建场景获取到id，使用id绑定设备 ，如果绑定设备失败需要删除场景 告知创建失败。
 * 以后完成结束此页面
 */

public class AddGroupFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {

    public static final String TAG = AddGroupFragment.class.getSimpleName();
    private FragmentAddGroupBinding mBinding;
    private LayoutAddGroupBinding addGroupBinding;
    private LayoutSelectLampBinding selectLampBinding;
    private LampAdapter mLampAdapter;
    private CommonItemAdapter commonItemAdapter;

    private AddGroup addGroup;
    public ObservableBoolean isLoading = new ObservableBoolean(false);
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
        addGroup = new AddGroup();
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

    private  int groupId=-1;
    private void subscribeUI(SceneViewModel viewModel) {
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
                        groupId=body.groupId;
                        List<String> ids = mLampAdapter.getSelectedNum();
                        String deviceIds = new Gson().toJson(ids);
                        Log.d(TAG, "onChanged: "+deviceIds);
                        viewModel.addDeviceToGroupRequest.setValue(new Pair<>(id,deviceIds));
                    }
                }
            }
        });

        viewModel.addDeviceToGroupObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                Log.d(TAG, "bind light");
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    List<Lamp> lampList = mLampAdapter.getLampList();
                    for (Lamp lamp : lampList) {
                        if (BindingAdapters.LIGHT_SELECTED==lamp.lampStatus.get()) {
                            allocDeviceGroup(groupId, lamp.getProductUuid(), true);
                        }
                    }
                    getActivity().finish();
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
     * 添加或删除灯具到组
     */

    private void updateLampToScene(Lamp lamp) {

        SmartLightApp lightApp = SmartLightApp.INSTANCE();
        String url = String.format("%s%s", Url.PREFIX, lamp.isSelected() ? Url.ADD_DEVICE_TO_GROUP : Url.DElDEVICE_FROM_GROUP);
        Map<String, String> params = new ArrayMap<>();
//        params.put("groupId", addScene.getGroup().getId());
        params.put("deviceId", lamp.getId());
        String paramsStr = new GsonBuilder().serializeNulls().create().toJson(params);
        OkHttpUtils.postString()
                .url(url)
                .content(paramsStr)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("accessToken", lightApp.getUserProfile().getSessionid())
                .tag(getActivity())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String result, int arg1) {
                        Log.d(TAG, "addLampToScene: result = [" + result + "], arg1 = [" + arg1 + "]");
                        RequestResult requestResult = new Gson().fromJson(result, RequestResult.class);
                        /*if (requestResult.succeed()) {
                            allocDeviceGroup(addScene.getGroup().getUid(), lamp.getDevice_id(), lamp.isSelected());
                        }*/
//                        Toast.makeText(lightApp, requestResult.getResultMsg(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Call call, Exception exception, int arg2) {
                        String message = exception.getMessage();
                        Log.d(TAG, message);
                        isLoading.set(false);
                        Toast.makeText(lightApp, message, Toast.LENGTH_SHORT).show();

                    }
                });

    }


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
                CommonItem itemName = commonItemAdapter.getItem(1);
                String name = itemName.observableValue.get();
                Log.d(TAG, "name " + name);
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                addGroup.name = name;
                if (null == addGroup.pic) {
                    Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> ids = mLampAdapter.getSelectedNum();
                if (ids.isEmpty()) {
                    Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.addGroupRequest.setValue(addGroup);
                break;
        }


    }

    //选择灯具返回添加页面 统计选择的灯数
    private void setLampsNum() {
        mBinding.addScene.setCurrentItem(0);
        List<String> ids= mLampAdapter.getSelectedNum();
        CommonItem item = commonItemAdapter.getItem(2);
        item.observableValue.set(String.valueOf(ids.size()));
    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = commonItemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
//        item.value = file.getAbsolutePath();
        addGroup.pic = file;
//        commonItemAdapter.notifyItemChanged(0);

    }
}

