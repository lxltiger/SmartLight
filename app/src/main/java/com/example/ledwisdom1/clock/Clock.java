package com.example.ledwisdom1.clock;

import android.os.Parcel;
import android.os.Parcelable;

public class Clock implements Parcelable{

    //时间
    public String time;
    //    星期
    public String days;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeString(this.days);
    }

    public Clock() {
    }

    protected Clock(Parcel in) {
        this.time = in.readString();
        this.days = in.readString();
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
}
