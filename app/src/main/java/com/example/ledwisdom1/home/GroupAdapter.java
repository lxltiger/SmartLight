package com.example.ledwisdom1.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemGroupBinding;
import com.example.ledwisdom1.home.entity.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页场景列表适配器
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groupList;

    private final OnHandleSceneListener mHandleSceneListener;


    public GroupAdapter(OnHandleSceneListener handleSceneListener) {
        mHandleSceneListener = handleSceneListener;
        groupList = new ArrayList<>();
    }

    /**
     * 刷新添加 先清空再更新
     *
     * @param scenes
     */
    public void addScenes(List<Group> scenes) {
        groupList.clear();
        groupList.addAll(scenes);
        notifyDataSetChanged();
    }



    public void removeScene(Group scene) {
        groupList.remove(scene);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_group, parent, false);
        binding.setHandler(mHandleSceneListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group scene = groupList.get(position);
        holder.mBinding.setGroup(scene);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupBinding mBinding;

        public ViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

