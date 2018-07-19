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
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentGroupBinding;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.scene.SceneActivity;
import com.example.ledwisdom1.sevice.TelinkLightService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 场景页面
 */
public class GroupFragment extends Fragment implements CallBack{
    public static final String TAG = GroupFragment.class.getSimpleName();
    private FragmentGroupBinding mBinding;
    private GroupAdapter groupAdapter;
    private HomeViewModel viewModel;


    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance() {
        Bundle args = new Bundle();
        GroupFragment fragment = new GroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group, container, false);
        mBinding.setHandler(this);
        mBinding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        groupAdapter = new GroupAdapter(mHandleSceneListener);
        mBinding.scenes.setAdapter(groupAdapter);
        return mBinding.getRoot();
    }

    boolean on = false;
    private OnHandleSceneListener mHandleSceneListener = new OnHandleSceneListener() {
        @Override
        public void onItemClick(Group scene) {
            Log.d(TAG, "onItemClick() called with: scene = [" + scene + "]");
            byte opcode = (byte) 0xD0;
            int dstAddr = scene.getGroupId();
            if (on) {
                Log.d(TAG, "kai");
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x01, 0x00, 0x00});
            } else {
                Log.d(TAG, "guan");
                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
                        new byte[]{0x00, 0x00, 0x00});
            }
            on = !on;
        }

        @Override
        public void onEditClick(Group group) {
            Intent intent = SceneActivity.newIntent(getActivity(),SceneActivity.ACTION_GROUP, group);
            startActivity(intent);

        }

        @Override
        public void onDeleteClick(Group scene) {
        }
    };


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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mBinding.setIsLoading(true);
        viewModel.groupListRequest.setValue(1);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }



    @Override
    public void handleClick(View view) {
        startActivity(SceneActivity.newIntent(getActivity(),SceneActivity.ACTION_GROUP, null));
    }
}
