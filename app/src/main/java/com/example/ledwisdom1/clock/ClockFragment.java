package com.example.ledwisdom1.clock;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentClockBinding;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 闹钟的添加和编辑页面
 */
public class ClockFragment extends Fragment implements TimePickerFragment.Listener, WeekDayFragment.Listener {
    public static final String TAG = ClockFragment.class.getSimpleName();
    private FragmentClockBinding binding;
    private ClockViewModel viewModel;
    /**
     * 添加或修改的参数封装
     */
    private ClockRequest clockRequest = new ClockRequest();
    private boolean isAdd = true;

    public static ClockFragment newInstance(Clock clock) {
        ClockFragment fragment = new ClockFragment();
        Bundle args = new Bundle();
        args.putParcelable("clock", clock);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ClockViewModel.class);
        handleArgument(viewModel);
        binding.setViewModel(viewModel);
    }

    private void handleArgument(ClockViewModel viewModel) {
        Bundle arguments = getArguments();
            Clock clock = arguments.getParcelable("clock");
            //说明是修改
            if (clock != null) {
                Log.d(TAG, "clock:" + clock);
                isAdd = false;
                clockRequest.clockId = clock.getId();
                clockRequest.repeat = clock.repeat;
                clockRequest.time=clock.cronTime;
                //获取
                viewModel.clockId.setValue(clock.getId());
//                clockRequest.name = clock.getName();
//                clockRequest.type = clock.getType() + "";
                switch (clock.getType()) {
                    case 1:
                        binding.rbOpen.setChecked(true);
                        break;
                    case 2:
                        binding.rbClose.setChecked(true);
                        break;
                }
            } else {
                //添加默认参数
                isAdd = true;
                clockRequest.name = "openClock";
                clockRequest.type = "1";
            }
            binding.setTitle(isAdd?"新建闹钟":"修改闹钟");
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.confirm:
                if (isAdd) {
                    addClock();
                } else {
                    updateClock();
                }
                break;
            case R.id.setTime:
                TimePickerFragment.newInstance().show(getChildFragmentManager(), TimePickerFragment.TAG);
                break;
            case R.id.epoch:
                WeekDayFragment.newInstance(clockRequest.repeat).show(getChildFragmentManager(), WeekDayFragment.TAG);
                break;
            case R.id.devices:
                if (isAdd) {
                    ClockActivity.start(getActivity(), ClockActivity.ACTION_LAMP_LIST, null);
                } else {
                    ClockActivity.start(getActivity(), ClockActivity.ACTION_SELECTED_LAMP, null);
                }
                break;
        }
    }

    private void updateClock() {
        if (TextUtils.isEmpty(clockRequest.repeat)) {
            showToast("重复周期没有设定");
            return;
        }
        viewModel.isLoading.set(true);
        clockRequest.cycle = String.format("0 %s ? * %s", clockRequest.time, clockRequest.repeat);
        viewModel.updateClockRequest.setValue(clockRequest);

    }

    private void addClock() {
        if (TextUtils.isEmpty(clockRequest.time)) {
            showToast("时间没有设定");
            return;
        }
        if (TextUtils.isEmpty(clockRequest.repeat)) {
            showToast("重复周期没有设定");
            return;
        }
        List<String> ids = viewModel.getSelectedLampIds();
        if (ids.isEmpty()) {
            showToast("还没有选择灯具");
            return;
        }
        viewModel.isLoading.set(true);
        clockRequest.deviceId = new Gson().toJson(ids);
        //cron 格式 Seconds Minutes Hours Day-of-Month Month Day-of-Week Year ? ：表示每月的某一天，或第几周的某一天
        clockRequest.cycle = String.format("0 %s ? * %s", clockRequest.time, clockRequest.repeat);
        viewModel.clockRequest.setValue(clockRequest);
    }

    //闹钟类型的选择
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_open:
                clockRequest.name = "openClock";
                clockRequest.type = "1";
                break;
            case R.id.rb_close:
                clockRequest.name = "closeClock";
                clockRequest.type = "2";
                break;
        }
    }


    @Override
    public void onTimeSet(int hour, int min) {
        //cron 格式
        clockRequest.time = String.format("%s %s", min, hour);
    }

    @Override
    public void onWeekDaySet(String weekDays) {
        clockRequest.repeat = weekDays;
    }
}
