package com.example.ledwisdom1.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemHubBinding;
import com.example.ledwisdom1.home.entity.Hub;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页Hub列表适配器
 */
public class HubAdapter extends RecyclerView.Adapter<HubAdapter.ViewHolder> {

    private List<Hub> mHubList;

    private final OnHandleHubListener mOnHandleHubListener;

    public HubAdapter(OnHandleHubListener handleHubListener) {
        mOnHandleHubListener = handleHubListener;
        mHubList =new ArrayList<>();
    }


    /**
     * 分页加载的时候添加更多
     * @param hubs
     */
    public void addMoreLamps(List<Hub> hubs) {
        mHubList.addAll(hubs);
        notifyDataSetChanged();
    }

    /**
     * 刷新添加 先情况再更新
     * @param hubs
     */
    public void addHubs(List<Hub> hubs) {
        mHubList.clear();
        mHubList.addAll(hubs);
        notifyDataSetChanged();
    }



    public void removeLamp(Hub lamp) {
        mHubList.remove(lamp);
//        notifyItemRemoved(meshBean.getPosition());
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHubBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_hub, parent, false);
        binding.setHandler(mOnHandleHubListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hub lamp = mHubList.get(position);
//        lamp.setDescription(String.format("%s\n%s",lamp.getGatewayName(),lamp.getGatewayMac()));
        holder.mBinding.setHub(lamp);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mHubList ==null?0: mHubList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         private final ItemHubBinding mBinding;

         public ViewHolder(ItemHubBinding binding) {
            super(binding.getRoot());
             mBinding = binding;
         }
    }
}

