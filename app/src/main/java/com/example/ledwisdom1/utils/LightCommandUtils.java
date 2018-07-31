package com.example.ledwisdom1.utils;

import android.util.Log;

import com.example.ledwisdom1.sevice.TelinkLightService;
import com.telink.bluetooth.light.Opcode;

import java.text.DateFormat;
import java.util.Calendar;

public class LightCommandUtils {
    private static final String TAG = "LightCommandUtils";

    //获取灯具的时间
    public static void getLampTime() {
        Log.d(TAG, "getLampTime: ");
        //表示本地连接的灯
        int address = 0x0000;
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E8.getValue(), address, new byte[]{0x10});
    }

    public static void synLampTime() {
        byte[] params = new byte[7];
        //所有灯
        int address = 0xffff;
        Calendar instance = Calendar.getInstance();
        int year = instance.get(Calendar.YEAR);
        int offset = 0;
        params[offset++] = (byte) (year >> 8 & 0xff);
        params[offset++] = (byte) (year & 0xff);
        params[offset++] = (byte) instance.get(Calendar.MONTH);
        params[offset++] = (byte) instance.get(Calendar.DAY_OF_MONTH);
        params[offset++] = (byte) instance.get(Calendar.HOUR_OF_DAY);
        params[offset++] = (byte) instance.get(Calendar.MINUTE);
        params[offset++] = (byte) instance.get(Calendar.SECOND);
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E4.getValue(), address, params);
    }

    //获取闹钟
    public static void getAlarm() {
        //表示本地连接的灯
        int address = 0x0000;
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E6.getValue(), address, new byte[]{0x00});
    }

    public static void addAlarm() {
        byte[] params = new byte[10];
        //所有灯
        int address = 0xffff;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(instance.getTimeInMillis() + 30 * 1000);
        int offset = 0;
        //添加闹钟
        params[offset++] = 0x00;
        //自动分配索引
        params[offset++] = 0x00;
// 0x91 week 开  0x90 week 关
        params[offset++] = (byte) 0x91;
        params[offset++] = (byte) (instance.get(Calendar.MONTH) + 1);
//        params[offset++] = (byte) instance.get(Calendar.DAY_OF_MONTH);
        params[offset++] = 0x7f;
        params[offset++] = (byte) instance.get(Calendar.HOUR_OF_DAY);
        params[offset++] = (byte) instance.get(Calendar.MINUTE);
        params[offset++] = (byte) instance.get(Calendar.SECOND);
        params[offset++] = 0x00;
        String format = DateFormat.getDateTimeInstance().format(instance.getTimeInMillis());
        Log.d(TAG, format);
        TelinkLightService.Instance().sendCommand(Opcode.BLE_GATT_OP_CTRL_E5.getValue(), address, params);
    }

    public static void allocDeviceGroup(int groupAddress, int dstAddress, boolean add) {
        byte opcode = (byte) 0xD7;
        byte[] params = new byte[]{0x01, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};

        params[0] = (byte) (add ? 0x01 : 0x00);
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);
    }


}
