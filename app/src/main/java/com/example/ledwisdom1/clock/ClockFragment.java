package com.example.ledwisdom1.clock;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.adapter.SelectedLampAdapter;
import com.example.ledwisdom1.adapter.UnSelectedLampAdapter;
import com.example.ledwisdom1.databinding.FragmentClockBinding;
import com.example.ledwisdom1.databinding.LayoutClockBinding;
import com.example.ledwisdom1.databinding.LayoutLampSelectedBinding;
import com.example.ledwisdom1.databinding.LayoutLampUnselectedBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.common.BindingAdapters;
import com.example.ledwisdom1.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 闹钟的添加和编辑页面
 */
public class ClockFragment extends Fragment implements TimePickerFragment.Listener, WeekDayFragment.Listener, CallBack {
    public static final String TAG = ClockFragment.class.getSimpleName();
    private FragmentClockBinding binding;
    private ClockViewModel viewModel;
    /**
     * 添加或修改的参数封装
     */
    private ClockRequest clockRequest = new ClockRequest();
    //未选灯具列表
    private UnSelectedLampAdapter unSelectedLampAdapter;
    //已选灯具列表 修改的时候才使用
    private SelectedLampAdapter selectedLampAdapter;
    private Clock clock;

    public static ClockFragment newInstance(Clock clock) {
        ClockFragment fragment = new ClockFragment();
        Bundle args = new Bundle();
        args.putParcelable("clock", clock);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clock = getArguments().getParcelable("clock");
        //说明是修改
        if (clock != null&&!TextUtils.isEmpty(clock.getId())) {
            clockRequest.isAdd = false;
            clockRequest.clockId = clock.getId();
            clockRequest.repeat = clock.repeat;
            clockRequest.time = clock.cronTime;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock, container, false);

        LayoutClockBinding clockBinding = DataBindingUtil.inflate(inflater, R.layout.layout_clock, container, false);
        clockBinding.setHandler(this);
        clockBinding.setTitle(clockRequest.isAdd ? "新建闹钟" : "修改闹钟");
        clockBinding.setType(null != clock ? clock.getType() : 1);
//        handleClockType(clockBinding);

        LayoutLampSelectedBinding lampSelectedBinding = DataBindingUtil.inflate(inflater, R.layout.layout_lamp_selected, container, false);
        lampSelectedBinding.setHandler(this);
        selectedLampAdapter = new SelectedLampAdapter(handleLampListener);
        lampSelectedBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampSelectedBinding.recyclerView.setAdapter(selectedLampAdapter);


        LayoutLampUnselectedBinding lampUnselectedBinding = DataBindingUtil.inflate(inflater, R.layout.layout_lamp_unselected, container, false);
        lampUnselectedBinding.setHandler(this);
        unSelectedLampAdapter = new UnSelectedLampAdapter(handleLampListener);
        lampUnselectedBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lampUnselectedBinding.recyclerView.setAdapter(unSelectedLampAdapter);


        List<View> viewList = new ArrayList<>();
        viewList.add(clockBinding.getRoot());
        viewList.add(lampSelectedBinding.getRoot());
        viewList.add(lampUnselectedBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        binding.viewPager.setAdapter(pagerAdapter);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ClockViewModel.class);
        subscribeUI(viewModel);
        viewModel.lampListRequest.setValue(null != clock ? clock.getId() : "");
    }

    //两个adapter 共用一个
    private OnHandleLampListener handleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {
//            对未选的设备进行选择标记 确定的时候将移动lamp
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
            selectedLampAdapter.removeLamp(lamp);
            unSelectedLampAdapter.addLamp(lamp);
        }
    };

    private void subscribeUI(ClockViewModel viewModel) {
        viewModel.lampListObserver.observe(this, new Observer<List<Lamp>>() {
            @Override
            public void onChanged(@Nullable List<Lamp> lamps) {
                if (lamps != null) {
                    selectedLampAdapter.addLamps(lamps);
                    unSelectedLampAdapter.addLamps(lamps);
                    //记录旧的设备
                    clockRequest.oldDeviceId = selectedLampAdapter.getIds();
                } else {
                    ToastUtil.showToast("没有数据");
                }
            }
        });


        viewModel.clockObserver.observe(this, new Observer<ClockResult>() {
            @Override
            public void onChanged(@Nullable ClockResult clockResult) {
                binding.setIsLoading(false);
                if (null != clockResult) {
                    // TODO: 2018/7/26 0026 闹钟处理
                    getActivity().onBackPressed();
                    showToast("创建闹钟成功");
                } else {
                    viewModel.isLoading.set(false);
                    showToast("创建闹钟失败");
                }
            }
        });

        viewModel.updateClockObserver.observe(this, new Observer<ClockRequest>() {
            @Override
            public void onChanged(@Nullable ClockRequest clockRequest) {
                binding.setIsLoading(false);
                if (clockRequest != null) {
                    // TODO: 2018/7/26 0026 闹钟处理
                    getActivity().onBackPressed();
                    showToast("更新闹钟成功");
                } else {
                    showToast("更新闹钟失败");
                }
            }
        });
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                handleBackPressed();
                break;
            case R.id.confirm:
                handleConfirm();
                break;
            case R.id.setTime:
                TimePickerFragment.newInstance().show(getChildFragmentManager(), TimePickerFragment.TAG);
                break;
            case R.id.add:
                binding.viewPager.setCurrentItem(2);
                break;
            case R.id.epoch:
                WeekDayFragment.newInstance(clockRequest.repeat).show(getChildFragmentManager(), WeekDayFragment.TAG);
                break;
            case R.id.devices:
                if (clockRequest.isAdd) {
                    binding.viewPager.setCurrentItem(2);
                } else {
                    binding.viewPager.setCurrentItem(1);
                }
                break;
        }
    }

    //回退的处理
    private void handleBackPressed() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 2:
                unSelectedLampAdapter.resetLampStatus();
                binding.viewPager.setCurrentItem(1);
                break;
            case 1:
                binding.viewPager.setCurrentItem(0);
                break;
            case 0:
                getActivity().onBackPressed();
                break;

        }

    }

    private void handleConfirm() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 2:
                List<Lamp> lamps = unSelectedLampAdapter.removeSelectLamps();
                selectedLampAdapter.addSelectedLamp(lamps);
                binding.viewPager.setCurrentItem(1);
                break;
            case 0:
                if (clockRequest.isAdd) {
                    addClock();
                } else {
                    updateClock();
                }
                break;
        }

    }

    private void updateClock() {
        if (TextUtils.isEmpty(clockRequest.repeat)) {
            showToast("重复周期没有设定");
            return;
        }

        clockRequest.newDeviceId  = selectedLampAdapter.getIds();
        if (TextUtils.isEmpty( clockRequest.newDeviceId)) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.setIsLoading(true);
        clockRequest.cycle = String.format("? %s ? * %s", clockRequest.time, clockRequest.repeat);
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
        clockRequest.deviceId  = selectedLampAdapter.getIds();
        if (TextUtils.isEmpty(clockRequest.deviceId)) {
            Toast.makeText(getContext(), "还没有选择灯具", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.setIsLoading(true);
        //cron 格式 Seconds Minutes Hours Day-of-Month Month Day-of-Week Year ? ：表示每月的某一天，或第几周的某一天
        clockRequest.cycle = String.format("? %s ? * %s", clockRequest.time, clockRequest.repeat);
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
            case R.id.rb_rgb:
                clockRequest.name = "rgbClock";
                clockRequest.type = "3";
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
