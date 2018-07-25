package com.example.ledwisdom1.clock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.example.ledwisdom1.R;

public class TimePickerFragment extends BottomSheetDialogFragment  {
    public static final String TAG = TimePickerFragment.class.getSimpleName();
    private Listener listener;
    public static TimePickerFragment newInstance(int hour, int min) {
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("min", min);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TimePickerFragment newInstance() {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(null);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final  Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            listener = (Listener) parentFragment;
        }else{
            listener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int hour = arguments.getInt("hour");
            int min = arguments.getInt("min");
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(min);
        }
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Integer currentHour = timePicker.getCurrentHour();
//                Integer currentMinute = timePicker.getCurrentMinute();
//                Log.d(TAG, "currentHour:" + currentHour);
//                Log.d(TAG, "currentMinute:" + currentMinute);
                if (listener != null) {
                    listener.onTimeSet(timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                }
                dismiss();
            }
        });
    }



    public interface Listener {
        void onTimeSet(int hour, int min);
    }

}
