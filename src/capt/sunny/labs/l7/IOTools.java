package capt.sunny.labs.l7;

import capt.sunny.labs.l7.CreatureMap;
import capt.sunny.labs.l7.RequestException;
import capt.sunny.labs.l7.Wrapper;
import capt.sunny.labs.l7.WrapperUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class IOTools {
    public static String getMultiline(BufferedReader paramBufferedReader) throws IOException {
        StringBuilder mainStringBuilder = new StringBuilder();
        StringBuilder withoutParametersStringBuilder = new StringBuilder();
        boolean isParameter = false;
        int intChar = paramBufferedReader.read();
        if (intChar == 10 || intChar == 13)
            intChar = paramBufferedReader.read();
        char c;
        boolean wasEnd = false;
        boolean startParameters = false;
        for (; ; ) {
            if (wasEnd && (intChar == 10 || intChar == 13))
                break;
            c = (char) intChar;
            if (wasEnd) {
                while (paramBufferedReader.ready())
                    paramBufferedReader.read();
                throw new InvalidParameterException("Nothing can be after \";\"");
            }
            if (c == '"') {
                isParameter = !isParameter;
                mainStringBuilder.append(c);
            } else if (c == ';' && !isParameter) {
                wasEnd = true;
            } else if (((c != ';') && (c != '\n') && (c != '\r') && (c != ' ')) || (isParameter)) {
                mainStringBuilder.append(c);
            }
            if (!startParameters) {
                if (c != '{') {
                    c = c == '\n' ? '*' : c;
                    c = c == '\r' ? '*' : c;
                    withoutParametersStringBuilder.append(c);
                } else {
                    startParameters = true;
                }
            }
            intChar = paramBufferedReader.read();
        }
        return String.format("%s\n%s", mainStringBuilder.toString(), withoutParametersStringBuilder.toString().replace("\n", "^").replace(":", "^").replace("{", "^").replace("}", "^").replace("\"", "^"));

    }

    public static List<String[]> readFile(String fileName) throws IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName));
        return readFile(reader, fileName);
    }

    public static JSONObject getJsonFile(String filePath){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));) {
            while (reader.ready()) {
                sb.append((char) reader.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

    public static List<String[]> readFile(InputStreamReader reader, String fileName) throws IOException {
        List<String[]> fileLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int preChar;
        int ch = reader.read();
        boolean flage = true;
        int index = 0;
        if (ch != -1) {
            fileLines.add(new String[10]);
            if (((char) ch) != '"') sb.append((char) ch);
            while (ch != -1) {
                flage = ((char) ch == '"' ? !flage : flage);

                preChar = ch;
                ch = reader.read();
                if (ch == -1) {
                    if (index <= 8) fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    break;
                } else if (ch == '\n' && flage) {
                    if (index != 8 && index != 9) {
                        throw new InvalidParameterException(String.format((index < 9 ? "Lacks" : "Too much") + " parameters in %d line. (must be 10)\n", fileLines.size()));
                    }
                    fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    sb = new StringBuilder();
                    fileLines.add(new String[10]);
                    index = 0;
                } else if (ch == ',' && flage) {
                    fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    sb = new StringBuilder();
                    index++;
                } else if ((ch != '"') || (flage && ch == '"' && preChar == '"')) {
                    if (index > 10) {
                        throw new InvalidParameterException(String.format("Too much parameters in %d line. (must be 10)\n", fileLines.size()));
                    }
                    sb.append((char) ch);
                }

            }
        }
        return fileLines;
    }

    public static capt.sunny.labs.l7.CreatureMap getCreatureMapFromFile(String fileName, String charsetName) {
        capt.sunny.labs.l7.CreatureMap creatureMap = new capt.sunny.labs.l7.CreatureMap();


        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), charsetName)) {
            List<String[]> fileLines = IOTools.readFile(reader, fileName);
            if (!fileLines.isEmpty()) {
                boolean isLastLineEmpty = (fileLines.get(fileLines.size() - 1)[0].length() == 0) && (fileLines.get(fileLines.size() - 1)[1] == null);
                if (isLastLineEmpty)
                    fileLines.remove(fileLines.size() - 1);
                creatureMap = new CreatureMap(fileLines);
            }
        } catch (FileNotFoundException e) {
            throw new InvalidParameterException(String.format("Can't read \"%s\"", fileName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (InvalidParameterException e) {
            System.out.println("Failed to load objects: " + e.getMessage());
            System.exit(-1);
        }

        return creatureMap;
    }

    public static String getCSVQuotes(String src) {
//        StringBuilder sb = new StringBuilder(src);
//        for (int i=1;i<src.length();i++){
//            if (src.charAt(i-1)==src.charAt(i))
//                sb.replace(i, i+1, "");
//        }
//        return sb.toString();
        return src.replace("\"", "\"\"");
    }


    //    Object obj = inputStream.readObject();
    //      CommandWithObject<Creature> commandWithObject = (CommandWithObject<Creature>) obj;
//    public static StringWrapper getDeserializedStringWrapper(byte[] _bytes) throws IOException, ClassNotFoundException {
//        StringWrapper obj = null;
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
//             ObjectInputStream ois = new ObjectInputStream(bais)) {
//            obj = (StringWrapper) ois.readObject();
//        } catch (StreamCorruptedException e) {
//            System.out.println(e.getMessage());
//        }
//        return obj;
//    }

    public static <T> byte[] getSerializedObj(T _obj) throws IOException {
        //если слишком большой обработай на сервере лишь строку
        byte[] obj = null;
        try (ByteArrayOutputStream serializeBuf = new ByteArrayOutputStream(1000);
             ObjectOutputStream serializingStream = new ObjectOutputStream(serializeBuf);) {
            serializingStream.writeObject(_obj);
            serializeBuf.flush();
            serializingStream.close();
            obj = serializeBuf.toByteArray();
        }
        return obj;
    }

    public static <K> K getDeserializedObject(byte[] _bytes) throws IOException, ClassNotFoundException {
        K obj = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            obj = (K) ois.readObject();
        } catch (Error | Exception e) {
            throw new RequestException("I can not deserialize this object: " + e.getMessage());
        }
        return obj;
    }

//    public static <T> T readObjectFromStream(InputStream inputStream) throws IOException, ClassNotFoundException {
//        ObjectInputStream ois = new ObjectInputStream(inputStream);
//        T obj = (T) ois.readObject();
//        return obj;
//    }

    public static Object readObject(InputStream inputStream) throws IOException, ClassNotFoundException, InterruptedException {
        return readObject(inputStream, false);
    }


    public static Object readObject(InputStream inputStream, boolean needProcessBar) throws IOException, ClassNotFoundException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(WrapperUtils.SIZE_OF_CHUNK);

        List<Wrapper> chunks = new ArrayList<>();
        inputStream.read(buffer.array());
        Wrapper chunk = IOTools.<Wrapper>getDeserializedObject(buffer.array());
        chunks.add(chunk);
        int onePercentOfChunks = -1;
        String progressPatt = "#";
        if (needProcessBar) {
            if (chunk.totalChunkNumber < 100) {
                for (int i = 0; i < 100 / chunk.totalChunkNumber; i++)
                    progressPatt += "#";
            } else {
                onePercentOfChunks = chunk.totalChunkNumber / 100;
            }
            System.out.println("\nLoading...");
            System.out.print(progressPatt);
        }
        while (!chunk.isLast()) {
            buffer.clear();
            inputStream.read(buffer.array());
            if (buffer.array()[0]==0 && buffer.array()[1]==0 && buffer.array()[2]==0 && buffer.array()[3]==0)
                break;
            chunk = IOTools.<Wrapper>getDeserializedObject(buffer.array());
            chunks.add(chunk);
            //Thread.sleep(100);
            if (onePercentOfChunks == -1) {
                System.out.print(progressPatt);
            } else {
                if (chunk.chunkNumber % onePercentOfChunks == 0)
                    System.out.print(progressPatt);
            }

        }
        System.out.println();
        Object obj = WrapperUtils.<Object>getDeserializedObject(chunks);
        return obj;
    }


    public static <T> void sendObject(SocketChannel channel, T obj, String _className) throws IOException, InterruptedException {

        List<Wrapper> wrappedSerializedCommand = WrapperUtils.wrapUp(IOTools.<T>getSerializedObj(obj), _className);
        int onePercentOfChunks = -1;
        String progressPatt = "#";
        if (wrappedSerializedCommand.size() < 100) {
            for (int i = 0; i < 100 / wrappedSerializedCommand.size(); i++)
                progressPatt += "#";
        } else {
            onePercentOfChunks = (wrappedSerializedCommand.size() / 100) + (wrappedSerializedCommand.size() % 100 == 0 ? 0 : 1);
        }
        System.out.println("\nSending...");

        for (int i = 0; i < wrappedSerializedCommand.size(); i++) {
            channel.write(ByteBuffer.wrap(IOTools.getSerializedObj(wrappedSerializedCommand.get(i))));
            Thread.sleep(1000);

            if (onePercentOfChunks == -1) {
                System.out.print(progressPatt);
            } else {
                if (i % onePercentOfChunks == 0)
                    System.out.print(progressPatt);
            }

        }
        System.out.println();
    }

    public static <T> void sendObject(ObjectOutputStream oos, T obj, String _className) throws IOException, InterruptedException {
        sendObject(oos, obj, _className, false, false);
    }


    public static <T> void sendObject(ObjectOutputStream oos, T obj, String _className, boolean needProcessBar, boolean interruptFlage) throws IOException, InterruptedException {
        List<Wrapper> wrappedSerializedCommand = WrapperUtils.wrapUp(IOTools.<T>getSerializedObj(obj), _className);
        int onePercentOfChunks = -1;
        String progressPatt = "#";
        if (needProcessBar) {
            if (wrappedSerializedCommand.size() < 100) {
                for (int i = 0; i < 100 / wrappedSerializedCommand.size(); i++)
                    progressPatt += "#";
            } else {
                onePercentOfChunks = wrappedSerializedCommand.size() / 100;
            }
            System.out.println("\nSending...");
            System.out.print(progressPatt);
        }

        for (int i = 0; i < wrappedSerializedCommand.size(); i++) {
            oos.write(IOTools.getSerializedObj(wrappedSerializedCommand.get(i)));
            oos.flush();
            if (!interruptFlage)
                Thread.sleep(100);
            if (needProcessBar) {
                if (onePercentOfChunks == -1) {
                    System.out.print(progressPatt);
                } else {
                    if (i % onePercentOfChunks == 0)
                        System.out.print(progressPatt);
                }
            }
        }
        System.out.println();
    }


}
