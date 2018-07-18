package com.example.ledwisdom1.mesh;


import android.view.View;

/**
 * Mesh列表的条目点击事件处理
 */
public interface OnMeshListener {


    void onItemClick(View view, Mesh meshBean);

    void onDeleteClick(Mesh meshBean);

    boolean onItemLongClick(Mesh light);




}
