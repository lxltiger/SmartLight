package com.example.ledwisdom1.clock;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentLampListDialogBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.LampAdapter;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.utils.AutoClearValue;
import com.example.ledwisdom1.utils.BindingAdapters;

/**
 * 闹钟页面的灯具列表，可以作为对话框 从页面底部弹出，目前作为fragment
 */
public class LampListDialogFragment extends BottomSheetDialogFragment implements CallBack {
    public static final String TAG = LampListDialogFragment.class.getSimpleName();
    private ClockViewModel viewModel;
    private AutoClearValue<FragmentLampListDialogBinding> binding;
    private LampAdapter lampAdapter;

    public static LampListDialogFragment newInstance(/*int itemCount*/) {
        final LampListDialogFragment fragment = new LampListDialogFragment();
        final Bundle args = new Bundle();
//        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentLampListDialogBinding lampListDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lamp_list_dialog, null, false);
        lampListDialogBinding.setHandler(this);
        lampListDialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampAdapter = new LampAdapter(mOnHandleLampListener);
        //显示是否选中图片
        lampAdapter.setShowSelectIcon(true);
        lampListDialogBinding.recyclerView.setAdapter(lampAdapter);
        binding = new AutoClearValue<>(this, lampListDialogBinding);
        return lampListDialogBinding.getRoot();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ClockViewModel.class);
        subscribeUI(viewModel);

    }

    private void subscribeUI(ClockViewModel viewModel) {
        viewModel.lampListObserver.observe(this, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    LampList body = apiResponse.body;
                    lampAdapter.addLampsForSelection(body.getList());
                }
            }
        });

        //场景或情景已有灯具 标记出来
       /* viewModel.groupDevicesObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> selectedLamps) {
                if (selectedLamps != null) {
                    List<Lamp> lampList = lampAdapter.getLampList();
                    for (Lamp lamp : lampList) {
                        if (selectedLamps.contains(lamp)) {
                            lamp.lampStatus.set(BindingAdapters.LIGHT_SELECTED);
                        }
                    }
                }
            }
        });*/


    }

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



    @Override
    public void handleClick(View v) {

        switch (v.getId()) {
            case R.id.iv_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.iv_select:
                viewModel.allLamps.clear();
                viewModel.allLamps.addAll(lampAdapter.getLampList());
                //更新已选择灯具页面
                /*if (!viewModel.MODE_ADD) {
                    List<Lamp> selectedLamps = lampAdapter.getSelectedLamps();
                    viewModel.groupDevicesObserver.setValue(selectedLamps);
                }*/
                getActivity().onBackPressed();
                break;

        }
    }


}
