package com.example.ledwisdom1.clock;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Objects;

public class Clock implements Parcelable{


    /**
     * isOpen : 1
     * clockId : 32840
     * name : openClock
     * id : 5eac3fc6405f4dfc9775a94884cf6679
     * type : 1
     * cycle : 0 13 14 ? * 2,3,4
     */

    private int isOpen;
    private int clockId;
    private String name;
    private String id;
    private int type;
    private String cycle;
    //用来显示
    public String time="";
//    用来设置闹钟的格式
    public String cronTime="";
    public String repeat="";

    public Clock() {
    }




    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getClockId() {
        return clockId;
    }

    public void setClockId(int clockId) {
        this.clockId = clockId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clock clock = (Clock) o;
        return clockId == clock.clockId &&
                Objects.equals(id, clock.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(clockId, id);
    }

    @Override
    public String toString() {
        return "Clock{" +
                "isOpen=" + isOpen +
                ", clockId=" + clockId +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", cycle='" + cycle + '\'' +
                ", time='" + time + '\'' +
                ", cronTime='" + cronTime + '\'' +
                ", repeat='" + repeat + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isOpen);
        dest.writeInt(this.clockId);
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.cycle);
        dest.writeString(this.time);
        dest.writeString(this.cronTime);
        dest.writeString(this.repeat);
    }

    protected Clock(Parcel in) {
        this.isOpen = in.readInt();
        this.clockId = in.readInt();
        this.name = in.readString();
        this.id = in.readString();
        this.type = in.readInt();
        this.cycle = in.readString();
        this.time = in.readString();
        this.cronTime = in.readString();
        this.repeat = in.readString();
    }

    public static final Creator<Clock> CREATOR = new Creator<Clock>() {
        @Override
        public Clock createFromParcel(Parcel source) {
            return new Clock(source);
        }

        @Override
        public Clock[] newArray(int size) {
            return new Clock[size];
        }
    };

    public void parseCycle() {
        if (!TextUtils.isEmpty(cycle)) {
            String[] strings = cycle.split(" ");
            if (strings.length>5) {
                time = String.format("%s:%s", strings[2], strings[1]);
                cronTime = String.format("%s %s", strings[1], strings[2]);
                repeat = strings[5];
            }
        }
    }
}
