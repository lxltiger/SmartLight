package com.example.ledwisdom1.clock;

import java.util.List;

public class ClockList {


    /**
     * bottomPageNo : 1
     * list : [{"createTime":{"date":19,"day":4,"hours":16,"minutes":54,"month":6,"seconds":40,"time":1531990480000,"timezoneOffset":-480,"year":118},"creater":"1ec1664c7872468e9241887bd9f7babc","icon":"/sceneIcon/sceneIcon_20180719165439.png","id":"62aaaa397e994873b9e6ccbf876ce205","meshId":"b14497ec94044a8c94a37bf075d69d02","meshName":"","name":"height","sceneId":32800},{"createTime":{"date":19,"day":4,"hours":16,"minutes":48,"month":6,"seconds":27,"time":1531990107000,"timezoneOffset":-480,"year":118},"creater":"1ec1664c7872468e9241887bd9f7babc","icon":"/sceneIcon/sceneIcon_20180719164826.png","id":"bf5df6ff5b1647cfb9014d065fc00288","meshId":"b14497ec94044a8c94a37bf075d69d02","meshName":"","name":"kimasc","sceneId":32799}]
     * nextPageNo : 1
     * pageNo : 1
     * pageSize : 10
     * previousPageNo : 1
     * topPageNo : 1
     * totalPages : 1
     * totalRecords : 2
     */

    private int bottomPageNo;
    private int nextPageNo;
    private int pageNo;
    private int pageSize;
    private int previousPageNo;
    private int topPageNo;
    private int totalPages;
    private int totalRecords;
    private List<Clock> list;

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

    public List<Clock> getList() {
        return list;
    }

    public void setList(List<Clock> list) {
        this.list = list;
    }


}
