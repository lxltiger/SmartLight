package com.example.ledwisdom1.home;


import com.example.ledwisdom1.home.entity.Group;

/**
 * 首页场景列表点击事件处理
 */
public interface OnHandleGroupListener {


    void onItemClick(Group group);

    void onEditClick(Group group);

    void onDeleteClick(Group group);
}
