package com.example.ledwisdom1.device.entity;

import java.util.List;

public class LampList {


    /**
     * bottomPageNo : 1
     * list : [{"delFlag":"0","deviceId":1,"deviceMac":"66:55:11:22:00:01","deviceName":"kim_led","deviceState":16,"factoryId":"4354","gatewayId":"1102F483CD9E6126","id":"0500235267714911b538214a544e760c","meshName":"kimascend","productId":"4","shortName":"kim_led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"},{"delFlag":"0","deviceId":4,"deviceMac":"66:55:44:33:22:0D","deviceName":"kim_led","deviceState":13,"factoryId":"4354","gatewayId":"1102F483CD9E6126","id":"249b0b5ab98c40db8f7a0c57b6059adc","meshName":"kimascend","productId":"4","shortName":"kim_led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"},{"delFlag":"0","deviceId":3,"deviceMac":"66:55:44:33:22:0B","deviceName":"kim_led","deviceState":11,"factoryId":"4354","gatewayId":"1102F483CD9E6126","id":"42652102a3844bfa837bebe5592229ba","meshName":"kimascend","productId":"4","shortName":"kim_led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"},{"delFlag":"0","deviceId":5,"deviceMac":"66:55:44:33:22:0A","deviceName":"kim_led","deviceState":10,"factoryId":"4354","gatewayId":"1102F483CD9E6126","id":"7aea7d0a373e4432950c716d13e8a49f","meshName":"kimascend","productId":"4","shortName":"kim_led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"},{"delFlag":"0","deviceId":2,"deviceMac":"66:55:44:33:22:0E","deviceName":"kim_led","deviceState":14,"factoryId":"4354","gatewayId":"1102F483CD9E6126","id":"f749fa2e870743a287c437611e1c3d77","meshName":"kimascend","productId":"4","shortName":"kim_led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"}]
     * nextPageNo : 1
     * pageNo : 1
     * pageSize : 9
     * previousPageNo : 1
     * topPageNo : 1
     * totalPages : 1
     * totalRecords : 5
     */

    private int bottomPageNo;
    private int nextPageNo;
    private int pageNo;
    private int pageSize;
    private int previousPageNo;
    private int topPageNo;
    private int totalPages;
    private int totalRecords;
    private List<Lamp> list;

    public int getBottomPageNo() {
        return bottomPageNo;
    }

    public void setBottomPageNo(int bottomPageNo) {
        this.bottomPageNo = bottomPageNo;
    }

    public int getNextPageNo() {
        return nextPageNo;
    }

    public void setNextPageNo(int nextPageNo) {
        this.nextPageNo = nextPageNo;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPreviousPageNo() {
        return previousPageNo;
    }

    public void setPreviousPageNo(int previousPageNo) {
        this.previousPageNo = previousPageNo;
    }

    public int getTopPageNo() {
        return topPageNo;
    }

    public void setTopPageNo(int topPageNo) {
        this.topPageNo = topPageNo;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<Lamp> getList() {
        return list;
    }

    public void setList(List<Lamp> list) {
        this.list = list;
    }


}
