package com.example.ledwisdom1.command;

import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.utils.ToastUtil;
import com.telink.bluetooth.light.LightAdapter;

public class MeshStatus {
    public    boolean isStatusValid() {
        int value = SmartLightApp.INSTANCE().getMeshStatus();
        switch (value) {
            case LightAdapter.STATUS_LOGIN:
                return true;
            case LightAdapter.STATUS_LOGOUT:
                ToastUtil.showToast("失去连接");
                return false;
            case LightAdapter.STATUS_CONNECTING:
                ToastUtil.showToast("正在连接");
                return false;
            case -1:
                ToastUtil.showToast("蓝牙网络离线");
                return false;
            case -2:
                ToastUtil.showToast("蓝牙出了问题 重启试试");
                return false;
        }

        return false;

    }

    /**
     * 当前是否使用的蓝牙 不是蓝牙就是WIFI
     * @return
     */
    public boolean isBlueTooth() {
        return SmartLightApp.INSTANCE().isBlueTooth();
    }

}
