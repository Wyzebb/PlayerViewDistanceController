package me.wyzebb.playerviewdistancecontroller.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public final class WorldDataRow {
    private final UUID playerUUID;
    private final String world;
    private final int vd;
    private final int vdAdmin;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;

    private WorldDataRow(UUID playerUUID, String world, int vd, int vdAdmin, Timestamp createdAt, Timestamp updatedAt) {
        this.playerUUID = playerUUID;
        this.world = world;
        this.vd = vd;
        this.vdAdmin = vdAdmin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WorldDataRow fromResultSet(ResultSet resultSet) throws SQLException {
        return new WorldDataRow(
                UUID.fromString(resultSet.getString("player_uuid")),
                resultSet.getString("world"),
                resultSet.getInt("vd"),
                resultSet.getInt("vd_admin"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at")
        );
    }

    public String toString() {
        return "Data for " + playerUUID + " for world " + world + ": vd " + vd + ", vdAdmin " + vdAdmin + ", createdAt " + createdAt + ", updatedAt " + updatedAt + ", updatedAt " + createdAt;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getWorld() {
        return world;
    }

    public int getVd() {
        return vd;
    }

    public int getVdAdmin() {
        return vdAdmin;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}