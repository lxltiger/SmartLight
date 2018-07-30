package com.example.ledwisdom1.clock;


import android.widget.CompoundButton;

/**
 * 闹钟列表点击事件处理
 */
public interface OnHandleClockListener {

    void onItemClick(Clock clock);
    void onItemDelete(Clock clock);

    void onCheckedChanged(CompoundButton buttonView, boolean isChecked, Clock clock);

    void onSwitchClick(Clock clock);
}
