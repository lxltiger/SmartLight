/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.light;

public final class LeDeleteParameters extends Parameters {

    public static LeDeleteParameters create() {
        return new LeDeleteParameters();
    }

    public LeDeleteParameters setMeshName(String value) {
        this.set(PARAM_MESH_NAME, value);
        return this;
    }

    public LeDeleteParameters setPassword(String value) {
        this.set(PARAM_MESH_PASSWORD, value);
        return this;
    }

    public LeDeleteParameters setTimeoutSeconds(int value) {
        this.set(PARAM_TIMEOUT_SECONDS, value);
        return this;
    }

    public LeDeleteParameters setDeviceInfo(DeviceInfo value) {
        this.set(PARAM_DEVICE_LIST, value);
        return this;
    }
}
