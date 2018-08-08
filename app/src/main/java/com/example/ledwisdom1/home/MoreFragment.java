package com.example.ledwisdom1.home;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.clock.ClockActivity;
import com.example.ledwisdom1.databinding.FragmentMoreBinding;
import com.example.ledwisdom1.scene.GroupSceneActivity;
import com.example.ledwisdom1.user.UserActivity;
import com.example.ledwisdom1.common.AutoClearValue;

/**
 * A simple {@link Fragment} subclass.
 * 主页更多界面
 */
public class MoreFragment extends Fragment implements CallBack {


    public static final String TAG = MoreFragment.class.getSimpleName();
    private AutoClearValue<FragmentMoreBinding> binding;
    private HomeViewModel viewModel;

    public static MoreFragment newInstance() {
        Bundle args = new Bundle();
        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMoreBinding moreBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        moreBinding.setHandler(this);
        binding = new AutoClearValue<>(this, moreBinding);
        return moreBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
//        binding.setViewModel(viewModel);
        subscribeUI(viewModel);
    }

    private void subscribeUI(HomeViewModel homeViewModel) {
       /* homeViewModel.defaultMeshObserver.observe(this, new Observer<DefaultMesh>() {
            @Override
            public void onChanged(@Nullable DefaultMesh defaultMesh) {
                binding.get().setHomeIcon(defaultMesh.aijiaIcon);
                binding.get().setName(defaultMesh.aijiaName);
            }
        });*/
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.portrait://点击头像
                break;
            case R.id.btn_scene:
//                GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_SCENE_LIST);
            {
                Intent intent = new Intent(getActivity(), GroupSceneActivity.class);
                intent.putExtra("action", GroupSceneActivity.ACTION_SCENE_LIST);
                startActivityForResult(intent, 10);
            }
            break;
            case R.id.btn_clock:
                ClockActivity.start(getActivity(), ClockActivity.ACTION_CLOCK_LIST, null);
                break;
            case R.id.about_us:
                UserActivity.start(getActivity(), UserActivity.ACTION_ABOUT_US);
                break;
            case R.id.feed_back:
                UserActivity.start(getActivity(), UserActivity.ACTION_FEED_BACK);
                break;
            case R.id.setting: {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("action", UserActivity.ACTION_SETTING);
                //有可能退出
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == Activity.RESULT_FIRST_USER && resultCode == Activity.RESULT_OK) {
//            UserActivity是singleTop 模式
            UserActivity.start(getActivity(), UserActivity.ACTION_LOGIN);
            getActivity().finish();
        }
    }


}
