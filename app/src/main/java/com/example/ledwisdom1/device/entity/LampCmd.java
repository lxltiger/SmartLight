package com.example.ledwisdom1.device.entity;


/**
 * 使用MQTT发给服务端的灯具命令
 * 控制灯的开关和亮度
 */
public class LampCmd {

    /**
     * cmd : 5
     * deviceId : 4
     * type : 1
     * color : 0
     * brightness : 100
     */

    private int cmd;
    private int deviceId;
    private int type;
    private String color;
    private int brightness;

    public LampCmd(int cmd, int deviceId, int type, String color, int brightness) {
        this.cmd = cmd;
        this.deviceId = deviceId;
        this.type = type;
        this.color = color;
        this.brightness = brightness;
    }

    public LampCmd() {
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public String toString() {
        return "LampCmd{" +
                "cmd=" + cmd +
                ", deviceId=" + deviceId +
                ", type=" + type +
                ", color='" + color + '\'' +
                ", brightness=" + brightness +
                '}';
    }
}
