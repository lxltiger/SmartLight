package com.example.ledwisdom1.command;

public abstract class ColorCommand extends MeshStatus{


    private int addr;

    public ColorCommand(int address) {
        this.addr = address;
    }


    public void setAddr(int addr) {
        this.addr = addr;
    }

    //    带延迟的开关灯 delay一般是5秒，0就是立马执行
    public final void setColor(byte red, byte green, byte blue) {
        if (isBlueTooth()) {
            if (isStatusValid()) {
                setColorByBLE(addr, red, green, blue);
            }
        } else {
            setColorByWIFI(addr, red, green, blue);
        }
    }


    protected abstract void setColorByBLE(int addr, byte red, byte green, byte blue);

    //wifi没有发现delay方法 先加上
    protected abstract void setColorByWIFI(int addr, byte red, byte green, byte blue);
}
