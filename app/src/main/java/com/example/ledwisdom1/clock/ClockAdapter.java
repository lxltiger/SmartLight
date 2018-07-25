package com.example.ledwisdom1.clock;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemClockBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 闹钟列表适配器
 */
public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder> {

    private List<Clock> clockList;

    private final OnHandleClockListener handleClockListener;


    public ClockAdapter(OnHandleClockListener handleLampListener) {
        handleClockListener = handleLampListener;
        clockList = new ArrayList<>();
    }


    public List<Clock> getLampList() {
        return clockList;
    }


    /**
     * 刷新添加 先情况再更新
     *
     */
    public void addLamps(List<Clock> clocks) {
        clockList.clear();
        clockList.addAll(clocks);
        notifyDataSetChanged();
    }





    public void removeLamp(Clock c) {
        clockList.remove(c);
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClockBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_clock, parent, false);
        binding.setListener(handleClockListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clock clock = clockList.get(position);
        holder.mBinding.setClock(clock);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return clockList == null ? 0 : clockList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemClockBinding mBinding;

        public ViewHolder(ItemClockBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

