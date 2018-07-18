package com.example.ledwisdom1.home;


import com.example.ledwisdom1.home.entity.Group;

/**
 * 首页场景列表点击事件处理
 */
public interface OnHandleSceneListener {


    void onItemClick(Group scene);

    void onEditClick(Group scene);

    void onDeleteClick(Group scene);
}
