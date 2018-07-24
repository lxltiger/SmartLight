package com.example.ledwisdom1.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemSceneHomeBinding;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.scene.OnHandleSceneListener;
import com.example.ledwisdom1.scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页情景列表适配器
 */
public class HomeSceneAdapter extends RecyclerView.Adapter<HomeSceneAdapter.ViewHolder> {

    private List<Scene> sceneList;

    private final OnHandleSceneListener mHandleSceneListener;


    public HomeSceneAdapter(OnHandleSceneListener handleSceneListener) {
        mHandleSceneListener = handleSceneListener;
        sceneList = new ArrayList<>();
    }

    /**
     * 刷新添加 先清空再更新
     *
     * @param scenes
     */
    public void addScenes(List<Scene> scenes) {
        sceneList.clear();
        sceneList.addAll(scenes);
        notifyDataSetChanged();
    }



    public void removeScene(Group scene) {
        sceneList.remove(scene);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSceneHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_scene_home, parent, false);
        binding.setHandler(mHandleSceneListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scene scene = sceneList.get(position);
        holder.mBinding.setScene(scene);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return sceneList == null ? 0 : sceneList.size();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSceneHomeBinding mBinding;

        public ViewHolder(ItemSceneHomeBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

