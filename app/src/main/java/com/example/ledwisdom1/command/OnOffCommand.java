package com.example.ledwisdom1.command;

public abstract class OnOffCommand extends MeshStatus {


    private int addr;

    public OnOffCommand(int address) {
        this.addr = address;
    }

    //    带延迟的开关灯 delay一般是5秒，0就是立马执行
    public final void turnOnOff(boolean on, int delay) {
        if (isBlueTooth()) {
            if (isStatusValid()) {
                executeOnOffByBLE(on, addr, delay);
            }
        } else {
            executeOnOffByWIFI(on, addr, delay);
        }
    }

    protected abstract void executeOnOffByBLE(boolean on, int addr, int delay);

    //wifi没有发现delay方法 先加上
    protected abstract void executeOnOffByWIFI(boolean on, int addr, int delay);
}
