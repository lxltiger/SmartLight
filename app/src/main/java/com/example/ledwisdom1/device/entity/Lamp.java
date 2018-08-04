package com.example.ledwisdom1.device.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.databinding.ObservableInt;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.utils.BindingAdapters;

import java.util.Objects;

/**
 * device_id : 10
 * name : kimascend
 * id : c80d1471c75647878957af88ab01712d
 * mac : 66:55:44:33:22:0A
 * gateway_id : d-1e7fa74f-b220-4033-95db-158529d5644c
 */
@Entity(tableName = "lamp",primaryKeys = "id")
public class Lamp implements Parcelable {

    @NonNull
    private String id;
    private int device_id;
    private int productUuid;
    private int typeId;
    private String name;
    private String mac;
    private String gateway_id;

    //增加字段
    private String meshId;
    private String description;
    private int brightness;
    private int color;
    private int temperature;
    //是否设置 在情景和闹钟中使用
    @Ignore
    public boolean isSetting=false;
    @Ignore
    public ObservableInt lampStatus = new ObservableInt(BindingAdapters.LIGHT_OFF);


    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public boolean isSelected() {
        return BindingAdapters.LIGHT_SELECTED==lampStatus.get();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(int productUuid) {
        this.productUuid = productUuid;
    }


    public Lamp() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lamp lamp = (Lamp) o;
        return Objects.equals(id, lamp.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Lamp{" +
                "id='" + id + '\'' +
                ", device_id=" + device_id +
                ", productUuid=" + productUuid +
                ", name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", gateway_id='" + gateway_id + '\'' +
                ", meshId='" + meshId + '\'' +
                ", description='" + description + '\'' +
                ", brightness=" + brightness +
                ", color=" + color +
                ", temperature=" + temperature +
                ", lampStatus=" + lampStatus.get() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.device_id);
        dest.writeInt(this.productUuid);
        dest.writeInt(this.typeId);
        dest.writeString(this.name);
        dest.writeString(this.mac);
        dest.writeString(this.gateway_id);
        dest.writeString(this.meshId);
        dest.writeInt(this.brightness);
        dest.writeInt(this.color);
        dest.writeInt(this.temperature);
    }

    protected Lamp(Parcel in) {
        this.id = in.readString();
        this.device_id = in.readInt();
        this.productUuid = in.readInt();
        this.typeId = in.readInt();
        this.name = in.readString();
        this.mac = in.readString();
        this.gateway_id = in.readString();
        this.meshId = in.readString();
        this.brightness = in.readInt();
        this.color = in.readInt();
        this.temperature = in.readInt();
    }

    public static final Creator<Lamp> CREATOR = new Creator<Lamp>() {
        @Override
        public Lamp createFromParcel(Parcel source) {
            return new Lamp(source);
        }

        @Override
        public Lamp[] newArray(int size) {
            return new Lamp[size];
        }
    };
}