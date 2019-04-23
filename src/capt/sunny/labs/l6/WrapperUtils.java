package capt.sunny.labs.l6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WrapperUtils {
    public static final int CHUNK_SIZE = 200;
    public static final int SIZE_OF_CHUNK = 1000;

    public static <T> T getDeserializedObject(List<Wrapper> chunks) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[chunks.size() * CHUNK_SIZE];
        for (int i = 0; i < chunks.size(); i++) {
            System.arraycopy(chunks.get(i).chunk, 0, bytes, CHUNK_SIZE * i, chunks.get(i).chunk.length);
        }
        return IOTools.<T>getDeserializedObject(bytes);
    }


    public static List<Wrapper> wrapUp(byte[] _bytes, String className) {
        List<Wrapper> chunks = new ArrayList<>();
        byte[] tempBytes;
        int i = 0;
        int size = _bytes.length / CHUNK_SIZE;
        size = (((double) _bytes.length) / CHUNK_SIZE) - ((int) _bytes.length / CHUNK_SIZE) > 0 ? ++size : size;
        for (; i < size; i++) {
            tempBytes = Arrays.copyOfRange(_bytes, i * CHUNK_SIZE, (i + 1) * CHUNK_SIZE);//-1 ???????????????????
            chunks.add(new Wrapper(tempBytes, i, size, className));
        }
        return chunks;
    }


}
