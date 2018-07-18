package com.example.ledwisdom1.home.entity;

import java.util.List;

public class GroupList {

    /**
     * bottomPageNo : 1
     * list : [{"uid":32772,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"书房","icon":"http://192.168.1.33:80//lamp/upload/scene-4.png","meshName":"kimios","id":"043d56ff700e4677a6697ee08a391a03","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"},{"uid":32768,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"客厅","icon":"http://192.168.1.33:80//lamp/upload/scene-0.png","meshName":"kimios","id":"3b0cea57f12b44768a61662149732493","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"},{"uid":32770,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"卧室","icon":"http://192.168.1.33:80//lamp/upload/scene-2.png","meshName":"kimios","id":"6a94c52f6e9e4294a72541bdbd94eff8","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"},{"uid":32769,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"餐厅","icon":"http://192.168.1.33:80//lamp/upload/scene-1.png","meshName":"kimios","id":"a50bd8ea0017463693989b05d41c51a2","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"},{"uid":32773,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"卫生间","icon":"http://192.168.1.33:80//lamp/upload/scene-5.png","meshName":"kimios","id":"bd66db2295404efe97c8c560c5b37e53","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"},{"uid":32771,"createBy":"f96f13c6e52a4b7abe1da9da578add52","loginName":"18217612547","name":"儿童房","icon":"http://192.168.1.33:80//lamp/upload/scene-3.png","meshName":"kimios","id":"d13a475a2df4488b8fb0e95cf72b1e38","userId":"f96f13c6e52a4b7abe1da9da578add52","createDate":"2018-06-13 02:03:56"}]
     * nextPageNo : 1
     * pageNo : 1
     * pageSize : 9
     * previousPageNo : 1
     * topPageNo : 1
     * totalPages : 1
     * totalRecords : 6
     */

    private int bottomPageNo;
    private int nextPageNo;
    private int pageNo;
    private int pageSize;
    private int previousPageNo;
    private int topPageNo;
    private int totalPages;
    private int totalRecords;
    private List<Group> list;

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

    public List<Group> getList() {
        return list;
    }

    public void setList(List<Group> list) {
        this.list = list;
    }


}
