package me.wyzebb.playerviewdistancecontroller.data;

public class PlayerDataHandler {
    private boolean pingMode;
    private int chunks;
    private int adminChunks;

    public int getChunks() {
        return chunks;
    }

    public int getAdminChunks() {
        return adminChunks;
    }

    public boolean isPingMode() {
        return pingMode;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public void setAdminChunks(int adminChunks) {
        this.adminChunks = adminChunks;
    }

    public void setPingMode(boolean pingMode) {
        this.pingMode = pingMode;
    }
}
