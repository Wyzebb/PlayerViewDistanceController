package me.wyzebb.playerviewdistancecontroller.data;

public class PlayerDataHandler {
    private int chunks;
    private int chunksOthers;
    private boolean pingMode;
    private int chunksPing;

    public int getChunks() {
        return chunks;
    }

    public int getChunksOthers() {
        return chunksOthers;
    }

    public boolean isPingMode() {
        return pingMode;
    }

    public int getChunksPing() {
        return chunksPing;
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

    public void setChunksPing(int chunksPing) {
        this.chunksPing = chunksPing;
    }
}
