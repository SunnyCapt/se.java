package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.Command;
import capt.sunny.labs.l7.IOTools;
import capt.sunny.labs.l7.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidParameterException;

public class AnswerMSocket implements Runnable {
    Command command;
    DataManager dataManager;
    String message = "";
    Exception[] exception;
    ObjectOutputStream oos;
    User[] user;

    public AnswerMSocket(Command _command, DataManager _dataManager, ObjectOutputStream _oos, Exception[] _exception, User[] _user) {
        command = _command;
        dataManager = _dataManager;
        exception = _exception;
        oos = _oos;
        user = _user;
    }

    @Override
    public void run() {
        String errMessage = null;
        // token verification
        if (!(user[0] == null || command.getUserName() == null || command.getToken() == null || command.getUserName().equals(" ") || command.getToken().equals(" "))
                && !command.getName().equals("login") && !command.getName().equals("help")) {
            if (!(command.getUserName().equals(user[0].getNick()) &&
                    command.getToken().equals(user[0].getToken()) &&
                    !user[0].isTokenValid())) {
                errMessage = "Wrong token, login again...";
            }
        }
        try {
            message = errMessage == null ? CommandExecutor.execute(dataManager, command, "UTF-8") : errMessage;
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
        } catch (InterruptedException ignored) {

        } catch (IOException e) {
            exception[0] = e;
        }
    }

}
