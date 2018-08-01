package com.example.ledwisdom1.home.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 场景
 */
public class Group implements Parcelable {
    /**
     * groupSceneId : 32790
     * name : Ghhjjhjj
     * icon : /groupIcon/groupIcon_20180717175722.png
     * id : 064d2fe04d5e4fa0a7b28eefe8a52d09
     */

    private int groupId;
    private String name;
    private String icon;
    private String id;

    public boolean selected = false;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupId);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.id);
    }

    public Group() {
    }

    protected Group(Parcel in) {
        this.groupId = in.readInt();
        this.name = in.readString();
        this.icon = in.readString();
        this.id = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}