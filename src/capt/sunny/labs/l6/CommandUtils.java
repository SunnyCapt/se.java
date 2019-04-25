package capt.sunny.labs.l6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandUtils {



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

    public static Command readCommand(BufferedReader bufferedReader) throws IOException {
        String multilineCommand = IOTools.getMultiline(bufferedReader);
        int indexOfFullCommandName = multilineCommand.lastIndexOf("\n");
        String commandLine = multilineCommand.substring(0, indexOfFullCommandName);
        String commandName = multilineCommand.substring(indexOfFullCommandName+1);
        if (commandLine.equals("")) {
            throw new InvalidParameterException("Enter command\n");
        }
        Command command = CommandUtils.getCommand(commandLine);
        if (!(commandName.startsWith(command.getName()))) {
            throw new InvalidParameterException("command not found\n");
        }
        String[] commandPars = Commands.valueOf(command.getName().toUpperCase()).getParameterNames();
        String patt = String.format("^%s%s%s$",command.getName(), commandPars.length>=1?"\\{.*\\}":"", commandPars.length==2?"\\{.*\\}":"");
        if (!Pattern.compile(patt).matcher(commandLine).matches())
            throw new InvalidParameterException("command not found\n");
        return command;
    }



    public static Command readCommand(InputStream inputStream) throws IOException, ClassNotFoundException, InterruptedException, StreamCorruptedException {
        Object obj = IOTools.readObject(inputStream);//new ObjectInputStream(inputStream)
        if (!(obj instanceof Command)) {
            throw new RequestException(" send me only Command type objects!\n");
        }
        return (Command) obj;
    }
}