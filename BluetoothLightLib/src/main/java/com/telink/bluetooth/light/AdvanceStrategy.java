package com.telink.bluetooth.light;

import android.util.Log;

/**
 * 命令写入FIFO策略
 */
public abstract class AdvanceStrategy {

    public final static byte[] DEFAULT_SAMPLE_LIST = new byte[]{(byte) 0xD0, (byte) 0xD2, (byte) 0xE2};

    private final static AdvanceStrategy DEFAULT = new DefaultAdvanceStrategy();
    private static AdvanceStrategy definition;
    protected Callback mCallback;
    protected int sampleRate = 200;
    protected byte[] sampleOpcodes;

    private static final int COMMAND_DELAY = 320;

    public static AdvanceStrategy getDefault() {
        synchronized (AdvanceStrategy.class) {
            if (definition != null)
                return definition;
        }
        return DEFAULT;
    }

    public static void setDefault(AdvanceStrategy strategy) {
        synchronized (AdvanceStrategy.class) {
            if (strategy != null)
                definition = strategy;
        }
    }

    static public boolean isExists(byte opcode, byte[] opcodeList) {
        for (byte opc : opcodeList) {
            if ((opc & 0xFF) == (opcode & 0xFF))
                return true;
        }

        return false;
    }

    final public int getSampleRate() {
        return sampleRate;
    }

    /**
     * 设置采样率,单位毫秒
     *
     * @param sampleRate
     */
    final public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * 回调接口,采样到的命令交由回调接口处理
     *
     * @param mCallback
     */
    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public byte[] getSampleOpcodes() {
        if (sampleOpcodes == null)
            return DEFAULT_SAMPLE_LIST;
        return sampleOpcodes;
    }

    /**
     * 设置采样的Opcode数组
     *
     * @param sampleOpcodes
     */
    public void setSampleOpcodes(byte[] sampleOpcodes) {
        this.sampleOpcodes = sampleOpcodes;
    }

    /**
     * 处理传进来的命令
     *
     * @param opcode     命令吗
     * @param address    目标地址
     * @param params     命令参数
     * @param delay      命令延时
     * @param tag        命令标签
     * @param noResponse 命令发送方式
     * @param immediate  是否立即写入底层FIFO
     * @return 命令是否成功写入
     */
    abstract public boolean postCommand(byte opcode, int address, byte[] params, int delay, Object tag, boolean noResponse, boolean immediate);

    /**
     * 启动,执行初始化
     */
    abstract public void onStart();

    /**
     * 停止，做一些清理工作
     */
    abstract public void onStop();

    public interface Callback {
        boolean onCommandSampled(byte opcode, int address, byte[] params, Object tag, int delay, boolean noResponse);
    }

    /**
     * 默认的命令发送策略
     */
    private static class DefaultAdvanceStrategy extends AdvanceStrategy {

        public final static String TAG = "AdvanceStrategy";

        private long lastSampleTime;

        public DefaultAdvanceStrategy() {
        }

        @Override
        public void onStart() {
            this.lastSampleTime = 0;
        }

        @Override
        public void onStop() {
        }

        @Override
        public boolean postCommand(byte opcode, int address, byte[] params, int delay, Object tag, boolean noResponse, boolean immediate) {
            long currentTime = System.currentTimeMillis();
            boolean flag = false;
            boolean exists = false;

            if (lastSampleTime == 0) {
                //第一个命令,直接写入FIFO
                lastSampleTime = currentTime;
                flag = true;
            } else if (immediate || (exists = !isExists(opcode, this.getSampleOpcodes()))) {
                //立即发送的命令,不在采样列表中的命令直接发送
                flag = true;
                if (exists) {
                    if (delay <= 0) {
                        delay = COMMAND_DELAY;
                    }
                }
            } else {
                //计算和最后一次采样时间的间隔,获取采样的命令
                long interval = currentTime - this.lastSampleTime;
                if (interval >= this.getSampleRate()) {
                    lastSampleTime = currentTime;
                    flag = true;
                }
            }

            if (flag && this.mCallback != null) {
                Log.d(TAG, "Sample Opcode : " + Integer.toHexString(opcode));
                //所有采样到的命令立即交给回调接口处理
                return this.mCallback.onCommandSampled(opcode, address, params, tag, delay, noResponse);
            } else {
                Log.d(TAG, "Miss Opcode : " + Integer.toHexString(opcode));
            }

            return false;
        }
    }
}
