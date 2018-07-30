package com.example.ledwisdom1.clock;

//添加或修改闹钟的请求
public class ClockRequest {
    public String name;
    public String clockId;
    public String meshId;
    public String type;
    public String time;
    public String repeat;
    public String cycle;
    public String deviceId;
    public int isOpen;

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
