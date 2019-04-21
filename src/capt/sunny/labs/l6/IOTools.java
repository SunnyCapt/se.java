package capt.sunny.labs.l6;

import capt.sunny.labs.l6.serv.CreatureMap;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class IOTools {
    public static String getMultiline(BufferedReader paramBufferedReader) throws IOException {
        StringBuilder mainStringBuilder = new StringBuilder();
        StringBuilder withoutParametersStringBuilder = new StringBuilder();
        boolean isParameter = false;
        int intChar = paramBufferedReader.read();
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

    public static CreatureMap getCreatureMapFromFile(String fileName, String charsetName) {
        CreatureMap creatureMap = new CreatureMap();


        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), charsetName)) {
            List<String[]> fileLines = IOTools.readFile(reader, fileName);
            if (!fileLines.isEmpty()) {
                boolean isLastLineEmpty = (fileLines.get(fileLines.size() - 1)[0].length() == 0) && (fileLines.get(fileLines.size() - 1)[1] == null);
                if (isLastLineEmpty)
                    fileLines.remove(fileLines.size() - 1);
                creatureMap = new CreatureMap(fileLines);
            }
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Can't read \"%s\"", fileName));
            System.exit(-1);
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
    public static StringWrapper getDeserializedStringWrapper(byte[] _bytes) throws IOException, ClassNotFoundException {
        StringWrapper obj = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            obj = (StringWrapper) ois.readObject();
        } catch (StreamCorruptedException e) {
            System.out.println(e.getMessage());
        }
        return obj;
    }

    public static byte[] getSerializedObj(String _string, Class clazz) throws IOException {
        //если слишком большой обработай на сервере лишь строку
        CommandWithObject<Creature> commandWrapper = new CommandWithObject<Creature>(_string, clazz);
        byte[] obj = null;
        try (ByteArrayOutputStream serializeBuf = new ByteArrayOutputStream(7331);
             ObjectOutputStream serializingStream = new ObjectOutputStream(serializeBuf);) {
            serializingStream.writeObject(commandWrapper);
            serializeBuf.flush();
            obj = serializeBuf.toByteArray();
        }
        return obj;
    }

    public static List<byte[]> getSerializedStringWrapper(String _string) throws IOException {
        List<byte[]> list = new ArrayList<>();
        try (ByteArrayOutputStream serializeBuf = new ByteArrayOutputStream(10);
             ObjectOutputStream serializingStream = new ObjectOutputStream(serializeBuf);) {

            List<String> strWrp= IOTools.getStringChunks(_string);
            for (int i=0;i<strWrp.size();i++){
                serializingStream.writeObject(new StringWrapper(strWrp.get(i), i, strWrp.size()));
                serializingStream.flush();
                list.add(serializeBuf.toByteArray());
                serializeBuf.reset();
            }
        }
        return list;
    }

    public static List<String> getStringChunks(String _string) {
        List<String> list = new ArrayList<>();
        int lastIndex = 0;
        for (int i = 0; i < (int) _string.length() / 20; i++) {
            list.add(_string.substring(20 * i, 20 * (i + 1)));
            lastIndex += 20;
        }
        if (((double) _string.length() / 20) - ((int) _string.length() / 20) != 0) {
            list.add(_string.substring(lastIndex));
        }
        return list;
    }

    public static <T> T readObjectFromStream(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        T obj = (T) ois.readObject();
        return obj;
    }
}
