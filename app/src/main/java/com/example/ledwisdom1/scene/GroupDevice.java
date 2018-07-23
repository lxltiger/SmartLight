package com.example.ledwisdom1.scene;

import com.example.ledwisdom1.device.entity.Lamp;

import java.util.List;

/*场景下的设备*/
public class GroupDevice {

    /**
     * list : [{"productUuid":10,"device_id":3,"name":"kimascend","id":"2a798c96df8c4c3f87c6131ba1cdf7e6","mac":"66:55:44:33:22:0A","gateway_id":"d-6bb0fd74-ebfd-4e0d-b73a-38d27ac3c8b7"},{"productUuid":11,"device_id":2,"name":"kimascend","id":"23bc256eed544ccd9ae394c162cef25d","mac":"66:55:44:33:22:0B","gateway_id":"d-64bfe09c-4ba9-402d-9f2f-0985061c6770"}]
     * groupSceneId : db1afe3002bb407392d52094726ec62e
     */

    private String groupId;
    private List<Lamp> list;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<Lamp> getList() {
        return list;
    }

    public void setList(List<Lamp> list) {
        this.list = list;
    }

}
