package com.example.ledwisdom1.home;


import com.example.ledwisdom1.device.entity.Lamp;

/**
 * 首页灯具列表点击事件处理
 */
public interface OnHandleLampListener {


    void onItemClick(Lamp lamp);

    void onEditClick(Lamp lamp);

    void onDeleteClick(Lamp lamp);
}
