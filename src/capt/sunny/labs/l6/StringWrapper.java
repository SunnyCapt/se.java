package capt.sunny.labs.l6;

import java.io.Serializable;

class StringsWrapper implements Serializable {
    public int chunkNumber;
    public int totalChunkNumber;
    public String chunk;

    public StringsWrapper(String _chunk, int _chunkNumber, int _totalChunkNumber) {
        chunkNumber = _chunkNumber;
        totalChunkNumber = _totalChunkNumber;
        chunk = _chunk;
    }

    public boolean isLast(){
        return (chunkNumber+1) == totalChunkNumber;
    }

}
