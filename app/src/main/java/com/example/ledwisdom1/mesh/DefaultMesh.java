package com.example.ledwisdom1.mesh;

public class DefaultMesh {

    /**
     * aijiaIcon : /homeIcon/homeIcon_20180709115433.png
     * aijiaName : home name
     * createTime : {"date":9,"day":1,"hours":11,"minutes":54,"month":6,"seconds":34,"time":1531108474000,"timezoneOffset":-480,"year":118}
     * creater : 1ec1664c7872468e9241887bd9f7babc
     * id : 6e855981180e420c8fb1cf3d9acd82ed
     * name : ec1a9d9e
     * password : 38c205
     * shareCounts : 0
     */

    public String aijiaIcon;
    public String aijiaName;
    public String creater;
    public String id;
    public String name;
    public String password;
    public String shareCounts;
    public int deviceCount;
    public String gatewayId;


    public DefaultMesh() {
        this.password = "";
        this.name = "";
        this.creater = "";
        this.aijiaName = "";
        this.aijiaIcon = "";
        this.id = "";
        shareCounts = "0";
        deviceCount=0;
        gatewayId = "";
    }


    @Override
    public String toString() {
        return "DefaultMesh{" +
                "aijiaIcon='" + aijiaIcon + '\'' +
                ", aijiaName='" + aijiaName + '\'' +
                ", creater='" + creater + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", shareCounts='" + shareCounts + '\'' +
                '}';
    }
}
