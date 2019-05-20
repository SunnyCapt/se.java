package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.Command;
import capt.sunny.labs.l7.IOTools;
import capt.sunny.labs.l7.User;
import capt.sunny.labs.l7.serv.db.DB;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;

public class AnswerMSocket implements Runnable {
    Command command;
    DataManager dataManager;
    String fileName;
    String message = "";
    Exception[] exception;
    ObjectOutputStream oos;
    User[] user;
    DB db;

    public AnswerMSocket(Command _command, DataManager _dataManager, ObjectOutputStream _oos, Exception[] _exception, User[] _user) {
        command = _command;
        dataManager = _dataManager;
        exception = _exception;
        oos = _oos;
        user = _user;
    }

    @Override
    public void run() {
        try {
            message = CommandExecutor.execute(dataManager,  command, "UTF-8");
        } catch (InvalidParameterException | FileSavingException e) {

            message = "Invalid: " + e.getMessage();
            System.out.println(message);
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
