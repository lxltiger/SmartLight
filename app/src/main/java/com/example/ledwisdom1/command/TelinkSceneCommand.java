package com.example.ledwisdom1.command;

import com.example.ledwisdom1.sevice.TelinkLightService;

import static com.telink.bluetooth.light.Opcode.BLE_GATT_OP_CTRL_EE;
import static com.telink.bluetooth.light.Opcode.BLE_GATT_OP_CTRL_EF;

public class TelinkSceneCommand extends SceneCommand {


    public TelinkSceneCommand() {
    }

    public TelinkSceneCommand(int sceneAddress, int dstAddress) {
        super(sceneAddress, dstAddress);
    }

    @Override
    protected void handleSceneOperationByBLE(SceneOperation sceneOperation, int sceneAddress, int dstAddress, byte light, byte red, byte green, byte blue) {
        switch (sceneOperation) {
            case ADD: {
                byte[] params = new byte[]{0x01, (byte) (sceneAddress & 0xFF), light, red, green, blue};
                TelinkLightService.Instance().sendCommand(BLE_GATT_OP_CTRL_EE.getValue(), dstAddress, params);
            }
            break;
            case DELETE: {
                byte[] params = new byte[]{0x00, (byte) (sceneAddress & 0xFF)};
                TelinkLightService.Instance().sendCommand(BLE_GATT_OP_CTRL_EE.getValue(), dstAddress, params);
            }
            break;
            case LOAD: {
                byte[] params = new byte[]{(byte) (sceneAddress & 0xFF)};
                TelinkLightService.Instance().sendCommand(BLE_GATT_OP_CTRL_EF.getValue(), dstAddress, params);
            }
            break;

        }
    }

    @Override
    protected void handleSceneOperationByWIFI(SceneOperation sceneOperation, int sceneAddress, int dstAddress, byte light, byte red, byte green, byte blue) {

    }


}
