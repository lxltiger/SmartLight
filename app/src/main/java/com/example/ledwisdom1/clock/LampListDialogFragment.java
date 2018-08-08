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
import com.example.ledwisdom1.adapter.UnSelectedLampAdapter;
import com.example.ledwisdom1.databinding.FragmentLampListDialogBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.common.AutoClearValue;
import com.example.ledwisdom1.utils.ToastUtil;

import java.util.List;

/**
 * 闹钟页面没有被选中的灯具列表，可以作为对话框 从页面底部弹出，目前作为fragment
 */
@Deprecated
public class LampListDialogFragment extends BottomSheetDialogFragment implements CallBack {
    public static final String TAG = LampListDialogFragment.class.getSimpleName();
    private ClockViewModel viewModel;
    private AutoClearValue<FragmentLampListDialogBinding> binding;
    private UnSelectedLampAdapter lampAdapter;

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
        lampAdapter = new UnSelectedLampAdapter();
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
        viewModel.lampListObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    lampAdapter.addLamps(lamps);
                }else{
                    ToastUtil.showToast("没有数据");
                }
            }
        });
    }



    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
            case R.id.iv_select:
                getActivity().onBackPressed();
                break;
        }
    }


}
