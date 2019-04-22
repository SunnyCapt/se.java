package capt.sunny.labs.l6;

import java.io.*;
import java.util.List;

public class Wrapper <T> implements Serializable {
    public int chunkNumber;
    public int totalChunkNumber;
    public byte[] chunk;
    public String className;

    public boolean isLast(){
        return (chunkNumber+1) == totalChunkNumber;
    }

    public Wrapper(byte[] _butes, int _chunkNumber, int _totalChunkNumber, String _className){
        chunk = _butes;
        chunkNumber = _chunkNumber;
        totalChunkNumber = _totalChunkNumber;
        className = _className;
    }
}


