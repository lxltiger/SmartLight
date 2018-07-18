package com.example.ledwisdom1.device;


import com.example.ledwisdom1.model.Light;

/**
 * 添加新灯的点击事件处理
 */
public interface OnHandleNewLightListener {


    void onItemClick(Light light);

    //    添加灯具 此按钮有多种状态 需要分别处理
    void onAddClick(Light light);

    boolean onItemLongClick(Light light);
}
