package com.example.ledwisdom1.scene;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentSelectedLampsBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.LampAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 场景或情景已选的灯具列表
 */
public class SelectedLampListFragment extends Fragment implements CallBack {
    public static final String TAG = SelectedLampListFragment.class.getSimpleName();
    private GroupSceneViewModel viewModel;
    private FragmentSelectedLampsBinding binding;
    private LampAdapter lampAdapter;


    public static SelectedLampListFragment newInstance(/**String param1, String param2*/) {
        SelectedLampListFragment fragment = new SelectedLampListFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_selected_lamps, container, false);
        lampAdapter = new LampAdapter(null);
        lampAdapter.setShowSelectIcon(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(lampAdapter);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        //场景已选设备
        viewModel.groupDevicesObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    lampAdapter.addLampsForSelection(lamps);
                }
            }
        });
        //        情景已选中灯具
        /*viewModel.sceneDevicesObserver.observe(this, new Observer<ApiResponse<GroupDevice>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<GroupDevice> apiResponse) {
                if (apiResponse != null) {
                    if (apiResponse.isSuccessful()) {
                        GroupDevice groupDevice = apiResponse.body;
                        List<Lamp> lampsSelected = groupDevice.getList();
                        // 假设灯具已经加载
                        List<Lamp> lampList = lampAdapter.getLampList();
                        for (Lamp lamp : lampList) {
                            if (lampsSelected.contains(lamp)) {
                                lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                            }
                        }
                    }
                }
            }
        });*/
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.iv_add:
                GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_LAMP_LIST);
                break;

        }
    }


}
