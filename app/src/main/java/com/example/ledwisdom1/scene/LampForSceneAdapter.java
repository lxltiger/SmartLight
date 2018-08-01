package com.example.ledwisdom1.scene;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLampForSceneBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.OnHandleLampListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 已选的灯具适配器 不需要更新状态 能侧啥删除
 */
public class LampForSceneAdapter extends RecyclerView.Adapter<LampForSceneAdapter.ViewHolder> {

    private List<Lamp> mLampList;

    private final OnHandleLampListener mOnHandleLampListener;



    public LampForSceneAdapter(OnHandleLampListener handleLampListener) {
        mOnHandleLampListener = handleLampListener;
        mLampList = new ArrayList<>();
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




    @NonNull
    @Override
    public LampForSceneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLampForSceneBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp_for_scene, parent, false);
        binding.setHandler(mOnHandleLampListener);
        return new LampForSceneAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LampForSceneAdapter.ViewHolder holder, int position) {
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

        private final ItemLampForSceneBinding mBinding;

        public ViewHolder(ItemLampForSceneBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}