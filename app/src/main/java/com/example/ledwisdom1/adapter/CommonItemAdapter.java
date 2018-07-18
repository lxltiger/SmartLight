
package com.example.ledwisdom1.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.CommonItemClickListener;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemCommonBinding;
import com.example.ledwisdom1.model.CommonItem;

import java.util.List;


/**
 * 常用条目适配器
 */

public class CommonItemAdapter extends RecyclerView.Adapter<CommonItemAdapter.ViewHolder> {

    private List<CommonItem> itemList;

    private final CommonItemClickListener mHandleSceneListener;


    public CommonItemAdapter(CommonItemClickListener handleSceneListener, List<CommonItem> commonItems) {
        mHandleSceneListener = handleSceneListener;
        itemList = commonItems;
    }


    public CommonItem getItem(int pos) {
        return itemList.get(pos);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommonBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_common, parent, false);
        binding.setCallBack(mHandleSceneListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommonItem commonItem = itemList.get(position);
        holder.mBinding.setItem(commonItem);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCommonBinding mBinding;

        public ViewHolder(ItemCommonBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}


