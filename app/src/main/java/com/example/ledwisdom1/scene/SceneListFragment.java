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
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.databinding.FragmentSceneListBinding;
import com.example.ledwisdom1.utils.LightCommandUtils;

import java.util.List;

/**
 * 情景列表页面
 * 情景不仅能控制多个灯具 还能控制多个场景
 */
public class SceneListFragment extends Fragment implements CallBack {
    public static final String TAG = SceneListFragment.class.getSimpleName();
    private SceneAdapter sceneAdapter;
    private SceneViewModel viewModel;
    private FragmentSceneListBinding binding;


    public SceneListFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_list, container, false);
        binding.setHandler(this);
        binding.scenes.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        sceneAdapter = new SceneAdapter(mHandleSceneListener);
        binding.scenes.setAdapter(sceneAdapter);
        return binding.getRoot();
    }

    private OnHandleSceneListener mHandleSceneListener = new OnHandleSceneListener() {
        @Override
        public void onItemClick(Scene scene) {
            LightCommandUtils.loadScene(scene.getSceneId());
        }

        @Override
        public void onEditClick(Scene scene) {
            GroupSceneActivity.start(getContext(), GroupSceneActivity.ACTION_SCENE, scene);
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(SceneViewModel.class);
       /* viewModel.sceneListObserver.observe(this, new Observer<ApiResponse<SceneList>>() {
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
        });*/

        viewModel.sceneListObserver.observe(this, new Observer<Resource<List<Scene>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Scene>> resource) {
                binding.setResource(resource);
                if (null != resource.data) {
                    sceneAdapter.addScenes(resource.data);
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
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.btn_add:
                GroupSceneActivity.start(getContext(), GroupSceneActivity.ACTION_SCENE, null);
                break;

        }
    }
}
