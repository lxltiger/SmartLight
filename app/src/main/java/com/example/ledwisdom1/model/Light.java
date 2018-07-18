package com.example.ledwisdom1.model;

import android.databinding.ObservableInt;

import com.example.ledwisdom1.utils.BindingAdapters;
import com.telink.bluetooth.light.DeviceInfo;

@Deprecated
public final class Light {

//    public String name;
//    public String macAddress;
    public int meshAddress;
    public int brightness;
    public int color;
    public int temperature;
    public DeviceInfo raw;
    public boolean selected;

    public Light() { }

    public Light(DeviceInfo raw) {
        this.raw = raw;
    }

    /**
     * 灯具描述
     * 描述根据场景不同所使用的自断不同
     */
    public String mDescription="";
    /**
     * 新灯条码中添加按钮的状态
     * 0-添加
     * 1-正在添加
     * 2-添加成功
     * 当这个可观察的值发生变化时 会在绑定的方法中重新设置icon的图标
     */
    public ObservableInt mAddStatus = new ObservableInt(BindingAdapters.ADD);

    /**
     * 灯的状态
     * 0 关
     * 1 开
     * 2 离线
     */
    public ObservableInt mLightStatus = new ObservableInt(BindingAdapters.LIGHT_OFF);



    @Override
    public String toString() {
        return "Light{" +
                ", meshAddress=" + meshAddress +
                ", brightness=" + brightness +
                ", mAddStatus=" + mAddStatus.get() +
                ", mLightStatus=" + mLightStatus.get() +
                '}';
    }
}
