package com.example.ledwisdom1.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.ledwisdom1.device.entity.Lamp;

public class LightSetting implements Parcelable {
    /**
     * 类型为GROUP 控制场景
     * 类型为SCENE,CLOCK 添加到情景
     */
    public int address;
    public Kind kind;
    public Lamp lamp;
    //情景或闹钟的id
    public String id;

    public LightSetting(int meshAddress) {
        this.address = meshAddress;
        this.kind = Kind.GROUP;
    }

    public LightSetting(Lamp lamp) {
        this.lamp = lamp;
        this.kind = Kind.LAMP;
    }

    public LightSetting(Lamp lamp, int address,String id, Kind kind) {
        this.address = address;
        this.lamp = lamp;
        this.id = id;
        this.kind = kind;
    }

    //    在情景和闹钟的灯具设置才显示目录
    public boolean showMenu() {
        return this.kind == Kind.SCENE || this.kind == Kind.CLOCK;
    }

    public enum Kind {
        LAMP, GROUP, SCENE, CLOCK
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.address);
        dest.writeInt(this.kind == null ? -1 : this.kind.ordinal());
        dest.writeParcelable(this.lamp, flags);
        dest.writeString(this.id);
    }

    protected LightSetting(Parcel in) {
        this.address = in.readInt();
        int tmpKind = in.readInt();
        this.kind = tmpKind == -1 ? null : Kind.values()[tmpKind];
        this.lamp = in.readParcelable(Lamp.class.getClassLoader());
        this.id = in.readString();
    }

    public static final Creator<LightSetting> CREATOR = new Creator<LightSetting>() {
        @Override
        public LightSetting createFromParcel(Parcel source) {
            return new LightSetting(source);
        }

        @Override
        public LightSetting[] newArray(int size) {
            return new LightSetting[size];
        }
    };
}
