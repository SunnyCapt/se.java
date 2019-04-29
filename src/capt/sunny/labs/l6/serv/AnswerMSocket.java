package capt.sunny.labs.l6.serv;

import capt.sunny.labs.l6.Command;
import capt.sunny.labs.l6.CreatureMap;
import capt.sunny.labs.l6.IOTools;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;

public class AnswerMSocket implements Runnable {
    Command command;
    CreatureMap creatureMap;
    String fileName;
    String message = "";
    Exception[] exception;
    ObjectOutputStream oos;

    public AnswerMSocket(Command _command, CreatureMap _creatureMap, ObjectOutputStream _oos, String _fileName, Exception[] _exception) {
        command = _command;
        creatureMap = _creatureMap;
        fileName = _fileName;
        exception = _exception;
        oos = _oos;
    }

    @Override
    public void run() {
        try {
            check(command);
            message = command.executeCommand(creatureMap, fileName, "UTF-8");
            check(command);
        } catch (InvalidParameterException | FileSavingException e) {
            message = "Invalid: " + e.getMessage();
        } catch (Exception e) {
            exception[0] = e;
        }
        try {
            if (!message.isEmpty()) {
                IOTools.sendObject(oos, message, String.class.getName(), false, true);
            }
        } catch (IOException | InterruptedException e) {
            exception[0]=e;
        }
    }

    private void check(Command command) {
        String _fileName;
        if (command.getName().equals("import")) {
            creatureMap.copyOf(new CreatureMap(command.getObjectMap()));
        } else if (command.getName().equals("load")) {
            if (message.startsWith(Server.getDataDirectory()) || message.startsWith("~") || message.equals("data/data.csv")) {
                _fileName = message;
                try {
                    creatureMap.copyOf(IOTools.getCreatureMapFromFile(_fileName, "UTF-8"));
                    message = "File loaded";
                } catch (Exception e) {
                    message = "File didnt load: " + e.getMessage();
                }
            } else {
                message = "File didnt load: FORBIDDEN, path to file on server must starts with " + Server.getDataDirectory() + "\n";
            }
        }
    }
}
