package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.Command;
import capt.sunny.labs.l7.Commands;
import org.json.JSONException;

import javax.security.auth.login.LoginException;
import java.security.InvalidParameterException;
import java.util.Random;

public class CommandExecutor {


    public static String execute(DataManager dataManager, Command command, String charsetName) throws FileSavingException, InvalidParameterException {

        if (command.getName().equals("show"))
            System.out.println();
        if ((command.getUserName() == null || command.getToken() == null || command.getUserName().equals(" ") || command.getToken().equals(" ") )  && !command.getName().equals("login") && !command.getName().equals("help"))
            return "\nYou are not logged in, please use the login \ncommand for it. (see manual with help command)";

        switch (command.getName()) {
            case "login":
                String patt = "{\"nick\":\"%s\", \"token\":\"%s\", \"message\":\"%s\"}";
                try {
                    return String.format(patt,
                            command.getUserName(),
                            SUserUtils.login(command.getFirstParameter(), command.getSecondParameter(), dataManager).getToken(),
                            String.format("Hello %s. You haven't been in street racing for a long time!", command.getFirstParameter())
                    );
                } catch (LoginException e) {
                    return String.format(patt," "," ",e.getMessage());
                }
            case "insert":
                try {
                    dataManager.insert(command.getFirstParameter(), command.getObject());
                    return "Item added\n";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the name and the object to add\n");
                } catch (InvalidParameterException e) {
                    throw new InvalidParameterException("Failed to initialize object: " + e.getMessage());
                }
            case "show":
                return dataManager.show();
            case "save":
                dataManager.save(command.getName());
                return "\nFile saved\n";
            case "add_if_min":
                try {
                    dataManager.add_if_min(command.getObject());
                    return "";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the object to add\n");
                } catch (InvalidParameterException e) {
                    throw new InvalidParameterException("Failed to initialize objects: \n" + e.getMessage());
                }
            case "remove":
                try {
                    dataManager.remove(command.getFirstParameter());
                    return "item removed";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the name of the object to be deleted.\n");
                }
            case "info":
                return dataManager.info();
            case "remove_lower":
                try {
                    dataManager.remove_lower(command.getFirstParameter());
                    return "all lower items removed";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the name\n");
                }
            case "help":
                return Commands.help();
            case "exit":
                try {
                    dataManager.save(charsetName);
                    return "File saved, bye...";
                } catch (FileSavingException ignored) {
                    return "File didnt save, sorry, bye...";
                }
            default:
                throw new InvalidParameterException("not found");
        }

    }
}