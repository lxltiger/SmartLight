package com.example.ledwisdom1.clock;


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
import com.example.ledwisdom1.scene.GroupSceneActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 灯具已选的灯具列表
 */
public class SelectedLampListFragment extends Fragment implements CallBack {
    public static final String TAG = SelectedLampListFragment.class.getSimpleName();
    private ClockViewModel viewModel;
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
        viewModel = ViewModelProviders.of(getActivity()).get(ClockViewModel.class);
        //场景已选设备
        viewModel.clockDevicesObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    lampAdapter.addLampsForSelection(lamps);
                }
            }
        });

    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.iv_add:
                ClockActivity.start(getActivity(), ClockActivity.ACTION_LAMP_LIST,null);
                break;

        }
    }


}
