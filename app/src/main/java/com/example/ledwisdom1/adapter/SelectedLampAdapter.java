package com.example.ledwisdom1.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLampSelectBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.example.ledwisdom1.common.BindingAdapters;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 已选的灯具适配器 不需要更新状态 能侧啥删除
 */
public class SelectedLampAdapter extends RecyclerView.Adapter<SelectedLampAdapter.ViewHolder> {

    private List<Lamp> mLampList;

    private final OnHandleLampListener mOnHandleLampListener;


    public SelectedLampAdapter(/*OnHandleLampListener handleLampListener*/) {
        this(null);
    }

    public SelectedLampAdapter(OnHandleLampListener handleLampListener) {
        mOnHandleLampListener = handleLampListener;
        mLampList = new ArrayList<>();
    }


    public List<Lamp> getmLampList() {
        return mLampList;
    }

    /**
     * 刷新添加 先情况再更新
     *
     * @param lamps
     */

    public void addLamps(List<Lamp> lamps) {
        mLampList.clear();
        for (Lamp lamp : lamps) {
            if (lamp.isSelected()) {
                mLampList.add(lamp);
            }
        }
        notifyDataSetChanged();

    }

    //获取已选的设备 用来添加或删除
    public String getIds() {
        List<String> ids = new ArrayList<>();
        for (Lamp lamp : mLampList) {
            ids.add(lamp.getId());
        }
        if (ids.isEmpty()) {
            return "";
        }
        return new Gson().toJson(ids);
    }


    public void addSelectedLamp(List<Lamp> lamps) {
        mLampList.addAll(lamps);
        notifyDataSetChanged();
    }

    public void removeLamp(Lamp lamp) {
        lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
        mLampList.remove(lamp);
        notifyDataSetChanged();
    }

    private OnHandleLampListener handleLampListener = new OnHandleLampListener() {
        @Override
        public void onItemClick(Lamp lamp) {

        }

        @Override
        public void onEditClick(Lamp lamp) {

        }

        @Override
        public void onDeleteClick(Lamp lamp) {
            removeLamp(lamp);
        }
    };

    @NonNull
    @Override
    public SelectedLampAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLampSelectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp_select, parent, false);
        //为了兼容
        binding.setHandler(null != mOnHandleLampListener ? mOnHandleLampListener : handleLampListener);
        return new SelectedLampAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedLampAdapter.ViewHolder holder, int position) {
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

        private final ItemLampSelectBinding mBinding;

        public ViewHolder(ItemLampSelectBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}