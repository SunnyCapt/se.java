package capt.sunny.labs.l6;

import java.io.Serializable;

public class StringWrapper implements Serializable {
    public int chunkNumber;
    public int totalChunkNumber;
    public String chunk;

    public StringWrapper(String _chunk, int _chunkNumber, int _totalChunkNumber) {
        chunkNumber = _chunkNumber;
        totalChunkNumber = _totalChunkNumber;
        chunk = _chunk;
    }

    public boolean isLast(){
        return (chunkNumber+1) == totalChunkNumber;
    }


}
