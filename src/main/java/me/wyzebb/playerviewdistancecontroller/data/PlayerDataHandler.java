package me.wyzebb.playerviewdistancecontroller.data;

public class PlayerDataHandler {
    private int chunks;
    private int chunksOthers;
    private boolean pingMode;

    public int getChunks() {
        return chunks;
    }

    public int getChunksOthers() {
        return chunksOthers;
    }

    public boolean isPingMode() {
        return pingMode;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public void setChunksOthers(int chunksOthers) {
        this.chunksOthers = chunksOthers;
    }

    public void setPingMode(boolean pingMode) {
        this.pingMode = pingMode;
    }
}
