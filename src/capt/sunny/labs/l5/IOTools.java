package capt.sunny.labs.l5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOTools {
    public static Map<String, String> getMultiline(BufferedReader paramBufferedReader) throws IOException {
        StringBuilder mainStringBuilder = new StringBuilder();
        StringBuilder withoutParametersStringBuilder = new StringBuilder();
        boolean isParameter = false;
        int intChar = paramBufferedReader.read();
        char c;
        boolean wasEnd = false;
        for (; ; ) {
            if (wasEnd && (intChar == 10 || intChar == 13))
                break;
            //System.out.println(intChar);
            c = (char) intChar;
            if (wasEnd) {
                throw new InvalidParameterException("Nothing can be after \";\"");
            }
            if (c == '"') {
                isParameter = !isParameter;
            } else if (c == ';' && !isParameter) {
                wasEnd = true;
            } else if (((c != ';') && (c != '\n') && (c != '\r') && (c != ' ')) || (isParameter)) {
                mainStringBuilder.append(c);
            }
            if (!isParameter) {
                withoutParametersStringBuilder.append(c);
            }
            intChar = paramBufferedReader.read();
        }
        Map<String, String> result = new HashMap<>();
        result.put("fullCommandInput", mainStringBuilder.toString());
        result.put("withoutParametersInput", withoutParametersStringBuilder.toString());
        return result;
    }

    public static List<String[]> readFile(InputStreamReader reader, String fileName) throws IOException {
        List<String[]> fileLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int preChar;
        int ch = reader.read();
        boolean flage = true;
        int index = 0;
        if (ch != -1) {
            fileLines.add(new String[9]);
            if (((char) ch) != '"') sb.append((char) ch);
            while (ch != -1) {
                flage = ((char) ch == '"' ? !flage : flage);

                preChar = ch;
                ch = reader.read();
                if (ch == -1) {
                    if (index <= 9) fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    break;
                } else if (ch == '\n' && flage) {
                    if (index !=9 &&  index !=10) {
                        throw new InvalidParameterException(String.format((index<10?"Lacks":"Too much" )+ " parameters in %d line. (must be 10)\n", fileLines.size()));
                    }
                    fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    sb = new StringBuilder();
                    fileLines.add(new String[9]);
                    index = 0;
                } else if (ch == ',' && flage) {
                    fileLines.get(fileLines.size() - 1)[index] = sb.toString();
                    sb = new StringBuilder();
                    index++;
                } else if ((ch != '"') || (flage&&ch == '"' && preChar == '"')) {
                    if (index > 10) {
                        throw new InvalidParameterException(String.format("Too much parameters in %d line. (must be 10)\n", fileLines.size()));
                    }
                    sb.append((char) ch);
                }

            }
        }
        return fileLines;
    }
}
