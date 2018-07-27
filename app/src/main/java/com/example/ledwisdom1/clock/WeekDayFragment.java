package com.example.ledwisdom1.clock;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemWeekdayBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekDayFragment extends BottomSheetDialogFragment {
    public static final String TAG = WeekDayFragment.class.getSimpleName();
    private Listener listener;
    private WeekDayAdapter adapter;

    public static WeekDayFragment newInstance(String repeat) {
        Bundle args = new Bundle();
        args.putString("repeat", repeat);
        WeekDayFragment fragment = new WeekDayFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            listener = (Listener) parentFragment;
        } else {
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
        return inflater.inflate(R.layout.fragment_week_day, container, false);

    }

    //生成星期集合，如果以前有选择 需要标注
    private List<WeekDay> populate() {
        List<WeekDay> weekDays = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("EEE", Locale.CHINA);
        Bundle arguments = getArguments();
        String repeat = arguments.getString("repeat", "");
        List<Integer> selectDays=new ArrayList<>();
        if (!TextUtils.isEmpty(repeat)) {
            Log.d(TAG, repeat);
            String[] days = repeat.split(",");
            for (String day : days) {
                selectDays.add(Integer.parseInt(day));
            }
        }
//        周日=1  周六=7
        for (int i = 1; i <= 7; i++) {
            instance.set(Calendar.DAY_OF_WEEK, i);
            String format = dateFormat.format(instance.getTimeInMillis());
            weekDays.add(new WeekDay(format, selectDays.contains(i)));
        }
        return weekDays;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final List<WeekDay> weekDays = populate();
        adapter = new WeekDayAdapter(weekDays);
        recyclerView.setAdapter(adapter);
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < weekDays.size(); i++) {
                        if (weekDays.get(i).checked) {
                            stringBuilder.append(',').append(i + 1);
                        }
                    }
                    if (stringBuilder.length() > 0) {

                        stringBuilder.deleteCharAt(0);
                    }
                    listener.onWeekDaySet(stringBuilder.toString());
                }
                dismiss();
            }
        });
    }


    public interface Listener {
        void onWeekDaySet(String weekDays);
    }

    private static class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder> {

        private List<WeekDay> weekDays;

        public WeekDayAdapter(List<WeekDay> weekDays) {
            this.weekDays = weekDays;
        }

        public List<WeekDay> getWeekDays() {
            return weekDays;
        }

        public static class WeekDayViewHolder extends RecyclerView.ViewHolder {
            public final ItemWeekdayBinding binding;

            public WeekDayViewHolder(ItemWeekdayBinding itemWeekdayBinding) {
                super(itemWeekdayBinding.getRoot());
                binding = itemWeekdayBinding;
            }
        }


        @NonNull
        @Override
        public WeekDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemWeekdayBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_weekday, parent, false);
            return new WeekDayViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull WeekDayViewHolder holder, int position) {
            WeekDay weekDay = weekDays.get(position);
            holder.binding.setWeekday(weekDay);
            holder.binding.executePendingBindings();
        }


        @Override
        public int getItemCount() {
            return weekDays == null ? 0 : weekDays.size();
        }
    }

}
