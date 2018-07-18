package com.example.ledwisdom1.mesh;


import java.util.Objects;

/**
 * 代表一个蓝牙网络
 * todo 替代Mesh
 */
@Deprecated
public class MeshBean {
    /**
     * meshPassword : 123456
     * name : 18217612547
     * meshName : EasyShare
     * id : f92f5ac59d27498c857c3a5c3d014e9a
     * othersId :
     * userId : f96f13c6e52a4b7abe1da9da578add52
     */

    private String meshPassword;
    private String name;
    private String meshName;
    private String id;
    private String othersId;
    private String otherName;
    private String userId;
    /**
     * 在集合中的位置 如果是第一个条目，其UI的为深色
     */
    private int position;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMeshPassword() {
        return meshPassword;
    }

    public void setMeshPassword(String meshPassword) {
        this.meshPassword = meshPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOthersId() {
        return othersId;
    }

    public void setOthersId(String othersId) {
        this.othersId = othersId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    /**
     * 是否是好友的网络
     * @return
     */
    public  boolean isFriendMesh() {
        return !othersId.equals(userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,meshName,meshPassword);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null||!(obj instanceof MeshBean)) {
            return false;
        }

        MeshBean meshBean= (MeshBean) obj;
        return id.equals(meshBean.getId());
    }

    @Override
    public String toString() {
        return "MeshBean{" +
                "meshPassword='" + meshPassword + '\'' +
                ", name='" + name + '\'' +
                ", meshName='" + meshName + '\'' +
                ", id='" + id + '\'' +
                ", othersId='" + othersId + '\'' +
                ", otherName='" + otherName + '\'' +
                ", userId='" + userId + '\'' +
                ", position=" + position +
                '}';
    }
}