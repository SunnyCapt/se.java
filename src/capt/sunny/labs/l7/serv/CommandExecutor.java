package capt.sunny.labs.l7.serv;

import capt.sunny.labs.l7.Command;
import capt.sunny.labs.l7.Commands;
import capt.sunny.labs.l7.serv.db.DBException;
import org.json.JSONException;

import javax.security.auth.login.LoginException;
import java.security.InvalidParameterException;
import java.util.Random;

public class CommandExecutor {


    public static String execute(DataManager dataManager, Command command, String charsetName) throws FileSavingException, InvalidParameterException {


        if ((command.getUserName() == null || command.getToken() == null || command.getUserName().equals("") || command.getToken().equals("") )  && !command.getName().equals("login") && !command.getName().equals("help"))
            return "\nYou are not logged in, please use the login \ncommand for it. (see manual with help command)";

        switch (command.getName()) {
            case "login":
                String patt = "{\"nick\":\"%s\", \"token\":\"%s\", \"message\":\"%s\"}";
                try {
                    return String.format(patt,
                            command.getFirstParameter(),
                            SUserUtils.login(command.getFirstParameter(), command.getSecondParameter(), dataManager),
                            String.format("Hello %s. You haven't been in street racing for a long time!", command.getFirstParameter())
                    );
                } catch (LoginException e) {
                    return String.format(patt,"","",e.getMessage());
                }
            case "signin":
                return SUserUtils.signin(command.getFirstParameter(), command.getSecondParameter(), command.getThirdParameter(), dataManager);
            case "insert":
                try {
                    dataManager.insert(command.getObject().getName(), command.getObject());
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
                try {
                    dataManager.save(command.getUserName());
                    return "File saved";
                }catch (DBException e) {
                    return e.getMessage();
                }
            case "add_if_min":
                try {
                    return dataManager.add_if_min(command.getObject())?"Item added":"Item not added";
                } catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the object to add\n");
                } catch (InvalidParameterException e) {
                    throw new InvalidParameterException("Failed to initialize objects: \n" + e.getMessage());
                }
            case "remove":
                try {
                    dataManager.remove(command.getFirstParameter(), command.getUserName());
                    return "Item removed";
                } catch (InvalidParameterException e){
                    return e.getMessage();
                }catch (JSONException e) {
                    throw new InvalidParameterException(e.getMessage());
                } catch (NullPointerException e) {
                    throw new InvalidParameterException("Specify the name of the object to be deleted.\n");
                }
            case "info":
                return dataManager.info();
            case "remove_lower":
                try {
                    dataManager.remove_lower(command.getFirstParameter(), command.getUserName());
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
                    dataManager.save(command.getUserName());
                    return "File saved, bye...";
                }catch (DBException e) {
                    return e.getMessage();
                }
            default:
                throw new InvalidParameterException("not found");
        }

    }
}