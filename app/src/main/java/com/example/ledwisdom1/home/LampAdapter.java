package com.example.ledwisdom1.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLampBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.utils.BindingAdapters;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备页面灯具列表适配器
 */
public class LampAdapter extends RecyclerView.Adapter<LampAdapter.ViewHolder> {

    private List<Lamp> mLampList;

    private final OnHandleLampListener mOnHandleLampListener;

    /**
     * 默认不显示选中图片
     */
    private  boolean showSelectIcon=false;

    public LampAdapter(OnHandleLampListener handleLampListener) {
        mOnHandleLampListener = handleLampListener;
        mLampList =new ArrayList<>();
    }

    public void setShowSelectIcon(boolean showSelectIcon) {
        this.showSelectIcon = showSelectIcon;
    }

    public List<Lamp> getLampList() {
        return mLampList;
    }

    /**
     * 分页加载的时候添加更多
     * @param lamps
     */
    public void addMoreLamps(List<Lamp> lamps) {
        mLampList.addAll(lamps);
        notifyDataSetChanged();
    }

    /**
     * 刷新添加 先情况再更新
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
        List<String> deviceIds=new ArrayList<>();
        for (Lamp lamp : mLampList) {
            if (BindingAdapters.LIGHT_SELECTED==lamp.lampStatus.get()) {
                deviceIds.add(lamp.getId());
            }
        }

        return deviceIds;
    }

    public List<String> getIds() {
        List<String> deviceIds=new ArrayList<>();
        for (Lamp lamp : mLampList) {
                deviceIds.add(lamp.getId());
        }

        return deviceIds;
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
            if (lamp.getProductUuid() == meshAddress) {
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
        ItemLampBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp, parent, false);
        //条目多选的时候不显示menu
        if(showSelectIcon) binding.swipeMenu.setSwipeEnable(false);
        binding.setHandler(mOnHandleLampListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lamp lamp = mLampList.get(position);
        lamp.setDescription(String.format("%s\n%s",lamp.getName(),lamp.getMac()));
        holder.mBinding.setLamp(lamp);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mLampList ==null?0: mLampList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         private final ItemLampBinding mBinding;

         public ViewHolder(ItemLampBinding binding) {
            super(binding.getRoot());
             mBinding = binding;
         }
    }
}

