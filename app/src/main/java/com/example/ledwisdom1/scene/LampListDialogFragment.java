package com.example.ledwisdom1.scene;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import java.util.List;

/**
 * 灯具列表的对话框形式 从页面底部弹出
 */
@Deprecated
public class LampListDialogFragment extends BottomSheetDialogFragment implements CallBack {
    public static final String TAG = LampListDialogFragment.class.getSimpleName();
    private GroupSceneViewModel viewModel;
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
        binding=new AutoClearValue<>(this,lampListDialogBinding);
        return lampListDialogBinding.getRoot();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        subscribeUI(viewModel);

    }

    private void subscribeUI(GroupSceneViewModel viewModel) {
        viewModel.lampListObserver.observe(this, new Observer<ApiResponse<LampList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<LampList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    LampList body = apiResponse.body;
                    lampAdapter.addLampsForSelection(body.getList());
                }
            }
        });

        //        情景已选中灯具
        viewModel.sceneDevicesObserver.observe(this, new Observer<ApiResponse<GroupDevice>>() {
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
        });


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
    public void onAttach(Context context) {
        super.onAttach(context);
       /*  final Fragment parent = getParentFragment();
       if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }*/
    }

    @Override
    public void onDetach() {
//        mListener = null;
        super.onDetach();
    }

    @Override
    public void handleClick(View view) {
//        将操作后的数据集设置到viewmodel中，与初始数据相比仅修改了是否选中状态
//        viewModel.groupSceneLamps.setValue(lampAdapter.getLampList());
        dismiss();
    }


}
