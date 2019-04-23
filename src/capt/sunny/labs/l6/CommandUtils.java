package capt.sunny.labs.l6;

import java.io.IOException;



public class CommandUtils {


    //public final  static String multilineHelpInfo = "\nmultiline\n\tвыключить(если включен)/включить(если выключен) многострочный ввод\n";

    public static Command getCommand(String commandLine) throws IOException {
        int indexOfSecondParameter = -1;
        int indexOfFirstParameter = -1;
        boolean i = true;
        char preChar = ' ';
        char c;
        for (int n = 0; n < commandLine.length(); n++) {
            c = commandLine.charAt(n);
            if ((indexOfSecondParameter == -1) && preChar == '}' && c == '{' && i)
                indexOfSecondParameter = n;
            if (((indexOfFirstParameter == -1) && (c == '{')) && i)
                indexOfFirstParameter = n;
            if ((indexOfFirstParameter != -1) && (indexOfSecondParameter != -1))
                break;
            if (c == '"')
                i = !i;
            preChar = c;
        }
        return new Command(
                indexOfFirstParameter == -1 ? commandLine.replace(" ", "") : commandLine.substring(0, indexOfFirstParameter).replace(" ", ""),
                indexOfFirstParameter == -1 ? null : indexOfSecondParameter == -1 ? commandLine.substring(indexOfFirstParameter) : commandLine.substring(indexOfFirstParameter, indexOfSecondParameter),
                indexOfSecondParameter == -1 ? null : commandLine.substring(indexOfSecondParameter)
        );
    }
}