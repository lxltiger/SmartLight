package com.example.ledwisdom1.home;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.common.BindingAdapters;
import com.example.ledwisdom1.databinding.FragmentGroupListBinding;
import com.example.ledwisdom1.device.DeviceActivity;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.scene.GroupSceneActivity;

import java.util.List;

/**
 * 场景页面
 */
public class GroupListFragment extends Fragment /*implements CallBack*/{
    public static final String TAG = GroupListFragment.class.getSimpleName();
    private FragmentGroupListBinding mBinding;
    private GroupAdapter groupAdapter;
    private HomeViewModel viewModel;


    public GroupListFragment() {
        // Required empty public constructor
    }

    public static GroupListFragment newInstance() {
        Bundle args = new Bundle();
        GroupListFragment fragment = new GroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false);
        mBinding.toolbar.toolbar.inflateMenu(R.menu.icon_add);
        mBinding.toolbar.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        mBinding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        groupAdapter = new GroupAdapter(mHandleSceneListener);
        mBinding.scenes.setAdapter(groupAdapter);
        return mBinding.getRoot();
    }

    private OnHandleGroupListener mHandleSceneListener = new OnHandleGroupListener() {
        @Override
        public void onItemClick(Group group) {
            DeviceActivity.start(getActivity(),DeviceActivity.ACTION_GROUP_CONTROL,group.getGroupId(),100, BindingAdapters.LIGHT_ON);
        }

        @Override
        public void onEditClick(Group group) {
            Intent intent = GroupSceneActivity.newIntent(getActivity(), GroupSceneActivity.ACTION_GROUP, group);
            startActivity(intent);

        }

        @Override
        public void onDeleteClick(Group scene) {
        }
    };

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity(GroupSceneActivity.newIntent(getActivity(), GroupSceneActivity.ACTION_GROUP, null));
                return true;

        }
        return false;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        viewModel.groupListObserver.observe(this, new Observer<ApiResponse<GroupList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupList> apiResponse) {
                mBinding.setIsLoading(false);
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body != null) {
                        List<Group> list = apiResponse.body.getList();
                        if (list != null) {
                            groupAdapter.addScenes(list);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mBinding.setIsLoading(true);
        viewModel.groupListRequest.setValue(1);
    }


}
