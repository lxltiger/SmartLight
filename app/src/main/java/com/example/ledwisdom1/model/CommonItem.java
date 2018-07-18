package com.example.ledwisdom1.model;

import android.databinding.ObservableField;

/**
 * 通用的条目
 */
public class CommonItem {

    public  String name="";
    //值类型 1.图片 2.文本，默认文本
    public boolean isText = true;
    //默认图片资源id
    public int defaultImageResourceId;
    //是否可以点击，能点击显示右箭头 默认不可点击
    public boolean clickable = false;
    //如果是图片 ，值就是url，是文本就是文字内容
    public String value="";
    public ObservableField<String> observableValue;
    //条目在列表的位置 用来点击时确定条目
    public int pos;

    public CommonItem(int pos,String name, boolean isText, int defaultImageResourceId, boolean clickable, String value) {
        this.name = name;
        this.isText = isText;
        this.defaultImageResourceId = defaultImageResourceId;
        this.clickable = clickable;
        this.value = value;
        observableValue=new ObservableField<>(value);
        this.pos = pos;
    }


    @Override
    public String toString() {
        return "CommonItem{" +
                "name='" + name + '\'' +
                ", isText=" + isText +
                ", defaultImageResourceId=" + defaultImageResourceId +
                ", clickable=" + clickable +
                ", value='" + value + '\'' +
                '}';
    }
}
