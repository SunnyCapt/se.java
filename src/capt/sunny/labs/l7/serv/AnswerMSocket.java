package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.Command;
import capt.sunny.labs.l7.CreatureMap;
import capt.sunny.labs.l7.IOTools;
import capt.sunny.labs.l7.serv.FileSavingException;
import capt.sunny.labs.l7.serv.Server;

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
    User[] user;

    public AnswerMSocket(Command _command, CreatureMap _creatureMap, ObjectOutputStream _oos, String _fileName, Exception[] _exception, User[] _user) {
        command = _command;
        creatureMap = _creatureMap;
        fileName = _fileName;
        exception = _exception;
        oos = _oos;
        user = _user;
    }

    @Override
    public void run() {
        try {
            message = command.executeCommand(creatureMap, fileName, user, "UTF-8");
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

}
