package com.example.ledwisdom1.mesh;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemMeshBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Mesh列表适配器
 */
public class MeshAdapter extends RecyclerView.Adapter<MeshAdapter.ViewHolder> {

    private List<Mesh> mMeshBeanList;

    private final OnMeshListener mOnMeshListener;

    public MeshAdapter( OnMeshListener meshListener) {
        mOnMeshListener = meshListener;
        mMeshBeanList =new ArrayList<>();
    }



    /**
     * 分页加载的时候添加更多
     * @param meshBeans
     */
    public void addMoreMeshes(List<Mesh> meshBeans) {
        mMeshBeanList.addAll(meshBeans);
        notifyDataSetChanged();
    }

    /**
     * 刷新添加 先情况再更新
     * @param meshBeans
     */
    public void addMeshes(List<Mesh> meshBeans) {
        mMeshBeanList.clear();
        mMeshBeanList.addAll(meshBeans);
        notifyDataSetChanged();
    }

//    添加非好友的mesh
    public void addNonFriendMesh(Mesh meshBean) {
        mMeshBeanList.add(meshBean);
    }


       //获取第一个mesh todo 添加选择标记
    public Mesh getMesh() {
        Mesh meshBean=null;
        if (mMeshBeanList.size() > 0) {
            meshBean = mMeshBeanList.get(0);
        }
        return meshBean;
    }


    /**
     *改变默认的mesh
     * @param mesh 点击的mesh 选为默认的
     */
    public void changeDefaultMesh(Mesh mesh) {
        for (Mesh mesh1 : mMeshBeanList) {
            if (mesh1.equals(mesh)) {
                mesh1.setIsDefault(1);
            }else{
                mesh1.setIsDefault(0);
            }
        }
        notifyDataSetChanged();
    }

    public void showDeleteIcon(boolean show) {
        for (Mesh mesh : mMeshBeanList) {
            mesh.setShowDeleteIcon(show);
        }
        notifyDataSetChanged();
    }

    /**
     * 移除蓝牙网络 更新当前蓝牙网络
     * @param meshBean
     */
    public void removeMesh(Mesh meshBean) {
        mMeshBeanList.remove(meshBean);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeshBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_mesh, parent, false);
        binding.setHandler(mOnMeshListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mesh meshBean = mMeshBeanList.get(position);
//        meshBean.setPosition(position);
        holder.mBinding.setMesh(meshBean);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mMeshBeanList ==null?0: mMeshBeanList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

         private final ItemMeshBinding mBinding;

         public ViewHolder(ItemMeshBinding binding) {
            super(binding.getRoot());
             mBinding = binding;
         }
    }
}

