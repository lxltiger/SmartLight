package com.example.ledwisdom1.mesh;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemHomeBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页我的数据
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<String> mMeshBeanList;


    public HomeAdapter() {
        mMeshBeanList =new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mMeshBeanList.add("");
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_home, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mMeshBeanList ==null?0: mMeshBeanList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         private final ItemHomeBinding mBinding;

         public ViewHolder(ItemHomeBinding binding) {
            super(binding.getRoot());
             mBinding = binding;
         }
    }
}

