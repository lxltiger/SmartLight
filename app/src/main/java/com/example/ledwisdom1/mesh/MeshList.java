package com.example.ledwisdom1.mesh;

import java.util.List;

public class MeshList {

    /**
     * bottomPageNo : 1
     * list : [{"password":"071692","isDefault":0,"name":"3063ff65","creater":"1ec1664c7872468e9241887bd9f7babc","homeName":"home name","homeIcon":"/homeIcon/homeIcon_20180711100612.png","id":"73c8913f54554dfb91e59ba9b06f3834"},{"password":"75f370","isDefault":1,"name":"ed91ca65","creater":"1ec1664c7872468e9241887bd9f7babc","homeName":"home name","homeIcon":"/homeIcon/homeIcon_20180711100506.png","id":"bf9a530374c34a22b5de537833991c96"}]
     * nextPageNo : 1
     * pageNo : 1
     * pageSize : 40
     * previousPageNo : 1
     * topPageNo : 1
     * totalPages : 1
     * totalRecords : 9
     */

    private int bottomPageNo;
    private int nextPageNo;
    private int pageNo;
    private int pageSize;
    private int previousPageNo;
    private int topPageNo;
    private int totalPages;
    private int totalRecords;
    private List<Mesh> list;

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

    public List<Mesh> getList() {
        return list;
    }

    public void setList(List<Mesh> list) {
        this.list = list;
    }


}
