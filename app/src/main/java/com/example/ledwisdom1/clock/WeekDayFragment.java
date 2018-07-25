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

public class WeekDayFragment extends BottomSheetDialogFragment  {
    public static final String TAG = WeekDayFragment.class.getSimpleName();
    private Listener listener;
    private WeekDayAdapter adapter;

    public static WeekDayFragment newInstance(int hour, int min) {
        Bundle args = new Bundle();
        args.putInt("hour", hour);
        args.putInt("min", min);
        WeekDayFragment fragment = new WeekDayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static WeekDayFragment newInstance() {
        WeekDayFragment fragment = new WeekDayFragment();
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
        return inflater.inflate(R.layout.fragment_week_day, container, false);

    }

    private List<WeekDay> populate() {
        List<WeekDay> weekDays = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("EEE", Locale.CHINA);
        instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            String format = dateFormat.format(instance.getTimeInMillis());
//            System.out.println(format);
            instance.add(Calendar.DAY_OF_WEEK, 1);
            weekDays.add(new WeekDay(format));
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
        Bundle arguments = getArguments();
        if (arguments != null) {

        }
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onWeekDaySet(weekDays);
                }
                dismiss();
            }
        });
    }



    public interface Listener {
        void onWeekDaySet(List<WeekDay> weekDays);
    }

    private static class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder>{

        private List<WeekDay> weekDays;

        public WeekDayAdapter(List<WeekDay> weekDays) {
            this.weekDays = weekDays;
        }

        public List<WeekDay> getWeekDays() {
            return weekDays;
        }

        public static class WeekDayViewHolder extends RecyclerView.ViewHolder{
            public final ItemWeekdayBinding binding;
            public WeekDayViewHolder(ItemWeekdayBinding itemWeekdayBinding) {
                super(itemWeekdayBinding.getRoot());
                binding=itemWeekdayBinding;
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
            return weekDays==null?0:weekDays.size();
        }
    }

}
