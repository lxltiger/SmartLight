package com.example.ledwisdom1.command;

import com.example.ledwisdom1.sevice.TelinkLightService;
import com.telink.bluetooth.light.Opcode;

public class TelinkColorCommand extends ColorCommand {

    public TelinkColorCommand(int address) {
        super(address);
    }


    @Override
    protected void setColorByBLE(int addr, byte red, byte green, byte blue) {
        byte[] params = new byte[]{0x04, red,  green, blue};
        TelinkLightService.Instance().sendCommandNoResponse(Opcode.BLE_GATT_OP_CTRL_E2.getValue(), addr, params);
    }

    @Override
    protected void setColorByWIFI(int addr, byte red, byte green, byte blue) {

    }
}
