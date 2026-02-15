package me.wyzebb.playerviewdistancecontroller.models;

public class WorldDataRow {
    private String userId;
    private String world;
    private int vd;
    private int vdAdmin;
    private String createdAt;
    private String updatedAt;

    public WorldDataRow() {}

    public String toString() {
        return "Data for " + userId + " for world " + world + ": vd " + vd + ", vdAdmin " + vdAdmin + ", createdAt " + createdAt + ", updatedAt " + updatedAt + ", updatedAt " + createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getVd() {
        return vd;
    }

    public void setVd(int vd) {
        this.vd = vd;
    }

    public int getVdAdmin() {
        return vdAdmin;
    }

    public void setVdAdmin(int vdAdmin) {
        this.vdAdmin = vdAdmin;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}