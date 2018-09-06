package com.example.ledwisdom1.home.entity;

public class MyData {

    private String name;
    private String value;
    private int iconId;

    public MyData(String name, String value, int iconId) {
        this.name = name;
        this.value = value;
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
