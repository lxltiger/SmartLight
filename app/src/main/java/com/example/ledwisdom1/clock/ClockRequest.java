package com.example.ledwisdom1.clock;

//添加或修改闹钟的请求
public class ClockRequest {
    public String name;
    public String clockId;
    public String meshId;
    public int type;
    public String time;
    public String repeat;
    public String cycle;
    //修改时使用 存储旧设备
    public String oldDeviceId = "";
    //    新设备
    public String newDeviceId = "";
    public String deviceId;
    public int isOpen;
    //    默认添加
    public boolean isAdd = true;

    public ClockRequest(String clockId) {
        this.clockId = clockId;
    }

    public ClockRequest(String clockId, int isOpen) {
        this.clockId = clockId;
        this.isOpen = isOpen;
    }

    public ClockRequest() {
    }
}
