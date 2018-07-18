package com.example.ledwisdom1.home;


import com.example.ledwisdom1.home.entity.Hub;

/**
 * 首页Hub列表点击事件处理
 */
public interface OnHandleHubListener {


    void onItemClick(Hub hub);

    void onEditClick(Hub hub);

    void onDeleteClick(Hub hub);
}
