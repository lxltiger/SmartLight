package com.example.ledwisdom1.model;

import java.io.Serializable;

@Deprecated
public class DeviceInfo implements Serializable {

    public String macAddress;
    public String deviceName;

    public String meshName;
    public int meshAddress;
    public int meshUUID;
    public int productUUID;
    public int status;
    public byte[] longTermKey = new byte[16];
    public String firmwareRevision;

    public boolean selected;
}
