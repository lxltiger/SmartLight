package com.example.ledwisdom1.app;


public class MySampleAdvanceStrategy extends AdvanceStrategy{

    public final static String TAG = "AdvanceStrategy";

    public MySampleAdvanceStrategy() {
    }

    @Override
    public boolean postCommand(byte opcode, int address, byte[] params, int delay, Object tag, boolean noResponse, boolean immediate) {
        //所有采样到的命令立即交给回调接口处理
        if (this.mCallback != null)
            return this.mCallback.onCommandSampled(opcode, address, params, tag, delay, noResponse);
        return false;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}