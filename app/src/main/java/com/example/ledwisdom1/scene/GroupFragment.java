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
import com.example.ledwisdom1.databinding.FragmentGroupBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.model.CommonItem;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;


/**
 * 添加场景
 * 图片、场景名称、设备都不能为空
 * 先创建场景获取到id，使用id绑定设备 ，如果绑定设备失败需要删除场景 告知创建失败。
 * 以后完成结束此页面
 * <p>
 * 修改场景
 * 图片可以为空 但请求接口不同
 */


public class GroupFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {
    public static final String TAG = GroupFragment.class.getSimpleName();
    private FragmentGroupBinding binding;
    private CommonItemAdapter itemAdapter;
    private GroupSceneViewModel viewModel;

    public static GroupFragment newInstance() {
        Bundle args = new Bundle();
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group, container, false);
        binding.setHandler(this);
        itemAdapter = new CommonItemAdapter(itemClickListener);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(itemAdapter);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        boolean add = viewModel.MODE_ADD;
        binding.setAdd(add);
        binding.setTitle(add ? "新建场景" : "修改场景");
        itemAdapter.setItems(viewModel.generateItems());
        subscribeUI(viewModel);
    }


    private void subscribeUI(GroupSceneViewModel viewModel) {
//        修改场景的时候 显示设备数量
        viewModel.groupDevicesObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    CommonItem device = itemAdapter.getItem(2);
                    device.observableValue.set(String.valueOf(lamps.size()));
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
                GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_EDIT_NAME);
                break;
            case 2:
                if (viewModel.MODE_ADD) {
                    GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_LAMP_LIST);
                }else {
                    GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_SELECTED_LAMP);
                }
                break;

        }
    };



    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
//                创建场景
            case R.id.confirm:
//                不为空说明是修改
                if (viewModel.MODE_ADD) {
                    addGroup();
                } else {
                    updateGroup();
                }
                break;
            case R.id.delete:
                viewModel.deleteGroup(true);
                break;
        }

    }

    //    添加场景
    private void addGroup() {
        if (TextUtils.isEmpty(viewModel.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(viewModel.imagePath)) {
            Toast.makeText(getContext(), "还没有设置图片", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> ids = viewModel.getSelectedLampIds();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.isLoading.set(true);
        viewModel.groupSceneRequest.name = viewModel.name;
        viewModel.groupSceneRequest.pic = new File(viewModel.imagePath);
        viewModel.groupSceneRequest.deviceId = new Gson().toJson(ids);
        viewModel.addGroupRequest.setValue(viewModel.groupSceneRequest);
    }

    //    更新场景 只有名称需要判断
    private void updateGroup() {
        if (TextUtils.isEmpty(viewModel.name)) {
            Toast.makeText(getContext(), "还没有设置名称", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.groupSceneRequest.name = viewModel.name;
        //如果是网络地址不处理，不是的话说明改动了图片
        if (!viewModel.imagePath.startsWith(Config.IMG_PREFIX)) {
            viewModel.groupSceneRequest.pic = new File(viewModel.imagePath);
        }
        viewModel.isLoading.set(true);
        viewModel.updateGroupRequest.setValue(viewModel.groupSceneRequest);

    }


    //    处理头像的回调
    @Override
    public void onItemClicked(File file) {
        CommonItem item = itemAdapter.getItem(0);
        item.observableValue.set(file.getAbsolutePath());
        //需要在viewModel中记录
        viewModel.imagePath = file.getAbsolutePath();

    }
}


