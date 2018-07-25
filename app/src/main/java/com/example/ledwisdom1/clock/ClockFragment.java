package com.example.ledwisdom1.clock;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentClockBinding;

import java.util.List;

/**
 * 闹钟的添加和编辑页面
 */
public class ClockFragment extends Fragment implements TimePickerFragment.Listener,WeekDayFragment.Listener{
    public static final String TAG = ClockFragment.class.getSimpleName();
    private FragmentClockBinding binding;
    private ClockViewModel viewModel;
    /**
     * 添加或修改的参数封装
     */
    private ClockRequest clockRequest=new ClockRequest();

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
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.confirm:
                Log.d(TAG, "binding.rbOpen.isChecked():" + binding.rbOpen.isChecked());
                break;
            case R.id.setTime:
                TimePickerFragment.newInstance().show(getChildFragmentManager(),TimePickerFragment.TAG);
                break;
            case R.id.epoch:
                WeekDayFragment.newInstance().show(getChildFragmentManager(), WeekDayFragment.TAG);
                break;
            case R.id.devices:
                break;
        }
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


    @Override
    public void onTimeSet(int hour, int min) {
        Log.d(TAG, "onTimeSet() called with: hour = [" + hour + "], min = [" + min + "]");

    }

    @Override
    public void onWeekDaySet(List<WeekDay> weekDays) {
        for (WeekDay weekDay : weekDays) {
            if (weekDay.checked) {
                Log.d(TAG, weekDay.day);
            }
        }
    }
}
