package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.example.ledwisdom1.databinding.FragmentSceneListBinding;
import com.example.ledwisdom1.sevice.TelinkLightService;

import java.util.List;

/**
 *
 * 情景列表页面
 * 情景不仅能控制多个灯具 还能控制多个场景
 */
public class SceneListFragment extends Fragment implements CallBack{
    public static final String TAG = SceneListFragment.class.getSimpleName();
    private SceneAdapter sceneAdapter;
    private GroupSceneViewModel viewModel;


    public SceneListFragment() {
        // Required empty public constructor
    }

    public static SceneListFragment newInstance() {
        Bundle args = new Bundle();
        SceneListFragment fragment = new SceneListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentSceneListBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_list, container, false);
        mBinding.setHandler(this);
        mBinding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        sceneAdapter = new SceneAdapter(mHandleSceneListener);
        mBinding.scenes.setAdapter(sceneAdapter);
        return mBinding.getRoot();
    }

    boolean on = false;
    private OnHandleSceneListener mHandleSceneListener = new OnHandleSceneListener() {
        @Override
        public void onItemClick(Scene scene) {
            byte opcode = (byte) 0xD0;
            int dstAddr = scene.getSceneId();
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
        public void onEditClick(Scene scene) {
//            viewModel.scene.setValue(scene);
            GroupSceneActivity.start(getContext(),GroupSceneActivity.ACTION_ADD_SCENE,scene);
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        viewModel.sceneListObserver.observe(this, new Observer<ApiResponse<SceneList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<SceneList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body != null) {
                        List<Scene> list = apiResponse.body.getList();
                        if (list != null) {
                            sceneAdapter.addScenes(list);
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
        viewModel.sceneListRequest.setValue(1);
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
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.btn_add:
                GroupSceneActivity.start(getContext(),GroupSceneActivity.ACTION_ADD_SCENE,null);
                break;

        }
    }
}
