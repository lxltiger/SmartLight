package com.example.ledwisdom1.scene;

public class DeviceSetting {


    /**
     * createTime : {"date":9,"day":4,"hours":9,"minutes":53,"month":7,"seconds":2,"time":1533779582000,"timezoneOffset":-480,"year":118}
     * id : 2a02a491ebb548f4aedf6471b97e5b02
     * objectId : 78f0042352b14336b87a43230dc45790
     * setting : “{"red":81,"blue":0,"green":255,"light":84}” String类型
     * sonId : adae62f3974e414c97288a8de84a8e0b
     * type : 0
     */

    private String id;
    private String objectId;
    //不能写成对象 否则解析失败
    private String setting;
    private String sonId;
    private int type;

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public String getSonId() {
        return sonId;
    }

    public void setSonId(String sonId) {
        this.sonId = sonId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Setting {


        private int red;
        private int blue;
        private int green;
        private int light;


        public int getRed() {
            return red;
        }

        public void setRed(int red) {
            this.red = red;
        }

        public int getBlue() {
            return blue;
        }

        public void setBlue(int blue) {
            this.blue = blue;
        }

        public int getGreen() {
            return green;
        }

        public void setGreen(int green) {
            this.green = green;
        }

        public int getLight() {
            return light;
        }

        public void setLight(int light) {
            this.light = light;
        }
    }

    @Override
    public String toString() {
        return "DeviceSetting{" +
                "id='" + id + '\'' +
                ", objectId='" + objectId + '\'' +
                ", setting=" + setting +
                ", sonId='" + sonId + '\'' +
                ", type=" + type +
                '}';
    }
}
