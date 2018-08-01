package com.example.ledwisdom1.scene;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemGroupForSceneBinding;
import com.example.ledwisdom1.home.OnHandleGroupListener;
import com.example.ledwisdom1.home.entity.Group;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * 情景中需要的场景列表适配器
 */
public class GroupForSceneAdapter extends RecyclerView.Adapter<GroupForSceneAdapter.ViewHolder> {

    private List<Group> groupList;

//    private final OnHandleGroupListener onHandleGroupListener;


    public GroupForSceneAdapter(/*OnHandleGroupListener handleSceneListener*/) {
//        onHandleGroupListener = handleSceneListener;
        groupList = new ArrayList<>();
    }

    /**
     * 刷新添加 先清空再更新
     *
     * @param groups
     */
    public void addGroup(List<Group> groups) {
        groupList.clear();
        groupList.addAll(groups);
        notifyDataSetChanged();
    }


    public void removeGroup(Group group) {
        groupList.remove(group);
        notifyDataSetChanged();
    }

    //最多选一个场景
    private OnHandleGroupListener listener = new OnHandleGroupListener() {
        @Override
        public void onItemClick(Group group) {
            if (group.selected) {
                //取消选中
                group.selected = false;
                notifyDataSetChanged();
            } else {
                for (Group item : groupList) {
                    item.selected = false;
                }
                group.selected = true;
                notifyDataSetChanged();
            }

        }

        @Override
        public void onEditClick(Group group) {

        }

        @Override
        public void onDeleteClick(Group group) {

        }
    };

    public String getSelectedGroupId() {
        List<String> ids=new ArrayList<>();
        for (Group group : groupList) {
            if (group.selected) {
                ids.add(group.getId());
            }
        }
        if (ids.isEmpty()) {
            return "";
        }
        return new Gson().toJson(ids);
    }

    public Group getSelectedGroup() {
        for (Group group : groupList) {
            if (group.selected) {
                return group;
            }
        }
        return null;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupForSceneBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_group_for_scene, parent, false);
        binding.setHandler(listener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.mBinding.setGroup(group);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    public void resetGroupStatus() {
        for (Group group : groupList) {
            group.selected=false;
        }
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemGroupForSceneBinding mBinding;

        public ViewHolder(ItemGroupForSceneBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

