package com.example.ledwisdom1.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLampBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.utils.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

/**
 * 未选的灯具适配器 需要更新选择状态 不能侧滑删除
 */
public class UnSelectedLampAdapter extends RecyclerView.Adapter<UnSelectedLampAdapter.ViewHolder> {

    private List<Lamp> mLampList;

    private final OnHandleLampListener mHandleLampListener;


    public UnSelectedLampAdapter(/*OnHandleLampListener handleLampListener*/) {
        this(null);
    }

    public UnSelectedLampAdapter(OnHandleLampListener handleLampListener) {
        this.mHandleLampListener = handleLampListener;
        mLampList = new ArrayList<>();
    }


    /**
     * 添加未被选择的灯具
     *
     * @param lamps
     */
    public void addLamps(List<Lamp> lamps) {
        mLampList.clear();
        for (Lamp lamp : lamps) {
            if (!lamp.isSelected()) {
                mLampList.add(lamp);
            }
        }
        notifyDataSetChanged();
    }

    public void addLamp(Lamp lamp) {
        mLampList.add(lamp);
        notifyDataSetChanged();
    }

    public void resetLampStatus() {
        for (Lamp lamp : mLampList) {
            lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
        }
    }

    //从集合中移除已选的
    public List<Lamp> removeSelectLamps() {
        List<Lamp> lamps = new ArrayList<>();
        for (Lamp lamp : mLampList) {
            if (lamp.isSelected()) {
                lamps.add(lamp);
            }
        }
        mLampList.removeAll(lamps);
        notifyDataSetChanged();
        return lamps;
    }


    private OnHandleLampListener handleLampListener = new OnHandleLampListener() {
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


    @NonNull
    @Override
    public UnSelectedLampAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLampBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp, parent, false);
        binding.swipeMenu.setSwipeEnable(false);
        binding.setHandler(mHandleLampListener != null ? mHandleLampListener : handleLampListener);
        return new UnSelectedLampAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UnSelectedLampAdapter.ViewHolder holder, int position) {
        Lamp lamp = mLampList.get(position);
        lamp.setDescription(String.format("%s\n%s", lamp.getName(), lamp.getMac()));
        holder.mBinding.setLamp(lamp);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mLampList == null ? 0 : mLampList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemLampBinding mBinding;

        public ViewHolder(ItemLampBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}