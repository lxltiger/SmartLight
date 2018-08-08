package com.example.ledwisdom1.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLampBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.common.BindingAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 设备页面灯具列表适配器
 */
public class LampAdapter extends RecyclerView.Adapter<LampAdapter.ViewHolder> {

    private List<Lamp> mLampList;

    private final OnHandleLampListener mOnHandleLampListener;

    /**
     * 默认不显示选中图片
     */
    private boolean showSelectIcon = false;

    public LampAdapter(OnHandleLampListener handleLampListener) {
        mOnHandleLampListener = handleLampListener;
        mLampList = new ArrayList<>();
    }

    public void setShowSelectIcon(boolean showSelectIcon) {
        this.showSelectIcon = showSelectIcon;
    }

    public List<Lamp> getLampList() {
        return mLampList;
    }

    /**
     * 分页加载的时候添加更多
     *
     * @param lamps
     */
    public void addMoreLamps(List<Lamp> lamps) {
        mLampList.addAll(lamps);
        notifyDataSetChanged();
    }

    public void replaceLamps(List<Lamp> data) {
        if (mLampList.isEmpty()) {
            if (data != null) {
                mLampList.addAll(data);
                notifyItemRangeChanged(0, data.size() - 1);
            }
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mLampList.size();
                }

                @Override
                public int getNewListSize() {
                    return data.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Lamp old = mLampList.get(oldItemPosition);
                    Lamp now = data.get(newItemPosition);
                    return Objects.equals(old, now);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Lamp old = mLampList.get(oldItemPosition);
                    Lamp now = data.get(newItemPosition);
                    return Objects.equals(old.getId(), now.getId())&&
                     Objects.equals(old.getBrightness(), now.getBrightness())&&
                     Objects.equals(old.getColor(), now.getColor());
                }
            });
            diffResult.dispatchUpdatesTo(this);
            mLampList = data;
        }
    }

    /**
     * 刷新添加 先情况再更新
     *
     * @param lamps
     */
    public void addLamps(List<Lamp> lamps) {
        mLampList.clear();
        mLampList.addAll(lamps);
        notifyDataSetChanged();
    }


    public void addLampsForSelection(List<Lamp> lamps) {
        if (showSelectIcon) {
            for (Lamp lamp : lamps) {
                lamp.lampStatus.set(BindingAdapters.LIGHT_HIDE);
            }
            mLampList.clear();
            mLampList.addAll(lamps);
            notifyDataSetChanged();
        }
    }

    public List<String> getSelectedNum() {
        List<String> deviceIds = new ArrayList<>();
        for (Lamp lamp : mLampList) {
            if (BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get()) {
                deviceIds.add(lamp.getId());
            }
        }

        return deviceIds;
    }

    public List<String> getIds() {
        List<String> deviceIds = new ArrayList<>();
        for (Lamp lamp : mLampList) {
            deviceIds.add(lamp.getId());
        }
        return deviceIds;
    }

    //获取选择的灯具
    public List<Lamp> getSelectedLamps() {
        List<Lamp> lamps = new ArrayList<>();
        for (Lamp lamp : mLampList) {
            if (BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get()) {
                lamps.add(lamp);
            }
        }
        return lamps;
    }


    public void removeLamp(Lamp lamp) {
        mLampList.remove(lamp);
        notifyDataSetChanged();
    }

    /**
     * 因为meshAddress是唯一的
     * 在灯的状态发生改变的时候 通过meshAddress 来定位Lamp修改状态
     *
     * @param meshAddress
     */
    public Lamp getLamp(int meshAddress) {
        for (int i = 0; i < getItemCount(); i++) {
            Lamp lamp = mLampList.get(i);
            if (lamp.getDevice_id() == meshAddress) {
                return lamp;
            }
        }

        return null;
    }

    public void meshOff() {
        for (Lamp lamp : mLampList) {
            lamp.lampStatus.set(BindingAdapters.LIGHT_CUT);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLampBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp, parent, false);
        //条目多选的时候不显示menu
        if (showSelectIcon) binding.swipeMenu.setSwipeEnable(false);
        binding.setHandler(mOnHandleLampListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

