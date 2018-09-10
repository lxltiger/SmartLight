package com.example.ledwisdom1.command;

public abstract class BrightnessCommand extends MeshStatus{

    private int addr;
    public BrightnessCommand(int address) {
        this.addr = address;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public  final void setBrightness(int brightness) {
        if (isBlueTooth()) {
            if (isStatusValid()) {
                setBrightnessByBLE(addr,brightness);
            }
        } else {
            setBrightnessByWIFI(addr,brightness);

        }
    }

    protected abstract void setBrightnessByBLE( int addr,int brightness);
    protected abstract void setBrightnessByWIFI(int addr,int brightness);
}
