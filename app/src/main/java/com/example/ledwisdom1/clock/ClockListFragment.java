package com.example.ledwisdom1.clock;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.databinding.FragmentClockListBinding;
import com.example.ledwisdom1.utils.ToastUtil;

import java.util.List;

/**
 * 闹钟列表
 */
public class ClockListFragment extends Fragment implements CallBack {
    public static final String TAG = ClockListFragment.class.getSimpleName();
    private ClockAdapter clockAdapter;
    private ClockViewModel viewModel;


    public ClockListFragment() {
        // Required empty public constructor
    }

    public static ClockListFragment newInstance() {
        Bundle args = new Bundle();
        ClockListFragment fragment = new ClockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClockListBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_list, container, false);
        mBinding.setHandler(this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        clockAdapter = new ClockAdapter(handleClockListener);
        mBinding.recyclerView.setAdapter(clockAdapter);
        return mBinding.getRoot();
    }

    private OnHandleClockListener handleClockListener = new OnHandleClockListener() {
        @Override
        public void onItemClick(Clock clock) {
            ClockActivity.start(getContext(), ClockActivity.ACTION_CLOCK, clock);
        }

        @Override
        public void onItemDelete(Clock clock) {
            viewModel.deleteClick(clock);
        }


        @Override
        public void onSwitchClick(Clock clock) {
            viewModel.switchClock(clock);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, Clock clock) {
            Log.d(TAG, "onCheckedChanged() calle, isChecked = [" + isChecked + "], clock = [" + clock + "]");
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ClockViewModel.class);
        viewModel.clockListObserver.observe(this, new Observer<ApiResponse<ClockList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<ClockList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body != null) {
                        List<Clock> list = apiResponse.body.getList();
                        if (list != null) {
                            clockAdapter.addClocks(list);
                        }
                    }
                }
            }
        });

        viewModel.deleteClockObserver.observe(this, new Observer<Clock>() {
            @Override
            public void onChanged(@Nullable Clock clock) {
                if (clock != null) {
                    clockAdapter.removeClock(clock);
                } else {
                    ToastUtil.showToast("删除失败");
                }
            }
        });

        viewModel.switchClockObserver.observe(this, new Observer<Clock>() {
            @Override
            public void onChanged(@Nullable Clock clock) {
                if (clock != null) {
                    clockAdapter.notifyDataSetChanged();
                }else{
                    ToastUtil.showToast("切换失败");
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        viewModel.clockListRequest.setValue(1);
    }


    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.btn_add:
                ClockActivity.start(getContext(), ClockActivity.ACTION_CLOCK, null);
                break;
        }
    }
}
