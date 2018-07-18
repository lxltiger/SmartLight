package com.example.ledwisdom1.home.entity;

import java.util.List;

public class HubList {


    /**
     * bottomPageNo : 1
     * list : [{"delFlag":"0","factoryId":"","gatewayDesc":"","gatewayId":"1102F483CD9E6126","gatewayIp":"","gatewayMac":"","gatewayMac2":"","gatewayName":"","gatewayState":0,"id":"7dc3cd533df84555a833532889c93282","meshName":"kimascend","password":"68:db:54:80:be:e3","productId":"","routing":"kimascend-led","userId":"a85afc76d81f4b3a92039f0eb8c2855b"}]
     * nextPageNo : 1
     * pageNo : 1
     * pageSize : 9
     * previousPageNo : 1
     * topPageNo : 1
     * totalPages : 1
     * totalRecords : 1
     */

    private int bottomPageNo;
    private int nextPageNo;
    private int pageNo;
    private int pageSize;
    private int previousPageNo;
    private int topPageNo;
    private int totalPages;
    private int totalRecords;
    private List<Hub> list;

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

    public List<Hub> getList() {
        return list;
    }

    public void setList(List<Hub> list) {
        this.list = list;
    }


}
